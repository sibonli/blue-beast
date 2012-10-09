/**
 *  *  BLUE-BEAST - Bayesian and Likelihood-based methods Usability Extension
 *  Copyright (C) 2011 Wai Lok Sibon Li & Steven H Wu

 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  @author Steven H Wu
 *  @author Wai Lok Sibon Li
 */

package bb.mcmc.analysis;

import java.util.Arrays;

import dr.math.distributions.NormalDistribution;

public class RafteryConvergeStat extends AbstractConvergeStat {

	public static final Class<? extends ConvergeStat> thisClass = RafteryConvergeStat.class;
	public static final String STATISTIC_NAME = "Raftery and Lewis's diagnostic";
	public static final String SHORT_NAME = "Raftery"; // raftery.diag in R

	private double quantile;
	private double error;
	private double probs;
	private double convergeEps;
	private double z;
	private double phi;
	private double nmin;

	private double rafteryThreshold = 5;

	public RafteryConvergeStat() {
		super(STATISTIC_NAME, SHORT_NAME);
	}


	public RafteryConvergeStat(double quantile, double error, double prob,
			double convergeEps, double rafteryThreshold) {
		//TODO(SW) think about whether we want empty constructor?
		//keep it for now because we used it quite a bit is the progressReporter
		//this();
		super(STATISTIC_NAME, SHORT_NAME);
		setParameters(quantile, error, prob, convergeEps, rafteryThreshold);
	}

	private void setupDefaultParameterValue() {
		setParameters(0.025, 0.005, 0.95, 0.001, 5);
	}


	private void setParameters(double quantile, double error, double prob, double convergeEps, double rafteryThreshold){
		this.quantile = quantile;
		this.error = error;
		this.probs = prob;
		this.convergeEps = convergeEps;
		this.rafteryThreshold = rafteryThreshold;

		z = 0.5 * (1 + probs);
		phi = NormalDistribution.quantile(z, 0, 1);
		nmin = Math.ceil((quantile * (1 - quantile) * phi * phi) / (error * error)); // 3746, niter>3746

	}

	@Override
	public void checkConverged() {
        boolean hac = true;
		for (String key : convergeStat.keySet()) {
			if (convergeStat.get(key) > rafteryThreshold) {
				hasConverged.put(key, false);
				hac = false;
			}
			else {
				hasConverged.put(key, true);
			}
		}
        haveAllConverged = hac;
	}

	@Override
	public void calculateStatistic() {
		checkTestVariableName();

		final int NIte = traceValues.get(testVariableName[0]).length;
		final int thin = 1;// TODO, get thinning info

		if (NIte < nmin) {
			System.err.println("Error: No. of iteration " + NIte + " is less than nmin: " + nmin);
			System.exit(-1);
		}

		for (String key : testVariableName) {
			System.out.println("Calculating "+STATISTIC_NAME+": "+key);
			final double iRatio = calculateRaftery(key, thin);
			convergeStat.put(key, iRatio);

		}
	}

	private double calculateRaftery(String key, int thin) {

		final double[] x = traceValues.get(key);
		final double quant = quantileType7InR(x, quantile);
		final boolean[] dichot = caluclateDichot(x, quant);

		boolean[] testres = new boolean[0];
		int newDim = testres.length;
		int kthin = 0;
		double bic = 1;
		while (bic >= 0) {
			kthin += thin;
			testres = ConvergeStatUtils.thinWindow(dichot, kthin);
//			for (int j = 0; j < dichot.length; j++) {
//				if (x[j] < quant) {
//					dichot[j] = true;
//				}
//			}
			newDim = testres.length;
			final int[][][] testtran = create3WaysContingencyTable(testres,
					newDim);
			final double g2 = tripleForLoop(testtran);
			bic = g2 - Math.log(newDim - 2) * 2.0;

		}

        final int[][] finaltran = create2WaysContingencyTable(testres, newDim);
        final double alpha = (double) finaltran[0][1]/(finaltran[0][0] + finaltran[0][1]);
        final double beta =  (double) finaltran[1][0]/(finaltran[1][0] + finaltran[1][1]);

		final double tempburn = Math.log( (convergeEps * (alpha + beta)) / Math.max(alpha, beta) )
				/ (Math.log(Math.abs(1 - alpha - beta)));
		final int nburn = (int) Math.ceil(tempburn) * kthin;
		final double tempprec = ((2 - alpha - beta) * alpha * beta * phi * phi)
				/ (Math.pow((alpha + beta), 3) * error * error);

	    final int nkeep = (int) Math.ceil(tempprec) * kthin;
		final double iRatio = (nburn + nkeep)/nmin;
		if(debug == true){
			System.out.println(nburn +"\t"+ (nkeep+nburn) +"\t"+ nmin +"\t"+ iRatio);
		}
		return iRatio;
	}

