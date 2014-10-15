import java.util.BitSet;

public abstract class Orientation {
	private Map map;
	private BitSet options;
	private Odometer odo;
	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;
	public static final int FORWARD = 0, LEFT = 1, BACKWARDS = 2, RIGHT = 3;
	private int width, height;
	
	private int getOptionIndex(int x, int y, int direction){
		return (y*map.getWidth()+x)*4+direction;
	}
	
	public Orientation(Map map, Odometer odo){
		this.map = map;
		this.odo = odo;
		this.width = map.getWidth();
		this.height = map.getHeight();
		int x, y;
		this.options = new BitSet(height*width*4);
		
		for (x=0;x < width; x++){
			for(y=0;y < height; y++){
				if(!map.blocked(x, y)){
					options.set(this.getOptionIndex(x, y, NORTH));
					options.set(this.getOptionIndex(x, y, EAST));
					options.set(this.getOptionIndex(x, y, SOUTH));
					options.set(this.getOptionIndex(x, y, WEST));
				}
			}
		}
	}
	
	public void orienteer(){
		while(options.cardinality() != 1){
			//TODO: Add orienteering code
		}
	}
	
	public boolean isOption(int x, int y, int direction){
		return options.get(this.getOptionIndex(x, y, direction));
	}
	
	public void clearOption(int x, int y, int direction){
		options.clear(this.getOptionIndex(x, y, direction));
	}
	
	public boolean match(int x, int y, int direction, boolean blocked){
		return options.get(this.getOptionIndex(x, y, direction))==blocked;
	}
	
	public int getOffsetDist(int grid, int offset){
		return grid - offset;
	}
	
	public int getOffsetDist(int grid, double offset){
		return grid - map.getGrid(offset);
	}
	
	
}
