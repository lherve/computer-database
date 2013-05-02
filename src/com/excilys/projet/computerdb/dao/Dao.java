package com.excilys.projet.computerdb.dao;

import java.util.Collection;

public interface Dao<T> {

	public T insert(T t);
	
	public T update(T t);
	
	public boolean delete(T t);
	
	public T get(int id);

	public Collection<T> getFromTo(int start, int end);
	
	public Collection<T> getAll();
	
	public int count();
	
}
