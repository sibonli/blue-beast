/**
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


 * @author Wai Lok Sibon Li
 *
 */

package bb.main;

import bb.loggers.BlueBeastLogger;
import bb.mcmc.analysis.*;
import bb.mcmc.extendMCMC.AdaptChainLengthInterval;
import bb.report.ProgressReporter;
import bb.report.ReportUtils;
import dr.app.beast.BeastMain;
import dr.app.tracer.application.InstantiableTracerApp;
import dr.inference.markovchain.MarkovChain;
import dr.inference.mcmc.MCMCOptions;
import dr.inference.operators.MCMCOperator;
import dr.inference.operators.OperatorSchedule;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.commons.lang3.ArrayUtils;

//import bb.report.LoadTracer;

public class BlueBeast {

    protected static OperatorSchedule operators;
    protected static MCMCOptions mcmcOptions;
    protected static MarkovChain markovChain;
    protected File logFile = null;
    //protected HashMap<String, ArrayList<Double>> traceInfo;

    private long nextCheckChainLength;
    ProgressReporter progressReporter;


    // only the name of these class, used to pass things around. I think we can remove this once the parse in fully working, maybe only use for local/non-BEAST test
    protected static ArrayList<Class<? extends ConvergeStat>> convergenceStatsToUse;

    // store the actual implemented and working ConvergeStat instance.     
    protected static ArrayList<ConvergeStat> convergenceStats;
    protected static String[] variableNames;

    /* Copied from BlueBeastMain. Not sure we need this twice */
    protected static int essLowerLimitBoundary;
    protected static double burninPercentage;
    protected static boolean dynamicCheckingInterval;
//    protected static boolean autoOptimiseWeights;
    protected static boolean optimiseChainLength;
    protected static long maxChainLength;
    protected static long initialCheckInterval;

    protected static boolean loadTracer;

    public static int MIN_SAMPLE_SIZE = 20000; // Who needs more than 20,000 samples?



    private double progress;    // Stores the progress of the MCMC chain
	private int stepSize;
    protected BlueBeastLogger blueBeastLogger;

    private int lastLoggedState = -1; // Currently still unused. Variable only used for adding data from file


    /**
     * Main constructor
     *
     */
    public BlueBeast(OperatorSchedule operators, MCMCOptions mcmcOptions, MarkovChain markovChain,
                     ArrayList<Class<? extends ConvergeStat>> convergenceStatsToUse, BlueBeastLogger blueBeastLogger, //String[] variableNames,
                     int essLowerLimitBoundary, double burninPercentage, boolean dynamicCheckingInterval,
                     /*boolean autoOptimiseWeights, */ boolean optimiseChainLength, long maxChainLength,
                     long initialCheckInterval, boolean loadTracer) {
        printCitation();
        logFile = null;

        this.stepSize = 1;
        this.operators = operators;
        this.mcmcOptions = mcmcOptions;
        this.markovChain = markovChain;
        this.convergenceStatsToUse = convergenceStatsToUse;
        this.blueBeastLogger = blueBeastLogger;
        this.variableNames = blueBeastLogger.getVariableNames().toArray(new String[blueBeastLogger.getVariableNames().size()]);

        this.essLowerLimitBoundary = essLowerLimitBoundary;
        this.burninPercentage = burninPercentage;
        this.dynamicCheckingInterval = dynamicCheckingInterval;
//        this.autoOptimiseWeights = autoOptimiseWeights;
        this.optimiseChainLength = optimiseChainLength;
        this.maxChainLength = maxChainLength;
        mcmcOptions.setChainLength(maxChainLength); // Just a safety check
        this.initialCheckInterval = initialCheckInterval;
        this.loadTracer = loadTracer;
        setNextCheckChainLength(initialCheckInterval);
        initialize();
    }


