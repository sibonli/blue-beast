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
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import dr.inference.trace.TraceCorrelation;
import dr.inference.trace.TraceFactory;

public class ESSConvergeStat extends AbstractConvergeStat {

	public static final ESSConvergeStat INSTANCE = new ESSConvergeStat();
	public static final Class<? extends ConvergeStat> thisClass = ESSConvergeStat.class;
	public static final String STATISTIC_NAME = "Effective Sample Size";
	private static final TraceFactory.TraceType TRACETYPE = TraceFactory.TraceType.INTEGER;
	
	private int stepSize;
	private int essLowerLimitBoundary;

	

	/*
	 * should initialize this class first, then just updating it;
	 */
	public ESSConvergeStat() {
		statisticName = STATISTIC_NAME;
		
	}
	
	public ESSConvergeStat(String[] testVariableName){
		this();
		setTestVariableName(testVariableName);
		setupDefaultParameterValue();
	}
	
	public ESSConvergeStat(String[] testVariableName, int stepSize, int essLowerLimitBoundary) {
		this();
		setTestVariableName(testVariableName);
		this.stepSize = stepSize;
		this.essLowerLimitBoundary = essLowerLimitBoundary;

	}
	private void setupDefaultParameterValue(){
		this.stepSize = 1;
		this.essLowerLimitBoundary = 100;
		
	}

	public int getESSLowerLimitBoundary() {
		return essLowerLimitBoundary;
	}

	@Override
	public void calculateStatistic() {
		
		checkTestVariableName();
		for (String key : testVariableName) {
			System.out.println("Calculating "+STATISTIC_NAME+": "+key);
			final double ess = calculateESS(key);
			convergeStat.put(key, ess);

		}
	}

	private double calculateESS(String key) {

		List<Double> l = Arrays
				.asList(ArrayUtils.toObject(traceValues.get(key)));
		TraceCorrelation<Double> traceCorrelation = new TraceCorrelation<Double>(
				l, TRACETYPE, stepSize);
		double ess = traceCorrelation.getESS();
		return ess;

	}

	@Override
	public void checkConverged() {

		for (String key : convergeStat.keySet() ) {
			if (convergeStat.get(key) < essLowerLimitBoundary) {
				hasConverged.put(key, false);
				haveAllConverged = false;
			}
			else {
				hasConverged.put(key, true);
			}
				
		}
		
	}


}
