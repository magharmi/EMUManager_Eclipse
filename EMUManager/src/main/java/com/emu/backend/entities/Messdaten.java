package com.emu.backend.entities;

import org.springframework.data.annotation.Id;

public class Messdaten {

    @Id
    public String id;

    public String timestamp;
    public String geraetId;
    public String typ;
    public String watt;
    public String ampere;
    public String volt;
    public String induktiveBlindleistung;
    public String kapazitiveBlindleistung;

    public Messdaten() {
    }

    public Messdaten(String timestamp, String geraetId, String typ, String watt, String ampere, String volt, String induktiveBlindleistung, String kapazitiveBlindleistung) {
        this.timestamp = timestamp;
        this.geraetId = geraetId;
        this.typ = typ;
        this.watt = watt;
        this.ampere = ampere;
        this.volt = volt;
        this.induktiveBlindleistung = induktiveBlindleistung;
        this.kapazitiveBlindleistung = kapazitiveBlindleistung;
    }

    @Override
    public String toString() {
        return "MeasurementData{" +
                "id='" + id + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", geraetId='" + geraetId + '\'' +
                ", typ='" + typ + '\'' +
                ", watt='" + watt + '\'' +
                ", ampere='" + ampere + '\'' +
                ", volt='" + volt + '\'' +
                ", induktiveBlindleistung='" + induktiveBlindleistung + '\'' +
                ", kapazitiveBlindleistung='" + kapazitiveBlindleistung + '\'' +
                '}';
    }
}
