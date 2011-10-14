package bb.mcmc.analysis;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * @author Wai Lok Sibon Li
 *
 */
public interface ConvergeStat {

    public String getStatisticName();

    public void calculateStatistic();
    
    public void updateTrace(Hashtable<String, ArrayList<Double>> traceInfo);

    public double getStat(String varName);

    public boolean hasConverged();
    
    public double[] getAllStat();

}
