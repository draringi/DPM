import lejos.util.Timer;
import lejos.util.TimerListener;
import lejos.nxt.*;

public class Odometer implements TimerListener {
	public static final int DEFAULT_PERIOD = 25, X = 0, Y = 1, THETA = 2;
	private TwoWheeledRobot robot;
	private Timer odometerTimer;
	private Navigation nav;
	// position data
	private Object lock;
	private double x, y, theta;
	private double right_radius, left_radius, width;
	int oldLeftTacho, oldRightTacho, diffLeftTacho, diffRightTacho, leftTacho, rightTacho;
	
	public Odometer(TwoWheeledRobot robot, int period, boolean start) {
		// initialise variables
		this.robot = robot;
		this.nav = new Navigation(this);
		odometerTimer = new Timer(period, this);
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		lock = new Object();
		this.left_radius = robot.leftRadius();
		this.right_radius = robot.rightRadius();
		this.width = robot.width();
		
		// start the odometer immediately, if necessary
		if (start)
			odometerTimer.start();
	}
	
	public Odometer(TwoWheeledRobot robot) {
		this(robot, DEFAULT_PERIOD, false);
	}
	
	public Odometer(TwoWheeledRobot robot, boolean start) {
		this(robot, DEFAULT_PERIOD, start);
	}
	
	public Odometer(TwoWheeledRobot robot, int period) {
		this(robot, period, false);
	}
	
	public void timedOut() {
		double deltaC, deltaTheta;
		leftTacho = Motor.A.getTachoCount();
		rightTacho = Motor.B.getTachoCount();
		// Determine the change in degrees between for each tacho since last check
		diffLeftTacho = leftTacho -  oldLeftTacho;
		diffRightTacho = rightTacho - oldRightTacho;
		// Update the old values with the new ones
		oldLeftTacho = leftTacho;
		oldRightTacho = rightTacho;
		
		deltaC = (getLeftDelta(diffLeftTacho) + getRightDelta(diffRightTacho))/2;
		deltaTheta = (getLeftDelta(diffLeftTacho) - getRightDelta(diffRightTacho))/width;


		synchronized (lock) {
			// don't use the variables x, y, or theta anywhere but here!
			x += deltaC * Math.cos( Math.PI*theta/180 + deltaTheta/2 );
			y += deltaC * Math.sin( Math.PI*theta/180 + deltaTheta/2 );
			theta = fixDegAngle(theta + deltaTheta/Math.PI * 180);
		}
	}
	
	// accessors
	public void getPosition(double [] pos) {
		synchronized (lock) {
			pos[0] = x;
			pos[1] = y;
			pos[2] = theta;
		}
	}
	
	public double getX(){
		double result;
		synchronized (lock) {
			result = x;
		}
		return result;
	}

	public double getY(){
		double result;
		synchronized (lock) {
			result = y;
		}
		return result;
	}

	public double getAngle(){
		double result;
		synchronized (lock) {
			result = theta;
		}
		return result;
	}

	public TwoWheeledRobot getTwoWheeledRobot() {
		return robot;
	}
	
	public Navigation getNavigation() {
		return this.nav;
	}
	
	/**
	 * Helper function to convert degrees to radians
	 * @param degrees
	 * @return radians
	 */
	private double degToRadians(int degrees){
		return degrees * Math.PI / 180;
	}

	/**
	 * Determines the distance traveled by the left wheel
	 * @param degreeDelta Change in Degrees
	 * @return distance in cm
	 */
	private double getLeftDelta(int degreeDelta) {
		return left_radius * degToRadians(degreeDelta);
	}
	
	/**
	 * Determines the distance traveled by the right wheel
	 * @param degreeDelta Change in Degrees
	 * @return distance in cm
	 */
	private double getRightDelta(int degreeDelta) {
		return right_radius * degToRadians(degreeDelta);
	}
	
	// mutators
	public void setPosition(double [] pos, boolean [] update) {
		synchronized (lock) {
			if (update[0]) x = pos[0];
			if (update[1]) y = pos[1];
			if (update[2]) theta = pos[2];
		}
	}
	
	// static 'helper' methods
	public static double fixDegAngle(double angle) {		
		if (angle < 0.0)
			angle = 360.0 + (angle % 360.0);
		
		return angle % 360.0;
	}
	
	public static double minimumAngleFromTo(double a, double b) {
		double d = fixDegAngle(b - a);
		
		if (d < 180.0)
			return d;
		else
			return d - 360.0;
	}
}
