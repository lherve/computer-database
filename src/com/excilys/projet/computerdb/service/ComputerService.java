package com.excilys.projet.computerdb.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.projet.computerdb.dao.Dao.Order;
import com.excilys.projet.computerdb.dao.Dao.Sort;
import com.excilys.projet.computerdb.daoImpl.CompanyDao;
import com.excilys.projet.computerdb.daoImpl.ComputerDao;
import com.excilys.projet.computerdb.db.Connector;
import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.model.Computer;
import com.excilys.projet.computerdb.model.Page;

public enum ComputerService {

	I;

	private final static Logger logger = LoggerFactory.getLogger(ComputerService.class);
	
	public Computer getComputer(int id) {
		Connection con = Connector.JDBC.getConnection();
		
		Computer cpu = null;
		
		try {
			cpu = ComputerDao.I.get(id, con);
		} catch (SQLException e) {
			logger.warn("Service - get computer:"+e.getMessage());
			logger.warn("Service - get "+id);
		}
		
		Connector.JDBC.closeConnection(con);
		
		return cpu;
	}

	public Computer updateComputer(Computer cpu) {
		Connection con = Connector.JDBC.getConnection();
		
		if(cpu.getId() > 0) {
			try {
				cpu = ComputerDao.I.update(cpu, con);
			} catch (SQLException e) {
				logger.warn("Service - update computer:"+e.getMessage());
				logger.warn("Service - update "+cpu);
			}
		}
		else {
			try {
				cpu = ComputerDao.I.insert(cpu, con);
			} catch (SQLException e) {
				logger.warn("Service - insert computer:"+e.getMessage());
				logger.warn("Service - insert "+cpu);
			}
		}
		
		Connector.JDBC.closeConnection(con);
		
		return cpu;
	}
	
	public boolean deleteComputer(int id) {
		Connection con = Connector.JDBC.getConnection();
		
		boolean result = false;
		
		try {
			result = ComputerDao.I.delete(new Computer(id, null), con);
		} catch (SQLException e) {
			logger.warn("Service - delete computer:"+e.getMessage());
			logger.warn("Service - delete "+id);
		}
		
		Connector.JDBC.closeConnection(con);
		
		return result;
	}

	public List<Company> getCompanies() {
		Connection con = Connector.JDBC.getConnection();
		
		List<Company> companies = CompanyDao.I.getAll(Sort.NAME, Order.ASC, con);
		
		Connector.JDBC.closeConnection(con);
		
		return companies;
	}

	public int count(String search) {
		Connection con = Connector.JDBC.getConnection();
		
		int i = -1;
		
		try {
			i =  ComputerDao.I.count(search, con);
		}
		catch (SQLException e) {
			logger.warn("Service - count computers:"+e.getMessage());
			logger.warn("Service - count with "+search);
		}
		
		Connector.JDBC.closeConnection(con);
		
		return i;
	}
	
	public Page loadPage(int number, int sort, String search) {
		Connection con = Connector.JDBC.getConnection();
		
		Page p = new Page(number, search);

		if(number < 0) {
			p.setPrevious(new Page(-(number + 1)));
			p.setNext(new Page(-number));
		}
		else {
			p.setNext(new Page(number + 1));
			if(number > 0) {
				p.setPrevious(new Page(number - 1));
			}
			else {
				p.setPrevious(new Page(number));
			}
		}

		number = Math.abs(number);
		
		p.setStart(number * Page.SIZE + 1);
		p.setEnd((number + 1) * Page.SIZE);
		
		if(sort < 0) {
			p.setOrder(Order.DESC);
		}
		else {
			p.setOrder(Order.ASC);
		}
		
		sort = Math.abs(sort);
		
		switch(sort) {
		case 2:
			p.setSort(Sort.INTRODUCED);
			break;
		case 3:
			p.setSort(Sort.DISCONTINUED);
			break;
		case 4:
			p.setSort(Sort.COMPANY);
			break;
		default:
			p.setSort(Sort.NAME);
		}
		
		try {
			p.setCpus(ComputerDao.I.getFromTo(p.getStart(), p.getEnd(), p.getSort(), p.getOrder(), p.getSearch(), con));
		} catch (SQLException e) {
			logger.warn("Service - load page:"+e.getMessage());
			logger.warn("Service - load "+p);
		}
	
		Connector.JDBC.closeConnection(con);
		
		return p;
	}

}
