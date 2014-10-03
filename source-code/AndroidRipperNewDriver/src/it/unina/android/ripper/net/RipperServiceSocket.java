package it.unina.android.ripper.net;

import it.unina.android.ripper.model.Event;
import it.unina.android.ripper.model.Input;
import it.unina.android.ripper.net.Message;
import it.unina.android.ripper.net.MessageType;
import it.unina.android.ripper_service.net.packer.MessagePacker;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class RipperServiceSocket {
	public static final String TAG = "RipperServiceSocket";
	
	public static int READ_TIMEOUT = 10000;
	
	String host;
	int port;
	Socket socket = null;
	boolean connected = false;	
	
	long curIndex = 0;
	
	public RipperServiceSocket(String host, int port)
	{
		super();
		this.host = new String(host);
		this.port = port;
	}
	
	private byte[] concatenateByteArrays(byte[] a, byte[] b) {
	    byte[] result = new byte[a.length + b.length]; 
	    System.arraycopy(a, 0, result, 0, a.length); 
	    System.arraycopy(b, 0, result, a.length, b.length); 
	    
	    a = null;
	    b = null;
	    
	    return result;
	}
	
	public void connect()  throws UnknownHostException, IOException
	{
		this.socket = new Socket(host, port);
		connected = true; //if exception this line isn't reached
	}
	
	public boolean isConnected()
	{
		return this.connected;
	}
	
	public void disconnect()
	{
		if (this.socket != null)
			if (this.socket.isConnected())
				try{ this.socket.close(); } catch(Throwable t) {}
		
		connected = false;
	}
	
	private void sendBytes(byte[] message) throws IOException {
		this.socket.getOutputStream().write(message);
		this.socket.getOutputStream().flush();
	}
	
	private byte[] readBytes(int timeout, boolean bigBuffer) throws IOException
	{
		if(bigBuffer) {
			this.socket.setSoTimeout(timeout);
			byte[] buffer = this.readBytesBigBuffer();
			this.socket.setSoTimeout(0);
			return buffer;
		} else {
			this.socket.setSoTimeout(timeout);
			byte[] buffer = this.readBytes();
			this.socket.setSoTimeout(0);
			return buffer;			
		}
	}
	
	private byte[] readBytes() throws IOException
	{
		byte[] buffer = new byte[5600];
		
		int c = 0;
		int i = 0;
		while((c = this.socket.getInputStream().read()) != 16 && c != -1)
			buffer[i++] = (byte)c;

		//connessione caduta
		if (c == -1) {
			buffer = null;
			return null;
		}
		
		byte[] buffer_trim = new byte[i];
		for (int j = 0; j < i; j++)
			buffer_trim[j] = buffer[j];
		
		buffer = null;
		
		return buffer_trim;
	}
	
	private byte[] readBytesBigBuffer() throws IOException
	{
		byte[] buffer = new byte[65000];
		
		int c = 0;
		int i = 0;
		while((c = this.socket.getInputStream().read()) != 16 && c != -1)
			buffer[i++] = (byte)c;

		//connessione caduta
		if (c == -1) {
			buffer = null;
			return null;
		}
			
		
		byte[] buffer_trim = new byte[i];
		for (int j = 0; j < i; j++)
			buffer_trim[j] = buffer[j];
		
		buffer = null;
		
		return buffer_trim;
	}
	
	public void sendMessage(Message msg)
	{
		msg.addParameter("index", Long.toString(++curIndex));
		
		byte[] packed = MessagePacker.pack(msg);
		
		if (packed != null)
		{
			byte[] EOF = { 16 };
			byte[] message = concatenateByteArrays(packed, EOF);
			
			try {
				this.sendBytes(message);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			message = null;
		}
		else
		{
			throw new RuntimeException("Send failed!");
		}
	}
	
	public Message readMessage() throws SocketException
	{
		//return readMessage(0);
		
		try {
			byte[] buffer = this.readBytes();
			
			if (buffer != null)
			{
				System.out.println();
				System.out.println(new String(buffer));
				System.out.println();
				
				Message msg = new Message(MessagePacker.unpack(buffer));
				System.out.println("" + msg.getType());
				
				try
				{
					String index = msg.getParameterValue("index");
					long indexLong = Long.parseLong(index);
					
					if (indexLong >= curIndex)
						return msg;
				}
				catch(Throwable t) {
				}
			}
		} catch (java.net.SocketException se) {
			throw se;
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;		
	}
	
	public Message readMessage(int timeout, boolean bigBuffer) throws SocketException
	{
		
		try {
			byte[] buffer = this.readBytes(timeout, bigBuffer);
			
			if (buffer != null)
			{
				System.out.println();
				System.out.println(new String(buffer));
				System.out.println();
				
				Message msg = new Message(MessagePacker.unpack(buffer));
				System.out.println("" + msg.getType());

				try
				{
					String index = msg.getParameterValue("index");
					long indexLong = Long.parseLong(index);
					
					if (indexLong >= curIndex) {
						curIndex = indexLong; 
						return msg;
					}
				}
				catch(Throwable t) {
				}
				
			}
		} catch (java.net.SocketException se) {
			throw se;
		} catch (java.net.SocketTimeoutException e) {
			
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;		
	}
	
	public boolean isAlive() throws SocketException
	{
		this.sendMessage(Message.getPingMessage());
		Message m = this.readMessage(5000, false);
		return (m != null);
	}
	
	public Message ping() throws SocketException
	{
		this.sendMessage(Message.getPingMessage());
		Message m = this.readMessage(2000, false);
		
		return m;
	}
	
	public String describe() throws SocketException
	{
		return describe(5);
	}
	
	public String describe(int MAX_RETRY) throws SocketException
	{
		Message describeMsg = null;
		this.sendMessage(Message.getDescribeMessage());

		int describeCnt = 0;
		do {			
			describeMsg = this.readMessage(1000, true);
			
			if (describeMsg != null && describeMsg.getType().equals(MessageType.DESCRIBE_MESSAGE) == false)
			{
				System.out.println("Message != DSC -> " + describeMsg.getType());
				continue;
			}
			
			if (describeMsg != null && describeMsg.getParameterValue("wait") != null)
			{
				try { Thread.sleep(1000); } catch(Throwable tr) {}
				
				this.sendMessage(Message.getDescribeMessage());
				
				continue;
			}
			else if (describeMsg != null && describeMsg.getParameterValue("xml") != null)
			{
				String xml = describeMsg.getParameterValue("xml");
				if (xml != null && xml.length() > 45)
				{
					return xml;
				}
				
				this.sendMessage(Message.getDescribeMessage());
				
				if (xml != null)
					System.out.println(xml);
			}
			else
			{
				try { Thread.sleep(1000); } catch(Throwable tr) {}
				
				if (describeCnt++ > MAX_RETRY)
					throw new RuntimeException("describeCnt overflow");
				
				//System.out.println("Describe retry " + describeCnt);
				//this.sendMessage(Message.getDescribeMessage());

				continue;
			}
		} while(true);		
	}
	
	public void sendEvent(Event evt)
	{
		if (evt.getInputs() != null && evt.getInputs().size() > 0)
			for(Input input : evt.getInputs())
				this.sendMessage(Message.getInputMessage(Integer.toString(input.getWidget().getId()), input.getInputType(), input.getValue()));
		
		if (evt.getWidget() != null)
			this.sendMessage(Message.getEventMessage((evt.getWidget().getId()!=null)?Integer.toString(evt.getWidget().getId()):"-1", Integer.toString(evt.getWidget().getIndex()), evt.getWidget().getName(), evt.getWidget().getSimpleType(), evt.getInteraction(), evt.getValue()));
		else
			this.sendMessage(Message.getEventMessage(null, null, null, null, evt.getInteraction(), evt.getValue()));
	}	
}
