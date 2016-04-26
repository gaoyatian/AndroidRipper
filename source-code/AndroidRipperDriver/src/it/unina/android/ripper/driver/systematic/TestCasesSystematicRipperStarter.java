package it.unina.android.ripper.driver.systematic;

import it.unina.android.ripper.autoandroidlib.Actions;
import it.unina.android.ripper.comparator.GenericComparator;
import it.unina.android.ripper.comparator.GenericComparatorConfiguration;
import it.unina.android.ripper.comparator.IComparator;
import it.unina.android.ripper.input.RipperInput;
import it.unina.android.ripper.observer.RipperEventListener;
import it.unina.android.ripper.output.RipperOutput;
import it.unina.android.ripper.planner.Planner;
import it.unina.android.ripper.scheduler.LimitedDepthBreadthScheduler;
import it.unina.android.ripper.scheduler.LimitedDepthDepthScheduler;
import it.unina.android.ripper.scheduler.Scheduler;
import it.unina.android.ripper.termination.TerminationCriterion;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TestCasesSystematicRipperStarter implements RipperEventListener {

	public final static String VERSION = "0.1";
	
	Properties conf;
	TestCasesExecutionSystematicDriver driver;
	
	String configFile;
	
	/*
	public static void main(String[] args) {
		
		System.out.println("Android Ripper Installer");
		
		SystematicRipperStarter systematicRipper = null;
		
		if (args.length > 0) {
			
			if (new File(args[0]).exists()) {
				System.out.println("Using configuration file : '"+args[0]+"'!");
				systematicRipper = new SystematicRipperStarter(args[0]);
			} else {
				System.out.println("Configuration file '"+args[0]+"' does not exist!");
				System.exit(0);
			}
		} else {
			System.out.println("Using default configuration file 'ripper.properties'!");
			systematicRipper = new SystematicRipperStarter();
		}
	
		if (systematicRipper != null) {
			System.out.println("Starting ripper...");
			systematicRipper.startRipping();
		} else {
			System.out.println("Initialization error!");
		}
	}
	
	public SystematicRipperStarter()
	{
		this("systematic.properties");
	}
	*/
	
	public TestCasesSystematicRipperStarter(String configFile) {
		super();
		if (new File(configFile).exists() == false) {
			throw new RuntimeException("File "+configFile+" not Found!");
		} else {
			this.configFile = configFile;
		}
	}
	
	public void startRipping()
	{
		println("Systematic Ripper " + VERSION);
		
		//read configuration
		//TODO: file name by args
		println("Loading configuration");
		conf = this.loadConfigurationFile(this.configFile);
		
		Scheduler scheduler = null;
		Planner planner = null;
		RipperInput ripperInput = null;
		TerminationCriterion terminationCriterion = null;
		IComparator comparator = null;
		RipperOutput ripperOutput = null;
		
		if (conf != null)
		{
			String pullCoverage = conf.getProperty("coverage", "0");
			String pullCoverageZero = conf.getProperty("coverage_zero", "0");
			String coveragePath = null;
			try { coveragePath = conf.getProperty("coverage_path", ((new java.io.File( "." ).getCanonicalPath())+"/coverage/")); } catch (IOException e) { }
			String reportFile = conf.getProperty("report_file", "report.xml");
			String logFilePrefix = conf.getProperty("log_file_prefix", "log_");
			String avd_name = conf.getProperty("avd_name", null);
			String avd_port = conf.getProperty("avd_port", "5554");
			String aut_package = conf.getProperty("aut_package", null);
			String aut_main_activity = conf.getProperty("aut_main_activity", null);
			String ping_max_retry = conf.getProperty("ping_max_retry", "10");
			String ack_max_retry = conf.getProperty("ack_max_retry", "10");
			String failure_threshold = conf.getProperty("failure_threshold", "10");
			String ping_failure_threshold = conf.getProperty("ping_failure_threshold", "3");
			String sleep_after_task = conf.getProperty("sleep_after_task", "0");
			String comparatorClass = conf.getProperty("comparator", "it.unina.android.ripper.comparator.GenericComparator");
			String schedulerClass = conf.getProperty("scheduler", "it.unina.android.ripper.scheduler.BreadthScheduler");
			String plannerClass = conf.getProperty("planner", "it.unina.android.ripper.planner.HandlerBasedPlanner");
			String inputClass = conf.getProperty("ripper_input", "it.unina.android.ripper.input.XMLRipperInput");
			String terminationCriterionClass = conf.getProperty("termination_criterion", "it.unina.android.ripper.termination.EmptyActivityStateListTerminationCriterion");
			String ripperOutputClass = conf.getProperty("ripper_output", "it.unina.android.ripper.output.XMLRipperOutput");
			
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
			
			
			TestCasesExecutionSystematicDriver.PULL_COVERAGE = pullCoverage.equals("1");
			TestCasesExecutionSystematicDriver.PULL_COVERAGE_ZERO = pullCoverageZero.equals("1");
			TestCasesExecutionSystematicDriver.COVERAGE_PATH = coveragePath;
			TestCasesExecutionSystematicDriver.REPORT_FILE = reportFile;
			TestCasesExecutionSystematicDriver.LOG_FILE_PREFIX = logFilePrefix;
			TestCasesExecutionSystematicDriver.AVD_NAME = avd_name;
			TestCasesExecutionSystematicDriver.EMULATOR_PORT = Integer.parseInt(avd_port);
			TestCasesExecutionSystematicDriver.AUT_PACKAGE = aut_package;
			TestCasesExecutionSystematicDriver.AUT_MAIN_ACTIVITY = aut_main_activity;
			
			TestCasesExecutionSystematicDriver.SLEEP_AFTER_TASK = Integer.parseInt(sleep_after_task);
			
			TestCasesExecutionSystematicDriver.PING_MAX_RETRY = Integer.parseInt(ping_max_retry);
			TestCasesExecutionSystematicDriver.ACK_MAX_RETRY =  Integer.parseInt(ack_max_retry);
			TestCasesExecutionSystematicDriver.FAILURE_THRESHOLD = Integer.parseInt(failure_threshold);
			TestCasesExecutionSystematicDriver.PING_FAILURE_THRESHOLD = Integer.parseInt(ping_failure_threshold);
			
			Actions.ANDROID_RIPPER_SERVICE_WAIT_SECONDS = Integer.parseInt(ANDROID_RIPPER_SERVICE_WAIT_SECONDS);
			Actions.ANDROID_RIPPER_WAIT_SECONDS = Integer.parseInt(ANDROID_RIPPER_WAIT_SECONDS);
			//Actions.START_EMULATOR_NO_SNAPSHOOT_WAIT_SECONDS = Integer.parseInt(START_EMULATOR_NO_SNAPSHOOT_WAIT_SECONDS);
			//Actions.START_EMULATOR_SNAPSHOOT_WAIT_SECONDS = Integer.parseInt(START_EMULATOR_SNAPSHOOT_WAIT_SECONDS);
			
			TestCasesExecutionSystematicDriver.LOGCAT_PATH = logcatPath;
			TestCasesExecutionSystematicDriver.XML_OUTPUT_PATH = xmlOutputPath;
			TestCasesExecutionSystematicDriver.JUNIT_OUTPUT_PATH = junitOutputPath;
			
			if (schedulerClass != null) {
				
				if ( schedulerClass.equals("it.unina.android.ripper.scheduler.LimitedDepthBreadthScheduler") ) {
					int max = 10; //TODO: read from config
					scheduler = new LimitedDepthBreadthScheduler(max);
				} else if ( schedulerClass.equals("it.unina.android.ripper.scheduler.LimitedDepthDepthScheduler") ) {
					int max = 10; //TODO: read from config
					scheduler = new LimitedDepthDepthScheduler(max);
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
				ripperInput = (RipperInput) Class.forName(inputClass).newInstance();
			} catch (Exception ex) {
				println("ERROR: description_loader class " + inputClass);
				ex.printStackTrace();
				System.exit(1);
			}
			
			try {
				terminationCriterion = (TerminationCriterion) Class.forName(terminationCriterionClass).newInstance();
			} catch (Exception ex) {
				println("ERROR: termination_criterion class " + terminationCriterionClass);
				ex.printStackTrace();
				System.exit(1);
			}
			
			try {
				ripperOutput = (RipperOutput) Class.forName(ripperOutputClass).newInstance();
			} catch (Exception ex) {
				println("ERROR: ripper_output class " + ripperOutputClass);
				ex.printStackTrace();
				System.exit(1);
			}	
			
			if (comparatorClass.equals("it.unina.android.ripper.comparator.GenericComparator")) {			
				String comparatorConfiguration = conf.getProperty("comparator_configuration", "DefaultComparator");
				if (comparatorConfiguration != null) {
					
					if ( comparatorConfiguration.equals("DefaultComparator") ) {
						comparator = new GenericComparator( GenericComparatorConfiguration.Factory.getDefaultComparator() );
					} else if ( comparatorConfiguration.equals("NameComparator") ) {
						comparator = new GenericComparator( GenericComparatorConfiguration.Factory.getNameComparator() );
					} else if ( comparatorConfiguration.equals("CustomWidgetSimpleComparator") ) {
						comparator = new GenericComparator( GenericComparatorConfiguration.Factory.getCustomWidgetSimpleComparator() );
					} else if ( comparatorConfiguration.equals("CustomWidgetIntensiveComparator") ) {
						comparator = new GenericComparator( GenericComparatorConfiguration.Factory.getCustomWidgetIntensiveComparator() );
					} else {
						println("ERROR: comparator configuration not found");
					}
					
				} else {
					println("ERROR: comparator configuration undefined");
				}
			} else {
				
				try {
					comparator = (IComparator) Class.forName(comparatorClass).newInstance();
				} catch (Exception ex) {
					println("ERROR: comparator class " + comparatorClass);
					ex.printStackTrace();
					System.exit(1);
				}
				
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
		driver = new TestCasesExecutionSystematicDriver(
				scheduler,
				planner,
				ripperInput,
				comparator,
				terminationCriterion,
				ripperOutput
		);
		
		terminationCriterion.init(driver);
		driver.setRipperEventListener(this);
		
		driver.startRipping();
		
		while(driver.isRunning())
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
