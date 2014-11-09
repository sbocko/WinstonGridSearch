package sk.upjs.winston.gridsearch.model;

/**
 * This class stores single result of SVM (SMO) algorithm run for given dataset
 * and hyperparameter values for kernel (-K), complexity constant (-C)
 * and epsilon for round-off error (-P) of SVM (SMO) algorithm.
 * Created by stefan on 7/5/14.
 */
public class SvmSearchResultLibSVM extends SearchResult {
    public static final String KERNEL_LINEAR_KERNEL = "LinearKernel";
    public static final String KERNEL_RBF_KERNEL = "RBFKernel";


    private String kernel;
    private double complexityConstant;
    private double gamma;

    public SvmSearchResultLibSVM() {
    }

    public SvmSearchResultLibSVM(Dataset dataset, double rmse, String kernel, double complexityConstant, double gamma) {
        super(dataset, rmse);
        this.kernel = kernel;
        this.complexityConstant = complexityConstant;
        this.gamma = gamma;
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

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }
}
