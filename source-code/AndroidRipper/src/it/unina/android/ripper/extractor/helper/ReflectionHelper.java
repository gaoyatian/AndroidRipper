package it.unina.android.ripper.extractor.helper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

/**
 * Classe contenente metodi che utilizzano la reflection 
 * 
 * @author nicola
 *
 */
public class ReflectionHelper
{
	public static final String TAG = "ReflectionHelper";
	/**
	 * Controlla se una classe implementa l'interfaccia data
	 * 
	 * @param className CanonicalName della classe
	 * @param interfaceName CanonicalName dell'interfaccia
	 * @return esito della ricerca
	 * @throws ClassNotFoundException
	 */
	public static boolean implementsInterface(String className, String interfaceName) throws ClassNotFoundException
	{
		Class<?> myClass = (Class<?>) Class.forName(className);
		return ReflectionHelper.implementsInterface(myClass, interfaceName);
	}
	
	/**
	 * Controlla se una classe implementa l'interfaccia data
	 * 
	 * @param myClass Classe da scansionare
	 * @param interfaceName CanonicalName dell'interfaccia
	 * @return esito della ricerca
	 */
	public static boolean implementsInterface(Class<?> myClass, String interfaceName)
	{
		for (Class<?> myInterface : myClass.getInterfaces())
		{
			//Log.v(TAG, myInterface.toString());
			if (myInterface.getCanonicalName().equals(interfaceName))
				return true;
		}
		return false;
	}
	
	/**
	 * Scansiona una classe e le sue variabili alla ricerca di qualcosa che implementi
	 * un'interfaccia.
	 * 
	 * Inoltre controlla se ci sono variabili definite inline del tipo dell'interfaccia
	 * 
	 * @param className CanonicalName della classe
	 * @param interfaceName CanonicalName dell'interfaccia
	 * @return esito della scansione
	 * @throws ClassNotFoundException
	 */
	public static boolean scanClassForInterface(String className, String interfaceName) throws ClassNotFoundException
	{
		Class<?> myClass = (Class<?>) Class.forName(className);
		return ReflectionHelper.scanClassForInterface(myClass, interfaceName);
	}
	
	/* NOTA:
	 *      if (field.getType().isAssignableFrom(myType))
	 */
	
	/**
	 * Scansiona una classe e le sue variabili alla ricerca di qualcosa che implementi
	 * un'interfaccia.
	 * 
	 * Inoltre controlla se ci sono variabili definite inline del tipo dell'interfaccia
	 * 
	 * @param myClass Classe da scansionare
	 * @param interfaceName CanonicalName dell'interfaccia
	 * @return esito della scansione
	 * @return
	 */
	public static boolean scanClassForInterface(Class<?> myClass, String interfaceName)
	{
		//controllo se la classe implementa lei stessa l'interfaccia 
		if ( ReflectionHelper.implementsInterface(myClass, interfaceName))
		{
			Log.v(TAG, "Found interface : " + interfaceName + " in " + myClass.getCanonicalName());
			return true;
		}
		//controllo se un campo implementa l'interfaccia
		for(Field field : myClass.getDeclaredFields() )
		{
			Class<?> fieldClass = field.getType();
								
			//controlla se implementa l'interfaccia
			if ( ReflectionHelper.implementsInterface(fieldClass, interfaceName))
			{
				Log.v(TAG, "Found field implements : " + interfaceName  + " in " + fieldClass.getCanonicalName());
				return true;
			}
			
			//controlla se e' una definizione inline
			if (fieldClass.getCanonicalName().equals(interfaceName))
			{
				Log.v(TAG, "Found field inline definition : " + interfaceName  + " in " + fieldClass.getCanonicalName());
				return true;
			}
		}
		
		Log.v(TAG, "Not found : " + interfaceName  + " in " + myClass.getCanonicalName());
		return false;
	}
		
	/**
	 * Utilizzando la Reflection ottiene i listener associati ad un oggetto View
	 * 
	 * @param view View da esaminare
	 * @return 	risultato sotto forma di HashMap<String, Boolean>, che associa il nome del
	 * 			listener alla sua effettiva esistenza
	 */
	public static HashMap<String, Boolean> reflectViewListeners(android.view.View view)
    {
		HashMap<String, Boolean> ret = new HashMap<String, Boolean>();
		
		ret.put( "OnFocusChangeListener", checkIfFieldIsSet(view, "android.view.View", "mOnFocusChangeListener") );
		
		ret.put( "OnClickListener",
					checkIfFieldIsSet(view, "android.view.View", "mOnClickListener")
				||	checkIfFieldIsSet(view, "android.view.View", "mOnTouchListener")
		);
		
		ret.put( "OnLongClickListener",
					checkIfFieldIsSet(view, "android.view.View", "mOnLongClickListener")
				||	checkIfFieldIsSet(view, "android.view.View", "mOnCreateContextMenuListener")
		);
		
		ret.put( "OnKeyListener", checkIfFieldIsSet(view, "android.view.View", "mOnKeyListener") );
		
		if (view instanceof android.widget.TextView) //EditText
		{
			ret.put( "TextChangedListener", checkIfArrayListFieldIsSet(view, "android.widget.TextView", "mListeners") );
		}
		
		if (view instanceof android.widget.AbsListView) //ListView
		{
			ret.put( "OnScrollListener", checkIfFieldIsSet(view, "android.widget.AbsListView", "mOnScrollListener") );			
			ret.put( "OnItemSelectedListener", checkIfFieldIsSet(view, "android.widget.AdapterView", "mOnItemSelectedListener") );
			ret.put( "OnItemClickListener", checkIfFieldIsSet(view, "android.widget.AdapterView", "mOnItemClickListener") );
			ret.put( "OnItemLongClickListener", checkIfFieldIsSet(view, "android.widget.AdapterView", "mOnItemLongClickListener") );
			
			/*
			//longClickPatch
			if ( ret.get(InteractionType.LONG_CLICK) == true ) 
			{
				ret.remove(InteractionType.LONG_CLICK);
				
				if ( ret.get(InteractionType.LIST_LONG_SELECT) == false )
					ret.put(InteractionType.LIST_LONG_SELECT, true);
			}
			*/
		}
		
		if (view instanceof android.view.ViewGroup)
		{
			ret.put( "OnHierarchyChangeListener", checkIfFieldIsSet(view, "android.view.ViewGroup", "mOnHierarchyChangeListener") );
			ret.put( "AnimationListener", checkIfFieldIsSet(view, "android.view.ViewGroup", "mAnimationListener") );
		}
		
    	return ret;
    }
	
