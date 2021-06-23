package org.ptyxiakh.persistence;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public  class DataQuery
{
    //Η μέθοδος αποθηκεύει τα δεδομένα στη βάση δεδομένων
    //επιστρέφει έναν ακέραιο με βάση τον οποίο εμφανίζει το κατάλληλο παράθυρο με το αντίστοιχο μήνυμα
    public static int create(String name, Map<String,Double> tableData)
    {
        int savingProssecc;
        boolean successfulStorage = true;
        Measurements measurement = new Measurements();
        List <Measurements> measurementsList = new ArrayList<>();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CHARTISTICS");
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        Data data = new Data(name);
        //Δημιουργία αντικειμένων Measurements από τα δεδομένα του map
        //και αποθήκευση στη βάση δεδομένων
        tableData.forEach( (k,v) ->
        {
            //measurement = new Measurements(k,v.doubleValue());
            measurement.setDate(k);
            measurement.setValue(v);
            data.getMeasurementsList().add(measurement);
        });
        entityManager.persist(data);
        //Σε περίπτωση που παρουσιαστεί PersistenceException
        //εμφανίσε μήνυμα διπλοεγγραφής
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
            savingProssecc=2;
        }
        else
            savingProssecc =1;
        return savingProssecc;
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
