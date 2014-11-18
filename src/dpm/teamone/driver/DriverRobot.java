package dpm.teamone.driver;

import dpm.teamone.driver.communications.BetaComms;
import dpm.teamone.driver.communications.CommunicationsManager;
import dpm.teamone.driver.events.EventManager;
import dpm.teamone.driver.maps.GridMap;
import dpm.teamone.driver.maps.MapFactory;
import dpm.teamone.driver.navigation.NavigationController;

/**
 * DriverRobot is the Main control class and Constant storage class for the
 * Drive Brick
 * 
 * @author Michael Williams
 *
 */
public class DriverRobot {

	/**
	 * Main thread of the driver block. This block handles navigation, and
	 * communication with the C&C server.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		EventManager events;
		NavigationController nav;
		GridMap map;
		CommunicationsManager comms = new CommunicationsManager();
		//BetaComms beta = new BetaComms(); // I hate this class and will kill it as soon as possible,
		int mapData[] = new int[MAP_DATA_LENGTH];
		//beta.waitForMap(mapData);
		mapData[MAP_DATA_MAP] = BETA_MAP;
		comms.prepareTravel();
		//map = MapFactory.getMap(mapData[MAP_DATA_MAP]);
		//map = MapFactory.getBetaMap(mapData[MAP_DATA_MAP]);
		map = MapFactory.lab5Map();
		nav = new NavigationController(map);
		nav.setDropZone(mapData[MAP_DATA_DROP_X], mapData[MAP_DATA_DROP_Y], 1, 1);
		//nav.setPickUpZone(mapData[MAP_DATA_PICKUP_X], mapData[MAP_DATA_PICKUP_Y], mapData[MAP_DATA_PICKUP_W], mapData[MAP_DATA_PICKUP_H]);
		nav.localize();
		events = new EventManager(nav);
		events.start();
		nav.driveToPickup();
		comms.prepareClaw();
		nav.findObject();
		comms.grabObject();
		//This is the end of the Beta Goal
		//nav.driveToDrop();
		//comms.releaseObject();
	}
	
	private static final int BETA_MAP = 1;

	/**
	 * Length of a map data array. Has a value of {@value}
	 */
	public static final int MAP_DATA_LENGTH = 3;

	/**
	 * Index of map data array for the map number. Has a value of {@value}
	 */
	public static final int MAP_DATA_MAP = 0;

	/**
	 * Index of map data array for the location of the drop zone in the x-axis.
	 * Has a value of {@value}
	 */
	public static final int MAP_DATA_DROP_X = 1;

	/**
	 * Index of map data array for the location of the drop zone in the y-axis.
	 * Has a value of {@value}
	 */
	public static final int MAP_DATA_DROP_Y = 2;

	public static final int POS_X = 0;

	public static final int POS_Y = 1;

	public static final int POS_THETA = 2;

	

}