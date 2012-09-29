package bb.mcmc.analysis.glm;

/**
 * <p>Title: javastat</p>
 * <p>Description: JAVA programs for statistical computations</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Tung Hai University</p>
 * @author Wen Hsiang Wei and B. J. Guo
 * @version 1.4
 */


import static bb.mcmc.analysis.glm.LinkFunction.*;
import javastat.util.GLMDataManager;

public class LogLinearRegression3 extends GLMTemplate
{

    /**
     * The continuous covariate.
     */

    public double[][] continuousCovariate;


    /**
     * The offset.
     */

    public double[] offset;

    /**
     * The object represents a log-linear regression model.
     */

    private GLMDataManager glmDataManager = new GLMDataManager();


    
    
    public LogLinearRegression3()
    {
        link = LOG;
    }

    /**
     * The IRLS estimate.
     * @param response the responses.
     * @param offset the offset.
     * @param continuousCovariate the numerical values of the covariates
     *                            (excluding the one corresponding to the
     *                            intercept),
     * <br>                       continuousCovariate[j]: the (j+1)'th covariate
     *                                                    vector.
     * @return the estimated coefficients.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    public double[] coefficients(double[] response,
                                 double[] offset,
                                 double[] ...continuousCovariate)
    {
        this.response = response;
        this.offset = offset;
        this.continuousCovariate = continuousCovariate;

        return super.coefficients(response,
                                  glmDataManager.
                                  addIntercept(continuousCovariate));
    }

    /**
     * The means of the responses.
     * @param coefficients the estimated coefficients.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the means of the responses.
     * @exception IllegalArgumentException input link function is not supported.
     */

    @Override
	protected double[] means(double[] coefficients,
                             double[] ...covariate)
    {
        if (link == null)
        {
            link = LOG;
        }
        means = this.means(link, coefficients, covariate);
        switch (link)
        {
            case LOG:
                for (int i = 0; i < means.length; i++)
                {
                    means[i] = offset[i] * means[i];
                }
                break;
            case IDENTITY:
                for (int i = 0; i < means.length; i++)
                {
                    means[i] = offset[i] + means[i];
                }
                break;
            case SQUARE_ROOT:
                for (int i = 0; i < means.length; i++)
                {
                    means[i] = Math.pow(Math.sqrt(offset[i]) + means[i], 2.0);
                }
                break;
            default:
                throw new IllegalArgumentException(
                    "Input link function is not supported.");
        }

        return means;
    }

    /**
     * The weights.
     * @param coefficients the estimated coefficients.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the weights.
     * @exception IllegalArgumentException input link function is not supported.
     */

    @Override
	protected double[][] weights(double[] coefficients,
                                 double[] ...covariate)
    {
    	
        means = means(coefficients, covariate);
        weights = new double[means.length][means.length];
        
        switch ( link)
        {
            case LOG:
                for (int i = 0; i < means.length; i++)
                {
                    weights[i][i] =  means[i];

                }
                break;
            case IDENTITY:
                for (int i = 0; i < means.length; i++)
                {
                    weights[i][i] = 1.0 / means[i];
                }
                break;
            case SQUARE_ROOT:
                for (int i = 0; i < means.length; i++)
                {
                    weights[i][i] = 0.25;
                }
                break;
            default:
                throw new IllegalArgumentException(
                    "Input link function is not supported.");
        }

        return weights;
    }



}
