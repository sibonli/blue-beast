package bb.mcmc.analysis;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: sibon
 * Date: 3/6/11
 * Time: 6:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class StandardConvergenceStatistic extends AbstractConvergeStat{

    public static final StandardConvergenceStatistic INSTANCE = new StandardConvergenceStatistic();

    public StandardConvergenceStatistic(Double[] traceInfo) {
        this();
        // TODO
    }

    public StandardConvergenceStatistic() {
        STATISTIC_NAME = "Standard";
    }

    public void calculateStatistic() {
        // TODO
    }

    public double getStat(String varName) {
        // TODO
        return 50.0; // Temporary value
    }


	public void addTrace(ArrayList<Double> traceInfo) {
		// TODO Auto-generated method stub
		
	}

//	@Override
	public double[] getAllStat() {
		// TODO Auto-generated method stub
		return null;
	}


    public boolean hasConverged() {
        return false;
    }
}
