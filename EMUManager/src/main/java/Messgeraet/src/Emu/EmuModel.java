package Messgeraet.src.Emu;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import Messgeraet.src.JavaFX.FXController;
import net.sf.yad2xx.FTDIException;

public class EmuModel {
    static EmuModel emuModel;

    private EmuModel(){}

    public static EmuModel getInstance()
    {
        if (emuModel == null)
            emuModel = new EmuModel();

        return emuModel;
    }

    EmuConnection emuCon;
    ReturnManager rm = new ReturnManager();
    private boolean requestDone;

    public void connect() throws FTDIException, IOException {
        emuCon = new EmuConnection();
        emuCon.connect();
        System.out.println("Verbindung zum EMU aufgebaut.");
    }

    public void startProgrammingMode() throws FTDIException {
        emuCon.sendProgrammingMode();
        System.out.println("Der Programmiermodus wurde gestartet.");
    }

    public void disconnect() throws FTDIException {
        emuCon.disconnect();
        System.out.println("Verbindung zum EMU abgebrochen.");
    }

    public void readPower() throws FTDIException {
        setRequestDone(false);
        emuCon.sendRequest(new byte[]{1, 82, 49, 2, 49, 46, 55, 46, 49, 40, 41, 3});
        System.out.println("Request Wirkleistung wurde ausgef�hrt.");
    }

    public void readWorkInMeasurementTime() throws FTDIException {
        setRequestDone(false);
        byte[] bytes = "1.8.1()".getBytes(StandardCharsets.US_ASCII);
        emuCon.sendRequest(new byte[]{0x01, 0x52, 0x31, 0x02});    // SOH R1 STX 1.8.1() ETX
        emuCon.sendRequest(bytes);
        emuCon.sendRequest(new byte[]{0x03});

        System.out.println("Request Arbeit im Messzeitraum");
    }

    public void readMeasurementTime() throws FTDIException {
        setRequestDone(false);
        byte[] bytes = "130.130()".getBytes(StandardCharsets.US_ASCII);
        emuCon.sendRequest(new byte[]{0x01, 0x52, 0x31, 0x02});    // SOH R1 STX
        emuCon.sendRequest(bytes);                                    // 130.128()
        emuCon.sendRequest(new byte[]{0x03});                        // ETX

        System.out.println("Request Messzeitdauer");
    }

    public void readLoadGear() throws FTDIException {
        setRequestDone(false);
        byte[] bytes = "P.01(;)".getBytes(StandardCharsets.US_ASCII);
        emuCon.sendRequest(new byte[]{0x01, 0x52, 0x35, 0x02});    // SOH R1 STX
        emuCon.sendRequest(bytes);    //01(;)
        emuCon.sendRequest(new byte[]{0x03});    // ETX

        System.out.println("Request Lastgang");
    }

    public void readCompleteMeasurementData() throws FTDIException {
        setRequestDone(false);
        //ACK 050 CR LF
        byte[] bytes = "000".getBytes(StandardCharsets.US_ASCII);
        emuCon.sendRequest(new byte[]{0x06});    // ACK
        emuCon.sendRequest(bytes);    //000
        emuCon.sendRequest(new byte[]{0x0D, 0x0A});    // CR LF
    }

    public String processAndPutOutOuput(String typ) {
        Thread thread;
        thread = new Thread() {
            public void run() {
                try {
                    while (emuModel.isRequestDone() == false) { // Solange der Request l�uft, wird gewartet
                        sleep(20);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            thread.join(); // Wartet, bis thread fertig ist, bzw. der Request abgeschlossen ist
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String uebergabe = EmuConnection.baos.toString();
        thread.interrupt();
        return rm.get(typ, uebergabe);
    }

    public boolean isRequestDone() {
        return requestDone;
    }

    public void setRequestDone(boolean requestDone) {
        this.requestDone = requestDone;
    }
}
