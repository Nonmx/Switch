package sselab.switchy.property.factory;

import sselab.nusek.csl.Csl;
import sselab.switchy.property.CSL;
import sselab.switchy.property.Property;

public interface PropertyFactory {
	public Property createProperty(Csl csl);
	
}
