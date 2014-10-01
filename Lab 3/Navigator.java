import lejos.util.*;
import lejos.nxt.*;

/**
 * Navigational class. It takes in way-points or angles, and travels to, or turns to, the given value.
 * @author Michael Williams (260369438)
 * @author Leonardo Siracusa (260585931)
 *
 */
class Navigator implements TimerListener {
	private Odometer odometer;
	private OdometryDisplay odometryDisplay;
	private boolean travelling;
	private boolean turning;
	private static final double RADIUS = 2.123;
	private static final double WIDTH = 14.645;
	static private final double TOLERANCE = 0.5;
	static private final double MIN_DENOMINATOR = 0.1;
	static private final double MIN_ANGLE = Math.PI/32;
	static private final double MODULUS = 2* Math.PI;
	static private final int FORWARD_SPEED = 250;
	static private final int TURNING_SPEED = 100;
	static private final int WALL_ALERT = 8;
	static private final UltrasonicSensor ultrasonic = new UltrasonicSensor(SensorPort.S2);
	private Object travelLock;
	private Object angleLock;
	private double xTarget, yTarget, targetTheta;
	static private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;
	
	public Navigator () {
		this.odometer = new Odometer(RADIUS, RADIUS, WIDTH);
		this.odometryDisplay = new OdometryDisplay(odometer);
		odometer.start();
		odometryDisplay.start();
		this.travelling = false;
		this.turning = false;
		this.travelLock = new Object();
		this.angleLock = new Object();
		for (NXTRegulatedMotor motor : new NXTRegulatedMotor[] { leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(1000);
		}
	}
	
	/**
	 * Called everytime the timer reaches a set amount of time.
	 * It then calls each logical sections respective container function.
	 */
	public void timedOut() {
		boolean turningTemp;
		this.turn();
		synchronized(angleLock){
			turningTemp = this.turning;
		}
		travel(!turningTemp); 
	}
	
	/**
	 * Container function for traveling code.
	 * This function determines if the bot was reached the target yet.
	 * If not, it checks if it is heading in the correct direction.
	 * If not, it sets the new heading to the correct direction.
	 * It also checks that there isn't a wall in front of the robot.
	 * If there is, it dodges it.
	 * @param allowedToTravel Boolean telling the function if it is allowed to travel or not.
	 */
	private void travel(boolean allowedToTravel){
		synchronized (travelLock) {
			if(this.travelling && allowedToTravel){
		    	if(safeDistance()) {
		    		double xDiff = xTarget - odometer.getX();
		    		double yDiff = yTarget - odometer.getY();
		    		if (atDestination(xDiff, yDiff)){
		    			engineStop();
		    			return;
		    		} else {
		    			double theta;
		    			if (Math.abs(xDiff) > MIN_DENOMINATOR) {
		    				theta = Math.atan(yDiff/xDiff);
		    				if (xDiff < 0){
		    					theta += Math.PI;
		    				}
		    			} else {
		    				if (yDiff > 0) {
		    					theta = Math.PI/2;
		    				} else {
		    					theta = -Math.PI/2;
		    				}
		    			}
		    			if(Math.abs(diffTheta(theta)) < MIN_ANGLE ){
		    				engineStart();
		    			} else {
		    				turnTo(theta);
		    			}
		    		}
		    	} else {
		    		this.dodge();
				}
			} 
		}
	}
	
	/**
	 * Calculates the relative distance between two headings.
	 * @param theta
	 * @return
	 */
	double diffTheta(double theta){
		return modulus(theta - modulus(odometer.getTheta()));
	}
	
	/**
	 * Checks that wall isn't too close
	 * @return False if wall is close, True otherwise.
	 */
	private boolean safeDistance(){
		return ultrasonic.getDistance() > WALL_ALERT;
	}
	
	/**
	 * Determines if robot has reached the waypoint or not.
	 * @param xDiff Distance from the target location on the x-axis 
	 * @param yDiff Distance from the target location on the y-axis
	 * @return True if current location is within the predefined tolerance.
	 */
	private boolean atDestination(double xDiff, double yDiff){
		return Math.abs(xDiff) < TOLERANCE && Math.abs(yDiff) < TOLERANCE;
	}
	
	/**
	 * Triggered when a block is detected
	 */
	private void dodge(){
		leftMotor.flt();
		rightMotor.flt();
		rightMotor.rotate(-convertAngle(95));
		rightMotor.flt();
		leftMotor.rotate(convertDistance(40), true);
		rightMotor.rotate(convertDistance(40));
		leftMotor.rotate(-convertAngle(60));
		leftMotor.rotate(convertDistance(35), true);
		rightMotor.rotate(convertDistance(35));
		leftMotor.flt();
		rightMotor.flt();
	}
	
	
	/*
	 * Container function for the turning logic.
	 * This function calculates if the system should turn left or right,
	 * and stops when the heading of the robot is within an acceptable  
	 */
	private void turn(){
		synchronized (angleLock) {
			if(this.turning){
		    	double deltaTheta = modulus(targetTheta - modulus(odometer.getTheta()));
				if (Math.abs(deltaTheta) < MIN_ANGLE){
					this.turning = false;
				} else {
					if (deltaTheta < 0){
						turnLeft();
					} else {
						turnRight();
					}
				}
			}
		}
	}

	/**
	 * Converts a rotational angle into motor rotation distance.
	 * @param angle Angle to turn
	 * @return angle for motor to rotate
	 */
	private static int convertAngle(double angle) {
		return (int) ((WIDTH * angle) / ( RADIUS));
	}
	
	/**
	 * Converts a translation distance into motor rotation distance.
	 * @param distance Distance in cm to travel 
	 * @return angle for motor to rotate
	 */
	private static int convertDistance(double distance) {
		return (int) ((180.0 * distance) / (Math.PI * RADIUS));
	}
	
	/**
	 * Set waypoint co-ordinate to travel to
	 * @param x Location on x-axis to travel to
	 * @param y Location on y-axis to travel to
	 */
	public void travelTo(double x, double y){
		synchronized (travelLock) {
			this.travelling = true;
			this.xTarget = x;
			this.yTarget = y;
		}
		this.odometryDisplay.setTarget(x, y);
	}
	
	/**
	 * Set new heading
	 * @param theta Angle relative to positive x-axis to face
	 */
	public void turnTo(double theta){
		synchronized (angleLock) {
			this.targetTheta = modulus(theta); 
			this.turning = true;
		}
	}
	
	/**
	 * Determines if the robot is currently performing a navigation operation
	 * @return True if robot is navigating (Travelling or Turning), false otherwise
	 */
	public boolean isNavigating(){
		boolean result;
		synchronized (travelLock) {
			result = this.travelling;
		}
		synchronized(angleLock){
			result = result || this.turning;
		}
		return result;
	}
	
	/**
	 * Stops the engine, and reports the end of navigation
	 */
	private void engineStop(){
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
		synchronized (angleLock){
			this.turning = false;
		}
		this.travelling = false;
	}
	
	/**
	 * Starts the engine in a forward motion
	 */
	private void engineStart(){
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.forward();
		rightMotor.forward();
	}
	
	/**
	 * Rotate the robot in a counter-clockwise direction
	 */
	private void turnLeft(){
		leftMotor.setSpeed(TURNING_SPEED);
		rightMotor.setSpeed(TURNING_SPEED);
		leftMotor.backward();
		rightMotor.forward();
	}
	
	/**
	 * Rotate the robot in a clockwise direction
	 */
	private void turnRight(){
		leftMotor.setSpeed(TURNING_SPEED);
		rightMotor.setSpeed(TURNING_SPEED);
		leftMotor.forward();
		rightMotor.backward();
	}
	
	/**
	 * Correctional function, to make up for the broken modulus function in the lejos firmware.
	 * @param value  Input Angle
	 * @return The input angle modulus 2pi, shifted down by pi, resulting in a value between -pi and +pi
	 */
	private static double modulus(double value){
		value = value % MODULUS;
		if (value < -Math.PI) {
			value += MODULUS;
		}
		if (value > Math.PI){
			value -= MODULUS;
		}
		return value;
	}
}
