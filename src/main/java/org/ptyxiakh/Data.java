package org.ptyxiakh;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "data")
public class Data
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "data_id")
    private int id;

    @Column ( name = "name")
    private String name;

    @OneToMany(mappedBy = "data")
    private List<Measurements> measurementsList;

    public List<Measurements> getMeasurementsList()
    {
        return measurementsList;
    }

    public void setMeasurementsList(List<Measurements> measurementsList)
    {
        this.measurementsList = measurementsList;
    }

    public Data()
    {

    }

    public Data( String name)
    {

        this.name = name;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
