package it.unina.android.ripper.states;

import it.unina.android.ripper.comparator.IComparator;
import it.unina.android.ripper.model.ActivityDescription;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class ActivityStateList extends ArrayList<ActivityDescription> implements Serializable
{
	IComparator comparator = null;

	int lastActivityId = 0;
	
	public ActivityStateList(IComparator comparator)
	{
		super();
		this.comparator = comparator;
	}

	public String containsActivity(ActivityDescription activity1)
	{
		for (ActivityDescription activity2 : this)
		{
			if (activity1 == null || activity2 == null)
				continue;
			
			if ((Boolean)this.comparator.compare(activity1, activity2))
				return activity2.getId();
		}
				
		return null;
	}
	
	public String getEquivalentActivityStateId(ActivityDescription activity1)
	{
		return this.containsActivity(activity1);
	}
	
	public boolean addActivity(ActivityDescription a)
	{
		a.setId("a"+(++lastActivityId));
		return this.add(a);
	}
	
	public ActivityDescription getLatestAdded()
	{
		if (this.size() > 0)
			return this.get(this.size() - 1);
		else
			return null;
	}
	
	public void saveToFile(String fileName) {
		try {
			FileOutputStream fileOut = new FileOutputStream(fileName);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(this);
			out.close();
			fileOut.close();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static ActivityStateList loadFromFile(String fileName) {
		ActivityStateList t = null;
		
		try {
			FileInputStream fileIn = new FileInputStream(fileName);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			t = (ActivityStateList) in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return t;
	}

	@Override
	public boolean add(ActivityDescription e) {
		boolean added = super.add(e);
		
		if (added) {
			this.saveToFile("current_ActivityStateList.bin");
		}
		
		return added;
	}
}
