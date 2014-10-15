import java.util.BitSet;

public abstract class Orientation {
	private Map map;
	private BitSet options;
	private Odometer odo;
	private UltraSonic us;
	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;
	public static final int FORWARD = 0, LEFT = 1, BACKWARDS = 2, RIGHT = 3;
	public static final int X = 0, Y = 1, THETA = 3;
	private int width, height;
	private static final double ANGLE_TOLERANCE = 10;
	
	private int getOptionIndex(int x, int y, int direction){
		return (y*map.getWidth()+x)*4+direction;
	}
	
	public Orientation(Map map, Odometer odo){
		this.map = map;
		this.odo = odo;
		this.us = new UltraSonic();
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
			double [] pos = new double[3];
			odo.getPosition(pos);
			int [] offset = new int[3];
			offset[X] = map.getGrid(pos[X]);
			offset[Y] = map.getGrid(pos[Y]);
			offset[THETA] =  getOrientation(pos[THETA]);
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
		return grid + offset;
	}
	
	public int getOffsetDist(int grid, double offset){
		return grid + map.getGrid(offset);
	}
	
	public int getOrientation(double angle){
		angle = Odometer.fixDegAngle(angle);
		double offset = Math.abs(Odometer.minimumAngleFromTo(angle, 0));
		if (offset < ANGLE_TOLERANCE){
			return FORWARD;
		}
		offset = Math.abs(Odometer.minimumAngleFromTo(angle, 90));
		if (offset < ANGLE_TOLERANCE){
			return RIGHT;
		}
		offset = Math.abs(Odometer.minimumAngleFromTo(angle, 180));
		if (offset < ANGLE_TOLERANCE){
			return BACKWARDS;
		}
		offset = Math.abs(Odometer.minimumAngleFromTo(angle, 270));
		if (offset < ANGLE_TOLERANCE){
			return LEFT;
		}
		return -1;
	}
	
	public int getOffsetDirection(int initial, int offset){
		return (initial + offset)%4;
	}
	
	public int getOffsetDirection(int initial, double angle){
		return (initial + getOrientation(angle))%4;
	}
	
	public boolean validOption(int x, int y, int direction, int xOffset, int yOffset, int dOffset, boolean wall){
		x = getOffsetDist(x, xOffset);
		y = getOffsetDist(y, yOffset);
		if(!map.valid(x, y)){
			return false;
		}
		direction = getOffsetDirection(direction, dOffset);
		return match(x, y, direction, wall);
	}
}
