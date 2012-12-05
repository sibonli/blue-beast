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


import dr.stats.DiscreteStatistics;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.Arrays;
import java.util.Deque;

public class GewekeConvergeStat extends AbstractConvergeStat{

	public static final Class<? extends ConvergeStat> THIS_CLASS = GewekeConvergeStat.class;
	public static final String STATISTIC_NAME = "Geweke's convergence diagnostic";
	public static final String SHORT_NAME = "Geweke"; // geweke.diag in R
	public static final NormalDistribution nd = new NormalDistribution();

	private double frac1; // default 0.1
	private double frac2; // default 0.5
	private double gewekeThreshold = 1.96;
	private double gewekeProgressThreshold;

    public GewekeConvergeStat() {
    	super(STATISTIC_NAME, SHORT_NAME);
    	setupDefaultParameterValue();
    }

    
	public GewekeConvergeStat(double frac1, double frac2, double gewekeThreshold) {
		//TODO(SW) think about whether we want empty constructor? 
		//keep it for now because we used it quite a bit is the progressReporter
		//this();
		super(STATISTIC_NAME, SHORT_NAME);
		this.frac1 = frac1;
		this.frac2 = frac2;
		setGewekeThreshold(gewekeThreshold);
		
		
    }
	private void setGewekeThreshold(double gewekeThreshold) {
		this.gewekeThreshold = gewekeThreshold;
		gewekeProgressThreshold = 1-nd.cumulativeProbability(this.gewekeThreshold);
		
	}


	private void setupDefaultParameterValue(){
		frac1 = 0.1;
		frac2 = 0.5;
		
	}



	@Override
	protected boolean checkEachConverged(double stat, String key) {

		boolean isConverged = true;

		if (Double.isNaN(stat)) {
			System.err.println(STATISTIC_NAME + " could not be calculated for variable with id " + key +
					"("	+ Double.NaN + "). Geweke algorithm might not converged. Check log file for details. ");
		} else if (Math.abs(stat) > gewekeThreshold && !Double.isInfinite(stat) ) {
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
	
//		final double progress = Math.exp( rafteryThreshold - avgStat );
	
//		return progress;
		
		final double progress = (1-nd.cumulativeProbability(Math.abs(avgStat)))/gewekeProgressThreshold;
//			final double tempP = (1-nd.cumulativeProbability(Math.abs(gewekeStat)-gewekeThreshold))/0.5;
//			R Code
//			data<- seq(1.96,4,by=0.01)
//			plot(data, 1-(pnorm(abs(data))-pnorm(1.96))/0.025, type="l", col=2)
//			plot(data, (1-pnorm(data-1.96))/0.5, type="l", col=2)
         
		return progress;
	}


	@Override
	protected double calculateEachStat(String key){
		
		final double[] t = traceValues.get(key);
		
		final int length = t.length;
    	final int indexStart = (int) Math.floor(length * (1-frac2)) ;
    	final int indexEnd   = (int) Math.ceil(length * frac1);
    	final double[] dStart = Arrays.copyOfRange(t, 0, indexEnd);
		final double[] dEnd = Arrays.copyOfRange(t, indexStart, length);
		final double meanStart = DiscreteStatistics.mean(dStart);
		final double meanEnd = DiscreteStatistics.mean(dEnd);
		final double varStart = ConvergeStatUtils.spectrum0(dStart)/dStart.length;
		final double varEnd = ConvergeStatUtils.spectrum0(dEnd)/dEnd.length;
		final double bothVar = varStart+varEnd;
		
		double stat = (meanStart - meanEnd) / Math.sqrt(bothVar);
		

		if(Double.isNaN(stat)){ //Use two separate if to handle other NaN cases later
			if (Double.isNaN(bothVar)){
				stat = Double.NEGATIVE_INFINITY;
				System.err.println(STATISTIC_NAME + " could not be calculated for variable with id " + key +
						". This is due to logged values being unchanged during the run");//. Check log file for details. ");
			}
		}
		return stat;
		
	}

	
}
