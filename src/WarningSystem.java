import java.util.List;
import java.util.Map;

public class WarningSystem {

    public static void triggerWarnings(List<BuildingZone> zones) {
        System.out.println("\n[EcoTrack Pro] Checking environmental thresholds...");
        boolean allClear = true;

        for (BuildingZone zone : zones) {
            for (Map.Entry<String, Sensor> entry : zone.getSensors().entrySet()) {
                Sensor sensor = entry.getValue();
                if (!sensor.isEmpty() && sensor.isAboveWarning()) {
                    System.out.printf("[WARNING] %s | %s avg %.2f %s exceeds threshold of %.0f %s%n",
                        zone.getZoneName(),
                        sensor.getName(),
                        sensor.calculateAverage(),
                        sensor.getUnit(),
                        sensor.getWarnThreshold(),
                        sensor.getUnit());
                    allClear = false;
                }
            }
        }

        if (allClear) System.out.println("[OK] All readings within safe limits.");
    }
}
