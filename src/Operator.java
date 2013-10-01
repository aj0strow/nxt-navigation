import lejos.util.Timer;
import lejos.util.TimerListener;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;

/*
*  The Operator is in charge of operating the wheels of the NXT robot.
*  It assumes it starts at (0, 0) at an angle of 0. 
*
*  It is told to turn or travel to a specific location. During transit
*  it isNavigating. 
*/

public class Operator implements TimerListener {
	public static final int PERIOD = 1;
	
	private static final int ACCELERATION = 3000;
	private static final int ROTATE_SPEED = 150;
	
	private Odometer odometer;
	private UltrasonicPoller ultrasonicPoller;
	private boolean navigating = false;
	
	NXTRegulatedMotor leftMotor, rightMotor;
	private double radius, separation;
	
	private Point destination;
	private Position position;
	private double previousDistance;
	private double previousAngleDifference;
	
	private int avoidanceCount = 0;
	
	private int leftSpeed, rightSpeed;

	public Operator(Configuration configuration) {
		this.leftMotor = configuration.leftMotor;
		this.rightMotor = configuration.rightMotor;
		this.radius = configuration.radius;
		this.separation = configuration.separation;
		
		this.odometer = new Odometer(configuration);
		this.ultrasonicPoller = new UltrasonicPoller(configuration.sensorPort);
		
		Timer odometerTimer = new Timer(Odometer.PERIOD, odometer);
		Timer pollerTimer = new Timer(UltrasonicPoller.PERIOD, ultrasonicPoller);
		
		
		odometerTimer.start();
		pollerTimer.start();
		
		leftMotor.setAcceleration(ACCELERATION);
		rightMotor.setAcceleration(ACCELERATION);
		
		resetSpeeds();
	}
	
	public void timedOut() {
		this.position = odometer.getPosition();
		
		if (isNavigating()) {
			if (arrived()) {
				stopNavigation();
			} else {
				navigate();
			}
		}
	}
	
	public boolean isNavigating() {
		return destination != null;
	}
	
	private boolean arrived() {
		double distance = position.distanceTo(destination);
		
		boolean atDestination = distance < 1.0;
		boolean distanceWorsened = distance < 10.0 && previousDistance < distance;
		this.previousDistance = distance;
		
		return atDestination || distanceWorsened;
	}

	public void travelTo(Point point) {
		this.position = odometer.getPosition();
		turnTo(position.angleTo(point));
		startNavigation();
		this.destination = point.clone();
	}
	
	private void turnTo(double theta) {
		double currentTheta = odometer.getTheta();
		
		double goingLeft = Angle.normalize(currentTheta - theta);
		double goingRight = Angle.normalize(theta - currentTheta);
		
		if (goingLeft < goingRight) {
			rotateLeft(goingLeft);
		} else {
			rotateRight(goingRight);
		}
	}
	
	private void navigate() {
		int ultrasonicDistance = ultrasonicPoller.getDistance();
		
		double startAngle = position.theta;
		double stopAngle = position.angleTo(destination);
			
		double difference = Angle.difference(startAngle, stopAngle);
		
		if (ultrasonicDistance < 26) {
			sharplyRight();
			this.avoidanceCount = 500;
		} else {
			if (correctAngle(difference) || avoidanceCount > 0) {
				this.avoidanceCount --;
				resetSpeeds();
			} else {
				double goingLeft = Angle.normalize(startAngle - stopAngle);
				double goingRight = Angle.normalize(stopAngle - startAngle);
				if (goingLeft < goingRight) {
					left(difference);
				} else {
					right(difference);
				}
			}			
		}
		this.previousAngleDifference = difference;
		handleMotors();
	}
	
	private boolean correctAngle(double angleDifference) {
		boolean correctAngle = angleDifference < Math.PI / 200;
		boolean angleWorsened = angleDifference < Math.PI / 50 && previousAngleDifference < angleDifference;
		return correctAngle || angleWorsened;
	}
	
	private void sharplyRight() {
		this.leftSpeed = ROTATE_SPEED;
		this.rightSpeed = ROTATE_SPEED;
		rightMotor.backward();
	}
	
	private void left(double angle) {
		int maxSpeed = ROTATE_SPEED * 2;
		
		if (angle > Math.PI / 2) {
			this.rightSpeed = ROTATE_SPEED + ROTATE_SPEED / 2;
		} else if (angle > Math.PI / 4) {
			this.rightSpeed = ROTATE_SPEED + ROTATE_SPEED / 4;
		} else if (angle > Math.PI / 8) {
			this.rightSpeed = ROTATE_SPEED + ROTATE_SPEED / 8;
		} else {
			this.rightSpeed = ROTATE_SPEED + 3;
		}
		this.leftSpeed = maxSpeed - rightSpeed;
		forward();
	}
	
	private void right(double angle) {
		this.leftSpeed = ROTATE_SPEED + 5;
		this.rightSpeed = ROTATE_SPEED - 5;
		forward();
	}

	private void startNavigation() {
		resetSpeeds();
		// handleMotors();
	}
	
	private void resetSpeeds() {
		this.leftSpeed = ROTATE_SPEED;
		this.rightSpeed = ROTATE_SPEED;
		forward();
	}
	
	private void handleMotors() {
		leftMotor.setSpeed(leftSpeed);
		rightMotor.setSpeed(rightSpeed);
	}
	
	private void forward() {
		leftMotor.forward();
		rightMotor.forward();
	}
	
	private void stopNavigation() {
		stop();
		this.destination = null;
	}
	
	private void stop() {
		leftMotor.stop(true);
		rightMotor.stop(false);
	}

	
	
	private void rotateLeft(double radians) {
		int amount = tachoAmount(radians);
		leftMotor.rotate(-amount, true);
		rightMotor.rotate(amount, false);
	}
	
	private void rotateRight(double radians) {
		rotateLeft(-radians);
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