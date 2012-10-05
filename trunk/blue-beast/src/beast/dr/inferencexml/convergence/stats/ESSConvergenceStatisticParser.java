/*
* ESSConvergenceStatisticParser.java
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

import bb.mcmc.analysis.ConvergeStat;
import bb.mcmc.analysis.ESSConvergeStat;
import dr.xml.AttributeRule;
import dr.xml.XMLObject;
import dr.xml.XMLParseException;
import dr.xml.XMLSyntaxRule;

/**
* @author Wai Lok Li
*/
public class ESSConvergenceStatisticParser {


	//FIXME(SW): BB, should we put all names is ESSConvergeStat? or just use the same name?
    public static final String ESS_CONVERGENCE_STATISTIC = "essConvergenceStatistic";
//    public static final String ESS_CONVERGENCE_STATISTIC = ESSConvergeStat.STATISTIC_NAME;

    public static final String ESS_LOWER_LIMIT_BOUNDARY = "essLowerLimitBoundary";
    public static final String ESS_STEP_SIZE = "essStepSize";
    
    public String getParserName() {
        return ESS_CONVERGENCE_STATISTIC;
//    	return ESSConvergeStat.STATISTIC_NAME;
    }
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
//    	FIXME(SW): BB, do we still need set/specific default values in ESSConvergeStat?
//    	OR are we going to handle all the default values here (in parse class)
    	final int essStepSize = xo.getAttribute(ESS_STEP_SIZE, 1);
        final int essLowerLimitBoundary = xo.getAttribute(ESS_LOWER_LIMIT_BOUNDARY, 100);

        return new ESSConvergeStat(essStepSize, essLowerLimitBoundary);
        //TODO(SW): after BEAST parse the whole xml files, 
//        we must be able to record/find the list of parameters beening recorded in BB_LOG
//        therefore we should be able to get that is String[] testingVariable and init to start with.
        
//        return new ESSConvergeStat(String[] testingVariable, essStepSize, essLowerLimitBoundary);

    }

    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************

    public String getParserDescription() {
        return
                "This element returns the effective sample size convergence statistic";
    }

    public Class<? extends ConvergeStat> getReturnType() {
        return ESSConvergeStat.class;
    }

    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }

    private XMLSyntaxRule[] rules = new XMLSyntaxRule[]{
            AttributeRule.newIntegerRule(ESS_LOWER_LIMIT_BOUNDARY, true, "Minimum value of the ESS required to consider the chain converged (default: 100)"),
//            AttributeRule.newBooleanRule(SINGLE_ROOT_RATE, true, "Whether only a single rate should be used for the two children branches of the root"),

    };

}
