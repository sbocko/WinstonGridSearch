package sk.upjs.winston.gridsearch.exec;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import sk.upjs.winston.gridsearch.algorithms.DecisionTreeModel;
import sk.upjs.winston.gridsearch.algorithms.KnnModel;
import sk.upjs.winston.gridsearch.algorithms.LogisticRegressionModel;
import sk.upjs.winston.gridsearch.algorithms.SvmModel;
import sk.upjs.winston.gridsearch.database.DatabaseConnector;
import sk.upjs.winston.gridsearch.model.*;
import weka.classifiers.functions.supportVector.*;
import weka.core.Instances;

import java.io.*;
import java.util.List;

/**
 * Created by stefan on 9/6/14.
 */
public class SimilarityBestResultSearch {
    public static final int ERROR_READING_DATA_FILE = -2;
    private static final String[] datasetNamesFromRandomSearch = new String[]{"ann-train.data",
            "bezdekIris.data", "breast-cancer-wisconsin.data", "allbp.data",
            "german.data", "kinship.data", "glass.data", "Hill_Valley_with_noise_Training.data",
            "adult+stretch.data", "auto-mpg.data-original", "adult-stretch.data",
            "dermatology.data", "ecoli.data", "parkinsons.data", "processed.switzerland.data",
            "secom_labels.data", "hepatitis.data", "haberman.data", "tae.data",
            "housing.data", "hayes-roth.data", "semeion.data", "slump_test.data",
            "flag.data", "bridges.data.version2", "movement_libras_5.data",
            "processed.cleveland.data", "sponge.data", "anneal.data", "test5.data",
            "backup-large.data", "reprocessed.hungarian.data", "mammographic_masses.data",
            "nursery.data", "cmc.data", "house-votes-84.data", "test3.data",
            "train5.data", "servo.data", "switzerland.data", "original.data",
            "post-operative.data", "lenses.data", "move.data", "CalIt2.data",
            "long-beach-va.data", "processed.va.data", "ionosphere.data", "train3.data",
            "eighthr.data", "sick.data", "trains-transformed.data", "test4.data",
            "car.data", "new-thyroid.data", "sensor_readings_4.data", "sick-euthyroid.data",
            "iris.data", "test2.data", "communities.data", "o-ring-erosion-or-blowby.data",
            "bupa.data", "new.data", "horse-colic.data", "gripper.data", "audiology.standardized.data",
            "dis.data", "allhypo.data", "meta.data", "soybean-large.data", "page-blocks.data",
            "o-ring-erosion-only.data", "hypothyroid.data", "movement_libras_10.data",
            "poker-hand-training-true.data", "thyroid0387.data", "sensor_readings_2.data",
            "spambase.data", "allhyper.data", "kr-vs-kp.data", "yellow-small.data",
            "movement_libras.data", "shuttle-landing-control.data", "Hill_Valley_without_noise_Training.data",
            "flare.data1", "balance-scale.data", "movement_libras_1.data", "movement_libras_9.data",
            "processed.hungarian.data", "abalone.data", "soybean-small.data",
            "agaricus-lepiota.data", "train6.data", "hungarian.data", "ann-test.data",
            "Hill_Valley_with_noise_Testing.data", "synthetic_control.data", "crx.data",
            "imports-85.data", "arrhythmia.data", "train2.data", "Hill_Valley_without_noise_Testing.data",
            "allrep.data", "clean1.data", "letter-recognition.data", "Dodgers.data",
            "segmentation.data", "lrs.data", "krkopt.data", "magic04.data", "wine.data",
            "zoo.data", "wdbc.data", "vowel-context.data", "clean2.data", "wpbc.data",
            "movement_libras_8.data", "test1.data", "yacht_hydrodynamics.data",
            "CNAE-9.data", "sensor_readings_24.data", "pima-indians-diabetes.data",
            "onehr.data", "waveform-+noise.data", "train4.data", "turn.data",
            "tic-tac-toe.data", "yellow-small+adult-stretch.data", "splice.data",
            "yeast.data", "waveform.data", "isolet5.data", "adult.data", "connect-4.data",
            "secom.data", "ad.data", "synthetic.data", "isolet1+2+3+4.data",
            "kddcup.data_10_percent", "covtype.data", "USCensus1990.data.txt"};
    private static SessionFactory factory;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("No datafile on input.");
            return;
        }
        String datasetFilePath = args[0];
