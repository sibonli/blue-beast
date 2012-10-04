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

import bb.mcmc.analysis.*;
import bb.report.ProgressReporter;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Tests for BLUE-BEAST
 */
public class TestConvergenceProgress extends TestCase {

    ESSConvergeStat essConvergeStat;
    GelmanConvergeStat gelmanConvergeStat;
    AbstractConvergeStat gewekeConvergeStat;
    HeidelbergConvergeStat heidelbergConvergeStat;
    RafteryConvergeStat rafteryConvergeStat;

    String[] variableNames;


    public void setUp() {
        String[] variableNames = {"bob", "rules", "the", "quake", "3", "world"};
        this.variableNames = variableNames;

        HashMap<String, ArrayList<Double>> traceInfo = new HashMap<String, ArrayList<Double>>();
        addTraceInfoData(traceInfo);
        System.out.println("size: " + traceInfo.size() + "\t" + traceInfo.get("bob").size() + "\t" + traceInfo.get("bob").get(0));

        HashMap<String, double[]> traceValue = ConvergeStatUtils.traceInfoToArrays(traceInfo, 0);
        essConvergeStat = new ESSConvergeStat(variableNames, 0, 100);
        essConvergeStat.updateValues(traceValue);

        // TODO set these stats up properly along with the corresponding methods
        gelmanConvergeStat = new GelmanConvergeStat();
        gelmanConvergeStat.updateValues(traceValue);

        gewekeConvergeStat = new GewekeConvergeStat();
        gewekeConvergeStat.updateValues(traceValue);

        heidelbergConvergeStat = new HeidelbergConvergeStat();
        heidelbergConvergeStat.updateValues(traceValue);

        rafteryConvergeStat = new RafteryConvergeStat();
        rafteryConvergeStat.updateValues(traceValue);
    }

    //TODO move this to ConvergeStatTest.java later
    public void testGetVariableNames() {
        String[] vn = essConvergeStat.getTestVariableNames();
        for(int i=0; i<variableNames.length; i++) {
            assertEquals(variableNames[i], vn[i]);
        }
    }
    //TODO move this to ConvergeStatTest.java later
    public void testGetVariable() {
        double[] allStats = essConvergeStat.getAllStat();
        for(int i=0; i<variableNames.length; i++) {
            double stat = essConvergeStat.getStat(variableNames[i]);
            assertEquals(stat, allStats[i]);
        }
    }

    public void testProgressESSOnly() {
        ArrayList<ConvergeStat> convergenceStats = new ArrayList<ConvergeStat>(1);
        convergenceStats.add(essConvergeStat);
        ProgressReporter pr = new ProgressReporter(convergenceStats);
        System.out.println("movement " + pr.calculateProgress() + "\t" + essConvergeStat.getESSLowerLimitBoundary());
        assertEquals(0.07299, pr.calculateProgress(), 1e-10);
    }

    public void testProgressGelman() {
        ArrayList<ConvergeStat> convergenceStats = new ArrayList<ConvergeStat>(1);
        // Stats to add here

        ProgressReporter pr = new ProgressReporter(convergenceStats);
        //assertEquals(pr.calculateProgress(), 1.0, 1e-10);
    }

    public void testProgressGeweke() {
        ArrayList<ConvergeStat> convergenceStats = new ArrayList<ConvergeStat>(1);
        // Stats to add here

        ProgressReporter pr = new ProgressReporter(convergenceStats);
        //assertEquals(pr.calculateProgress(), 1.0, 1e-10);
    }

    public void testProgressHeidelberg() {
        ArrayList<ConvergeStat> convergenceStats = new ArrayList<ConvergeStat>(1);
        // Stats to add here

        ProgressReporter pr = new ProgressReporter(convergenceStats);
        //assertEquals(pr.calculateProgress(), 1.0, 1e-10);
    }

    public void testProgressRaftery() {
        ArrayList<ConvergeStat> convergenceStats = new ArrayList<ConvergeStat>(1);
        // Stats to add here

        ProgressReporter pr = new ProgressReporter(convergenceStats);
        //assertEquals(pr.calculateProgress(), 1.0, 1e-10);
    }

    /* ESS and Gelman */
    public void testProgressCombination1() {
        ArrayList<ConvergeStat> convergenceStats = new ArrayList<ConvergeStat>(2);
        // Stats to add here

        ProgressReporter pr = new ProgressReporter(convergenceStats);
        //assertEquals(pr.calculateProgress(), 1.0, 1e-10);
    }

    /* Geweke, Gelman and Raftery
     * Add more later
     */
    public void testProgressCombination2() {
        ArrayList<ConvergeStat> convergenceStats = new ArrayList<ConvergeStat>(3);
        // Stats to add here

        ProgressReporter pr = new ProgressReporter(convergenceStats);
        //assertEquals(pr.calculateProgress(), 1.0, 1e-10);
    }

