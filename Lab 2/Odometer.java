/*
 * Odometer.java
 */
import lejos.nxt.*;

public class Odometer extends Thread {
	// robot position
	private double x, y, theta;

	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;

	// necessary robot-dependant variables for calculations
	private double right_radius, left_radius, width;

	// default constructor
	public Odometer(double left_radius, double right_radius, double width) {
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		lock = new Object();
		this.left_radius = left_radius;
		this.right_radius = right_radius;
		this.width = width;
	}

	// run method (required for Thread)
	public void run() {
		long updateStart, updateEnd;
		double deltaLeft, deltaRight, deltaC, deltaTheta;
		
		while (true) {
			updateStart = System.currentTimeMillis();
			deltaLeft = left_radius * Motor.A.getTachoCount() * Math.PI/180;
			Motor.A.resetTachoCount();
			deltaRight = right_radius * Motor.B.getTachoCount() * Math.PI/180;
			Motor.B.resetTachoCount();
			deltaC = (deltaRight + deltaLeft)/2;
			deltaTheta = (deltaRight - deltaLeft)/width;


			synchronized (lock) {
				// don't use the variables x, y, or theta anywhere but here!
				x += deltaC * Math.cos( theta + deltaTheta/2 );
				y += deltaC * Math.sin( theta + deltaTheta/2 );
				theta = theta + deltaTheta;
			}

			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	// accessors
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta;
		}
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
}
