package it.unina.android.ripper.comparator;

import it.unina.android.ripper.constants.SimpleType;
import it.unina.android.ripper.model.ActivityDescription;
import it.unina.android.ripper.model.WidgetDescription;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenericComparator implements IComparator, Serializable {

	/* BASIC DEBUG FUNCTIONS ;-) */
	public static boolean DEBUG = true;
	public static final String TAG = "GenericComparator";
	public static void debug(String s) { if (DEBUG)	System.out.println("["+TAG+"]"+s); }
	public static void debug(boolean condition, String s) { if (DEBUG && condition)	System.out.println("["+TAG+"]"+s); }
	/* END OF BASIC DEBUG FUNCTIONS ;-) */
	
	GenericComparatorConfiguration config = null;
	
	public GenericComparator()
	{
		super();
		this.config = GenericComparatorConfiguration.Factory.getDefaultComparator();
	}

	public GenericComparator(GenericComparatorConfiguration config)
	{
		super();
		this.config = config;
	}
	
	@Override
	public Object compare(ActivityDescription activity1, ActivityDescription activity2)
	{
		//TODO: parameters validation ;-)
		
		//filter widget if needed
		ArrayList<WidgetDescription> filteredWidgets1 = new ArrayList<WidgetDescription>();
		ArrayList<WidgetDescription> filteredWidgets2 = new ArrayList<WidgetDescription>();
		if (
					config.testIfFilteredWidgetsMatch
				||	config.compareActivityFilteredWidgetsCount
		)
		{
			List<String> wList = Arrays.asList(config.filteredWidgetsArray);
			filteredWidgets1 = this.filterWidgets(activity1, wList);
			filteredWidgets2 = this.filterWidgets(activity2, wList);
		}
		
		
		
		//compare activity names		
		if (config.compareActivityNames)
		{
			 
			 if (		(activity1.getName() != null && activity2.getName() == null) 	
					 ||	(activity1.getName() == null && activity2.getName() != null)
					 ||	(activity1.getName() != null && activity2.getName() != null && activity1.getName().equals(activity2.getName()) == false))
			 {			
				debug("compare activity names -> false");
				return false;
			 }
		}
		debug(config.compareActivityNames, "compare activity names -> true");
			
		
		
		//compare activity titles
		if (config.compareActivityTitles)
		{
			 if (		(activity1.getTitle() != null && activity2.getTitle() == null) 	
					 ||	(activity1.getTitle() == null && activity2.getTitle() != null)
					 ||	(activity1.getTitle() != null && activity2.getTitle() != null && activity1.getTitle().equals(activity2.getTitle()) == false))
			 {			
				debug("compare activity titles -> false");
				return false;
			 }
		}
		debug(config.compareActivityTitles, "compare activity titles -> true");
		
		
		
		//compare activity classes
		if (config.compareActivityClasses)
		{
			String className1 = null;
			String className2 = null;
			
			if (activity1.getActivityClass() != null)
				className1 = activity1.getActivityClass().getCanonicalName();
			else if (activity1.getClassName() != null)
				className1 = activity1.getClassName();
			else
				className1 = null;

			debug("className1 = " + ((className1 == null)?"null":className1) );
			
			if (activity2.getActivityClass() != null)
				className2 = activity2.getActivityClass().getCanonicalName();
			else if (activity2.getClassName() != null)
				className2 = activity2.getClassName();
			else
				className2 = null;
			
			debug("className2 = " + ((className2 == null)?"null":className2) );
			
			if (
						(className1 != null && className2 == null)
					||	(className1 == null && className2 != null)
					||	(className1 != null && className2 != null && className1.equals(className2) == false)
			)
			{
				debug("compare activity classes -> false");
				return false;
			}
			debug("compare activity classes -> true");
		}
		
		
		
		//compare activity widget count
		if (config.compareActivityWidgetsCount && activity1.getWidgets().size() != activity2.getWidgets().size())
		{
			debug("compare activity widget count -> false");
			return false;
		}
		debug(config.compareActivityWidgetsCount, "compare activity widget count -> true");
		
		
		
		//compare activity FILTERED widget count
		if (config.compareActivityFilteredWidgetsCount && filteredWidgets1.size() != filteredWidgets2.size())
		{
			debug("compare activity FILTERED widget count -> false");
			return false;
		}
		debug(config.compareActivityWidgetsCount, "compare activity FILTERED widget count -> true");
		
		
		
		//test if widgets match
		if (config.testIfWidgetsMatch)
		{
			ArrayList<WidgetDescription> widgets1 = activity1.getWidgets();
			ArrayList<WidgetDescription> widgets2 = activity2.getWidgets();
			
			if (testIfWidgetsListMatch(widgets1, widgets2) == false)
			{
				debug("test if widgets match -> false");
				return false;
			}
		}
		debug(config.testIfWidgetsMatch, "test if widgets match -> true");
		
		
		
		//test if FILTERED widgets match
		if (config.testIfFilteredWidgetsMatch)
		{
			if (testIfWidgetsListMatch(filteredWidgets1, filteredWidgets2) == false)
			{
				debug("test if FILTERED widgets match -> false");
				return false;
			}
		}
		debug(config.testIfFilteredWidgetsMatch, "test if FILTERED widgets match -> true");
		
		
		
		
		return true;
	}

	private ArrayList<WidgetDescription> filterWidgets(ActivityDescription activity, List<String> filteredWidgets)
	{	
		ArrayList<WidgetDescription> ret = new ArrayList<WidgetDescription>();
		
		for (WidgetDescription wd : activity.getWidgets())
			if (filteredWidgets.contains(wd.getSimpleType()))
				ret.add(wd);
			
		return ret;
	}
	
	protected boolean matchWidget(WidgetDescription w1, WidgetDescription w2)
	{
		if (config.compareWidgetIds && w1.getId() != w1.getId())
		{
			debug("compare widget id -> false");
			return false;
		}
		debug(config.compareWidgetIds, "compare widget id -> true");
		
		
		
		if (config.compareWidgetSimpleType && w1.getSimpleType().equals(w2.getSimpleType()) == false)
		{
			debug("compare widget simple type -> false");
			return false;
		}
		debug(config.compareWidgetSimpleType, "compare widget simple type -> true");
		
		
		
		
		if (config.testWidgetVisibilityChange && w1.isVisible() == w2.isVisible())
		{
			debug("test widget visibility change -> false");
			return false;
		}
		debug(config.testWidgetVisibilityChange, "test widget visibility change -> true");
		
		
		
		
		if (config.testWidgetEnablingChange && w1.isVisible() == w2.isVisible())
		{
			debug("test widget enabling change -> false");
			return false;
		}
		debug(config.testWidgetEnablingChange, "test widget enabling change -> true");

		
		
		
		if (w1.getSimpleType().equals(SimpleType.LIST_VIEW) && w2.getSimpleType().equals(SimpleType.LIST_VIEW))
		{		
			if (config.compareListItemCount &&  w1.getCount() != w2.getCount())
			{
				debug("compare list item count -> false");
				return false;
			}
			debug(config.compareListItemCount, "compare list item count -> true");
			
			
			if (config.testIfBothListHaveAtLeastOneElement &&  w1.getCount() >= 1 && w2.getCount() >= 1)
			{
				debug("testIfBothListHaveAtLeastOneElement -> false");
				return false;
			}
			debug(config.testIfBothListHaveAtLeastOneElement, "testIfBothListHaveAtLeastOneElement -> true");
			
			
			if (config.testIfBothListHaveMinusThanAFixedNumberOfElements &&  w1.getCount() <= config.fixedNumberOfListElements && w2.getCount() <= config.fixedNumberOfListElements)
			{
				debug("testIfBothListHaveMinusThanAFixedNumberOfElements -> false");
				return false;
			}
			debug(config.testIfBothListHaveMinusThanAFixedNumberOfElements, "testIfBothListHaveMinusThanAFixedNumberOfElements -> true");
		}
			
		
		
		
		if (w1.getSimpleType().equals(SimpleType.MENU_VIEW) && w2.getSimpleType().equals(SimpleType.MENU_VIEW))
		{
			if (config.compareMenuItemCount && w1.getCount() != w2.getCount())
			{
				debug("compare menu item count -> false");
				return false;
			}
			debug(config.compareMenuItemCount, "compare menu item count -> true");
		}
		
		
		
		
		return true;
	}
	
	protected boolean testIfWidgetsListMatch(ArrayList<WidgetDescription> widgets1, ArrayList<WidgetDescription> widgets2)
	{
		ArrayList<WidgetDescription> checkedAlready = new ArrayList<WidgetDescription>()
		{
			@Override
			public boolean contains(Object o)
			{
				if (o == null && WidgetDescription.class.isInstance(o) == false)
					return false;
				
				return lookFor((WidgetDescription)o, this);
			}
			
		};
		
		//pass1
		for (WidgetDescription w1 : widgets1)
		{
			//if (checkedAlready.contains(w1) == false) {
				if(lookFor(w1, widgets2) == false)
				{
					debug("lookFor(w1, widgets2) no");
					return false;
				}
				checkedAlready.add(w1);
			//}
		}
		
		//pass2
		for (WidgetDescription w2 : widgets2)
		{
			if (checkedAlready.contains(w2) == false) {
				if(lookFor(w2, widgets1) == false)
				{
					debug("lookFor(w2, widgets1) no");
					return false;
				}
			}
		}
	
		return true;
	}
	
	private boolean lookFor(WidgetDescription w1, ArrayList<WidgetDescription> widgets)
	{
		for (WidgetDescription w2 : widgets)
			if (matchWidget(w1, w2))
				return true;
		
		return false;
	}
}
