package bb.mcmc.analysis;

/**
 * Created by IntelliJ IDEA.
 * User: sibon
 * Date: 3/6/11
 * Time: 6:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class StandardConvergenceStatistic implements ConvergenceStatistic {

    public static final StandardConvergenceStatistic INSTANCE = new StandardConvergenceStatistic();

    public StandardConvergenceStatistic(Double[] traceInfo) {
        // TODO
    }

    public StandardConvergenceStatistic() {

    }

    public void calculateStatistic() {
        // TODO
    }

    public double getValue() {
        // TODO
        return 50.0; // Temporary value
    }
}
