
package dpm.teamone.driver.navigation;

import dpm.teamone.driver.maps.GridMap;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.FixedRangeScanner;
import lejos.robotics.localization.MCLPoseProvider;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Pose;
import lejos.robotics.pathfinding.FourWayGridMesh;


public class Localizer{
    
     private static final double WHEEL_RADIUS=2.5;
     //Distance between centers of the left and right wheels
     private static final double TRACK_WIDTH=15.5;
     private static NXTRegulatedMotor LEFT_MOTOR = Motor.A, RIGHT_MOTOR = Motor.B;
     private DifferentialPilot pilot= new DifferentialPilot(WHEEL_RADIUS,TRACK_WIDTH,LEFT_MOTOR,RIGHT_MOTOR);
     
     // The Ultrasonic sensor is not-mounted on a motor so instead of
     // only the sensor rotating, the whole robot will turn
     
     private FixedRangeScanner scanner = new FixedRangeScanner(pilot,new UltrasonicSensor(SensorPort.S3));
     private GridMap map;
     private static final int particles =25, borders =50;
     private MCLPoseProvider localisationAlgo;
     
     //Constructor
     public Localizer(GridMap map){
         LineMap lMap = this.map.getLineMap();
         this.localisationAlgo= new MCLPoseProvider(pilot,scanner,lMap,particles,borders);
}
     
     // Return robot's current position and heading using Monte Carlo 
     // Localisation Algorithm
     public Pose performLocalisation(){
     return this.localisationAlgo.getPose();
     }
    
}
