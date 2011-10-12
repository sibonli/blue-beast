

package bb.mcmc.adapt;


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
            // TODO improve algorithm for dynamic chain length (long)
            int lengthRequired = (int) Math.round(currentState * (1 - progress));
            lengthRequired += currentState;
            if(lengthRequired > maxChainLength) {
                System.out.println("Warning: BLUE-BEAST thinks that the maxChainLength may not be long enough to converge the chain");
            }
            System.out.println("Recommend length required changed to: " + Math.min(lengthRequired, maxChainLength));
            return Math.min(lengthRequired, maxChainLength);

        }
        else {
            System.out.println("Recommend length required changed to: " + Math.min(currentState + initialCheckInterval, maxChainLength));
            return Math.min(currentState + initialCheckInterval, maxChainLength);
        }


//        return Math.min(mcmcOptions.getChainLength() + 1, maxChainLength); //temp
//        return Math.min(mcmcOptions.getChainLength() + 10, maxChainLength); //temp
    }

}
