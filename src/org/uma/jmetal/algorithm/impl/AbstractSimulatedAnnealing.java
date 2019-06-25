//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.algorithm.impl;

import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.mosa.criteria.AcceptanceCriteria;
import org.uma.jmetal.algorithm.multiobjective.mosa.criteria.CoolingScheme;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;

/**
 * Abstract class for implementing versions of Multi-objective Simulated
 * Annealing (MOSA) algorithm.
 * 
 * @author Jos� Carlos Soto Monterrubio <soto190@gmail.com>
 *
 * @param <S>
 *            Generic type of solution.
 * @param <R>
 *            Generic type of algorithm result.
 */
public abstract class AbstractSimulatedAnnealing<S extends Solution<?>, R> implements Algorithm<R> {

	public enum TransitionRule {
		ScalarLinear, ScalarChebyshev, SimpleProduct, Chebyshev, Product, Weak, Mixed, MinimunCost, MaximumCost, RandomCost, SelfCost, AverageCost
	}

	private double intialTemperature;
	private double finalTemperature;
	private double alpha;
	private double metropolisLength;

	protected List<S> population;

	protected double temperature;
	protected MutationOperator<S> mutationOperator;
	protected AcceptanceCriteria<S> criteria;
	protected TransitionRule rule;
//	protected CoolingScheme coolingScheme;

	public List<S> getPopulation() {
		return this.population;
	}

	public void setPopulation(List<S> population) {
		this.population = population;
	}

	protected abstract void initProgress();

	protected abstract void updateProgress();

	protected abstract void updatePopulation(S candidate);

	protected abstract boolean isStopConditionReached();
	
	protected abstract boolean isStochasticEquilibriumReached();

	/**
	 * Reduce the temperature.
	 * 
	 * @return the new reduced temperature.
	 */
	protected abstract double coolingScheme();

	protected abstract S selectRandomSolution();

	/**
	 * It is a function probability to accept a new solution, also known as
	 * transition rule or acceptance criteria. This functions considers the
	 * energy differences between the new solution and the current solution.
	 * {@link org.uma.jmetal.algorithm.multiobjective.mosa.criteria}
	 * 
	 * @param candidate
	 *            The new solution.
	 * @param current
	 *            The current solution.
	 * @return <b>True </b> if the Botlzman probability accepts the new
	 *         candidate solution, <b>false</b> otherwise.
	 */
	protected abstract boolean acceptanceCriteria(S candidate, S current);

	protected abstract List<S> createInitialPopulation();

	/**
	 * Perturb the given solution. Made a mutation of the given solution.
	 * 
	 *            solution to be perturbed.
	 * @return A new perturbed solution.
	 */
	protected abstract S perturbation(S solution);

	protected abstract int getIterations();

	protected abstract List<S> evaluatePopulation(List<S> population);

	public abstract R getResult();

	protected List<S> getNonDominatedSolutions(List<S> solutionList) {
		return SolutionListUtils.getNondominatedSolutions(solutionList);
		//return solutionList;

	}

	/**
	 * Metropolis cycle or Markov's chain.
	 * 
	 * @return
	 */
	protected abstract boolean metropolisCycle();

	public double getInitialTemperature() {
		return intialTemperature;
	}

	public void setIntialTemperature(double intialTemperature) {
		this.intialTemperature = intialTemperature;
	}

	public double getFinalTemperature() {
		return finalTemperature;
	}

	protected double getTemperature() {
		return this.temperature;
	}

	public void setFinalTemperature(double finalTemperature) {
		this.finalTemperature = finalTemperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public double getMetropolisLength() {
		return metropolisLength;
	}

	public void setMetropolisLength(double metropolisLength) {
		this.metropolisLength = metropolisLength;
		if(this.metropolisLength < 1)
			this.metropolisLength = 1;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public void run() {

	}
}
