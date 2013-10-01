public class Angle {
	public static double normalize(double angle) {
	   double normalized = angle % (2 * Math.PI);
	   if (normalized < 0) normalized += 2 * Math.PI;
	   return normalized;
	}
	
	public static double difference(double startAngle, double stopAngle) {
	   if (stopAngle < startAngle) stopAngle += 2 * Math.PI;
	   return stopAngle - startAngle;
	}
	
	public static double between(double startAngle, double stopAngle) {
	   return normalize(startAngle + difference(startAngle, stopAngle) / 2);
	}
	
	public static int direction(double startAngle, double stopAngle) {
		double goingLeft = normalize(startAngle - stopAngle);
		double goingRight = normalize(stopAngle - startAngle);
		return goingLeft < goingRight ? -1 : 1;
	}
}