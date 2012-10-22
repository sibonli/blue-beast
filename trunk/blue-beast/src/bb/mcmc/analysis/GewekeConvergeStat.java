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

import dr.stats.DiscreteStatistics;

public class GewekeConvergeStat extends AbstractConvergeStat{

	public static final Class<? extends ConvergeStat> thisClass = GewekeConvergeStat.class;
	public static final String STATISTIC_NAME = "Geweke's convergence diagnostic";
	public static final String SHORT_NAME = "Geweke"; // geweke.diag in R
	

	private double frac1; // default 0.1
	private double frac2; // default 0.5
	private double gewekeThreshold = 1.96;


    public GewekeConvergeStat() {
    	super(STATISTIC_NAME, SHORT_NAME);
    }

    
	public GewekeConvergeStat(double frac1, double frac2, double gewekeThreshold) {
		//TODO(SW) think about whether we want empty constructor? 
		//keep it for now because we used it quite a bit is the progressReporter
		//this();
		super(STATISTIC_NAME, SHORT_NAME);
		this.frac1 = frac1;
		this.frac2 = frac2;
		this.gewekeThreshold = gewekeThreshold;

    }
	private void setupDefaultParameterValue(){
		frac1 = 0.1;
		frac2 = 0.5;
		
	}

	@Override
	protected void checkConverged() {
	    boolean hac = true;
		for (String key : convergeStat.keySet()) {
			if (Math.abs(convergeStat.get(key)) > gewekeThreshold ) {
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
	 	for (String key : testVariableName) {
			System.out.println("Calculating "+STATISTIC_NAME+": "+key);

			final double gewekeStat = calculateGewekeStat(key);
			convergeStat.put(key, gewekeStat );
			
		}
    }
	private double calculateGewekeStat(String key){
		
		final double[] t = traceValues.get(key);
		final int length = t.length;
    	final int indexStart = (int) Math.floor(length * (1-frac2)) ;
    	final int indexEnd   = (int) Math.ceil(length * frac1);
    	
    	final double[] dStart = Arrays.copyOfRange(t, 0, indexEnd);
		final double[] dEnd = Arrays.copyOfRange(t, indexStart, length);
		
		final double meanStart = DiscreteStatistics.mean(dStart);
		final double meanEnd = DiscreteStatistics.mean(dEnd);
		final double varStart = ConvergeStatUtils.spectrum0(dStart)/dStart.length;;
		final double varEnd = ConvergeStatUtils.spectrum0(dEnd)/dEnd.length;

		final double gewekeStat = (meanStart - meanEnd) / Math.sqrt(varStart+varEnd);
		return gewekeStat;
		
	}

	
}
