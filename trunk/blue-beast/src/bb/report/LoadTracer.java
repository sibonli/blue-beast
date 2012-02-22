/**
 *  *  BLUE-BEAST - Bayesian and Likelihood-based methods Usability Extension
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
 *
 *  @author Wai Lok Sibon Li
 */

package bb.report;

import dr.app.tracer.application.TracerApp;
import dr.app.tracer.application.TracerFrame;
import dr.app.util.OSType;
import dr.inference.trace.LogFileTraces;
import jam.framework.Application;
import jam.framework.DocumentFrame;
import jam.framework.DocumentFrameFactory;
import jam.framework.MenuBarFactory;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class LoadTracer {

    public static void loadTracer(String logFileName) {
        String[] s = {logFileName};
        TracerApp.main(s);
    }

    public static void writeBBLogToFile(HashMap<String, ArrayList<Double>> traceInfo, String outFileName) {
        try {
            PrintWriter pw = new PrintWriter(new PrintStream(new FileOutputStream(outFileName)), true);

            Set<String> set = traceInfo.keySet();


            Iterator<String> keyIterator = set.iterator();
            //for(String s : set) {

            while(keyIterator.hasNext()) {
                pw.print(keyIterator.next() + "\t");
            }
            pw.println();

            keyIterator = set.iterator();
            int rowCount = traceInfo.get(keyIterator.next()).size();



            Collection<ArrayList<Double>> traceData = traceInfo.values();


            Iterator<ArrayList<Double>> dataIterator;
            for(int i = 0; i < rowCount; i++) {
                dataIterator = traceData.iterator();
                while(dataIterator.hasNext()) {
                    pw.print(dataIterator.next().get(i) + "\t");
                }
                pw.println();
            }


            pw.close();
        }
        catch(IOException e) {
            throw new RuntimeException("Problem writing temporary log file for Tracer");
        }

    }
}
