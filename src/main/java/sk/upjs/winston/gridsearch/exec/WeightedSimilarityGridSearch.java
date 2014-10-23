package sk.upjs.winston.gridsearch.exec;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import sk.upjs.winston.gridsearch.database.DatabaseConnector;
import sk.upjs.winston.gridsearch.model.Dataset;
import sk.upjs.winston.gridsearch.model.SearchResult;
import sk.upjs.winston.gridsearch.model.SimilarityGridSearch;

import java.util.List;

/**
 * Created by stefan on 10/14/14.
 */
public class WeightedSimilarityGridSearch {
    private static SessionFactory factory;

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Incorrect input. Please provide weights for knn, decision tree and logistic regression");
            return;
        }

        double knnWeight = Double.parseDouble(args[0]);
        double decTreeWeight = Double.parseDouble(args[1]);
        double logRegWeight = Double.parseDouble(args[2]);
//        double knnWeight = 0.5;
//        double decTreeWeight = 0.01;
//        double logRegWeight = 0.3;

//        System.out.format("\n%32s%32s%32s%32s%32s", "Knn weight", "Decision Tree weight", "Logistic regression weight", "# of better", "total # of results");


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

            List<Dataset> datasets = databaseConnector.getApplicableDatasetsForDefaultGridSimilaritySearch();

            int betterResults = 0;
            int totalResults = 51123;

            for (int i = 0; i < datasets.size(); i++) {
                Dataset targetDataset = datasets.get(i);
                double minDissimilarity = Double.MAX_VALUE;
                Dataset mostSimilarDataset = null;

                //find most similar dataset
                for (int j = 0; j < datasets.size(); j++) {
                    if (i != j) {
                        Dataset currentDataset = datasets.get(j);
                        double currentDissimilarity = databaseConnector.weightedDatasetDissimilarityFromDefaultHyperparametersWithoutSVM(targetDataset, currentDataset, knnWeight, decTreeWeight, logRegWeight);
                        if (currentDissimilarity < minDissimilarity) {
                            minDissimilarity = currentDissimilarity;
                            mostSimilarDataset = currentDataset;
                        }
                    }
                }

                //get best hyperparameter values
                SearchResult bestSearch = databaseConnector.similaritySearchForDataset(targetDataset, databaseConnector.bestSearchResultForDatasetWithoutSVM(mostSimilarDataset));
                betterResults += databaseConnector.numberOfBetterSearchResultsForDatasetWithoutSVM(targetDataset, bestSearch.getRmse());
//                                totalResults += databaseConnector.totalNumberOfSearchResultsForDatasetWithoutSVM(targetDataset);
            }
            session.save(new SimilarityGridSearch(knnWeight, decTreeWeight, logRegWeight, betterResults));
//            System.out.format("\n%32.2f%32.2f%32.2f%32s%32s\n\n", knnWeight, decTreeWeight, logRegWeight, betterResults, totalResults);
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

    private static class Weights {
        double knnWeight, decTreeWeight, logRegWeight;

        private Weights(double knnWeight, double decTreeWeight, double logRegWeight) {
            this.knnWeight = knnWeight;
            this.decTreeWeight = decTreeWeight;
            this.logRegWeight = logRegWeight;
        }
    }
}
