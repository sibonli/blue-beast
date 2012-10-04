/**
 *  *  BLUE-BEAST - Bayesian and Likelihood-based methods Usability Extension
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
 */

package bb.report;

import bb.mcmc.analysis.ConvergeStat;
import bb.mcmc.analysis.ESSConvergeStat;

import java.util.ArrayList;

public class ProgressReporter {

    private int essLowerLimitBoundary;
    private int essIndex;
    private ArrayList<ConvergeStat> convergenceStats;
    private ConvergenceProgress[] convergenceProgress;

	public ProgressReporter(ArrayList<ConvergeStat> convergenceStats) {

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
        String[] variableNames = convergenceStats.get(essIndex).getTestVariableNames();

        if(convergenceProgress==null) {
            convergenceProgress = new ConvergenceProgress[convergenceStats.get(essIndex).getAllStat().length];
            for(int i=0; i<convergenceProgress.length; i++) {
                convergenceProgress[i] = new ConvergenceProgress(variableNames[i], -1.0, new ESSConvergeStat());
            }
        }


        double minESS = Double.MAX_VALUE;//currentESSScores[0]/essLowerLimitBoundary;
        for(int i=0; i<currentESSScores.length; i++) {
            convergenceProgress[i].setProgress(currentESSScores[i]/ ((ESSConvergeStat) convergenceStats.get(essIndex)).getESSLowerLimitBoundary());//essLowerLimitBoundary);
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
        return convergenceProgress[i];
    }

    public ConvergenceProgress getConvergenceProgress(String variableName) {
        search: for(int i=0; i<convergenceProgress.length; i++) {
            if(convergenceProgress[i].getName().equals(variableName)) {
                return convergenceProgress[i];
            }
        }
        return null;
    }

    public int getConvergenceProgressLength() {
        return convergenceProgress.length;
    }
	
	
}
