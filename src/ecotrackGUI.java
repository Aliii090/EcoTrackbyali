import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;


public class EcoTrackGUI extends JFrame implements ActionListener {

    private final SensorManager manager = new SensorManager();

    private final JComboBox<String> zoneSelector;

    private final JTextField tempField = new JTextField(8);
    private final JTextField co2Field  = new JTextField(8);
    private final JTextField humField  = new JTextField(8);

    private final JButton addBtn      = new JButton("Add Reading");
    private final JButton reportBtn   = new JButton("Generate Report");
    private final JButton warningBtn  = new JButton("Check Warnings");
    private final JButton clearBtn    = new JButton("Clear All");

    private final JTextArea outputArea = new JTextArea(18, 50);


    private final JLabel statusLabel = new JLabel(" Ready");

    public EcoTrackGUI() {
        super("EcoTrack Pro — Environmental Monitoring System");

        manager.addZone(new BuildingZone("Office Block A"));
        manager.addZone(new BuildingZone("Research Lab B"));

        String[] zoneNames = manager.getZones()
            .stream()
            .map(BuildingZone::getZoneName)
            .toArray(String[]::new);
        zoneSelector = new JComboBox<>(zoneNames);

        buildUI();
        wireEvents();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null); 
        setVisible(true);
    }


    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Sensor Input",
            TitledBorder.LEFT, TitledBorder.TOP));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;

        addRow(inputPanel, gbc, 0, "Zone:",          zoneSelector);
        addRow(inputPanel, gbc, 1, "Temperature (°C):", tempField);
        addRow(inputPanel, gbc, 2, "CO2 (ppm):",     co2Field);
        addRow(inputPanel, gbc, 3, "Humidity (%):",  humField);

        // ---- Button panel ----
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
       styleButton(addBtn, Color.WHITE, Color.BLACK);
       styleButton(reportBtn, Color.WHITE, Color.BLACK);
       styleButton(warningBtn, Color.WHITE, Color.BLACK);
       styleButton(clearBtn, Color.WHITE, Color.BLACK);
        btnPanel.add(addBtn);
        btnPanel.add(reportBtn);
        btnPanel.add(warningBtn);
        btnPanel.add(clearBtn);

        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        outputArea.setBackground(new Color(20, 20, 20));
        outputArea.setForeground(new Color(180, 255, 180));
        outputArea.setCaretColor(Color.GREEN);
        JScrollPane scroll = new JScrollPane(outputArea);
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Output Console",
            TitledBorder.LEFT, TitledBorder.TOP));

        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());

        JPanel topPanel = new JPanel(new BorderLayout(0, 8));
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(btnPanel,   BorderLayout.SOUTH);

        add(topPanel,    BorderLayout.NORTH);
        add(scroll,      BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row,
                        String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

  
    private void wireEvents() {
        addBtn.addActionListener(this);
        reportBtn.addActionListener(this);
        warningBtn.addActionListener(this);
        clearBtn.addActionListener(this);

        zoneSelector.addItemListener(e -> {
            setStatus("Zone switched to: " + zoneSelector.getSelectedItem());
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == addBtn)     handleAddReading();
        else if (source == reportBtn)  handleGenerateReport();
        else if (source == warningBtn) handleCheckWarnings();
        else if (source == clearBtn)   handleClear();
    }

     private void handleAddReading() {
        String zoneName = (String) zoneSelector.getSelectedItem();
        BuildingZone zone = getZoneByName(zoneName);
        if (zone == null) return;

        double temp, co2, hum;
        try {
            temp = Double.parseDouble(tempField.getText().trim());
            co2  = Double.parseDouble(co2Field.getText().trim());
            hum  = Double.parseDouble(humField.getText().trim());
        } catch (NumberFormatException ex) {
            log("[ERROR] Please enter valid numbers in all fields.");
            setStatus("Invalid input — check your values.");
            return;
        }

        log("--- Adding sample to: " + zoneName + " ---");
        boolean t = zone.recordReading("Temperature", temp);
        boolean c = zone.recordReading("CO2",         co2);
        boolean h = zone.recordReading("Humidity",    hum);

        if (t) log("  Temperature: " + temp + " C  [OK]");
        if (c) log("  CO2:         " + co2 + " ppm [OK]");
        if (h) log("  Humidity:    " + hum + " %   [OK]");

        setStatus("Reading added to " + zoneName);
        tempField.setText(""); co2Field.setText(""); humField.setText("");
        tempField.requestFocus();
    }

    private void handleGenerateReport() {
        log("\n EcoTrack Pro Report ");
        boolean anyData = false;
        for (BuildingZone zone : manager.getZones()) {
            log("\nZone: " + zone.getZoneName());
            for (Map.Entry<String, Sensor> entry : zone.getSensors().entrySet()) {
                Sensor sensor = entry.getValue();
                if (!sensor.isEmpty()) {
                    anyData = true;
                    log(String.format("  Avg %-15s: %.2f %s",
                        sensor.getName(), sensor.calculateAverage(), sensor.getUnit()));
                }
            }
        }
        if (!anyData) log("  No readings recorded yet.");
        log("=======\n");
        setStatus("Report generated.");
    }

    private void handleCheckWarnings() {
        log("\n[EcoTrack Pro] Checking environmental thresholds...");
        boolean allClear = true;
        for (BuildingZone zone : manager.getZones()) {
            for (Map.Entry<String, Sensor> entry : zone.getSensors().entrySet()) {
                Sensor sensor = entry.getValue();
                if (!sensor.isEmpty() && sensor.isAboveWarning()) {
                    log(String.format("[WARNING] %s | %s avg %.2f %s exceeds threshold of %.0f %s",
                        zone.getZoneName(), sensor.getName(),
                        sensor.calculateAverage(), sensor.getUnit(),
                        sensor.getWarnThreshold(), sensor.getUnit()));

                    JOptionPane.showMessageDialog(this,
                        zone.getZoneName() + "\n" + sensor.getName() +
                        " average: " + String.format("%.2f", sensor.calculateAverage()) +
                        " " + sensor.getUnit() + "\nThreshold: " + sensor.getWarnThreshold(),
                        "⚠ Environmental Warning",
                        JOptionPane.WARNING_MESSAGE);
                    allClear = false;
                }
            }
        }
        if (allClear) log("[OK] All readings within safe limits.");
        setStatus("Warning check complete.");
    }

    private void handleClear() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Clear all recorded readings and reset the form?",
            "Confirm Clear", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

    
        manager.getZones().clear();
        manager.addZone(new BuildingZone("Office Block A"));
        manager.addZone(new BuildingZone("Research Lab B"));

        tempField.setText(""); co2Field.setText(""); humField.setText("");
        outputArea.setText("");
        log("[EcoTrack Pro] All data cleared. Ready for new readings.");
        setStatus("Cleared.");
    }


    private void log(String message) {
        outputArea.append(message + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    private void setStatus(String msg) {
        statusLabel.setText("  " + msg);
    }

    private BuildingZone getZoneByName(String name) {
        for (BuildingZone z : manager.getZones())
            if (z.getZoneName().equals(name)) return z;
        return null;
    }

     public static void main(String[] args) {
        SwingUtilities.invokeLater(EcoTrackGUI::new);
    }
}