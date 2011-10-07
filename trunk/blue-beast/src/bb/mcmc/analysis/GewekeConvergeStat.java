package bb.mcmc.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import dr.app.beauti.util.NumberUtil;
import dr.inference.trace.TraceCorrelation;
import dr.stats.DiscreteStatistics;

public class GewekeConvergeStat extends AbstractConvergeStat{

    public static final GewekeConvergeStat INSTANCE = new GewekeConvergeStat();
    
    
    private HashMap<String, Double> stat;


	private double fracEnd;
	private double fracStart;
    
    
    public GewekeConvergeStat(String[] varNames) {
    	this(varNames, 0.1, 0.5); //by default the first 10% and the last 50%
    }

	public GewekeConvergeStat(String[] varNames, double fracStart, double fracEnd) {
		
		this.variableName = varNames; // each stat can calculate different variable set
		this.fracStart = fracStart;
		this.fracEnd = fracEnd;
		stat = new HashMap<String, Double>();
		for (String s : variableName) {
			stat.put(s, 0.0);
		}
		
    }

    
    public GewekeConvergeStat() {

    }

//    @Override
	public void calculateStatistic() {
    	
    	int start = 0; // TODO need get burnin info
    	int end = traceInfo.get(variableName[0]).size();
    	int indexStart = (int) (end * fracStart);
    	int indexEnd   = (int) (end * fracEnd);
    	
		for (String s : variableName) {
			
			ArrayList<Double> t = traceInfo.get(s);
			List<Double> yStart = t.subList(start, indexStart);
			List<Double> yEnd = t.subList(indexEnd, end);
			
			// TODO pre calculate this "outside"
			double[] dStart = new double[indexStart-start]; //TODO double check index
			for (int i = 0; i < dStart.length; i++) {
				dStart[i] = yStart.get(i);
			}
			
			double[] dEnd = new double[end-indexEnd];			
			for (int i = 0; i < dEnd.length; i++) {
				dEnd[i] = yEnd.get(i);
			}
			
			t.toArray();
			Double[] D = new Double[indexStart-start+2];
			t.toArray(D);
			
		
			double meanStart = DiscreteStatistics.mean(dStart);
			double meanEnd = DiscreteStatistics.mean(dEnd);
			double varStart = 0.5;//FIXME
			double varEnd = 0.5;//FIXME
			
			double geweke = (meanStart - meanEnd) / Math.sqrt(varStart+varEnd);
			stat.put(s, geweke );	
		}
    }

	public double getStat(String name) {
		return stat.get(name);
	}


	public void addTrace(ArrayList<Double> traceInfo) {
		// TODO Auto-generated method stub
		
	}

//	@Override
	public double[] getAllStat() {
		// TODO Auto-generated method stub
		return null;
	}

    public boolean hasConverged() {
        return false;
    }
}
