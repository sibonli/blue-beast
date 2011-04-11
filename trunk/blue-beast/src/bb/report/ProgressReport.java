/*
 * @author Wai Lok Sibon Li
 */

package bb.report;

public class ProgressReport {

    private double[] currentESSScores;
    int essLowerLimitBoundary;

	public ProgressReport(int essLowerLimitBoundary) {
        //this.currentESSScores = currentESSScores; /* These values should change according to changes in the original array */
        this.essLowerLimitBoundary = essLowerLimitBoundary;
		System.out.println("Note: Progress report is only a rough estimate");
	}
	
	public double getSatas(){
		return 0;
		
	}

    public double calculateProgress(double[] currentESSScores) {
        double minESS = currentESSScores[0];
        for(int i=1; i<currentESSScores.length; i++) {
            if(currentESSScores[i]<minESS) {
                minESS = currentESSScores[i];
            }
        }
        return minESS/ESSLowerLimitBoundary;
    }
	
	public void reportProgress(){
        printProgress(calculateProgress(currentESSScores, essLowerLimitBoundary));
		
	}
    public void printProgress(double p){
        int percentage = Math.round(((float) p));
		System.out.println(percentage + "% complete");

	}

	
	
	
}
