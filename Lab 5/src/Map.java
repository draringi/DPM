import java.util.BitSet;

/**
 * Map stores map data, and performs calculations between odometer data and map-tile data
 * @author Michael Williams (260369438)
 * @author Leonardo Siracusa (260585931)
 *
 */
public class Map {
	private BitSet bitset; 
	private int width, height;
	
	public Map(int height, int width) {
		this.height = height;
		this.width = width;
		this.bitset = new BitSet(width*height);
		this.bitset.clear();
	}

	/**
	 * Clears the map
	 */
	public void clear(){
		bitset.clear();
	}
	
	/**
	 * Clears a point on the map
	 */
	public void clear(int x, int y){
		bitset.clear(getIndex(x, y));
	}
	
	
	/**
	 * Sets a point on the map
	 */
	public void set(int x, int y){
		bitset.set(getIndex(x, y));
	}
	
	/**
	 * Factory for the default map for Lab 5
	 */
	public static Map DefaultMap(){
		Map map = new Map(4, 4);
		map.set(1, 0);
		map.set(0, 3);
		map.set(2, 2);
		map.set(3, 2);
		return map;
	}
	
	/**
	 * 
	 */
	public boolean blocked(int x, int y){
		if(!(valid(x, y))){
			return true;
		}
		return bitset.get(getIndex(x, y));
	}
	
	/**
	 * Converts 2 dimensional co-ordinates into linear index value
	 */
	public int getIndex(int x, int y){
		return y*width+x;
	}
	
	/**
	 * Returns the height of the map, as set at initialization
	 */
	public int getHeight(){
		return this.height;
	}
	
	/**
	 * Returns the width of the map, as set at initialization
	 */
	public int getWidth(){
		return this.width;
	}
	
	/**
	 * Converts double from the odometer system into grid id value
	 */
	public int getGrid(double val){
		return getGrid(val, false);
	}
	
	/**
	 * Converts grid-id value into odometer system value
	 */
	public double getPos(int val){
		return (double) val*30.0 - 15;
	}
	
	/**
	 * Converts grid co-ordinates into odometer system co-ordinates
	 */
	public double [] getPos(int [] val){
		double [] pos = new double [2];
		pos[Odometer.X] = getPos(val[Odometer.X]);
		pos[Odometer.Y] = getPos(val[Odometer.Y]);
		return pos;
	}
	
	/**
	 * Converts double from the odometer system into grid id value
	 */
	public int getGrid(double val, boolean orienteering){
		val /=30;
		if(!orienteering){
			val = Math.ceil(val);
		}
		return (int) Math.round(val);
	}
	
	/**
	 * Reports if a co-ordinate set is a valid spot on the map
	 */
	public boolean valid(int x, int y){
		return (x >= 0 && x < width && y >= 0 && y < height);
	}
}
