package org.usfirst.frc3668.TroBot.commands;

import org.usfirst.frc3668.TroBot.Robot;
import org.usfirst.frc3668.TroBot.RobotMap;
import org.usfirst.frc3668.TroBot.Settings;

import edu.wpi.first.wpilibj.command.Command;

public class CmdLift extends Command {
	boolean _isFinished = false;
	double _throttle;
	double _targetTics;
	double _deltaSignum;
	double _initTics;

	public CmdLift(double throttle, double targetTicks) {
		_throttle = throttle;
		_targetTics = targetTicks;

		requires(Robot.subLift);
	}

	@Override
	protected void initialize() {
		_isFinished = false;
		_initTics = Robot.subLift.getEncoderTics();
		_deltaSignum = Math.signum(_targetTics - _initTics);
	}

	@Override
	protected void execute() {
		double currentTics = Robot.subLift.getEncoderTics();
		double throttle = 0;
		double deltaTics = _targetTics - currentTics;
		_deltaSignum = Math.signum(deltaTics);
		if (_deltaSignum > 0) {
			throttle = Settings.liftUpSpeed;
		} else {
			throttle = Settings.liftDownSpeed;
		}
		if (Math.abs(deltaTics) <= Settings.slowLiftThresh) {
			throttle = throttle *  Settings.slowConstantForLift;
		}
		else if (Math.abs(_initTics - currentTics)<= Settings.slowLiftThresh){
			throttle = throttle * Settings.slowLiftThresh;
		}
		
		// System.err.println("Curr Tics: " + currentTics + " Target Tics: " +
		// _targetTics + " throttle: " + throttle
		// + " Pivot Status: " + Robot.pivotStatus);
		Robot.subLift.lift(throttle);
		if (currentTics > _targetTics - Settings.liftWindow && currentTics < _targetTics + Settings.liftWindow) {
			_isFinished = true;
		}

	}

	@Override
	protected boolean isFinished() {
		return _isFinished;
	}

	@Override
	protected void end() {
		System.err.println("Done auto lift");
		Robot.subLift.lift(0);
	}

	@Override
	protected void interrupted() {
		System.err.println("HELP! INTERRUPTED AUTO LIFT");
		end();
	}
}
