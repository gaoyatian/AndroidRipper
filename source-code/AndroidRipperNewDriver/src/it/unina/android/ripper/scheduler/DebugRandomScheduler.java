package it.unina.android.ripper.scheduler;

import it.unina.android.ripper.model.Event;
import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.planner.task.TaskList;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Hashtable;

public class DebugRandomScheduler extends RandomScheduler {

	private class EventCounter {
		Event e;
		int count = 0;
		
		EventCounter(Event e)
		{
			count = 0;
		}
		
		public void inc()
		{
			count++;
		}
		
		public int getCount()
		{
			return count;
		}
	}
		
	//hash(Acctivity, widgetid, widgetname, eventtype), eventcounter
	private Hashtable<String, EventCounter> eventCounters = null;
	private void addEvent(Task t)
	{
		Event e = t.get(0);
		String key = ((e.getWidget()!=null)?e.getWidget().getId() + ":":"") + ((e.getWidget()!=null)?e.getWidget().getName() + ":":"") + ((e.getWidget()!=null)?e.getWidget().getTextualId() + ":":"") + ((e.getWidget()!=null)?e.getWidget().getSimpleType() + ":":"") + e.getInteraction();
		
		EventCounter evtCnt = eventCounters.get(key); 
		if (evtCnt == null)
		{
			evtCnt = new EventCounter(e);
			eventCounters.put(key, evtCnt);
		}
	}
	
	private void addEventPerformed(Task t)
	{
		Event e = t.get(0);
		String key = ((e.getWidget()!=null)?e.getWidget().getId() + ":":"") + ((e.getWidget()!=null)?e.getWidget().getName() + ":":"") + ((e.getWidget()!=null)?e.getWidget().getTextualId() + ":":"") + ((e.getWidget()!=null)?e.getWidget().getSimpleType() + ":":"") + e.getInteraction();
		
		EventCounter evtCnt = eventCounters.get(key); 	
		evtCnt.inc();
	}
	
	private void outputEventList()
	{
		String report = "";
		for (String k : eventCounters.keySet())
		{
			EventCounter ec = eventCounters.get(k);
			report += k + " -> " + ec.getCount() + "\n";
		}
		
		try
		{
			FileWriter fileWritter = new FileWriter("events.txt", false);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	        bufferWritter.write(report);
	        bufferWritter.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	
	
	public DebugRandomScheduler(long seed)
	{
		super(seed);
		eventCounters = new Hashtable<String, EventCounter>();
	}
	
	@Override
	public Task nextTask() {
		if (this.taskList.size() > 0)
		{
			int pos = (int)( random.nextInt( taskList.size() ) );
			
			Task t = taskList.get(pos);
			taskList.clear();
			
			this.addEventPerformed(t);
			outputEventList();
			return t;
		}
		else
			return null;
	}
	
	@Override
	public void addTask(Task t) {
		super.addTask(t);
		this.addEvent(t);
	}

	@Override
	public void addTasks(TaskList taskList) {
		super.addTasks(taskList);
		for(Task t : taskList)
			this.addEvent(t);
	}
}
