package dpm.teamone.driver.events;

import java.util.Queue;

import lejos.robotics.navigation.Pose;
import dpm.teamone.driver.navigation.Direction;

public class LineLogger {

	private static Queue<LineRecord> records;

	public static void addRecord(LineRecord record) {
		records.push(record);
	}

	public static void addRecord(Pose status, float dist, boolean leftFirst,
			float believed, float offset, float real, Direction dir, Pose prev) {
		records.push(new LineRecord(status, dist, leftFirst, believed, offset,
				real, dir, prev));
	}

	public static LineRecord getNext() {
		return (LineRecord) records.pop();
	}

	public static boolean hasRecords() {
		return isInit() && !(records.isEmpty());
	}

	public static void Init() {
		if (records == null) {
			records = new Queue<LineRecord>();
		}
	}

	public static boolean isInit() {
		return (records != null);
	}
}
