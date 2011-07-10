package bb.mcmc.extendMCMC;

import dr.inference.mcmc.MCMCOptions;

public class AnalysisMCMCChain {

	

	
//	- take the current state and run the MCMC chain for another X iterations
	public static void stopMCMCChain(MCMCOptions mcmcOptions){
        // TODO
        mcmcOptions.setChainLength(mcmcOptions.getChainLength() + 1000); // temp
	}
			
	
}
