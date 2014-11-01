// Test file for the navigation using a map without obstacles
// Since localisation has not yet been completed the robot's
// initial location is considered to be (0,0) facing north.


package dpm.teamone.driver.navigation;

import dpm.teamone.driver.maps.GridMap;
import lejos.robotics.navigation.Pose;


public class Test {
    
    public static void main(String[] args){
    
       GridMap grid = new GridMap(4,4);
       NavigationController nav= new NavigationController(grid);  
       Pose initialLocation = new Pose(0,0,90); //Replace by localisation after
       nav.setPose(initialLocation);
       nav.driveToGrid(50, 50);
      
    }
    
}
