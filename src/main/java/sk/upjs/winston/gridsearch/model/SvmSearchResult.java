package sk.upjs.winston.gridsearch.model;

/**
 * This class stores single result of SVM (SMO) algorithm run for given dataset
 * and hyperparameter values for kernel (-K), complexity constant (-C)
 * and epsilon for round-off error (-P) of SVM (SMO) algorithm.
 * Created by stefan on 7/5/14.
 */
public class SvmSearchResult extends SearchResult {
    public static final String KERNEL_STRING_KERNEL = "StringKernel";
    public static final String KERNEL_POLYNOMIAL_KERNEL = "PolyKernel";
    public static final String KERNEL_NORMALIZED_POLYNOMIAL_KERNEL = "NormalizedPolyKernel";
    public static final String KERNEL_RBF_KERNEL = "RBFKernel";


    private String kernel;
    private double complexityConstant;
    private double epsilonRoundOffError;

    public SvmSearchResult() {
    }

    public SvmSearchResult(Dataset dataset, double rmse, String kernel, double complexityConstant, double epsilonRoundOffError) {
        super(dataset, rmse);
        this.kernel = kernel;
        this.complexityConstant = complexityConstant;
        this.epsilonRoundOffError = epsilonRoundOffError;
    }

    public String getKernel() {
        return kernel;
    }

    public void setKernel(String kernel) {
        this.kernel = kernel;
    }

    public double getComplexityConstant() {
        return complexityConstant;
    }

    public void setComplexityConstant(double complexityConstant) {
        this.complexityConstant = complexityConstant;
    }

    public double getEpsilonRoundOffError() {
        return epsilonRoundOffError;
    }

    public void setEpsilonRoundOffError(double epsilonRoundOffError) {
        this.epsilonRoundOffError = epsilonRoundOffError;
    }
}
