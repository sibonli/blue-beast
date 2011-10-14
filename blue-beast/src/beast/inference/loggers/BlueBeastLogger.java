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

package beast.inference.loggers;

import dr.inference.loggers.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.*;

import dr.inference.loggers.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A class for a general purpose logger.
 *
 * @author Andrew Rambaut
 * @author Alexei Drummond
 * @version $Id: MCLogger.java,v 1.18 2005/05/24 20:25:59 rambaut Exp $
 */
public class BlueBeastLogger implements Logger {

    /**
     * Output performance stats in this log
     */
    private final boolean performanceReport;
    private final int performanceReportDelay;

    /**
     * Constructor. Will log every logEvery.
     *
     * @param formatter the formatter of this logger
     * @param logEvery  logging frequency
     */
//    public BlueBeastLogger(LogFormatter formatter, int logEvery, int initialLogInterval, boolean performanceReport, int performanceReportDelay) {
    public BlueBeastLogger(LogFormatter formatter, int logEvery, boolean performanceReport, int performanceReportDelay) {

        //addFormatter(formatter);
        this.logEvery = logEvery;
//        this.initialLogInterval = initialLogInterval;
        this.performanceReport = performanceReport;
        this.performanceReportDelay = performanceReportDelay;
        variableNames = new ArrayList<String>();
    }

    /**
     * Constructor. Will log every logEvery.
     *
     * @param formatter the formatter of this logger
     * @param logEvery  logging frequency
     */
//    public BlueBeastLogger(LogFormatter formatter, int logEvery, int initialLogInterval, boolean performanceReport) {
//        this(formatter, logEvery, initialLogInterval, performanceReport, 0);
    public BlueBeastLogger(LogFormatter formatter, int logEvery, boolean performanceReport) {
        this(formatter, logEvery, performanceReport, 0);
    }

    /**
     * Constructor. Will log every logEvery.
     *
     * @param logEvery logging frequency
     */
//    public BlueBeastLogger(String fileName, int logEvery, int initialLogInterval, boolean performanceReport, int performanceReportDelay) throws IOException {
//        this(new TabDelimitedFormatter(new PrintWriter(new FileWriter(fileName))), logEvery, initialLogInterval, performanceReport, performanceReportDelay);
    public BlueBeastLogger(String fileName, int logEvery, boolean performanceReport, int performanceReportDelay) throws IOException {
        this(new TabDelimitedFormatter(new PrintWriter(new FileWriter(fileName))), logEvery, performanceReport, performanceReportDelay);
    }

    /**
     * Constructor. Will log every logEvery.
     *
     * @param logEvery logging frequency
     */
    //public BlueBeastLogger(int logEvery, int initialLogInterval) {
    public BlueBeastLogger(int logEvery) {
        //this(new TabDelimitedFormatter(System.out), logEvery, initialLogInterval, true, 0); // 10000 is just an arbitrary default for now
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

    public final void add(Loggable loggable) {
        System.out.println("Begin logging object " + loggable.getClass().toString());
        //variableNames.add(loggable.getClass().toString());

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

//    // TOD remove this as soon as formatter removed (mid)
//    protected void logHeading(String heading) {
//        for (LogFormatter formatter : formatters) {
//            formatter.logHeading(heading);
//        }
//    }
//
//    // TOD remove this as soon as formatter removed  (mid)
//    protected void logLine(String line) {
//        for (LogFormatter formatter : formatters) {
//            formatter.logLine(line);
//        }
//    }
//
//    // TOD remove this as soon as formatter removed  (mid)
//    protected void logLabels(String[] labels) {
//        for (LogFormatter formatter : formatters) {
//            formatter.logLabels(labels);
//        }
//    }
//
//    protected void logValues(String[] values) {
//        // TOD remove this as soon as formatter removed  (mid)
//        for (LogFormatter formatter : formatters) {
//            formatter.logValues(values);
//        }
//    }

    public void startLogging() {

        traceInfo = new Hashtable<String, ArrayList<Double>>();
        if (logEvery > 0) {

            traceInfo.put("state", new ArrayList<Double>());

            final int columnCount = getColumnCount();
            //String[] labels = new String[columnCount + 1];

            //labels[0] = "state";

            for (int i = 0; i < columnCount; i++) {
                //labels[i + 1] = getColumnLabel(i);
                traceInfo.put(getColumnLabel(i), new ArrayList<Double>());
            }

            //logLabels(labels);
        }

//        // TOD remove all of this as soon as formatter removed (mid)
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

    public void log(int state) {

//        if (performanceReport && !performanceReportStarted && state >= performanceReportDelay) {
//            startTime = System.currentTimeMillis();
//            startState = state;
//            formatter.setMaximumFractionDigits(2);
//        }


        if (logEvery > 0 && (state % logEvery == 0)) {
            System.out.println("Logging data to BLUE BEAST... ( " + state + ")");
            final int columnCount = getColumnCount();

//            String[] values = new String[columnCount + (performanceReport ? 2 : 1)];
            String[] values = new String[columnCount + 2];

            values[0] = Integer.toString(state);
            ArrayList<Double> states = traceInfo.get("state");
            states.add((double) state);
            traceInfo.put("state", states);       // toto This may be redundant

            for (int i = 0; i < columnCount; i++) {

                // TODO how do we handle non-numeric variables? Can't treat them as doubles (mid)
                values[i + 1] = getColumnFormatted(i);
                ArrayList<Double> columnValues = traceInfo.get(getColumnLabel(i));
                columnValues.add(Double.parseDouble(getColumnFormatted(i)));
                traceInfo.put(getColumnLabel(i), columnValues); // todo This may be redundant. Check (mid)
            }

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
//        // TOD remove this as soon as formatter removed (mid)
//        for (LogFormatter formatter : formatters) {
//            formatter.stopLogging();
//        }
    }

    private String title = null;

    private ArrayList<LogColumn> columns = new ArrayList<LogColumn>();

    protected int logEvery = 0;
//    protected int initialLogInterval = 0;

//    // TOD remove this as soon as formatter removed (mid)
//    public List<LogFormatter> getFormatters() {
//        return formatters;
//    }
//
//    // TOD remove this as soon as formatter removed (mid)
//    public void setFormatters(List<LogFormatter> formatters) {
//        this.formatters = formatters;
//    }

    public ArrayList<String> getVariableNames() {
        return variableNames;
    }

    public Hashtable<String, ArrayList<Double>> getTraceInfo() {
        return traceInfo;
    }


    protected List<LogFormatter> formatters = new ArrayList<LogFormatter>();

    private boolean performanceReportStarted = false;
    private long startTime;
    private int startState;

    private final NumberFormat formatter = NumberFormat.getNumberInstance();


    private ArrayList<String> variableNames;
    protected Hashtable<String, ArrayList<Double>> traceInfo;

}
