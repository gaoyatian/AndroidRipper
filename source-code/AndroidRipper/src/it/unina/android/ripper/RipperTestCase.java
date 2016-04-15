package it.unina.android.ripper;

import java.lang.reflect.Method;
import java.util.Map;

import com.robotium.solo.Solo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import it.unina.android.ripper.automation.IAutomation;
import it.unina.android.ripper.automation.RipperAutomation;
import it.unina.android.ripper.automation.robot.IRobot;
import it.unina.android.ripper.automation.robot.RobotiumWrapperRobot;
import it.unina.android.ripper.configuration.Configuration;
import it.unina.android.ripper.extractor.IExtractor;
import it.unina.android.ripper.extractor.SimpleExtractor;
import it.unina.android.ripper.extractor.output.OutputAbstract;
import it.unina.android.ripper.extractor.output.XMLOutput;
import it.unina.android.ripper.extractor.screenshoot.IScreenshotTaker;
import it.unina.android.ripper.extractor.screenshoot.RobotiumScreenshotTaker;
import it.unina.android.ripper.log.Debug;
import it.unina.android.ripper.model.ActivityDescription;
import it.unina.android.ripper.net.Message;
import it.unina.android.ripper.net.MessageType;
import it.unina.android.ripper_service.IAndroidRipperService;
import it.unina.android.ripper_service.IAnrdoidRipperServiceCallback;

public class RipperTestCase  extends ActivityInstrumentationTestCase2  {

	public static final String TAG = "RipperTestCase";
	
	IRobot robot = null;
	IAutomation automation = null;
	IExtractor extractor = null;
	IScreenshotTaker screenshotTaker = null;
	
	private boolean testRunning = true;

    ActivityManager mActivityManager;
    Context mContext;
	private boolean readyToOperate = false;
    
	public RipperTestCase() {
		super(Configuration.autActivityClass);
		
		/*
		 * NOTE: Capture App Exception
		 * 
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				
				System.out.println(ex.getMessage());
				System.exit(1);
			}
		});
		 */
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		mContext = this.getInstrumentation().getContext();
		mActivityManager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
		
		bindCommunicationServices();
	}
	
	@Override
	protected void tearDown() throws Exception {
		
		unbindCommunicationServices();
		
		if (this.automation != null)
		{
			Activity theActivity = this.automation.getCurrentActivity();
			
			try {
				this.automation.finalizeRobot();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			theActivity.finish();
		}
		
		try { this.unbindCommunicationServices(); }	catch(Throwable tr) {}
		
		super.tearDown();
	}
	
	public IAutomation getAutomation()
	{
		return this.automation;
	}
	
	public void afterRestart()
	{
		automation.setActivityOrientation(Solo.PORTRAIT);
		sleepAfterTask();
		automation.waitOnThrobber();
		
		//TODO: precrawling		
		Debug.info(this, "Ready to operate after restarting...");
	}
	
	public void testApplication()
	{			
		//init components
		this.robot = new RobotiumWrapperRobot(this);
		this.automation = new RipperAutomation(this.robot);
		this.extractor = new SimpleExtractor(this.robot);
		this.screenshotTaker = new RobotiumScreenshotTaker(this.robot);
		
		this.afterRestart();
		readyToOperate = true;
		
		//Debug.log(extractor.extract().getTitle());
		//Debug.log(extractor.extract().getWidgets().size()+"");
		
		//automation.fireEvent (16908315, 16, "OK", "button", "click");
		//automation.fireEvent (0, "", "null", "openMenu");
		
		//Debug.log(extractor.extract().getWidgets().size()+"");
		
		//automation.fireEvent (0, "", "null", "back");
		//automation.fireEvent (2131099651, 6, "", "button", "click");
		//automation.fireEvent (0, "", "null", "changeOrientation");
		
		//infinite loop
		while(this.testRunning)
			this.robot.sleep(500);
		
		//this.sleepAfterTask();
	}
	
	private void sleepAfterTask()
	{
		automation.sleep (Configuration.SLEEP_AFTER_TASK);
	}
	

	IAndroidRipperService mService = null;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            mService = IAndroidRipperService.Stub.asInterface(service);

            try {
                mService.register(mCallback);
            } catch (RemoteException e) {
            	e.printStackTrace();
            }

        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };
    
