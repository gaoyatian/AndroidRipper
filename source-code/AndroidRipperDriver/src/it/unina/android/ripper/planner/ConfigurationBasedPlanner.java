package it.unina.android.ripper.planner;

import it.unina.android.ripper.constants.SimpleType;
import it.unina.android.ripper.model.Input;
import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.model.WidgetDescription;
import it.unina.android.ripper.planner.task.TaskList;
import it.unina.android.ripper.planner.widget_events.ListViewEventPlanner;
import it.unina.android.ripper.planner.widget_events.RadioGroupEventPlanner;
import it.unina.android.ripper.planner.widget_events.SeekBarEventPlanner;
import it.unina.android.ripper.planner.widget_events.SpinnerEventPlanner;
import it.unina.android.ripper.planner.widget_events.TextViewEventPlanner;
import it.unina.android.ripper.planner.widget_events.WidgetEventPlanner;
import it.unina.android.ripper.planner.widget_inputs.values_generator.RandomNumericValuesGenerator;

import java.util.ArrayList;
import java.util.HashMap;

public class ConfigurationBasedPlanner extends HandlerBasedPlanner
{	
	HashMap<String, WidgetEventPlanner.WidgetEventPlannerConfiguration> eventConfiguration;
	
	public ConfigurationBasedPlanner() {
		super();
		this.eventConfiguration = new HashMap<String, WidgetEventPlanner.WidgetEventPlannerConfiguration>();
		
		this.eventConfiguration.put(SimpleType.LIST_VIEW, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, true, false));
		this.eventConfiguration.put(SimpleType.PREFERENCE_LIST, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
		this.eventConfiguration.put(SimpleType.SINGLE_CHOICE_LIST, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, true, false));
		this.eventConfiguration.put(SimpleType.MULTI_CHOICE_LIST, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, true, false));
		this.eventConfiguration.put(SimpleType.MULTI_CHOICE_LIST, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, true, false));
		this.eventConfiguration.put(SimpleType.SPINNER, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, true, false));
		this.eventConfiguration.put(SimpleType.RADIO_GROUP, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, true, false));
		this.eventConfiguration.put(SimpleType.TEXT_VIEW, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
		this.eventConfiguration.put(SimpleType.SEEK_BAR, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, true, false));
		this.eventConfiguration.put(SimpleType.RATING_BAR, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, true, false));
	}

	@Override
	protected TaskList planForWidget(Task currentTask, WidgetDescription widgetDescription, ArrayList<Input> inputs, String... options)
	{
		//excludes widgets used as input
		if (isInputWidget(widgetDescription) == false)
		{
			WidgetEventPlanner widgetEventPlanner;
			
			//expandmenu == list
			//numberpicker -> click?
			//auto_complete_text
			//search_bar
			
			if (widgetDescription.getSimpleType().equals(SimpleType.LIST_VIEW))
			{
				widgetEventPlanner = new ListViewEventPlanner(widgetDescription, MAX_INTERACTIONS_FOR_LIST);
			}
			else if (widgetDescription.getSimpleType().equals(SimpleType.PREFERENCE_LIST))
			{
				widgetEventPlanner = new ListViewEventPlanner(widgetDescription, MAX_INTERACTIONS_FOR_PREFERENCES_LIST);
			}
			else if (widgetDescription.getSimpleType().equals(SimpleType.SINGLE_CHOICE_LIST))
			{
				widgetEventPlanner =  new ListViewEventPlanner(widgetDescription, MAX_INTERACTIONS_FOR_SINGLE_CHOICE_LIST);
			}
			else if (widgetDescription.getSimpleType().equals(SimpleType.MULTI_CHOICE_LIST))
			{
				widgetEventPlanner =  new ListViewEventPlanner(widgetDescription, MAX_INTERACTIONS_FOR_MULTI_CHOICE_LIST);
			}
			else if (widgetDescription.getSimpleType().equals(SimpleType.SPINNER))
			{
				widgetEventPlanner =  new SpinnerEventPlanner(widgetDescription, MAX_INTERACTIONS_FOR_SPINNER);
			}
			else if (widgetDescription.getSimpleType().equals(SimpleType.RADIO_GROUP))
			{
				widgetEventPlanner =  new RadioGroupEventPlanner(widgetDescription, MAX_INTERACTIONS_FOR_SPINNER);
			}
			else if (widgetDescription.getSimpleType().equals(SimpleType.TEXT_VIEW))
			{
				widgetEventPlanner =  new TextViewEventPlanner(widgetDescription);
			}
			else if (widgetDescription.getSimpleType().equals(SimpleType.SEEK_BAR) || widgetDescription.getSimpleType().equals(SimpleType.RATING_BAR))
			{
				RandomNumericValuesGenerator randomValuesGenerator = new RandomNumericValuesGenerator(0,99);
				widgetEventPlanner = new SeekBarEventPlanner(widgetDescription, randomValuesGenerator);
			}
			else
			{
				//widgetEventPlanner =  new WidgetEventPlanner(widgetDescription);
				return null;
			}
			
			WidgetEventPlanner.WidgetEventPlannerConfiguration config = eventConfiguration.get(widgetDescription.getSimpleType());
			if (config == null)
				config = new WidgetEventPlanner.WidgetEventPlannerConfiguration();
			
			return widgetEventPlanner.planForWidget(
					currentTask,
					inputs,
					config,
					options
			);
		}
		else
		{
			return null; //widget is an input
		}
	}
}
