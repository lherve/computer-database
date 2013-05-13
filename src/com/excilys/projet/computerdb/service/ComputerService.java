package com.excilys.projet.computerdb.service;

import java.util.List;

import com.excilys.projet.computerdb.dao.Dao.Order;
import com.excilys.projet.computerdb.dao.Dao.Sort;
import com.excilys.projet.computerdb.daoImpl.CompanyDao;
import com.excilys.projet.computerdb.daoImpl.ComputerDao;
import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.model.Computer;

public enum ComputerService {

	I;
	
	public boolean deleteComputer(int id) {
		Computer cpu = new Computer(id, "");
		return ComputerDao.I.delete(cpu);
	}
	
	public List<Company> getCompanies() {
		return CompanyDao.I.getAll(Sort.NAME, Order.ASC);
	}
	
}
