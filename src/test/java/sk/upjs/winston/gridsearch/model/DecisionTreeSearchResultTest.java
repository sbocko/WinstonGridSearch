package sk.upjs.winston.gridsearch.model;

import junit.framework.TestCase;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DecisionTreeSearchResultTest extends TestCase {
    private SessionFactory factory;

    public void setUp() throws Exception {
        super.setUp();
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable e) {
            e.printStackTrace();
            fail();
        }
    }

    public void tearDown() throws Exception {
        factory.close();
    }
}