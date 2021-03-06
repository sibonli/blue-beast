/*
* GelmanConvergenceStatisticParser.java
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

import bb.mcmc.analysis.GelmanConvergeStat;
import dr.xml.*;

/**
* @author Wai Lok Li
*/
public class GelmanConvergenceStatisticParser extends AbstractXMLObjectParser {


    public static final String GELMAN_CONVERGENCE_STATISTIC = "gelmanConvergenceStatistic";

    public static final String ESS_LOWER_LIMIT_BOUNDARY = "essLowerLimitBoundary";

    public String getParserName() {
        return GELMAN_CONVERGENCE_STATISTIC;
    }

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        final double essLowerLimitBoundary = xo.getAttribute(ESS_LOWER_LIMIT_BOUNDARY, 100);

//        return new GelmanConvergeStat(essLowerLimitBoundary);

        return new GelmanConvergeStat();
    }

    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************

    public String getParserDescription() {
        return
                "This element returns Gelman's convergence statistic";
    }

    public Class getReturnType() {
        return GelmanConvergeStat.class;
    }

    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }

    private XMLSyntaxRule[] rules = new XMLSyntaxRule[]{
            AttributeRule.newDoubleRule(ESS_LOWER_LIMIT_BOUNDARY, true, "Minimum value of the ESS required to consider the chain converged (default: 100)"),
//            AttributeRule.newBooleanRule(SINGLE_ROOT_RATE, true, "Whether only a single rate should be used for the two children branches of the root"),

    };

}
