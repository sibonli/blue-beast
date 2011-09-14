/*
 * BlueBeastMain.java
 *
 * Copyright (C) 2011 Wai Lok Sibon Li and Steven Wu
 *
 * This file is part of BLUE BEAST.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership and licensing.
 *
 * BLUE BEAST is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 *  BLUE BEAST is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with BLUE BEAST; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301  USA
 *
 * @author Wai Lok Sibon Li
 *
 */

package bb.main;

import bb.mcmc.analysis.ConvergeStat;
import bb.mcmc.analysis.ESSConvergeStat;
import bb.mcmc.analysis.GelmanConvergeStat;
import bb.mcmc.analysis.GewekeConvergeStat;
import bb.mcmc.analysis.ZTempNovelConvergenceStatistic;
import dr.app.util.Arguments;
import dr.app.util.Utils;
import dr.inference.mcmc.MCMCOptions;
import dr.inference.model.Parameter;
import dr.inference.operators.MCMCOperator;
import dr.inference.operators.OperatorSchedule;
import dr.inference.operators.ScaleOperator;
import dr.inference.operators.SimpleOperatorSchedule;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This class is only used when using Blue BEAST for an external program (i.e. not BEAST)
 * Is not needed otherwise (I think)
 *
 */
public class BlueBeastMain {

    public static final String version = "0.1";

    //TODO make sure these values reflect the values in BlueBeast.java
    protected static int essLowerLimitBoundary = 100;
    protected static double burninPercentage = 0.1;
    protected static boolean dynamicCheckingInterval = true;
    protected static boolean autoOptimiseWeights = true;
    protected static boolean optimiseChainLength = true;
    protected static int maxChainLength = Integer.MAX_VALUE;
    protected static ArrayList<ConvergeStat> convergenceStatsToUse;



    protected static final String CITATION = "";




    protected static void printTitle() {
        System.out.println();
        centreLine("BLUE BEAST - Bayesian Likelihood Usability Extension for BEAST", 60);
        centreLine("Version " + version + ", " + "2011", 60);
//				version.getVersionString() + ", " + version.getDateString(), 60);
        System.out.println();
        centreLine("by", 60);
        System.out.println();
        centreLine("Wai Lok Sibon Li and Steven H. Wu", 60);
        System.out.println();
        centreLine("Departments of Biostatistics and Human Genetics", 60);
        centreLine("UCLA", 60);
        centreLine("sibonli@ucla.edu", 60);
        System.out.println();
        centreLine("Department of Biology", 60);
        centreLine("Duke University", 60);
        centreLine("steven.wu@duke.edu", 60);
        System.out.println();
        System.out.println();
    }
    protected static void centreLine(String line, int pageWidth) {
        int n = pageWidth - line.length();
        int n1 = n / 2;
        for (int i = 0; i < n1; i++) {
            System.out.print(" ");
        }
        System.out.println(line);
    }
    protected static void printUsage(Arguments arguments) {

        arguments.printUsage("BlueBeastMain", "<input-file-name> <output-file-name>");
        System.out.println();
        System.out.println("  Example: BlueBeastMain bb.test.log out.txt");
        System.out.println();
    }


	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		testCase(args);
		
