package org.architecturemining.samplingframework.models.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.architecturemining.samplingframework.models.DirectlyFollowsFrequencyMatrix;


/**
 * 
 * @author jmw
 *
 * @param <T>
 * 
 * This is a simple implementation using adjacency sets to implement the matrix.  
 */
public class DirectlyFollowsFrequencyMatrixImpl<T> implements DirectlyFollowsFrequencyMatrix<T> {
	
	private class Item {
		private int frequency;
		
		public Item(int value) {
			set(value);
		}
		
		public void add(int toAdd) {
			frequency += toAdd;
		}
		
		public void set(int value) {
			frequency = value;
		}
		
		public int value() {
			return frequency;
		}
	}
	
	private HashMap<T, HashMap<T, Item>> mapping = new HashMap<>();
	
	@Override
	public boolean exists(T from, T to) {
		return (frequency(from, to) > 0);
	}
	
	@Override
	public int frequency(T from, T to) {
		if(mapping.containsKey(from)) {
			if (mapping.get(from).containsKey(to)) {
				return mapping.get(from).get(to).value();
			}
		}
		return 0;
	}
	
	public void increment(T from, T to) {
		add(from, to, 1);
	}
	
	public void add(T from, T to, int toAdd) {
		if (! mapping.containsKey(from)) {
			mapping.put(from, new HashMap<>());
		}
		if (!mapping.get(from).containsKey(to)) {
			mapping.get(from).put(to, new Item(0));
		}
		mapping.get(from).get(to).add(toAdd);
	}
	
	public void set(T from, T to, int value) {
		if (! mapping.containsKey(from)) {
			mapping.put(from, new HashMap<>());
		}
		if (!mapping.get(from).containsKey(to)) {
			mapping.get(from).put(to, new Item(0));
		}
		
		mapping.get(from).get(to).set(value);
	}
	
	public void clear() {
		mapping.clear();
	}

	public Collection<T> getTo(T from) {
		if (mapping.containsKey(from)) {
			return mapping.get(from).keySet();
		} else {
			return Collections.emptySet();
		}
	}

	public Collection<T> getFrom() {
		return mapping.keySet();
	}

}
