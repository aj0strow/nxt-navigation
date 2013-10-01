import lejos.util.TimerListener;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.LCD;

/*
*  the Odometer is programmed for theta = 0.0 rad corresponding to the
*  + x axis and for the angle to increase to 2π going counter-clockwise. 
*/

public class Odometer implements TimerListener {
	// 25ms is the suggested period between readings
	public static final int PERIOD = 25;
	
	private Position position;

	private double radius, separation;
	private final NXTRegulatedMotor leftMotor, rightMotor;
	
	private int leftCount, rightCount;
	
	private Object lock;
	
	// radius: wheel radius (cm)
	// separation: wheen separation from middle of tires (cm)
	public Odometer(Configuration configuration) {
		this.leftMotor = configuration.leftMotor;
		this.rightMotor = configuration.rightMotor;
		
		this.radius = configuration.radius;
		this.separation = configuration.separation;
		
		this.leftCount = 0;
		this.rightCount = 0;
		
		// theta: looking at + y axis
		this.position = new Position(0.0, 0.0, 3 * Math.PI / 2);
		this.lock = new Object();
	}
		
	public void timedOut() {
		int newLeftCount = leftMotor.getTachoCount();
		int newRightCount = rightMotor.getTachoCount();
		
		int deltaLeftCount = newLeftCount - leftCount;
		int deltaRightCount = newRightCount - rightCount;
		
		updatePosition(deltaLeftCount, deltaRightCount);
		
		this.leftCount = newLeftCount;
		this.rightCount = newRightCount;
	}
	
	// get, set, incr position
	
	public Position getPosition() {
		Position position = null;
		synchronized (lock) {
			position = this.position.clone();
		}
		return position;
	}
	
	public void setPosition(Position position) {
		synchronized (lock) {
			this.position = position.clone();
		}
	}
	
	public void incrPosition(double dx, double dy, double dtheta) {
		synchronized (lock) {
			this.position.x += dx;
			this.position.y += dy;
			this.position.theta = Angle.normalize(this.position.theta + dtheta);
		}
	}
	
	// get x, y, theta
	
	public double getX() {
		double result;
		synchronized (lock) { result = position.x; }
		return result;
	}

	public double getY() {
		double result;
		synchronized (lock) { result = position.y; }
		return result;
	}

	// 0 <= theta < 2π
	public double getTheta() {
		double result;
		synchronized (lock) { result = position.theta; }
		return result;
	}
	
	// incr x, y, theta
	
	public void incrX(double dx) {
		synchronized (lock) { this.position.x += dx; }
	}
	
	public void incrY(double dy) {
		synchronized (lock) { this.position.y += dy; }
	}
	
	public void incrTheta(double dtheta) {
		synchronized (lock) { 
			this.position.theta = Angle.normalize(position.theta + dtheta);
		}
	}
	
	
	
	private void updatePosition(int deltaLeftCount, int deltaRightCount) {
		double leftArcDistance = arcDistance(deltaLeftCount);
		double rightArcDistance = arcDistance(deltaRightCount);
			
		double dtheta = (leftArcDistance - rightArcDistance) / separation;
		double displacement = (leftArcDistance + rightArcDistance) / 2.0;
		
		double currentTheta = getTheta();
			
		double dx = displacement * Math.cos(currentTheta + dtheta / 2);
		double dy = displacement * Math.sin(currentTheta + dtheta / 2);
		
		incrPosition(dx, -dy, dtheta);
		displayPosition();
	}
	
	// tacho counts are tachometer-measured angle rotations in degrees
	private double arcDistance(int deltaTachoCount) {
		return Math.toRadians(deltaTachoCount) * radius;
	}
	
	private void displayPosition() {
		Position position = getPosition();
		
		LCD.clear();
		LCD.drawString("x: " + position.x, 0, 0);
		LCD.drawString("y: " + position.y, 0, 1);
		LCD.drawString("t: " + position.theta, 0, 2);
	}
}