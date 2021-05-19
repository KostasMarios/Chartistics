package org.ptyxiakh;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        //Double doubleObj = 45.567832;
        //Με το doubleValue() να δοκιμάσω να μετατρέψω τa Double
        //double d = doubleObj.doubleValue();
        //System.out.println(d);
        //width:640,height:480
        scene = new Scene(loadFXML("startpage"));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Chartistics");
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}