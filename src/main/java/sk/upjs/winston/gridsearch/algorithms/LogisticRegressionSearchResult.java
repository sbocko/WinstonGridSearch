package sk.upjs.winston.gridsearch.algorithms;

/**
 * This class stores single result of logistic regression (Logistic) algorithm run for given dataset {datasetName}
 * and hyperparameter values for ridge and maximum number of iterations of logistic regression (Logistic) algorithm.
 * Created by stefan on 6/14/14.
 */
public class LogisticRegressionSearchResult extends SearchResult {
    public static final int ITERATE_UNTIL_CONVERGENCE = -1;
    private double ridge;
    private int maximumNumberOfIterations;

    public LogisticRegressionSearchResult(String datasetName, double rmse, double ridge, int maximumNumberOfIterations) {
        this.setDatasetName(datasetName);
        this.setRmse(rmse);
        this.ridge = ridge;
        this.maximumNumberOfIterations = maximumNumberOfIterations;
    }

    public static int getIterateUntilConvergence() {
        return ITERATE_UNTIL_CONVERGENCE;
    }

    public double getRidge() {
        return ridge;
    }

    public void setRidge(double ridge) {
        this.ridge = ridge;
    }

    public int getMaximumNumberOfIterations() {
        return maximumNumberOfIterations;
    }

    public void setMaximumNumberOfIterations(int maximumNumberOfIterations) {
        this.maximumNumberOfIterations = maximumNumberOfIterations;
    }
}
