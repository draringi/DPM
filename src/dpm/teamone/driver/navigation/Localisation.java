package dpm.teamone.driver.navigation;

import java.util.ArrayList;

import dpm.teamone.driver.maps.GridMap;
import lejos.geom.Point;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.robotics.navigation.Pose;

/**
 * Localisation class allows for a quick robot localisation using 4 movements
 * only
 * 
 * 
 * @author Mehdi Benguerrah
 * 
 */
public class Localisation {

	private GridMap map;
	private UltraSonic sensor;
	private NavigationController navigation;
	private int currentDirection;
	private int distanceFromInitial;

	public Localisation(GridMap map,NavigationController nav) {
		this.map = map;
		sensor = new UltraSonic();
		navigation = nav;
	}

	/**
	 * Performs a quick and accurate localisation by using 4 readings (at
	 * 0,90,180,-90 degrees) and then computing what the theoritical readings
	 * would be for every possible starting point and heading. Returns the best
	 * match.
	 * 
	 * @return Initial position and heading of the robot
	 */
	public Pose performLocalisation() {
		int[] surroundings = getSurroundings();
		return localize(surroundings);

	}

	/**
	 * Reads distances at 0,90,180 and -90 degrees of the robot (relative to the
	 * robot's initial heading)
	 * 
	 * @return Values read by the sensor
	 */
	private int[] getSurroundings() {
		int[] inp = setUpArray(4);

		int turns = 0;

		for (int x = 0; x < 4; x++) {
			if (sensor.poll() < 20) {
				Sound.beep();
				inp[x] = 0;

			}
			this.navigation.rotate(-90);
		}
		int nullValueCount = getCountArray(inp);
		int nullValues = 0;
		for (int i = 0; i < 4; i++) {
			if (inp[i] == -1) {
				nullValues++;
				for (int t = 0; t < (i - turns); t++) {
					turns = i;
					this.navigation.rotate(-90);
				}
				if (nullValues == nullValueCount) {
					this.currentDirection = i;
					inp[i] = normalize(getDistance(false));
					this.distanceFromInitial = inp[i];
				} else {
					inp[i] = normalize(getDistance(true));

				}
			}

		}

		return inp;
	}

	private int getCountArray(int[] inp) {
		int count = 0;
		for (int i = 0; i < inp.length; i++) {
			if (inp[i] == -1) {
				count++;
			}
		}
		return count;
	}

	private int[] setUpArray(int size) {
		int[] temp = new int[size];
		for (int i = 0; i < size; i++) {

			temp[i] = -1;
		}
		return temp;
	}

	/**
	 * Helper method that finds best match
	 * 
	 * @param inp
	 *            Distances at 0,90,180 and -90 degrees of the robot (relative
	 *            to the robot's initial heading)
	 * 
	 * @return Robot's initial position and heading
	 */
	public int getDistance(boolean doReturn) {

		return driveUntilWall(doReturn);

	}

	public int driveUntilWall(boolean doReturn) {
		this.navigation.setPose(new Pose(0, 0, 0));
		Point curr = new Point(0, 0);
		int threshold = 20;
		int sensor_distance = sensor.poll();
		int distance = 0;
		boolean is0 = true;
		while (sensor_distance > threshold) {
			this.navigation.forward();
			is0 = false;
			sensor_distance = sensor.poll();
		}
		this.navigation.stop();
		if (is0) {
			return 0;
		}
		distance = (int) this.navigation.getPose().distanceTo(curr);
		Sound.twoBeeps();
		LCD.clear();
		LCD.drawString("Distance :" + normalize(distance + sensor_distance), 0,
				4);

		if (doReturn) {
			navigation.getPilot().travel(-distance);
		}
		return distance + sensor_distance;

	}

	public Pose localize(int[] inp) {
		Pose initialLocation = null;
		Pose currentLocation = null;
		ArrayList<Pose> matches = new ArrayList<Pose>();
		for (int x = 0; x < this.map.getWidth(); x++) {
			for (int y = 0; y < this.map.getHeight(); y++) {
				for (int ori = 0; ori < 4; ori++) {
					if (!this.map.isObstacle(x, y)) {
						int[] surr = computeSur(x, y, ori);
						if (isMatch(surr, inp)) {
							Sound.twoBeeps();
							initialLocation = new Pose((float) map.getPos(x),(float) map.getPos(y), getAngle(ori));
							currentLocation = getCurrentLocation(initialLocation);
							//matches.add(initialLocation);
							//return initialLocation;
							return getCurrentLocation(initialLocation);
							//return new Pose((float) map.getPos((int)initialLocation.getX()),(float) map.getPos((int)initialLocation.getY()), getAngle((int)initialLocation.getHeading()));
						}
					}
				}
			}
		}
		if(matches.size()==1){
			
			Pose ret = matches.get(0);
			ret= new Pose((float) map.getPos((int)ret.getX()),(float) map.getPos((int)ret.getY()), getAngle((int)ret.getHeading()));
			currentLocation = getCurrentLocation(initialLocation);
			currentLocation =  new Pose((float) map.getPos((int)currentLocation.getX()),(float) map.getPos((int)currentLocation.getY()), getAngle((int)currentLocation.getHeading()));
			this.navigation.setPose(currentLocation);
			return ret;
		}
		else{
			int[] surr = this.getSurroundings();
		for (int i = 0; i < matches.size(); i++) {
			initialLocation = matches.get(i);
			currentLocation = getCurrentLocation(initialLocation);
			if(isMatch(surr,computeSur((int)currentLocation.getX(), (int)currentLocation.getY(), (int) currentLocation.getHeading()))){
				
				initialLocation = new Pose((float) map.getPos((int)initialLocation.getX()),(float) map.getPos((int)initialLocation.getY()), getAngle((int)initialLocation.getHeading()));
				currentLocation =  new Pose((float) map.getPos((int)currentLocation.getX()),(float) map.getPos((int)currentLocation.getY()), getAngle((int)currentLocation.getHeading()));
				this.navigation.setPose(currentLocation);
				return initialLocation;
			}
		}
		}
		return null;
	}
	private Pose getCurrentLocation(Pose initial){
		Pose current;
		int heading = (int) initial.getHeading();
		heading = (heading+this.currentDirection)%4;
		int x =(int) initial.getX();
		int y =(int)initial.getY();
		if(heading==0){
			y+= (this.distanceFromInitial/30);
			
		}
		else if(heading==1){
			x+=(this.distanceFromInitial/30);
			
		}
else if(heading==2){
	y-= (this.distanceFromInitial/30);	
			
		}
else if(heading==3){
	x-= (this.distanceFromInitial/30);
	
}
		current = new Pose(x,y,heading);
		return current;
	}

