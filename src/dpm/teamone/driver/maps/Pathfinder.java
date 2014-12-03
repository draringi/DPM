package dpm.teamone.driver.maps;

import java.util.Queue;

import lejos.nxt.Sound;
import lejos.robotics.navigation.Waypoint;

/**
 * Pathfinding class, taking advantage of knowing the map to avoid having to
 * read from the ultrasonic to dodge walls.
 * Based off of A*.
 * 
 * @author Michael Williams (260369438)
 * @author Leonardo Siracusa (260585931)
 * 
 */
public class Pathfinder {
	class Pair {
		int x, y;

		public Pair(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	private static final int X = 0, Y = 1;
	private final int[][] cellList;
	private final GridMap map;
	private final Queue<Waypoint> path;

	private final int width, height;

	/**
	 * 
	 * @param map Map to find the paths for.
	 */
	public Pathfinder(GridMap map) {
		this.map = map;
		this.path = new Queue<Waypoint>();
		// this.size = *;
		this.width = map.getWidth();
		this.height = map.getHeight();
		this.cellList = new int[this.width][this.height];
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				this.cellList[x][y] = -1;
			}
		}
	}

	/**
	 * finds the shortest path from start to end.
	 * If table hasn't been generated, it generates it.
	 * @param start Starting grid
	 * @param end Final grid (only used if path not already generated)
	 */
	public void findPath(int[] start, int[] end) {
		if(this.cellList[start[X]][start[Y]]==-1){
			this.generatePaths(end, start[X], start[Y]);
		}
		int x = start[X];
		int y = start[Y];
		while ((x != end[X]) || (y != end[Y])) {
			int dist = this.cellList[x][y];
			if (this.nextStep(x, y + 1, dist)) {
				this.path.addElement(this.map.convertToWaypoint(x, y + 1));
				y++;
				continue;
			}
			if (this.nextStep(x, y - 1, dist)) {
				this.path.addElement(this.map.convertToWaypoint(x, y - 1));
				y--;
				continue;
			}
			if (this.nextStep(x + 1, y, dist)) {
				this.path.addElement(this.map.convertToWaypoint(x + 1, y));
				x++;
				continue;
			}
			if (this.nextStep(x - 1, y, dist)) {
				this.path.addElement(this.map.convertToWaypoint(x - 1, y));
				x--;
				continue;
			}
		}

	}

	/**
	 * Generates Pathfinding table for a given destination.
	 * @param end Final destination.
	 * @param xTarget Unused. Remains for API conformance.
	 * @param yTarget Unused. Remains for API conformance.
	 */
	public void generatePaths(int[] end, int xTarget, int yTarget) {
		Queue<Pair> studyList = new Queue<Pair>();
		studyList.push(new Pair(end[0], end[1]));
		this.cellList[end[0]][end[1]] = 0;
		while (!studyList.empty()) {
			Pair pair = (Pair) studyList.pop();
			int dist = this.cellList[pair.x][pair.y];
			if (dist == -1) {
				studyList.push(pair);
				Sound.buzz();
				continue;
			}
			if (!this.map.blocked(pair.x, pair.y + 1)
					&& (this.cellList[pair.x][pair.y + 1] == -1)) {
				this.cellList[pair.x][pair.y + 1] = dist + 1;
				studyList.push(new Pair(pair.x, pair.y + 1));
			}
			if (!this.map.blocked(pair.x, pair.y - 1)
					&& (this.cellList[pair.x][pair.y - 1] == -1)) {
				this.cellList[pair.x][pair.y - 1] = dist + 1;
				studyList.push(new Pair(pair.x, pair.y - 1));
			}
			if (!this.map.blocked(pair.x + 1, pair.y)
					&& (this.cellList[pair.x + 1][pair.y] == -1)) {
				this.cellList[pair.x + 1][pair.y] = dist + 1;
				studyList.push(new Pair(pair.x + 1, pair.y));
			}
			if (!this.map.blocked(pair.x - 1, pair.y)
					&& (this.cellList[pair.x - 1][pair.y] == -1)) {
				this.cellList[pair.x - 1][pair.y] = dist + 1;
				studyList.push(new Pair(pair.x - 1, pair.y));
			}
			Sound.beep();
		}
	}

	/**
	 * Gets next waypoint in a path.
	 * @return next Waypoint in a path.
	 */
	public Waypoint getNext() {
		return (Waypoint) this.path.pop();
	}

	/**
	 * Gets status of path.
	 * @return True if more of a path remains. False if path had been extracted, or no path has been generated.
	 */
	public boolean isPath() {
		return !this.path.isEmpty();
	}

	/**
	 * Determines if a grid location is the next step in the path.
	 * This only makes sense if called on adjacent tiles.
	 * @param x X-axis location.
	 * @param y Y-axis location.
	 * @param dist Current Distance from destination. 
	 * @return True if next step in path, false otherwise.
	 */
	private boolean nextStep(int x, int y, int dist) {
		return this.map.valid(x, y) && (this.cellList[x][y] == (dist - 1));
	}
}
