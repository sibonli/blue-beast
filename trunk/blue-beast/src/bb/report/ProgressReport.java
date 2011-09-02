/*
 * @author Wai Lok Sibon Li
 */

package bb.report;

import bb.mcmc.analysis.ConvergeStat;
import bb.mcmc.analysis.ESSConvergeStat;

public class ProgressReport {

    //private double[] currentESSScores;
    int essLowerLimitBoundary;

	public ProgressReport(int essLowerLimitBoundary) {
        //this.currentESSScores = currentESSScores; /* These values should change according to changes in the original array */
        this.essLowerLimitBoundary = essLowerLimitBoundary;
		System.out.println("Note: Progress report is only a rough estimate");
	}

    /**
     * Calculates how far the run is completed.
     * Currently only supports ESS but will hopefully will support other statistics in the future
     */
    public double calculateProgress(double[] currentESSScores) {
        double minESS = currentESSScores[0];
        for(int i=1; i<currentESSScores.length; i++) {
            if(currentESSScores[i]<minESS) {
                minESS = currentESSScores[i];
            }
        }
        return minESS/essLowerLimitBoundary;
    }

    /**
     * Alternative parameter input
     * @param essValues
     * @return
     */
    public double calculateProgress(ConvergeStat[] essValues) {
        double[] essScores = new double[essValues.length];
        for(int i=0; i<essValues.length; i++) {
            essScores[i] = essValues[i].getStat(null);
        }
        return calculateProgress(essScores);

    }
	
//	public void reportProgress(){
//        printProgress(calculateProgress(currentESSScores, essLowerLimitBoundary));
//
//	}

    public void printProgress(double p){
        int percentage = Math.round(((float) p));
		System.out.println(percentage + "% complete");

	}

	
	
	
}
