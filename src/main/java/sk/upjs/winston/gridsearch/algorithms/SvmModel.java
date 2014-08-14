package sk.upjs.winston.gridsearch.algorithms;

import sk.upjs.winston.gridsearch.model.Dataset;
import sk.upjs.winston.gridsearch.model.Model;
import sk.upjs.winston.gridsearch.model.SearchResult;
import sk.upjs.winston.gridsearch.model.SvmSearchResult;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.*;
import weka.core.Instances;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Class for performing SVM (SMO) analysis of datasets.
 * Created by stefan on 7/5/14.
 */
public class SvmModel extends Model {
    public static final double MIN_C = 0.0;
    public static final double MAX_C = 2.0;
    public static final double STEP_C = 0.05;
    private static final double DEFAULT_P = 1.0e-12;
    public static final double MIN_P = DEFAULT_P / 10;
    public static final double MAX_P = DEFAULT_P * 5;
    public static final double STEP_P = DEFAULT_P / 20;

    /**
     * Performes SMO algorithm and evaluates results 10 times with 10-fold cross validation method.
     * Returnes the mean squared error for given model.
     * @param dataInstances dataset instances
     * @param kernel SVM kernel to use
     * @param complexityConstant -C parameter of SMO algorithm
     * @param epsilonRoundOffError -P parameter of Weka
     * @return root mean squared error
     */
    public double svm(Instances dataInstances, Kernel kernel, double complexityConstant, double epsilonRoundOffError) {
        SMO smo = new SMO();
        Evaluation evaluation = null;
        try {
            kernel.buildKernel(dataInstances);
            smo.setKernel(kernel);
            smo.setC(complexityConstant);
            smo.setEpsilon(epsilonRoundOffError);

            evaluation = new Evaluation(dataInstances);
            evaluation.crossValidateModel(smo, dataInstances, 10, new Random(1));
        } catch (Exception e) {
//            e.printStackTrace();
            return ERROR_DURING_CLASSIFICATION;
        }
        return evaluation.rootMeanSquaredError();
    }

    /**
         * Performs SVM for kernels (StringKernel, PolyKernel, NormalizedPolyKernel, RBFKernel)
         * with default parameters, complexity constant C=MIN_C..MAX_C with step STEP_C and
         * epsilonRoundOffError P=MIN_P..MAX_P with step STEP_P. Returns RMSE for every values combination.
         * When something goes wrong during search, the result of this search is not included in result set.
         * @param dataset dataset details which belongs to returned search result
         * @param dataInstances dataset instances
         * @return Set of SvmSearchResult instances
         */
    public Set<SearchResult> svmSearch(Dataset dataset, Instances dataInstances) {
        Set<SearchResult> results = new HashSet<SearchResult>();
        double rmse;
        for (double c = MIN_C; c <= MAX_C; c += STEP_C) {
            for (double p = MIN_P; p <= MAX_P; p += STEP_P) {
                rmse = svm(dataInstances, new StringKernel(), c, p);
                if (rmse != ERROR_DURING_CLASSIFICATION) {
                    SearchResult res = new SvmSearchResult(dataset, rmse, SvmSearchResult.KERNEL_STRING_KERNEL, c, p);
                    results.add(res);
                }
                rmse = svm(dataInstances, new PolyKernel(), c, p);
                if (rmse != ERROR_DURING_CLASSIFICATION) {
                    SearchResult res = new SvmSearchResult(dataset, rmse, SvmSearchResult.KERNEL_POLYNOMIAL_KERNEL, c, p);
                    results.add(res);
                }
                rmse = svm(dataInstances, new NormalizedPolyKernel(), c, p);
                if (rmse != ERROR_DURING_CLASSIFICATION) {
                    SearchResult res = new SvmSearchResult(dataset, rmse, SvmSearchResult.KERNEL_NORMALIZED_POLYNOMIAL_KERNEL, c, p);
                    results.add(res);
                }
                rmse = svm(dataInstances, new RBFKernel(), c, p);
                if (rmse != ERROR_DURING_CLASSIFICATION) {
                    SearchResult res = new SvmSearchResult(dataset, rmse, SvmSearchResult.KERNEL_RBF_KERNEL, c, p);
                    results.add(res);
                }
                System.out.println("c: " + c + ", p: " + p);
            }
        }
        return results;
    }

    /**
     * Performes SVM algorithm with random
     * hyperparameter values and evaluates results 10 times with 10-fold cross validation method.
     * Returnes the SvmSearchResult object for given model.
     *
     * @param dataInstances dataset instances
     * @param dataset dataset details which belongs to returned search result
     *
     * @return svm search result object
     */
    public SvmSearchResult svmRandomAnalysis(Instances dataInstances, Dataset dataset) {
        Kernel kernel = getRandomParameterKernel();
        double complexityConstant = getRandomParameterComplexityConstant(MIN_C,MAX_C*5);
        double epsilonRoundOffError = getRandomParameterEpsilonRoundOffError(MIN_P/2,MAX_P*2);
        if(kernel instanceof StringKernel){
            return new SvmSearchResult(dataset, svm(dataInstances,kernel,complexityConstant,epsilonRoundOffError), SvmSearchResult.KERNEL_STRING_KERNEL, complexityConstant, epsilonRoundOffError);
        } else if(kernel instanceof PolyKernel){
            return new SvmSearchResult(dataset, svm(dataInstances,kernel,complexityConstant,epsilonRoundOffError), SvmSearchResult.KERNEL_POLYNOMIAL_KERNEL, complexityConstant, epsilonRoundOffError);
        } else if(kernel instanceof NormalizedPolyKernel){
            return new SvmSearchResult(dataset, svm(dataInstances,kernel,complexityConstant,epsilonRoundOffError), SvmSearchResult.KERNEL_NORMALIZED_POLYNOMIAL_KERNEL, complexityConstant, epsilonRoundOffError);
        } else if(kernel instanceof RBFKernel){
            return new SvmSearchResult(dataset, svm(dataInstances,kernel,complexityConstant,epsilonRoundOffError), SvmSearchResult.KERNEL_RBF_KERNEL, complexityConstant, epsilonRoundOffError);
        }
        return null;
    }

    /**
     * Generates a random kernel parameter for SVM algorithm.
     * Possible kernels are: StringKernel, PolyKernel, NormalizedPolyKernel, RBFKernel.
     * Each kernel has equal probability to be chosen.
     *
     * @return the random kernel instance
     */
    public Kernel getRandomParameterKernel() {
        double rand = Math.random();
        if(rand < 0.25d){
            return new StringKernel();
        }else if(rand < 0.5d){
            return new PolyKernel();
        }else if(rand < 0.75d){
            return new NormalizedPolyKernel();
        }
        return new RBFKernel();
    }

    /**
     * Generates a random value for complexity constant parameter of SVM algorithm.
     *
     * @param from min value for the generated random number (inclusive)
     * @param to   max value for the generated random number (exclusive)
     * @return the random double value
     */
    public double getRandomParameterComplexityConstant(double from, double to) {
        if (from > to) {
            double f = from;
            from = to;
            to = f;
        }
        return from + Math.random() * (to - from);
    }

    /**
     * Generates a random value for epsilon round off error parameter of SVM algorithm.
     *
     * @param from min value for the generated random number (inclusive)
     * @param to   max value for the generated random number (exclusive)
     * @return the random double value
     */
    public double getRandomParameterEpsilonRoundOffError(double from, double to) {
        if (from > to) {
            double f = from;
            from = to;
            to = f;
        }
        return from + Math.random() * (to - from);
    }

}
