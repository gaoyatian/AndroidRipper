package it.unina.android.ripper_service.net;

import it.unina.android.ripper_service.net.packer.MessagePacker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Server extends Thread
{
	long curIndex = 0;
	
	public static int PORT = 8888;
	private boolean running = true;
	private boolean connected = false;

	ServerSocket serverSocket = null;
	Socket socket = null;
	DataInputStream dataInputStream = null;
	DataOutputStream dataOutputStream = null;

	Handler handler = null;
	
	public Server(Handler handler)
	{
		super();
		this.handler = handler;
	}
	
	public void startServer()
	{
		try {
			Log.v("SERVER", "Starting ServerSocket on PORT " + PORT);
			serverSocket = new ServerSocket(PORT);
			Log.v("SERVER", "Started ServerSocket on PORT " + PORT);
			connected = true;
		} catch (IOException e) {
			throw new RuntimeException("Error in Server Socket: " + e.getMessage());
		}
		
		this.start();
	}
	
	public void stopServer()
	{
		Log.v("SERVER", "Stopping ServerSocket on PORT " + PORT);
		this.running = false;		
		
		if( socket != null )
			try { socket.close(); } catch (IOException e) { e.printStackTrace(); }
		
		if( dataInputStream != null )
			try { dataInputStream.close(); } catch (IOException e) { e.printStackTrace(); }
		
		if( dataOutputStream != null )
			try { dataOutputStream.close(); } catch (IOException e) { e.printStackTrace(); }
		
		this.connected = false;
		Log.v("SERVER", "Stopped ServerSocket on PORT " + PORT);
	}
	
	public void run()
	{

		try
		{
			while (running)
			{
				Log.v("SERVER", "Ready to accept connections on port: " + PORT);
				socket = serverSocket.accept();
				
				dataInputStream = new DataInputStream(socket.getInputStream());
				dataOutputStream = new DataOutputStream(socket.getOutputStream());
				
				while (running)
				{
					Log.v("", "ready");
					byte[] buffer = new byte[1024];
					int c = 0;
					int i = 0;
					while((c = dataInputStream.read()) != 16 && c != -1)
					{
						//Log.v("", "read " + c);
						buffer[i++] = (byte)c;
					}
					
					//connessione caduta
					if (c == -1)
					{
						Log.v("SERVER", "ServerSocket on PORT " + PORT + ": offline");
						notifyDisconnect();
						break;
					}
					
					byte[] buffer_trim = new byte[i];
					for (int j = 0; j < i; j++)
						buffer_trim[j] = buffer[j];
										
					Map<String,String> message = MessagePacker.unpack(buffer_trim);
					
					if (message != null) {		
						String s = message.get("type");
						Log.v("RipperService", s);
						
						long seqNum = Long.parseLong(message.get("index"));
						if (seqNum >= curIndex) {
							curIndex = seqNum;
							this.notifyReceived(message);
						}
					}
					
					
				}
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}		
				
				
//				System.out.println("ip: " + socket.getInetAddress());
//				System.out.println("message: " + dataInputStream.readUTF());
//				dataOutputStream.writeUTF("Hello!");
			
				// TODO Auto-generated catch block
				// e.printStackTrace();
	
	}
	
	public void notifyReceived(Map<String,String> line)
	{
		try
		{
			Message m = new Message();
			m.obj = line;
			m.what = MSG_TYPE_NOTIFY_RECEIVED;
			handler.handleMessage(m);
		}
		catch(Throwable tr) { 
			tr.printStackTrace();
		}
	}
	
	public void notifyDisconnect()
	{
		try
		{
			Message m = new Message();
			m.what = MSG_TYPE_NOTIFY_DISCONNECTION;
			handler.handleMessage(m);
		}
		catch(Throwable tr) { 
			tr.printStackTrace();
		}
	}
	
	public void send(Map<String,String> message)
	{	
		message.put("index", Long.toString(++curIndex));
		
		byte[] packed = MessagePacker.pack(message);
		
		if (packed != null)
		{
			byte[] EOF = { 16 }; 
			byte[] msg = concatenateByteArrays(packed, EOF);
										
			try {
				socket.getOutputStream().write(msg);
				socket.getOutputStream().flush();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		else
		{
			throw new RuntimeException("Send failed!");
		}
	}
	
	byte[] concatenateByteArrays(byte[] a, byte[] b) {
	    byte[] result = new byte[a.length + b.length]; 
	    System.arraycopy(a, 0, result, 0, a.length); 
	    System.arraycopy(b, 0, result, a.length, b.length); 
	    return result;
	}
	
	public static final int MSG_TYPE_NOTIFY_RECEIVED = 1;
	public static final int MSG_TYPE_NOTIFY_DISCONNECTION = 0;
}
