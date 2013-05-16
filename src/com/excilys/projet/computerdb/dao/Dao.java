package com.excilys.projet.computerdb.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

public interface Dao<T> {

	T insert(T t, Connection con) throws SQLException;
	
	T update(T t, Connection con) throws SQLException;
	
	boolean delete(T t, Connection con) throws SQLException;
	
	T get(int id, Connection con) throws SQLException;

	Collection<T> getFromTo(int start, int end, Sort sortedBy, Order order, String search, Connection con) throws SQLException;
	
	Collection<T> getAll(Sort sortedBy, Order order, Connection con) throws SQLException;
	
	int count(String search, Connection con) throws SQLException;
	
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
