package org.nixus.core.structure.auxiliary;

/**
 * Represents a measurable object.
 * */
public interface Measurable<T, M> {
	
	/**
	 * Returns the value of the this object. For example this could be the 
	 * number of miles of a path
	 * */
	M measure();
}