    public void testProgressAll() {
        ArrayList<ConvergeStat> convergenceStats = new ArrayList<ConvergeStat>();
        // Stats to add here

        ProgressReporter pr = new ProgressReporter(convergenceStats);
        //assertEquals(pr.calculateProgress(), 1.0, 1e-10);

    }

    public void addTraceInfoData(HashMap<String, ArrayList<Double>> traceInfo) {
        ArrayList<Double[]> dArray = new ArrayList<Double[]>(6);
        /* Randomly generated numbers, log format below */
        Double[] bob = {0.69918, 0.47532, 0.97139, 0.92149, 0.55168, 0.52942, 0.41052, 0.39066, 0.24480, 0.11675, 0.44650, 0.71395, 0.27401, 0.15864, 0.57735, 0.47484, 0.91220, 0.35630, 0.04200, 0.17326};
        Double[] rules = {59.29762, 91.80184, 2.76338, 85.06132, 0.41212, 3.83888, 96.90474, 84.63864, 15.98229, 54.55198, 74.36847, 68.74247, 67.78117, 44.69331, 11.02412, 97.50843, 26.33062, 58.79940, 36.54200, 29.67487};
        Double[] the = {29.26350, 26.22820, 29.26179, 29.72408, 25.08671, 29.38350, 28.86587, 28.55601, 27.62340, 28.78492, 27.79723, 26.96269, 29.60455, 27.99917, 25.48177, 28.75819, 25.25725, 29.89180, 26.51568, 26.80717};
        Double[] quake = {8.24819, 6.86036, 13.35304, 17.28698, 1.48916, 13.32536, 12.52371, 17.06472, 12.65120, 13.30698, 1.30967, 16.18992, 1.19224, 14.92740, 5.70020, 2.22989, 0.02313, 9.32873, 6.85278, 6.23542};
        Double[] three = {270.41752, 446.29195, 144.24325, 466.83107, 188.41932, 3.33763, 418.47249, 476.98302, 115.25923, 158.38014, 499.25036, 205.96280, 83.36237, 484.78296, 368.34671, 64.00216, 305.82996, 471.47072, 13.74782, 360.69430};
        Double[] world = {0.36010, 0.04096, 0.03284, 0.65127, 0.93622, 0.30815, 0.36202, 0.86928, 0.40382, 0.88682, 0.01165, 0.19008, 0.66189, 0.31196, 0.12578, 0.93198, 0.80790, 0.60186, 0.24131, 0.46580};

        dArray.add(bob);
        dArray.add(rules);
        dArray.add(the);
        dArray.add(quake);
        dArray.add(three);
        dArray.add(world);

        for(int i=0; i<dArray.size(); i++) {
            traceInfo.put(variableNames[i], new ArrayList<Double>(Arrays.asList(dArray.get(i))));
        }


        //state	bob	rules	the	quake	3	world
        //0	0.69918	59.29762	29.26350	8.24819	270.41752	0.36010
        //1	0.47532	91.80184	26.22820	6.86036	446.29195	0.04096
        //2	0.97139	2.76338	29.26179	13.35304	144.24325	0.03284
        //3	0.92149	85.06132	29.72408	17.28698	466.83107	0.65127
        //4	0.55168	0.41212	25.08671	1.48916	188.41932	0.93622
        //5	0.52942	3.83888	29.38350	13.32536	3.33763	0.30815
        //6	0.41052	96.90474	28.86587	12.52371	418.47249	0.36202
        //7	0.39066	84.63864	28.55601	17.06472	476.98302	0.86928
        //8	0.24480	15.98229	27.62340	12.65120	115.25923	0.40382
        //9	0.11675	54.55198	28.78492	13.30698	158.38014	0.88682
        //10	0.44650	74.36847	27.79723	1.30967	499.25036	0.01165
        //11	0.71395	68.74247	26.96269	16.18992	205.96280	0.19008
        //12	0.27401	67.78117	29.60455	1.19224	83.36237	0.66189
        //13	0.15864	44.69331	27.99917	14.92740	484.78296	0.31196
        //14	0.57735	11.02412	25.48177	5.70020	368.34671	0.12578
        //15	0.47484	97.50843	28.75819	2.22989	64.00216	0.93198
        //16	0.91220	26.33062	25.25725	0.02313	305.82996	0.80790
        //17	0.35630	58.79940	29.89180	9.32873	471.47072	0.60186
        //18	0.04200	36.54200	26.51568	6.85278	13.74782	0.24131
        //19	0.17326	29.67487	26.80717	6.23542	360.69430	0.46580


    }

}
