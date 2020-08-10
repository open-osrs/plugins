package net.runelite.client.plugins.nmzhelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.runelite.api.Client;

public class TaskSet
{
	public List<Task> taskList = new ArrayList<>();

	public TaskSet(Task... tasks)
	{
		taskList.addAll(Arrays.asList(tasks));
	}

	public void addAll(Task... tasks)
	{
		taskList.addAll(Arrays.asList(tasks));
	}

	public TaskSet(Client client, NMZHelperConfig config, Task... tasks)
	{
		taskList.addAll(Arrays.asList(tasks));
		verifyClient(client);
		verifyConfig(config);
	}

	public void addAll(Client client, NMZHelperConfig config, Task... tasks)
	{
		taskList.addAll(Arrays.asList(tasks));
		verifyClient(client);
		verifyConfig(config);
	}

	public void clear()
	{
		taskList.clear();
	}

	public void verifyClient(Client client)
	{
		if (client == null)
		{
			return;
		}

		for (Task task : taskList)
		{
			if (task.client == null)
			{
				task.client = client;
			}
		}
	}

	public void verifyConfig(NMZHelperConfig config)
	{
		if (config == null)
		{
			return;
		}

		for (Task task : taskList)
		{
			if (task.config == null)
			{
				task.config = config;
			}
		}
	}

	/**
	 * Iterates through all the tasks in the set and returns
	 * the highest priority valid task.
	 * @return The first valid task from the task list or null if no valid task.
	 */
	public Task getValidTask()
	{
		for (Task task : this.taskList)
		{
			if (task.validate())
			{
				return task;
			}
		}
		return null;
	}
}
