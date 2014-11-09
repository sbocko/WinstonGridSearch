package sk.upjs.winston.gridsearch.exec;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Created by stefan on 11/4/14.
 */
public class Binarizer {

    public static final String NUMERIC_ATTRIBUTE_POSITION = "POSITION";

    public static void main(String[] args) throws Exception {
        if(args.length != 2){
            System.out.println("Incorrect input! Usage: java -jar Binarizer.jar input_data.arff output_dir");
        }
        String datasetFile = args[0];
        String outputDir = args[1];
//        String outputDir = "other";
//        String datasetFile = "other/car.arff";

        if(outputDir.endsWith("/")){
            outputDir = outputDir.substring(0,outputDir.length()-1);
            System.out.println(outputDir);
        }

        // load input
        Instances input = new Instances(new BufferedReader(new FileReader(datasetFile)));
//        input.setClassIndex(input.numAttributes() - 1);
        Map<String, Map<String, Integer>> valueAndPositionForAttribute = new HashMap<String, Map<String, Integer>>();

        // compute the new number of attributes and their positions
        int numberOfAttributes = 0;
        for (int i = 0; i < input.numAttributes(); i++) {
            Attribute attribute = input.attribute(i);
            if (attribute.isNumeric()) {
                Map<String, Integer> position = new HashMap<String, Integer>();
                position.put(NUMERIC_ATTRIBUTE_POSITION, numberOfAttributes);
                valueAndPositionForAttribute.put(attribute.name(), position);
                numberOfAttributes++;
            } else {
                if (attribute.numValues() == 2 || attribute.index() == (input.numAttributes()-1)) {
                    Map<String, Integer> positions = new HashMap<String, Integer>();
                    Enumeration enm = attribute.enumerateValues();
                    positions.put(enm.nextElement().toString(), numberOfAttributes);
                    valueAndPositionForAttribute.put(attribute.name(), positions);
                    numberOfAttributes++;
                } else {
                    Map<String, Integer> positions = new HashMap<String, Integer>();
                    Enumeration enm = attribute.enumerateValues();
                    while (enm.hasMoreElements()) {
                        positions.put(enm.nextElement().toString(), numberOfAttributes);
                        numberOfAttributes++;
                    }
                    valueAndPositionForAttribute.put(attribute.name(), positions);
                }
            }
        }
//        System.out.println(valueAndPositionForAttribute);

        // assign new attributes to new dataset
        FastVector attributes = new FastVector();
        for (int i = 0; i < input.numAttributes(); i++) {
            Attribute attribute = input.attribute(i);
            TreeMap<String, Integer> positionsForAttribute = new TreeMap<String, Integer>();
            positionsForAttribute.putAll(valueAndPositionForAttribute.get(attribute.name()));

            Iterator it = positionsForAttribute.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (positionsForAttribute.size() > 1) {
                    attributes.addElement(new Attribute(attribute.name() + ":" + pair.getKey()));
                } else {
                    if (pair.getKey().equals(NUMERIC_ATTRIBUTE_POSITION)) {
                        attributes.addElement(new Attribute(attribute.name()));
                    } else {
                        attributes.addElement(new Attribute(attribute.name() + ":is(" + pair.getKey() + ")"));
                    }
                }
                it.remove(); // avoids a ConcurrentModificationException
            }
        }

        Instances output = new Instances("test", attributes, 0);
        System.out.println(output);

        // assign instances to new dataset
        for (int i = 0; i < input.numInstances(); i++) {
            Instance inputInstance = input.instance(i);
            double[] values = new double[numberOfAttributes];
            for (int j = 0; j < input.numAttributes(); j++) {
                Attribute attribute = input.attribute(j);
                if (attribute.isNumeric()) {
                    values[valueAndPositionForAttribute.get(attribute.name()).get(NUMERIC_ATTRIBUTE_POSITION)] = inputInstance.value(j);
                } else {
                    Integer idx = valueAndPositionForAttribute.get(attribute.name()).get(attribute.value((int) inputInstance.value(j)));
                    if (idx != null) {
                        values[idx] = 1;
                    }
                }
//                    System.out.println(input.attribute(j).value((int) inputInstance.value(j)));
            }
            output.add(new Instance(1, values));
        }

//        System.out.println(output);

        // save the dataset
        output.setRelationName(
                input.relationName() + "_binarized");
        if(!input.relationName().contains("data")){
            input.setRelationName(input.relationName() + ".data");
        }
        String filename = outputDir + "/" + input.relationName().replaceAll(
                ".[Dd][Aa][Tt][Aa]$", "_binarized" + ".arff");
        System.out.println(filename);
        ArffSaver saver = new ArffSaver();
        saver.setInstances(output);
        saver.setFile(new File(filename));
        saver.setDestination(new File(filename));
        saver.writeBatch();
    }
}
