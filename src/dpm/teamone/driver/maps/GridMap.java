package dpm.teamone.driver.maps;

import java.util.ArrayList;
import java.util.BitSet;

import dpm.teamone.driver.DriverRobot;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.pathfinding.FourWayGridMesh;
import lejos.geom.Rectangle;
import lejos.geom.Line;

/**
 * The GridMap provides an easy way to create maps for the robot, working on a
 * grid structure, unlike the LineMap. It is used to generate LineMaps and
 * GridMeshes
 * 
 * @author Michael Williams
 *
 */
public class GridMap {

	private BitSet bitset;

	private int width, height;

	private LineMap linemap;
	private FourWayGridMesh mesh;

	private static final float TILE_SIZE = 30;
	private static final float CLEARANCE = 1;

	/**
	 * Create an empty map with the provided height and width
	 * 
	 * @param width
	 *            Width of the new GridMap
	 * @param height
	 *            Height of the new GridMap
	 */
	public GridMap(int width, int height) {
		this.width = width;
		this.height = height;
		this.bitset = new BitSet(width*height);
	}

	/**
	 * Returns the internal index in the BitSet for a given position
	 * 
	 * @param x
	 *            x-axis location
	 * @param y
	 *            y-axis location
	 * @return Internal Index
	 */
	private int getIndex(int x, int y) {
		return width * y + x;
	}

	/**
	 * Sets the provided location as a wall
	 * 
	 * @param x
	 *            x-axis location
	 * @param y
	 *            y-axis location
	 */

	protected void set(int x, int y){
		this.bitset.set(getIndex(x, y));
	}

	/**
	 * 
	 * @return Height of the GridMap
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * 
	 * @return Width of the GridMap
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Lazy Constructor of the GridMesh
	 * 
	 * @return GridMesh representing the GridMap
	 */
	public FourWayGridMesh getGridMesh() {
		if (mesh == null) {
			mesh = generateGridMesh();
		}
		return mesh;
	}

	/**
	 * Actual Constructor of the GridMesh
	 * 
	 * @return GridMesh representing the GridMap
	 */
	private FourWayGridMesh generateGridMesh() {
		return new FourWayGridMesh(this.getLineMap(), TILE_SIZE, CLEARANCE);
	}

	/**
	 * Lazy Constructor of the LineMap
	 * 
	 * @return LineMap equivalent of the GridMap
	 */
	public LineMap getLineMap() {
		if (linemap == null) {
			linemap = generateLineMap();
		}
		return linemap;
	}

	/**
	 * Actual Constructor of the LineMap
	 * 
	 * @return LineMap equivalent of the GridMap
	 */
	private LineMap generateLineMap() {
		Rectangle rect = new Rectangle(-TILE_SIZE, -TILE_SIZE, width*TILE_SIZE, height*TILE_SIZE);
		ArrayList<Line> lines = new ArrayList<Line>();
		for(int x=0; x < width; x++){
			for(int y=0; y < height; y++){
				if(bitset.get(getIndex(x, y))){
					lines.add(new Line((x-1)*TILE_SIZE, x*TILE_SIZE, y*TILE_SIZE, y*TILE_SIZE));
					lines.add(new Line((x-1)*TILE_SIZE, x*TILE_SIZE, (y-1)*TILE_SIZE, (y-1)*TILE_SIZE));
					lines.add(new Line((x-1)*TILE_SIZE, (x-1)*TILE_SIZE, (y-1)*TILE_SIZE, y*TILE_SIZE));
					lines.add(new Line(x*TILE_SIZE, x*TILE_SIZE, (y-1)*TILE_SIZE, y*TILE_SIZE));
				}
			}
		}
		return new LineMap(lines.toArray(new Line[lines.size()]), rect);
	}

	/**
	 * Converts grid-id value into odometer system value
	 * 
	 * @return positional value of the center of a grid.
	 */
	public double getPos(int val) {
		return (double) val * 30.0 - 15;
	}

	/**
	 * Converts double from the odometer system into grid id value
	 */
	public int getGrid(double val, boolean orienteering) {
		val /= 30;
		if (!orienteering) {
			val = Math.ceil(val);
		}
		return (int) Math.round(val);
	}

	public int getGrid(double val) {
		return getGrid(val, false);
	}
	
	public boolean blocked(int x, int y){
		if(!(valid(x, y))){
			return true;
		}
		return bitset.get(getIndex(x, y));
	}

	/**
	 * Reports if a co-ordinate set is a valid spot on the map
	 */
	public boolean valid(int x, int y){
		return (x >= 0 && x < width && y >= 0 && y < height);
	}
	
	/**
	 * Converts grid co-ordinates into odometer system co-ordinates
	 */
	public double [] getPos(int [] val){
		double [] pos = new double [2];
		pos[DriverRobot.POS_X] = getPos(val[DriverRobot.POS_X]);
		pos[DriverRobot.POS_Y] = getPos(val[DriverRobot.POS_Y]);
		return pos;
	}
}