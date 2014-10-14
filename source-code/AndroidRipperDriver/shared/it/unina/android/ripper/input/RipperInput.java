package it.unina.android.ripper.input;

import org.w3c.dom.Element;

import it.unina.android.ripper.model.ActivityDescription;

public interface RipperInput
{
	public ActivityDescription inputActivityDescription(Element description);
	public ActivityDescription inputActivityDescription(String description);
}
