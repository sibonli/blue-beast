/**
 *  BLUE-BEAST - Bayesian and Likelihood-based methods Usability Extension
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


 * @author Wai Lok Sibon Li
 *
 */

package test;

import bb.mcmc.analysis.*;
import bb.report.LoadTracer;
import bb.report.ProgressReporter;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Tests for BLUE-BEAST
 */
public class TestLoadTracer extends TestCase {




    public void setUp() {
    }

    public void testProgressESSOnly() {
        LoadTracer.LoadTracer("/Users/sibon/IdeaProjects/beast-mcmc/influenza_NA_rate_test_month_relaxed_clock_bb.log");
        assertEquals("test", "test");
    }
}