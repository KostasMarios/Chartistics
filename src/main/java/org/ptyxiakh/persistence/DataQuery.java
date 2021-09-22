package org.ptyxiakh.persistence;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/*This class contains basic actions for
*communication with the database. */
public  class DataQuery
{
    // The method stores the data in the database
    //returns an integer based on which displays the appropriate window with the corresponding message
    public static int create(String name, Map<String,Double> tableData)
    {
        int savingProcess;
        boolean successfulStorage = true;
        //The EntityManagerFactory class provides support for the EntityManager class
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CHARTISTICS");
        //The EntityManager class provides the connection to the database and
        //various functions
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        Data data = new Data(name);
        //Create Measurements objects from Μap data
        //and storage in the database
        tableData.forEach( (k,v) ->
        {
            Measurements measurement = new Measurements(k,v.doubleValue());
            data.getMeasurementsList().add(measurement);
        });
        entityManager.persist(data);
        //If PersistenceException occurs
        //displaye a message that this record already exists
        try
        {
            entityManager.flush();
        }
        catch (PersistenceException ex)
        {
            successfulStorage = false;
        }

        entityManager.getTransaction().commit();
        entityManager.close();
        emf.close();
        if(successfulStorage)
        {
            savingProcess=2;
        }
        else
            savingProcess =1;
        return savingProcess;
    }
    public static List<String> getDataName()
    {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CHARTISTICS");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Query query = em.createQuery("select d.name from Data d");
        List<String> list = query.getResultList();
        em.close();
        emf.close();

        return list;
    }

    public static LinkedHashMap<String,Double> findData(String dataName)
    {
        Map<String,Double> dataMap = new LinkedHashMap<>();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CHARTISTICS");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Query query = em.createQuery("select d from Data d");
        List<Data> list = query.getResultList();
        for (Data data: list)
        {
            if(data.getName().equals(dataName))
            {
                dataName=data.getName();
                List<Measurements> listMeasurements = data.getMeasurementsList();
                for (Measurements measurements : listMeasurements)
                    dataMap.put(measurements.getDate(),measurements.getValue());
            }
        }
        em.close();
        emf.close();
        return (LinkedHashMap<String, Double>) dataMap;
    }

    public static Data findDataToDelete(String dataName)
    {
        Data dataToDelete = null;
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CHARTISTICS");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Query query = em.createQuery("select d from Data d");
        List<Data> list = query.getResultList();
        for (Data data: list)
        {
            if(data.getName().equals(dataName))
            {
                dataToDelete=data;

            }
        }
        em.close();
        emf.close();
        return dataToDelete;
    }

    public static boolean deleteRecord(Data data)
    {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CHARTISTICS");
        EntityManager em = emf.createEntityManager();
        Data removeData = em.find(Data.class, data.getId());
        em.getTransaction().begin();
        em.remove(removeData);
        em.getTransaction().commit();
        em.close();
        emf.close();

        return true;
    }

}
