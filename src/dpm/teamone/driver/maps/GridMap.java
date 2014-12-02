package dpm.teamone.driver.maps;

import java.util.ArrayList;
import java.util.BitSet;
import lejos.nxt.LCD;
import lejos.geom.Line;
import lejos.geom.Point;
import lejos.geom.Rectangle;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.Path;
import lejos.util.Delay;

/**
 * The GridMap provides an easy way to create maps for the robot, working on a
 * grid structure, unlike the LineMap. It is used to generate LineMaps and
 * GridMeshes
 * 
 * @author Michael Williams
 * @author Mehdi Benguerrah
 */
public class GridMap {

	private final BitSet bitset;

	private LineMap linemap;

	public final int TILE_SIZE = 30;
	private final int width, height;
	private final byte pickupX, pickupY;
	private Pathfinder pickupPaths, dropPaths;

	/**
	 * Create an empty map with the provided height and width
	 * 
	 * @param width
	 *            Width of the new GridMap
	 * @param height
	 *            Height of the new GridMap
	 */
	public GridMap(int width, int height, byte x, byte y) {
		this.width = width;
		this.height = height;
		this.bitset = new BitSet(width * height);
		this.pickupX = x;
		this.pickupY = y;
	}

	public boolean blocked(int x, int y) {
		if (!(this.valid(x, y))) {
			return true;
		}
		return this.bitset.get(this.getIndex(x, y));
	}

	public Point convertToPoint(int x, int y) {
		return new Point((float) this.getPos(x), (float) this.getPos(y));
	}

	public Waypoint convertToWaypoint(int x, int y) {
		return new Waypoint(this.convertToPoint(x, y));
	}