    /**
     * Use this constructor only if operators cannot be changed implicitly (within the program)
     * but rather using the logfile
     * Otherwise use the main constructor
     */
    public BlueBeast(OperatorSchedule operators, MCMCOptions mcmcOptions, int currentChainLength,
                     ArrayList<Class<? extends ConvergeStat>> convergenceStatsToUse,
                     int essLowerLimitBoundary, double burninPercentage, boolean dynamicCheckingInterval,
                     /* boolean autoOptimiseWeights, */ boolean optimiseChainLength, long maxChainLength,
                     long initialCheckInterval, String logFileLocation, String outputFileName, boolean loadTracer) {
        printCitation();
        this.operators = operators;
        this.mcmcOptions = mcmcOptions;
        this.markovChain = null;

        this.convergenceStatsToUse = convergenceStatsToUse;
        this.essLowerLimitBoundary = essLowerLimitBoundary;
        this.burninPercentage = burninPercentage;
        this.dynamicCheckingInterval = dynamicCheckingInterval;
//        this.autoOptimiseWeights = autoOptimiseWeights;
        this.optimiseChainLength = optimiseChainLength;
        this.maxChainLength = maxChainLength;
        mcmcOptions.setChainLength(maxChainLength); // Just a safety check
        this.initialCheckInterval = initialCheckInterval;
        this.loadTracer = loadTracer;
        initialize();

        HashMap<String, ArrayList<Double>> traceInfo = new HashMap<String, ArrayList<Double>>(); // temp

        BufferedReader br;
        try {
            logFile = new File(logFileLocation);
            //FileReader logFileReader = new FileReader(logFile);
            br = new BufferedReader(new FileReader(logFile));



            String line = null;
            while((line = br.readLine())!=null) {

                if(line.matches("\\w+[\\t\\w+]+")) {  // Header line
                    this.variableNames = line.split("\t");
                    for(int i=0; i<variableNames.length; i++) {
                        traceInfo.put(variableNames[i], new ArrayList<Double>());
                    }
                }
                else if(line.matches("\\d+[\\t-?\\d+[\\.d+]?]+")) {   // Data line
                //if(line.matches("[\\t\\d+\\.?d*]+")) {   // Data line
                    String[] split = line.split("\t");
                    if(split.length != variableNames.length) {
                        throw new RuntimeException("Incorrect number of elements in log line: "
                                + split.length + " instead of " + variableNames.length);
                    }

                    for(int i=0; i<split.length; i++) {
                        traceInfo.get(variableNames[i]).add(Double.parseDouble(split[i]));
                    }


                }
                else {
                    System.out.println("Non-logfile line: " + line);
                }
            }
        }catch (IOException e) {
            System.err.println("Input file location not valid");
            e.printStackTrace();
        }

        //boolean converged = false;
        boolean converged = check(currentChainLength, traceInfo);

        try {
            if (outputFileName != null) {
                FileOutputStream outputStream = new FileOutputStream(outputFileName);
                System.setOut(new PrintStream(outputStream));
            }
        }catch (IOException e) {
            System.err.println("Output file location not valid");
            e.printStackTrace();
        }

        /* Do the analysis below */
        progressReporter.printProgress(progress);
        if(converged)  {
            System.out.println("Chains have converged");
        }
        else {
            System.out.println("Chains have not converged");
            System.out.println("Next check should be performed at " + getNextCheckChainLength());
            if(getNextCheckChainLength() != mcmcOptions.getChainLength()) {
                throw new RuntimeException("Inconsistency in next check chain length (maybe it doesn't matter?)");
            }
            if(operators != null) {
//                if(!autoOptimiseWeights) {
//                    throw new RuntimeException("If operators not null, then auto optimize weights should be true");
//                }
                for(int i=0; i<operators.getOperatorCount(); i++) {
                    MCMCOperator o = operators.getOperator(i);
                    System.out.println(o + "\t" + o.getOperatorName() + "\t" + o.getWeight());
                }
            }
        }
    }



    public void printCitation() {
//        System.out.println("BLUE-BEAST  Copyright (C) 2011  Wai Lok Sibon Li & Steven H. Wu");
        BeastMain.centreLine("BLUE-BEAST  Copyright (C) 2011  Wai Lok Sibon Li & Steven H. Wu", 60);
        System.out.println("This program comes with ABSOLUTELY NO WARRANTY; for details type `show w'.");
        System.out.println("This is free software, and you are welcome to redistribute it");
        System.out.println("under certain conditions; type `show c' for details.");
        BeastMain.centreLine("BLUE BEAST is in use. Please cite " + BlueBeastMain.CITATION, 60);
        System.out.println("Note: It is recommended that the convergence of MCMC chains are verified manually");
        //initializeTraceInfo();
    }

