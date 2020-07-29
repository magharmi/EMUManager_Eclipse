package Messgeraet.src.Models.Database;

import Messgeraet.src.Emu.Measurement;
import Messgeraet.src.JavaFX.DialogCreator;
import com.emu.backend.entities.*;
import javafx.scene.control.Alert;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseModel {

    private static DatabaseModel instance;

    private DatabaseModel() {
    }

    public static DatabaseModel getInstance() {
        if (DatabaseModel.instance == null) {
            DatabaseModel.instance = new DatabaseModel();
        }
        return DatabaseModel.instance;
    }

    public void saveLoadGear(Map<String, Measurement> messdaten) throws IOException {
        String deviceTypeString = null, idString;

        DialogCreator dialogCreator = new DialogCreator("Models");
        HashMap<String, String> deviceTypeAndId = (HashMap<String, String>) dialogCreator.getDialog();

        for (String key : deviceTypeAndId.keySet()) {
            deviceTypeString = key;
        }

        idString = deviceTypeAndId.get(deviceTypeString);

        // Beide Inputfelder f�r Datenspeicherung d�rfen NICHT leer sein!
        if (!deviceTypeString.isEmpty() && !idString.isEmpty()) {
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("http://localhost:8080/Messdaten");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json; charset=UTF-8");

            for (String key : messdaten.keySet()) {
                String timestamp = key;
                String power = messdaten.get(key).getPower();
                String amperage = messdaten.get(key).getAmperage();
                String voltage = messdaten.get(key).getVoltage();
                String inductiveReactivePower = messdaten.get(key).getInductiveReactivePower();
                String capacitiveReactivePower = messdaten.get(key).getCapacitiveReactivePower();
                StringEntity params = new StringEntity("{messung:{\"timestamp\":\"" + timestamp + "\", \"geraeteId\":\"" + idString + "\", \"typ\":\"" + deviceTypeString + "\", \"watt\":\"" + power + "\", \"ampere\":\"" + amperage + "\", \"volt\":\"" + voltage + "\" , \"induktiveBlindleistung\":\"" + inductiveReactivePower + "\", \"kapazitiveBlindleistung\":\"" + capacitiveReactivePower + "\"}}", "UTF-8");

                httpPost.setEntity(params);     // POST-Parameter festlegen
                HttpResponse response = httpClient.execute(httpPost);   // POST senden und Response erhalten

                HttpEntity entity = response.getEntity();
                System.out.println(EntityUtils.toString(entity, "UTF-8"));  // Response ausgeben
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler");
            alert.setHeaderText("Fehlerhafte Eintr�ge");
            alert.setContentText("Bitte geben Sie eine einzigartige ID und den jeweiligen Ger�tetyp ein.");

            alert.showAndWait();
        }
    }

    public void loadMeasurementData(String deviceId) throws IOException, JSONException {

        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:8080/Messdaten/" + deviceId);
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");

        HttpResponse response = httpClient.execute(httpGet);   // GET senden und Response erhalten
        HttpEntity entity = response.getEntity();
        String resultString = EntityUtils.toString(entity, "UTF-8");

        JSONArray jsonArray = new JSONArray(resultString);
        Measurement[] measurement = new Measurement[jsonArray.length()];

        for (int i = 0; i < jsonArray.length(); i++) {
            String timestamp = jsonArray.getJSONObject(i).getString("timestamp");
            String power = jsonArray.getJSONObject(i).getString("watt");
            String amperage = jsonArray.getJSONObject(i).getString("ampere");
            String voltage = jsonArray.getJSONObject(i).getString("volt");
            String inductiveReactivePower = jsonArray.getJSONObject(i).getString("induktiveBlindleistung");
            String capacitiveReactivePower = jsonArray.getJSONObject(i).getString("kapazitiveBlindleistung");

            measurement[i] = new Measurement(power, amperage, voltage, inductiveReactivePower, capacitiveReactivePower);

            System.out.println(timestamp);
            System.out.println("Watt: " + measurement[i].getPower() + "\tAmpere: " + measurement[i].getAmperage() + "\tVolt: " + measurement[i].getVoltage() + "\tIB: " + measurement[i].getInductiveReactivePower() + "\tKB: " + measurement[i].getCapacitiveReactivePower());
        }
    }

    public HashMap<String, List<Messdaten>> loadAllMeasurementData() throws IOException, JSONException {

        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:8080/Messdaten/all");
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");

        HttpResponse response = httpClient.execute(httpGet);   // GET senden und Response erhalten
        HttpEntity entity = response.getEntity();
        String resultString = EntityUtils.toString(entity, "UTF-8");

        JSONArray jsonArray = new JSONArray(resultString);
        Messdaten messdaten;
        List<List<Messdaten>> deviceMeasurementData = new ArrayList<>();
        int deviceCounter = 1;
        deviceMeasurementData.add(new ArrayList<Messdaten>());

        HashMap<String, List<Messdaten>> stringMeasurementDataHashMap = new HashMap<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            String deviceId = jsonArray.getJSONObject(i).getString("geraetId");
            String timestamp = jsonArray.getJSONObject(i).getString("timestamp");
            String type = jsonArray.getJSONObject(i).getString("typ");
            String power = jsonArray.getJSONObject(i).getString("watt");
            String amperage = jsonArray.getJSONObject(i).getString("ampere");
            String voltage = jsonArray.getJSONObject(i).getString("volt");
            String inductiveReactivePower = jsonArray.getJSONObject(i).getString("induktiveBlindleistung");
            String capacitiveReactivePower = jsonArray.getJSONObject(i).getString("kapazitiveBlindleistung");

            messdaten = new Messdaten(timestamp, deviceId, type, power, amperage, voltage, inductiveReactivePower, capacitiveReactivePower);

            if (Integer.parseInt(messdaten.geraetId) == deviceCounter) {
                deviceMeasurementData.get(deviceCounter - 1).add(messdaten);
            } else {
                stringMeasurementDataHashMap.put(Integer.toString(deviceCounter), deviceMeasurementData.get(deviceCounter - 1));    // geraetId mit leerer Liste einf�gen
                deviceCounter++;
                deviceMeasurementData.add(new ArrayList<Messdaten>());
            }
        }
        stringMeasurementDataHashMap.put(Integer.toString(deviceCounter), deviceMeasurementData.get(deviceCounter - 1));
        return stringMeasurementDataHashMap;
    }
}
