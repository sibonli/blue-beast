package beast.core;

import bb.main.BlueBeast;
import dr.inference.loggers.Logger;
import dr.inference.markovchain.MarkovChain;
import dr.inference.markovchain.MarkovChainListener;
import dr.inference.mcmc.MCMC;
import dr.inference.mcmc.MCMCCriterion;
import dr.inference.mcmc.MCMCOptions;
import dr.inference.model.Likelihood;
import dr.inference.model.Model;
import dr.inference.operators.*;
import dr.inference.operators.OperatorSchedule;
import dr.inference.prior.Prior;
import dr.util.NumberFormatter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by IntelliJ IDEA.
 * User: sibon
 * Date: 3/15/11
 * Time: 2:43 PM
 * To change this template use File | Settings | File Templates.
 *
 * @author Wai Lok Sibon Li
 *
 */

//@Description("Sibon's MCMC extension with periodical tests")
public class BlueBeastMCMC extends MCMC {

    public BlueBeastMCMC(String s) {
        super(s);
    }


    /**
     * Must be called before calling chain.
     *
     * @param options    the options for this MCMC analysis
     * @param schedule   operator schedule to be used in chain.
     * @param likelihood the likelihood for this MCMC
     * @param loggers    an array of loggers to record output of this MCMC run
     * @param bb         a Blue Beast control object
     */
    public void init(
            MCMCOptions options,
            Likelihood likelihood,
            OperatorSchedule schedule,
            Logger[] loggers, BlueBeast bb) {

        init(options, likelihood, Prior.UNIFORM_PRIOR, schedule, loggers, bb);
    }

    /**
     * Must be called before calling chain.
     *
     * Override method from MCMC.java
     *
     * @param options    the options for this MCMC analysis
     * @param prior      the prior disitrbution on the model parameters.
     * @param schedule   operator schedule to be used in chain.
     * @param likelihood the likelihood for this MCMC
     * @param loggers    an array of loggers to record output of this MCMC run
     * @param bb         a Blue Beast control object
     */
    public void init(
            MCMCOptions options,
            Likelihood likelihood,
            Prior prior,
            OperatorSchedule schedule,
            Logger[] loggers, BlueBeast bb) {


        MCMCCriterion criterion = new MCMCCriterion();
        criterion.setTemperature(options.getTemperature());

//        mc = new BlueBeastMarkovChain(prior, likelihood, schedule, criterion,
//                options.fullEvaluationCount(), options.minOperatorCountForFullEvaluation(), options.useCoercion());
        mc = new BlueBeastMarkovChain(prior, likelihood, schedule, criterion,
                options.fullEvaluationCount(), options.minOperatorCountForFullEvaluation(), options.useCoercion(), bb);

        this.options = options;
        this.loggers = loggers;
        this.schedule = schedule;
        //this.init(options, likelihood, prior, schedule, loggers);

        //initialize transients
        currentState = 0;
        this.bb = bb;
    }


    /**
     * Must be called before calling chain.
     *
     * @param chainlength chain length
     * @param likelihood the likelihood for this MCMC
     * @param operators  an array of MCMC operators
     * @param loggers    an array of loggers to record output of this MCMC run
     * @param bb         a Blue Beast control object
     */
    public void init(int chainlength,
                     Likelihood likelihood,
                     MCMCOperator[] operators,
                     Logger[] loggers, BlueBeast bb) {

        MCMCOptions options = new MCMCOptions();
        options.setCoercionDelay(0);
        options.setChainLength(chainlength);
        MCMCCriterion criterion = new MCMCCriterion();
        criterion.setTemperature(1);
        OperatorSchedule schedule = new SimpleOperatorSchedule();
        for (MCMCOperator operator : operators) schedule.addOperator(operator);

        init(options, likelihood, Prior.UNIFORM_PRIOR, schedule, loggers, bb);

        this.bb = bb;
    }


    public int getCheckInterval() {
        return checkInterval;
    }

    public void setCheckInterval(int checkInterval) {
        this.checkInterval = checkInterval;
    }



    /**
     * this markov chain does most of the work.
     */
    //private MarkovChain mc;
//    private BlueBeastMarkovChain mc;
//    private MCMCOptions options;                   // merge
//    private Logger[] loggers;
//    private OperatorSchedule schedule;
//    private String id = null;

    private int checkInterval; /* period between samples being tested (default 1000) */
    //private boolean doCheck; /* flag to indicate whether samples should be tested (default true) */




