// Test file for the navigation using a map without obstacles
// Since localisation has not yet been completed the robot's
// initial location is considered to be (0,0) facing north.


package dpm.teamone.driver.navigation;

import maps.GridMap;
import maps.MapFactory;
import lejos.robotics.navigation.Pose;
import lejos.nxt.*;

public class Test_Localisation{
    
    public static void main(String[] args){
    
    	int buttonChoice;
    	


		do {
			

			// ask the user whether the motors should drive in a square or float
			LCD.drawString("< Left | Right >", 0, 0);
			LCD.drawString("       |        ", 0, 1);
			LCD.drawString(" Test | Test  ", 0, 2);
			LCD.drawString("Navig | localisation   ", 0, 3);
			

			buttonChoice=Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		if (buttonChoice == Button.ID_LEFT) {
			 
			   GridMap map = MapFactory.lab5Map();
			   NavigationController  nav = new NavigationController(map);
			  Localisation loc = new Localisation(map);
			  Pose p=loc.performLocalisation();
			  LCD.clear();
			  LCD.drawString("X:"+p.getX()+" Y:"+p.getY(), 0, 2);
			  LCD.drawString(" Heading: "+p.getHeading(), 0, 4);
			 nav.setPose(p);
			 nav.driveToGrid(50, 50);
			  
		} else {
			
		}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	
     
      
    }
    
}
