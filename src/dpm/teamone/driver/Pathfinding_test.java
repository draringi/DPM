package dpm.teamone.driver;

import lejos.geom.Point;
import lejos.nxt.comm.RConsole;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.Path;
import dpm.teamone.driver.maps.GridMap;
import dpm.teamone.driver.maps.MapFactory;
import dpm.teamone.driver.maps.Pathfinder;

public class Pathfinding_test {

	public static void main(String[] args) {
		RConsole.openUSB(0);
		GridMap map = MapFactory.getBetaMap(1);
		String str = "w: " + map.getWidth() + "\t h: " + map.getHeight(); 
		RConsole.println(str);
		Point start = new Point(195,15);
		Point end = new Point(45, 45);
		str = "start: (" + map.getGrid(start.x) +", " +map.getGrid(start.y)+")\tend: (" + map.getGrid(end.x) +", " +map.getGrid(end.y)+")";
		RConsole.println(str);
		Path p = map.getPath(start, end);
		for(int i = 0; i < p.size(); i++){
			Waypoint wp = p.get(i);
			RConsole.println(wp.toString());
		}
		Pathfinder pfinder = new Pathfinder(map);
		int s[] = new int[2];
		int e[] = new int[2];
		s[0] = 7;
		s[1] = 1;
		e[0] = 2;
		e[1] = 2;
		pfinder.findPath(s, e);
		while(pfinder.isPath()){
			Waypoint wp = pfinder.getNext();
			RConsole.println(wp.toString());
		}
	}

}
