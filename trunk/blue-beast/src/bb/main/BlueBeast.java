package bb.main;

import bb.mcmc.adapt.AdaptAcceptanceRatio;
import bb.mcmc.adapt.AdaptChainLengthInterval;
import bb.mcmc.analysis.*;
import bb.report.ProgressReport;
import dr.inference.mcmc.MCMCOptions;
import dr.inference.operators.MCMCOperator;
import dr.inference.operators.OperatorSchedule;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

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
    protected Hashtable<String, ArrayList<Double>> traceInfo;

    private int nextCheckChainLength;
    ProgressReport progressReport;

    protected static ArrayList<ConvergenceStatistic> convergenceStatsToUse;
    protected static String[] variableNames;

    /* Copied from BlueBeastMain. Not sure we need this twice */
    protected static int essLowerLimitBoundary;
    protected static double burninPercentage;
    protected static boolean dynamicCheckingInterval;
    protected static boolean autoOptimiseWeights;
    protected static boolean optimiseChainLength;
    protected static int maxChainLength;



    //TODO more constructors which include the program parameters

    /**
     * Use this constructor only if operators cannot be changed implicitly (within the program)
     * but rather using the logfile
     * Otherwise use the main constructor
     */
    public BlueBeast(OperatorSchedule operators, MCMCOptions mcmcOptions,
                     ArrayList<ConvergenceStatistic> convergenceStatsToUse, String logFileLocation) {
        //TODO this constructor is not complete. Do the other one first
        printCitation();
        this.operators = operators;
        this.mcmcOptions = mcmcOptions;
        try {
            logFile = new File(logFileLocation);
            FileReader logFileReader = new FileReader(logFile);

        }catch (IOException e) {
            System.err.println("Input file location not valid");
            e.printStackTrace();
        }
        //TODO obtain a list of trace variable names to variableNames (ideally from logfile) and plug into initialize()
        variableNames = new String[3];
        initialize();
    }

    /**
     * Main constructor
     *
     */
    public BlueBeast(OperatorSchedule operators, MCMCOptions mcmcOptions,
                     ArrayList<ConvergenceStatistic> convergenceStatsToUse, String[] variableNames,
                     int essLowerLimitBoundary, double burninPercentage, boolean dynamicCheckingInterval,
                     boolean autoOptimiseWeights, boolean optimiseChainLength, int maxChainLength) {
        printCitation();
        logFile = null;
        this.operators = operators;
        this.mcmcOptions = mcmcOptions;
        this.convergenceStatsToUse = convergenceStatsToUse;
        this.variableNames = variableNames;

        this.essLowerLimitBoundary = essLowerLimitBoundary;
        this.burninPercentage = burninPercentage;
        this.dynamicCheckingInterval = dynamicCheckingInterval;
        this.autoOptimiseWeights = autoOptimiseWeights;
        this.optimiseChainLength = optimiseChainLength;
        this.maxChainLength = maxChainLength;
        initialize();
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
        initializeTraceInfo(variableNames);
        initializeConvergenceStatistics(convergenceStatsToUse);
        initializeProgressReport(essLowerLimitBoundary);

        nextCheckChainLength = 1000;
        initializeInitialCheckInterval(dynamicCheckingInterval);

    }

    private void initializeProgressReport(int essLowerLimitBoundary) {
        progressReport = new ProgressReport(essLowerLimitBoundary);
    }

    private void initializeConvergenceStatistics(ArrayList<ConvergenceStatistic> convergenceStatsToUse) {
        // Not sure I need to initialize anything here really... Might save this method just in case though
    }

    /**
     * Regardless of whether the interval is dynamic, the default initial check is set at 5% of the total initial chain
     * length
     */
    private void initializeInitialCheckInterval(boolean dynamicCheckingInterval) {
        nextCheckChainLength = (int) Math.round(mcmcOptions.getChainLength()*0.05); //temp

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
    private void initializeTraceInfo(String[] variableNames) {
        traceInfo = new Hashtable<String, ArrayList<Double>>();
        for(int i=0; i<variableNames.length; i++) {
            traceInfo.put(variableNames[i], new ArrayList<Double>());
        }
    }

    /**
     * Add log data to the existing object
     * i.e. this should be called whenever the BEAST log file is updated
     * and an argument of estimated values for each variable of interest should be provided
     */
    public void addLogData(String[] variableNames, double[] traceData) {
        if(variableNames.length != traceData.length) {
            System.out.println("Error in BlueBeast.java: variableNames.length != traceData.length");
            System.exit(-1);
        }
        for(int i=0; i<variableNames.length; i++) {
            if(traceInfo.containsKey(variableNames[i])) {
                traceInfo.get(variableNames[i]).add(new Double(traceData[i]));
                System.out.println("added variable, traceinfo.size(): " + traceInfo.size());
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
        //TODO Check if this works properly
        try {
            RandomAccessFile rfile = new RandomAccessFile(logFile,"r");
            rfile.seek(logFile.length());
            String logLine = rfile.readLine();
            System.out.println("logfile: " + logLine);
        }catch (IOException e) {
            System.err.println("Input file location not valid");
            e.printStackTrace();
        }
    }

    /**
     * Checks whether convergence has been met and whether the analysis is complete
     * Calls all the functionality that takes place at each check from BEAST
     */
    public void check() {
        /* Calculate whether convergence has been met */
        ConvergenceStatistic[][] convergenceStatValues = calculateConvergenceStatistics(convergenceStatsToUse, traceInfo, burninPercentage);
        int i=0;
        search: for(ConvergenceStatistic cs : convergenceStatsToUse) {
            if(cs.getClass().equals(ESSConvergenceStatistic.class)) {
                break search;
            }
            else {
                i++;
            }
        }
        //ESSConvergenceStatistic[] essValues = calculateESSScores(convergenceStatsToUse, traceInfo, burninPercentage);
        progressReport.calculateProgress(convergenceStatValues[i]);
        if(autoOptimiseWeights) {
            AdaptAcceptanceRatio.adaptAcceptanceRatio(operators); // Could easily change this to a static method call
        }
        if(optimiseChainLength) {
            setNextCheckChainLength(AdaptChainLengthInterval.calculateNextCheckingInterval(mcmcOptions, traceInfo, convergenceStatsToUse, dynamicCheckingInterval, maxChainLength));
        }
    }



    /**
     * Computes new values for convergence statistics
     * Utilises an Array of Hashtables
     * @param convergenceStatsToUse
     */
    private ConvergenceStatistic[][] calculateConvergenceStatistics(ArrayList<ConvergenceStatistic> convergenceStatsToUse,
                                                Hashtable<String, ArrayList<Double>> traceInfo, double burninPercentage) {
        //Hashtable<String, ConvergenceStatistic>[] convergenceStatValues = new Hashtable<String, ConvergenceStatistic>[10];

        /* Need two dimensions, one for variable names, one for the statistic type */
        //double[][] convergenceStatValues = new double[convergenceStatsToUse.size()][traceInfo.size()];
        ConvergenceStatistic[][] convergenceStatValues = new ConvergenceStatistic[convergenceStatsToUse.size()][traceInfo.size()];


        //for(int i=0; i<convergenceStatsToUse.size(); i++) {
        int i=0;
        for(ConvergenceStatistic cs : convergenceStatsToUse) {
            //for(int j=0; j<traceInfo.size(); j++) {
            Set<String> keys = traceInfo.keySet();
            int j=0;
            for(String s : keys) {
                ArrayList<Double> traceDataArrayList = traceInfo.get(s);
                Double[] traceData = traceDataArrayList.toArray(new Double[traceDataArrayList.size()]);

                if(cs.getClass().equals(ESSConvergenceStatistic.class)) {
                    convergenceStatValues[i][j] = new ESSConvergenceStatistic(traceData);
                }
                else if(cs.getClass().equals(InterIntraChainVarianceConvergenceStatistic.class)) {
                    convergenceStatValues[i][j] = new InterIntraChainVarianceConvergenceStatistic(traceData);
                }
                else if(cs.getClass().equals(ZTempNovelConvergenceStatistic.class)) {
                    convergenceStatValues[i][j] = new ZTempNovelConvergenceStatistic(traceData);
                }
                else if(cs.getClass().equals(StandardConvergenceStatistic.class)) {
                    convergenceStatValues[i][j] = new StandardConvergenceStatistic(traceData);
                }
                else if(cs.getClass().equals(SimpleConvergenceStatistic.class)) {
                    convergenceStatValues[i][j] = new SimpleConvergenceStatistic(traceData);
                }
                else {
                    throw new RuntimeException("No such convergence statistic");
                }

                j++;
            }
            i++;

            //}
        }


        //Hashtable<String, ConvergenceStatistic>[] convergenceStatValues = new Hashtable[10];
        //convergenceStatValues[0] = new Hashtable<String, ConvergenceStatistic>();
        return convergenceStatValues;
    }

    /**
     * Computes new values for convergence statistics
     * @param convergenceStatsToUse
     */
//    private ESSConvergenceStatistic[] calculateESSScores(ArrayList<ConvergenceStatistic> convergenceStatsToUse, Hashtable<String, ArrayList> traceInfo, double burninPercentage) {
//
//    }
}
