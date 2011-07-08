package bb.mcmc.adapt;

import dr.inference.operators.MCMCOperator;

public class AdaptAcceptanceRatio {
    // TODO the whole thing
	public static void adaptAcceptanceRatio(MCMCOperator[] currentOperators){
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
