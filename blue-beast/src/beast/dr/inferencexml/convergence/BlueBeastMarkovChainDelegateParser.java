/*
* BlueBeastConvergenceListener.java
*
* Copyright (c) 2002-2012 Alexei Drummond, Andrew Rambaut and Marc Suchard
*
* This file is part of BEAST.
* See the NOTICE file distributed with this work for additional
* information regarding copyright ownership and licensing.
*
* BEAST is free software; you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
*  BEAST is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with BEAST; if not, write to the
* Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
* Boston, MA  02110-1301  USA
*/

package dr.inferencexml.convergence;

import bb.loggers.BlueBeastLogger;
import bb.mcmc.analysis.*;
import dr.app.convergence.BlueBeastMarkovChainDelegate;
import dr.xml.*;

import java.util.ArrayList;

/**
* @author Wai Lok Sibon Li
*/
public class BlueBeastMarkovChainDelegateParser extends AbstractXMLObjectParser {

    public static final String DELEGATE = "BlueBeast";

    public String getParserName() {
        return DELEGATE;
    }

    /**
     * @return an mcmc object based on the XML element it was passed.
     */
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {




//        ArrayList<ConvergeStat> convergenceStatsToUse = new ArrayList<ConvergeStat>();
//        String convergenceStatsToUseParameters = xo.getAttribute(CONVERGENCE_STATS_TO_USE, "all");
//        String convergenceStatsToUseParameters = xo.getAttribute(CONVERGENCE_STATS_TO_USE, "ESS");
//        if(convergenceStatsToUseParameters.equals("all")) {
//            convergenceStatsToUse.add(ESSConvergeStat.INSTANCE);
//            convergenceStatsToUse.add(GelmanConvergeStat.INSTANCE);
//            convergenceStatsToUse.add(GewekeConvergeStat.INSTANCE);
//            convergenceStatsToUse.add(ZTempNovelConvergenceStatistic.INSTANCE);
//        }
//        else if(convergenceStatsToUseParameters.equals("ESS")) {
//            convergenceStatsToUse.add(ESSConvergeStat.INSTANCE);
//        }
//        if(convergenceStatsToUseParameters.equals("interIntraChainVariance")) {
//            convergenceStatsToUse.add(GewekeConvergeStat.INSTANCE);
//            convergenceStatsToUse.add(GelmanConvergeStat.INSTANCE);
//        }

        //String[] variableNames = varNames.toArray(new String[varNames.size()]);
//        String[] variableNames = null;//bbl.getvariableNames();

//        int essLowerLimitBoundary = xo.getAttribute(ESS_LOWER_LIMIT_BOUNDARY, 200);
        double burninPercentage = xo.getAttribute(BURNIN_PERCENTAGE, 0.1);
        boolean dynamicCheckingInterval = xo.getAttribute(DYNAMIC_CHECKING_INTERVAL, true);
        //boolean autoOptimiseWeights = xo.getAttribute(AUTO_OPTIMISE_WEIGHTS, false);
        boolean optimiseChainLength = xo.getAttribute(OPTIMISE_CHAIN_LENGTH, true);
        boolean loadTracer = xo.getAttribute(LOAD_TRACER, true);
        int maxChainLength = xo.getAttribute(MAX_CHAIN_LENGTH, Integer.MAX_VALUE);





        final ArrayList<ConvergeStat> convergeStats = new ArrayList<ConvergeStat>(5);


        for (int i = 0; i < xo.getChildCount(); i++) {
            final Object child = xo.getChild(i);
            if (child instanceof ConvergeStat) {
//                if(child instanceof ESSConvergeStat) {
//                    convergeStats.add((ESSConvergeStat) child);
//                }
                convergeStats.add((ConvergeStat) child); // This may not work
            }
//            else if(! (child instanceof BlueBeastLogger)) {
//                throw new RuntimeException("Inputted a wrong object type to the logger, not valid in BLUE-BEAST");
////                logger.addColumn(new LogColumn.Default(child.getClass().toString(), child));
//            }
        }

        // Default is to use ESS only
        if(convergeStats.size() == 0)  {
            ESSConvergeStat essConvergeStat = new ESSConvergeStat(1, 200);
//            essConvergeStat.setupDefaultParameterValues();
            convergeStats.add(essConvergeStat);
        }








        BlueBeastLogger bbl = (BlueBeastLogger) xo.getChild(BlueBeastLogger.class);

        long initialCheckInterval = 1000;
        if (xo.hasAttribute(INITIAL_CHECK_INTERVAL)) {
            initialCheckInterval = xo.getIntegerAttribute(INITIAL_CHECK_INTERVAL);
            System.out.println("Starting log interval: " + initialCheckInterval);
        }
//        mcmc.setCheckInterval(initialCheckInterval);

        return new BlueBeastMarkovChainDelegate(convergeStats, bbl, /*essLowerLimitBoundary, */ burninPercentage,
                dynamicCheckingInterval, optimiseChainLength, maxChainLength,
                initialCheckInterval, loadTracer);
    }


