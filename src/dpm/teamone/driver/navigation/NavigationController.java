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

/**
 * The NavigationController provides a easy to use wrapper for the LeJos
 * Navigation API It handles Movement, Localization and Pathfinding
 * 
 * @author Mehdi Benguerrah
 * @author Michael Williams
 *
 */
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
        
		/**
		 * Sets up the underlying Pilot, Navigation and Localization subsystem
		 * 
		 * @param map
		 *            Map of course to be used.
		 */
        public NavigationController(GridMap map){
        this.pilot= new DifferentialPilot(WHEEL_DIAMETER,TRACK_WIDTH,LEFT_MOTOR,RIGHT_MOTOR);
        this.navigator= new Navigator(pilot);
        this.map=map;
        this.pilot.setTravelSpeed(FORWARD_SPEED);
        this.pilot.setRotateSpeed(ROTATE_SPEED);
        this.pickupZone = new int[4];
        this.dropZone = new int[4];
        }
        
        /**
         * Determines shortest path to a Location
         * 
         * @param x
         *            Location in the x-axis
         * @param y
         *            Location in the y-axis
         * @return shortest path to destination
         */
        public Path getPath(int x, int y) {
        int currentX = (int) getPose().getX() / 30; //X index 
        int currentY = (int) getPose().getY() / 30; // Y index
        ArrayList<Node> path = new ArrayList<Node>();

        calculatePaths(currentX, currentY, x, y, path, 0);
        return getShortestPath();
    }

    public Path getPath_TEST(int v, int w, int x, int y) {
        int currentX = v;//X index 
        int currentY = w; // Y index
        ArrayList<Node> path = new ArrayList<Node>();

        calculatePaths(currentX, currentY, x, y, path, 0);
        return getShortestPath();
    }

    private Path getShortestPath() {
        int shortestPath = 100;
        int index = 0;

        for (int x = 0; x < this.paths.size(); x++) {
            if (this.paths.get(x).size() < shortestPath) {
              
                shortestPath = this.paths.get(x).size();
                index = x;
            }
        }
        List<Node> shortestList = this.paths.get(index);
        Path path = new Path();
        for (int y = 0; y < shortestList.size(); y++) {
            Node node = shortestList.get(y);
            path.add(new Waypoint((node.x*this.map.TILE_SIZE+15), (node.y*this.map.TILE_SIZE+15)));
        }
        return path;
    }

    private ArrayList<Node> adjustPath(ArrayList<Node> p, int count) {
        ArrayList<Node> temp = new ArrayList<Node>();
        for (int i = 0; i < count - 1; i++) {
            temp.add(p.get(i));
           
        }
        return temp;
    }

    private void calculatePaths(int x1, int y1, int x2, int y2, ArrayList<Node> path, int count) {

        count++;
        path.add(new Node(x1, y1));

        if (count < path.size()) {
          
            path = adjustPath(path, count);
            path.add(new Node(x1, y1));
        }

        if (x1 == x2 && y1 == y2) {
 
            this.paths.add(adjustPath(path,path.size()+1));

        } else {
            if (isIndexValid(x1, y1 + 1, path)) {

                calculatePaths(x1, y1 + 1, x2, y2, path, count);

            }
            if (isIndexValid(x1, y1 - 1, path)) {

                calculatePaths(x1, y1 - 1, x2, y2, path, count);

            }
            if (isIndexValid(x1 + 1, y1, path)) {

                calculatePaths(x1 + 1, y1, x2, y2, path, count);

            }
            if (isIndexValid(x1 - 1, y1, path)) {

                calculatePaths(x1 - 1, y1, x2, y2, path, count);

            }
            path.remove(path.size() - 1);
        }

    }

    private boolean containsNode(Node n, ArrayList<Node> nodes) {

        boolean contains = false;
        for (int x = 0; x < nodes.size(); x++) {
            Node temp = nodes.get(x);
            if ((temp.x == n.x) && (temp.y == n.y)) {
                contains = true;
            }

        }
        return contains;
    }

    private boolean isIndexValid(int x, int y, ArrayList<Node> path) {
        boolean isValid = true;
        if ((x < 0) || (y < 0) || (y >= map.getHeight() || (x >= map.getHeight()))) {
            isValid = false;
        }
        if (map.isObstacle(x, y)) {
            isValid = false;
        }

        if (containsNode(new Node(x, y), path)) {
            isValid = false;
        }

        return isValid;

    }
        
        /**
         * Drives the robot along the requested route
         * 
         * @param route
         *            path to follow
         */
        public void followPath(Path route){
        this.navigator.followPath(route);
        }
        
        /**
         * Drives the robot to the requested co-ordinates
         * 
         * @param x
         *            Location in the x-axis
         * @param y
         *            Location in the y-axis
         */
	public void driveToGrid(int x, int y) {
            followPath(getPath(x,y));
	}
	
	
	/**
	 * Drives the robot to the requested co-ordinates and turns to requested
	 * heading
	 * 
	 * @param x
	 *            Location in the x-axis
	 * @param y
	 *            Location in the y-axis
	 * @param direction
	 *            Cardinal Heading
	 */
	public void driveToGrid(int x, int y, Direction direction) {
		driveToGrid(x, y);
		turnTo(direction);
	}

	/**
	 * Sets the Drop Zone
	 * 
	 * @param x
	 *            Drop zone x-axis start point
	 * @param y
	 *            Drop zone y-axis start point
	 * @param width
	 *            Drop zone x-axis length
	 * @param height
	 *            Drop zone y-axis length
	 */
	public void setDropZone(int x, int y, int width, int height) {
		this.dropZone[0] = x;
		this.dropZone[1] = y;
		this.dropZone[2] = width;
		this.dropZone[3] = height;
	}
        
      
	/**
	 * Sets the Pick-up Zone
	 * 
	 * @param x
	 *            Pick-up zone x-axis start point
	 * @param y
	 *            Pick-up zone y-axis start point
	 * @param width
	 *            Pick-up zone x-axis length
	 * @param height
	 *            Pick-up zone y-axis length
	 */
	public void setPickUpZone(int x, int y, int width, int height) {
		this.pickupZone[0] = x;
		this.pickupZone[1] = y;
		this.pickupZone[2] = width;
		this.pickupZone[3] = height;
	}

	/**
	 * Turns to requested direction
	 * 
	 * @param direction
	 *            Cardinal Heading
	 */
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
	
	/**
	 * Turns to requested direction
	 * 
	 * @param angle
	 *            Angle clockwise from East (Positive x-axis)
	 */
	public void turnTo(double angle) {
            this.pilot.rotate(angle);       //Rotates to specified angle
	}
        
	/**
	 * @return current location and heading
	 */
        public Pose getPose(){
        return this.navigator.getPoseProvider().getPose(); 
        }

        /**
         * Set current location and heading
         * 
         * @param p
         *            current location and heading
         */
        public void setPose(Pose p){
        this.navigator.getPoseProvider().setPose(p);
        }
}
