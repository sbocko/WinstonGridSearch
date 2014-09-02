package sk.upjs.winston.gridsearch.exec;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import sk.upjs.winston.gridsearch.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stefan on 9/2/14.
 */
public class SimilarityDefaultGridSearch {
    private static final int DEFAULT_KNN_PARAMETER_K = 3;
    private static final double DEFAULT_DECISION_TREE_PARAMETER_PRUNING = 0.25;
    private static final int DEFAULT_DECISION_TREE_PARAMETER_MIN_NUMBER_OF_INSTANCES = 2;
    private static final boolean DEFAULT_DECISION_TREE_PARAMETER_UNPRUNED = false;
    private static final double DEFAULT_LOGISTIC_REGRESSION_PARAMETER_RIDGE = 0.05;
    private static SessionFactory factory;

    public static void main(String[] args) {
        // create session factory
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        // create new db session
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            List<Dataset> datasets = getApplicableDatasets(session);
            for (int i = 0; i < datasets.size(); i++) {
                Dataset targetDataset = datasets.get(i);
                double minDissimilarity = Double.MAX_VALUE;
                Dataset mostSimilarDataset = null;

                //find most similar dataset
                for (int j = 0; j < datasets.size(); j++) {
                    if (i != j) {
                        Dataset currentDataset = datasets.get(j);
                        double currentDissimilarity = datasetDissimilarity(targetDataset, currentDataset, session);
                        if (currentDissimilarity < minDissimilarity) {
                            minDissimilarity = currentDissimilarity;
                            mostSimilarDataset = currentDataset;
                        }
                    }
                }

                //get best hyperparameter values
                SearchResult bestSearch = similaritySearchForDataset(targetDataset, bestSearchResultForDataset(mostSimilarDataset, session), session);
                System.out.println("Dataset: " + targetDataset.getDatasetName() + " , similar to: " + mostSimilarDataset.getDatasetName() +
                        " with dissimilarity of: " + minDissimilarity + " best rmse: " + bestSearchResultForDataset(targetDataset, session).getRmse() +
                        " and similarity search rmse of: " + bestSearch.getRmse() + " pos: " +
                        betterSearchResultsForDataset(targetDataset, bestSearch.getRmse(), session) + "/" +
                        totalSearchResultsForDataset(targetDataset, session));
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return;
        } finally {
            session.close();
        }
        System.out.println("Done.");
    }

    private static SearchResult similaritySearchForDataset(Dataset dataset, SearchResult template, Session session) {
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

    private static double datasetDissimilarity(Dataset dataset1, Dataset dataset2, Session session) {
        double dissimilarity = 0;
        dissimilarity += Math.abs(defaultKnnSearchResultForDataset(dataset1, session).getRmse() -
                defaultKnnSearchResultForDataset(dataset2, session).getRmse());
        dissimilarity += Math.abs(defaultDecisionTreeSearchResultForDataset(dataset1, session).getRmse() -
                defaultDecisionTreeSearchResultForDataset(dataset2, session).getRmse());
        dissimilarity += Math.abs(defaultLogisticRegressionSearchResultForDataset(dataset1, session).getRmse() -
                defaultLogisticRegressionSearchResultForDataset(dataset2, session).getRmse());

        return dissimilarity;
    }

    private static SearchResult defaultKnnSearchResultForDataset(Dataset dataset, Session session) {
        SearchResult result = (KnnSearchResult) session.createQuery("FROM KnnSearchResult Where dataset_id=" +
                dataset.getId() + " and k=" + DEFAULT_KNN_PARAMETER_K).uniqueResult();
        return result;
    }

    private static SearchResult defaultLogisticRegressionSearchResultForDataset(Dataset dataset, Session session) {
        SearchResult result = (LogisticRegressionSearchResult) session.createQuery("FROM LogisticRegressionSearchResult Where dataset_id=" +
                dataset.getId() + " and ridge=" + DEFAULT_LOGISTIC_REGRESSION_PARAMETER_RIDGE).uniqueResult();
        return result;
    }

    private static SearchResult defaultDecisionTreeSearchResultForDataset(Dataset dataset, Session session) {
        SearchResult result = (DecisionTreeSearchResult) session.createQuery("FROM DecisionTreeSearchResult Where dataset_id=" +
                dataset.getId() + " and confidence_factor=" + DEFAULT_DECISION_TREE_PARAMETER_PRUNING +
                " and min_number_of_instances_per_leaf=" + DEFAULT_DECISION_TREE_PARAMETER_MIN_NUMBER_OF_INSTANCES +
                " and unpruned=" + DEFAULT_DECISION_TREE_PARAMETER_UNPRUNED).uniqueResult();
        return result;
    }

    private static SearchResult bestSearchResultForDataset(Dataset dataset, Session session) {
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

    private static List<Dataset> getApplicableDatasets(Session session) {
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

    private static long totalSearchResultsForDataset(Dataset dataset, Session session) {
        long result = 0;
        result += (Long) session.createQuery("SELECT count(*) FROM KnnSearchResult Where dataset_id=" + dataset.getId()).iterate().next();
        result += (Long) session.createQuery("SELECT count(*) FROM LogisticRegressionSearchResult Where dataset_id=" + dataset.getId()).iterate().next();
        result += (Long) session.createQuery("SELECT count(*) FROM DecisionTreeSearchResult Where dataset_id=" + dataset.getId()).iterate().next();
        return result;
    }

    private static long betterSearchResultsForDataset(Dataset dataset, double rmseLimit, Session session) {
        long result = 0;
        result += (Long) session.createQuery("SELECT count(*) FROM KnnSearchResult Where dataset_id=" +
                dataset.getId() + " and rmse<" + rmseLimit).iterate().next();
        result += (Long) session.createQuery("SELECT count(*) FROM LogisticRegressionSearchResult Where dataset_id=" +
                dataset.getId() + " and rmse<" + rmseLimit).iterate().next();
        result += (Long) session.createQuery("SELECT count(*) FROM DecisionTreeSearchResult Where dataset_id=" +
                dataset.getId() + " and rmse<" + rmseLimit).iterate().next();
        return result;
    }
}
