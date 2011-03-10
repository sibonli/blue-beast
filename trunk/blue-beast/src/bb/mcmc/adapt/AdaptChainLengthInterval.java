

package bb.mcmc.adapt;


import dr.inference.mcmc.MCMCOptions;

/*
* Adapt the chain length and the next interval we are checking
*/
public class AdaptChainLengthInterval {

    public void calculateNextCheckingInterval(MCMCOptions mcmcOptions){
        //AdaptChainLengthInterval();
        mcmcOptions.setChainLength(mcmcOptions.getChainLength() + 1000); // temp
	}


}
