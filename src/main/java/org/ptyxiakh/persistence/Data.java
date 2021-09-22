package org.ptyxiakh.persistence;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
/*This class stores the name of the data
*selected by the user and the corresponding measurements
**/

/*The class with the comment @Entity is an entity
 *these items will be stored in the database via the JPA
 **/
@Entity
public class Data
{
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Integer id;
    //I put @Column (unique = true) to avoid the same values in the database
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
