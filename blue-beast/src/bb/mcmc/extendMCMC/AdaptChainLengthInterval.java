package bb.mcmc.extendMCMC;


import bb.mcmc.analysis.ConvergeStat;
import dr.inference.mcmc.MCMCOptions;

import java.util.ArrayList;
import java.util.HashMap;

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
            if(progress < 0.2) {
                lengthRequired = currentState * 2;
                System.out.println("progress < 0.2 " + lengthRequired);
            }
            else if(progress < 0.5) {
                lengthRequired /= 2; // Just so that checks are more frequent when the chain hasn't stabilized yet, arbitrary value at this point
                System.out.println("progress < 0.5 " + lengthRequired);
            }

            int checkInterval = Math.min(lengthRequired, maxChainLength);
            if(checkInterval < currentState) {
                if (new Double(progress).isNaN()) {
                    System.out.println("WARNING: BLUE BEAST thinks something is wrong with the BEAST run (progress indicators = NaN) but will not intervene. ");
                }
                else {
                    throw new RuntimeException("Check interval is set to after the current state (" + checkInterval + ", " + currentState +  ", " + progress +  ", " + maxChainLength +  ", " + initialCheckInterval + "). Contact Sibon Li");
                }
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
