package sk.upjs.winston.gridsearch.model;

import junit.framework.TestCase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

public class KnnSearchResultTest extends TestCase {
    public static final int TEST_K = 3;
    public static final double TEST_RMSE = 0.9;
    private SessionFactory factory;
    private Dataset testDataset;

    public void setUp() throws Exception {
        super.setUp();
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable e) {
            e.printStackTrace();
            fail();
        }
        testDataset = createAndSaveTestDataset();
    }

    @Test
    public void testSaveKnnSearchResult() throws Exception {
        Long knnId = null;

        //save knnSearchResult object to DB
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            SearchResult knnSearchResult = new KnnSearchResult(this.testDataset, TEST_RMSE, TEST_K);
            knnId = (Long) session.save(knnSearchResult);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            fail();
        } finally {
            session.close();
        }

        //check if knnSearchResult object exists in DB
        session = factory.openSession();
        tx = null;
        try {
            tx = session.beginTransaction();

            KnnSearchResult knnSearchResult = (KnnSearchResult) session.createCriteria(KnnSearchResult.class).
                    add(Restrictions.eq("id", knnId)).
                    uniqueResult();
            assertEquals(knnSearchResult.getDataset().getDatasetName(), testDataset.getDatasetName());
            assertEquals(knnSearchResult.getRmse(), TEST_RMSE);
            assertEquals(knnSearchResult.getK(), TEST_K);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            fail();
        } finally {
            session.close();
        }

//        remove testing data from DB
        deleteSearchResult(knnId);
    }

    private void deleteSearchResult(Long knnId) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            KnnSearchResult knnSearchResult = (KnnSearchResult) session.createCriteria(KnnSearchResult.class).
                    add(Restrictions.eq("id", knnId)).
                    uniqueResult();
            session.delete(knnSearchResult);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            fail();
        } finally {
            session.close();
        }
    }

    /*
     * Create test dataset before tests. Used in setUp() method.
     */
    private Dataset createAndSaveTestDataset(){
        Dataset dataset = null;
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            dataset = new Dataset("name");
            session.save(dataset);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            fail();
        } finally {
            session.close();
        }
        return dataset;
    }

    /*
     * Delete test dataset after tests. Used in tearDown() method.
     */
    private void deleteTestDataset(Dataset savedDataset){
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
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

    public void tearDown() throws Exception {
        deleteTestDataset(testDataset);
        factory.close();
    }
}