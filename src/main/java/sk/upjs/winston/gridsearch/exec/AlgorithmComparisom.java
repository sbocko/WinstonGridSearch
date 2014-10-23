package sk.upjs.winston.gridsearch.exec;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import sk.upjs.winston.gridsearch.database.DatabaseConnector;
import sk.upjs.winston.gridsearch.model.Dataset;
import sk.upjs.winston.gridsearch.model.SearchResult;

import java.util.List;

/**
 * Created by stefan on 10/22/14.
 */
public class AlgorithmComparisom {
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
            DatabaseConnector databaseConnector = new DatabaseConnector(session);

            String method = "Dec Tree";
            System.out.format("\n%32s%40s%32s%32s%32s%32s%20s%20s\n\n", "Dataset", "Most similar dataset (combined)", "Most similar dataset (" + method + ")", "Best rmse", "rmse (combined)", "rmse ("+method+")", "Position (comb)", "Position ("+method+")");

            List<Dataset> datasets = databaseConnector.getApplicableDatasetsForDefaultGridSimilaritySearch();

            for (int i = 0; i < datasets.size(); i++) {
                Dataset targetDataset = datasets.get(i);
                double minDissimilarityComb = Double.MAX_VALUE;
                double minDissimilarityForMethod = Double.MAX_VALUE;
                Dataset mostSimilarDatasetComb = null;
                Dataset mostSimilarDatasetForMethod = null;

                //find most similar dataset
                for (int j = 0; j < datasets.size(); j++) {
                    if (i != j) {
                        Dataset currentDataset = datasets.get(j);
                        double currentDissimilarity = databaseConnector.datasetDissimilarityFromDefaultHyperparametersWithoutSVM(targetDataset, currentDataset);
                        if (currentDissimilarity < minDissimilarityComb) {
                            minDissimilarityComb = currentDissimilarity;
                            mostSimilarDatasetComb = currentDataset;
                        }

                        currentDissimilarity = databaseConnector.datasetDissimilarityFromDecisionTreeDefaultHyperparameters(targetDataset, currentDataset);
                        if (currentDissimilarity < minDissimilarityForMethod) {
                            minDissimilarityForMethod = currentDissimilarity;
                            mostSimilarDatasetForMethod = currentDataset;
                        }
                    }
                }

                //get best hyperparameter values
                SearchResult bestSearchComb = databaseConnector.similaritySearchForDataset(targetDataset, databaseConnector.bestDecisionTreeSearchResultForDataset(mostSimilarDatasetComb));
                SearchResult bestSearchForMethod = databaseConnector.similaritySearchForDataset(targetDataset, databaseConnector.bestKnnSearchResultForDataset(mostSimilarDatasetForMethod));
                String positionComb = databaseConnector.numberOfBetterDecisionTreeSearchResultsForDataset(targetDataset, bestSearchComb.getRmse()) + "/" +
                        databaseConnector.totalNumberOfDecisionTreeSearchResultsForDataset(targetDataset);
                String positionForMethod = databaseConnector.numberOfBetterDecisionTreeSearchResultsForDataset(targetDataset, bestSearchForMethod.getRmse()) + "/" +
                        databaseConnector.totalNumberOfDecisionTreeSearchResultsForDataset(targetDataset);

//                System.out.format("%32s%40s%32s%32.20f%32.20f%32.20f%20s%20s\n", targetDataset.getDatasetName(), mostSimilarDatasetComb.getDatasetName(), mostSimilarDatasetForMethod.getDatasetName(),
//                    databaseConnector.bestDecisionTreeSearchResultForDataset(targetDataset).getRmse(), bestSearchComb.getRmse(), bestSearchForMethod.getRmse(), positionComb, positionForMethod);
                System.out.println(mostSimilarDatasetForMethod.getDatasetName());
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return;
        } finally {
            session.close();
            session.getSessionFactory().close();
        }
        System.out.println("Done.");
    }
}
