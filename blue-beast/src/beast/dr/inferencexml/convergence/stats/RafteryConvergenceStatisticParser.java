/*
* RafteryConvergenceStatisticParser.java
*
* Copyright (C) 2002-2009 Alexei Drummond and Andrew Rambaut
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
* BEAST is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with BEAST; if not, write to the
* Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
* Boston, MA  02110-1301  USA
*/

package beast.dr.inferencexml.convergence.stats;

import bb.mcmc.analysis.RafteryConvergeStat;
import dr.xml.*;

/**
* @author Wai Lok Li
*/
public class RafteryConvergenceStatisticParser extends AbstractXMLObjectParser {


    public static final String RAFTERY_CONVERGENCE_STATISTIC = "rafteryConvergenceStatistic";

    public static final String QUANTILE = "quantile";
    public static final String ERROR = "error";
    public static final String PROBABILITY = "prob";
    public static final String CONVERGE_EPS = "convergeEps";
    public static final String THRESHOLD = "threshold";


    public String getParserName() {
        return RAFTERY_CONVERGENCE_STATISTIC;
    }

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        final double quantile = xo.getAttribute(QUANTILE, 0.025);
        final double error = xo.getAttribute(ERROR, 0.005);
        final double prob = xo.getAttribute(PROBABILITY, 0.95);
        final double convergeEps = xo.getAttribute(CONVERGE_EPS, 0.001);
        final double threshold = xo.getAttribute(THRESHOLD, 5);

        return new RafteryConvergeStat(quantile, error, prob, convergeEps, threshold);

//        return new RafteryConvergeStat();
    }

    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************

    public String getParserDescription() {
        return
                "This element returns the Raftery convergence statistic";
    }

    public Class getReturnType() {
        return RafteryConvergeStat.class;
    }

    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }

    private XMLSyntaxRule[] rules = new XMLSyntaxRule[]{
            AttributeRule.newDoubleRule(QUANTILE, true, "Quantile to be estimated"),
            AttributeRule.newDoubleRule(ERROR, true, "The desired margin of error of the estimate."),
            AttributeRule.newDoubleRule(PROBABILITY, true, "Probability of attaining the desired degree of error - 'error'"),
            AttributeRule.newDoubleRule(CONVERGE_EPS, true, "Precision required for estimate of time to convergence"),
            AttributeRule.newDoubleRule(THRESHOLD, true, "Threshold of when to stop iterating for values to converge (implementation is not perfect)"),
//            AttributeRule.newBooleanRule(SINGLE_ROOT_RATE, true, "Whether only a single rate should be used for the two children branches of the root"),

    };

}
