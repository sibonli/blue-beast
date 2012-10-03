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


import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javastat.regression.glm.LinkFunction;
import javastat.regression.glm.LogLinearRegression;
import javastat.regression.lm.LinearRegression;


import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.complex.Complex;
import org.nevec.rjm.BigDecimalMath;


import bb.mcmc.analysis.glm.LogGammaRegression;


import dr.stats.DiscreteStatistics;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;


public class GewekeConvergeStat extends AbstractConvergeStat{

    public static final AbstractConvergeStat INSTANCE = new GewekeConvergeStat();
    private static final double SQRT3 = Math.sqrt(3);
    
    private double frac1;	//default 0.1
    private double frac2;		//default 0.5
	
    
    public GewekeConvergeStat() {
        STATISTIC_NAME = "Geweke";
    }
    
    public GewekeConvergeStat(String[] testVariableName) {
    	this();
    	setupTestingValues(testVariableName);
    	setupDefaultParameterValue();
    	
    }

    
	public GewekeConvergeStat(String[] testVariableName, double frac1, double frac2) {
		this();
		setupTestingValues(testVariableName);
		this.frac1 = frac1;
		this.frac2 = frac2;
		
    }
	private void setupDefaultParameterValue(){
		frac1 = 0.1;
		frac2 = 0.5;
		
	}

//    @Override
	@Override
	public void calculateStatistic() {
    	
		checkTestVariableName();
		
	 	for (String s : testVariableName) {
			System.out.println("Calculating Geweke diagnostic for : "+s);
			
			double[] t = traceValues.get(s);
			final int length = t.length;
	    	final int indexStart = (int) Math.floor(length * (1-frac2)) ;
	    	final int indexEnd   = (int) Math.ceil(length * frac1);
	    	
	    	double[] dStart = Arrays.copyOfRange(t, 0, indexEnd);
			double[] dEnd = Arrays.copyOfRange(t, indexStart, length);
			final double meanStart = DiscreteStatistics.mean(dStart);
			final double meanEnd = DiscreteStatistics.mean(dEnd);
			final double varStart = calVar(dStart);//FIXME
			final double varEnd = calVar(dEnd);;//FIXME

			final double gewekeStat = (meanStart - meanEnd) / Math.sqrt(varStart+varEnd);
			convergeStat.put(s, gewekeStat );
//			System.out.println(varStart +"\t"+ varEnd +"\t"+ gewekeStat);
			
		}
    }

	private double calVar(double[] data) {
		
		final int maxLength = 200; // 200 is the default, TODO, change later
		int batchSize;
		
		double[] newData;
		
		if(data.length > maxLength){ 
			final double index = 1.0*data.length/maxLength;
			
			batchSize = (int) Math.ceil(index);
			int from = 0;
			int to = batchSize;
			final ArrayList<Double> tempData = new ArrayList<Double>();
			while(to <= data.length){
//				double[] temp = Arrays.copyOfRange(data, from, to);	System.out.println(Arrays.toString(temp));
				final double mean = DiscreteStatistics.mean( Arrays.copyOfRange(data, from, to) );
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
	
		double[] complexArray = realToComplexArray(newData); 
		
		DoubleFFT_1D fft = new DoubleFFT_1D(N);
		fft.complexForward(complexArray);
		
		double[] spec = new double[N];
		
		for (int i = 0; i < N; i++) {
//			Complex complexData = new Complex[N];
			Complex complexData = new Complex(complexArray[i*2], complexArray[i*2+1]);
			complexData = complexData.multiply(complexData.conjugate());
			spec[i] = complexData.getReal()/N;
		}		
		
		LogGammaRegression gammaGLM = new LogGammaRegression();
				
		spec = Arrays.copyOfRange(spec, 1, f1.length+1);
//		spec = ArrayUtils.subarray(spec, 1, f1.length+1);
		
		double[] offset1 = new double[Nfreq];
		Arrays.fill(offset1, 1);

		double[] coefficients = gammaGLM.coefficients(spec, offset1, f1);
		double v = Math.exp(coefficients[0] + coefficients[1]*-SQRT3);
		 
		return v;
	}

	
	

	@Override
	public double getStat(String name) {
		return convergeStat.get(name);
	}


	public void addTrace(ArrayList<Double> traceInfo) {
		// TODO Auto-generated method stub
		
	}

	//@Override
	@Override
	public double[] getAllStat() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	(double[] values, int stepSize) 
        final int samples = values.length;
        int maxLag = Math.min(samples - 1, MAX_LAG);

        double[] gammaStat = new double[maxLag];
        //double[] varGammaStat = new double[maxLag];
        double varStat = 0.0;
        //double varVarStat = 0.0;
        //double assVarCor = 1.0;
        //double del1, del2;

        for (int lag = 0; lag < maxLag; lag++) {
            for (int j = 0; j < samples - lag; j++) {
                final double del1 = values[j] - mean;
                final double del2 = values[j + lag] - mean;
                gammaStat[lag] += (del1 * del2);
                //varGammaStat[lag] += (del1*del1*del2*del2);
            }

            gammaStat[lag] /= ((double) (samples - lag));
            //varGammaStat[lag] /= ((double) samples-lag);
            //varGammaStat[lag] -= (gammaStat[0] * gammaStat[0]);

            if (lag == 0) {
                varStat = gammaStat[0];
                //varVarStat = varGammaStat[0];
                //assVarCor = 1.0;
            } else if (lag % 2 == 0) {
                // fancy stopping criterion :)
                if (gammaStat[lag - 1] + gammaStat[lag] > 0) {
                    varStat += 2.0 * (gammaStat[lag - 1] + gammaStat[lag]);
                    // varVarStat += 2.0*(varGammaStat[lag-1] + varGammaStat[lag]);
                    // assVarCor  += 2.0*((gammaStat[lag-1] * gammaStat[lag-1]) + (gammaStat[lag] * gammaStat[lag])) / (gammaStat[0] * gammaStat[0]);
                }
                // stop
                else
                    maxLag = lag;
            }
        }

        // standard error of mean
        stdErrorOfMean = Math.sqrt(varStat / samples);

        // auto correlation time
        ACT = stepSize * varStat / gammaStat[0];

	
	*/

					


    @Override
	public boolean hasConverged() {
        return false;
    }

	@Override
	public String notConvergedSummary() {
		// TODO Auto-generated method stub
		return null;
	}


}
