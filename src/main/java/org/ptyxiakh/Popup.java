package org.ptyxiakh;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Popup
{
    public static void display(int type)
    {
        Stage popupwindow=new Stage();

        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle("ΣΦΑΛΜΑ");
        Label label1= new Label();

        //Έλεγχος τύπου σφάλματος
        switch (type)
        {
            case -2: label1.setText("Παρουσιάστηκε κάποιο σφάλμα,παρακαλώ προσπαθήστε ξανά!");
            break;
            case -1: label1.setText("Ελένξτε την σύνδεσή σας στο δίκτυο!");
            break;
            case 0: label1.setText("Τα δεδομένα που ζητάτε δεν υπάρχουν!");
            break;
        }

        Button button1= new Button("Close");


        button1.setOnAction(e -> popupwindow.close());



        VBox layout= new VBox(10);


        layout.getChildren().addAll(label1, button1);

        layout.setAlignment(Pos.CENTER);

        Scene scene1= new Scene(layout, 300, 250);

        popupwindow.setScene(scene1);

        popupwindow.showAndWait();
    }

}
