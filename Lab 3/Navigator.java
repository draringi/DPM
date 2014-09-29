import lejos.util.*;
import java.util.Queue;

class Navigator implements TimerListener {
	private Odometer odometer;
	private boolean travelling;
	private Object lock;
	private double xTarget, yTarget;
	
	public Navigator () {
		this.odometer = new Odometer();
		this.travelling = false;
	}
	
	public void timedOut() {
	    synchronized (lock) {
	    	if(this.travelling){
	    		
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
			
		}
	}
	
	public boolean isNavigating(){
		boolean result;
		synchronized (lock) {
			result = this.travelling;
		}
		return result;
	}
}
