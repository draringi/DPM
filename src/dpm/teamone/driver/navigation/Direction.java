package dpm.teamone.driver.navigation;

/**
 * Enumeration of the 4 cardinal directions
 * @author Michael Willaims
 *
 */
enum Direction {

	NORTH,

	EAST,

	SOUTH,

	WEST;

	/**
	 * Adds Cardinal directions together, to correct for offsets
	 * @param dir Direction offset to add
	 * @return Corrected Direction
	 */
	public Direction add(Direction dir) {
		return this;
	}

}