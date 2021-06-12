package org.ptyxiakh;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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

    private Task<ObservableList<String>> task;

    public void DataBaseButtonClicked(ActionEvent event)
    {
        progressIndicator.setVisible(true);
        dataLoadingLabel.setVisible(true);
        task = new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() throws Exception
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
                            ok_button.setVisible(true);
                        }
                    }
                });
                return listView;
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

}
