///**
// *  *  BLUE-BEAST - Bayesian and Likelihood-based methods Usability Extension
// *  Copyright (C) 2011 Wai Lok Sibon Li & Steven H Wu
//
// *  This program is free software: you can redistribute it and/or modify
// *  it under the terms of the GNU General Public License as published by
// *  the Free Software Foundation, either version 3 of the License, or
// *  (at your option) any later version.
//
// *  This program is distributed in the hope that it will be useful,
// *  but WITHOUT ANY WARRANTY; without even the implied warranty of
// *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// *  GNU General Public License for more details.
//
// *  You should have received a copy of the GNU General Public License
// *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
// *
// *  @author Wai Lok Sibon Li
// */
//
//package bb.report;
//
//import dr.app.tracer.application.TracerFrame;
//import jam.framework.ExitException;
//
//import javax.swing.*;
//import java.security.Permission;
//
//public class LoadTracer {
//
////    private static SecurityManager securityManager;
//
//    public static void loadTracer(String logFileName) {
//        final String[] s = {logFileName};
//
//
//        Icon icon = null;
//        BlueBeastTracerApplication app = new BlueBeastTracerApplication("nameString", "aboutString", icon, "websiteURLString", "helpURLString");
////        BlueBeastTracerApplication app = new BlueBeastTracerApplication();
//
//        TracerFrame frame = new TracerFrame("Convergence Tester");
//        app.setDocumentFrame(frame);
//        app.initialize();
//
//        frame.processTraces(s);
//
////        app.doNew();
//
////        TracerFrame frame = (TracerFrame) app.getDefaultFrame();
//
//
//        /* Old stuff
//
//        Thread thread = new Thread() {
//            public void run() {
//                try {
//                    TracerApp.main(s);
//                }
//                catch(Exception e) {
//                    System.err.println("this is me");
//                    e.printStackTrace();
//                }
//            }
//        };
//        //Runtime.getRuntime().removeShutdownHook(thread);
//        thread.start();
////        securityManager = System.getSecurityManager();
//        System.setSecurityManager(new NoExitSecurityManager());
//
////        runTracerApp(s);
////        TracerThread tt = new TracerThread(s);
////        new Thread(tt).start();
//        //TracerApp.main(s);
//        // TODO Set Tracer burnin value, may have to copy tracerApp code (short)
//*/
//    }
//
//
//
//
//
////    protected static class ExitException extends SecurityException {
//////        public final int status;
////        public ExitException(int status) {
//////            super("There is no escape!");
////            // TODO handle the exception properly  (short)
////            // TODO threaded properly so it doesn't exit when Tracer exits (short)
////            // TODO for some reason the error appears to not stem from this but after? even though it says error comes from here
//////            super("Exiting Tracer (Ignore this Exception)");
//////            this.status = status;
////        }
////    }
////
////    private static class NoExitSecurityManager extends SecurityManager {
////        @Override
////        public void checkPermission(Permission perm) /*throws AccessControlException*/ {
////            //super.checkPermission(perm, context);
////            // allow anything.
////            if (perm.getName().contains("exitVM.1")) {
////                throw new SecurityException(
////                "System.exit calls not allowed!");
////            }
////        }
////        @Override
////        public void checkPermission(Permission perm, Object context) /*throws AccessControlException*/ {
////            //super.checkPermission(perm, context);
////            // allow anything.
////        }
////        @Override
////        public void checkExit(int status) {
////            super.checkExit(status);
////            PrintStream errorStream = System.err;
////                System.setSecurityManager(securityManager); // or save and restore original
////                throw new ExitException(status);
//////            try {
//////                System.setErr(new PrintStream(new ByteArrayOutputStream(0)));
//////                //throw new ExitException(status);
//////
//////            }
//////            catch (SecurityException e) {
//////                System.out.println("Catch!!! ");
//////            }
//////            finally {
//////                System.setErr(errorStream);
//////            }
////
////        }
////    }
//
//}
//
//class NoExitSecurityManager extends SecurityManager {
//    @Override
//    public void checkPermission(Permission perm) {
////            super.checkPermission(perm);
//    }
//    @Override
//    public void checkPermission(Permission perm, Object context) {
////            super.checkPermission(perm, context);
//    }
//    @Override
//    public void checkExit(int status) {
//        super.checkExit(status);
//        throw new ExitException(status);//ExitException(status);
//
//    }
//}
//
////class ExitException extends SecurityException {    // Delete this later and import from jam
////    public final int status;
////    public ExitException(int status) {
////        super();
////        this.status = status;
////    }
////}
//
//
////
////class TracerThread implements Runnable {
////    String[] s;
////    public TracerThread(String[] s) {
////        this.s = s;
////    }
////
////    public void run() {
////        try {
////            TracerApp.main(s);
////        }
////        catch(Exception e) {
////            System.err.println("this is me");
////            e.printStackTrace();
////        }
////    }
////
//////    public void e
////}
