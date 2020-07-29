package Messgeraet.src.JavaFX;

import javafx.beans.property.SimpleStringProperty;

public class SspMeasurementData {

    private SimpleStringProperty sspTimestamp;
    private SimpleStringProperty sspWatt;
    private SimpleStringProperty sspKilowattstunde;
    private SimpleStringProperty sspAmpere;
    private SimpleStringProperty sspVolt;
    private SimpleStringProperty sspInduktiveBlindleistung;
    private SimpleStringProperty sspKapazitiveBlindleistung;

    public SspMeasurementData(){}

    public String getSspTimestamp() {
        return sspTimestamp.get();
    }

    public void setSspTimestamp(String sspTimestamp) {
        this.sspTimestamp = new SimpleStringProperty(sspTimestamp);
    }

    public String getSspKilowattstunde() {
        return sspKilowattstunde.get();
    }

    public void setSspWork(String sspKilowattstunde) {
        this.sspKilowattstunde = new SimpleStringProperty(sspKilowattstunde);
    }

    public String getSspWatt() {
        return sspWatt.get();
    }

    public void setSspPower(String sspWatt) {
        this.sspWatt = new SimpleStringProperty(sspWatt);
    }

    public String getSspAmpere() {
        return sspAmpere.get();
    }

    public void setSspAmperage(String sspAmpere) {
        this.sspAmpere = new SimpleStringProperty(sspAmpere);
    }

    public String getSspVolt() {
        return sspVolt.get();
    }

    public void setSspVoltage(String sspVolt) {
        this.sspVolt = new SimpleStringProperty(sspVolt);
    }

    public String getSspInduktiveBlindleistung() {
        return sspInduktiveBlindleistung.get();
    }

    public void setSspInductiveReactivePower(String sspInduktiveBlindleistung) {
        this.sspInduktiveBlindleistung = new SimpleStringProperty(sspInduktiveBlindleistung);
    }

    public String getSspKapazitiveBlindleistung() {
        return sspKapazitiveBlindleistung.get();
    }

    public void setSspCapacitiveReactivePower(String sspKapazitiveBlindleistung) {
        this.sspKapazitiveBlindleistung = new SimpleStringProperty(sspKapazitiveBlindleistung);
    }
}
