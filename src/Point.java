public class Point {
	public double x;
	public double y;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double distanceTo(Point point) {
		double dx = point.x - x, dy = point.y - y;
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	public double angleTo(Point point) {
		double arctan = Math.atan2(point.y - y, point.x - x);
		return 2 * Math.PI - Angle.normalize(arctan);
	}
	
	public Point clone() {
		return new Point(x, y);
	}
}