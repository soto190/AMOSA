package org.uma.jmetal.algorithm.multiobjective.nsga2;

import java.util.List;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.IntegerSBXCrossover;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

public class NSGAIIFLC<S extends Solution<?>> extends NSGAII {

	private double average;

	@SuppressWarnings("unchecked")
	public NSGAIIFLC(Problem<S> problem, int maxEvaluations, int populationSize, CrossoverOperator<S> crossoverOperator,
			MutationOperator<S> mutationOperator, SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator) {
		super(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, selectionOperator,
				evaluator);

	}
	
	@Override protected void updateProgress() {
	    evaluations += getMaxPopulationSize() ;
	  }

	
	protected void flc(){
		int totalSolutionsInF0 = getNonDominatedSolutions(getPopulation()).size();
		double crossoverProbability = 0.9; /*TODO this value is changed by the FLC.*/
		double crossoverDistributionIndex = 20.0;

		this.crossoverOperator = new IntegerSBXCrossover(crossoverProbability, crossoverDistributionIndex);

			}

	/**
	 * TODO Use the fuzzy logic controller to increment the number of iterations
	 * 
	 * After each generation the fuzzy logic controller is called
	 *  - mutation probability 
	 *  - crossover probability
	 *  - max evaluations or generations
	 */
	@Override
	protected boolean isStoppingConditionReached() {
		
		flc();
		
		if(evaluations >= maxEvaluations)
			return true;
		return false;
	}
	
	  @Override public void run() {
		    List<S> offspringPopulation;
		    List<S> matingPopulation;
		   List<S> population = this.getPopulation();

		    population = createInitialPopulation();
		    population = evaluatePopulation(population);
		    initProgress();
		    while (!isStoppingConditionReached()) {
		      matingPopulation = selection(population);
		      offspringPopulation = reproduction(matingPopulation);
		      offspringPopulation = evaluatePopulation(offspringPopulation);
		      population = replacement(population, offspringPopulation);
		      updateProgress();
		      
		    }
	  }

}
