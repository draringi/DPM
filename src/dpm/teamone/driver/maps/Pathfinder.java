package dpm.teamone.driver.maps;

import java.util.Queue;

import lejos.robotics.navigation.Waypoint;

/**
 * Pathfinding class, taking advantage of knowing the map to avoid having to
 * read from the ultrasonic to dodge walls.
 * 
 * @author Michael Williams (260369438)
 * @author Leonardo Siracusa (260585931)
 * 
 */
public class Pathfinder {
	private static final int X = 0, Y = 1;
	private final int[][] cellList;
	private final GridMap map;
	private final Queue<Waypoint> path;
	private final int width, height;

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

	public void findPath(int[] start, int[] end) {
		this.cellList[end[X]][end[Y]] = 0;
		while (this.cellList[start[X]][start[Y]] == -1) {
			for (int x = 0; x < this.width; x++) {
				for (int y = 0; y < this.height; y++) {
					if (this.cellList[x][y] != -1) {
						int dist = this.cellList[x][y];
						if (!this.map.blocked(x, y + 1)
								&& (this.cellList[x][y + 1] == -1)) {
							this.cellList[x][y + 1] = dist + 1;
						}
						if (!this.map.blocked(x, y - 1)
								&& (this.cellList[x][y - 1] == -1)) {
							this.cellList[x][y - 1] = dist + 1;
						}
						if (!this.map.blocked(x + 1, y)
								&& (this.cellList[x + 1][y] == -1)) {
							this.cellList[x + 1][y] = dist + 1;
						}
						if (!this.map.blocked(x - 1, y)
								&& (this.cellList[x - 1][y] == -1)) {
							this.cellList[x - 1][y] = dist + 1;
						}
					}
				}
			}
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

	public Waypoint getNext() {
		return (Waypoint) this.path.pop();
	}

	public boolean isPath() {
		return !this.path.isEmpty();
	}

	private boolean nextStep(int x, int y, int dist) {
		return this.map.valid(x, y) && (this.cellList[x][y] == (dist - 1));
	}
}
