package dpm.teamone.armcontrol;

/**
 * The DriverComms handles communications with the Drive Brick.
 * 
 * @author Michael Williams
 *
 */
public class DriverComms {

	private Arm arm;

	/**
	 * Tells the Drive Brick that is has successfully dropped the block
	 */
	public void signalDropped() {
	}

	/**
	 * Tells the Drive Brick that is has successfully picked-up the block
	 */
	public void signalPickedUp() {
	}

	/**
	 * Tells the Drive Brick to correct its orientation to allow better ability
	 * to pick up a block
	 * 
	 * @param deg
	 *            Degrees to turn (In a clockwise direction)
	 */
	public void signalRotate(int deg) {
	}

}