    /**
     * This method actually initiates the MCMC analysis.
     * This is where the BlueBeastMCMC magic happens
     */
    public void chain() {

        stopping = false;
        currentState = 0;

        timer.start();

        if (loggers != null) {
            for (Logger logger : loggers) {
                logger.startLogging();
            }
        }

        if (!stopping) {
            mc.addMarkovChainListener(chainListener);


            int chainLength = getChainLength();

            final int coercionDelay = getCoercionDelay();

            if (coercionDelay > 0) {
                // Run the chain for coercionDelay steps with coercion disabled
                mc.runChain(coercionDelay, true);
                chainLength -= coercionDelay;

                // reset operator acceptance levels
                for (int i = 0; i < schedule.getOperatorCount(); i++) {
                    schedule.getOperator(i).reset();
                }
            }

            //mc.runChain(chainLength, false);
            mc.runChain(bb.getMaxChainLength(), false);

            mc.terminateChain();

            mc.removeMarkovChainListener(chainListener);
        }
        timer.stop();
    }


    /**
     * override method
     * @return the progress (0 to 1) of the MCMC analysis.
     */
    // TODO Check if this method is necessary at all method getProgress in MCMC.java is final, cannot override, may not need to (long)
    public final double getProgress2() { //getProgress() {
        //are there any if statements here? Just saying. Don't think so (long)
        //return bb.getProgress();
        return 1.0;
    }






