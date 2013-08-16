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
import bb.report.ReportUtils;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Tests the file input/output functionals of BLUE-BEAST
 */
public class TestBlueBeast {



    @Before
    public void setUp() {


    }

    @After
    public void tearDown() {

    }

    @Test
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

    @Test
    public void testThinLog() {
        HashMap<String, ArrayList<Double>> testLog = new HashMap<String, ArrayList<Double>>();
        Double[] statesArray = {0.0, 1000.0, 2000.0, 3000.0, 4000.0, 5000.0, 6000.0, 7000.0, 8000.0, 9000.0};
        Double[] testVariable1Array = {-0.8, -0.7, -0.6, -0.5, -0.4, -0.3, -0.2, -0.1, -0.0, 0.1};
        Double[] testVariable2Array = {324.0, 103200.213410, 432100.6430, -30.0, 4430.00001, -5.0, 632231.0, -632231.0, 8320.0, 9000.0};
        ArrayList<Double> states = new ArrayList<Double>(Arrays.asList(statesArray));
        ArrayList<Double> testVariable1 = new ArrayList<Double>(Arrays.asList(testVariable1Array));
        ArrayList<Double> testVariable2 = new ArrayList<Double>(Arrays.asList(testVariable2Array));
        testLog.put("state", states);
        testLog.put("testVariable1", testVariable1);
        testLog.put("testVariable2", testVariable2);
        ReportUtils.thinTraceInfo(testLog, 1);
        assertEquals(10, testLog.values().iterator().next().size());
        ReportUtils.thinTraceInfo(testLog, 3); // 0, 3, 6, 9
        System.out.println(testLog.values().iterator().next().size());
        assertEquals(4, testLog.values().iterator().next().size());
        assertEquals(statesArray[0], testLog.get("state").get(0));
        assertEquals(statesArray[3], testLog.get("state").get(1));
        assertEquals(statesArray[6], testLog.get("state").get(2));
        assertEquals(statesArray[9], testLog.get("state").get(3));
        assertEquals(testVariable1Array[0], testLog.get("testVariable1").get(0));
        assertEquals(testVariable1Array[3], testLog.get("testVariable1").get(1));
        assertEquals(testVariable1Array[6], testLog.get("testVariable1").get(2));
        assertEquals(testVariable1Array[9], testLog.get("testVariable1").get(3));
        assertEquals(testVariable2Array[0], testLog.get("testVariable2").get(0));
        assertEquals(testVariable2Array[3], testLog.get("testVariable2").get(1));
        assertEquals(testVariable2Array[6], testLog.get("testVariable2").get(2));
        assertEquals(testVariable2Array[9], testLog.get("testVariable2").get(3));
    }
}
