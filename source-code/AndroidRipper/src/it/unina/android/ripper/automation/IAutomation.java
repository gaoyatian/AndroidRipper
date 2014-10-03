package it.unina.android.ripper.automation;

import it.unina.android.ripper.automation.robot.IRobot;
import android.app.Activity;

public interface IAutomation {

	public abstract void fireEvent(String widgetId, Integer widgetIndex,
			String widgetName, String widgetType, String eventType, String value);

	public abstract void fireEvent(int widgetId, int widgetIndex,
			String widgetType, String eventType);

	public abstract void fireEvent(int widgetId, int widgetIndex,
			String widgetName, String widgetType, String eventType);

	public abstract void fireEvent(int widgetIndex, String widgetName,
			String widgetType, String eventType);

	public abstract void fireEvent(int widgetId, int widgetIndex,
			String widgetName, String widgetType, String eventType, String value);

	public abstract void fireEvent(int widgetIndex, String widgetName,
			String widgetType, String eventType, String value);

	public abstract void fireEvent (String widgetName, String widgetType, String eventType, String value);
	
	public abstract void setInput(int widgetId, String interactionType, String value);

	public abstract void restart();

	public abstract void waitOnThrobber();

	public abstract void setActivityOrientation(int orientation);

	public abstract void sleep(int time);

	public abstract Activity getCurrentActivity();

	public abstract void finalizeRobot() throws Throwable;

	public IRobot getRobot();
}