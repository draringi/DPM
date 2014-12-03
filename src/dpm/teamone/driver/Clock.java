package dpm.teamone.driver;

/**
 * Clock keeps track of the time the robot has been running, and tells if it has
 * hit the time limit.
 * 
 * @author Michael Williams
 * 
 */
public class Clock {

	private int timeLimit;
	private long timeEnd;

	/**
	 * 
	 * @param timeLimit Last run start time in seconds
	 */
	public Clock(int timeLimit) {
		this.timeLimit = timeLimit;
		this.timeEnd = -1;
	}

	/**
	 * Starts the clock.
	 */
	public void start() {
		this.timeEnd = System.currentTimeMillis() + (this.timeLimit * 1000);
	}

	/**
	 * Checks if time is up.
	 * @return True if time passed since start was is greater than timeLimit, false otherwise. If start hasn't been called, always returns false.
	 */
	public boolean timeUp() {
		return ((this.timeEnd > 0) && (System.currentTimeMillis() > this.timeEnd));
	}
}
