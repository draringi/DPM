package dpm.teamone.driver.navigation;

/**
 * Enumeration of the 4 cardinal directions
 * 
 * @author Michael Willaims
 * 
 */
public enum Direction {

	EAST(1, "East"),

	NORTH(0, "North"),

	SOUTH(2, "South"),

	WEST(3, "West");

	/**
	 * Converts from angle to basic integer enums without returning a Direction object.
	 * @param angle Heading in degrees.
	 * @return Integer representation of direction.
	 */
	public static int angleToInt(float angle) {
		return toInt(fromAngle(angle));
	}

	/**
	 * Creates Direction object from angle
	 * @param angle Heading in degrees.
	 * @return Direction object corresponding to the cardinal direction of given angle.
	 */
	public static Direction fromAngle(float angle) {
		// Sanitize any bad angles. This section should be skipped, but exists
		// for necessities sake/
		while (angle < 0) {
			angle += 360;
		}
		int angle_int;
		angle = angle % 360;
		angle /= 90;
		angle_int = Math.round(angle) % 4;
		Direction dir;
		switch (angle_int) {
		case 0:
			dir = EAST;
			break;
		case 1:
			dir = NORTH;
			break;
		case 2:
			dir = WEST;
			break;
		case 3:
		default:
			dir = SOUTH;
		}
		return dir;
	}

	/**
	 * Converts from basic integer enums to angle without returning a Direction object.
	 * @param val Integer representation of direction.
	 * @return Corresponding angle in degrees.
	 */
	public static float intToAngle(int val) {
		return toDirection(val).toAngle();
	}

	/**
	 * Converts from basic integer enums to Direction Object.
	 * @param i Integer value of Direction.
	 * @return Direction associated with integer.
	 */
	public static Direction toDirection(int i) {
		Direction result = NORTH;
		i = i % 4;
		switch (i) {
		case 0:
			result = NORTH;
			break;
		case 1:
			result = EAST;
			break;
		case 2:
			result = SOUTH;
			break;
		case 3:
			result = WEST;
			break;
		}
		return result;
	}

	/**
	 * @param dir Direction
	 * @return Integer representation.
	 */
	public static int toInt(Direction dir) {
		return dir.val;
	}

	/**
	 * Cardinal name of direction in English.
	 */
	private String name;

	/**
	 * Integer representation of direction.
	 */
	private int val;

	/**
	 * @param i 
	 * @param name
	 * @see #name
	 * @see #val
	 */
	Direction(int i, String name) {
		this.val = i;
		this.name = name;
	}

	/**
	 * Adds Cardinal directions together, to correct for offsets
	 * 
	 * @param dir
	 *            Direction offset to add
	 * @return Corrected Direction
	 */
	public Direction add(Direction dir) {
		return toDirection(this.val + dir.val);
	}

	/**
	 * @return Angle of direction in degrees.
	 */
	public float toAngle() {
		switch (this) {
		case NORTH:
			return 90;
		case EAST:
			return 0;
		case SOUTH:
			return -90;
		case WEST:
		default:
			return 180;
		}
	}

	/**
	 * Name of Direction, conforming to Java API standards.
	 */
	@Override
	public String toString() {
		return this.name;
	}

}