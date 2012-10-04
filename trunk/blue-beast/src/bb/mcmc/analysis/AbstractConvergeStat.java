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

import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbstractConvergeStat implements ConvergeStat {

	protected HashMap<String, Double> convergeStat = new HashMap<String, Double>();
	protected HashMap<String, double[]> traceValues;
	protected HashMap<String, Boolean> hasConverged = new HashMap<String, Boolean>();
	protected String[] testVariableName; // only the one we need to test
	protected boolean haveAllConverged = true;
	protected String statisticName;
	protected boolean debug;
	
	protected void checkTestVariableName() {
		if (testVariableName == null) {
			System.err.println("testVariable are not set yet");
			System.exit(-1);
		}

	}

	@Override
	public void setTestVariableName(String[] testVariableName) {
		this.testVariableName = testVariableName;
	}

	@Override
	public String toString() {
		checkConverged();
		StringBuilder sb = new StringBuilder(statisticName + "\n");

		for (String key : testVariableName) {
			sb.append(key).append("\t").append(convergeStat.get(key))
					.append("\t").append(hasConverged.get(key)).append("\n");
		}
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
		checkConverged();
		return haveAllConverged;
	}

	@Override
	public String notConvergedSummary() {
		StringBuilder sb = new StringBuilder(statisticName
				+ "\nThe following variables might not converged yet\n");
	
		for (String key : hasConverged.keySet()) {
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
