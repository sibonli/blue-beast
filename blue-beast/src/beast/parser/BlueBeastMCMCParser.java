/*
 * Interaction with BEAST XML
 * XML options to turn our program on
 * Warning message when turned on: Please cite our paper if you use this. Advise to check chain manually
 * Weights placed on the proposal kernels will be treated as starting weights
 * Optional XML options (hidden) -
 *      constant/dynamic interval change
 *      autooptimise weights
 *      maximum chain length (if not infinity)
 *      convergence assessment statistics (default is to use all)
 *
 *
 * Mostly copied from MCMCParser from BEAST
 *
 * @author Wai Lok Sibon Li

*/

package beast.parser;

import bb.main.BlueBeast;
import bb.mcmc.analysis.ConvergenceStatistic;
import bb.mcmc.analysis.ESSConvergenceStatistic;
import bb.mcmc.analysis.InterIntraChainVarianceConvergenceStatistic;
import bb.mcmc.analysis.ZTempNovelConvergenceStatistic;
import beast.core.BlueBeastMCMC;
import dr.inference.loggers.Logger;
import dr.inference.loggers.MCLogger;
import dr.inference.markovchain.MarkovChain;
import dr.inference.mcmc.MCMC;
import dr.inference.mcmc.MCMCOptions;
import dr.inference.model.CompoundLikelihood;
import dr.inference.model.Likelihood;
import dr.inference.operators.OperatorSchedule;
import dr.xml.*;

import java.util.ArrayList;

public class BlueBeastMCMCParser extends AbstractXMLObjectParser {

    public String getParserName() {
        return MCMC;
    }

    /**
     * @return an mcmc object based on the XML element it was passed.
     */
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        BlueBeastMCMC mcmc = new BlueBeastMCMC(xo.getAttribute(NAME, "mcmc1"));
        MCMCOptions options = new MCMCOptions();
        OperatorSchedule opsched = (OperatorSchedule) xo.getChild(OperatorSchedule.class);
        Likelihood likelihood = (Likelihood) xo.getChild(Likelihood.class);
//        BlueBeast bb = (BlueBeast) xo.getChild(BlueBeast.class);
        ArrayList<Logger> loggers = new ArrayList<Logger>();

        likelihood.setUsed();

        options.setChainLength(xo.getIntegerAttribute(CHAIN_LENGTH));
        options.setUseCoercion(xo.getAttribute(COERCION, true));
        if (xo.hasAttribute(PRE_BURNIN)) {
            options.setCoercionDelay(xo.getAttribute(PRE_BURNIN, options.getChainLength() / 100));
        }
        options.setCoercionDelay(xo.getAttribute(COERCION_DELAY, options.getChainLength() / 100));
        options.setTemperature(xo.getAttribute(TEMPERATURE, 1.0));
        options.setFullEvaluationCount(xo.getAttribute(FULL_EVALUATION, 2000));
        options.setMinOperatorCountForFullEvaluation(xo.getAttribute(MIN_OPS_EVALUATIONS, 1));

        if (xo.hasAttribute(CHECK_INTERVAL)) {
            mcmc.setCheckInterval(xo.getIntegerAttribute(CHECK_INTERVAL));
        }
        else {
            mcmc.setCheckInterval(1000);
        }

        ArrayList<String> varNames = new ArrayList<String>(xo.getChildCount());
        for (int i = 0; i < xo.getChildCount(); i++) {
            Object child = xo.getChild(i);
            if (child instanceof Logger) {
                loggers.add((Logger) child);
                varNames.add(((MCLogger) child).getColumn(0).getLabel()); // Hope this works
            }
        }

        mcmc.setShowOperatorAnalysis(true);
        if (xo.hasAttribute(OPERATOR_ANALYSIS)) {
            mcmc.setOperatorAnalysisFileName(xo.getStringAttribute(OPERATOR_ANALYSIS));
        }

        Logger[] loggerArray = new Logger[loggers.size()];
        loggers.toArray(loggerArray);

        java.util.logging.Logger.getLogger("dr.inference").info("Creating the MCMC chain:" +
                "\n  chainLength=" + options.getChainLength() +
                "\n  autoOptimize=" + options.useCoercion() +
                (options.useCoercion() ? "\n  autoOptimize delayed for " + options.getCoercionDelay() + " steps" : "") +
                (options.fullEvaluationCount() == 0 ? "\n  full evaluation test off" : "")
                );


        ArrayList<ConvergenceStatistic> convergenceStatsToUse = new ArrayList<ConvergenceStatistic>();
        String convergenceStatsToUseParameters = xo.getAttribute(CONVERGENCE_STATS_TO_USE, "all");
        if(convergenceStatsToUseParameters.equals("all")) {
            convergenceStatsToUse.add(ESSConvergenceStatistic.INSTANCE);
            convergenceStatsToUse.add(InterIntraChainVarianceConvergenceStatistic.INSTANCE);
            convergenceStatsToUse.add(ZTempNovelConvergenceStatistic.INSTANCE);
        }
        else if(convergenceStatsToUseParameters.equals("ESS")) {
            convergenceStatsToUse.add(ESSConvergenceStatistic.INSTANCE);
        }
        if(convergenceStatsToUseParameters.equals("interIntraChainVariance")) {
            convergenceStatsToUse.add(InterIntraChainVarianceConvergenceStatistic.INSTANCE);
        }

