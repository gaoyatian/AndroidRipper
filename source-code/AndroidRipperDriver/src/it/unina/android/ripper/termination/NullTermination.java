package it.unina.android.ripper.termination;

import it.unina.android.ripper.driver.AbstractDriver;

public class NullTermination implements TerminationCriterion {

	public NullTermination() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(AbstractDriver dirver) {

	}

	@Override
	public boolean check() {
		return false;
	}

}
