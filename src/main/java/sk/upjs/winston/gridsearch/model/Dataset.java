package sk.upjs.winston.gridsearch.model;

/**
 * Hibernate ORM class for dataset table. Instances of this class are attributes
 * of search results (knn, logistic regression, decision tree, svm).
 * Created by stefan on 6/29/14.
 */
public class Dataset {
    private Long id;
    private String datasetName;

    public Dataset() {
    }

    public Dataset(String datasetName) {
        this.datasetName = datasetName;
    }

    public Dataset(Long id, String datasetName) {
        this.id = id;
        this.datasetName = datasetName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    @Override
    public String toString() {
        return "Dataset{" +
                "id=" + id +
                ", datasetName='" + datasetName + '\'' +
                '}';
    }
}
