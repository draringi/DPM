package dpm.teamone.armcontrol;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;

/**
 * Arm handles picking up and dropping blocks
 * 
 * @author Michael Williams
 *
 */
public class Arm {

	private static final int GRAB_ANGLE = -40;
	private static final int RELEASE_ANGLE = 90;
	private static final int LOWER_ANGLE = 0;
	private static final int RAISE_ANGLE = -90;
	private static final NXTRegulatedMotor claw = Motor.A;
	private static final NXTRegulatedMotor arm = Motor.B;
	
	private static boolean grabbing = false;
	private static boolean raised = false;
	/**
	 * Grabs the block in front of it
	 */
	public static void grab() {
		if(!grabbing){
			claw.setSpeed(0.2f);
			claw.rotateTo(GRAB_ANGLE, true);
			grabbing = true;
			try{
				Thread.sleep(500);
			} catch(Exception e){
				Sound.beep();
		
			}
		}
	}

	/**
	 * Lowers the arm
	 */
	public static void lower() {
		if(raised){
			arm.rotateTo(LOWER_ANGLE);
			raised = false;
		}
	}

	/**
	 * Raises the arm above the robot
	 */
	public static void raise() {
		if(!raised){
			arm.rotateTo(RAISE_ANGLE);
			raised = true;
		}
	}

	/**
	 * Releases the block being carried
	 */
	public static void release() {
		if(grabbing){
			claw.rotateTo(RELEASE_ANGLE);
			grabbing = false;
		}
	}

}
