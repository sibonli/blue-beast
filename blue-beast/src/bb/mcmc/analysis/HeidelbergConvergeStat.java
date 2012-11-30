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
import java.util.Deque;
import java.util.HashMap;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.MathUtils;

import cern.jet.math.Bessel;
import cern.jet.stat.Gamma;

import umontreal.iro.lecuyer.probdist.CramerVonMisesDist;

import dr.stats.DiscreteStatistics;


//TODO not yet implemented class
public class HeidelbergConvergeStat extends AbstractConvergeStat {

	public static final Class<? extends ConvergeStat> THIS_CLASS = HeidelbergConvergeStat.class;
	public static final String STATISTIC_NAME = "Heidelberger and Welch's convergence diagnostic";
	public static final String SHORT_NAME = "Heidel"; // heidel.diag in R
	private static final double LOG_EPS = Math.log(1e-5);
	
	private double eps; // default 0.1;
	private double pvalue; // default 0.05;
	private double heidelThreshold;

    public HeidelbergConvergeStat() {
    	super(STATISTIC_NAME, SHORT_NAME);
	}

	public HeidelbergConvergeStat(double eps, double pvalue, double heidelThreshold) {
		//TODO(SW) think about whether we want empty constructor? 
		//keep it for now because we used it quite a bit is the progressReporter
		//this();
		super(STATISTIC_NAME, SHORT_NAME);
		this.eps = eps;
		this.pvalue = pvalue;
		this.heidelThreshold = heidelThreshold;
		
	}

//
//	@Override
//	public void calculateStatistic() {
//
//		for (String key : convergeStat.keySet()) {
//			System.out.println("Calculating "+STATISTIC_NAME+": "+key);
//
//			final double heidelStat = calculateHeidelStat(key);
//			convergeStat.put(key, heidelStat );
//			
//		}
//		checkConverged();
//		calculateOverallProgress();
//    }

	@Override
	protected double calculateEachStat(String key){

		double[] t = traceValues.get(key);
		t = Arrays.copyOfRange(t, 0, 48);
		int length = t.length;
System.out.println(length +"\t"+ Arrays.toString(t)+"\t"+ length);
//		length=13578;
//		t = new double[length];
		

		
		int indexStart = (int) Math.ceil(length/2.0-1) ;
//		System.out.println(length +"\t"+  indexStart);
		double[] Y = Arrays.copyOfRange(t, indexStart, length);
		double S0 = ConvergeStatUtils.spectrum0(Y);
		System.out.println(Y.length +"\t"+ indexStart +"\t"+ S0 +"\t"+ Arrays.toString(Y) );
		
		
		
		final double oneTenthLength = length/10.0;
		
		for (int i = 0; i < 5; i++) {
			int startVec = (int) Math.ceil(0 + i*oneTenthLength);
			Y = Arrays.copyOfRange(t, startVec, length);
			int n = Y.length;
			double ybar =  DiscreteStatistics.mean(Y);
			double nS0 = n*S0;
			
			double[] B = new double[n];
			double[] Bsq = new double[n];
			B[0] = Y[0] - ybar;
			
			for (int j = 1; j < B.length; j++) {
				B[j] = B[j-1] + Y[j] - ybar;
			}
			for (int j = 0; j < Bsq.length; j++) {
				 Bsq[j] = B[j]*B[j] / nS0; 
			}
			double ind = StatUtils.sum(Bsq)/n;
			
//			Bsq <- (B * B)/(n * S0)
			
			System.out.println(n +"\t"+ S0 +"\t"+  
					StatUtils.sum(B) +"\t"+ StatUtils.sum(Bsq) +"\t"+ ind);
			
			CramerVonMisesDist dist = new CramerVonMisesDist(2);
			System.out.println(dist.barF(ind));
			System.out.println(dist.cdf(ind));
			System.out.println(dist.density(ind));
			System.out.println(dist.density(ind));
//			The Cramer-von Mises Distribution
			boolean ind2 = pcramer(ind); // TODO:

			if(ind2){
				break;
			}
			
			
//		    int output_list[] = new int[input_list.length];
//		    long startTime = System.currentTimeMillis();
//		    for (int i = 1; i < input_list.length; i++)
//		         output_list[i] = output_list[i-1] + input_list[i];
//		    long endTime = System.currentTimeMillis();

//			System.out.println(Y.length +"\t"+ ybar);
			
			
//			for (i in seq(along = start.vec)) {
//	            Y <- window(Y, start = start.vec[i])
//	            n <- niter(Y)
//	            ybar <- mean(Y)
//	            B <- cumsum(Y) - ybar * (1:n)
//	            Bsq <- (B * B)/(n * S0)
//	            I <- sum(Bsq)/n
//	            if (converged <- !is.na(I) && pcramer(I) < 1 - pvalue) 
//	                break
//	        }
		}
		double S0ci = ConvergeStatUtils.spectrum0(Y);
		double halfwidth = 1.96 * Math.sqrt(S0ci/Y.length);
		
//		System.out.println(oneTenthLength +"\t"+ Arrays.toString(startVec));
		
		
//		final double[] t = traceValues.get(key);
//		final int length = t.length;
    	
//    	final int indexEnd   = (int) Math.ceil(length * frac1);
//    	
//    	final double[] dStart = Arrays.copyOfRange(t, 0, indexEnd);
//		final double[] dEnd = Arrays.copyOfRange(t, indexStart, length);
//		
		
		return 0;
	}

