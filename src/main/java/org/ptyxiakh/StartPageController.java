package org.ptyxiakh;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import java.util.LinkedHashMap;
import java.util.List;
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

    public void DataBaseButtonClicked(ActionEvent event)
    {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CHARTISTICS");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Query query = em.createQuery("select d.name from Data d");
        List<String> list = query.getResultList();
        em.close();
        emf.close();

        //Να μην ξεχάσω να εμφανίζω κάτι σε περίπτωση που η βάση είναι κενή
        if(list.isEmpty())
            noDataLabel.setVisible(true);
        else
        {
            startpage_label.setText("Επίλεξτε 1 από τα δεδομένα:");
            e_button.setVisible(false);
            db_button.setVisible(false);
            startpage_listView.getItems().setAll(list);
            startpage_listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            startpage_listView.setVisible(true);
            startpage_listView.getSelectionModel().select(0);
            buck_button.setVisible(true);
            ok_button.setVisible(true);
        }
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CHARTISTICS");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        //System.out.println("Selected data name:"+startpage_listView.getSelectionModel().getSelectedItem());
        String name = startpage_listView.getSelectionModel().getSelectedItem();
        Query query = em.createQuery("select d from Data d");
        List<Data> list = query.getResultList();
        //System.out.println("Mesaurments date for selected item");
        for (Data data: list)
            {
//                System.out.println("Data name:"+ data.getName());
                if(data.getName().equals(name))
                {
                    dataName=data.getName();
                    List<Measurements> listMeasurements = data.getMeasurementsList();
                    for (Measurements measurements : listMeasurements)
//                        System.out.println("Date:" + measurements.getDate() + "Value:" + measurements.getValue());
                        tableData.put(measurements.getDate(),measurements.getValue());
                }
            }
        em.close();
        emf.close();

        try
        {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("dataProcessing.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ok_button.getScene().getWindow();
            DataProcessingController dataProcessingController = loader.getController();
            dataProcessingController.processData(tableData,dataName);
            stage.setScene(scene);
            stage.setTitle("Chartistics");
            stage.show();
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
            primaryStage.show();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }
}
