package sk.upjs.winston.gridsearch.model;

import junit.framework.TestCase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

public class LogisticRegressionSearchResultTest extends TestCase {
    public static final double TEST_RMSE = 0.1;
    public static final double TEST_RIDGE = 0;
    public static final int TEST_MAXIMUM_NUMBER_OF_ITERATIONS = -1;
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
    public void testSaveLogisticRegressionSearchResult() throws Exception {
        Long logisticRegressionId = null;

        //save logisticRegressionSearchResult object to DB
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            SearchResult logisticRegressionSearchResult =
                    new LogisticRegressionSearchResult(this.testDataset, TEST_RMSE,
                            TEST_RIDGE, TEST_MAXIMUM_NUMBER_OF_ITERATIONS);
            logisticRegressionId = (Long) session.save(logisticRegressionSearchResult);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            fail();
        } finally {
            session.close();
        }

        //check if logisticRegressionSearchResult object exists in DB
        session = factory.openSession();
        tx = null;
        try {
            tx = session.beginTransaction();

            LogisticRegressionSearchResult logisticRegressionSearchResult = (LogisticRegressionSearchResult) session.createCriteria(LogisticRegressionSearchResult.class).
                    add(Restrictions.eq("id", logisticRegressionId)).
                    uniqueResult();
            assertEquals(logisticRegressionSearchResult.getDataset().getDatasetName(), testDataset.getDatasetName());
            assertEquals(logisticRegressionSearchResult.getRmse(), TEST_RMSE);
            assertEquals(logisticRegressionSearchResult.getRidge(), TEST_RIDGE);
            assertEquals(logisticRegressionSearchResult.getMaximumNumberOfIterations(),TEST_MAXIMUM_NUMBER_OF_ITERATIONS);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            fail();
        } finally {
            session.close();
        }

//        remove testing data from DB
        deleteSearchResult(logisticRegressionId);
    }

    @Test
    public void testDeleteLogisticRegressionSearchResult() throws Exception {
        Long logisticRegressionId = null;

        //save logisticRegressionSearchResult object to DB
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            SearchResult logisticRegressionSearchResult =
                    new LogisticRegressionSearchResult(this.testDataset, TEST_RMSE,
                            TEST_RIDGE, TEST_MAXIMUM_NUMBER_OF_ITERATIONS);
            logisticRegressionId = (Long) session.save(logisticRegressionSearchResult);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            fail();
        } finally {
            session.close();
        }

        //check if logisticRegressionSearchResult object exists in DB
        session = factory.openSession();
        tx = null;
        try {
            tx = session.beginTransaction();

            LogisticRegressionSearchResult savedLogisticRegressionSearchResult = (LogisticRegressionSearchResult) session.createCriteria(LogisticRegressionSearchResult.class).
                    add(Restrictions.eq("id", logisticRegressionId)).
                    uniqueResult();
            session.delete(savedLogisticRegressionSearchResult);


            savedLogisticRegressionSearchResult = (LogisticRegressionSearchResult) session.createCriteria(LogisticRegressionSearchResult.class).
                    add(Restrictions.eq("id", logisticRegressionId)).
                    uniqueResult();
            assertEquals(savedLogisticRegressionSearchResult, null);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            fail();
        } finally {
            session.close();
        }
    }

    private void deleteSearchResult(Long logisticRegressionId) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            LogisticRegressionSearchResult logisticRegressionSearchResult = (LogisticRegressionSearchResult) session.createCriteria(LogisticRegressionSearchResult.class).
                    add(Restrictions.eq("id", logisticRegressionId)).
                    uniqueResult();
            session.delete(logisticRegressionSearchResult);

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