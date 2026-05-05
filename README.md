# EcoTrack Pro





EcoTrack Pro is a modular Java console application that reads environmental sensor data (temperature, humidity, CO2), stores multiple readings using ArrayList, calculates averages, and triggers warnings when thresholds are exceeded.

---

Project Structure

```
EcoTrackPro/
├── src/
│   ├── Main.java            Entry point with console menu
│   ├── Sensor.java           Stores readings and calculates averages
│   ├── BuildingZone.java     Groups sensors by location
│   ├── SensorManager.java    Manages all zones and coordinates input
│   └── WarningSystem.java    Checks thresholds and triggers warnings
├── README.md
└── .gitignore
```



How to Run in IntelliJ IDEA/ Apache

1. Open The IDE 
2. Click File then New then Project
3. Select Java, set the project SDK, and name it "EcoTrackPro"
4. Copy all .java files into the src folder
5. Rightclick Main.java then Run 'Main.main()'

