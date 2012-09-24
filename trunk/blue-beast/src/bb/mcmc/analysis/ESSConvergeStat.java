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
import java.util.Set;

import dr.inference.trace.TraceCorrelation;
import dr.inference.trace.TraceFactory;


public class ESSConvergeStat extends AbstractConvergeStat{

	public static final ESSConvergeStat INSTANCE = new ESSConvergeStat();
	private static final TraceFactory.TraceType TRACETYPE = TraceFactory.TraceType.INTEGER;
	
	private int stepSize;
    private int essLowerLimitBoundary;
    private double burninPercentage;
	
	

	/*
	 * should initialize this class first, then just updating it;
	 */
	public ESSConvergeStat() {
		STATISTIC_NAME = "Effective Sample Size";
	}

	public ESSConvergeStat(int stepSize, String[] varNames, double burninPercentage, int essLowerLimitBoundary) {
        this();
		this.stepSize = stepSize;
		variableNames = varNames; // each stat can calculate different variable set
        this.burninPercentage = burninPercentage;
        this.essLowerLimitBoundary = essLowerLimitBoundary;

		convergeStat = new HashMap<String, Double>();
		for (String s : variableNames) {
			convergeStat.put(s, 0.0);
		}
		
		
	}


	@Override
	public void calculateStatistic() {
       
        final int totalLength = traceInfo.get(variableNames[0]).size();
        final int burnin = (int) Math.round(totalLength * burninPercentage);
        
        for (String s : variableNames) {
            final List<Double> l = getSubList(traceInfo.get(s), burnin, totalLength);
//            List<Double> l = getSubList(traceInfo.get(s), 0, totalLength); /* For no burnin */
//            System.out.print(s + "\t");
            //System.out.print(s + "\t" + traceInfo.get(s));
			final TraceCorrelation traceCorrelation = new TraceCorrelation(l, TRACETYPE, stepSize);
			convergeStat.put(s, traceCorrelation.getESS() );
            //System.out.println("ESS: " + traceCorrelation.getESS());
		}
        System.out.println();

	}





//		@Override
	public double[] getAllStat() {
        final double[] statDouble = new double[convergeStat.size()];
        final ArrayList<Double> statValues = new ArrayList<Double>(convergeStat.values());
        int i=0;
        for(final Double d : statValues) {
            statDouble[i] = d;
            i++;
        }
        return statDouble;

	}


    @Override
	public boolean hasConverged() {
        boolean converged = true;
        //double[] stats = getAllStat();
        //for(int i=0; i<stats.length; i++) {
        for(final Double d : convergeStat.values()) {
            if(d<essLowerLimitBoundary) {
                converged = false;
            }
        }
        return converged;
    }


    public int getESSLowerLimitBoundary() {
        return essLowerLimitBoundary;
    }

	@Override
	public String notConvergedSummary() {
		// TODO Auto-generated method stub
		return null;
	}



}
