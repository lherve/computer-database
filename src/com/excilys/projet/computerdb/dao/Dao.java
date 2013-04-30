package com.excilys.projet.computerdb.dao;

import java.util.Collection;

public interface Dao {

	public boolean insert(Object o);
	
	public boolean update(Object o);
	
	public boolean delete(Object o);
	
	public Object get(int id);

	public Collection<Object> getFromTo(int start, int end);
	
	public Collection<Object> getAll();
	
}
