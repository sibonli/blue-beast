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


import java.util.Arrays;
import java.util.HashMap;

import dr.math.distributions.NormalDistribution;



public class RafteryConvergeStat extends AbstractConvergeStat {

    public static final RafteryConvergeStat INSTANCE = new RafteryConvergeStat();
    private double q;
    private double r;
    private double s;
    private double convergeEps;
	private int NVar;
	private double rafteryStatThreshold = 5; 	

    public RafteryConvergeStat() {
		STATISTIC_NAME = "Raftery and Lewis's diagnostic";
	}

	public RafteryConvergeStat(String[] testVariableName) {
    	this();
    	setupDefaultParameterValue();
    	setupTestingValues(testVariableName);
        
    }
    
    public RafteryConvergeStat(double quantile, double error, double prob, double convergeEps){
	    this();
    	q = quantile;
	    r = error;
	    s = prob;
	    this.convergeEps = convergeEps;
	    
	    
	}
	
	public RafteryConvergeStat(double quantile, double error, double prob,
			double convergeEps, String[] testVariableName) {

		this(quantile,error, prob, convergeEps);
	    setupTestingValues(testVariableName);
	    
	}
	


    private void setupDefaultParameterValue(){
		q = 0.025;
		r = 0.005;
		s = 0.95;
		convergeEps = 0.001;    	
	}


	@Override
	public void calculateStatistic() {
		checkTestVariableName();
    	boolean debug = false;
//    	debug = true;

    	final int NIte = traceValues.get(testVariableName[0]).length;
        final double z = 0.5 * (1 + s);
    	final double phi = NormalDistribution.quantile(z, 0, 1);
    	final double nmin = Math.ceil(  (q * (1 - q) * phi*phi)/ (r*r)   ); // 3746, niter>3746
    	final int thin = 1;//TODO, get thinning info

    	if(NIte < nmin){
    		System.err.println("Error: No. of iteration "+NIte+ " is less than nmin: "+nmin);
    		System.exit(-1);
    	}

		for (String key : testVariableName) {

			final double[] x = traceValues.get(key);
			final double quant = quantileType7InR(x, q);
    		final boolean[] dichot = new boolean[x.length];

    		for (int j = 0; j < dichot.length; j++) {
				if (x[j] < quant){
					dichot[j] = true;
				}
			}
    		
	        boolean[] testres = new boolean[0];
	        int newDim = testres.length;
	        int kthin = 0;
	        double bic = 1;
	        while (bic >= 0 ) {
	            kthin += thin;
	            testres = thinWindow(dichot, kthin);
	    		for (int j = 0; j < dichot.length; j++) {
					if (x[j] < quant){
						dichot[j] = true;
					}
				}
	            newDim = testres.length;
	            final int[][][] testtran = create3WaysContingencyTable(testres, newDim);
	        	final double g2 = tripleForLoop(testtran);
				bic = g2 - Math.log(newDim-2) * 2.0;

			}
	        
	        final int[][] finaltran = create2WaysContingencyTable(testres, newDim);
	        final double alpha = (double) finaltran[0][1]/(finaltran[0][0] + finaltran[0][1]);
	        final double beta =  (double) finaltran[1][0]/(finaltran[1][0] + finaltran[1][1]);
	        
			final double tempburn = Math.log( (convergeEps * (alpha + beta)) / Math.max(alpha, beta) ) 
					/ (Math.log(Math.abs(1 - alpha - beta)));
			final int nburn = (int) Math.ceil(tempburn) * kthin;
			final double tempprec = ((2 - alpha - beta) * alpha * beta * phi * phi)
					/ (Math.pow((alpha + beta), 3) * r * r);				
        
		    final int nkeep = (int) Math.ceil(tempprec) * kthin;
			double iRatio = (nburn + nkeep)/nmin;
			convergeStat.put(key, iRatio);
			if(debug == true){
				System.out.println(nburn +"\t"+ (nkeep+nburn) +"\t"+ nmin +"\t"+ iRatio);
			}
		}
    }


