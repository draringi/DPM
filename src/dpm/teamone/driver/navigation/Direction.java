package dpm.teamone.driver.navigation;

/**
 * Enumeration of the 4 cardinal directions
 * 
 * @author Michael Willaims
 *
 */
public enum Direction {

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
	
	public static Direction fromAngle(int angle){
		// Sanitize any bad angles. This section should be skipped, but exists for necessities sake/
		while(angle < 0){
			angle += 360;
		}
		angle = angle % 360;
		angle += 45;
		angle /= 90;
		angle = angle % 4;
		Direction dir;
		switch(angle){
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

	public float toAngle(){
		switch(this){
		case NORTH:
			return 90;
		case EAST:
			return 0;
		case SOUTH:
			return 270;
		case WEST:
		default:
			return 180;
		}
	}
	
	@Override
	public String toString() {
		return this.name;
	}

}