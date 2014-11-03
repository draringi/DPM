package dpm.teamone.driver.maps;

/**
 * MapFactory is a Map producing Factory with only static functions
 * It is used to get Maps for use in other classes.
 * @author Michael Williams
 *
 */
public class MapFactory {

	/**
	 * Easy programmatic way to get a map given a MapID
	 * @param mapID Id of the map wanted
	 * @return GridMap representation of requested Map
	 */
	public static GridMap getMap(int mapID) {
		return null;
	}
	
	/**
	 * Static producer of maps matching the one used in Lab 5 
	 * @return Lab 5 style GridMap
	 */
	public static GridMap lab5Map(){
		GridMap map = new GridMap(4,4);
		map.set(1, 0);
		map.set(0, 3);
		map.set(2, 2);
		map.set(3, 2);
		return map;
	}

}