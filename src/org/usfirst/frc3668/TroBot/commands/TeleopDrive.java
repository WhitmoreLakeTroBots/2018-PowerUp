// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc3668.TroBot.commands;
import org.usfirst.frc3668.TroBot.Robot;
import org.usfirst.frc3668.TroBot.RobotMap;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class TeleopDrive extends Command {

double throttle = 0;
double throttleIncement = 0.01;
    public TeleopDrive() {
        requires(Robot.subChassis);
    }

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
    	Robot.subChassis.resetRightEncoder();
    	Robot.subChassis.resetGyro();
    	RobotMap.navx.reset();
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
    	if(Robot.oi.joyDrive.getRawButton(5)) {
    		throttle = throttle + throttleIncement;
    	} else if(Robot.oi.joyDrive.getRawButton(3)) {
    		throttle = throttle - throttleIncement;
    	}
    	//Robot.subChassis.DriveMan(throttle, throttle);
    	Robot.subChassis.Drive(Robot.oi.joyDrive);
    	//System.err.println("Right Encoder: " + Robot.subChassis.getRightEncoderDist() + "\t Left Encoder: "+ Robot.subChassis.getLeftEncoderDist() + " throttle: " + throttle);
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
    }
}
