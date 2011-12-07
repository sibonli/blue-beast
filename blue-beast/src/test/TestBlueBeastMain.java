package test;

import bb.main.BlueBeastMain;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Tests the file input/output functionals of BLUE-BEAST
 * @author Wai Lok Sibon Li
 */
public class TestBlueBeastMain extends TestCase {

    private String inputFileLocation;

    public void setUp() {
//        try {
//            inputFileLocation = "";
//            PrintWriter inputFile = new PrintWriter();
//
//
//            inputFile.close();
//        } catch (IOException ioe) {
//            System.err.println("Error writing an input file for test");
//            ioe.printStackTrace();
//        }

        String[] args = {"one", "two", "three"};
        BlueBeastMain.main(args);
    }

    public void tearDown() {

    }

    public void testInput() {

    }

    public void testOutput() {

    }
}
