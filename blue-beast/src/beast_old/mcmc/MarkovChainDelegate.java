/*
 * MarkovChainListener.java
 *
 * Copyright (C) 2002-2006 Alexei Drummond and Andrew Rambaut
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
 *  BEAST is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with BEAST; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */

package beast_old.mcmc;

import dr.inference.mcmc.MCMCOptions;
import dr.inference.operators.OperatorSchedule;

/**
 * An interface for facilitating delegating of tasks in an MCMC chain.
 *
 * @author Wai Lok Sibon Li
 *
 */
@Deprecated
public interface MarkovChainDelegate {

    void setup(MCMCOptions options, OperatorSchedule schedule);

    void currentState(long state);

    void currentStateEnd(long state);

	void finished(long chainLength);
}
