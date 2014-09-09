package sk.upjs.winston.gridsearch.model;

/**
 * Created by stefan on 9/7/14.
 */
public class SimilarityBestSearchResult extends SearchResult {
    private Dataset similarDataset;
    private double similarityError;

    public SimilarityBestSearchResult(){
        super();
    }

    public SimilarityBestSearchResult(Dataset dataset, double rmse, Dataset similarDataset, double similarityError) {
        super(dataset, rmse);
        this.similarDataset = similarDataset;
        this.similarityError = similarityError;
    }

    public Dataset getSimilarDataset() {
        return similarDataset;
    }

    public void setSimilarDataset(Dataset similarDataset) {
        this.similarDataset = similarDataset;
    }

    public double getSimilarityError() {
        return similarityError;
    }

    public void setSimilarityError(double similarityError) {
        this.similarityError = similarityError;
    }
}
