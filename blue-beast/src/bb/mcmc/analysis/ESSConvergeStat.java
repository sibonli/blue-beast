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

import java.util.*;


import dr.inference.trace.TraceCorrelation;
import dr.inference.trace.TraceFactory;
import dr.inference.trace.TraceFactory.TraceType;
import dr.inference.trace.TraceList;
import dr.stats.DiscreteStatistics;
import dr.util.HeapSort;
import dr.util.NumberFormatter;


public class ESSConvergeStat extends AbstractConvergeStat{

	public static final ESSConvergeStat INSTANCE = new ESSConvergeStat();
	private static final TraceFactory.TraceType TRACETYPE = TraceFactory.TraceType.INTEGER;
//	private static final String STATISTIC_NAME = "Effective Sample Size";
	// TODO TraceFactory.TraceType.INTEGER; OR TraceFactory.TraceType.DOUBLE ?? 
	// stevenhwu think it Double, need to check
	
	
	private int stepSize;
    private int essLowerLimitBoundary;
    private double burninPercentage;
	
	private HashMap<String, Double> stat;

	/*
	 * should initialize this class first, then just updating it;
	 */
	public ESSConvergeStat() {
        STATISTIC_NAME = "Effective Sample Size";
        //TODO must be a way to have "final STATISTIC_NAME" for each class, and still have get() method in AbstractConvergeStat  
	}

	public ESSConvergeStat(int stepSize, String[] varNames, double burninPercentage, int essLowerLimitBoundary) {
        this();
		this.stepSize = stepSize;
		this.variableNames = varNames; // each stat can calculate different variable set
        System.out.println("pong " + variableNames.length);
        this.burninPercentage = burninPercentage;
        this.essLowerLimitBoundary = essLowerLimitBoundary;

		stat = new HashMap<String, Double>();
		for (String s : variableNames) {
			stat.put(s, 0.0);
		}
		
		
	}


	public void calculateStatistic() {
//        Set<String> keys =  traceInfo.keySet();
//        for (String s : keys) {
//            System.out.println("keys " + s);
//        }

        System.out.println("Calculating ESS for statistics: ");
        
        //TODO remove the follow line later, for faster testing purpose only
        // skip using proper BlueBeastLogger, so variables are not initialised properly 
//        int i = 0;
//        variableNames = new String[traceInfo.size()];
//        for (String s : traceInfo.keySet()) {
//			variableNames[i] = s;
//			i++;
//		}
        //
        int totalLength = traceInfo.get(variableNames[0]).size();
        int burnin = (int) Math.round(totalLength * burninPercentage);

        for (String s : variableNames) {
            List<Double> l = getSubList(traceInfo.get(s), burnin, totalLength);
//            List<Double> l = getSubList(traceInfo.get(s), 0, totalLength); /* For no burnin */
            System.out.print(s + "\t");
            //System.out.print(s + "\t" + traceInfo.get(s));
			TraceCorrelation traceCorrelation = new TraceCorrelation(l, TRACETYPE, stepSize);
			stat.put(s, traceCorrelation.getESS() );
            //System.out.println("ESS: " + traceCorrelation.getESS());
		}
        System.out.println();

	}



	private void checkTraceInfo(ArrayList<Double> traceInfo2) {
//		if(this.traceInfo == null){
//			this.traceInfo = traceInfo2;
//		}
//		else{
//			int length = traceInfo.size();
//			traceInfo.addAll(traceInfo2.subList(length, traceInfo2.size()));
//		}
	}

	public double getStat(String name) {
		return stat.get(name);
	}

//    public String[] getStatNames() {
//        return stat.keySet().toArray(new String[stat.size()]);
//    }

	
	public double getStat() {
		// TODO Auto-generated method stub. stevenhwu: do we need this method??
		
		return 0;
	}

//	@Override
	public double[] getAllStat() {
        double[] statDouble = new double[stat.size()];
        ArrayList<Double> statValues = new ArrayList<Double>(stat.values());
        int i=0;
        for(Double d : statValues) {
            statDouble[i] = d;
            i++;
        }
        return statDouble;
        //return ArrayUtils.toPrimitive((new ArrayList<Double>(stat.values())).toArray(new Double[stat.size()]));
		//return null;
	}


    public boolean hasConverged() {
        boolean converged = true;
        //double[] stats = getAllStat();
        //for(int i=0; i<stats.length; i++) {
        for(Double d : stat.values()) {
            if(d<essLowerLimitBoundary) {
                converged = false;
            }
        }
        return converged;
    }


    public int getESSLowerLimitBoundary() {
        return essLowerLimitBoundary;
    }

}
