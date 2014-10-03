package it.unina.android.ripper.driver.random;

import it.unina.android.ripper.autoandroidlib.Actions;
import it.unina.android.ripper.autoandroidlib.logcat.LogcatDumper;
import it.unina.android.ripper.description.IDescriptionLoader;
import it.unina.android.ripper.description.XMLDescriptionLoader;
import it.unina.android.ripper.model.ActivityDescription;
import it.unina.android.ripper.model.Event;
import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.net.Message;
import it.unina.android.ripper.net.MessageType;
import it.unina.android.ripper.net.RipperServiceSocket;
import it.unina.android.ripper.observer.RipperEventListener;
import it.unina.android.ripper.output.RipperOutput;
import it.unina.android.ripper.output.XMLRipperOutput;
import it.unina.android.ripper.planner.HandlerBasedPlanner;
import it.unina.android.ripper.planner.Planner;
import it.unina.android.ripper.planner.task.TaskList;
import it.unina.android.ripper.scheduler.DebugRandomScheduler;
import it.unina.android.ripper.scheduler.RandomScheduler;
import it.unina.android.ripper.scheduler.Scheduler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.SocketException;

/**
 * TODO:
 * - extracted_events invece di tasklist
 * - selected_event intorno ad event
 * - entrambi in activity
 * 
 * @author Testing
 *
 */

public class RandomDriver
{
	public static int PORT = 8888;
	public static String AVD_NAME = "test";
	public static String AUT_PACKAGE = "";
	public static String AUT_MAIN_ACTIVITY = "";
	public static int EMULATOR_PORT = 5554;
	
	public static int SLEEP_AFTER_EVENT = 0;
	public static int SLEEP_AFTER_TASK = 0;
	public static int SLEEP_AFTER_RESTART = 0;
	
	public static int NUM_EVENTS = 30000;
	
	public static boolean PULL_COVERAGE = true;
	public static boolean PULL_COVERAGE_ZERO = true;
	public static int COVERAGE_FREQUENCY = 100; //if frequency == 0 use samples
	public static int COVERAGE_SAMPLES[] = { 50,100,200,300,500,700,1000,1500,2000,2500,3000,5000,7000,10000,15000,20000,25000,30000 };
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
	public static long RANDOM_SEED = System.currentTimeMillis();
	
	public static int SOCKET_EXCEPTION_THRESHOLD = 2;
	
	public static String LOGCAT_PATH = "";
	public static String XML_OUTPUT_PATH = "";
	public static String JUNIT_OUTPUT_PATH = "";
	
	Scheduler scheduler;
	Planner planner;
	RipperServiceSocket rsSocket;
	IDescriptionLoader descriptionLoader;	
	
	RipperOutput ripperOutput;
	
	boolean running = true;	
	
	String currentLogFile;
	
	public RandomDriver()
	{
		this(
				new RandomScheduler(RANDOM_SEED),
				new HandlerBasedPlanner(),
				new XMLDescriptionLoader()
		);
	}
	
	public RandomDriver(Planner planner)
	{
		this(
			new DebugRandomScheduler(RANDOM_SEED),
			planner,		
			new XMLDescriptionLoader()
		);
	}

	public RandomDriver(Scheduler scheduler, Planner planner) {
		this(
				scheduler,
				planner,
				new XMLDescriptionLoader()
		);
	}	
	
