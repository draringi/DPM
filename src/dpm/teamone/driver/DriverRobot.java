package dpm.teamone.driver;

import lejos.nxt.Button;
import lejos.util.Delay;
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
		Clock clock = new Clock(LAST_RUN);
		CommunicationsManager comms = new CommunicationsManager();
		int mapData[];
		mapData = comms.waitForMap();
		comms.prepareTravel();
		map = MapFactory.getMap(mapData[MAP_DATA_MAP]);
		//map = MapFactory.getBetaMap(mapData[MAP_DATA_MAP]);
		//map = MapFactory.lab5Map();
		nav = new NavigationController(map);
		nav.setDropZone(7, 7, 1, 1);
		//nav.setPickUpZone(3, 1, 1, 1); // Lab 5 map
		nav.setPickUpZone(2, 2, 0, 0); //Any 8x8 map
		events = new EventManager(nav);
		Delay.msDelay(10);
		events.start();
		EventManager.pause();
		clock.start();
		nav.localize();
		Delay.msDelay(10);
		while(!clock.timeUp()){
			EventManager.restart();
			nav.driveToPickup();
			EventManager.pause();
			int dist = nav.findObject();
			nav.travel(dist/2);
			dist = nav.findObject();
			comms.prepareClaw();
			nav.travel(dist);
			comms.grabObject();
			// This is the end of the Beta Goal
			EventManager.restart();
			nav.driveToDrop();
			EventManager.pause();
			nav.travel(-30);
			comms.releaseObject();
			System.gc();
		}
		EventManager.pause();
		Button.waitForAnyPress();
	}
	
	private static final int LAST_RUN = 360;

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

	/**
	 * Length of a map data array. Has a value of {@value}
	 */
	public static final int MAP_DATA_LENGTH = 3;

	/**
	 * Index of map data array for the map number. Has a value of {@value}
	 */
	public static final int MAP_DATA_MAP = 0;

	public static final int POS_THETA = 2;

	public static final int POS_X = 0;

	public static final int POS_Y = 1;

}