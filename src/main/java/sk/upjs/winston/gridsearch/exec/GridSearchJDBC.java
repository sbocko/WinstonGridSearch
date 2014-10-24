package sk.upjs.winston.gridsearch.exec;

import sk.upjs.winston.gridsearch.database.DatabaseManager;
import sk.upjs.winston.gridsearch.model.Dataset;
import sk.upjs.winston.gridsearch.model.SvmSearchResult;

import java.sql.*;

/**
 * Created by stefan on 10/23/14.
 */
public class GridSearchJDBC {

    public static void main(String[] args) throws SQLException {
        DatabaseManager databaseManager = new DatabaseManager();
        Dataset dataset = new Dataset();
        dataset.setDatasetName("test");
        dataset.setId(1l);
        SvmSearchResult svmSearchResult = new SvmSearchResult();
        svmSearchResult.setDataset(dataset);
        svmSearchResult.setRmse(0.12343264832d);
        svmSearchResult.setKernel("testKernel");
        svmSearchResult.setComplexityConstant(0.0000000000001d);
        svmSearchResult.setEpsilonRoundOffError(0.000000000001d);
        databaseManager.saveDatasetToDatabase(dataset);
        databaseManager.saveSvmSearchResultToDatabase(svmSearchResult);
    }
}
