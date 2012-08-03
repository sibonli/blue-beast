package bb.mcmc.analysis;

/**
 * <p>Title: javastat</p>
 * <p>Description: JAVA programs for statistical computations</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Tung Hai University</p>
 * @author Wen Hsiang Wei and B. J. Guo
 * @version 1.4
 */

import java.util.*;

import javastat.*;
import static javastat.regression.glm.LinkFunction.*;
import static javastat.util.Argument.*;
import static javastat.util.Output.*;
import javastat.regression.glm.ExponentialFamily;
import javastat.regression.glm.GLMTemplate;
import javastat.regression.glm.LinkFunction;
import javastat.regression.glm.LogLinearRegression;
import javastat.regression.glm.LogisticRegression;
import javastat.util.*;

import Jama.*;

public class LogLinearRegression2 extends LogLinearRegression{


    @Override
	protected double[] pearsonResiduals(double[] response,
                                        double[] ...covariate)
    {
        coefficients = super.coefficients(response, covariate);
        System.out.println(link);
        means = means(coefficients, covariate);
        responseVariance = responseVariance(means, ExponentialFamily.GAMMA);
        pearsonResiduals = pearsonResiduals(responseVariance, response,
                                            covariate);
        output.put(PEARSON_RESIDUALS, pearsonResiduals);

        return pearsonResiduals;
    }


//
//    public double[] responseVariance(double[] means,
//                                     ExponentialFamily distribution)
//    {
//        responseVariance = new double[means.length];
//        switch (distribution)
//        {
//            case NORMAL:
//                for (int i = 0; i < means.length; i++)
//                {
//                    responseVariance[i] = 1.0;
//                }
//                break;
//            case POISSON:
//                for (int i = 0; i < means.length; i++)
//                {
//                    responseVariance[i] = means[i];
//                }
//                break;
//            case BINOMIAL:
//                for (int i = 0; i < means.length; i++)
//                {
//                    responseVariance[i] = means[i] * (1.0 - means[i]);
//                }
//                break;
//            case GAMMA:
//                for (int i = 0; i < means.length; i++)
//                {
//                    responseVariance[i] = Math.pow(means[i], 2.0);
//                }
//                break;
//            case INVERSE_GAUSSIAN:
//                for (int i = 0; i < means.length; i++)
//                {
//                    responseVariance[i] = Math.pow(means[i], 3.0);
//                }
//                break;
//            default:
//                throw new IllegalArgumentException
//                        ("Wrong input distribution function.");
//        }
//        output.put(RESPONSE_VARIANCE, responseVariance);
//
//        return responseVariance;
//    }

    
}
