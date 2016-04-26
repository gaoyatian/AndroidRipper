package it.unina.android.ripper.scheduler;

import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.model.TaskList;

public class LimitedDepthBreadthScheduler implements Scheduler {

	private TaskList taskList;
	private int limit = 0;	
	
	public LimitedDepthBreadthScheduler(int limit)
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
		this.taskList.add(t);
	}

	@Override
	public void addTasks(TaskList taskList) {
		this.taskList.addAll(taskList);
	}

	@Override
	public TaskList getTaskList() {
		return this.taskList;
	}

}
