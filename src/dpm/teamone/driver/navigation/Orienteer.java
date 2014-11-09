package dpm.teamone.driver.navigation;
import java.util.BitSet;

import dpm.teamone.driver.maps.GridMap;

/**
 * 
 * @author Michael Williams (260369438)
 * @author Leonardo Siracusa (260585931)
 *
 */
public abstract class Orienteer {
	private GridMap map;
	private BitSet options;
	private NavigationController nav;
	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;
	public static final int FORWARD = 0, LEFT = 1, BACKWARDS = 2, RIGHT = 3;
	public static final int X = 0, Y = 1, THETA = 2;
	public static final int THRESHOLD = 25;
	private int width, height;
	private static final double TILE_SIZE = 30, TILE_OFFSET = 15;
	private int count;
	private Object lock;
	private UltraSonic us;

	
	
	/**
	 * Converts an x, y, heading triplet into an index value
	 * @param x
	 * @param y
	 * @param direction
	 * @return Internal index value
	 */
	private int getOptionIndex(int x, int y, int direction){
		return (y*map.getWidth()+x)*4+direction;
	}
	
	/**
	 * Default constructor 
	 * @param map
	 * @param odo
	 */
	public Orienteer(GridMap map, NavigationController nav){
		this.map = map;
		this.nav = nav;
		this.us = new UltraSonic();
		this.width = map.getWidth();
		this.height = map.getHeight();
		int x, y;
		this.options = new BitSet(height*width*4);
		this.count = 0;
		this.lock = new Object();
		this.options.clear();
		for (y=0;y < height; y++){
			for(x=0;x < width; x++){
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
	public void move(boolean wall, int direction){
		
	}
	
	/**
	 * Returns the number of options left
	 * @return
	 */
	public int optionsLeft(){
		return options.cardinality();
	}
	
	/**
	 * Returns the start point, if only 1 option remains. 
	 * @param start Start Point
	 */
	public void getOption(int [] start){
		if(options.cardinality() == 1){
			for(int y=0; y < height; y++){
				for(int x=0; x < width; x++){
					for(int d=0; d < 4; d++){
						if(isOption(x, y, d)){
							start[X] = x;
							start[Y] = y;
							start[THETA] = d;
							return;
						}
					}
				}
			}
			start[THETA] = -1;
		} else {
			start[THETA] = -2;
		}
		
	}
	
	/**
	 * Navigate the map, until the start point is concluded
	 * Then, update the odometer
	 * @param nav
	 */
	public void localize(){
		int [] option;
		int [] offset;
		double [] pos;
		while(options.cardinality() > 1){
			pos = new double[3];
			nav.getPosition(pos);
			offset = new int[3];
			offset[X] = map.getGrid(pos[X], true);
			offset[Y] = map.getGrid(pos[Y], true);
			offset[THETA] =  getOrientation(pos[THETA]);
			boolean wall = (us.poll() < THRESHOLD );
			
			for(int y=0; y < height; y++){
				for(int x=0; x < width; x++){
					for(int d=0; d < 4; d++){
						if(isOption(x, y, d)){
							option = new int[3];
							option[X] = x;
							option[Y] = y;
							option[THETA] = d;
							int [] correctedOffset = getCorrectedOffset(offset, option[THETA]);
							if(!validOption(option, correctedOffset, wall)){
								options.clear(getOptionIndex(x, y, d));
							}
						}
					}
				}
			}
			synchronized(lock){
				count++;
			}
			if(options.cardinality() <= 1){
				break;
			}
			move(wall, getOrientation(pos[THETA]));
		}
		option = new int[3];
		getOption(option);
		pos = new double[3];
		nav.getPosition(pos);
		getCorrectedOffset(pos, option[THETA]);
		double [] start = new double [3];
		convertTilePosition(option, start);
		nav.setPosition(addPositions(pos, start));
	}
	
	/**
	 * 
	 * @return number of checks occured
	 */
	public int getCount(){
		int result;
		synchronized(lock){
			result = this.count;
		}
		return result;
	}
	
	/**
	 * Adds 2 Odometer system co-ordinates together in a sane manner
	 * @param posOne
	 * @param posTwo
	 * @return
	 */
	public static double [] addPositions(double [] posOne, double [] posTwo){
		double [] result = new double [3];
		result[X] = posOne[X] + posTwo[X];
		result[Y] = posOne[Y] + posTwo[Y];
		result[THETA] = fixDegAngle(posOne[THETA] + posTwo[THETA]);
		return result;
	}
	
	// static 'helper' methods
		protected static double fixDegAngle(double angle) {		
			if (angle < 0.0)
				angle = 360.0 + (angle % 360.0);
			
			return angle % 360.0;
		}
		
		protected static double minimumAngleFromTo(double a, double b) {
			double d = fixDegAngle(b - a);
			
			if (d < 180.0)
				return d;
			else
				return d - 360.0;
		}
	
	/**
	 * Converts map tiles into odometer co-ords
	 * @param tile
	 * @param pos
	 */
	public static void convertTilePosition(int [] tile, double [] pos){
		pos[X] = tile[X]*TILE_SIZE - TILE_OFFSET;
		pos[Y] = tile[Y]*TILE_SIZE - TILE_OFFSET;
		pos[THETA] = 90*tile[THETA];
	}
	
	/**
	 * checks if a triplet is a valid option
	 * @param x
	 * @param y
	 * @param direction
	 * @return
	 */
	public boolean isOption(int x, int y, int direction){
		return options.get(this.getOptionIndex(x, y, direction));
	}
	
	/**
	 * removes an option from the option matrix
	 * @param x
	 * @param y
	 * @param direction
	 */
	public void clearOption(int x, int y, int direction){
		options.clear(this.getOptionIndex(x, y, direction));
	}
	
	/**
	 * Checks if the map status at a given location and heading matches the ultrasonic
	 * reading.
	 * @param x
	 * @param y
	 * @param direction
	 * @param blocked
	 * @return true, if matching, false otherwise.
	 */
	public boolean match(int x, int y, int direction, boolean blocked){
		double angle = orientationToRads(direction);
		x += (int) Math.sin(angle);
		y += (int) Math.cos(angle);
		return map.blocked(x, y)==blocked;
	}
	
	/**
	 * 
	 * @param grid
	 * @param offset
	 * @return
	 */
	public int getOffsetDist(int grid, int offset){
		return grid + offset;
	}
	
	/**
	 * 
	 * @param grid
	 * @param offset
	 * @return
	 */
	public int getOffsetDist(int grid, double offset){
		return grid + map.getGrid(offset);
	}
	
	/**
	 * 
	 * @param angle
	 * @return
	 */
	public int getOrientation(double angle){
		return (int) ( Math.round(fixDegAngle(angle) / 90.0) ) % 4;
	}
	
	/**
	 * Adds 2 directions, and returns a valid direction
	 * @param initial
	 * @param offset
	 * @return
	 */
	public int getOffsetDirection(int initial, int offset){
		return (initial + offset)%4;
	}
	
	/**
	 * Adds 2 directions, and returns a valid direction
	 * @param initial
	 * @param angle
	 * @return
	 */
	public int getOffsetDirection(int initial, double angle){
		return (initial + getOrientation(angle))%4;
	}
	
	public static double orientationToRads(int orientation){
		return orientation*Math.PI/2;
	}
	
	/**
	 * corrects an offset for a given starting orientation in the grid system
	 * @param offset
	 * @param orientation
	 * @return
	 */
	public int [] getCorrectedOffset(int [] offset, int orientation){
		int [] correctedOffset = new int[3];
		double angle = orientationToRads(orientation);
		correctedOffset[THETA] = offset[THETA];
		correctedOffset[X] = (int) (offset[X]*Math.cos(angle) + offset[Y]*Math.sin(angle));
		correctedOffset[Y] = (int) (offset[Y]*Math.cos(angle) - offset[X]*Math.sin(angle));
		return correctedOffset;	
	}
	
	/**
	 * corrects an offset for a given starting orientation in the odometer system
	 * @param offset
	 * @param orientation
	 */
	public void getCorrectedOffset(double [] offset, int orientation){
		double [] correctedOffset = new double[2];
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
	
	/**
	 * Reports if an option is potentially valid, given an offset and Ultrasonic reading
	 * @param pos
	 * @param offset
	 * @param wall
	 * @return
	 */
	public boolean validOption(int [] pos, int [] offset, boolean wall){
		return validOption(pos[X], pos[Y], pos[THETA], offset[X], offset[Y], offset[THETA], wall);
	}
	
	/**
	 * Reports if an option is potentially valid, given an offset and Ultrasonic reading
	 * @param x
	 * @param y
	 * @param direction
	 * @param xOffset
	 * @param yOffset
	 * @param dOffset
	 * @param wall
	 * @return
	 */
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