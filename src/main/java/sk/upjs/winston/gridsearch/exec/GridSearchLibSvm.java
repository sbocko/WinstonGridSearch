package sk.upjs.winston.gridsearch.exec;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import sk.upjs.winston.gridsearch.algorithms.DecisionTreeModel;
import sk.upjs.winston.gridsearch.algorithms.KnnModel;
import sk.upjs.winston.gridsearch.algorithms.LogisticRegressionModel;
import sk.upjs.winston.gridsearch.model.ComputationTimeForResult;
import sk.upjs.winston.gridsearch.model.Dataset;
import sk.upjs.winston.gridsearch.model.SearchResult;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;

/**
 * Created by stefan on 11/6/14.
 */
public class GridSearchLibSvm {

    private static final String VERSION = "0.1-libsvm";
    private static SessionFactory factory;

    public static void main(String[] args) throws IOException {
//        DatabaseManagerLibSVM databaseManager = new DatabaseManagerLibSVM();

        if (args.length != 1) {
            System.out.println(getHelp());
            return;
        }
        String filePath = args[0];

//        String filePath = "./other/sensor_readings_2.arff";

        //get dataset name
        File dataFile = new File(filePath);
        BufferedReader reader = new BufferedReader(
                new FileReader(dataFile));
        Instances dataInstances = new Instances(reader);
        reader.close();
        String datasetName = dataInstances.relationName();

        //setup session factory
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }


        //KNN SESSION
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Dataset dataset = (Dataset) session.createQuery("FROM Dataset WHERE name=" + datasetName).uniqueResult();

            KnnModel knn = new KnnModel();
            Set<SearchResult> results = knn.knnSearch(dataset, dataInstances);
            saveSearchResults(session, results);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        //DECISION TREE SESSION

        session = factory.openSession();
        tx = null;
        try {
            tx = session.beginTransaction();
            Dataset dataset = (Dataset) session.createQuery("FROM Dataset WHERE name=" + datasetName).uniqueResult();

            DecisionTreeModel decisionTree = new DecisionTreeModel();
            Set<SearchResult> results = decisionTree.j48Search(dataset, dataInstances);
            saveSearchResults(session, results);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        //LOGISTIC REGRESSION SESSION

        session = factory.openSession();
        tx = null;
        try {
            tx = session.beginTransaction();
            Dataset dataset = (Dataset) session.createQuery("FROM Dataset WHERE name=" + datasetName).uniqueResult();

            LogisticRegressionModel logisticRegressionModel = new LogisticRegressionModel();
            Set<SearchResult> results = logisticRegressionModel.logisticRegressionSearch(dataset, dataInstances);
            saveSearchResults(session, results);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

////        DATASET SESSION
//        File dataFile = new File(filePath);
//        BufferedReader reader = new BufferedReader(
//                new FileReader(dataFile));
//        Instances dataInstances = new Instances(reader);
//        reader.close();
//        // setting class attribute
//        dataInstances.setClassIndex(dataInstances.numAttributes() - 1);
//
//        Dataset dataset = new Dataset(dataInstances.relationName());
//        //save dataset and its ID
//        long datasetId = databaseManager.saveDatasetToDatabase(dataset);
//        dataset.setId(datasetId);
//        ArrayList<SvmSearchResultLibSVM> svmResultsToPersist = new ArrayList<SvmSearchResultLibSVM>(4000);
//
//        //SVM SESSION
//        for (double c = 0; c <= 10; c += 0.1) {
//            for (double g = 0.00001; g <= 10; g *= 10) {
//                //initialize svm classifier
//                LibSVM svm = new LibSVM();
//                svm.setCost(c);
//                svm.setGamma(g);
//
////                String[] oldOptions = svm.getOptions();
////                String[] options = new String[oldOptions.length+1];
////                for (int i = 0; i < oldOptions.length; i++) {
////                    options[i] = oldOptions[i];
////                }
////                options[options.length-1] = "-q";
////                svm.setOptions(options);
//
//                try {
//                    svm.buildClassifier(dataInstances);
//                    Evaluation evaluation = new Evaluation(dataInstances);
//                    evaluation.crossValidateModel(svm, dataInstances, 5, new Random(1));
//                    SvmSearchResultLibSVM svmSearchResultLibSVM = new SvmSearchResultLibSVM(dataset, evaluation.rootMeanSquaredError(), SvmSearchResultLibSVM.KERNEL_RBF_KERNEL, c, g);
//                    svmResultsToPersist.add(svmSearchResultLibSVM);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                svm.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_LINEAR, LibSVM.TAGS_KERNELTYPE));
//                try {
//                    svm.buildClassifier(dataInstances);
//                    Evaluation evaluation = new Evaluation(dataInstances);
//                    evaluation.crossValidateModel(svm, dataInstances, 5, new Random(1));
//                    SvmSearchResultLibSVM svmSearchResultLibSVM = new SvmSearchResultLibSVM(dataset, evaluation.rootMeanSquaredError(), SvmSearchResultLibSVM.KERNEL_LINEAR_KERNEL, c, g);
//                    svmResultsToPersist.add(svmSearchResultLibSVM);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        databaseManager.saveSvmSearchResultsToDatabase(svmResultsToPersist);
//        System.out.println("Dataset " + dataset.getDatasetName() + " processed.");
//    }
    }

    public static String getHelp() {
        String help = "Welcome to Winston Grid Search v" + VERSION + "\n\nUsage: ";
        help += "java -jar WinstonGridSearch.jar <datasetFile.arff>";

        return help;
    }

    private static void saveSearchResults(Session session, Set<SearchResult> searchResults) {
        for (SearchResult result : searchResults) {
            if (result != null) {
                session.save(result);
            }
        }
        session.flush();
    }
}
