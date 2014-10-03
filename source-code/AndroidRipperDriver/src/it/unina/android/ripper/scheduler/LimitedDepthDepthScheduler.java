package it.unina.android.ripper.scheduler;

import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.planner.task.TaskList;

public class LimitedDepthDepthScheduler implements Scheduler {

	private TaskList taskList;
	private int limit = 0;	
	
	public LimitedDepthDepthScheduler(int limit)
	{
		super();
		this.taskList = new TaskList();
	}
	
	@Override
	public Task nextTask() {
		if (this.taskList.size() > 0)
		{
			Task t = null;
			
			//todo: TESTING
			do
			{
				t = taskList.remove(0);
			}
			while (t.size() > limit && t != null);
			
			return t;
		}
		else
		{
			return null;
		}
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