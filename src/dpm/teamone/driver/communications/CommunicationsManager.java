package dpm.teamone.driver.communications;

/**
 * The CommunicationsManager handles the different communication modules, and
 * exports the commands and events for the main thread to use.
 * 
 * @author Michael Williams
 * 
 */
public class CommunicationsManager {

	private final ControlComms cnc;
	private final TruckComms truck;

	/**
	 * Standard Constructor, Creates the individual communication modules and
	 * sets them up.
	 */
	public CommunicationsManager() {
		this.cnc = new ControlComms();
		this.truck = new TruckComms();
		this.truck.setup();
	}

	/**
	 * Tells the other brick to close the claw (Grab).
	 */
	public void grabObject() {
		this.truck.grab();
	}

	/**
	 * Tells the other brick to raise the arm (Lift).
	 */
	public void liftObject() {
		this.truck.pickUp();
	}

	/**
	 * Tells the other brick to prepare for object grabbing (Arm the claw).
	 */
	public void prepareClaw() {
		this.truck.armClaw();
	}

	/**
	 * Tells the other brick to raise and close the claw for moving (Travel).
	 */
	public void prepareTravel() {
		this.truck.travel();
	}

	/**
	 * Tells the other brick to place the object on the ground (Release).
	 */
	public void releaseObject() {
		this.truck.drop();
	}

	/**
	 * Blocking function that waits for the Map info from the C&C server
	 * 
	 * @param mapData
	 *            array of data containing map number, block location and drop
	 *            off location
	 */
	public int[] waitForMap() {
		return this.cnc.getMapData();
	}
}
