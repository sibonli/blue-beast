package bb.main;

import bb.mcmc.adapt.AdaptProposalKernelWeights;
import bb.mcmc.extendMCMC.AdaptChainLengthInterval;
import bb.mcmc.analysis.*;
import bb.report.ProgressReport;
import beast.inference.loggers.BlueBeastLogger;
import dr.inference.mcmc.MCMCOptions;
import dr.inference.operators.OperatorSchedule;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: sibon
 * Date: 3/10/11
 * Time: 10:21 AM
 * To change this template use File | Settings | File Templates.
 *
 * @author Wai Lok Sibon Li
 *
 */
public class BlueBeast {

    protected static OperatorSchedule operators;
    protected static MCMCOptions mcmcOptions;
    protected File logFile = null;
    //protected Hashtable<String, ArrayList<Double>> traceInfo;

    private int nextCheckChainLength;
    ProgressReport progressReport;

    protected static ArrayList<ConvergeStat> convergenceStats;
    protected static String[] variableNames;

    /* Copied from BlueBeastMain. Not sure we need this twice */
    protected static int essLowerLimitBoundary;
    protected static double burninPercentage;
    protected static boolean dynamicCheckingInterval;
    protected static boolean autoOptimiseWeights;
    protected static boolean optimiseChainLength;
    protected static int maxChainLength;
    protected static int initialCheckInterval;



    private double progress;    // Stores the progress of the MCMC chain
	private int stepSize;
    protected BlueBeastLogger blueBeastLogger;

    private int lastLoggedState = -1; // Variable only used for adding data from file



    //TODO more constructors which include the program parameters (long)

    /**
     * Use this constructor only if operators cannot be changed implicitly (within the program)
     * but rather using the logfile
     * Otherwise use the main constructor
     */
    public BlueBeast(OperatorSchedule operators, MCMCOptions mcmcOptions,
                     ArrayList<ConvergeStat> convergenceStatsToUse, String logFileLocation) {
        //TODO this constructor (for file read-in) is not complete. Do the other one first (long)
        printCitation();
        this.operators = operators;
        this.mcmcOptions = mcmcOptions;
        BufferedReader br;
        try {
            logFile = new File(logFileLocation);
            //FileReader logFileReader = new FileReader(logFile);
            br = new BufferedReader(new FileReader(logFile));

            String line = null;
            while((line = br.readLine())!=null) {
                if(line.matches("state[\\t\\w+]+")) {
                    variableNames = line.split("\t");
                }
            }
        }catch (IOException e) {
            System.err.println("Input file location not valid");
            e.printStackTrace();
        }



        variableNames = new String[3];
        initialize();
    }

    /**
     * Main constructor
     *
     */
    public BlueBeast(OperatorSchedule operators, MCMCOptions mcmcOptions,
                     ArrayList<ConvergeStat> convergenceStatsToUse, BlueBeastLogger blueBeastLogger, //String[] variableNames,
                     int essLowerLimitBoundary, double burninPercentage, boolean dynamicCheckingInterval,
                     boolean autoOptimiseWeights, boolean optimiseChainLength, int maxChainLength, int initialCheckInterval) {
        printCitation();
        logFile = null;
        //FIXME how should we get this, from constructor or MCMC. Bon Bon:  What?

        this.stepSize = 1;
        this.operators = operators;
        this.mcmcOptions = mcmcOptions;
        this.convergenceStats = convergenceStatsToUse;
        this.blueBeastLogger = blueBeastLogger;
        this.variableNames = blueBeastLogger.getVariableNames().toArray(new String[blueBeastLogger.getVariableNames().size()]);

        this.essLowerLimitBoundary = essLowerLimitBoundary;
        this.burninPercentage = burninPercentage;
        this.dynamicCheckingInterval = dynamicCheckingInterval;
        this.autoOptimiseWeights = autoOptimiseWeights;
        this.optimiseChainLength = optimiseChainLength;
        this.maxChainLength = maxChainLength;
        mcmcOptions.setChainLength(maxChainLength); // Just a safety check
        this.initialCheckInterval = initialCheckInterval;
        setNextCheckChainLength(initialCheckInterval);
        initialize();
        //this.traceinfo = blueBeastLogger.getTraceInfo();
    }

    public void printCitation() {
        System.out.println("BLUE BEAST is in use. Please cite " + BlueBeastMain.CITATION);
        System.out.println("Note: It is recommended that the convergence of MCMC chains are verified manually");
        //initializeTraceInfo();
    }

