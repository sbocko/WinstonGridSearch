package sk.upjs.winston.gridsearch.exec;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import sk.upjs.winston.gridsearch.algorithms.DecisionTreeModel;
import sk.upjs.winston.gridsearch.algorithms.KnnModel;
import sk.upjs.winston.gridsearch.algorithms.LogisticRegressionModel;
import sk.upjs.winston.gridsearch.algorithms.SvmModel;
import sk.upjs.winston.gridsearch.model.ComputationTimeForResult;
import sk.upjs.winston.gridsearch.model.Dataset;
import sk.upjs.winston.gridsearch.model.SearchResult;
import sk.upjs.winston.gridsearch.model.SvmSearchResult;
import weka.classifiers.functions.supportVector.NormalizedPolyKernel;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.classifiers.functions.supportVector.StringKernel;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GridSearch2 {
    private static final Logger logger = Logger.getLogger(RandomSearch.class.getName());
    private static final double VERSION = 0.1;

    private static SessionFactory factory;

    public static void main(String[] args) throws IOException{
        if (args.length != 1) {
            System.out.println(getHelp());
            return;
        }

        String filePath = args[0];
//        String filePath = "./other/iris.arff";
//        args = new String[1];
//        args[0] = "iris.arff";

//        File df = new File(filePath);
//        BufferedReader r = new BufferedReader(
//                new FileReader(df));
//        Instances di = new Instances(r);
//        r.close();
//        // setting class attribute
//        di.setClassIndex(di.numAttributes() - 1);
//
//        for (int i = 0; i < 10; i++) {
//            KnnModel kn = new KnnModel();
//            double res = kn.knn(di, 3);
//            System.out.println(res);
//        }
//
//        if(true){
//            return;
//        }


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
        long startTime = System.currentTimeMillis();
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

            KnnModel knn = new KnnModel();
            Set<SearchResult> results = knn.knnSearch(dataset, dataInstances);
            saveSearchResults(session, results);

            logger.log(Level.FINE, "Dataset " + args[0] + " processed knn.");

            int time = (int) (System.currentTimeMillis() - startTime);
            ComputationTimeForResult computationTime = new ComputationTimeForResult(dataset, "knn", time);
            session.save(computationTime);

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
        startTime = System.currentTimeMillis();
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

            DecisionTreeModel decisionTree = new DecisionTreeModel();
            Set<SearchResult> results = decisionTree.j48Search(dataset, dataInstances);
            saveSearchResults(session, results);


            int time = (int) (System.currentTimeMillis() - startTime);
            ComputationTimeForResult computationTime = new ComputationTimeForResult(dataset, "decision_tree", time);
            session.save(computationTime);

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
        startTime = System.currentTimeMillis();
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

            LogisticRegressionModel logisticRegressionModel = new LogisticRegressionModel();
            Set<SearchResult> results = logisticRegressionModel.logisticRegressionSearch(dataset, dataInstances);
            saveSearchResults(session, results);

            int time = (int) (System.currentTimeMillis() - startTime);
            ComputationTimeForResult computationTime = new ComputationTimeForResult(dataset, "logistic_regression", time);
            session.save(computationTime);

            logger.log(Level.FINE, "Dataset " + args[0] + " processed logistic regression.");
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            logger.log(Level.FINE, "Error for dataset  " + args[0] + " : " + e.getMessage());
        } finally {
            session.close();
        }

//        //SVM SESSION
        startTime = System.currentTimeMillis();
        for (double c = SvmModel.MIN_C; c <= SvmModel.MAX_C; c += SvmModel.STEP_C) {
            for (double p = SvmModel.MIN_P; p <= SvmModel.MAX_P; p += SvmModel.STEP_P) {
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

                    double rmse;

                    rmse = svmModel.svm(dataInstances, new StringKernel(), c, p);
                    if (rmse != SvmModel.ERROR_DURING_CLASSIFICATION) {
                        SearchResult res = new SvmSearchResult(dataset, rmse, SvmSearchResult.KERNEL_STRING_KERNEL, c, p);
                        session.save(res);
                    }
                    rmse = svmModel.svm(dataInstances, new PolyKernel(), c, p);
                    if (rmse != SvmModel.ERROR_DURING_CLASSIFICATION) {
                        SearchResult res = new SvmSearchResult(dataset, rmse, SvmSearchResult.KERNEL_POLYNOMIAL_KERNEL, c, p);
                        session.save(res);
                    }
                    rmse = svmModel.svm(dataInstances, new NormalizedPolyKernel(), c, p);
                    if (rmse != SvmModel.ERROR_DURING_CLASSIFICATION) {
                        SearchResult res = new SvmSearchResult(dataset, rmse, SvmSearchResult.KERNEL_NORMALIZED_POLYNOMIAL_KERNEL, c, p);
                        session.save(res);
                    }
                    rmse = svmModel.svm(dataInstances, new RBFKernel(), c, p);
                    if (rmse != SvmModel.ERROR_DURING_CLASSIFICATION) {
                        SearchResult res = new SvmSearchResult(dataset, rmse, SvmSearchResult.KERNEL_RBF_KERNEL, c, p);
                        session.save(res);
                    }
                    System.out.println("Processed SVM with c=" + c + ", p=" + p + " and dataset: " + dataset.getDatasetName());

                    tx.commit();
                } catch (Exception e) {
                    if (tx != null) tx.rollback();
                    e.printStackTrace();
                    logger.log(Level.FINE, "Error for dataset  " + args[0] + " : " + e.getMessage());
                } finally {
                    session.close();
                }
            }
        }

        session = factory.openSession();
        tx = null;
        try {
            tx = session.beginTransaction();
            Dataset dataset = (Dataset) session.createQuery("FROM Dataset WHERE id=" + datasetId).uniqueResult();
            int time = (int) (System.currentTimeMillis() - startTime);
            ComputationTimeForResult computationTime = new ComputationTimeForResult(dataset, "svm", time);
            session.save(computationTime);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            logger.log(Level.FINE, "Error for dataset  " + args[0] + " : " + e.getMessage());
        } finally {
            session.close();
        }

        logger.log(Level.FINE, "Dataset " + args[0] + " processed successfully.");
    }

    private static void saveSearchResults(Session session, Set<SearchResult> searchResults) {
        for (SearchResult result : searchResults) {
            if (result != null) {
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
}
