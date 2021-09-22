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
    //The metadataList stores information about the data
    private ArrayList<String> metadataList = new ArrayList<>();
    private int responseCode;
    //Initialize an input stream object for reading json
    InputStream jsonStream;
    //Create LinkedHashMap to maintain data order
    private static  Map<String,Double> jsonTableData = new LinkedHashMap<>();
    private static String dataName;

    //The method returns an integer
    //for any successful or unsuccessful data download
    public int getQuandlData(String stringUrl)
    {
        int arraySize=1;
        String valueString = "";
        try
        {
            URL url = new URL(stringUrl);
            if(!jsonTableData.isEmpty())
            jsonTableData.clear();
            //Analyze the URL in HttpsURLConnection to open the link to retrieve JSON data
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            //Set the request to GET
            httpsURLConnection.setRequestMethod("GET");
            //Get the Rest API response
            responseCode = httpsURLConnection.getResponseCode();
            //Check in case of an error
            if(responseCode != 200 && responseCode != 404)
            {
                return -2;
            }
            //Check in case the data is not found
            else  if ( responseCode == 404 )
            {
                return 0;
            }
            //No problem
            else
            {
                jsonStream = httpsURLConnection.getInputStream();
                Reader jsonStreamReader = new InputStreamReader(jsonStream, StandardCharsets.UTF_8);
                //Create json reader to read json data from web
                JsonReader reader = Json.createReader(jsonStreamReader);
                //Get the json root object
                JsonObject jsonObject = reader.readObject();
                reader.close();
                //Analyze the root object to access json values
                JsonObject datasetObject =jsonObject.getJsonObject("dataset");
                metadataList.add(datasetObject.getString("name"));
                dataName = metadataList.get(0);
                metadataList.add(datasetObject.getString("description"));
                metadataList.add(datasetObject.getString("refreshed_at"));
                metadataList.add(datasetObject.getString("newest_available_date"));
                metadataList.add(datasetObject.getString("oldest_available_date"));
                //Create a json table for the names of the table columns
                JsonArray columnNames = datasetObject.getJsonArray("column_names");
                //Procedure for separating table values by comma
                //If the column names are more than one, we use a comma
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
                //Procedure for retrieving table data from JSON
                JsonArray arrayObj = datasetObject.getJsonArray("data");
                for(JsonValue value : arrayObj)
                {
                    //Procedure for removing brackets from the data
                    if(value.toString().startsWith("[") && value.toString().endsWith("]"))
                    {
                        String jsonData = value.toString().replaceAll("(\\[|\\])","");
                        String[] dataArray = jsonData.trim().split(",");
                        jsonTableData.put(dataArray[0].replaceAll("\"","").trim(),Double.valueOf(dataArray[1].trim()));
                    }
                }
            }
        }
        //In case there is no network connection
        catch (IOException malformedURLException)
        {
            return -1;
        }
        return 1;
    }

    //Returns information about the data selected by the user
    public ArrayList<String> getMetadataList()
    {
        return metadataList;
    }

    public static String getDataName()
{
    return dataName;
}

    //Returns JSON table data
    public static Map<String,Double> getJsonTableData()
    {
        return jsonTableData;
    }
}