	public static boolean checkIfFieldIsSet(Object o, String baseClass, String fieldName)
	{
		java.lang.reflect.Field field;
		
		try
    	{
			//TODO: cache
			Class<?> viewObj = Class.forName(baseClass);
			field = viewObj.getDeclaredField(fieldName);
			field.setAccessible(true);
			
			boolean ret = (field.get(o) != null);
			Log.v(TAG, o.getClass().getCanonicalName() + " > " + fieldName + " FOUND | " + ((ret)?"ACTIVE":"NOT ACTIVE") );
			return ret;
			
			/* NOTA:
			 * Senza log
			 * 		return (field.get(o) != null);
			 */
    	}
		catch (Exception e)
		{
			Log.e(TAG, e.toString());
		}
		
		Log.v(TAG, o.getClass().getCanonicalName() + " > " + fieldName + " NOT FOUND");
		
		return false;
	}
	
	public static boolean checkIfArrayListFieldIsSet(Object o, String baseClass, String fieldName)
	{
		java.lang.reflect.Field field;
		
		try
    	{
			//TODO: cache
			Class<?> viewObj = Class.forName(baseClass);
			field = viewObj.getDeclaredField(fieldName);
			field.setAccessible(true);
			
			ArrayList arrayListField = (ArrayList) field.get(o);
			
			if (arrayListField != null)
			{
				if (arrayListField.size() > 0)
				{
					Log.v(TAG, o.getClass().getCanonicalName() + " > " + fieldName + " FOUND | ACTIVE" );
					return true;
				}
				else
				{
					Log.v(TAG, o.getClass().getCanonicalName() + " > " + fieldName + " FOUND | NOT ACTIVE" );
				}
				
				/* NOTA:
				 * Senza log
				 * 		return (arrayListField.size() > 0);
				 */
			}
			else
			{
				//NOTA: Senza log, va eliminato questo else
				Log.v(TAG, o.getClass().getCanonicalName() + " > " + fieldName + " FOUND | NULL" );
				return false;
			}
    	}
		catch (Exception e)
		{
			Log.e(TAG, e.toString());
		}
		
		Log.v(TAG, o.getClass().getCanonicalName() + " > " + fieldName + " NOT FOUND");
		
		return false;
	}
	
	public static Object getPrivateField(String canonicalClassName, String fieldName, Object o)
	{
		try
		{
			Class<?> viewObj = Class.forName(canonicalClassName);
			Field field = viewObj.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(o);
		}
		catch(Exception ex)
		{
			Log.e(TAG, ex.toString());
		}
		
		return null;
	}
	
	/* NOTA:
	 * 
	 * bisognerebbe controllare la firma del metodo invece che solo il nome
	 * 
	 */
	public static boolean hasDeclaredMethod(Class<?> c, String methodName)
	{
		try
		{
			for ( Method m : c.getDeclaredMethods() )
				if (m.getName().equals(methodName))
					return true;
		}
		catch(Exception ex)
		{
			Log.e(TAG, ex.toString());
		}
		
		return false;
	}
	
	public static boolean isDescendant(Class<?> descendant, Class<?> anchestor)
	{
		return anchestor.isAssignableFrom(descendant);
	}
	
	
	/*
	private static void enableAllViewClassListenerFields()
	{
		Field field = null;
		Class<?> viewObj = null;
		
		try {
			viewObj = Class.forName("android.view.View");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Are you on Android?");
		}
		
		try
		{
			field = viewObj.getDeclaredField("mOnFocusChangeListener");
			field.setAccessible(true);
		}
		catch (Exception ex) {}
		
		try
		{
			field = viewObj.getDeclaredField("mOnClickListener");
			field.setAccessible(true);
		}
		catch (Exception ex) {}
		
		try
		{
			field = viewObj.getDeclaredField("mOnLongClickListener");
			field.setAccessible(true);
		}
		catch (Exception ex) {}
		
		try
		{
			field = viewObj.getDeclaredField("mOnCreateContextMenuListener");
			field.setAccessible(true);
		}
		catch (Exception ex) {}
		
		try
		{
			field = viewObj.getDeclaredField("mOnKeyListener");
			field.setAccessible(true);
		}
		catch (Exception ex) {}
		
		try
		{
			field = viewObj.getDeclaredField("mOnTouchListener");
			field.setAccessible(true);
		}
		catch (Exception ex) {}
	}
	*/	
}
