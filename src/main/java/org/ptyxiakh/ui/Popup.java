package org.ptyxiakh.ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * H κλάση Popup εμφανίζει ένα παράθυρο σφάλματος
 * με το κατάλληλο μήνυμα λάθους
 */
public class Popup
{
    Stage popupWindow;
    Label label;
    Button button;
    VBox vBoxLayout;
    Scene scene;

    public Popup()
    {
        this.popupWindow = new Stage();
        this.popupWindow.setResizable(false);
        this.popupWindow.getIcons().add(new Image("error_icon.png"));
        this.popupWindow.initModality(Modality.APPLICATION_MODAL);
        this.label = new Label();
        this.button = new Button("OK");
        button.setOnAction(e -> this.popupWindow.close());
        this.vBoxLayout = new VBox(10);
        this.vBoxLayout.getChildren().addAll(this.label,this.button);
        this.vBoxLayout.setAlignment(Pos.CENTER);
        this.scene = new Scene(this.vBoxLayout,300, 270);
    }

    public void display(int type)
    {
        this.popupWindow.setTitle("ΣΦΑΛΜΑ");
        //Έλεγχος τύπου σφάλματος
        switch (type)
        {
            case -2: this.label.setText("Παρουσιάστηκε κάποιο σφάλμα,παρακαλώ προσπαθήστε ξανά!");
            break;
            case -1: this.label.setText("Ελέγξτε την σύνδεσή σας στο δίκτυο!");
            break;
            case 0: this.label.setText("Τα δεδομένα που ζητάτε δεν υπάρχουν!");
            break;
        }
       this.popupWindow.setScene(this.scene);
        this.popupWindow.showAndWait();
    }
    //Μέθοδος εμφάνισης μηνυμάτων για την αποθήκευση των δεδομένων στη βάση δεδομένων
    public  void sqlPopUp(int type)
    {
        this.popupWindow.setTitle("ΕΙΔΟΠΟΙΗΣΗ");
        //Έλεγχος τύπου σφάλματος
        switch (type)
        {
            case 1: label.setText("Αυτή η εγγραφή υπάρχει ήδη!");
                break;
            case 2: label.setText("Τα δεδομένα αποθηκεύτηκαν!");
                break;
        }
        this.popupWindow.setScene(this.scene);
        this.popupWindow.showAndWait();
    }
}
