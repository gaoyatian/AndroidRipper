package it.unina.android.ripper.planner;

import it.unina.android.ripper.model.ActivityDescription;
import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.planner.task.TaskList;

public abstract class Planner
{
	public TaskList plan(Task currentTask, ActivityDescription activity) {
		return plan(currentTask ,activity, null);
	}
	
	public abstract TaskList plan(Task currentTask, ActivityDescription activity, String ... options);
}
