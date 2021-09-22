package org.ptyxiakh.ui;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.ptyxiakh.businessInfrastructure.ReadFiles;
import org.ptyxiakh.domain.JSON;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/*
 * With the Internet Data Controller the user selects the data he wants for a country or the world and sees information about them.
 * The InternetDataController communicates with the JSON class
 * */
public class InternetDataController
{

    JSON json;
    ReadFiles readFiles;
    private ArrayList<String> metadataList = new ArrayList<>();
    int okButtonCounter = 0;
    /*With this display you match the TitledPane code with the
     *id of the category e.g. bp_TitledPane -> BP
     * */
    private Map<String,String> dataCategoryMap;
    private ObservableList<Map<String, String>> dataItems = FXCollections.<Map<String, String>>observableArrayList();
    @FXML
    private TitledPane bp_TitledPane;
    @FXML
    private TitledPane uniDevOrg_TitledPane;
    @FXML
    private TitledPane wwDevInd_TitledPane;
    @FXML
    private ComboBox bp_ComboBox;
    ObservableList<String>bpData;
    private Map<String,String> mapBpData;
    @FXML
    private ComboBox uniDevOrg_ComboBox;
    ObservableList<String>uniDevOrgData;
    private Map<String,String> mapUniDevOrgData;
    @FXML
    private ComboBox wwDevInd_ComboBox;
    ObservableList<String>wwDevIndData;
    Map<String,String> mapWwDevIndData;
    @FXML
    private ComboBox countries_ComboBox;
    private ObservableList<String> countriesData=FXCollections.observableArrayList();
    private Map<String,String> mapXwres ;
    @FXML
    private ToggleGroup orderToggleGroup;
    @FXML
    private RadioButton  ascRadioButton;
    @FXML
    private RadioButton  descRadioButton;
    @FXML
    private Button okButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button buckButton;
    @FXML
    private Accordion dataCategory_Accordion;
    @FXML
    private TableView tableView=new TableView();
    @FXML
    private TableColumn<Map,String> nameCol= new TableColumn<>("Όνομα");
    @FXML
    private TableColumn<Map,String> descriptionCol = new TableColumn<>("Περιγραφή");
    @FXML
    private TableColumn<Map,String> lastUpdateCol = new TableColumn<>("Τελευταία ενημέρωση");
    @FXML
    private TableColumn<Map,String> latestDateCol = new TableColumn<>("Νεότερη ημ/νία");
    @FXML
    private TableColumn<Map,String> olderDateCol = new TableColumn<>("Παλαιότερη ημ/νία");
    @FXML
    private TableColumn<Map,String> dataTableCol = new TableColumn<>("Ονόματα στηλών");
    @FXML
    private TableColumn<Map,String> frequencyCol = new TableColumn<>("Συχνότητα");
    @FXML
    private TableColumn<Map,String> typeCol = new TableColumn<>("Τύπος");
    @FXML
    private HBox firstInvisibleHbox;
    @FXML
    private HBox secondInvisibleHbox;
    @FXML
    private HBox dataLoadingHbox;
    @FXML
    private Label dataLoadingLabel;
    @FXML
    private ProgressIndicator  progressIndicator;
    private Task<Void> task;
    //We set true because the descending value is the default value
    private boolean descOrderSelected = true;

