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
import bb.mcmc.analysis.ConvergeStat;
import beast.core.BlueBeastMCMC;
import dr.inference.loggers.Logger;
import dr.inference.markovchain.MarkovChain;
import dr.inference.mcmc.MCMC;
import dr.inference.mcmc.MCMCOptions;
import dr.inference.model.CompoundLikelihood;
import dr.inference.model.Likelihood;
import dr.inference.operators.OperatorSchedule;
import dr.xml.*;

import java.util.ArrayList;

public class BlueBeastParser extends AbstractXMLObjectParser {

    public String getParserName() {
        return BLUE_BEAST_MCMC;
    }

    //TODO This class is redundant and should not be used or committed. BlueBeast objects are parsed within BlueBeastMCMCParser

    /**
     * @return an mcmc object based on the XML element it was passed.
     */
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        BlueBeastMCMC mcmc = new BlueBeastMCMC(xo.getAttribute(NAME, "mcmc1"));

        if (xo.hasAttribute(CHECK_INTERVAL)) {
            mcmc.setCheckInterval(xo.getIntegerAttribute(CHECK_INTERVAL));
        }


        for (int i = 0; i < xo.getChildCount(); i++) {
            Object child = xo.getChild(i);
            if (child instanceof Logger) {
                loggers.add((Logger) child); //FIXME
            }
        }

        mcmc.setShowOperatorAnalysis(true);
        if (xo.hasAttribute(OPERATOR_ANALYSIS)) {
            mcmc.setOperatorAnalysisFileName(xo.getStringAttribute(OPERATOR_ANALYSIS));
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
            new ElementRule(OperatorSchedule.class),
            new ElementRule(Likelihood.class),
            new ElementRule(Logger.class, 1, Integer.MAX_VALUE),
            new ElementRule(BlueBeast.class)
    };

    public static final String COERCION = "autoOptimize";
    public static final String NAME = "name";
    public static final String PRE_BURNIN = "preBurnin";
    public static final String COERCION_DELAY = "autoOptimizeDelay";
    public static final String BLUE_BEAST_MCMC = "mcmc";
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
