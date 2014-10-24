package sk.upjs.winston.gridsearch.database;

import sk.upjs.winston.gridsearch.model.Dataset;
import sk.upjs.winston.gridsearch.model.SvmSearchResult;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages the database operations through JDBC.
 */
public class DatabaseManager {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/stefan_bocko";
    //  Database credentials
    static final String USER = "stefan_bocko";
    static final String PASS = "NPr-uMW-GM8-k9Y";
    //table names
    private static final String TABLE_DATASET = "dataset_gs4";
    private static final String TABLE_SVM = "svm_gs4";

    public boolean saveSvmSearchResultToDatabase(SvmSearchResult toSave) {
        Connection conn = null;
        Statement stmt = null;
        try {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            stmt = conn.createStatement();

            String sql = "INSERT INTO " + TABLE_SVM +
                    "(dataset_id, rmse, kernel, complexity_constant, epsilon_err) VALUES (" + toSave.getDataset().getId() +
                    ", " + toSave.getRmse() + ", '" + toSave.getKernel() + "', " + toSave.getComplexityConstant() + ", " + toSave.getEpsilonRoundOffError() + ")";
//            System.out.println(sql);
            stmt.executeUpdate(sql);

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
            return false;
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
            return false;
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return true;
    }

    public boolean saveDatasetToDatabase(Dataset toSave) {
        Connection conn = null;
        Statement stmt = null;
        try {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            stmt = conn.createStatement();

            String sql = "INSERT INTO " + TABLE_DATASET +
                    "(`name`) VALUES ('" + toSave.getDatasetName() + "')";
//            System.out.println(sql);
            stmt.executeUpdate(sql);

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
            return false;
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
            return false;
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return true;
    }

}
