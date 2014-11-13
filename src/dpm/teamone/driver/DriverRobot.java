package dpm.teamone.driver;

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
		int mapData[] = new int[MAP_DATA_LENGTH];
		comms.waitForMap(mapData);
		map = MapFactory.getMap(mapData[MAP_DATA_MAP]);
		nav = new NavigationController(map);
		nav.setDropZone(mapData[MAP_DATA_DROP_X], mapData[MAP_DATA_DROP_Y], mapData[MAP_DATA_DROP_W], mapData[MAP_DATA_DROP_H]);
		nav.setPickUpZone(mapData[MAP_DATA_PICKUP_X], mapData[MAP_DATA_PICKUP_Y], mapData[MAP_DATA_PICKUP_W], mapData[MAP_DATA_PICKUP_H]);
		nav.localize();
		events = new EventManager(nav);
		nav.driveToPickup();
		nav.findObject();
		//This is the end of the Beta Goal
		//comms.grabObject();
		//nav.driveToDrop();
		//comms.releaseObject();
	}

	/**
	 * Length of a map data array. Has a value of {@value}
	 */
	public static final int MAP_DATA_LENGTH = 9;

	/**
	 * Index of map data array for the map number. Has a value of {@value}
	 */
	public static final int MAP_DATA_MAP = 0;

	/**
	 * Index of map data array for the location of the pick-up zone in the
	 * x-axis. Has a value of {@value}
	 */
	public static final int MAP_DATA_PICKUP_X = 1;

	/**
	 * Index of map data array for the location of the pick-up zone in the
	 * y-axis. Has a value of {@value}
	 */
	public static final int MAP_DATA_PICKUP_Y = 2;

	/**
	 * Index of map data array for the width of the pickup zone. Has a value of
	 * * {@value}
	 */
	public static final int MAP_DATA_PICKUP_W = 3;

	/**
	 * Index of map data array for the height of the pickup zone. Has a value of
	 * * {@value}
	 */
	public static final int MAP_DATA_PICKUP_H = 4;

	/**
	 * Index of map data array for the location of the drop zone in the x-axis.
	 * Has a value of {@value}
	 */
	public static final int MAP_DATA_DROP_X = 5;

	/**
	 * Index of map data array for the location of the drop zone in the y-axis.
	 * Has a value of {@value}
	 */
	public static final int MAP_DATA_DROP_Y = 6;

	/**
	 * Index of map data array for the width of the drop zone. Has a value of *
	 * {@value}
	 */
	public static final int MAP_DATA_DROP_W = 7;

	/**
	 * Index of map data array for the height of the drop zone. Has a value of *
	 * {@value}
	 */
	public static final int MAP_DATA_DROP_H = 8;

	public static final int POS_X = 0;

	public static final int POS_Y = 1;

	public static final int POS_THETA = 2;

	

}