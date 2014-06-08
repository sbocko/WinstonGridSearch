package sk.upjs.winston.gridsearch.algorithms;

/**
 * This class is supposed to be subclassed to represent the results of modelling.
 * For given models a subclass of this class should own its hyperparameter values
 * and root mean squared error {rmse} for given {datasetName} using these hyperparameters.
 * Created by stefan on 6/8/14.
 */
public abstract class SearchResult {
    private String datasetName;
//    root mean squared error
    private double rmse;


    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public double getRmse() {
        return rmse;
    }

    public void setRmse(double rmse) {
        this.rmse = rmse;
    }
}
