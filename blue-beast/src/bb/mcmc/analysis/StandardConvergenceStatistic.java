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

public class StandardConvergenceStatistic extends AbstractConvergeStat{

    public static final StandardConvergenceStatistic INSTANCE = new StandardConvergenceStatistic();

    public StandardConvergenceStatistic(Double[] traceInfo) {
        this();
        // TODO
    }

    public StandardConvergenceStatistic() {
        STATISTIC_NAME = "Standard";
    }

    @Override
	public void calculateStatistic() {
        // TODO
    }

    @Override
	public double getStat(String varName) {
        // TODO
        return 50.0; // Temporary value
    }


	public void addTrace(ArrayList<Double> traceInfo) {
		// TODO Auto-generated method stub
		
	}

//	@Override
	@Override
	public double[] getAllStat() {
		// TODO Auto-generated method stub
		return null;
	}


    @Override
	public boolean hasConverged() {
        return false;
    }

	@Override
	public String notConvergedSummary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateVaules(HashMap<String, double[]> values) {
		// TODO Auto-generated method stub
		
	}
}
