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

import javastat.regression.glm.ExponentialFamily;
import javastat.regression.glm.LogLinearRegression;



import dr.app.beauti.util.NumberUtil;
import dr.inference.trace.TraceCorrelation;
import dr.stats.DiscreteStatistics;
import flanagan.analysis.Regression;
import flanagan.analysis.RegressionFunction;
import flanagan.complex.Complex;
import flanagan.math.FourierTransform;

public class GewekeConvergeStat extends AbstractConvergeStat{

    public static final GewekeConvergeStat INSTANCE = new GewekeConvergeStat();
    
    private HashMap<String, Double> stat;
	private double fracEnd;
	private double fracStart;
	private double burninPercentage;
    
    public GewekeConvergeStat() {
        STATISTIC_NAME = "Geweke";
    }
    
    public GewekeConvergeStat(String[] varNames) {
    	this(varNames, 0.1, 0.5, 0.1); //by default the first 10% and the last 50%, 10% burnin
    }

    public GewekeConvergeStat(String[] varNames, double burninPercentage) {
    	this(varNames, burninPercentage, 0.1, 0.5); //by default the first 10% and the last 50%
    }
    
	public GewekeConvergeStat(String[] varNames, double burninPercentage, double fracStart, double fracEnd) {
		this();
		this.variableNames = varNames; // each stat can calculate different variable set
		this.fracStart = fracStart;
		this.fracEnd = fracEnd;
		this.burninPercentage = burninPercentage;
		stat = new HashMap<String, Double>();
		for (String s : variableNames) {
			stat.put(s, 0.0);
		}
		
    }


//    @Override
	public void calculateStatistic() {
    	
	       
        //TODO remove the follow line later, for faster testing purpose only
        // skip using proper BlueBeastLogger, so variables are not initialised properly 
        int xi = 0;
        variableNames = new String[traceInfo.size()];
        for (String s : traceInfo.keySet()) {
			variableNames[xi] = s;
			xi++;
		}
        //
        
    	int totalLength = traceInfo.get(variableNames[0]).size();
    	int burnin = (int) Math.round(totalLength * burninPercentage);
    	int newLength = totalLength - burnin;
    	
    	int indexStart = (int) (newLength * fracStart) + burnin;
    	int indexEnd   = (int) (newLength * fracEnd)+ burnin;
    	
    	int startLength = indexStart-burnin;
    	int endLength = totalLength-indexEnd;
    	System.out.println(traceInfo.size());
		for (String s : variableNames) {
			System.out.println("In Geweke: "+s);
			ArrayList<Double> t = traceInfo.get(s);
			List<Double> yStart = getSubList(t, burnin, indexStart);
			List<Double> yEnd = getSubList(t, indexEnd, totalLength);
			
			// TODO fastest way List<Double> to double[] NOT DOUBLE[] ?
			double[] dStart = new double[startLength]; //TODO double check index
			for (int i = 0; i < dStart.length; i++) {
				dStart[i] = yStart.get(i);
			}
			
			double[] dEnd = new double[endLength];			
			for (int i = 0; i < dEnd.length; i++) {
				dEnd[i] = yEnd.get(i);
			}
			
			t.toArray();
			Double[] D = new Double[indexStart-burnin+2];
			t.toArray(D);
			
		
			double meanStart = DiscreteStatistics.mean(dStart);
			double meanEnd = DiscreteStatistics.mean(dEnd);
			double varStart = calVar(dStart);//FIXME
			double varEnd = 0.5;//FIXME
			
			double gewekeStat = (meanStart - meanEnd) / Math.sqrt(varStart+varEnd);
			stat.put(s, gewekeStat );	
		}
    }

	private double calVar(double[] data) {
		
		double var = 1;
		int batchSize = 1;
		int maxLength = 200; // 200 is the default, TODO, change later
		double[] newData;
		if(data.length > maxLength){ 
			double index = 1.0*data.length/maxLength;
			
			batchSize = (int) Math.ceil(index);
//			System.out.println(data.length +"\t" +index + "\t" + batchSize);

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
		}
		
		
		double spectrum0 = calSpectrum0(newData);
		var = spectrum0 * batchSize;
		var /= data.length;
		System.out.println("Batch size:\t"+batchSize+"\t"+var);
		return 0;
	}

