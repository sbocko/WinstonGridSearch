package sk.upjs.winston.gridsearch.model;

/**
 * Hibernate ORM class for metadata table.
 * Created by stefan on 11/02/14.
 */
public class Metadata {
    private Long id;
    private String filename;
    private int instances;
    private int attributes;
    private String dataType;
    private int missingValues;

    public Metadata() {
    }

    public Metadata(String filename, int instances, int attributes, String dataType, int missingValues) {
        this.filename = filename;
        this.instances = instances;
        this.attributes = attributes;
        this.dataType = dataType;
        this.missingValues = missingValues;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getInstances() {
        return instances;
    }

    public void setInstances(int instances) {
        this.instances = instances;
    }

    public int getAttributes() {
        return attributes;
    }

    public void setAttributes(int attributes) {
        this.attributes = attributes;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getMissingValues() {
        return missingValues;
    }

    public void setMissingValues(int missingValues) {
        this.missingValues = missingValues;
    }
}
