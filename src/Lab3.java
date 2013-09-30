import lejos.nxt.*;

public class Lab3 {
	
	private static double WHEEL_RADIUS = 2.8;
	private static double WHEEL_SEPARATION = 16.0;
	private static NXTRegulatedMotor LEFT_MOTOR = Motor.A, RIGHT_MOTOR = Motor.B;
	
	public static void main(String[] args) {
		int buttonChoice;
		
		LCD.clear();
		LCD.drawString("< Left | Right >", 0, 0);
		LCD.drawString("       |        ", 0, 1);
		LCD.drawString("Points | Block  ", 0, 2);
		
		do {
			buttonChoice = Button.waitForAnyPress();
		} while(buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
		
		Configuration configuration = configure();
		Operator operator = new Operator(configuration);
						
		if (buttonChoice == Button.ID_LEFT) {
			Point[] destinations = new Point[]{ new Point(60.0, 30.0),
				  new Point(30.0, 30.0), new Point(30.0, 60.0), new Point(60.0, 0.0) };
			
		   for (Point point : destinations) {
				operator.travelTo(point);
				waitFor(operator);
			}
		} else if (buttonChoice == Button.ID_RIGHT) {

		}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
	
	private static Configuration configure() {
		Configuration config = new Configuration();
		config.leftMotor = LEFT_MOTOR;
		config.rightMotor = RIGHT_MOTOR;
		config.radius = WHEEL_RADIUS;
		config.separation = WHEEL_SEPARATION;
		return config;
	}
	
	private static void waitFor(Operator operator) {
		while (operator.isNavigating()) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
	}
	
}