package dpm.teamone.driver.maps;
import java.util.Queue;

import lejos.robotics.navigation.Waypoint;


/**
 * Pathfinding class, taking advantage of knowing the map to
 * avoid having to read from the ultrasonic to dodge walls.
 * @author Michael Williams (260369438)
 * @author Leonardo Siracusa (260585931)
 *
 */
public class Pathfinder {
	private GridMap map;
	private Queue<Waypoint> path;
	private static final int X=0, Y=1;
	private int [] cellList;
	private int size;
	
	public Pathfinder(GridMap map){
		this.map = map;
		this.path = new Queue<Waypoint>();
		this.size = map.getWidth()*map.getHeight();
		this.cellList = new int [size];
		for (int i = 0; i < size; i++){
			this.cellList[i]= -1;
		}
	}
	
	void findPath(int [] start, int [] end){
		int startIndex = map.getIndex(start[X], start[Y]);
		int endIndex = map.getIndex(end[X], end[Y]); 
		cellList[endIndex] = 0;
		while(cellList[startIndex] == -1){
			for(int i = 0; i < size; i++){
				if(cellList[i] != -1){
					int dist = cellList[i]; 
					int x = i % map.getWidth();
					int y = i / map.getWidth();
					if(!map.blocked(x, y+1) && cellList[map.getIndex(x, y+1)] == -1){
						cellList[map.getIndex(x, y + 1)] = dist + 1; 
					}
					if(!map.blocked(x, y-1) && cellList[map.getIndex(x, y-1)] == -1){
						cellList[map.getIndex(x, y - 1)] = dist + 1; 
					}
					if(!map.blocked(x+1, y) && cellList[map.getIndex(x+1, y)] == -1){
						cellList[map.getIndex(x + 1, y)] = dist + 1; 
					}
					if(!map.blocked(x-1, y) && cellList[map.getIndex(x-1, y)] == -1){
						cellList[map.getIndex(x - 1, y)] = dist + 1; 
					}
				}
			}
		}
		int loc = startIndex;
		while (loc != endIndex){
			int dist = cellList[loc]; 
			int x = loc % map.getWidth();
			int y = loc / map.getWidth();
			if(nextStep(x, y+1, dist)){
				path.addElement(map.convertToWaypoint(x, y+1));
				loc = map.getIndex(x, y+1);
				continue;
			}
			if(nextStep(x, y-1, dist)){
				path.addElement(map.convertToWaypoint(x, y-1));
				loc = map.getIndex(x, y-1);
				continue; 
			}
			if(nextStep(x+1, y, dist)){
				path.addElement(map.convertToWaypoint(x+1, y));
				loc = map.getIndex(x+1, y);
				continue; 
			}
			if(nextStep(x-1, y, dist)){
				path.addElement(map.convertToWaypoint(x, y));
				loc = map.getIndex(x-1, y);
				continue; 
			}
		}
	}
	
	private boolean nextStep(int x, int y, int dist){
		return map.valid(x, y) && (cellList[map.getIndex(x, y)] == dist - 1);
	}
	
	public boolean isPath(){
		return !path.isEmpty();
	}
	
	public Waypoint getNext(){
		return (Waypoint) path.pop();
	}
}
