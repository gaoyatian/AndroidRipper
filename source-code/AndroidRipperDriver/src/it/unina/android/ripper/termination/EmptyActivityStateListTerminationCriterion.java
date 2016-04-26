package it.unina.android.ripper.termination;

import it.unina.android.ripper.driver.AbstractDriver;
import it.unina.android.ripper.model.TaskList;

public class EmptyActivityStateListTerminationCriterion implements TerminationCriterion {

	TaskList mTaskList;

	public EmptyActivityStateListTerminationCriterion() {
		super();
	}

	@Override
	public void init(AbstractDriver driver) {	
		this.mTaskList = driver.getScheduler().getTaskList();
	}
	
	@Override
	public boolean check() {
		return mTaskList.isEmpty();
	}

}
