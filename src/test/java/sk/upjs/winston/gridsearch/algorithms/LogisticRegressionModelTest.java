package sk.upjs.winston.gridsearch.algorithms;

import junit.framework.TestCase;
import org.junit.Test;
import sk.upjs.winston.gridsearch.model.Dataset;
import sk.upjs.winston.gridsearch.model.LogisticRegressionSearchResult;
import sk.upjs.winston.gridsearch.model.Model;
import sk.upjs.winston.gridsearch.model.SearchResult;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Set;

public class LogisticRegressionModelTest extends TestCase {

    private static final double LOGISTIC_REGRESSION_ALGORITHM_R1_MINF_RMSE = 0.14243190834235903;

    @Test
    public void testLogisticRegressionSuccess() throws Exception {
        File dataFile = new File("other/iris.arff");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new FileReader(dataFile));
            Instances dataInstances = new Instances(reader);
            reader.close();
            // setting class attribute
            dataInstances.setClassIndex(dataInstances.numAttributes() - 1);

            LogisticRegressionModel logisticRegressionModel = new LogisticRegressionModel();
            double rmse = logisticRegressionModel.logisticRegression(dataInstances, 0, LogisticRegressionSearchResult.ITERATE_UNTIL_CONVERGENCE);
            assertEquals(rmse, LOGISTIC_REGRESSION_ALGORITHM_R1_MINF_RMSE);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testLogisticRegressionNullInstances() {
        LogisticRegressionModel logisticRegressionModel = new LogisticRegressionModel();
        double rmse = logisticRegressionModel.logisticRegression(null, 0, LogisticRegressionSearchResult.ITERATE_UNTIL_CONVERGENCE);
        assertEquals(rmse, (double) Model.ERROR_DURING_CLASSIFICATION);
    }

    @Test
    public void testLogisticRegressionWrongClassificationAttributeType() {
        File dataFile = new File("other/dataset.arff");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new FileReader(dataFile));
            Instances dataInstances = new Instances(reader);
            reader.close();
            // setting class attribute
            dataInstances.setClassIndex(dataInstances.numAttributes() - 1);

            LogisticRegressionModel logisticRegressionModel = new LogisticRegressionModel();
            double rmse = logisticRegressionModel.logisticRegression(dataInstances, 0, LogisticRegressionSearchResult.ITERATE_UNTIL_CONVERGENCE);
            assertEquals(rmse, (double) Model.ERROR_DURING_CLASSIFICATION);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testlogisticRegressionSearchNull() throws Exception {
        LogisticRegressionModel logisticRegressionModel = new LogisticRegressionModel();
        Set<SearchResult> searchResults = logisticRegressionModel.logisticRegressionSearch(null, null);
        assertEquals(searchResults.size(), 0);
    }

    @Test
    public void testlogisticRegressionSearchSuccess() throws Exception {
        File dataFile = new File("other/iris.arff");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new FileReader(dataFile));
            Instances dataInstances = new Instances(reader);
            reader.close();
            // setting class attribute
            dataInstances.setClassIndex(dataInstances.numAttributes() - 1);

            Dataset dataset = new Dataset(dataInstances.relationName());

            LogisticRegressionModel logisticRegressionModel = new LogisticRegressionModel();
            Set<SearchResult> searchResults = logisticRegressionModel.logisticRegressionSearch(dataset, dataInstances);
            assertNotSame(searchResults.size(), 0);

            for (SearchResult result : searchResults) {
                assertEquals(result.getDataset(), dataset);
                assertEquals(((LogisticRegressionSearchResult) result).getMaximumNumberOfIterations(), LogisticRegressionSearchResult.ITERATE_UNTIL_CONVERGENCE);
                assertEquals(result.getClass(), LogisticRegressionSearchResult.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }


}