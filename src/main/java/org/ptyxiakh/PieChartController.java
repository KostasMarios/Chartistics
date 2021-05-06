package org.ptyxiakh;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class PieChartController implements Initializable
{
    @FXML
    private PieChart pieChart;
    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     */
    //When the Scene is loaded it will call initialize method
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

        Map<String,Double> dataMap = JSON.getJsonTableData();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String,Double> mapEntry : dataMap.entrySet())
          pieChartData.add(new PieChart.Data(mapEntry.getKey(), mapEntry.getValue()));

//        (
////                dataMap.forEach((k,v)->
////                {
////                    new PieChart.Data(k,v);
////                })
//           new PieChart.Data("Grapefruit", 13),
//           new PieChart.Data("Oranges", 25),
//           new PieChart.Data("Plums", 10),
//           new PieChart.Data("Pears", 22),
//           new PieChart.Data("Apples", 30)
//        );


        pieChart.setData(pieChartData);

    }
}
