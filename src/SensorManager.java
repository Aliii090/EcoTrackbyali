import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SensorManager {

    private final List<BuildingZone> zones;

    public SensorManager() {
        zones = new ArrayList<>();
    }

    public void addZone(BuildingZone zone) {
        zones.add(zone);
    }

    public List<BuildingZone> getZones() {
        return zones;
    }

    public void readSensorData(Scanner sc, int count) {
        System.out.println("\n[EcoTrack Pro] Reading " + count + " sensor samples...\n");

        for (BuildingZone zone : zones) {
            System.out.println("Zone: " + zone.getZoneName());

            for (int i = 0; i < count; i++) {
                System.out.println("  Sample " + (i + 1));
                try {
                    for (Map.Entry<String, Sensor> entry : zone.getSensors().entrySet()) {
                        String sensorName = entry.getKey();
                        Sensor sensor     = entry.getValue();
                        System.out.print("    " + sensorName + " (" + sensor.getUnit() + "): ");
                        double value = Double.parseDouble(sc.nextLine().trim());
                        boolean recorded = zone.recordReading(sensorName, value);
                        if (recorded) System.out.println("    [OK]  Sample recorded.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("    [ERROR] Invalid input - sample skipped.");
                }
            }
            System.out.println();
        }
    }

    public void printReport() {
        System.out.println("\n= EcoTrack Pro Report =");
        for (BuildingZone zone : zones) {
            System.out.println("\nZone: " + zone.getZoneName());
            for (Map.Entry<String, Sensor> entry : zone.getSensors().entrySet()) {
                Sensor sensor = entry.getValue();
                if (!sensor.isEmpty()) {
                    System.out.printf("  Avg %-15s: %.2f %s%n",
                        sensor.getName(), sensor.calculateAverage(), sensor.getUnit());
                }
            }
        }
        System.out.println("==========================================");
    }
}
