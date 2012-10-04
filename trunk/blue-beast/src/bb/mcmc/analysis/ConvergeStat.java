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

public interface ConvergeStat {

	

	
	//Implemented in the abstract class only
	public void updateValues(HashMap<String, double[]> values);
	
	public double getStat(String varName);

	public double[] getAllStat();

	public boolean haveAllConverged();
	
	public String getStatisticName();

	public void setTestVariableName(String[] testVariableName);

	public String[] getTestVariableNames();
	
	//In each individual classes
	public void checkConverged();
	
	
	public void calculateStatistic();
	
	@Override
	public String toString();
	
	public String notConvergedSummary();
	
	
	
	// TODO: remove these methods later
	@Deprecated
	public void updateTrace(HashMap<String, ArrayList<Double>> traceInfo);
	@Deprecated
	public String[] getVariableNames();

}
