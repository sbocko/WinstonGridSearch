package sk.upjs.winston.gridsearch.model;

import junit.framework.TestCase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

public class SvmSearchResultTest extends TestCase {
    public static final String TEST_KERNEL = SvmSearchResult.KERNEL_POLYNOMIAL_KERNEL;
    public static final double TEST_RMSE = 0.9;
    public static final double TEST_C = 1.0;
    public static final double TEST_P = 1.0e-12;
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
    public void testSaveSvmSearchResult() throws Exception {
        Long svmId = null;

        //save svmSearchResult object to DB
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            SearchResult svmSearchResult = new SvmSearchResult(this.testDataset, TEST_RMSE, TEST_KERNEL, TEST_C, TEST_P);
            svmId = (Long) session.save(svmSearchResult);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            fail();
        } finally {
            session.close();
        }

        //check if svmSearchResult object exists in DB
        session = factory.openSession();
        tx = null;
        try {
            tx = session.beginTransaction();

            SvmSearchResult svmSearchResult = (SvmSearchResult) session.createCriteria(SvmSearchResult.class).
                    add(Restrictions.eq("id", svmId)).
                    uniqueResult();
            assertEquals(svmSearchResult.getDataset().getDatasetName(), testDataset.getDatasetName());
            assertEquals(svmSearchResult.getRmse(), TEST_RMSE);
            assertEquals(svmSearchResult.getKernel(), TEST_KERNEL);
            assertEquals(svmSearchResult.getComplexityConstant(), TEST_C);
            assertEquals(svmSearchResult.getEpsilonRoundOffError(), TEST_P);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            fail();
        } finally {
            session.close();
        }

//        remove testing data from DB
        deleteSearchResult(svmId);
    }

    @Test
    public void testDeleteSvmSearchResult() throws Exception {
        Long svmId = null;

        //save svmSearchResult object to DB
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            SearchResult svmSearchResult = new SvmSearchResult(this.testDataset, TEST_RMSE, TEST_KERNEL, TEST_C, TEST_P);
            svmId = (Long) session.save(svmSearchResult);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            fail();
        } finally {
            session.close();
        }

        //check if svmSearchResult object exists in DB
        session = factory.openSession();
        tx = null;
        try {
            tx = session.beginTransaction();

            SvmSearchResult savedSvmSearchResult = (SvmSearchResult) session.createCriteria(SvmSearchResult.class).
                    add(Restrictions.eq("id", svmId)).
                    uniqueResult();
            session.delete(savedSvmSearchResult);


            savedSvmSearchResult = (SvmSearchResult) session.createCriteria(SvmSearchResult.class).
                    add(Restrictions.eq("id", svmId)).
                    uniqueResult();
            assertEquals(savedSvmSearchResult, null);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            fail();
        } finally {
            session.close();
        }
    }

    private void deleteSearchResult(Long svmId) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            SvmSearchResult svmSearchResult = (SvmSearchResult) session.createCriteria(SvmSearchResult.class).
                    add(Restrictions.eq("id", svmId)).
                    uniqueResult();
            session.delete(svmSearchResult);

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