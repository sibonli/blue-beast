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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;

import dr.inference.trace.LogFileTraces;
import dr.inference.trace.TraceDistribution;
import dr.inference.trace.TraceException;

//TODO not yet implemented class
public class GelmanConvergeStat extends AbstractConvergeStat{

	public static final Class<? extends ConvergeStat> THIS_CLASS = GelmanConvergeStat.class;
    public static final String STATISTIC_NAME = "Gelman and Rubin's convergence diagnostic";
	public static final String SHORT_NAME = "Gelman"; // gelman.diag in R
	
    public GelmanConvergeStat() {
    	super(STATISTIC_NAME, SHORT_NAME);		
    	System.err.println("Not yet implemented");
    }


	public void getTrace(String fileName, int inBurnin) throws TraceException, IOException {

		final File file = new File(fileName);
		final LogFileTraces traces = new LogFileTraces(fileName, file);
		traces.loadTraces();

		int burnin = inBurnin;
		if (burnin == -1) {
			burnin = traces.getMaxState() / 10;
		}
		traces.setBurnIn(burnin);

		String[] names;

		names = new String[]{"mean", "stdErr", "hpdLower", "hpdUpper", "ESS"};


		int warning = 0;
		for (int i = 0; i < traces.getTraceCount(); i++) {

			traces.analyseTrace(i);
			final TraceDistribution distribution = traces.getDistributionStatistics(i);

			final double ess = distribution.getESS();
			if (ess < 100) {
				warning += 1;
			} 
			distribution.getMean();
			distribution.getStdError();
			distribution.getLowerHPD();
			distribution.getUpperHPD();

			distribution.getVariance();

		}

		if (warning > 0) {
			System.out.println(" * WARNING: The results of this MCMC analysis may be invalid as ");
			System.out.println("            one or more statistics had very low effective sample sizes (ESS)");
		}



	}

@Override
	protected double calculateEachStat(String key){
    	final double[][] x = new double[][]{ {
    			0.9521188, 0.7698521, 0.09786176, 0.189116, 0.4292446, 0.3552301, 0.1950153, 0.1609035, 0.7716015, 0.4270067, 0.6004399, 0.4515346, 0.4395135, 0.6111626, 0.2658075, 0.6770691, 0.6616288, 0.9267304, 0.2816667, 0.06748204, 0.1979046, 0.2143528, 0.5945547, 0.4755721, 0.06334918, 0.9702486, 0.2009796, 0.4992713, 0.8446587, 0.3563186, 0.356484, 0.999883, 0.4203504, 0.2364206, 0.9131223, 0.03164529, 0.04497413, 0.5463757, 0.3191485, 0.4725209, 0.6027767, 0.484445, 0.5674497, 0.3412174, 0.1340296, 0.04187226, 0.4331442, 0.9524557, 0.5584405, 0.573079, 0.6960952, 0.2993999, 0.1897566, 0.04685504, 0.4884698, 0.3126638, 0.8774368, 0.4977949, 0.1991413, 0.7041003, 0.4410823, 0.7197942, 0.2353791, 0.2876871, 0.7144782, 0.6349463, 0.02958081, 0.3038256, 0.4939831, 0.8845382, 0.2020212, 0.9699524, 0.3046274, 0.3399967, 0.0384582, 0.9735209, 0.9277686, 0.8917364, 0.9809197, 0.04350204, 0.02877053, 0.684295, 0.7715872, 0.8490528, 0.869996, 0.9306584, 0.5692669, 0.2354259, 0.8870876, 0.9625581, 0.8990587, 0.0770488, 0.6198589, 0.4055902, 0.3698118, 0.631353, 0.9166882, 0.2780314, 0.4052221, 0.1304384
    		}, {
    			0.5234658, 0.5011702, 0.9104071, 0.5291853, 0.8460505, 0.9973632, 0.6077828, 0.9252267, 1.227432, 0.6356644, 1.349593, 1.470319, 1.404593, 0.771907, 1.092811, 0.6525535, 0.8905253, 0.6920996, 1.107879, 0.9931984, 1.328427, 0.5206647, 0.8475291, 1.410994, 1.247727, 1.130342, 1.19297, 1.409428, 1.147567, 1.206023, 0.9944026, 1.013745, 0.9296224, 1.184361, 1.130101, 1.354509, 0.9867336, 0.8438606, 0.6794144, 0.7989926, 1.027386, 0.5038223, 1.348911, 0.9478085, 0.6732905, 0.6463801, 1.055562, 1.14798, 0.5706039, 0.882299, 1.4959, 1.193184, 1.049748, 1.426155, 0.5753637, 0.8083308, 0.9589618, 0.5340521, 1.235058, 1.120921, 0.5946081, 1.20715, 1.308434, 1.433847, 0.5062377, 1.363858, 0.5498102, 1.31056, 1.172672, 1.101259, 1.307601, 1.444165, 1.194528, 0.6525332, 0.8386869, 0.7177501, 0.5930926, 0.5587183, 0.8124385, 0.5561617, 0.6768884, 0.6702416, 1.150888, 1.271023, 1.033207, 0.5202234, 0.9449494, 0.720158, 1.013462, 1.10213, 0.5790577, 1.191986, 1.211635, 1.053111, 1.14731, 0.6961994, 1.218642, 1.306064, 0.7342098, 1.300959
    		} };
		return overallProgress;
    			
    	// some pre-processing of traceInfo required
    	// must be multi chain
    	// different for single/multi var 
    	//TODO double[][] or ArrayList<>??
//    	gelmanDiag(x);
    			
//		checkConverged();
//		overallProgress = calculateProgress();
    }




