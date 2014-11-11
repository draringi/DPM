package dpm.teamone.armcontrol;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

/**
 * Arm handles picking up and dropping blocks
 * 
 * @author Michael Williams
 *
 */
public class Arm {

	private static final int GRAB_ANGLE = -90;
	private static final int RELEASE_ANGLE = 0;
	private static final int LOWER_ANGLE = -90;
	private static final int RAISE_ANGLE = 0;
	private static final NXTRegulatedMotor claw = Motor.A;
	private static final NXTRegulatedMotor arm = Motor.B;
	private static boolean grabbing = false;
	private static boolean raised = true;
	/**
	 * Grabs the block in front of it
	 */
	public static void grab() {
		if(!grabbing){
			claw.rotateTo(GRAB_ANGLE);
			grabbing = true;
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