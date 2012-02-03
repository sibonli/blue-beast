/**
 *  BLUE-BEAST - Bayesian and Likelihood-based methods Usability Extension
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


 * @author Wai Lok Sibon Li
 *
 */

package test;

import bb.main.BlueBeastMain;
import junit.framework.TestCase;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Tests the file input/output functionals of BLUE-BEAST
 */
public class TestBlueBeast extends TestCase {



    public void setUp() {


    }

    public void tearDown() {

    }

    public void testInputOutput() {
        final String inputFileLocation = "tempInputFile.txt";
        final String outputFileLocation = "outputFile.txt";

        try {



            PrintWriter inputFile = new PrintWriter(new BufferedWriter(new FileWriter(inputFileLocation)), true);
            inputFile.println();

            inputFile.close();
        } catch (IOException ioe) {
            System.err.println("Error writing an input file for test");
            ioe.printStackTrace();
        }

        String[] args = {"-currentChainLength 1000", "-essLowerLimitBoundary 100", "-dynamicCheckingInterval",
                "-autoOptimiseWeights", "-optimiseChainLength", "-maxChainLength 10000", "-initialCheckInterval 10000",
                "-burninPercentage 0.10", "-convergenceStatsToUse ESS ", outputFileLocation};
        BlueBeastMain.main(args);
    }
}
