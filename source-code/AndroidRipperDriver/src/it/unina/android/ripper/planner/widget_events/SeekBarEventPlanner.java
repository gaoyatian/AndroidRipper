package it.unina.android.ripper.planner.widget_events;

import it.unina.android.ripper.constants.InteractionType;
import it.unina.android.ripper.model.Input;
import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.model.WidgetDescription;
import it.unina.android.ripper.planner.task.TaskList;
import it.unina.android.ripper.planner.widget_inputs.values_generator.ValuesGenerator;

import java.util.ArrayList;

public class SeekBarEventPlanner extends WidgetEventPlanner {
	
	ValuesGenerator mValuesGenerator;
	
	public SeekBarEventPlanner(WidgetDescription widgetDescription, ValuesGenerator valuesGenerator) {
		super(widgetDescription);
		this.mValuesGenerator = valuesGenerator; 
	}
	
	@Override
	protected TaskList tap(Task currentTask, ArrayList<Input> inputs, String... options)
	{
		TaskList t = new TaskList();
		t.add(new Task(currentTask, mWidget, InteractionType.SET_BAR, inputs, mValuesGenerator.generate()));
		return t;
	}

}
