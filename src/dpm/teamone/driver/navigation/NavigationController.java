package dpm.teamone.driver.navigation;

import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;
import dpm.teamone.driver.maps.GridMap;

public class NavigationController {

	private DifferentialPilot pilot;

	private Navigator navigator;

	public int[] dropZone;

	public int[] pickupZone;

	public GridMap map;

	public void driveToGrid(int x, int y) {
	}

	public void driveToGrid(int x, int y, Direction direction) {
	}

	public void setDropZonet(int x, int y, int width, int height) {
	}

	public void setPickUpZone(int x, int y, int width, int height) {
	}

	public void turnTo(Direction direction) {
	}

	public void turnTo(double angle) {
	}

}