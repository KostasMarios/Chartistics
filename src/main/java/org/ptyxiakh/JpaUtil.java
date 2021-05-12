package org.ptyxiakh;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JpaUtil
{
    private static EntityManagerFactory emf;
    Measurements measurement;
    List <Measurements> measurementsList ;
    public void create(String name, Map<String,Double> tableData)
    {
        measurementsList = new ArrayList<>();
        emf = Persistence.createEntityManagerFactory("CHARTISTICS");
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        Data data = new Data(name);
        //Δημιουργία αντικειμένων Measurements από τα δεδομένα του map
        //και αποθήκευση στη βάση δεδομένων
         tableData.forEach( (k,v) ->
        {
            measurement = new Measurements(k,v.doubleValue());
            data.getMeasurementsList().add(measurement);
//            entityManager.persist(measurement);
//            measurementsList.add(measurement);
        });


        entityManager.persist(data);
        entityManager.flush();
        entityManager.getTransaction().commit();
        entityManager.close();
        emf.close();
    }
}
