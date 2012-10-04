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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.google.common.primitives.Doubles;


public abstract class AbstractConvergeStat implements ConvergeStat{

	protected HashMap<String, Double> convergeStat = new HashMap<String, Double>();
	protected String[] testVariableName; // only the one we need to test
	protected HashMap<String, double[]> traceValues;
	protected HashMap<String, Boolean> hasConverged = new HashMap<String, Boolean>();
	protected boolean haveAllConverged = true;
    
	protected String statisticName;

	@Override
	public void setTestVariableName(String[] testVariableName) {
		this.testVariableName = testVariableName;
	}

	protected void checkTestVariableName() {
		if (testVariableName == null) {
			System.err.println("testVariable are not set yet");
			System.exit(-1);
		}

	}
	
	@Override
	public String toString(){
		checkConverged();
		StringBuilder sb = new StringBuilder(statisticName+"\n");
		
		for (String key : testVariableName) {
			sb.append(key).append("\t").append(convergeStat.get(key)).append("\t")
					.append(hasConverged.get(key)).append("\n");
		}
		sb.append("all diag converged?: ").append(haveAllConverged).append("\n");
		return sb.toString();
	}

	@Override
	public void updateValues(HashMap<String, double[]> traceValues) {
		this.traceValues = traceValues;
	
	}
	@Override
	public boolean haveAllConverged(){
		checkConverged();
		return haveAllConverged;
	}
	@Override
	public String getStatisticName() {
	    return statisticName;
	}

	@Override
	public String[] getTestVariableNames() {
	    return testVariableName;
	}

	@Override
	public double getStat(String name) {
		return convergeStat.get(name);
	}

	@Override
	public double[] getAllStat() {
	    final double[] statDouble = new double[convergeStat.size()];
	    final ArrayList<Double> statValues = new ArrayList<Double>(convergeStat.values());
	    int i=0;
	    for(Double d : statValues) {
	        statDouble[i] = d;
	        i++;
	    }
	    return statDouble;
	
	}

	//TODO: remove these methods later
	@Override
	@Deprecated
	public void updateTrace(HashMap<String, ArrayList<Double>> traceInfo) {
	}

	@Override
	@Deprecated
	public String[] getVariableNames() {
		return null;
	}

	    
	@Override
	public String notConvergedSummary() {
		StringBuilder sb = new StringBuilder(statisticName+"\nThe following variables might not converged yet\n");
		
		for (String key : hasConverged.keySet()) {
			if(!hasConverged.get(key)){
				sb.append(key).append("\t").append(convergeStat.get(key)).append("\n");
			}
		}
		return sb.toString();
	}

	public static HashMap<String, double[]> traceInfoToArrays(
			HashMap<String, ArrayList<Double>> traceInfo, int burnin) {

		HashMap<String, double[]> newValues = new HashMap<String, double[]>();
		final Set<String> names = traceInfo.keySet();
		
		for (String key : names) {
			final List<Double> t = getSubList(traceInfo.get(key), burnin);
			newValues.put(key, Doubles.toArray(t));
		}
		return newValues;
	}

	public static double[] realToComplexArray(double[] newData) {
		double[] complexArray = new double[newData.length*2];
		for (int i = 0; i < newData.length; i++) {
			complexArray[i*2] = newData[i];
		}
		return complexArray;
	}

	public static <T> List<T> getSubList(ArrayList<T> list, int burnin) {
		List<T> subList= list.subList(burnin, list.size());
		return subList;
	}


	public static <T> List<T> getSubList(ArrayList<T> list, int start,
			int subListLength) {
		List<T> subList= list.subList(start,subListLength);
		return subList;
	}


	protected static <T> T[] thinWindow(T[] array, int kthin) {

		if(kthin == 1){
    		return array;
    	}
    	else{
    		final int count = (int) Math.ceil(array.length/(double)kthin);
            final Class<?> type = array.getClass().getComponentType();
    		final T[] newArray = (T[]) Array.newInstance(type, count);
    		for (int i = 0; i < newArray.length; i++) {
				newArray[i] = array[ kthin*i ];
			}
    		return newArray;
    	}
    	
	}
	protected static boolean[] thinWindow(boolean[] array, int kthin) {
    	if(kthin == 1){
    		return array;
    	}
    	else{
    		
    		final int count = (int) Math.ceil(array.length/(double)kthin);
    		final boolean[] newArray = new boolean[count];

    		for (int i = 0; i < newArray.length; i++) {
				newArray[i] = array[ kthin*i ];
			}
    		return newArray;
    	}
    	
	}
	protected static int[] thinWindow(int[] array, int kthin) {

    	if(kthin == 1){
    		return array;
    	}
    	else{
    		final int count = (int) Math.ceil(array.length/(double)kthin);
    		final int[] newArray = new int[count];

    		for (int i = 0; i < newArray.length; i++) {
				newArray[i] = array[ kthin*i ];
			}
    		return newArray;
    	}
    	
	}

}
