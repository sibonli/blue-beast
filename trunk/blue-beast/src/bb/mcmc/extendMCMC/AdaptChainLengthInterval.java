package bb.mcmc.extendMCMC;


import bb.mcmc.analysis.ConvergeStat;
import dr.inference.mcmc.MCMCOptions;

import java.util.ArrayList;
import java.util.Hashtable;

/*
* Adapt the chain length and the next interval we are checking
*/
public class AdaptChainLengthInterval {

    public static int calculateNextCheckingInterval(double progress,
                                                    boolean dynamicCheckingInterval, int maxChainLength, int initialCheckInterval, int currentState){

        if(dynamicCheckingInterval) {

            //int lengthRequired = (int) Math.round(currentState * (1 - progress));
            int lengthRequired = (int) Math.round(currentState / progress);
            //lengthRequired += currentState;
            if(lengthRequired > maxChainLength) {
                System.out.println("Warning: BLUE-BEAST thinks that the maxChainLength may not be long enough to converge the chain");
            }

            // TODO improve algorithm for dynamic chain length. Change below (long)
            if(progress < 0.5) {
                lengthRequired /= 2; // Just so that checks are more frequent when the chain hasn't stabilized yet, arbitrary value at this point
            }

            int checkInterval = Math.min(lengthRequired, maxChainLength);
            if(checkInterval < currentState) {
                throw new RuntimeException("Check interval is set to after the current state. Contact Sibon Li");
            }
            System.out.println("Next check will be performed at: " + Math.min(lengthRequired, maxChainLength));
            return checkInterval;

        }
        else {
            System.out.println("Next check will be performed at: " + Math.min(currentState + initialCheckInterval, maxChainLength));
            return Math.min(currentState + initialCheckInterval, maxChainLength);
        }


//        return Math.min(mcmcOptions.getChainLength() + 1, maxChainLength); //temp
//        return Math.min(mcmcOptions.getChainLength() + 10, maxChainLength); //temp
    }

}
