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
import java.io.File;
import java.util.Locale;

public class LoadTracer {

    public static void LoadTracer(String logFileName) {
        String[] s = {logFileName};

//        Locale.setDefault(Locale.US);
//
//        boolean lafLoaded = false;
//
//        if (OSType.isMac()) {
//            System.setProperty("apple.awt.graphics.UseQuartz", "true");
//            System.setProperty("apple.awt.antialiasing","true");
//            System.setProperty("apple.awt.rendering","VALUE_RENDER_QUALITY");
//
//            System.setProperty("apple.laf.useScreenMenuBar","true");
//            System.setProperty("apple.awt.draggableWindowBackground","true");
//            System.setProperty("apple.awt.showGrowBox","true");
//
//            // set the Quaqua Look and Feel in the UIManager
//            try {
//                UIManager.setLookAndFeel(
//                        "ch.randelshofer.quaqua.QuaquaLookAndFeel"
//                );
//                lafLoaded = true;
//
//
//            } catch (Exception e) {
//                //
//            }
//
//            UIManager.put("SystemFont", new Font("Lucida Grande", Font.PLAIN, 13));
//            UIManager.put("SmallSystemFont", new Font("Lucida Grande", Font.PLAIN, 11));
//        }
//
//        if (!lafLoaded) {
//            try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            } catch (Exception e1) {
//                e1.printStackTrace();
//            }
//        }
//
//        try {
//            java.net.URL url = TracerApp.class.getResource("images/Tracer.png");
//            Icon icon = null;
//
//            if (url != null) {
//                icon = new ImageIcon(url);
//            }
//
//            final String nameString = "Tracer";
//            final String versionString = "v1.6.0pre";
//            String aboutString = "<html><center><p>MCMC Trace Analysis Tool<br>" +
//                    "Version " + versionString + ", 2003-2011</p>" +
//                    "<p>by<br>" +
//
//                    "Andrew Rambaut, Marc A. Suchard, Walter Xie and Alexei J. Drummond</p>" +
//
//                    "<p>Institute of Evolutionary Biology, University of Edinburgh<br>" +
//                    "<a href=\"mailto:a.rambaut@ed.ac.uk\">a.rambaut@ed.ac.uk</a></p>" +
//
//                    "<p>Departments of Biomathematics, Biostatistics and Human Genetics, UCLA<br>" +
//                    "<a href=\"mailto:msuchard@ucla.edu\">msuchard@ucla.edu</a></p>" +
//
//                    "<p>Department of Computer Science, University of Auckland<br>" +
//                    "<a href=\"mailto:alexei@cs.auckland.ac.nz\">alexei@cs.auckland.ac.nz</a></p>" +
//
//                    "<p>Available from the BEAST site:<br>" +
//                    "<a href=\"http://beast.bio.ed.ac.uk/\">http://beast.bio.ed.ac.uk/</a></p>" +
//                    "<p>Source code distributed under the GNU LGPL:<br>" +
//                    "<a href=\"http://code.google.com/p/beast-mcmc/\">http://code.google.com/p/beast-mcmc/</a></p>" +
//                    "<p>Thanks for contributions to: Joseph Heled, Oliver Pybus & Benjamin Redelings</p>" +
//                    "</center></html>";
//
//            String websiteURLString = "http://beast.bio.ed.ac.uk/";
//            String helpURLString = "http://beast.bio.ed.ac.uk/Tracer";
//
//            TracerApp app = new TracerApp(nameString, aboutString, icon, websiteURLString, helpURLString);
//            app.setDocumentFrameFactory(new DocumentFrameFactory() {
//                public DocumentFrame createDocumentFrame(Application app, MenuBarFactory menuBarFactory) {
//                    return new TracerFrame("Tracer");
//                }
//            });
//            app.initialize();
//
//            app.doNew();
//
////            if (s.length > 0) {
////                TracerFrame frame = (TracerFrame) app.getDefaultFrame();
////                for (String fileName : s) {
////
////                    File file = new File(fileName);
////                    LogFileTraces[] traces = { new LogFileTraces(fileName, file) };
////
////                    frame.processTraces(traces);
////                }
////            }
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(new JFrame(), "Fatal exception: " + e,
//                    "Please report this to the authors",
//                    JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
//        }





//        Locale.setDefault(Locale.US);
//
//        boolean lafLoaded = false;
//
//        if (OSType.isMac()) {
//            System.setProperty("apple.awt.graphics.UseQuartz", "true");
//            System.setProperty("apple.awt.antialiasing","true");
//            System.setProperty("apple.awt.rendering","VALUE_RENDER_QUALITY");
//
//            System.setProperty("apple.laf.useScreenMenuBar","true");
//            System.setProperty("apple.awt.draggableWindowBackground","true");
//            System.setProperty("apple.awt.showGrowBox","true");
//
//            // set the Quaqua Look and Feel in the UIManager
//            try {
//                UIManager.setLookAndFeel(
//                        "ch.randelshofer.quaqua.QuaquaLookAndFeel"
//                );
//                lafLoaded = true;
//
//
//            } catch (Exception e) {
//                //
//            }
//
//            UIManager.put("SystemFont", new Font("Lucida Grande", Font.PLAIN, 13));
//            UIManager.put("SmallSystemFont", new Font("Lucida Grande", Font.PLAIN, 11));
//        }
//
//        if (!lafLoaded) {
//            try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            } catch (Exception e1) {
//                e1.printStackTrace();
//            }
//        }
//        final String nameString = "Tracer";
//        String aboutString = "test";
//        String websiteURLString = "http://beast.bio.ed.ac.uk/";
//        String helpURLString = "http://beast.bio.ed.ac.uk/Tracer";
//        //TracerApp app = new TracerApp(nameString, aboutString, icon, websiteURLString, helpURLString);
//        TracerApp app = new TracerApp(nameString, aboutString, null, websiteURLString, helpURLString);
//            app.setDocumentFrameFactory(new DocumentFrameFactory() {
//                public DocumentFrame createDocumentFrame(Application app, MenuBarFactory menuBarFactory) {
//                    return new TracerFrame("Tracer");
//                }
//            });
//            app.initialize();

        TracerApp.main(s);
        //new TracerApp();
    }
}