	/**
	 * Actual Constructor of the LineMap
	 * 
	 * @return LineMap equivalent of the GridMap
	 */
	private LineMap generateLineMap() {
		Rectangle rect = new Rectangle(-this.TILE_SIZE, -this.TILE_SIZE,
				this.width * this.TILE_SIZE, this.height * this.TILE_SIZE);
		ArrayList<Line> lines = new ArrayList<Line>();
		// Add surrounding wall
		lines.add(new Line((this.width - 1) * this.TILE_SIZE, -this.TILE_SIZE,
				-this.TILE_SIZE, -this.TILE_SIZE));
		lines.add(new Line((this.width - 1) * this.TILE_SIZE, (this.height - 1)
				* this.TILE_SIZE, -this.TILE_SIZE, (this.height - 1)
				* this.TILE_SIZE));
		lines.add(new Line((this.width - 1) * this.TILE_SIZE, (this.height - 1)
				* this.TILE_SIZE, (this.width - 1) * this.TILE_SIZE,
				-this.TILE_SIZE));
		lines.add(new Line(-this.TILE_SIZE, (this.height - 1) * this.TILE_SIZE,
				-this.TILE_SIZE, -this.TILE_SIZE));
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				if (this.bitset.get(this.getIndex(x, y))) {
					lines.add(new Line((x - 1) * this.TILE_SIZE, y
							* this.TILE_SIZE, x * this.TILE_SIZE, y
							* this.TILE_SIZE));
					lines.add(new Line((x - 1) * this.TILE_SIZE, (y - 1)
							* this.TILE_SIZE, x * this.TILE_SIZE, (y - 1)
							* this.TILE_SIZE));
					lines.add(new Line((x - 1) * this.TILE_SIZE, (y - 1)
							* this.TILE_SIZE, (x - 1) * this.TILE_SIZE, y
							* this.TILE_SIZE));
					lines.add(new Line(x * this.TILE_SIZE, (y - 1)
							* this.TILE_SIZE, x * this.TILE_SIZE, y
							* this.TILE_SIZE));
				}
			}
		}
		return new LineMap(lines.toArray(new Line[lines.size()]), rect);
	}

	public int getGrid(double val) {
		return this.getGrid(val, false);
	}

	/**
	 * Converts double from the odometer system into grid id value
	 */
	public int getGrid(double val, boolean orienteering) {
		if (!orienteering) {
			val += 15;
		}
		val /= 30.0;
		return (int) Math.round(val);
	}

	/**
	 * 
	 * @return Height of the GridMap
	 */
	public int getHeight() {
		return this.height;
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
	protected int getIndex(int x, int y) {
		return (this.width * y) + x;
	}

	public LineMap getLineMap() {
		if (this.linemap == null) {
			this.linemap = this.generateLineMap();
		}
		return this.linemap;
	}

	public Path getPath(Point start, Point end) {
		Pathfinder finder = new Pathfinder(this);
		int s[] = new int[2];
		int e[] = new int[2];
		s[0] = this.getGrid(start.x);
		s[1] = this.getGrid(start.y);
		e[0] = this.getGrid(end.x);
		e[1] = this.getGrid(end.y);
		finder.findPath(s, e);
		Path path = new Path();
		while (finder.isPath()) {
			path.add(finder.getNext());
		}
		return path;
	}
	
	public Path getPathPickup(Point start) {
		int s[] = new int[2];
		int e[] = new int[2];
		s[0] = this.getGrid(start.x);
		s[1] = this.getGrid(start.y);
		e[0] = pickupX;
		e[1] = pickupY;
		pickupPaths.findPath(s, e);
		Path path = new Path();
		while (pickupPaths.isPath()) {
			path.add(pickupPaths.getNext());
		}
		return path;
	}

	public Path getPathDrop(Point start, Point end) {
		int s[] = new int[2];
		int e[] = new int[2];
		s[0] = this.getGrid(start.x);
		s[1] = this.getGrid(start.y);
		e[0] = this.getGrid(end.x);
		e[1] = this.getGrid(end.y);
		dropPaths.findPath(s, e);
		Path path = new Path();
		while (dropPaths.isPath()) {
			path.add(dropPaths.getNext());
		}
		return path;
	}
	
	/**
	 * Converts grid-id value into odometer system value
	 * 
	 * @return positional value of the center of a grid.
	 */
	public double getPos(int val) {
		return (val * 30.0) - 15;
	}

	/**
	 * 
	 * @return Width of the GridMap
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Lazy Constructor of the LineMap
	 * 
	 * @return LineMap equivalent of the GridMap
	 */
	public boolean isObstacle(int x, int y) {
		boolean obstacle = false;
		try {
			obstacle = this.bitset.get(this.getIndex(x, y));
		} catch (Exception e) {

		}
		return obstacle;
	}

	/**
	 * Sets the provided location as a wall
	 * 
	 * @param x
	 *            x-axis location
	 * @param y
	 *            y-axis location
	 */

	protected void set(int x, int y) {
		this.bitset.set(this.getIndex(x, y));
	}

	/**
	 * Reports if a co-ordinate set is a valid spot on the map
	 */
	public boolean valid(int x, int y) {
		return ((x >= 0) && (x < this.width) && (y >= 0) && (y < this.height));
	}
	
	public int getPickupX(){
		return this.pickupX;
	}
	
	public int getPickupY(){
		return this.pickupY;
	}

	public void GeneratePickupPaths(final int x, final int y){
		pickupPaths = new Pathfinder(this);
		int end[] = new int[2];
		end[0] = pickupX;
		end[1] = pickupY;
		pickupPaths.generatePaths(end, x, y);
		LCD.clear(0);
	}
	
	public void GenerateDropPaths(final int x, final int y){
		dropPaths = new Pathfinder(this);
		int end[] = new int[2];
		end[0] = x;
		end[1] = y;
		dropPaths.generatePaths(end, pickupX, pickupY);
	}
	
	/**
	 * Converts grid co-ordinates into odometer system co-ordinates
	 */
	// public double [] getPos(int [] val){
	// double [] pos = new double [2];
	// pos[DriverRobot.] = getPos(val[DriverRobot.POS_X]);
	// pos[DriverRobot.POS_Y] = getPos(val[DriverRobot.POS_Y]);
	// return pos;
	// }
}
