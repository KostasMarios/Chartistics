package org.ptyxiakh.ui;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.ptyxiakh.persistence.Data;
import org.ptyxiakh.persistence.DataQuery;

import java.awt.*;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
/*This class contains methods for the start screen specifically
 *for importing data from stored or the Internet
 * */
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
        /*Using the Task to display the stored data
        *the process will be done in a background thread
        * */
        task = new Task<Void>()
        {
            @Override
            protected Void call() throws Exception
            {
                ObservableList<String> listView = FXCollections.observableArrayList();
                listView.setAll(DataQuery.getDataName());
                /*Using the runLater() method,
                 *the JavaFx thread changes the scene
                 **/
                Platform.runLater(new Runnable() {
                    @Override
                    public void run()
                    {
                        progressIndicator.setVisible(false);
                        dataLoadingLabel.setVisible(false);
                        startpage_listView.setItems(listView);
                        //Show message that base is empty
                        if(startpage_listView.getItems().isEmpty())
                            noDataLabel.setVisible(true);
                        else
                        {
                            startpage_label.setText("???????????????? 1 ?????? ???? ????????????????:");
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
        /*Scene change process */
        try {
            Stage stage = (Stage) e_button.getScene().getWindow();
            stage.close();
            Stage primaryStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("internetData.fxml"));
            primaryStage.setTitle("Chartistics");
            primaryStage.getIcons().add(new Image("statistics_icon.png"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }
    /*This method is called when
     *the user selects stored data
     **/
    public  void okButtonClicked(ActionEvent event)
    {
        Map<String,Double> tableData = new LinkedHashMap<>();
        String dataName= " ";
        dataName = startpage_listView.getSelectionModel().getSelectedItem();
        tableData = DataQuery.findData(dataName);
        /*Display the data processing screen
         *and create a data Processing Controller object
         **/
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
            primaryStage.getIcons().add(new Image("statistics_icon.png"));
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
            primaryStage.getIcons().add(new Image("statistics_icon.png"));
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
        dataLoadingLabel.setText("???????????????? ??????????????????...");

        dataLoadingLabel.setVisible(true);
        String name = startpage_listView.getSelectionModel().getSelectedItem();
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception
            {
                ObservableList<String> listView = FXCollections.observableArrayList();
                boolean deleted ;
                Data dataToDelete;
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
                        if(deleted)
                        {
                            startpage_listView.setItems(listView);
                            if (startpage_listView.getItems().isEmpty())
                            {
                                startpage_listView.setVisible(false);
                                noDataLabel.setVisible(true);
                            }
                        }
                        else
                        {
                            dataLoadingLabel.setText("?? ???????????????? ??????????????!!!");
                            dataLoadingLabel.setFont(javafx.scene.text.Font.font(Font.BOLD));
                            dataLoadingLabel.setVisible(true);
                        }
                    }
                });
                return null;
            }
        };
        new Thread(task).start();
    }
}
