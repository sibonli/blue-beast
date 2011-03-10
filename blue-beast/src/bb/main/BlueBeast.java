package bb.main;

import dr.inference.mcmc.MCMCOptions;
import dr.inference.operators.MCMCOperator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: sibon
 * Date: 3/10/11
 * Time: 10:21 AM
 * To change this template use File | Settings | File Templates.
 *
 * @author Wai Lok Sibon Li
 *
 */
public class BlueBeast {

    protected static MCMCOperator[] operators;
    protected static MCMCOptions mcmcOptions;
    protected FileReader logFile;

    /**
     * Use this constructor only if operators can be changed implicitly
     * Otherwise use the main method
     */
    public BlueBeast(MCMCOperator[] operators, MCMCOptions mcmcOptions, String logFileLocation) {
        this.operators = operators;
        this.mcmcOptions = mcmcOptions;
        try {
            File f = new File(logFileLocation);
            FileReader logFile = new FileReader(f);

        }catch (IOException e) {
            System.err.println("Input file location not valid");
            e.printStackTrace();
        }
    }

    public BlueBeast(MCMCOperator[] operators, MCMCOptions mcmcOptions) {
        this.operators = operators;
        this.mcmcOptions = mcmcOptions;
    }

    public BlueBeast() {

        System.out.println("BLUE BEAST is in use. Please cite " + BlueBeastMain.CITATION);
        System.out.println("Note: It is recommended that the convergence of MCMC chains are verified manually");

    }

    /**
     * Add log data to the existing object
     * i.e. this should be called whenever the BEAST log file is updated
     * and an argument of estimated values for each variable of interest should be provided
     */
    public void addLogData(String[] s, double[] d) {

    }
    /**
     * Add log data to the existing object
     * This method is an alternative for methods that have provided the log file name instead
     * and prompts BLUE BEAST to check the log file for updates
     */
    public void addLogData() {

    }
}
