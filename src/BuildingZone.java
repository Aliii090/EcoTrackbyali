import java.util.LinkedHashMap;
import java.util.Map;

public class BuildingZone {

    private final String zoneName;
    private final Map<String, Sensor> sensors;

    public BuildingZone(String zoneName) {
        this.zoneName = zoneName;
        this.sensors  = new LinkedHashMap<>();

        sensors.put("Temperature", new Sensor("Temperature", "C",   -50.0, 60.0,   35.0));
        sensors.put("CO2",         new Sensor("CO2",         "ppm",  300,  5000, 1000.0));
        sensors.put("Humidity",    new Sensor("Humidity",    "%",      0,   100,   80.0));
    }

    public boolean recordReading(String sensorName, double value) {
        Sensor sensor = sensors.get(sensorName);
        if (sensor == null) {
            System.out.println("[ERROR] Unknown sensor: " + sensorName);
            return false;
        }
        return sensor.addReading(value);
    }

    public Map<String, Sensor> getSensors() { return sensors; }
    public String getZoneName()             { return zoneName; }
}
