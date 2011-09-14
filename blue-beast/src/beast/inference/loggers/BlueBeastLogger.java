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
import java.util.ArrayList;
import java.util.List;
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
    public BlueBeastLogger(LogFormatter formatter, int logEvery, int initialLogInterval, boolean performanceReport, int performanceReportDelay) {

        addFormatter(formatter);
        this.logEvery = logEvery;
        this.initialLogInterval = initialLogInterval;
        this.performanceReport = performanceReport;
        this.performanceReportDelay = performanceReportDelay;
    }

    /**
     * Constructor. Will log every logEvery.
     *
     * @param formatter the formatter of this logger
     * @param logEvery  logging frequency
     */
    public BlueBeastLogger(LogFormatter formatter, int logEvery, int initialLogInterval, boolean performanceReport) {
        this(formatter, logEvery, initialLogInterval, performanceReport, 0);
    }

    /**
     * Constructor. Will log every logEvery.
     *
     * @param logEvery logging frequency
     */
    public BlueBeastLogger(String fileName, int logEvery, int initialLogInterval, boolean performanceReport, int performanceReportDelay) throws IOException {
        this(new TabDelimitedFormatter(new PrintWriter(new FileWriter(fileName))), logEvery, initialLogInterval, performanceReport, performanceReportDelay);
    }

    /**
     * Constructor. Will log every logEvery.
     *
     * @param logEvery logging frequency
     */
    public BlueBeastLogger(int logEvery, int initialLogInterval) {
        this(new TabDelimitedFormatter(System.out), logEvery, initialLogInterval, true, 0); // 10000 is just an arbitrary default for now
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

    public int getInitialLogInterval() {
        return initialLogInterval;
    }

    public void setInitialLogInterval(int initialLogInterval) {
        this.initialLogInterval = initialLogInterval;
    }

    public final void addFormatter(LogFormatter formatter) {

        formatters.add(formatter);
    }

    public final void add(Loggable loggable) {

        LogColumn[] columns = loggable.getColumns();

        for (LogColumn column : columns) {
            addColumn(column);
        }
    }

    public final void addColumn(LogColumn column) {

        columns.add(column);
    }

    public final void addColumns(LogColumn[] columns) {

        for (LogColumn column : columns) {
            addColumn(column);
        }
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

    protected void logHeading(String heading) {
        for (LogFormatter formatter : formatters) {
            formatter.logHeading(heading);
        }
    }

    protected void logLine(String line) {
        for (LogFormatter formatter : formatters) {
            formatter.logLine(line);
        }
    }

    protected void logLabels(String[] labels) {
        for (LogFormatter formatter : formatters) {
            formatter.logLabels(labels);
        }
    }

    protected void logValues(String[] values) {
        for (LogFormatter formatter : formatters) {
            formatter.logValues(values);
        }
    }

    public void startLogging() {

        for (LogFormatter formatter : formatters) {
            formatter.startLogging(title);
        }

        if (title != null) {
            logHeading(title);
        }

        if (logEvery > 0) {
            final int columnCount = getColumnCount();
            String[] labels = new String[columnCount + 1];

            labels[0] = "state";

            for (int i = 0; i < columnCount; i++) {
                labels[i + 1] = getColumnLabel(i);
            }

            logLabels(labels);
        }
    }

    public void log(int state) {

//        if (performanceReport && !performanceReportStarted && state >= performanceReportDelay) {
//            startTime = System.currentTimeMillis();
//            startState = state;
//            formatter.setMaximumFractionDigits(2);
//        }


        // TODO add a statement in here that makes it log to traceInfo instead
        if (logEvery > 0 && (state % logEvery == 0)) {

            final int columnCount = getColumnCount();

//            String[] values = new String[columnCount + (performanceReport ? 2 : 1)];
            String[] values = new String[columnCount + 2];

            values[0] = Integer.toString(state);

            for (int i = 0; i < columnCount; i++) {

                values[i + 1] = getColumnFormatted(i);
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

            logValues(values);
        }

//        if (performanceReport && !performanceReportStarted && state >= performanceReportDelay) {
//            performanceReportStarted = true;
//        }

    }

    public void stopLogging() {

        for (LogFormatter formatter : formatters) {
            formatter.stopLogging();
        }
    }

    private String title = null;

    private ArrayList<LogColumn> columns = new ArrayList<LogColumn>();

    protected int logEvery = 0;
    protected int initialLogInterval = 0;

    public List<LogFormatter> getFormatters() {
        return formatters;
    }

    public void setFormatters(List<LogFormatter> formatters) {
        this.formatters = formatters;
    }

    protected List<LogFormatter> formatters = new ArrayList<LogFormatter>();

    private boolean performanceReportStarted = false;
    private long startTime;
    private int startState;

    private final NumberFormat formatter = NumberFormat.getNumberInstance();

}
