package org.ptyxiakh;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/*
* Με τον  Controller ο χρήστης επιλέγει τα δεδομένα που θέλει για κάποια χώρα ή παγκόσμια
* και βλέπει πληροφορίες γι'αυτά.
* Ο Controller επικοινωνεί με την κλάση JSON
* */
public class Controller
{

    JSON json;
    ReadFiles readFiles;
    private ArrayList<String> metadataList = new ArrayList<>();
    int okButtonCounter = 0;
    //Με αυτό τον χάρτη αντιστοιχείτε ο κωδικός του TitledPane με το
    //id της κατηγορίας π.χ. bp_TitledPane -> BP
    private Map<String,String> dataCategoryMap;

    private ObservableList<Map<String, String>> dataItems = FXCollections.<Map<String, String>>observableArrayList();

    //private String diagramUrl="";

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
    private Map<String,String> mapXwres = new TreeMap<>();
    private Map<String,String> mapHelper ;

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
    private TableColumn<Map,String> datasTableCol = new TableColumn<>("Ονόματα στηλών");

    @FXML
    private TableColumn<Map,String> frequencyCol = new TableColumn<>("Συχνότητα");

    @FXML
    private TableColumn<Map,String> typeCol = new TableColumn<>("Τύπος");

    @FXML
    private HBox firstInvisibleHbox;

    @FXML
    private HBox secondInvisibleHbox;

    //Βάζουμε true επειδή η φθίνουσα είναι τιμή προεπιλογής
    private boolean descOrderSelected = true;

    //Η μέθοδος αυτή δίνει τις επιλογές για τα δεδομένα,την χώρα και την σειρά
    public void initialize()
    {

        readFiles = new ReadFiles();
        readFiles.readFileData("xwres.txt");
        mapHelper = readFiles.getFileData();
        for (Map.Entry<String,String> xwres : mapHelper.entrySet() )
        {
            countriesData.add(xwres.getKey());
            mapXwres.put(xwres.getKey(), xwres.getValue());
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
                "Ημερομισθια και μισθοι στη φαρμακευτική",
                "Υπάλληλοι σε φούρνους",
                "Ημερομίσθια και μισθοί σε φούρνους",
                "Εγκαταστάσεις σε γαλακτοκομικά προϊόντα",
                "Εργαζόμενοι στα γαλακτοκομικά προϊόντα",
                "Υπάλληλοι σε γεωργικά μηχανήματα",
                "Εγκαταστάσεις σε αγροτικά μηχανήματα",
                "Παραγωγή σε γεωργικά μηχανήματα",
                "Ημερομισθια και μισθοι σε αγροτικά μηχανήματα"
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
        datasTableCol.setCellValueFactory(new MapValueFactory<>("tableValues"));
        frequencyCol.setCellValueFactory(new MapValueFactory<>("frequency"));
        typeCol.setCellValueFactory(new MapValueFactory<>("type"));
        tableView.isResizable();
        tableView.setPlaceholder(new Label("Ο πίνακας είναι κενός"));

        //Αλλάζει την  επιλογή του Radio Button ανάλογα με την επιλογή του χρήστη.
        orderToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
        {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle old_toggle, Toggle new_toggle)
            {

                if(orderToggleGroup.getSelectedToggle()!= null)
                {
                    if(ascRadioButton.isSelected())
                    {
                        System.out.println("Data will be asc.");
                        descOrderSelected = false;
                   }
                    else
                    {
                        System.out.println("Data will be desc.");
                        descOrderSelected = true;
                    }
                }
            }
        });

        //Δημιουργία αντικειμένου JSON
        json = new JSON();
   }

    public void okClicked(ActionEvent event)
    {

        int typeError;
        String urlString = "https://www.quandl.com/api/v3/datasets/";
        Map<String,String> dataItem = new HashMap<>();
        //Όταν πατηθεί πάλι το κουμπί OK άδειασε τις μεταβλητές
        //για να μπουν τα νέα δεδομένα
        if(okButtonCounter>0)
        {
            dataItems.clear();
            metadataList.clear();
            tableView.getItems().clear();
           //diagramUrl="";
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
            //System.out.println("descDataOrder button is selected.");
            urlString += ".json?order=desc&api_key=mZVZ31PNXAaaDB24BUeV";
        }
        else
        {
            //System.out.println("ascDataOrder button is selected.");
            urlString += ".json?order=asc&api_key=mZVZ31PNXAaaDB24BUeV";

        }

        typeError=json.getQuandlData(urlString);
        //Εμφάνισε το κατάλληλο μήνυμα σε περίπτωση που υπάρξει κάποιο πρόβλημα
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

            tableView.getItems().addAll(dataItems);
            descriptionCol.setCellFactory(WRAPPING_CELL_FACTORY);
            nextButton.setVisible(true);
        }
        okButtonCounter++;
    }
    public void onBuckButton(ActionEvent event)
    {
        try
        {
            Stage stage = (Stage) buckButton.getScene().getWindow();
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
    /**
     * Όταν κληθεί αυτή η μέθοδος,τα δεδομένα που επιλέξαμε
     * θα εμφανιστούν στο παράθυρο επεξεργασίας
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
//            stage.close();
//            Stage primaryStage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Chartistics");
            stage.show();
//            primaryStage.setScene(new Scene(root));
//            primaryStage.show();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }
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
