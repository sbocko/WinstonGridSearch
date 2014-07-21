package sk.upjs.winston.gridsearch;

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

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
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

            Set<SearchResult> results = new HashSet<SearchResult>();

            KnnModel knn = new KnnModel();
            results = knn.knnSearch(dataset, dataInstances);
            saveSearchResults(session, results);

            DecisionTreeModel decisionTree = new DecisionTreeModel();
            results = decisionTree.j48Search(dataset, dataInstances);
            saveSearchResults(session, results);

            LogisticRegressionModel logisticRegression = new LogisticRegressionModel();
            results = logisticRegression.logisticRegressionSearch(dataset, dataInstances);
            saveSearchResults(session, results);

            SvmModel svm = new SvmModel();
            results = svm.svmSearch(dataset, dataInstances);
            saveSearchResults(session, results);

            logger.log(Level.FINE, "Dataset " + args[0] + " processed successfully.");
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            logger.log(Level.FINE, "Error for dataset  " + args[0] + " : " + e.getMessage());
        } finally {
            session.close();
        }

    }

    private static void saveSearchResults(Session session, Set<SearchResult> searchResults) {
        for (SearchResult result : searchResults) {
            session.save(result);
        }
        session.flush();
    }

//    public static void main(String[] args) {
//        if(args.length == 0){
//            System.out.println(getHelp());
//
//        }
//
//
//
////        CSV2ArffConverter converter = new CSV2ArffConverter();
////        File csvInput = new File("other/car.csv");
//        File arffOutput = new File("other/iris.arff");
////        boolean converted = converter.convertCsvToArff(csvInput,arffOutput);
////        System.out.println("Conversion success: " + converted);
//
//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(
//                    new FileReader(arffOutput));
//            Instances data = new Instances(reader);
//            reader.close();
//            // setting class attribute
//            data.setClassIndex(data.numAttributes() - 1);
//
//            GridSearch gs = new GridSearch();
//            IBk decisionTree = new IBk();
//
//            gs.setClassifier(decisionTree);
//            System.out.println(gs.getBestClassifier());
//            Evaluation evaluation = new Evaluation(data);
//            evaluation.crossValidateModel(decisionTree, data, 10, new Random(1));
//            System.out.println(evaluation.toSummaryString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static String getHelp() {
        String help = "Welcome to Winston Grid Search v" + VERSION + "\n\nUsage: ";
        help += "java -jar WinstonGridSearch.jar <datasetFile.arff>";

        return help;
    }
}
