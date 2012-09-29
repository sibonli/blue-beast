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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javastat.regression.glm.LogLinearRegression;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.complex.Complex;


import bb.mcmc.analysis.glm.LogLinearRegression3;


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
			System.out.println("In Geweke: "+s);
			
			double[] t = traceValues.get(testVariableName[2]);
//			double[] t = new double[150];
//			for (int i = 0; i < t.length; i++) {
//				t[i] = i+1;
//			}
			final int length = t.length;
	    	final int indexStart = (int) Math.floor(length * (1-frac2)) ;
	    	final int indexEnd   = (int) Math.ceil(length * frac1);
	    	
	    	double[] dStart = Arrays.copyOfRange(t, 0, indexEnd);
			double[] dEnd = Arrays.copyOfRange(t, indexStart, length);
			System.out.println(Arrays.toString(dStart));
			System.out.println(Arrays.toString(dEnd));
			final double meanStart = DiscreteStatistics.mean(dStart);
			final double meanEnd = DiscreteStatistics.mean(dEnd);
			System.out.println(meanStart +"\t"+ meanEnd);
			final double varStart = calVar(dStart);//FIXME
			final double varEnd = calVar(dEnd);;//FIXME
			
			 
System.out.println(varStart +"\t"+ varEnd);
			final double gewekeStat = (meanStart - meanEnd) / Math.sqrt(varStart+varEnd);
			convergeStat.put(s, gewekeStat );	
		}
    }

	private double calVar(double[] data) {
		
		final int maxLength = 200; // 200 is the default, TODO, change later
		double var;
		int batchSize;
		
		double[] newData;
		
		if(data.length > maxLength){ 
			final double index = 1.0*data.length/maxLength;
			
			batchSize = (int) Math.ceil(index);
//			System.out.println(data.length +"\t" +index + "\t" + batchSize);

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
		var = spectrum0 * batchSize;
		var /= data.length;
		System.out.println("Batch size:\t"+batchSize+"\t"+var);
		return var;
	}

	private double calSpectrum0(double[] newData) {
//		
//		newData = new double[64];
//		for (int i = 0; i < newData.length; i++) {
////			newData[i] = i+1;
//			newData[i] = Math.random();
//		}
//		newData = new double[]{
//				0.4545743294640914, 0.4890745581217115, 0.30042940098706894, 0.4707295162201641, 0.0713410206323366, 0.15980090845863948, 0.5754560447693043, 0.13338232607689504, 0.9790859641327162, 0.26089043848955207, 0.7017871537846505, 0.03634205754313202, 0.9875607473175799, 0.3630642862051169, 0.5537607689855073, 0.5246653866950118, 0.4688757077581439, 0.6976498071898495, 0.8552990022758744, 0.15308666816109628, 0.7638789071175892, 0.8441535914862047, 0.7494152545873657, 0.1107129731270774, 0.18195040591184541, 0.8695031339060532, 0.06491203619378805, 0.6578279157736787, 0.27075603852331054, 0.9092222550462666, 0.3362075700863504, 0.6514950299844957, 0.7366062602993024, 0.61146900722348, 0.3519567703556141, 0.3079402713716608, 0.3814176615623889, 0.826779224866, 0.5091083641822587, 0.45223254557143344, 0.6888757330527064, 0.41952276638494523, 0.5312664924532033, 0.6395702125975705, 0.24024961418364077, 0.101493589250092, 0.17370196098330148, 0.6975201994006468, 0.9694105498418089, 0.7437188187964971, 0.38208436385325506, 0.43484611407901974, 0.3769577690673891, 0.7094861526043598, 0.9979834400179121, 0.49009991321284196, 0.21495695989302543, 0.3697528539930821, 0.5114142424389282, 0.5519418942320705, 0.8266478959336361, 0.11151382629755957, 0.5491773217552562, 0.4731914482228514
//		};
		System.out.println("NEW DATA:\t"+newData.length +"\t"+ Arrays.toString(newData));
		//
		
		final int N = newData.length;
		final int Nfreq = (int) Math.floor(N/2);
		final double oneOverN = 1.0/N;
		
		double[] freq = new double[Nfreq]; 
		double[] f1 = new double[Nfreq];
		
		for (int i = 0; i < Nfreq; i++) {
			freq[i] = oneOverN * (i+1); 
			f1[i] = SQRT3 * (4*freq[i]-1);
		}
		System.out.println("freq\t"+Arrays.toString(freq));
		System.out.println("f1\t"+Arrays.toString(f1));

	
		double[] complexArray = new double[N*2];
		for (int i = 0; i < newData.length; i++) {
			complexArray[i*2] = newData[i];
		}
		System.out.println(Arrays.toString(complexArray));
		
		DoubleFFT_1D fft = new DoubleFFT_1D(N);
		fft.complexForward(complexArray);
		Complex[] complexData = new Complex[N];
		double[] spec = new double[N];
		
		for (int i = 0; i < complexData.length; i++) {
			complexData[i] = new Complex(complexArray[i*2], complexArray[i*2+1]);
		}

		
		for (int i = 0; i < N; i++) {
			Complex temp = complexData[i].multiply(complexData[i].conjugate());
			spec[i] = temp.getReal()/N;
//System.out.println(complexData[i].toString());
		}

//		System.out.println("spec:\t"+Arrays.toString(spec));
		
		/*
		 * poisson      0.8857       0.1604
		 * gamma		0.7849       0.1768
		 * 
		 */
		LogLinearRegression testclass1 = new LogLinearRegression();
		LogLinearRegression3 testclass3 = new LogLinearRegression3();
		double[] offset1 = new double[f1.length];

		spec = Arrays.copyOfRange(spec, 1, f1.length+1);
//		spec = ArrayUtils.subarray(spec, 1, f1.length+1);
		
		Arrays.fill(offset1, 1);
		
		final double[][] tx2 = new double[1][f1.length];
		tx2[0] = f1;	
		
		System.out.println("spec\t"+Arrays.toString(spec));
		System.out.println("f1\t"+Arrays.toString(f1));
		System.out.println("--");
		double[] coefficients = testclass3.coefficients(spec, offset1, f1);
		System.out.println(Arrays.toString(coefficients));
		System.out.println("R\t 4.886       -1.674");
//		coefficients = testclass1.coefficients(spec, offset1, tx2);
//		System.out.println(Arrays.toString(coefficients));
		
		double[] ty = new double[10];
		double[] tx = new double[10];
		for (int i = 0; i < 10; i++) {
			ty[i] = i*2+1;
			tx[i] = i+1;
		}

		
		offset1 = new double[ty.length];
		Arrays.fill(offset1, 1);
		
//		coefficients = testclass2.coefficients(ty, offset1, tx);
//		System.out.println(Arrays.toString(coefficients));
		System.out.println("poisson\t0.8857\t0.1604\n" +
							"gamma\t0.7849\t0.1768\n" +
							"     0.6165       0.2657");
		System.exit(-1);
		
		
		System.out.println("start out");
		System.out.println(Arrays.toString(coefficients));

		 
		//TODO code this
		/*
		 * var = spectrum0*batchSize;    out <- do.spectrum0(x, max.freq = max.freq, order = order)
    out$spec <- out$spec * batch.size


do.spectrum0
function (x, max.freq = 0.5, order = 1) 
{
    fmla <- switch(order + 1, spec ~ one, spec ~ f1, spec ~ f1 + 
        f2)
    if (is.null(fmla)) 
        stop("invalid order")
    N <- nrow(x)
    Nfreq <- floor(N/2)
    freq <- seq(from = 1/N, by = 1/N, length = Nfreq)
    f1 <- sqrt(3) * (4 * freq - 1)
    f2 <- sqrt(5) * (24 * freq^2 - 12 * freq + 1)
    v0 <- numeric(ncol(x))
    for (i in 1:ncol(x)) {
        y <- x[, i]
        if (var(y) == 0) {
            v0[i] <- 0
        }
        else {
            yfft <- fft(y)
            spec <- Re(yfft * Conj(yfft))/N
            spec.data <- data.frame(one = rep(1, Nfreq), f1 = f1, 
                f2 = f2, spec = spec[1 + (1:Nfreq)], inset = I(freq <= 
                  max.freq))
            glm.out <- glm(fmla, family = Gamma(link = "log"),
                data = spec.data)
            v0[i] <- predict(glm.out, type = "response", newdata = data.frame(spec = 0, 
                one = 1, f1 = -sqrt(3), f2 = sqrt(5)))
        }
    }
    return(list(spec = v0))
}
			
		*/
		return 0;
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
