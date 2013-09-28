import lejos.nxt.*;

public class Lab3 {
	
	private static double WHEEL_RADIUS = 2.8;
	private static double WHEEL_SEPARATION = 16.0;
	
	public static void main(String[] args) {
		int buttonChoice;
		
		LCD.clear();
		LCD.drawString("< Left | Right >", 0, 0);
		LCD.drawString("       |        ", 0, 1);
		LCD.drawString("Points | Block  ", 0, 2);
		
		do {
			buttonChoice = Button.waitForAnyPress();
		} while(buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
				
		if (buttonChoice == Button.ID_LEFT) {
			Operator operator = new Operator();
			
			operator.travelTo(60.0, 30.0);
			waitFor(operator);
			
			operator.travelTo(30.0, 30.0);
			waitFor(operator);
			
			operator.travelTo(30.0, 60.0);
			waitFor(operator);
			
			operator.travelTo(60.0, 0.0);
			waitFor(operator);
		}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
	
	private static void waitFor(Operator operator) {
		while (operator.isNavigating()) {
			try {
				Thread.sleep(100);
			} catch(InterruptedException e) {}
		}
	}
	
}