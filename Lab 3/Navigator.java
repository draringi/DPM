import lejos.util.*;
import lejos.nxt.*;

class Navigator implements TimerListener {
	private Odometer odometer;
	private OdometryDisplay odometryDisplay;
	private boolean travelling;
	private boolean turning;
	private static final double RADIUS = 2.15;
	private static final double WIDTH = 14.52;
	static private final double TOLERANCE = 0.2;
	static private final double MIN_DENOMINATOR = 0.1;
	static private final double MIN_ANGLE = Math.PI/16;
	static private final double MODULUS = 2* Math.PI;
	static private final int FORWARD_SPEED = 250;
	static private final int TURNING_SPEED = 100;
	static private final int DODGING_SPEED = 50;
	static private final int FILTER_OUT = 5;
	static private final int WALL_ALERT = 25;
	private int filterControl;
	private boolean dodging;
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
		this.dodging = false;
		this.travelLock = new Object();
		this.angleLock = new Object();
		filterControl = 21;
		for (NXTRegulatedMotor motor : new NXTRegulatedMotor[] { leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(1000);
		}
	}
	
	public void timedOut() {
		boolean turningTemp;
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
			turningTemp = this.turning;
		}
		synchronized (travelLock) {
			if(this.travelling && !turningTemp){
		    	if(ultrasonicFilter(ultrasonic.getDistance()) > WALL_ALERT) {
		    		double xDiff = xTarget - odometer.getX();
		    		double yDiff = yTarget - odometer.getY();
		    		if (Math.abs(xDiff) < TOLERANCE && Math.abs(yDiff) < TOLERANCE){
		    			engineStop();
		    			synchronized (angleLock){
		    				this.turning = false;
		    			}
		    			this.travelling = false;
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
		    			if(Math.abs(modulus(theta - modulus(odometer.getTheta()))) < MIN_ANGLE ){
		    				engineStart();
		    			} else {
		    				turnTo(theta);
		    			}
		    		}
		    	} else {
		    		if(!dodging){
		    			Motor.C.rotate(-45, false);
		    			Motor.C.flt();
		    			leftMotor.setSpeed(FORWARD_SPEED);
		    			rightMotor.setSpeed(TURNING_SPEED);
		    			leftMotor.forward();
		    			rightMotor.forward();
		    			dodging = true;
		    		} else {
		    			leftMotor.setSpeed(FORWARD_SPEED);
		    			rightMotor.setSpeed(dodgingSpeed(ultrasonicFilter(ultrasonic.getDistance())));
		    			if(filterControl >= FILTER_OUT){
		    				Motor.C.rotate(45, false);
							Motor.C.flt();
							dodging = false;
		    			}
		    		}
				}
			} 
		} 
	}
	
	private int dodgingSpeed( int distance){
		return Math.min(Math.max((50*(distance - 20)),1),300);
	}
	
	public void travelTo(double x, double y){
		synchronized (travelLock) {
			this.travelling = true;
			this.xTarget = x;
			this.yTarget = y;
		}
		this.odometryDisplay.setTarget(x, y);
	}
	
	public void turnTo(double theta){
		synchronized (angleLock) {
			this.targetTheta = modulus(theta); 
			this.turning = true;
		}
	}
	
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
	
	private void engineStop(){
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
	}
	
	private void engineStart(){
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.forward();
		rightMotor.forward();
	}
	
	private void turnLeft(){
		leftMotor.setSpeed(TURNING_SPEED);
		rightMotor.setSpeed(TURNING_SPEED);
		leftMotor.backward();
		rightMotor.forward();
	}
	
	private void turnRight(){
		leftMotor.setSpeed(TURNING_SPEED);
		rightMotor.setSpeed(TURNING_SPEED);
		leftMotor.forward();
		rightMotor.backward();
	}
	
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
	
	private int ultrasonicFilter(int value) {
		if (value <= WALL_ALERT && filterControl >= FILTER_OUT) {
			filterControl = 0;
		}
		if (filterControl < FILTER_OUT) {
			filterControl++;
			value = WALL_ALERT;
		}
		return value;
	}
}
