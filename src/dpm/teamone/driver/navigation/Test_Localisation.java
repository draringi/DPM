/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dpm.teamone.driver.navigation;

import dpm.teamone.driver.maps.GridMap;
import static dpm.teamone.driver.maps.MapFactory.lab5Map;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.robotics.navigation.Pose;

/**
 *
 * @author Mehdi
 */
public class Test_Localisation {
    
    public static void main(String[] args){
   
        GridMap map = lab5Map();

        Localisation loc = new Localisation(map);

        int [] initialLocation = setArray(0,0,30,90);
        Pose result = loc.localize(initialLocation);
        System.out.println("X: "+result.getX()+", Y: "+result.getY()+" Angle "+result.getHeading());
     
    }
   private static int[] setArray(int x1,int x2,int x3,int x4){
   int[] temp = new int[4];
   temp[0]=x1;temp[1]=x2;temp[2]=x3;temp[3]=x4;
   return temp;
   }
    }
    