	private boolean pcramer(double q) {
		// TODO Auto-generated method stub
//		LOG_EPS;
		double[] y = new double[4];
		double sqrtQ = Math.sqrt(q);
		for (int k = 0; k < y.length; k++) {
			double k4Plus1 = 4*k + 1;
			double z = Gamma.gamma(k+0.5) * Math.sqrt(k4Plus1)/
					(Gamma.gamma(k+1) * Math.pow(Math.PI, 1.5)* sqrtQ);
			double u = k4Plus1 * k4Plus1 / 16 / q;
//			The Cramer-von Mises Distribution
			
			System.out.println("==\t" + k + "\t" + z + "\t" + u + "\t"
					+ Bessel.kn(0, u) + "\t" + kn2(0.25, u));
	if (u > -LOG_EPS){
				Bessel.kn(1/4, u); //TODO: double check with R
			}
			
		}
		System.exit(-1);
		
//		R output
//		+         print(paste(z,u, besselK(x = u, nu = 1/4) ))
//		+     }
//		[1] "0.182668671185458 0.0205829633998658 4.91169288129494"
//		[1] "0.204229783065121 0.514574084996645 0.934986263984243"
//		[1] "0.205502255083641 1.66722003538913 0.175141220098447"
//		[1] "0.205819143868763 3.47852081457732 0.0202433768572695"

//		> pcramer
//		function (q, eps = 1e-05) 
//		{
//		    log.eps <- log(eps)
//		    y <- matrix(0, nrow = 4, ncol = length(q))
//		    for (k in 0:3) {
//		        z <- gamma(k + 0.5) * sqrt(4 * k + 1)/(gamma(k + 1) * 
//		            pi^(3/2) * sqrt(q))
//		        u <- (4 * k + 1)^2/(16 * q)
//		        y[k + 1, ] <- ifelse(u > -log.eps, 0, z * exp(-u) * besselK(x = u, 
//		            nu = 1/4))
//		    }
//		    return(apply(y, 2, sum))
//		}

		return false;
	}

	
	/*
	heidel.diag
function (x, eps = 0.1, pvalue = 0.05) 
{
    if (is.mcmc.list(x)) 
        return(lapply(x, heidel.diag, eps))
    x <- as.mcmc(as.matrix(x))
    HW.mat0 <- matrix(0, ncol = 6, nrow = nvar(x))
    dimnames(HW.mat0) <- list(varnames(x), c("stest", "start", 
        "pvalue", "htest", "mean", "halfwidth"))
    HW.mat <- HW.mat0
    for (j in 1:nvar(x)) {
        start.vec <- seq(from = start(x), to = end(x)/2, by = niter(x)/10)
        Y <- x[, j, drop = TRUE]
        n1 <- length(Y)
        S0 <- spectrum0(window(Y, start = end(Y)/2))$spec
        converged <- FALSE
        for (i in seq(along = start.vec)) {
            Y <- window(Y, start = start.vec[i])
            n <- niter(Y)
            ybar <- mean(Y)
            B <- cumsum(Y) - ybar * (1:n)
            Bsq <- (B * B)/(n * S0)
            I <- sum(Bsq)/n
            if (converged <- !is.na(I) && pcramer(I) < 1 - pvalue) 
                break
        }
        S0ci <- spectrum0(Y)$spec
        halfwidth <- 1.96 * sqrt(S0ci/n)
        passed.hw <- !is.na(halfwidth) & (abs(halfwidth/ybar) <= 
            eps)
        if (!converged || is.na(I) || is.na(halfwidth)) {
            nstart <- NA
            passed.hw <- NA
            halfwidth <- NA
            ybar <- NA
        }
        else {
            nstart <- start(Y)
        }
        HW.mat[j, ] <- c(converged, nstart, 1 - pcramer(I), passed.hw, 
            ybar, halfwidth)
    }
    class(HW.mat) <- "heidel.diag"
    return(HW.mat)
}

	*/

	
	
	
	/**
	 * Returns the modified Bessel function of the third kind
	 * of order <tt>nn</tt> of the argument.
	 * <p>
	 * The range is partitioned into the two intervals [0,9.55] and
	 * (9.55, infinity).  An ascending power series is used in the
	 * low range, and an asymptotic expansion in the high range.
	 *
	 * @param nn the order of the Bessel function.
	 * @param x the value to compute the bessel function of.
	 */
	static public double kn2(double nd, double x) throws ArithmeticException {
	/*
	Algorithm for Kn.
						   n-1 
					   -n   -  (n-k-1)!    2   k
	K (x)  =  0.5 (x/2)     >  -------- (-x /4)
	 n                      -     k!
						   k=0
	
						inf.                                   2   k
		   n         n   -                                   (x /4)
	 + (-1)  0.5(x/2)    >  {p(k+1) + p(n+k+1) - 2log(x/2)} ---------
						 -                                  k! (n+k)!
						k=0
	
	where  p(m) is the psi function: p(1) = -EUL and
	
						  m-1
						   -
		  p(m)  =  -EUL +  >  1/k
						   -
						  k=1
	
	For large x,
											 2        2     2
										  u-1     (u-1 )(u-3 )
	K (z)  =  sqrt(pi/2z) exp(-z) { 1 + ------- + ------------ + ...}
	 v                                        1            2
										1! (8z)     2! (8z)
	asymptotically, where
	
			   2
		u = 4 v .
	
	*/
		final double EUL = 5.772156649015328606065e-1;
		final double MAXNUM = Double.MAX_VALUE;
		final int MAXFAC = 31;
		int nn;// = (int) nd;
	
		double k, kf, nk1f, nkf, zn, t, s, z0, z;
		double ans, fn, pn, pk, zmn, tlg, tox;
		int i;//, n;
	
		if( nd < 0 )
			nn = (int) -nd;
		else
			nn = (int) nd;
	
		if( nn > MAXFAC ) throw new ArithmeticException("Overflow");
		if( x <= 0.0 ) throw new IllegalArgumentException();
	
		if( x <= 9.55 ) {
			ans = 0.0;
			z0 = 0.25 * x * x;
			fn = 1.0;
			pn = 0.0;
			zmn = 1.0;
			tox = 2.0/x;
	
			if( nn > 0 ) {
				/* compute factorial of n and psi(n) */
				pn = -EUL;
				k = 1.0;
				for( i=1; i<nn; i++ ) {
					pn += 1.0/k;
					k += 1.0;
					fn *= k;
				}
	
				zmn = tox;
	
				if( nn == 1 ) {
					ans = 1.0/x;
				}
				else {
					nk1f = fn/nd;
					kf = 1.0;
					s = nk1f;
					z = -z0;
					zn = 1.0;
					for( i=1; i<nn; i++ ) {
						nk1f = nk1f/(nn-i);
						kf = kf * i;
						zn *= z;
						t = nk1f * zn / kf;
						s += t;   
						if( (MAXNUM - Math.abs(t)) < Math.abs(s) ) throw new ArithmeticException("Overflow");
						if( (tox > 1.0) && ((MAXNUM/tox) < zmn) ) throw new ArithmeticException("Overflow");
						zmn *= tox;
					}
					s *= 0.5;
					t = Math.abs(s);
					if( (zmn > 1.0) && ((MAXNUM/zmn) < t) )  throw new ArithmeticException("Overflow");
					if( (t > 1.0) && ((MAXNUM/t) < zmn) )  throw new ArithmeticException("Overflow");
					ans = s * zmn;
				}
			}
	
	
			tlg = 2.0 * Math.log( 0.5 * x );
			pk = -EUL;
			if( nn == 0 ) {
				pn = pk;
				t = 1.0;
			}
			else {
				pn = pn + 1.0/nd;
				t = 1.0/fn;
			}
			s = (pk+pn-tlg)*t;
			k = 1.0;
			do {
				t *= z0 / (k * (k+nd));
				pk += 1.0/k;
				pn += 1.0/(k+nd);
				s += (pk+pn-tlg)*t;
				k += 1.0;
				}
			while( Math.abs(t/s) > MACHEP );
	
			s = 0.5 * s / zmn;
			if( (nn & 1) > 0)
				s = -s;
			ans += s;
	
			return(ans);
		}
	
	
	
		/* Asymptotic expansion for Kn(x) */
		/* Converges to 1.4e-17 for x > 18.4 */
		if( x > MAXLOG )  throw new ArithmeticException("Underflow");
		k = nd;
		pn = 4.0 * k * k;
		pk = 1.0;
		z0 = 8.0 * x;
		fn = 1.0;
		t = 1.0;
		s = t;
		nkf = MAXNUM;
		i = 0;
		do {
			z = pn - pk * pk;
			t = t * z /(fn * z0);
			nk1f = Math.abs(t);
			if( (i >= nn) && (nk1f > nkf) ) {
				ans = Math.exp(-x) * Math.sqrt( Math.PI/(2.0*x) ) * s;
				return(ans);
			}
			nkf = nk1f;
			s += t;
			fn += 1.0;
			pk += 2.0;
			i += 1;
		} while( Math.abs(t/s) > MACHEP );
	
	
		ans = Math.exp(-x) * Math.sqrt( Math.PI/(2.0*x) ) * s;
		return(ans);
	}
	protected static final double MACHEP =  1.11022302462515654042E-16;
	protected static final double MAXLOG =  7.09782712893383996732E2;

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
}
