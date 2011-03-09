package bb.mcmc.analysis;

import java.io.File;
import java.io.IOException;
import java.util.List;

import dr.inference.trace.LogFileTraces;
import dr.inference.trace.MarginalLikelihoodAnalysis;
import dr.inference.trace.TraceDistribution;
import dr.inference.trace.TraceException;
import dr.util.NumberFormatter;

public class InterIntraChainVarianceConvergenceStatistic implements ConvergenceStatistic {

	public void getTrace(String fileName, int inBurnin) throws TraceException, IOException {

		File file = new File(fileName);
		LogFileTraces traces = new LogFileTraces(fileName, file);
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
			TraceDistribution distribution = traces.getDistributionStatistics(i);

			double ess = distribution.getESS();
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

//	need double check the algorithm
	// x is the mean for each chain
	public double betweenChainVariance(double[] x, double mean) {
		
        double var = 0;
        int count = x.length;
        for(double aX : x) {
            if( Double.isNaN(aX) ) {
                count--;
            } else {
                double diff = aX - mean;
                var += diff * diff;
            }
        }

        if (count < 2) {
            count = 1; // to avoid division by zero
        } else {
            count = count - 1; // for ML estimate
        }
        var = var / (double) count * x.length;
        

        return var;
	}
	
	public double weightChainVar(double wVar, double bVar, int n){
		double oneOverN = 1 / (double) n;
		double weightVar = (1 - oneOverN) * wVar + oneOverN * bVar ;
		return weightVar;
		
		
	}

	public double PotentialScaleReductionFactor(double wVar, double weightVar){
		
		return Math.sqrt(weightVar/wVar);
		
	}
	

}