    IAnrdoidRipperServiceCallback mSecondaryService = null;
    private ServiceConnection mSecondaryConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            mSecondaryService = IAnrdoidRipperServiceCallback.Stub.asInterface(service);
        }

        public void onServiceDisconnected(ComponentName className) {
            mSecondaryService = null;
        }
    };
	
    private void bindCommunicationServices()
    {
    	/*
    	this.getInstrumentation().getContext().bindService(new Intent("it.unina.android.ripper_service.IAndroidRipperService"),
                mConnection, Context.BIND_AUTO_CREATE);
    	this.getInstrumentation().getContext().bindService(new Intent("it.unina.android.ripper_service.IAnrdoidRipperServiceCallback"),
                mSecondaryConnection, Context.BIND_AUTO_CREATE);
        */
    	
    	Intent bindIntent = new Intent(".IAndroidRipperService");
        bindIntent.setClassName("it.unina.android.ripper_service", "it.unina.android.ripper_service.AndroidRipperService");
        this.getInstrumentation().getContext().bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);
        
        bindIntent = new Intent(".IAnrdoidRipperServiceCallback");
        bindIntent.setClassName("it.unina.android.ripper_service", "it.unina.android.ripper_service.AndroidRipperService");
        this.getInstrumentation().getContext().bindService(bindIntent, mSecondaryConnection, Context.BIND_AUTO_CREATE);
    }
    
    private void unbindCommunicationServices()
    {
    	try {
			mService.unregister(mCallback);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	this.getInstrumentation().getContext().unbindService(mConnection);
    	this.getInstrumentation().getContext().unbindService(mSecondaryConnection);
    }
    
    private IAnrdoidRipperServiceCallback mCallback = new IAnrdoidRipperServiceCallback.Stub() {
    	
		@Override
		public void receive(Map message)
				throws RemoteException {
	
			Message msg = new Message(message);
			
			Debug.info("Recived message: " + msg.getType());
			
			if (readyToOperate == false) //skip
			{
				Log.v(TAG, "Not ready to operate!");
				return;
			}
			
			if (msg.isTypeOf(MessageType.CONFIG_MESSAGE))
			{
				//config
				Message m = Message.getAckMessage();
				m.addParameter("index", msg.get("index"));
				mService.send(m);
			}
			else if (msg.isTypeOf(MessageType.PING_MESSAGE))
			{
				Message m = Message.getPongMessage();
				m.addParameter("index", msg.get("index"));
				mService.send(m);

			}
			else if (msg.isTypeOf(MessageType.DESCRIBE_MESSAGE))
			{
				try
				{
					String processName = getForegroundApp2();
					Log.v(TAG, "DSC : " + processName);
					
					if(processName.equals(Configuration.PACKAGE_NAME) == false)
					{
						Log.v(TAG, "DSC : wait process name");
	        			Message retMsg = Message.getDescribeMessage();
	        			retMsg.addParameter("wait","wait");
	        			retMsg.addParameter("index", msg.get("index"));
						mService.send(message);				
					}
					else
					{
						getInstrumentation().waitForIdleSync();
						
		        		Activity activity = getActivity();
		        		
		        		if (activity != null)
		        		{
		        			OutputAbstract o = new XMLOutput();
		        			
		        			ActivityDescription ad = extractor.extract();
		        			o.addActivityDescription( ad );
		        			
		        			Message retMsg = Message.getDescribeMessage();
		        			retMsg.addParameter("index", msg.get("index"));
		        			
		        			String s = o.output();		        			
		        			retMsg.addParameter("xml", s);
		        			
		        			mService.send(retMsg);
		        		}
		        		else
		        		{
		        			Log.v(TAG, "DSC : wait activity null");
		           			Message retMsg = Message.getDescribeMessage();
		        			retMsg.addParameter("wait","wait");
		        			retMsg.addParameter("index", msg.get("index"));
							mService.send(message);	
		        		}
					}
				}
				catch(Throwable t)
				{
					t.printStackTrace();
					Message retMsg = Message.getFailMessage();
        			retMsg.addParameter("index", msg.get("index"));
					mService.send(message);	
				}
			}
			else if (msg.isTypeOf(MessageType.INPUT_MESSAGE))
			{
				String processName = getForegroundApp2();
				Log.v(TAG, "DSC : " + processName);
				
				if(processName.equals(Configuration.PACKAGE_NAME))
				{
					Integer widgetId = Integer.parseInt(msg.get("widgetId"));
					String inputType = msg.get("inputType");
					String value = msg.get("value");
					
					try
					{
						automation.setInput(widgetId, inputType, value);
						Message m = Message.getAckMessage();
						m.addParameter("index", msg.get("index"));
						mService.send(m);
					}
					catch(Throwable t)
					{
						t.printStackTrace();
						Message retMsg = Message.getFailMessage();
	        			retMsg.addParameter("index", msg.get("index"));
						mService.send(message);
					}
				}
				else
				{
					Message retMsg = Message.getFailMessage();
        			retMsg.addParameter("index", msg.get("index"));
					mService.send(message);
				}
			}
			else if (msg.isTypeOf(MessageType.EVENT_MESSAGE))
			{
				String processName = getForegroundApp2();
				Log.v(TAG, "DSC : " + processName);
				
				if(processName.equals(Configuration.PACKAGE_NAME))
				{
					String widgetId = msg.get("widgetId");
					String widgetIndexString = msg.get("widgetIndex");
					Integer widgetIndex = (widgetIndexString!=null)?Integer.parseInt(widgetIndexString):null;
					String widgetName = msg.get("widgetName");
					String widgetType = msg.get("widgetType");
					String eventType = msg.get("eventType");
					String value = msg.get("value");
					
					try
					{
						automation.fireEvent(widgetId, widgetIndex, widgetName, widgetType, eventType, value);
						Message m = Message.getAckMessage();
						m.addParameter("index", msg.get("index"));
						mService.send(m);
					}
					catch(Throwable t)
					{
						t.printStackTrace();
						Message retMsg = Message.getFailMessage();
	        			retMsg.addParameter("index", msg.get("index"));

	        			try {
							RipperTestCase.this.dumpCoverage("coverage-dump.ec");
							retMsg.addParameter("coverage_file", "coverage-dump.ec");
						} catch(Exception ex) {
							ex.printStackTrace();
						}

						mService.send(message);
					}
				}
				else
				{
					Message retMsg = Message.getFailMessage();
        			retMsg.addParameter("index", msg.get("index"));
        			
        			try {
						RipperTestCase.this.dumpCoverage("coverage-dump.ec");
						retMsg.addParameter("coverage_file", "coverage-dump.ec");
					} catch(Exception ex) {
						ex.printStackTrace();
					}
					
        			mService.send(message);
				}
			}
			else if (msg.isTypeOf(MessageType.END_MESSAGE))
			{
				testRunning = false;
				//mService.send(Message.getAckMessage());
			}
			else if (msg.isTypeOf(MessageType.HOME_MESSAGE))
			{
				//TODO:
			}
			else if (msg.isTypeOf(MessageType.COVERAGE_MESSAGE))
			{
				//TODO: add coverage_%timestamp%.ec for async download from driver
				String filename = msg.get("filename");
				//String filename = "coverage.ec";
				
				Log.v(TAG, "Dumping coverage data!");
				try {
					RipperTestCase.this.dumpCoverage(filename);
					Message m = Message.getAckMessage();
					m.addParameter("index", msg.get("index"));
					mService.send(m);
				} catch (Exception e1) {
					Message m = Message.getNAckMessage();
					m.addParameter("index", msg.get("index"));
					mService.send(m);
				}

			}
			else
			{
				Message m = Message.getNAckMessage();
				m.addParameter("index", msg.get("index"));
				mService.send(m);
			}
		}

    };
    
    protected void dumpCoverage(String filename) throws Exception {
		Log.v(TAG, "Dumping coverage data!");
		java.io.File coverageFile = new java.io.File("/data/data/" + Configuration.PACKAGE_NAME + "/" + filename); // chmod 777 from adb shell
		Class<?> emmaRTClass = Class.forName("com.vladium.emma.rt.RT");
		Method dumpCoverageMethod = emmaRTClass.getMethod("dumpCoverageData", java.io.File.class, boolean.class, boolean.class);
		// dumpCoverageMethod.invoke(null, null, false, false);
		dumpCoverageMethod.invoke(null, coverageFile, false, false);
    }

    /*
    private RunningAppProcessInfo getForegroundApp() {
        RunningAppProcessInfo result=null, info=null;

        if(mActivityManager==null)
            mActivityManager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List <RunningAppProcessInfo> l = mActivityManager.getRunningAppProcesses();
        Iterator <RunningAppProcessInfo> i = l.iterator();
        while(i.hasNext()){
            info = i.next();
            if(info.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && !isRunningService(info.processName)){
                result=info;
                break;
            }
        }
        return result;
    }

    private String  getForegroundApp2() {
    	 if(mActivityManager==null)
             mActivityManager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
    	 
         List< ActivityManager.RunningTaskInfo > taskInfo = mActivityManager.getRunningTasks(1);
    	 ComponentName componentInfo = taskInfo.get(0).topActivity;
    	 
    	 return componentInfo.getPackageName();
    }
    
    private ComponentName getActivityForApp(RunningAppProcessInfo target){
        ComponentName result=null;
        ActivityManager.RunningTaskInfo info;

        if(target==null)
            return null;

        if(mActivityManager==null)
            mActivityManager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List <ActivityManager.RunningTaskInfo> l = mActivityManager.getRunningTasks(9999);
        Iterator <ActivityManager.RunningTaskInfo> i = l.iterator();

        while(i.hasNext()){
            info=i.next();
            if(info.baseActivity.getPackageName().equals(target.processName)){
                result=info.topActivity;
                break;
            }
        }

        return result;
    }

    private boolean isStillActive(RunningAppProcessInfo process, ComponentName activity)
    {
        // activity can be null in cases, where one app starts another. for example, astro
        // starting rock player when a move file was clicked. we dont have an activity then,
        // but the package exits as soon as back is hit. so we can ignore the activity
        // in this case
        if(process==null)
            return false;

        RunningAppProcessInfo currentFg=getForegroundApp();
        ComponentName currentActivity=getActivityForApp(currentFg);

        if(currentFg!=null && currentFg.processName.equals(process.processName) &&
                (activity==null || currentActivity.compareTo(activity)==0))
            return true;

        Log.i(TAG, "isStillActive returns false - CallerProcess: " + process.processName + " CurrentProcess: "
                + (currentFg==null ? "null" : currentFg.processName) + " CallerActivity:" + (activity==null ? "null" : activity.toString())
                + " CurrentActivity: " + (currentActivity==null ? "null" : currentActivity.toString()));
        return false;
    }

    private boolean isRunningService(String processname){
        if(processname==null || processname.isEmpty())
            return false;

        RunningServiceInfo service;

        if(mActivityManager==null)
            mActivityManager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List <RunningServiceInfo> l = mActivityManager.getRunningServices(9999);
        Iterator <RunningServiceInfo> i = l.iterator();
        while(i.hasNext()){
            service = i.next();
            if(service.process.equals(processname))
                return true;
        }

        return false;
    }
    */
    private String  getForegroundApp2() {
    	try
    	{
    		if (mService != null)
    			return mService.getForegroundProcess();
    	} catch (Throwable t) {}
    	
    	return "";
    }
}
