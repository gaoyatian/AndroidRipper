package it.unina.android.ripper.termination;

import it.unina.android.ripper.driver.AbstractDriver;

public interface TerminationCriterion {
	public void init(AbstractDriver driver);
	public boolean check();
}
