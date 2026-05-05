import java.util.ArrayList;
import java.util.List;

public class Sensor {

    private final String name;
    private final String unit;
    private final double minValid;
    private final double maxValid;
    private final double warnThreshold;
    private final List<Double> readings;

    public Sensor(String name, String unit, double minValid, double maxValid, double warnThreshold) {
        this.name          = name;
        this.unit          = unit;
        this.minValid      = minValid;
        this.maxValid      = maxValid;
        this.warnThreshold = warnThreshold;
        this.readings      = new ArrayList<>();
    }

    public boolean addReading(double value) {
        if (value < minValid || value > maxValid) {
            System.out.println("[SKIP] " + name + " out of range (" + minValid + " to " + maxValid + " " + unit + ").");
            return false;
        }
        readings.add(value);
        return true;
    }

    public double calculateAverage() {
        if (readings.isEmpty()) return 0.0;
        double sum = 0;
        for (double v : readings) sum += v;
        return sum / readings.size();
    }

    public boolean isAboveWarning() {
        return calculateAverage() > warnThreshold;
    }

    public String getName()          { return name; }
    public String getUnit()          { return unit; }
    public double getWarnThreshold() { return warnThreshold; }
    public List<Double> getReadings(){ return readings; }
    public boolean isEmpty()         { return readings.isEmpty(); }
}
