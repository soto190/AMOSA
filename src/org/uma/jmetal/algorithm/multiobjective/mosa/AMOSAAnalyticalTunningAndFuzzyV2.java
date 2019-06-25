package org.uma.jmetal.algorithm.multiobjective.mosa;

import java.util.Arrays;
import java.util.Comparator;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.DominanceComparator;

import com.mathworks.toolbox.javabuilder.MWArray;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

import fuzzyForSA.FuzzyForSA;
import com.fuzzylite.*;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Gaussian;
import com.fuzzylite.term.Sigmoid;
import com.fuzzylite.term.Triangle;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
/**
 * Archive Multi-objective Simulated Annealing (AMOSA)
 * 
 * Bandyopadhyay, S., Saha, S., Maulik, U., & Deb, K. (2008). A simulated
 * annealing-based multiobjective optimization algorithm: AMOSA. Evolutionary
 * Computation, IEEE Transactions on, 12(3), 269-283.
 *
 * @author José Carlos Soto Monterrubio <soto190@gmail.com>
 * @version 1.0
 *
 */

public class AMOSAAnalyticalTunningAndFuzzyV2<S extends Solution<?>> extends AMOSA<S> {

	private static final Comparator<Solution<?>> DOMINANCE_COMPARATOR = new DominanceComparator<Solution<?>>();
	protected int maxLength;
	protected double beta;
	protected double maxDeterioration;
	protected double minDeterioration;
	protected double explorationLevel;

	protected double average;
	protected double maxDif;
	protected double minDif;
	protected double deltaAccepted;
	protected double mean;
	protected double variance;

	

	protected S bestSolution;
	protected S worstSolution;

	protected S bestAcceptedSolution;
	protected S worstAcceptedSolution;

	protected int totalAcceptedInMetropolis;
	protected double totalAmountInMetropolis;

	protected double averageAmountInMetropolis;

	protected FuzzyForSA fuzzy;
	
	protected Engine engine;
	InputVariable normTemperature, normAvgDominance;  
	OutputVariable metropolisIncrement;
	
	// protected double deltaAmountDominance;

	public AMOSAAnalyticalTunningAndFuzzyV2(Problem<S> problem, int populationSize, double alpha, double maxDeterioration,
			double minDeterioration, double neighborhoodExploration, MutationOperator<S> mutation, int hardLimit,
			int softLimit) {
		super(problem, populationSize, 0, 0, alpha, 0, mutation, null, hardLimit, softLimit);
		this.maxDeterioration = maxDeterioration;
		this.minDeterioration = minDeterioration;
		this.explorationLevel = neighborhoodExploration;

	}

	// protected S perturbation(S solution){
	//
	// S candidate = (S) solution.copy();
	// this.problem.evaluate(this.mutationOperator.execute(candidate));
	//
	// for (int i = 0; i < 5; i++) {
	// int dominance = DOMINANCE_COMPARATOR.compare(candidate, solution);
	// if(dominance == -1)
	// return candidate;
	// else
	// this.problem.evaluate(this.mutationOperator.execute(candidate));
	//
	// }
	//
	// return candidate;
	// }

	@Override
	protected void initProgress() {
		try {
			fuzzy = new FuzzyForSA();
		} catch (MWException e) {
			e.printStackTrace();
		}
		load_FIS();
		iterations = 1;

		double neightborhoodSize = problem.getNumberOfVariables();
		this.maxLength = (int) (neightborhoodSize * explorationLevel);

		this.setMetropolisLength(10);

		double[] energyCost = new double[this.populationSize];

		for (int i = 0; i < this.populationSize; i++) {
			S solution = this.getSolution(i);
			for (int nObj = 0; nObj < this.problem.getNumberOfObjectives(); nObj++)
				energyCost[i] += (this.randomGenerator.nextDouble() * solution.getObjective(nObj));
		}
		Arrays.sort(energyCost);

		double maxDeterioration = energyCost[energyCost.length - 1] - energyCost[0];
		double minDeterioration = energyCost[1] - energyCost[0];

		this.setIntialTemperature(-maxDeterioration / Math.log(this.maxDeterioration));
		this.setFinalTemperature(-minDeterioration / Math.log(this.minDeterioration));

		this.temperature = this.getInitialTemperature();

		double n = (Math.log(this.getFinalTemperature()) - Math.log(this.getInitialTemperature()))
				/ Math.log(this.getAlpha());

		this.beta = Math.exp(((Math.log(this.maxLength) - Math.log(this.getMetropolisLength())) / n));

		System.out.println("Initial temperature: " + this.getInitialTemperature());
		System.out.println("Final Temperature: " + this.getFinalTemperature());
		System.out.println("Exploration level: " + this.getExplorationLevel());
		System.out.println("Neighborhood size: " + neightborhoodSize);
		System.out.println("Beta: " + this.getBeta());
		System.out.println("Alpha: " + this.getAlpha());
		System.out.println("Max Length: " + this.getMaxLength());
		System.out.println("Total steps:" + n);

	}

