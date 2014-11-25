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
		this.cellList = new int[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				this.cellList[x][y] = -1;
			}
		}
	}

	public void findPath(int[] start, int[] end) {
		cellList[end[X]][end[Y]] = 0;
		while (cellList[start[X]][start[Y]] == -1) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					if (cellList[x][y] != -1) {
						int dist = cellList[x][y];
						if (!map.blocked(x, y + 1)
								&& (cellList[x][y + 1] == -1)) {
							cellList[x][y + 1] = dist + 1;
						}
						if (!map.blocked(x, y - 1)
								&& (cellList[x][y - 1] == -1)) {
							cellList[x][y - 1] = dist + 1;
						}
						if (!map.blocked(x + 1, y)
								&& (cellList[x + 1][y] == -1)) {
							cellList[x + 1][y] = dist + 1;
						}
						if (!map.blocked(x - 1, y)
								&& (cellList[x - 1][y] == -1)) {
							cellList[x - 1][y] = dist + 1;
						}
					}
				}
			}
		}
		int x = start[X];
		int y = start[Y];
		while ((x != end[X]) || (y != end[Y])) {
			int dist = cellList[x][y];
			if (nextStep(x, y + 1, dist)) {
				path.addElement(map.convertToWaypoint(x, y + 1));
				y++;
				continue;
			}
			if (nextStep(x, y - 1, dist)) {
				path.addElement(map.convertToWaypoint(x, y - 1));
				y--;
				continue;
			}
			if (nextStep(x + 1, y, dist)) {
				path.addElement(map.convertToWaypoint(x + 1, y));
				x++;
				continue;
			}
			if (nextStep(x - 1, y, dist)) {
				path.addElement(map.convertToWaypoint(x - 1, y));
				x--;
				continue;
			}
		}

	}

	public Waypoint getNext() {
		return (Waypoint) path.pop();
	}

	public boolean isPath() {
		return !path.isEmpty();
	}

	private boolean nextStep(int x, int y, int dist) {
		return map.valid(x, y) && (cellList[x][y] == (dist - 1));
	}
}
