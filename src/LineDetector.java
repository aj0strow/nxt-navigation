import lejos.nxt.Sound;
import lejos.nxt.LightSensor;
import lejos.util.TimerListener;


public class LineDetector implements TimerListener {
	private static final long PERIOD = 10;
	private static final int LIGHT_THRESHOLD = 400;
	private static final double LINE_SEPARATION = 30.0;
	private static final double SENSOR_DISPLACEMENT = 11.6;
	
	private final LightSensor lightSensor;
	
	private boolean wasLine = false;
	
	public LineDetector(LightSensor lightSensor) {
		this.lightSensor = lightSensor;
	}
	
	public void timedOut() {
		boolean isLine = lightSensor.readNormalizedValue() < LIGHT_THRESHOLD;
		if (isLine && !wasLine) {
			Sound.twoBeeps();
			// report line
		}
		wasLine = isLine;
	}
}