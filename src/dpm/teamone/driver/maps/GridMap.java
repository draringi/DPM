package dpm.teamone.driver.maps;

import java.util.ArrayList;
import java.util.BitSet;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.geom.Rectangle;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.NavigationMesh;
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

	private static final float CLEARANCE = 1;

	private final BitSet bitset;

	private LineMap linemap;
	private NavigationMesh mesh;

	public final int TILE_SIZE = 30;
	private final int width, height;

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
		this.bitset = new BitSet(width * height);
	}

	public boolean blocked(int x, int y) {
		if (!(valid(x, y))) {
			return true;
		}
		return bitset.get(getIndex(x, y));
	}

	public Point convertToPoint(int x, int y) {
		return new Point((float) this.getPos(x), (float) this.getPos(y));
	}

	public Waypoint convertToWaypoint(int x, int y) {
		return new Waypoint(this.convertToPoint(x, y));
	}

	/**
	 * Actual Constructor of the GridMesh
	 * 
	 * @return GridMesh representing the GridMap
	 */
	private NavigationMesh generateGridMesh() {
		// return new FourWayGridMesh(this.getLineMap(), TILE_SIZE, CLEARANCE);
		return new GridMesh(this);
	}

	/**
	 * Actual Constructor of the LineMap
	 * 
	 * @return LineMap equivalent of the GridMap
	 */
	private LineMap generateLineMap() {
		Rectangle rect = new Rectangle(-TILE_SIZE, -TILE_SIZE, width
				* TILE_SIZE, height * TILE_SIZE);
		ArrayList<Line> lines = new ArrayList<Line>();
		// Add surrounding wall
		lines.add(new Line((width - 1) * TILE_SIZE, -TILE_SIZE, -TILE_SIZE,
				-TILE_SIZE));
		lines.add(new Line((width - 1) * TILE_SIZE, (height - 1) * TILE_SIZE,
				-TILE_SIZE, (height - 1) * TILE_SIZE));
		lines.add(new Line((width - 1) * TILE_SIZE, (height - 1) * TILE_SIZE,
				(width - 1) * TILE_SIZE, -TILE_SIZE));
		lines.add(new Line(-TILE_SIZE, (height - 1) * TILE_SIZE, -TILE_SIZE,
				-TILE_SIZE));
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (bitset.get(getIndex(x, y))) {
					lines.add(new Line((x - 1) * TILE_SIZE, y * TILE_SIZE, x
							* TILE_SIZE, y * TILE_SIZE));
					lines.add(new Line((x - 1) * TILE_SIZE,
							(y - 1) * TILE_SIZE, x * TILE_SIZE, (y - 1)
									* TILE_SIZE));
					lines.add(new Line((x - 1) * TILE_SIZE,
							(y - 1) * TILE_SIZE, (x - 1) * TILE_SIZE, y
									* TILE_SIZE));
					lines.add(new Line(x * TILE_SIZE, (y - 1) * TILE_SIZE, x
							* TILE_SIZE, y * TILE_SIZE));
				}
			}
		}
		return new LineMap(lines.toArray(new Line[lines.size()]), rect);
	}

	public int getGrid(double val) {
		return getGrid(val, false);
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

	/**
	 * Lazy Constructor of the GridMesh
	 * 
	 * @return GridMesh representing the GridMap
	 */
	public NavigationMesh getGridMesh() {
		if (mesh == null) {
			mesh = generateGridMesh();
		}
		return mesh;
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
		return (width * y) + x;
	}

	public LineMap getLineMap() {
		if (linemap == null) {
			linemap = generateLineMap();
		}
		return linemap;
	}

	public Path getPath(Point start, Point end) {
		Pathfinder finder = new Pathfinder(this);
		int s[] = new int[2];
		int e[] = new int[2];
		s[0] = getGrid(start.x);
		s[1] = getGrid(start.y);
		e[0] = getGrid(end.x);
		e[1] = getGrid(end.y);
		finder.findPath(s, e);
		Path path = new Path();
		while (finder.isPath()) {
			path.add(finder.getNext());
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
			obstacle = bitset.get(getIndex(x, y));
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
		this.bitset.set(getIndex(x, y));
	}

	/**
	 * Reports if a co-ordinate set is a valid spot on the map
	 */
	public boolean valid(int x, int y) {
		return ((x >= 0) && (x < width) && (y >= 0) && (y < height));
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
