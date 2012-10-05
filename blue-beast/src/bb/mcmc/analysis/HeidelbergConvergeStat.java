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
import java.util.HashMap;

//TODO not yet implemented class
public class HeidelbergConvergeStat extends AbstractConvergeStat {

	public static final Class<? extends ConvergeStat> thisClass = HeidelbergConvergeStat.class;
	public static final String STATISTIC_NAME = "Heidelberger and Welch's convergence diagnostic";
	public static final String SHORT_NAME = "Heidel"; // heidel.diag in R

	
    public HeidelbergConvergeStat() {
    	super(STATISTIC_NAME, SHORT_NAME);
    	System.err.println("Not yet implemented");
	}

	@Override
	public void checkConverged() {
		haveAllConverged = false;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void calculateStatistic() {
    	// TODO Auto-generated method stub
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

}
