package dpm.teamone.driver.events;

import lejos.robotics.navigation.Pose;
import dpm.teamone.driver.navigation.Direction;

public class LineRecord {
	public float believedAngle, angleOffset, realAngle;
	public Direction dir;
	public float dist;
	public boolean leftFirst;
	public Pose status, previous;

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
