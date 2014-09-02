package sk.upjs.winston.gridsearch.exec;

import weka.classifiers.trees.J48;

/**
 * Created by stefan on 9/2/14.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println(new J48().getUnpruned());
    }
}
