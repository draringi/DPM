package dpm.teamone.driver;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.robotics.navigation.Pose;
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
 * @see dpm.teamone.armcontrol.ArmControl
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
		Sound.beep();
		Sound.buzz();
		Sound.beep();
		Clock clock = new Clock(LAST_RUN);
		CommunicationsManager comms = new CommunicationsManager();
		int mapData[];
		mapData = comms.waitForMap();
		map = MapFactory.getMap(mapData[MAP_DATA_MAP]);
		// map = MapFactory.getBetaMap(mapData[MAP_DATA_MAP]);
		// map = MapFactory.lab5Map();
		nav = new NavigationController(map);
		nav.setDropZone(mapData[MAP_DATA_DROP_X], mapData[MAP_DATA_DROP_Y], 1,
				1);
		map.GenerateDropPaths(mapData[MAP_DATA_DROP_X],
				mapData[MAP_DATA_DROP_Y]);
		map.GeneratePickupPaths(11, 11);
		comms.grabObject();
		comms.prepareClaw();
		comms.prepareTravel();
		events = new EventManager(nav);
		Delay.msDelay(10);
		events.start();
		EventManager.pause();
		clock.start();
		nav.localize();
		Delay.msDelay(10);
		while (!clock.timeUp()) {
			EventManager.restart();
			EventManager.restart();
			Sound.beep();
			Pose loc = nav.getPose();
			LCD.drawInt((int) loc.getX(), 0, 3);
			LCD.drawInt((int) loc.getY(), 0, 4);
			nav.driveToPickup();
			EventManager.pause();
			int dist = nav.findObject();
			nav.travel(dist / 2);
			dist = nav.findObject();
			nav.travel(-5);
			comms.prepareClaw();
			nav.travel(dist + 5);
			comms.grabObject();
			nav.travel(-5);
			comms.liftObject();
			// This is the end of the Beta Goal
			nav.driveToPickup();
			EventManager.restart();
			EventManager.restart();
			nav.driveToDrop();
			EventManager.pause();
			nav.travel(-15);
			comms.releaseObject();
		}
		Sound.buzz();
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
	 * Has a value of {@value}.
	 */
	public static final int MAP_DATA_DROP_Y = 2;

	/**
	 * Length of a map data array.
	 * Has a value of {@value}.
	 */
	public static final int MAP_DATA_LENGTH = 3;

	/**
	 * Index of map data array for the map number.
	 * Has a value of {@value}.
	 */
	public static final int MAP_DATA_MAP = 0;

	/**
	 * Value in location array for the heading.
	 * Has a value of {@value}.
	 */
	public static final int POS_THETA = 2;

	/**
	 * Value in location array for the x-value.
	 * Has a value of {@value}.
	 */
	public static final int POS_X = 0;

	/**
	 * Value in location array for the y-value.
	 * Has a value of {@value}.
	 */
	public static final int POS_Y = 1;

}
