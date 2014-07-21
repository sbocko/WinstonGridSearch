package sk.upjs.winston.gridsearch.converter;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import java.io.File;

public class CSV2ArffConverter {

    /**
     * takes 2 arguments:
     * - CSV input directory
     * - ARFF output directory
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("\nUsage: CSV2ArffConverter <inputDir> <outputDir>\n");
            System.exit(1);
        }

        File inputDir = new File(args[0]);
        if (inputDir.isDirectory()) {
            CSV2ArffConverter converter = new CSV2ArffConverter();
            for (File file : inputDir.listFiles()) {
                if (file.isFile() && !file.isHidden()) {
                    String outputFilePath = args[1];
                    if (outputFilePath.endsWith("/") || outputFilePath.endsWith("\\")) {
                        outputFilePath += file.getName().split(".data")[0] + ".arff";
                    } else {
                        outputFilePath += "/" + file.getName().split(".data")[0] + ".arff";
                    }
                    converter.convertCsvToArff(file, new File(outputFilePath));
                }
            }
        }
    }

    /*
     * Converts CSV data file to ARFF data file.
     * @param csvInput input data file in CSV format
     * @param arffOutput arff output data file
     * @return if conversion was successfull
     */
    public boolean convertCsvToArff(File csvInput, File arffOutput) {
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
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Error: " + csvInput.getName());
            return false;
        }
        return true;
    }

}