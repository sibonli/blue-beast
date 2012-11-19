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
import bb.mcmc.analysis.ConvergeStat;
import bb.mcmc.analysis.ESSConvergeStat;
import bb.mcmc.analysis.GewekeConvergeStat;
import bb.mcmc.analysis.RafteryConvergeStat;
import dr.app.util.Arguments;
import dr.app.util.Utils;
import dr.inference.mcmc.MCMCOptions;
import dr.inference.model.Parameter;
import dr.inference.operators.MCMCOperator;
import dr.inference.operators.OperatorSchedule;
import dr.inference.operators.ScaleOperator;
import dr.inference.operators.SimpleOperatorSchedule;

import java.util.ArrayList;


/**
 * This class is only used when using Blue BEAST for an external program (i.e. not BEAST)
 * Is not needed otherwise (I think)
 *
 */
public class BlueBeastMain {

    public static final String VERSION = "0.1";
    protected static final String CITATION = "WLS Li and S-H Wu, BLUE-BEAST: A tool for improving the usability and efficiency of Bayesian phylogenetic methods";

    //TODO make sure these values reflect the values in BlueBeast.java
    protected static double burninPercentage = 0.1;
    protected static boolean dynamicCheckingInterval = true;
//    protected static boolean autoOptimiseWeights = true;
    protected static boolean optimiseChainLength = true;
    protected static long maxChainLength = Long.MAX_VALUE;
//    protected static ArrayList<Class<? extends ConvergeStat>> convergenceStatsToUse;
//    protected static ArrayList<ConvergeStat>> convergenceStatsToUse;
    protected static ArrayList<ConvergeStat> convergenceStats;
    protected static long initialCheckInterval = 1000;
    protected static boolean loadTracer = true;
    protected static int essLowerLimitBoundary = 200;
    protected static int essStepSize = 1;
    protected static double rafteryQuantile = 0.025;
    protected static double rafteryError = 0.005;
    protected static double rafteryProb = 0.95;
    protected static double rafteryConvergeEps = 0.001;
    protected static double rafteryThreshold = 5;
    protected static double gewekeFrac1 = 0.1;
    protected static double gewekeFrac2 = 0.5;
    protected static double gewekeThreshold = 1.96;



