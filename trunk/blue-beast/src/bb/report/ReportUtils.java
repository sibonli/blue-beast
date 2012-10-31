package bb.report;

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

    //public static int MAX_SAMPLE_SIZE = 20000;
    public static String tempFileName = "thin_file_temp";

    public static void writeBBLogToFile(HashMap<String, ArrayList<Double>> traceInfo, String outFileName) {

        String STATE_TAG = "state";

        try {
            PrintWriter pw = new PrintWriter(new PrintStream(new FileOutputStream(outFileName)), true);

//            Set<String> set = traceInfo.keySet();
            HashSet<String> set = new HashSet(traceInfo.keySet());


            Iterator<String> keyIterator = set.iterator();
            //for(String s : set) {




//            int stateIndex = traceInfo.get(STATE_TAG);

            LinkedList<ArrayList<Double>> data = new LinkedList<ArrayList<Double>>();

            pw.print(STATE_TAG + "\t"); /* Always print state first */
            ArrayList<Double> statesList = traceInfo.get(STATE_TAG);
            while(keyIterator.hasNext()) {
                String key = keyIterator.next();
                if(!key.equals(STATE_TAG)) {
                    pw.print(key + "\t");
                    data.add(traceInfo.get(key));
                }
            }
            pw.println();

            for(int i = 0; i < statesList.size(); i++) {
                pw.print(statesList.get(i).intValue()+"\t");
                ListIterator<ArrayList<Double>> dataIterator = data.listIterator();
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

    public static void thinTraceInfo(HashMap<String, ArrayList<Double>> traceInfo, int factor) {

        System.out.println("Thinning number of samples logged to BLUE-BEAST to free up heap space. ");

        Iterator<Map.Entry<String, ArrayList<Double>>> it = traceInfo.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ArrayList<Double>> pairs = it.next();
//            String key = pairs.getKey(); // For testing purposes only. Can remove later
            ArrayList<Double> data = pairs.getValue();
            int i=data.size()-1;
            while(i > -1) {
                if((i % factor) != 0) {
                    Double d = data.remove(i);
                }
                i--;
            }
        }
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
