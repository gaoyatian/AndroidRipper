package it.unina.android.ripper.scheduler;

import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.model.TaskList;

public interface Scheduler {
	public Task nextTask();
	public void addTask(Task t);
	public void addTasks(TaskList taskList);
	public TaskList getTaskList();
}