    protected static void printTitle() {
        System.out.println();
        centreLine("BLUE BEAST - Bayesian and Likelihood methods Usability Extension", 60);
        centreLine("Version " + VERSION + ", " + "2011", 60);
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
		
//		testCaseSteven();

        printTitle();

        String inputFileName = null;
//        String operatorInfoFileName = null;
        String outputFileName = null;
        int currentChainLength = -1;


        Arguments arguments = new Arguments(
                new Arguments.Option[]{
                        new Arguments.Option("ess", "specifies the use of effective sample size as a convergence diagnosis"),
                        new Arguments.Option("geweke", "specifies the use of Geweke's statistic as a convergence diagnosis"),
                        new Arguments.Option("raftery", "specifies the use of Raftery and Lewis's statistic as a convergence diagnosis"),
                        new Arguments.IntegerOption("essLowerLimitBoundary", "Minimum value of the ESS required to consider the chain converged (default: 200)"),
                        new Arguments.IntegerOption("essStepSize", "Step size for the ESS (default: 1)"),
                        new Arguments.IntegerOption("rafteryQuantile", "Quantile to be estimated in Raftery and Lewis's diagnostic"),
                        new Arguments.IntegerOption("rafteryError", "The desired margin of error of the estimate in Raftery and Lewis's diagnostic"),
                        new Arguments.IntegerOption("rafteryProb", "Probability of attaining the desired degree of error in Raftery and Lewis's diagnostic"),
                        new Arguments.IntegerOption("rafteryConvergeEps", "Precision required for estimate of time to convergence in Raftery and Lewis's diagnostic"),
                        new Arguments.IntegerOption("rafteryThreshold", "Threshold of when to stop iterating for values to converge in Raftery and Lewis's diagnostic"),
                        new Arguments.IntegerOption("gewekeFrac1", "Fraction to use from beginning of chain in Geweke's diagnostic"),
                        new Arguments.IntegerOption("gewekeFrac2", "Fraction to use from end of chain in Geweke's diagnostic"),
                        new Arguments.IntegerOption("gewekeThreshold", "Threshold of when to stop iterating for values to converge in Geweke's diagnostic"),
                        new Arguments.IntegerOption("currentChainLength", "How long the chain has currently been run for"),
                        new Arguments.Option("dynamicCheckingInterval", "Whether the interval between checks for convergence are constant or dynamic (default: used)"),
//                        new Arguments.Option("autoOptimiseWeights", "Whether proposal kernel weights/acceptance ratios are automatically adjusted (default: used)"),
                        new Arguments.Option("optimiseChainLength", "Whether the MCMC chain length is automatically adjusted (default: used). If unused then chain length = maxChainLength"),
                        new Arguments.LongOption("maxChainLength", "Maximum Markov chain length that will be run (default: " + Integer.MAX_VALUE + ")"),
                        new Arguments.LongOption("initialCheckInterval", "Initial interval to perform Blue Beast check. If interval is not dynamic then this is the interval throughout the run"),
                        new Arguments.RealOption("burninPercentage", "Percentage of the length of the Markov chain which is treated as burnin at each checkpoint (default: 10% )"),
//                        new Arguments.StringOption("convergenceStatsToUse", new String[]{"all", "ESS", "interIntraChainVariance"}, false, "The statistics used to assess convergence of the chain (default: all)"),
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
//            // prompt a file input/input file name to get some operator weights. Output to console?
//        }
        optimiseChainLength = arguments.hasOption("optimiseChainLength");

        loadTracer = arguments.hasOption("loadTracer");



        if (arguments.hasOption("essLowerLimitBoundary")) {
            essLowerLimitBoundary = arguments.getIntegerOption("essLowerLimitBoundary");
        }
        if (arguments.hasOption("essStepSize")) {
            essStepSize = arguments.getIntegerOption("essStepSize");
        }
        if (arguments.hasOption("rafteryQuantile")) {
            rafteryQuantile = arguments.getIntegerOption("rafteryQuantile");
        }
        if (arguments.hasOption("rafteryError")) {
            rafteryError = arguments.getIntegerOption("rafteryError");
        }
        if (arguments.hasOption("rafteryProb")) {
            rafteryProb = arguments.getIntegerOption("rafteryProb");
        }
        if (arguments.hasOption("rafteryConvergeEps")) {
            rafteryConvergeEps = arguments.getIntegerOption("rafteryConvergeEps");
        }
        if (arguments.hasOption("rafteryThreshold")) {
            rafteryThreshold = arguments.getIntegerOption("rafteryThreshold");
        }
        if (arguments.hasOption("gewekeFrac1")) {
            gewekeFrac1 = arguments.getIntegerOption("gewekeFrac1");
        }
        if (arguments.hasOption("gewekeFrac2")) {
            gewekeFrac2 = arguments.getIntegerOption("gewekeFrac2");
        }
        if (arguments.hasOption("gewekeThreshold")) {
            gewekeThreshold = arguments.getIntegerOption("gewekeThreshold");
        }

        //TODO(SW): add other stats
        convergenceStats = new ArrayList<ConvergeStat>(6);
        if(arguments.hasOption("ess")) {
            convergenceStats.add(new ESSConvergeStat(essStepSize, essLowerLimitBoundary));
        }
        if(arguments.hasOption("geweke")) {
            convergenceStats.add(new GewekeConvergeStat(gewekeFrac1, gewekeFrac2, gewekeThreshold));
        }
        if(arguments.hasOption("raftery")) {
            convergenceStats.add(new RafteryConvergeStat(rafteryQuantile, rafteryError, rafteryProb, rafteryConvergeEps, rafteryThreshold));
        }
        if(convergenceStats.size()==0) {
            convergenceStats.add(new ESSConvergeStat(essStepSize, essLowerLimitBoundary));
        }


//        String convergenceStatsToUseParameters = "all";
//        if (arguments.hasOption("convergenceStatsToUse")) {
//            convergenceStatsToUseParameters = arguments.getStringOption("convergenceStatsToUse");
//        }
//        if(convergenceStatsToUseParameters.equals("all")) {
//
//            convergenceStatsToUse.add(ESSConvergeStat.thisClass);
//            convergenceStatsToUse.add(GewekeConvergeStat.thisClass);
//            convergenceStatsToUse.add(RafteryConvergeStat.thisClass);
////            convergenceStatsToUse.add(GelmanConvergeStat.thisClass);
//        }
//        else if(convergenceStatsToUseParameters.equals("ESS")) {
//            convergenceStatsToUse.add(ESSConvergeStat.thisClass);
//        }
//        if(convergenceStatsToUseParameters.equals("interIntraChainVariance")) {
//            convergenceStatsToUse.add(GelmanConvergeStat.thisClass);
//        }

        String[] args2 = arguments.getLeftoverArguments();

        if (args2.length == 2) {
            inputFileName = args2[0];
            outputFileName = args2[1];
        }
        else if (args2.length == 3) {
            inputFileName = args2[0];
            outputFileName = args2[1];
//            operatorInfoFileName = args2[2];
        }
        else if (args2.length == 1) {
            if (outputFileName == null) {
                outputFileName = Utils.getSaveFileName("BLUE-BEAST " + VERSION + " - Select output file");
            }
//            if (operatorInfoFileName == null) {
//                operatorInfoFileName = Utils.getSaveFileName("BLUE-BEAST " + VERSION + " - Select output file");
//            }
        }
        else {
            if (inputFileName == null) {
               // No input file name was given so throw up a dialog box...
                inputFileName = Utils.getLoadFileName("BLUE-BEAST " + VERSION + " - Select input file file to analyse");
            }
            if (outputFileName == null) {
                outputFileName = Utils.getSaveFileName("BLUE-BEAST " + VERSION + " - Select output file");
            }
//            if (operatorInfoFileName == null) {
//                operatorInfoFileName = Utils.getSaveFileName("BLUE-BEAST " + VERSION + " - Select output file");
//            }
        }

        if(inputFileName == null || outputFileName == null) {
            System.err.println("Missing input or output file name");
            printUsage(arguments);
            System.exit(1);

        }

//        MCMCOperator[] operators = null;
        MCMCOptions mcmcOptions = null;
        OperatorSchedule opSche = null;

//            new BlueBeast(null, null, convergenceStatsToUse, essLowerLimitBoundary, burninPercentage,
//                    dynamicCheckingInterval, autoOptimiseWeights, optimiseChainLength, maxChainLength,
//                    initialCheckInterval, outputFileName);

//        if(operatorInfoFileName != null) {
//            //TO    DO Parse and read-in MCMC operators etc (long).
//            operators = new MCMCOperator[10]; // Need to do this properly
//            opSche = new SimpleOperatorSchedule(); // Need to do this properly
//            for (MCMCOperator mcmcOperator : operators) {
//                opSche.addOperator(mcmcOperator);
//            }
//            mcmcOptions = new MCMCOptions(); // Need to do this properly
//        }
        mcmcOptions = new MCMCOptions(); // Need to do this properly
//        mcmcOptions.setChainLength(0);
        BlueBeast bb = new BlueBeast(opSche, mcmcOptions, currentChainLength, convergenceStats, essLowerLimitBoundary, burninPercentage,
                 dynamicCheckingInterval, /*autoOptimiseWeights, */optimiseChainLength, maxChainLength,
                 initialCheckInterval, inputFileName, outputFileName, loadTracer);
        System.out.println("\nBLUE-BEAST analysis complete.");
//        bb.check(currentChainLength);
//        System.exit(0);

//        try {
//            BufferedReader br = new BufferedReader(new FileReader(inputFileName));
//            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFileName)), true);
//            //read in data
////            BlueBeastMarkovChainDelegate blueBeastMarkovChainDelegate = new BlueBeastMarkovChainDelegate()
//            out.close();
//            br.close();
//
//        }
//        catch(IOException e) {
//            throw new RuntimeException("IO error - please check the file paths for the input and output files");
//        }
	}

	public static void testCaseSteven(){
	    
	    essLowerLimitBoundary = 100;
	    burninPercentage = 0.1;
	    dynamicCheckingInterval = true;
	    optimiseChainLength = true;
	    maxChainLength = 1000000;
	    initialCheckInterval = 1000;

        MCMCOptions mcmcOptions = new MCMCOptions();
        
        MCMCOperator[] operators = new MCMCOperator[10];
        for (int i = 0; i < operators.length; i++) {
			operators[i] = new ScaleOperator(new Parameter.Default(0.1*i), 0.75);
		}
        
        OperatorSchedule opSche = new SimpleOperatorSchedule(); // Need to do this properly
        for (MCMCOperator mcmcOperator : operators) {
        	opSche.addOperator(mcmcOperator);
		}
        
        BlueBeastLogger bbl = new BlueBeastLogger(10);
//        bbl.addVariableName(variableNames);
  
        convergenceStats = new ArrayList<ConvergeStat>();
        convergenceStats.add(new ESSConvergeStat(1, 200));
//        convergenceStats.add(new GewekeConvergeStat(0.1, 0.5, 1.96));
        convergenceStats.add(new RafteryConvergeStat(0.025, 0.005, 0.95, 0.001, 5));
        
        String inputFileName = "/home/sw167/workspace/blue-beast/data/testData10k.log";
        String outputFileName = "/home/sw167/workspace/blue-beast/data/testOut";
		int currentChainLength = 10000;
		mcmcOptions.setChainLength(20001);
		BlueBeast bb = new BlueBeast(opSche, mcmcOptions, currentChainLength,
				convergenceStats, essLowerLimitBoundary, burninPercentage,
				dynamicCheckingInterval, /* autoOptimiseWeights, */
				optimiseChainLength, maxChainLength, initialCheckInterval,
				inputFileName, outputFileName, false);
		
		
		for (ConvergeStat cs : convergenceStats) {
			System.err.println(cs.toString());
		}

        System.exit(0);
	}
	
}
