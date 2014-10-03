package it.unina.android.ripper.scheduler;

import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.planner.task.TaskList;

import java.util.Random;

public class RandomScheduler implements Scheduler {

	private long RANDOM_SEED = 1;
	
	protected TaskList taskList;
	protected Random random;
	
	public RandomScheduler(long seed)
	{
		super();
		this.taskList = new TaskList();
		this.RANDOM_SEED = seed;
		random = new Random(RANDOM_SEED);
	}
	
	@Override
	public Task nextTask() {
		if (this.taskList.size() > 0)
		{
			int pos = (int)( random.nextInt( taskList.size() ) );
			
			Task t = taskList.get(pos);
			taskList.clear();
			
			return t;
		}
		else
			return null;
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
