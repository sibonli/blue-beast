/*
 * BlueBeastMain.java
 *
 *  BLUE-BEAST - Bayesian and Likelihood-based methods Usability Extension
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
 * @author Wai Lok Sibon Li
 *
 */

package bb.main;

import bb.loggers.BlueBeastLogger;
import bb.mcmc.analysis.*;
import dr.app.util.Arguments;
import dr.app.util.Utils;
import dr.inference.mcmc.MCMCOptions;
import dr.inference.model.Parameter;
import dr.inference.operators.MCMCOperator;
import dr.inference.operators.OperatorSchedule;
import dr.inference.operators.ScaleOperator;
import dr.inference.operators.SimpleOperatorSchedule;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * This class is only used when using Blue BEAST for an external program (i.e. not BEAST)
 * Is not needed otherwise (I think)
 *
 */
public class BlueBeastMain {

    public static final String version = "0.1";

    //TODO make sure these values reflect the values in BlueBeast.java
    protected static int essLowerLimitBoundary = 200;
    protected static double burninPercentage = 0.1;
    protected static boolean dynamicCheckingInterval = true;
//    protected static boolean autoOptimiseWeights = true;
    protected static boolean optimiseChainLength = true;
    protected static long maxChainLength = Long.MAX_VALUE;
    protected static ArrayList<ConvergeStat> convergenceStatsToUse;
    protected static long initialCheckInterval = 1000;

    protected static boolean loadTracer = true;



    protected static final String CITATION = "Wai Lok Sibon Li and Steven H Wu";




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
		
		testCaseSteven();
		
//	}
//
//	public static void testCase(String[] args){
        printTitle();

        String inputFileName = null;
        String operatorInfoFileName = null;
        String outputFileName = null;
        int currentChainLength = -1;



