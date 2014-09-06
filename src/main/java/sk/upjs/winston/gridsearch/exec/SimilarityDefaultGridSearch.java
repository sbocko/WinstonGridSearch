package sk.upjs.winston.gridsearch.exec;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import sk.upjs.winston.gridsearch.database.DatabaseConnector;
import sk.upjs.winston.gridsearch.model.*;

import java.util.List;

/**
 * Created by stefan on 9/2/14.
 */
public class SimilarityDefaultGridSearch {
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

            System.out.format("\n%32s%32s%32s%32s%32s%15s%15s\n\n", "Dataset", "Most similar dataset", "Dissimilarity", "Best rmse", "Similarity search rmse", "Position", "<= default");

            List<Dataset> datasets = databaseConnector.getApplicableDatasetsForDefaultGridSimilaritySearch();
            for (int i = 0; i < datasets.size(); i++) {
                Dataset targetDataset = datasets.get(i);
                double minDissimilarity = Double.MAX_VALUE;
                Dataset mostSimilarDataset = null;

                //find most similar dataset
                for (int j = 0; j < datasets.size(); j++) {
                    if (i != j) {
                        Dataset currentDataset = datasets.get(j);
                        double currentDissimilarity = databaseConnector.datasetDissimilarityFromDefaultHyperparametersWithoutSVM(targetDataset, currentDataset);
                        if (currentDissimilarity < minDissimilarity) {
                            minDissimilarity = currentDissimilarity;
                            mostSimilarDataset = currentDataset;
                        }
                    }
                }

                //get best hyperparameter values
                SearchResult bestSearch = databaseConnector.similaritySearchForDatasetWithoutSVM(targetDataset, databaseConnector.bestSearchResultForDatasetWithoutSVM(mostSimilarDataset));

                String position = databaseConnector.numberOfBetterSearchResultsForDatasetWithoutSVM(targetDataset, bestSearch.getRmse()) + "/" +
                        databaseConnector.totalNumberOfSearchResultsForDatasetWithoutSVM(targetDataset);
                double minDefaultRmse = Math.min(databaseConnector.defaultKnnSearchResultForDataset(targetDataset).getRmse(), databaseConnector.defaultLogisticRegressionSearchResultForDataset(targetDataset).getRmse());
                minDefaultRmse = Math.min(minDefaultRmse, databaseConnector.defaultDecisionTreeSearchResultForDataset(targetDataset).getRmse());

                System.out.format("%32s%32s%32.20f%32.20f%32.20f%15s%15s\n", targetDataset.getDatasetName(), mostSimilarDataset.getDatasetName(), minDissimilarity,
                        databaseConnector.bestSearchResultForDatasetWithoutSVM(targetDataset).getRmse(), bestSearch.getRmse(), position, bestSearch.getRmse() <= minDefaultRmse);
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

}
