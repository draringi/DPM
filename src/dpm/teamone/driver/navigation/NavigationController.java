package dpm.teamone.driver.navigation;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.Path;
import lejos.robotics.pathfinding.ShortestPathFinder;
import dpm.teamone.driver.maps.GridMap;

/**
 * The NavigationController provides a easy to use wrapper for the LeJos
 * Navigation API It handles Movement, Localization and Pathfinding
 * 
 * @author Mehdi Benguerrah
 * @author Michael Williams
 *
 */
public class NavigationController {

	private static final double WHEEL_RADIUS = 2.5;
	private static final double TRACK_WIDTH = 15.5;
	private static NXTRegulatedMotor LEFT_MOTOR = Motor.A,
			RIGHT_MOTOR = Motor.B;
	private DifferentialPilot pilot;

	private Navigator navigator;

	public int[] dropZone;

	public int[] pickupZone;

	public GridMap map;

	/**
	 * Sets up the underlying Pilot, Navigation and Localization subsystem
	 * 
	 * @param map
	 *            Map of course to be used.
	 */
	public NavigationController(GridMap map) {
		this.pilot = new DifferentialPilot(WHEEL_RADIUS, TRACK_WIDTH,
				LEFT_MOTOR, RIGHT_MOTOR);
		this.navigator = new Navigator(this.pilot);
		this.map = map;
	}

	/**
	 * Drives the robot to the requested co-ordinates
	 * 
	 * @param x
	 *            Location in the x-axis
	 * @param y
	 *            Location in the y-axis
	 */
	public void driveToGrid(int x, int y) {
		this.followPath(this.getPath(x, y));
	}

	/**
	 * Drives the robot to the requested co-ordinates and turns to requested
	 * heading
	 * 
	 * @param x
	 *            Location in the x-axis
	 * @param y
	 *            Location in the y-axis
	 * @param direction
	 *            Cardinal Heading
	 */
	public void driveToGrid(int x, int y, Direction direction) {
	}

	/**
	 * Drives the robot along the requested route
	 * 
	 * @param route
	 *            path to follow
	 */
	protected void followPath(Path route) {
		this.navigator.followPath(route);
	}

	/**
	 * Determines shortest path to a Location
	 * 
	 * @param x
	 *            Location in the x-axis
	 * @param y
	 *            Location in the y-axis
	 * @return shortest path to destination
	 */
	private Path getPath(int x, int y) {
		Waypoint destination = new Waypoint(x, y); // Destination point
		Pose currentLocation = this.getPose();
		LineMap map = this.map.getLineMap();
		ShortestPathFinder pathAlgo = new ShortestPathFinder(map);
		Path route = null;
		try {
			route = pathAlgo.findRoute(currentLocation, destination);
		} catch (Exception e) {
		}
		return route;
	}

	/**
	 * @return current location and heading
	 */
	public Pose getPose() {
		return this.navigator.getPoseProvider().getPose();
	}

	/**
	 * Sets the Drop Zone
	 * 
	 * @param x
	 *            Drop zone x-axis start point
	 * @param y
	 *            Drop zone y-axis start point
	 * @param width
	 *            Drop zone x-axis length
	 * @param height
	 *            Drop zone y-axis length
	 */
	public void setDropZone(int x, int y, int width, int height) {
	}

	/**
	 * Sets the Pick-up Zone
	 * 
	 * @param x
	 *            Pick-up zone x-axis start point
	 * @param y
	 *            Pick-up zone y-axis start point
	 * @param width
	 *            Pick-up zone x-axis length
	 * @param height
	 *            Pick-up zone y-axis length
	 */
	public void setPickUpZone(int x, int y, int width, int height) {
	}

	/**
	 * Set current location and heading
	 * 
	 * @param p
	 *            current location and heading
	 */
	public void setPose(Pose p) {
		this.navigator.getPoseProvider().setPose(p);
	}

	/**
	 * Turns to requested direction
	 * 
	 * @param direction
	 *            Cardinal Heading
	 */
	public void turnTo(Direction direction) {
	}

	/**
	 * Turns to requested direction
	 * 
	 * @param angle
	 *            Angle clockwise from North
	 */
	public void turnTo(double angle) {
		this.pilot.rotate(angle);
	}
}