	public RandomDriver(Scheduler scheduler, Planner planner, IDescriptionLoader descriptionLoader)
	{
		super();
		
		this.scheduler = scheduler;
		this.planner = planner;		
		this.descriptionLoader = descriptionLoader;
		
		this.ripperOutput = new XMLRipperOutput();
		
		//TODO: generare dir coverage
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
	
	public void ifIsPausedDoPause()
	{
		if (paused)
		{
			do {
				Actions.sleepMilliSeconds(500);
			} while (paused);
		}
	}
	
	private void rippingLoop()
	{
		int retryCount = 0;
		
		int restartCount = 0;
		int failureCount = 0;
		int resetCount = 0;
		int resetTime = 0;
		
		int failureSinceLastSucces = 0;
		int pingFailures = 0;
		
		int N_EVENTS_DONE = 0;
		
		int currentCoverageSample = 0;
		
		int socketExceptionCount = 0;
		
		notifyRipperLog("Random Seed = " + RANDOM_SEED);
		
		//start emulator
		notifyRipperLog("Start emulator...");
		Actions.startEmulatorNoSnapshotSave(AVD_NAME, EMULATOR_PORT);
		
		//wait for emulator
		this.waitForEmulator(EMULATOR_PORT);
		
		//starts adb logcat dumper
		new LogcatDumper(EMULATOR_PORT, LOGCAT_PATH + "logcat_" + EMULATOR_PORT + "_" + System.currentTimeMillis() + ".txt").start();
		
		//start ripper service
		notifyRipperLog("Start ripper service...");
		Actions.startAndroidRipperService();
		
		//redirect port
		notifyRipperLog("Redir port...");
		Actions.sendMessageToEmualtor(EMULATOR_PORT, "redir add tcp:"+PORT+":"+PORT);
		
		long t1 = System.currentTimeMillis();
		while (running)
		{
			Actions.setRipperActive(running);
			
			//numero eventi raggiunto
			if (N_EVENTS_DONE >= NUM_EVENTS)
				break;
			
			notifyRipperLog("\n.......................................................................");
			notifyRipperLog("restart : " + (restartCount++));

			//socket
			rsSocket = new RipperServiceSocket("localhost", PORT);
			
			//start android ripper test case
			notifyRipperLog("Start ripper...");
			Actions.startAndroidRipper(AUT_PACKAGE);
			
			try
			{
				notifyRipperLog("Connect...");
				rsSocket.connect();
			
				try {
					//ping/pong
					int pingRetryCount = 0;
					do
					{
						notifyRipperLog("Ping...");
						Message m = rsSocket.ping();
						
						if (m != null && m.getType().equals(MessageType.PONG_MESSAGE))
						{
							pingFailures = 0; //reset failures count
							break;
						}
						else if (m != null && m.getType().equals(MessageType.PONG_MESSAGE))
						{
							notifyRipperLog("Message != PONG -> " + m.getType());
						}
						
						if (this.running == false )
							return;
						
						if (pingRetryCount++ > PING_MAX_RETRY)
						{
							notifyRipperLog("Ping failure : " + ++pingFailures);
							appendLineToLogFile("\n<failure type=\"ping\" />\n");
							break;
						}
						
					} while(true);
					
					while (		running //running
							&&	N_EVENTS_DONE < NUM_EVENTS //event count
							&&	(pingRetryCount <= PING_MAX_RETRY) //too many pings, need a restart
					)
					{
						
						//create a new log file 
						if (N_EVENTS_DONE == 0 || ((N_EVENTS_DONE - 1) >= NEW_LOG_FREQUENCY && ((N_EVENTS_DONE-1) % NEW_LOG_FREQUENCY == 0)))
						{
							if (N_EVENTS_DONE > 0)
								endLogFile();
							
							createLogFile();
						}
						
						//pull coverage at zero
						if (PULL_COVERAGE_ZERO && N_EVENTS_DONE == 0)
							pullCoverage(0);
						
						notifyRipperLog("\n.......................................................................");
						notifyRipperLog("Event: " + N_EVENTS_DONE + " of " + NUM_EVENTS);
						notifyRipperLog("Elapsed : " + (System.currentTimeMillis() - t1) / 1000);
						
						//alive
						notifyRipperLog("Alive...");
						if (rsSocket.isAlive() == false)
							break; //emulator killed
						
						//describe
						notifyRipperLog("Send describe msg...");
						String xml = rsSocket.describe();
						
						if (xml != null)
						{
							//parse
							notifyRipperLog("Parse...");
							//notifyRipperLog(xml);
							//appendLineToLogFile( xml.substring(45, xml.length() - 8) );
							
							ActivityDescription activity = descriptionLoader.load(xml);
							
							//plan
							notifyRipperLog("Plan...");
							TaskList plannedTasks = planner.plan(null, activity);
							
							if (plannedTasks != null && plannedTasks.size() > 0)
							{
								notifyRipperLog("plannedTasks " + plannedTasks.size());
								
								/*
								appendLineToLogFile("\n<extracted_events>");
								for (Task t : plannedTasks)
									appendLineToLogFile(t.get(0).toXMLString());
								appendLineToLogFile("</extracted_events>\n");
								*/
								appendLineToLogFile( ripperOutput.outputActivityDescriptionAndPlannedTasks(activity, plannedTasks) );
							}
							else
							{
								notifyRipperLog("error in planning!");
								
								appendLineToLogFile(ripperOutput.outputActivityDescription(activity));								
								appendLineToLogFile("\n<error type=\"no_planned_task\" />\n");
								continue; //nothing to do
							}
							
							scheduler.addTasks(plannedTasks);
							
							//schedule
							Task t = scheduler.nextTask();
							
							
							if (t == null)
							{
								notifyRipperLog("No scheduled task!");
								
								appendLineToLogFile("\n<error type=\"nothing_scheduled\" />\n");
								continue; //nothing to do
							}
							
							//hp: the task has only one event
							Event evt = t.get(0);
							
							/*
							appendLineToLogFile("<selected_event>");
							appendLineToLogFile(evt.toXMLString());
							appendLineToLogFile("</selected_event>");
							*/
							//appendLineToLogFile(ripperOutput.outputEvent(evt));
							
							notifyRipperLog("event:"+evt.toString());
							rsSocket.sendEvent(evt);
																				
							//wait for ack
							notifyRipperLog("Wait ack...");
							
							Message msg = null;
							retryCount = 0;
							do
							{								
								msg = rsSocket.readMessage(1000, false);
								
								if (msg != null)
									break;
								
							} while (running && retryCount++ < ACK_MAX_RETRY);
							
							if (retryCount > ACK_MAX_RETRY)
								notifyRipperLog("max retry exceded event ack");
							
							if (msg != null && msg.isTypeOf(MessageType.ACK_MESSAGE))
							{
								N_EVENTS_DONE++;
								
								//sreenshot
								if (SCREENSHOT)
								{
									
								}
								
								//coverage
								if (PULL_COVERAGE && COVERAGE_FREQUENCY != 0 && (N_EVENTS_DONE - 1) >= COVERAGE_FREQUENCY && ((N_EVENTS_DONE-1) % COVERAGE_FREQUENCY == 0))
								{
									notifyRipperLog("pull coverage...");
									pullCoverage(N_EVENTS_DONE - 1);
								}
								
								if (PULL_COVERAGE && COVERAGE_FREQUENCY == 0 && COVERAGE_SAMPLES.length > currentCoverageSample && (N_EVENTS_DONE - 1) == COVERAGE_SAMPLES[currentCoverageSample])
								{
									notifyRipperLog("pull coverage smaple at " + COVERAGE_SAMPLES[currentCoverageSample] + "...");								
									pullCoverage(COVERAGE_SAMPLES[currentCoverageSample]);
									currentCoverageSample += 1;
								}
								
								failureSinceLastSucces = 0;
							}
							else if ((msg != null && msg.isTypeOf(MessageType.FAIL_MESSAGE)) || running == false)
							{
								failureSinceLastSucces++;
								failureCount++;
								
								if (msg.containsKey("coverage_file")) {
									try {
										pullCoverageFile(msg.get("coverage_file"), (N_EVENTS_DONE - 1));
									} catch (Throwable throwable) {
										//ignored
									}
								}
								
								appendLineToLogFile("\n<failure type=\"fail_message\" />\n");
								
								if ((msg != null && msg.isTypeOf(MessageType.FAIL_MESSAGE)))
									notifyRipperLog("Failure! (0)");
								else
									notifyRipperLog("Failure! (1)");

								break;
							}
							
						}
						else
						{
							break;
						}
						
						Actions.sleepMilliSeconds(SLEEP_AFTER_TASK);
					}
				} catch (SocketException se) {
					se.printStackTrace();
					socketExceptionCount++;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (PULL_COVERAGE)
				{
					notifyRipperLog("pull coverage before end...");
					pullCoverage(N_EVENTS_DONE - 1);
				}
							
				//ends test
				notifyRipperLog("End message...");
				rsSocket.sendMessage(Message.getEndMessage());
				
				try {
					rsSocket.disconnect();
				} catch(Exception ex) {
					//ignored
				}

				Actions.sendBackKey();
				Actions.sendHomeKey();
				Actions.killProcessByPackage(AUT_PACKAGE, Integer.toString(EMULATOR_PORT));
				
				notifyRipperLog("Wait process end...");
				if (Actions.waitForProcessToEndMaxIterations(AUT_PACKAGE, EMULATOR_PORT, 2)) {
					Actions.killProcessByPackage(AUT_PACKAGE, Integer.toString(EMULATOR_PORT));
				}
				
				notifyRipperLog("Wait test_case end...");
				int i = 0; 
				while (Actions.isRipperActive() && i++ <= 4) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				//pullCoverageAfterEnd
				if (PULL_COVERAGE)
				{
					notifyRipperLog("pull coverage before end...");
					pullCoverageAfterEnd(N_EVENTS_DONE - 1);						
				}

				//pullJUnitLog
				pullJUnitLog(N_EVENTS_DONE - 1);
			}
			catch (SocketException se) {
				se.printStackTrace();
				socketExceptionCount++;
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			//kill emulator
			//notifyRipperLog("Killing...");
			//pullCoverage();
			//this.sendMessageToEmualtor("kill");
			
			//pulls coverage.ec
			//Actions.pullCoverage(AUT_PACKAGE, COVERAGE_PATH, N_EVENTS_DONE);
			
			this.notifyRipperTaskEnded();
		
			if (running && (failureSinceLastSucces >= FAILURE_THRESHOLD || pingFailures >=  PING_FAILURE_THRESHOLD || socketExceptionCount >= SOCKET_EXCEPTION_THRESHOLD ))
			{
				//set counters
				resetCount++;
				
				//if not for failures but for ping failures
				if (pingFailures <  PING_FAILURE_THRESHOLD)
					failureCount -= failureSinceLastSucces + 1;
				
				//reset counters
				failureSinceLastSucces = 0;
				pingFailures = 0;
				socketExceptionCount = 0;
				
				// reset ripper
				long resetTimeBegin = System.currentTimeMillis();
				notifyRipperLog("Killing...");
				Actions.sendMessageToEmualtor(EMULATOR_PORT, "kill");
				
				Actions.waitEmulatorClosed(EMULATOR_PORT);
				
				notifyRipperLog("Start emulator...");
				Actions.startEmulatorNoSnapshotSave(AVD_NAME, EMULATOR_PORT);
				
				//wait for emulator
				this.waitForEmulator(EMULATOR_PORT);
				
				//starts adb logcat dumper
				new LogcatDumper(EMULATOR_PORT, LOGCAT_PATH + "logcat_" + EMULATOR_PORT + "_" + System.currentTimeMillis() + ".txt").start();
				
				//start ripper service
				notifyRipperLog("Start ripper service...");
				Actions.startAndroidRipperService();
				
				//redirect port
				notifyRipperLog("Redir port...");
				Actions.sendMessageToEmualtor(EMULATOR_PORT, "redir add tcp:"+PORT+":"+PORT);
				
				resetTime += (System.currentTimeMillis() - resetTimeBegin);
			}
			
			//if paused wait
			this.ifIsPausedDoPause();
		}
		long t2 = (System.currentTimeMillis() - t1) / 1000;
		
		//kill emulator
		notifyRipperLog("Killing...");
		Actions.sendMessageToEmualtor(EMULATOR_PORT, "kill");

		
		notifyRipperLog("Total time = " + t2 + " seconds for n. events " + NUM_EVENTS);
		
		notifyRipperLog("End Loop!");
		
		String reportXML = "<?xml version=\"1.0\"?><report>\n";
			reportXML += "<seed>"+RANDOM_SEED+"</seed>\n";
			reportXML += "<events>"+NUM_EVENTS+"</events>\n";
			reportXML += "<execution_time>"+t2+"</execution_time>\n";
			reportXML += "<restart>"+restartCount+"</restart>\n";
			reportXML += "<failure>"+failureCount+"</failure>\n";
			reportXML += "<reset>"+resetCount+"</reset>\n";
			reportXML += "<reset_time>"+(resetTime/1000)+"</reset_time>\n";
		reportXML += "</report>";
		
		writeReportFile(reportXML);
		
		endLogFile();
		
		notifyRipperEnded();
	}
	
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
		try
		{
			FileWriter fileWritter = new FileWriter(XML_OUTPUT_PATH + REPORT_FILE, false);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	        bufferWritter.write(report);
	        bufferWritter.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void createLogFile()
	{
		currentLogFile = XML_OUTPUT_PATH + LOG_FILE_PREFIX + System.currentTimeMillis() + ".xml"; 
				
		try
		{
			FileWriter fileWritter = new FileWriter(currentLogFile, false);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	        bufferWritter.write("<?xml version=\"1.0\"?><root>\n\r");
	        bufferWritter.flush();
	        bufferWritter.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
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
	        bufferWritter.flush();
	        bufferWritter.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	
	public void appendLineToLogFile(String s)
	{
		if (currentLogFile == null && currentLogFile.equals("") == false)
			return;
		
		try
		{
			FileWriter fileWritter = new FileWriter(currentLogFile,true);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	        bufferWritter.write(s);
	        bufferWritter.flush();
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
			notifyRipperLog("coverage");
			Actions.pullCoverage(AUT_PACKAGE, cov_file_name, COVERAGE_PATH, count);
		}
	}
	
	public void pullCoverageAfterEnd(int count)
	{
		notifyRipperLog("pull coverage after end...");		
		Actions.pullCoverageStandardFile(AUT_PACKAGE, COVERAGE_PATH, count);
	}
	
	protected void pullCoverageFile(String src, int count) {
		notifyRipperLog("coverage");
		Actions.pullCoverage(AUT_PACKAGE, src, COVERAGE_PATH, count);
	}
	
	public void pullJUnitLog(int count) {
		notifyRipperLog("junit log");
		Actions.pullJUnitLog(AUT_PACKAGE, JUNIT_OUTPUT_PATH, count);
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
}
