package it.unina.android.ripper.planner;

import it.unina.android.ripper.constants.SimpleType;
import it.unina.android.ripper.model.Input;
import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.model.TaskList;
import it.unina.android.ripper.model.WidgetDescription;
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
	
	public static String[] configuredEventWidgetList = {
			SimpleType.BUTTON
			,SimpleType.MENU_ITEM
			,SimpleType.TEXT_VIEW
			//,SimpleType.LINEAR_LAYOUT
			//,SimpleType.RELATIVE_LAYOUT
			,SimpleType.CHECKBOX
			,SimpleType.TOGGLE_BUTTON
			,SimpleType.NUMBER_PICKER_BUTTON
			,SimpleType.IMAGE_VIEW
			,SimpleType.WEB_VIEW
			,SimpleType.PREFERENCE_LIST
			,SimpleType.LIST_VIEW
			,SimpleType.SINGLE_CHOICE_LIST
			,SimpleType.MULTI_CHOICE_LIST
			,SimpleType.SPINNER
			,SimpleType.RADIO_GROUP
			,SimpleType.SEEK_BAR
			,SimpleType.RATING_BAR
		};
	
	protected boolean isEventWidgetConfiguredForInteraction(WidgetDescription widget)
	{
		for (String s : configuredEventWidgetList)
			if(s.equals(widget.getSimpleType()))
				return true;

		return false;
	}
	
	public ConfigurationBasedPlanner() {
		super();
		
		this.eventConfiguration = new HashMap<String, WidgetEventPlanner.WidgetEventPlannerConfiguration>();
		
		this.eventConfiguration.put(SimpleType.BUTTON, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
		this.eventConfiguration.put(SimpleType.MENU_ITEM, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
		this.eventConfiguration.put(SimpleType.TEXT_VIEW, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
		//this.eventConfiguration.put(SimpleType.LINEAR_LAYOUT, new WidgetEventPlanner.WidgetEventPlannerConfiguration(false, false, false));
		//this.eventConfiguration.put(SimpleType.RELATIVE_LAYOUT, new WidgetEventPlanner.WidgetEventPlannerConfiguration(false, false, false));
		this.eventConfiguration.put(SimpleType.CHECKBOX, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
		this.eventConfiguration.put(SimpleType.TOGGLE_BUTTON, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
		this.eventConfiguration.put(SimpleType.NUMBER_PICKER_BUTTON, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
		
		this.eventConfiguration.put(SimpleType.IMAGE_VIEW, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, true, false));
		this.eventConfiguration.put(SimpleType.WEB_VIEW, new WidgetEventPlanner.WidgetEventPlannerConfiguration(false, true, false));
		
		this.eventConfiguration.put(SimpleType.PREFERENCE_LIST, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
		
		this.eventConfiguration.put(SimpleType.LIST_VIEW, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, true, false));
		this.eventConfiguration.put(SimpleType.SINGLE_CHOICE_LIST, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, true, false));
		this.eventConfiguration.put(SimpleType.MULTI_CHOICE_LIST, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, true, false));
		
		this.eventConfiguration.put(SimpleType.SPINNER, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
		this.eventConfiguration.put(SimpleType.RADIO_GROUP, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
		this.eventConfiguration.put(SimpleType.SEEK_BAR, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
		this.eventConfiguration.put(SimpleType.RATING_BAR, new WidgetEventPlanner.WidgetEventPlannerConfiguration(true, false, false));
	}

	@Override
	protected TaskList planForWidget(Task currentTask, WidgetDescription widgetDescription, ArrayList<Input> inputs, String... options)
	{
		//excludes widgets used as input and not enabled or not visible widgets
		if (	isInputWidget(widgetDescription) == false &&
				isEventWidgetConfiguredForInteraction(widgetDescription) == true &&
				(widgetDescription.isEnabled() && widgetDescription.isVisible())
		)
		{
			WidgetEventPlanner widgetEventPlanner = null;
			
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
				widgetEventPlanner =  new RadioGroupEventPlanner(widgetDescription, MAX_INTERACTIONS_FOR_RADIO_GROUP);
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
				widgetEventPlanner =  new WidgetEventPlanner(widgetDescription);
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
