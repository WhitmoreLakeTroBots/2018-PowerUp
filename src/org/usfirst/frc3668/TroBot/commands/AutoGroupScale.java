package org.usfirst.frc3668.TroBot.commands;

import org.usfirst.frc3668.TroBot.Robot;
import org.usfirst.frc3668.TroBot.Settings;
import org.usfirst.frc3668.TroBot.Settings.autoPosition;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoGroupScale extends CommandGroup {

	public AutoGroupScale(autoPosition position, boolean exp) {
		autoPosition scaleSide;
		boolean goodData = true;
		double angleMod = 1;
		double StraightToScale = 0;
		double StraightTurnToScale = 0;
 		if (Robot.gameData.charAt(1) == 'R') {
			scaleSide = autoPosition.right;
		} else if (Robot.gameData.charAt(1) == 'L') {
			scaleSide = autoPosition.left;
		} else {
			scaleSide = autoPosition.error;
			SmartDashboard.putString("error", "GAME DATA ERROR");
			goodData = false;
		}
 		if(!exp) {
 			StraightToScale = Settings.autoStraightToScale;
 			StraightTurnToScale	= Settings.autoStraightTurnToScale;
 		}else {
 			StraightToScale = Settings.autoExpStraightToScale;
 			StraightTurnToScale	= Settings.autoExpStraightTurnToScale;
 		}

		if (position == autoPosition.right) {
			angleMod = -1;
		}

		if (scaleSide == position && scaleSide != autoPosition.error) {

			CommandGroup ApproachScale = new CommandGroup();
			//ApproachScale.addSequential(new AutoDriveProfileGyro(angleMod * Settings.autoStraightTurnToScale,
			//		Settings.autoCruiseSpeed, Settings.autoForwardToBeCloseToScale));
			ApproachScale.addParallel(new CmdLift(Settings.liftUpSpeed, Settings.liftTicsToMaxScaleAuto));

			addSequential(new AutoDriveProfileGyro(0, Settings.autoCruiseSpeed,StraightToScale));
			//addParallel(new CmdCalibrateLift());
			//addParallel(new CmdCalibratePivot());
			addSequential(new AutoTurnGyro(Settings.autoTurnSpeed, angleMod * StraightTurnToScale));
			addSequential(ApproachScale);
			//if (safe) {
				addSequential(new AutoIntake(Settings.intakeAutoScaleOut, Settings.autoEjectCubeTime)); 
				addSequential(new CmdLift(Settings.liftUpSpeed, 0));
			//}

		}

		if (scaleSide != position && scaleSide != autoPosition.error) {

			CommandGroup ApproachScale = new CommandGroup();
			//ApproachScale.addSequential(
			//		new AutoTurnGyro(Settings.autoTurnSpeed, angleMod * Settings.autoTurnToPlaceInScale));
			ApproachScale.addParallel(new CmdLift(Settings.liftUpSpeed, Settings.liftTicsToScale));

			addSequential(new AutoDriveProfileGyro(0, Settings.autoCruiseSpeed, Settings.autoBehindSwitchDist));
			addSequential(new AutoTurnGyro(Settings.autoTurnSpeed, angleMod * Settings.autoTurnToFaceWall));
			addSequential(new AutoDriveProfileGyro(angleMod * Settings.autoTurnToFaceWall, Settings.autoCruiseSpeed,
					Settings.autoDrivePastSwitch));
			if (exp) {
				addSequential(new AutoTurnGyro(Settings.autoTurnSpeed, angleMod * Settings.autoTurnToFaceScale));
				addSequential(new AutoDriveProfileGyro(Settings.autoTurnToFaceScale, Settings.autoCruiseSpeed,
						Settings.autoDriveToScale));
				addSequential(ApproachScale);
				addSequential(new AutoIntake(Settings.intakeAutoScaleOut, Settings.autoEjectCubeTime));
				addSequential(new CmdLift(Settings.liftUpSpeed, 0));
			}
		}
		if (!goodData) {
			addSequential(new AutoDriveProfileGyro(0, Settings.autoCruiseSpeed, Settings.autoLineDistance));
		}
	}
}