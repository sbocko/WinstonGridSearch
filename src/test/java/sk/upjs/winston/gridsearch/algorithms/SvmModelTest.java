package sk.upjs.winston.gridsearch.algorithms;

import junit.framework.TestCase;
import org.junit.Test;
import sk.upjs.winston.gridsearch.model.Dataset;
import sk.upjs.winston.gridsearch.model.Model;
import sk.upjs.winston.gridsearch.model.SearchResult;
import sk.upjs.winston.gridsearch.model.SvmSearchResult;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Set;

public class SvmModelTest extends TestCase {

    public static final int TEST_COMPLEXITY_CONSTANT = 1;
    public static final double TEST_EPSILON_ROUND_OFF_ERROR = 1.0e-12;
    private static final double IRIS_POLY_KERNEL_C1_P_DEFAULT_RMSE = 0.2880329199292385;

    @Test
    public void testSvmSuccess() throws Exception {
        File dataFile = new File("other/iris.arff");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new FileReader(dataFile));
            Instances dataInstances = new Instances(reader);
            reader.close();
            // setting class attribute
            dataInstances.setClassIndex(dataInstances.numAttributes() - 1);

            SvmModel svmModel = new SvmModel();
            double rmse = svmModel.svm(dataInstances, new PolyKernel(), TEST_COMPLEXITY_CONSTANT, TEST_EPSILON_ROUND_OFF_ERROR);
            assertEquals(rmse, IRIS_POLY_KERNEL_C1_P_DEFAULT_RMSE);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testSvmNullInstances() throws Exception {
        SvmModel svmModel = new SvmModel();
        double rmse = svmModel.svm(null, new PolyKernel(), TEST_COMPLEXITY_CONSTANT, TEST_EPSILON_ROUND_OFF_ERROR);
        assertEquals(rmse, (double) Model.ERROR_DURING_CLASSIFICATION);
    }

    @Test
    public void testSvmNullKernel() throws Exception {
        File dataFile = new File("other/iris.arff");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new FileReader(dataFile));
            Instances dataInstances = new Instances(reader);
            reader.close();
            // setting class attribute
            dataInstances.setClassIndex(dataInstances.numAttributes() - 1);

            SvmModel svmModel = new SvmModel();
            double rmse = svmModel.svm(dataInstances, null, TEST_COMPLEXITY_CONSTANT, TEST_EPSILON_ROUND_OFF_ERROR);
            assertEquals(rmse, (double) Model.ERROR_DURING_CLASSIFICATION);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }


    @Test
    public void testSvmSearchSuccess() throws Exception {
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

            SvmModel svmModel = new SvmModel();
            Set<SearchResult> searchResults = svmModel.svmSearch(dataset, dataInstances);
            assertEquals(searchResults.size(), 11880);

            for (SearchResult result : searchResults) {
                assertEquals(result.getDataset(), dataset);
                assertEquals(result.getClass(), SvmSearchResult.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testSvmSearchNull() throws Exception {
        SvmModel svmModel = new SvmModel();
        Set<SearchResult> searchResults = svmModel.svmSearch(null, null);
        assertEquals(searchResults.size(), 0);
    }
}