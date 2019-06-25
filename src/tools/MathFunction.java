package tools;

public class MathFunction {

	public static int serieSumation(int sumToNumber){
		return ((sumToNumber * (sumToNumber + 1)) / 2);
	}
	
	public static double slope(double x1, double y1, double x2, double y2){
		return (x1 - x2) / (y1 - y2);
	}
	
}
