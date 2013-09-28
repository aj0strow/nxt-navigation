import lejos.util.Timer;

/*
*  The Operator is in charge of operating the wheels of the NXT robot.
*  It assumes it starts at (0, 0) at an angle of 0. 
*
*  It is told to turn or travel to a specific location. During transit
*  it isNavigating. 
*/

public class Operator {
	private Odometer odometer;

	public Operator() {
		Timer timer = new Timer(Odometer.PERIOD, odometer);
		
		
		
		// timer.start();
	}

	public void travelTo(double x, double y) {
		
	}
	
	public void turnTo(double theta) {
		
	}
	
	public boolean isNavigating() {
		return false;
	}
}