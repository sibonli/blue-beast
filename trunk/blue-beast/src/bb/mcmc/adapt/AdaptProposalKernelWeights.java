package bb.mcmc.adapt;

import bb.mcmc.analysis.ConvergeStat;
import bb.report.ConvergenceProgress;
import bb.report.ProgressReporter;
import dr.inference.operators.MCMCOperator;
import dr.inference.operators.OperatorSchedule;

import java.util.ArrayList;

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
