package dpm.teamone.armcontrol;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.util.Delay;

/**
 * Arm handles picking up and dropping blocks
 * 
 * @author Michael Williams
 * 
 */
public class Arm {

	/**
	 * Grabs the block in front of it
	 */
	public static void grab() {
		if (!grabbing) {
			claw.rotateTo(GRAB_ANGLE, true);
			grabbing = true;
			Delay.msDelay(600);
		}
	}

	/**
	 * Lowers the arm
	 */
	public static void lower() {
		if (raised) {
			arm.rotateTo(LOWER_ANGLE);
			raised = false;
		}
	}

	/**
	 * Raises the arm above the robot
	 */
	public static void raise() {
		if (!raised) {
			arm.rotateTo(RAISE_ANGLE);
			raised = true;
		}
	}

	/**
	 * Releases the block being carried
	 */
	public static void release() {
		if (grabbing) {
			claw.rotateTo(RELEASE_ANGLE);
			grabbing = false;
		}
	}

	/**
	 * The Motor controlling the arm. This allows us to easily control the arm.
	 */
	private static final NXTRegulatedMotor arm = Motor.B;

	/**
	 * The Motor controlling the claw. This allows us to easily control the
	 * claw.
	 */
	private static final NXTRegulatedMotor claw = Motor.A;

	/**
	 * Angle relative to starting position that the claw should try to go to
	 * grab the block. Value: {@value}
	 */
	private static final int GRAB_ANGLE = -10;

	/**
	 * Current status of the claw. True if grabbing, False if released.
	 */
	private static boolean grabbing = true;

	/**
	 * Angle relative to starting position that the arm should go to when
	 * lowered. Value: {@value}
	 */
	private static final int LOWER_ANGLE = 0;

	/**
	 * Angle relative to starting position that the arm should go to when
	 * raised. Value: {@value}
	 */
	private static final int RAISE_ANGLE = -90;

	/**
	 * Current status of the arm. True if raised, false if lowered.
	 */
	private static boolean raised = false;

	/**
	 * Angle relative to starting position that the claw should try to go to
	 * release the block. Value: {@value}
	 */
	private static final int RELEASE_ANGLE = 120;

}
