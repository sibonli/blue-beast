package bb.mcmc.adapt;

import dr.inference.operators.MCMCOperator;

public class AdaptAcceptanceRatio {

	public AdaptAcceptanceRatio(MCMCOperator[] currentOperators){
		getRatios();
        assessCurrentRatios();
        calculateNewRatios();
        setRatios();
	}
	
	public void assessCurrentRatios(){
		// Code for this can probably be taken from BEAST (they give summary at end of run right?)
	}

	public void calculateNewRatios(){
		
	}
	
	public void getRatios(){
		
	}
	
	
	public void setRatios(){

        //currentOperators[i].setWeight(); /* The set weight method is public */
		
	}
	
}
