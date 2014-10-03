package it.unina.android.ripper.autoandroidlib.logcat;

import java.io.FileOutputStream;
import java.io.PrintStream;

import com.googlecode.autoandroid.lib.AndroidTools;

public class LogcatDumper extends Thread {

	/*
	public static void main(String args[]) {
		new LogcatDumper(5554).start();
	}
	*/
	
	int emulatorPort;
	String filename;
	
	public LogcatDumper(int port) {
		this(port, "logcat_" + System.currentTimeMillis() + ".txt");
	}
	
	public LogcatDumper(int port, String filename) {
		super();
		this.emulatorPort = port;
		this.filename = filename;
	}

	@Override
	public void run() {		
				
		try {
			
			FileOutputStream fos = new FileOutputStream(filename, true);
			PrintStream ps = new PrintStream(fos);
			
			AndroidTools tools = AndroidTools.get();
			tools.adb("-s", "emulator-"+emulatorPort, "logcat").connectStderr(ps).connectStdout(ps).waitForSuccess();
			
			try { ps.close(); } catch (Exception ex) {}
			try { fos.close(); } catch (Exception ex) {}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
