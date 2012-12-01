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

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractConvergeStat implements ConvergeStat {

	private static final double PROGRESS_MIN = 0;
	
	protected HashMap<String, Double> convergeStat = new HashMap<String, Double>();
	// convergeStat == Double.NaN. Can't calculate stat
	// convergeStat == Double.NEGATIVE_INFINITY. Should not calculate, i.e. all values are identical, no operators
	
	protected HashMap<String, double[]> traceValues;
	protected HashMap<String, Boolean> hasConverged = new HashMap<String, Boolean>();
	protected HashMap<String, LinkedList<Double>> progressRecord; //TODO(SW) for more complicated calculation, it's not used for every stat yet

//	static protected String[] testVariableName; // TODO(SW) actually can set this to static, should be all the same for all ConvergeStat
	
	protected boolean debug = false;
	protected boolean haveAllConverged = true;
	protected double overallProgress;

	protected int traceValuesLength;
	
	public AbstractConvergeStat(String statisticName, String shortName) {
		this.statisticName = statisticName;
		this.shortName = shortName;
	}
	private void checkConverged(){

		haveAllConverged = true;
		for (String key : convergeStat.keySet()) {
			double stat = convergeStat.get(key);
			boolean isConverged = checkEachConverged(stat, key);
			hasConverged.put(key, isConverged);
			if(!isConverged){
				haveAllConverged = false;
			}

		}
//		haveAllConverged = hac;

		
	}
	

	private void calculateOverallProgress(){
		
		overallProgress = Double.MAX_VALUE;
		for (String key : convergeStat.keySet()) {
			Double stat = convergeStat.get(key);
			if ( isStatValid(stat) ) {
				LinkedList<Double> record = progressRecord.get(key);
				double progress = calculateEachProgress(stat, record);
				overallProgress = Math.min(overallProgress, progress);
			}		
		}
		if (overallProgress == Double.MAX_VALUE) {
			overallProgress = Double.NaN;
		}
		if (overallProgress > 1) {
			overallProgress = 1;
		}
		
	}
	@Override
	public void calculateStatistic() {
	
		for (String key : convergeStat.keySet()) {
			final double stat = calculateEachStat(key);
			convergeStat.put(key, stat);
		}
		checkConverged();
		calculateOverallProgress();
		
	}
	@Override
	public void setTestVariableName(String[] testVariableName) {
		if (testVariableName == null) {
			System.err.println("testVariable are not set: testVariableName == null");
			System.exit(-1);
		}
		this.testVariableName = testVariableName;
		progressRecord = new HashMap<String, LinkedList<Double>>();
		for (String name : testVariableName) {
			progressRecord.put(name, new LinkedList<Double>());
			convergeStat.put(name, 0.0);
		}
		//
//		for (String key : convergeStat.keySet()) {
//			convergeStat.put(key, Double.NEGATIVE_INFINITY);
//			hasConverged.put(key, false);
//		}
//		haveAllConverged = false;
//		overallProgress = 0;
		
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(statisticName + "\n");

		for (String key : convergeStat.keySet()) {
			sb.append(key).append("\t").append(convergeStat.get(key))
					.append("\t").append(hasConverged.get(key)).append("\n");
		}

		sb.append("progress:\t").append(overallProgress).append("\n");
		sb.append("all diag converged?: ").append(haveAllConverged)
				.append("\n");
		return sb.toString();
	}

	@Override
	public void updateValues(HashMap<String, double[]> traceValues) {
		this.traceValues = traceValues;
		this.traceValuesLength = traceValues.get(testVariableName[0]).length;
	}

	@Override
	public boolean haveAllConverged() {
//		checkConverged();
		return haveAllConverged;
	}

	@Override
	public String notConvergedSummary() {
		StringBuilder sb = new StringBuilder("Convergence in these variables has not yet reached according to " + statisticName + ": ");
//				+ "\nThe following variables might not converged yet\n");
	
		for (String key : convergeStat.keySet()) {
			if (!hasConverged.get(key)) {
                double value = (double)Math.round(convergeStat.get(key) * 1000) / 1000; // Rounds to three decimal places
				sb.append(key).append("(").append(value).append(")\t");
//						.append("\n");
			}
		}
        sb.append("\n");
		return sb.toString();
	}

	@Override
	public double[] getAllStat() {
		
		double[] statDouble = new double[testVariableName.length];
		for (int i = 0; i < testVariableName.length; i++) {
			statDouble[i] = convergeStat.get(testVariableName[i]);
		}
		return statDouble;
	
	}

	@Override
	public double getProgress() {
		return overallProgress;
	}

	@Override
	public double getStat(String name) {
		return convergeStat.get(name);
	}

	@Override
	public String getStatisticName() {
		return statisticName;
	}

	@Override
	public String[] getTestVariableNames() {
		return testVariableName;
	}

	private static boolean isStatValid(double stat) {
		
//		boolean isValid = (Double.isNaN(stat) == Double.isInfinite(stat));
		boolean isValid = !Double.isInfinite(stat);
		return isValid;
	}
	
	protected abstract double calculateEachStat(String key);
	protected abstract double calculateEachProgress(Double stat, Deque<Double> record);
	protected abstract boolean checkEachConverged(double stat, String key);


	private String[] testVariableName; // TODO(SW) actually can set this to static, only the one we need to test,
	private String statisticName;
	private String shortName;
	
	

}
