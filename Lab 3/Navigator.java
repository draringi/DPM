import lejos.util.*;

class Navigator implements TimerListener {
    private Odometer odometer;
    private Queue destinationQueue;
    private Location destination;
	public Navigator (Queue destinations) {
		this.destinationQueue = destinations;
		this.destination = destinationQueue.pop();
		this.odometer = new Odometer();
	}
	public void timedOut() {
	    
	}
	
	public class Location {
		double x, y;
		public Location(double x, double y){
	 		this.x = x;
	 		this.y = y;
	 	}
	 	
	 	double getX() {
	 		return this.x;
	 	}
	 	
	 	double getY() {
	 		return this.y;
	 	}
	}
}
