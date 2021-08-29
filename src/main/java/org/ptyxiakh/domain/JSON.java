package org.ptyxiakh.domain;
import javax.json.*;
import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class JSON
{
    //Η λίστα metadataList αποθηκεύει πληροφορίες για τα δεδομένα
    private ArrayList<String> metadataList = new ArrayList<>();
    private int responseCode;
    //Αρχικοποίηση ενός αντικειμένου ροής εισόδου για την ανάγνωση json
    InputStream jsonStream;
    //Δημιουργία LinkedHashMap, ώστε να διατηρείτε η σειρά των δεδομένων
    private static  Map<String,Double> jsonTableData = new LinkedHashMap<>();
    private static String dataName;

    //Η μέθοδος επιστρέφει έναν ακέραιο αριθμό
    //για κάθε επιτυχή λήψη των δεδομένων ή ανεπιτυχή
    public int getQuandlData(String stringUrl)
    {
        int arraySize=1;
        String valueString = "";
        try
        {
            URL url = new URL(stringUrl);
            if(!jsonTableData.isEmpty())
            jsonTableData.clear();
            //Ανέλυσε τη διεύθυνση URL στο HttpsURLConnection για να ανοίξει η σύνδεση προκειμένου να ληφθούν τα δεδομένα JSON
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            //Όρισε το αίτημα σε GET
            httpsURLConnection.setRequestMethod("GET");
            //Λάβε την κατάσταση απόκρισης του Rest API
            responseCode = httpsURLConnection.getResponseCode();
            //Έλεγχος σε περίπτωση που υπάρξει κάποιο σφάλμα
            if(responseCode != 200 && responseCode != 404)
            {
                return -2;
            }
            //Έλεγχος σε περίπτωση που δεν βρεθούν τα δεδομένα
            else  if ( responseCode == 404 )
            {
                return 0;
            }
            //Κανένα πρόβλημα
            else
            {
                jsonStream = httpsURLConnection.getInputStream();
                Reader jsonStreamReader = new InputStreamReader(jsonStream, StandardCharsets.UTF_8);
                //Δημιούργησε json αναγνώστη για να διαβάζει δεδομένα json από τον Ιστό
                JsonReader reader = Json.createReader(jsonStreamReader);
                //Λάβε το ριζικό αντικείμενο json
                JsonObject jsonObject = reader.readObject();
                reader.close();
                //Ανέλυσε το ριζικό αντικείμενο για πρόσβαση στις τιμές json
                JsonObject datasetObject =jsonObject.getJsonObject("dataset");
                metadataList.add(datasetObject.getString("name"));
                dataName = metadataList.get(0);
                metadataList.add(datasetObject.getString("description"));
                metadataList.add(datasetObject.getString("refreshed_at"));
                metadataList.add(datasetObject.getString("newest_available_date"));
                metadataList.add(datasetObject.getString("oldest_available_date"));
                //Δημιουργία πίνακα json για τα ονόματα των στηλών του πίνακα
                JsonArray columnNames = datasetObject.getJsonArray("column_names");
                //Διαδικασία διαχωρισμού των τιμών του πίνακα με κόμμα
                //Εάν τα ονόματα των στηλών είναι περισσότερα από ένα, χρησιμοποιούμε κόμμα
                for(JsonValue value : columnNames)
                {
                    valueString += value.toString().replaceAll("\"","");
                    if (arraySize < columnNames.size())
                        valueString += ",";
                    arraySize++;
                }
                metadataList.add(valueString);
                metadataList.add(datasetObject.getString("frequency"));
                metadataList.add(datasetObject.getString("type"));
                //Διαδικασία ανάκτησης των δεδομένων του πίνακα από το JSON
                JsonArray arrayObj = datasetObject.getJsonArray("data");
                for(JsonValue value : arrayObj)
                {
                    //Διαδικασία αφαίρεσης αγκυλών από τα δεδομένα
                    if(value.toString().startsWith("[") && value.toString().endsWith("]"))
                    {
                        String jsonData = value.toString().replaceAll("(\\[|\\])","");
                        String[] dataArray = jsonData.trim().split(",");
                        jsonTableData.put(dataArray[0].replaceAll("\"","").trim(),Double.valueOf(dataArray[1].trim()));
                    }
                }
            }
        }
        //Σε περίπτωση που δεν υπάρχει σύνδεση στο δίκτυο
        catch (IOException malformedURLException)
        {
            return -1;
        }
        return 1;
    }

    //Επιστρέφει πληροφορίες για τα δεδομένα
    public ArrayList<String> getMetadataList()
    {
        return metadataList;
    }

    public static String getDataName()
{
    return dataName;
}

    //Επιστρέφει τα δεδομένα του πίνακα JSON
    public static Map<String,Double> getJsonTableData()
    {
        return jsonTableData;
    }
}
