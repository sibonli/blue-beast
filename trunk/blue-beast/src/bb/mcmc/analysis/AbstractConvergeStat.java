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
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.google.common.primitives.Doubles;


public abstract class AbstractConvergeStat implements ConvergeStat{

	protected HashMap<String, Double> convergeStat = new HashMap<String, Double>();

	protected HashMap<String, ArrayList<Double>> traceInfo;
	protected String[] variableNames; // all variables names
	
	protected String[] testVariableName; // only the one we need to test
	protected HashMap<String, double[]> values;
	
    public String STATISTIC_NAME;
	
	
	@Override
	public void updateTrace (HashMap<String, ArrayList<Double>> traceInfo) {
		this.traceInfo = traceInfo;
	}

	@Override
	public void updateValues(HashMap<String, double[]> values) {
		this.values = values;
		
	}

    @Override
	public String getStatisticName() {
        return STATISTIC_NAME;
    }
	
    @Override
	public String[] getVariableNames() {
        return variableNames;
    }
    
	@Override
	public double getStat(String name) {
		return convergeStat.get(name);
	}
    
    public HashMap<String, double[]> traceInfoToArray(int burnin){

    	return traceInfoToArrays(traceInfo, burnin);
    }
    
	public static HashMap<String, double[]> traceInfoToArrays(
			HashMap<String, ArrayList<Double>> traceInfo, int burnin) {
		
		HashMap<String, double[]> newValues = new HashMap<String, double[]>();
		final Set<String> names = traceInfo.keySet();
		
    	for (String key : names) {
            final List<Double> t = getSubList(traceInfo.get(key), burnin);
            newValues.put(key, Doubles.toArray(t)) ;
		}
    	return newValues;
    }
    
	public static <T> List<T> getSubList(ArrayList<T> list, int burnin) {
		List<T> subList= list.subList(burnin, list.size());
		return subList;
	}


	public static <T> List<T> getSubList(ArrayList<T> list, int burnin,
			int totalLength) {
		final List<T> subList= list.subList(burnin,totalLength);
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
