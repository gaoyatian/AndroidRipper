package it.unina.android.ripper.comparator;

import it.unina.android.ripper.constants.SimpleType;

/**
 * GenericComparator configuration class
 * 
 * @author Testing
 *
 */
public class GenericComparatorConfiguration
{
	/**
	 * This class contains simple comparator configurations
	 * 
	 * @author Testing
	 *
	 */
	public static class Factory
	{
		/**
		 * NameComparator
		 * 
		 * @return
		 */
		public static GenericComparatorConfiguration getNameComparator()
		{
			GenericComparatorConfiguration ret = new GenericComparatorConfiguration();
			
			ret.compareActivityNames = true;
			ret.compareActivityTitles = true;
			
			return ret;
		}
		
		/**
		 * NullComparator
		 * 
		 * @return
		 */
		public static GenericComparatorConfiguration getDefaultComparator()
		{
			return new GenericComparatorConfiguration();
		}
		
		/**
		 * CustomWidgetSimpleComparator
		 * 
		 * @return
		 */
		public static GenericComparatorConfiguration getCustomWidgetSimpleComparator()
		{
			GenericComparatorConfiguration ret = new GenericComparatorConfiguration();
			
			ret.compareActivityNames = false;
			ret.compareActivityTitles = true;
			
			ret.testIfFilteredWidgetsMatch = true;
			
			String[] filteredWidgetsArray = { 
					SimpleType.EDIT_TEXT,
					SimpleType.BUTTON,
					SimpleType.MENU_VIEW,
					SimpleType.DIALOG_VIEW,
					SimpleType.SINGLE_CHOICE_LIST,
					SimpleType.MULTI_CHOICE_LIST,
					SimpleType.WEB_VIEW,
					SimpleType.TAB_HOST,
					SimpleType.LIST_VIEW
			};
			ret.filteredWidgetsArray = filteredWidgetsArray;
			
			ret.compareWidgetIds = true;
			ret.compareWidgetSimpleType = true;
			
			ret.compareMenuItemCount = true;

			ret.compareListItemCount = true;
			ret.maxListElementsCount = 3;
			
			return ret;
		}
		
		/**
		 * CustomWidgetIntensiveComparator
		 * 
		 * @return
		 */
		public static GenericComparatorConfiguration getCustomWidgetIntensiveComparator()
		{
			GenericComparatorConfiguration ret = new GenericComparatorConfiguration();
			
			ret.compareActivityNames = false;
			ret.compareActivityTitles = true;
			
			ret.testIfFilteredWidgetsMatch = true;
			
			String[] filteredWidgetsArray = {
					SimpleType.EDIT_TEXT,
					SimpleType.BUTTON,
					SimpleType.MENU_VIEW,
					SimpleType.DIALOG_VIEW,
					SimpleType.SINGLE_CHOICE_LIST,
					SimpleType.MULTI_CHOICE_LIST,
					SimpleType.WEB_VIEW,
					SimpleType.TAB_HOST,
					SimpleType.LIST_VIEW
			};
			ret.filteredWidgetsArray = filteredWidgetsArray;
			
			ret.compareWidgetIds = true;
			ret.compareWidgetSimpleType = true;
			
			ret.compareMenuItemCount = true;
			
			ret.compareListItemCount = true;
			ret.maxListElementsCount = 3;
			
			return ret;
		}
	}
	
	public GenericComparatorConfiguration() { super(); }
	
	/* ACTIVITY OPTIONS */
	public boolean compareActivityNames = false;
	public boolean compareActivityTitles = false;
	public boolean compareDialogTitle = false;
	public boolean compareActivityClasses = false;
	public boolean compareActivityWidgetsCount = false;	
	public boolean testIfWidgetsMatch = false;
	
	public String[] filteredWidgetsArray = {
			/*
			SimpleType.EDIT_TEXT,
			SimpleType.BUTTON,
			SimpleType.MENU_VIEW,
			SimpleType.DIALOG_VIEW,
			SimpleType.SINGLE_CHOICE_LIST,
			SimpleType.MULTI_CHOICE_LIST,
			SimpleType.WEB_VIEW,
			SimpleType.TAB_HOST 
			*/
	};
	public boolean compareActivityFilteredWidgetsCount = false;
	public boolean testIfFilteredWidgetsMatch = false;
	
	
	/* WIDGET OPTIONS */
	public boolean compareWidgetIds = false;
	public boolean compareWidgetSimpleType = false;
	public boolean testWidgetVisibilityChange = false;
	public boolean testWidgetEnablingChange = false;
	public boolean compareWidgetNames = false;
	public boolean compareWidgetClasses = false;
	
	/* SPECIFIC WIDGETS OPTIONS */

	//LIST
	public boolean compareListItemCount = false;
	public int maxListElementsCount = 5;
//	public boolean testIfBothListHaveAtLeastOneElement = false;
//	public boolean testIfBothListHaveMinusThanAFixedNumberOfElements = false;
//	public int fixedNumberOfListElements = 5;
	
	//MENU
	public boolean compareMenuItemCount = false;
}
