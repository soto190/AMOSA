package org.uma.jmetal.algorithm.multiobjective.mosa.criteria;

import org.uma.jmetal.algorithm.impl.AbstractSimulatedAnnealing.TransitionRule;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public class AcceptanceCriteria<S extends Solution<?>> {

	double[] weight;
	double[] r;
	double alpha;

	protected TransitionRule rule;

	public AcceptanceCriteria() {
		
	}
	
	public AcceptanceCriteria(TransitionRule rule) {
		this.rule = rule;
		this.alpha = 0.80;

	}

	public AcceptanceCriteria(TransitionRule rule, double[] weight) {
		this.rule = rule;
		this.weight = new double[weight.length];
		for (int i = 0; i < weight.length; i++)
			this.weight = weight;
	}

	public AcceptanceCriteria(TransitionRule rule, double[] weight, double r[]) {
		this.rule = rule;
		this.weight = new double[weight.length];
		this.r = new double[r.length];
		for (int i = 0; i < weight.length; i++) {
			this.weight[i] = weight[i];
			this.r[i] = r[i];
		}
	}

	public AcceptanceCriteria(TransitionRule rule, double alpha) {
		this.rule = rule;
		this.alpha = alpha;
	}

	public AcceptanceCriteria(TransitionRule rule, double[] weight, double alpha) {
		this.rule = rule;
		this.weight = new double[weight.length];
		this.r = new double[r.length];
		for (int i = 0; i < weight.length; i++)
			this.weight[i] = weight[i];

		this.alpha = alpha;
	}

	public double[] getWeight() {
		return weight;
	}

	public void setWeight(double[] weight) {
		this.weight = weight;
	}

	public double[] getR() {
		return r;
	}

	public void setR(double[] r) {
		this.r = r;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public double scalarLinear(S candidate, S current, double temperature) {

		double sum = 0;
		for (int obj = 0; obj < candidate.getNumberOfObjectives(); obj++)
			sum += weight[obj] * (candidate.getObjective(obj) - current.getObjective(obj));

		return Math.exp(sum / temperature);
	}

	public double scalarChebyshev(S candidate, S current, double temperature) {

		double maxCandidate = 0;
		double maxCurrent = 0;
		double tempCa = 0;
		double tempCu = 0;
		for (int obj = 0; obj < candidate.getNumberOfObjectives(); obj++) {
			tempCa = weight[obj] * (candidate.getObjective(obj) - r[obj]);
			tempCu = weight[obj] * (current.getObjective(obj) - r[obj]);

			if (tempCa > maxCandidate)
				maxCandidate = tempCa;
			if (tempCu > maxCurrent)
				maxCurrent = tempCu;
		}

		return Math.exp((maxCandidate - maxCurrent) / temperature);
	}

	public double simpleProduct(S candidate, S current, double temperature) {

		double product = 1;
		for (int obj = 0; obj < candidate.getNumberOfObjectives(); obj++)
			product *= Math.exp((candidate.getObjective(obj) - current.getObjective(obj)) / temperature);

		return product;
	}

	public double product(S candidate, S current, double temperature) {

		double product = 1;
		for (int obj = 0; obj < candidate.getNumberOfObjectives(); obj++)
			product *= Math.exp(weight[obj] * (candidate.getObjective(obj) - current.getObjective(obj)) / temperature);

		return product;
	}

	public double chebyshev(S candidate, S current, double temperature) {

		double min = 1;
		double temp = 1;
		for (int obj = 0; obj < candidate.getNumberOfObjectives(); obj++) {
			temp = Math.exp(weight[obj] * (candidate.getObjective(obj) - current.getObjective(obj)) / temperature);
			if (temp < min)
				min = temp;
		}

		return min;
	}

	public double weak(S candidate, S current, double temperature) {
			

		double max = 0;
		double temp = 1;
		for (int obj = 0; obj < candidate.getNumberOfObjectives(); obj++) {
			temp = Math.exp(weight[obj] * (candidate.getObjective(obj) - current.getObjective(obj)) / temperature);
			if (temp > max)
				max = temp;
		}

		return max;
	}

	public double mixed(S candidate, S current, double temperature) {

		return alpha * product(candidate, current, temperature) + (1 - alpha) * weak(candidate, current, temperature);
	}

	public double minimumCost(S candidate, S current, double temperature) {

		double min = Double.MAX_VALUE;
		double temp = 0;
		for (int obj = 0; obj < candidate.getNumberOfObjectives(); obj++) {
			temp = candidate.getObjective(obj) - current.getObjective(obj);
			if (temp < min)
				min = temp;
		}

		return Math.exp(min / temperature);
	}

	public double maximumCost(S candidate, S current, double temperature) {

		double max = 0;
		double temp = 0;
		for (int obj = 0; obj < candidate.getNumberOfObjectives(); obj++) {
			temp = candidate.getObjective(obj) - current.getObjective(obj);
			if (temp > max)
				max = temp;
		}

		return Math.exp(max / temperature);
	}

	public double randomCost(S candidate, S current, double temperature) {
		double sum = 0;
		JMetalRandom rnd = JMetalRandom.getInstance();
		for (int obj = 0; obj < candidate.getNumberOfObjectives(); obj++)
			sum += rnd.nextDouble() * (candidate.getObjective(obj) - current.getObjective(obj));

		return Math.exp(sum / temperature);
	}

	public double selfCost(S candidate, S current, double temperature) {
		double sum = 0;
		for (int obj = 0; obj < candidate.getNumberOfObjectives(); obj++)
			sum += candidate.getObjective(obj);
		return Math.exp(sum / temperature);
	}

	public double averageCost(S candidate, S current, double temperature) {
		double sum = 0;
		for (int obj = 0; obj < candidate.getNumberOfObjectives(); obj++)
			sum += candidate.getObjective(obj) - current.getObjective(obj);

		double average = sum / candidate.getNumberOfObjectives();
		return Math.exp(average / temperature);
	}

	public double boltzman(double energy, double temperature) {

		return Math.exp(energy / temperature);
	}
	


	public double execute(S candidate, S current, double temperature) {
		if (rule.equals(TransitionRule.ScalarLinear))
			return scalarLinear(candidate, current, temperature);
		else if (rule.equals(TransitionRule.ScalarChebyshev))
			return scalarChebyshev(candidate, current, temperature);
		else if (rule.equals(TransitionRule.SimpleProduct))
			return simpleProduct(candidate, current, temperature);
		else if (rule.equals(TransitionRule.Product))
			return product(candidate, current, temperature);
		else if (rule.equals(TransitionRule.Chebyshev))
			return chebyshev(candidate, current, temperature);
		else if (rule.equals(TransitionRule.Mixed))
			return mixed(candidate, current, temperature);
		else if (rule.equals(TransitionRule.MinimunCost))
			return minimumCost(candidate, current, temperature);
		else if (rule.equals(TransitionRule.MaximumCost))
			return maximumCost(candidate, current, temperature);
		else if (rule.equals(TransitionRule.RandomCost))
			return randomCost(candidate, current, temperature);
		else if (rule.equals(TransitionRule.SelfCost))
			return selfCost(candidate, current, temperature);
		else if (rule.equals(TransitionRule.AverageCost))
			return averageCost(candidate, current, temperature);
		else
			throw new JMetalException("Acceptance criteria not defined.");

	}

}
