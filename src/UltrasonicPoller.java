import lejos.nxt.UltrasonicSensor;
import lejos.util.TimerListener;
import lejos.nxt.SensorPort;

/*
*  The Ultrasonic Poller is in charge of reading the sensor, filtering out
*  bad readings, and sending the latest distance to the controller to be
*  processed in a timed loop. 
*/

public class UltrasonicPoller implements TimerListener {
	// polling timeout (milliseconds)
	public static final int PERIOD = 10;
	
	// maximum and minimum sensor distance readings
	public static final int MIN_DISTANCE = 1;
	public static final int MAX_DISTANCE = 255;
	
	// the amount of times the filter needs to see a max reading
	// before the reading can be trusted
	private static final int FILTER_AMOUNT = 8;
	private int filterCount = 0;
	
	// whether a max reading is actually a max, or really a min
	private boolean filterToMax = true;
	
	// saved distance in case the distance from the sensor is filtered
	private int distance = MAX_DISTANCE;
	private Object lock;
	
	private UltrasonicSensor sensor;
	
	public UltrasonicPoller(SensorPort sensorPort) {
		this.sensor = new UltrasonicSensor(sensorPort);
		this.lock = new Object();
	}
	
	public void timedOut() {
		int realDistance = filter(sensor.getDistance());
		setDistance(realDistance);
	}
	
	public int getDistance() {
		int result;
		synchronized(lock) { result = distance; }
		return result;
	}
	
	/*
	* The sensor reads erroneous max distance measurements when it is
	* very close to a wall, or also occasionally for no reason.
	* 
	* To make sure the measured distance is legit, we need to make sure 
	* it shows up multiple times in a row, and also check whether it was 
	* heading towards the min or to the max distance. 
	*/
	private int filter(int sensorDistance) {
		if (sensorDistance >= MAX_DISTANCE) {
			
			// sensor reading is at an extreme, and may need to be filtered
			if (filterCount > FILTER_AMOUNT) {
				
				// sensor really is at the min / max distance
				filterCount = 0;
				return getExtremeDistance();
			} else {
				
				// extreme sensor reading cannot be trusted
				filterCount ++;
				return getDistance();
			}
		}
		
		// sensor reading is ok
		filterCount = 0;
		return sensorDistance;
	}
	
	/*
	*  A max distance reading from the sensor means an extreme distance,
	*  not necessarily a maximum. (Could be a minimum).
	*/
	private int getExtremeDistance() {
		return filterToMax ? MAX_DISTANCE : MIN_DISTANCE;
	}
	
	/*
	*  The distance must be saved in case the next reading is filtered.
	*  We keep track if the robot is headed towards the min or max to
	*  get the correct extreme distance. 
	*/
	private void setDistance(int distance) {
		this.filterToMax = distance > 12;
		synchronized(lock) { this.distance = distance; }
	}
}
