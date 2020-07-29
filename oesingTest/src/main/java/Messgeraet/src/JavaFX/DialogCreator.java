package Messgeraet.src.JavaFX;

import Messgeraet.src.Models.Properties.PropertiesModel;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static javafx.scene.control.ButtonBar.ButtonData.OK_DONE;

public class DialogCreator {
    private String title, type, id;
    private int duration;
    private PropertiesModel propertiesModel = PropertiesModel.getInstance();
    private HashMap<String, String> variables = new HashMap<>();
    private ButtonType confirmButton;
    private Optional<ButtonType> result;
    CheckBox chkPower = new CheckBox("Leistung anzeigen");
    CheckBox chkAmperage = new CheckBox("Stromst�rke anzeigen");
    CheckBox chkVoltage = new CheckBox("Spannung anzeigen");
    CheckBox chkInductiveReactivePower = new CheckBox("Induktive Blindleistung");
    CheckBox chkCapacitiveReactivePower = new CheckBox("Kapazitive Blindleistung");
    CheckBox chkWork = new CheckBox("Arbeit anzeigen");

    public DialogCreator(){}

    public DialogCreator(String title) {
        this.title = title;
    }

    public DialogCreator(String title, String type, int duration) {
        this.title = title;
        this.type = type;
        this.duration = duration;
    }

    public Map<String, ? extends Serializable> getDialog() {
        if (title.equals("Schwellwerte eintragen")) {
            return getSetpointDialog();
        } else if (title.equals("Messdaten als CSV exportieren")) {
            getCSVExportDialog();
        } else if (title.equals("Balkendiagramm")) {
            return getBarchartDialog(type);
        }
        else if (title.equals("Liniendiagramm")) {
            return getLinechartDialog(type);
        }
        else if (title.equals("Tabellendarstellung")){
            return getTableViewDialog(type);
        }
        else if(title.equals("Models")){
            return getDatabaseDialog();
        }
        return null;
    }

