import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.ADSensorPort;
import lejos.nxt.SensorPort;

/*
*  best attempt at Java key / value parameters for configuration
*/

public class Configuration {
	public NXTRegulatedMotor leftMotor, rightMotor;
	public double radius, separation;
	public SensorPort sensorPort;
	
	public Configuration() {}
}