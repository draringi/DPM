package dpm.teamone.driver.maps;

import java.util.ArrayList;
import java.util.BitSet;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.geom.Rectangle;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.Path;

/**
 * The GridMap provides an easy way to create maps for the robot, working on a
 * grid structure, unlike the LineMap. It is used to generate LineMaps and
 * GridMeshes
 * 
 * @author Michael Williams
 * @author Mehdi Benguerrah
 */
public class GridMap {

	/**
	 * Internal representation of the 
	 */
	private final BitSet bitset;

	/**
	 * Linemap needed by some Lejos Libraries. Provided in case any need are used. 
	 */
	private LineMap linemap;

	/**
	 * Size of each tile.
	 * Value: {@value}
	 */
	public final int TILE_SIZE = 30;
	
	/**
	 * Dimensions of the map.
	 */
	private final int width, height;
	
	/**
	 * Co-ordinates of the pickup zone.
	 */
	private final byte pickupX, pickupY;
	
	/**
	 * Pathfinding table generated after map creation for common destinations.
	 */
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

	/**
	 * 
	 * @param x x-axis location
	 * @param y y-axis location
	 * @return If location is blocked.
	 */
	public boolean blocked(int x, int y) {
		if (!(this.valid(x, y))) {
			return true;
		}
		return this.bitset.get(this.getIndex(x, y));
	}

	/**
	 * Converts grid co-ordinates to a Point which can be used by navigation
	 * @param x x-axis location
	 * @param y y-axis location
	 * @return Point on the floor as measured in cm
	 * @see dpm.teamone.driver.navigation
	 */
	public Point convertToPoint(int x, int y) {
		return new Point((float) this.getPos(x), (float) this.getPos(y));
	}

	/**
	 * Converts grid co-ordinates to a Waypoint which can be used by navigation
	 * @param x x-axis location
	 * @param y y-axis location
	 * @return Waypoint on the floor as measured in cm
	 * @see dpm.teamone.driver.navigation
	 */
	public Waypoint convertToWaypoint(int x, int y) {
		return new Waypoint(this.convertToPoint(x, y));
	}

	/**
	 * Generates pathfinding table to the requested drop zone
	 * @param x Drop Zone x location
	 * @param y Drop Zone y location
	 */
	public void GenerateDropPaths(final int x, final int y) {
		this.dropPaths = new Pathfinder(this);
		int end[] = new int[2];
		end[0] = x;
		end[1] = y;
		this.dropPaths.generatePaths(end, this.pickupX, this.pickupY);
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

	/**
	 * Generates pathfinding table to the pickup zone
	 * @param x Unused
	 * @param y Unused
	 */
	public void GeneratePickupPaths(final int x, final int y) {
		this.pickupPaths = new Pathfinder(this);
		int end[] = new int[2];
		end[0] = this.pickupX;
		end[1] = this.pickupY;
		this.pickupPaths.generatePaths(end, x, y);
	}

	/**
	 * Converts cm value to grid id.
	 * @param val Location from 0 line in cm
	 * @return Grid ID.
	 */
	public int getGrid(double val) {
		return this.getGrid(val, false);
	}

	/**
	 * Converts double from the odometer system into grid id value.
	 * @param val Location from 0 line in cm
	 * @param orienteering True if offset should not be used. False for standard operation.
	 * @return Grid ID.
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

	/**
	 * Generates pathtable and finds shortest path.
	 * @param start Starting point.
	 * @param end Destination point.
	 * @return Shortest Path.
	 */
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

	/**
	 * Gets the Shortest path to the drop zone using pre-generated pathfinding table.
	 * @param start
	 * @param end Unused
	 * @return Shortest path to the drop zone
	 */
	public Path getPathDrop(Point start, Point end) {
		int s[] = new int[2];
		int e[] = new int[2];
		s[0] = this.getGrid(start.x);
		s[1] = this.getGrid(start.y);
		e[0] = this.getGrid(end.x);
		e[1] = this.getGrid(end.y);
		this.dropPaths.findPath(s, e);
		Path path = new Path();
		while (this.dropPaths.isPath()) {
			path.add(this.dropPaths.getNext());
		}
		return path;
	}

	/**
	 * Gets the Shortest path to the pickup zone using pre-generated pathfinding table.
	 * @param start
	 * @param end Unused
	 * @return Shortest path to the pickup zone
	 */
	public Path getPathPickup(Point start) {
		int s[] = new int[2];
		int e[] = new int[2];
		s[0] = this.getGrid(start.x);
		s[1] = this.getGrid(start.y);
		e[0] = this.pickupX;
		e[1] = this.pickupY;
		this.pickupPaths.findPath(s, e);
		Path path = new Path();
		while (this.pickupPaths.isPath()) {
			path.add(this.pickupPaths.getNext());
		}
		return path;
	}

	/**
	 * @return X location of the pickup zone.
	 */
	public int getPickupX() {
		return this.pickupX;
	}

	/**
	 * @return Y location of the pickup zone.
	 */
	public int getPickupY() {
		return this.pickupY;
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
}
