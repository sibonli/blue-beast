package bb.mcmc.analysis;

import java.util.ArrayList;
import java.util.Hashtable;


public interface ConvergeStat {

    public void calculateStatistic();
    
    public void updateTrace(Hashtable<String, ArrayList<Double>> traceInfo);

    public double getStat(String varName);
    
    public double[] getAllStat();

}
