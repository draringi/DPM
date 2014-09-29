import lejos.util.*;
import java.util.Queue;

class Navigator implements TimerListener {
	private Odometer odometer;
	private boolean travelling;
	private boolean turning;
	static private final TOLERANCE = 0.1;
	static private final MIN_ANGLE = Math.PI/16;
	static private final FORWARD_SPEED = 250;
	static private final TURNING_SPEED = 100;
	private Object lock;
	private double xTarget, yTarget, targetTheta;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;
	
	
	public Navigator () {
		this.odometer = new Odometer();
		this.travelling = false;
		this.turning = false;
	}
	
	public void timedOut() {
	    synchronized (lock) {
	    	if(this.turning){
	    		
	    	}
	    	if(this.travelling){
	    		if(true) {
	    			double xDiff = xTarget - odometer.getX();
	    			double yDiff = yTarget - odometer.getY();
	    			if (Math.abs(xDiff) < TOLERANCE && Math.abs(yDiff) < TOLERANCE){
	    				this.traveling = false;
	    				engineStop();
	    				break;
	    			}
	    			targetTheta = Math.atan(yDiff/xDiff);
	    			if (x < 0){
	    				targetTheta += Math.PI;
	    			}
	    			if( targetTheta - odometer.getTheta() < MIN_ANGLE ){
	    				engineStart();
	    			}
	    		}
	    	}
	    }
	}
	
	public void travelTo(double x, double y){
		synchronized (lock) {
			this.xTarget = x;
			this.yTarget = y;
			this.travelling = true;
		}
	}
	
	public void turnTo(double theta){
		synchronized (lock) {
			this.targetTheta = theta;
			this.turning = true;
		}
	}
	
	public boolean isNavigating(){
		boolean result;
		synchronized (lock) {
			result = this.travelling;
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
		leftMotor.forward()
		rightMotor.forward()
	}
	
	private void turnLeft(int turnAngle){
		leftMotor.setSpeed(TURNING_SPEED);
		rightMotor.setSpeed(TURNING_SPEED);
		leftMotor.rotate(-turnAngle, true);
		rightMotor.rotate(turnAngle, false);
	}
	
	private void turnRight(int turnAngle){
		leftMotor.setSpeed(TURNING_SPEED);
		rightMotor.setSpeed(TURNING_SPEED);
		leftMotor.rotate(turnAngle, true)
		rightMotor.rotate(-turnAngle, false)
	}
	
	private int convertAngle(double angle) {
		return WIDTH * angle * 180/(2*Math.PI*RADIUS);
	}
}
