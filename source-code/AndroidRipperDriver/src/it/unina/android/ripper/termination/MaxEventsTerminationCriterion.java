package it.unina.android.ripper.termination;

import it.unina.android.ripper.driver.AbstractDriver;

public class MaxEventsTerminationCriterion implements TerminationCriterion {

	AbstractDriver driver;
	int MAX_EVENTS = 0;
	
	public MaxEventsTerminationCriterion(int MAX_EVENTS) {
		super();
		this.MAX_EVENTS = MAX_EVENTS;
	}
	
	@Override
	public void init(AbstractDriver driver) {

		this.driver = driver;

	}

	@Override
	public boolean check() {
		return (this.driver.nEvents > MAX_EVENTS);
	}

}
