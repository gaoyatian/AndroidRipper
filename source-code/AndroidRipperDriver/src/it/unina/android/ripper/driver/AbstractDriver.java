package it.unina.android.ripper.driver;

import it.unina.android.ripper.autoandroidlib.Actions;
import it.unina.android.ripper.autoandroidlib.logcat.LogcatDumper;
import it.unina.android.ripper.description.IDescriptionLoader;
import it.unina.android.ripper.driver.exception.AckNotReceivedException;
import it.unina.android.ripper.driver.exception.NullMessageReceivedException;
import it.unina.android.ripper.model.ActivityDescription;
import it.unina.android.ripper.net.Message;
import it.unina.android.ripper.net.MessageType;
import it.unina.android.ripper.net.RipperServiceSocket;
import it.unina.android.ripper.observer.RipperEventListener;
import it.unina.android.ripper.output.RipperOutput;
import it.unina.android.ripper.planner.Planner;
import it.unina.android.ripper.planner.task.TaskList;
import it.unina.android.ripper.scheduler.Scheduler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;

public abstract class AbstractDriver {

	public static int PORT = 8888;
	public static String AVD_NAME = "test";
	public static String AUT_PACKAGE = "";
	public static String AUT_MAIN_ACTIVITY = "";
	public static int EMULATOR_PORT = 5554;
	
	public static int SLEEP_AFTER_EVENT = 0;
	public static int SLEEP_AFTER_TASK = 0;
	public static int SLEEP_AFTER_RESTART = 0;

	public static boolean PULL_COVERAGE = true;
	public static boolean PULL_COVERAGE_ZERO = true;
	public static String COVERAGE_PATH = "";
	
	public static boolean SCREENSHOT = false;
	public static String SCREENSHOTS_PATH = "./screenshots/";
	
	public static String REPORT_FILE = "report.xml";
	public static String LOG_FILE_PREFIX = "log_";
	public static int NEW_LOG_FREQUENCY = 100;
	
	public static int PING_MAX_RETRY = 10;
	public static int ACK_MAX_RETRY = 10;
	public static int FAILURE_THRESHOLD = 10;
	public static int PING_FAILURE_THRESHOLD = 3;
	
	public static int SOCKET_EXCEPTION_THRESHOLD = 2;
	
	public static String LOGCAT_PATH = "";
	public static String XML_OUTPUT_PATH = "";
	public static String JUNIT_OUTPUT_PATH = "";
	
	protected Scheduler scheduler;
	protected Planner planner;
	protected RipperServiceSocket rsSocket;
	protected IDescriptionLoader descriptionLoader;	
	
	protected boolean running = true;	
	
	protected String currentLogFile;
	protected RipperOutput ripperOutput;
	
	public AbstractDriver()
	{
		super();
	}

	public void startRipping()
	{
		this.running = true;
		notifyRipperLog("Start Ripping Loop...");
		this.rippingLoop();
	}
	
	private boolean paused = false;
	
	public void pauseRipping()
	{
		this.paused = true;
	}
	
	public void resumeRipping()
	{
		this.paused = false;
	}

	public void stopRipping()
	{
		this.running = false;
	}
	
	public boolean isRunning()
	{
		return this.running;
	}
	
	public void ifIsPausedDoPause()
	{
		if (paused)
		{
			do {
				Actions.sleepMilliSeconds(500);
			} while (paused);
		}
	}
	
	protected abstract void rippingLoop();
	
	public TaskList getTaskList()
	{
		return scheduler.getTaskList();
	}
	
	RipperEventListener mRipperDriverListener = null;

	public void setRipperEventListener(RipperEventListener l)
	{
		this.mRipperDriverListener = l;
	}
	
	public void notifyRipperStatus(String status)
	{
		if (mRipperDriverListener != null)
			this.mRipperDriverListener.ripperStatusUpdate(status);
	}

	public void notifyRipperLog(String log)
	{
		if (mRipperDriverListener != null)
			this.mRipperDriverListener.ripperLog(log);
	}

	public void notifyRipperTaskEnded()
	{
		if (mRipperDriverListener != null)
			this.mRipperDriverListener.ripperTaskEneded();
	}
	
	public void notifyRipperEnded()
	{
		if (mRipperDriverListener != null)
			this.mRipperDriverListener.ripperEneded();
	}
	
	
	public void writeReportFile(String report)
	{
		this.writeStringToFile(report, XML_OUTPUT_PATH + REPORT_FILE);
	}
	
