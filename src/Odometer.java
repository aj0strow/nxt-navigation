import lejos.util.TimerListener;
import lejos.nxt.NXTRegulatedMotor;

/*
*  the Odometer is programmed for theta = 0.0 rad corresponding to the
*  + x axis and for the angle to increase to 2π going counter-clockwise. 
*/

public class Odometer implements TimerListener {
	// 25ms is the suggested period between readings
	public static final int PERIOD = 25;
	
	private double x, y, theta;
	private double radius, separation;
	private final NXTRegulatedMotor leftMotor, rightMotor;
	
	private Object lock;
	
	// radius: wheel radius (cm)
	// separation: wheen separation from middle of tires (cm)
	public Odometer(NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor, double radius, double separation) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.radius = radius;
		this.separation = separation;
		this.lock = new Object();
	}
		
	public void timedOut() {
		
		/*			
		int newLeftCount = leftMotor.getTachoCount();
		int newRightCount = rightMotor.getTachoCount();
						
		int deltaLeftCount = newLeftCount - leftCount;
		int deltaRightCount = newRightCount - rightCount;
			
		leftCount = newLeftCount;
		rightCount = newRightCount;
			
		double leftArcDistance = arcDistance(deltaLeftCount);
		double rightArcDistance = arcDistance(deltaRightCount);
			
		double deltaTheta = (leftArcDistance - rightArcDistance) / separation;
		double displacement = (leftArcDistance + rightArcDistance) / 2.0;
			
		double currentTheta = getTheta();
			
		double deltaX = displacement * Math.cos(currentTheta + deltaTheta / 2);
		double deltaY = displacement * Math.sin(currentTheta + deltaTheta / 2);
			
		setX(getX() + deltaX);
		setY(getY() + deltaY);
		setTheta(currentTheta + deltaTheta);
		*/		
	}
	
	public double[] getPosition() {
		double[] position;
		synchronized (lock) {
			position = new double[]{ x, y, theta };
		}
		return position;
	}

	public double getX() {
		double result;
		synchronized (lock) { result = x; }
		return result;
	}

	public double getY() {
		double result;
		synchronized (lock) { result = y; }
		return result;
	}

	// 0 <= theta < 2π
	public double getTheta() {
		double result;
		synchronized (lock) { result = theta; }
		return result;
	}
	
	public void setX(double x) {
		synchronized (lock) { this.x = x; }
	}

	public void setY(double y) {
		synchronized (lock) { this.y = y; }
	}
	
	public void setTheta(double theta) {
		synchronized (lock) { this.theta = normalize(theta); }
	}
	
	private static double normalize(double angle) {
	   double normalized = angle % (2 * Math.PI);
	   if (normalized < 0) normalized += 2 * Math.PI;
	   return normalized;
	}
	
}