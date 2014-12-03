package dpm.teamone.driver.events;

import lejos.robotics.navigation.Pose;
import dpm.teamone.driver.navigation.Direction;

/**
 * Record used by the {@link LineLogger}
 * @author Michael Williams
 * @see LineCorrection
 */
public class LineRecord {
	public float believedAngle, angleOffset, realAngle;
	public Direction dir;
	public float dist;
	public boolean leftFirst;
	public Pose status, previous;

	/**
	 * Creates new Line Record.
	 * @param status Updated location.
	 * @param dist Distance between the two sensors seeing the line
	 * @param leftFirst If the left sensor saw the line first or not.
	 * @param believed What it thought its angle was.
	 * @param offset How far off it was.
	 * @param real What its real angle was.
	 * @param dir Direction the Robot was traveling in. 
	 * @param prev Previous believed Location.
	 */
	public LineRecord(Pose status, float dist, boolean leftFirst,
			float believed, float offset, float real, Direction dir,
			Pose previous) {
		this.status = status;
		this.dist = dist;
		this.leftFirst = leftFirst;
		this.believedAngle = believed;
		this.angleOffset = offset;
		this.realAngle = real;
		this.dir = dir;
		this.previous = previous;
	}
}
