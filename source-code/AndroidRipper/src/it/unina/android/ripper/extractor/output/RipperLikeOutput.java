package it.unina.android.ripper.extractor.output;

import it.unina.android.ripper.model.ActivityDescription;
import it.unina.android.ripper.model.WidgetDescription;

import java.io.StringWriter;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.annotation.TargetApi;

@TargetApi(8)
public class RipperLikeOutput extends OutputAbstract
{
	
	public RipperLikeOutput()
	{
		super();
	}
	
	public String output()
	{
		try
		{
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element root = doc.createElement(ROOT);
			doc.appendChild(root);
			
			for (ActivityDescription ad : this.activityDescription)
			{
				Element activity = doc.createElement(ACTIVITY);
				activity.setAttribute(ACTIVITY_TITLE, ad.getTitle());
				activity.setAttribute(ACTIVITY_NAME, ad.getName());
				//activity.setAttribute(ACTIVITY_CLASS, ad.getActivityClass().getCanonicalName());
				//activity.setAttribute(ACTIVITY_MENU, (ad.hasMenu()?"TRUE":"FALSE"));
				//activity.setAttribute(ACTIVITY_HANDLES_KEYPRESS, (ad.handlesKeyPress()?"TRUE":"FALSE"));
				//activity.setAttribute(ACTIVITY_HANDLES_LONG_KEYPRESS, (ad.handlesLongKeyPress()?"TRUE":"FALSE"));
				//activity.setAttribute(ACTIVITY_IS_TABACTIVITY, (ad.isTabActivity()?"TRUE":"FALSE"));
				
				String myID = "a"+System.currentTimeMillis();
				activity.setAttribute(ACTIVITY_UNIQUE_ID, myID);
				activity.setAttribute(ACTIVITY_ID, myID);
				
				HashMap<String, Boolean> listeners;
				/*
				HashMap<String, Boolean> listeners = ad.getListeners();
				for (String key : listeners.keySet())
				{
					Boolean value = listeners.get(key);
					activity.setAttribute(key, value?"TRUE":"FALSE");
				}
				
				ArrayList<String> supportedEvents = ad.getSupportedEvents();
				String values = "";
				for (String value : supportedEvents)
				{
					values+=(value + "|");
				}
				activity.setAttribute(SUPPORTED_EVENT, values);
				*/
				
				Element description = doc.createElement(DESCRIPTION);
				
				int wIndex = 0;
				for (WidgetDescription wd : ad.getWidgets())
				{
					Element widget = doc.createElement(WIDGET);
					widget.setAttribute(WIDGET_ID, Integer.toString(wd.getId()));
					widget.setAttribute(WIDGET_CLASS, wd.getType().getCanonicalName());
					widget.setAttribute(WIDGET_INDEX, Integer.toString(wIndex++));
					widget.setAttribute(WIDGET_UNIQUE_ID, "a"+System.currentTimeMillis());
					
					if (wd.getTextualId() != null)
						widget.setAttribute(WIDGET_R_ID, wd.getTextualId());
					
					if (wd.getTextType() != null)
						widget.setAttribute(WIDGET_TEXT_TYPE, wd.getTextType().toString());
					
					if (wd.getName() != null)
						widget.setAttribute(WIDGET_NAME, wd.getName());
					
					if (wd.getSimpleType() != null)
						widget.setAttribute(WIDGET_SIMPLE_TYPE, wd.getSimpleType());
					
					if (wd.getValue() != null)
						widget.setAttribute(WIDGET_VALUE, wd.getValue());
					
					if (wd.getCount() != null)
						widget.setAttribute(WIDGET_COUNT, wd.getCount().toString());
					
					widget.setAttribute(WIDGET_ENABLED, wd.isEnabled()?"true":"false");
					
					
					listeners = wd.getListeners();
					Boolean clickable = listeners.get("OnClickListener");
					Boolean long_clickable = listeners.get("OnLongClickListener");
					widget.setAttribute(WIDGET_CLICKABLE, clickable?"true":"false");
					widget.setAttribute(WIDGET_LONG_CLICKABLE, long_clickable?"true":"false");
					
					/*
					for (String key : listeners.keySet())
					{
						Boolean value = listeners.get(key);
						widget.setAttribute(key, value?"TRUE":"FALSE");
					}
					*/
					
					/*
					supportedEvents = ad.getSupportedEvents();
					values = "";
					for (String value : supportedEvents)
					{
						values+=(value + "|");
					}
					widget.setAttribute(SUPPORTED_EVENT, values);
					*/
					
					description.appendChild(widget);
				}
				
				activity.appendChild(description);
				activity.appendChild(doc.createElement("SUPPORTED_EVENTS")); //per compatibilita
				root.appendChild(activity);
			}
			
			//set up a transformer
            TransformerFactory transfac = TransformerFactory.newInstance();
            //transfac.setAttribute("indent-number", Integer.valueOf(2));
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            //trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            //trans.setOutputProperty(OutputKeys.INDENT, "yes");

            //create string from xml tree
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);
            String xmlString = sw.toString();
            
           return xmlString;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return "";
	}

	public static final String ROOT = "session";
	
	public static final String ACTIVITY = "FINAL_ACTIVITY";	
	public static final String ACTIVITY_TITLE = "title";
	public static final String ACTIVITY_NAME = "name";
	public static final String ACTIVITY_CLASS = "class";
	public static final String ACTIVITY_MENU = "menu";
	public static final String ACTIVITY_HANDLES_KEYPRESS = "keypress";
	public static final String ACTIVITY_HANDLES_LONG_KEYPRESS = "longkeypress";
	public static final String ACTIVITY_IS_TABACTIVITY = "tab_activity";
	public static final String ACTIVITY_UNIQUE_ID = "unique_id";
	public static final String ACTIVITY_ID = "id";
	
	public static final String DESCRIPTION = "DESCRIPTION";
	
	public static final String LISTENER = "listener";
	public static final String LISTENER_CLASS = "class";
	public static final String LISTENER_PRESENT = "present";
	
	public static final String SUPPORTED_EVENT = "supported_event";
	public static final String SUPPORTED_EVENT_TYPE = "type";
	
	public static final String WIDGET = "widget";
	public static final String WIDGET_ID = "id";
	public static final String WIDGET_INDEX = "index";
	public static final String WIDGET_CLASS = "type";
	
	public static final String WIDGET_TEXT_TYPE = "text_type";
	public static final String WIDGET_SIMPLE_TYPE = "simple_type";
	public static final String WIDGET_NAME = "name";
	
	public static final String WIDGET_ENABLED = "available";	
	public static final String WIDGET_CLICKABLE = "clickable";
	public static final String WIDGET_LONG_CLICKABLE = "long_clickable";
	
	public static final String WIDGET_VALUE = "value";
	public static final String WIDGET_COUNT = "count";	
	
	public static final String WIDGET_R_ID = "r_id";
	public static final String WIDGET_UNIQUE_ID = "unique_id";
}
