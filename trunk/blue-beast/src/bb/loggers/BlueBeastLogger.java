/*
 * MCLogger.java
 *
 * Copyright (C) 2002-2009 Alexei Drummond and Andrew Rambaut
 *
 * This file is part of BEAST.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership and licensing.
 *
 * BEAST is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * BEAST is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with BEAST; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */

package bb.loggers;

import dr.inference.loggers.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A class for a general purpose logger.
 *
 * @author Andrew Rambaut
 * @author Alexei Drummond
 * @author Wai Lok Sibon Li
 *
 * @version $Id: BlueBeastLogger.java,v 1.18 2005/05/24 20:25:59 rambaut Exp $
 */
public class BlueBeastLogger implements Logger {

    /**
     * Output performance stats in this log
     */
    private final boolean performanceReport;
    private final int performanceReportDelay;

    public final String STATE_TAG = "state";
    private int sampleCount;

    /**
     * Constructor. Will log every logEvery.
     *
     * @param formatter the formatter of this logger
     * @param logEvery  logging frequency
     */
    public BlueBeastLogger(LogFormatter formatter, int logEvery, boolean performanceReport, int performanceReportDelay) {

        //addFormatter(formatter);
        this.logEvery = logEvery;
//        this.initialLogInterval = initialLogInterval;
        this.performanceReport = performanceReport;
        this.performanceReportDelay = performanceReportDelay;
        variableNames = new ArrayList<String>();
        sampleCount = 0;
    }

    /**
     * Constructor. Will log every logEvery.
     *
     * @param formatter the formatter of this logger
     * @param logEvery  logging frequency
     */
    public BlueBeastLogger(LogFormatter formatter, int logEvery, boolean performanceReport) {
        this(formatter, logEvery, performanceReport, 0);
    }

    /**
     * Constructor. Will log every logEvery.
     *
     * @param logEvery logging frequency
     */
    public BlueBeastLogger(String fileName, int logEvery, boolean performanceReport, int performanceReportDelay) throws IOException {
        this(new TabDelimitedFormatter(new PrintWriter(new FileWriter(fileName))), logEvery, performanceReport, performanceReportDelay);
    }

    /**
     * Constructor. Will log every logEvery.
     *
     * @param logEvery logging frequency
     */
    public BlueBeastLogger(int logEvery) {
        this(new TabDelimitedFormatter(System.out), logEvery, true, 0); // 10000 is just an arbitrary default for now
    }

    public final void setTitle(String title) {
        this.title = title;
    }

    public final String getTitle() {
        return title;
    }

    public int getLogEvery() {
        return logEvery;
    }

    public void setLogEvery(int logEvery) {
        this.logEvery = logEvery;
    }

    public final void add(Loggable loggable) {
        System.out.println("Begin logging object " + loggable.getClass().toString());

        LogColumn[] columns = loggable.getColumns();

        for (LogColumn column : columns) {
            if(column instanceof NumberColumn) {
                addColumn(column);
                variableNames.add(column.getLabel());
            }
            else {
                // TODO convergence assessment of non-numerical variables? (long)
                throw new RuntimeException("Column " + column.getLabel() + "not added. Variable type is currently not accepted for convergence assessment");
            }
            System.out.println("with column label \"" + column.getLabel() + "\"");
        }
    }


    public final void addColumn(LogColumn column) {
        columns.add(column);
        //throw new RuntimeException("BlueBeast does not support input of columns. Please directly provide the Loggable objects themselves");
    }

    public final void addColumns(LogColumn[] columns) {
//        for (LogColumn column : columns) {
//            addColumn(column);
//        }
        throw new RuntimeException("BlueBeast does not support input of columns. Please directly provide the Loggable objects themselves");
    }

    public final int getColumnCount() {
        return columns.size();
    }

    public final LogColumn getColumn(int index) {

        return columns.get(index);
    }

    public final String getColumnLabel(int index) {

        return columns.get(index).getLabel();
    }

    public final String getColumnFormatted(int index) {

        return columns.get(index).getFormatted();
    }

//    public int getInitialLogInterval() {
//        return initialLogInterval;
//    }
//
//    public void setInitialLogInterval(int initialLogInterval) {
//        this.initialLogInterval = initialLogInterval;
//    }

//    //TOD remove this method, constructor and also from BlueBeastLoggerParser method call
//    public final void addFormatter(LogFormatter formatter) {
//
//        formatters.add(formatter);
//    }
//    // TOD remove this as soon as formatter removed
//    protected void logHeading(String heading) {
//        for (LogFormatter formatter : formatters) {
//            formatter.logHeading(heading);
//        }
//    }
//
//    // TOD remove this as soon as formatter removed
//    protected void logLine(String line) {
//        for (LogFormatter formatter : formatters) {
//            formatter.logLine(line);
//        }
//    }
//
//    // TOD remove this as soon as formatter removed
//    protected void logLabels(String[] labels) {
//        for (LogFormatter formatter : formatters) {
//            formatter.logLabels(labels);
//        }
//    }
//
//    protected void logValues(String[] values) {
//        // TOD remove this as soon as formatter removed
//        for (LogFormatter formatter : formatters) {
//            formatter.logValues(values);
//        }
//    }

