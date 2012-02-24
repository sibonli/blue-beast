package bb.report;

import dr.app.tools.NexusExporter;
import dr.evolution.io.Importer;
import dr.evolution.io.NexusImporter;
import dr.evolution.io.TreeImporter;
import dr.evolution.tree.Tree;
import dr.inference.trace.LogFileTraces;
import dr.inference.trace.TraceException;

import java.io.*;
import java.util.*;

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
public class ReportUtils {

    public static int MAX_SAMPLE_SIZE = 20000;
    public static String tempFileName = "thin_file_temp";

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

    public static void thinTraceInfo(Hashtable<String, ArrayList<Double>> traceInfo, int factor) {
        factor = 2;
    }

    public static void thinLogFile(String fileName, int factor) {
        thinLogFile(new File(fileName), factor);
    }
    public static void thinLogFile(File file, int factor) {

        int burnin = 0;

        try {
            FileReader fileReader = new FileReader(file);

            LogFileTraces traces = new LogFileTraces(file.getName(), file);
            traces.loadTraces();
            traces.setBurnIn(burnin);

            for (int i = 0; i < traces.getTraceCount(); i++) {
                traces.analyseTrace(i);
            }
            //return traces;

        } catch(IOException e) {
            throw new RuntimeException("Error reading/writing file while thinning the log file");
        }
//        catch (Importer.ImportException e) {
//            throw new RuntimeException("Importer error while thinning the tree file");
//        }
        catch (TraceException e) {
            throw new RuntimeException("Trace exception while thinning the tree file");
        }
    }

    public static void thinTreeFile(String fileName, int factor) {
        thinTreeFile(new File(fileName), factor);
    }
    public static void thinTreeFile(File file, int factor) {

        //NexusExporter exporter = new NexusExporter();

        try {
            FileReader fileReader = new FileReader(file);
            TreeImporter importer = new NexusImporter(fileReader);

            int count = 0;
            while (importer.hasTree()) {
                    Tree tree = importer.importNextTree();
            }


        } catch(IOException e) {
            throw new RuntimeException("Error in file I/O while thinning the tree file");
        }
        catch (Importer.ImportException e) {
            throw new RuntimeException("Importer error while thinning the tree file");
        }
    }
}