	public void setBeta(double beta) {
		this.beta = beta;
	}

	public double getBeta() {
		return this.beta;
	}

	public int getMaxLength() {
		return this.maxLength;
	}

	public void setNeighborhoodExploration(double neighborhoodExploration) {
		this.explorationLevel = neighborhoodExploration;
	}

	public double getExplorationLevel() {
		return this.explorationLevel;
	}

	/**
	 * TODO delete this method after the tests.
	 */
	public void printMOSAAnalyticalProgress() {
		System.out.println("Temperature: " + String.format("%16.6f", this.getTemperature()) + " Metropolis length: "
				+ String.format("%16.6f", this.getMetropolisLength()));
	}

	@Override
	protected boolean metropolisCycle() {
		if (metropolis++ < this.getMetropolisLength())
			return true;
//
//		if (this.getTemperature() > (this.getInitialTemperature() / 2))
//			this.setMetropolisLength(this.beta * this.getMetropolisLength());

//		if (this.getTemperature() < (this.getInitialTemperature() / 2))
		
		double tmpMetropolis = this.getMetropolisLength() * (this.beta + getMetropolisFromFuzzy(averageAmountInMetropolis));
		
		if(tmpMetropolis > this.maxLength)
			tmpMetropolis = this.maxLength;
			
		this.setMetropolisLength(tmpMetropolis);

		metropolis = 0;
		return false;
	}

	protected void updateWorstAndBestSolutions(S solution, double deltaAmount) {

		if (deltaAmount < bestLocalEnergy) {
			bestLocalEnergy = deltaAmount;
			bestSolution = solution;
		}

		if (deltaAmount > worstLocalEnergy) {
			worstLocalEnergy = deltaAmount;
			worstSolution = solution;
		}

	}

	
	protected void updateBestAndWorstAverageDominance(double avg) {
		if (avg < bestGlobalAvg) {
			bestGlobalAvg = avg;
		}

		if (avg > bestGlobalAvg) {
			worstGlobalAvg = avg;
		}

	}

	protected double getNormalizedTemperature() {

		return (this.temperature - this.getFinalTemperature())
				/ (this.getInitialTemperature() - this.getFinalTemperature());
	}

	protected double getNormalizedAmountDominance(double average) {
		// return (average - bestAvg ) / (this.worstAmount - bestAvg);
		double avgTmp = (average) / (this.worstLocalEnergy);
		if (avgTmp > 1)
			return 1;
		return avgTmp;

	}

	protected double getMetropolisFromFuzzy(double averageDominance) {

		double normTemp = getNormalizedTemperature();
		double normAvg = getNormalizedAmountDominance(averageDominance);
		Object[] results;
		try {
			results = fuzzy.fuzzyForSA(1, new double[] { normTemp, normAvg });

			MWArray result = (MWNumericArray) results[0];
			double[][] total = (double[][]) result.toArray();

			System.out.println(
					"in: " + totalAcceptedInMetropolis + " " + temperature + "(" + normTemp + ") " + averageDominance
							+ "(" + normAvg + ") " + total[0][0] + ": " + (this.getMetropolisLength() + total[0][0]));

			return total[0][0];

		} catch (MWException e) {
			e.printStackTrace();
		}

		return 0;
	}

	public void run() {

		createInitialPopulation();
		evaluatePopulation(this.archive.getSolutionList());
		initProgress();
		currentS = selectRandomSolution();

		averageAmountInMetropolis = 0.0;

		while (!isStopConditionReached()) {
			totalAmountInMetropolis = 0;
			totalAcceptedInMetropolis = 0;

			while (metropolisCycle()) {
				newS = perturbation(currentS);
				updateRange(newS);

				deltaAmountDominance = amountOfDomination(newS, currentS);
				updateWorstAndBestSolutions(newS, deltaAmountDominance);

				int flagDominance = DOMINANCE_COMPARATOR.compare(newS, currentS);

				if (flagDominance == 1) /* 1: current_pt dominates new_pt */
					currentS = caseI(newS, currentS);
				else if (flagDominance == 0) /* 0: No dominance */
					currentS = caseII(newS, currentS);
				else if (flagDominance == -1) /*-1: new_pt dominates current_pt */
					currentS = caseIII(newS, currentS);

				if (currentS == newS) {
					updateBestAndWorstGlobalSolutions(newS, deltaAmountDominance);
					totalAmountInMetropolis += deltaAmountDominance;
					totalAcceptedInMetropolis++;
				}

				updateProgress();
			}
			averageAmountInMetropolis = totalAmountInMetropolis / totalAcceptedInMetropolis;

			coolingScheme();
		}
		if (archive.size() > archive.getSoftLimit())
			archive.clustering();

	}

