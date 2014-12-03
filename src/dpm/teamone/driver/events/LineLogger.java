package dpm.teamone.driver.events;

import java.util.Queue;

import lejos.robotics.navigation.Pose;
import dpm.teamone.driver.navigation.Direction;

/**
 * Logging utility for debugging the Line Corrector.
 * It keeps track of the location it thought it was at, the correction,
 * and the new value, as well as which sensor saw the line first.
 * @author Michael Williams
 * @see LineCorrector
 * @see LineRecord
 */
public class LineLogger {

	/**
	 * Adds new record to the Line Logger
	 * @param record Record to be added.
	 */
	public static void addRecord(LineRecord record) {
		records.push(record);
	}

	/**
	 * Adds new record to the Line Logger
	 * @param status Updated location.
	 * @param dist Distance between the two sensors seeing the line
	 * @param leftFirst If the left sensor saw the line first or not.
	 * @param believed What it thought its angle was.
	 * @param offset How far off it was.
	 * @param real What its real angle was.
	 * @param dir Direction the Robot was traveling in. 
	 * @param prev Previous believed Location.
	 */
	public static void addRecord(Pose status, float dist, boolean leftFirst,
			float believed, float offset, float real, Direction dir, Pose prev) {
		records.push(new LineRecord(status, dist, leftFirst, believed, offset,
				real, dir, prev));
	}

	/**
	 * Gets the next record in the Line Logger.
	 * @return next record in the internal queue.
	 */
	public static LineRecord getNext() {
		return (LineRecord) records.pop();
	}

	/**
	 * Returns state of the Line Logger.  
	 * @return False if Line Logger is empty or not initialized, true if records exist. 
	 */
	public static boolean hasRecords() {
		return isInit() && !(records.isEmpty());
	}

	/**
	 * Initializes the Line Logger
	 */
	public static void Init() {
		if (records == null) {
			records = new Queue<LineRecord>();
		}
	}

	/**
	 * Returns status off the Line Logger.
	 * @return True if Line Logger has been initialized, false otherwise.
	 */
	public static boolean isInit() {
		return (records != null);
	}

	/**
	 * Internal dynamic storage for the logs.
	 */
	private static Queue<LineRecord> records;
}
