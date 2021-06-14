package org.ptyxiakh;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Κλάση αποθήκευσης των οντοτήτων στη βάση δεδομένων
 * */
public class JpaUtil
{
    private static EntityManagerFactory emf;
    Measurements measurement;
    List <Measurements> measurementsList ;
    public void create(String name, Map<String,Double> tableData)
    {
        boolean successfulStorage = true;
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
        });
        entityManager.persist(data);
        //Σε περίπτωση που παρουσιαστεί PersistenceException
       //εμφανίσε μήνυμα διπλοεγγραφής
        Popup popup = new Popup();
        try
        {
            entityManager.flush();
        }
        catch (PersistenceException ex)
        {

            popup.sqlPopUp(1);
            successfulStorage = false;
        }

        entityManager.getTransaction().commit();
        entityManager.close();
        emf.close();
        if(successfulStorage)
        {
            popup.sqlPopUp(2);
        }
    }
}
