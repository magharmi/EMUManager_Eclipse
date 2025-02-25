
package Messgeraet.src.JavaFX;

import Messgeraet.src.CSVExport.CSVExport;
import Messgeraet.src.Charts.BarChartClass;
import Messgeraet.src.Charts.LineChart;
import Messgeraet.src.Charts.TableView;
import Messgeraet.src.Emu.EmuConnection;
import Messgeraet.src.Emu.EmuModel;
import Messgeraet.src.Emu.ReturnManager;
import Messgeraet.src.Models.Database.DatabaseModel;
import Messgeraet.src.Models.Gauge.GaugeModel;
import Messgeraet.src.Models.Properties.PropertiesModel;
import com.emu.backend.entities.*;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Section;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.sf.yad2xx.FTDIException;
import javafx.beans.*;

import java.io.IOException;
import java.util.*;

import org.json.JSONException;

public class FXController {
    EmuModel emuModel = EmuModel.getInstance();
    DatabaseModel databaseModel = DatabaseModel.getInstance();
    PropertiesModel propertiesModel = PropertiesModel.getInstance();

    static FXController fxController;

    public FXController(){}

    public static FXController getInstance()
    {
        if (fxController == null)
            fxController = new FXController();

        return fxController;
    }

    String uebergabe = "";

    boolean connected = false;
    public static boolean requestDone = false;
    public List<CheckBox> checkboxList = new ArrayList<>();
    public HashMap<String, List<Messdaten>> devices;
    public List<String> selectedDevices = new ArrayList<>();

    @FXML
    private Button btnConnect, btnProgrammingMode, btnPower, btnMeasurementTime, btnWorkInMeasurementTime,
            btnComplete, btnSaveLoadGear, btnLoadLoadGear, btnLoadGear, btnTableView, btnLineChart, btnBarChart,
            btnLoadDevices, btnExportCSV, btnCompareSetpoints;
    @FXML
    public VBox vBoxDevices;
    @FXML
    public Gauge gaugePower, gaugeWork;


    @FXML
    public void loadDevices() {
        btnLoadDevices.setDisable(true);
        checkboxList.clear();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ger�te laden");
        alert.setHeaderText(null);
        alert.setContentText("Ger�te werden aus der Datenbank geladen.");

        alert.show();

        Task<HashMap<String, List<Messdaten>>> task = new Task() {
            @Override
            public HashMap<String, List<Messdaten>> call() throws IOException, JSONException {
                return databaseModel.loadAllMeasurementData();
            }
        };

        task.setOnSucceeded(e -> {
            devices = task.getValue();

            for (Map.Entry<String, List<Messdaten>> entry : devices.entrySet()) {
                CheckBox checkBox = new CheckBox(entry.getValue().get(0).typ + " (ID: " + entry.getKey() + ")");
                checkBox.setId(entry.getKey());
                checkboxList.add(checkBox);
            }

            vBoxDevices.getChildren().clear();
            vBoxDevices.getChildren().addAll(checkboxList);

            btnTableView.setDisable(false);
            btnLineChart.setDisable(false);
            btnBarChart.setDisable(false);
            btnExportCSV.setDisable(false);
            btnLoadDevices.setDisable(false);
        });
        new Thread(task).start();
    }

    @FXML
    protected void connect() throws FTDIException, IOException {
        if (!connected) {
            emuModel.connect();
            btnConnect.setText("Disconnect");
            btnProgrammingMode.setDisable(false);
            connected = true;
            showAlert(Alert.AlertType.INFORMATION, "Verbindung zu EMU herstellen", "", "Verbindung zum EMU-Messger�t wurde hergestellt.");
        } else {
            emuModel.disconnect();
            btnConnect.setText("Connect");
            btnProgrammingMode.setDisable(true);
            connected = false;
            showAlert(Alert.AlertType.INFORMATION, "Verbindung zu EMU trennen", "", "Verbindung zum EMU-Messger�t wurde getrennt.");
        }
    }

