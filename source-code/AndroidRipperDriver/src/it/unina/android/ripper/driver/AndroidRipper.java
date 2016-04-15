package it.unina.android.ripper.driver;

import java.io.File;

import it.unina.android.ripper.driver.random.RandomRipperStarter;
import it.unina.android.ripper.driver.systematic.SystematicRipperStarter;
import it.unina.android.ripper.driver.systematic.TestCasesSystematicRipperStarter;

public class AndroidRipper {

	public static void main(String[] args) {

		boolean noProblem = false;
		
		System.out.println("Android Ripper");
		
		if (args.length < 2) {
			System.out.println("ERROR: You haven't specified needed parameters!");
		} else {
			if (args[0] != null && (args[0].equals("systematic") || args[0].equals("s")) ) {
				if (checkConfigurationFile(args[1])) {
					new SystematicRipperStarter(args[1]).startRipping();
				} else {
					System.out.println("ERROR: Configuration file not found!");
				}
			} else if (args[0] != null && (args[0].equals("random") || args[0].equals("r")) ) {
				if (checkConfigurationFile(args[1])) {
					new RandomRipperStarter(args[1]).startRipping();
				} else {
					System.out.println("ERROR: Configuration file not found!");
				}
			} else if (args[0] != null && (args[0].equals("systematic-with-test-cases") || args[0].equals("tc")) ) {
				if (checkConfigurationFile(args[1])) {
					new TestCasesSystematicRipperStarter(args[1]).startRipping();
				} else {
					System.out.println("ERROR: Configuration file not found!");
				}
			}
		}
		
		if (noProblem == false) {
			printUsageInstructions();
		}
	}
	
	public static boolean checkConfigurationFile(String fileName) {
		if (new File(fileName).exists()) {
			return true;
		} else {
			return false;
		}
	}

	public static void printUsageInstructions() {
		System.out.println();
		System.out.println("Usage: java -jar AndroidRipper.jar [s|r] config.properties");
		System.out.println();
		System.out.println("Parameter 1:");
		System.out.println("\ts or systematic\t\tsystematic exploration");
		System.out.println("\tr or random\t\trandom exploration");
		System.out.println();
		System.out.println("Parameter 2:");
		System.out.println("- configuration file name and path");
		System.out.println();
		System.out.println();
	}
}
