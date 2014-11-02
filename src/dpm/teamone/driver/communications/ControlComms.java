package dpm.teamone.driver.communications;

/**
 * The ControlComms module talks with the C&C server,
 * Receiving map data, and sending positional data
 * @author Michael Williams
 *
 */
public class ControlComms {

	private int[] mapBuffer;
	

	/**
	 * Provides the map data received from the C&C server
	 * @param mapData Array long enough to store map data, in which the map data is to be entered  
	 * @see dpm.teamone.driver.DriverRobot#MAP_DATA_LENGTH
	 */
	protected void getMapData(int[] mapData) {
	}

	/**
	 * Sends the provided co-ordinates to the C&C server
	 * @param x Grid location on the x-axis 
	 * @param y Grid location on the y-axis
	 */
	protected void sendPos(int x, int y) {
	}

	/**
	 * Starts the connection with the C&C server 
	 */
	protected void setup() {
	}

}