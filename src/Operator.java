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
	// correct the navigation every millisecond
	public static final int PERIOD = 1;
	
	// default motor acceleration / speed
	private static final int ACCELERATION = 3000;
	private static final int SPEED = 150;
	
	// when the ultrasonic poller has a distance < 26
	// the robot must avoid an imminent block
	private static final int ULTRASONIC_THRESHOLD = 26;
	
	private Odometer odometer;
	private UltrasonicPoller ultrasonicPoller;
	private boolean navigating = false;
	
	// configuration
	NXTRegulatedMotor leftMotor, rightMotor;
	private double radius, separation;
	
	private Point destination;
	private Position position;
	
	private double previousDistance;
	private double previousAngleDifference;
	
	private int avoidanceCount = 0;
	
	private int leftSpeed = SPEED, rightSpeed = SPEED;

	public Operator(Configuration configuration) {
		
		// copy values from configuration
		this.leftMotor = configuration.leftMotor;
		this.rightMotor = configuration.rightMotor;
		this.radius = configuration.radius;
		this.separation = configuration.separation;
		
		// operator needs an odometer and ultrasonic poller
		this.odometer = new Odometer(configuration);
		this.ultrasonicPoller = new UltrasonicPoller(configuration.sensorPort);
		
		// start the odometer and poller
		(new Timer(Odometer.PERIOD, odometer)).start();
		(new Timer(UltrasonicPoller.PERIOD, ultrasonicPoller)).start();
		
		leftMotor.setAcceleration(ACCELERATION);
		rightMotor.setAcceleration(ACCELERATION);
	}
	
	public void timedOut() {
		this.position = odometer.getPosition();
		
		if (isNavigating()) {
			if (arrived()) stopNavigation();
			else navigate();
		}
	}

	public void travelTo(Point point) {
		turnTo(position.angleTo(point));
		startNavigation();
		this.destination = point.clone();
	}
	
	public boolean isNavigating() {
		return destination != null;
	}
		
	private void startNavigation() {
		// a "navigation" is the journey to the destination
		
		this.avoidanceCount = 0;
		straight();
	}
	
	private void stopNavigation() {
		stop();
		this.destination = null;
	}
	
	private boolean arrived() {
		double distance = position.distanceTo(destination);
		
		boolean atDestination = distance < 1.0;
		boolean distanceWorsened = distance < 10.0 && previousDistance < distance;
		this.previousDistance = distance;
		
		return atDestination || distanceWorsened;
	}

	
	private void turnTo(double theta) {		
		double goingLeft = Angle.normalize(position.theta - theta);
		double goingRight = Angle.normalize(theta - position.theta);
		
		if (goingLeft < goingRight) rotateLeft(goingLeft);
		else rotateRight(goingRight);
	}
	
	private void navigate() {
		int ultrasonicDistance = ultrasonicPoller.getDistance();
		
		double startAngle = position.theta;
		double stopAngle = position.angleTo(destination);
		double difference = Angle.difference(startAngle, stopAngle);
		
		if (ultrasonicDistance < ULTRASONIC_THRESHOLD) {
			// AVOID BLOCK: rotate, go forward for ~ 1/2 a second
			
			sharplyRotate();
			this.avoidanceCount = (1000 / PERIOD) * 500;
		} else if (correctAngle(difference) || avoidanceCount > 0) {
			// GO STRAIGHT: reset speeds, both forward

			this.avoidanceCount --;
			straight();
		} else {
			// CORRECT PATH: +/- left and right speeds
			
			double goingLeft = Angle.normalize(startAngle - stopAngle);
			double goingRight = Angle.normalize(stopAngle - startAngle);
			if (goingLeft < goingRight) left(difference);
			else right(difference);
		}
		this.previousAngleDifference = difference;
		setMotorSpeeds();
	}
	
	private boolean correctAngle(double angleDifference) {
		// whether the robot is pointed the right direction
		
		boolean correctAngle = angleDifference < Math.PI / 200;
		boolean angleWorsened = angleDifference < Math.PI / 50 && previousAngleDifference < angleDifference;
		return correctAngle || angleWorsened;
	}
	
	private void sharplyRotate() {
		this.leftSpeed = SPEED;
		this.rightSpeed = -SPEED;
	}
	
	// 0 <= radians <= π
	private void left(double radians) {
		this.rightSpeed = SPEED + speedAmount(radians);
		this.leftSpeed = SPEED * 2 - rightSpeed;
	}
	
	// 0 <= radians <= π
	private void right(double radians) {
		this.leftSpeed = SPEED + speedAmount(radians);
		this.rightSpeed = 2 * SPEED - leftSpeed;
	}
	
	// 0 <= speedAmount <= SPEED
	private int speedAmount(double radians) {
		return (int) (SPEED * radians / Math.PI);
	}

	private void straight() {
		this.leftSpeed = SPEED;
		this.rightSpeed = SPEED;
	}
	
	private void setMotorSpeeds() {
		// actually set the motors to the desired speeds
		setMotorSpeed(leftMotor, leftSpeed);
		setMotorSpeed(rightMotor, rightSpeed);
	}
	
	private static void setMotorSpeed(NXTRegulatedMotor motor, int speed) {
		if (speed >= 0) {
			motor.setSpeed(speed);
			motor.forward();
		} else {
			motor.setSpeed(-speed);
			motor.backward();
		}
	}
	
	private void stop() {
		leftMotor.stop(true);
		rightMotor.stop(false);
	}
	
	private void rotateLeft(double radians) {
		// laterally rotate the robot the amount of radians left
		
		int amount = tachoAmount(radians);
		leftMotor.rotate(-amount, true);
		rightMotor.rotate(amount, false);
	}
	
	private void rotateRight(double radians) {
		rotateLeft(-radians);
	}
	
	private int tachoAmount(double radians) {
		// radians of lateral rotation -> degrees of wheel rotation
		
		return tachoDegrees(arcLength(radians));
	}
		
	private double arcLength(double radians) {
		// distance wheels must travel to spin the robot radians radians
		
		return (radians / 2.0) * separation;
	}
	
	private int tachoDegrees(double arcLength) {
		// amount of degrees the wheels must rotate to go arcLength distance
		
		return (int) ((180.0 * arcLength) / (Math.PI * radius));
	}
}