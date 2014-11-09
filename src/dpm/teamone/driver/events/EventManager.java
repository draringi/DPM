package dpm.teamone.driver.events;

import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import dpm.teamone.driver.DriverRobot;

/**
 * The EventManager handles all the Event driven programming of the robot. At
 * the moment this only includes updating the position based off of lines
 * crossed.
 * 
 * @author Michael Williams
 *
 */
public class EventManager {

	private Arbitrator arbitrator;
	public DriverRobot robot;

	/**
	 * Constructor creates an underlying arbitrator while providing access to
	 * the robot to any events
	 * 
	 * @param robot
	 *            Main Driver Robot control
	 */
	public EventManager(DriverRobot robot) {
		this.robot = robot;
		Behavior behaviors[] = { new LineCorrecter() };
		this.arbitrator = new Arbitrator(behaviors);
	}

	/**
	 * Starts the underlying Arbitrator
	 */
	public void start() {
	}

}