/**
 * 
 */
package bb.mcmc.analysis;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * @author Steven Wu
 * @author Wai Lok Sibon Li
 *
 */
public abstract class AbstractConvergeStat implements ConvergeStat{

	protected Hashtable<String, ArrayList<Double>> traceInfo;
	protected String[] variableNames;
    public String STATISTIC_NAME;
	
	
	public void updateTrace (Hashtable<String, ArrayList<Double>> traceInfo) {
		this.traceInfo = traceInfo;
		
	}

    public String getStatisticName() {
        return STATISTIC_NAME;
    }
	
    public String[] getVariableNames() {
        return variableNames;
    }

	
}