	public void writeStringToFile(String string, String file)
	{
		try
		{
			FileWriter fileWritter = new FileWriter(file, false);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	        bufferWritter.write(string);
	        bufferWritter.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void appendStringToFile(String string, String file)
	{
		try
		{
			FileWriter fileWritter = new FileWriter(file, true);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	        bufferWritter.write(string);
	        bufferWritter.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	protected int LOG_FILE_NUMBER = 0;
	
	public void createLogFile()
	{
		//currentLogFile = LOG_FILE_PREFIX + System.currentTimeMillis() + ".xml"; 
		currentLogFile = XML_OUTPUT_PATH + LOG_FILE_PREFIX + LOG_FILE_NUMBER + ".xml";
				
		try
		{
			FileWriter fileWritter = new FileWriter(currentLogFile, false);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	        bufferWritter.write("<?xml version=\"1.0\"?><root>\n\r");
	        bufferWritter.close();	        
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		LOG_FILE_NUMBER++;
	}
	
	public void endLogFile()
	{
		if (currentLogFile == null && currentLogFile.equals("") == false)
			return;
		
		try
		{
			FileWriter fileWritter = new FileWriter(currentLogFile, true);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	        bufferWritter.write("\n\r</root>");
	        bufferWritter.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	
	public void appendLineToLogFile(String s)
	{
		if (currentLogFile == null || currentLogFile.equals(""))
			return;
		
		try
		{
			FileWriter fileWritter = new FileWriter(currentLogFile,true);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	        bufferWritter.write(s);
	        bufferWritter.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void pullCoverage(int count) throws SocketException
	{
		notifyRipperLog("pull coverage...");
		
		String cov_file_name = "coverage"+System.currentTimeMillis()+".ec";
		
		Message mCov = new Message(MessageType.COVERAGE_MESSAGE);
		mCov.addParameter("filename", cov_file_name);
		rsSocket.sendMessage(mCov);
		
		Message message = null;
		int retryCount = 0;
		do
		{								
			message = rsSocket.readMessage(1000, false);
			
			if (message != null)
				break;
			
		} while (running && retryCount++ < ACK_MAX_RETRY);
		
		if (retryCount > ACK_MAX_RETRY)
			notifyRipperLog("max retry exceded coverage");

		
		if (message != null && message.isTypeOf(MessageType.ACK_MESSAGE))
		{
			//notifyRipperLog("coverage");
			//Actions.pullCoverage(AUT_PACKAGE, cov_file_name, COVERAGE_PATH, count);
			this.pullCoverageFile(cov_file_name, count);
		}
	}
	
	protected void pullCoverageFile(String src, int count) {
		notifyRipperLog("coverage");
		Actions.pullCoverage(AUT_PACKAGE, src, COVERAGE_PATH, count);
	}
	
	public void pullCoverageAfterEnd(int count)
	{
		notifyRipperLog("pull coverage after end...");		
		Actions.pullCoverageStandardFile(AUT_PACKAGE, COVERAGE_PATH, count);
	}
	
	public void pullJUnitLog(int count) {
		notifyRipperLog("junit log");
		Actions.pullJUnitLog(AUT_PACKAGE, JUNIT_OUTPUT_PATH, count);
	}
	
	protected boolean ping()
	{
		int pingRetryCount = 0;
		
		try
		{			
			do
			{
				notifyRipperLog("Ping...");
				Message m = rsSocket.ping();
				
				if (m != null && m.getType().equals(MessageType.PONG_MESSAGE))
				{
					return true;
				}
				else if (m != null && m.getType().equals(MessageType.PONG_MESSAGE))
				{
					notifyRipperLog("Message != PONG -> " + m.getType());
				}
				
				if (this.running == false )
					return false;
				
				if (pingRetryCount++ > PING_MAX_RETRY)
				{
					appendLineToLogFile("\n<failure type=\"ping\" />\n");
					return false;
				}
				
			} while(true);
		}
		catch (Exception ex)
		{
			return false;
		}
	}

	public Message waitAck() throws AckNotReceivedException, NullMessageReceivedException
	{
		Message msg = null;
		
		//wait for ack
		notifyRipperLog("Wait ack...");							
		
		int retryCount = 0;
		
		do
		{
			
			try {
				msg = rsSocket.readMessage(1000, false);
			} catch (Exception ex) {
				return null;
			}
			
			if (msg != null)
				break;
			
		} while (running && retryCount++ < ACK_MAX_RETRY);
		
		if (running == false)
		{
			notifyRipperLog("running == false");
			return null;
		}
		
		if (retryCount > ACK_MAX_RETRY)
		{
			notifyRipperLog("waitAck() : max retry exceded event ack");
			throw new AckNotReceivedException();
		}
		
		if (msg == null)
		{
			notifyRipperLog("null message");
			throw new NullMessageReceivedException();
		}		
		
		return msg;
	}
	
	public String getCurrentDescription() throws IOException
	{
		//describe
		notifyRipperLog("Send describe msg...");
		String xml = rsSocket.describe();
		
		if (xml != null)
		{
			notifyRipperLog(xml);
			//appendLineToLogFile( xml.substring(45, xml.length() - 8) );
		}
		
		return xml;
	}
	
	public void updateLatestDescriptionAsActivityDescription() throws IOException
	{
		this.getCurrentDescriptionAsActivityDescription();
	}
	
	int activityUID = 0;
	public ActivityDescription getCurrentDescriptionAsActivityDescription() throws IOException
	{
		this.lastActivityDescription = null;
		
		String xml = this.getCurrentDescription();
		
		if (xml != null)
		{
			this.lastActivityDescription = descriptionLoader.load(xml);
			this.lastActivityDescription.setUid( Integer.toString( ++activityUID ) );
		}
		return this.lastActivityDescription;
	}
	
	ActivityDescription lastActivityDescription = null;
	public ActivityDescription getLastActivityDescription()
	{
		return lastActivityDescription;
	}

	private int LOGCAT_FILE_NUMBER = 0;
	
	protected boolean startup()
	{
		int pingFailures = 0;
		
		long startup_t1 = System.currentTimeMillis();
		
		notifyRipperLog("Start emulator...");
		Actions.startEmulatorNoSnapshotSave(AVD_NAME, EMULATOR_PORT);
		
		//wait for emulator
		this.waitForEmulator(EMULATOR_PORT);

		//starts adb logcat dumper
		new LogcatDumper(EMULATOR_PORT, LOGCAT_PATH + "logcat_" + EMULATOR_PORT + "_" + (LOGCAT_FILE_NUMBER) + ".txt").start();
		LOGCAT_FILE_NUMBER++;
		
		Actions.setRipperActive(running);
		
		//start ripper service
		notifyRipperLog("Start ripper service...");
		Actions.startAndroidRipperService();
		
		//redirect port
		notifyRipperLog("Redir port...");
		Actions.sendMessageToEmualtor(EMULATOR_PORT, "redir add tcp:"+PORT+":"+PORT);
		
		//socket
		rsSocket = new RipperServiceSocket("localhost", PORT);
		
		//start android ripper test case
		notifyRipperLog("Start ripper...");
		Actions.startAndroidRipper(AUT_PACKAGE);
		
		int socket_exception_count = 0;
		
			try
			{
				notifyRipperLog("Connect...");
				
				do
				{
					try
					{
						
						if (rsSocket.isConnected() == false)
							rsSocket.connect();
						else
							break;
						
					} catch (Exception se) {
						socket_exception_count++;
					}
				} while (socket_exception_count <= SOCKET_EXCEPTION_THRESHOLD);
				if ((socket_exception_count >= SOCKET_EXCEPTION_THRESHOLD))
					return false;
				
				boolean ping = false;			
				do {
					ping = this.ping();
					if (ping == false)
						pingFailures++;
									
				} while(ping == false && pingFailures <=  PING_FAILURE_THRESHOLD);
			
				long startup_time = System.currentTimeMillis() - startup_t1;
				notifyRipperLog("Startup time: " + startup_time);
				
				//ready to go
				if (	running && 
						(pingFailures <=  PING_FAILURE_THRESHOLD) //too many pings, need a restart
				)
				{
					//TODO: this.notifyRipperStarted()
					return true;		
				}
			} catch (Exception se) {
				se.printStackTrace();
			}
		
		return false;
	}
	
	/*
	 * Kills the emulator
	 * */
	protected boolean shutdown()
	{
		notifyRipperLog("Shutdown...");
		//Actions.sendMessageToEmualtor(EMULATOR_PORT, "kill");
		Actions.sendMessageToEmualtor(EMULATOR_PORT, "kill");		
		
		Actions.waitEmulatorClosed(EMULATOR_PORT);
		
		this.notifyRipperTaskEnded();
		
		return true;
	}
	
	/**
	 * Check if AVD is ready
	 * 
	 * @param avdPort
	 */
	protected void waitForEmulator(Integer avdPort) {
		notifyRipperLog("Waiting for AVD...");
		Actions.waitForEmulator(avdPort);
		notifyRipperLog("AVD online!");
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public Planner getPlanner() {
		return planner;
	}

	public RipperOutput getRipperOutput() {
		return ripperOutput;
	}	
	
}
