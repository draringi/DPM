import lejos.util.*;
import lejos.nxt.*;

class Navigator implements TimerListener {
	private Odometer odometer;
	private boolean travelling;
	private boolean turning;
	private static final double RADIUS = 2.15;
	private static final double WIDTH = 14.00;
	static private final double TOLERANCE = 0.1;
	static private final double MIN_ANGLE = Math.PI/16;
	static private final int FORWARD_SPEED = 250;
	static private final int TURNING_SPEED = 100;
	static private final int TURN_ANGLE = 5;
	private Object travelLock;
	private Object angleLock;
	private double xTarget, yTarget, targetTheta;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;
	
	
	public Navigator () {
		this.odometer = new Odometer(RADIUS, RADIUS, WIDTH);
		odometer.start();
		this.travelling = false;
		this.turning = false;
		this.travelLock = new Object();
		this.angleLock = new Object();
	}
	
	public void timedOut() {
		synchronized (angleLock) {
			if(this.turning){
		    		double deltaTheta = targetTheta - (odometer.getTheta() % (2 * Math.PI));
				if (Math.abs(deltaTheta) < MIN_ANGLE){
					this.turning = false;
				} else {
					if (deltaTheta > 0){
						turnLeft();
					} else {
						turnRight();
					}
				}
			}
		}
		synchronized (travelLock) {
			if(this.travelling){
		    		if(true) {
		    			double xDiff = xTarget - odometer.getX();
		    			double yDiff = yTarget - odometer.getY();
		    			if (Math.abs(xDiff) < TOLERANCE && Math.abs(yDiff) < TOLERANCE){
		    				this.travelling = false;
		    				engineStop();
		    			} else {
						double theta = Math.atan(yDiff/xDiff);
						if (xDiff < 0){
							theta += Math.PI;
						}
						turnTo(theta);
						if( targetTheta - odometer.getTheta() < MIN_ANGLE ){
							engineStart();
						}
					}
		    		}
			}
		}
	}
	
	public void travelTo(double x, double y){
		synchronized (travelLock) {
			this.xTarget = x;
			this.yTarget = y;
			this.travelling = true;
		}
	}
	
	public void turnTo(double theta){
		synchronized (angleLock) {
			this.targetTheta = theta % (2 * Math.PI); // Ensure that theta is between 0 and 2 pi
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
	
	private int convertAngle(double angle) {
		return radiansToDegrees(angle*WIDTH*2);
	}
	
	private int radiansToDegrees(double rads) {
		return (int) Math.round(rads/Math.PI * 180);
	}
}