    /**
     * Initialises all the variables required to run a BLUE BEAST analysis
     * 
     */
    private void initialize() {
        //initializeTraceInfo(variableNames);
        initializeConvergenceStatistics(convergenceStats);
        initializeProgressReport(essLowerLimitBoundary);

        //setNextCheckChainLength(1000);
        //initializeInitialCheckInterval(dynamicCheckingInterval);

    }

    private void initializeProgressReport(int essLowerLimitBoundary) {
        progressReport = new ProgressReport(essLowerLimitBoundary);
    }

    private void initializeConvergenceStatistics(ArrayList<ConvergeStat> convergenceStatsToUse) {
        // Not sure I need to initialize anything here really... Might save this method just in case though
    	ArrayList<ConvergeStat> newStat = new ArrayList<ConvergeStat>();
    	for (ConvergeStat cs : convergenceStatsToUse) {
            if(cs.getClass().equals(ESSConvergeStat.class)) {
                newStat.add(new ESSConvergeStat(stepSize, variableNames, burninPercentage, essLowerLimitBoundary));
            }
            else if(cs.getClass().equals(GewekeConvergeStat.class)) {
            	newStat.add(new GewekeConvergeStat(variableNames));
            }
            else if(cs.getClass().equals(GelmanConvergeStat.class)) {
            	newStat.add(new GelmanConvergeStat());
            }
            else if(cs.getClass().equals(ZTempNovelConvergenceStatistic.class)) {
            	newStat.add(new ZTempNovelConvergenceStatistic());
            }
            else if(cs.getClass().equals(StandardConvergenceStatistic.class)) {
            	newStat.add(new StandardConvergenceStatistic());
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

    public int getNextCheckChainLength() {
        return nextCheckChainLength;
    }
    public void setNextCheckChainLength(int length) {
        nextCheckChainLength = length;
    }


    /**
     * Initializes the Arraylists contained in the Hashtable traceInfo
     */
    // This method is unused
//    private void initializeTraceInfo(String[] variableNames) {
//        traceInfo = new Hashtable<String, ArrayList<Double>>();
//        for(int i=0; i<variableNames.length; i++) {
//            traceInfo.put(variableNames[i], new ArrayList<Double>());
//        }
//    }

    /**
     * Add log data to the existing object
     * i.e. this should be called whenever the BEAST log file is updated
     * and an argument of estimated values for each variable of interest should be provided
     */
    // This method is unused
//    public void addLogData(String[] variableNames, double[] traceData) {
//        if(variableNames.length != traceData.length) {
//            System.out.println("Error in BlueBeast.java: variableNames.length != traceData.length");
//            System.exit(-1);
//        }
//        for(int i=0; i<variableNames.length; i++) {
//            if(traceInfo.containsKey(variableNames[i])) {
//                traceInfo.get(variableNames[i]).add(new Double(traceData[i])); //TO DO think auto box handle this
////                System.out.println("added variable, traceinfo.size(): " + traceInfo.size());
//            }
//            else {
//                System.out.println("Error in BlueBeast.java: traceInfo Does not contain key of variableNames[i]" + variableNames[i]);
//                System.exit(-1);
//            }
//
//        }
//    }

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
                //TODO Properly add stuff to the trace (long)
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
    public boolean check(int currentState) {
        System.out.println("\t\tBLUE BEAST now performing check");
        /* Calculate whether convergence has been met */
    	convergenceStats = calculateConvergenceStatistics(convergenceStats, blueBeastLogger.getTraceInfo());
        int index=0, i=0;
        //int i=0;
        //ConvergeStat[][] convergenceStatValues;

        boolean allStatsConverged = true;    // Whether convergence has been reached according to all convergence statistics

        for(ConvergeStat cs : convergenceStats) {
            if(!cs.hasConverged()) {
                System.out.println("Convergence has not yet been reached, according to statistic: " + cs.getStatisticName());
                allStatsConverged = false;
            }

            if(cs.getClass().equals(ESSConvergeStat.class)) {
                index = i;
            }

            i++;

        }

        //ESSConvergenceStatistic[] essValues = calculateESSScores(convergenceStatsToUse, traceInfo, burninPercentage);

        /* Reporting progress */
        // TODO progressReport must take into account all variables (long)
        progress = progressReport.calculateProgress(convergenceStats.get(index).getAllStat());
        progressReport.printProgress(progress);

        /* If job is complete */
        if(allStatsConverged) {
            System.out.println("All variables have converged. Progress is now " + (progress * 100) + "%. Job quitting");
            mcmcOptions.setChainLength(getNextCheckChainLength()); // Just a safety check
            return true;
        }
        System.out.println("Chain has not converged, continue running");
        if(autoOptimiseWeights) {
            System.out.println("Proposal kernel weights are being optimized");
            AdaptProposalKernelWeights.adaptAcceptanceRatio(operators); // Could easily change this to a static method call
        }
        if(optimiseChainLength) {
            System.out.println("Chain length is being optimized");
            setNextCheckChainLength(AdaptChainLengthInterval.calculateNextCheckingInterval(progress, dynamicCheckingInterval, maxChainLength, initialCheckInterval, currentState));
        }


        return false;
    }



    /**
     * Computes new values for convergence statistics
     * Utilises an Array of Hashtables
     * @param convergenceStatsToUse
     */
    private ArrayList<ConvergeStat> calculateConvergenceStatistics(ArrayList<ConvergeStat> convergenceStatsToUse,
                                                Hashtable<String, ArrayList<Double>> traceInfo) {
        //Hashtable<String, ConvergenceStatistic>[] convergenceStatValues = new Hashtable<String, ConvergenceStatistic>[10];

        /* Need two dimensions, one for variable names, one for the statistic type */
    	// Change to 1 dim, all calculation are handle inside each ConvergeStat variable
        //double[][] convergenceStatValues = new double[convergenceStatsToUse.size()][traceInfo.size()];
        //ConvergeStat[] convergenceStatValues = new ConvergeStat[convergenceStatsToUse.size()];
       
        for(ConvergeStat cs : convergenceStatsToUse) {

//                Double[] traceData = traceDataArrayList.toArray(new Double[traceDataArrayList.size()]);

                if(cs.getClass().equals(ESSConvergeStat.class)) {
                	cs.updateTrace(traceInfo);
                	cs.calculateStatistic();
                }
                else if(cs.getClass().equals(GelmanConvergeStat.class)) {
//                    convergenceStatValues[i][j] = new GelmanConvergeStat();
                }
                else if(cs.getClass().equals(GewekeConvergeStat.class)) {
                	cs.updateTrace(traceInfo);
                	cs.calculateStatistic();
               	
                }
                else if(cs.getClass().equals(ZTempNovelConvergenceStatistic.class)) {
//                    convergenceStatValues[i][j] = new ZTempNovelConvergenceStatistic();
                }
                else if(cs.getClass().equals(StandardConvergenceStatistic.class)) {
//                    convergenceStatValues[i][j] = new StandardConvergenceStatistic();
                }
                
                else {
                    throw new RuntimeException("No such convergence statistic");
                }
        }


        //Hashtable<String, ConvergenceStatistic>[] convergenceStatValues = new Hashtable[10];
        //convergenceStatValues[0] = new Hashtable<String, ConvergenceStatistic>();
        return convergenceStatsToUse;
    }

    public double getProgress() {
        return progress;
    }

    public Hashtable<String, ArrayList<Double>> getTraceInfo() {
        return blueBeastLogger.getTraceInfo();
    }

    public int getMaxChainLength() {
        return maxChainLength;
    }

//
//    /**
//     * Computes new values for convergence statistics
//     * @param convergenceStats
//     */
//    private ESSConvergenceStatistic[] calculateESSScores(ArrayList<ConvergenceStatistic> convergenceStatsToUse, Hashtable<String, ArrayList> traceInfo, double burninPercentage) {
//
//    }

    public void test(){
    	
//    	Enumeration<String> E = traceInfo.keys();
//    	while(E.hasMoreElements()){
//    		System.out.print(E.nextElement()+"\t");
//    	}
//    	for (int j = 0; j < 100; j++) {
//	    	double[] value = new double[variableNames.length];
//	    	for (int i = 0; i < value.length; i++) {
//				value[i] = Math.random();
////				value[i] = i+j;
//			}
//            // TO DO fix this because addLogData is no longer used
//	    	// addLogData(variableNames, value);
//    	}
//    	calculateConvergenceStatistics(convergenceStats, blueBeastLogger.getTraceInfo(), burninPercentage);
//    	for (ConvergeStat cs : convergenceStats) {
//            	for (String s : variableNames) {
//        			System.out.println(s+"\t"+cs.getClass()+"\t"+cs.getStat(s));
//        		}
//
//    	}
//
    }
    
}
