package it.unina.android.ripper.net;

import java.util.HashMap;
import java.util.Map;

public class Message extends HashMap<String, String>
{
	private static final long serialVersionUID = 2199912L;

	public static Message getDescribeMessage()
	{
		return new Message(MessageType.DESCRIBE_MESSAGE);
	}
	
	public static Message getAckMessage()
	{
		return new Message(MessageType.ACK_MESSAGE);
	}
	
	public static Message getPingMessage()
	{
		return new Message(MessageType.PING_MESSAGE);
	}

	public static Message getPongMessage()
	{
		return new Message(MessageType.PONG_MESSAGE);
	}

	public static Message getFailMessage()
	{
		return new Message(MessageType.FAIL_MESSAGE);
	}

	public static Message getCrashMessage()
	{
		return new Message(MessageType.CRASH_MESSAGE);
	}

	public static Message getNAckMessage()
	{
		return new Message(MessageType.NACK_MESSAGE);
	}
	
	public static Message getEndMessage()
	{
		return new Message(MessageType.END_MESSAGE);
	}

	public static Message getEventMessage()
	{
		return new Message(MessageType.EVENT_MESSAGE);
	}

	public static Message getEventMessage(String widgetId, String widgetIndex, String widgetName, String widgetType, String eventType, String value)
	{
		Message msg = new Message(MessageType.EVENT_MESSAGE);
		
		msg.addParameter("widgetId", widgetId);
		msg.addParameter("widgetIndex", widgetIndex);
		msg.addParameter("widgetName", widgetName);
		msg.addParameter("widgetType", widgetType);
		msg.addParameter("eventType", eventType);
		msg.addParameter("value", value);
		
		return msg;
	}
	
	public static Message getInputMessage()
	{
		return new Message(MessageType.INPUT_MESSAGE);
	}
	
	public static Message getInputMessage(String widgetId, String interactionType, String value)
	{
		Message msg = new Message(MessageType.INPUT_MESSAGE);
		
		msg.addParameter("widgetId", widgetId);
		msg.addParameter("inputType", interactionType); //TODO: interactionType anche come chiave (anche nel client)
		msg.addParameter("value", value);
		
		return msg;
	}
	
	public static Message getConfigMessage()
	{
		return new Message(MessageType.CONFIG_MESSAGE);
	}

	public Message(Map message)
	{
		super();
		super.putAll(message);
	}
	
	public Message()
	{
		super();
	}
	
	public Message(String type)
	{
		super();
		this.setType(type);
	}
	
	public void setType(String type)
	{
		this.put(TYPE_KEY, type);
	}
	
	public void addParameter(String key, String value)
	{
		this.put(key, value);
	}
	
	public String getParameterValue(String key)
	{
		return this.get(key);
	}
	
	public String getType()
	{
		return this.get(TYPE_KEY);
	}
	
	public boolean isTypeOf(String type)
	{
		return (this.containsKey(TYPE_KEY) && this.get(TYPE_KEY) != null && this.get(TYPE_KEY).equals(type));		
	}
	
	private static final String TYPE_KEY = "type";
}
