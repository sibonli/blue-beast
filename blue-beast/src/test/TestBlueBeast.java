package test;

import bb.main.BlueBeastMain;
import junit.framework.TestCase;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Tests the file input/output functionals of BLUE-BEAST
 * @author Wai Lok Sibon Li
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
