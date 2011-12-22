package test;

import bb.mcmc.analysis.*;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import flanagan.analysis.Regression;
import flanagan.analysis.RegressionFunction;

/**
 * Tests the file input/output functionals of BLUE-BEAST
 * @author Wai Lok Sibon Li
 */
public class TestConvergenceStats extends TestCase {
    Hashtable<String, ArrayList<Double>> dataSet1;
    Hashtable<String, ArrayList<Double>> dataSet2;
    Hashtable<String, ArrayList<Double>> dataSet3;
    
    Hashtable<String, double[]> testDataThomas1;
    Hashtable<String, double[]> testDataThomas2;
    
    String[] variableNames;


    public void setUp() {
        dataSet1 = new Hashtable<String, ArrayList<Double>>();
        dataSet2 = new Hashtable<String, ArrayList<Double>>();
        dataSet3 = new Hashtable<String, ArrayList<Double>>();
        testDataThomas1 = creatTestDataset1();
        String[] s = {"one", "two", "three"};
        variableNames = s;

    }

    public void testThomasLibrary() {
    	// R Code
//    	data.x <- c(0.1, 1.2, 2.3, 3.4, 4.5, 5.6, 6.7, 7.8, 8.9, 10.0)
//    	data.y <- c(0.0, 6.0, 13.0, 21.0, 30.0, 40.0, 51.0, 63.0, 76.0, 90.0)
//    	data.all <- as.data.frame( cbind(data.y, data.x) )
//    	result<- glm(data.y~data.x, data=data.all )
//    	summary(result)$coefficients

        Hashtable<String, Double> expected = new Hashtable<String, Double>();
        expected.put("coefficient1", -6.909091);
        expected.put("coefficient2", 9.090909);
        expected.put("sdtError1", 2.4218359);
        expected.put("sdtError2", 0.4065578);

        Regression glm = new Regression(testDataThomas1.get("X"), testDataThomas1.get("Y"));
		glm.linear();

		Hashtable<String, Double> actual = new Hashtable<String, Double>();
		double[] result = glm.getBestEstimates();
		actual.put("coefficient1", result[0]);
		actual.put("coefficient2", result[1]);
		result = glm.getBestEstimatesErrors();
		actual.put("sdtError1", result[0]);
		actual.put("sdtError2", result[1]);

		for (String key : expected.keySet()) {
			assertEquals(expected.get(key), actual.get(key), 1e-7);
		}
        
    }
    
    public void testThomasLibraryLogistic() { //TODO not working yet
    	
    	// R Code
//    	data.x <- c(0.1, 1.2, 2.3, 3.4, 4.5, 5.6, 6.7, 7.8, 8.9, 10.0)
//    	data.by <- c(0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0)
//    	data.all <- as.data.frame( cbind(data.y, data.x) )
//    	result<- glm(data.by~data.x, family=binomial(link = "logit"), data=data.all )
//    	summary(result)$coefficients

        Hashtable<String, Double> expected = new Hashtable<String, Double>();
        expected.put("coefficient1", -0.5667497);
        expected.put("coefficient2", 0.1122277);
//        expected.put("sdtError1", 1.2215225);
//        expected.put("sdtError2", 0.2057517);

        Regression glm = new Regression(testDataThomas1.get("X"), testDataThomas1.get("BinaryY"));
//		glm.logistic();
//        glm.sigmoidThreshold();

//        glm = new Regression(testDataThomas1.get("BX1"), testDataThomas1.get("BY"));

        // Create instances of the class holding the function, y = a + b.exp(-c.x), evaluation method
        FunctOne f1 = new FunctOne();

        // initial estimates of a and c in y = a + b.exp(-c.x)
        double[] start = new double[2];
        start[0] = 0;      // initial estimate of a
        start[1] = 0;      // initial estimate of c
        
        // initial step sizes for a and c in y = a + b.exp(-c.x)
        double[] step = new double[2];
        step[0] = 0.05;      // initial step size for a
        step[1] = 0.05;     // initial step size for c

        // call non-linear regression using default tolerance and maximum iterations and plot display option
        glm.simplexPlot(f1, start, step, 1e-10, (int) 1e20);
        
        glm.gaussian();
        System.out.println(Arrays.toString(glm.getXdata()[0]));
		System.out.println(Arrays.toString(glm.getYdata()));
		
        
        System.out.println(glm.getMinTest());
        System.out.println(glm.getSimplexSd());
        System.out.println(glm.getTolerance());
		
		Hashtable<String, Double> actual = new Hashtable<String, Double>();
		double[] result = glm.getBestEstimates();
		System.out.println(Arrays.toString(result));
		actual.put("coefficient1", result[0]);
		actual.put("coefficient2", result[1]);
		result = glm.getBestEstimatesErrors();
		System.out.println(Arrays.toString(result));
		actual.put("sdtError1", result[0]);
		actual.put("sdtError2", result[1]);
		
		for (String key : expected.keySet()) {
			assertEquals(expected.get(key), actual.get(key), 1e-7);
		}
		
//		System.out.println(f.getSegmentLength()+"\t"+f.getSegmentNumber());
//		System.out.println(f.getNumberOfPsdPoints());

        
    }

