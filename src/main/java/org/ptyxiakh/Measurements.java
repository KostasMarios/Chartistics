package org.ptyxiakh;

import javax.persistence.*;

@Entity
@Table(name = "measurement")
public class Measurements
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "measurement_id")
    private int id;

    @Column ( name = "date")
    private String date;

    @Column ( name = "value")
    private double value;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "data_id")
    private Data data;

    public Measurements() {
    }

    public Measurements(int id, String date, double value) {
        this.id = id;
        this.date = date;
        this.value = value;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


}
