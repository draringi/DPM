package dpm.teamone.driver.navigation;

import java.util.BitSet;

import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import lejos.util.Delay;
import dpm.teamone.driver.events.EventManager;
import dpm.teamone.driver.maps.GridMap;

/**
 * Smart Localization system, which tries to run in log(n) time,
 * where n is the number of potential starting positions.
 * @author Michael Williams
 *
 */
public class Orienteer {
	
	/**
	 * Adds 2 Odometer system co-ordinates together in a sane manner
	 *
	 * @param posOne
	 * @param posTwo
	 * @return
	 */
	public static Pose addPositions(Pose posOne, Pose posTwo) {
		Pose result = posOne;
		result.setLocation(posOne.getLocation().add(posTwo.getLocation()));
		result.rotateUpdate(posTwo.getHeading());
		return result;
	}

	/**
	 * Helper function to convert to radians for use in the Java trig functions
	 * @param angle Angle in Degrees
	 * @return Angle in Radians
	 */
	public static double degreesToRads(float angle) {
		return ((angle / 180) * Math.PI);
	}

	/**
	 * Helper function that takes angle, and returns basic facing enum 
	 * @param angle Angle in Degrees
	 * @return Integer value representing a direction
	 */
	public static int facingToInt(float angle) {
		angle /= 90;
		return -Math.round(angle) % 4;
	}

	/**
	 * Helpter function that converts {@Link Direction} Integer values to radians.
	 * @param orientation Integer representation of a Direction
	 * @return Angle in Radians
	 */
	public static double orientationToRads(int orientation) {
		return degreesToRads(Direction.intToAngle(orientation));
	}

	/**
	 * Basic helper enum for direction facing, which adds well with Direction integers.
	 * They are negative for this reason, as these are counterclock-wise, and Direction ints are clockwise in nature.
	 * Value: {@value} 
	 */
	public static final int FORWARD = 0, LEFT = -1, BACKWARDS = -2, RIGHT = -3;

	/**
	 * Integer representations of Directions.
	 * @see Direction
	 */
	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;

	/**
	 * Max Distance in cm to consider a wall. Value: {@value}cm.
	 */
	public static final int THRESHOLD = 25;

	/**
	 * Index values of location arrays.
	 */
	public static final int X = 0, Y = 1, THETA = 2;

	private int count;
	private final LCDinfo lcd;
	private final Object lock;

	private final GridMap map;

	private final NavigationController nav;

	private final BitSet options;

	private final UltraSonic us;

	private final int width, height;

	/**
	 * Default constructor
	 * 
	 * @param map Map to be used
	 * @param nav Navigation Controller
	 */
	public Orienteer(GridMap map, NavigationController nav) {
		this.map = map;
		this.nav = nav;
		this.us = new UltraSonic();
		this.width = map.getWidth();
		this.height = map.getHeight();
		this.lcd = new LCDinfo();
		int x, y;
		this.options = new BitSet(this.height * this.width * 4);
		this.count = 0;
		this.lock = new Object();
		this.options.clear();
		for (y = 0; y < this.height; y++) {
			for (x = 0; x < this.width; x++) {
				if (!map.blocked(x, y)) {
					this.options.set(this.getOptionIndex(x, y, NORTH));
					this.options.set(this.getOptionIndex(x, y, EAST));
					this.options.set(this.getOptionIndex(x, y, SOUTH));
					this.options.set(this.getOptionIndex(x, y, WEST));
				}
			}
		}
	}

