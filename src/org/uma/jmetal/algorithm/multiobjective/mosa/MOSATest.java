package org.uma.jmetal.algorithm.multiobjective.mosa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.algorithm.multiobjective.mosa.criteria.AcceptanceCriteria;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.impl.Hypervolume;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;

//import metrics.HypervolumeFleischer;
//import metrics.HypervolumeIE;

public class MOSATest<S extends Solution<?>> extends MOSAAnalyticalTunning<S> {
	
	protected double[] maxValues;

	public MOSATest(Problem<S> problem, int populationSize, double alpha, double maxDeterioration,
			double minDeterioration, double neighborhoodExploration, MutationOperator<S> mutation,
			AcceptanceCriteria<S> criteria) {

		super(problem, populationSize, alpha, maxDeterioration, minDeterioration, neighborhoodExploration, mutation, criteria);
		this.setAlpha(alpha);
		this.maxDeterioration = maxDeterioration;
		this.minDeterioration = minDeterioration;
		this.explorationLevel = neighborhoodExploration;
		maxValues = new double[problem.getNumberOfObjectives()];

	}

	public List<S> perturbation(List<S> population) {

		ArrayList<S> candidate = new ArrayList<S>(100);

		for (Solution<?> s : population) {
			S temp = ((S) s.copy());
			this.problem.evaluate(this.mutationOperator.execute(temp));
			candidate.add(temp);
		}
		return candidate;
	}
	
	public boolean acceptanceCriteria(List<S> candidate, List<S> population){
		double[] maxValues = new double[candidate.get(0).getNumberOfObjectives()];
		
		for (int nSol = 0; nSol < candidate.size(); nSol++) {
			for (int nObj = 0; nObj < maxValues.length; nObj++) {
				
				double objC = candidate.get(nSol).getObjective(nObj);
				double objP = population.get(nSol).getObjective(nObj);
						
				if(objC > maxValues[nObj])
					maxValues[nObj] = candidate.get(nSol).getObjective(nObj);
				if(objP > maxValues[nObj])
					maxValues[nObj] = population.get(nSol).getObjective(nObj);
			}
		}
		double hvC = -1;
		double hvP = -1;
		
		/*try {
			hvC = HypervolumeFleischer.computeHypervolume((List<Solution<?>>)candidate, maxValues);
			hvP = HypervolumeFleischer.computeHypervolume((List<Solution<?>>)population, maxValues);

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		/*
		ArrayList<S> all = new ArrayList<S>(candidate);
		all.addAll(population);
		ArrayFront allFront = new ArrayFront(all);
		
		FrontNormalizer fr = new FrontNormalizer(candidate);
		
		List<S> normC = (List<S>) fr.normalize(candidate);
		List<S> normP = (List<S>) fr.normalize(population);
		ArrayFront afC = new ArrayFront(normC);
		ArrayFront afP = new ArrayFront(normP);
		
		FrontUtils.getMaximumValues(afC);

		;
		Hypervolume<List<S>> qi = new Hypervolume<List<S>>(allFront);
		double hvC = qi.evaluate((List<S>) normC);
		double hvP = qi.evaluate((List<S>) normP);
		
		*/
		return (Math.exp((hvC - hvP) / temperature) < randomGenerator.nextDouble()) ;
	}
	
	public void updatePopulation(List<S> candidate){
		
		ArrayList<S> join = new ArrayList<S>(candidate);
		join.addAll(population);
		if (this.population.size() >= this.populationSize) {
			this.population = getNonDominatedSolutions(join);
			while (this.population.size() >= this.populationSize * 0.20)
				this.population.remove(randomGenerator.nextInt(0, this.population.size() - 1));
		}
		
		this.population = getNonDominatedSolutions(join);
	}
	
	
	public void run() {

		this.population = createInitialPopulation();
		this.population = evaluatePopulation(this.population);
		initProgress();

		while (!isStopConditionReached()) {
			while (metropolisCycle()) {
				List<S> candidate = perturbation(this.population);
				if (acceptanceCriteria(candidate, this.population))
					updatePopulation(candidate);
				updateProgress();
			}
			coolingScheme();
		}
	}

}
