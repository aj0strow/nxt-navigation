import lejos.util.Timer;
import lejos.nxt.NXTRegulatedMotor;

/*
*  The Operator is in charge of operating the wheels of the NXT robot.
*  It assumes it starts at (0, 0) at an angle of 0. 
*
*  It is told to turn or travel to a specific location. During transit
*  it isNavigating. 
*/

public class Operator {
	private static final int ACCELERATION = 3000;
	private static final int ROTATE_SPEED = 150;
	
	private Odometer odometer;
	private boolean navigating = false;
	
	NXTRegulatedMotor leftMotor, rightMotor;
	private double radius, separation;

	public Operator(Configuration configuration) {
		this.odometer = new Odometer(configuration);
		this.leftMotor = configuration.leftMotor;
		this.rightMotor = configuration.rightMotor;
		this.radius = configuration.radius;
		this.separation = configuration.separation;
		
		Timer timer = new Timer(Odometer.PERIOD, odometer);

		timer.start();
		
		leftMotor.setAcceleration(ACCELERATION);
		rightMotor.setAcceleration(ACCELERATION);
	}
	
	public boolean isNavigating() {
		return navigating;
	}

	public void travelTo(double x, double y) {
		
	}
	
	public void turnTo(double theta) {
		this.navigating = true;
		
		double currentTheta = odometer.getTheta();
		
		double difference = theta - currentTheta;
		if (difference < -Math.PI) {
			difference += 2 * Math.PI;
		} else if (difference > Math.PI) {
			difference -= 2 * Math.PI;
		}
		rotateRight(difference);
		
		this.navigating = false;
	}
	
	private void rotateLeft(double radians) {
		setRotateSpeed();
		int amount = tachoAmount(radians);
		leftMotor.rotate(- amount, true);
		rightMotor.rotate(amount, false);
	}
	
	private void rotateRight(double radians) {
		setRotateSpeed();
		int amount = tachoAmount(radians);
		leftMotor.rotate(amount, true);
		rightMotor.rotate(- amount, false);
	}
	
	private void setRotateSpeed() {
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
	}
	
	private int tachoAmount(double radians) {
		return tachoDegrees(arcLength(radians));
	}
		
	private double arcLength(double radians) {
		return (radians / 2.0) * separation;
	}
	
	private int tachoDegrees(double arcLength) {
		return (int) ((180.0 * arcLength) / (Math.PI * radius));
	}
}