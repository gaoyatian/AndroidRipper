package it.unina.android.ripper.driver.random;

import it.unina.android.ripper.autoandroidlib.Actions;
import it.unina.android.ripper.description.IDescriptionLoader;
import it.unina.android.ripper.driver.systematic.SystematicDriver;
import it.unina.android.ripper.observer.RipperEventListener;
import it.unina.android.ripper.planner.Planner;
import it.unina.android.ripper.scheduler.DebugRandomScheduler;
import it.unina.android.ripper.scheduler.RandomScheduler;
import it.unina.android.ripper.scheduler.Scheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class RandomRipperStarter implements RipperEventListener {

	public final static String VERSION = "0.2";
	
	Properties conf;
	RandomDriver driver;
	
	String configFile;
	
	/*
	public static void main(String[] args) {
		
		System.out.println("Android Ripper Installer");
		
		RandomRipperStarter randomRipper = null;
		
		if (args.length > 0) {
			
			if (new File(args[0]).exists()) {
				System.out.println("Using configuration file : '"+args[0]+"'!");
				randomRipper = new RandomRipperStarter(args[0]);
			} else {
				System.out.println("Configuration file '"+args[0]+"' does not exist!");
				System.exit(0);
			}
		} else {
			System.out.println("Using default configuration file 'ripper.properties'!");
			randomRipper = new RandomRipperStarter();
		}
	
		if (randomRipper != null) {
			System.out.println("Starting ripper...");
			randomRipper.startRipping();
		} else {
			System.out.println("Initialization error!");
		}
	}
	
	public RandomRipperStarter() {
		this("random.properties");
	}
	*/
	
	public RandomRipperStarter(String configFile) {
		super();
		if (new File(configFile).exists() == false) {
			throw new RuntimeException("File "+configFile+" not Found!");
		} else {
			this.configFile = configFile;
		}
	}
	
	public void startRipping()
	{
		println("Random Ripper " + VERSION);
		
		//read configuration
		//TODO: file name by args
		println("Loading configuration");
		conf = this.loadConfigurationFile(this.configFile);
		
		Scheduler scheduler = null;
		Planner planner = null;
		IDescriptionLoader descriptionLoader = null;
		
		if (conf != null)
		{
			String numEvents = conf.getProperty("events","30000");
			String seed = conf.getProperty("seed", null);
			String pullCoverage = conf.getProperty("coverage", "0");
			String pullCoverageZero = conf.getProperty("coverage_zero", "0");
			String coverageFrequency = conf.getProperty("coverage_frequency", "100");
			String coveragePath = null;
			try { coveragePath = conf.getProperty("coverage_path", ((new java.io.File( "." ).getCanonicalPath())+"/coverage/")); } catch (IOException e) { }
			String reportFile = conf.getProperty("report_file", "report.xml");
			String logFilePrefix = conf.getProperty("log_file_prefix", "log_");
			String newLogFrequency = conf.getProperty("new_log_frequency", "100");
			String avd_name = conf.getProperty("avd_name", null);
			String avd_port = conf.getProperty("avd_port", "5554");
			String aut_package = conf.getProperty("aut_package", null);
			String aut_main_activity = conf.getProperty("aut_main_activity", null);
			String ping_max_retry = conf.getProperty("ping_max_retry", "10");
			String ack_max_retry = conf.getProperty("ack_max_retry", "10");
			String failure_threshold = conf.getProperty("failure_threshold", "10");
			String ping_failure_threshold = conf.getProperty("ping_failure_threshold", "3");
			String sleep_after_task = conf.getProperty("sleep_after_task", "0");
			
			String schedulerClass = conf.getProperty("scheduler", "it.unina.android.ripper.scheduler.DebugRandomScheduler");
			String plannerClass = conf.getProperty("planner", "it.unina.android.ripper.planner.HandlerBasedPlanner");
			String descriptionLoaderClass = conf.getProperty("description_loader", "it.unina.android.ripper.description.XMLDescriptionLoader");
			
			String logcatPath = null;
			try { logcatPath = conf.getProperty("logcat_path", ((new java.io.File( "." ).getCanonicalPath())+"/logcat/")); } catch (IOException e) { }
			
			String xmlOutputPath = null;
			try { xmlOutputPath = conf.getProperty("xml_path", ((new java.io.File( "." ).getCanonicalPath())+"/model/")); } catch (IOException e) { }
			
			String junitOutputPath = null;
			try { junitOutputPath = conf.getProperty("junit_path", ((new java.io.File( "." ).getCanonicalPath())+"/junit/")); } catch (IOException e) { }
			
			//validation
			if (avd_name == null)
				throw new RuntimeException("avd_name null!");
			if (aut_package == null)
				throw new RuntimeException("aut_package null!");
			if (aut_main_activity == null)
				throw new RuntimeException("aut_main_activity null!");
			
			String ANDROID_RIPPER_SERVICE_WAIT_SECONDS = conf.getProperty("ANDROID_RIPPER_SERVICE_WAIT_SECONDS", "3");
			String ANDROID_RIPPER_WAIT_SECONDS = conf.getProperty("ANDROID_RIPPER_WAIT_SECONDS", "3");
			//String START_EMULATOR_NO_SNAPSHOOT_WAIT_SECONDS = conf.getProperty("START_EMULATOR_NO_SNAPSHOOT_WAIT_SECONDS", "60");
			//String START_EMULATOR_SNAPSHOOT_WAIT_SECONDS = conf.getProperty("START_EMULATOR_SNAPSHOOT_WAIT_SECONDS", "20");
			
			
			RandomDriver.NUM_EVENTS = Integer.parseInt(numEvents);
			RandomDriver.PULL_COVERAGE = pullCoverage.equals("1");
			RandomDriver.PULL_COVERAGE_ZERO = pullCoverageZero.equals("1");
			RandomDriver.COVERAGE_PATH = coveragePath;
			RandomDriver.COVERAGE_FREQUENCY = Integer.parseInt(coverageFrequency);
			RandomDriver.REPORT_FILE = reportFile;
			RandomDriver.LOG_FILE_PREFIX = logFilePrefix;
			RandomDriver.NEW_LOG_FREQUENCY = Integer.parseInt(newLogFrequency);
			RandomDriver.AVD_NAME = avd_name;			
			RandomDriver.EMULATOR_PORT = Integer.parseInt(avd_port);
			RandomDriver.AUT_PACKAGE = aut_package;
			RandomDriver.AUT_MAIN_ACTIVITY = aut_main_activity;
			
			RandomDriver.SLEEP_AFTER_TASK = Integer.parseInt(sleep_after_task);
			
			RandomDriver.PING_MAX_RETRY = Integer.parseInt(ping_max_retry);
			RandomDriver.ACK_MAX_RETRY =  Integer.parseInt(ack_max_retry);
			RandomDriver.FAILURE_THRESHOLD = Integer.parseInt(failure_threshold);
			RandomDriver.PING_FAILURE_THRESHOLD = Integer.parseInt(ping_failure_threshold);
			
			long seedLong = System.currentTimeMillis();
			if (seed != null && seed.equals("") == false) {
				RandomDriver.RANDOM_SEED = Long.parseLong(seed);
			}
			RandomDriver.RANDOM_SEED = seedLong;
			
			Actions.ANDROID_RIPPER_SERVICE_WAIT_SECONDS = Integer.parseInt(ANDROID_RIPPER_SERVICE_WAIT_SECONDS);
			Actions.ANDROID_RIPPER_WAIT_SECONDS = Integer.parseInt(ANDROID_RIPPER_WAIT_SECONDS);
			//Actions.START_EMULATOR_NO_SNAPSHOOT_WAIT_SECONDS = Integer.parseInt(START_EMULATOR_NO_SNAPSHOOT_WAIT_SECONDS);
			//Actions.START_EMULATOR_SNAPSHOOT_WAIT_SECONDS = Integer.parseInt(START_EMULATOR_SNAPSHOOT_WAIT_SECONDS);
			
			RandomDriver.LOGCAT_PATH = logcatPath;
			RandomDriver.XML_OUTPUT_PATH = xmlOutputPath;
			RandomDriver.JUNIT_OUTPUT_PATH = junitOutputPath;
			
			if (schedulerClass != null) {
			
				if ( schedulerClass.equals("it.unina.android.ripper.scheduler.RandomScheduler") ) {
					scheduler = new RandomScheduler(seedLong);
				} else if ( schedulerClass.equals("it.unina.android.ripper.scheduler.DebugRandomScheduler") ) { 
					scheduler = new DebugRandomScheduler(seedLong);
				} else {
					
					try {
						scheduler = (Scheduler) Class.forName(schedulerClass).newInstance();
					} catch (Exception ex) {
						println("ERROR: scheduler class " + schedulerClass);
						ex.printStackTrace();
						System.exit(1);
					}
					
				}
				
			} else {
				System.out.println("ERROR: scheduler class undefined");
			}
			
			try {
				planner = (Planner) Class.forName(plannerClass).newInstance();
			} catch (Exception ex) {
				println("ERROR: planner class " + plannerClass);
				ex.printStackTrace();
				System.exit(1);
			}
			
			try {
				descriptionLoader = (IDescriptionLoader) Class.forName(descriptionLoaderClass).newInstance();
			} catch (Exception ex) {
				println("ERROR: description_loader class " + descriptionLoaderClass);
				ex.printStackTrace();
				System.exit(1);
			}
			
			if (new java.io.File(coveragePath).exists() == false)
				new java.io.File(coveragePath).mkdir();
			
			if (new java.io.File(logcatPath).exists() == false)
				new java.io.File(logcatPath).mkdir();
			
			if (new java.io.File(xmlOutputPath).exists() == false)
				new java.io.File(xmlOutputPath).mkdir();
			
			if (new java.io.File(junitOutputPath).exists() == false)
				new java.io.File(junitOutputPath).mkdir();
			
			// TODO: remove if useless
			//			if (new java.io.File(logFile).exists())
			//				new java.io.File(logFile).delete();
		}
		else 
		{
			println("ERROR: Missing configuration file!");
			System.exit(1);
		}
		
		//starting ripper
		println("Starting ripper");
		
		driver = new RandomDriver(scheduler, planner, descriptionLoader);
		
		
		driver.setRipperEventListener(this);
		
		driver.startRipping();
		
		while(driver.running)
		{
			//TODO: stop pause commands
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private Properties loadConfigurationFile(String fileName)
	{
		Properties conf = new Properties();
		
		try {
			conf.load(new FileInputStream(fileName));
			return conf;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return null;
	}

	@Override
	public void ripperLog(String log) {
		println(log);
	}

	@Override
	public void ripperStatusUpdate(String status) {
		println(status);
	}

	@Override
	public void ripperTaskEneded() {

	}

	@Override
	public void ripperEneded() {
		println("Ripper Ended!");
		System.exit(0);
	}
	
	protected void println(String line)
	{
		System.out.println("["+System.currentTimeMillis()+"] " + line);
	}
}
