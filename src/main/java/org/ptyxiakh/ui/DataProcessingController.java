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
    //Κουμπί επιστροφής στην προηγούμενη οθόνη
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
    //Κουμπί για έναρξη της διαδικασίας ελέγχου
    //της στατικότητας των δεδομένων με τον
    //αλγόριθμο Augmented Dickey Fuller
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
    //Δήλωση παραμέτρων Arima
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
        qParameterText.setText("Για την παράμετρο q βάζουμε το lag που είναι\nπάνω από το θετικό όριο.");
        qParameterText.setVisible(false);
       firstStepLabel.setText("1)ΕΛΕΓΧΟΣ ΣΤΑΘΕΡΟΤΗΤΑΣ ΤΩΝ ΔΕΔΟΜΕΝΩΝ ME\nTON ΑΛΓΟΡΙΘΜΟ AUGMENTED DICKEY FULLER");
//       lagTextLabel.setText("Εισάγεται την τιμή για το Lag(με τιμή <="+(int)(12*(Math.pow((tableData.size()/100),(1/4))))+"):");
    }
    //Η μέθοδος αυτή χρησιμοποιείτε για να εντοπιστεί
   //εάν η χρήση τις κλάσης γίνεται με δεδομένα της βάσης δεδομένων ή του διαδικτύου
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

        //Συμπλήρωσε με μεμονωμένες τιμές την στήλη ημ/νία
        dateColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Double>, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, Double>, String> p)
            {
                /*Επιστροφή ιδιότητας για ένα μόνο κελί,
                 *δεν μπορούμε να χρησιμοποιήσουμε βρόχο εδώ
                 *Για την πρώτη στήλη χρησιμοποιούμε το κλειδί της απεικόνισης*/
                return new SimpleStringProperty(p.getValue().getKey());
            }
        });
        //Συμπλήρωσε τη στήλη με τις μετρήσεις
        measurementsColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Double>, Double>, ObservableValue<Double>>()
        {
            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<Map.Entry<String, Double>, Double> p)
            {
                // Για τη δεύτερη στήλη χρησιμοποιούμε την τιμή του Map
                return new SimpleObjectProperty(p.getValue().getValue());
            }
        });

        //Δημιουργία ObservableArrayList με τα δεδομένα του Map
        ObservableList<Map.Entry<String, Double>> items = FXCollections.observableArrayList(tableDataParam.entrySet());
        //Βάλε τα δεδομένα του ObservableList στο TableView
        dataTable.getItems().addAll(items);

        //Επιλογή διαγράμματος
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
    //Μέθοδος επιστροφής στην προηγούμενη σκηνή
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

    //Μέθοδος αποθήκευσης των δεδομένων
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
            //Διαδικασία μετατροπής Double --> double
            //1)Αποθηκεύουμε τις τιμές της απεικόνισης στη συλλογή Double
            DoublesCollection = tableData.values();
            //2)Μετατροπή της συλλογής σε πίνακα
            DoublesArray = DoublesCollection.toArray(new Double[DoublesCollection.size()]);
            //3)Μετατροπή του πίνακα από αντικείμενα Double σε πρωτόγονo τύπο double
            doublePrimitiveArray = ArrayUtils.toPrimitive(DoublesArray);
            //O Αλγόριθμος AugmentedDickeyFuller με βάση τον αριθμό των lag ελέγχει εάν τα δεδομένα
            //είναι σταθερά(stationary)
        for(int i=1;i<=lagValue;i++)
        {
            AugmentedDickeyFuller augmentedDickeyFuller = new AugmentedDickeyFuller(doublePrimitiveArray, lagValue);
            //Εάν βρεθεί σε κάποιο lag ότι τα δεδομένα είναι σταθερά αύξησε τον μετρητή
            if (!augmentedDickeyFuller.isNeedsDiff())
                stationaryCounter++;
        }
            //Διαδικασία επεξεργασίας των δεδομένων όταν αυτά δεν είναι σταθερά
            if(stationaryCounter==0)
            {
                //Στο Label είναι δυνατή η αλλαγή γραμμής με τη χρήση \n
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

    //Διαδικασία υλοποίησης της μεθόδου διαφοράς, εμφάνιση διαγράμματος αυτοσυσχέτισης
    //και έλεγχος σταθερότητας
    public void startDifferencing(ActionEvent event)
    {
        //Μετρητής σταθερότητας
        int times=0;
        int stationarityCounterAcf=0;
        isStationaryLabel.setVisible(false);
        //Μέγιστη τιμή σφάλματος
        //int lagAcf = (int)(12*(Math.pow((tableData.size()/100),(1/4))));

        //Ο χρήστης δίνει πόσες φορές θα εφαρμοστή η διαφορά
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

            //Έλεγχος με τον AugmentedDickeyFuller της σταθερότητας των δεδομένων
            //που έγινε η χρήση της μεθόδου της διαφοράς
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
                //Εμφάνισε το διάγραμμα αυτοσυσχέτισης
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

    //Καλούμε τη μέθοδο όταν τα δεδομένα είναι σταθερά
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
        //Έλεγχος ύπαρξης τιμής στις παραμέτρους
        if (qParameterTextField.getText().isEmpty() || pParameterTextField.getText().isEmpty() || forecastSizeTextField.getText().isEmpty())
        {
            arimaLabel.setText("Βάλτε τιμή στις παραμέτρους!!!");
            arimaLabel.setVisible(true);
        }
        //Έλεγχος ύπαρξης αρνητικής τιμής στις παραμέτρους
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
