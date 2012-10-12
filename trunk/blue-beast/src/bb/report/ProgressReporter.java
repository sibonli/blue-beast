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
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.StatUtils;

public class ProgressReporter {

	/*
	 * TODO: Implemented in the most simple way which only take progress from ESSConvergeStat
	 * Any addition ConvergeStat will fail and wont work at all.
	 * Need to redo  ConvergenceProgress[], so it handles multiple ConvergeStat 	
	*/
    private int essIndex;
    private ConvergenceProgress[] convergenceProgress;

	public ProgressReporter(ArrayList<ConvergeStat> convergenceStats) {

        int index=0; 
        for(ConvergeStat cs : convergenceStats) {
            if(cs.getClass().equals(ESSConvergeStat.class)) {
            	essIndex = index;

            }

            index++;
        }
        


		System.out.println("Note: Progress report is only a rough estimate");
	}


    public void printProgress(double p){
        int percentage = Math.round(((float) p) * 100);
		System.out.println(percentage + "% complete");

	}


    public ConvergenceProgress getConvergenceProgress(int i) {
        return convergenceProgress[i];
    }

    public ConvergenceProgress getConvergenceProgress(String variableName) {
//        convergenceProgress;
        for (ConvergenceProgress cp : convergenceProgress) {
        	if(cp.isNameEquals(variableName)) {
                return cp;
            }
        }
		return null;
    }

    public int getConvergenceProgressLength() {
        return convergenceProgress.length;
    }


	public double getProgress(ArrayList<ConvergeStat> convergenceStats) {
		double progress = calculateOverallProgress(convergenceStats);
		return progress;
	}


	/**
	 * Calculates how far the run is completed.
	 * Currently only supports ESS but will hopefully will support other statistics in the future
	 * @param convergenceStats 
	 */
	private double calculateOverallProgress(ArrayList<ConvergeStat> convergenceStats) {
		//TODO: getProgress() is not in interface/abstract yet, because it's only apply to ESS at this stage. Requires casting

		String[] variableNames = convergenceStats.get(essIndex).getTestVariableNames();
		if (convergenceProgress == null) {
			convergenceProgress = new ConvergenceProgress[variableNames.length];
			for (int i = 0; i < convergenceProgress.length; i++) {
				convergenceProgress[i] = new ConvergenceProgress(
						variableNames[i], -1.0, ESSConvergeStat.STATISTIC_NAME);
			}
		}
	
		double[] currentESSSProgress = ((ESSConvergeStat) convergenceStats.get(essIndex)).getProgress();
	    System.out.println(Arrays.toString(currentESSSProgress));
	    double minESS = StatUtils.min(currentESSSProgress);
	    for(int i=0; i<currentESSSProgress.length; i++) {
	        convergenceProgress[i].setProgress(currentESSSProgress[i]);
//	        if(convergenceProgress[i].getProgress()<minESS) {
//	            minESS = convergenceProgress[i].getProgress();
//	        }
	    }
	    return minESS;
	}
	
	
}
