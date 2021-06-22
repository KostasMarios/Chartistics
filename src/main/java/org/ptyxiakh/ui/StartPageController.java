package org.ptyxiakh.ui;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.ptyxiakh.persistence.Data;
import org.ptyxiakh.persistence.DataQuery;

import java.awt.*;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class StartPageController
{
    @FXML
    private Button db_button;

    @FXML
    private Button e_button;

    @FXML
    private Button buck_button;

    @FXML
    private Button ok_button;

    @FXML
    private Label startpage_label;

    @FXML
    ListView<String> startpage_listView;

    @FXML
    private Label noDataLabel;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Label dataLoadingLabel;

    @FXML
    private Button delete_button;

    private Task<Void> task;

    public void DataBaseButtonClicked(ActionEvent event)
    {
        progressIndicator.setVisible(true);
        dataLoadingLabel.setVisible(true);
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception
            {
                ObservableList<String> listView = FXCollections.observableArrayList();
                listView.setAll(DataQuery.getDataName());

                Platform.runLater(new Runnable() {
                    @Override
                    public void run()
                    {
                        progressIndicator.setVisible(false);
                        dataLoadingLabel.setVisible(false);
                        startpage_listView.setItems(listView);
                        //Εμφάνισε μήνυμα ότι η βάση είναι κενή
                        if(startpage_listView.getItems().isEmpty())
                            noDataLabel.setVisible(true);
                        else
                        {
                            startpage_label.setText("Επίλεξτε 1 από τα δεδομένα:");
                            e_button.setVisible(false);
                            db_button.setVisible(false);
                            startpage_listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                            startpage_listView.setVisible(true);
                            startpage_listView.getSelectionModel().select(0);
                            buck_button.setVisible(true);
                            delete_button.setVisible(true);
                            ok_button.setVisible(true);
                        }
                    }
                });
                return null;
            }
        };
        new Thread(task).start();
    }

    public void InternetDataButtonClicked(ActionEvent event)
    {
        try {
            Stage stage = (Stage) e_button.getScene().getWindow();
            stage.close();
            Stage primaryStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
            primaryStage.setTitle("Chartistics");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public  void okButtonClicked(ActionEvent event)
    {
        Map<String,Double> tableData = new LinkedHashMap<>();
        String dataName= " ";
        String name = startpage_listView.getSelectionModel().getSelectedItem();
        tableData = DataQuery.findData(name);

        try
        {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("dataProcessing.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ok_button.getScene().getWindow();
            stage.close();
            Stage primaryStage = new Stage();
            DataProcessingController dataProcessingController = loader.getController();
            dataProcessingController.setCallFromDatabase(true);
            dataProcessingController.processData(tableData,dataName);
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Chartistics");
            primaryStage.show();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public  void onBuckButton(ActionEvent event)
    {
        try
        {
            Stage stage = (Stage) e_button.getScene().getWindow();
            stage.close();
            Stage primaryStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("startpage.fxml"));
            primaryStage.setTitle("Chartistics");
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.show();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public void onDeleteButton(ActionEvent event)
    {
        progressIndicator.setVisible(true);
        dataLoadingLabel.setText("Διαγραφή...");
        dataLoadingLabel.setVisible(true);
        String name = startpage_listView.getSelectionModel().getSelectedItem();
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception
            {
                ObservableList<String> listView = FXCollections.observableArrayList();
                boolean deleted = false;
                Data dataToDelete = null;
                dataToDelete =DataQuery.findDataToDelete(name);
                deleted =  DataQuery.deleteRecord(dataToDelete);
                if (deleted)
                    listView.setAll(DataQuery.getDataName());
                Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        progressIndicator.setVisible(false);
                        dataLoadingLabel.setVisible(false);
                        if(!listView.isEmpty())
                        {
                            startpage_listView.setItems(listView);
                        }
                        else
                        {
                            dataLoadingLabel.setText("Η διαγραφή απέτυχε!!!");
                            dataLoadingLabel.setFont(javafx.scene.text.Font.font(Font.BOLD));
                        }
                    }
                });
                return null;
            }
        };
        new Thread(task).start();
//        if(deleted)
//        {
            //System.out.println("Data removed!!!");


            //startpage_listView.setItems(listView);
       //}
//        else
            //System.out.println("Data is still there");

    }
}
