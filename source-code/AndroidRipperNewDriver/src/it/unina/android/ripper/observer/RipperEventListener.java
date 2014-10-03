package it.unina.android.ripper.observer;

public interface RipperEventListener {
	public void ripperLog(String log);
	public void ripperStatusUpdate(String status);
	public void ripperTaskEneded();
	public void ripperEneded();
}
