package bb.mcmc.analysis.glm;

import javastat.util.GLMDataManager;

public class LogGammaRegression extends GLMTemplate {

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

	public LogGammaRegression() {

	}

	/**
	 * The IRLS estimate.
	 * 
	 * @param response
	 *            the responses.
	 * @param offset
	 *            the offset.
	 * @param continuousCovariate
	 *            the numerical values of the covariates (excluding the one
	 *            corresponding to the intercept), <br>
	 *            continuousCovariate[j]: the (j+1)'th covariate vector.
	 * @return the estimated coefficients.
	 * @exception IllegalArgumentException
	 *                the response vector and rows of the covariate matrix must
	 *                have the same length.
	 */

	public double[] coefficients(double[] response, double[] offset,
			double[]... continuousCovariate) {
		this.response = response;
		this.offset = offset;
		this.continuousCovariate = continuousCovariate;

		return super.coefficients(response,
				glmDataManager.addIntercept(continuousCovariate));
	}

	/**
	 * The means of the responses.
	 * 
	 * @param coefficients
	 *            the estimated coefficients.
	 * @param covariate
	 *            the values of the covariates, <br>
	 *            covariate[j]: the (j+1)'th covariate vector.
	 * @return the means of the responses.
	 * @exception IllegalArgumentException
	 *                input link function is not supported.
	 */

	@Override
	protected double[] means(double[] coefficients, double[]... covariate) {

		means = this.meansLog(coefficients, covariate);

		for (int i = 0; i < means.length; i++) {
			means[i] = offset[i] * means[i];
		}

		return means;
	}

	/**
	 * The weights.
	 * 
	 * @param coefficients
	 *            the estimated coefficients.
	 * @param covariate
	 *            the values of the covariates, <br>
	 *            covariate[j]: the (j+1)'th covariate vector.
	 * @return the weights.
	 * @exception IllegalArgumentException
	 *                input link function is not supported.
	 */

	@Override
	protected double[][] weights(double[] coefficients, double[]... covariate) {

		means = means(coefficients, covariate);
		weights = new double[means.length][means.length];

		for (int i = 0; i < means.length; i++) {
			weights[i][i] = means[i];

		}

		return weights;
	}

}
