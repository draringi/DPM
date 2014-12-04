package dpm.teamone.driver.maps;

/**
 * MapFactory is a Map producing Factory with only static functions It is used
 * to get Maps for use in other classes.
 * 
 * @author Michael Williams
 * @see GridMap
 */
public class MapFactory {

	/**
	 * @return Map 1 from the Beta Demo.
	 */
	private static GridMap beta1() {
		GridMap map = new GridMap(8, 8, (byte) 2, (byte) 2);
		map.set(0, 5);
		map.set(1, 7);
		map.set(2, 4);
		map.set(2, 6);
		map.set(2, 7);
		map.set(3, 5);
		map.set(4, 1);
		map.set(4, 2);
		map.set(4, 3);
		map.set(6, 2);
		map.set(6, 5);
		map.set(7, 0);
		map.set(7, 2);
		map.set(7, 3);
		map.set(7, 6);
		return map;
	}

	/**
	 * @return Map 2 from the Beta Demo.
	 */
	private static GridMap beta2() {
		GridMap map = new GridMap(8, 8, (byte) 2, (byte) 2);
		map.set(0, 5);
		map.set(1, 6);
		map.set(2, 0);
		map.set(2, 3);
		map.set(2, 4);
		map.set(3, 1);
		map.set(3, 7);
		map.set(4, 4);
		map.set(5, 6);
		map.set(5, 7);
		map.set(5, 0);
		map.set(7, 0);
		map.set(7, 1);
		map.set(7, 6);
		map.set(7, 7);
		return map;
	}

	/**
	 * @return Map 3 from the Beta Demo.
	 */
	private static GridMap beta3() {
		GridMap map = new GridMap(8, 8, (byte) 2, (byte) 2);
		map.set(0, 7);
		map.set(2, 3);
		map.set(2, 6);
		map.set(3, 2);
		map.set(3, 3);
		map.set(3, 4);
		map.set(3, 6);
		map.set(4, 0);
		map.set(4, 7);
		map.set(5, 0);
		map.set(5, 5);
		map.set(6, 4);
		map.set(7, 0);
		map.set(7, 4);
		map.set(7, 6);
		return map;
	}

	/**
	 * @return Blank 4x4 map.
	 */
	public static GridMap blankMap() {
		return new GridMap(4, 4, (byte) 2, (byte) 2);
	}

	/**
	 * Returns requested map for Beta Demo
	 * 
	 * @param mapID
	 *            Map wanted
	 * @return Map requested.
	 */
	public static GridMap getBetaMap(int mapID) {
		switch (mapID) {
		case 1:
			return beta1();
		case 2:
			return beta2();
		case 3:
			return beta3();
		default:
			return null;
		}
	}

	/**
	 * Easy programmatic way to get a map given a MapID
	 * 
	 * @param mapID
	 *            Id of the map wanted
	 * @return GridMap representation of requested Map
	 */
	public static GridMap getMap(int mapID) {
		switch (mapID) {
		case 1:
			return mapOne();
		case 2:
			return mapTwo();
		case 3:
			return mapThree();
		case 4:
			return mapFour();
		case 5:
			return mapFive();
		case 6:
			return mapSix();
		default:
			return null;
		}
	}

	/**
	 * Static producer of maps matching the one used in Lab 5
	 * 
	 * @return Lab 5 style GridMap
	 */
	public static GridMap lab5Map() {
		GridMap map = new GridMap(4, 4, (byte) 3, (byte) 1);
		map.set(1, 0);
		map.set(0, 3);
		map.set(2, 2);
		map.set(3, 2);
		return map;
	}

	/**
	 * @return Map 5 from the Final Demo.
	 */
	private static GridMap mapFive() {
		GridMap map = new GridMap(12, 12, (byte) 2, (byte) 2);
		map.set(0, 3);
		map.set(0, 5);
		map.set(0, 8);
		map.set(3, 1);
		map.set(3, 3);
		map.set(3, 10);
		map.set(4, 3);
		map.set(4, 6);
		map.set(4, 7);
		map.set(4, 10);
		map.set(5, 8);
		map.set(6, 9);
		map.set(7, 6);
		map.set(7, 7);
		map.set(8, 1);
		map.set(8, 5);
		map.set(8, 10);
		map.set(9, 2);
		map.set(9, 6);
		map.set(10, 10);
		map.set(11, 2);
		map.set(11, 9);
		return map;
	}

