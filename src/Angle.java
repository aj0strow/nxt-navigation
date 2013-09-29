public class Angle {
	
	public static double normalize(double angle) {
	   double normalized = angle % (2 * Math.PI);
	   if (normalized < 0) normalized += 2 * Math.PI;
	   return normalized;
	}
}