package org.architecturemining.samplingframework.models;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class BufferedResultList<R> implements List<R> {
	
	public class ListItem {
		
	}
	
	private File store;
	
	private List<R> results = new ArrayList<>();
	
	public BufferedResultList(File file)  {
		this.store = file;
		
		if (!store.getParentFile().exists()) {
			store.getParentFile().mkdirs();			
		}
	}
	
	public void writeLine(String line) throws IOException {
		FileWriter writer = new FileWriter(store, true);
		writer.write(line + System.lineSeparator());
		
		writer.close();
	}
	
	public int size() {
		return results.size();
	}

	public boolean isEmpty() {
		return results.isEmpty();
	}

	public boolean contains(Object o) {
		return results.contains(o);
	}

	public Iterator<R> iterator() {
		return results.iterator();
	}

	public Object[] toArray() {
		return results.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return results.toArray(a);
	}

	public boolean add(R e) {
		try {
			writeLine(e.toString());
			return results.add(e);
		} catch (IOException e1) {
		}
		return false;
	}

	public boolean remove(Object o) {
		throw new NotImplementedException();
	}

	public boolean containsAll(Collection<?> c) {
		return results.containsAll(c);
	}

	public boolean addAll(Collection<? extends R> c) {
		
		try {
			FileWriter writer = new FileWriter(store, true);
			for(R r: c) {
				writer.write(r.toString());
				writer.write(System.lineSeparator());
				results.add(r);
			}
			writer.close();
			return true;
			
		} catch(IOException e) {
			return false;
		}
	}

	public boolean addAll(int index, Collection<? extends R> c) {
		throw new NotImplementedException();
	}

	public boolean removeAll(Collection<?> c) {
		throw new NotImplementedException();
	}

	public boolean retainAll(Collection<?> c) {
		throw new NotImplementedException();
	}

	public void clear() {
		results.clear();
	}

	public R get(int index) {
		return results.get(index);
	}

	public R set(int index, R element) {
		throw new NotImplementedException();
	}

	public void add(int index, R element) {
		throw new NotImplementedException();
	}

	public R remove(int index) {
		throw new NotImplementedException();
	}

	public int indexOf(Object o) {
		return results.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return results.lastIndexOf(o);
	}

	public ListIterator<R> listIterator() {
		return results.listIterator();
	}

	public ListIterator<R> listIterator(int index) {
		return results.listIterator(index);
	}

	public List<R> subList(int fromIndex, int toIndex) {
		return results.subList(fromIndex, toIndex);
	}
	
}
