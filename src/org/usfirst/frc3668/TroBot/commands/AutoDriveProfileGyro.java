package org.usfirst.frc3668.TroBot.commands;

import org.usfirst.frc3668.TroBot.PID;
import org.usfirst.frc3668.TroBot.Robot;
import org.usfirst.frc3668.TroBot.Settings;
import org.usfirst.frc3668.TroBot.motionProfile.Logger;
import org.usfirst.frc3668.TroBot.motionProfile.MotionProfiler;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoDriveProfileGyro extends Command{
	double MAXSPEED = Settings.chassisMaxInchesPerSecond;
	double _distance;
	double _cruiseSpeed;
	boolean _isFinished = false;
	double _accerlation = Settings.profileDriveAccelration; // inches/sec/sec
	double _startTime;
	double _requestedHeading = 0;
	double _distanceSignum;
	double _absDistance;
	double _abortTime;
	boolean _isRunaway;
	MotionProfiler mp;
	Logger log = new Logger(Settings.profileLogName);
	PID pid = new PID(Settings.profileKp, Settings.profileKi, Settings.profileKd);

	public AutoDriveProfileGyro(double requestedHeading, double cruiseSpeed, double distance) {
		requires(Robot.subChassis);
		_distance = distance;
		_absDistance = Math.abs(distance);
		_distanceSignum = Math.signum(distance);
		_cruiseSpeed = cruiseSpeed;
		_requestedHeading = requestedHeading;
	}

	public AutoDriveProfileGyro (double cruiseSpeed, double distance) {
		requires(Robot.subChassis);
		_distance = distance;
		_absDistance = Math.abs(distance);
		_distanceSignum = Math.signum(distance);
		_cruiseSpeed = cruiseSpeed;
	}
	
	protected void ProfileMockConstructor(double Speed, double distance){
		_distance = distance;
		_absDistance = Math.abs(distance);
		_distanceSignum = Math.signum(distance);
		_cruiseSpeed = Speed;		
	}
	
	// Called just before this Command runs the first time
	protected void initialize() {
		mp = new MotionProfiler(_absDistance, Settings.profileInitVelocity, _cruiseSpeed, _accerlation);
		Robot.subChassis.resetBothEncoders();
		_abortTime = _absDistance / _cruiseSpeed;
		System.err.println(String.format(
				"Projected Accelration Time: %1$.3f \tProjected Cruise Time: %2$.3f \t Projected Deccelration Time: %3$.3f \t Projected Length of Drive: %4$.3f \t Given Distance: %5$.3f \t Abort: %6$.3f",
				mp._accelTime, mp._cruiseTime, mp._deccelTime, mp._stopTime, _distance, _abortTime));
		_startTime = RobotMath.getTime();
		_isFinished = false;
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		double encoderVal = Robot.subChassis.getLeftEncoderDist();
		double deltaTime = RobotMath.getTime() - _startTime;
		double profileDist = mp.getTotalDistanceTraveled(deltaTime);
		double currentHeading = Robot.subChassis.gyroNormalize((int)Robot.subChassis.getGyroAngle);
		double turnValue = headingDelta(currentHeading);
		double profileVelocity = mp.getProfileCurrVelocity(deltaTime);
		double throttlePos = (profileVelocity / MAXSPEED);
		double pidVal = pid.calcPID(profileDist, encoderVal);
		double finalThrottle = throttlePos + pidVal;
		
		String msg = String.format(
				"CurrVel: %1$.3f \t throttle: %2$.3f \t Time: %3$.3f \t ProfileX: %4$.3f \t Encoder: %5$.3f \t PID Value: %10$.3f \t P: %14$.3f \t I: %13$.3f \t D: %11$.3f \t Final Throttle: %12$.3f",
				profileVelocity, throttlePos, deltaTime, mp.getTotalDistanceTraveled(deltaTime),
				encoderVal, Robot.subChassis.getLeftEncoderDist(),
				Robot.subChassis.getRightEncoderDist(), currentHeading, turnValue, pidVal, pid.getDError(), finalThrottle, pid.getIError(), pid.getPError());
		//FULL LOG MESSAGE: CurrVel: %1$.3f \t throttle: %2$.3f \t deltaTime: %3$.3f \t Disantce Travelled: %4$.3f \t AvgEncoder: %5$.3f \t Left Encoder: %6$.3f \t Right Encoder: %7$.3f \t Gyro Raw Heading: %8$.3f \t Turn Value: %9$.3f \t PID Value: %10$.3f \t P Value: %11$.3f \t Final Throttle: %12$.3f
		System.err.println(msg);
		//log.makeEntry(msg);
		SmartDashboard.putNumber("Drive Left Encoder:", Robot.subChassis.getLeftEncoderDist());
		SmartDashboard.putNumber("Drive Right Encoder", Robot.subChassis.getRightEncoderDist());

		Robot.subChassis.Drive((finalThrottle * _distanceSignum), turnValue);

		if (deltaTime > _abortTime && Robot.subChassis.getEncoderAvgDistInch() == 0) {
			System.out.println("Pasted Abort Time, Dead Encoders");
			_isFinished = true;
			Robot.subChassis._isSafeToMove = false;
		}
		if ( encoderVal < _absDistance + Settings.profileMovementThreshold && encoderVal > _absDistance - Settings.profileMovementThreshold) {
			System.err.println("At Distance");
			_isFinished = true;
		}
	}

	protected double headingDelta(double currentHeading) {
		return RobotMath.headingDelta(currentHeading, _requestedHeading, Settings.chassisDriveStraightGyroKp);
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return _isFinished;
	}

	// Called once after isFinished returns true
	protected void end() {
		Robot.subChassis.Drive(0, 0);
		Robot.subChassis.resetBothEncoders();
		System.out.println("CmdBothDriveWithProfileAndGyro is Finished");
		System.err.println(String.format(
				"Projected Accelration Time: %1$.3f \tProjected Cruise Time: %2$.3f \t Projected Deccelration Time: %3$.3f \t Projected Length of Drive: %4$.3f \t Given Distance: %5$.3f",
				mp._accelTime, mp._cruiseTime, mp._deccelTime, mp._stopTime, _distance));

		// mp = null;
		// log.write();
		// log = null;
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
		end();
	}
}