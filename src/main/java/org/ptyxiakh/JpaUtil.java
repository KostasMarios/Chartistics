package org.ptyxiakh;
import javax.persistence.*;

public class JpaUtil
{
    private static EntityManagerFactory emf;

    public void create(String name)
    {
        emf = Persistence.createEntityManagerFactory("CHARTISTICS");
        EntityManager entityManager = emf.createEntityManager();
        Data data = new Data(name);
        entityManager.getTransaction().begin();
        entityManager.persist(data);
        entityManager.getTransaction().commit();
        entityManager.close();
        emf.close();
    }
}
