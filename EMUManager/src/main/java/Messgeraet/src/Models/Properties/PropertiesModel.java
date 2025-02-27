package Messgeraet.src.Models.Properties;

import java.io.*;
import java.util.Properties;

public class PropertiesModel {
    private static PropertiesModel instance;

    private PropertiesModel() {
    }

    public static PropertiesModel getInstance() {
        if (PropertiesModel.instance == null) {
            PropertiesModel.instance = new PropertiesModel();
        }
        return PropertiesModel.instance;
    }

    public Properties getSetpoints() throws IOException {
        InputStream input = new FileInputStream("src/main/resources/Setpoints");
        Properties properties = new Properties();
        properties.load(input);

        return (properties);
    }

    public void saveSetpoints(String power, String work, String amperage, String voltage, String inductiveReactivePower, String capacitiveReactivePower) {
        try (OutputStream outputStream = new FileOutputStream("src/main/resources/Setpoints")) {

            Properties properties = new Properties();

            properties.setProperty("watt", power);
            properties.setProperty("kilowattstunde", work);
            properties.setProperty("ampere", amperage);
            properties.setProperty("volt", voltage);
            properties.setProperty("induktiveBlindleistung", inductiveReactivePower);
            properties.setProperty("kapazitiveBlindleistung", capacitiveReactivePower);

            properties.store(outputStream, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
