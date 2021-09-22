package org.ptyxiakh.ui;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.lang3.ArrayUtils;
import org.ptyxiakh.businessInfrastructure.AugmentedDickeyFuller;
import org.ptyxiakh.businessInfrastructure.Plots;
import org.ptyxiakh.businessInfrastructure.TimeSeries;
import org.ptyxiakh.domain.Arima;
import org.ptyxiakh.domain.ArimaParams;
import org.ptyxiakh.domain.ForecastResult;
import org.ptyxiakh.persistence.DataQuery;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class DataProcessingController implements Initializable
{
    //Return button to the previous screen
    @FXML
    private Button buckButton;
    @FXML
    private Button saveButton;
    @FXML
    private RadioButton lineChartButton;
    @FXML
    private RadioButton pieChartButton;
    @FXML
    private ToggleGroup diagramType;
    @FXML
    private TableView<Map.Entry<String,Double>> dataTable = new TableView<>();
    @FXML
    private  TableColumn<Map.Entry<String, Double>, String> dateColumn = new TableColumn<>("Ημ/νία");
    @FXML
    private TableColumn<Map.Entry<String, Double>, Double> measurementsColumn = new TableColumn<>("Μετρήσεις");
    @FXML
    private AnchorPane chartsContainer;
    @FXML
    private Label messageLabel;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private  LineChart<String,Number> lineChart;
    @FXML
    private PieChart pieChart;
    @FXML
    private Label dataNameLabel;
    //Button to start the static data control process
   // with the Augmented Dickey Fuller algorithm
    @FXML
    private Button augmentedDickeyFullerButton;
    @FXML
    private Button differencingButton;
    @FXML
    private Label stationarityLabel;
    @FXML
    private Label firstStepLabel;
    @FXML
    private Label nextStepLabel;
    @FXML
    private Label thirdStepLabel;
    @FXML
    private Label qParameterLabel;
    @FXML
    private Label qParameterText;
    @FXML
    private Label differencingValueLabel;
    @FXML
    private TextArea differenceHelperText;
    @FXML
    private TextField differenceTextField;
    @FXML
    private  TextField qParameterTextField;
    @FXML
    private Label fourthStepLabel;
    @FXML
    private Button acfPlotButton;
    @FXML
    private Button pacfPlotButton;
    @FXML
    private Label pParameterLabel;
    @FXML
    private TextField pParameterTextField;
    @FXML
    private Label pParameterText;
    @FXML
    private Label arimaStepLabel;
    @FXML
    private Button arimaButton;
    @FXML
    private Label arimaLabel;
    @FXML
    private Label forecastSizeLabel;
    @FXML
    private TextField forecastSizeTextField;
    @FXML
    private Label isStationaryLabel;
    @FXML
    private Label savingDataLabel;
    @FXML
    private ProgressIndicator savingDataIndicator;
    private Collection<Double> DoublesCollection;
    private Double[] DoublesArray;
    private double[] doublePrimitiveArray;
    private  Map<String,Double> tableData;
    private int lagValue;
    private boolean netSource;
    //Arima parameter declaration
    int d,q,p;
    private Task<Void> task;
    private boolean callFromDatabase = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        d=0;
        q=0;
        p=0;
        netSource=false;
        qParameterText.setText("Για την παράμετρο q βάζουμε το lag που είναι θετικό.");
        qParameterText.setVisible(false);
       firstStepLabel.setText("1)ΕΛΕΓΧΟΣ ΣΤΑΘΕΡΟΤΗΤΑΣ ΤΩΝ ΔΕΔΟΜΕΝΩΝ ME\nTON ΑΛΓΟΡΙΘΜΟ AUGMENTED DICKEY FULLER");
//       lagTextLabel.setText("Εισάγεται την τιμή για το Lag(με τιμή <="+(int)(12*(Math.pow((tableData.size()/100),(1/4))))+"):");
    }
    //Use this method to determine if the class
    // is being used with database or internet data
    public void setNetSource(boolean netSource) {
        this.netSource = netSource;
    }

    public void setCallFromDatabase(boolean callFromDatabase)
    {
        this.callFromDatabase = callFromDatabase;
    }

    public void processData(Map<String,Double> tableDataParam, String dataName)
    {
        if(callFromDatabase)
        {
            saveButton.setVisible(false);
        }
            tableData=tableDataParam;
            dataNameLabel.setText(dataName);

        //Fill in the date column with individual values
        dateColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Double>, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, Double>, String> p)
            {
                /*Return property for a single cell,
                 *we can not use a loop here.
                 *For the first column we use the display key */
                return new SimpleStringProperty(p.getValue().getKey());
            }
        });
        //Fill in the column with the measurements
        measurementsColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Double>, Double>, ObservableValue<Double>>()
        {
            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<Map.Entry<String, Double>, Double> p)
            {
                //For the second column we use the Map value
                return new SimpleObjectProperty(p.getValue().getValue());
            }
        });

        //Create ObservableArrayList with Map data
        ObservableList<Map.Entry<String, Double>> items = FXCollections.observableArrayList(tableDataParam.entrySet());
        //Put the ObservableList data in TableView
        dataTable.getItems().addAll(items);

        //Chart selection
        diagramType.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
        {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle old_toggle, Toggle new_toggle)
            {
                if(diagramType.getSelectedToggle()!= null)
                {
                    if(lineChartButton.isSelected())
                    {
                        pieChart.setVisible(false);
                        XYChart.Series<String,Number> seriesLineChart = new XYChart.Series<>();
                        messageLabel.setVisible(false);
                        lineChart.setVisible(true);
                        lineChart.setTitle(dataName);
                        for(Map.Entry<String,Double> mapEntry : tableDataParam.entrySet())
                        {
                            seriesLineChart.getData().add(new XYChart.Data(mapEntry.getKey(),mapEntry.getValue()));
                        }
                        lineChart.getData().addAll(seriesLineChart);
                    }
                    else
                    {
                        lineChart.setVisible(false);
                        messageLabel.setVisible(false);
                        pieChart.setVisible(true);
                        pieChart.setTitle(dataName);
                        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

                        for(Map.Entry<String,Double> mapEntry : tableDataParam.entrySet())
                        {
                            pieChartData.add(new PieChart.Data(mapEntry.getKey(),mapEntry.getValue()));
                        }

                        pieChart.setData(pieChartData);
                    }
                }
            }
        });
    }
    //Return to the previous scene method
    public void onBuckButton(ActionEvent event)
    {
        try
        {
            if(netSource)
            {
                Parent basePageParent = FXMLLoader.load(getClass().getResource("internetData.fxml"));
                Scene basePageScene = new Scene(basePageParent);
                Stage dataProcessingPage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                dataProcessingPage.hide();
                dataProcessingPage.setScene(basePageScene);
                dataProcessingPage.show();
            }
            else
            {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("startpage.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) buckButton.getScene().getWindow();
                StartPageController startPageController = loader.getController();
                startPageController.DataBaseButtonClicked(event);
                stage.setScene(scene);
                stage.setResizable(false);
                stage.setTitle("Chartistics");
                stage.show();
            }
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    //Data storage method
    public void saveData(ActionEvent event)
    {
        savingDataLabel.setVisible(true);
        savingDataIndicator.setVisible(true);
        task = new Task<Void>()
        {
            @Override
            protected Void call() throws Exception
            {
                int savingProcess;
                savingProcess = DataQuery.create(dataNameLabel.getText(),tableData);
                Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        savingDataLabel.setVisible(false);
                        savingDataIndicator.setVisible(false);
                        Popup popup = new Popup();
                        popup.sqlPopUp(savingProcess);
                    }
                });
                return null;
            }
        };
        new Thread(task).start();
    }

    public void startAugmentedDickeyFuller(ActionEvent event)
    {
        int stationaryCounter=0;
//        lagWarningLabel.setVisible(false);
//        lagNegativeLabel.setVisible(false);
        stationarityLabel.setVisible(false);
        differencingButton.setVisible(false);
        lagValue= (int) (12*(Math.pow((tableData.size()/100),(1/4))));
//        if(lagTextField.getText().isEmpty())
//            lagWarningLabel.setVisible(true);
//        else if (lagTextField.getText().startsWith("-"))
//            lagNegativeLabel.setVisible(true);
//        else
//        {
//            lagValue = Integer.parseInt(lagTextField.getText());
            //Conversion process Double --> double
            //1)We save the values of the Map in the Double collection
            DoublesCollection = tableData.values();
            //2)Convert the collection to a table
            DoublesArray = DoublesCollection.toArray(new Double[DoublesCollection.size()]);
            //3)Convert table from Double objects to primitive double type
            doublePrimitiveArray = ArrayUtils.toPrimitive(DoublesArray);
            //The AugmentedDickeyFuller Algorithm based on the number of lag
           // checks if the data are stationary
        for(int i=1;i<=lagValue;i++)
        {
            AugmentedDickeyFuller augmentedDickeyFuller = new AugmentedDickeyFuller(doublePrimitiveArray, lagValue);
            //If it is found in a lag that the data are stationary increase the counter
            if (!augmentedDickeyFuller.isNeedsDiff())
                stationaryCounter++;
        }
            //Data processing process when they are not stationary
            if(stationaryCounter==0)
            {
                //In the Label it is possible to change the line using \n
                stationarityLabel.setText("Τα δεδομένα δεν είναι σταθερά.\nΘα γίνει η κατάλληλη μετατροπή στο 2ο βήμα.");
                stationarityLabel.setVisible(true);
                nextStepLabel.setText("2)H ΜΕΘΟΔΟΣ ΤΗΣ ΔΙΑΦΟΡΑΣ ΚΑΙ\nΕΛΕΓΧΟΣ ΜΕ ΔΙΑΓΡΑΜΜΑ ΑΥΤΟΣΥΣΧΕΤΙΣΗΣ");
                nextStepLabel.setVisible(true);
                differenceHelperText.setVisible(true);
                differencingValueLabel.setVisible(true);
                differenceTextField.setVisible(true);
                differencingButton.setVisible(true);
            }
            else
            {
                stationarityLabel.setText("Τα δεδομένα είναι σταθερά,οπότε d=0.\nΤο 2ο βήμα θα παραληφθεί.");
                stationarityLabel.setVisible(true);
                thirdStepLabel.setText("3)ΔΙΑΓΡΑΜΜΑ ΑΥΤΟΣΥΣΧΕΤΙΣΗΣ ΚΑΙ ΕΙΣΑΓΩΓΗ ΤΙΜΗΣ MA(q)");
                thirdStepLabel.setVisible(true);
                acfPlotButton.setVisible(true);
                qParameterLabel.setVisible(true);
                qParameterText.setVisible(true);
                qParameterTextField.setVisible(true);
                fourthStepLabel.setVisible(true);
                pacfPlotButton.setVisible(true);
                pParameterTextField.setVisible(true);
                pParameterLabel.setVisible(true);
                pParameterText.setVisible(true);

                pacfPlotButton.setOnAction(new EventHandler<ActionEvent>()
                {
                    @Override
                    public void handle(ActionEvent event)
                    {
                        Plots.plotPacf( doublePrimitiveArray,lagValue);
                        arimaStepLabel.setVisible(true);
                        arimaButton.setVisible(true);
                        forecastSizeLabel.setVisible(true);
                        forecastSizeTextField.setVisible(true);
                    }
                });
            }
    }

    //Procedure for implementing the difference method,
    //displaying an autocorrelation diagram and checking stationarity
    public void startDifferencing(ActionEvent event)
    {
        //Stationarity meter
        int times=0;
        int stationarityCounterAcf=0;
        isStationaryLabel.setVisible(false);
        //Maximum error value
        //int lagAcf = (int)(12*(Math.pow((tableData.size()/100),(1/4))));

        //The user gives how many times the difference will be applied
        if (differenceTextField.getText().isEmpty())
        {
            isStationaryLabel.setText("Εισάγεται τιμή για τη διαφορά d!!!");
            isStationaryLabel.setVisible(true);
        }
        else if (Integer.parseInt(differenceTextField.getText())<0)
        {
            isStationaryLabel.setText("Εισάγεται θετική τιμή για τη διαφορά d !!!");
            isStationaryLabel.setVisible(true);
        }
        else
        {
            times = Integer.parseInt(differenceTextField.getText());

            TimeSeries timeSeries = new TimeSeries(doublePrimitiveArray);
            TimeSeries differencedTimeSeries = timeSeries.difference(1, times);
            double[] differencedData = TimeSeries.difference(doublePrimitiveArray, 1, times);

            //AugmentedDickeyFuller test of data stationarity
            for (int i = 1; i <= 12; i++) {
                AugmentedDickeyFuller augmentedDickeyFuller = new AugmentedDickeyFuller(differencedData, i);
                if (!augmentedDickeyFuller.isNeedsDiff())
                {
                    stationarityCounterAcf++;
                }
            }
            if (stationarityCounterAcf == 0)
            {
                isStationaryLabel.setText("Τα δεδομένα δεν είναι σταθερά!");
                isStationaryLabel.setVisible(true);
                thirdStepLabel.setVisible(false);
                qParameterLabel.setVisible(false);
                qParameterText.setVisible(false);
                qParameterTextField.setVisible(false);
                fourthStepLabel.setVisible(false);
                pacfPlotButton.setVisible(false);
                pParameterTextField.setVisible(false);
                pParameterLabel.setVisible(false);
                pParameterText.setVisible(false);
            }
            else
            {
                isStationaryLabel.setText("Τα δεδομένα είναι σταθερά!");
                isStationaryLabel.setVisible(true);
                //Display the autocorrelation diagram
                Plots.plotAcf(differencedTimeSeries, lagValue);
                thirdStepLabel.setText("3)ΕΙΣΑΓΕΤΑΙ ΤΗΝ ΤΙΜΗ ΜA(q)");
                thirdStepLabel.setVisible(true);
                qParameterLabel.setVisible(true);
                qParameterText.setVisible(true);
                qParameterTextField.setVisible(true);
                fourthStepLabel.setVisible(true);
                pacfPlotButton.setVisible(true);
                pParameterTextField.setVisible(true);
                pParameterLabel.setVisible(true);
                pParameterText.setVisible(true);

                pacfPlotButton.setOnAction(new EventHandler<ActionEvent>()
                {
                    @Override
                    public void handle(ActionEvent event)
                    {
                        Plots.plotPacf(differencedData, lagValue);
                        arimaStepLabel.setVisible(true);
                        arimaButton.setVisible(true);
                        forecastSizeLabel.setVisible(true);
                        forecastSizeTextField.setVisible(true);
                    }
                });

            }
        }
    }

    //We call the method when the data are stationary
    public void startAcfPlot(ActionEvent event)
    {

        TimeSeries timeSeries = new TimeSeries(doublePrimitiveArray);
        Plots.plotAcf(timeSeries,lagValue);

    }
    public void startArima(ActionEvent event)
    {
        int count = 1;
        DecimalFormat df = new DecimalFormat("#.##");
        String arimaResult = "Πρόβλεψη:";
        //Check the existence of a value in the parameters
        if (qParameterTextField.getText().isEmpty() || pParameterTextField.getText().isEmpty() || forecastSizeTextField.getText().isEmpty())
        {
            arimaLabel.setText("Βάλτε τιμή στις παραμέτρους!!!");
            arimaLabel.setVisible(true);
        }
        //Check for a negative value in the parameters
        else if (Integer.parseInt(qParameterTextField.getText())<0 || Integer.parseInt(pParameterTextField.getText())<0 || Integer.parseInt(forecastSizeTextField.getText())<0)
        {
            arimaLabel.setText("Αρνητική τιμή σε παράμετρο!!!");
            arimaLabel.setVisible(true);
        }
        else
        {
            arimaLabel.setVisible(false);
            if (differenceTextField.getText().isEmpty())
                d=0;
            else
                d=Integer.parseInt(differenceTextField.getText());
            ArimaParams arimaParams = new ArimaParams(Integer.parseInt(pParameterTextField.getText()),d,Integer.parseInt(qParameterTextField.getText()),0,0,0,0);
            ForecastResult forecastResult = Arima.forecast_arima(doublePrimitiveArray,Integer.parseInt(forecastSizeTextField.getText()),arimaParams);
            double[] forecastData = forecastResult.getForecast();
            for (int i=0;i<forecastData.length;i++)
            {
                arimaResult +=df.format(forecastData[i]);
                if (count < forecastData.length)
                    arimaResult+=" ";
                count++;
            }
             //arimaResult+=".Τιμή RMSE:"+df.format(forecastResult.getRMSE());
            arimaLabel.setText(arimaResult.replaceAll(",","."));
            arimaLabel.setVisible(true);
        }
    }
}
