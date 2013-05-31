package com.excilys.projet.computerdb.exception;

public class DBException extends RuntimeException {

	private static final long serialVersionUID = 8381769194516571949L;

	public DBException() {
	}

	public DBException(String message) {
		super(message);
	}
	
}
