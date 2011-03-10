/*
 * @author Steven Wu
 */

package bb.mcmc.analysis;

import java.io.File;
import java.io.IOException;

import dr.inference.trace.LogFileTraces;
import dr.inference.trace.TraceAnalysis;
import dr.inference.trace.TraceDistribution;
import dr.inference.trace.TraceException;
import dr.inference.trace.TraceList;
import dr.util.NumberFormatter;

public class CalculateESS implements ConvergenceStatistic {

	
	// TraceList report = TraceAnalysis.report("tutorial1.log");
	// return report(fileName, -1, null);
	//	return report(fileName, burnin, likelihoodName,false);
	public CalculateESS(String fileName, int inBurnin, String likelihoodName, boolean withStdError) throws IOException, TraceException{


        int fieldWidth = 14;
        int firstField = 25;
        NumberFormatter formatter = new NumberFormatter(6);
        formatter.setPadding(true);
        formatter.setFieldWidth(fieldWidth);

        File file = new File(fileName);

        LogFileTraces traces = new LogFileTraces(fileName, file);

        traces.loadTraces();

        int burnin = inBurnin;
        if (burnin == -1) {
            burnin = traces.getMaxState() / 10;
        }

        traces.setBurnIn(burnin);

        System.out.println();
        System.out.println("burnIn   <= " + burnin);
        System.out.println("maxState  = " + traces.getMaxState());
        System.out.println();

        System.out.print(formatter.formatToFieldWidth("statistic", firstField));
        String[] names;

        if (!withStdError)
            names = new String[]{"mean", "hpdLower", "hpdUpper", "ESS"};
        else
            names = new String[]{"mean", "stdErr", "hpdLower", "hpdUpper", "ESS"};

        for (String name : names) {
            System.out.print(formatter.formatToFieldWidth(name, fieldWidth));
        }
        System.out.println();

        int warning = 0;
        for (int i = 0; i < traces.getTraceCount(); i++) {

            traces.analyseTrace(i);
            TraceDistribution distribution = traces.getDistributionStatistics(i);

            double ess = distribution.getESS();
            

        }
	}

}
