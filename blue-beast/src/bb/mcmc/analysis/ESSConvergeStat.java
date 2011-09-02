/*
 * @author Steven Wu
 */

package bb.mcmc.analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

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
	private static final TraceFactory.TraceType TRACETYPE = TraceFactory.TraceType.DOUBLE;

	
	private int stepSize;
	
	private HashMap<String, Double> stat;

	/*
	 * should initilise this class first, then just updating it;
	 */
	public ESSConvergeStat() {

	}

	public ESSConvergeStat(int stepSize, String[] varNames) {
		this.stepSize = stepSize;
		this.variableName = varNames; // each stat can calculate different variable set
		stat = new HashMap<String, Double>();
		for (String s : variableName) {
			stat.put(s, 0.0);
		}
		
		
	}


	public void calculateStatistic() {
		for (String s : variableName) {
			TraceCorrelation traceCorrelation = new TraceCorrelation(traceInfo.get(s),
					TRACETYPE, stepSize);	
			stat.put(s, traceCorrelation.getESS() );	
		}
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

	@Override
	public double[] getAllStat() {

		return null;
	}



}
