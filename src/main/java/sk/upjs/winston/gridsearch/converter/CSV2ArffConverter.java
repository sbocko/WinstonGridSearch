package sk.upjs.winston.gridsearch.converter;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CSV2ArffConverter {

    /*
     * Converts CSV data file to ARFF data file.
     * @param csvInput input data file in CSV format
     * @param arffOutput arff output data file
     * @return if conversion was successfull
     */
    public boolean convertCsvToArff(File csvInput, File arffOutput){
        try {
            // load CSV
            CSVLoader loader = new CSVLoader();
            loader.setSource(csvInput);
            Instances data = loader.getDataSet();

            // save ARFF
            ArffSaver saver = new ArffSaver();
            saver.setInstances(data);
            saver.setFile(arffOutput);
            saver.setDestination(arffOutput);
            saver.writeBatch();
        } catch (IOException|NullPointerException e){
//            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * takes 2 arguments:
     * - CSV input file
     * - ARFF output file
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("\nUsage: CSV2ArffConverter <input.csv> <output.arff>\n");
            System.exit(1);
        }
        CSV2ArffConverter converter = new CSV2ArffConverter();
        converter.convertCsvToArff(new File(args[0]),new File(args[1]));
    }

}