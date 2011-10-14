package bb.mcmc.analysis;

import java.util.ArrayList;

public class ZTempNovelConvergenceStatistic extends AbstractConvergeStat{

    public static final ZTempNovelConvergenceStatistic INSTANCE = new ZTempNovelConvergenceStatistic();

    public ZTempNovelConvergenceStatistic(Double[] traceInfo) {
        this();
        // TODO
    }

    public ZTempNovelConvergenceStatistic() {
        STATISTIC_NAME = "Temp";
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
