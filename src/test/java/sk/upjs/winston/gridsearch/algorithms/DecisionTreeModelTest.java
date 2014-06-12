package sk.upjs.winston.gridsearch.algorithms;

import junit.framework.TestCase;
import org.junit.Test;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Set;

public class DecisionTreeModelTest extends TestCase {

    private static final double J48_ALGORITHM_M1_C025_UNPRUNED_RMSE = 0.18802107625446746;

    @Test
    public void testJ48DecisionTreeAnalysisSuccess() throws Exception {
        File dataFile = new File("other/iris.arff");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new FileReader(dataFile));
            Instances dataInstances = new Instances(reader);
            reader.close();
            // setting class attribute
            dataInstances.setClassIndex(dataInstances.numAttributes() - 1);

            DecisionTreeModel decisionTreeModel = new DecisionTreeModel();
            double rmse = decisionTreeModel.j48DecisionTreeAnalysis(dataInstances, 1, (float) 0.25, false);
            assertEquals(rmse, J48_ALGORITHM_M1_C025_UNPRUNED_RMSE);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testJ48DecisionTreeAnalysisNullInstances() throws Exception {
        DecisionTreeModel decisionTreeModel = new DecisionTreeModel();
        double rmse = decisionTreeModel.j48DecisionTreeAnalysis(null, 1, (float) 0.25, false);
        assertEquals(rmse, (double) Model.ERROR_DURING_CLASSIFICATION);
    }

    @Test
    public void testJ48DecisionTreeAnalysisNegativeCParameter() throws Exception {
        File dataFile = new File("other/iris.arff");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new FileReader(dataFile));
            Instances dataInstances = new Instances(reader);
            reader.close();
            // setting class attribute
            dataInstances.setClassIndex(dataInstances.numAttributes() - 1);

            DecisionTreeModel decisionTreeModel = new DecisionTreeModel();
            double rmse = decisionTreeModel.j48DecisionTreeAnalysis(dataInstances, 1, (float) -0.25, false);
            assertEquals(rmse, (double) Model.ERROR_DURING_CLASSIFICATION);
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

            DecisionTreeModel decisionTreeModel = new DecisionTreeModel();
            Set<SearchResult> searchResults = decisionTreeModel.j48Search(dataInstances);
            assertNotSame(searchResults.size(), 0);

            for (SearchResult result : searchResults) {
                assertEquals(result.getDatasetName(), dataInstances.relationName());
                assertEquals(result.getClass(), DecisionTreeSearchResult.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testJ48SearchNull() throws Exception {
        DecisionTreeModel decisionTreeModel = new DecisionTreeModel();
        Set<SearchResult> searchResults = decisionTreeModel.j48Search(null);
        assertEquals(searchResults.size(), 0);
    }
}