	private static boolean[] caluclateDichot(double[] x, double quant) {

		final boolean[] dichot = new boolean[x.length];
		for (int i = 0; i < x.length; i++) {
			if (x[i] < quant) {
				dichot[i] = true;
			}
		}
		return dichot;
	}

	private static double quantileType7InR(double[] x, double q) {

		final double[] tempX = x.clone();
		Arrays.sort(tempX);
		final int n = tempX.length;
		final double index = 1 + (n - 1) * q;
		final double lo = Math.floor(index);
		final double hi = Math.ceil(index);
		Arrays.sort(tempX);
		double qs = tempX[(int) lo - 1];
		final double h = index - lo;
		if (h != 0) {
			qs = (1 - h) * qs + h * tempX[(int) hi - 1];
		}
		return qs;

	}

	private static double tripleForLoop(int[][][] testtran) {
		double g2 = 0;
		for (int i1 = 0; i1 < 2; i1++) {
			for (int i2 = 0; i2 < 2; i2++) {
				for (int i3 = 0; i3 < 2; i3++) {
					if (testtran[i1][i2][i3] != 0) {
						final int t1 = testtran[i1][i2][0] + testtran[i1][i2][1];
						final int t2 = testtran[0][i2][i3] + testtran[1][i2][i3];
						final double td = 0.0
								+ testtran[0][i2][0] + testtran[0][i2][1]
								+ testtran[1][i2][0] + testtran[1][i2][1];
						final double fitted = t1 * t2 / td;
						g2 += testtran[i1][i2][i3]
								* Math.log(testtran[i1][i2][i3] / fitted) * 2.0;
					}
				}
			}
		}
		return g2;
	}

	private static int[][] create2WaysContingencyTable(boolean[] testres,
			int newDim) {

		final boolean[] b1 = Arrays.copyOfRange(testres, 0, newDim - 1);
		final boolean[] b2 = Arrays.copyOfRange(testres, 1, newDim);

		final int table[][] = new int[][] { { 0, 0 },
											{ 0, 0 } };

		for (int i = 0; i < b1.length; i++) {
			if (b1[i] && b2[i]) {
				table[1][1] += 1;
			} else if (!b1[i] && b2[i]) {
				table[0][1] += 1;
			} else if (b1[i] && !b2[i]) {
				table[1][0] += 1;
			} else if (!b1[i] && !b2[i]) {
				table[0][0] += 1;
			}
		}
		return table;

	}

	private static int[][][] create3WaysContingencyTable(boolean[] testres,
			int newDim) {

		final boolean[] b1 = Arrays.copyOfRange(testres, 0, newDim - 2);
		final boolean[] b2 = Arrays.copyOfRange(testres, 1, newDim - 1);
		final boolean[] b3 = Arrays.copyOfRange(testres, 2, newDim);

		final int table[][][] = new int[][][] { { { 0, 0 }, { 0, 0 } },
				{ { 0, 0 }, { 0, 0 } } };

		for (int i = 0; i < b1.length; i++) {
			if (b1[i] && b2[i] && b3[i]) {
				table[1][1][1] += 1;
			} else if (!b1[i] && b2[i] && b3[i]) {
				table[0][1][1] += 1;
			} else if (b1[i] && !b2[i] && b3[i]) {
				table[1][0][1] += 1;
			} else if (b1[i] && b2[i] && !b3[i]) {
				table[1][1][0] += 1;
			} else if (b1[i] && !b2[i] && !b3[i]) {
				table[1][0][0] += 1;
			} else if (!b1[i] && b2[i] && !b3[i]) {
				table[0][1][0] += 1;
			} else if (!b1[i] && !b2[i] && b3[i]) {
				table[0][0][1] += 1;
			} else if (!b1[i] && !b2[i] && !b3[i]) {
				table[0][0][0] += 1;
			}
		}
		// for (final int[][] element : table) {
		// for (int[] element2 : element) {
		// System.out.println(Arrays.toString(element2));
		// }
		// }
		return table;

	}

}