        Arguments arguments = new Arguments(
                new Arguments.Option[]{
                        new Arguments.IntegerOption("currentChainLength", "How long the chain has currently been run for"),
                        new Arguments.IntegerOption("essLowerLimitBoundary", "Minimum ESS required to consider the chain converged (default: 100)"),
                        new Arguments.Option("dynamicCheckingInterval", "Whether the interval between checks for convergence are constant or dynamic (default: used)"),
//                        new Arguments.Option("autoOptimiseWeights", "Whether proposal kernel weights/acceptance ratios are automatically adjusted (default: used)"),
                        new Arguments.Option("optimiseChainLength", "Whether the MCMC chain length is automatically adjusted (default: used). If unused then chain length = maxChainLength"),
                        new Arguments.LongOption("maxChainLength", "Maximum Markov chain length that will be run (default: " + Integer.MAX_VALUE + ")"),
                        new Arguments.LongOption("initialCheckInterval", "Initial interval to perform Blue Beast check. If interval is not dynamic then this is the interval throughout the run"),
                        new Arguments.RealOption("burninPercentage", "Percentage of the length of the Markov chain which is treated as burnin at each checkpoint (default: 10% )"),
                        new Arguments.StringOption("convergenceStatsToUse", new String[]{"all", "ESS", "interIntraChainVariance"}, false, "The statistics used to assess convergence of the chain (default: all)"),
                        new Arguments.Option("loadTracer", "Whether to load tracer when Blue Beast thinks convergence has been reached"),
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
        if (arguments.hasOption("currentChainLength")) {
            currentChainLength = arguments.getIntegerOption("currentChainLength");
        }
        if (arguments.hasOption("essLowerLimitBoundary")) {
            essLowerLimitBoundary = arguments.getIntegerOption("essLowerLimitBoundary");
        }
        if (arguments.hasOption("maxChainLength")) {
            maxChainLength = arguments.getLongOption("maxChainLength");
        }
        if (arguments.hasOption("initialCheckInterval")) {
            initialCheckInterval = arguments.getLongOption("initialCheckInterval");
        }
        if(arguments.hasOption("burninPercentage")) {
            burninPercentage = arguments.getRealOption("burninPercentage");
            if(burninPercentage>=1.0 || burninPercentage<0) {
                throw new RuntimeException("Burning percentage invalid, cannot be " + burninPercentage);
            }
        }
        dynamicCheckingInterval = arguments.hasOption("dynamicCheckingInterval");
//        autoOptimiseWeights = arguments.hasOption("autoOptimiseWeights");
//        if(autoOptimiseWeights) {
//            //TODO prompt a file input/input file name to get some operator weights. Output to console?
//        }
        optimiseChainLength = arguments.hasOption("optimiseChainLength");

        loadTracer = arguments.hasOption("loadTracer");

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

        String[] args2 = arguments.getLeftoverArguments();

        if (args2.length == 2) {
            inputFileName = args2[0];
            outputFileName = args2[1];
        }
        else if (args2.length == 3) {
            inputFileName = args2[0];
            outputFileName = args2[1];
            operatorInfoFileName = args2[2];
        }
        else if (args2.length == 1) {
            if (outputFileName == null) {
                outputFileName = Utils.getSaveFileName("BLUE-BEAST " + version + " - Select output file");
            }
            if (operatorInfoFileName == null) {
                operatorInfoFileName = Utils.getSaveFileName("BLUE-BEAST " + version + " - Select output file");
            }
        }
        else {
            if (inputFileName == null) {
               // No input file name was given so throw up a dialog box...
                inputFileName = Utils.getLoadFileName("BLUE-BEAST " + version + " - Select input file file to analyse");
            }
            if (outputFileName == null) {
                outputFileName = Utils.getSaveFileName("BLUE-BEAST " + version + " - Select output file");
            }
            if (operatorInfoFileName == null) {
                operatorInfoFileName = Utils.getSaveFileName("BLUE-BEAST " + version + " - Select output file");
            }
        }

        if(inputFileName == null || outputFileName == null) {
            System.err.println("Missing input or output file name");
            printUsage(arguments);
            System.exit(1);

        }

        MCMCOperator[] operators = null;
        MCMCOptions mcmcOptions = null;
        OperatorSchedule opSche = null;

//            new BlueBeast(null, null, convergenceStatsToUse, essLowerLimitBoundary, burninPercentage,
//                    dynamicCheckingInterval, autoOptimiseWeights, optimiseChainLength, maxChainLength,
//                    initialCheckInterval, outputFileName);

        if(operatorInfoFileName != null) {
            //TODO Parse and read-in MCMC operators etc (long).
            operators = new MCMCOperator[10]; // Need to do this properly
            opSche = new SimpleOperatorSchedule(); // Need to do this properly
            for (MCMCOperator mcmcOperator : operators) {
                opSche.addOperator(mcmcOperator);
            }
            mcmcOptions = new MCMCOptions(); // Need to do this properly

        }
        new BlueBeast(opSche, mcmcOptions, currentChainLength, convergenceStatsToUse, essLowerLimitBoundary, burninPercentage,
                 dynamicCheckingInterval, /*autoOptimiseWeights, */optimiseChainLength, maxChainLength,
                 initialCheckInterval, inputFileName, outputFileName, loadTracer);
        System.exit(0);


	}

	public static void testCaseSteven(){
		// copied from BlueBeastTest
	    BlueBeast bb;
	    ArrayList<String> variableNames = new ArrayList<String>();
	    
	    
	    variableNames.addAll(Arrays.asList(new String[] {"Sneezy", "Sleepy", "Dopey", "Doc", "Happy", "Bashful", "Grumpy"}));
	    MCMCOperator[] operators;
	    MCMCOptions mcmcOptions;
	    ArrayList<ConvergeStat> convergenceStatsToUse;
	    int essLowerLimitBoundary;
	    double burninPercentage;
	    boolean dynamicCheckingInterval;
	    boolean autoOptimiseWeights;
	    boolean optimiseChainLength;
	    long maxChainLength;
	    long initialCheckInterval;

        operators = new MCMCOperator[variableNames.size()];
        /* We can easily change to see if it works for other operators too */
        operators[0] = new ScaleOperator(new Parameter.Default(0.0), 0.75);
        operators[1] = new ScaleOperator(new Parameter.Default(0.1), 0.75);
        operators[2] = new ScaleOperator(new Parameter.Default(0.2), 0.75);
        operators[3] = new ScaleOperator(new Parameter.Default(0.3), 0.75);
        operators[4] = new ScaleOperator(new Parameter.Default(0.4), 0.75);
        operators[5] = new ScaleOperator(new Parameter.Default(0.5), 0.75);
        operators[6] = new ScaleOperator(new Parameter.Default(0.6), 0.75);
        mcmcOptions = new MCMCOptions();

        essLowerLimitBoundary = 5;
        burninPercentage = 0.2;
        dynamicCheckingInterval =true;
        autoOptimiseWeights = true;
        optimiseChainLength = true;
        maxChainLength = 100;
        initialCheckInterval = 100;


        convergenceStatsToUse = new ArrayList<ConvergeStat>();
        convergenceStatsToUse.add(ESSConvergeStat.INSTANCE);
        convergenceStatsToUse.add(GewekeConvergeStat.INSTANCE);
//        convergenceStatsToUse.add(GelmanConvergeStat.INSTANCE);


        OperatorSchedule opSche = new SimpleOperatorSchedule(); // Need to do this properly
        for (MCMCOperator mcmcOperator : operators) {
        	opSche.addOperator(mcmcOperator);
		}
        
        BlueBeastLogger bbl = new BlueBeastLogger(10);
        bbl.addVariableName(variableNames);
        bb = new BlueBeast(opSche, mcmcOptions, null, convergenceStatsToUse, bbl,
                     essLowerLimitBoundary, burninPercentage, dynamicCheckingInterval,
                     /*autoOptimiseWeights, */optimiseChainLength, maxChainLength, initialCheckInterval, loadTracer);
//        //TODO: Dont hard code this shit
//        String inputFileName = "/home/sw167/workspace/blue-beast/data/testData2.log";
//        String outputFileName = "/home/sw167/workspace/blue-beast/data/testData1.out";;
//        int currentChainLength = 1000000;
//        bb = new BlueBeast(opSche, mcmcOptions, currentChainLength, convergenceStatsToUse, essLowerLimitBoundary, burninPercentage,
//                dynamicCheckingInterval, /*autoOptimiseWeights, */optimiseChainLength, maxChainLength,
//                initialCheckInterval, inputFileName, outputFileName, loadTracer);
        bb.testSteven();

	}
	
}
