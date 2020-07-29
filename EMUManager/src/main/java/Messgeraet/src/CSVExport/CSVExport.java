package Messgeraet.src.CSVExport;

import Messgeraet.src.JavaFX.DialogCreator;
import Messgeraet.src.JavaFX.FXController;
import com.emu.backend.entities.*;
import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import javafx.scene.control.Alert;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CSVExport {

    public void export(List<String> selectedDevices, HashMap<String, List<Messdaten>> devices){
        try {
            DialogCreator dialogCreator = new DialogCreator("Messdaten als CSV exportieren");
            dialogCreator.getDialog();

            if (selectedDevices.size() > 0 && dialogCreator.getVariables() != null) {
                String path = dialogCreator.getVariables().get("path");
                FileOutputStream os = new FileOutputStream(path);

                // Set UTF-8
                os.write(0xef);
                os.write(0xbb);
                os.write(0xbf);

                String[] headerRecord = {"Ger�teId", "Typ", "Timestamp", "Leistung", "Arbeit", "Spannung", "Stromst�rke", "Induktive Blindleistung", "Kapazitive Blindleistung"};

                ICSVWriter csvWriter = new CSVWriterBuilder(new OutputStreamWriter(os, "UTF-8"))
                        .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                        .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
                        .withEscapeChar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
                        .withLineEnd(CSVWriter.DEFAULT_LINE_END)
                        .build();

                csvWriter.writeNext(headerRecord);

                for (Iterator<String> iter = selectedDevices.iterator(); iter.hasNext(); ) {
                    String selectedDeviceId = iter.next();
                    for (Map.Entry<String, List<Messdaten>> entry : devices.entrySet()) {
                        String deviceIdKey = entry.getKey();
                        if (deviceIdKey.equals(selectedDeviceId)) {
                            for (int i = 0; i < entry.getValue().size(); i++) {
                                String deviceId = entry.getValue().get(i).geraetId;
                                String type = entry.getValue().get(i).typ;
                                String timestamp = entry.getValue().get(i).timestamp;
                                String power = entry.getValue().get(i).watt;
                                String work = Double.toString((Double.parseDouble(entry.getValue().get(i).watt) * 0.05) / 1000);
                                String voltage = entry.getValue().get(i).volt;
                                String stromstaerke = entry.getValue().get(i).ampere;
                                String induktiveBlindleistung = entry.getValue().get(i).induktiveBlindleistung;
                                String kapazitiveBlindleistung = entry.getValue().get(i).kapazitiveBlindleistung;

                                csvWriter.writeNext(new String[]{deviceId, type, timestamp, power, work, voltage, stromstaerke, induktiveBlindleistung, kapazitiveBlindleistung});
                            }
                        }
                    }
                }


                csvWriter.close();
                os.close();
                FXController.getInstance().showAlert(Alert.AlertType.INFORMATION, "Messdaten als CSV exportieren", "CSV-Export erfolgreich!", "Die Messdaten wurden erfolgreich zum folgenden Pfad exportiert!\n" + path);
            } else if (dialogCreator.getVariables() == null) {
                FXController.getInstance().showAlert(Alert.AlertType.ERROR, "Messdaten als CSV exportieren", "Messdaten konnte nicht exportiert werden!", "Es muss mindestens ein Ger�t ausgew�hlt werden.");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
