package dpm.teamone.driver;

import lejos.geom.Point;
import lejos.robotics.pathfinding.Path;
import dpm.teamone.driver.maps.GridMap;
import dpm.teamone.driver.maps.MapFactory;

public class Pathfinding_test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GridMap map = MapFactory.getBetaMap(1);
		Path p = map.getPath(new Point(315,45), new Point(45, 45));
		for(int i = 0; i < p.size(); i++){
			System.out.println(p.get(i));
		}
	}

}
