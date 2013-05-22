package com.excilys.projet.computerdb.model;

import java.text.DateFormat;
import java.util.Calendar;

public class Computer {

	/* Attributes */
	
	private int id;
	private String name;
	private Calendar introduced;
	private Calendar discontinued;
	private Company company;
	
	/* Constructors */
	
	public Computer(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public Computer(int id, String name, Calendar introduced, Calendar discontinued, Company company) {
		this.id = id;
		this.name = name;
		this.introduced = introduced;
		this.discontinued = discontinued;
		this.company = company;
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

	public Calendar getIntroduced() {
		return introduced;
	}
	
	public String getIntroducedToString() {
		return introduced != null ? DateFormat.getDateInstance(DateFormat.MEDIUM).format(introduced.getTime()) : "-";
	}

	public void setIntroduced(Calendar introduced) {
		this.introduced = introduced;
	}

	public Calendar getDiscontinued() {
		return discontinued;
	}
	
	public String getDiscontinuedToString() {
		return discontinued != null ? DateFormat.getDateInstance(DateFormat.MEDIUM).format(discontinued.getTime()) : "-";
	}

	public void setDiscontinued(Calendar discontinued) {
		this.discontinued = discontinued;
	}

	public Company getCompany() {
		return company;
	}
	
	public void setCompany(Company company) {
		this.company = company;
	}
	
	@Override
	public String toString() {
		return getId() + " - " + getName() + " [" + getIntroduced() + "],[" + getDiscontinued() + "] -" + getCompany();
	}
	
	@Override
	public int hashCode() {
		if(getName() != null) return getName().hashCode() + getId();
		else return getId();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof Computer)) return false;
		else return getId() == ((Company)o).getId();
	}
}
