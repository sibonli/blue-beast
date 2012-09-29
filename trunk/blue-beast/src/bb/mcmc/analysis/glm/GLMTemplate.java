package bb.mcmc.analysis.glm;

/**
 * <p>Title: javastat</p>
 * <p>Description: JAVA programs for statistical computations</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Tung Hai University</p>
 * @author Wen Hsiang Wei and B. J. Guo
 * @version 1.4
 */

import java.util.*;

import javastat.*;

import static javastat.util.Output.*;

import javastat.util.*;

import JSci.maths.statistics.*;

import Jama.*;

/**
 *
 * The class provides basic methods for fitting generalized linear models.</p>
 */

public abstract class GLMTemplate extends StatisticalAnalysis
{

    /**
     * The response.
     */

    public double[] response;

    /**
     * The values of the covariates (excluding the one corresponding to the
     * intercept),
     * <br> covariate[j]: the (j+1)'th covariate vector.
     */

    public double[][] covariate;

    /**
     * The parameter estimates,
     * <br> coefficients[j]: the parameter estiamte corresponding to the
     *                       (j+1)'th covariate.
     */

    public double[] coefficients;

    /**
     * The standard errors of the parameter estimates,
     * <br> coefficientSE[j]: the standard error of the (j+1)'th parameter
     *                        estimate.
     */

    public double[] coefficientSE;

    /**
     * The level of significance.
     */

    public double alpha;




    public double[] linearPredictors;

    /**
     * The correlation matrix of the estimated coefficients,
     * <br> correlation[i][j]: the correlation of the (i+1)'th and the (j+1)'th
     *                         estiamted coefficients.
     */



    public double[][] variance;


    /**
     * The weight matrix.
     */

    public double[][] weights;

    /**
     * The means of the responses.
     */

    public double[] means;



    public LinkFunction link;

    /**
     * The double array with the values of (XWX).
     */

    private double[][] xwx;

    /**
     * The inverse of the weight matrix.
     */

    private double[][] inversedWeights;

    /**
     * The error.
     */

    private double error;

    /**
     * The response matrix.
     */

    private Matrix responseMatrix;

    /**
     * The covariate matrix.
     */

    private Matrix covariateMatrix;

    /**
     * The coefficient matrix.
     */

    private Matrix coefficientMatrix;

    /**
     * The matrix with linear predictor values.
     */

    private Matrix linearPredictorMatrix;

    /**
     * The weight matrix.
     */

    private Matrix weightMatrix;

    /**
     * The z matrix.
     */

    private Matrix zMatrix;

    /**
     * The (XWX) matrix.
     */

    private Matrix xwxMatrix;

    /**
     * The updated coefficient matrix.
     */

    private Matrix updatedCoefficientMatrix;

    /**
     * The object represents a normal distribution.
     */

    private NormalDistribution normalDistribution;



    public GLMTemplate() {}

    /**
     * The IRLS estimate.
     * @param argument the empty argument.
     * @param dataObject the input responses and values of the covariates.
     * @return the estimated coefficients.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    protected double[] coefficients(Hashtable argument,
                                    Object ...dataObject)
    {
        return coefficients(dataObject);
    }

    /**
     * The IRLS estimate.
     * @param dataObject the input responses and values of the covariates,
     *                   for example, coefficients(response,covariate).
     * @return the estimated coefficients.
     * @exception IllegalArgumentException the response vector and rows of the
     *                                     covariate matrix must have the same
     *                                     length.
     */

    protected double[] coefficients(Object ...dataObject)
    {
        response = (double[]) dataObject[0];
        covariate = (double[][]) dataObject[1];
        BasicStatistics.convergenceCriterion = new double[]{0.000001};
        new javastat.util.DataManager().checkDimension(covariate);
        if (response.length != covariate[0].length)
        {
            throw new IllegalArgumentException(
                    "The response vector and rows of the covariate matrix " +
                    "must have the same length.");
        }
//        coefficients = new GLMDataManager().setInitialEstimate(covariate);
        coefficients = setInitialEstimate(covariate);
        covariateMatrix = new Matrix(covariate);
        responseMatrix = new Matrix(response, response.length);
        error = 1.0;
        while (error > BasicStatistics.convergenceCriterion[0])
        {
            coefficientMatrix = new Matrix(coefficients, coefficients.length);
            weights = weights(coefficients, covariate);
            inversedWeights = new double[weights.length][weights.length];
            linearPredictors = linearPredictors(covariate, coefficients);
            means = means(coefficients, covariate);
//System.out.println(Arrays.toString(means));            
            for (int i = 0; i < weights.length; i++)
            {
                if (weights[i][i] == 0)
                {
                    inversedWeights[i][i] = 0;
                }
                else
                {
                	inversedWeights[i][i] = 1.0 / weights[i][i];
//                	inversedWeights[i][i] = 1.0/ means[i];// weights[i][i];
                }
//                System.out.println(weights[i][i] +"\t"+ inversedWeights[i][i]);
//                weights[i][i] /= means[i]; //effectively weight=1?
//                weights[i][i] = 1;
//                inversedWeights[i][i] = 1;
            }
            weightMatrix = new Matrix(weights);
System.out.println(Arrays.toString(means));
//System.out.println(Arrays.toString(weightMatrix.getColumnPackedCopy()));
            linearPredictorMatrix = covariateMatrix.transpose().times(
                    coefficientMatrix);
    
            zMatrix = linearPredictorMatrix.plus(
                    new Matrix(inversedWeights).times(responseMatrix.minus(
                            new Matrix(means, means.length))));
            xwxMatrix = covariateMatrix.times(weightMatrix).times(
                    covariateMatrix.transpose());

            if (Math.abs(xwxMatrix.det()) <= 1e-8)
            {
                xwx = xwxMatrix.getArray();
                for (int i = 0; i < xwx.length; i++)
                {
                    xwx[i][i] = xwx[i][i] + 0.1;
                }
                xwxMatrix = new Matrix(xwx);
            }
            updatedCoefficientMatrix = xwxMatrix.inverse().times(
                    covariateMatrix.times(weightMatrix)).times(zMatrix);
System.out.println("===\t"+Arrays.toString(linearPredictorMatrix.getColumnPackedCopy()));
System.out.println("=\t"+Arrays.toString(zMatrix.getColumnPackedCopy()));
System.out.println("=\t"+Arrays.toString(xwxMatrix.getColumnPackedCopy()));
System.out.println("=\t"+Arrays.toString(updatedCoefficientMatrix.getColumnPackedCopy()));
            coefficients = updatedCoefficientMatrix.getColumnPackedCopy();
    
            error = Math.pow(updatedCoefficientMatrix.minus(
                    coefficientMatrix).normF(), 2.0);
        }
        output.put(COEFFICIENTS, coefficients);

        return coefficients;
    }



