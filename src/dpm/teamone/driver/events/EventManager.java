package dpm.teamone.driver.events;

import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import dpm.teamone.driver.navigation.NavigationController;

/**
 * The EventManager handles all the Event driven programming of the robot. At
 * the moment this only includes updating the position based off of lines
 * crossed.
 * 
 * @author Michael Williams
 * 
 */
public class EventManager extends Thread {

	private static Object lock;
	private static boolean running;

	public static boolean isRunning() {
		boolean result;
		synchronized (lock) {
			result = running;
		}
		return result;
	}
	
	public static void pause() {
		synchronized (lock) {
			running = false;
		}
	}

	public static void restart() {
		synchronized (lock) {
			running = true;
		}
	}
	
	private final Arbitrator arbitrator;


	/**
	 * Constructor creates an underlying arbitrator while providing access to
	 * the robot to any events
	 * 
	 * @param robot
	 *            Main Driver Robot control
	 */
	public EventManager(NavigationController nav) {
		Behavior behaviors[] = { new LineCorrecter(nav) };
		this.arbitrator = new Arbitrator(behaviors);
		lock = new Object();
	}

	/**
	 * Starts the underlying Arbitrator
	 */
	@Override
	public void run() {
		restart();
		arbitrator.start();
	}
}
