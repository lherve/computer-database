package com.excilys.projet.computerdb.dao;

import java.sql.SQLException;
import java.util.Collection;

public interface Dao<T> {

	T insert(T t) throws SQLException;
	
	T update(T t) throws SQLException;
	
	boolean delete(T t) throws SQLException;
	
	T get(int id) throws SQLException;

	Collection<T> getFromTo(int start, int end, Sort sortedBy, Order order, String search) throws SQLException;
	
	Collection<T> getAll(Sort sortedBy, Order order) throws SQLException;
	
	int count(String search) throws SQLException;
	
	enum Sort {
		ID,
		NAME,
		INTRODUCED,
		DISCONTINUED,
		COMPANY;
	}
	
	enum Order {
		ASC,
		DESC;
	}
	
}
