package org.uma.jmetal.problem.multiobjective;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.problem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.solution.impl.SchedulingIntegerSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import hcs.HCS;
import hcs.Machine;
import tools.Utilities;

public class SchedulingProblem extends AbstractIntegerProblem {

	private HCS hcs;
	protected final JMetalRandom randomGenerator = JMetalRandom.getInstance();;

	public SchedulingProblem(int indexInstance) {
		Utilities utils = new Utilities();
		hcs = utils.readInstance(indexInstance);
		this.setName(hcs.getName());
		this.setNumberOfObjectives(2);
		this.setNumberOfVariables(2 * hcs.getTotalTasks());

		List<Integer> lowerLimit = new ArrayList<Integer>(getNumberOfVariables());
		List<Integer> upperLimit = new ArrayList<Integer>(getNumberOfVariables());

		for (int i = 0; i < hcs.getTotalTasks(); i++) {
			lowerLimit.add(0);
			upperLimit.add(hcs.getTotalMachines() - 1);
		}

		for (int i = hcs.getTotalTasks(); i < 2 * hcs.getTotalTasks(); i++) {
			lowerLimit.add(0);
			upperLimit.add(hcs.getMaxKConfig() - 1);
		}

		this.setLowerLimit(lowerLimit);
		this.setUpperLimit(upperLimit);
	}

	public void evaluate(IntegerSolution solution) {
		evaluateScheduling((SchedulingIntegerSolution) solution);
	}

	public void evaluateScheduling(SchedulingIntegerSolution solution) {
		double makespan = 0.0;
		double energy = 0.0;

		int totalTasks = this.hcs.getTotalMachines();
		solution.resetTimeInMachines();

		int k = 0;
		for (int t = 0; t < this.hcs.getTotalTasks(); t++) {

			k = (int) solution.getVariableValue(t + totalTasks);

			Machine m = this.hcs.getMachine((int) solution.getVariableValue(t));

			if (k > m.getTotalConfig() - 1) {
				k = m.getTotalKConfig() - 1;
				solution.setVariableValue(t + this.hcs.getTotalTasks(), k);
			}

			double P = m.getExecTime(t) / m.getKspeed(k);

			solution.increaseTimeInMachine(m.getId(), P);
			solution.setTaskInMachine(t, m.getId());
			energy += P * m.getKvoltaje(k) * m.getKvoltaje(k);

			if (solution.getTimeInMachine(m.getId()) > makespan)
				makespan = solution.getTimeInMachine(m.getId());

		}

		solution.setObjective(0, makespan);
		solution.setObjective(1, energy);
	}

	public IntegerSolution createSolution() {

		SchedulingIntegerSolution solution = new SchedulingIntegerSolution(this);

		for (int i = 0; i < hcs.getTotalTasks(); i++) {
			Integer machine = randomGenerator.nextInt(getLowerBound(i), getUpperBound(i));
			Integer config = randomGenerator.nextInt(getLowerBound(i + hcs.getTotalTasks()),
					hcs.getMachine(machine).getTotalConfig() - 1);

			solution.setVariableValue(i, machine);
			solution.setVariableValue(i + hcs.getTotalTasks(), config);
		}

		for (int i = 0; i < getNumberOfObjectives(); i++) {
			solution.setObjective(i, 0.0);
		}

		return solution;
	}

	public int getTotalMachines() {
		return this.hcs.getTotalMachines();
	}

	public int getTotalTasks() {
		return this.hcs.getTotalTasks();
	}

	public int getMaxConfig(int machine) {
		return this.hcs.getMachine(machine).getTotalKConfig();
	}

	
	public IntegerSolution createTestSolution(int obj1, int obj2){
		SchedulingIntegerSolution solution = new SchedulingIntegerSolution(this);
		
		solution.setObjective(1, obj1);
		solution.setObjective(2, obj2);
		return solution;
	}
}
