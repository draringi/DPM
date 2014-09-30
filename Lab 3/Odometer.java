/* Michael Williams - 260369438
 * Leonardo Siracusa - 260585931
 * Odometer.java
 */
import lejos.nxt.*;

public class Odometer extends Thread {
	// robot position
	private double x, y, theta;

	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;
	static private final double MODULUS = 2* Math.PI;

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

	/**
	 * run method (required for Thread)
	 * determined by dTheta = (dL - dR)/width
	 * and dC = (dL + dR)/2 
	 */
	public void run() {
		long updateStart, updateEnd;
		double deltaC, deltaTheta;
		int oldLeftTacho, oldRightTacho, diffLeftTacho, diffRightTacho, leftTacho, rightTacho;
		oldLeftTacho = oldRightTacho = 0;
		while (true) {
			updateStart = System.currentTimeMillis();
			
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
				x += deltaC * Math.cos( theta + deltaTheta/2 );
				y += deltaC * Math.sin( theta + deltaTheta/2 );
				theta = modulus(theta + deltaTheta);
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