    //TODO remove the duplicate methods/code which were taken from MCMC.java (mid)
    /**
     * Here are a list of methods which are duplicated from Beast MCMC.java just because those methods are currently
     * still private. Hopefully I can do something about that later.
     */







//    @Override
//    public BlueBeastMarkovChain getMarkovChain() {
//        return mc;
//    }



//    private final MarkovChainListener chainListener = new MarkovChainListener() {              // merge
//
//        // MarkovChainListener interface *******************************************
//        // for receiving messages from subordinate MarkovChain
//
//        /**
//         * Called to update the current model keepEvery states.
//         */
//        public void currentState(int state, Model currentModel) {
//
//            currentState = state;
//
//            if (loggers != null) {
//                for (Logger logger : loggers) {
//                    logger.log(state);
//                }
//            }
//        }
//
//        /**
//         * Called when a new new best posterior state is found.
//         */
//        public void bestState(int state, Model bestModel) {
//            currentState = state;
//        }
//
//        /**
//         * cleans up when the chain finishes (possibly early).
//         */
//        public void finished(int chainLength) {
//            currentState = chainLength;
//
//            if (loggers != null) {
//                for (Logger logger : loggers) {
//                    logger.log(currentState);
//                    logger.stopLogging();
//                }
//            }
//            // OperatorAnalysisPrinter class can do the job now
//            if (showOperatorAnalysis) {
//                showOperatorAnalysis(System.out);
//                try {
//                    FileOutputStream out = new FileOutputStream(id + ".operators");
//                    showOperatorAnalysis(new PrintStream(out));
//                    out.flush();
//                    out.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            if (operatorAnalysisFileName != null) {
//                try {
//                    FileOutputStream out = new FileOutputStream(operatorAnalysisFileName);
//                    showOperatorAnalysis(new PrintStream(out));
//                    out.flush();
//                    out.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            // How should premature finish be flagged?
//        }
//    };
//
//
//        /**
//     * Writes ano operator analysis to the provided print stream
//     *
//     * @param out the print stream to write operator analysis to
//     */
//    private void showOperatorAnalysis(PrintStream out) {
//        out.println();
//        out.println("Operator analysis");
//            out.println(formatter.formatToFieldWidth("Operator", 50) +
//                    formatter.formatToFieldWidth("Tuning", 9) +
//                    formatter.formatToFieldWidth("Count", 11) +
//                    formatter.formatToFieldWidth("Time", 9) +
//                    formatter.formatToFieldWidth("Time/Op", 9) +
//                    formatter.formatToFieldWidth("Pr(accept)", 11) +
//                    (options.useCoercion() ? "" : " Performance suggestion"));
//
//        for (int i = 0; i < schedule.getOperatorCount(); i++) {
//
//            final MCMCOperator op = schedule.getOperator(i);
//            if (op instanceof JointOperator) {
//                JointOperator jointOp = (JointOperator) op;
//                for (int k = 0; k < jointOp.getNumberOfSubOperators(); k++) {
//                    out.println(formattedOperatorName(jointOp.getSubOperatorName(k))
//                            + formattedParameterString(jointOp.getSubOperator(k))
//                            + formattedCountString(op)
//                            + formattedTimeString(op)
//                            + formattedTimePerOpString(op)
//                            + formattedProbString(jointOp)
//                            + (options.useCoercion() ? "" : formattedDiagnostics(jointOp, MCMCOperator.Utils.getAcceptanceProbability(jointOp)))
//                    );
//                }
//            } else {
//                out.println(formattedOperatorName(op.getOperatorName())
//                        + formattedParameterString(op)
//                        + formattedCountString(op)
//                        + formattedTimeString(op)
//                        + formattedTimePerOpString(op)
//                        + formattedProbString(op)
//                        + (options.useCoercion() ? "" : formattedDiagnostics(op, MCMCOperator.Utils.getAcceptanceProbability(op)))
//                );
//            }
//
//        }
//        out.println();
//    }
//
//    private String formattedOperatorName(String operatorName) {
//        return formatter.formatToFieldWidth(operatorName, 50);
//    }
//
//    private String formattedParameterString(MCMCOperator op) {
//        String pString = "        ";
//        if (op instanceof CoercableMCMCOperator && ((CoercableMCMCOperator) op).getMode() != CoercionMode.COERCION_OFF) {
//            pString = formatter.formatToFieldWidth(formatter.formatDecimal(((CoercableMCMCOperator) op).getRawParameter(), 3), 8);
//        }
//        return pString;
//    }
//
//    private String formattedCountString(MCMCOperator op) {
//        final int count = op.getCount();
//        return formatter.formatToFieldWidth(Integer.toString(count), 10) + " ";
//    }
//
//    private String formattedTimeString(MCMCOperator op) {
//        final long time = op.getTotalEvaluationTime();
//        return formatter.formatToFieldWidth(Long.toString(time), 8) + " ";
//    }
//
//    private String formattedTimePerOpString(MCMCOperator op) {
//        final double time = op.getMeanEvaluationTime();
//        return formatter.formatToFieldWidth(formatter.formatDecimal(time, 2), 8) + " ";
//    }
//
//    private String formattedProbString(MCMCOperator op) {
//        final double acceptanceProb = MCMCOperator.Utils.getAcceptanceProbability(op);
//        return formatter.formatToFieldWidth(formatter.formatDecimal(acceptanceProb, 4), 11) + " ";
//    }
//
//    private String formattedDiagnostics(MCMCOperator op, double acceptanceProb) {
//
//        String message = "good";
//        if (acceptanceProb < op.getMinimumGoodAcceptanceLevel()) {
//            if (acceptanceProb < (op.getMinimumAcceptanceLevel() / 10.0)) {
//                message = "very low";
//            } else if (acceptanceProb < op.getMinimumAcceptanceLevel()) {
//                message = "low";
//            } else message = "slightly low";
//
//        } else if (acceptanceProb > op.getMaximumGoodAcceptanceLevel()) {
//            double reallyHigh = 1.0 - ((1.0 - op.getMaximumAcceptanceLevel()) / 10.0);
//            if (acceptanceProb > reallyHigh) {
//                message = "very high";
//            } else if (acceptanceProb > op.getMaximumAcceptanceLevel()) {
//                message = "high";
//            } else message = "slightly high";
//        }
//
//        String performacsMsg;
//        if (op instanceof GibbsOperator) {
//            performacsMsg = "none (Gibbs operator)";
//        } else {
//            final String suggestion = op.getPerformanceSuggestion();
//            performacsMsg = message + "\t" + suggestion;
//        }
//
//        return performacsMsg;
//    }
//
//        //PRIVATE METHODS *****************************************
//    private int getCoercionDelay() {
//
//        int delay = options.getCoercionDelay();
//        if (delay < 0) {
//            delay = options.getChainLength() / 100;
//        }
//        if (options.useCoercion()) return delay;
//
//        for (int i = 0; i < schedule.getOperatorCount(); i++) {
//            MCMCOperator op = schedule.getOperator(i);
//
//            if (op instanceof CoercableMCMCOperator) {
//                if (((CoercableMCMCOperator) op).getMode() == CoercionMode.COERCION_ON) return delay;
//            }
//        }
//
//        return -1;
//    }
//
//
//    private final boolean isAdapting = true;
//    private boolean stopping = false;
//    private boolean showOperatorAnalysis = true;
//    private String operatorAnalysisFileName = null;
//    private final dr.util.Timer timer = new dr.util.Timer();
//    private int currentState = 0;
//    private final NumberFormatter formatter = new NumberFormatter(8);

    private BlueBeast bb;

}
