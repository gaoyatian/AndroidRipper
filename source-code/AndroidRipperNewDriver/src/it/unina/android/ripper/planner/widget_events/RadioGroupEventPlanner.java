package it.unina.android.ripper.planner.widget_events;

import it.unina.android.ripper.constants.InteractionType;
import it.unina.android.ripper.model.Input;
import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.model.WidgetDescription;
import it.unina.android.ripper.planner.task.TaskList;

import java.util.ArrayList;

public class RadioGroupEventPlanner extends WidgetEventPlanner {

	int MAX_INTERACTIONS = 3;
	
	public RadioGroupEventPlanner(WidgetDescription widgetDescription, int MAX_INTERACTIONS) {
		super(widgetDescription);
		this.MAX_INTERACTIONS = MAX_INTERACTIONS;
	}
	
	@Override
	protected TaskList tap(Task currentTask, ArrayList<Input> inputs, String... options) {
		TaskList t = new TaskList();
		int count = mWidget.getCount() != null?mWidget.getCount():0;
		for (int i = 1; i <= Math.min(count, MAX_INTERACTIONS); i++)
			t.addNewTaskForWidget(currentTask, mWidget, InteractionType.RADIO_SELECT, inputs, Integer.toString(i));
		return t;
	}

}
