package dpm.teamone.driver;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.comm.RConsole;
import lejos.robotics.navigation.Pose;
import dpm.teamone.driver.events.EventManager;
import dpm.teamone.driver.events.LineLogger;
import dpm.teamone.driver.events.LineRecord;
import dpm.teamone.driver.maps.MapFactory;
import dpm.teamone.driver.navigation.NavigationController;

public class correction_test {

	public static void main(String[] args) {
		Button.waitForAnyPress();
		NavigationController nav = new NavigationController(MapFactory.blankMap());
		nav.setPose(new Pose(-15, -15, 0));
		LineLogger.Init();
		EventManager events = new EventManager(nav);
		events.start();
		//nav.driveToGrid(0, 0);
		//nav.driveToGrid(1, 0);
		//nav.driveToGrid(2, 0);
		//nav.driveToGrid(1, 1);
		nav.driveToGrid(3, 3);
		nav.driveToGrid(1, 1);
		//nav.driveToGrid(2, 0);
		EventManager.pause();
		//nav.turnTo(0);
		nav.turnTo(180);
		Pose pose = nav.getPose();
		LCD.drawInt(Math.round((pose.getX() * 10)), 0, 2);
		RConsole.openUSB(0);
		int i = 0;
		while(LineLogger.hasRecords()){
			LineRecord record = LineLogger.getNext();
			RConsole.println("Record " + i++);
			if(record.leftFirst){
				RConsole.println("Left Sensor was first");
			} else {
				RConsole.println("Right Sensor was first");
			}
			String str = "Distance between triggers: " + record.dist;
			RConsole.println(str);
			str = "It thought it was at " + record.believedAngle;
			RConsole.println(str);
			str = "It was off by " + record.angleOffset;
			RConsole.println(str);
			str = "It was at " + record.realAngle;
			RConsole.println(str);
			str = "Which is " + record.dir.toString();
			RConsole.println(str);
			str = "Status: " + record.status;
			RConsole.println(str);
			str = "Previous Status: " + record.previous;
			RConsole.println(str);
			RConsole.print("\n");
		}
	}

}
