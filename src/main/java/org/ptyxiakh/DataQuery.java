package org.ptyxiakh;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public  class DataQuery
{
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
//                System.out.println("Data name:"+ data.getName());
            if(data.getName().equals(dataName))
            {
                dataName=data.getName();
                List<Measurements> listMeasurements = data.getMeasurementsList();
                for (Measurements measurements : listMeasurements)
//                        System.out.println("Date:" + measurements.getDate() + "Value:" + measurements.getValue());
                    dataMap.put(measurements.getDate(),measurements.getValue());
            }
        }
        em.close();
        emf.close();

        return (LinkedHashMap<String, Double>) dataMap;
    }

}