	/**
	 * @return Map 4 from the Final Demo.
	 */
	private static GridMap mapFour() {
		GridMap map = new GridMap(12, 12, (byte) 2, (byte) 2);
		map.set(0, 2);
		map.set(0, 3);
		map.set(0, 4);
		map.set(0, 8);
		map.set(1, 4);
		map.set(2, 0);
		map.set(2, 5);
		map.set(2, 10);
		map.set(3, 2);
		map.set(3, 9);
		map.set(3, 11);
		map.set(4, 3);
		map.set(5, 10);
		map.set(6, 4);
		map.set(8, 4);
		map.set(8, 8);
		map.set(9, 0);
		map.set(9, 4);
		map.set(9, 6);
		map.set(11, 1);
		map.set(11, 5);
		map.set(11, 10);
		return map;
	}

	/**
	 * @return Map 1 from the Final Demo.
	 */
	private static GridMap mapOne() {
		GridMap map = new GridMap(12, 12, (byte) 2, (byte) 1);
		map.set(0, 6);
		map.set(0, 9);
		map.set(1, 3);
		map.set(2, 2);
		map.set(2, 11);
		map.set(3, 5);
		map.set(3, 10);
		map.set(4, 4);
		map.set(4, 7);
		map.set(5, 0);
		map.set(5, 2);
		map.set(6, 3);
		map.set(7, 7);
		map.set(8, 0);
		map.set(8, 1);
		map.set(8, 6);
		map.set(9, 4);
		map.set(9, 7);
		map.set(9, 9);
		map.set(10, 2);
		map.set(10, 6);
		map.set(11, 1);
		return map;
	}

	/**
	 * @return Map 6 from the Final Demo.
	 */
	private static GridMap mapSix() {
		GridMap map = new GridMap(12, 12, (byte) 2, (byte) 2);
		map.set(0, 10);
		map.set(1, 5);
		map.set(2, 3);
		map.set(3, 2);
		map.set(3, 10);
		map.set(4, 2);
		map.set(4, 7);
		map.set(5, 2);
		map.set(5, 4);
		map.set(5, 6);
		map.set(5, 11);
		map.set(6, 5);
		map.set(6, 9);
		map.set(7, 6);
		map.set(7, 8);
		map.set(8, 2);
		map.set(8, 10);
		map.set(9, 0);
		map.set(9, 4);
		map.set(9, 8);
		map.set(10, 5);
		map.set(10, 10);
		return map;
	}

	/**
	 * @return Map 3 from the Final Demo.
	 */
	private static GridMap mapThree() {
		GridMap map = new GridMap(12, 12, (byte) 2, (byte) 2);
		map.set(0, 3);
		map.set(0, 8);
		map.set(2, 3);
		map.set(2, 6);
		map.set(3, 2);
		map.set(3, 4);
		map.set(3, 10);
		map.set(4, 1);
		map.set(4, 5);
		map.set(4, 6);
		map.set(4, 7);
		map.set(6, 6);
		map.set(6, 8);
		map.set(7, 0);
		map.set(7, 11);
		map.set(9, 6);
		map.set(10, 0);
		map.set(10, 3);
		map.set(10, 4);
		map.set(11, 2);
		map.set(11, 11);
		return map;
	}

	/**
	 * @return Map 2 from the Final Demo.
	 */
	private static GridMap mapTwo() {
		GridMap map = new GridMap(12, 12, (byte) 2, (byte) 2);
		map.set(0, 4);
		map.set(0, 5);
		map.set(3, 2);
		map.set(3, 5);
		map.set(3, 6);
		map.set(3, 9);
		map.set(4, 5);
		map.set(4, 9);
		map.set(4, 10);
		map.set(5, 5);
		map.set(6, 8);
		map.set(6, 9);
		map.set(7, 0);
		map.set(8, 4);
		map.set(8, 5);
		map.set(8, 10);
		map.set(9, 1);
		map.set(9, 6);
		map.set(9, 11);
		map.set(10, 7);
		map.set(11, 2);
		map.set(11, 5);
		map.set(11, 8);
		return map;
	}
}
