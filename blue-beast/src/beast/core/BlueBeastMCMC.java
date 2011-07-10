package beast.core;

import dr.inference.loggers.Logger;
import dr.inference.markovchain.MarkovChain;
import dr.inference.mcmc.MCMC;
import dr.inference.mcmc.MCMCCriterion;
import dr.inference.mcmc.MCMCOptions;
import dr.inference.model.Likelihood;
import dr.inference.operators.OperatorSchedule;
import dr.inference.prior.Prior;
import dr.util.NumberFormatter;

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
    // TODO
    /*public Input<Integer> m_period = new Input<Integer>("period","period between samples being tested (default 1000)", 1000);

        public Input<Boolean> m_doTest = new Input<Boolean>("useTest","flag to indicate whether samples should be tested (default true)", true);

        boolean m_bTest;
        int m_nPeriod;

        //@Override
        public void initAndValidate() {
            m_bTest = useTest.get();
            m_nPeriod = m_period.get();
        }

        //@Override
        void callUserFunction(int iSample) {
            if (m_bTest && iSample % m_nPeriod==0) {
                // do the work here
            }
        }  */

        /**
     * @return the length of this analysis.
     */
//    public final int getChainLength() {
//        //getOptions().setChainLength(-1);
//        return getOptions().getChainLength();
//    }

    public BlueBeastMCMC(String s) {
        super(s);
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
     */
    public void init(
            MCMCOptions options,
            Likelihood likelihood,
            Prior prior,
            OperatorSchedule schedule,
            Logger[] loggers) {

        MCMCCriterion criterion = new MCMCCriterion();
        criterion.setTemperature(options.getTemperature());

        mc = new MarkovChain(prior, likelihood, schedule, criterion,
                options.fullEvaluationCount(), options.minOperatorCountForFullEvaluation(), options.useCoercion());

        this.options = options;
        this.loggers = loggers;
        this.schedule = schedule;

        //initialize transients
        currentState = 0;

        // Does not seem to be in use (JH)
/*
        stepsPerReport = 1;
        while ((getChainLength() / stepsPerReport) > 1000) {
            stepsPerReport *= 2;
        }*/
    }


    public int getCheckInterval() {
        return checkInterval;
    }

    public void setCheckInterval(int checkInterval) {
        this.checkInterval = checkInterval;
    }

    public boolean isDoTest() {
        return doTest;
    }

    public void setDoTest(boolean doTest) {
        this.doTest = doTest;
    }

    private final boolean isAdapting = true;
    private boolean stopping = false;
    private boolean showOperatorAnalysis = true;
    private String operatorAnalysisFileName = null;
    private final dr.util.Timer timer = new dr.util.Timer();
    private int currentState = 0;
    //private int stepsPerReport = 1000;
    private final NumberFormatter formatter = new NumberFormatter(8);

    /**
     * this markov chain does most of the work.
     */
    private MarkovChain mc;

    /**
     * the options of this MCMC analysis
     */
    private MCMCOptions options;

    private Logger[] loggers;
    private OperatorSchedule schedule;

    private String id = null;

    private int checkInterval; /* period between samples being tested (default 1000) */
    private boolean doTest; /* flag to indicate whether samples should be tested (default true) */

}