    //This method concerns options for data, country and sort order
    public void initialize()
    {
        //Create a JSON object
        json = new JSON();
        readFiles = new ReadFiles();
        readFiles.readFileData("xwres.txt");
        mapXwres = new TreeMap<>(readFiles.getFileData());
        for (Map.Entry<String,String> xwres : mapXwres.entrySet() )
        {
            countriesData.add(xwres.getKey());
        }
        readFiles.clearMap();

        dataCategoryMap = new HashMap<>();
        dataCategoryMap.put("bp_TitledPane","BP");
        dataCategoryMap.put("uniDevOrg_TitledPane","UIST");
        dataCategoryMap.put("wwDevInd_TitledPane","WWDI");

        bpData = FXCollections.observableArrayList
       (
         "Εκπομπές διοξειδίου του άνθρακα(CO2)",
                "Παραγωγή άνθρακα",
                "Παραγωγή ηλεκτρισμού",
                "Υδροηλεκτρική κατανάλωση",
                "Κατανάλωση φυσικού αερίου",
                "Παραγωγή φυσικού αερίου",
                "Κατανάλωση φυσικού αερίου - Καθημερινός μέσος όρος",
                "Παραγωγή φυσικού αερίου - Καθημερινός μέσος όρος",
                "Κατανάλωση πυρηνικής ενέργειας",
                "Κατανάλωση πετρελαίου",
                "Κατανάλωση πετρελαίου - Καθημερινός μέσος όρος",
                "Παραγωγή πετρελαίου - Ημερήσιος μέσος όρος",
                "Παραγωγή πετρελαίου",
                "Κατανάλωση ηλιακής ενέργειας",
                "Κατανάλωση αιολικής ενέργειας"
        );
        uniDevOrgData = FXCollections.observableArrayList
        (
        "Εγκαταστάσεις σε όπλα",
                "Παραγωγή στα όπλα",
                "Υπάλληλοι στα όπλα",
                "Ημερομίσθια και μισθοί στα όπλα",
                "Καταστήματα κοσμημάτων",
                "Υπάλληλοι σε κοσμήματα",
                "Παραγωγή στα κοσμήματα",
                "Ημερομίσθια και μισθοί στα κοσμήματα",
                "Προστιθέμενη αξία στα κοσμήματα",
                "Παραγωγή στα υποδήματα",
                "Υπάλληλοι σε υποδήματα",
                "Υπάλληλοι στη φαρμακευτική",
                "Παραγωγή στη φαρμακευτική",
                "Ημερομίσθια και μισθοί στη φαρμακευτική",
                "Υπάλληλοι σε φούρνους",
                "Ημερομίσθια και μισθοί σε φούρνους",
                "Εγκαταστάσεις σε γαλακτοκομικά προϊόντα",
                "Εργαζόμενοι στα γαλακτοκομικά προϊόντα",
                "Υπάλληλοι σε γεωργικά μηχανήματα",
                "Εγκαταστάσεις σε αγροτικά μηχανήματα",
                "Παραγωγή σε γεωργικά μηχανήματα",
                "Ημερομίσθια και μισθοί σε αγροτικά μηχανήματα"
        );
        wwDevIndData = FXCollections.observableArrayList
        (
         "Αστικός πληθυσμός",
                "Συνολικός πληθυσμός",
                "Αγροτικός πληθυσμός",
                "Τηλεφωνικές γραμμές",
                "ΑΕγχΠ(τρέχουσα LCU)",
                "Είδη θηλαστικών-απειλούμενα",
                "Πληθυσμός στη μεγαλύτερη πόλη",
                "Πρωτοβάθμια εκπαίδευση-μαθητές",
                "Πρωτοβάθμια εκπαίδευση-εκπαιδευτικοί",
                "Πληρωμές φόρου(αριθμός)",
                "Διεθνής τουρισμός(αριθμός αναχωρήσεων)",
                "Πρόσβαση στην ηλεκτρική ενέργεια(% του πληθυσμού)",
                "Πραγματικό επιτόκιο(%)",
                "Οχήματα(ανά χιλιόμετρο δρόμου)",
                "Διεθνής τουρισμός(αριθμός αφίξεων)"
        );

        bp_ComboBox.setItems(bpData);
        bp_ComboBox.getSelectionModel().selectFirst();
        uniDevOrg_ComboBox.setItems(uniDevOrgData);
        uniDevOrg_ComboBox.getSelectionModel().selectFirst();
        wwDevInd_ComboBox.setItems(wwDevIndData);
        wwDevInd_ComboBox.getSelectionModel().selectFirst();
        countries_ComboBox.setItems(countriesData);
        countries_ComboBox.getSelectionModel().selectFirst();

        nameCol.setCellValueFactory(new MapValueFactory<>("name"));
        descriptionCol.setCellValueFactory(new MapValueFactory<>("description"));
        lastUpdateCol.setCellValueFactory(new MapValueFactory<>("lastUpdate"));
        latestDateCol.setCellValueFactory(new MapValueFactory<>("latestDate"));
        olderDateCol.setCellValueFactory(new MapValueFactory<>("olderDate"));
        dataTableCol.setCellValueFactory(new MapValueFactory<>("tableValues"));
        frequencyCol.setCellValueFactory(new MapValueFactory<>("frequency"));
        typeCol.setCellValueFactory(new MapValueFactory<>("type"));
        tableView.isResizable();
        tableView.setPlaceholder(new Label("Ο πίνακας είναι κενός"));

        //Changes the selection of Radio Button depending on the user's choice
        orderToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
        {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle old_toggle, Toggle new_toggle)
            {
                if(orderToggleGroup.getSelectedToggle()!= null)
                {
                    if(ascRadioButton.isSelected())
                    {
                        descOrderSelected = false;
                    }
                    else
                    {
                        descOrderSelected = true;
                    }
                }
            }
        });
   }

    /*This method collects data from Quandl based on user preference*/
    public void okClicked(ActionEvent event)
    {
        //Due to the change of the page www.quandl.com
        //became data.nasdaq.com, it works without the change
        String urlString = "https://www.quandl.com/api/v3/datasets/";
        final String taskString;
        Map<String,String> dataItem = new HashMap<>();
        dataLoadingLabel.setVisible(true);
        progressIndicator.setVisible(true);
        //When the OK button is pressed again,
        //empty the variables to enter the new data
        if(okButtonCounter>0)
        {
            dataItems.clear();
            metadataList.clear();
            tableView.getItems().clear();
       }
        switch (dataCategory_Accordion.getExpandedPane().getId())
        {
            case "bp_TitledPane":
                urlString+=dataCategoryMap.get(dataCategory_Accordion.getExpandedPane().getId())+"/";
                readFiles.readFileData("bp_Data.txt");
                mapBpData = readFiles.getFileData();
                urlString+= mapBpData.get(bp_ComboBox.getValue().toString())+mapXwres.get(countries_ComboBox.getValue().toString());
                readFiles.clearMap();
                break;
            case "uniDevOrg_TitledPane":
                urlString+=dataCategoryMap.get(dataCategory_Accordion.getExpandedPane().getId())+"/";
                readFiles.readFileData("uniDevOrg_Data.txt");
                mapUniDevOrgData = readFiles.getFileData();
                urlString+=mapUniDevOrgData.get(uniDevOrg_ComboBox.getValue().toString())+mapXwres.get(countries_ComboBox.getValue().toString());
                readFiles.clearMap();
                break;
            case "wwDevInd_TitledPane":
                urlString+=dataCategoryMap.get(dataCategory_Accordion.getExpandedPane().getId())+"/";
                readFiles.readFileData("wwDevInd_Data.txt");
                mapWwDevIndData = readFiles.getFileData();
                urlString+=mapXwres.get(countries_ComboBox.getValue().toString())+mapWwDevIndData.get(wwDevInd_ComboBox.getValue().toString());
                readFiles.clearMap();
                break;
        }

        if(descOrderSelected)
        {
            urlString += ".json?order=desc&api_key=mZVZ31PNXAaaDB24BUeV";
        }
        else
        {
            urlString += ".json?order=asc&api_key=mZVZ31PNXAaaDB24BUeV";
        }
        taskString= urlString;
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception
            {
                int typeError=json.getQuandlData(taskString);
                //These commands run in the JavaFx thread
                Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        dataLoadingLabel.setVisible(false);
                        progressIndicator.setVisible(false);
                        if( (typeError==-2) || (typeError==-1) ||(typeError==0) )
                        {
                            Popup popup = new Popup();
                            switch (typeError)
                            {
                                case -2:popup.display(-2);
                                    break;
                                case -1:popup.display(-1);
                                    break;
                                case 0:popup.display(0);
                                    break;
                            }
                        }
                        else
                        {
                            metadataList = json.getMetadataList();
                            dataItem.put("name",metadataList.get(0));
                            dataItem.put("description",metadataList.get(1));
                            dataItem.put("lastUpdate",metadataList.get(2));
                            dataItem.put("latestDate",metadataList.get(3));
                            dataItem.put("olderDate",metadataList.get(4));
                            dataItem.put("tableValues",metadataList.get(5));
                            dataItem.put("frequency",metadataList.get(6));
                            dataItem.put("type",metadataList.get(7));

                            dataItems.add(dataItem);

                            tableView.getItems().setAll(dataItems);
                            descriptionCol.setCellFactory(WRAPPING_CELL_FACTORY);
                            nextButton.setVisible(true);
                        }
                        okButtonCounter++;

                    }
                });
                return null;
            }
        };
        new Thread(task).start();
    }

    //Return to the previous screen
    public void onBuckButton(ActionEvent event)
    {
        try
        {
            Stage stage = (Stage) buckButton.getScene().getWindow();
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

    /**
     * When this method is called, the selected data
     * will be displayed in the edit window
     */
    public void nextClicked(ActionEvent event)
    {
        Map <String,Double> tableData = JSON.getJsonTableData();
        String dataName = JSON.getDataName();
       try
        {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("dataProcessing.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) nextButton.getScene().getWindow();
            DataProcessingController dataProcessingController = loader.getController();
            dataProcessingController.setNetSource(true);
            dataProcessingController.processData(tableData,dataName);
            stage.setScene(scene);
            stage.setTitle("Chartistics");
            stage.getIcons().add(new Image("statistics_icon.png"));
            stage.show();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    //Method of displaying data in Table View cells
    public static final Callback<TableColumn<Map,String>, TableCell<Map,String>> WRAPPING_CELL_FACTORY =
            new Callback<TableColumn<Map,String>, TableCell<Map,String>>()
            {

                @Override public TableCell<Map,String> call(TableColumn<Map,String> param)
                {
                    TableCell<Map,String> tableCell = new TableCell<Map,String>() {
                        @Override protected void updateItem(String item, boolean empty) {
                            if (item == getItem()) return;

                            super.updateItem(item, empty);

                            if (item == null) {
                                super.setText(null);
                                super.setGraphic(null);
                            } else {
                                super.setText(null);
                                Label l = new Label(item);
                                l.setWrapText(true);
                                VBox box = new VBox(l);
                                l.heightProperty().addListener((observable,oldValue,newValue)-> {
                                    box.setPrefHeight(newValue.doubleValue()+7);
                                    Platform.runLater(()->this.getTableRow().requestLayout());
                                });
                                super.setGraphic(box);
                            }
                        }
                    };
                    return tableCell;
                }
            };
}
