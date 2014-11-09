package dpm.teamone.driver.navigation;

import dpm.teamone.driver.maps.GridMap;
import lejos.robotics.navigation.Pose;

public class Localisation {

    private GridMap map;
    private UltraSonic sensor;
    private NavigationController navigation;

    public Localisation(GridMap map) {
        this.map = map;
//        sensor = new UltraSonic();
//        navigation = new NavigationController(map);
    }

    public Pose performLocalisation() {
        int[] surroundings = getSurroundings();
        return localize(surroundings);

    }

    private int[] getSurroundings() {
        int[] inp = new int[4];
        for (int x = 0; x < 4; x++) {
            inp[x] = normalize(this.sensor.poll());
            this.navigation.turnTo(-90);
        }
        return inp;
    }

    public Pose localize(int[] inp) {
        Pose initialLocation = null;
        for (int x = 0; x < this.map.getWidth(); x++) {
            for (int y = 0; y < this.map.getHeight(); y++) {
                for (int ori = 0; ori < 4; ori++) {
                    if(!this.map.isObstacle(x, y)){
                    int[] surr = computeSur(x, y, ori);
                    if (isMatch(surr, inp)) {
                        initialLocation = new Pose((x*this.map.TILE_SIZE+15), (y*this.map.TILE_SIZE+15), getAngle(ori));
                        return initialLocation;
                    }
                }
                }
            }
        }
        return initialLocation;
    }

    private int getAngle(int i) {

        int angle = 0;

        switch (i) {
            case 0:
                angle = 90;
                break;
            case 1:
                angle = 0;
                break;
            case 2:
                angle = -90;
                break;
            case 3:
                angle = 180;
                break;
        }
        return angle;

    }

    private boolean isMatch(int[] A1, int[] A2) {
        boolean temp = true;
        for (int x = 0; x < A1.length; x++) {
            if (A1[x] != A2[x]) {
                temp = false;
            }
        }
        return temp;
    }

    public int[] computeSur(int x, int y, int ori) {
        // 0 = North    1=East      2=South     3=West
        int[] surr = new int[4];
        int temp = ori;
        for (int i = 0; i < 4; i++) {
            if (temp == 0) {
                if (y == this.map.getHeight()) {
                    surr[i] = 0;
                } else {
                    int incr = 0;
                    int distance = 0;
                    while (!this.map.isObstacle(x, (y + incr+1))&&(y+incr+1)!=this.map.getHeight()) {
                        distance += this.map.TILE_SIZE;
                        incr++;
                    }
                    surr[i] = distance;
                }

            } else if (temp == 1) {
                if (x == this.map.getWidth()) {
                    surr[i] = 0;
                } else {
                    int incr = 0;
                    int distance = 0;
                    while (!this.map.isObstacle(x + (incr+1), (y))&&(x+incr+1)!=this.map.getWidth()) {
                        distance += this.map.TILE_SIZE;
                        incr++;
                    }
                    surr[i] = distance;
                }

            } else if (temp == 2) {
                if (y == 0) {
                    surr[i] = 0;
                } else {
                    int incr = 0;
                    int distance = 0;
                    while ((!this.map.isObstacle(x, (y - incr-1)))&&(y-incr)!=0) {
                        distance += this.map.TILE_SIZE;
                        incr++;
                    }
                    surr[i] = distance;
                }
            } else if (temp == 3) {
                if (x == 0) {
                    surr[i] = 0;
                } else {
                    int incr = 0;
                    int distance = 0;
                    while ((!this.map.isObstacle(x - incr-1, (y)))&&(x-incr)!=0) { // Until obstacle or wall is reached
                        distance += this.map.TILE_SIZE;
                        incr++;
                    }
                    surr[i] = distance;
                }
            }
            temp = incrementOrientation(temp);
            
        }
        System.out.println("For ("+x+","+y+","+ori+") Result:"+arrayToString(surr));
        return surr;
    }
private String arrayToString(int[] x){
String ret = "";
for(int y=0;y<x.length;y++){
ret +=x[y]+" "; 
}
return ret;
}
    private int incrementOrientation(int ori) {
int temp = ori;
        if (temp < 3) {
            temp++;
        } else {
            
            temp= 0;
        }
        return temp;
    }

    private int normalize(int inp) {

        return ((inp / this.map.TILE_SIZE) * this.map.TILE_SIZE);
    }
}
