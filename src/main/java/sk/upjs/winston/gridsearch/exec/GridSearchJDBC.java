package sk.upjs.winston.gridsearch.exec;

import sk.upjs.winston.gridsearch.algorithms.SvmModel;
import sk.upjs.winston.gridsearch.database.DatabaseManager;
import sk.upjs.winston.gridsearch.model.Dataset;
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
import java.sql.SQLException;

/**
 * Created by stefan on 10/23/14.
 */
public class GridSearchJDBC {

    private static final String VERSION = "0.1";

    public static void main(String[] args) throws SQLException, IOException {
        DatabaseManager databaseManager = new DatabaseManager();

        if (args.length != 1) {
            System.out.println(getHelp());
            return;
        }

        String filePath = args[0];
//        String filePath = "./other/sensor_readings_2.arff";
//        args = new String[1];
//        args[0] = "iris.arff";

//        DATASET SESSION
        File dataFile = new File(filePath);
        BufferedReader reader = new BufferedReader(
                new FileReader(dataFile));
        Instances dataInstances = new Instances(reader);
        reader.close();
        // setting class attribute
        dataInstances.setClassIndex(dataInstances.numAttributes() - 1);

        Dataset dataset = new Dataset(dataInstances.relationName());
        //save dataset and its ID
        long datasetId = databaseManager.saveDatasetToDatabase(dataset);
        dataset.setId(datasetId);


        //SVM SESSION
        for (double c = SvmModel.MIN_C; c <= SvmModel.MAX_C; c += SvmModel.STEP_C) {
            for (double p = SvmModel.MIN_P; p <= SvmModel.MAX_P; p += SvmModel.STEP_P) {
                SvmModel svmModel = new SvmModel();

                double rmse;

                rmse = svmModel.svm(dataInstances, new StringKernel(), c, p);
                if (rmse != SvmModel.ERROR_DURING_CLASSIFICATION) {
                    SvmSearchResult res = new SvmSearchResult(dataset, rmse, SvmSearchResult.KERNEL_STRING_KERNEL, c, p);
                    databaseManager.saveSvmSearchResultToDatabase(res);
                }
                rmse = svmModel.svm(dataInstances, new PolyKernel(), c, p);
                if (rmse != SvmModel.ERROR_DURING_CLASSIFICATION) {
                    SvmSearchResult res = new SvmSearchResult(dataset, rmse, SvmSearchResult.KERNEL_POLYNOMIAL_KERNEL, c, p);
                    databaseManager.saveSvmSearchResultToDatabase(res);
                }
                rmse = svmModel.svm(dataInstances, new NormalizedPolyKernel(), c, p);
                if (rmse != SvmModel.ERROR_DURING_CLASSIFICATION) {
                    SvmSearchResult res = new SvmSearchResult(dataset, rmse, SvmSearchResult.KERNEL_NORMALIZED_POLYNOMIAL_KERNEL, c, p);
                    databaseManager.saveSvmSearchResultToDatabase(res);
                }
                rmse = svmModel.svm(dataInstances, new RBFKernel(), c, p);
                if (rmse != SvmModel.ERROR_DURING_CLASSIFICATION) {
                    SvmSearchResult res = new SvmSearchResult(dataset, rmse, SvmSearchResult.KERNEL_RBF_KERNEL, c, p);
                    databaseManager.saveSvmSearchResultToDatabase(res);
                }
                System.out.println("Processed SVM with c=" + c + ", p=" + p + " and dataset: " + dataset.getDatasetName());
            }
        }
        System.out.println("Dataset " + dataset.getDatasetName() + " processed.");
    }

    public static String getHelp() {
        String help = "Welcome to Winston Grid Search v" + VERSION + "\n\nUsage: ";
        help += "java -jar WinstonGridSearch.jar <datasetFile.arff>";

        return help;
    }
}
