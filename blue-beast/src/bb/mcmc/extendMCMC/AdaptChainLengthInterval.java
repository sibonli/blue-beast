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
 */
package bb.mcmc.extendMCMC;


import bb.mcmc.analysis.ConvergeStat;
import bb.mcmc.analysis.GewekeConvergeStat;
import bb.mcmc.analysis.RafteryConvergeStat;

import java.util.ArrayList;
import java.util.HashMap;

/*
* Adapt the chain length and the next interval we are checking
* @author Wai Lok Sibon Li
*/
public class AdaptChainLengthInterval {

    private ArrayList<ConvergeStat> convergenceStats;
    private boolean dynamicCheckingInterval;
    private long maxChainLength;
    private long initialCheckInterval;
    private int logEvery;
    private double burninPercentage;
    private HashMap<Class<? extends ConvergeStat>, ConvergeStat> csMaps;

    public AdaptChainLengthInterval(ArrayList<ConvergeStat> convergenceStats, boolean dynamicCheckingInterval,
                                                        long maxChainLength, long initialCheckInterval, int logEvery, double burninPercentage) {

        this.convergenceStats = convergenceStats;
        this.dynamicCheckingInterval = dynamicCheckingInterval;
        this.maxChainLength = maxChainLength;
        this.initialCheckInterval = initialCheckInterval;
        this.logEvery = logEvery;
        this.burninPercentage = burninPercentage;
        csMaps = new HashMap<Class<? extends ConvergeStat>, ConvergeStat>(convergenceStats.size());
        for(ConvergeStat cs : convergenceStats) {
            csMaps.put(cs.getClass(), cs);
        }
    }


    public long calculateNextCheckingInterval(double progress, double lastObservableProgress, long currentState){

        if(Double.isNaN(progress)) { // Progress is not yet calculable.

            if(csMaps.containsKey(RafteryConvergeStat.THIS_CLASS)) {
//                RafteryConvergeStat rafteryConvergeStat = (RafteryConvergeStat) csMaps.get(RafteryConvergeStat.thisClass);
                int nmin = ((RafteryConvergeStat) csMaps.get(RafteryConvergeStat.THIS_CLASS)).getNMin();
                long rafteryThreshold = (long) Math.ceil((nmin * logEvery) / (1 - burninPercentage ));
//                System.out.println("RAFTERY IS USED " + nmin + " " + rafteryThreshold + " " + burninPercentage + " " + currentState);

                if(currentState >= rafteryThreshold) { // Sanity check. Raftery threshold has already been passed, error
                    new RuntimeException("Error in Raftery convergence statistic calculation. Please contact Sibon Li. ");
                }
                return ceilBySample(rafteryThreshold);
            }
            else if (csMaps.containsKey(GewekeConvergeStat.THIS_CLASS)) {
//                System.out.println("GEWEKE IS USED");
//                long t = currentState / logEvery;
                return logEvery * ((currentState / logEvery)+1); // Next time a sample happens
            }
            else {
                new RuntimeException("Error in convergence statistic calculation (NaN value not related to Raftery or Geweke). Please contact Sibon Li. ");
                return -1;
            }
        }
        else {
            if(dynamicCheckingInterval) {

                //int lengthRequired = (int) Math.round(currentState * (1 - progress));
                long lengthRequired = Math.round(currentState / progress) + 1;  // +1 is so that it at least moves one iteration
                //lengthRequired += currentState;
                if(lengthRequired > maxChainLength) {
                    System.out.println("Warning: BLUE-BEAST thinks that the maxChainLength may not be long enough to converge the chain");
                }

                // TODO improve algorithm for dynamic chain length. Change below (long)
                if(progress < 0.2) {
                    lengthRequired = currentState * 2 + 1;
    //                System.out.println("progress < 0.2 " + lengthRequired);
                }
                else if(progress < 0.5) {
                    lengthRequired = lengthRequired / 2 + 1; // Just so that checks are more frequent when the chain hasn't stabilized yet, arbitrary value at this point
    //                System.out.println("progress < 0.5 " + lengthRequired);
                }

                long checkInterval = Math.min(lengthRequired, maxChainLength);
                if(checkInterval < currentState) {
                    if (Double.isNaN(progress)) {
                        System.err.println("WARNING: BLUE BEAST thinks something is wrong with the BEAST run (progress indicators = NaN) but will not intervene. ");
                    }
                    else {
                        throw new RuntimeException("Check interval is set to before the current state (" + checkInterval + ", " + currentState +  ", " + progress +  ", " + maxChainLength +  ", " + initialCheckInterval + "). Contact Sibon Li");
                    }
                }
//                System.out.println("Next check will be performed at: " + checkInterval);
                return ceilBySample(checkInterval);

            }
            else {
                long checkInterval = Math.min(currentState + initialCheckInterval, maxChainLength);
                System.out.println("Next check will be performed at: " + checkInterval);
                return checkInterval;
            }
        }


//        return Math.min(mcmcOptions.getChainLength() + 1, maxChainLength); //temp
//        return Math.min(mcmcOptions.getChainLength() + 10, maxChainLength); //temp
    }

    private long ceilBySample(long n) {
        return n - (n % logEvery) + logEvery;
    }
}
