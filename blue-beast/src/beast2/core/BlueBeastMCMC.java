package beast2.core;

/**
 * Created by IntelliJ IDEA.
 * User: sibon
 * Date: 3/15/11
 * Time: 2:43 PM
 * To change this template use File | Settings | File Templates.
 *
 * @author Wai Lok Sibon Li
 *
 */

//@Description("Sibon's MCMC extension with periodical tests")

@Deprecated
public class BlueBeastMCMC extends beast_old.mcmc.MCMC {
    // todo
//    public Input<Integer> m_period = new Input<Integer>("period","period between samples being tested (default 1000)", 1000);
//        public Input<Boolean> m_doTest = new Input<Boolean>("useTest","flag to indicate whether samples should be tested (default true)", true);

        boolean m_bTest;
        int m_nPeriod;

    public BlueBeastMCMC(String s) {
        super(s);
    }

    //@Override
    public void initAndValidate() {
//            m_bTest = useTest.get();
//            m_nPeriod = m_period.get();
        }

    //@Override
        void callUserFunction(int iSample) {
            if (m_bTest && iSample % m_nPeriod==0) {
                // do the work here
            }
        }
}