	/**
	 * Converts angle system used to actual degrees angle
	 * 
	 * @param i
	 *            0= NORTH 1=EAST 2=SOUTH 3=WEST
	 * 
	 * @return Angle in degrees
	 */
	private float getAngle(int i) {

		int angle = 0;

		switch (i) {
		case 0:
			angle = 90;
			break;
		case 1:
			angle = 0;
			break;
		case 2:
			angle = -90;
			break;
		case 3:
			angle = 180;
			break;
		}
		return angle;

	}

	/**
	 * Helper method that finds best match
	 * 
	 * @param A1
	 *            Array containing distances read by robot
	 * @param A2
	 *            Array containing theoritical values for a specified Pose
	 * 
	 * @return True if the arrays contain the same distances in the same order
	 */
	private boolean isMatch(int[] A1, int[] A2) {
		boolean temp = true;
		for (int x = 0; x < A1.length; x++) {
			if (A1[x] != A2[x]) {
				temp = false;
			}
		}
		return temp;
	}

	/**
	 * Processing method. Computes the theoritical distances for a specified
	 * Pose
	 * 
	 * @param x
	 *            x-Coordinate in the grid
	 * @param y
	 *            y-Coordinate in the grid
	 * @param ori
	 *            Orientation (heading)
	 * 
	 * @return Array of theoritical distances
	 */
	public int[] computeSur(int x, int y, int ori) {
		// 0 = North 1=East 2=South 3=West
		int[] surr = new int[4];
		int temp = ori;
		for (int i = 0; i < 4; i++) {
			if (temp == 0) {
				if (y == this.map.getHeight()) {
					surr[i] = 0;
				} else {
					int incr = 0;
					int distance = 0;
					while (!this.map.isObstacle(x, (y + incr + 1))
							&& (y + incr + 1) != this.map.getHeight()) {
						distance += this.map.TILE_SIZE;
						incr++;
					}
					surr[i] = distance;
				}

			} else if (temp == 1) {
				if (x == this.map.getWidth()) {
					surr[i] = 0;
				} else {
					int incr = 0;
					int distance = 0;
					while (!this.map.isObstacle(x + (incr + 1), (y))
							&& (x + incr + 1) != this.map.getWidth()) {
						distance += this.map.TILE_SIZE;
						incr++;
					}
					surr[i] = distance;
				}

			} else if (temp == 2) {
				if (y == 0) {
					surr[i] = 0;
				} else {
					int incr = 0;
					int distance = 0;
					while ((!this.map.isObstacle(x, (y - incr - 1)))
							&& (y - incr) != 0) {
						distance += this.map.TILE_SIZE;
						incr++;
					}
					surr[i] = distance;
				}
			} else if (temp == 3) {
				if (x == 0) {
					surr[i] = 0;
				} else {
					int incr = 0;
					int distance = 0;
					while ((!this.map.isObstacle(x - incr - 1, (y)))
							&& (x - incr) != 0) { // Until obstacle or wall is
													// reached
						distance += this.map.TILE_SIZE;
						incr++;
					}
					surr[i] = distance;
				}
			}
			temp = incrementOrientation(temp);

		}
		// System.out.println("For (" + x + "," + y + "," + ori + ") Result:" +
		// arrayToString(surr)); // For testing
		return surr;
	}

	private String arrayToString(int[] x) {
		String ret = "";
		for (int y = 0; y < x.length; y++) {
			ret += x[y] + " ";
		}
		return ret;
	}

	/**
	 * Finds next angle after a 90degrees incrementation
	 * 
	 * @param ori
	 *            Orientation
	 * 
	 * @return Next angle
	 */
	private int incrementOrientation(int ori) {
		int temp = ori;
		if (temp < 3) {
			temp++;
		} else {

			temp = 0;
		}
		return temp;
	}

	/**
	 * Normalizes sensor's reading to make it match the theoritical readings
	 * (i.e if robot reads a value between 25-35)
	 * 
	 * @param inp
	 *            Distance read by the ultrasonic sensor in cm
	 * 
	 * @return Array of theoritical distances
	 */
	private int normalize(int inp) {
		return map.getGrid(inp, true) * map.TILE_SIZE;
	}

}
