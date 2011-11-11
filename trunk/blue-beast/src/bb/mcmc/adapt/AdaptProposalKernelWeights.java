package bb.mcmc.adapt;

import bb.mcmc.analysis.ConvergeStat;
import dr.inference.operators.MCMCOperator;
import dr.inference.operators.OperatorSchedule;

import java.util.ArrayList;

public class AdaptProposalKernelWeights {
    // TODO Currently working
	public static void adaptAcceptanceRatio(OperatorSchedule currentOperators, double progress, ArrayList<ConvergeStat> convergenceStats){
		getRatios();
        assessCurrentRatios();
        calculateNewRatios();
        setRatios();
	}
	
	public static void assessCurrentRatios(){
		// Code for this can probably be taken from BEAST (they give summary at end of run right?)
	}

	public static void calculateNewRatios(){
		
	}
	
	public static void getRatios(){
		
	}
	
	
	public static void setRatios(){

        //currentOperators[i].setWeight(); /* The set weight method is public */
		
	}
	
}
