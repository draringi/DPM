package dpm.teamone.driver;

import dpm.teamone.driver.communications.CommunicationsManager;
import dpm.teamone.driver.events.EventManager;
import dpm.teamone.driver.maps.GridMap;
import dpm.teamone.driver.navigation.NavigationController;

/**
 * DriverRobot is the Main control class and Constant storage class
 * for the Drive Brick
 * @author Michael Williams
 *
 */
public class DriverRobot {
	
	/**
	 * Length of a map data array. Has a value of {@value} 
	 */
	public static final int MAP_DATA_LENGTH = 9;

	/**
	 * Main thread of the driver block.
	 * This block handles navigation, and communication with the C&C server. 
	 * @param args
	 */
	public static void main(String[] args) {
	}

	private EventManager myEventManager;
	private NavigationController myNavigationController;
	private GridMap myGridMap;

	private CommunicationsManager myCommunicationsManager;

}