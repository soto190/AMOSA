package org.uma.jmetal.operator.scheduling.mutation;

import java.util.ArrayList;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.solution.impl.SchedulingIntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public class LoadBalancingMutation implements MutationOperator<SchedulingIntegerSolution> {

	private double mutationProbability;
	private JMetalRandom randomGenerator;

	public LoadBalancingMutation(double mutationProbability) {
		if (mutationProbability < 0)
			throw new JMetalException("Mutation probability is negative: " + mutationProbability);

		this.mutationProbability = mutationProbability;
		randomGenerator = JMetalRandom.getInstance();
	}

	/* Getter */
	public double getMutationProbability() {
		return mutationProbability;
	}

	public SchedulingIntegerSolution execute(SchedulingIntegerSolution solution) {
		if (solution == null)
			throw new JMetalException("Null parameter");

		doMutation(mutationProbability, solution);
		return solution;
	}

	private void doMutation(double mutationProbability, SchedulingIntegerSolution solution) {

		if (randomGenerator.nextDouble() < mutationProbability) {

			solution.makespanMachines.clear();

			/**
			 * Gets machines which produces makespan.
			 */
			for (int m = 0; m < solution.getTotalMachines(); m++)
				if (solution.getTimeInMachine(m) >= solution.getObjective(0))
					solution.makespanMachines.add(m);
			int m = solution.makespanMachines.get(0);
			if (solution.makespanMachines.size() > 1)
				m = solution.makespanMachines
						.get(randomGenerator.nextInt(0, solution.getMakespanMachines().size() - 1));

			ArrayList<Integer> tasks = solution.getTasksInMachine(m);
			int taskToMove = tasks.get(0);
			if (tasks.size() > 1)
				taskToMove = tasks.get(randomGenerator.nextInt(0, tasks.size() - 1));


			int	newMachine = randomGenerator.nextInt(0, solution.getProblem().getTotalMachines() - 1);
			int config = randomGenerator.nextInt(0, solution.getProblem().getMaxConfig(newMachine) - 1);

			solution.setVariableValue(taskToMove, newMachine);
			solution.setVariableValue(taskToMove + solution.getTotalTasks(), config);
		}

	}

}
