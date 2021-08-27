package org.ptyxiakh.persistence;

import javax.persistence.*;
/*Η κλάση αυτή αποθηκεύει τις μετρήσεις της πληροφορίας*/
@Entity
public class Measurements
{
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Integer id;

    private String date;

    private double value;

    public Measurements()
    {
    }

    public Measurements(String date, double value)
    {
        this.date = date;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

}
