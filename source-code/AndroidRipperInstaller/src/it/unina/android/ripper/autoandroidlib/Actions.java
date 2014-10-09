package it.unina.android.ripper.autoandroidlib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;

import com.googlecode.autoandroid.lib.AndroidTools;

public class Actions {

	public static int ANDROID_RIPPER_SERVICE_WAIT_SECONDS = 3;
	public static int ANDROID_RIPPER_WAIT_SECONDS = 3;
	public static int START_EMULATOR_NO_SNAPSHOOT_WAIT_SECONDS = 0;
	public static int START_EMULATOR_SNAPSHOOT_WAIT_SECONDS = 0;
	
	public static AndroidTools tools = AndroidTools.get();

	//anche tramite adb
	public static void sendMessageToEmualtor(int port, String message)
	{
		try {
			Socket socket = new Socket("localhost",port);
			
			sleepSeconds(1);
			
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println(message);
			out.flush();
			out.close();
			socket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void sendBackKey()
	{
		try {
			tools.adb("shell", "input", "keyevent", "4").waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public static void killAll()
	{
		/*
		try {
			tools.adb("shell", "am kill-all");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/		
	}
	
	public static void sendHomeKey()
	{
		try {
			tools.adb("shell", "input", "keyevent", "3").waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void startAndroidRipperService()
	{
		try {
			tools.adb("shell", "am startservice -a it.unina.android.ripper_service.ANDROID_RIPPER_SERVICE").connectStdout(System.out).connectStderr(System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sleepSeconds(ANDROID_RIPPER_SERVICE_WAIT_SECONDS);
	}
	
	public static void sleepSeconds(int seconds)
	{
		sleepMilliSeconds(seconds * 1000);
	}
	
	public static void sleepMilliSeconds(int milli)
	{
		try {
			Thread.sleep(milli);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean ripperActive = false;
	
	public static boolean isRipperActive()
	{
		return ripperActive;
	}
	
	public static void setRipperActive(boolean b)
	{
		ripperActive = b;
	}
	
	public static void startAndroidRipper(String AUT_PACKAGE)
	{
		createAUTFilesDir(AUT_PACKAGE);
		
		new Thread() {
			public void run()
			{
				try {
					ripperActive = true;
					tools.adb("shell", "am instrument -w -e coverage true -e class it.unina.android.ripper.RipperTestCase it.unina.android.ripper/pl.polidea.instrumentation.PolideaInstrumentationTestRunner").connectStdout(System.out).connectStderr(System.out).waitFor() ;
					ripperActive = false;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//.connectStdout(System.out).connectStderr(System.err);		
			}
		}.start();			

		sleepSeconds(ANDROID_RIPPER_WAIT_SECONDS);
	}
	
	public static void createAUTFilesDir(String AUT_PACKAGE)
	{
		try {
			tools.adb("shell", "mkdir","/data/data/"+AUT_PACKAGE+"/files");
			tools.adb("shell", "chmod", "-R", "777", "/data/data/"+AUT_PACKAGE+"/files");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//sleepSeconds(3);
	}
	
	public static void startEmulatorNoSnapshotLoad(final String AVD_NAME, final int EMULATOR_PORT)
	{
			(new Thread() {
				public void run()
				{
					try {
						tools.emulator("-avd",AVD_NAME,"-no-snapshot-load", "-port",Integer.toString(EMULATOR_PORT)).connectStdout(System.out).connectStderr(System.err).waitForSuccess();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
			
				
			sleepSeconds(START_EMULATOR_NO_SNAPSHOOT_WAIT_SECONDS);
	}
	
	public static void startEmulatorNoSnapshotLoadWipeData(final String AVD_NAME, final int EMULATOR_PORT)
	{
			(new Thread() {
				public void run()
				{
					try {
						tools.emulator("-avd",AVD_NAME,"-no-snapshot-load", "-wipe-data" , "-port",Integer.toString(EMULATOR_PORT)).connectStdout(System.out).connectStderr(System.err).waitForSuccess();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
			
				
			sleepSeconds(START_EMULATOR_NO_SNAPSHOOT_WAIT_SECONDS);
	}
	
	public static void startEmulatorNoSnapshotSave(final String AVD_NAME, final int EMULATOR_PORT)
	{
		try {
			tools.emulator("-avd", AVD_NAME,"-no-snapshot-save", "-port",Integer.toString(EMULATOR_PORT)).connectStdout(System.out).connectStderr(System.out);
			
			sleepSeconds(START_EMULATOR_SNAPSHOOT_WAIT_SECONDS);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//adb pull %/data/data/%APPPACKAGE%/files% %FILESPATH%
	public static void pullCoverage(final String AUT_PACKAGE, final String COV_PATH, final int COV_COUNTER)
	{
		pullCoverage(AUT_PACKAGE, "coverage.ec", COV_PATH, COV_COUNTER);
	}
	
	public static void pullCoverage(final int EMULATOR_PORT, final String AUT_PACKAGE, final String COV_PATH, final int COV_COUNTER)
	{
		pullCoverage(EMULATOR_PORT, AUT_PACKAGE, "coverage.ec", COV_PATH, COV_COUNTER);
	}
	
	public static void pullCoverage(final String AUT_PACKAGE, final String COV_FILE, final String COV_PATH, final int COV_COUNTER)
	{
		DecimalFormat num = new DecimalFormat("00000");
		
		String src = "/data/data/"+AUT_PACKAGE+"/"+COV_FILE;
		String dest = COV_PATH+"coverage"+ num.format(COV_COUNTER) +".ec";

		try {
			tools.adb("pull", src, dest).connectStderr(System.out).connectStdout(System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}
	
	public static void pullCoverage(final int EMULATOR_PORT, final String AUT_PACKAGE, final String COV_FILE, final String COV_PATH, final int COV_COUNTER)
	{
		DecimalFormat num = new DecimalFormat("00000");
		
		String src = "/data/data/"+AUT_PACKAGE+"/"+COV_FILE;
		String dest = COV_PATH+"coverage"+ num.format(COV_COUNTER) +".ec";

		try {
			tools.adb("-s", "emulator-"+EMULATOR_PORT, "pull", src, dest).connectStderr(System.out).connectStdout(System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void pullCoverageStandardFile(final String AUT_PACKAGE, final String COV_PATH, final int COV_COUNTER)
	{
			//Actions.sleepSeconds(3);
			DecimalFormat num = new DecimalFormat("00000");
			
			String src = "/data/data/"+AUT_PACKAGE+"/files/coverage.ec";
			String dest = COV_PATH+"coverage"+ num.format(COV_COUNTER) +"_ec.ec";

			try {
				tools.adb("pull", src, dest).connectStderr(System.out).connectStdout(System.out).waitFor();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Actions.sleepSeconds(3);
	}
	
	public static void pullCoverageStandardFile(final int EMULATOR_PORT, final String AUT_PACKAGE, final String COV_PATH, final int COV_COUNTER)
	{
		//Actions.sleepSeconds(3);
		DecimalFormat num = new DecimalFormat("00000");
		
		String src = "/data/data/"+AUT_PACKAGE+"/files/coverage.ec";
		String dest = COV_PATH+"coverage"+ num.format(COV_COUNTER) +"_ec.ec";

		try {
			tools.adb("-s", "emulator-"+EMULATOR_PORT, "pull", src, dest).connectStderr(System.out).connectStdout(System.out).waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Actions.sleepSeconds(3);
	}
	
	public static void pullJUnitLog(final String AUT_PACKAGE, final String DEST_PATH, final int COV_COUNTER)
	{
		DecimalFormat num = new DecimalFormat("00000");
		
		String src = "/data/data/"+AUT_PACKAGE+"/files/it.unina.android.ripper-TEST.xml";
		String dest = DEST_PATH+"junit-log-"+ num.format(COV_COUNTER) +".xml";

		try {
			tools.adb("pull", src, dest).connectStderr(System.out).connectStdout(System.out).waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void pullJUnitLog(final int EMULATOR_PORT, final String AUT_PACKAGE, final String DEST_PATH, final int COV_COUNTER)
	{
		DecimalFormat num = new DecimalFormat("00000");
		
		String src = "/data/data/"+AUT_PACKAGE+"/files/it.unina.android.ripper-TEST.xml";
		String dest = DEST_PATH+"junit-log-"+ num.format(COV_COUNTER) +".xml";

		try {
			tools.adb("-s", "emulator-"+EMULATOR_PORT, "pull", src, dest).connectStderr(System.out).connectStdout(System.out).waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void waitForEmulatorOnline(Integer avdPort) {

		boolean waitingEmulatorBoot = true;

		do {

			try {
				final Process p = Runtime.getRuntime().exec("adb devices");

				try {
					String line = "";
					BufferedReader input = new BufferedReader(
							new InputStreamReader(p.getInputStream()));
					while ((line = input.readLine()) != null) {
						if (line != null && line.contains(avdPort.toString())) {
							if (line.contains("device")) {
								waitingEmulatorBoot = false;
							}
						}
					}
					input.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				p.waitFor();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} while (waitingEmulatorBoot);
		
	}
	
	public static void waitForEmulatorBoot(Integer avdPort) {

		boolean waitingEmulatorOnline = true;

		do {

			try {
				final Process p = Runtime.getRuntime().exec("adb -s emulator-"+avdPort+" shell getprop init.svc.bootanim");

				try {
					String line = "";
					BufferedReader input = new BufferedReader(
							new InputStreamReader(p.getInputStream()));
					while ((line = input.readLine()) != null) {
							if (line.contains("stopped")) {
								waitingEmulatorOnline = false;
							}
					}
					input.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				p.waitFor();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} while (waitingEmulatorOnline);
		
	}
	
	public static void waitForProcessToEnd(String AUT_PACKAGE, Integer avdPort) {

		boolean found = false;

		do {

			found = false;
			
			try {
				final Process p = Runtime.getRuntime().exec("adb -s emulator-"+avdPort+" shell ps " + AUT_PACKAGE);

				try {
					String line = "";
					BufferedReader input = new BufferedReader(
							new InputStreamReader(p.getInputStream()));
					
					while ((line = input.readLine()) != null) {
						if (line.contains(AUT_PACKAGE)) {
							found = true;
						}
					}
					
					input.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				p.waitFor();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} while (found);
		
	}
	
	public static boolean killProcessByPackage(String AUT_PACKAGE, String avdPort) {
		String pid = getProcessPID(AUT_PACKAGE, avdPort);
		return killProcess(pid);
	}
	
	public static boolean killProcess(String PID) {
		if (PID != null) 
		{
			try {
				tools.adb("shell", "ps", "-9", PID ).waitFor();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	public static String getProcessPID(String AUT_PACKAGE, String avdPort) {

		String pid = null;
		
		try {
			final Process p = Runtime.getRuntime().exec("adb -s emulator-"+avdPort+" shell ps " + AUT_PACKAGE);

			try {
				String line = "";
				BufferedReader input = new BufferedReader(
						new InputStreamReader(p.getInputStream()));
				
				while ((line = input.readLine()) != null) {
					if (line.contains(AUT_PACKAGE)) {
						String[] split = line.split(" +", -3);
						pid = split[1];
					}
				}
				
				input.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			p.waitFor();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return pid;
	}
	
	public static void waitForEmulator(Integer avdPort) {
		waitForEmulatorOnline(avdPort);
		System.out.println("Emulator Online!");
		waitForEmulatorBoot(avdPort);
		System.out.println("Emulator Booted!");
	}

	public static void waitEmulatorClosed(Integer avdPort) {
		boolean waitingEmulatorClose = false;

		do {

			waitingEmulatorClose = false;
			
			try {
				final Process p = Runtime.getRuntime().exec("adb devices");

				try {
					String line = "";
					BufferedReader input = new BufferedReader(
							new InputStreamReader(p.getInputStream()));
					while ((line = input.readLine()) != null) {
						if (line != null && line.contains(avdPort.toString())) {
							if (line.contains("device")) {
								waitingEmulatorClose = true;
							}
						}
					}
					input.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				p.waitFor();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} while (waitingEmulatorClose);
		
		System.out.println("Emulator offline!");
		
	}

	public static boolean waitForProcessToEndMaxIterations(String AUT_PACKAGE, int avdPort, int maxIter) {
		boolean found = false;

		int iter = 0;
		
		do {

			found = false;
			
			try {
				final Process p = Runtime.getRuntime().exec("adb -s emulator-"+avdPort+" shell ps " + AUT_PACKAGE);

				try {
					String line = "";
					BufferedReader input = new BufferedReader(
							new InputStreamReader(p.getInputStream()));
					
					while ((line = input.readLine()) != null) {
						if (line.contains(AUT_PACKAGE)) {
							found = true;
						}
					}
					
					input.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				p.waitFor();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		} while (found && ++iter <= maxIter );
		
		return (iter > maxIter);
	}
	
}
	
