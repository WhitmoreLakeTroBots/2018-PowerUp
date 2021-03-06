package org.usfirst.frc3668.TroBot.commands;

import org.usfirst.frc3668.TroBot.Robot;
import org.usfirst.frc3668.TroBot.RobotMap;
import org.usfirst.frc3668.TroBot.Settings;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class CmdPivotUp extends Command {
	double _initEncoder;
	protected boolean _isFinished = false;
	boolean _goingDown = true;

	public CmdPivotUp() {
		// Use requires() here to declare subsystem dependencies
		requires(Robot.subPivot);
	}

	// Called just before this Command runs the first time
	protected void initialize() {
		_isFinished = false;
		Robot.subPivot.getPivotStatus();
		//System.err.println("Init: UP Pivot Status: " + Robot.pivotStatus + "  Frw Limit: " + Robot.subLift.getLiftForwardLimit() + " Rev Limit: "
		//		+ Robot.subPivot.getReverseLimitSwitch());
	}

	protected void execute() {
		Robot.subPivot.getPivotStatus();
		double throttle = 0;
		double currentTics = Robot.subPivot.getEncoders();
		if (currentTics > Settings.pivotGravityFallThreshold) {
			throttle = -Settings.pivotGravityFallFast;
		} else if (currentTics < Settings.pivotGravityFallThreshold) {
			throttle = -Settings.pivotGravityFallSlow;
		}

		Robot.subPivot.pivot(throttle);

		if(Robot.pivotStatus == Settings.pivotStatus.isUp) {
			_isFinished = true;
		}
		
		//System.err.println("currentTics: " + currentTics + " lift Tics: " + Robot.subLift.getEncoderTics()
		//+ " Throttle: " + throttle + " Pivot Status: " + Robot.pivotStatus + " Frw Limit: " + Robot.subPivot.getForwardLimitSwitch() + " Rev Limit: "
		//+ Robot.subPivot.getReverseLimitSwitch() + " is Finished: " + _isFinished);
		
		//if (Robot.subPivot.getReverseLimitSwitch()) {
		//	_isFinished = true;
		//}
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return _isFinished;
	}

	// Called once after isFinished returns true
	protected void end() {
		System.err.println("UP pivot done" + " Rev Limit: " + Robot.subPivot.getReverseLimitSwitch()
		+ "  Frw Limit: " + Robot.subPivot.getForwardLimitSwitch() + "\t Pivot Status: " + Robot.pivotStatus + "\t encoder: " + Robot.subPivot.getEncoders());
		Robot.subPivot.pivot(0);
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
		System.err.println("Interrupted Pivot Up");
		end();
	}
}