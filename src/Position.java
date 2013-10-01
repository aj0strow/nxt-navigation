/*
*  Position is a Point with an angle (theta).
*/

public class Position extends Point {
	public double theta;
	
	public Position(double x, double y, double theta) {
		super(x, y);
		this.theta = theta;
	}
	
	public Position(Point point, double theta) {
		this(point.x, point.y, theta);
	}
	
	public Position clone() {
		return new Position(x, y, theta);
	}
}