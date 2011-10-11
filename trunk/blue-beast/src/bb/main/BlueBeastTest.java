package bb.main;

import bb.main.BlueBeast;
import bb.mcmc.analysis.ConvergeStat;
import bb.mcmc.analysis.ESSConvergeStat;
import bb.mcmc.analysis.GelmanConvergeStat;
import beast.inference.loggers.BlueBeastLogger;
import dr.inference.mcmc.MCMCOptions;
import dr.inference.model.Parameter;
import dr.inference.operators.MCMCOperator;
import dr.inference.operators.OperatorSchedule;
import dr.inference.operators.ScaleOperator;
import dr.inference.operators.SimpleOperatorSchedule;
import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: sibon
 * Date: 7/8/11
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 *
 * @author Wai Lok Sibon Li
 *
 */
public class BlueBeastTest extends TestCase {

    //TODO This whole class needs to be revamped and have more functionality added (long)

    private BlueBeast bb;
    private String[] variableNames = {"Sneezy", "Sleepy", "Dopey", "Doc", "Happy", "Bashful", "Grumpy"};
    private MCMCOperator[] operators;
    private MCMCOptions mcmcOptions;
    private ArrayList<ConvergeStat> convergenceStatsToUse;
    private int essLowerLimitBoundary;
    private double burninPercentage;
    private boolean dynamicCheckingInterval;
    private boolean autoOptimiseWeights;
    private boolean optimiseChainLength;
    private int maxChainLength;
    private int initialCheckInterval;

    public void setUp() {

        operators = new MCMCOperator[variableNames.length];
        /* We can easily change to see if it works for other operators too */
        operators[0] = new ScaleOperator(new Parameter.Default(0.0), 0.75);
        operators[1] = new ScaleOperator(new Parameter.Default(0.1), 0.75);
        operators[2] = new ScaleOperator(new Parameter.Default(0.2), 0.75);
        operators[3] = new ScaleOperator(new Parameter.Default(0.3), 0.75);
        operators[4] = new ScaleOperator(new Parameter.Default(0.4), 0.75);
        operators[5] = new ScaleOperator(new Parameter.Default(0.5), 0.75);
        operators[6] = new ScaleOperator(new Parameter.Default(0.6), 0.75);
        mcmcOptions = new MCMCOptions();

        convergenceStatsToUse = new ArrayList<ConvergeStat>();
        convergenceStatsToUse.add(ESSConvergeStat.INSTANCE);
        convergenceStatsToUse.add(GelmanConvergeStat.INSTANCE);


        essLowerLimitBoundary = 5;
        burninPercentage = 0.2;
        dynamicCheckingInterval =true;
        autoOptimiseWeights = true;
        optimiseChainLength = true;
        maxChainLength = 100;
        initialCheckInterval = 100;

        
        OperatorSchedule opSche = new SimpleOperatorSchedule(); // Need to do this properly
        for (MCMCOperator mcmcOperator : operators) {
        	opSche.addOperator(mcmcOperator);
		}
        BlueBeastLogger bbl = new BlueBeastLogger(10);
        bb = new BlueBeast(opSche, mcmcOptions, convergenceStatsToUse, bbl,
                     essLowerLimitBoundary, burninPercentage, dynamicCheckingInterval,
                     autoOptimiseWeights, optimiseChainLength, maxChainLength, initialCheckInterval);
    }

    public void testAddLogData() {

        final int iterations = 10;

        double[] traceStep = new double[variableNames.length];
        for(int i=0; i<traceStep.length; i++) {
            traceStep[i] = Math.random();
        }

        /* Add the data multiple times and see if it still works */
        for(int i=0; i<iterations; i++) {
            bb.blueBeastLogger.log(i);
            //addLogData(variableNames, traceStep);
        }

        for(int i=0; i<variableNames.length; i++) {
            ArrayList<Double> traceValues = bb.getTraceInfo().get(variableNames[i]);
            int count = 0;
            for(double d : traceValues) {
                assertEquals(d, traceStep[i], 1e-10);
                count++;
            }
            assertEquals(count, iterations, 1e-10);
        }
        //assertEquals(check, check2, 1e-10);
        //assertTrue(boolean condition);
        //assertFalse(boolean condition)
        //assertSame(java.lang.Object expected, java.lang.Object actual)
    }
}