    private HashMap<String, Date[]> getTableViewDialog(String type){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        Date vonDate = new Date();
        Date bisDate = new Date();
        Date[] vonBis = new Date[2];
        HashMap<String, Date[]> geraeteZeiten = new HashMap<>();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(type + " Konfiguration");

        ButtonType confirmButton = new ButtonType("Tabelle anzeigen", OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextField von = new TextField();
        von.setPromptText("yyyy-MM-ddThh:mm:ss");
        TextField bis = new TextField();
        bis.setPromptText("yyyy-MM-ddThh:mm:ss");

        grid.add(new Label("Von:"), 0, 0);
        grid.add(von, 1, 0);
        grid.add(chkPower, 1, 1);
        grid.add(chkWork, 1, 2);
        grid.add(chkVoltage, 1, 3);
        grid.add(chkAmperage, 1, 4);
        grid.add(chkInductiveReactivePower, 1, 5);
        grid.add(chkCapacitiveReactivePower, 1, 6);

        dialog.getDialogPane().setContent(grid);

        // Fokus auf Von-Eingabefeld
        Platform.runLater(() -> von.requestFocus());

        Optional<ButtonType> result = dialog.showAndWait();

        // Wenn "Anzeigen"-Button gedr�ckt wird. Bei Abbrechen, wird nicht gespeichert!
        if (result.get() == confirmButton) {
            String vonZeit = von.getText().equals("") ? "1970-01-01T00:00:00" : von.getText();     // Wenn Eingabe leer, dann von 1970
            setChkPower(chkPower);
            setChkWork(chkWork);
            setChkVoltage(chkVoltage);
            setChkAmperage(chkAmperage);
            setChkInductiveReactivePower(chkInductiveReactivePower);
            setChkCapacitiveReactivePower(chkCapacitiveReactivePower);

            try {
                Calendar cal = Calendar.getInstance();

                vonDate = sdf.parse(vonZeit);
                cal.setTime(vonDate);
                cal.add(Calendar.MINUTE, duration);
                bisDate = cal.getTime();
            } catch (Exception e) {
            }

            vonBis[0] = vonDate;
            vonBis[1] = bisDate;

            geraeteZeiten.put(id, vonBis);

            return geraeteZeiten;
        }
        else{
            return null;
        }
    }

    private HashMap<String, String> getDatabaseDialog(){

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Messdaten speichern");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Speichern", OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Create the geraeteTyp and id labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField deviceType = new TextField();
        deviceType.setPromptText("Ger�tetyp");
        TextField id = new TextField();
        id.setPromptText("ID");

        grid.add(new Label("Ger�tetyp:"), 0, 0);
        grid.add(deviceType, 1, 0);
        grid.add(new Label("ID:"), 0, 1);
        grid.add(id, 1, 1);

        // Enable/Disable login button depending on whether a geraeteTyp was entered.
        Node saveButton = dialog.getDialogPane().lookupButton(loginButtonType);
        saveButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        id.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the geraeteTyp field by default.
        Platform.runLater(() -> deviceType.requestFocus());

        HashMap<String, String> hashMap = new HashMap<>();

        // Convert the result to a geraeteTyp-id-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                hashMap.put(deviceType.getText(), id.getText());
            }
            return null;
        });

        return hashMap;
    }

    private HashMap<String, Date[]> getLinechartDialog(String type){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(type + " Konfiguration");
        Date vonDate = new Date();
        Date bisDate = new Date();
        TextField von = new TextField();
        Date[] vonBis = new Date[2];
        HashMap<String, Date[]> geraeteZeiten = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

        confirmButton = new ButtonType("Diagramm anzeigen", OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(20, 20, 10, 10));

        von.setPromptText("yyyy-MM-ddThh:mm:ss");

        grid.add(new Label("Von:"), 0, 0);
        grid.add(von, 1, 0);
        grid.add(chkPower, 1, 1);
        grid.add(chkVoltage, 1, 2);
        grid.add(chkAmperage, 1, 3);
        grid.add(chkInductiveReactivePower, 1, 4);
        grid.add(chkCapacitiveReactivePower, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Fokus auf Von-Eingabefeld
        Platform.runLater(() -> von.requestFocus());

        result = dialog.showAndWait();

        if (result.get() == confirmButton) {
            vonBis = new Date[2];
            String vonZeit = von.getText().equals("") ? "1970-01-01T00:00:00" : von.getText();     // Wenn Eingabe leer, dann von 1970
            setChkPower(chkPower);
            setChkVoltage(chkVoltage);
            setChkAmperage(chkAmperage);
            setChkInductiveReactivePower(chkInductiveReactivePower);
            setChkCapacitiveReactivePower(chkCapacitiveReactivePower);

            try {
                Calendar cal = Calendar.getInstance();

                vonDate = sdf.parse(vonZeit);
                cal.setTime(vonDate);
                cal.add(Calendar.MINUTE, duration);
                bisDate = cal.getTime();
            } catch (Exception e) {
            }

            vonBis[0] = vonDate;
            vonBis[1] = bisDate;

            geraeteZeiten.put(id, vonBis);

            return geraeteZeiten;
        }
        else{
            return null;
        }
    }

    private HashMap<String, Date[]> getBarchartDialog(String type) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(type + " Konfiguration");
        Date vonDate = new Date();
        Date bisDate = new Date();
        Date[] vonBis = new Date[2];
        HashMap<String, Date[]> geraeteZeiten = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

        confirmButton = new ButtonType("Diagramm anzeigen", OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextField von = new TextField();
        von.setPromptText("yyyy-MM-ddThh:mm:ss");

        grid.add(new Label("Von:"), 0, 0);
        grid.add(von, 1, 0);

        dialog.getDialogPane().setContent(grid);

        // Fokus auf Von-Eingabefeld
        Platform.runLater(() -> von.requestFocus());

        result = dialog.showAndWait();

        if (result.get() == confirmButton) {
            String vonZeit = von.getText().equals("") ? "1970-01-01T00:00:00" : von.getText();     // Wenn Eingabe leer, dann von 1970

            try {
                Calendar cal = Calendar.getInstance();

                vonDate = sdf.parse(vonZeit);
                cal.setTime(vonDate);
                cal.add(Calendar.MINUTE, duration);
                bisDate = cal.getTime();
            } catch (Exception e) {
            }

            vonBis[0] = vonDate;
            vonBis[1] = bisDate;

            geraeteZeiten.put(id, vonBis);

            return geraeteZeiten;
        }
        return null;
    }

    public int getDurationDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Dauer in Stunden festlegen");
        TextField von = new TextField();

        confirmButton = new ButtonType("Dauer festlegen", OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(20, 20, 10, 10));

        von.setPromptText("48 (leer: 100 Jahre/876000 Stunden)");

        von.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("^[0-9]*$")) {
                    von.setText(oldValue);
                }
            }
        });

        grid.add(new Label("Dauer in Stunden:"), 0, 0);
        grid.add(von, 1, 0);

        dialog.getDialogPane().setContent(grid);

        // Fokus auf Von-Eingabefeld
        Platform.runLater(() -> von.requestFocus());

        result = dialog.showAndWait();

        if (result.get() == confirmButton) {
            double hours = von.getText().equals("") ? 876000 : Double.parseDouble(von.getText());     // Wenn Eingabe leer, dann 100 Jahre
            duration = (int) Math.round(hours * 60);

            return duration;
        }
        else{
            return -1;
        }
    }

    private HashMap<String, String> getCSVExportDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Messdaten als CSV exportieren");

        confirmButton = new ButtonType("Exportieren", OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        Button openFilechooser = new Button("Zielpfad angeben");
        TextField exportPath = new TextField();
        exportPath.setPrefWidth(200);
        exportPath.setEditable(false);
        exportPath.setPromptText("Zielpfad");
        TextField textFieldFilename = new TextField();
        textFieldFilename.setPrefWidth(200);
        textFieldFilename.setPromptText("Dateiname");

        openFilechooser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                Stage stage = new Stage();
                File selectedDirectory = directoryChooser.showDialog(stage);

                if (selectedDirectory != null) {
                    exportPath.setText(selectedDirectory.getAbsolutePath());
                }
            }
        });

        Label filename = new Label("Dateiname:");
        Label targetPath = new Label("Zielpfad:");
        filename.setPrefWidth(70);
        targetPath.setPrefWidth(70);

        VBox vBox = new VBox();
        vBox.setSpacing(5);

        HBox hBox0 = new HBox();
        hBox0.setSpacing(5);
        hBox0.getChildren().addAll(targetPath, exportPath, openFilechooser);
        HBox hBox1 = new HBox();
        hBox1.setSpacing(5);
        hBox1.getChildren().addAll(filename, textFieldFilename);
        vBox.getChildren().addAll(hBox0, hBox1);

        dialog.getDialogPane().setContent(vBox);

        // Fokus auf Von-Eingabefeld
        Platform.runLater(() -> openFilechooser.requestFocus());

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.get() == confirmButton) {
            String path = exportPath.getText() + "\\" + textFieldFilename.getText() + ".csv";
            variables.put("path", path);
        } else {
            return null;
        }
        return null;
    }

    private HashMap<String, String> getSetpointDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);

        confirmButton = new ButtonType("Speichern", OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextField watt = new TextField();
        watt.setPromptText("Watt");
        TextField kilowattstunde = new TextField();
        kilowattstunde.setPromptText("Kilowattstunde");
        TextField ampere = new TextField();
        ampere.setPromptText("Ampere");
        TextField volt = new TextField();
        volt.setPromptText("Volt");
        TextField induktiveBlindleistung = new TextField();
        induktiveBlindleistung.setPromptText("Induktive Blindleistung");
        TextField kapazitiveBlindleistung = new TextField();
        kapazitiveBlindleistung.setPromptText("Kapazitive Blindleistung");

        // Es k�nnen nur Zahlen und Punkte eingetragen werden
        watt.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                    watt.setText(oldValue);
                }
            }
        });
        kilowattstunde.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                    kilowattstunde.setText(oldValue);
                }
            }
        });
        ampere.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                    ampere.setText(oldValue);
                }
            }
        });
        volt.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                    volt.setText(oldValue);
                }
            }
        });
        induktiveBlindleistung.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                    induktiveBlindleistung.setText(oldValue);
                }
            }
        });
        kapazitiveBlindleistung.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                    kapazitiveBlindleistung.setText(oldValue);
                }
            }
        });


        grid.add(new Label("Watt:"), 0, 0);
        grid.add(watt, 1, 0);
        grid.add(new Label("Kilowattstunde:"), 0, 1);
        grid.add(kilowattstunde, 1, 1);
        grid.add(new Label("Ampere:"), 0, 2);
        grid.add(ampere, 1, 2);
        grid.add(new Label("Volt:"), 0, 3);
        grid.add(volt, 1, 3);
        grid.add(new Label("Induktive Blindleistung:"), 0, 4);
        grid.add(induktiveBlindleistung, 1, 4);
        grid.add(new Label("Kapazitive Blindleistung:"), 0, 5);
        grid.add(kapazitiveBlindleistung, 1, 5);

        // Lade (wenn vorhanden) gespeicherte Konfiguration
        try {
            Properties savedConfiguration = propertiesModel.getSetpoints();
            watt.setText(savedConfiguration.getProperty("watt"));
            kilowattstunde.setText(savedConfiguration.getProperty("kilowattstunde"));
            ampere.setText(savedConfiguration.getProperty("ampere"));
            volt.setText(savedConfiguration.getProperty("volt"));
            induktiveBlindleistung.setText(savedConfiguration.getProperty("induktiveBlindleistung"));
            kapazitiveBlindleistung.setText(savedConfiguration.getProperty("kapazitiveBlindleistung"));
        } catch (Exception e) {
        }

        dialog.getDialogPane().setContent(grid);

        // Fokus auf Watt-Eingabefeld
        Platform.runLater(() -> watt.requestFocus());

        Optional<ButtonType> result = dialog.showAndWait();

        // Wenn "Speichern"-Button gedr�ckt wird. Bei Abbrechen, wird nicht gespeichert!
        if (result.get() == confirmButton) {
            String sollWatt = watt.getText();
            String sollKilowattstunde = kilowattstunde.getText();
            String sollAmpere = ampere.getText();
            String sollVolt = volt.getText();
            String sollInduktiveBlindleistung = induktiveBlindleistung.getText();
            String sollKapazitiveBlindleistung = kapazitiveBlindleistung.getText();

            variables.put("watt", watt.getText());
            variables.put("kilowattstunde", kilowattstunde.getText());
            variables.put("ampere", ampere.getText());
            variables.put("volt", volt.getText());
            variables.put("induktiveBlindleistung", induktiveBlindleistung.getText());
            variables.put("kapazitiveBlindleistung", kapazitiveBlindleistung.getText());

            return variables;
        }
        return null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PropertiesModel getPropertiesModel() {
        return propertiesModel;
    }

    public void setPropertiesModel(PropertiesModel propertiesModel) {
        this.propertiesModel = propertiesModel;
    }

    public HashMap<String, String> getVariables() {
        return variables;
    }

    public void setVariables(HashMap<String, String> variables) {
        this.variables = variables;
    }

    public ButtonType getConfirmButton() {
        return confirmButton;
    }

    public void setConfirmButton(ButtonType confirmButton) {
        this.confirmButton = confirmButton;
    }

    public Optional<ButtonType> getResult() {
        return result;
    }

    public void setResult(Optional<ButtonType> result) {
        this.result = result;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CheckBox getChkPower() {
        return chkPower;
    }

    public void setChkPower(CheckBox chkPower) {
        this.chkPower = chkPower;
    }

    public CheckBox getChkAmperage() {
        return chkAmperage;
    }

    public void setChkAmperage(CheckBox chkAmperage) {
        this.chkAmperage = chkAmperage;
    }

    public CheckBox getChkVoltage() {
        return chkVoltage;
    }

    public void setChkVoltage(CheckBox chkVoltage) {
        this.chkVoltage = chkVoltage;
    }

    public CheckBox getChkInductiveReactivePower() {
        return chkInductiveReactivePower;
    }

    public void setChkInductiveReactivePower(CheckBox chkInductiveReactivePower) {
        this.chkInductiveReactivePower = chkInductiveReactivePower;
    }

    public CheckBox getChkCapacitiveReactivePower() {
        return chkCapacitiveReactivePower;
    }

    public void setChkCapacitiveReactivePower(CheckBox chkCapacitiveReactivePower) {
        this.chkCapacitiveReactivePower = chkCapacitiveReactivePower;
    }

    public CheckBox getChkWork() {
        return chkWork;
    }

    public void setChkWork(CheckBox chkWork) {
        this.chkWork = chkWork;
    }
}