    public void startLogging() {

        traceInfo = new HashMap<String, ArrayList<Double>>();
        if (logEvery > 0) {

            traceInfo.put(STATE_TAG, new ArrayList<Double>());

            final int columnCount = getColumnCount();
            //String[] labels = new String[columnCount + 1];

            //labels[0] = "state";

            for (int i = 0; i < columnCount; i++) {
                //labels[i + 1] = getColumnLabel(i);
                traceInfo.put(getColumnLabel(i), new ArrayList<Double>());
            }

            //logLabels(labels);
        }

//        // TOD remove all of this as soon as formatter removed
//        for (LogFormatter formatter : formatters) {
//            formatter.startLogging(title);
//        }
//
//        if (title != null) {
//            logHeading(title);
//        }
//
//        if (logEvery > 0) {
//            final int columnCount = getColumnCount();
//            String[] labels = new String[columnCount + 1];
//
//            labels[0] = "state";
//
//            for (int i = 0; i < columnCount; i++) {
//                labels[i + 1] = getColumnLabel(i);
//            }
//
//            logLabels(labels);
//        }
    }

    public void log(long state) {

//        if (performanceReport && !performanceReportStarted && state >= performanceReportDelay) {
//            startTime = System.currentTimeMillis();
//            startState = state;
//            formatter.setMaximumFractionDigits(2);
//        }


        if (logEvery > 0 && (state % logEvery == 0)) {
            //System.out.println("Logging data to BLUE BEAST... ( " + state + ")");
            final int columnCount = getColumnCount();

//            String[] values = new String[columnCount + (performanceReport ? 2 : 1)];
            String[] values = new String[columnCount + 2];

            values[0] = Long.toString(state);
            //ArrayList<Double> states = traceInfo.get(STATE_TAG);
            traceInfo.get(STATE_TAG).add((double) state);

            for (int i = 0; i < columnCount; i++) {

                // TODO see above, same issue (long)
                values[i + 1] = getColumnFormatted(i);
                ArrayList<Double> columnValues = traceInfo.get(getColumnLabel(i));
                columnValues.add(Double.parseDouble(getColumnFormatted(i)));
//                double d = Double.parseDouble(getColumnFormatted(i));
//                if(Double.isNaN(d)) {
//                    throw new RuntimeException("Numerical error: NaN values were logged. ");
//                }
//                else if(Double.isInfinite(d)) {
//                    throw new RuntimeException("Numerical error: Infinite values were logged. ");
//                }
//                columnValues.add(d);
                //traceInfo.put(getColumnLabel(i), columnValues);
            }
            sampleCount++;

//            if (performanceReport) {
//                if (performanceReportStarted) {
//
//                    long time = System.currentTimeMillis();
//
//                    double hoursPerMillionStates = (double) (time - startTime) / (3.6 * (double) (state - startState));
//
//                    String hpm = formatter.format(hoursPerMillionStates);
//                    if( hpm.equals("0") ) {
//                        // test cases can run fast :)
//                        hpm = formatter.format(1000 * hoursPerMillionStates);
//                        values[columnCount + 1] = hpm + " hours/billion states";
//                    } else {
//                        values[columnCount + 1] = hpm + " hours/million states";
//                    }
//
//                } else {
//                    values[columnCount + 1] = "-";
//                }
//            }

            //logValues(values);
        }

//        if (performanceReport && !performanceReportStarted && state >= performanceReportDelay) {
//            performanceReportStarted = true;
//        }

    }

    public void stopLogging() {
//        // TOD remove this as soon as formatter removed
//        for (LogFormatter formatter : formatters) {
//            formatter.stopLogging();
//        }
    }

    private String title = null;

    private ArrayList<LogColumn> columns = new ArrayList<LogColumn>();

    protected int logEvery = 0;
//    protected int initialLogInterval = 0;

//    // TOD remove this as soon as formatter removed
//    public List<LogFormatter> getFormatters() {
//        return formatters;
//    }
//
//    // TOD remove this as soon as formatter removed
//    public void setFormatters(List<LogFormatter> formatters) {
//        this.formatters = formatters;
//    }

    public ArrayList<String> getVariableNames() {
        return variableNames;
    }

    public HashMap<String, ArrayList<Double>> getTraceInfo() {
        return traceInfo;
    }


    protected List<LogFormatter> formatters = new ArrayList<LogFormatter>();

    private boolean performanceReportStarted = false;
    private long startTime;
    private int startState;

    private final NumberFormat formatter = NumberFormat.getNumberInstance();


    private ArrayList<String> variableNames;
    protected HashMap<String, ArrayList<Double>> traceInfo;

	public void addVariableName(ArrayList<String> variableNames) {
		this.variableNames = variableNames;
		
	}

    public int getSampleCount() {
        return sampleCount;
    }
}
