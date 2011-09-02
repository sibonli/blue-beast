/**
 * 
 */
package bb.mcmc.analysis;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * @author Steven Wu
 *
 */
public abstract class AbstractConvergeStat implements ConvergeStat{

	protected Hashtable<String, ArrayList<Double>> traceInfo;
	protected String[] variableName;  
	
	
	public void updateTrace (Hashtable<String, ArrayList<Double>> traceInfo) {
		this.traceInfo = traceInfo;
		
	}
	
	

	
}
