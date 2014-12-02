package dpm.teamone.driver.maps;

import java.util.Queue;

import lejos.robotics.navigation.Waypoint;

import lejos.nxt.Sound;
import lejos.nxt.LCD;
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
	
	class Pair{
		int x, y;
		public Pair(int x, int y){
			this.x = x;
			this.y = y;
		}
	}

	public void generatePaths(int[] end, int xTarget, int yTarget){
		Queue<Pair> studyList = new Queue<Pair>();
		studyList.push(new Pair(end[0], end[1]));
		this.cellList[end[0]][end[1]] = 0;
		while (!studyList.empty()) {
			Pair pair = (Pair) studyList.pop();
			int dist = this.cellList[pair.x][pair.y];
			if(dist == -1){
				studyList.push(pair);
				Sound.buzz();
				continue;
			}
			if (!this.map.blocked(pair.x, pair.y + 1)&& (this.cellList[pair.x][pair.y + 1] == -1)) {
				this.cellList[pair.x][pair.y + 1] = dist + 1;
				studyList.push(new Pair(pair.x, pair.y+1));
			}
			if (!this.map.blocked(pair.x, pair.y - 1)&& (this.cellList[pair.x][pair.y - 1] == -1)) {
				this.cellList[pair.x][pair.y - 1] = dist + 1;
				studyList.push(new Pair(pair.x, pair.y-1));
			}
			if (!this.map.blocked(pair.x + 1, pair.y)&& (this.cellList[pair.x + 1][pair.y] == -1)) {
				this.cellList[pair.x + 1][pair.y] = dist + 1;
				studyList.push(new Pair(pair.x+1, pair.y));
			}
			if (!this.map.blocked(pair.x - 1, pair.y)&& (this.cellList[pair.x - 1][pair.y] == -1)) {
				this.cellList[pair.x - 1][pair.y] = dist + 1;
				studyList.push(new Pair(pair.x - 1, pair.y));
			}
			Sound.beep();
		}
	}

	public void findPath(int[] start, int[] end) {
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
