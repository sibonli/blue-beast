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

//	public static final ESSConvergeStat INSTANCE = new ESSConvergeStat();
	public static final Class<? extends ConvergeStat> thisClass = ESSConvergeStat.class;
	public static final String STATISTIC_NAME = "Effective Sample Size";
	public static final String SHORT_NAME = "ESS";
	
	private static final TraceFactory.TraceType TRACETYPE = TraceFactory.TraceType.INTEGER;
	
	private int stepSize;
	private int essThreshold;


	public ESSConvergeStat() {
		super(STATISTIC_NAME, SHORT_NAME);
        setupDefaultParameterValues();
	}

	public ESSConvergeStat(int stepSize, int essThreshold) {
		//TODO(SW) think about whether we want empty constructor? 
		//keep it for now because we used it quite a bit is the progressReporter
		//this();
		super(STATISTIC_NAME, SHORT_NAME);
        if(stepSize < 1) {
            throw new RuntimeException("Error in BLUE-BEAST. stepSize for ESS convergence statistic cannot be less than 1");
        }
		this.stepSize = stepSize;
		this.essThreshold = essThreshold;

	}
	
	private void setupDefaultParameterValues(){
		this.stepSize = 1;
		this.essThreshold = 200;
		
	}

	
	@Override
	public void calculateStatistic() {
		
		for (String key : testVariableName) {
			final double ess = calculateESS(key);
			convergeStat.put(key, ess);
		}
		checkConverged();
		calculateProgress();
	}

	@Override
	protected void checkConverged() {
	    boolean hac = true;
		for (String key : convergeStat.keySet() ) {
			Double stat = convergeStat.get(key);
			if (stat < essThreshold) {//  || Double.isNaN(stat)) {
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
	protected void calculateProgress() {
		progress = 1;
		for (int i = 0; i < testVariableName.length; i++) {
			final double currentProgress  = convergeStat.get(testVariableName[i]) / essThreshold;
            if(!Double.isNaN(currentProgress)) {
			    progress = Math.min(progress, currentProgress);
            }
		}
		
		if(progress > 1){
			progress = 1;
		}

	}

	private double calculateESS(String key) {

		List<Double> l = Arrays
				.asList(ArrayUtils.toObject(traceValues.get(key)));
		
		TraceCorrelation<Double> traceCorrelation = new TraceCorrelation<Double>(
				l, TRACETYPE, stepSize);
		final double ess = traceCorrelation.getESS();
		return ess;

	}

	public int getESSLowerLimitBoundary() {
		return essThreshold;
	}

	public int getStepSize() {
		return stepSize;
	}

	


}
