package Messgeraet.src.Charts;

import Messgeraet.src.JavaFX.DialogCreator;
import Messgeraet.src.JavaFX.FXController;
import com.emu.backend.entities.*;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BarChartClass {
    public FXController fxController = FXController.getInstance();
    private List<String> selectedDevices;
    private HashMap<String, List<Messdaten>> devices;

    public BarChartClass(List<String> selectedDevices, HashMap<String, List<Messdaten>> devices) {
        this.selectedDevices = selectedDevices;
        this.devices = devices;
    }

    public void loadBarChart() {
        final CategoryAxis typeAxisPower = new CategoryAxis();
        final NumberAxis powerAxis = new NumberAxis();
        typeAxisPower.setLabel("Typ");
        powerAxis.setLabel("Watt");
        final CategoryAxis typeAxisWork = new CategoryAxis();
        final NumberAxis workAxis = new NumberAxis();
        typeAxisWork.setLabel("Typ");
        workAxis.setLabel("KwH");
        final javafx.scene.chart.BarChart<String, Number> barChartWork = new javafx.scene.chart.BarChart<>(typeAxisPower, powerAxis);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        Stage stage = new Stage();
        stage.setTitle("Balkendiagramm");
        HashMap<String, Date[]> deviceTimes = new HashMap<>();
        DialogCreator dialogCreator = new DialogCreator();

        if (selectedDevices.size() > 0) {
            int duration = dialogCreator.getDurationDialog();
            if (duration == -1) {
                return;
            }
            for (int j = 0; j < selectedDevices.size(); j++) {
                String typ = devices.get(selectedDevices.get(j)).get(0).typ;
                String id = devices.get(selectedDevices.get(j)).get(0).geraetId;
                dialogCreator = new DialogCreator("Balkendiagramm", typ, duration);
                dialogCreator.setId(id);

                // load dialog and get date (from/to) of device
                deviceTimes = (HashMap<String, Date[]>) dialogCreator.getDialog();

                if (deviceTimes != null) {
                    double tempWork = 0;
                    for (Map.Entry<String, List<Messdaten>> entry : devices.entrySet()) {
                        XYChart.Series workSeries = new XYChart.Series();
                        workSeries.setName("Wattstunde");
                        String geraetID = entry.getKey();
                        typ = entry.getValue().get(0).typ;
                        if (geraetID.equals(id)) {
                            for (int i = 0; i < entry.getValue().size(); i++) {
                                Date date = new Date();
                                String timestamp = entry.getValue().get(i).timestamp;
                                try {
                                    date = sdf.parse(timestamp);
                                } catch (Exception e) {
                                }
                                if (date.after(deviceTimes.get(id)[0]) && date.before(deviceTimes.get(id)[1])) {
                                    // Berechne kWh = Leistung (Watt) * 3 Minuten in Stunden
                                    tempWork += (Double.parseDouble(entry.getValue().get(i).watt) * 0.05) / 1000;
                                    workSeries.setName(entry.getValue().get(i).typ + "\nStartdatum: " + deviceTimes.get(id)[0] + "\nEnddatum: " + deviceTimes.get(id)[1]);
                                }
                            }
                            workSeries.getData().add(new XYChart.Data<>(typ, tempWork));
                            barChartWork.getData().add(workSeries);
                        }
                    }
                } else {
                    return;
                }
            }
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            barChartWork.setPrefWidth(primaryScreenBounds.getWidth() - 200);
            barChartWork.setPrefHeight(primaryScreenBounds.getHeight() - 100);

            Scene scene = new Scene(barChartWork, primaryScreenBounds.getWidth() - 200, primaryScreenBounds.getHeight() - 100);

            stage.setScene(scene);
            stage.show();
        } else {
            Platform.runLater(() -> FXController.getInstance().showAlert(Alert.AlertType.ERROR, "Fehler!", "Balkendiagramm konnte nicht angezeigt werden!", "Es muss mindestens ein Ger�t ausgew�hlt werden."));
        }
    }
}
