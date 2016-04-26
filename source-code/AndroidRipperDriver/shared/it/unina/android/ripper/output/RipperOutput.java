package it.unina.android.ripper.output;

import it.unina.android.ripper.model.ActivityDescription;
import it.unina.android.ripper.model.Event;
import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.model.TaskList;
import it.unina.android.ripper.model.WidgetDescription;

public interface RipperOutput {
	public String outputActivityDescription(ActivityDescription a);
	public String outputActivityDescriptionAndPlannedTasks(ActivityDescription a, TaskList t);
	public String outputWidgetDescription(WidgetDescription a);
	public String outputEvent(Event a);
	public String outputFiredEvent(Event evt);
	public String outputTask(Task a);
	public String outputExtractedEvents(TaskList a);
	public String outputExtractedEvents(TaskList t, ActivityDescription from);
	public String outputStep(Event e, ActivityDescription a);
	public String outputStepAndPlannedTasks(Event e, ActivityDescription a, TaskList t);
	public String outputFirstStep(ActivityDescription ad, TaskList t);
}