	/*
	     raftery.diag
	function (data, q = 0.025, r = 0.005, s = 0.95, converge.eps = 0.001) 
	{
	    if (is.mcmc.list(data)) 
	        return(lapply(data, raftery.diag, q, r, s, converge.eps))
	    data <- as.mcmc(data)
	    resmatrix <- matrix(nrow = nvar(data), ncol = 4, dimnames = list(varnames(data, 
	        allow.null = TRUE), c("M", "N", "Nmin", "I")))
	    phi <- qnorm(0.5 * (1 + s))
	    nmin <- as.integer(ceiling((q * (1 - q) * phi^2)/r^2))
	    if (nmin > niter(data)) 
	        resmatrix <- c("Error", nmin)
	    else for (i in 1:nvar(data)) {
	        if (is.matrix(data)) {
	            quant <- quantile(data[, i, drop = TRUE], probs = q)
	            dichot <- mcmc(data[, i, drop = TRUE] <= quant, start = start(data), 
	                end = end(data), thin = thin(data))
	        }
	        else {
	            quant <- quantile(data, probs = q)
	            dichot <- mcmc(data <= quant, start = start(data), 
	                end = end(data), thin = thin(data))
	        }
	        kthin <- 0
	        bic <- 1
	        while (bic >= 0) {
	            kthin <- kthin + thin(data)
	            testres <- as.vector(coda:::window.mcmc(dichot, thin = kthin))
	            testres <- factor(testres, levels = c(FALSE, TRUE))
	            newdim <- length(testres)
	            testtran <- table(testres[1:(newdim - 2)], testres[2:(newdim - 1)], testres[3:newdim])
	            testtran <- array(as.double(testtran), dim = dim(testtran))
	            g2 <- 0
	            for (i1 in 1:2) {
	                for (i2 in 1:2) {
	                  for (i3 in 1:2) {
	                    if (testtran[i1, i2, i3] != 0) {
	                      fitted <- (sum(testtran[i1, i2, 1:2]) * 
	                        sum(testtran[1:2, i2, i3]))/(sum(testtran[1:2, 
	                        i2, 1:2]))
	                      g2 <- g2 + testtran[i1, i2, i3] * log(testtran[i1, 
	                        i2, i3]/fitted) * 2
	                    }
	                  }
	                }
	            }
	            bic <- g2 - log(newdim - 2) * 2
	        }
	        finaltran <- table(testres[1:(newdim - 1)], testres[2:newdim])
	        alpha <- finaltran[1, 2]/(finaltran[1, 1] + finaltran[1, 
	            2])
	        beta <- finaltran[2, 1]/(finaltran[2, 1] + finaltran[2, 
	            2])
	        tempburn <- log((converge.eps * (alpha + beta))/max(alpha, 
	            beta))/(log(abs(1 - alpha - beta)))
	        nburn <- as.integer(ceiling(tempburn) * kthin)
	        tempprec <- ((2 - alpha - beta) * alpha * beta * phi^2)/(((alpha + 
	            beta)^3) * r^2)
	        nkeep <- as.integer(ceiling(tempprec) * kthin)
	        iratio <- (nburn + nkeep)/nmin
	        resmatrix[i, 1] <- nburn
	        resmatrix[i, 2] <- nkeep + nburn
	        resmatrix[i, 3] <- nmin
	        resmatrix[i, 4] <- signif(iratio, digits = 3)
	    }
	    y <- list(params = c(r = r, s = s, q = q), resmatrix = resmatrix)
	    class(y) <- "raftery.diag"
	    return(y)
	}
	<environment: namespace:coda>
	
	    */


	@Override
	public boolean hasConverged() {
		boolean converged = true;
		for (double d : convergeStat.values()) {
			if(d> rafteryStatThreshold ){
				converged = false;
				break;
			}
		}
	    return converged;
	}

	@Override
	public double[] getAllStat() {
	    return null;
	}

	@Override
	public String notConvergedSummary() {
		// TODO Auto-generated method stub
		return null;
	}

