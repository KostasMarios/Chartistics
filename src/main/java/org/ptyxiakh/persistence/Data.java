package org.ptyxiakh.persistence;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
/*Η κλάση αυτή αποθηκεύει το όνομα του δεδομένου
 *που επέλεξε ο χρήστης και τις αντίστοιχες μετρήσεις*/
/*Η κλάση με το σχόλιο @Entity αποτελεί οντότητα
* δηλαδή τα αντικείμενα αυτά θα αποθηκευτούν στη βάση
* μέσω του JPA*/
@Entity
public class Data
{
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Integer id;
    //Βάζω @Column(unique = true) για αποφυγή ίδιων τιμών στη βάση δεδομένων
    @Column(unique = true)
    private String name;

   @OneToMany( targetEntity= Measurements.class,cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true )
    private List<Measurements> measurementsList = new ArrayList<>();

    public Data()
    {

    }

    public Data( String name)
    {
        this.name = name;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Measurements> getMeasurementsList() {
        return measurementsList;
    }

    public void setMeasurementsList(List<Measurements> measurementsList) {
        this.measurementsList = measurementsList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
