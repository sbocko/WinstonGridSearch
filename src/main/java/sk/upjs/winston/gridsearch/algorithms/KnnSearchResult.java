package sk.upjs.winston.gridsearch.algorithms;

/**
 * This class stores single result of knn algorithm run for given dataset {datasetName}
 * and hyperparameter value {k} of knn algorithm.
 * Created by stefan on 6/8/14.
 */
public class KnnSearchResult extends SearchResult{
    private int k;

    public KnnSearchResult(String datasetName,double rmse, int k) {
        this.setDatasetName(datasetName);
        this.setRmse(rmse);
        this.k = k;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }
}
