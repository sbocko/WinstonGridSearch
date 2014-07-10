package sk.upjs.winston.gridsearch.model;

import junit.framework.TestCase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DatasetTest extends TestCase {
    private SessionFactory factory;

    @Before
    public void setUp() throws Exception {
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testSaveDataset() throws Exception {
        Long datasetId = null;
        String datasetName = "testSaveDataset 1";

        //save dataset object to DB
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Dataset dataset = new Dataset(datasetName);
            datasetId = (Long) session.save(dataset);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            fail();
        } finally {
            session.close();
        }

        //check if dataset object exists in DB
        session = factory.openSession();
        tx = null;
        try {
            tx = session.beginTransaction();

            Dataset savedDataset = (Dataset) session.createCriteria(Dataset.class).
                    add(Restrictions.eq("id", datasetId)).
                    uniqueResult();
            assertEquals(savedDataset.getDatasetName(), datasetName);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            fail();
        } finally {
            session.close();
        }

//        remove testing data from DB
        deleteDataset(datasetId);
    }

    @Test
    public void testDeleteDataset() throws Exception {
        Long datasetId = null;
        String datasetName = "testDeleteDataset 1";

        //save dataset to DB and check if it is saved
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Dataset dataset = new Dataset(datasetName);
            datasetId = (Long) session.save(dataset);

            Dataset savedDataset = (Dataset) session.createCriteria(Dataset.class).
                    add(Restrictions.eq("id", datasetId)).
                    uniqueResult();
            assertEquals(savedDataset.getDatasetName(), datasetName);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            fail();
        } finally {
            session.close();
        }

        //delete dataset from DB
        session = factory.openSession();
        tx = null;
        try {
            tx = session.beginTransaction();

            Dataset savedDataset = (Dataset) session.createCriteria(Dataset.class).
                    add(Restrictions.eq("id", datasetId)).
                    uniqueResult();
            session.delete(savedDataset);

            savedDataset = (Dataset) session.createCriteria(Dataset.class).
                    add(Restrictions.eq("id", datasetId)).
                    uniqueResult();
            assertEquals(savedDataset, null);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            fail();
        } finally {
            session.close();
        }
    }

    private void deleteDataset(Long datasetId) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Dataset savedDataset = (Dataset) session.createCriteria(Dataset.class).
                    add(Restrictions.eq("id", datasetId)).
                    uniqueResult();
            session.delete(savedDataset);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            fail();
        } finally {
            session.close();
        }
    }

    @After
    public void tearDown() throws Exception {
        factory.close();
    }
}