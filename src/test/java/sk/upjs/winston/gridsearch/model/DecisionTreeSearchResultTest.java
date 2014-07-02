package sk.upjs.winston.gridsearch.model;

import junit.framework.TestCase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

public class DecisionTreeSearchResultTest extends TestCase {
    public static final double TEST_RMSE = 0.1;
    public static final double TEST_CONFIDENCE_FACTOR = 0.5;
    public static final int TEST_MINIMUM_NUMBER_OF_INSTANCES_PER_LEAF = 2;
    public static final boolean TEST_UNPRUNED = true;
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
    public void testSaveDecisionTreeSearchResult() throws Exception {
        Long decisionTreeId = null;

        //save decisionTreeSearchResult object to DB
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            SearchResult decisionTreeSearchResult =
                    new DecisionTreeSearchResult(this.testDataset, TEST_RMSE, TEST_CONFIDENCE_FACTOR,
                            TEST_MINIMUM_NUMBER_OF_INSTANCES_PER_LEAF, TEST_UNPRUNED);
            decisionTreeId = (Long) session.save(decisionTreeSearchResult);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            fail();
        } finally {
            session.close();
        }

        //check if decisionTreeSearchResult object exists in DB
        session = factory.openSession();
        tx = null;
        try {
            tx = session.beginTransaction();

            DecisionTreeSearchResult decisionTreeSearchResult = (DecisionTreeSearchResult) session.createCriteria(DecisionTreeSearchResult.class).
                    add(Restrictions.eq("id", decisionTreeId)).
                    uniqueResult();
            assertEquals(decisionTreeSearchResult.getDataset().getDatasetName(), testDataset.getDatasetName());
            assertEquals(decisionTreeSearchResult.getRmse(), TEST_RMSE);
            assertEquals(decisionTreeSearchResult.getConfidenceFactor(), TEST_CONFIDENCE_FACTOR);
            assertEquals(decisionTreeSearchResult.getMinimumNumberOfInstancesPerLeaf(), TEST_MINIMUM_NUMBER_OF_INSTANCES_PER_LEAF);
            assertEquals(decisionTreeSearchResult.isUnpruned(), TEST_UNPRUNED);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            fail();
        } finally {
            session.close();
        }

//        remove testing data from DB
        deleteSearchResult(decisionTreeId);
    }

    @Test
    public void testDeleteDecisionTreeSearchResult() throws Exception {
        Long decisionTreeId = null;

        //save decisionTreeSearchResult object to DB
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            SearchResult decisionTreeSearchResult =
                    new DecisionTreeSearchResult(this.testDataset, TEST_RMSE, TEST_CONFIDENCE_FACTOR,
                            TEST_MINIMUM_NUMBER_OF_INSTANCES_PER_LEAF, TEST_UNPRUNED);
            decisionTreeId = (Long) session.save(decisionTreeSearchResult);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            fail();
        } finally {
            session.close();
        }

        //check if decisionTreeSearchResult object exists in DB
        session = factory.openSession();
        tx = null;
        try {
            tx = session.beginTransaction();

            DecisionTreeSearchResult savedDecisionTreeSearchResult = (DecisionTreeSearchResult) session.createCriteria(DecisionTreeSearchResult.class).
                    add(Restrictions.eq("id", decisionTreeId)).
                    uniqueResult();
            session.delete(savedDecisionTreeSearchResult);


            savedDecisionTreeSearchResult = (DecisionTreeSearchResult) session.createCriteria(DecisionTreeSearchResult.class).
                    add(Restrictions.eq("id", decisionTreeId)).
                    uniqueResult();
            assertEquals(savedDecisionTreeSearchResult, null);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            fail();
        } finally {
            session.close();
        }
    }

    private void deleteSearchResult(Long decisionTreeId) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            DecisionTreeSearchResult decisionTreeSearchResult = (DecisionTreeSearchResult) session.createCriteria(DecisionTreeSearchResult.class).
                    add(Restrictions.eq("id", decisionTreeId)).
                    uniqueResult();
            session.delete(decisionTreeSearchResult);

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
    private Dataset createAndSaveTestDataset() {
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
    private void deleteTestDataset(Dataset savedDataset) {
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