//        String datasetFilePath = "./other/iris.arff";

        // create session factory
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            ex.printStackTrace();
            return;
        }

        // create new db session
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            DatabaseConnector databaseConnector = new DatabaseConnector(session);
            String datasetName = getDatasetNameFromDataFile(datasetFilePath);
            Dataset targetDataset = databaseConnector.getDatasetByName(datasetName);
            if (targetDataset == null) {
                System.out.println("Could not find dataset for name: " + datasetName);
                return;
            }

            // get all datasets
            List<Dataset> datasetList = databaseConnector.getApplicableDatasetsForBestResultRandomSimilaritySearch();
            datasetList.remove(targetDataset);

            for (Dataset otherDataset : datasetList) {
                System.out.println("Processing dataset: " + targetDataset + " for comparison with: " + otherDataset.getDatasetName());
                SearchResult bestSearchResultForOtherDataset = databaseConnector.bestSearchResultForDataset(otherDataset);
                SearchResult searchResultFromSS = databaseConnector.similaritySearchForDataset(targetDataset, bestSearchResultForOtherDataset);
                double similaritySearchRmse;
                if (searchResultFromSS == null) {
                    similaritySearchRmse = performNewSearchWithGivenHyperparametersForData(bestSearchResultForOtherDataset, datasetFilePath);
                } else {
                    similaritySearchRmse = searchResultFromSS.getRmse();
                }
                if(bestSearchResultForOtherDataset != null && similaritySearchRmse != -1) {
                    double dissimilarity = databaseConnector.datasetDissimilarityFromSearchResultsRmse(bestSearchResultForOtherDataset.getRmse(), similaritySearchRmse);
                    SimilarityBestSearchResult similarityBestSearchResult = new SimilarityBestSearchResult(targetDataset, similaritySearchRmse, otherDataset, dissimilarity);
                    session.save(similarityBestSearchResult);
                }
            }


            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return;
        } finally {
            session.close();
        }
        System.out.println("Done.");

    }

    private static double performNewSearchWithGivenHyperparametersForData(SearchResult template, String filePath) {
        double rmse = ERROR_READING_DATA_FILE;

        try {
            File dataFile = new File(filePath);
            BufferedReader reader = new BufferedReader(
                    new FileReader(dataFile));
            Instances dataInstances = new Instances(reader);
            reader.close();

            dataInstances.setClassIndex(dataInstances.numAttributes() - 1);

            if (template instanceof KnnSearchResult) {
                KnnModel knn = new KnnModel();
                rmse = knn.knn(dataInstances, ((KnnSearchResult) template).getK());
            }
            if (template instanceof DecisionTreeSearchResult) {
                DecisionTreeModel decisionTree = new DecisionTreeModel();
                rmse = decisionTree.j48DecisionTreeAnalysis(dataInstances,
                        ((DecisionTreeSearchResult) template).getMinimumNumberOfInstancesPerLeaf(),
                        (float) ((DecisionTreeSearchResult) template).getConfidenceFactor(),
                        ((DecisionTreeSearchResult) template).isUnpruned());
            }
            if (template instanceof LogisticRegressionSearchResult) {
                LogisticRegressionModel logisticRegression = new LogisticRegressionModel();
                rmse = logisticRegression.logisticRegression(dataInstances,
                        ((LogisticRegressionSearchResult) template).getRidge(),
                        ((LogisticRegressionSearchResult) template).getMaximumNumberOfIterations());
            }
            if (template instanceof SvmSearchResult) {
                SvmModel svm = new SvmModel();
                Kernel kernel = null;
                String kernelString = ((SvmSearchResult) template).getKernel();

                if (kernelString.equals(SvmSearchResult.KERNEL_STRING_KERNEL)) {
                    kernel = new StringKernel();
                }
                if (kernelString.equals(SvmSearchResult.KERNEL_POLYNOMIAL_KERNEL)) {
                    kernel = new PolyKernel();
                }
                if (kernelString.equals(SvmSearchResult.KERNEL_NORMALIZED_POLYNOMIAL_KERNEL)) {
                    kernel = new NormalizedPolyKernel();
                }
                if (kernelString.equals(SvmSearchResult.KERNEL_RBF_KERNEL)) {
                    kernel = new RBFKernel();
                }
                rmse = svm.svm(dataInstances, kernel,
                        ((SvmSearchResult) template).getComplexityConstant(),
                        ((SvmSearchResult) template).getEpsilonRoundOffError());
            }
            return rmse;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return ERROR_READING_DATA_FILE;
        } catch (IOException e) {
            e.printStackTrace();
            return ERROR_READING_DATA_FILE;
        }
    }

    private static String getDatasetNameFromDataFile(String filePath){
        try {
            File dataFile = new File(filePath);
            BufferedReader reader = new BufferedReader(
                    new FileReader(dataFile));
            Instances dataInstances = new Instances(reader);
            reader.close();
            // setting class attribute
            return dataInstances.relationName();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
