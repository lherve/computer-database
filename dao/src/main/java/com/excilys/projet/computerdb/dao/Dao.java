package com.excilys.projet.computerdb.dao;

import java.util.List;
import java.util.Observable;

import static com.excilys.projet.computerdb.model.Page.*;

public interface Dao<T> {

	T insert(T t);
	
	T update(T t);
	
	boolean delete(T t);
	
	T get(int id);

	List<T> getFromTo(int start, int end, Sort sortedBy, Order order, String search);
	
	List<T> getAll(Sort sortedBy, Order order);
	
	int count(String search);
	
	Observable getDataUpdateNotifier();
	
}