    public String getParserDescription() {
        return "This element returns a delegate to complete tasks related to the MCMC.";
    }

    public Class getReturnType() {
        return BlueBeastMarkovChainDelegate.class;
    }

    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }

    private final XMLSyntaxRule[] rules = {
//            AttributeRule.newStringRule(CHECK_INTERVAL, true),
            AttributeRule.newLongIntegerRule(INITIAL_CHECK_INTERVAL, true),
//            AttributeRule.newStringRule(CONVERGENCE_STATS_TO_USE, true),
//            AttributeRule.newIntegerRule(ESS_LOWER_LIMIT_BOUNDARY, true),
            AttributeRule.newDoubleRule(BURNIN_PERCENTAGE, true),
            AttributeRule.newBooleanRule(DYNAMIC_CHECKING_INTERVAL, true),
            //AttributeRule.newBooleanRule(AUTO_OPTIMISE_WEIGHTS, true),
            AttributeRule.newBooleanRule(OPTIMISE_CHAIN_LENGTH, true),
            AttributeRule.newBooleanRule(LOAD_TRACER, true),
            AttributeRule.newLongIntegerRule(MAX_CHAIN_LENGTH, true),
            new ElementRule(BlueBeastLogger.class),

            new OrRule(
                    new XMLSyntaxRule[]{
                            //new ElementRule(Columns.class, 1, Integer.MAX_VALUE),
                            new ElementRule(ConvergeStat.class, 0, Integer.MAX_VALUE)
                    }
            )

    };

    //public static final String CHECK_INTERVAL = "checkInterval";
//    public static final String CONVERGENCE_STATS_TO_USE = "convergenceStatsToUse";
//    public static final String ESS_LOWER_LIMIT_BOUNDARY = "essLowerLimitBoundary";
    public static final String BURNIN_PERCENTAGE = "burninPercentage";
    public static final String DYNAMIC_CHECKING_INTERVAL = "dynamicCheckingInterval";
    //public static final String AUTO_OPTIMISE_WEIGHTS = "autoOptimiseWeights";
    public static final String OPTIMISE_CHAIN_LENGTH = "optimiseChainLength";
    public static final String MAX_CHAIN_LENGTH = "maxChainLength";
    public static final String INITIAL_CHECK_INTERVAL = "initialCheckInterval";
    public static final String LOAD_TRACER = "loadTracer";

}


