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
 * @see lejos.robotics.subsumption.Arbitrator
 * 
 */
public class EventManager extends Thread {

	/**
	 * Returns current set status of the Event Subsystem.
	 * 
	 * @return Status. True if running, false otherwise.
	 */
	public static boolean isRunning() {
		boolean result;
		synchronized (lock) {
			result = running;
		}
		return result;
	}

	/**
	 * Pauses the Event Subsystem
	 */
	public static void pause() {
		synchronized (lock) {
			running = false;
		}
	}

	/**
	 * (Re)starts the Event Subsystem
	 */
	public static void restart() {
		synchronized (lock) {
			running = true;
		}
	}

	/**
	 * Internal lock for the Event Subsystem status
	 */
	private static Object lock = new Object();

	/**
	 * Status of the Event Subsystem. When true, allowed to run as usual. When
	 * false, the system is paused.
	 */
	private static boolean running;

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
	}

	/**
	 * Starts the underlying Arbitrator
	 */
	@Override
	public void run() {
		this.arbitrator.start();
	}
}
