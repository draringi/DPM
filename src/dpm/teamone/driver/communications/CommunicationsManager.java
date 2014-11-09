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

	}

	/**
	 * Blocking function that waits for the Map info from the C&C server
	 * 
	 * @param mapData
	 *            array of data containing map number, block location and drop
	 *            off location
	 */
	public void waitForMap(int[] mapData) {
		cnc.setup();
		cnc.getMapData(mapData);
	}

}