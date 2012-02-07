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
import java.util.List;


public abstract class AbstractConvergeStat implements ConvergeStat{

	protected HashMap<String, ArrayList<Double>> traceInfo;
	protected String[] variableNames;
    public String STATISTIC_NAME;
	
	
	public void updateTrace (HashMap<String, ArrayList<Double>> traceInfo) {
		this.traceInfo = traceInfo;
	}

    public String getStatisticName() {
        return STATISTIC_NAME;
    }
	
    public String[] getVariableNames() {
        return variableNames;
    }
    

	public <T> List<T> getSubList(ArrayList<T> list, int burnin,
			int totalLength) {
		List<T> subList= list.subList(burnin,totalLength);
		return subList;
	}


	
}