    public void testESS() {
        ESSConvergeStat stat = new ESSConvergeStat();
        stat.updateTrace(dataSet1);
    }

    public void testGelman() {
        GelmanConvergeStat stat = new GelmanConvergeStat();
    }

    public void testGeweke() {
        GewekeConvergeStat stat = new GewekeConvergeStat();
    }

    public void testHeidelberg() {
        HeidelbergConvergeStat stat = new HeidelbergConvergeStat();
    }

    public void testRaftery() {
        RafteryConvergeStat stat = new RafteryConvergeStat();
    }

    
    private Hashtable<String, double[]> creatTestDataset1(){
    	
		double[] x = new double[10];
		double[] y = new double[10];
		for (int i = 0; i < 10; i++) {
			x[i] = (1+i)/10.0+i;
			y[i] = (11+i)/2.0*i;
		}
		//For copy/paste dataset into R
//		System.out.println(Arrays.toString(x));
//		System.out.println(Arrays.toString(y));
//		System.out.println(Arrays.toString(by));
		Hashtable<String, double[]> data = new Hashtable<String, double[]>();
		data.put("X", x);
		data.put("Y", y);
//		double[] by = 
		data.put("BinaryY", new double[]{0.0,0.0,0.0,0.0,0.0,1.0,0.0,1.0,1.0,1.0});
		
		data.put("BY", new double[]{1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0});
		data.put("BX1", new double[]{110.0, 110.0, 93.0, 110.0, 175.0, 105.0, 245.0, 62.0, 95.0, 123.0, 123.0, 180.0, 180.0, 180.0, 205.0, 215.0, 230.0, 66.0, 52.0, 65.0, 97.0, 150.0, 150.0, 245.0, 175.0, 66.0, 91.0, 113.0, 264.0, 175.0, 335.0, 109.0});
		data.put("BX2", new double[]{2.620, 2.875, 2.320, 3.215, 3.440, 3.460, 3.570, 3.190, 3.150, 3.440, 3.440, 4.070, 3.730, 3.780, 5.250, 5.424, 5.345, 2.200, 1.615, 1.835, 2.465, 3.520, 3.435, 3.840, 3.845, 1.935, 2.140, 1.513, 3.170, 2.770, 3.570, 2.780});
				

		return(data);
    }
		
	 

}


class FunctOne implements RegressionFunction{


    public double function(double[] p, double[] x){
         double y = 1.0 / (1.0 + Math.exp( - (p[0]+p[1]*x[0]) ));
//         double y = p[0]+p[1]*x[0] ;
         return y;
    }

}
/*
//Class to demonstrate non-linear regression method, Regression.simplex.
public class RegressionExampleOne{

    public static void main(String[] arg){

             // x data array
             double[] xArray = {0.0,0.5,1.0,1.5,2.0,2.5,3.0,3.5,4.0,4.5,5.0,5.5,6.0,6.5,7.0,7.5};
             // observed y data array
             double[] yArray = {10.9996,8.8481,7.1415,6.5376,6.466,5.1026,4.7215,3.7115,3.8383,3.4997,3.7972,3.4459,2.8564,3.291,3.0518,3.6073};
             // estimates of the standard deviations of y
             double[] sdArray = {0.5,0.45,0.55,0.44,0.46,0.51,0.56,0.48,0.5,0.45,0.55,0.44,0.46,0.51,0.56,0.48};


             // Create instances of the class holding the function, y = a + b.exp(-c.x), evaluation method
             FunctOne f1 = new FunctOne();

             // assign value to constant b in the function
             f1.setB(8.0D);

             // initial estimates of a and c in y = a + b.exp(-c.x)
             double[] start = new double[2];
             start[0] = 6.0D;      // initial estimate of a
             start[1] = 0.1D;      // initial estimate of c

             // initial step sizes for a and c in y = a + b.exp(-c.x)
             double[] step = new double[2];
             step[0] = 0.6D;      // initial step size for a
             step[1] = 0.05D;     // initial step size for c

             // create an instance of Regression
             Regression reg = new Regression(xArray, yArray, sdArray);

             // call non-linear regression using default tolerance and maximum iterations and plot display option
             reg.simplexPlot(f1, start, step);
    }
}*/