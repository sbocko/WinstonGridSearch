package sk.upjs.winston.gridsearch.model;

/**
 * Created by stefan on 10/15/14.
 */
public class SimilarityGridSearch {
    private Long id;
    private double knnWeight;
    private double decTreeWeight;
    private double logRegWeight;
    private int betterResults;

    public SimilarityGridSearch() {
    }

    public SimilarityGridSearch(double knnWeight, double decTreeWeight, double logRegWeight, int betterResults) {
        this.knnWeight = knnWeight;
        this.decTreeWeight = decTreeWeight;
        this.logRegWeight = logRegWeight;
        this.betterResults = betterResults;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getKnnWeight() {
        return knnWeight;
    }

    public void setKnnWeight(double knnWeight) {
        this.knnWeight = knnWeight;
    }

    public double getDecTreeWeight() {
        return decTreeWeight;
    }

    public void setDecTreeWeight(double decTreeWeight) {
        this.decTreeWeight = decTreeWeight;
    }

    public double getLogRegWeight() {
        return logRegWeight;
    }

    public void setLogRegWeight(double logRegWeight) {
        this.logRegWeight = logRegWeight;
    }

    public int getBetterResults() {
        return betterResults;
    }

    public void setBetterResults(int betterResults) {
        this.betterResults = betterResults;
    }
}