        String[] variableNames = varNames.toArray(new String[varNames.size()]);

        int essLowerLimitBoundary = xo.getAttribute(ESS_LOWER_LIMIT_BOUNDARY, 100);
        double burninPercentage = xo.getAttribute(BURNIN_PERCENTAGE, 0.1);;
        boolean dynamicCheckingInterval = xo.getAttribute(DYNAMIC_CHECKING_INTERVAL, true);
        boolean autoOptimiseWeights = xo.getAttribute(AUTO_OPTIMISE_WEIGHTS, true);
        boolean optimiseChainLength = xo.getAttribute(OPTIMISE_CHAIN_LENGTH, true);
        int maxChainLength = xo.getAttribute(MAX_CHAIN_LENGTH, Integer.MAX_VALUE);
        BlueBeast bb = new BlueBeast(opsched, options, convergenceStatsToUse, variableNames, essLowerLimitBoundary,
                burninPercentage, dynamicCheckingInterval, autoOptimiseWeights, optimiseChainLength, maxChainLength);

        mcmc.init(options, likelihood, opsched, loggerArray, bb);

        MarkovChain mc = mcmc.getMarkovChain();
        double initialScore = mc.getCurrentScore();

        if (initialScore == Double.NEGATIVE_INFINITY) {
            String message = "The initial posterior is zero";
            if (likelihood instanceof CompoundLikelihood) {
                message += ": " + ((CompoundLikelihood) likelihood).getDiagnosis(2);
            } else {
                message += "!";
            }
            throw new IllegalArgumentException(message);
        }

        if (!xo.getAttribute(SPAWN, true))
            mcmc.setSpawnable(false);

        return mcmc;
    }

    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************

    public String getParserDescription() {
        return "This element returns an MCMC chain and runs the chain as a side effect.";
    }

    public Class getReturnType() {
        return MCMC.class;
    }

    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }

    private final XMLSyntaxRule[] rules = {
            AttributeRule.newIntegerRule(CHAIN_LENGTH),
            AttributeRule.newBooleanRule(COERCION, true),
            AttributeRule.newIntegerRule(COERCION_DELAY, true),
            AttributeRule.newIntegerRule(PRE_BURNIN, true),
            AttributeRule.newDoubleRule(TEMPERATURE, true),
            AttributeRule.newIntegerRule(FULL_EVALUATION, true),
            AttributeRule.newIntegerRule(MIN_OPS_EVALUATIONS, true),
            AttributeRule.newBooleanRule(SPAWN, true),
            AttributeRule.newStringRule(NAME, true),
            AttributeRule.newStringRule(OPERATOR_ANALYSIS, true),
            AttributeRule.newStringRule(CHECK_INTERVAL, true),

            AttributeRule.newStringRule(CONVERGENCE_STATS_TO_USE, true),
            AttributeRule.newIntegerRule(ESS_LOWER_LIMIT_BOUNDARY, true),
            AttributeRule.newDoubleRule(BURNIN_PERCENTAGE, true),
            AttributeRule.newBooleanRule(DYNAMIC_CHECKING_INTERVAL, true),
            AttributeRule.newBooleanRule(AUTO_OPTIMISE_WEIGHTS, true),
            AttributeRule.newBooleanRule(OPTIMISE_CHAIN_LENGTH, true),
            AttributeRule.newIntegerRule(MAX_CHAIN_LENGTH, true),

            new ElementRule(OperatorSchedule.class),
            new ElementRule(Likelihood.class),
            new ElementRule(Logger.class, 1, Integer.MAX_VALUE),

//            new ElementRule(BlueBeast.class)
    };

    public static final String COERCION = "autoOptimize";
    public static final String NAME = "name";
    public static final String PRE_BURNIN = "preBurnin";
    public static final String COERCION_DELAY = "autoOptimizeDelay";
    public static final String MCMC = "mcmc";
    public static final String CHAIN_LENGTH = "chainLength";
    public static final String FULL_EVALUATION = "fullEvaluation";
    public static final String MIN_OPS_EVALUATIONS = "minOpsFullEvaluations";
    public static final String WEIGHT = "weight";
    public static final String TEMPERATURE = "temperature";
    public static final String SPAWN = "spawn";
    public static final String OPERATOR_ANALYSIS = "operatorAnalysis";
    public static final String CHECK_INTERVAL = "checkInterval";
    public static final String CONVERGENCE_STATS_TO_USE = "convergenceStatsToUse";
    public static final String ESS_LOWER_LIMIT_BOUNDARY = "essLowerLimitBoundary";
    public static final String BURNIN_PERCENTAGE = "burninPercentage";
    public static final String DYNAMIC_CHECKING_INTERVAL = "dynamicCheckingInterval";
    public static final String AUTO_OPTIMISE_WEIGHTS = "autoOptimiseWeights";
    public static final String OPTIMISE_CHAIN_LENGTH = "optimiseChainLength";
    public static final String MAX_CHAIN_LENGTH = "maxChainLength";







}
