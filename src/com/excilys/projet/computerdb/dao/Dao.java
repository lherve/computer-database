package com.excilys.projet.computerdb.dao;

import java.util.Collection;

public interface Dao<T> {

	public T insert(T t);
	
	public T update(T t);
	
	public boolean delete(T t);
	
	public T get(int id);

	public Collection<T> getFromTo(int start, int end, Sort sortedBy, Order order, String search);
	
	public Collection<T> getAll(Sort sortedBy, Order order);
	
	public int count(String search);
	
	public static enum Sort {
		ID,
		NAME,
		INTRODUCED,
		DISCONTINUED,
		COMPANY;
	}
	
	public static enum Order {
		ASC,
		DESC;
	}
	
}
