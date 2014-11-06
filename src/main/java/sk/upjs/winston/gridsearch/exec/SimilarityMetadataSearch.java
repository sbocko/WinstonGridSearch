package sk.upjs.winston.gridsearch.exec;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import sk.upjs.winston.gridsearch.database.DatabaseConnector;
import sk.upjs.winston.gridsearch.model.Dataset;
import sk.upjs.winston.gridsearch.model.SearchResult;
import weka.core.Instances;

import java.io.*;
import java.util.List;

/**
 * Created by stefan on 9/6/14.
 */
public class SimilarityMetadataSearch {
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

            System.out.format("\n%32s%32s%32s%32s%32s%32s\n\n", "Dataset", "Most similar dataset", "Most similar by metadata", "Position by alg. similarity", "Position by metadata","our < their");

            List<Dataset> datasets = databaseConnector.getApplicableDatasetsForDecisionTreeDefaultGridSimilaritySearch();

            for (int i = 0; i < datasets.size(); i++) {
                Dataset targetDataset = datasets.get(i);
                double distance = Double.MAX_VALUE;
                Dataset mostSimilarDataset = null;
                double minDissimilarity = Double.MAX_VALUE;
                Dataset mostSimilarityDataset = null;

                //find most similar dataset
                for (int j = 0; j < datasets.size(); j++) {
                    if (i != j) {
                        Dataset currentDataset = datasets.get(j);
                        double currentDistance = databaseConnector.datasetSimilarityByMetadata(targetDataset, currentDataset);
                        if (currentDistance < distance) {
                            distance = currentDistance;
                            mostSimilarDataset = currentDataset;
                        }
                        double currentDissimilarity = databaseConnector.datasetDissimilarityFromDecisionTreeDefaultHyperparameters(targetDataset, currentDataset);
                        if (currentDissimilarity < minDissimilarity) {
                            minDissimilarity = currentDissimilarity;
                            mostSimilarityDataset = currentDataset;
                        }
                    }
                }

                //get best hyperparameter values
                SearchResult bestMetadataSearch = databaseConnector.similaritySearchForDataset(targetDataset, databaseConnector.bestDecisionTreeSearchResultForDataset(mostSimilarDataset));
                SearchResult bestSimilaritySearch = databaseConnector.similaritySearchForDataset(targetDataset, databaseConnector.bestDecisionTreeSearchResultForDataset(mostSimilarityDataset));

                int totalPosition = databaseConnector.totalNumberOfDecisionTreeSearchResultsForDataset(targetDataset);
                String similarityPosition = databaseConnector.numberOfBetterDecisionTreeSearchResultsForDataset(targetDataset, bestSimilaritySearch.getRmse()) + "/" + totalPosition;
                String metadataPosition = databaseConnector.numberOfBetterDecisionTreeSearchResultsForDataset(targetDataset, bestMetadataSearch.getRmse()) + "/" + totalPosition;

                System.out.format("%32s%32s%32s%32s%32s%32s\n", targetDataset.getDatasetName(), mostSimilarityDataset.getDatasetName(), mostSimilarDataset.getDatasetName(), similarityPosition, metadataPosition, bestSimilaritySearch.getRmse() == bestMetadataSearch.getRmse() ? "=" : bestSimilaritySearch.getRmse() < bestMetadataSearch.getRmse());
//                System.out.println(mostSimilarDataset.getDatasetName());
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


    private static String getDatasetNameFromDataFile(String filePath) {
        try {
            File dataFile = new File(filePath);
            BufferedReader reader = new BufferedReader(
                    new FileReader(dataFile));
            Instances dataInstances = new Instances(reader);
            reader.close();
            // setting class attribute
            return dataInstances.relationName();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
