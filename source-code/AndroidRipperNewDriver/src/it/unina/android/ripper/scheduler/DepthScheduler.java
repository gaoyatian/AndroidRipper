package it.unina.android.ripper.scheduler;

import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.planner.task.TaskList;

public class DepthScheduler implements Scheduler {

	private TaskList taskList;
	
	public DepthScheduler()
	{
		super();
		this.taskList = new TaskList();
	}
	
	@Override
	public Task nextTask() {
		if (this.taskList.size() > 0)
			return this.taskList.remove(0);
		else
			return null;
	}

	@Override
	public void addTask(Task t) {
		this.taskList.add(0, t);
	}

	@Override
	public void addTasks(TaskList taskList) {
		this.taskList.addAll(0, taskList);
	}

	@Override
	public TaskList getTaskList() {
		return this.taskList;
	}

}
