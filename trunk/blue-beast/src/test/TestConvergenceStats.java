package test;

import bb.mcmc.analysis.*;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Tests the file input/output functionals of BLUE-BEAST
 * @author Wai Lok Sibon Li
 */
public class TestConvergenceStats extends TestCase {
    Hashtable<String, ArrayList<Double>> dataSet1;
    Hashtable<String, ArrayList<Double>> dataSet2;
    Hashtable<String, ArrayList<Double>> dataSet3;
    String[] variableNames;


    public void setUp() {
        dataSet1 = new Hashtable<String, ArrayList<Double>>();
        dataSet2 = new Hashtable<String, ArrayList<Double>>();
        dataSet3 = new Hashtable<String, ArrayList<Double>>();
        String[] s = {"one", "two", "three"};
        variableNames = s;

    }

    public void testThomasLibrary() {
        Hashtable<Double, Double> resultsFromR = new Hashtable<Double, Double>();
    }

    public void testESS() {
        ESSConvergeStat stat = new ESSConvergeStat();
        stat.updateTrace(dataSet1);
    }

    public void testGelman() {
        GelmanConvergeStat stat = new GelmanConvergeStat();
    }

    public void testGeweke() {
        GewekeConvergeStat stat = new GewekeConvergeStat();
    }

    public void testHeidelberg() {
        HeidelbergConvergeStat stat = new HeidelbergConvergeStat();
    }

    public void testRaftery() {
        RafteryConvergeStat stat = new RafteryConvergeStat();
    }


}
