package dpm.teamone.driver.navigation;

import dpm.teamone.driver.maps.GridMap;
import lejos.robotics.navigation.Pose;
/**
 * Localisation class allows for a quick robot localisation using 4 movements only
 * 
 * 
 * @author Mehdi Benguerrah
 *
 */
public class Localisation {

    private GridMap map;
    private UltraSonic sensor;
    private NavigationController navigation;

    public Localisation(GridMap map) {
        this.map = map;
        //sensor = new UltraSonic();
        //navigation = new NavigationController(map);
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
        int[] inp = new int[4];
        for (int x = 0; x < 4; x++) {
            inp[x] = normalize(this.sensor.poll());
            this.navigation.turnTo(-90);
        }
        return inp;
    }

    /**
     * Helper method that finds best match
     *
     * @param inp Distances at 0,90,180 and -90 degrees of the robot (relative
     * to the robot's initial heading)
     *
     * @return Robot's initial position and heading
     */
    public Pose localize(int[] inp) {
        Pose initialLocation = null;
        for (int x = 0; x < this.map.getWidth(); x++) {
            for (int y = 0; y < this.map.getHeight(); y++) {
                for (int ori = 0; ori < 4; ori++) {
                    if (!this.map.isObstacle(x, y)) {
                        int[] surr = computeSur(x, y, ori);
                        if (isMatch(surr, inp)) {
                            initialLocation = new Pose((x * this.map.TILE_SIZE + 15), (y * this.map.TILE_SIZE + 15), getAngle(ori));
                            return initialLocation;
                        }
                    }
                }
            }
        }
        return initialLocation;
    }

    /**
     * Converts angle system used to actual degrees angle
     *
     * @param i 0= NORTH 1=EAST 2=SOUTH 3=WEST
     *
     * @return Angle in degrees
     */
    private int getAngle(int i) {

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
     * @param A1 Array containing distances read by robot
     * @param A2 Array containing theoritical values for a specified Pose
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
     * @param x x-Coordinate in the grid
     * @param y y-Coordinate in the grid
     * @param ori Orientation (heading)
     *
     * @return Array of theoritical distances
     */
    public int[] computeSur(int x, int y, int ori) {
        // 0 = North    1=East      2=South     3=West
        int[] surr = new int[4];
        int temp = ori;
        for (int i = 0; i < 4; i++) {
            if (temp == 0) {
                if (y == this.map.getHeight()) {
                    surr[i] = 0;
                } else {
                    int incr = 0;
                    int distance = 0;
                    while (!this.map.isObstacle(x, (y + incr + 1)) && (y + incr + 1) != this.map.getHeight()) {
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
                    while (!this.map.isObstacle(x + (incr + 1), (y)) && (x + incr + 1) != this.map.getWidth()) {
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
                    while ((!this.map.isObstacle(x, (y - incr - 1))) && (y - incr) != 0) {
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
                    while ((!this.map.isObstacle(x - incr - 1, (y))) && (x - incr) != 0) { // Until obstacle or wall is reached
                        distance += this.map.TILE_SIZE;
                        incr++;
                    }
                    surr[i] = distance;
                }
            }
            temp = incrementOrientation(temp);

        }
        System.out.println("For (" + x + "," + y + "," + ori + ") Result:" + arrayToString(surr)); // For testing
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
     * @param ori Orientation
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
     * @param inp Distance read by the ultrasonic sensor in cm
     *
     * @return Array of theoritical distances
     */
    private int normalize(int inp) {

        return (((inp + (int) this.navigation.TRACK_WIDTH) / this.map.TILE_SIZE) * this.map.TILE_SIZE);
    }
}
