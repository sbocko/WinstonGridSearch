package sk.upjs.winston.gridsearch.algorithms;

/**
 * Abstract class for different classification models.
 * Methods in these classes should return ERROR_DURING_CLASSIFICATION constant,
 * when some exception or other error appears.
 * Created by stefan on 6/12/14.
 */
public abstract class Model {
    //return this constant, when some error appeares during modelling
    public static final int ERROR_DURING_CLASSIFICATION = -1;
}
