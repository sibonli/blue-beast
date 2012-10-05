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


import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.complex.Complex;

import bb.mcmc.analysis.glm.LogGammaRegression;
import dr.stats.DiscreteStatistics;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;


public class GewekeConvergeStat extends AbstractConvergeStat{

	public static final Class<? extends ConvergeStat> thisClass = GewekeConvergeStat.class;
	public static final String STATISTIC_NAME = "Geweke's convergence diagnostic";
	public static final String SHORT_NAME = "Geweke"; // geweke.diag in R
	
	private static final double SQRT3 = Math.sqrt(3);
	
	private static LogGammaRegression gammaGLM = new LogGammaRegression();

	private double frac1; // default 0.1
	private double frac2; // default 0.5
	private double gewekeThreshold = 1.96;


    public GewekeConvergeStat() {
    	super(STATISTIC_NAME, SHORT_NAME);
    }

    
	public GewekeConvergeStat(double frac1, double frac2, double gewekeThreshold) {
		//TODO(SW) think about whether we want empty constructor? 
		//keep it for now because we used it quite a bit is the progressReporter
		//this();
		super(STATISTIC_NAME, SHORT_NAME);
		this.frac1 = frac1;
		this.frac2 = frac2;
		this.gewekeThreshold = gewekeThreshold;
		
    }
	private void setupDefaultParameterValue(){
		frac1 = 0.1;
		frac2 = 0.5;
		
	}

	@Override
	public void checkConverged() {
	
		for (String key : convergeStat.keySet()) {
			if (Math.abs(convergeStat.get(key)) > gewekeThreshold ) {
				hasConverged.put(key, false);
				haveAllConverged = false;
			}
			else {
				hasConverged.put(key, true);
			}
		}
	}
	@Override
	public void calculateStatistic() {

		checkTestVariableName();
	 	for (String key : testVariableName) {
			System.out.println("Calculating "+STATISTIC_NAME+": "+key);

			final double gewekeStat = calculateGewekeStat(key);
			convergeStat.put(key, gewekeStat );
			
		}
    }
	private double calculateGewekeStat(String key){
		
		final double[] t = traceValues.get(key);
		final int length = t.length;
    	final int indexStart = (int) Math.floor(length * (1-frac2)) ;
    	final int indexEnd   = (int) Math.ceil(length * frac1);
    	
    	final double[] dStart = Arrays.copyOfRange(t, 0, indexEnd);
		final double[] dEnd = Arrays.copyOfRange(t, indexStart, length);
		
		final double meanStart = DiscreteStatistics.mean(dStart);
		final double meanEnd = DiscreteStatistics.mean(dEnd);
		final double varStart = calVar(dStart);
		final double varEnd = calVar(dEnd);

		final double gewekeStat = (meanStart - meanEnd) / Math.sqrt(varStart+varEnd);
		return gewekeStat;
		
	}

	private static double calVar(double[] data) {
		
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
		var /= data.length;
		
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
