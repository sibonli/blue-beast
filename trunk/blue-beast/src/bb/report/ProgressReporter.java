/*
 * @author Wai Lok Sibon Li
 */

package bb.report;

import bb.mcmc.analysis.ConvergeStat;
import bb.mcmc.analysis.ESSConvergeStat;

import java.util.ArrayList;

public class ProgressReporter {

    //private double[] currentESSScores;
    private int essLowerLimitBoundary;
    private int essIndex;
    private ArrayList<ConvergeStat> convergenceStats;
    private ConvergenceProgress[] convergenceProgress;
    //double[] progresses;

	public ProgressReporter(int essLowerLimitBoundary, ArrayList<ConvergeStat> convergenceStats) {//) {
        //this.currentESSScores = currentESSScores; /* These values should change according to changes in the original array */

        this.convergenceStats = convergenceStats;



        int index=0, i=0;
        for(ConvergeStat cs : convergenceStats) {
            if(cs.getClass().equals(ESSConvergeStat.class)) {
                index = i;
            }

            i++;
        }
        essIndex = index;
        this.essLowerLimitBoundary = essLowerLimitBoundary;
		System.out.println("Note: Progress report is only a rough estimate");
	}

    /**
     * Calculates how far the run is completed.
     * Currently only supports ESS but will hopefully will support other statistics in the future
     */
    public double calculateProgress() {
        double[] currentESSScores = convergenceStats.get(essIndex).getAllStat();
        String[] variableNames = convergenceStats.get(essIndex).getVariableNames();



        if(convergenceProgress==null) {
            //System.out.println("hohoho");
            convergenceProgress = new ConvergenceProgress[convergenceStats.get(essIndex).getAllStat().length];
            for(int i=0; i<convergenceProgress.length; i++) {
                convergenceProgress[i] = new ConvergenceProgress(variableNames[i], -1.0, new ESSConvergeStat());
                //System.out.println("bobobo");
            }
        }
        //convergenceProgress = new ConvergenceProgress[variableNames.length];

        //System.out.println("dododo " + currentESSScores.length + "\t" + variableNames.length + "\t" + convergenceProgress.length);

        double minESS = Double.MAX_VALUE;//currentESSScores[0]/essLowerLimitBoundary;
        for(int i=0; i<currentESSScores.length; i++) {
//            if(convergenceProgress[i]==null) {
//                System.out.println("rororor");
//            }
            convergenceProgress[i].setProgress(currentESSScores[i]/essLowerLimitBoundary);
            if(convergenceProgress[i].getProgress()<minESS) {
                minESS = convergenceProgress[i].getProgress();
            }
        }
        return minESS;
    }


    public void printProgress(double p){
        int percentage = Math.round(((float) p) * 100);
		System.out.println(percentage + "% complete");

	}


    public ConvergenceProgress getConvergenceProgress(int i) {
//        if(i>=convergenceProgress.length) {
//            throw new RuntimeException("Array index out of bounds for convergenceProgress");
//        }
        return convergenceProgress[i];
    }

    public int getConvergenceProgressLength() {
        return convergenceProgress.length;
    }
	
	
}
