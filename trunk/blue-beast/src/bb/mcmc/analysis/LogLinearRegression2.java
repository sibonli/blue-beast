package bb.mcmc.analysis;

/**
 * <p>Title: javastat</p>
 * <p>Description: JAVA programs for statistical computations</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Tung Hai University</p>
 * @author Wen Hsiang Wei and B. J. Guo
 * @version 1.4
 */

import static javastat.regression.glm.LinkFunction.LOG;
import static javastat.util.Output.PEARSON_RESIDUALS;
import javastat.regression.glm.ExponentialFamily;
import javastat.regression.glm.LogLinearRegression;
import Jama.Matrix;

public class LogLinearRegression2 extends LogLinearRegression
{

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
                             double[] ...covariate)    {
        if (link == null)
        {
            link = LOG;
            
        }
        link = LOG;
        means = this.means(link, coefficients, covariate);
       
                for (int i = 0; i < means.length; i++)
                {
                    means[i] = offset[i] * means[i];
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
                for (int i = 0; i < means.length; i++)
                {
                    weights[i][i] = means[i];
                }
        

        return weights;
    }

    /**
     * The deviance function.
     * @param response the responses.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the residual deviance.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    @Override
	protected double deviance(double[] response,
                              double[] ...covariate)
    {
        coefficients = super.coefficients(response, covariate);
        means = means(coefficients, covariate);
        deviance = 0.0;
        for (int i = 0; i < means.length; i++)
        {
            if (response[i] != 0.0)
            {
                deviance += 2 *
                        (response[i] * Math.log(response[i] / means[i]) -
                         (response[i] - means[i]));
            }
            else
            {
                deviance += 2 * means[i];
            }
        }

        return deviance;
    }

    /**
     * The null deviance function.
     * @param response the responses.
     * @return the null deviance.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    @Override
	protected double nullDeviance(double[] response)
    {
        return deviance(response, new Matrix(1, response.length, 1).getArray());
    }

    /**
     * The Pearson residuals.
     * @param response the responses.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the Pearson residuals.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    @Override
	protected double[] pearsonResiduals(double[] response,
                                        double[] ...covariate)
    {
        coefficients = super.coefficients(response, covariate);
        means = means(coefficients, covariate);
        responseVariance = responseVariance(means, ExponentialFamily.GAMMA);
        pearsonResiduals = pearsonResiduals(responseVariance, response,
                                            covariate);
        output.put(PEARSON_RESIDUALS, pearsonResiduals);

        return pearsonResiduals;
    }
//
//    /**
//     * The deviance residuals.
//     * @param response the binomial responses.
//     * @param covariate the values of the covariates,
//     * <br>             covariate[j]: the (j+1)'th covariate vector.
//     * @return the deviance residuals.
//     * @exception IllegalArgumentException the response vector and rows of the
//     *                                     covariate matrix must have the same
//     *                                     length.
//     */
//
//    protected double[] devianceResiduals(double[] response,
//                                         double[] ...covariate)
//    {
//        coefficients = super.coefficients(response, covariate);
//        means = means(coefficients, covariate);
//        devianceResiduals = new double[response.length];
//        residuals = 0.0;
//        for (int i = 0; i < means.length; i++)
//        {
//            if (response[i] / means[i] > 0 && response[i] != 0)
//            {
//                residuals = Math.sqrt(2 *
//                                      (response[i] *
//                                       Math.log(response[i] / means[i]) -
//                                       (response[i] - means[i])));
//            }
//            else
//            {
//                residuals = Math.sqrt( -2 * (response[i] - means[i]));
//            }
//            if (response[i] - means[i] > 0)
//            {
//                devianceResiduals[i] = residuals;
//            }
//            else
//            {
//                devianceResiduals[i] = -1.0 * residuals;
//            }
//        }
//
//        return devianceResiduals;
//    }


}
