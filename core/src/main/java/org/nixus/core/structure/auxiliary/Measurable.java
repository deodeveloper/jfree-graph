package org.nixus.core.structure.auxiliary;

/**
 * Represents a measurable object.
 * */
public interface Measurable<T> {
	
	/**
	 * Returns the value of the this object. For example this could be the 
	 * number of miles of a path. For now for more complex type a transformation to long is required
	 * */
	int measure();
}
