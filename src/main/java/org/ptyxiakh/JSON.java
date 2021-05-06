package org.ptyxiakh;

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
    private String stringHelper;
    public List<Double> listDoubles;
    public static Double[] doubles;
    double[] primitiveDoubles;
    Collection<Double> values;
    //initializing an input stream object to read json
    InputStream jsonStream;
    //Json Map
    private static final Map<String,Double> jsonTableData = new LinkedHashMap<>();

    private static String dataName;
    //Θα επιστρέφει μια τιμη int ανάλογα με το response code
    //π.χ. εάν υπάρχει IOException θα στέλνω -1 ώστε μετά ο Controller
    //να καλέσει την κλάση(που θα φτιάξω) για τo Pop up window,
    //παρόμοια στα υπόλοιπα
    public int getQuandlData(String stringUrl)
    {
        int arraySize=1;
        String valueString = "";
        try
        {
            URL url = new URL(stringUrl);
            //Parse URL into HttpsURLConnection in order to open the connection in order to get the JSON data
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();

            //Set the request to GET
            httpsURLConnection.setRequestMethod("GET");

            //Get the response status of the Rest API
            responseCode = httpsURLConnection.getResponseCode();
            //System.out.println("Response code is: " +responseCode);


            //Έλεγχος σε περίπτωση που υπάρξει κάποιο σφάλμα
            if(responseCode != 200 && responseCode != 404)
            {
//                throw new RuntimeException("HttpResponseCode: " +responseCode);
//                System.out.println("Response code is: " +responseCode);
                return -2;
            }
            //Έλεγχος σε περίπτωση που δεν βρεθούν τα δεδομένα
            else  if ( responseCode == 404 )
            {
//                System.out.println("Response code is: " +responseCode);
//                System.out.println("XXXX <<DATA NOT FOUND!!!>> XXXX");
                return 0;
            }
            //Κανένα πρόβλημα
            else
            {
//                System.out.println("Response code is: " +responseCode);
                jsonStream = httpsURLConnection.getInputStream();
                //System.out.println("jsonStream is created");
                Reader jsonStreamReader = new InputStreamReader(jsonStream, StandardCharsets.UTF_8);
                //System.out.println("jsonStreamReader is created");
                //Create reader to read json data from web
                JsonReader reader = Json.createReader(jsonStreamReader);
                //System.out.println("reader is created");

                //Get the root json object
                JsonObject jsonObject = reader.readObject();
                //System.out.println("jsonObject is created");

                reader.close();

                //Parsing the root object to access the JSON values
                JsonObject datasetObject =jsonObject.getJsonObject("dataset");
                metadataList.add(datasetObject.getString("name"));
                dataName = metadataList.get(0);
                metadataList.add(datasetObject.getString("description"));
                metadataList.add(datasetObject.getString("refreshed_at"));
                metadataList.add(datasetObject.getString("newest_available_date"));
                metadataList.add(datasetObject.getString("oldest_available_date"));

                //Creating a json array object to get the values
                //Δημιουργία πίνακα json για τα ονόματα των στηλών του πίνακα
                JsonArray columnNames = datasetObject.getJsonArray("column_names");
                //Διαδικασία διαχωρισμού των τιμών του πίνακα με κόμα
                //Εάν τα ονόματα των στηλών είναι περισσότερα από ένα, χρησμοποιούμε κόμα
                for(JsonValue value : columnNames)
                {
                    valueString += value.toString().replaceAll("\"","");
                    if (arraySize < columnNames.size())
                        valueString += ",";
                    arraySize++;
                }
                arraySize=1;
                metadataList.add(valueString);
                metadataList.add(datasetObject.getString("frequency"));
                metadataList.add(datasetObject.getString("type"));

                //Διαδικασία ανάκτησης των δεδομένων του πίνακα από το JSON
                JsonArray arrayObj = datasetObject.getJsonArray("data");
                for(JsonValue value : arrayObj)
                {
                    if(value.toString().startsWith("[") && value.toString().endsWith("]"))
                    {
                        String jsonData = value.toString().replaceAll("(\\[|\\])","");
                        String[] dataArray = jsonData.trim().split(",");
                        jsonTableData.put(dataArray[0].replaceAll("\"","").trim(),Double.valueOf(dataArray[1].trim()));
                    }
                }
//                values = jsonMap.values();
//                doubles = values.toArray(new Double[values.size()]);
//                listDoubles = new ArrayList<>();
//                for (Double value: doubles)
//                {
//                    listDoubles.add(value);
//                    System.out.println("Printing list value: "+value);
//                }
//                primitiveDoubles = ArrayUtils.toPrimitive(doubles);
//                System.out.println("------Printing Map keys---------");
//                for (String key : jsonMap.keySet())
//                {
//                    System.out.println(key);
//                }
//                System.out.println("--------Printing Map values----------");
//                for (Double values : jsonMap.values())
//                {
//                    System.out.println(values);
//
//                }
                //O Αλγόριθμος AugmentedDickeyFuller  με βάση τον αριθμό των lag ελέγχει εάν τα δεδομένα
                //είναι σταθερά(stationary)
//                AugmentedDickeyFuller augmentedDickeyFuller = new AugmentedDickeyFuller(primitiveDoubles,20);
//                if(augmentedDickeyFuller.isNeedsDiff())
//                    System.out.println("The data are not stationary");
//                else
//                {
//                    System.out.println("The data are stationary");
//                    Stats stats = new Stats();
//                    //Τα acfValues είναι όσα και τα lag
//                    double[] acfValues = stats.getAcf(listDoubles,20);
//                    for (double value: acfValues )
//                    {
//                        System.out.println("Acf value: "+value);
//                    }
//                    int p = 8;
//                    int d = 20;
//                    int q = 5;
//                    int P = 0;
//                    int D = 0;
//                    int Q = 0;
//                    int m = 0;
//                    int forecastSize = 1;
//                    ArimaParams arimaParams = new ArimaParams(6,20,7,2,1,1,1);
//                    ForecastResult forecastResult = Arima.forecast_arima(primitiveDoubles, forecastSize, arimaParams);
//                    double[] forecastData = forecastResult.getForecast();
//                    for (int i=0;i<forecastData.length;i++)
//                    {
//                        System.out.println(forecastData[i]);
//                    }
//                    System.out.println(forecastResult.getLog());
//                    System.out.println(forecastResult.getRMSE());
//                }
//                System.out.println("P-value of GDP: "+augmentedDickeyFuller.pValue());
//                TimeSeries timeSeriesJson = new TimeSeries(primitiveDoubles,"GDP-Greece");
//                AlgoSecondOrderDifferencing algoSecDiff = new AlgoSecondOrderDifferencing();
//                algoSecDiff.runAlgorithm(timeSeriesJson);
//                algoSecDiff.printStats();

            }
        }
        //Σε περίπτωση που δεν υπάρχει σύνδεση στο δίκτυο
        catch (IOException malformedURLException)
        {
//            System.out.println("No internet connection");
            return -1;
        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
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
    public int getQuandleResponseCode()
    {
        return responseCode;
    }

    //Επιστρέφει τα δεδομένα του πίνακα JSON
    public static Map<String,Double> getJsonTableData()
    {
        return jsonTableData;
    }

    public static Map<String,String> getSampleMap()
    {
        Map<String,String> sampleMap = new HashMap<>();
        sampleMap.put("2018-12-31","1.847");
        sampleMap.put("2017-12-31","1.802");
        sampleMap.put("2016-12-31","1.764");

//        System.out.println("Printing jsonMap keys and values");
//        for (Map.Entry<String,Double> testMap : jsonMap.entrySet())
//        {
//            System.out.println("Key:"+testMap.getKey()+" Value:"+testMap.getValue() );
//        }
//        System.out.println("In the class JSON");
//        System.out.println("------Printing Map keys---------");
//        for (String key : jsonMap.keySet())
//        {
//            System.out.println(key);
//        }
//        System.out.println("--------Printing Map values----------");
//        for (Double values : jsonMap.values())
//        {
//            System.out.println(values);
//        }
       return sampleMap;
    }
}
