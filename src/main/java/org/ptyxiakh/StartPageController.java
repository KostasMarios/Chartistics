package org.ptyxiakh;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.List;

public class StartPageController
{
    @FXML
    private Button db_button;

    @FXML
    private Button e_button;

    @FXML
    private Button cancel_button;

    @FXML
    private Button ok_button;

    @FXML
    private Label startpage_label;

    @FXML
    ListView<String> startpage_listView;


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
        {
            startpage_label.setText("Επίλεξτε 1 από τα δεδομένα:");
            e_button.setVisible(false);
            db_button.setVisible(false);
            startpage_listView.getItems().setAll(list);
            startpage_listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            startpage_listView.setVisible(true);
            startpage_listView.getSelectionModel().select(0);
            cancel_button.setVisible(true);
            ok_button.setVisible(true);
//            for (String s : list)
//            {
//                System.out.println(s);
//            }
        }
    }

    public void InternetDataButtonClicked(ActionEvent event)
    {

    }

    public  void okButtonClicked(ActionEvent event)
    {
        System.out.println(startpage_listView.getSelectionModel().getSelectedItem());
    }
}
