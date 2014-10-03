package it.unina.android.ripper.extractor.output;

import it.unina.android.ripper.model.ActivityDescription;
import it.unina.android.ripper.model.WidgetDescription;

import java.io.StringWriter;
import java.util.ArrayList;
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
public class XMLOutput extends OutputAbstract
{
	
	public XMLOutput()
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
				activity.setAttribute(ACTIVITY_CLASS, ad.getActivityClass().getCanonicalName());
				activity.setAttribute(ACTIVITY_MENU, (ad.hasMenu()?"TRUE":"FALSE"));
				activity.setAttribute(ACTIVITY_HANDLES_KEYPRESS, (ad.handlesKeyPress()?"TRUE":"FALSE"));
				activity.setAttribute(ACTIVITY_HANDLES_LONG_KEYPRESS, (ad.handlesLongKeyPress()?"TRUE":"FALSE"));
				activity.setAttribute(ACTIVITY_IS_TABACTIVITY, (ad.isTabActivity()?"TRUE":"FALSE"));
				activity.setAttribute(ACTIVITY_TABS_COUNT, (ad.isTabActivity()?Integer.toString(ad.getTabsCount()):"0"));
				
				HashMap<String, Boolean> listeners = ad.getListeners();
				for (String key : listeners.keySet())
				{
					Boolean value = listeners.get(key);
					
					Element listener = doc.createElement(LISTENER);
					listener.setAttribute(LISTENER_CLASS, key);
					listener.setAttribute(LISTENER_PRESENT, value?"TRUE":"FALSE");
					activity.appendChild(listener);
				}
				
				ArrayList<String> supportedEvents = ad.getSupportedEvents();
				for (String value : supportedEvents)
				{
					Element supportedEvent = doc.createElement(SUPPORTED_EVENT);
					supportedEvent.setAttribute(SUPPORTED_EVENT_TYPE, value);
					activity.appendChild(supportedEvent);
				}
				
				for (WidgetDescription wd : ad.getWidgets())
				{
					Element widget = doc.createElement(WIDGET);
					widget.setAttribute(WIDGET_ID, Integer.toString(wd.getId()));
					widget.setAttribute(WIDGET_CLASS, wd.getType().getCanonicalName());
					
					widget.setAttribute(WIDGET_SIMPLE_TYPE, wd.getSimpleType());
					
					if (wd.getTextualId() != null)
						widget.setAttribute(WIDGET_R_ID, wd.getTextualId());
					
					if (wd.getTextType() != null)
						widget.setAttribute(WIDGET_TEXT_TYPE, wd.getTextType().toString());
					
					if (wd.getName() != null)
						widget.setAttribute(WIDGET_NAME, wd.getName());
					
					if (wd.getValue() != null)
						widget.setAttribute(WIDGET_VALUE, wd.getValue());
					
					if (wd.getCount() != null)
						widget.setAttribute(WIDGET_COUNT, wd.getCount().toString());
					
					if (wd.getIndex() != null)
						widget.setAttribute(WIDGET_INDEX, wd.getIndex().toString());
					
					widget.setAttribute(WIDGET_ENABLED, wd.isEnabled()?"TRUE":"FALSE");
					widget.setAttribute(WIDGET_VISIBLE, wd.isVisible()?"TRUE":"FALSE");
					
					if (wd.getParentId() != null)
						widget.setAttribute(WIDGET_PARENT_ID, wd.getParentId().toString());
					else
						widget.setAttribute(WIDGET_PARENT_ID, "");

					if (wd.getParentName() != null)
						widget.setAttribute(WIDGET_PARENT_NAME, wd.getParentName());
					else
						widget.setAttribute(WIDGET_PARENT_NAME, "");
					
					if (wd.getParentType() != null)
						widget.setAttribute(WIDGET_PARENT_TYPE, wd.getParentType());
					else
						widget.setAttribute(WIDGET_PARENT_TYPE, "");

					if (wd.getAncestorId() != null)
						widget.setAttribute(WIDGET_ANCESTOR_ID, wd.getAncestorId().toString());
					else
						widget.setAttribute(WIDGET_ANCESTOR_ID, "");
					
					if (wd.getAncestorType() != null)
						widget.setAttribute(WIDGET_ANCESTOR_TYPE, wd.getAncestorType());
					else
						widget.setAttribute(WIDGET_ANCESTOR_TYPE, "");
					
					listeners = wd.getListeners();
					for (String key : listeners.keySet())
					{
						Boolean value = listeners.get(key);
						
						Element listener = doc.createElement(LISTENER);
						listener.setAttribute(LISTENER_CLASS, key);
						listener.setAttribute(LISTENER_PRESENT, (value != null && value)?"TRUE":"FALSE");
						widget.appendChild(listener);
					}
					
					supportedEvents = ad.getSupportedEvents();
					for (String value : supportedEvents)
					{
						Element supportedEvent = doc.createElement(SUPPORTED_EVENT);
						supportedEvent.setAttribute(SUPPORTED_EVENT_TYPE, value);
						activity.appendChild(supportedEvent);
					}
					activity.appendChild(widget);
				}
				
				root.appendChild(activity);
			}
			
			//set up a transformer
            TransformerFactory transfac = TransformerFactory.newInstance();
            //transfac.setAttribute("indent-number", Integer.valueOf(2));
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

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

	public static final String ROOT = "root";
	
	public static final String ACTIVITY = "activity";	
	public static final String ACTIVITY_TITLE = "title";
	public static final String ACTIVITY_CLASS = "class";
	public static final String ACTIVITY_NAME = "name";
	public static final String ACTIVITY_MENU = "menu";
	public static final String ACTIVITY_HANDLES_KEYPRESS = "keypress";
	public static final String ACTIVITY_HANDLES_LONG_KEYPRESS = "longkeypress";
	public static final String ACTIVITY_IS_TABACTIVITY = "tab_activity";
	public static final String ACTIVITY_TABS_COUNT = "tab_activity";
	
	public static final String LISTENER = "listener";
	public static final String LISTENER_CLASS = "class";
	public static final String LISTENER_PRESENT = "present";
	
	public static final String SUPPORTED_EVENT = "supported_event";
	public static final String SUPPORTED_EVENT_TYPE = "type";
	
	public static final String WIDGET = "widget";
	public static final String WIDGET_ID = "id";
	public static final String WIDGET_INDEX = "index";
	public static final String WIDGET_CLASS = "class";
	public static final String WIDGET_SIMPLE_TYPE = "simple_type";
	
	public static final String WIDGET_TEXT_TYPE = "text_type";
	public static final String WIDGET_NAME = "name";
	public static final String WIDGET_ENABLED = "enabled";
	public static final String WIDGET_VISIBLE = "visible";
	
	public static final String WIDGET_VALUE = "value";
	public static final String WIDGET_COUNT = "count";
	
	public static final String WIDGET_R_ID = "r_id";
	
	public static final String WIDGET_PARENT_ID = "p_id";
	public static final String WIDGET_PARENT_NAME = "p_name";
	public static final String WIDGET_PARENT_TYPE = "p_type";
	
	public static final String WIDGET_ANCESTOR_ID = "ancestor_id";
	public static final String WIDGET_ANCESTOR_TYPE = "ancestor_type";
}
