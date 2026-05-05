import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        SensorManager manager = new SensorManager();
        manager.addZone(new BuildingZone("Office Block A"));
        manager.addZone(new BuildingZone("Research Lab B"));

        System.out.print("[EcoTrack Pro] How many samples to read per zone? ");
        int count = 0;
        try {
            count = Integer.parseInt(sc.nextLine().trim());
            if (count <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid sample count. Exiting.");
            return;
        }

        manager.readSensorData(sc, count);
        manager.printReport();
        WarningSystem.triggerWarnings(manager.getZones());

        sc.close();
    }
}