    @FXML
    protected void programmingMode() throws FTDIException {
        emuModel.startProgrammingMode();
        btnWorkInMeasurementTime.setDisable(false);
        btnMeasurementTime.setDisable(false);
        btnPower.setDisable(false);
        btnLoadLoadGear.setDisable(false);
        btnLoadGear.setDisable(false);
        btnComplete.setDisable(false);
        showAlert(Alert.AlertType.INFORMATION, "Programmiermodus gestartet", "", "Der Programmiermodus wurde gestartet.");
    }

    @FXML
    protected void getMeasurementTime() throws FTDIException {
        emuModel.readMeasurementTime();
        showAlert(Alert.AlertType.INFORMATION, "Messzeitdauer", "", emuModel.processAndPutOutOuput("Tage"));
    }

    @FXML
    protected void getWorkInMeasurementTime() throws FTDIException {
        emuModel.readWorkInMeasurementTime();
        showAlert(Alert.AlertType.INFORMATION, "Arbeit im Messzeitraum", "", emuModel.processAndPutOutOuput("kWh"));
    }

    @FXML
    protected void getPower() throws FTDIException {
        emuModel.readPower();
        showAlert(Alert.AlertType.INFORMATION, "Momentane Wirkleistung", "", emuModel.processAndPutOutOuput("Watt"));
    }

    @FXML
    protected void getLoadGear() throws FTDIException {
        showAlert(Alert.AlertType.INFORMATION, "Lastgang auslesen", "", "Der Lastgang wird ausgelesen. Dies kann je nach eingestellter Messzeitdauer und Intervall bis zu sechs Stunden dauern.");
        emuModel.readLoadGear();
        emuModel.processAndPutOutOuput("Last");
        btnSaveLoadGear.setDisable(false);
        showAlert(Alert.AlertType.INFORMATION, "Lastgang auslesen", "", "Der Lastgang wurde erfolgreich ausgelesen.");
    }

    @FXML
    protected void getComplete() throws FTDIException {
        emuModel.readCompleteMeasurementData();
    }

    @FXML
    protected void saveLoadGear() throws IOException {
        ReturnManager returnManager = new ReturnManager();
        System.out.println(returnManager.getMessdaten());
        databaseModel.saveLoadGear(returnManager.getMessdaten());
        showAlert(Alert.AlertType.INFORMATION, "Lastgang speichern", "", "Der Lastgang wurde erfolgreich in der Datenbank gespeichert.");
    }

    @FXML
    protected void loadLoadGear() throws IOException, JSONException {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Messdaten aus Datenbank laden");
        dialog.setHeaderText("");
        dialog.setContentText("Bitte geben Sie die Ger�teID ein:");

        Optional<String> deviceIdInput = dialog.showAndWait();

        databaseModel.loadMeasurementData(deviceIdInput.get());    // Messdaten je nach ID laden
        showAlert(Alert.AlertType.INFORMATION, "Lastgang laden", "", "Der Lastgang wurde aus der Datenbank geladen.");
    }

    @FXML
    protected void showTableView() {
        setSelectedDevices();
        TableView tableView = new TableView(selectedDevices, devices);
        tableView.loadTableView();
    }

