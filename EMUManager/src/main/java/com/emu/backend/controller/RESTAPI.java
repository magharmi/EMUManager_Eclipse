package com.emu.backend.controller;

import com.emu.backend.entities.Messdaten;
import com.emu.backend.repository.MessdatenRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RESTAPI {
    final MessdatenRepository messdatenRepository;

    public RESTAPI(MessdatenRepository messdatenRepository) {
        this.messdatenRepository = messdatenRepository;
    }

    @PostMapping("/Messdaten")
    public String speichereMessdaten(@RequestBody String JSONMessdaten) throws JSONException {
        JSONObject jsonObject = new JSONObject(JSONMessdaten);
        System.out.println(JSONMessdaten);
        String timestamp = jsonObject.getJSONObject("messung").get("timestamp").toString();
        String deviceId = jsonObject.getJSONObject("messung").get("geraeteId").toString();
        String type = jsonObject.getJSONObject("messung").get("typ").toString();
        String power = jsonObject.getJSONObject("messung").get("watt").toString();
        String amperage = jsonObject.getJSONObject("messung").get("ampere").toString();
        String voltage = jsonObject.getJSONObject("messung").get("volt").toString();
        String inductiveReactivePower = jsonObject.getJSONObject("messung").get("induktiveBlindleistung").toString();
        String capacitiveReactivePower = jsonObject.getJSONObject("messung").get("kapazitiveBlindleistung").toString();

        Messdaten measurementData = new Messdaten();
        measurementData.timestamp = timestamp;
        measurementData.geraetId = deviceId;
        measurementData.typ = type;
        measurementData.watt = power;
        measurementData.ampere = amperage;
        measurementData.volt = voltage;
        measurementData.induktiveBlindleistung = inductiveReactivePower;
        measurementData.kapazitiveBlindleistung = capacitiveReactivePower;

        messdatenRepository.save(measurementData);

        return "Gespeichert";
    }

    @GetMapping("/Messdaten/{deviceId}")
    public Messdaten[] loadMeasurementData(@PathVariable String deviceId) {
        Messdaten[] measurementData = messdatenRepository.findAllByGeraetId(deviceId);

        return measurementData;
    }

    @GetMapping("/Messdaten/all")
    public List<Messdaten> loadAllMeasurementData() {
        return messdatenRepository.findAll();
    }
}