	@Override
	protected double calculateEachProgress(Double stat, Deque<Double> record) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	protected boolean checkEachConverged(double stat, String key) {
		// TODO Auto-generated method stub
		return false;
	}


	//	need double check the algorithm
	// x is the mean for each chain
	private double betweenChainVariance(double[] x, double mean) {
		
	    double var = 0;
	    int count = x.length;
	    for(final double aX : x) {
	        if( Double.isNaN(aX) ) {
	            count--;
	        } else {
	            final double diff = aX - mean;
	            var += diff * diff;
	        }
	    }
	
	    if (count < 2) {
	        count = 1; // to avoid division by zero
	    } else {
	        count = count - 1; // for ML estimate
	    }
	    var = var / count * x.length;
	    
	
	    return var;
	}


	private double weightChainVar(double wVar, double bVar, int n){
		final double oneOverN = 1 / (double) n;
		final double weightVar = (1 - oneOverN) * wVar + oneOverN * bVar ;
		return weightVar;
		
		
	}


	// parameter format??
	private double[] gelmanDiag(ArrayList<double[][]> x ){
			//ArrayList.size() number of Chain
			// double[noVar][noIte]
	    	
			final double[] diag = new double[2];
			final int NChain = x.size();
			if(NChain < 2){
				System.err.println("Gelman and Rubin's convergence diagnostic requires at least 2 chains");
				System.exit(-1);
			}
			final int NVar = x.get(0).length; 
			final int NIter = x.get(0)[0].length; 
			
			
			
			final double[] S2 = new double[NChain];
			final double[] xbar = new double[NChain];
	//		for (int i = 0; i < NChain; i++) {
	//			S2[i] = StatUtils.variance(x[i]);
	//			xbar[i] = StatUtils.mean(x[i]);
	//		}
	//		double W = StatUtils.mean(S2);
	//		double B = NIter*StatUtils.variance(xbar);
	//		System.out.println(W);
	//		System.out.println(B);
			return diag;
				
		}

	private double PotentialScaleReductionFactor(double wVar, double weightVar){
		
		return Math.sqrt(weightVar/wVar);

	}

	/*
	gelman.diag
	function (x, confidence = 0.95, transform = FALSE, autoburnin = TRUE, 
	    multivariate = TRUE) 
	{
	    x <- as.mcmc.list(x)
	    if (nchain(x) < 2) 
	        stop("You need at least two chains")
	    if (autoburnin && start(x) < end(x)/2) 
	        x <- window(x, start = end(x)/2 + 1)
	    Niter <- niter(x)
	    Nchain <- nchain(x)
	    Nvar <- nvar(x)
	    xnames <- varnames(x)
	    if (transform) 
	        x <- gelman.transform(x)
	    x <- lapply(x, as.matrix)
	    S2 <- array(sapply(x, var, simplify = TRUE), dim = c(Nvar, 
	        Nvar, Nchain))
	    W <- apply(S2, c(1, 2), mean)
	    xbar <- matrix(sapply(x, apply, 2, mean, simplify = TRUE), 
	        nrow = Nvar, ncol = Nchain)
	    B <- Niter * var(t(xbar))
	    if (Nvar > 1 && multivariate) {
	        if (is.R()) {
	            CW <- chol(W)
	            emax <- eigen(backsolve(CW, t(backsolve(CW, B, transpose = TRUE)), 
	                transpose = TRUE), symmetric = TRUE, only.values = TRUE)$values[1]
	        }
	        else {
	            emax <- eigen(qr.solve(W, B), symmetric = FALSE, 
	                only.values = TRUE)$values
	        }
	        mpsrf <- sqrt((1 - 1/Niter) + (1 + 1/Nvar) * emax/Niter)
	    }
	    else mpsrf <- NULL
	    w <- diag(W)
	    b <- diag(B)
	    s2 <- matrix(apply(S2, 3, diag), nrow = Nvar, ncol = Nchain)
	    muhat <- apply(xbar, 1, mean)
	    var.w <- apply(s2, 1, var)/Nchain
	    var.b <- (2 * b^2)/(Nchain - 1)
	    cov.wb <- (Niter/Nchain) * diag(var(t(s2), t(xbar^2)) - 2 * 
	        muhat * var(t(s2), t(xbar)))
	    V <- (Niter - 1) * w/Niter + (1 + 1/Nchain) * b/Niter
	    var.V <- ((Niter - 1)^2 * var.w + (1 + 1/Nchain)^2 * var.b + 
	        2 * (Niter - 1) * (1 + 1/Nchain) * cov.wb)/Niter^2
	    df.V <- (2 * V^2)/var.V
	    df.adj <- (df.V + 3)/(df.V + 1)
	    B.df <- Nchain - 1
	    W.df <- (2 * w^2)/var.w
	    R2.fixed <- (Niter - 1)/Niter
	    R2.random <- (1 + 1/Nchain) * (1/Niter) * (b/w)
	    R2.estimate <- R2.fixed + R2.random
	    R2.upper <- R2.fixed + qf((1 + confidence)/2, B.df, W.df) * 
	        R2.random
	    psrf <- cbind(sqrt(df.adj * R2.estimate), sqrt(df.adj * R2.upper))
	    dimnames(psrf) <- list(xnames, c("Point est.", "Upper C.I."))
	    out <- list(psrf = psrf, mpsrf = mpsrf)
	    class(out) <- "gelman.diag"
	    out
	}
	
	*/	



}
