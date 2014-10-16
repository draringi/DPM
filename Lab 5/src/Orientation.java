import java.util.BitSet;

public abstract class Orientation {
	private Map map;
	private BitSet options;
	private Odometer odo;
	private UltraSonic us;
	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;
	public static final int FORWARD = 0, LEFT = 1, BACKWARDS = 2, RIGHT = 3;
	public static final int X = 0, Y = 1, THETA = 2;
	public static final int THRESHOLD = 25;
	private int width, height;
	private static final double ANGLE_TOLERANCE = 45, TILE_SIZE = 30, TILE_OFFSET = 15;
	private int count;
	private Object lock;
	private static final boolean [] UPDATE_ALL = {true, true, true};
	
	private int getOptionIndex(int x, int y, int direction){
		return (y*map.getWidth()+x)*4+direction;
	}
	private void getIndexOption(int index, int [] option){
		option[THETA] = index % 4;
		index = index/4;
		option[X] = index % width;
		option[Y] = index / width;
	}
	
	public Orientation(Map map, Odometer odo){
		this.map = map;
		this.odo = odo;
		this.us = new UltraSonic();
		this.width = map.getWidth();
		this.height = map.getHeight();
		int x, y;
		this.options = new BitSet(height*width*4);
		this.count = 0;
		this.lock = new Object();
		
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
	
	/**
	 * Moves the robot to a new orientation, dependent on implementation
	 * @param wall If there is a wall in front of the robot or not
	 * @param direction Current travel direction relative to starting point
	 */
	abstract public void move(boolean wall, int direction);
	
	public int optionsLeft(){
		return options.cardinality();
	}
	
	public void getOption(int [] start){
		if(options.cardinality() == 1){
			for(int i=0; i < options.length(); i++){
				if(options.get(i)){
					getIndexOption(i, start);
					break;
				}
			}
		}
	}
	
	
	public void orienteer(){
		int i;
		int [] option;
		int [] offset;
		double [] pos;
		while(options.cardinality() != 1){
			pos = new double[3];
			odo.getPosition(pos);
			offset = new int[3];
			offset[X] = map.getGrid(pos[X], true);
			offset[Y] = map.getGrid(pos[Y], true);
			offset[THETA] =  getOrientation(pos[THETA]);
			boolean wall = (us.poll() < THRESHOLD );
			
			for(i=0; i < options.length(); i++){
				if(options.get(i)){
					option = new int[3];
					getIndexOption(i, option);
					int [] correctedOffset = getCorrectedOffset(offset, option[THETA]);
					if(!validOption(option, correctedOffset, wall)){
						options.clear(i);
					}
				}
			}
			synchronized(lock){
				count++;
			}
			if(options.cardinality() == 1){
				break;
			}
			move(wall, offset[THETA]);
		}
		option = new int[3];
		for(i=0; i < options.length(); i++){
			if(options.get(i)){
				getIndexOption(i, option);
				break;
			}
		}
		pos = new double[3];
		odo.getPosition(pos);
		getCorrectedOffset(pos, option[THETA]);
		double [] start = new double [3];
		convertTilePosition(option, start);
		odo.setPosition(addPositions(pos, start), UPDATE_ALL);
	}
	
	public int getCount(){
		int result;
		synchronized(lock){
			result = this.count;
		}
		return result;
	}
	
	public static double [] addPositions(double [] posOne, double [] posTwo){
		double [] result = new double [3];
		result[X] = posOne[X] + posTwo[X];
		result[Y] = posOne[Y] + posTwo[Y];
		result[THETA] = Odometer.fixDegAngle(posOne[THETA] + posTwo[THETA]);
		return result;
	}
	
	public static void convertTilePosition(int [] tile, double [] pos){
		pos[X] = tile[X]*TILE_SIZE - TILE_OFFSET;
		pos[Y] = tile[Y]*TILE_SIZE - TILE_OFFSET;
		pos[THETA] = 90*tile[THETA];
	}
	
	public boolean isOption(int x, int y, int direction){
		return options.get(this.getOptionIndex(x, y, direction));
	}
	
	public void clearOption(int x, int y, int direction){
		options.clear(this.getOptionIndex(x, y, direction));
	}
	
	public boolean match(int x, int y, int direction, boolean blocked){
		boolean result = false;
		switch(direction){
		case NORTH:
			result = map.blocked(x, y+1);
			break;
		case EAST:
			result = map.blocked(x+1, y);
			break;
		case SOUTH:
			result = map.blocked(x, y-1);
			break;
		case WEST:
			result = map.blocked(x-1, y);
		}
		return result==blocked;
	}
	
	public int getOffsetDist(int grid, int offset){
		return grid + offset;
	}
	
	public int getOffsetDist(int grid, double offset){
		return grid + map.getGrid(offset);
	}
	
	public int getOrientation(double angle){
		angle = Odometer.fixDegAngle(angle);
		return (int) ( Math.round(angle / 90.0) ) % 4;
	}
	
	public int getOffsetDirection(int initial, int offset){
		return (initial + offset)%4;
	}
	
	public int getOffsetDirection(int initial, double angle){
		return (initial + getOrientation(angle))%4;
	}
	
	public int [] getCorrectedOffset(int [] offset, int orientation){
		int [] correctedOffset = new int[3];
		correctedOffset[THETA] = orientation;
		switch(orientation){
		case NORTH:
			correctedOffset[Y] = offset[Y];
			correctedOffset[X] = offset[X];
			break;
		case EAST:
			correctedOffset[X] = offset[Y];
			correctedOffset[Y] = -offset[X];
			break;
		case SOUTH:
			correctedOffset[Y] = -offset[Y];
			correctedOffset[X] = -offset[X];
			break;
		case WEST:
			correctedOffset[X] = -offset[Y];
			correctedOffset[Y] = offset[X];
			break;
		}
		return correctedOffset;	
	}
	
	public void getCorrectedOffset(double [] offset, int orientation){
		double [] correctedOffset = new double[2];
		offset[THETA] = orientation * 90;
		switch(orientation){
		case NORTH:
			correctedOffset[Y] = offset[Y];
			correctedOffset[X] = offset[X];
			break;
		case EAST:
			correctedOffset[X] = offset[Y];
			correctedOffset[Y] = -offset[X];
			break;
		case SOUTH:
			correctedOffset[Y] = -offset[Y];
			correctedOffset[X] = -offset[X];
			break;
		case WEST:
			correctedOffset[X] = -offset[Y];
			correctedOffset[Y] = offset[X];
			break;
		}
		offset[X] = correctedOffset[X];
		offset[Y] = correctedOffset[Y];
	}
	
	public boolean validOption(int [] pos, int [] offset, boolean wall){
		return validOption(pos[X], pos[Y], pos[THETA], offset[X], offset[Y], offset[THETA], wall);
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