    private static double[] setInitialEstimate(double[][] covariate) {


            double[] initialEstimate = new double[covariate.length];
            double a;
            for (int i = 0; i < initialEstimate.length; i++)
            {
                a = 0;
                for (int j = 0; j < covariate[i].length; j++)
                {
                    a += covariate[i][j];
                }
                initialEstimate[i] = covariate[i].length / (1 * a);
                
            }
//            initialEstimate = new double[]{1,-1};
            System.out.println("+++ "+Arrays.toString(initialEstimate));
//            initialEstimate[1] = -initialEstimate[1]; 
            return initialEstimate;
 
	}

	public double[] linearPredictors(double[][] covariate,
                                     double[] coefficients)
    {
        linearPredictors = new Matrix(covariate).transpose().times(
                new Matrix(coefficients, coefficients.length)).
                           getColumnPackedCopy();
        return linearPredictors;
    }

    /**
	 * The means of the responses.
	 * @param link the link.
	 * @param coefficients the parameter estimates,
	 * <br>                coefficients[j]: the parameter estiamte corresponding
	 *                     to the (j+1)'th covariate.
	 * @param covariate the values of the covariates,
	 * <br>             covariate[j]: the (j+1)'th covariate vector.
	 * @return the means of the responses.
	 * @exception IllegalArgumentException wrong input link function.
	 */
	
	protected double[] means(LinkFunction link,
	                         double[] coefficients,
	                         double[] ...covariate)
	{
	    linearPredictors = linearPredictors(covariate, coefficients);
	    means = new double[linearPredictors.length];
	    switch (link)
	    {
	        case IDENTITY:
	            for (int i = 0; i < means.length; i++)
	            {
	                means[i] = linearPredictors[i];
	            }
	            break;
	        case LOG:
	            for (int i = 0; i < means.length; i++)
	            {
	                means[i] = Math.exp(linearPredictors[i]);
//	                int j = 1;
//	                if (means[i]==Double.POSITIVE_INFINITY){
//	                	means[i] = Math.exp(linearPredictors[i]/(Math.pow(2, j)));
//	                	j++;
//	                }
	            }
	            break;
	        case INVERSE:
	            for (int i = 0; i < means.length; i++)
	            {
	                means[i] = 1 / linearPredictors[i];
	            }
	            break;
	        case INVERSE_SQUARE:
	            for (int i = 0; i < means.length; i++)
	            {
	                means[i] = Math.pow(linearPredictors[i], -1 * 0.5);
	            }
	            break;
	        case SQUARE_ROOT:
	            for (int i = 0; i < means.length; i++)
	            {
	                means[i] = Math.pow(linearPredictors[i], 2.0);
	            }
	            break;
	        case LOGIT:
	            for (int i = 0; i < means.length; i++)
	            {
	                means[i] = Math.exp(linearPredictors[i]) /
	                           (1.0 + Math.exp(linearPredictors[i]));
	            }
	            break;
	        case PROBIT:
	            for (int i = 0; i < means.length; i++)
	            {
	                means[i] = normalDistribution.inverse(linearPredictors[i]);
	            }
	            break;
	        case COMPLEMENTARY_LOGLOG:
	            for (int i = 0; i < means.length; i++)
	            {
	                means[i] = 1.0 - Math.exp( -1.0 *
	                                           Math.exp(linearPredictors[i]));
	            }
	            break;
	        default:
	            throw new IllegalArgumentException
	                    ("Wrong input link function.");
	    }
	    output.put(MEANS, means);
	
	    return means;
	}

	protected abstract double[][] weights(double[] coefficients,
                                          double[] ...covariate);

    /**
     * The means of the responses.
     * @param coefficients the parameter estimates,
     * <br>                coefficients[j]: the parameter estiamte corresponding
     *                     to the (j+1)'th covariate.
     * @param covariate the values of the covariates,
     * <br>             covariate[j]: the (j+1)'th covariate vector.
     * @return the means of the responses.
     */

    protected abstract double[] means(double[] coefficients,
                                      double[] ...covariate);

    
}
