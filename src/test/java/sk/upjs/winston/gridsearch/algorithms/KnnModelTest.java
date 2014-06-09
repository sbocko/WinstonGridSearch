package sk.upjs.winston.gridsearch.algorithms;

import junit.framework.TestCase;
import org.junit.Test;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Set;

public class KnnModelTest extends TestCase {

    private static final double IRIS_3KNN_RMSE = 0.17026766863246218;

    @Test
    public void testKnnSuccess() throws Exception {
        File dataFile = new File("other/iris.arff");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new FileReader(dataFile));
            Instances dataInstances = new Instances(reader);
            reader.close();
            // setting class attribute
            dataInstances.setClassIndex(dataInstances.numAttributes() - 1);

            KnnModel knnModel = new KnnModel();
            double rmse = knnModel.knn(dataInstances, 3);
            assertEquals(rmse, IRIS_3KNN_RMSE);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testKnnNullInstances() throws Exception {
        KnnModel knnModel = new KnnModel();
        double rmse = knnModel.knn(null, 3);
        assertEquals(rmse, (double) KnnModel.ERROR_DURING_CLASSIFICATION);
    }

    @Test
    public void testKnnNegativeKParameter() throws Exception {
        File dataFile = new File("other/iris.arff");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new FileReader(dataFile));
            Instances dataInstances = new Instances(reader);
            reader.close();
            // setting class attribute
            dataInstances.setClassIndex(dataInstances.numAttributes() - 1);

            KnnModel knnModel = new KnnModel();
            double rmse = knnModel.knn(dataInstances, -5);
            assertEquals(rmse, (double) KnnModel.ERROR_DURING_CLASSIFICATION);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testKnnSearchSuccess() throws Exception {
        File dataFile = new File("other/iris.arff");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new FileReader(dataFile));
            Instances dataInstances = new Instances(reader);
            reader.close();
            // setting class attribute
            dataInstances.setClassIndex(dataInstances.numAttributes() - 1);

            KnnModel knnModel = new KnnModel();
            Set<SearchResult> searchResults = knnModel.knnSearch(dataInstances);
            assertEquals(searchResults.size(), 100);

            for (SearchResult result : searchResults) {
                assertEquals(result.getDatasetName(), dataInstances.relationName());
                assertEquals(result.getClass(), KnnSearchResult.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testKnnSearch() throws Exception {
        KnnModel knnModel = new KnnModel();
        Set<SearchResult> searchResults = knnModel.knnSearch(null);
        assertEquals(searchResults.size(), 0);
    }
}