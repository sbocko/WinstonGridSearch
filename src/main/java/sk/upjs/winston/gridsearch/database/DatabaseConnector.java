package sk.upjs.winston.gridsearch.database;

import org.hibernate.Session;
import sk.upjs.winston.gridsearch.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for simplifying work with a database.
 */
public class DatabaseConnector {
    private static final int DEFAULT_KNN_PARAMETER_K = 3;
    private static final double DEFAULT_DECISION_TREE_PARAMETER_PRUNING = 0.25;
    private static final int DEFAULT_DECISION_TREE_PARAMETER_MIN_NUMBER_OF_INSTANCES = 2;
    private static final boolean DEFAULT_DECISION_TREE_PARAMETER_UNPRUNED = false;
    private static final double DEFAULT_LOGISTIC_REGRESSION_PARAMETER_RIDGE = 0.05;

    private Session session;

    public DatabaseConnector(Session session) {
        this.session = session;
    }

    public SearchResult similaritySearchForDataset(Dataset dataset, SearchResult template) {
        SearchResult result = null;
        if (template instanceof KnnSearchResult) {
            result = (KnnSearchResult) session.createQuery("FROM KnnSearchResult Where dataset_id=" +
                    dataset.getId() + " and k=" + ((KnnSearchResult) template).getK()).uniqueResult();
        }
        if (template instanceof LogisticRegressionSearchResult) {
            result = (LogisticRegressionSearchResult) session.createQuery("FROM LogisticRegressionSearchResult Where dataset_id=" +
                    dataset.getId() + " and ridge=" + ((LogisticRegressionSearchResult) template).getRidge()).uniqueResult();
        }
        if (template instanceof DecisionTreeSearchResult) {
            result = (DecisionTreeSearchResult) session.createQuery("FROM DecisionTreeSearchResult Where dataset_id=" +
                    dataset.getId() + " and confidence_factor=" + ((DecisionTreeSearchResult) template).getConfidenceFactor() +
                    " and min_number_of_instances_per_leaf=" + ((DecisionTreeSearchResult) template).getMinimumNumberOfInstancesPerLeaf() +
                    " and unpruned=" + ((DecisionTreeSearchResult) template).isUnpruned()).uniqueResult();
        }

        return result;
    }

    /**
     * Computes the dissmilarity of two datasets based on default knn, decision tree and logistic regression hyperparameters rmse.
     * @param dataset1 first dataset
     * @param dataset2 second dataset
     * @return
     */
    public double datasetDissimilarityFromDefaultHyperparametersWithoutSVM(Dataset dataset1, Dataset dataset2) {
        double dissimilarity = 0;
        dissimilarity += Math.abs(defaultKnnSearchResultForDataset(dataset1).getRmse() -
                defaultKnnSearchResultForDataset(dataset2).getRmse());
        dissimilarity += Math.abs(defaultDecisionTreeSearchResultForDataset(dataset1).getRmse() -
                defaultDecisionTreeSearchResultForDataset(dataset2).getRmse());
        dissimilarity += Math.abs(defaultLogisticRegressionSearchResultForDataset(dataset1).getRmse() -
                defaultLogisticRegressionSearchResultForDataset(dataset2).getRmse());

        return dissimilarity;
    }

    public SearchResult defaultKnnSearchResultForDataset(Dataset dataset) {
        SearchResult result = (KnnSearchResult) session.createQuery("FROM KnnSearchResult Where dataset_id=" +
                dataset.getId() + " and k=" + DEFAULT_KNN_PARAMETER_K).uniqueResult();
        return result;
    }

    public SearchResult defaultLogisticRegressionSearchResultForDataset(Dataset dataset) {
        SearchResult result = (LogisticRegressionSearchResult) session.createQuery("FROM LogisticRegressionSearchResult Where dataset_id=" +
                dataset.getId() + " and ridge=" + DEFAULT_LOGISTIC_REGRESSION_PARAMETER_RIDGE).uniqueResult();
        return result;
    }

    public SearchResult defaultDecisionTreeSearchResultForDataset(Dataset dataset) {
        SearchResult result = (DecisionTreeSearchResult) session.createQuery("FROM DecisionTreeSearchResult Where dataset_id=" +
                dataset.getId() + " and confidence_factor=" + DEFAULT_DECISION_TREE_PARAMETER_PRUNING +
                " and min_number_of_instances_per_leaf=" + DEFAULT_DECISION_TREE_PARAMETER_MIN_NUMBER_OF_INSTANCES +
                " and unpruned=" + DEFAULT_DECISION_TREE_PARAMETER_UNPRUNED).uniqueResult();
        return result;
    }