	private double calSpectrum0(double[] newData) {
		
		int N = 64;
		int Nfreq = N/2;
		newData = new double[N];
		for (int i = 0; i < newData.length; i++) {
//			newData[i] = i+1;
			newData[i] = Math.random();
		}
		newData = new double[]{
				0.4545743294640914, 0.4890745581217115, 0.30042940098706894, 0.4707295162201641, 0.0713410206323366, 0.15980090845863948, 0.5754560447693043, 0.13338232607689504, 0.9790859641327162, 0.26089043848955207, 0.7017871537846505, 0.03634205754313202, 0.9875607473175799, 0.3630642862051169, 0.5537607689855073, 0.5246653866950118, 0.4688757077581439, 0.6976498071898495, 0.8552990022758744, 0.15308666816109628, 0.7638789071175892, 0.8441535914862047, 0.7494152545873657, 0.1107129731270774, 0.18195040591184541, 0.8695031339060532, 0.06491203619378805, 0.6578279157736787, 0.27075603852331054, 0.9092222550462666, 0.3362075700863504, 0.6514950299844957, 0.7366062602993024, 0.61146900722348, 0.3519567703556141, 0.3079402713716608, 0.3814176615623889, 0.826779224866, 0.5091083641822587, 0.45223254557143344, 0.6888757330527064, 0.41952276638494523, 0.5312664924532033, 0.6395702125975705, 0.24024961418364077, 0.101493589250092, 0.17370196098330148, 0.6975201994006468, 0.9694105498418089, 0.7437188187964971, 0.38208436385325506, 0.43484611407901974, 0.3769577690673891, 0.7094861526043598, 0.9979834400179121, 0.49009991321284196, 0.21495695989302543, 0.3697528539930821, 0.5114142424389282, 0.5519418942320705, 0.8266478959336361, 0.11151382629755957, 0.5491773217552562, 0.4731914482228514
		};
		System.out.println(Arrays.toString(newData));
		
		FourierTransform f = new FourierTransform(newData);
		System.out.println(f.getUsedDataLength()+"\t"+f.getOriginalDataLength());
		f.transform();
		Complex[] fc = f.getTransformedDataAsComplex();
		double[] fd = f.getTransformedDataAsAlternate();
//		System.out.println(Arrays.toString(fc));
//		System.out.println(Arrays.toString(fd));
		
		
		Complex[] fc2 = fc;
		double[] spec = new double[fc.length];
		for (int i = 0; i < fc2.length; i++) {
			fc2[i] = Complex.times(fc[i], fc[i].conjugate());
			spec[i] = fc2[i].getReal()/fc2.length;
		}
//		System.out.println("spec:\t"+Arrays.toString(spec));
		
		double[] x = new double[Nfreq]; //freq in R
		double[] f1 = new double[Nfreq];
		double sqrt3 = Math.sqrt(3);
		x[0] = 1.0/N;
		f1[0] = sqrt3 * (4*x[0]-1);
		for (int i = 1; i < Nfreq; i++) {
			x[i] = x[0]+x[(i-1)]; 
			f1[i] = sqrt3 * (4*x[i]-1);
//			f1 <- sqrt(3) * (4 * freq - 1)
		}
		double[] y = Arrays.copyOfRange(spec, 1, Nfreq+1);
//		System.out.println("x\t"+Arrays.toString(x));
//		System.out.println("f1\t"+Arrays.toString(f1));
//		System.out.println("y\t"+Arrays.toString(y));
		for (int i = 0; i < f1.length; i++) {
//			f1[i] = Math.exp(f1[i]);
		}
		
		double[] ty = new double[10];
		double[] tx = new double[10];
		for (int i = 0; i < 10; i++) {
			ty[i] = i+2;
			tx[i] = i+1;
		}
//		ty = new double[]{0.1,0.1,0.1,0.8,0.7,0.1,0.1,0.6,0.7,0.2};
//		tx = new double[]{0.1,0.1,0.3,0.5,0.7,0.6,0.8,0.5,0.9,0.4};
//		ty = new double[]{0,0,0,0,1,0,1,1,1,1};
//		System.out.println(Arrays.toString(tx));
//		System.out.println(Arrays.toString(ty));
		
//		Regression glm = new Regression(f1, y);
		Regression glm = new Regression(tx, ty);
		
		
//		glm.linear();
//		glm.gammaStandard();
//		glm.logistic();
//		glm.exponentialSimple();
//		glm.gaussian();
		
//		System.out.println(glm.getNmax());
//		System.out.println(glm.getNiter());
//		glm.setTolerance(0.0000001);
//		System.out.println(glm.getTolerance());
		double[] start = new double[2];
		start[0] = -1; // initial estimate of a
		start[1] = 1; // initial estimate of c

		// initial step sizes for a and c in y = a + b.exp(-c.x)
		double[] step = new double[2];
		step[0] = 0.1; // initial step size for a
		step[1] = 0.1; // initial step size for c

		// create an instance of Regression
		
		FunctOne ff = new FunctOne();
//		glm.setErrorsAsScaled();
//		glm.setErrorsAsSD();
//		glm.simplexPlot(ff, start, step);

//		System.out.println(Arrays.toString(glm.getXdata()[0]));
//		System.out.println(Arrays.toString(glm.getYdata()));
//		
//		System.out.println("coef\t"+Arrays.toString( glm.getCoeff() ));
//		
//		System.out.println("beta\t"+Arrays.toString( glm.getBestEstimates()  ) );
//		System.out.println("fitted\t"+Arrays.toString( glm.getYcalc()) );
//		System.out.println("residual\t"+Arrays.toString( glm.getResiduals() ) );
//		System.out.println("beta\t"+Arrays.toString( glm.getBestEstimatesErrors()  ) );
//		System.out.println(glm.getSumOfSquares());
//		
//		System.out.println("T\t"+Arrays.toString( glm.getTvalues()  ) );
//		System.out.println("P\t"+Arrays.toString( glm.getPvalues()  ) );
		
//		f.setSegmentLength(32);
//		f.setSegmentNumber(1);
//		System.out.println(f.getDeltaT());
//		System.out.println(f.getHeight());
		System.out.println(f.getName());
		System.out.println(f.getSegmentLength()+"\t"+f.getSegmentNumber());
//		System.out.println(f.getNumberOfPsdPoints());
		
		
		Hashtable argument = new Hashtable(); 
		// Fits a logistic regression model 
//		RegressionType.LOGISTIC
		
//		argument.put(Argument.REGRESSION_TYPE, "Logistic"); 
//		StatisticalAnalysis testclass1 = new GLM(argument, ty, tx).statisticalAnalysis; 
//		double[] coefficients = (double[]) testclass1.output.get(Output.COEFFICIENTS); 
		System.out.println("AOEUAOEU");
		
		
		String [][] shipData = {{"a", "a", "a", "a", "a", "a", "a", "a", "b", "b", "b", "b", "b", "b", "b", "b", 
            "c", "c", "c", "c", "c", "c", "c", "c", "d", "d", "d", "d", "d", "d", "d", "d", 
            "e", "e", "e", "e", "e", "e", "e", "e"}}; 
double[] offset = {127, 63, 1095, 1095, 1512, 3353, 0, 2244, 44882, 17176, 28609, 
    20370, 7064, 13099, 0, 7177, 1179, 552, 781, 676, 783, 1948, 0, 274, 251, 
   105, 288, 192, 349, 1208, 0, 2051, 45, 0, 789, 437, 1157, 2161, 0, 542}; 
double[] damageNumber = {0, 0, 3, 4, 6, 18, 0, 11, 39, 29, 58, 53, 12, 44, 0, 18, 1, 
                       1, 0, 1, 6, 2, 0, 1, 0, 0, 0, 0, 2, 11, 0, 4, 0, 0, 7, 7, 5, 12, 0, 1}; 

//		ty = new double[]{
//				0.292036062293038, 0.0348821791292517, 0.0143220236239171, 0.00584791106461178, 0.00467692918237764
//				};
		
		double[][] tx2 = new double[1][tx.length];
		tx2[0] = tx;
		
		
		System.out.println(Arrays.toString(tx2[0]));
		System.out.println(Arrays.toString(ty));
		double[] t0 = new double[ty.length];
		Arrays.fill(t0, 1);
		
		/*
		 * poisson      0.8857       0.1604
		 * gamma		0.7849       0.1768
		 * 
		 */
//		LogLinearRegression testclass2 = new LogLinearRegression();
		LogLinearRegression2 testclass2 = new LogLinearRegression2(); 
		LogLinearRegression testclass3 = new LogLinearRegression();
//		testclass2.
//		LogLinearRegression testclass2 = new LogLinearRegression(damageNumber, offset, shipData); 
//		LogisticRegression testclass2 = new LogisticRegression(ty, tx2);
		testclass2.pearsonResiduals(ty, tx2);
		double[] coefficients = testclass2.coefficients(ty, t0, tx2);
		double[][] confidenceInterval = testclass2.confidenceInterval(0.1,ty, t0, tx2); 
		double [] testStatistic = testclass2.testStatistic(ty, t0, tx2);
		double [] pValue = testclass2.pValue(ty, t0, tx2);
		double [][] devianceTable = testclass2.devianceTable(ty, t0, tx2);
		System.out.println(Arrays.toString(coefficients));
//		System.out.println(Arrays.toString(testclass2.responseVariance(testclass2.means,
//				ExponentialFamily.GAMMA)));;
				System.out.println(Arrays.toString(testclass3.coefficients(ty, t0, tx2)));
				testclass3.confidenceInterval(0.1,ty, t0, tx2); 
		System.out.println(Arrays.toString(testclass3.responseVariance(testclass3.means,
						ExponentialFamily.GAMMA)));;
//		coefficients = testclass2.coefficients(ty, t0, tx2);
//		confidenceInterval = testclass2.confidenceInterval(0.1,ty, t0, tx2); 
//		testStatistic = testclass2.testStatistic(ty, t0, tx2);
//		pValue = testclass2.pValue(ty, t0, tx2);
//		devianceTable = testclass2.devianceTable(ty, t0, tx2);

		System.out.println("start out");
		System.out.println(Arrays.toString(coefficients));
//		System.out.println(Arrays.toString(testclass2.coefficientSE));
//		System.out.println(Arrays.toString(confidenceInterval[0] ));
//		System.out.println(Arrays.toString(testStatistic));
//		System.out.println(Arrays.toString(pValue));
//		System.out.println(Arrays.toString(devianceTable[0]));
//		System.out.println(testclass2.deviance);
//		System.out.println(testclass2.);
		System.out.println("end out");
		
//		double[][] confidenceInterval = (double[][]) testclass1.output.get(CONFIDENCE_INTERVAL); 
//		double[] testStatistic = (double[]) testclass1.output.get(TEST_STATISTIC); 
//		double[] pValue = (double[]) testclass1.output.get(PVALUE); 
//		double[][] devianceTable = (double[][]) testclass1.output.get(DEVIANCE_TABLE); 
		System.out.println(Arrays.toString(coefficients));
		
		f = new FourierTransform(ty);
		double powerSpec[][] = f.powerSpectrum();
		for (int i = 0; i < spec.length; i++) {
			System.out.println(i+"\t"+Arrays.toString(powerSpec[i]));
		}
		 
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

	
	class FunctOne implements RegressionFunction{

//        private double b = 0.0D;

        public double function(double[] p, double[] x){
//                 double y = p[0] + b*Math.exp(-p[1]*x[0]);
//        	 double y =  1.0 / (1.0 + Math.exp(- (p[0] + p[1]*x[0]) ));
        	double e = Math.exp( (p[0] + p[1]*x[0]) );
        	double y = e / (1+e);
//        	double y =  (p[0] + p[1]*x[0]) ;
             return y;
        }

//        public void setB(double b){
//           this.b = b;
//        }
	}

	
	
	public double getStat(String name) {
		return stat.get(name);
	}


	public void addTrace(ArrayList<Double> traceInfo) {
		// TODO Auto-generated method stub
		
	}

	//@Override
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

					


    public boolean hasConverged() {
        return false;
    }
}