	/**
	 * Function to count how many remaining options would have a wall if we traveled
	 * in a given direction. 
	 * @param direction Direction to travel.
	 * @return Number of remaining options that would have a wall.
	 */
	private int changeCount(int direction) {
		Pose pos = this.nav.getPose();
		int offset[] = new int[3];

		offset[THETA] = (facingToInt(pos.getHeading()) + direction) % 4;
		if (direction == FORWARD) {
			Point p = this.getFront();
			offset[X] = this.map.getGrid(p.getX());
			offset[Y] = this.map.getGrid(p.getY());
		} else {
			offset[X] = this.map.getGrid(pos.getX());
			offset[Y] = this.map.getGrid(pos.getY());
		}
		int changecount = 0;
		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				for (int d = 0; d < 4; d++) {
					if (this.isOption(x, y, d)) {
						int option[] = new int[3];
						option[X] = x;
						option[Y] = y;
						option[THETA] = d;
						int[] correctedOffset = this.getCorrectedOffset(offset,
								option[THETA]);
						if (this.validOption(option, correctedOffset, true)) {
							changecount++;
						}
					}
				}
			}
		}
		return changecount;
	}

	/**
	 * removes an option from the option matrix
	 * 
	 * @param x
	 * @param y
	 * @param direction
	 */
	public void clearOption(int x, int y, int direction) {
		this.options.clear(this.getOptionIndex(x, y, direction));
	}

	/**
	 * corrects an offset for a given starting orientation in the grid system
	 * 
	 * @param offset
	 * @param orientation
	 * @return
	 */
	public int[] getCorrectedOffset(int[] offset, int orientation) {
		int[] correctedOffset = new int[3];
		double angle = orientationToRads(orientation);
		correctedOffset[THETA] = offset[THETA];
		correctedOffset[X] = (int) Math.round((offset[X] * Math.cos(angle))
				- (offset[Y] * Math.sin(angle)));
		correctedOffset[Y] = (int) Math.round((offset[Y] * Math.cos(angle))
				+ (offset[X] * Math.sin(angle)));
		return correctedOffset;
	}

	/**
	 * corrects an offset for a given starting orientation in the odometer
	 * system
	 * 
	 * @param offset
	 * @param orientation
	 */
	public void getCorrectedOffset(Pose offset, int orientation) {
		float x = offset.getX() + 15;
		float y = offset.getY() + 15;
		double angle = orientationToRads(orientation);
		offset.setLocation(
				(float) ((x * Math.cos(angle)) - (y * Math.sin(angle))),
				(float) ((y * Math.cos(angle)) + (x * Math.sin(angle))));
	}

	/**
	 * 
	 * @return number of checks occured
	 */
	public int getCount() {
		int result;
		synchronized (this.lock) {
			result = this.count;
		}
		return result;
	}

	/**
	 * @return  Point containing grid co-ords of the grid in front of the robot. 
	 */
	private Point getFront() {
		int x, y;
		Direction dir;
		Pose pos = this.nav.getPose();
		x = this.map.getGrid(pos.getX());
		y = this.map.getGrid(pos.getY());
		dir = Direction.fromAngle(pos.getHeading());
		switch (dir) {
		case EAST:
			x++;
			break;
		case NORTH:
			y++;
			break;
		case WEST:
			x--;
			break;
		case SOUTH:
			y--;
			break;
		}
		return new Point((float) this.map.getPos(x), (float) this.map.getPos(y));
	}

	/**
	 * Adds 2 directions, and returns a valid direction
	 * 
	 * @param initial
	 * @param angle
	 * @return
	 */
	public int getOffsetDirection(int initial, double angle) {
		return (initial + facingToInt((float) angle)) % 4;
	}

	/**
	 * Adds 2 directions, and returns a valid direction
	 * 
	 * @param initial
	 * @param offset
	 * @return
	 */
	public int getOffsetDirection(int initial, int offset) {
		return (initial + offset + 4) % 4;
	}

	/**
	 * Adds a grid ID and an Offset
	 * @param grid Grid ID
	 * @param offset Offset in cm
	 * @return Updated grid ID
	 */
	public int getOffsetDist(int grid, double offset) {
		return grid + this.map.getGrid(offset);
	}

	/**
	 * Adds a grid ID and an Offset
	 * @param grid Grid ID
	 * @param offset Offset in grids
	 * @return Updated grid ID
	 */
	public int getOffsetDist(int grid, int offset) {
		return grid + offset;
	}

	/**
	 * Returns the start point, if only 1 option remains.
	 * 
	 * @param start
	 *            Start Point
	 */
	public void getOption(int[] start) {
		if (this.options.cardinality() == 1) {
			for (int y = 0; y < this.height; y++) {
				for (int x = 0; x < this.width; x++) {
					for (int d = 0; d < 4; d++) {
						if (this.isOption(x, y, d)) {
							start[X] = x;
							start[Y] = y;
							start[THETA] = d;
							return;
						}
					}
				}
			}
			start[THETA] = -1;
		} else {
			start[THETA] = -2;
		}

	}

	/**
	 * Converts an x, y, heading triplet into an index value
	 * 
	 * @param x
	 * @param y
	 * @param direction
	 * @return Internal index value
	 */
	private int getOptionIndex(int x, int y, int direction) {
		return (((y * this.map.getWidth()) + x) * 4) + direction;
	}

	/**
	 * checks if a triplet is a valid option
	 * 
	 * @param x
	 * @param y
	 * @param direction
	 * @return True if valid, false otherwise.
	 */
	public boolean isOption(int x, int y, int direction) {
		return this.options.get(this.getOptionIndex(x, y, direction));
	}

	/**
	 * Navigate the map, until the start point is concluded Then, update the
	 * odometer
	 * 
	 * @return Starting location and orientation
	 */
	public Pose localize() {
		this.nav.setPose(new Pose(-15, -15, 0));
		int[] option;
		int[] offset;
		Pose pos;
		while (this.options.cardinality() > 1) {
			pos = this.nav.getPose();
			offset = new int[3];
			offset[X] = this.map.getGrid(pos.getX());
			offset[Y] = this.map.getGrid(pos.getY());
			offset[THETA] = facingToInt(pos.getHeading());
			boolean wall = (this.us.poll() < THRESHOLD);

			for (int y = 0; y < this.height; y++) {
				for (int x = 0; x < this.width; x++) {
					for (int d = 0; d < 4; d++) {
						if (this.isOption(x, y, d)) {
							option = new int[3];
							option[X] = x;
							option[Y] = y;
							option[THETA] = d;
							int[] correctedOffset = this.getCorrectedOffset(
									offset, option[THETA]);
							if (!this
									.validOption(option, correctedOffset, wall)) {
								this.options
								.clear(this.getOptionIndex(x, y, d));
							}
						}
					}
				}
			}
			synchronized (this.lock) {
				this.count++;
			}

			if (this.options.cardinality() <= 1) {
				break;
			}
			this.move(wall, Direction.fromAngle(Math.round(pos.getHeading())));
		}
		option = new int[3];
		this.getOption(option);
		this.lcd.setStartPos(option);
		pos = this.nav.getPose();
		this.getCorrectedOffset(pos, option[THETA]);
		Pose start = new Pose();
		start.setLocation((float) this.map.getPos(option[X]),
				(float) this.map.getPos(option[Y]));
		start.setHeading(Direction.intToAngle(option[THETA]));
		this.nav.setPose(addPositions(pos, start));
		return start;
	}

	/**
	 * Checks if the map status at a given location and heading matches the
	 * ultrasonic reading.
	 * 
	 * @param x
	 * @param y
	 * @param direction
	 * @param blocked
	 * @return true, if matching, false otherwise.
	 */
	public boolean match(int x, int y, int direction, boolean blocked) {
		double angle = orientationToRads(direction);
		x += (int) Math.round(Math.cos(angle));
		y += (int) Math.round(Math.sin(angle));
		return this.map.blocked(x, y) == blocked;
	}

	/**
	 * Moves the robot to a new orientation, dependent on implementation
	 * 
	 * @param wall
	 *            If there is a wall in front of the robot or not
	 * @param direction
	 *            Current travel direction relative to starting point
	 */
	public void move(boolean wall, Direction direction) {
		int remaining_center = this.options.cardinality() / 2;
		int count[] = new int[2];
		count[0] = Math.abs(this.changeCount(LEFT) - remaining_center);
		count[1] = Math.abs(this.changeCount(RIGHT) - remaining_center);
		int min;
		if (count[0] > count[1]) {
			min = 1;
		} else {
			min = 0;
		}
		if (!wall) {
			int frontCount = Math.abs(this.changeCount(FORWARD)
					- remaining_center);
			if (frontCount <= count[min]) {
				min = 2;
			}
		}
		float angle;
		switch (min) {
		case 0:
			angle = Direction.intToAngle((Direction.angleToInt(this.nav
					.getPose().getHeading()) + LEFT + 4) % 4);
			this.nav.turnTo(angle);
			break;
		case 1:
			angle = Direction.intToAngle((Direction.angleToInt(this.nav
					.getPose().getHeading()) + RIGHT + 4) % 4);
			this.nav.turnTo(angle);
			break;
		case 2:
			this.nav.gotoPoint(this.getFront());
			Delay.msDelay(400);
			EventManager.restart();
			EventManager.restart();
			while (this.nav.moving()) {
				Delay.msDelay(100);
			}
			EventManager.pause();
			EventManager.pause();
			Delay.msDelay(10);
		}
	}

	/**
	 * Returns the number of options left
	 * 
	 * @return Options left
	 */
	public int optionsLeft() {
		return this.options.cardinality();
	}

	/**
	 * Reports if an option is potentially valid, given an offset and Ultrasonic
	 * reading
	 * 
	 * @param x
	 * @param y
	 * @param direction
	 * @param xOffset
	 * @param yOffset
	 * @param dOffset
	 * @param wall
	 * @return
	 */
	public boolean validOption(int x, int y, int direction, int xOffset,
			int yOffset, int dOffset, boolean wall) {
		x += xOffset;
		y += yOffset;
		if (!this.map.valid(x, y)) {
			return false;
		}
		direction = this.getOffsetDirection(direction, dOffset);
		return this.match(x, y, direction, wall);
	}

	/**
	 * Reports if an option is potentially valid, given an offset and Ultrasonic
	 * reading
	 * 
	 * @param pos
	 * @param offset
	 * @param wall
	 * @return
	 */
	public boolean validOption(int[] pos, int[] offset, boolean wall) {
		return this.validOption(pos[X], pos[Y], pos[THETA], offset[X],
				offset[Y], offset[THETA], wall);
	}
}
