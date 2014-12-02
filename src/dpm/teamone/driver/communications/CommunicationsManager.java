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

	public void grabObject() {
		this.truck.grab();
	}

	public void liftObject() {
		this.truck.pickUp();
	}

	public void prepareClaw() {
		this.truck.armClaw();
	}

	public void prepareTravel() {
		this.truck.travel();
	}

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
