package dpm.teamone.driver;

/**
 * Clock keeps track of the time the robot has been running, and tells if it has hit the time limit.
 * 
 * @author Michael Williams
 * 
 */
public class Clock {

	private int timeLimit;
	private long timeEnd;
	
	public Clock(int timeLimit){
		this.timeLimit = timeLimit;
		this.timeEnd = -1;
	}
	
	public void start(){
		timeEnd = System.currentTimeMillis() + timeLimit*1000;
	}

	public boolean timeUp(){
		return (timeEnd > 0 && System.currentTimeMillis() > timeEnd);
	}
}
