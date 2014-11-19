package dpm.teamone.driver.maps;

/**
 * MapFactory is a Map producing Factory with only static functions It is used
 * to get Maps for use in other classes.
 * 
 * @author Michael Williams
 *
 */
public class MapFactory {

	/**
	 * Easy programmatic way to get a map given a MapID
	 * 
	 * @param mapID
	 *            Id of the map wanted
	 * @return GridMap representation of requested Map
	 */
	public static GridMap getMap(int mapID) {
		switch(mapID){
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
		GridMap map = new GridMap(4, 4);
		map.set(1, 0);
		map.set(0, 3);
		map.set(2, 2);
		map.set(3, 2);
		return map;
	}

	public static GridMap getBetaMap(int mapID){
		switch(mapID){
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
	
	private static GridMap beta1(){
		GridMap map = new GridMap(8, 8);
		map.set(0, 5);
		map.set(1, 7);
		map.set(2, 4);
		map.set(2, 6);
		map.set(2, 7);
		map.set(3, 5);
		map.set(4, 1);
		map.set(4, 2);
		map.set(4, 3);
		map.set(6, 3);
		map.set(6, 5);
		map.set(7, 0);
		map.set(7, 2);
		map.set(7, 3);
		map.set(7, 6);
		return map;
	}
	
	private static GridMap beta2(){
		GridMap map = new GridMap(8, 8);
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
	
	private static GridMap beta3(){
		GridMap map = new GridMap(8, 8);
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
}