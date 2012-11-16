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

import java.util.HashMap;
import java.util.LinkedList;

public abstract class AbstractConvergeStat implements ConvergeStat {

	protected HashMap<String, Double> convergeStat = new HashMap<String, Double>();
	protected HashMap<String, double[]> traceValues;
	protected HashMap<String, Boolean> hasConverged = new HashMap<String, Boolean>();
//	static protected String[] testVariableName; // TODO(SW) actually can set this to static, should be all the same for all ConvergeStat
	protected String[] testVariableName; // TODO(SW) actually can set this to static, only the one we need to test,
//	protected HashMap<String, double[]> progressRecord; //TODO(SW) for more complicated calculation, it's not used for every stat yet
	protected HashMap<String, LinkedList<Double>> progressRecord; //TODO(SW) for more complicated calculation, it's not used for every stat yet
	
	
	protected boolean haveAllConverged = true;
	protected String statisticName;
	protected String shortName;
	protected boolean debug;
	protected double progress;
	
	protected abstract void checkConverged();
	protected abstract void calculateProgress();
	
	public AbstractConvergeStat(String statisticName, String shortName) {
		this.statisticName = statisticName;
		this.shortName = shortName;
	}

	protected void checkTestVariableName() {
		if (testVariableName == null) {
			System.err.println("testVariable are not set yet");
			System.exit(-1);
		}

	}

	@Override
	public void setTestVariableName(String[] testVariableName) {
		this.testVariableName = testVariableName;
//		progressRecord = new HashMap<String, double[]>();
		progressRecord = new HashMap<String, LinkedList<Double>>();
		for (String name : testVariableName) {
			progressRecord.put(name, new LinkedList<Double>());
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(statisticName + "\n");

		for (String key : testVariableName) {
			sb.append(key).append("\t").append(convergeStat.get(key))
					.append("\t").append(hasConverged.get(key)).append("\n");
		}
		sb.append("progress:\t").append(progress).append(" %\n");
		sb.append("all diag converged?: ").append(haveAllConverged)
				.append("\n");
		return sb.toString();
	}

	@Override
	public void updateValues(HashMap<String, double[]> traceValues) {
		this.traceValues = traceValues;

	}

	@Override
	public boolean haveAllConverged() {
//		checkConverged();
		return haveAllConverged;
	}

	@Override
	public String notConvergedSummary() {
		StringBuilder sb = new StringBuilder(statisticName
				+ "\nThe following variables might not converged yet\n");
	
		for (String key : testVariableName) {
			if (!hasConverged.get(key)) {
				sb.append(key).append("\t").append(convergeStat.get(key))
						.append("\n");
			}
		}
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
		return progress;
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

	

}
