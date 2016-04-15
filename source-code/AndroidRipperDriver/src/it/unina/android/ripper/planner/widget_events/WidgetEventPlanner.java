package it.unina.android.ripper.planner.widget_events;

import it.unina.android.ripper.constants.InteractionType;
import it.unina.android.ripper.model.Input;
import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.model.WidgetDescription;
import it.unina.android.ripper.planner.task.TaskList;

import java.util.ArrayList;

public class WidgetEventPlanner {

	public static class WidgetEventPlannerConfiguration {
		public boolean doClick = false;
		public boolean doLongClick = false;
		public boolean doFocus = false;
		
		public WidgetEventPlannerConfiguration() {
			super();
		}
		
		public WidgetEventPlannerConfiguration(boolean doTap, boolean doLongTap, boolean doFocus) {
			super();
			this.doClick = doTap;
			this.doLongClick = doLongTap;
			this.doFocus = doFocus;
		}
	}
	
	protected WidgetDescription mWidget;
	
	public WidgetEventPlanner(WidgetDescription widget)
	{
		super();
		this.mWidget = widget;
	}
	
	public TaskList planForWidget(Task currentTask, ArrayList<Input> inputs, String... options)
	{
		TaskList taskList = new TaskList();
		
		if ( this.canPlanForWidget() )
		{
			if (mWidget.isClickable() || mWidget.isListenerActive("OnItemClickListener"))
				taskList.addAll(tap(currentTask, inputs, options));
			
			if (mWidget.isLongClickable() || mWidget.isListenerActive("OnItemLongClickListener"))
				taskList.addAll(longTap(currentTask, inputs, options));
			
			if (mWidget.hasOnFocusChangeListener() || mWidget.isListenerActive("OnFocusListener"))
				taskList.addAll(focus(currentTask, inputs, options));
		}
		
		return taskList;
	}
	
	public TaskList planForWidget(Task currentTask, ArrayList<Input> inputs, WidgetEventPlannerConfiguration configuration, String... options)
	{
		TaskList taskList = new TaskList();
		
		if ( this.canPlanForWidget() )
		{
			if (mWidget.isClickable() || configuration.doClick)
				taskList.addAll(tap(currentTask, inputs, options));
			
			if (mWidget.isLongClickable() || configuration.doLongClick)
				taskList.addAll(longTap(currentTask, inputs, options));
			
			if (mWidget.hasOnFocusChangeListener() || configuration.doFocus)
				taskList.addAll(focus(currentTask, inputs, options));
		}
		
		return taskList;
	}
	
	public boolean canPlanForWidget()
	{
		return mWidget != null && mWidget.isEnabled() && mWidget.isVisible(); // && mWidget.getSimpleType() != null && mWidget.getSimpleType().equals("") == false;
	}
	
	protected TaskList tap(Task currentTask, ArrayList<Input> inputs, String... options)
	{
		TaskList t = new TaskList();
		t.add(new Task(currentTask, mWidget, InteractionType.CLICK, inputs));
		return t;
	}
	
	protected TaskList longTap(Task currentTask, ArrayList<Input> inputs, String... options)
	{
		TaskList t = new TaskList();
		t.add(new Task(currentTask, mWidget, InteractionType.LONG_CLICK, inputs));
		return t;
	}
	
	protected TaskList focus(Task currentTask, ArrayList<Input> inputs, String... options)
	{
		TaskList t = new TaskList();
		t.add(new Task(currentTask, mWidget, InteractionType.FOCUS, inputs));
		return t;
	}
}
