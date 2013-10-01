import lejos.nxt.ButtonListener;
import lejos.nxt.Button;

public class ExitListener implements ButtonListener {
	
	public ExitListener() {}
	
	public void buttonPressed(Button button) {
		System.exit(0);
	}
	
	public void buttonReleased(Button button) {}
}