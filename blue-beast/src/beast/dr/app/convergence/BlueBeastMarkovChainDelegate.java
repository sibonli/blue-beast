/*
* BlueBeastConvergenceListener.java
*
* Copyright (c) 2002-2012 Alexei Drummond, Andrew Rambaut and Marc Suchard
*
* This file is part of BEAST.
* See the NOTICE file distributed with this work for additional
* information regarding copyright ownership and licensing.
*
* BEAST is free software; you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
*  BEAST is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with BEAST; if not, write to the
* Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
* Boston, MA  02110-1301  USA
*/

package beast.dr.app.convergence;

import bb.loggers.BlueBeastLogger;
import bb.main.BlueBeast;
import bb.mcmc.analysis.ConvergeStat;
import dr.inference.markovchain.MarkovChain;
import dr.inference.markovchain.MarkovChainDelegate;
import dr.inference.mcmc.MCMCOptions;
import dr.inference.operators.OperatorSchedule;

import java.util.ArrayList;

/**
* @author Wai Lok Sibon Li
*/
public class BlueBeastMarkovChainDelegate implements MarkovChainDelegate {

    private MCMCOptions options;
    private OperatorSchedule schedule;
    private MarkovChain markovChain;
    BlueBeast bb;

    //protected HashMap<String, ArrayList<Double>> traceInfo;

    protected static ArrayList<ConvergeStat> convergenceStats;
    protected static int essLowerLimitBoundary;
    protected static double burninPercentage;
    protected static boolean dynamicCheckingInterval;
//    protected static boolean autoOptimiseWeights;
    protected static boolean optimiseChainLength;
    protected static long maxChainLength;
    protected static long initialCheckInterval;
    protected static boolean loadTracer;
    protected BlueBeastLogger blueBeastLogger;

    public BlueBeastMarkovChainDelegate(ArrayList<ConvergeStat> convergenceStatsToUse, BlueBeastLogger blueBeastLogger,
                                        int essLowerLimitBoundary, double burninPercentage, boolean dynamicCheckingInterval,
                                        boolean optimiseChainLength, long maxChainLength,
                                        long initialCheckInterval, boolean loadTracer) {



        this.convergenceStats = convergenceStatsToUse;
        this.blueBeastLogger = blueBeastLogger;
        this.essLowerLimitBoundary = essLowerLimitBoundary;
        this.burninPercentage = burninPercentage;
        this.dynamicCheckingInterval = dynamicCheckingInterval;
//        this.autoOptimiseWeights = autoOptimiseWeights;
        this.optimiseChainLength = optimiseChainLength;
        this.maxChainLength = maxChainLength;
        this.initialCheckInterval = initialCheckInterval;
        this.loadTracer = loadTracer;

//        blueBeastLogger = new BlueBeastLogger(formatter, logEvery, false, 10000);

    }

    public void setup(MCMCOptions options, OperatorSchedule schedule, MarkovChain markovChain) {
        this.options = options;
        this.schedule = schedule;
        this.markovChain = markovChain;


        bb = new BlueBeast(schedule, options, markovChain, convergenceStats, blueBeastLogger,
                     essLowerLimitBoundary, burninPercentage, dynamicCheckingInterval,
                optimiseChainLength, maxChainLength, initialCheckInterval, loadTracer);
//        bb.setCheckInterval(initialCheckInterval);
//        bb = new BlueBeast(/* All the input parameters for BB */);
    }

    public void currentState(long state) {
    }

    public void currentStateEnd(long state) {
        bb.checkThinLog();
        if(state == bb.getNextCheckChainLength()) {
            System.out.println("Performing a check at current state " + state);
            boolean chainConverged = bb.check(state);
//            if(chainConverged && !loadTracer) {
//                if(loadTracer) {
//                    System.out.println("Loading Tracer option set, opening Tracer with log file. Please exit BEAST manually");
//                    options.setChainLength(maxChainLength);
//                    ReportUtils.writeBBLogToFile(traceInfo, tempFileName);
//                    InstantiableTracerApp.loadInstantiableTracer("BLUE-BEAST (Tracer)", tempFileName, (long) (burninPercentage * mcmcOptions.getChainLength()));
//                }
//                else {
//                System.out.println("get chain " + options.getChainLength());
//                options.setChainLength(state+1);
//                System.out.println("get chain " + options.getChainLength() + "\t" + state);
//
//                }
//            }
//            long time = System.currentTimeMillis();
//            chainConverged = bb.check(currentState);
//            nextCheckInterval = bb.getNextCheckChainLength();
//            time = (System.currentTimeMillis() - time) / 1000;
//            System.out.println("BLUE-BEAST check took " + time + " seconds.");
//            if(chainConverged) {
//                pleaseStop();
//            }
        }


//        if(currentState==nextCheckInterval) {
//                System.out.println("Performing a check at current state " + currentState);
//                long time = System.currentTimeMillis();
//                chainConverged = bb.check(currentState);
//                nextCheckInterval = bb.getNextCheckChainLength();
//                time = (System.currentTimeMillis() - time) / 1000;
//                System.out.println("BLUE-BEAST check took " + time + " seconds.");
//                if(chainConverged) {
//                    pleaseStop();
//                }
//            }

//        check();
//        logData();


    }

    public void finished(long chainLength) {}
}
