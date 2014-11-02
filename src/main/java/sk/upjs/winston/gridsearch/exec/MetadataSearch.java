package sk.upjs.winston.gridsearch.exec;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import sk.upjs.winston.gridsearch.model.Metadata;
import weka.core.AttributeStats;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by stefan on 11/2/14.
 */
public class MetadataSearch {
    public static final String DATA_TYPE_INTEGER = "INT";
    public static final String DATA_TYPE_REAL = "REAL";
    public static final String DATA_TYPE_CATEGORICAL = "CAT";
    public static final String DATA_TYPE_MULTIVARIATE = "MULT";

    private static final String VERSION = "0.1";

    private static SessionFactory factory;

    public static void main(String[] args) throws IOException {
        // create session factory
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        // create new db session, save dataset and grid search results
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            File datasetDir = new File("/Users/stefan/Documents/School/diplomovka/arff_datasets/");
            for (File datasetFile : datasetDir.listFiles()) {
                System.out.println(datasetFile.getName());
                BufferedReader r = new BufferedReader(
                        new FileReader(datasetFile));
                Instances instances = new Instances(r);
                r.close();

                String filename = instances.relationName();
                int numberOfInstances = instances.numInstances();
                int numberOfAttributes = instances.numAttributes();
                String dataType = null;
                int missingValues = 0;

                boolean wasInt = false;
                boolean wasReal = false;
                boolean wasCategorical = false;
                for (int i = 0; i < numberOfAttributes; i++) {
                    AttributeStats attributeStats = instances.attributeStats(i);
                    if (i < numberOfAttributes - 1) {
                        boolean isNominal = instances.attribute(i).isNominal();
                        if (attributeStats.intCount != 0 && attributeStats.realCount == 0 && !isNominal) {
                            wasInt = true;
                        } else if (attributeStats.realCount != 0 && !isNominal) {
                            wasReal = true;
                        } else {
                            wasCategorical = true;
                        }
                    }
                    missingValues += attributeStats.missingCount;
                }
                if ((wasCategorical && wasInt) || (wasCategorical && wasReal) || (wasReal && wasInt)) {
                    dataType = DATA_TYPE_MULTIVARIATE;
                } else if (wasCategorical) {
                    dataType = DATA_TYPE_CATEGORICAL;
                } else if (wasReal) {
                    dataType = DATA_TYPE_REAL;
                } else if (wasInt) {
                    dataType = DATA_TYPE_INTEGER;
                }

                Metadata metadata = new Metadata(filename, numberOfInstances, numberOfAttributes, dataType, missingValues);
                session.save(metadata);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return;
        } finally {
            session.close();
            session.getSessionFactory().close();
        }
    }
}
