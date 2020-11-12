package org.architecturemining.samplingframework.models;

import java.util.Collection;

public interface DirectlyFollowsFrequencyMatrix<T> {
	
	public boolean exists(T from, T to);
	public int frequency(T from, T to);
	
	public Collection<T> getTo(T from);
	public Collection<T> getFrom();

}
