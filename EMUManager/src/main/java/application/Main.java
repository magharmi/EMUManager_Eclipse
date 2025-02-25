package application;

import java.io.*;
import java.net.URL;

import Messgeraet.src.Emu.*;
import net.sf.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.sf.yad2xx.FTDIException;

public class Main extends Application
{	
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Hauptmenue.fxml"));
            
            primaryStage.setTitle("EMU Manager");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
         
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
	
	public static void main(String[] args) throws IOException, FTDIException {
		launch(args);
	}
}