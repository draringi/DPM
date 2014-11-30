package dpm.teamone.driver.navigation;

import java.util.ArrayList;
import java.util.List;

import lejos.geom.Point;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.Node;
import lejos.robotics.pathfinding.Path;
import dpm.teamone.driver.events.EventManager;
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

	private static final double FORWARD_SPEED = 10;
	private static NXTRegulatedMotor LEFT_MOTOR = Motor.A,
			RIGHT_MOTOR = Motor.B;
	private static final double ROTATE_SPEED = 20;
	private static final double TRACK_WIDTH = 19.2;
	private static final double WHEEL_DIAMETER = 4.0;
	public int[] dropZone;
	public GridMap map;
	private final Navigator navigator;

	private final ArrayList<ArrayList<Node>> paths = new ArrayList<ArrayList<Node>>();

	public int[] pickupZone;

	public DifferentialPilot pilot;

	/**
	 * Sets up the underlying Pilot, Navigation and Localization subsystem
	 * 
	 * @param map
	 *            Map of course to be used.
	 */
	public NavigationController(GridMap map) {
		this.pilot = new DifferentialPilot(WHEEL_DIAMETER, WHEEL_DIAMETER,
				TRACK_WIDTH, LEFT_MOTOR, RIGHT_MOTOR, false);
		this.navigator = new Navigator(pilot);
		this.map = map;
		this.pilot.setTravelSpeed(FORWARD_SPEED);
		this.pilot.setRotateSpeed(ROTATE_SPEED);
		this.pickupZone = new int[4];
		this.dropZone = new int[4];
	}

	private ArrayList<Node> adjustPath(ArrayList<Node> p, int count) {
		ArrayList<Node> temp = new ArrayList<Node>();
		for (int i = 0; i < (count - 1); i++) {
			temp.add(p.get(i));

		}
		return temp;
	}

	private void calculatePaths(int x1, int y1, int x2, int y2,
			ArrayList<Node> path, int count) {

		count++;
		path.add(new Node(x1, y1));

		if (count < path.size()) {

			path = adjustPath(path, count);
			path.add(new Node(x1, y1));
		}

		if ((x1 == x2) && (y1 == y2)) {

			this.paths.add(adjustPath(path, path.size() + 1));

		} else {
			if (isIndexValid(x1 + 1, y1, path)) {

				calculatePaths(x1 + 1, y1, x2, y2, path, count);

			}
			if (isIndexValid(x1, y1 + 1, path)) {

				calculatePaths(x1, y1 + 1, x2, y2, path, count);

			}
			if (isIndexValid(x1, y1 - 1, path)) {

				calculatePaths(x1, y1 - 1, x2, y2, path, count);

			}

			if (isIndexValid(x1 - 1, y1, path)) {

				calculatePaths(x1 - 1, y1, x2, y2, path, count);

			}

		}
		path.remove(path.size() - 1);
	}

	private boolean containsNode(Node n, ArrayList<Node> nodes) {

		boolean contains = false;
		for (int x = 0; x < nodes.size(); x++) {
			Node temp = nodes.get(x);
			if ((temp.x == n.x) && (temp.y == n.y)) {
				contains = true;
			}

		}
		return contains;
	}

	public void correctPath() {
		this.navigator.followPath();
	}

	public void driveToDrop() {
		driveToGrid(dropZone[0], dropZone[1]);
	}

	/**
	 * Drives the robot to the requested co-ordinates
	 * 
	 * @param x
	 *            Grid Location in the x-axis
	 * @param y
	 *            Grid Location in the y-axis
	 */
	public void driveToGrid(int x, int y) {
		Path path;
		path = map.getPath(getPose().getLocation(),
				new Point((float) map.getPos(x), (float) map.getPos(y)));
		// followPath(getPath(x,y));
		followPath(path);
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
		driveToGrid(x, y);
		turnTo(direction);
	}

	public void driveToPickup() {
		driveToGrid(pickupZone[0], pickupZone[1]);

	}

	public void findObject() {
		UltraSonic us = new UltraSonic();

		int minAngle = 0;
		int minVal = 190;
		int currentAng = 200;
		this.turnTo(180);
		while (currentAng < 250) {
			Sound.beep();
			currentAng += 2;
			this.turnTo(currentAng);
			if (us.poll() < minVal) {

				minAngle = currentAng;
				minVal = us.poll();
			}
		}
		turnTo(minAngle + 5);
		pilot.travel(minVal);
		turnTo(minAngle + 7);
		pilot.travel(3);
		turnTo(minAngle + 2);
	}

	/**
	 * Drives the robot along the requested route
	 * 
	 * @param route
	 *            path to follow
	 */
	public void followPath(Path route) {
		this.navigator.setPath(route);
		this.navigator.singleStep(true);
		boolean done = false;
		while (!done) {
			EventManager.pause();
			Waypoint wp = this.navigator.getWaypoint();
			LEFT_MOTOR.flt(true);
			RIGHT_MOTOR.flt(true);
			this.navigator.rotateTo(this.getPose().angleTo(wp));
			this.navigator.followPath();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			EventManager.restart();
			done = this.navigator.waitForStop();
		}
	}

	public void forward() {
		this.pilot.forward();

	}

	public GridMap getMap() {
		return this.map;
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
	public Path getPath(int x, int y) {
		int currentX = map.getGrid(getPose().getX()); // X index
		int currentY = map.getGrid(getPose().getY()); // Y index
		ArrayList<Node> path = new ArrayList<Node>();

		calculatePaths(currentX, currentY, x, y, path, 0);
		return getShortestPath();
	}

	public Path getPath_TEST(int v, int w, int x, int y) {
		int currentX = v;// X index
		int currentY = w; // Y index
		ArrayList<Node> path = new ArrayList<Node>();

		calculatePaths(currentX, currentY, x, y, path, 0);
		return getShortestPath();
	}

	public DifferentialPilot getPilot() {

		return this.pilot;
	}

	/**
	 * @return current location and heading
	 */
	public Pose getPose() {
		return this.navigator.getPoseProvider().getPose();
	}

	private Path getShortestPath() {
		int shortestPath = 1000;
		int index = 0;

		for (int i = 0; i < this.paths.size(); i++) {
			if (this.paths.get(i).size() < shortestPath) {

				shortestPath = this.paths.get(i).size();
				index = i;
			}
		}
		List<Node> shortestList = this.paths.get(index);
		Path path = new Path();
		for (int y = 0; y < shortestList.size(); y++) {
			Node node = shortestList.get(y);
			path.add(new Waypoint(map.getPos(Math.round(node.x)), map
					.getPos(Math.round(node.x))));
		}
		return path;
	}

	private boolean isIndexValid(int x, int y, ArrayList<Node> path) {
		boolean isValid = true;
		if (map.blocked(x, y)) {
			isValid = false;
		} else if (containsNode(new Node(x, y), path)) {
			isValid = false;
		}

		return isValid;

	}

	public void localize() {
		LCDinfo lcd = new LCDinfo();
		Orienteer localizer = new Orienteer(this.map, this);
		Pose startingPoint = localizer.localize();
		int x = map.getGrid(startingPoint.getX());
		int y = map.getGrid(startingPoint.getY());
		Direction dir = Direction.fromAngle(Math.round(startingPoint
				.getHeading()));
		lcd.setStartPos(x, y, dir);
	}

	public void rotate(double angle) {
		this.pilot.rotate(angle);
	}

	public void rotate(int angle) {
		this.pilot.rotate(angle);
	}

	public void setAngle(int ang) {
		Pose p = this.getPose();
		p.setHeading(ang);
		this.navigator.getPoseProvider().setPose(p);

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
		this.dropZone[0] = x;
		this.dropZone[1] = y;
		this.dropZone[2] = width;
		this.dropZone[3] = height;
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
		this.pickupZone[0] = x;
		this.pickupZone[1] = y;
		this.pickupZone[2] = width;
		this.pickupZone[3] = height;
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

	public void stop() {
		this.pilot.stop();

	}

	/**
	 * Turns to requested direction
	 * 
	 * @param direction
	 *            Cardinal Heading
	 */
	public void turnTo(Direction direction) {
		turnTo(direction.toAngle());
	}

	/**
	 * Turns to requested direction
	 * 
	 * @param angle
	 *            Angle clockwise from East (Positive x-axis)
	 */
	public void turnTo(double angle) {
		navigator.rotateTo(angle); // Rotates to specified angle
	}

	public boolean moving(){
		return pilot.isMoving();
	}
	
	public void gotoPoint(Point next) {
		Waypoint wp = new Waypoint(next);
		navigator.goTo(wp);
	}
}