///*
//* BlueBeastConvergenceListener.java
//*
//* Copyright (c) 2002-2012 Alexei Drummond, Andrew Rambaut and Marc Suchard
//*
//* This file is part of BEAST.
//* See the NOTICE file distributed with this work for additional
//* information regarding copyright ownership and licensing.
//*
//* BEAST is free software; you can redistribute it and/or modify
//* it under the terms of the GNU Lesser General Public License as
//* published by the Free Software Foundation; either version 2
//* of the License, or (at your option) any later version.
//*
//*  BEAST is distributed in the hope that it will be useful,
//*  but WITHOUT ANY WARRANTY; without even the implied warranty of
//*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//*  GNU Lesser General Public License for more details.
//*
//* You should have received a copy of the GNU Lesser General Public
//* License along with BEAST; if not, write to the
//* Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
//* Boston, MA  02110-1301  USA
//*/
//
//package dr.app.convergence;
//
//import bb.mcmc.analysis.*;
//import bb.loggers.BlueBeastLogger;
//
//import dr.xml.*;
//
//import java.util.ArrayList;
//
///**
//* @author Wai Lok Sibon Li
//*/
//public class BlueBeastMarkovChainDelegateParser extends AbstractXMLObjectParser {
//
//    public static final String DELEGATE = "BlueBeast";
//
//    public String getParserName() {
//        return DELEGATE;
//    }
//
//    /**
//     * @return an mcmc object based on the XML element it was passed.
//     */
//    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
//
//
//
//
//        ArrayList<ConvergeStat> convergenceStatsToUse = new ArrayList<ConvergeStat>();
////        String convergenceStatsToUseParameters = xo.getAttribute(CONVERGENCE_STATS_TO_USE, "all");
//        String convergenceStatsToUseParameters = xo.getAttribute(CONVERGENCE_STATS_TO_USE, "ESS");
//        if(convergenceStatsToUseParameters.equals("all")) {
//            convergenceStatsToUse.add(ESSConvergeStat.INSTANCE);
//            convergenceStatsToUse.add(GelmanConvergeStat.INSTANCE);
//            convergenceStatsToUse.add(GewekeConvergeStat.INSTANCE);
//            convergenceStatsToUse.add(ZTempNovelConvergenceStatistic.INSTANCE);
//        }
//        else if(convergenceStatsToUseParameters.equals("ESS")) {
//            convergenceStatsToUse.add(ESSConvergeStat.INSTANCE);
//        }
//        if(convergenceStatsToUseParameters.equals("interIntraChainVariance")) {
//            convergenceStatsToUse.add(GewekeConvergeStat.INSTANCE);
//            convergenceStatsToUse.add(GelmanConvergeStat.INSTANCE);
//        }
//
//        //String[] variableNames = varNames.toArray(new String[varNames.size()]);
////        String[] variableNames = null;//bbl.getvariableNames();
//
//        int essLowerLimitBoundary = xo.getAttribute(ESS_LOWER_LIMIT_BOUNDARY, 200);
//        double burninPercentage = xo.getAttribute(BURNIN_PERCENTAGE, 0.1);
//        boolean dynamicCheckingInterval = xo.getAttribute(DYNAMIC_CHECKING_INTERVAL, true);
//        //boolean autoOptimiseWeights = xo.getAttribute(AUTO_OPTIMISE_WEIGHTS, false);
//        boolean optimiseChainLength = xo.getAttribute(OPTIMISE_CHAIN_LENGTH, true);
//        boolean loadTracer = xo.getAttribute(LOAD_TRACER, true);
//        int maxChainLength = xo.getAttribute(MAX_CHAIN_LENGTH, Integer.MAX_VALUE);
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//        BlueBeastLogger bbl = (BlueBeastLogger) xo.getChild(BlueBeastLogger.class);
//
//        long initialCheckInterval = 1000;
//        if (xo.hasAttribute(INITIAL_CHECK_INTERVAL)) {
//            initialCheckInterval = xo.getIntegerAttribute(INITIAL_CHECK_INTERVAL);
//            System.out.println("Starting log interval: " + initialCheckInterval);
//        }
////        mcmc.setCheckInterval(initialCheckInterval);
//
//        return new BlueBeastMarkovChainDelegate(convergenceStatsToUse, bbl, essLowerLimitBoundary, burninPercentage,
//                dynamicCheckingInterval, optimiseChainLength, maxChainLength,
//                initialCheckInterval, loadTracer);
//    }
//
//
//    public String getParserDescription() {
//        return "This element returns a delegate to complete tasks related to the MCMC.";
//    }
//
//    public Class getReturnType() {
//        return BlueBeastMarkovChainDelegate.class;
//    }
//
//    public XMLSyntaxRule[] getSyntaxRules() {
//        return rules;
//    }
//
//    private final XMLSyntaxRule[] rules = {
////            AttributeRule.newStringRule(CHECK_INTERVAL, true),
//            AttributeRule.newLongIntegerRule(INITIAL_CHECK_INTERVAL, true),
//            AttributeRule.newStringRule(CONVERGENCE_STATS_TO_USE, true),
//            AttributeRule.newIntegerRule(ESS_LOWER_LIMIT_BOUNDARY, true),
//            AttributeRule.newDoubleRule(BURNIN_PERCENTAGE, true),
//            AttributeRule.newBooleanRule(DYNAMIC_CHECKING_INTERVAL, true),
//            //AttributeRule.newBooleanRule(AUTO_OPTIMISE_WEIGHTS, true),
//            AttributeRule.newBooleanRule(OPTIMISE_CHAIN_LENGTH, true),
//            AttributeRule.newBooleanRule(LOAD_TRACER, true),
//            AttributeRule.newLongIntegerRule(MAX_CHAIN_LENGTH, true),
//            new ElementRule(BlueBeastLogger.class)
//    };
//
//    //public static final String CHECK_INTERVAL = "checkInterval";
//    public static final String CONVERGENCE_STATS_TO_USE = "convergenceStatsToUse";
//    public static final String ESS_LOWER_LIMIT_BOUNDARY = "essLowerLimitBoundary";
//    public static final String BURNIN_PERCENTAGE = "burninPercentage";
//    public static final String DYNAMIC_CHECKING_INTERVAL = "dynamicCheckingInterval";
//    //public static final String AUTO_OPTIMISE_WEIGHTS = "autoOptimiseWeights";
//    public static final String OPTIMISE_CHAIN_LENGTH = "optimiseChainLength";
//    public static final String MAX_CHAIN_LENGTH = "maxChainLength";
//    public static final String INITIAL_CHECK_INTERVAL = "initialCheckInterval";
//    public static final String LOAD_TRACER = "loadTracer";
//
//}
