import lejos.nxt.*;
import lejos.util.Timer;

public class Lab3 {
	
	private static double WHEEL_RADIUS = 2.8;
	private static double WHEEL_SEPARATION = 16.0;
	private static NXTRegulatedMotor LEFT_MOTOR = Motor.A, RIGHT_MOTOR = Motor.B;
		
	public static void main(String[] args) {
		
		// press escape button to exit at any time
		Button.ESCAPE.addButtonListener(new ExitListener());
		
		int buttonChoice;
		
		LCD.clear();
		LCD.drawString("< Left | Right >", 0, 0);
		LCD.drawString("       |        ", 0, 1);
		LCD.drawString("Points | Block  ", 0, 2);
		
		do {
			buttonChoice = Button.waitForAnyPress();
		} while(buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
				
		Operator operator = new Operator(getConfiguration());
		(new Timer(Operator.PERIOD, operator)).start();
		
		Point[] destinations;
		if (buttonChoice == Button.ID_LEFT) {			
			destinations = new Point[] { 
					new Point(60.0, 30.0),
				   new Point(30.0, 30.0), 
					new Point(30.0, 60.0), 
					new Point(60.0, 0.0)
			};
		} else {			
			destinations = new Point[] { 
					new Point(0.0, 60.0), 
					new Point(60.0, 0.0) 
			};
		}
		
	   for (Point point : destinations) {
			operator.travelTo(point);
			waitFor(operator);
		}
		
		Button.waitForAnyPress();
	}

	private static Configuration getConfiguration() {
		Configuration config = new Configuration();
		config.leftMotor = LEFT_MOTOR;
		config.rightMotor = RIGHT_MOTOR;
		config.radius = WHEEL_RADIUS;
		config.separation = WHEEL_SEPARATION;
		config.sensorPort = SensorPort.S1;
		return config;
	}
	
	private static void waitFor(Operator operator) {
		// sleep until the operator is done navigating before setting
		// a new destination
		
		while (operator.isNavigating()) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
	}
	
}