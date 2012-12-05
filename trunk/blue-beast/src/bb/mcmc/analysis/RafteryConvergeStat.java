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


import dr.math.distributions.NormalDistribution;
import dr.stats.DiscreteStatistics;

import java.util.Deque;

public class RafteryConvergeStat extends AbstractConvergeStat {

	public static final Class<? extends ConvergeStat> THIS_CLASS = RafteryConvergeStat.class;
	public static final String STATISTIC_NAME = "Raftery and Lewis's diagnostic";
	public static final String SHORT_NAME = "Raftery"; // raftery.diag in R

	private double quantile;
//	private double error;
	private double errorSquare;
	private double probs;
	private double convergeEps;
	private double z;
//	private double phi;
	private double phiSquare;
	private int nmin;

	private double rafteryThreshold = 5;

	public RafteryConvergeStat() {
		super(STATISTIC_NAME, SHORT_NAME);
		setupDefaultParameterValue();
	}


	public RafteryConvergeStat(double quantile, double error, double prob,
			double convergeEps, double rafteryThreshold) {
		super(STATISTIC_NAME, SHORT_NAME);
		setParameters(quantile, error, prob, convergeEps, rafteryThreshold);
	}

	private void setupDefaultParameterValue() {
		setParameters(0.025, 0.005, 0.95, 0.001, 5);
	}


	private void setParameters(double quantile, double error, double prob, double convergeEps, double rafteryThreshold){
		this.quantile = quantile;
		setError(error);
		this.probs = prob;
		this.convergeEps = convergeEps;
		this.rafteryThreshold = rafteryThreshold;

		z = 0.5 * (1 + probs);
		setPhi( NormalDistribution.quantile(z, 0, 1) );
		
		nmin = (int) (Math.ceil((quantile * (1 - quantile) * phiSquare) / (errorSquare))); // 3746, niter>3746

	}

	@Override
	public void calculateStatistic() {

		if (traceValuesLength < nmin) {
			System.err.println("Warning: " + STATISTIC_NAME + " requires more samples before it can be calculated (current: " + traceValuesLength + ", required: " + nmin + ")");
//                    "Warning: Number of samples " + NIte + " is less than the nmin of " + nmin +"\n\t"+ STATISTIC_NAME +" cannot be calculated until a sufficient number of samples is available");
			for (String key : convergeStat.keySet()) {
				convergeStat.put(key, Double.NEGATIVE_INFINITY);
				hasConverged.put(key, false);
			}
			haveAllConverged = false;
			overallProgress = Double.NaN;
		}
		
		else{
			super.calculateStatistic();
		}
		
	}


	@Override
	protected boolean checkEachConverged(double stat, String key) {

		boolean isConverged = true;

		if (Double.isNaN(stat)) {
			System.err.println(STATISTIC_NAME + " could not be calculated for variable with id " + key +
					"("	+ Double.NaN + "). Check log file for details. ");
		} else if (stat > rafteryThreshold && !Double.isInfinite(stat) ) {
			isConverged = false;
		} 
		return isConverged;
		
		
	}


	@Override
	protected double calculateEachProgress(Double stat, Deque<Double> record) {
	
		if(!Double.isNaN(stat)){
			if(record.size() > 2 ){
				record.pop();
			}
			record.add(stat);
		}
		double avgStat = 0;
		for (double d : record) {
			avgStat+= d;
		}
		avgStat /= record.size();
	
		final double progress = Math.exp( rafteryThreshold - avgStat );
	
		return progress;
	}

	@Override
	protected double calculateEachStat(String key){

		final double[] x = traceValues.get(key);

		final int thin = 1;
		
		final double quant = ConvergeStatUtils.quantileType7InR(x, quantile);
		final boolean[] dichot = whichLessQuant(x, quant);

		boolean[] testres = new boolean[0];
		int newDim = testres.length;
		int kthin = 0;
		double bic = 1;
		while (bic >= 0) {
			kthin += thin;
			testres = ConvergeStatUtils.thinWindow(dichot, kthin);
			newDim = testres.length;
			final int[][][] testtran = ConvergeStatUtils
					.create3WaysContingencyTable(testres, newDim);
			final double g2 = tripleForLoop(testtran);
			bic = g2 - Math.log(newDim - 2) * 2.0;

		}

		final int[][] finaltran = ConvergeStatUtils
				.create2WaysContingencyTable(testres, newDim);
		final double alpha = ((double) finaltran[0][1])/(finaltran[0][0] + finaltran[0][1]);
        final double beta =  ((double) finaltran[1][0])/(finaltran[1][0] + finaltran[1][1]);
        final double alphaPlusBeta = alpha + beta;
        
		final double tempburn = Math.log( (convergeEps * alphaPlusBeta) / Math.max(alpha, beta) )
				/ (Math.log(Math.abs(1 - alphaPlusBeta)));
		final double nburn = Math.ceil(tempburn) * kthin;
		
		final double tempprec = ((2 - alphaPlusBeta) * alpha * beta * phiSquare)
				/ (Math.pow(alphaPlusBeta, 3) * errorSquare);
	    final double nkeep = Math.ceil(tempprec) * kthin;
	    
		double stat = (nburn + nkeep)/nmin;
		if(debug == true){
			System.err.println(nburn +"\t"+ (nkeep) +"\t"+ nmin +"\t"+ stat);
		}
		
		if(Double.isNaN(stat)){ //Use two separate if to handle other NaN cases later
			if (DiscreteStatistics.variance(x)==0){
				stat = Double.NEGATIVE_INFINITY;
				System.err.println(STATISTIC_NAME + " could not be calculated for " + key + 
						". This is due to logged values being unchanged during the run.");// Check log file for details. ");
			}
		}
		return stat;
	}

	private static boolean[] whichLessQuant(double[] x, double quant) {

		final boolean[] indicator = new boolean[x.length];
		for (int i = 0; i < x.length; i++) {
			if (x[i] < quant) {
				indicator[i] = true;
			}
		}
		return indicator;
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

    private void setPhi(double phi) {
	//		this.phi = phi;
			phiSquare = phi*phi;
			
		}


	private void setError(double error) {
	//		this.error = error;
			errorSquare = error * error;
			
		}


	public int getNMin() {
        return nmin;
    }
}
