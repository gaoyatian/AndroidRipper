package it.unina.android.ripper.planner.widget_events;

import it.unina.android.ripper.constants.InteractionType;
import it.unina.android.ripper.model.Input;
import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.model.WidgetDescription;
import it.unina.android.ripper.planner.task.TaskList;

import java.util.ArrayList;

public class TextViewEventPlanner extends WidgetEventPlanner {
	
	public TextViewEventPlanner(WidgetDescription widgetDescription) {
		super(widgetDescription);
	}
	
	@Override
	protected TaskList tap(Task currentTask, ArrayList<Input> inputs, String... options)
	{
		TaskList t = new TaskList();
		t.add(new Task(currentTask, mWidget, InteractionType.CLICK_ON_TEXT, inputs));
		return t;
	}

}
