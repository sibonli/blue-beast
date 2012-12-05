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

import dr.inference.trace.TraceCorrelation;
import dr.inference.trace.TraceFactory;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class ESSConvergeStat extends AbstractConvergeStat {

	// public static final ESSConvergeStat INSTANCE = new ESSConvergeStat();
	public static final Class<? extends ConvergeStat> THIS_CLASS = ESSConvergeStat.class;
	public static final String STATISTIC_NAME = "Effective Sample Size";
	public static final String SHORT_NAME = "ESS";

	private static final TraceFactory.TraceType TRACETYPE = TraceFactory.TraceType.INTEGER;

	private int stepSize;
	private double essThreshold;

	public ESSConvergeStat() {
		super(STATISTIC_NAME, SHORT_NAME);
		setupDefaultParameterValues();
	}

	public ESSConvergeStat(int stepSize, int essThreshold) {
		// TODO(SW) think about whether we want empty constructor?
		// keep it for now because we used it quite a bit is the
		// progressReporter
		// this();
		super(STATISTIC_NAME, SHORT_NAME);
		if (stepSize < 1) {
			throw new RuntimeException(
					"Error in BLUE-BEAST. stepSize for ESS convergence statistic cannot be less than 1");
		}
		this.stepSize = stepSize;
		this.essThreshold = essThreshold;

	}

	private void setupDefaultParameterValues() {
		this.stepSize = 1;
		this.essThreshold = 200.0;

	}



	@Override
	protected boolean checkEachConverged(double stat, String key) {
		
		boolean isConverged = true;

		if (Double.isNaN(stat)) {
			System.err.println(STATISTIC_NAME + " could not be calculated for variable with id " + key +
					"("	+ Double.NaN + "). Check log file for details. ");
		} else if (stat < essThreshold && !Double.isInfinite(stat) ) {// || Double.isNaN(stat)) {
			isConverged = false;
		} 

		return isConverged;

	}

	@Override
	protected double calculateEachProgress(Double stat, Deque<Double> record) {
		
		if(!Double.isNaN(stat)){
			if(record.size() > 0 ){
				record.pop();
			}
			record.add(stat);
		}
		stat = record.peekFirst();
		double progress = stat / essThreshold;
		return progress;
	}

	@Override
	protected double calculateEachStat(String key){
	
		List<Double> listDouble = Arrays.asList(ArrayUtils.toObject(traceValues.get(key)));
	
		TraceCorrelation<Double> traceCorrelation = new TraceCorrelation<Double>(
				listDouble, TRACETYPE, stepSize);
		double stat = traceCorrelation.getESS();
	
		if(Double.isNaN(stat)){ //Use two separate if to handle other NaN cases later
			if (traceCorrelation.getVariance()==0){
				stat = Double.NEGATIVE_INFINITY;
				System.err.println(STATISTIC_NAME + " could not be calculated for variable with id " + key +
						". This is due to logged values being unchanged during the run");//. Check log file for details. ");
			}
		}
		return stat;
	
	}

	public double getESSLowerLimitBoundary() {
		return essThreshold;
	}

	public int getStepSize() {
		return stepSize;
	}

}
