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

    public SearchResult similaritySearchForDatasetWithoutSVM(Dataset dataset, SearchResult template) {
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

    public SearchResult similaritySearchForDataset(Dataset dataset, SearchResult template) {
        SearchResult result = null;
        if (template instanceof KnnSearchResult) {
            result = (KnnSearchResult) session.createQuery("FROM KnnSearchResult Where dataset_id=" +
                    dataset.getId() + " and k=" + ((KnnSearchResult) template).getK()).setMaxResults(1).uniqueResult();
        }
        if (template instanceof LogisticRegressionSearchResult) {
            result = (LogisticRegressionSearchResult) session.createQuery("FROM LogisticRegressionSearchResult Where dataset_id=" +
                    dataset.getId() + " and ridge=" + ((LogisticRegressionSearchResult) template).getRidge()).setMaxResults(1).uniqueResult();
        }
        if (template instanceof DecisionTreeSearchResult) {
            result = (DecisionTreeSearchResult) session.createQuery("FROM DecisionTreeSearchResult Where dataset_id=" +
                    dataset.getId() + " and confidence_factor=" + ((DecisionTreeSearchResult) template).getConfidenceFactor() +
                    " and min_number_of_instances_per_leaf=" + ((DecisionTreeSearchResult) template).getMinimumNumberOfInstancesPerLeaf() +
                    " and unpruned=" + ((DecisionTreeSearchResult) template).isUnpruned()).setMaxResults(1).uniqueResult();
        }
        if (template instanceof SvmSearchResult) {
            result = (SvmSearchResult) session.createQuery("FROM SvmSearchResult Where dataset_id=" +
                    dataset.getId() + " and kernel='" + ((SvmSearchResult) template).getKernel() +
                    "' and complexityConstant=" + ((SvmSearchResult) template).getComplexityConstant() +
                    " and epsilonRoundOffError=" + ((SvmSearchResult) template).getEpsilonRoundOffError()).setMaxResults(1).uniqueResult();
        }

        return result;
    }

    /**
     * Computes the dissmilarity of two datasets based on default knn, decision tree and logistic regression hyperparameters rmse.
     *
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

    public double datasetDissimilarityFromSearchResultsRmse(double rmse1, double rmse2){
        return Math.abs(rmse1-rmse2);
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

    public SearchResult bestSearchResultForDataset(Dataset dataset) {
        SearchResult knnSearchResult = (KnnSearchResult) session.createQuery("FROM KnnSearchResult result Where result.dataset.id="+dataset.getId()+" order by result.rmse").setMaxResults(1).uniqueResult();
        SearchResult logisticRegressionSearchResult = (LogisticRegressionSearchResult) session.createQuery("FROM LogisticRegressionSearchResult result Where result.dataset.id="+dataset.getId()+" order by result.rmse").setMaxResults(1).uniqueResult();
        SearchResult decisionTreeSearchResult = (DecisionTreeSearchResult) session.createQuery("FROM DecisionTreeSearchResult result Where result.dataset.id="+dataset.getId()+" order by result.rmse").setMaxResults(1).uniqueResult();
        SearchResult svmSearchResult = (SvmSearchResult) session.createQuery("FROM SvmSearchResult result Where result.dataset.id="+dataset.getId()+" order by result.rmse").setMaxResults(1).uniqueResult();

        if (knnSearchResult != null &&
                (logisticRegressionSearchResult == null || knnSearchResult.getRmse() <= logisticRegressionSearchResult.getRmse()) &&
                (decisionTreeSearchResult == null || knnSearchResult.getRmse() <= decisionTreeSearchResult.getRmse()) &&
                (svmSearchResult == null || knnSearchResult.getRmse() <= svmSearchResult.getRmse())) {
            return knnSearchResult;
        } else if (decisionTreeSearchResult != null &&
                (knnSearchResult == null || decisionTreeSearchResult.getRmse() <= knnSearchResult.getRmse()) &&
                (logisticRegressionSearchResult == null || decisionTreeSearchResult.getRmse() <= logisticRegressionSearchResult.getRmse()) &&
                (svmSearchResult == null || decisionTreeSearchResult.getRmse() <= svmSearchResult.getRmse())) {
            return decisionTreeSearchResult;
        } else if (logisticRegressionSearchResult != null &&
                (knnSearchResult == null || logisticRegressionSearchResult.getRmse() <= knnSearchResult.getRmse()) &&
                (decisionTreeSearchResult == null || logisticRegressionSearchResult.getRmse() <= decisionTreeSearchResult.getRmse()) &&
                (svmSearchResult == null || logisticRegressionSearchResult.getRmse() <= svmSearchResult.getRmse())) {
            return decisionTreeSearchResult;
        }
        return svmSearchResult;
    }

    public SearchResult worstSearchResultForDataset(Dataset dataset) {
        SearchResult knnSearchResult = (KnnSearchResult) session.createQuery("FROM KnnSearchResult result Where result.dataset.id="+dataset.getId()+" order by result.rmse desc").setMaxResults(1).uniqueResult();
        SearchResult logisticRegressionSearchResult = (LogisticRegressionSearchResult) session.createQuery("FROM LogisticRegressionSearchResult result Where result.dataset.id="+dataset.getId()+" order by result.rmse desc").setMaxResults(1).uniqueResult();
        SearchResult decisionTreeSearchResult = (DecisionTreeSearchResult) session.createQuery("FROM DecisionTreeSearchResult result Where result.dataset.id="+dataset.getId()+" order by result.rmse desc").setMaxResults(1).uniqueResult();
        SearchResult svmSearchResult = (SvmSearchResult) session.createQuery("FROM SvmSearchResult result Where result.dataset.id="+dataset.getId()+" order by result.rmse desc").setMaxResults(1).uniqueResult();

        if (knnSearchResult != null &&
                (logisticRegressionSearchResult == null || knnSearchResult.getRmse() <= logisticRegressionSearchResult.getRmse()) &&
                (decisionTreeSearchResult == null || knnSearchResult.getRmse() <= decisionTreeSearchResult.getRmse()) &&
                (svmSearchResult == null || knnSearchResult.getRmse() <= svmSearchResult.getRmse())) {
            return knnSearchResult;
        } else if (decisionTreeSearchResult != null &&
                (knnSearchResult == null || decisionTreeSearchResult.getRmse() <= knnSearchResult.getRmse()) &&
                (logisticRegressionSearchResult == null || decisionTreeSearchResult.getRmse() <= logisticRegressionSearchResult.getRmse()) &&
                (svmSearchResult == null || decisionTreeSearchResult.getRmse() <= svmSearchResult.getRmse())) {
            return decisionTreeSearchResult;
        } else if (logisticRegressionSearchResult != null &&
                (knnSearchResult == null || logisticRegressionSearchResult.getRmse() <= knnSearchResult.getRmse()) &&
                (decisionTreeSearchResult == null || logisticRegressionSearchResult.getRmse() <= decisionTreeSearchResult.getRmse()) &&
                (svmSearchResult == null || logisticRegressionSearchResult.getRmse() <= svmSearchResult.getRmse())) {
            return decisionTreeSearchResult;
        }
        return svmSearchResult;
    }

    public Dataset getDatasetByName(String datasetName) {
        Dataset dataset = (Dataset) session.createQuery("FROM Dataset Where datasetName='" + datasetName + "'").uniqueResult();
        return dataset;
    }

    public List<Dataset> getApplicableDatasetsForBestResultRandomSimilaritySearch() {
        List<Dataset> datasetList = (ArrayList<Dataset>) session.createQuery("FROM Dataset").list();
        return datasetList;
    }

    public List<Dataset> getApplicableDatasetsForDefaultGridSimilaritySearch() {
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
