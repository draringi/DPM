import java.util.BitSet;

public class Map {
	private BitSet bitset; 
	private int width, height;
	
	public Map(int height, int width) {
		this.height = height;
		this.width = width;
		bitset = new BitSet(width*height);
	}

	public void clear(){
		bitset.clear();
	}
	
	public void clear(int x, int y){
		bitset.clear(getIndex(x,y));
	}
	
	public void set(int x, int y){
		bitset.set(getIndex(x,y));
	}
	
	public static Map DefaultMap(){
		Map map = new Map(4, 4);
		map.set(1,0);
		map.set(0, 3);
		map.set(2, 2);
		map.set(3,2);
		return map;
	}
	
	public boolean blocked(int x, int y){
		if(x < 0||x >= width || y < 0 || y >= height){
			return true;
		}
		return bitset.get(getIndex(x,y));
	}
	
	private int getIndex(int x, int y){
		return y*width+x;
	}
	
	public int getHeight(){
		return this.height;
	}
	
	public int getWidth(){
		return this.width;
	}
	
	public int getGrid(double val){
		return (int) Math.round(Math.ceil(val));
	}
}
