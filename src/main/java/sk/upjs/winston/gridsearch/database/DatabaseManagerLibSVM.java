package sk.upjs.winston.gridsearch.database;

import sk.upjs.winston.gridsearch.model.Dataset;
import sk.upjs.winston.gridsearch.model.SvmSearchResult;
import sk.upjs.winston.gridsearch.model.SvmSearchResultLibSVM;

import java.sql.*;
import java.util.Iterator;
import java.util.List;

/**
 * Manages the database operations through JDBC.
 */
public class DatabaseManagerLibSVM {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://master.exp.upjs.sk/stefan_bocko";
    //  Database credentials
    static final String USER = "stefan_bocko";
    static final String PASS = "NPr-uMW-GM8-k9Y";
    //table names
    private static final String TABLE_DATASET = "dataset_libsvm";
    private static final String TABLE_SVM = "svm_libsvm";

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
                    "(dataset_id, rmse, kernel, complexity_constant, gamma) VALUES (" + toSave.getDataset().getId() +
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

    /**
     * Saves dataset to DB.
     * @param toSave dataset to be saved
     * @return generated dataset id
     */
    public long saveDatasetToDatabase(Dataset toSave) {
        long result = -1l;
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
            stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            int idPosition = 1;

            if (rs != null && rs.next()) {
                result = rs.getLong(idPosition);
            }
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
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
        return result;
    }
    
    public boolean saveSvmSearchResultsToDatabase(List<SvmSearchResultLibSVM>toSave) {
        Connection conn = null;
        Statement stmt = null;
        try {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            stmt = conn.createStatement();

            String sql = "INSERT INTO " + TABLE_SVM +
                    "(dataset_id, rmse, kernel, complexity_constant, gamma) ";
            for (Iterator iterator = toSave.iterator(); iterator.hasNext();) {
				SvmSearchResultLibSVM svmSearchResultLibSVM = (SvmSearchResultLibSVM) iterator
						.next();
				if (toSave.get(0) == svmSearchResultLibSVM)
					sql+= "VALUES";
				else 
					sql+= ",";
				sql+= " (" + svmSearchResultLibSVM.getDataset().getId() +
	                    ", " + svmSearchResultLibSVM.getRmse() + ", '" + svmSearchResultLibSVM.getKernel() + "', " + svmSearchResultLibSVM.getComplexityConstant() + ", " + svmSearchResultLibSVM.getGamma() + ")";
			}
            		
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
