import java.util.Random;

public class StochasticOrientation extends Orientation {
	private Random rng;
	private static final int LEFT = 0, FORWARD = 1;
	/**
	 * @param map
	 * @param odo
	 */
	public StochasticOrientation(Map map, Odometer odo) {
		super(map, odo);
		rng = new Random();
	}

	@Override
	public void move(boolean wall, int direction, Navigation nav) {
		if(wall){
			nav.turn(-90);
		} else {
			int result = rng.nextInt(2);
			switch(result){
			case LEFT:
				nav.turn(-90);
				break;
			case FORWARD:
				nav.travelTile(direction);
				break;
			}
		}
	}

}
