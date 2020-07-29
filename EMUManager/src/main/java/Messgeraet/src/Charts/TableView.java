package Messgeraet.src.Charts;

import Messgeraet.src.JavaFX.DialogCreator;
import Messgeraet.src.JavaFX.FXController;
import Messgeraet.src.JavaFX.SspMeasurementData;
import com.emu.backend.entities.*;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class TableView {
    public FXController fxController = FXController.getInstance();
    private List<String> selectedDevices;
    private HashMap<String, List<Messdaten>> devices;

    public TableView(List<String> selectedDevices, HashMap<String, List<Messdaten>> devices) {
        this.selectedDevices = selectedDevices;
        this.devices = devices;
    }

    public void loadTableView(){
        Stage stage = new Stage();
        stage.setTitle("Tabellenansicht der Messdaten");
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        DecimalFormat df = new DecimalFormat("###.######");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        HashMap<String, Date[]> deviceTimes = new HashMap<>();
        int counter = 0;
        List<javafx.scene.control.TableView> tables = null;
        Label deviceName = new Label();
        DialogCreator dialogCreator = new DialogCreator();


        if (selectedDevices.size() > 0) {
            int duration = dialogCreator.getDurationDialog();
            if(duration == -1){
                return;
            }
            for (int j = 0; j < selectedDevices.size(); j++) {
                String type = devices.get(selectedDevices.get(j)).get(0).typ;
                String id = devices.get(selectedDevices.get(j)).get(0).geraetId;
                dialogCreator = new DialogCreator("Tabellendarstellung", type, duration);
                dialogCreator.setId(id);

                // load dialog and get date (from/to) of device
                deviceTimes = (HashMap<String, Date[]>) dialogCreator.getDialog();

                if (deviceTimes != null) {
                    tables = new ArrayList<>();
                    for (Map.Entry<String, List<Messdaten>> entry : devices.entrySet()) {
                        String deviceId = entry.getKey();

                        if (deviceId.equals(id)) {
                            tables.add(new javafx.scene.control.TableView());

                            TableColumn timestampCol = new TableColumn("Timestamp");
                            TableColumn powerCol = new TableColumn("Leistung");
                            TableColumn workCol = new TableColumn("Arbeit");
                            TableColumn amperageCol = new TableColumn("Stromst�rke");
                            TableColumn voltageCol = new TableColumn("Spannung");
                            TableColumn inductiveReactivePowerCol = new TableColumn("Induktive Blindleistung");
                            TableColumn capacitiveReactivePowerCol = new TableColumn("Kapazitive Blindleistung");

                            inductiveReactivePowerCol.setPrefWidth(100);

                            timestampCol.setCellValueFactory(new PropertyValueFactory<>("sspTimestamp"));
                            powerCol.setCellValueFactory(new PropertyValueFactory<>("sspWatt"));
                            workCol.setCellValueFactory(new PropertyValueFactory<>("sspKilowattstunde"));
                            amperageCol.setCellValueFactory(new PropertyValueFactory<>("sspAmpere"));
                            voltageCol.setCellValueFactory(new PropertyValueFactory<>("sspVolt"));
                            inductiveReactivePowerCol.setCellValueFactory(new PropertyValueFactory<>("sspInduktiveBlindleistung"));
                            capacitiveReactivePowerCol.setCellValueFactory(new PropertyValueFactory<>("sspKapazitiveBlindleistung"));

                            for (int i = 0; i < entry.getValue().size(); i++) {
                                deviceName = new Label(entry.getValue().get(i).typ);
                                Date date = new Date();
                                String timestamp = entry.getValue().get(i).timestamp;
                                try {
                                    date = sdf.parse(timestamp);
                                } catch (Exception e) {
                                }
                                if (dialogCreator.getChkPower().isSelected() || dialogCreator.getChkWork().isSelected() || dialogCreator.getChkAmperage().isSelected() || dialogCreator.getChkVoltage().isSelected() || dialogCreator.getChkInductiveReactivePower().isSelected() || dialogCreator.getChkCapacitiveReactivePower().isSelected()) {
                                    if (date.after(deviceTimes.get(deviceId)[0]) && date.before(deviceTimes.get(deviceId)[1])) {
                                        SspMeasurementData sspMeasurementData = new SspMeasurementData();
                                        sspMeasurementData.setSspTimestamp(timestamp);
                                        if (dialogCreator.getChkPower().isSelected()) {
                                            String power = entry.getValue().get(i).watt;
                                            sspMeasurementData.setSspPower(power);
                                        }
                                        if (dialogCreator.getChkWork().isSelected()) {
                                            double power = Double.parseDouble(entry.getValue().get(i).watt);
                                            double work = (power * 0.05) / 1000;
                                            String stringWork = df.format(work);
                                            sspMeasurementData.setSspWork(stringWork);
                                        }
                                        if (dialogCreator.getChkAmperage().isSelected()) {
                                            String amperage = entry.getValue().get(i).ampere;
                                            sspMeasurementData.setSspAmperage(amperage);
                                        }
                                        if (dialogCreator.getChkVoltage().isSelected()) {
                                            String voltage = entry.getValue().get(i).volt;
                                            sspMeasurementData.setSspVoltage(voltage);
                                        }
                                        if (dialogCreator.getChkInductiveReactivePower().isSelected()) {
                                            String inductiveReactivePower = entry.getValue().get(i).induktiveBlindleistung;
                                            sspMeasurementData.setSspInductiveReactivePower(inductiveReactivePower);
                                        }
                                        if (dialogCreator.getChkCapacitiveReactivePower().isSelected()) {
                                            String capacitiveReactivePower = entry.getValue().get(i).kapazitiveBlindleistung;
                                            sspMeasurementData.setSspCapacitiveReactivePower(capacitiveReactivePower);
                                        }
                                        tables.get(0).getItems().add(sspMeasurementData);
                                    }
                                }
                                else{
                                    Platform.runLater(() -> FXController.getInstance().showAlert(Alert.AlertType.ERROR, "Fehler!", "Tabellendarstellung konnte nicht angezeigt werden!", "Es muss mindestens ein Ger�t ausgew�hlt werden."));
                                    return;
                                }
                            }
                            tables.get(0).getColumns().add(timestampCol);
                            if (dialogCreator.getChkPower().isSelected()) {
                                tables.get(0).getColumns().add(powerCol);
                            }
                            if (dialogCreator.getChkWork().isSelected()) {
                                tables.get(0).getColumns().add(workCol);
                            }
                            if (dialogCreator.getChkAmperage().isSelected()) {
                                tables.get(0).getColumns().add(amperageCol);
                            }
                            if (dialogCreator.getChkVoltage().isSelected()) {
                                tables.get(0).getColumns().add(voltageCol);
                            }
                            if (dialogCreator.getChkInductiveReactivePower().isSelected()) {
                                tables.get(0).getColumns().add(inductiveReactivePowerCol);
                            }
                            if (dialogCreator.getChkCapacitiveReactivePower().isSelected()) {
                                tables.get(0).getColumns().add(capacitiveReactivePowerCol);
                            }
                        }
                    }
                }
                else{
                    return;
                }
                vBox.getChildren().addAll(deviceName, tables.get(0));
            }
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(vBox);
            scrollPane.setFitToWidth(true);
            scrollPane.setPannable(true);
            Scene scene = new Scene(scrollPane, 1100, 600);

            stage.setScene(scene);
            stage.show();
        } else {
            Platform.runLater(() -> FXController.getInstance().showAlert(Alert.AlertType.ERROR, "Fehler!", "Tabellendarstellung konnte nicht angezeigt werden!", "Es muss mindestens ein Ger�t ausgew�hlt werden."));
        }
    }

}
