package sk.upjs.winston.gridsearch.algorithms;

import sk.upjs.winston.gridsearch.model.Dataset;
import sk.upjs.winston.gridsearch.model.LogisticRegressionSearchResult;
import sk.upjs.winston.gridsearch.model.Model;
import sk.upjs.winston.gridsearch.model.SearchResult;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.Logistic;
import weka.core.Instances;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Class for performing logistic regression analysis of datasets.
 * Created by stefan on 6/14/14.
 */
public class LogisticRegressionModel extends Model {

    public static final double RIDGE_STEP = 0.05;

    /*
             * Performes logistic regression algorithm and evaluates results 10 times with 10-fold cross validation method.
             * Returnes the mean squared error for given model.
             * @param dataInstances dataset instances
             * @param ridge ridge parameter for logistic regression algorithm
             * @param maximumNumberOfIterations maximum number of iterations parameter
             * for logistic regression algorithm, -1 for iteration until convergence
             * @return root mean squared error
             */
    public double logisticRegression(Instances dataInstances, double ridge, int maximumNumberOfIterations) {
        Logistic logistic = new Logistic();
        logistic.setRidge(ridge);
        logistic.setMaxIts(maximumNumberOfIterations);

        Evaluation evaluation = null;
        try {
            evaluation = new Evaluation(dataInstances);
            evaluation.crossValidateModel(logistic, dataInstances, 10, new Random(1));
        } catch (Exception e) {
//            e.printStackTrace();
            return ERROR_DURING_CLASSIFICATION;
        }
        return evaluation.rootMeanSquaredError();
    }

    /*
     * Performs logistic regression for r=0..1 with step {RIDGE_STEP} and returns RMSE for every value.
     * When something goes wrong during search, the result of this search is not included in result set.
     * @param dataset dataset details which belongs to returned search result
     * @param dataInstances dataset instances
     * @return Set of LogisticRegressionSearchResult instances
     */
    public Set<SearchResult> logisticRegressionSearch(Dataset dataset, Instances dataInstances) {
        Set<SearchResult> results = new HashSet<>();
        for (double r = 0; r <= 1; r += RIDGE_STEP) {
            double rmse = logisticRegression(dataInstances, r, LogisticRegressionSearchResult.ITERATE_UNTIL_CONVERGENCE);
            if (rmse != ERROR_DURING_CLASSIFICATION) {
                SearchResult res = new LogisticRegressionSearchResult(dataset, rmse, r, LogisticRegressionSearchResult.ITERATE_UNTIL_CONVERGENCE);
                results.add(res);
            }
        }
        return results;
    }

}
