package it.unina.android.ripper_service;
 
import it.unina.android.ripper_service.net.Server;

import java.util.Map;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

public class AndroidRipperService extends Service
{
	RemoteCallbackList<IAnrdoidRipperServiceCallback> mCallbacks; // = new RemoteCallbackList<IAnrdoidRipperServiceCallback>();
	
	AndroidRipperServiceStub mBinder; // = new AndroidRipperServiceStub(this, this.getApplicationContext()); 
	
	Server server = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.v("AndroidRipperService", "onCreate()");
		
		mCallbacks = new RemoteCallbackList<IAnrdoidRipperServiceCallback>();
		ActivityManager activityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
		mBinder = new AndroidRipperServiceStub(this, activityManager);
		
		server = new Server(this.mHandler);
		server.startServer();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.v("AndroidRipperService", "onBind()");
		return mBinder;
	}
	
	@Override
	public void onDestroy()
	{
		Log.v("AndroidRipperService", "onDestroy()");
		server.stopServer();
		super.onDestroy();
	}

	public void send(Map<String,String> message) {
		Log.v("AndroidRipperService", "send()");
		server.send(message);
	}
	
	synchronized public void register(IAnrdoidRipperServiceCallback cb)
	{
		if (cb != null) {
			mCallbacks.register(cb);
			Log.v("AndroidRipperService", "register()");
		}
		else
		{
			Log.v("AndroidRipperService", "register() fail");
		}
	}
	
	synchronized public void unregister(IAnrdoidRipperServiceCallback cb)
	{
		if (cb != null) {
			mCallbacks.unregister(cb);
			Log.v("AndroidRipperService", "unregister()");
		}
		else
		{
			Log.v("AndroidRipperService", "unregister() fail");
		}
	}
	
	synchronized public void unregisterAll()
	{
		mCallbacks.kill();
		mCallbacks = new RemoteCallbackList<IAnrdoidRipperServiceCallback>();
	}
	
	synchronized protected void broadcast(Map<String,String> message)
    {
		Log.v("AndroidRipperService", "broadcast()");
        final int N = mCallbacks.beginBroadcast();
        for(int i=0; i<N; i++)
        {
            try {
            	mCallbacks.getBroadcastItem(i).receive(message);
            } catch (RemoteException e) {
                //RemoteCallbackList will take care of removing dead objects
            }
        }
        mCallbacks.finishBroadcast();
        Log.v("AndroidRipperService", "broadcast() done");
    }
	
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg)
		{
			/*
			switch (msg.what)
			{
				case Protocol.RECEIVED_MESSAGE: {
					Log.v("AndroidRipperService", "Received message!" + msg.obj.toString());
					broadcast(msg.arg1, (String)msg.obj);
					break;
				}
	        }
	        */
			
			switch (msg.what)
			{
				case Server.MSG_TYPE_NOTIFY_RECEIVED:
					Log.v("AndroidRipperService", "mHandler.handleMessage() RECIVED");
					broadcast((Map<String,String>)msg.obj);
					break;
				
				case Server.MSG_TYPE_NOTIFY_DISCONNECTION:
					Log.v("AndroidRipperService", "mHandler.handleMessage() DISCONNECTED");
					unregisterAll();
					break;
			}
	    }
	};
}
