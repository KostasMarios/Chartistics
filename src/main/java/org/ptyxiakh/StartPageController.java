package org.ptyxiakh;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.List;

public class StartPageController
{
    @FXML
    Button db_button;

    @FXML
    Button e_button;


    public void DataBaseButtonClicked(ActionEvent event)
    {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CHARTISTICS");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Query query = em.createQuery("select d.name from Data d");
        List<String> list = query.getResultList();
        if(list.isEmpty())
            System.out.println("THE LIST IS EMPTY!!!");
        else
            for (String s:list)
            {
                System.out.println(s);
            }
    }

    public void InternetDataButtonClicked(ActionEvent event)
    {

    }
}