    /**
     * Initialises all the variables required to run a BLUE BEAST analysis
     * 
     */
    private void initialize() {
        //initializeTraceInfo(variableNames);
        initializeConvergenceStatistics(); // use ArrayList<Class<? extends ConvergeStat>> convergenceStatsToUse;
        initializeProgressReport();

        //setNextCheckChainLength(1000);
        //initializeInitialCheckInterval(dynamicCheckingInterval);

    }

    private void initializeProgressReport() {
    	//TODO(SW) redundant function, and maybe need to change constructor later
        progressReporter = new ProgressReporter(convergenceStats);
    }

    private void initializeConvergenceStatistics() {

    	ArrayList<ConvergeStat> newStat = new ArrayList<ConvergeStat>();

		HashMap<Class<? extends ConvergeStat>, String[]> testingVariables = 
				new HashMap<Class<? extends ConvergeStat>, String[]>();
				
		//TODO(SW): eventually these if-else-if will be gone, handled by parser
		for (Class<? extends ConvergeStat> csClass : convergenceStatsToUse) {
            if(csClass.equals(ESSConvergeStat.class)) {
            	newStat.add(new ESSConvergeStat(stepSize, essLowerLimitBoundary));
            }
            else if(csClass.equals(GewekeConvergeStat.class)) {
            	double frac1 = 0.1;
                double frac2 = 0.5;
                double th = 1.96;
            	newStat.add(new GewekeConvergeStat(frac1, frac2, th));
            }
            else if(csClass.equals(GelmanConvergeStat.class)) {
//            	newStat.add(new GelmanConvergeStat());
            }
            else if(csClass.equals(RafteryConvergeStat.class)) {
                double quantile = 0.025;
                double error = 0.005;
                double prob = 0.95;
                double convergeEps = 0.001;
                double th = 5;
            	newStat.add(new RafteryConvergeStat(quantile, error, prob, convergeEps, th));
            }
            else if(csClass.equals(HeidelbergConvergeStat.class)) {
//            	newStat.add(new HeidelbergConvergeStat());
            }
		}
		convergenceStats = newStat;
    }


    /**
     * Regardless of whether the interval is dynamic, the default initial check is set at 5% of the total initial chain
     * length
     */
    private void initializeInitialCheckInterval(boolean dynamicCheckingInterval) {
        //nextCheckChainLength = (int) Math.round(mcmcOptions.getChainLength()*0.05);

	}

	/**
	 * Computes new values for convergence statistics Utilises an Array of
	 * HashMaps
	 * 
	 * @param convergenceStats
	 * 
	 * @param convergenceStatsToUse
	 */
	private void calculateConvergenceStatistics(
			HashMap<String, ArrayList<Double>> traceInfo) {

		// TODO: pass burnin to this method
		int burnin = 0;
//		mcmcOptions.getChainLength()
		HashMap<String, double[]> values = ConvergeStatUtils.traceInfoToArrays(
				traceInfo, burnin);


		String[] testingVariables = Arrays.copyOfRange(variableNames, 0, 6);
//		TODO(SW): testVariableName should get set before this method, no point to set it multiple times
		for (ConvergeStat cs : convergenceStats) {
			System.out.println("\nCalculating " + cs.getStatisticName());
			cs.updateValues(values);
			cs.setTestVariableName(testingVariables);
			cs.calculateStatistic();
		}

	}

