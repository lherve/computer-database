package com.excilys.projet.computerdb.model;

public class Company {

	/* Attributes */
	
	private int id;
	private String name;
	
	
	/* Constructors */
	
	public Company(int id, String name) {
		this.id = id;
		this.name = name;
	}

	
	/* Getters & Setters */
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
