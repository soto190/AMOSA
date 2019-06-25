package org.uma.jmetal.solution.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.uma.jmetal.problem.IntegerProblem;

import org.uma.jmetal.problem.multiobjective.SchedulingProblem;
import org.uma.jmetal.solution.IntegerSolution;

public class SchedulingIntegerSolution extends AbstractGenericSolution<Integer, IntegerProblem>
		implements IntegerSolution {

	public List<Integer> makespanMachines;
	public List<ArrayList<Integer>> tasksInMachine;
	private double[] timeInMachine;

	public SchedulingIntegerSolution(SchedulingProblem problem) {
		super(problem);

		//overallConstraintViolationDegree = 0.0;
		//numberOfViolatedConstraints = 0;

		makespanMachines = new ArrayList<Integer>();
		tasksInMachine = new ArrayList<ArrayList<Integer>>();

		timeInMachine = new double[problem.getTotalMachines()];
		for (int i = 0; i < problem.getTotalMachines(); i++)
			tasksInMachine.add(new ArrayList<Integer>());

	}

	public SchedulingIntegerSolution(SchedulingIntegerSolution solution) {
		super(solution.problem);

		for (int i = 0; i < problem.getNumberOfVariables(); i++) {
			setVariableValue(i, solution.getVariableValue(i));
		}

		for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
			setObjective(i, solution.getObjective(i));
		}

		// tasksInMachine = (List<ArrayList<Integer>>) solution.copy();
		this.timeInMachine = new double[solution.timeInMachine.length];
		this.tasksInMachine = new ArrayList<ArrayList<Integer>>(); 

		for (int m = 0; m < solution.tasksInMachine.size(); m++) {
			this.tasksInMachine.add(new ArrayList<Integer>());
			ArrayList<Integer> list = solution.getTasksInMachine(m);

			for (int t = 0; t < list.size(); t++)
				this.setTaskInMachine(list.get(t), m);

		}

		for (int i = 0; i < timeInMachine.length; i++)
			this.timeInMachine[i] = solution.getTimeInMachine(i);

		//overallConstraintViolationDegree = solution.overallConstraintViolationDegree;
		//numberOfViolatedConstraints = solution.numberOfViolatedConstraints;

		attributes = new HashMap<Object, Object>(solution.attributes);

		makespanMachines = new ArrayList<Integer>();

	}

	public String getVariableValueString(int index) {
		return getVariableValue(index).toString();
	}

	public SchedulingIntegerSolution copy() {
		return new SchedulingIntegerSolution(this);
	}

	public Integer getLowerBound(int index) {
		return this.problem.getLowerBound(index);
	}

	public Integer getUpperBound(int index) {
		return this.problem.getUpperBound(index);
	}

	public void setTimeInMachine(int idMachine, double time) {
		this.timeInMachine[idMachine] = time;
	}

	public void increaseTimeInMachine(int idMachine, double time) {
		this.timeInMachine[idMachine] += time;
	}

	public double getTimeInMachine(int idMachine) {
		return this.timeInMachine[idMachine];
	}

	public SchedulingProblem getProblem() {
		return (SchedulingProblem) this.problem;
	}

	public int getTotalMachines() {
		return this.timeInMachine.length;
	}

	public int getTotalTasks(){
		return this.getProblem().getTotalTasks();
	}
	public void setTaskInMachine(int task, int machine) {
		this.tasksInMachine.get(machine).add(task);
	}

	public ArrayList<Integer> getTasksInMachine(int machine) {
		return this.tasksInMachine.get(machine);
	}

	public List<Integer> getMakespanMachines() {
		return this.makespanMachines;
	}
	
	public void resetTimeInMachines(){
		this.timeInMachine = new double[timeInMachine.length];	
	}
}
