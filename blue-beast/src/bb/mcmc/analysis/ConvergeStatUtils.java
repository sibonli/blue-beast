package bb.mcmc.analysis;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.google.common.primitives.Doubles;

public class ConvergeStatUtils {

	public static <T> List<T> getSubList(ArrayList<T> list, int start,
			int subListLength) {
		List<T> subList= list.subList(start,subListLength);
		return subList;
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

	protected static <T> T[] thinWindow(T[] array, int kthin) {
	
		if(kthin == 1){
			return array;
		}
		else{
			final int count = (int) Math.ceil(array.length/(double)kthin);
	        final Class<?> type = array.getClass().getComponentType();
	        @SuppressWarnings("unchecked") // OK, because array is of type T
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
