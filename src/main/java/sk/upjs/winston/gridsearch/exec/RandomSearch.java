package sk.upjs.winston.gridsearch.exec;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import sk.upjs.winston.gridsearch.algorithms.DecisionTreeModel;
import sk.upjs.winston.gridsearch.algorithms.KnnModel;
import sk.upjs.winston.gridsearch.algorithms.LogisticRegressionModel;
import sk.upjs.winston.gridsearch.algorithms.SvmModel;
import sk.upjs.winston.gridsearch.model.Dataset;
import sk.upjs.winston.gridsearch.model.SearchResult;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RandomSearch {
    private static final Logger logger = Logger.getLogger(RandomSearch.class.getName());
    private static final double VERSION = 0.1;

    private static SessionFactory factory;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println(getHelp());
            return;
        }

        String filePath = args[0];
//        String filePath = "./other/iris.arff";

        // create session factory
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        Long datasetId = -1l;

        //DATASET SESSION

        // create new db session, save dataset and grid search results
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            File dataFile = new File(filePath);
            BufferedReader reader = new BufferedReader(
                    new FileReader(dataFile));
            Instances dataInstances = new Instances(reader);
            reader.close();
            // setting class attribute
            dataInstances.setClassIndex(dataInstances.numAttributes() - 1);

            Dataset dataset = new Dataset(dataInstances.relationName());
            session.save(dataset);
            datasetId = dataset.getId();

            logger.log(Level.FINE, "Dataset " + args[0] + " processed successfully.");
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            logger.log(Level.FINE, "Error for dataset  " + args[0] + " : " + e.getMessage());
            return;
        } finally {
            session.close();
        }

        //KNN SESSION

        session = factory.openSession();
        tx = null;
        try {
            tx = session.beginTransaction();

            File dataFile = new File(filePath);
            BufferedReader reader = new BufferedReader(
                    new FileReader(dataFile));
            Instances dataInstances = new Instances(reader);
            reader.close();
            // setting class attribute
            dataInstances.setClassIndex(dataInstances.numAttributes() - 1);

            Dataset dataset = (Dataset) session.createQuery("FROM Dataset WHERE id=" + datasetId).uniqueResult();

            Set<SearchResult> results = new HashSet<SearchResult>();
            for (int i = 0; i < 100; i++) {
                KnnModel knn = new KnnModel();
                results.add(knn.knnRandomAnalysis(dataInstances, dataset));
            }
            saveSearchResults(session,results);

            logger.log(Level.FINE, "Dataset " + args[0] + " processed knn.");
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            logger.log(Level.FINE, "Error for dataset  " + args[0] + " : " + e.getMessage());
        } finally {
            session.close();
        }

        //DECISION TREE SESSION

        session = factory.openSession();
        tx = null;
        try {
            tx = session.beginTransaction();

            File dataFile = new File(filePath);
            BufferedReader reader = new BufferedReader(
                    new FileReader(dataFile));
            Instances dataInstances = new Instances(reader);
            reader.close();
            // setting class attribute
            dataInstances.setClassIndex(dataInstances.numAttributes() - 1);

            Dataset dataset = (Dataset) session.createQuery("FROM Dataset WHERE id=" + datasetId).uniqueResult();

            Set<SearchResult> results = new HashSet<SearchResult>();
            for (int i = 0; i < 2000; i++) {
                DecisionTreeModel decisionTree = new DecisionTreeModel();
                results.add(decisionTree.j48DecisionTreeRandomAnalysis(dataInstances, dataset));
            }
            saveSearchResults(session, results);

            logger.log(Level.FINE, "Dataset " + args[0] + " processed decision tree.");
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            logger.log(Level.FINE, "Error for dataset  " + args[0] + " : " + e.getMessage());
        } finally {
            session.close();
        }

        //LOGISTIC REGRESSION SESSION

        session = factory.openSession();
        tx = null;
        try {
            tx = session.beginTransaction();

            File dataFile = new File(filePath);
            BufferedReader reader = new BufferedReader(
                    new FileReader(dataFile));
            Instances dataInstances = new Instances(reader);
            reader.close();
            // setting class attribute
            dataInstances.setClassIndex(dataInstances.numAttributes() - 1);

            Dataset dataset = (Dataset) session.createQuery("FROM Dataset WHERE id=" + datasetId).uniqueResult();

            Set<SearchResult> results = new HashSet<SearchResult>();
            for (int i = 0; i < 20; i++) {
                LogisticRegressionModel logisticRegressionModel = new LogisticRegressionModel();
                results.add(logisticRegressionModel.logisticRegressionRandomAnalysis(dataInstances, dataset));
            }
            saveSearchResults(session, results);

            logger.log(Level.FINE, "Dataset " + args[0] + " processed logistic regression.");
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            logger.log(Level.FINE, "Error for dataset  " + args[0] + " : " + e.getMessage());
        } finally {
            session.close();
        }

        //SVM SESSION
        for (int i = 0; i < 11880; i++) {
            session = factory.openSession();
            tx = null;
            try {
                tx = session.beginTransaction();

                File dataFile = new File(filePath);
                BufferedReader reader = new BufferedReader(
                        new FileReader(dataFile));
                Instances dataInstances = new Instances(reader);
                reader.close();
                // setting class attribute
                dataInstances.setClassIndex(dataInstances.numAttributes() - 1);

                Dataset dataset = (Dataset) session.createQuery("FROM Dataset WHERE id=" + datasetId).uniqueResult();

                SvmModel svmModel = new SvmModel();
                session.save(svmModel.svmRandomAnalysis(dataInstances, dataset));
                System.out.println("SVM saved: " + svmModel);

                tx.commit();
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
                logger.log(Level.FINE, "Error for dataset  " + args[0] + " : " + e.getMessage());
            } finally {
                session.close();
            }
        }
        logger.log(Level.FINE, "Dataset " + args[0] + " processed successfully.");
    }

    private static void saveSearchResults(Session session, Set<SearchResult> searchResults) {
        for (SearchResult result : searchResults) {
            if(result != null) {
                System.out.println("Saving result: " + result);
                session.save(result);
            }
        }
        session.flush();
    }

    public static String getHelp() {
        String help = "Welcome to Winston Grid Search v" + VERSION + "\n\nUsage: ";
        help += "java -jar WinstonGridSearch.jar <datasetFile.arff>";

        return help;
    }
}
