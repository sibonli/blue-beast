

package bb.mcmc.adapt;


import bb.mcmc.analysis.ConvergenceStatistic;
import dr.inference.mcmc.MCMCOptions;

import java.util.ArrayList;
import java.util.Hashtable;

/*
* Adapt the chain length and the next interval we are checking
*/
public class AdaptChainLengthInterval {

    public static int calculateNextCheckingInterval(MCMCOptions mcmcOptions, Hashtable<String, ArrayList> traceInfo, ArrayList<ConvergenceStatistic> convergenceStatsToUse, boolean dynamicCheckingInterval){
        //AdaptChainLengthInterval();
        // TODO assess whether convergence has occurred based on the values of the summary statistics
        boolean converged = assessConvergence(convergenceStatsToUse);
        mcmcOptions.setChainLength(mcmcOptions.getChainLength() + 20); // temp

        if(!converged) {
            //TODO determine how long we need to continue running for, temporarily a constant
            return mcmcOptions.getChainLength() + 10; //temp
        }
            /* Otherwise, if the chain is converged then stop the MCMC chain */
            System.out.println("Statisics indicate that the variables are converged");
            System.out.println("Stopping BEAST analysis");
            mcmcOptions.setChainLength(mcmcOptions.getChainLength() + 1);
            return mcmcOptions.getChainLength() + 1; //temp
	}

    public static boolean assessConvergence(ArrayList<ConvergenceStatistic> convergenceStatsToUse) {
        return false;
    }


}
