package it.unina.android.ripper.planner;

import it.unina.android.ripper.model.ActivityDescription;
import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.model.TaskList;

public abstract class Planner
{
	public static int MAX_INTERACTIONS_FOR_LIST = 3;
	public static int MAX_INTERACTIONS_FOR_PREFERENCES_LIST = 9999;
	public static int MAX_INTERACTIONS_FOR_SINGLE_CHOICE_LIST = 3;
	public static int MAX_INTERACTIONS_FOR_MULTI_CHOICE_LIST = 3;
	public static int MAX_INTERACTIONS_FOR_SPINNER = 9;
	public static int MAX_INTERACTIONS_FOR_RADIO_GROUP = 9;
	
	public static boolean CAN_GO_BACK = true;
	public static boolean CAN_CHANGE_ORIENTATION = true;
	public static boolean CAN_OPEN_MENU = true;
	public static boolean CAN_SCROLL_DOWN = false;
	public static boolean CAN_GENERATE_KEY_PRESS_EVENTS = false;
	public static boolean CAN_GENERATE_LONG_KEY_PRESS_EVENTS = false;
	public static boolean CAN_SWAP_TAB = true;
	
	public static boolean CAN_GO_BACK_ON_HOME_ACTIVITY = true;
	
	public TaskList plan(Task currentTask, ActivityDescription activity) {
		return plan(currentTask ,activity, null);
	}
	
	public abstract TaskList plan(Task currentTask, ActivityDescription activity, String ... options);
}
