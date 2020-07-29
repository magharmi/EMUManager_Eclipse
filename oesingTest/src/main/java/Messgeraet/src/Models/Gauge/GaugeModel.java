package Messgeraet.src.Models.Gauge;

import Messgeraet.src.JavaFX.FXController;
import Messgeraet.src.Models.Properties.PropertiesModel;
import eu.hansolo.medusa.Gauge;
import com.emu.backend.entities.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GaugeModel {
    PropertiesModel propertiesModel = PropertiesModel.getInstance();

    public void compareSetpoint(List<String> selectedDevices, HashMap<String, List<Messdaten>> devices, Gauge gaugePower, Gauge gaugeWork) throws IOException {
        if (selectedDevices.size() > 0) {
            boolean exceeded = false;
            String header = "";
            String content = "";
            String type = "";
            String timePower = "";
            String timeWork = "";
            String timeAmperage = "";
            String timeVoltage = "";
            String timeInductiveReactivePower = "";
            String timeCapacitiveReactivePower = "";
            double shouldPower = Double.parseDouble(propertiesModel.getSetpoints().getProperty("watt"));
            double shouldWork = Double.parseDouble(propertiesModel.getSetpoints().getProperty("kilowattstunde"));
            double shouldAmperage = Double.parseDouble(propertiesModel.getSetpoints().getProperty("ampere"));
            double shouldVoltage = Double.parseDouble(propertiesModel.getSetpoints().getProperty("volt"));
            double shouldInductiveReactivePower = Double.parseDouble(propertiesModel.getSetpoints().getProperty("induktiveBlindleistung"));
            double shouldCapacitiveReactivePower = Double.parseDouble(propertiesModel.getSetpoints().getProperty("kapazitiveBlindleistung"));
            double isPower = 0;
            double isWork = 0;
            double isAmperage = 0;
            double isVoltage = 0;
            double isInductiveReactivePower = 0;
            double isCapacitiveReactivePower = 0;

            for (Iterator<String> iter = selectedDevices.iterator(); iter.hasNext(); ) {
                String selectedDeviceId = iter.next();

                for (Map.Entry<String, List<Messdaten>> entry : devices.entrySet()) {
                    String deviceId = entry.getKey();
                    if (deviceId.equals(selectedDeviceId)) {
                        for (int i = 0; i < entry.getValue().size(); i++) {
                            type = entry.getValue().get(0).typ;

                            timePower = entry.getValue().get(i).timestamp;
                            isPower += Double.parseDouble(entry.getValue().get(i).watt);

                            timeWork = entry.getValue().get(i).timestamp;
                            isWork += (Double.parseDouble(entry.getValue().get(i).watt) * 0.05) / 1000;

                            timeAmperage = entry.getValue().get(i).timestamp;
                            isAmperage += Double.parseDouble(entry.getValue().get(i).ampere);

                            timeVoltage = entry.getValue().get(i).timestamp;
                            isVoltage += Double.parseDouble(entry.getValue().get(i).volt);

                            timeInductiveReactivePower = entry.getValue().get(i).timestamp;
                            isInductiveReactivePower += Double.parseDouble(entry.getValue().get(i).induktiveBlindleistung);

                            timeCapacitiveReactivePower = entry.getValue().get(i).timestamp;
                            isCapacitiveReactivePower += Double.parseDouble(entry.getValue().get(i).kapazitiveBlindleistung);
                        }
                    }
                }
                if (shouldPower != 0) {
                    if (isPower > shouldPower) {
                        exceeded = true;
                        content += type + " hat mit " + isPower + " Watt den Schwellwert von " + shouldPower + " Watt am " + timePower + " �berschritten.\n";
                    } else {
                        content += type + " hat den Schwellwert von " + shouldPower + " Watt nicht �berschritten.\n";
                    }
                }
                if (shouldWork != 0) {
                    if (isWork > shouldWork) {
                        exceeded = true;
                        content += type + " hat mit " + isWork + " kWh den Schwellwert von " + shouldWork + " kWh am " + timeWork + " �berschritten.\n";
                    } else {
                        content += type + " hat den Schwellwert von " + shouldWork + " kWh nicht �berschritten.\n";
                    }
                }
                if (shouldAmperage != 0) {
                    if (isAmperage > shouldAmperage) {
                        exceeded = true;
                        content += type + " hat mit " + isAmperage + " Ampere den Schwellwert von " + shouldAmperage + " Ampere am " + timeAmperage + " �berschritten.\n";
                    } else {
                        content += type + " hat den Schwellwert von " + shouldAmperage + " Ampere nicht �berschritten.\n";
                    }
                }
                if (shouldVoltage != 0) {
                    if (isVoltage > shouldVoltage) {
                        exceeded = true;
                        content += type + " hat mit " + isVoltage + " Volt den Schwellwert von " + shouldVoltage + " Volt am " + timeVoltage + " �berschritten.\n";
                    } else {
                        content += type + " hat den Schwellwert von " + shouldVoltage + " Volt nicht �berschritten.\n";
                    }
                }
                if (shouldInductiveReactivePower != 0) {
                    if (isInductiveReactivePower > shouldInductiveReactivePower) {
                        exceeded = true;
                        content += type + " hat mit " + isInductiveReactivePower + " Var den Schwellwert von " + isInductiveReactivePower + " Var am " + timeInductiveReactivePower + " �berschritten.\n";
                    } else {
                        content += type + " hat den Schwellwert von " + shouldInductiveReactivePower + " Var nicht �berschritten.\n";
                    }
                }
                if (shouldCapacitiveReactivePower != 0) {
                    if (isCapacitiveReactivePower > shouldCapacitiveReactivePower) {
                        exceeded = true;
                        content += type + " hat mit " + isCapacitiveReactivePower + " Var den Schwellwert von " + shouldCapacitiveReactivePower + " Var am " + timeCapacitiveReactivePower + " �berschritten.\n";
                    } else {
                        content += type + " hat den Schwellwert von " + shouldCapacitiveReactivePower + " Var nicht �berschritten.\n";
                    }
                }
            }
            FXController.setGauge(isPower, shouldPower, gaugePower);
            FXController.setGauge(isWork, shouldWork, gaugeWork);

            if (exceeded) {
                header = "Schwellwerte aus der Konfiguration wurden �berschritten.";
            } else {
                header = "Schwellwerte aus der Konfiguration wurden nicht �berschritten.";
            }
            String finalHeader = header;
            String finalContent = content;
            Platform.runLater(() -> FXController.getInstance().showAlert(Alert.AlertType.INFORMATION, "Schwellwerte �berschritten", finalHeader, finalContent));
        } else {
            Platform.runLater(() -> FXController.getInstance().showAlert(Alert.AlertType.ERROR, "Fehler!", "Schwellwerte k�nnen nicht verglichen werden!", "Es muss mindestens ein Ger�t ausgew�hlt werden."));
        }
    }

    public double roundUpTen(double value) {
        return Math.ceil(value / 10) * 10;
    }

    public Color getColor(double n, int maxValue) {
        int r = (int) (255 * n) / maxValue;
        int g = (int) (255 * (maxValue - n)) / maxValue;
        int b = 0;

        return Color.rgb(r, g, b);
    }
}