    @FXML
    protected void showLineChart() {
        setSelectedDevices();
        LineChart lineChart = new LineChart(selectedDevices, devices);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    lineChart.loadLinechart();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    protected void showBarChart() {
        setSelectedDevices();
        BarChartClass barChart = new BarChartClass(selectedDevices, devices);
        barChart.loadBarChart();
    }

    @FXML
    protected void compareSetpoint() throws IOException {
        Thread thread;
        thread = new Thread() {
            public void run() {
                btnCompareSetpoints.setDisable(true);
                setSelectedDevices();

                gaugePower.setSectionsVisible(true);
                gaugeWork.setSectionsVisible(true);
                gaugePower.setAnimated(true);
                gaugeWork.setAnimated(true);
                gaugePower.setAnimationDuration(3000);
                gaugePower.setAnimationDuration(3000);

                GaugeModel gaugeModel = new GaugeModel();
                try {
                    gaugeModel.compareSetpoint(selectedDevices, devices, gaugePower, gaugeWork);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                btnCompareSetpoints.setDisable(false);
            }
        };
        thread.start();
    }

    public static void setGauge(double isValue, double shouldValue, Gauge gauge) {
        List<Section> sections = new ArrayList<>();
        GaugeModel gaugeModel = new GaugeModel();

        double diffValue = (isValue / shouldValue) * 100;

        if (diffValue > 100) {
            int maxValue = (int) gaugeModel.roundUpTen(diffValue);   // n�chstgr��ter Zehner von diffValue als maxValue
            gauge.setMaxValue(maxValue);
            gauge.setUnit("100% = " + shouldValue);
            gauge.setMajorTickSpace(maxValue / 10); // Anzahl der Skalawerte
            // Sektionen mit Farben f�llen (gr�n zu rot)
            for (int i = 0; i < maxValue; i++) {
                sections.add(new Section(i, i + 1, gaugeModel.getColor(i, maxValue)));
            }
        } else {
            gauge.setMaxValue(100);
            gauge.setUnit("100% = " + shouldValue);
            gauge.setMajorTickSpace(100 / 10); // Anzahl der Skalawerte
            // Sektionen mit Farben f�llen (gr�n zu rot)
            for (int i = 0; i < 100; i++) {
                sections.add(new Section(i, i + 1, gaugeModel.getColor(i, 100)));
            }
        }

        gauge.setSections(sections);
        gauge.setValue(diffValue);
        gauge.setValueColor(gauge.getSections().get((int) diffValue).getColor());
        gauge.setBarColor(gauge.getSections().get((int) diffValue).getColor());
    }

    @FXML
    protected void exportCSV() {
        setSelectedDevices();

        CSVExport csvExport = new CSVExport();
        csvExport.export(selectedDevices, devices);
    }

    @FXML
    protected void setSetpoint() {
        DialogCreator dialogCreator = new DialogCreator("Schwellwerte eintragen");

        if (dialogCreator.getDialog() != null) {
            String shouldPower = dialogCreator.getVariables().get("watt");
            String shouldWork = dialogCreator.getVariables().get("kilowattstunde");
            String shouldAmperage = dialogCreator.getVariables().get("ampere");
            String shouldVoltage = dialogCreator.getVariables().get("volt");
            String shouldInductiveReactivePower = dialogCreator.getVariables().get("induktiveBlindleistung");
            String shouldCapacitiveReactivePower = dialogCreator.getVariables().get("kapazitiveBlindleistung");

            if (!shouldPower.equals("") || !shouldWork.equals("") || !shouldAmperage.equals("") || !shouldVoltage.equals("") || !shouldInductiveReactivePower.equals("") || !shouldCapacitiveReactivePower.equals("")) {
                propertiesModel.saveSetpoints(shouldPower, shouldWork, shouldAmperage, shouldVoltage, shouldInductiveReactivePower, shouldCapacitiveReactivePower);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Erfolgreich gespeichert");
                alert.setHeaderText("Schwellwerte wurden erfolgreich lokal abgespeichert!");
                alert.setContentText("Watt:\t" + shouldPower + "\nKilowattstunde:\t" + shouldWork + "\nAmpere:\t" + shouldAmperage + "\nVolt:\t\t" + shouldVoltage + "\nInduktive Blindleistung:\t" + shouldInductiveReactivePower + "\nKapazitive Blindleistung:\t" + shouldCapacitiveReactivePower);

                alert.showAndWait();
            } else {
                showAlert(Alert.AlertType.ERROR, "Fehler beim Speichern", "Schwellwert konnte nicht gespeichert werden!", "Es muss mindestens ein Schwellwert eingetragen sein.");
            }
        }
    }

    public void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    @FXML
    protected void exit() throws FTDIException {
        if (connected == true) {
            emuModel.disconnect();
            btnConnect.setText("Connect");
            connected = false;
        }
        System.exit(0);
    }

    public void setSelectedDevices() {
        selectedDevices.clear();
        for (CheckBox checkBox : checkboxList) {
            if (checkBox.isSelected()) {
                selectedDevices.add(checkBox.getId());
            }
        }
    }
}