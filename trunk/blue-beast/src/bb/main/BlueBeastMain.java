/*
 * BlueBeastMain.java
 *
 * Copyright (C) 2011 Wai Lok Sibon Li and Steven Wu
 *
 * This file is part of BLUE BEAST.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership and licensing.
 *
 * BLUE BEAST is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 *  BLUE BEAST is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with BLUE BEAST; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301  USA
 *
 * @author Wai Lok Sibon Li
 *
 */

package bb.main;

import bb.mcmc.analysis.ConvergenceStat;
import java.util.ArrayList;


public class BlueBeastMain {

    protected int ESSLowerLimitBoundary = 100;
    protected boolean dynamicCheckingInterval = true;
    protected boolean autoOptimiseWeights = true;
    protected int maxChainLength = Integer.MAX_VALUE;
    protected ArrayList<ConvergenceStat> convergenceStatsToUse;


    private final String CITATION = "";


    public BlueBeastMain() {
        System.out.println("BLUE BEAST is in use. Please cite " + CITATION);
        System.out.println("Note: It is recommended that the convergence of MCMC chains are verified manually");

    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
