package it.unina.android.ripper.log;

import android.util.Log;

public class Debug
{	
	public static final int LOG_LEVEL_NONE = 0;
	public static final int LOG_LEVEL_INFO = 1;
	public static final int LOG_LEVEL_ALL = 2;
	
	public static int LOG_LEVEL = LOG_LEVEL_ALL;
	
	public static String TAG = "AndroidRipper";
	
	public static void log(String msg) {
		if (LOG_LEVEL >= LOG_LEVEL_ALL)
			Log.v(TAG, msg);
	}
	
	public static void info(String msg) {
		if (LOG_LEVEL >= LOG_LEVEL_INFO)
			Log.i(TAG, msg);
	}
	
	public static void log(Object o, String msg) {
		log("["+o.getClass().getSimpleName()+"] " + msg);
	}
	
	public static void info(Object o, String msg) {
		info("["+o.getClass().getSimpleName()+"] " + msg);
	}

}
