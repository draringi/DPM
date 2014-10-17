import java.util.BitSet;

public class Map {
	private BitSet bitset; 
	private int width, height;
	
	public Map(int height, int width) {
		this.height = height;
		this.width = width;
		this.bitset = new BitSet(width*height);
		this.bitset.clear();
	}

	public void clear(){
		bitset.clear();
	}
	
	public void clear(int x, int y){
		bitset.clear(getIndex(x, y));
	}
	
	public void set(int x, int y){
		bitset.set(getIndex(x, y));
	}
	
	public static Map DefaultMap(){
		Map map = new Map(4, 4);
		map.set(1, 0);
		map.set(0, 3);
		map.set(2, 2);
		map.set(3, 2);
		return map;
	}
	
	public boolean blocked(int x, int y){
		if(!(valid(x, y))){
			return true;
		}
		return bitset.get(getIndex(x, y));
	}
	
	public int getIndex(int x, int y){
		return y*width+x;
	}
	
	public int getHeight(){
		return this.height;
	}
	
	public int getWidth(){
		return this.width;
	}
	
	public int getGrid(double val){
		return getGrid(val, false);
	}
	
	public double getPos(int val){
		return (double) val*30.0 - 15;
	}
	
	public double [] getPos(int [] val){
		double [] pos = new double [2];
		pos[Odometer.X] = getPos(val[Odometer.X]);
		pos[Odometer.Y] = getPos(val[Odometer.Y]);
		return pos;
	}
	
	public int getGrid(double val, boolean orienteering){
		val /=30;
		if(!orienteering){
			val = Math.ceil(val);
		}
		return (int) Math.round(val);
	}
	
	public boolean valid(int x, int y){
		return (x >= 0 && x < width && y >= 0 && y < height);
	}
}
