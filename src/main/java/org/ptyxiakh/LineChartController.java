package org.ptyxiakh;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class LineChartController implements Initializable
{
    //CategoryAxis class will render non-numerical data in a line chart.
    @FXML
    private   CategoryAxis  xAxis ;
    @FXML
    private  NumberAxis yAxis;
    @FXML
    private LineChart<String,Number> lineChart ;


    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
//        xAxis = new CategoryAxis();
//        yAxis = new NumberAxis();
        XYChart.Series series = new XYChart.Series();
        series.setName("Greece-GDP");
//        lineChart = new LineChart<>(xAxis, yAxis);
//        series.getData().add(new XYChart.Data("Jan", 23));
//        series.getData().add(new XYChart.Data("Feb", 14));
//        series.getData().add(new XYChart.Data("Mar", 15));
//        series.getData().add(new XYChart.Data("Apr", 24));
//        series.getData().add(new XYChart.Data("May", 34));
//        series.getData().add(new XYChart.Data("Jun", 36));
//        series.getData().add(new XYChart.Data("Jul", 22));
//        series.getData().add(new XYChart.Data("Aug", 45));
//        series.getData().add(new XYChart.Data("Sep", 43));
//        series.getData().add(new XYChart.Data("Oct", 17));
//        series.getData().add(new XYChart.Data("Nov", 29));
//        series.getData().add(new XYChart.Data("Dec", 25));
        Map<String,Double> dataMap = JSON.getJsonTableData();
        for (Map.Entry<String,Double> mapEntry : dataMap.entrySet())
            series.getData().add(new XYChart.Data(mapEntry.getKey(),mapEntry.getValue()));
        lineChart.getData().addAll(series);
    }
}
