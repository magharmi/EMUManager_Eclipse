package Messgeraet.src.Emu;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReturnManager {
    String ergebnis;
    String returnStringNeu;
    public static Map<String, Measurement> messdaten = new LinkedHashMap<String, Measurement>();

    public String get(String typ, String returnString) {
        int beginIndex = 0;
        int endIndex = 0;
        String ohneKlammern, ohneKomma;
        ohneKomma = returnString.replaceAll(", ", "");
        beginIndex = ohneKomma.lastIndexOf("(") + 1;
        endIndex = ohneKomma.indexOf("*");
        if (typ == "Watt") {
            ohneKlammern = ohneKomma.substring(beginIndex, endIndex) + " W";
            ergebnis = "Die momentane Wirkleistung betr�gt: " + ohneKlammern;
        } else if (typ == "kWh") {
            ohneKlammern = ohneKomma.substring(beginIndex, endIndex) + " kWh";
            ergebnis = "Die Arbeit im Messzeitraum betr�gt: " + ohneKlammern;
        } else if (typ == "Tage") {
            ohneKlammern = ohneKomma.substring(beginIndex, endIndex) + " Sekunden";
            ergebnis = "Die Messdauer betr�gt: " + ohneKlammern;
        } else if (typ == "Last") {
            returnStringNeu = returnString;
            ergebnis = lastgangVerarbeiten(returnString);

        }
        return ergebnis;
    }

    public String lastgangVerarbeiten(String returnString) {
        boolean neu = true;
        String watt = null, ampere = null, volt = null, induktiveBlindleistung = null, kapazitiveBlindleistung = null, zeit = null;
        while (!(returnString == null)) {
            if (neu == true) {    //wird beim ersten Aufruf von lastgangVerarbeiten ausgef�hrt
                zeit = getZeit(returnString);
                zeit = LocalDateTime.parse("20" + zeit.substring(1), DateTimeFormatter.ofPattern("yyyyMMddHHmmss")).toString();    //Passe die Zeit an das Pattern an: zJJMMTThhmm zu yyyyMMddHHmmss
                neu = false;
                returnString = returnStringNeu;
            } else if (returnString.substring(0, returnString.indexOf("\n") + 1).length() == 41) {    //(0000.3)(00.040)(236.3)(0000.0)(0009.6)
                watt = getW(returnString);
                returnString = returnStringNeu;
            } else if (returnString.substring(0, returnString.indexOf("\n") + 1).length() == 33) {    //(00.040)(236.3)(0000.0)(0009.6)
                ampere = getA(returnString);
                returnString = returnStringNeu;
            } else if (returnString.substring(0, returnString.indexOf("\n") + 1).length() == 25) {    //(236.3)(0000.0)(0009.6)
                volt = getV(returnString);
                returnString = returnStringNeu;
            } else if (returnString.substring(0, returnString.indexOf("\n") + 1).length() == 18) {  //(0000.0)(0009.6)
                induktiveBlindleistung = getInduktiveBlindleistung(returnString);
                returnString = returnStringNeu;
            }
            else if (returnString.substring(0, returnString.indexOf("\n") + 1).length() == 10) {  //(0009.6)
                kapazitiveBlindleistung = getKapazitiveBlindleistung(returnString);
                Measurement measurement = new Measurement(watt, ampere, volt, induktiveBlindleistung, kapazitiveBlindleistung);    //Lege Objekt mit Messdaten an
                messdaten.put(zeit, measurement);    //F�ge das Objekt mit den Messdaten in die Map mit der jeweiligen Zeit
                System.out.println(zeit);
                zeit = LocalDateTime.parse(zeit, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")).plusMinutes(3).toString();    //Erh�he die Zeit um 3 Minuten
                returnString = returnStringNeu;
            }
            else if (returnString.substring(0, returnString.indexOf("\n") + 1).length() == 78) {    //P.01(0191128152324)(00)(01)(5)(1.5)(W)(11.5)(A)(12.5)(V)(3.5)(VAR)(4.5)(VAR)
                zeit = getZeit(returnString);
                zeit = LocalDateTime.parse("20" + zeit.substring(1), DateTimeFormatter.ofPattern("yyyyMMddHHmmss")).toString();    //Passe die Zeit an das Pattern an: zJJMMTThhmm zu yyyyMMddHHmmss
                returnString = returnStringNeu;
            } else if (returnString.substring(0, returnString.indexOf("\n") + 1).length() == 74) {    //(0191128152324)(00)(01)(5)(1.5)(W)(11.5)(A)(12.5)(V)(3.5)(VAR)(4.5)(VAR)
                zeit = getZeit(returnString);
                zeit = LocalDateTime.parse("20" + zeit.substring(1), DateTimeFormatter.ofPattern("yyyyMMddHHmmss")).toString();    //Passe die Zeit an das Pattern an: zJJMMTThhmm zu yyyyMMddHHmmss
                returnString = returnStringNeu;
            }
        }

        // Komplette Messdaten ausgeben
        for (String key : messdaten.keySet()) {
            System.out.println(key);
            System.out.println("Watt: " + messdaten.get(key).getPower() + "\tAmpere: " + messdaten.get(key).getAmperage() + "\tVolt: " + messdaten.get(key).getVoltage() + "\tIB: " + messdaten.get(key).getInductiveReactivePower() + "\tKB: " + messdaten.get(key).getCapacitiveReactivePower());
        }

        return "Lastgang erfolgreich ausgelesen";
    }

    public String getZeit(String returnString) {
        String ohneKomma = "";
        String zeit = "";
        int ersteKlammer, zweiteKlammer, newLine;

        ohneKomma = returnString.replaceAll(", ", "");    //alle Komma weg
        ersteKlammer = ohneKomma.indexOf("(");    //Index von (
        zweiteKlammer = ohneKomma.indexOf(")", ersteKlammer + 1);    //Index von zweiter )
        zeit = ohneKomma.substring(ersteKlammer + 1, zweiteKlammer);    //Zeit rausfiltern
        newLine = ohneKomma.indexOf("\n");    //Index von Newline
        returnStringNeu = ohneKomma.replace(ohneKomma.substring(0, newLine + 1), "");    //Die oberste Zeile entfernen

        return zeit;
    }

    public String getW(String returnString) {
        String watt = "";
        String ohneKlammerAuf, ohneKlammerZu;
        int ersteKlammer, zweiteKlammer;

        ersteKlammer = returnString.indexOf("(");    //Index von (
        zweiteKlammer = returnString.indexOf(")", ersteKlammer + 1);    //Index von zweiter )
        watt = returnString.substring(ersteKlammer + 1, zweiteKlammer);    //Watt rausfiltern
        ohneKlammerAuf = returnString.replaceFirst("\\(", "");
        ohneKlammerZu = ohneKlammerAuf.replaceFirst("\\)", "");
        returnStringNeu = ohneKlammerZu.replaceFirst(ohneKlammerZu.substring(0, ohneKlammerZu.indexOf("(")), "");    //Die oberste Zeile entfernen bzw. Watt entfernen

        return watt;
    }

    public String getA(String returnString) {
        String ampere = "";
        String ohneKlammerAuf, ohneKlammerZu;
        int ersteKlammer, zweiteKlammer;

        ersteKlammer = returnString.indexOf("(");    //Index von (
        zweiteKlammer = returnString.indexOf(")", ersteKlammer + 1);    //Index von zweiter )
        ampere = returnString.substring(ersteKlammer + 1, zweiteKlammer);    //Ampere rausfiltern
        ohneKlammerAuf = returnString.replaceFirst("\\(", "");
        ohneKlammerZu = ohneKlammerAuf.replaceFirst("\\)", "");
        returnStringNeu = ohneKlammerZu.replaceFirst(ohneKlammerZu.substring(0, ohneKlammerZu.indexOf("(")), "");    //Die oberste Zeile entfernen

        return ampere;
    }

    public String getV(String returnString) {
        String volt = "";
        String ohneKlammerAuf, ohneKlammerZu;
        int ersteKlammer, zweiteKlammer;

        ersteKlammer = returnString.indexOf("(");    //Index von (
        zweiteKlammer = returnString.indexOf(")", ersteKlammer + 1);    //Index von zweiter )
        volt = returnString.substring(ersteKlammer + 1, zweiteKlammer);    //Ampere rausfiltern
        ohneKlammerAuf = returnString.replaceFirst("\\(", "");
        ohneKlammerZu = ohneKlammerAuf.replaceFirst("\\)", "");
        returnStringNeu = ohneKlammerZu.replaceFirst(ohneKlammerZu.substring(0, ohneKlammerZu.indexOf("(")), "");    //Volt entfernen

        return volt;
    }

    public String getInduktiveBlindleistung(String returnString) {
        String induktiveBlindleistung = "";
        String ohneKlammerAuf, ohneKlammerZu;
        int ersteKlammer, zweiteKlammer;

        ersteKlammer = returnString.indexOf("(");    //Index von (
        zweiteKlammer = returnString.indexOf(")", ersteKlammer + 1);    //Index von zweiter )
        induktiveBlindleistung = returnString.substring(ersteKlammer + 1, zweiteKlammer);    //induktiveBlindleistung rausfiltern
        ohneKlammerAuf = returnString.replaceFirst("\\(", "");
        ohneKlammerZu = ohneKlammerAuf.replaceFirst("\\)", "");
        returnStringNeu = ohneKlammerZu.replaceFirst(ohneKlammerZu.substring(0, ohneKlammerZu.indexOf("(")), "");    //induktiveBlindleistung entfernen

        return induktiveBlindleistung;
    }

    public String getKapazitiveBlindleistung(String returnString) {
        String kapazitiveBlindleistung = "";
        String ohneKlammerAuf, ohneKlammerZu;
        int ersteKlammer, zweiteKlammer;

        ersteKlammer = returnString.indexOf("(");    //Index von (
        zweiteKlammer = returnString.indexOf(")", ersteKlammer + 1);    //Index von zweiter )
        kapazitiveBlindleistung = returnString.substring(ersteKlammer + 1, zweiteKlammer);    //kapazitiveBlindleistung rausfiltern
        ohneKlammerAuf = returnString.replaceFirst("\\(", "");
        ohneKlammerZu = ohneKlammerAuf.replaceFirst("\\)", "");
        System.out.println(ohneKlammerZu);
        try {
            returnStringNeu = ohneKlammerZu.replaceFirst(ohneKlammerZu.substring(0, ohneKlammerZu.indexOf("(")), "");    //2. Zeichenkette entfernen
        } catch (Exception e) {
            returnStringNeu = null;    //Abbruchbedingung setzen
        }

        return kapazitiveBlindleistung;
    }

    public Map<String, Measurement> getMessdaten() {
        return messdaten;
    }

    public void setMessdaten(Map<String, Measurement> messdaten) {
        this.messdaten = messdaten;
    }
}
