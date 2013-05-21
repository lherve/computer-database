package com.excilys.projet.computerdb.test;

import java.sql.SQLException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.excilys.projet.computerdb.daoImpl.CompanyDao;
import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.utils.Connector;

public class CompanyDaoTest {

	Company cie;
	
	@BeforeClass
	public void beforeClass() {
	}
	
	@AfterClass
	public void afterClass() {
		Connector.JDBC.closeConnection();
	}
	
	@Test
	public void get() throws SQLException {
		Company c = CompanyDao.I.get(1);
		assert c.getName().equals("Apple Inc.");
	}
	  
	@Test
	public void insert() throws SQLException {
		Company c = new Company(0, "Company testNG");
		assert (cie = CompanyDao.I.insert(c)).getId() > 0;
	}
	
	@Test
	public void update() throws SQLException {
		cie.setName("Retest");
		assert CompanyDao.I.update(cie).getName().equals("Retest");
	}
	
	@Test
	public void delete() throws SQLException {
		assert CompanyDao.I.delete(cie);
	}
	
}
