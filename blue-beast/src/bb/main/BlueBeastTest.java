package bb.main;

import bb.main.BlueBeast;
import bb.mcmc.analysis.ConvergenceStatistic;
import bb.mcmc.analysis.ESSConvergenceStatistic;
import bb.mcmc.analysis.InterIntraChainVarianceConvergenceStatistic;
import dr.inference.mcmc.MCMCOptions;
import dr.inference.model.Parameter;
import dr.inference.operators.MCMCOperator;
import dr.inference.operators.ScaleOperator;
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

    private BlueBeast bb;
    private String[] variableNames = {"Sneezy", "Sleepy", "Dopey", "Doc", "Happy", "Bashful", "Grumpy"};
    private MCMCOperator[] operators;
    private MCMCOptions mcmcOptions;
    ArrayList<ConvergenceStatistic> convergenceStatsToUse;
    int essLowerLimitBoundary;
    double burninPercentage;
    boolean dynamicCheckingInterval;
    boolean autoOptimiseWeights;
    boolean optimiseChainLength;
    int maxChainLength;

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

        convergenceStatsToUse = new ArrayList<ConvergenceStatistic>();
        convergenceStatsToUse.add(ESSConvergenceStatistic.INSTANCE);
        convergenceStatsToUse.add(InterIntraChainVarianceConvergenceStatistic.INSTANCE);


        essLowerLimitBoundary = 5;
        burninPercentage = 0.2;
        dynamicCheckingInterval =true;
        autoOptimiseWeights = true;
        optimiseChainLength = true;
        maxChainLength = 100;

        bb = new BlueBeast(operators, mcmcOptions, convergenceStatsToUse, variableNames,
                     essLowerLimitBoundary, burninPercentage, dynamicCheckingInterval,
                     autoOptimiseWeights, optimiseChainLength, maxChainLength);
    }

    public void testAddLogData() {

        final int iterations = 10;

        double[] traceStep = new double[variableNames.length];
        for(int i=0; i<traceStep.length; i++) {
            traceStep[i] = Math.random();
        }

        /* Add the data multiple times and see if it still works */
        for(int i=0; i<iterations; i++) {
            bb.addLogData(variableNames, traceStep);
        }

        for(int i=0; i<variableNames.length; i++) {
            ArrayList<Double> traceValues = bb.traceInfo.get(variableNames[i]);
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
