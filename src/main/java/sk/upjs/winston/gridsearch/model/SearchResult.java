package sk.upjs.winston.gridsearch.model;

/**
 * This class is supposed to be subclassed to represent the results of modelling.
 * For given models a subclass of this class should own its hyperparameter values
 * and root mean squared error {rmse} for given dataset using these hyperparameters.
 * Created by stefan on 6/8/14.
 */
public abstract class SearchResult implements Comparable<SearchResult>{
    private Long id;
    private Dataset dataset;
//    root mean squared error
    private double rmse;

    protected SearchResult() {
    }

    protected SearchResult(Dataset dataset, double rmse) {
        this.dataset = dataset;
        this.rmse = rmse;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public double getRmse() {
        return rmse;
    }

    public void setRmse(double rmse) {
        this.rmse = rmse;
    }

    /*
     * Object with smaller root mean squared error is smaller/before one with bigger error.
     */
    @Override
    public int compareTo(SearchResult searchResult) {
        if(this.rmse > searchResult.rmse){
            return 1;
        }
        if(this.rmse < searchResult.rmse){
            return -1;
        }
        return 0;
    }
}
