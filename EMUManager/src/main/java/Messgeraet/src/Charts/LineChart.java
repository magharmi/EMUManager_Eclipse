package Messgeraet.src.Charts;

import Messgeraet.src.JavaFX.DialogCreator;
import Messgeraet.src.JavaFX.FXController;
import com.emu.backend.entities.*;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.*;

public class LineChart implements Runnable {

    public FXController fxController = FXController.getInstance();
    private List<String> selectedDevices;
    private HashMap<String, List<Messdaten>> devices;

    public LineChart(List<String> selectedDevices, HashMap<String, List<Messdaten>> devices) {
        this.selectedDevices = selectedDevices;
        this.devices = devices;
    }

    public void run() {
        try {
            loadLinechart();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void loadLinechart() throws InterruptedException {
        final CategoryAxis typeAxisPower = new CategoryAxis();
        final NumberAxis powerAxis = new NumberAxis();
        final CategoryAxis typeAxisAmperage = new CategoryAxis();
        final NumberAxis amperageAxis = new NumberAxis();
        final CategoryAxis typeAxisVoltage = new CategoryAxis();
        final NumberAxis voltageAxis = new NumberAxis();
        final CategoryAxis typeAxisInductiveReactivePower = new CategoryAxis();
        final NumberAxis inductiveReactivePower = new NumberAxis();
        final CategoryAxis typeAxisCapacitiveReactivePower = new CategoryAxis();
        final NumberAxis capacitiveReactivePower = new NumberAxis();
        final CategoryAxis typeAxisWork = new CategoryAxis();
        final NumberAxis workAxis = new NumberAxis();

        typeAxisPower.setLabel("Zeit in Minuten");
        powerAxis.setLabel("Watt");
        typeAxisAmperage.setLabel("Zeit in Minuten");
        amperageAxis.setLabel("Ampere");
        typeAxisVoltage.setLabel("Zeit in Minuten");
        voltageAxis.setLabel("Volt");
        typeAxisInductiveReactivePower.setLabel("Zeit in Minuten");
        inductiveReactivePower.setLabel("Watt");
        typeAxisCapacitiveReactivePower.setLabel("Zeit in Minuten");
        capacitiveReactivePower.setLabel("Watt");
        typeAxisWork.setLabel("Zeit in Minuten");
        workAxis.setLabel("Watt");

        final javafx.scene.chart.LineChart<String, Number> lineChartPower = new javafx.scene.chart.LineChart<>(typeAxisPower, powerAxis);
        final javafx.scene.chart.LineChart<String, Number> lineChartAmperage = new javafx.scene.chart.LineChart<>(typeAxisAmperage, amperageAxis);
        final javafx.scene.chart.LineChart<String, Number> lineChartVoltage = new javafx.scene.chart.LineChart<>(typeAxisVoltage, voltageAxis);
        final javafx.scene.chart.LineChart<String, Number> lineChartInductiveReactivePower = new javafx.scene.chart.LineChart<>(typeAxisInductiveReactivePower, inductiveReactivePower);
        final javafx.scene.chart.LineChart<String, Number> lineChartCapacitiveReactivePower = new javafx.scene.chart.LineChart<>(typeAxisCapacitiveReactivePower, capacitiveReactivePower);
        final List<javafx.scene.chart.LineChart<String, Number>> lineChartWork = new ArrayList<>();

        XYChart.Series powerSeries = new XYChart.Series();
        XYChart.Series amperageSeries = new XYChart.Series();
        XYChart.Series voltageSeries = new XYChart.Series();
        XYChart.Series inductiveReactivePowerSeries = new XYChart.Series();
        XYChart.Series capacitiveReactivePowerSeries = new XYChart.Series();
        XYChart.Series powerSeries2 = new XYChart.Series();
        XYChart.Series inductiveReactivePowerSeries2 = new XYChart.Series();
        XYChart.Series capacitiveReactivePowerSeries2 = new XYChart.Series();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        HashMap<String, Date[]> deviceTimes;
        FlowPane root = new FlowPane();
        DialogCreator dialogCreator = new DialogCreator();

        if (selectedDevices.size() > 0) {
            int duration = dialogCreator.getDurationDialog();
            if (duration == -1) {
                return;
            }
            for (int j = 0; j < selectedDevices.size(); j++) {
                String type = devices.get(selectedDevices.get(j)).get(0).typ;
                String id = devices.get(selectedDevices.get(j)).get(0).geraetId;
                dialogCreator = new DialogCreator("Liniendiagramm", type, duration);
                dialogCreator.setId(id);

                // load dialog and get date (from/to) of device
                deviceTimes = (HashMap<String, Date[]>) dialogCreator.getDialog();

                if (deviceTimes != null) {
                    for (Map.Entry<String, List<Messdaten>> entry : devices.entrySet()) {
                        String deviceId = entry.getKey();

                        powerSeries = new XYChart.Series();
                        amperageSeries = new XYChart.Series();
                        voltageSeries = new XYChart.Series();
                        inductiveReactivePowerSeries = new XYChart.Series();
                        capacitiveReactivePowerSeries = new XYChart.Series();
                        powerSeries2 = new XYChart.Series();
                        inductiveReactivePowerSeries2 = new XYChart.Series();
                        capacitiveReactivePowerSeries2 = new XYChart.Series();

                        if (deviceTimes.containsKey(deviceId)) {
                            int counter = 0;
                            for (int i = 0; i < entry.getValue().size(); i++) {
                                Date date = new Date();
                                String timestamp = entry.getValue().get(i).timestamp;
                                try {
                                    date = sdf.parse(timestamp);
                                } catch (Exception e) {
                                }
                                if (date.after(deviceTimes.get(deviceId)[0]) && date.before(deviceTimes.get(deviceId)[1])) {
                                    counter++;
                                    if (dialogCreator.getChkPower().isSelected()) {
                                        powerSeries.setName(entry.getValue().get(i).typ + "\nStartdatum: " + deviceTimes.get(deviceId)[0] + "\nEnddatum: " + deviceTimes.get(deviceId)[1]);
                                        Number power = Double.parseDouble(entry.getValue().get(i).watt);
                                        powerSeries.getData().add(new XYChart.Data(Integer.toString(counter * 3), power));
                                    }
                                    if (dialogCreator.getChkAmperage().isSelected()) {
                                        amperageSeries.setName(entry.getValue().get(i).typ + "\nStartdatum: " + deviceTimes.get(deviceId)[0] + "\nEnddatum: " + deviceTimes.get(deviceId)[1]);
                                        Number amperage = Double.parseDouble(entry.getValue().get(i).ampere);
                                        amperageSeries.getData().add(new XYChart.Data(Integer.toString(counter * 3), amperage));
                                    }
                                    if (dialogCreator.getChkVoltage().isSelected()) {
                                        voltageSeries.setName(entry.getValue().get(i).typ + "\nStartdatum: " + deviceTimes.get(deviceId)[0] + "\nEnddatum: " + deviceTimes.get(deviceId)[1]);
                                        Number voltage = Double.parseDouble(entry.getValue().get(i).volt);
                                        voltageSeries.getData().add(new XYChart.Data(Integer.toString(counter * 3), voltage));
                                    }
                                    if (dialogCreator.getChkInductiveReactivePower().isSelected()) {
                                        inductiveReactivePowerSeries.setName(entry.getValue().get(i).typ + "\nStartdatum: " + deviceTimes.get(deviceId)[0] + "\nEnddatum: " + deviceTimes.get(deviceId)[1]);
                                        Number induktiveBlindleistung = Double.parseDouble(entry.getValue().get(i).induktiveBlindleistung);
                                        inductiveReactivePowerSeries.getData().add(new XYChart.Data(Integer.toString(counter * 3), induktiveBlindleistung));
                                    }
                                    if (dialogCreator.getChkCapacitiveReactivePower().isSelected()) {
                                        capacitiveReactivePowerSeries.setName(entry.getValue().get(i).typ + "\nStartdatum: " + deviceTimes.get(deviceId)[0] + "\nEnddatum: " + deviceTimes.get(deviceId)[1]);
                                        Number kapazitiveBlindleistung = Double.parseDouble(entry.getValue().get(i).kapazitiveBlindleistung);
                                        capacitiveReactivePowerSeries.getData().add(new XYChart.Data(Integer.toString(counter * 3), kapazitiveBlindleistung));
                                    }
                                    if (dialogCreator.getChkPower().isSelected() && dialogCreator.getChkInductiveReactivePower().isSelected() && dialogCreator.getChkCapacitiveReactivePower().isSelected()) {
                                        Number watt = Double.parseDouble(entry.getValue().get(i).watt);
                                        powerSeries2.getData().add(new XYChart.Data(timestamp, watt));

                                        Number induktiveBlindleistung = Double.parseDouble(entry.getValue().get(i).induktiveBlindleistung);
                                        inductiveReactivePowerSeries2.getData().add(new XYChart.Data(timestamp, induktiveBlindleistung));

                                        Number kapazitiveBlindleistung = Double.parseDouble(entry.getValue().get(i).kapazitiveBlindleistung);
                                        capacitiveReactivePowerSeries2.getData().add(new XYChart.Data(timestamp, kapazitiveBlindleistung));
                                    }
                                } else if (counter != 0) {
                                    break;
                                }
                            }
                            if (dialogCreator.getChkPower().isSelected()) {
                                lineChartPower.setTitle("Leistung");
                                lineChartPower.getData().add(powerSeries);
                                lineChartPower.setPrefWidth(1000);
                            }
                            if (dialogCreator.getChkAmperage().isSelected()) {
                                lineChartAmperage.setTitle("Stromst�rke");
                                lineChartAmperage.getData().add(amperageSeries);
                                lineChartAmperage.setPrefWidth(1000);
                            }
                            if (dialogCreator.getChkVoltage().isSelected()) {
                                lineChartVoltage.setTitle("Spannung");
                                lineChartVoltage.getData().add(voltageSeries);
                                lineChartVoltage.setPrefWidth(1000);
                            }
                            if (dialogCreator.getChkInductiveReactivePower().isSelected()) {
                                lineChartInductiveReactivePower.setTitle("Induktive Blindleistung");
                                lineChartInductiveReactivePower.getData().add(inductiveReactivePowerSeries);
                                lineChartInductiveReactivePower.setPrefWidth(1000);
                            }
                            if (dialogCreator.getChkCapacitiveReactivePower().isSelected()) {
                                lineChartCapacitiveReactivePower.setTitle("Kapazitive Blindleistung");
                                lineChartCapacitiveReactivePower.getData().add(capacitiveReactivePowerSeries);
                                lineChartCapacitiveReactivePower.setPrefWidth(1000);
                            }
                            if (dialogCreator.getChkPower().isSelected() && dialogCreator.getChkInductiveReactivePower().isSelected() && dialogCreator.getChkCapacitiveReactivePower().isSelected()) {
                                powerSeries2.setName("Leistung");
                                inductiveReactivePowerSeries2.setName("Induktive Blindleistung");
                                capacitiveReactivePowerSeries2.setName("Kapazitive Blindleistung");
                                lineChartWork.add(new javafx.scene.chart.LineChart<>(typeAxisWork, workAxis));
                                lineChartWork.get(j).setTitle(type);
                                lineChartWork.get(j).getData().addAll(powerSeries2, inductiveReactivePowerSeries2, capacitiveReactivePowerSeries2);
                                lineChartWork.get(j).setPrefWidth(1000);
                            }
                        }
                    }
                }
                else{
                    return;
                }
            }
        } else {
            Platform.runLater(() -> FXController.getInstance().showAlert(Alert.AlertType.ERROR, "Fehler!", "Tabellendarstellung konnte nicht angezeigt werden!", "Es muss mindestens eine Einheit ausgew�hlt werden."));
            return;
        }
        if (dialogCreator.getChkPower().isSelected() || dialogCreator.getChkAmperage().isSelected() || dialogCreator.getChkVoltage().isSelected() || dialogCreator.getChkInductiveReactivePower().isSelected() || dialogCreator.getChkCapacitiveReactivePower().isSelected()) {
            root.setPrefWidth(1000);
            root.setMinWidth(600);
            if (dialogCreator.getChkPower().isSelected()) {
                root.getChildren().add(lineChartPower);
            }
            if (dialogCreator.getChkAmperage().isSelected()) {
                root.getChildren().add(lineChartAmperage);
            }
            if (dialogCreator.getChkVoltage().isSelected()) {
                root.getChildren().add(lineChartVoltage);
            }
            if (dialogCreator.getChkInductiveReactivePower().isSelected()) {
                root.getChildren().add(lineChartInductiveReactivePower);
            }
            if (dialogCreator.getChkCapacitiveReactivePower().isSelected()) {
                root.getChildren().add(lineChartCapacitiveReactivePower);
            }
            if (dialogCreator.getChkPower().isSelected() && dialogCreator.getChkInductiveReactivePower().isSelected() && dialogCreator.getChkCapacitiveReactivePower().isSelected()) {
                for (javafx.scene.chart.LineChart<String, Number> chart : lineChartWork) {
                    root.getChildren().add(chart);
                }
            }
        } else {
            Platform.runLater(() -> FXController.getInstance().showAlert(Alert.AlertType.ERROR, "Fehler!", "Liniendiagramm konnte nicht angezeigt werden!", "Es muss mindestens eine Einheit ausgew�hlt werden."));
            return;
        }
        lineChartPower.setHorizontalGridLinesVisible(false);
        lineChartPower.setAnimated(false);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(root);
        scrollPane.setPannable(true);
        Scene scene = new Scene(scrollPane, 1100, 600);

        Stage stage = new Stage();
        stage.setTitle("Linechart Chart vom Lastgang");
        stage.setScene(scene);
        stage.show();
    }
}