package com.excilys.projet.computerdb.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "company")
public class Company {

	/* Attributes */
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private int id;
	
	@Column(name = "name", nullable = false)
	private String name;
	
	@OneToMany(mappedBy = "company")
	private List<Computer> computers;
	
	/* Constructors */

	public Company() {
	}
	
	public Company(int id, String name) {
		this.id = id;
		this.name = name;
	}

	
	/* Getters & Setters */
	
	
	public List<Computer> getComputers() {
		return computers;
	}

	public void setComputers(List<Computer> computers) {
		this.computers = computers;
	}
	
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
	
	@Override
	public String toString() {
		return getId() + " - " + getName() ;
	}
	
	@Override
	public int hashCode() {
		if(getName() != null) return getName().hashCode() + getId();
		else return getId();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof Company)) return false;
		else return getId() == ((Company)o).getId();
	}
}
