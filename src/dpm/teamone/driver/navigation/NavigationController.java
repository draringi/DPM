package dpm.teamone.driver.navigation;

import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;
import dpm.teamone.driver.maps.GridMap;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.FourWayGridMesh;
import lejos.robotics.pathfinding.Path;
import lejos.robotics.pathfinding.ShortestPathFinder;
public class NavigationController {
        
        private static final double WHEEL_RADIUS=2.5;
        private static final double TRACK_WIDTH=15.5;
        private static NXTRegulatedMotor LEFT_MOTOR = Motor.A, RIGHT_MOTOR = Motor.B;
	private DifferentialPilot pilot;

	private Navigator navigator;
        
        public int[] dropZone;

	public int[] pickupZone;

	public GridMap map;
        
        public NavigationController(GridMap map){
        this.pilot= new DifferentialPilot(WHEEL_RADIUS,TRACK_WIDTH,LEFT_MOTOR,RIGHT_MOTOR);
        this.navigator= new Navigator(pilot);
      
        this.map=map;
        }
        
	
        public Path getPath(int x, int y){
        Waypoint destination= new Waypoint(x,y);  //Destination point
        Pose currentLocation = getPose();
        LineMap map = this.map.getLineMap();
        ShortestPathFinder pathAlgo = new ShortestPathFinder(map);
        Path route=null;
        try{
        route = pathAlgo.findRoute(currentLocation, destination);
        }
        catch(Exception e){}
        return route;
        }
        
        public void followPath(Path route){
        
        this.navigator.followPath(route);
        }
	public void driveToGrid(int x, int y) {
            followPath(getPath(x,y));
	}

	public void driveToGrid(int x, int y, Direction direction) {
            followPath(getPath(x,y));
            pilot.rotate(TRACK_WIDTH);
	}

	public void setDropZonet(int x, int y, int width, int height) {
	}
        
      
	public void setPickUpZone(int x, int y, int width, int height) {
	}

	public void turnTo(Direction direction) {
	}

	public void turnTo(double angle) {
            this.pilot.rotate(angle);       //Rotates to specified angle
	}
        
        // Returns current location and heading
        public Pose getPose(){
        return this.navigator.getPoseProvider().getPose(); 
        }

         // Set current location and heading
        public void setPose(Pose p){
        this.navigator.getPoseProvider().setPose(p);
        }
}
