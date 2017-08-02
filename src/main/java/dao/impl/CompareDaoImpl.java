package dao.impl;

import crawl.SpiderGlobal;
import dao.CompareDao;
import domain.Compare;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

/**
 * Created by ooopic on 2017/7/28.
 */
public class CompareDaoImpl implements CompareDao {
    private EntityManager em = SpiderGlobal.getInstance().getEmf().createEntityManager();

    @Override
    public Compare save(Compare compare) {
        em.getTransaction().begin();
        em.persist(compare);
        em.getTransaction().commit();
        return compare;
    }

    @Override
    public List<Compare> findByTargetFile(String url) {
        em.getTransaction().begin();
        List l = em.createQuery("SELECT c FROM Compare c where c.targetFile = :d").setParameter("d",url).getResultList();
        em.getTransaction().commit();
        return l;
    }
}
