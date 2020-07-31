package net.runelite.client.plugins.nmzhelper;

import java.util.Arrays;
import java.util.TreeSet;

public class TaskSet extends TreeSet<Task>
{
	/**
	 * Default constructor that initializes the comparator and sets the stop condition to none.
	 */
	public TaskSet() {
		super((o1, o2) -> Integer.compare(o2.priority(), o1.priority()) < 0 ? -1 : 1);
	}

	/**
	 * Constructs a task set and adds all the tasks in the array to the set.
	 * Tasks are sorted by priority in descending order.
	 * @param tasks The tasks to be added.
	 */
	public TaskSet(Task... tasks) {
		super((o1, o2) -> Integer.compare(o2.priority(), o1.priority()) < 0 ? -1 : 1);
		addAll(tasks);
	}

	/**
	 * Adds all the tasks in the array to the set.
	 * @param tasks The tasks to be added.
	 * @return True if the set changed, false otherwise.
	 */
	public boolean addAll(Task... tasks) {
		return super.addAll(Arrays.asList(tasks));
	}

	/**
	 * Iterates through all the tasks in the set and returns
	 * the highest priority valid task.
	 * @return The first valid task from the task list or null if no valid task.
	 */
	public Task getValidTask() {
		for (Task task : this)
			if (task.validate())
				return task;
		return null;
	}
}
