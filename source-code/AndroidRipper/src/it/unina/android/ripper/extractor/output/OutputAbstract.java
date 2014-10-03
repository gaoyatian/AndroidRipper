package it.unina.android.ripper.extractor.output;

import it.unina.android.ripper.model.ActivityDescription;

import java.util.ArrayList;

public abstract class OutputAbstract
{
	protected ArrayList<ActivityDescription> activityDescription;
	
	public OutputAbstract()
	{
		this.activityDescription = new ArrayList<ActivityDescription>();
	}
	
	public void addActivityDescription(ActivityDescription ad)
	{
		this.activityDescription.add(ad);
	}
	
	public ArrayList<ActivityDescription> getActivityDescriptions()
	{
		return this.activityDescription;
	}
	
	public abstract String output();
}
