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

import org.apache.commons.math3.complex.Complex;

import bb.mcmc.analysis.glm.LogGammaRegression;

import com.google.common.primitives.Doubles;

import dr.stats.DiscreteStatistics;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class ConvergeStatUtils {

	private static final double SQRT3 = Math.sqrt(3);
	
	private static LogGammaRegression gammaGLM = new LogGammaRegression();

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

	protected static double quantileType7InR(double[] x, double q) {
	
		final double[] tempX = x.clone();
		Arrays.sort(tempX);
		final int n = tempX.length;
		final double index = 1 + (n - 1) * q;
		final double lo = Math.floor(index);
		final double hi = Math.ceil(index);
		Arrays.sort(tempX);
		double qs = tempX[(int) lo - 1];
		final double h = index - lo;
		if (h != 0) {
			qs = (1 - h) * qs + h * tempX[(int) hi - 1];
		}
		return qs;
	
	}

	protected static int[][] create2WaysContingencyTable(boolean[] testres,
			int newDim) {
	
		final boolean[] b1 = Arrays.copyOfRange(testres, 0, newDim - 1);
		final boolean[] b2 = Arrays.copyOfRange(testres, 1, newDim);
	
		final int table[][] = new int[][] { { 0, 0 },
											{ 0, 0 } };
	
		for (int i = 0; i < b1.length; i++) {
			if (b1[i] && b2[i]) {
				table[1][1] += 1;
			} else if (!b1[i] && b2[i]) {
				table[0][1] += 1;
			} else if (b1[i] && !b2[i]) {
				table[1][0] += 1;
			} else if (!b1[i] && !b2[i]) {
				table[0][0] += 1;
			}
		}
		return table;
	
	}

	protected static int[][][] create3WaysContingencyTable(boolean[] testres,
			int newDim) {
	
		final boolean[] b1 = Arrays.copyOfRange(testres, 0, newDim - 2);
		final boolean[] b2 = Arrays.copyOfRange(testres, 1, newDim - 1);
		final boolean[] b3 = Arrays.copyOfRange(testres, 2, newDim);
	
		final int table[][][] = new int[][][] { { { 0, 0 }, { 0, 0 } },
				{ { 0, 0 }, { 0, 0 } } };
	
		for (int i = 0; i < b1.length; i++) {
			if (b1[i] && b2[i] && b3[i]) {
				table[1][1][1] += 1;
			} else if (!b1[i] && b2[i] && b3[i]) {
				table[0][1][1] += 1;
			} else if (b1[i] && !b2[i] && b3[i]) {
				table[1][0][1] += 1;
			} else if (b1[i] && b2[i] && !b3[i]) {
				table[1][1][0] += 1;
			} else if (b1[i] && !b2[i] && !b3[i]) {
				table[1][0][0] += 1;
			} else if (!b1[i] && b2[i] && !b3[i]) {
				table[0][1][0] += 1;
			} else if (!b1[i] && !b2[i] && b3[i]) {
				table[0][0][1] += 1;
			} else if (!b1[i] && !b2[i] && !b3[i]) {
				table[0][0][0] += 1;
			}
		}

		return table;
	
	}

	protected static double spectrum0(double[] data) {
		
		final int maxLength = 200; // 200 is the default, TODO, change later
		int batchSize;
		double[] newData;
		
		if(data.length > maxLength){ 
			
			final double index = 1.0*data.length/maxLength;
			batchSize = (int) Math.ceil(index);
			int from = 0;
			int to = batchSize;
			ArrayList<Double> tempData = new ArrayList<Double>();
			
			while(to <= data.length){
//				double[] temp = Arrays.copyOfRange(data, from, to);	System.out.println(Arrays.toString(temp));
				double mean = DiscreteStatistics.mean( Arrays.copyOfRange(data, from, to) );
				tempData.add(mean);
				from = to;
				to += batchSize;
			}
			
			newData = new double[tempData.size()];
			for (int i = 0; i < newData.length; i++) {
				newData[i]  = tempData.get(i);
			}
		}
		else{
			newData = data;
			batchSize = 1;
		}
		
		double spectrum0 = calSpectrum0(newData);
		double var = spectrum0 * batchSize;
		
		
		return var;
	}



	private static double calSpectrum0(double[] newData) {

		final int N = newData.length;
		final int Nfreq = (int) Math.floor(N/2);
		final double oneOverN = 1.0/N;
		
		double[] freq = new double[Nfreq]; 
		double[] f1 = new double[Nfreq];
		
		for (int i = 0; i < Nfreq; i++) {
			freq[i] = oneOverN * (i+1); 
			f1[i] = SQRT3 * (4*freq[i]-1);
		}

		double[] complexArray = ConvergeStatUtils.realToComplexArray(newData);
		double[] spec = new double[N];
		DoubleFFT_1D fft = new DoubleFFT_1D(N);
		fft.complexForward(complexArray);

		for (int i = 0; i < N; i++) {
			Complex complexData = new Complex(complexArray[i * 2],
					complexArray[i * 2 + 1]);
			complexData = complexData.multiply(complexData.conjugate());
			spec[i] = complexData.getReal() / N;
		}

		spec = Arrays.copyOfRange(spec, 1, f1.length + 1);

		
		double[] coefficients = gammaGLM.coefficients(spec, f1);
		double v = Math.exp(coefficients[0] + coefficients[1] * -SQRT3);

		return v;
	}

}