	private static double quantileType7InR(double[] x, double q) {

		final double[] tempX = x.clone();
		Arrays.sort(tempX);
		final int n = tempX.length;
		final double index = 1+(n-1)*q;
		final double lo = Math.floor(index);
		final double hi = Math.ceil(index);
		Arrays.sort(tempX);
		double qs = tempX[(int)lo-1]; 
		final double h = index - lo;
		if (h != 0){
			qs = (1 - h) * qs + h * tempX[(int)hi-1];
		}
		return qs;
		
		
	}
	private static double tripleForLoop(int[][][] testtran) {
		double g2 = 0;
		for (int i1 = 0; i1 < 2; i1++) {
			for (int i2 = 0; i2 < 2; i2++) {
				for (int i3 = 0; i3 < 2; i3++) {
					if (testtran[i1][i2][i3] != 0) {
                    	final int t1 = testtran[i1][i2][0]+testtran[i1][i2][1];
                    	final int t2 = testtran[0][i2][i3]+testtran[1][i2][i3];
                    	final double td = 0.0+testtran[0][i2][0]+testtran[0][i2][1]+testtran[1][i2][0]+testtran[1][i2][1];
                    	final double fitted = t1*t2/td;
                    	g2 +=  testtran[i1][i2][i3] * Math.log(testtran[i1][i2][i3]/fitted) * 2.0;
					}
				}
			}
		}
		return g2;
	}
	

	private static int[][] create2WaysContingencyTable(boolean[] testres, int newDim) {

    	final boolean[] b1 = Arrays.copyOfRange(testres, 0, newDim-1);
    	final boolean[] b2 = Arrays.copyOfRange(testres, 1, newDim);
    	
		final int table[][] = new int[][] { { 0, 0 }, { 0, 0 } };

    	for (int i = 0; i < b1.length; i++) {
			if( b1[i] && b2[i] ){
				table[1][1] += 1;
			}
			else if( !b1[i] && b2[i] ){
				table[0][1] += 1;
			}
			else if( b1[i] && !b2[i] ){
				table[1][0] += 1;
			}
			else if( !b1[i] && !b2[i] ){
				table[0][0] += 1;
			}
		}
    	return table;
		
	}
	
	private static int[][][] create3WaysContingencyTable(boolean[] testres, int newDim) {

    	final boolean[] b1 = Arrays.copyOfRange(testres, 0, newDim-2);
    	final boolean[] b2 = Arrays.copyOfRange(testres, 1, newDim-1);
    	final boolean[] b3 = Arrays.copyOfRange(testres, 2, newDim);
    	
    	final int table[][][] = new int[][][]{ 
    							{{0,0}, {0,0}},
    							{{0,0}, {0,0}}   	};

    	for (int i = 0; i < b1.length; i++) {
			if( b1[i] && b2[i] && b3[i]){
				table[1][1][1] += 1;
			}
			else if( !b1[i] && b2[i] && b3[i]){
				table[0][1][1] += 1;
			}
			else if( b1[i] && !b2[i] && b3[i]){
				table[1][0][1] += 1;
			}
			else if( b1[i] && b2[i] && !b3[i]){
				table[1][1][0] += 1;
			}
			else if( b1[i] && !b2[i] && !b3[i]){
				table[1][0][0] += 1;
			}
			else if( !b1[i] && b2[i] && !b3[i]){
				table[0][1][0] += 1;
			}
			else if( !b1[i] && !b2[i] && b3[i]){
				table[0][0][1] += 1;
			}
			else if( !b1[i] && !b2[i] && !b3[i]){
				table[0][0][0] += 1;
			}
		}
//    	for (final int[][] element : table) {
//    		for (int[] element2 : element) {
//    			System.out.println(Arrays.toString(element2));
//    		}
//    	}
		return table;
    	
		
//		for(kthin in 1:5){
//         	testres <- as.vector(coda:::window.mcmc(dichot, thin = kthin))
//			testres <- factor(testres, levels = c(FALSE, TRUE))
//         newdim <- length(testres)
//         testtran <- table(testres[1:(newdim - 2)], testres[2:(newdim - 1)], testres[3:newdim])
//         testtran <- array(as.double(testtran), dim = dim(testtran))
//       
//         for(i in 1:2){ for(j in 1:2){ print(testtran[i,j,]) }}
//
//		}
//		

		
	}


}
