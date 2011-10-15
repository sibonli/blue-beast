/*
 * @author Steven Wu
 */

package bb.mcmc.analysis;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.sun.deploy.util.ArrayUtil;
import dr.inference.operators.MCMCOperator;
import dr.inference.operators.OperatorSchedule;
import dr.inference.trace.LogFileTraces;
import dr.inference.trace.Trace;
import dr.inference.trace.TraceAnalysis;
import dr.inference.trace.TraceCorrelation;
import dr.inference.trace.TraceDistribution;
import dr.inference.trace.TraceException;
import dr.inference.trace.TraceFactory;
import dr.inference.trace.TraceFactory.TraceType;
import dr.inference.trace.TraceList;
import dr.stats.DiscreteStatistics;
import dr.util.HeapSort;
import dr.util.NumberFormatter;

public class ESSConvergeStat extends AbstractConvergeStat{

	public static final ESSConvergeStat INSTANCE = new ESSConvergeStat();
	private static final TraceFactory.TraceType TRACETYPE = TraceFactory.TraceType.INTEGER;

	
	private int stepSize;
    private int essLowerLimitBoundary;
    private double burninPercentage;
	
	private HashMap<String, Double> stat;

	/*
	 * should initialize this class first, then just updating it;
	 */
	public ESSConvergeStat() {
        STATISTIC_NAME = "Effective Sample Size";
	}

	public ESSConvergeStat(int stepSize, String[] varNames, double burninPercentage, int essLowerLimitBoundary) {
        this();
		this.stepSize = stepSize;
		this.variableName = varNames; // each stat can calculate different variable set
        this.burninPercentage = burninPercentage;
        this.essLowerLimitBoundary = essLowerLimitBoundary;

		stat = new HashMap<String, Double>();
		for (String s : variableName) {
			stat.put(s, 0.0);
		}
		
		
	}


    // TODO need to take into account burn-in (short)
	public void calculateStatistic() {
//        Set<String> keys =  traceInfo.keySet();
//        for (String s : keys) {
//            System.out.println("keys " + s);
//        }
        System.out.println("Calculating ESS for statistics: ");
		for (String s : variableName) {
            //System.out.print(s + "\t" + traceInfo.get(s));
            System.out.print(s + "\t");
			TraceCorrelation traceCorrelation = new TraceCorrelation(traceInfo.get(s),
					TRACETYPE, stepSize);
			stat.put(s, traceCorrelation.getESS() );
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

	
	public double getStat() {
		// TODO Auto-generated method stub
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


}
