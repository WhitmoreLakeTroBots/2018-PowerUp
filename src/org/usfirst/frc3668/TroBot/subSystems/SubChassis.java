package org.usfirst.frc3668.TroBot.subSystems;

import org.usfirst.frc3668.TroBot.Robot;
import org.usfirst.frc3668.TroBot.RobotMap;
import org.usfirst.frc3668.TroBot.Settings;
import org.usfirst.frc3668.TroBot.commands.TeleopDrive;

// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS

/**
 *
 */
public class SubChassis extends Subsystem {
	public boolean _isSafeToMove  = true;

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

	// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
	// private final WPI_TalonSRX rightDrive1 = RobotMap.rightDrive1;
	// private final WPI_TalonSRX rightDrive2 = RobotMap.rightDrive2;
	// private final SpeedControllerGroup rightChassisMotors =
	// RobotMap.rightChassisMotors;
	// private final WPI_TalonSRX leftDrive1 = RobotMap.leftDrive1;
	// private final WPI_TalonSRX leftDrive2 = RobotMap.leftDrive2;
	// private final SpeedControllerGroup leftChassisMotors =
	// RobotMap.leftChassisMotors;
	// private final DifferentialDrive differentialDrive1 = RobotMap.chassisDrive;
	// private final AnalogGyro gyro = RobotMap.gyro;
	// private final Ultrasonic leftSonar = RobotMap.leftSonar;
	// private final Ultrasonic rightSonar = RobotMap.rightSonar;

	// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
	public void Drive(Joystick stick) {
		double joyX = stick.getX();
		double joyY = stick.getY();
		double rightMotorThrottle = (-joyX - joyY) * Settings.chassisRightSideScalar;
		double leftMotorThrottle = (-joyX + joyY) * Settings.chassisLeftSideScalar;
		if (Robot.isDriveInverted) {
			// RobotMap.chassisDrive.arcadeDrive(-joyY, -joyX, true);
			RobotMap.leftChassisMotors.set(leftMotorThrottle);
			RobotMap.rightChassisMotors.set(rightMotorThrottle);

		}
		if (!Robot.isDriveInverted) {
			// RobotMap.chassisDrive.arcadeDrive(joyY, -joyX, true);
			RobotMap.leftChassisMotors.set(-leftMotorThrottle);
			RobotMap.rightChassisMotors.set(-rightMotorThrottle);
		}
	}

	public void DriveMan(double leftThrottle, double rightThrottle) {
		RobotMap.leftChassisMotors.set(leftThrottle);
		RobotMap.rightChassisMotors.set(-rightThrottle);
	}

	public void Drive(double move, double rotate) {
		RobotMap.chassisDrive.arcadeDrive(move, rotate);
	}

	public double getEncoderAvgDistInch() {
		double retVal = 0;
//		double leftDistance = RobotMap.leftDrive1.getSelectedSensorPosition(0);
//		double rightDistance = RobotMap.rightDrive1.getSelectedSensorPosition(0);
//		if(leftDistance < Settings.chassisEncoderDeadValueThreshold){
//			retVal = rightDistance;
//		} else if (rightDistance < Settings.chassisEncoderDeadValueThreshold){
//			retVal = leftDistance;
//		} else {
//			retVal = (leftDistance + rightDistance) / 2;
//		}
		return retVal;
	}

	public double getRightEncoderDist () {
		return RobotMap.rightDrive1.getSelectedSensorPosition(0)* Settings.chassisEncoderDistancePerPulse;
	}

	public double getLeftEncoderDist (){
		return RobotMap.leftDrive1.getSelectedSensorPosition(0) * Settings.chassisEncoderDistancePerPulse;
	}
		public void resetRightEncoder() {

		RobotMap.rightDrive1.setSelectedSensorPosition(0, 0, 0);
	}

	public void resetBothEncoders(){
		RobotMap.rightDrive1.setSelectedSensorPosition(0, 0, 0);
		RobotMap.leftDrive1.setSelectedSensorPosition(0, 0, 0);
	}

	public double getGyroAngle() {
		return RobotMap.gyro.getAngle();
	}

	public void initializeGyro() {
		RobotMap.gyro.initGyro();
		RobotMap.gyro.calibrate();
		RobotMap.gyro.reset();
	}

	public void resetGyro() {
		RobotMap.gyro.reset();
	}

	public int gyroNormalize(int heading) {
		// takes the full turns out of heading
		// gives us values from 0 to 180 for the right side of the robot
		// and values from 0 to -179 degrees for the left side of the robot

		int degrees = heading % 360;

		if (degrees > 180) {
			degrees = degrees - 360;
		}

		if (degrees < -179) {
			degrees = degrees + 360;
		}

		return (degrees);
	}

	public boolean gyroInTol(int currHeading, int desiredHeading, int tol) {

		int upperTol = gyroNormalize(desiredHeading + tol);
		int lowerTol = gyroNormalize(desiredHeading - tol);
		int normalCurr = gyroNormalize(currHeading);

		float signumUpperTol = Math.signum(upperTol);
		float signumLowerTol = Math.signum(lowerTol);

		boolean retValue = false;
		// works for all positive numbers direction values
		if (signumUpperTol > 0 && signumLowerTol > 0) {
			if ((normalCurr >= lowerTol) && (normalCurr <= upperTol)) {
				retValue = true;
			}
		}

		// works for negative values
		else if (signumUpperTol < 0 && signumLowerTol < 0) {
			if ((normalCurr >= lowerTol) && (normalCurr <= upperTol)) {
				retValue = true;
			}
		}
		// mixed values -tol to + tol This happens at 180 degrees
		else if ((signumUpperTol < 0) && (signumLowerTol > 0)) {
			// System.out.println("upperTol " + upperTol + " Current " +
			// normalCurr + " lowerTol " + lowerTol);
			if ((Math.abs(normalCurr) >= Math.abs(lowerTol)) && (Math.abs(normalCurr) >= Math.abs(upperTol))) {
				retValue = true;
			}

		}
		// mixed values -tol to + tol This happens at 0 degrees
		else if ((signumUpperTol > 0) && (signumLowerTol < 0)) {
			// System.out.println("upperTol " + upperTol + " Current " +
			// normalCurr + " lowerTol " + lowerTol);
			if ((Math.abs(normalCurr) <= Math.abs(lowerTol)) && (Math.abs(normalCurr) <= Math.abs(upperTol))) {
				retValue = true;
			}

		}
		return (retValue);
	}

	@Override
	public void initDefaultCommand() {
		setDefaultCommand(new TeleopDrive());
	}

	@Override
	public void periodic() {
		// Put code here to be run every loop

	}

	// Put methods for controlling this subsystem
	// here. Call these from Commands.

}