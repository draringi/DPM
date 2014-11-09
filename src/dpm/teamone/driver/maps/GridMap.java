package dpm.teamone.driver.maps;

import java.util.ArrayList;
import java.util.BitSet;

import lejos.geom.Line;
import lejos.geom.Rectangle;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.pathfinding.FourWayGridMesh;
import dpm.teamone.driver.DriverRobot;

/**
 * The GridMap provides an easy way to create maps for the robot, working on a
 * grid structure, unlike the LineMap. It is used to generate LineMaps and
 * GridMeshes
 * 
 * @author Michael Williams
 * @author Mehdi Benguerrah
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
		this.bitset = new BitSet(width * height);
	}

	public boolean blocked(int x, int y) {
		if (!(this.valid(x, y))) {
			return true;
		}
		return this.bitset.get(this.getIndex(x, y));
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
	 * Actual Constructor of the LineMap
	 * 
	 * @return LineMap equivalent of the GridMap
	 */
	private LineMap generateLineMap() {
		Rectangle rect = new Rectangle(-TILE_SIZE, -TILE_SIZE, this.width
				* TILE_SIZE, this.height * TILE_SIZE);
		ArrayList<Line> lines = new ArrayList<Line>();
		// Add surrounding wall
		lines.add(new Line((this.width - 1) * TILE_SIZE, -TILE_SIZE,
				-TILE_SIZE, -TILE_SIZE));
		lines.add(new Line((this.width - 1) * TILE_SIZE, (this.height - 1)
				* TILE_SIZE, -TILE_SIZE, (this.height - 1) * TILE_SIZE));
		lines.add(new Line((this.width - 1) * TILE_SIZE, (this.height - 1)
				* TILE_SIZE, (this.width - 1) * TILE_SIZE, -TILE_SIZE));
		lines.add(new Line(-TILE_SIZE, (this.height - 1) * TILE_SIZE,
				-TILE_SIZE, -TILE_SIZE));
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				if (this.bitset.get(this.getIndex(x, y))) {
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
		return this.getGrid(val, false);
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
	public FourWayGridMesh getGridMesh() {
		if (this.mesh == null) {
			this.mesh = this.generateGridMesh();
		}
		return this.mesh;
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
	private int getIndex(int x, int y) {
		return (this.width * y) + x;
	}

	/**
	 * Lazy Constructor of the LineMap
	 * 
	 * @return LineMap equivalent of the GridMap
	 */
	public LineMap getLineMap() {
		if (this.linemap == null) {
			this.linemap = this.generateLineMap();
		}
		return this.linemap;
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
	 * Converts grid co-ordinates into odometer system co-ordinates
	 */
	public double[] getPos(int[] val) {
		double[] pos = new double[2];
		pos[DriverRobot.POS_X] = this.getPos(val[DriverRobot.POS_X]);
		pos[DriverRobot.POS_Y] = this.getPos(val[DriverRobot.POS_Y]);
		return pos;
	}

	/**
	 * 
	 * @return Width of the GridMap
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * 
	 * @return True if coordinates correspond to an obstacle
	 */
	public boolean isObstacle(int x, int y) {
		return this.bitset.get(this.getIndex(x, y));

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
