package sk.upjs.winston.gridsearch;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Main {
    private static final double VERSION = 0.1;

    private static SessionFactory factory;

    public static void main(String[] args) {
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

    }

//    public static void main(String[] args) {
//        if(args.length == 0){
//            System.out.println(getHelp());
//
//        }
//
//
//
////        CSV2ArffConverter converter = new CSV2ArffConverter();
////        File csvInput = new File("other/car.csv");
//        File arffOutput = new File("other/iris.arff");
////        boolean converted = converter.convertCsvToArff(csvInput,arffOutput);
////        System.out.println("Conversion success: " + converted);
//
//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(
//                    new FileReader(arffOutput));
//            Instances data = new Instances(reader);
//            reader.close();
//            // setting class attribute
//            data.setClassIndex(data.numAttributes() - 1);
//
//            GridSearch gs = new GridSearch();
//            IBk decisionTree = new IBk();
//
//            gs.setClassifier(decisionTree);
//            System.out.println(gs.getBestClassifier());
//            Evaluation evaluation = new Evaluation(data);
//            evaluation.crossValidateModel(decisionTree, data, 10, new Random(1));
//            System.out.println(evaluation.toSummaryString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static String getHelp() {
        String help = "Welcome to Winston Grid Search v" + VERSION + "\n\nUsage:";


        return help;
    }
}