	private void load_FIS() {
		
	      engine = new Engine();
	      engine.setName("Probabilides-operadores");
	      
	      normAvgDominance = new InputVariable();
	      normAvgDominance.setName("Dominance");
	      normAvgDominance.setRange(0.0, 1.0);
	      normAvgDominance.addTerm(new Gaussian("LOW_ENERGY",  0.18, 0.0));
	      normAvgDominance.addTerm(new Gaussian("MEAN_ENERGY", 0.18, 0.5));
	      normAvgDominance.addTerm(new Gaussian("HIGH_ENERGY", 0.18, 1.0));
	      engine.addInputVariable(normTemperature);
	      
	      normTemperature = new InputVariable();
	      normTemperature.setName("Temperature");
	      normTemperature.setRange(0.0, 1.0);
	      normTemperature.addTerm(new Gaussian("VERY_LOW",  0.05, 0.0));
	      normTemperature.addTerm(new Gaussian("LOW", 		0.1,  0.2));
	      normTemperature.addTerm(new Gaussian("MEAN", 		0.2,  0.5));
	      normTemperature.addTerm(new Gaussian("HIGH", 		0.1,  0.8));
	      normTemperature.addTerm(new Gaussian("VERY_HIGH", 0.05, 1.0));
	      engine.addInputVariable(normTemperature);
	      
	      metropolisIncrement = new OutputVariable();
	      metropolisIncrement.setName("BetaIncrement");
	      metropolisIncrement.setRange(-0.2, 0.2);
	      metropolisIncrement.addTerm(new Gaussian("HIGH_DECREASE", -0.03, -0.2));
	      metropolisIncrement.addTerm(new Gaussian("DECREASE", 		-0.03, -0.1));
	      metropolisIncrement.addTerm(new Gaussian("HOLD", 			-0.05,  0.0));
	      metropolisIncrement.addTerm(new Gaussian("INCREASE", 		 0.03,  0.1));
	      metropolisIncrement.addTerm(new Gaussian("HIGH_INCREASE",  0.03,  0.2));
	      engine.addOutputVariable(metropolisIncrement);
	      
	      RuleBlock ruleBlock = new RuleBlock();
	      ruleBlock.addRule(Rule.parse("if Temperature is VERY_HIGH and Dominance is HIGH_ENERGY then BetaIncrement is DECREASE", engine));
	      ruleBlock.addRule(Rule.parse("if Temperature is VERY_HIGH and Dominance is MEAN_ENERGY then BetaIncrement is HIGH_DECREASE", engine));
	      ruleBlock.addRule(Rule.parse("if Temperature is VERY_HIGH and Dominance is LOW_ENERGY then BetaIncrement is HIGH_DECREASE", engine));

	      ruleBlock.addRule(Rule.parse("if Temperature is HIGH and Dominance is HIGH_ENERGY then BetaIncrement is DECREASE", engine));
	      ruleBlock.addRule(Rule.parse("if Temperature is HIGH and Dominance is MEAN_ENERGY then BetaIncrement is HIGH_DECREASE", engine));
	      ruleBlock.addRule(Rule.parse("if Temperature is HIGH and Dominance is LOW_ENERGY then BetaIncrement is HIGH_DECREASE", engine));

	      ruleBlock.addRule(Rule.parse("if Temperature is MEAN and Dominance is HIGH_ENERGY then BetaIncrement is INCREASE", engine));
	      ruleBlock.addRule(Rule.parse("if Temperature is MEAN and Dominance is MEAN_ENERGY then BetaIncrement is HOLD", engine));
	      ruleBlock.addRule(Rule.parse("if Temperature is MEAN and Dominance is LOW_ENERGY then BetaIncrement is INCREASE", engine));

	      ruleBlock.addRule(Rule.parse("if Temperature is LOW and Dominance is HIGH_ENERGY then BetaIncrement is INCREASE", engine));
	      ruleBlock.addRule(Rule.parse("if Temperature is LOW and Dominance is MEAN_ENERGY then BetaIncrement is HIGH_DECREASE", engine));
	      ruleBlock.addRule(Rule.parse("if Temperature is LOW and Dominance is LOW_ENERGY then BetaIncrement is INCREASE", engine));

	      ruleBlock.addRule(Rule.parse("if Temperature is VERY_LOW and Dominance is HIGH_ENERGY then BetaIncrement is HOLD", engine));
	      ruleBlock.addRule(Rule.parse("if Temperature is VERY_LOW and Dominance is MEAN_ENERGY then BetaIncrement is INCREASE", engine));
	      ruleBlock.addRule(Rule.parse("if Temperature is VERY_LOW and Dominance is LOW_ENERGY then BetaIncrement is INCREASE", engine));

	      engine.configure("Minimum", "Maximum", "Minimum", "Maximum", "Centroid", null);
	      
	      StringBuilder status = new StringBuilder();
	        if (!engine.isReady(status)) {
	            throw new RuntimeException("Engine not ready. "
	                    + "The following errors were encountered:\n" + status.toString());
	        }
	  }
}
