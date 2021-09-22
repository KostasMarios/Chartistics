package org.ptyxiakh.businessInfrastructure;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.TreeMap;

/*ΑThis class reads files by comma and stores the values in a map
 **/
public class ReadFiles
{
    TreeMap<String,String> fileData ;

    Scanner scanner = null;

    public ReadFiles() 
    {
        this.fileData = new TreeMap<>();
    }

    public void readFileData(String textFile)
    {
            InputStream inputStream = this.getFileFromResourceAsStream(textFile);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,StandardCharsets.UTF_8);
            scanner = new Scanner( new BufferedReader(inputStreamReader));
            scanner.useDelimiter(",");
            while (scanner.hasNextLine())
            {
                String value = scanner.next();
                scanner.skip(scanner.delimiter());
                String valueId = scanner.nextLine();
                fileData.put(value,valueId);
            }
    }

    public TreeMap<String, String> getFileData()
    {
        return fileData;
    }

    public void clearMap()
    {
        fileData.clear();
    }

    //We get the file from the resources folder
    public InputStream getFileFromResourceAsStream(String fileName)
    {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        //If the file is not found, display an appropriate message
        if (inputStream == null)
        {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else
        {
            return inputStream;
        }
    }
}