    /**
     * Add log data to the existing object
     * i.e. this should be called whenever the BEAST log file is updated
     * and an argument of estimated values for each variable of interest should be provided
     * 
     */
    // This method is unused, make it deprecated to allow testing with simulated data
    @Deprecated
    public void addLogData(HashMap<String, ArrayList<Double>> traceInfo, String[] variableNames, double[] traceData) {
        if(variableNames.length != traceData.length) {
            System.out.println("Error in BlueBeast.java: variableNames.length != traceData.length");
            System.exit(-1);
        }
        for(int i=0; i<variableNames.length; i++) {
            if(traceInfo.containsKey(variableNames[i])) {
                traceInfo.get(variableNames[i]).add(new Double(traceData[i])); //TO DO think auto box handle this
//                System.out.println("added variable, traceinfo.size(): " + traceInfo.size());
            }
            else {
                System.out.println("Error in BlueBeast.java: traceInfo Does not contain key of variableNames[i]" + variableNames[i]);
                System.exit(-1);
            }

        }
    }

    /**
     * Add log data to the existing object
     * This method is an alternative for methods that have provided the log file name instead
     * and prompts BLUE BEAST to check the log file for updates
     */
    public void addLogData() {

        try {
            RandomAccessFile rfile = new RandomAccessFile(logFile,"r");
            rfile.seek(logFile.length());
            ArrayList<String>  logLines = new ArrayList<String>();
            String line;
            boolean started = false;
            int newState = -1;
            search: while((line = rfile.readLine()) != null) {
                int state = Integer.parseInt(line.substring(0, line.indexOf("\t")));
                if(!started) {  // Get the last logged state
                    newState = state;
                    started = true;
                }
                System.out.println("Reading state " + state + " from file. ");
                if(state>lastLoggedState) {
                    logLines.add(line);
                    System.out.println("logfile: " + line);
                    if(state==0) {
                        break search;
                    }
                }
                else {
                    break search;
                }

            }
            lastLoggedState = newState;

            for(int i = logLines.size()-1; i>=0; i--) {
                //TODO This method which saves from reading entire log file again. Properly add stuff to the trace (very long)
                logLines.get(i);
            }


        }catch (IOException e) {
            System.err.println("Input file location not valid");
            e.printStackTrace();
        }
    }

    /**
     * Checks whether convergence has been met and whether the analysis is complete
     * Calls all the functionality that takes place at each check from BEAST
     */
    public boolean check(long currentState) {
        return check(currentState, blueBeastLogger.getTraceInfo());
    }

    public boolean check(long currentState, HashMap<String, ArrayList<Double>> traceInfo) {
        System.out.println("\t\tBLUE BEAST now performing check");
        /* Calculate whether convergence has been met */
    	calculateConvergenceStatistics(traceInfo);

        boolean allStatsConverged = true;    // Whether convergence has been reached according to all convergence statistics

        for(ConvergeStat cs : convergenceStats) {
            if(!cs.haveAllConverged()) {
                System.out.println("Convergence has not yet been reached, according to statistic: " + cs.getStatisticName());
                System.out.println(cs.notConvergedSummary());
                allStatsConverged = false;
            }
        }

        /* Reporting progress */
        // TODO progressReporter must take into account all variables (long)
        progress = progressReporter.calculateProgress();//convergenceStats.get(index).getAllStat(), convergenceStats.get(0).getVariableNames());
        progressReporter.printProgress(progress);

        /* If job is complete */
        String tempFileName = "bb_temp.log";
        if(allStatsConverged) {
            int percentage = Math.round(((float) progress) * 100);
            System.out.println("BLUE-BEAST believes all variables have converged. Progress is now " + (percentage) + "%");
            if(loadTracer) {
                System.out.println("Loading Tracer option set, opening Tracer with log file. Please exit BEAST manually");

                ReportUtils.writeBBLogToFile(traceInfo, tempFileName);
                InstantiableTracerApp.loadInstantiableTracer("Tracer (via BLUE-BEAST)", tempFileName, (long) (burninPercentage * mcmcOptions.getChainLength()));
            }
            else {
                System.out.println("Load Tracer option not set. Job quitting");
                if(markovChain != null) {
                    markovChain.pleaseStop();
                }
                mcmcOptions.setChainLength(getNextCheckChainLength()); // Just a safety check, doesn't work as expected
            }
            return true;
        }
        System.out.println("Chain has not converged, continue running");
//        if(autoOptimiseWeights) {
//            System.out.println("Proposal kernel weights are being optimized");
//            AdaptProposalKernelWeights.adaptAcceptanceRatio(operators, progressReporter);//, convergenceStats);
//            for(int j=0; j<operators.getOperatorCount(); j++) {
//                MCMCOperator o = operators.getOperator(j);
//                System.out.println("operators: " + o.getOperatorName().replaceFirst(".+\\(", "").replaceFirst("\\).*", "") + "\t" + o.getWeight());
//            }
//            Set<String> variables = traceInfo.keySet();
//            for(String s : variables) {
//                System.out.println("trace variables: "  + s);
//            }
//        }
        if(optimiseChainLength) {
            System.out.println("Chain length is being optimized");
            setNextCheckChainLength(AdaptChainLengthInterval.calculateNextCheckingInterval(progress, dynamicCheckingInterval, maxChainLength, initialCheckInterval, currentState));
        }


        return false;
    }

