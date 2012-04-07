/**
 *  BLUE-BEAST - Bayesian and Likelihood-based methods Usability Extension
 *  Copyright (C) 2011 Wai Lok Sibon Li & Steven H Wu

 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  @author Wai Lok Sibon Li
 *
 */

package bb.mcmc.adapt;

import bb.mcmc.analysis.ConvergeStat;
import bb.report.ConvergenceProgress;
import bb.report.ProgressReporter;
import dr.inference.operators.MCMCOperator;
import dr.inference.operators.OperatorSchedule;

import java.util.ArrayList;


// TODO New synthesis: Only change operator weights at start of chain when they are initialized since this does not affect the target distribution (mid)
// When running a BEAST job it tells you if acceptance ratios were good anyways. one idea would be to do a short pre-run and use that information to adjust the weights and rerun

public class AdaptProposalKernelWeights {
	public static void adaptAcceptanceRatio(OperatorSchedule operatorSchedule, ProgressReporter progressReporter) {//, ArrayList<ConvergeStat> convergenceStats){
		//getRatios();
        //double progress[] = assessCurrentRatios(operatorSchedule, progressReporter);
        double[] newWeights = calculateNewRatios(operatorSchedule, progressReporter);
        setRatios(operatorSchedule, newWeights);
	}
	
//	public static double[] assessCurrentRatios(OperatorSchedule operatorSchedule, ProgressReporter progressReporter){
//		// Code for this can probably be taken from BEAST (they give summary at end of run right?)
//	}

	public static double[] calculateNewRatios(OperatorSchedule operatorSchedule, ProgressReporter progressReporter){
        double unfinishedTotal = 0.0;
        for(int i=0; i<progressReporter.getConvergenceProgressLength(); i++) {
            unfinishedTotal+= progressReporter.getConvergenceProgress(i).getProgress();
            //newRatios = new
        }

        int operatorCount = operatorSchedule.getOperatorCount();
        double totalWeight = 0.0;
        for(int i=0; i<operatorCount; i++) {
            MCMCOperator o = operatorSchedule.getOperator(i);
            totalWeight += o.getWeight();
        }


        // TODO double check this (currently working)
		// Find which operators correspond to which variables
        // Calculate how much progress each variable still requires
        // Calculate the new weights
        double[] newWeights = new double[operatorCount];
        for(int i=0; i<operatorCount; i++) {
            MCMCOperator o = operatorSchedule.getOperator(i);
            String name = o.getOperatorName().replaceFirst(".+\\(", "").replaceFirst("\\).*", "");
            double currentWeight = o.getWeight();

            ConvergenceProgress convergenceProgress = progressReporter.getConvergenceProgress(name);
            double completionRatio = convergenceProgress.getProgress() / unfinishedTotal;
            newWeights[i] = completionRatio * totalWeight;
        }

        // Return these values
        return newWeights;
	}
	
//	public static void getRatios(){
//
//	}
	

	public static void setRatios(OperatorSchedule operatorSchedule, double[] newRatios){
        for(int i=0; i<operatorSchedule.getOperatorCount(); i++) {
            operatorSchedule.getOperator(i).setWeight(newRatios[i]);
        }

        operatorSchedule.operatorsHasBeenUpdated();

        //currentOperators[i].setWeight(); /* The set weight method is public */
		
	}
	
}
