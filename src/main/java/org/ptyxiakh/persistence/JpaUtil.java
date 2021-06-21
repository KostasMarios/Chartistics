package org.ptyxiakh.persistence;
import org.ptyxiakh.ui.Popup;

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
    //Η μέθοδος αποθηκεύει τα δεδομένα στη βάση δεδομένων
    //επιστρέφει έναν ακέραιο με βάση τον οποίο εμφανίζει το κατάλληλο παράθυρο με το αντίστοιχο μήνυμα
    public int create(String name, Map<String,Double> tableData)
    {
        int savingProssecc;
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
}