    public void checkThinLog() {
        checkThinLog(blueBeastLogger.getTraceInfo());
    }
    
    public void checkThinLog(HashMap<String, ArrayList<Double>> traceInfo) {
//        int minSamples = 20000;
        /* Calculate the thinning factor */
        int currentSampleNumber = traceInfo.values().iterator().next().size();
        int factor = currentSampleNumber / MIN_SAMPLE_SIZE;
        if(factor > 1) {
            blueBeastLogger.setLogEvery(blueBeastLogger.getLogEvery() * factor);
            ReportUtils.thinTraceInfo(traceInfo, factor);
        }
    }



    public void setNextCheckChainLength(long length) {
	    nextCheckChainLength = length;
	}


	public long getNextCheckChainLength() {
	    return nextCheckChainLength;
	}


	public double getProgress() {
        return progress;
    }

    public HashMap<String, ArrayList<Double>> getTraceInfo() {
        return blueBeastLogger.getTraceInfo();
    }

    public long getMaxChainLength() {
        return maxChainLength;
    }



	public void testSteven()  {		
		
		HashMap<String, ArrayList<Double>> traceInfo = new HashMap<String, ArrayList<Double>>();
		
		String infile;
//		infile = System.getProperty("user.dir")+File.separatorChar+"data"+File.separatorChar+"testData10k.log";
		infile = System.getProperty("user.dir")+File.separatorChar+"data"+File.separatorChar+"testStrictClock.log";
		try {
			BufferedReader in = new BufferedReader(new FileReader(infile));
			String input;
			int j = 0;

			while( (input=in.readLine()) != null && j<10000){
				if( input.startsWith("state") ){
					StringTokenizer st = new StringTokenizer(input);
					variableNames = new String[0];
					while(st.hasMoreTokens()){
						String key = st.nextToken();
						traceInfo.put(key, new ArrayList<Double>() );
						variableNames = ArrayUtils.add(variableNames, key);
					}

				}
				else if( !(input.startsWith("#") || input.startsWith("state") )   ){
					double[] values = new double[variableNames.length];
					StringTokenizer st = new StringTokenizer(input);
	
					for (int i = 0; i < values.length; i++) {
						values[i] = Double.parseDouble( st.nextToken() );
						traceInfo.get(variableNames[i]).add((values[i])); 
					}
					j++;

				}
			}
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		HashMap<Class<? extends ConvergeStat>, String[]> testingVariablesHM = new HashMap<Class<? extends ConvergeStat>, String[]>();
		testingVariablesHM.put(ESSConvergeStat.class, variableNames);
		testingVariablesHM.put(GewekeConvergeStat.class, Arrays.copyOfRange(variableNames, 0, 6));
		testingVariablesHM.put(RafteryConvergeStat.class, Arrays.copyOfRange(variableNames, 2, 8));
		

//		calculateConvergenceStatistics(blueBeastLogger.getTraceInfo());
		calculateConvergenceStatistics(traceInfo);
		for (ConvergeStat cs : convergenceStats) {
			System.out.println(cs.toString());
		}
		
		check(1, traceInfo);
	}

}