    public SearchResult bestSearchResultForDatasetWithoutSVM(Dataset dataset) {
        SearchResult knnSearchResult = (KnnSearchResult) session.createQuery("FROM KnnSearchResult Where rmse in (Select min(knn.rmse) FROM KnnSearchResult knn)").list().get(0);
        SearchResult logisticRegressionSearchResult = (LogisticRegressionSearchResult) session.createQuery("FROM LogisticRegressionSearchResult Where rmse in (Select min(logReg.rmse) FROM LogisticRegressionSearchResult logReg)").list().get(0);
        SearchResult decisionTreeSearchResult = (DecisionTreeSearchResult) session.createQuery("FROM DecisionTreeSearchResult Where rmse in (Select min(decTree.rmse) FROM DecisionTreeSearchResult decTree)").list().get(0);

//        if (knnSearchResult.getRmse() <= logisticRegressionSearchResult.getRmse()) {
//            return knnSearchResult;
//        }
//        if (knnSearchResult.getRmse() > logisticRegressionSearchResult.getRmse()) {
//            return logisticRegressionSearchResult;
//        }


        if (knnSearchResult.getRmse() <= logisticRegressionSearchResult.getRmse() &&
                knnSearchResult.getRmse() <= decisionTreeSearchResult.getRmse()) {
            return knnSearchResult;
        } else if (decisionTreeSearchResult.getRmse() <= knnSearchResult.getRmse() &&
                decisionTreeSearchResult.getRmse() <= logisticRegressionSearchResult.getRmse()) {
            return decisionTreeSearchResult;
        }
        return logisticRegressionSearchResult;
    }

    public List<Dataset> getApplicableDatasets() {
        List<Dataset> knnDefaultList = (ArrayList<Dataset>) session.createQuery("SELECT dataset FROM KnnSearchResult Where k=" + DEFAULT_KNN_PARAMETER_K).list();

        List<Dataset> decisionTreeDefaultList = (ArrayList<Dataset>) session.createQuery("SELECT dataset FROM DecisionTreeSearchResult " +
                "Where confidence_factor=" + DEFAULT_DECISION_TREE_PARAMETER_PRUNING + " and min_number_of_instances_per_leaf=" +
                DEFAULT_DECISION_TREE_PARAMETER_MIN_NUMBER_OF_INSTANCES + " and unpruned=" + DEFAULT_DECISION_TREE_PARAMETER_UNPRUNED).list();

        List<Dataset> logisticRegressionDefaultList = (ArrayList<Dataset>) session.createQuery("SELECT dataset FROM LogisticRegressionSearchResult Where ridge="
                + DEFAULT_LOGISTIC_REGRESSION_PARAMETER_RIDGE).list();

        knnDefaultList.retainAll(decisionTreeDefaultList);
        knnDefaultList.retainAll(logisticRegressionDefaultList);

        return knnDefaultList;
    }

    public int totalNumberOfSearchResultsForDataset(Dataset dataset) {
        int result = 0;
        result += ((Long) session.createQuery("SELECT count(*) FROM KnnSearchResult Where dataset_id=" +
                dataset.getId()).iterate().next()).intValue();
        result += ((Long) session.createQuery("SELECT count(*) FROM LogisticRegressionSearchResult Where dataset_id=" +
                dataset.getId()).iterate().next()).intValue();
        result += ((Long) session.createQuery("SELECT count(*) FROM DecisionTreeSearchResult Where dataset_id=" +
                dataset.getId()).iterate().next()).intValue();
        result += ((Long) session.createQuery("SELECT count(*) FROM SvmSearchResult Where dataset_id=" +
                dataset.getId()).iterate().next()).intValue();
        return result;
    }

    public int numberOfBetterSearchResultsForDataset(Dataset dataset, double rmseLimit) {
        int result = 0;
        result += ((Long) session.createQuery("SELECT count(*) FROM KnnSearchResult Where dataset_id=" +
                dataset.getId() + " and rmse<" + rmseLimit).iterate().next()).intValue();
        result += ((Long) session.createQuery("SELECT count(*) FROM LogisticRegressionSearchResult Where dataset_id=" +
                dataset.getId() + " and rmse<" + rmseLimit).iterate().next()).intValue();
        result += ((Long) session.createQuery("SELECT count(*) FROM DecisionTreeSearchResult Where dataset_id=" +
                dataset.getId() + " and rmse<" + rmseLimit).iterate().next()).intValue();
        result += ((Long) session.createQuery("SELECT count(*) FROM SvmSearchResult Where dataset_id=" +
                dataset.getId() + " and rmse<" + rmseLimit).iterate().next()).intValue();
        return result;
    }

    public int totalNumberOfSearchResultsForDatasetWithoutSVM(Dataset dataset) {
        int result = 0;
        result += ((Long) session.createQuery("SELECT count(*) FROM KnnSearchResult Where dataset_id=" +
                dataset.getId()).iterate().next()).intValue();
        result += ((Long) session.createQuery("SELECT count(*) FROM LogisticRegressionSearchResult Where dataset_id=" +
                dataset.getId()).iterate().next()).intValue();
        result += ((Long) session.createQuery("SELECT count(*) FROM DecisionTreeSearchResult Where dataset_id=" +
                dataset.getId()).iterate().next()).intValue();
        return result;
    }

    public int numberOfBetterSearchResultsForDatasetWithoutSVM(Dataset dataset, double rmseLimit) {
        int result = 0;
        result += ((Long) session.createQuery("SELECT count(*) FROM KnnSearchResult Where dataset_id=" +
                dataset.getId() + " and rmse<" + rmseLimit).iterate().next()).intValue();
        result += ((Long) session.createQuery("SELECT count(*) FROM LogisticRegressionSearchResult Where dataset_id=" +
                dataset.getId() + " and rmse<" + rmseLimit).iterate().next()).intValue();
        result += ((Long) session.createQuery("SELECT count(*) FROM DecisionTreeSearchResult Where dataset_id=" +
                dataset.getId() + " and rmse<" + rmseLimit).iterate().next()).intValue();
        return result;
    }

}
