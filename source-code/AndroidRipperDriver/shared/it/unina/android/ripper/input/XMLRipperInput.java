package it.unina.android.ripper.input;

import it.unina.android.ripper.model.ActivityDescription;
import it.unina.android.ripper.model.WidgetDescription;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLRipperInput implements RipperInput {

	@Override
	public ActivityDescription inputActivityDescription(Element activityElement) {
		ActivityDescription ret = null;
		
		if (activityElement != null) {
			ret.setTitle(activityElement.getAttribute(ACTIVITY_TITLE));
			ret.setName(activityElement.getAttribute(ACTIVITY_NAME));
			ret.setClassName(activityElement.getAttribute(ACTIVITY_CLASS));
			ret.setHasMenu(activityElement.getAttribute(ACTIVITY_MENU)
					.equalsIgnoreCase("TRUE"));
			ret.setHandlesKeyPress(activityElement.getAttribute(
					ACTIVITY_HANDLES_KEYPRESS).equalsIgnoreCase("TRUE"));
			ret.setHandlesLongKeyPress(activityElement.getAttribute(
					ACTIVITY_HANDLES_KEYPRESS).equalsIgnoreCase("TRUE"));
			ret.setIsTabActivity(activityElement.getAttribute(
					ACTIVITY_IS_TABACTIVITY).equalsIgnoreCase("TRUE"));

			try
			{
				ret.setTabsCount( Integer.parseInt(activityElement.getAttribute(ACTIVITY_TABS_COUNT)));
			} catch(Throwable t){
				ret.setTabsCount(0);
			}
			
			NodeList childNodes = activityElement.getChildNodes();

			for (int index = 0; index < childNodes.getLength(); index++) {

				Node node = (Node) childNodes.item(index);
				
				//System.out.println("1)"+node.getNodeName());
				
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					
					Element e = (Element)node;
					//System.out.println("2)"+e.getNodeName());
					if (e.getNodeName().equals(LISTENER)) {

						ret.addListener(
								e.getAttribute(LISTENER_CLASS),
								e.getAttribute(LISTENER_PRESENT).equalsIgnoreCase(
										"TRUE"));

					} else if (e.getNodeName().equals(WIDGET)) {

						WidgetDescription wd = new WidgetDescription();

						wd.setId(e.getAttribute(WIDGET_ID));
						wd.setClassName(e.getAttribute(WIDGET_CLASS));
						wd.setName(e.getAttribute(WIDGET_NAME));
						wd.setSimpleType(e.getAttribute(WIDGET_SIMPLE_TYPE));
						wd.setEnabled(e.getAttribute(WIDGET_ENABLED)
								.equalsIgnoreCase("TRUE"));
						wd.setVisible(e.getAttribute(WIDGET_VISIBLE)
								.equalsIgnoreCase("TRUE"));
						
						wd.setIndex(Integer.parseInt(e.getAttribute(WIDGET_INDEX)));
						
						if (e.getAttribute(WIDGET_COUNT) != null && e.getAttribute(WIDGET_COUNT).equals("") == false)
							wd.setCount(Integer.parseInt(e.getAttribute(WIDGET_COUNT)));
						
						NodeList widgetChildNodes = e.getElementsByTagName(LISTENER);
						//System.out.println(widgetChildNodes.getLength());
						for (int index2 = 0; index2 < widgetChildNodes.getLength(); index2++) {
							Node node2 = (Node) widgetChildNodes.item(index2);
							if (node2.getNodeType() == Node.ELEMENT_NODE)
							{
									Element e2 = (Element)node2;
									wd.addListener(
											e2.getAttribute(LISTENER_CLASS),
											e2.getAttribute(LISTENER_PRESENT).equalsIgnoreCase(
													"TRUE"));
							}
						}
						
						try
						{
							wd.setParentId( Integer.parseInt(activityElement.getAttribute(WIDGET_PARENT_ID)));
						} catch(Throwable t){
							wd.setParentId(-1);
						}
						
						wd.setParentName(e.getAttribute(WIDGET_PARENT_NAME));
						wd.setParentType(e.getAttribute(WIDGET_PARENT_TYPE));
						
						try
						{
							wd.setAncestorId( Integer.parseInt(activityElement.getAttribute(WIDGET_ANCESTOR_ID)));
						} catch(Throwable t){
							wd.setAncestorId(-1);
						}
						
						wd.setAncestorType(e.getAttribute(WIDGET_ANCESTOR_TYPE));
						
						ret.addWidget(wd);
					}
				}
			}
		} else {
			// malformed xml
		}
		
		return ret;
	}
	
	@Override
	public ActivityDescription inputActivityDescription(String description) {
		
		//System.out.println(description);
		
		ActivityDescription ret = null;

		try {
			ret = new ActivityDescription();

			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder()
					.parse(new ByteArrayInputStream(description.getBytes()));
			Element root = doc.getDocumentElement();
			NodeList activityElements = root.getElementsByTagName(ACTIVITY);

			if (activityElements != null && activityElements.getLength() > 0) {
				Element activityElement = (Element) activityElements.item(0);

				ret = this.inputActivityDescription(activityElement);

			} else {
				// malformed xml
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return ret;
	}

	public static final String ROOT = "root";
	
	public static final String ACTIVITY = "activity";	
	public static final String ACTIVITY_TITLE = "title";
	public static final String ACTIVITY_NAME = "name";
	public static final String ACTIVITY_CLASS = "class";
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
