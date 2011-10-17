package bb.mcmc.analysis;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * @author Wai Lok Sibon Li
 */
public class RafteryConvergeStat extends AbstractConvergeStat {

    protected Hashtable<String, ArrayList<Double>> traceInfo;
	protected String[] variableName;
    public String STATISTIC_NAME;


	public void updateTrace (Hashtable<String, ArrayList<Double>> traceInfo) {
		this.traceInfo = traceInfo;
        STATISTIC_NAME = "Raftery-Lewis";
        // TODO

	}

    public String getStatisticName() {
        return STATISTIC_NAME;
    }


    public void calculateStatistic() {

    }


    public double getStat(String varName) {
        return -1.0;
    }

    public boolean hasConverged() {
        return false;
    }

    public double[] getAllStat() {
        return null;
    }
}
