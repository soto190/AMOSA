package org.uma.jmetal.algorithm.multiobjective.mosa.criteria;

/**
 * TODO make use of this class or redesign it.
 * 
 * @author soto190
 *
 */

public class CoolingScheme {
	
	
	public static double geometric(double temperature, double alpha){
		return alpha * temperature;
	}
	
	public static double exponential(double temperature, double alpha){
		return Math.exp(alpha * temperature);
	}
	
	public static double logarithmic(double temperature, double alpha){
		return temperature / Math.log(alpha);
	}

}
