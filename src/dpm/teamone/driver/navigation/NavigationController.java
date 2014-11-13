package dpm.teamone.driver.navigation;



import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;
import maps.GridMap;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.FourWayGridMesh;
import lejos.robotics.pathfinding.Path;
import lejos.robotics.pathfinding.ShortestPathFinder;
public class NavigationController {
        
        private static final double WHEEL_DIAMETER=4.1;
        private static final double TRACK_WIDTH=18.7;
        private static final double FORWARD_SPEED=15;
        private static final double ROTATE_SPEED=60;
        private static NXTRegulatedMotor LEFT_MOTOR = Motor.A, RIGHT_MOTOR = Motor.B;
	private DifferentialPilot pilot;

	private Navigator navigator;
        
        public int[] dropZone;

	public int[] pickupZone;

	public GridMap map;
        
        public NavigationController(GridMap map){
        this.pilot= new DifferentialPilot(WHEEL_DIAMETER,TRACK_WIDTH,LEFT_MOTOR,RIGHT_MOTOR);
        this.navigator= new Navigator(pilot);
        this.map=map;
        this.pilot.setTravelSpeed(FORWARD_SPEED);
        this.pilot.setRotateSpeed(ROTATE_SPEED);
        this.pickupZone = new int[4];
        this.dropZone = new int[4];
        }
        
	// Returns shortest path to destination 
        public Path getPath(int x, int y){
        Waypoint destination= new Waypoint(x,y);  //Destination point
        Pose currentLocation = getPose();
        LineMap map = this.map.getLineMap();
        ShortestPathFinder pathAlgo = new ShortestPathFinder(map);
        pathAlgo.lengthenLines(14);
        Path route=null;
        try{
        route = pathAlgo.findRoute(currentLocation, destination);}
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
		driveToGrid(x, y);
		turnTo(direction);
	}

	public void setDropZonet(int x, int y, int width, int height) {
		this.dropZone[0] = x;
		this.dropZone[1] = y;
		this.dropZone[2] = width;
		this.dropZone[3] = height;
	}
        
      
	public void setPickUpZone(int x, int y, int width, int height) {
		this.pickupZone[0] = x;
		this.pickupZone[1] = y;
		this.pickupZone[2] = width;
		this.pickupZone[3] = height;
	}

	public void turnTo(Direction direction) {
		turnTo(direction.toAngle());
	}
	
	public void localize(){
		
	}
	
	public void driveToPickup(){
		driveToGrid(pickupZone[0], pickupZone[1]);
	}
	
	public void driveToDrop(){
		driveToGrid(dropZone[0], dropZone[1]);
	}

	public DifferentialPilot getPilot(){
		
		return this.pilot;
	}
	
	public void findObject(){
		
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
