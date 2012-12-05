package bb.mcmc.analysis.glm;

import java.util.Arrays;
import java.util.Random;

import dr.math.MathUtils;

import javastat.regression.lm.LinearRegression;
import Jama.Matrix;

public abstract class GLMTemplate {

	private static final double EPSILON = 1e-8;
	private static final int MAX_ITE = 50;
	/**
	 * The response.
	 */

	public double[] response;

	/**
	 * The values of the covariates (excluding the one corresponding to the
	 * intercept), <br>
	 * covariate[j]: the (j+1)'th covariate vector.
	 */

	public double[][] covariate;

	/**
	 * The parameter estimates, <br>
	 * coefficients[j]: the parameter estiamte corresponding to the (j+1)'th
	 * covariate.
	 */

	public double[] coefficients;

	/**
	 * The standard errors of the parameter estimates, <br>
	 * coefficientSE[j]: the standard error of the (j+1)'th parameter estimate.
	 */

	public double[] linearPredictors;
	/**
	 * The weight matrix.
	 */

	public double[][] weights;

	/**
	 * The means of the responses.
	 */

	public double[] means;

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
	 * The IRLS estimate.
	 * 
	 * @param dataObject
	 *            the input responses and values of the covariates, for example,
	 *            coefficients(response,covariate).
	 * @return the estimated coefficients.
	 * @exception IllegalArgumentException
	 *                the response vector and rows of the covariate matrix must
	 *                have the same length.
	 */

	protected double[] coefficients(Object... dataObject) {
		
		boolean addErrorToLM = false;
		response = (double[]) dataObject[0];
		covariate = (double[][]) dataObject[1];
		error = 1.0;
		int ite = 0;
		new javastat.util.DataManager().checkDimension(covariate);
		if (response.length != covariate[0].length) {
			throw new IllegalArgumentException(
					"The response vector and rows of the covariate matrix "
							+ "must have the same length.");
		}
		// coefficients = new GLMDataManager().setInitialEstimate(covariate);
		coefficients = setInitialEstimate(covariate);
		covariateMatrix = new Matrix(covariate);
		responseMatrix = new Matrix(response, response.length);
		weights = new double[response.length][response.length];
		for (int i = 0; i < weights.length; i++) {
			weights[i][i] = 1;
		}
		weightMatrix = new Matrix(weights);
		inversedWeights = new double[weights.length][weights.length];
		
		while (error > EPSILON && ite<MAX_ITE) {
			coefficientMatrix = new Matrix(coefficients, coefficients.length);
			linearPredictors = linearPredictors(covariate, coefficients);
			means = means(coefficients, covariate);

			for (int i = 0; i < means.length; i++) {
				if (means[i] == 0) {
					inversedWeights[i][i] = 0;
				} else {
					inversedWeights[i][i] = 1.0 / means[i];
				}
			}
			linearPredictorMatrix = covariateMatrix.transpose().times(
					coefficientMatrix);
			zMatrix = linearPredictorMatrix.plus(new Matrix(inversedWeights)
					.times(responseMatrix.minus(new Matrix(means, means.length))));
			xwxMatrix = covariateMatrix.times(weightMatrix).times(
					covariateMatrix.transpose());

			if (Math.abs(xwxMatrix.det()) <= 1e-8) {
				xwx = xwxMatrix.getArray();
				for (int i = 0; i < xwx.length; i++) {
					xwx[i][i] = xwx[i][i] + 0.1;
				}
				xwxMatrix = new Matrix(xwx);
			}
			updatedCoefficientMatrix = xwxMatrix.inverse()
					.times(covariateMatrix.times(weightMatrix)).times(zMatrix);
			coefficients = updatedCoefficientMatrix.getColumnPackedCopy();

			error = Math.pow(updatedCoefficientMatrix.minus(coefficientMatrix).normF(), 2.0);

			if (Double.isNaN(error)) {
				LinearRegression lm = new LinearRegression();
				double[] spec = new double[response.length];
				for (int i = 0; i < response.length; i++) {
					spec[i] = Math.log(response[i]);
				}
				// potentially a good starting point
				coefficients = lm.coefficients(spec, covariate[1]);
				
				error = 1;

				if (addErrorToLM) {
//					System.err.println("Stuck at\t"+ Arrays.toString(coefficients));
//					System.exit(-1);
					coefficients = addRandomnessToCoefficient(coefficients);
				}
				addErrorToLM = true;
			}
			ite++;
		}
		if(ite>=MAX_ITE){
			System.err.println("GLM did not converge for a variable at this iteration (Geweke diagnostic). Variable ignored until next check");
		}
		return coefficients;
	}

	private static double[] addRandomnessToCoefficient(double[] coefficients) {
		for (int i = 0; i < coefficients.length; i++) {
			coefficients[i] += r.nextGaussian() * coefficients[i] ;
		}
		return coefficients;
		
	}

	private static double[] setInitialEstimate(double[][] covariate) {

		double[] initialEstimate = new double[covariate.length];
		double a;
		for (int i = 0; i < initialEstimate.length; i++) {
			a = 0;
			for (int j = 0; j < covariate[i].length; j++) {
				a += covariate[i][j];
			}
			initialEstimate[i] = covariate[i].length / (1 * a);

		}

		return initialEstimate;

	}

	public double[] linearPredictors(double[][] covariate, double[] coefficients) {
		linearPredictors = new Matrix(covariate).transpose()
				.times(new Matrix(coefficients, coefficients.length))
				.getColumnPackedCopy();
		return linearPredictors;
	}

	protected double[] meansLog(double[] coefficients, double[]... covariate) {
		linearPredictors = linearPredictors(covariate, coefficients);
		means = new double[linearPredictors.length];
		for (int i = 0; i < means.length; i++) {
			means[i] = Math.exp(linearPredictors[i]);
		}

		return means;
	}

	protected static double[][] addIntercept(double[]... covariate) {
		double[][] covariateWithIntercept = new double[covariate.length + 1][covariate[0].length];
		for (int i = 0; i < covariate[0].length; i++) {
			covariateWithIntercept[0][i] = 1.0;
		}
		for (int i = 1; i < covariateWithIntercept.length; i++) {
			covariateWithIntercept[i] = covariate[i - 1];
		}

		return covariateWithIntercept;
	}
	
	/**
	 * The means of the responses.
	 * 
	 * @param coefficients
	 *            the parameter estimates, <br>
	 *            coefficients[j]: the parameter estiamte corresponding to the
	 *            (j+1)'th covariate.
	 * @param covariate
	 *            the values of the covariates, <br>
	 *            covariate[j]: the (j+1)'th covariate vector.
	 * @return the means of the responses.
	 */

	protected abstract double[] means(double[] coefficients,
			double[]... covariate);
	
	private static Random r = new Random();
}
