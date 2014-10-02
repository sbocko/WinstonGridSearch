package sk.upjs.winston.gridsearch.model;


public class ComputationTimeForResult {
    private Long id;
    private Dataset dataset;
    private String method;
    private int computationLength;

    public ComputationTimeForResult() {
    }

    public ComputationTimeForResult(Dataset dataset, String method, int computationLength) {
        this.dataset = dataset;
        this.method = method;
        this.computationLength = computationLength;
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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getComputationLength() {
        return computationLength;
    }

    public void setComputationLength(int computationLength) {
        this.computationLength = computationLength;
    }
}
