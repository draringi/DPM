package dpm.teamone.driver.communications;

/**
 * The CommunicationsManager handles the different communication modules, and
 * exports the commands and events for the main thread to use.
 * 
 * @author Michael Williams
 *
 */
public class CommunicationsManager {

	private ControlComms cnc;
	private TruckComms truck;

	/**
	 * Standard Constructor, Creates the individual communication modules and
	 * sets them up.
	 */
	public CommunicationsManager() {
		this.cnc = new ControlComms();
		this.truck = new TruckComms();
		truck.setup();
	}

	/**
	 * Blocking function that waits for the Map info from the C&C server
	 * 
	 * @param mapData
	 *            array of data containing map number, block location and drop
	 *            off location
	 */
	public void waitForMap(int[] mapData) {
		this.cnc.setup();
		this.cnc.getMapData(mapData);
	}

	public void grabObject(){
		this.truck.pickUp();
	}
	
	public void releaseObject(){
		this.truck.drop();
	}
	
	public void prepareClaw(){
		this.truck.armClaw();
	}
	
	public void prepareTravel(){
		this.truck.travel();
	}
}