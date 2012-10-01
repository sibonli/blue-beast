/*
* GewekeConvergenceStatisticParser.java
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

import bb.mcmc.analysis.GewekeConvergeStat;
import dr.xml.AttributeRule;
import dr.xml.XMLObject;
import dr.xml.XMLParseException;
import dr.xml.XMLSyntaxRule;

/**
* @author Wai Lok Li
*/
public class GewekeConvergenceStatisticParser {


    public static final String GEWEKE_CONVERGENCE_STATISTIC = "gewekeConvergenceStatistic";

    public static final String BEGIN_FRAC = "beginFrac";
    public static final String END_FRAC = "endFrac";

    public String getParserName() {
        return GEWEKE_CONVERGENCE_STATISTIC;
    }

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

//        final int essLowerLimitBoundary = xo.getAttribute(ESS_LOWER_LIMIT_BOUNDARY, 100);
        final double begin_frac = xo.getAttribute(BEGIN_FRAC, 0.1);
        final double end_frac = xo.getAttribute(END_FRAC, 0.5);

//        return new GewekeConvergeStat(begin_frac, end_frac);

        return GewekeConvergeStat.INSTANCE;
    }

    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************

    public String getParserDescription() {
        return
                "This element returns Geweke's convergence statistic";
    }

    public Class getReturnType() {
        return GewekeConvergeStat.class;
    }

    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }

    private XMLSyntaxRule[] rules = new XMLSyntaxRule[]{
            AttributeRule.newDoubleRule(BEGIN_FRAC, true, "Fraction to use from beginning of chain"),
            AttributeRule.newDoubleRule(END_FRAC, true, "Fraction to use from end of chain"),
//            AttributeRule.newBooleanRule(SINGLE_ROOT_RATE, true, "Whether only a single rate should be used for the two children branches of the root"),

    };

}
