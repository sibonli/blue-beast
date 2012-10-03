package bb.mcmc.analysis.glm;

import java.math.BigDecimal;
import java.util.Arrays;


import javastat.regression.lm.LinearRegression;

import org.apache.commons.math3.linear.BlockFieldMatrix;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.util.BigReal;
import org.nevec.rjm.BigDecimalMath;

import Jama.Matrix;

public abstract class GLMTemplate {

	private static final double EPSILON = 1e-8;
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
	 * The object represents a normal distribution.
	 */


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
		boolean twice = false;
		response = (double[]) dataObject[0];
		covariate = (double[][]) dataObject[1];

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
		error = 1.0;

		weights = new double[response.length][response.length];
		for (int i = 0; i < weights.length; i++) {
			weights[i][i] = 1;
		}
		weightMatrix = new Matrix(weights);

		while (error > EPSILON) {
			coefficientMatrix = new Matrix(coefficients, coefficients.length);
			// weights = weights(coefficients, covariate);
			inversedWeights = new double[weights.length][weights.length];
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

				if (twice) {
					System.err.println("Stuck at\t"+ Arrays.toString(coefficients));
					System.exit(-1);
				}
				twice = true;
			}

		}

		return coefficients;
	}

	protected double[] coefficientsBigD(Object... dataObject) {
		response = (double[]) dataObject[0];
		covariate = (double[][]) dataObject[1];
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
		BlockFieldMatrix<BigReal> covariateMatrixBig = createBlockFieldMatrix(covariate);
		BlockFieldMatrix<BigReal> responseMatrixBig = createBlockFieldMatrix(response);

		error = 1.0;
		//
		// System.out.println(Arrays.toString(response));
		// System.out.println("res\t"+Arrays.toString(responseMatrix.getColumnPackedCopy()));
		// System.out.println(responseMatrix.getColumnDimension() +"\t"+
		// responseMatrix.getRowDimension());
		//
		// BigDecimal[] meansBigD;// = new BigDecimal[10];
		// BigDecimal[] weightBigD;// = new BigDecimal[10];
		//
		// BigReal[][] testData = {
		// {new BigReal(1),new BigReal(2),new BigReal(3)},
		// {new BigReal(2),new BigReal(5),new BigReal(3)},
		// {new BigReal(1),new BigReal(0),new BigReal(8)}
		// };
		// BlockFieldMatrix<BigReal> m = new
		// BlockFieldMatrix<BigReal>(testData);

		FieldMatrix<BigReal> coefficientMatrixBig;
		FieldMatrix<BigReal> linearPredictorMatrixBig;
		FieldMatrix<BigReal> zMatrixBig;
		FieldMatrix<BigReal> xwxMatrixBig;
		FieldMatrix<BigReal> updatedCoefficientMatrixBig;
		FieldMatrix<BigReal> inverseWeightsMatrixBig;
		FieldMatrix<BigReal> meansMatrixBig;
		FieldMatrix<BigReal> weightMatrixBig = createBlockFieldMatrix(1,
				response.length);
		BigReal[] meansBig;
		while (error > EPSILON) {
			coefficientMatrixBig = createBlockFieldMatrix(coefficients);

			weights = weights(coefficients, covariate);
			inversedWeights = new double[weights.length][weights.length];
			// linearPredictors = linearPredictors(covariate, coefficients);
			// means = means(coefficients, covariate);
			meansBig = meansBig(coefficients, covariate);
			// System.out.println(Arrays.toString(means));
			for (int i = 0; i < weights.length; i++) {
				if (weights[i][i] == 0) {
					inversedWeights[i][i] = 0;
				} else {
					inversedWeights[i][i] = 1.0 / weights[i][i];
					// inversedWeights[i][i] = 1.0/ means[i];// weights[i][i];
				}
				// System.out.println(weights[i][i] +"\t"+
				// inversedWeights[i][i]);
				// weights[i][i] /= means[i]; //effectively weight=1?
				// weights[i][i] = 1;
				// inversedWeights[i][i] = 1;
			}

			System.out.println("=mean\t" + Arrays.toString(means));
			System.out.println("=weights=0\t" + Arrays.toString(weights[0]));
			System.out.println("=weights=1\t" + Arrays.toString(weights[1]));
			// System.out.println(Arrays.toString(weightMatrix.getColumnPackedCopy()));
			linearPredictorMatrix = covariateMatrix.transpose().times(
					coefficientMatrix);
			linearPredictorMatrixBig = covariateMatrixBig.transpose().multiply(
					coefficientMatrixBig);

			zMatrix = linearPredictorMatrix.plus(new Matrix(inversedWeights)
					.times(responseMatrix
							.minus(new Matrix(means, means.length))));

			inverseWeightsMatrixBig = createBlockFieldMatrix(inversedWeights);
			meansMatrixBig = createBlockFieldMatrix(meansBig);
			zMatrixBig = linearPredictorMatrixBig.add(inverseWeightsMatrixBig
					.multiply(responseMatrixBig).subtract(meansMatrixBig));

			xwxMatrix = covariateMatrix.times(weightMatrix).times(
					covariateMatrix.transpose());
			xwxMatrixBig = covariateMatrixBig.multiply(weightMatrixBig)
					.multiply(covariateMatrixBig.transpose());

			if (Math.abs(xwxMatrix.det()) <= 1e-8) {
				xwx = xwxMatrix.getArray();
				for (int i = 0; i < xwx.length; i++) {
					xwx[i][i] = xwx[i][i] + 0.1;
				}
				xwxMatrix = new Matrix(xwx);
			}
			updatedCoefficientMatrix = xwxMatrix.inverse()
					.times(covariateMatrix.times(weightMatrix)).times(zMatrix);
			// updatedCoefficientMatrixBig = xwxMatrixBig
			// MatrixUtils.
			System.out.println("=pred=\t"
					+ Arrays.toString(linearPredictorMatrix
							.getColumnPackedCopy()));
			System.out.println("=zMat=\t"
					+ Arrays.toString(zMatrix.getColumnPackedCopy()));
			System.out.println("=xwx =\t"
					+ Arrays.toString(xwxMatrix.getColumnPackedCopy()));
			System.out.println("=coef=\t"
					+ Arrays.toString(updatedCoefficientMatrix
							.getColumnPackedCopy()));
			coefficients = updatedCoefficientMatrix.getColumnPackedCopy();

			error = Math.pow(updatedCoefficientMatrix.minus(coefficientMatrix)
					.normF(), 2.0);
		}

		return coefficients;
	}

	private static FieldMatrix<BigReal> createBlockFieldMatrix(
			BigReal[] meansBig) {
		BigReal[][] bigData = new BigReal[meansBig.length][1];
		for (int i = 0; i < meansBig.length; i++) {
			bigData[i][1] = meansBig[i];
		}
		BlockFieldMatrix<BigReal> m = new BlockFieldMatrix<BigReal>(bigData);
		return null;
	}

	private static BlockFieldMatrix<BigReal> createBlockFieldMatrix(int val,
			int length) {
		double[] data = new double[length];
		Arrays.fill(data, val);

		return createBlockFieldMatrix(data);
	}

	private static BlockFieldMatrix<BigReal> createBlockFieldMatrix(
			double[][] data) {

		BigReal[][] bigData = new BigReal[data.length][data.length];
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				bigData[i][j] = new BigReal(data[i][j]);
			}
		}
		BlockFieldMatrix<BigReal> m = new BlockFieldMatrix<BigReal>(bigData);
		return m;

	}

	private static BlockFieldMatrix<BigReal> createBlockFieldMatrix(
			double[] data) {

		BigReal[][] bigData = new BigReal[data.length][1];
		for (int i = 0; i < data.length; i++) {
			bigData[i][1] = new BigReal(data[i]);
		}
		BlockFieldMatrix<BigReal> m = new BlockFieldMatrix<BigReal>(bigData);
		return m;

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
	 * @exception IllegalArgumentException
	 *                wrong input link function.
	 */

	protected BigReal[] meansBig(double[] coefficients, double[]... covariate) {
		linearPredictors = linearPredictors(covariate, coefficients);
		BigReal[] meansBig = new BigReal[linearPredictors.length];

		for (int i = 0; i < means.length; i++) {
			means[i] = Math.exp(linearPredictors[i]);
			if (means[i] == Double.POSITIVE_INFINITY) {
				BigDecimal temp = new BigDecimal(linearPredictors[i]);
				meansBig[i] = new BigReal(BigDecimalMath.exp(temp));

			}
		}

		return meansBig;
	}

	protected double[] meansLog(double[] coefficients, double[]... covariate) {
		linearPredictors = linearPredictors(covariate, coefficients);
		means = new double[linearPredictors.length];
		for (int i = 0; i < means.length; i++) {
			means[i] = Math.exp(linearPredictors[i]);
		}

		return means;
	}

	protected abstract double[][] weights(double[] coefficients,
			double[]... covariate);

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

}
