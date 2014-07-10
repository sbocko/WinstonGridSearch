package sk.upjs.winston.gridsearch.model;

/**
 * This class stores single result of knn algorithm run for given dataset
 * and hyperparameter value {k} of knn algorithm.
 * Created by stefan on 6/8/14.
 */
public class KnnSearchResult extends SearchResult {
    private int k;

    public KnnSearchResult() {
    }

    public KnnSearchResult(Dataset dataset, double rmse, int k) {
        this.setDataset(dataset);
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
