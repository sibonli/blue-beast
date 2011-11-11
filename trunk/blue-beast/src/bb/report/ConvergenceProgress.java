package bb.report;

import bb.mcmc.analysis.ConvergeStat;

/*
* @author Wai Lok Sibon Li
*/
public class ConvergenceProgress {

    String name;
    double progress;
    ConvergeStat cs;

    public ConvergenceProgress(String name, double progress, ConvergeStat cs) {
        this.name = name;
        this.progress = progress;
        this.cs = cs;

    }

    public String getName() {
        return name;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double p) {
        progress = p;
    }

    public String getConvergenceStatType() {
        return cs.getStatisticName();
    }
}
