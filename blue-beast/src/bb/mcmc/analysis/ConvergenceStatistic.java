package bb.mcmc.analysis;

public interface ConvergenceStatistic {

    public void calculateStatistic();


    public abstract double getValue();

}
