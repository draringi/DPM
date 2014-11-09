package dpm.teamone.driver.navigation;

/**
 * Enumeration of the 4 cardinal directions
 * 
 * @author Michael Willaims
 *
 */
enum Direction {

	NORTH(0, "North"),

	EAST(1, "East"),

	SOUTH(2, "South"),

	WEST(3, "West");

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

	public static int toInt(Direction dir) {
		return dir.val;
	}

	/**
	 * Adds Cardinal directions together, to correct for offsets
	 * 
	 * @param dir
	 *            Direction offset to add
	 * @return Corrected Direction
	 */

	private int val;

	private String name;

	Direction(int i, String name) {
		this.val = i;
		this.name = name;
	}

	public Direction add(Direction dir) {
		return toDirection(this.val + dir.val);
	}

	@Override
	public String toString() {
		return this.name;
	}

}