		testCase2();
		
	}
	
	public static void testCase(String[] args){
        printTitle();

        String inputFileName = null;
        String targetTreeFileName = null;
        String outputFileName = null;



        Arguments arguments = new Arguments(
                new Arguments.Option[]{
                        new Arguments.IntegerOption("essLowerLimitBoundary", "Minimum ESS required to consider the chain converged (default: 100)"),
                        new Arguments.Option("dynamicCheckingInterval", "Whether the interval between checks for convergence are constant or dynamic (default: used)"),
                        new Arguments.Option("autoOptimiseWeights", "Whether proposal kernel weights/acceptance ratios are automatically adjusted (default: used)"),
                        new Arguments.Option("optimiseChainLength", "Whether the MCMC chain length is automatically adjusted (default: used)"),
                        new Arguments.IntegerOption("maxChainLength", "Maximum Markov chain length that will be run (default: " + Integer.MAX_VALUE + ")"),
                        new Arguments.RealOption("burninPercentage", "Percentage of the length of the Markov chain which is treated as burnin at each checkpoint (default: 10% )"),
                        new Arguments.StringOption("convergenceStatsToUse", new String[]{"all", "ESS", "interIntraChainVariance"}, false, "The statistics used to assess convergence of the chain (default: all)"),
                });
        try {
            arguments.parseArguments(args);
        } catch (Arguments.ArgumentException ae) {
            System.out.println(ae);
            printUsage(arguments);
            System.exit(1);
        }

        if (arguments.hasOption("help")) {
            printUsage(arguments);
            System.exit(0);
        }
        if (arguments.hasOption("essLowerLimitBoundary")) {
            essLowerLimitBoundary = arguments.getIntegerOption("essLowerLimitBoundary");
        }
        if (arguments.hasOption("maxChainLength")) {
            maxChainLength = arguments.getIntegerOption("maxChainLength");
        }
        if(arguments.hasOption("burninPercentage")) {
            burninPercentage = arguments.getRealOption("burninPercentage");
            if(burninPercentage>=1.0 || burninPercentage<0) {
                throw new RuntimeException("Burning percentage invalid, cannot be " + burninPercentage);
            }
        }
        dynamicCheckingInterval = arguments.hasOption("dynamicCheckingInterval");
        autoOptimiseWeights = arguments.hasOption("autoOptimiseWeights");
        if(autoOptimiseWeights) {
            //TODO prompt a file input/input file name to get some operator weights. Output to console?
        }
        optimiseChainLength = arguments.hasOption("optimiseChainLength");
        String convergenceStatsToUseParameters = "all";
        if (arguments.hasOption("convergenceStatsToUse")) {
            convergenceStatsToUseParameters = arguments.getStringOption("convergenceStatsToUse");
        }
        if(convergenceStatsToUseParameters.equals("all")) {
            convergenceStatsToUse.add(ESSConvergeStat.INSTANCE);
            convergenceStatsToUse.add(GelmanConvergeStat.INSTANCE);
            convergenceStatsToUse.add(ZTempNovelConvergenceStatistic.INSTANCE);
        }
        else if(convergenceStatsToUseParameters.equals("ESS")) {
            convergenceStatsToUse.add(ESSConvergeStat.INSTANCE);
        }
        if(convergenceStatsToUseParameters.equals("interIntraChainVariance")) {
            convergenceStatsToUse.add(GelmanConvergeStat.INSTANCE);
        }

        //TODO instead of using leftover arguments, using proper file indicating options
        String[] args2 = arguments.getLeftoverArguments();
        if (args2.length == 2) {
            targetTreeFileName = null;
            inputFileName = args2[0];
            outputFileName = args2[1];
        }
        else {

            if (inputFileName == null) {
               // No input file name was given so throw up a dialog box...
                inputFileName = Utils.getLoadFileName("BLUE BEAST " + version + " - Select input file file to analyse");
            }
            if (outputFileName == null) {
                outputFileName = Utils.getSaveFileName("BLUE BEAST " + version + " - Select output file");

            }
        }

        if(inputFileName == null || outputFileName == null) {
            System.err.println("Missing input or output file name");
            printUsage(arguments);
            System.exit(1);

        }

        //TODO Parse and read-in MCMC operators etc.
        MCMCOperator[] operators = new MCMCOperator[10]; // Need to do this properly
        OperatorSchedule opSche = new SimpleOperatorSchedule(); // Need to do this properly
        for (MCMCOperator mcmcOperator : operators) {
        	opSche.addOperator(mcmcOperator);
		}
        MCMCOptions mcmcOptions = new MCMCOptions(); // Need to do this properly
        new BlueBeast(opSche, mcmcOptions, convergenceStatsToUse, outputFileName);
        System.exit(0);

	}

	public static void testCase2(){
		
		
	    
	    
	    String[] variableNames = {"Sneezy", "Sleepy", "Dopey", "Doc", "Happy", "Bashful", "Grumpy"};
	    
	    MCMCOperator[] operators = new MCMCOperator[variableNames.length];
        /* We can easily change to see if it works for other operators too */
        operators[0] = new ScaleOperator(new Parameter.Default(0.0), 0.75);
        operators[1] = new ScaleOperator(new Parameter.Default(0.1), 0.75);
        operators[2] = new ScaleOperator(new Parameter.Default(0.2), 0.75);
        operators[3] = new ScaleOperator(new Parameter.Default(0.3), 0.75);
        operators[4] = new ScaleOperator(new Parameter.Default(0.4), 0.75);
        operators[5] = new ScaleOperator(new Parameter.Default(0.5), 0.75);
        operators[6] = new ScaleOperator(new Parameter.Default(0.6), 0.75);
        MCMCOptions mcmcOptions = new MCMCOptions(); // Need to do this properly

        convergenceStatsToUse = new ArrayList<ConvergeStat>();
        convergenceStatsToUse.add(ESSConvergeStat.INSTANCE);
        convergenceStatsToUse.add(GewekeConvergeStat.INSTANCE);


        essLowerLimitBoundary = 5;
        burninPercentage = 0;
        dynamicCheckingInterval =true;
        autoOptimiseWeights = true;
        optimiseChainLength = true;
        maxChainLength = 100;

        OperatorSchedule opSche = new SimpleOperatorSchedule(); 
        for (MCMCOperator mcmcOperator : operators) {
        	opSche.addOperator(mcmcOperator);
		}
        BlueBeast bb = new BlueBeast(opSche, mcmcOptions, convergenceStatsToUse, variableNames,
                     essLowerLimitBoundary, burninPercentage, dynamicCheckingInterval,
                     autoOptimiseWeights, optimiseChainLength, maxChainLength);
        System.out.println(Arrays.toString(variableNames));
	    bb.test();
	    
	    
	       
	    
	}
	
}