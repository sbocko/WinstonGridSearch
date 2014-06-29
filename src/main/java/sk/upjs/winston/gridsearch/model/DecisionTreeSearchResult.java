package sk.upjs.winston.gridsearch.model;

/**
 * This class stores single result of decision tree (J48) algorithm run for given dataset
 * and hyperparameter values for confidence factor, minimum number of instances per leaf
 * and whether the tree should be unpruned or not of decision tree (J48) algorithm.
 * Created by stefan on 6/10/14.
 */
public class DecisionTreeSearchResult extends SearchResult {
    private double confidenceFactor;
    private int minimumNumberOfInstancesPerLeaf;
    private boolean unpruned;

    public DecisionTreeSearchResult(Dataset dataset, double rmse, double confidenceFactor, int minimumNumberOfInstancesPerLeaf, boolean unpruned) {
        this.setDataset(dataset);
        this.setRmse(rmse);
        this.confidenceFactor = confidenceFactor;
        this.minimumNumberOfInstancesPerLeaf = minimumNumberOfInstancesPerLeaf;
        this.unpruned = unpruned;
    }

    public boolean isUnpruned() {
        return unpruned;
    }

    public void setUnpruned(boolean unpruned) {
        this.unpruned = unpruned;
    }

    public double getConfidenceFactor() {
        return confidenceFactor;
    }

    public void setConfidenceFactor(double confidenceFactor) {
        this.confidenceFactor = confidenceFactor;
    }

    public int getMinimumNumberOfInstancesPerLeaf() {
        return minimumNumberOfInstancesPerLeaf;
    }

    public void setMinimumNumberOfInstancesPerLeaf(int minimumNumberOfInstancesPerLeaf) {
        this.minimumNumberOfInstancesPerLeaf = minimumNumberOfInstancesPerLeaf;
    }
}
