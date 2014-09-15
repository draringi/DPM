#Lab 1: Wall Following#
##Background: Events##
For the purpose of making a wall following robot, it is necessary to develop a good
means of reading the ultrasonic sensor. The LEGO Mindstorms ultrasonic sensor is
capable of getting data at around 25 times per second, however due to the fact that
we won't be using timers, we will poll as fast as the ultrasonic will allow in a thread.
After each polling, the `UltrasonicPoller` will update the value of the distance
integer in the controller for which is being processed at that time.
 
In Lab1.java we create all the objects that we will need, which includes the two
controller types to be implemented. Based on whether the left or right button on the
NXT is pressed, it will choose the controller to be run accordingly.

The `UltrasonicPoller` object (called `usPoller`) is what gets the data from the
ultrasonic sensor, and gives it to any object of a class that implements the
`UltrasonicController` interface. Its constructor thus takes as arguments an
`UltrasonicSensor` object (which is part of the leJOS API), and any object
implementing the `UltrasonicController` interface. In the `UltrasonicPoller.java`
file provided, we see on line 16 how it gets the ultrasonic data, then passes it off to
the `UltrasonicController`, cont:

    public void run() {
    	while (true) {
    		//process collected data
    		cont.processUSData(us.getDistance());
    	}
    }

This is where any filters for the ultrasonic sensor should be implemented, such as
the removal of spurious 255 values.

The final element is the `processUSData(int distance)` method in `Pcontroller`
and `BangBangController`.

    public void processUSData(int distance) {
    	this.distance = distance;
    	// TODO: process a movement based on the us distance passed in (BANG-BANG style)
    	// wall on the right
    }

##Objective##
To navigate around a sequence of cinderblocks, making up a 'wall' containing gaps and both concave and convex corners, without touching it or deviating too far from it.

##Method##
