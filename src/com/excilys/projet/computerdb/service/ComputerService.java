package com.excilys.projet.computerdb.service;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.projet.computerdb.dao.Dao.Order;
import com.excilys.projet.computerdb.dao.Dao.Sort;
import com.excilys.projet.computerdb.daoImpl.CompanyDao;
import com.excilys.projet.computerdb.daoImpl.ComputerDao;
import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.model.Computer;
import com.excilys.projet.computerdb.model.Page;
import com.excilys.projet.computerdb.utils.Connector;
import com.mysql.jdbc.StringUtils;

public enum ComputerService {

	I;

	private final static Logger logger = LoggerFactory.getLogger(ComputerService.class);
	
	public Computer getComputer(int id) {
		Computer cpu = null;
		
		try {
			cpu = ComputerDao.I.get(id);
		} catch (SQLException e) {
			logger.warn("Service - get computer:"+e.getMessage());
			logger.warn("Service - get "+id);
		}
		
		Connector.JDBC.closeConnection();
		
		return cpu;
	}

	public Computer updateComputer(Computer cpu) {
		boolean commit = true;
		
		try {
			checkCompany(cpu);
		}
		catch (SQLException e) {
			logger.warn("Service - check company:"+e.getMessage());
			logger.warn("Service - check "+cpu);
			commit = false;
		}
		
		if(cpu.getId() > 0) {
			try {
				cpu = ComputerDao.I.update(cpu);
			} catch (SQLException e) {
				logger.warn("Service - update computer:"+e.getMessage());
				logger.warn("Service - update "+cpu);
				commit = false;
			}
		}
		else {
			try {
				cpu = ComputerDao.I.insert(cpu);
			} catch (SQLException e) {
				logger.warn("Service - insert computer:"+e.getMessage());
				logger.warn("Service - insert "+cpu);
				commit = false;
			}
		}
		
		if(commit) {
			try {
				Connector.JDBC.getConnection().commit();
			} catch (SQLException e) {
				logger.warn("Service - update computer commit failed");
			}
		}
		else {
			try {
				Connector.JDBC.getConnection().rollback();
			} catch (SQLException e) {
				logger.warn("Service - update computer rollback failed");
			}
		}
		
		Connector.JDBC.closeConnection();
		
		return cpu;
	}
	
	/* Méthode utilisée lors de l'ajout ou la mise-à-jour de computers 
	 * pour valider la company associée
	 */
	private Company checkCompany(Computer o) throws SQLException {
		Company c = o != null ? o.getCompany() : null;
		
		if(c != null) {
			
			if(c.getId() > 0 && (StringUtils.isEmptyOrWhitespaceOnly(c.getName()))) {
				
				Company search = CompanyDao.I.get(c.getId());
				
				if(search != null) {
					c = search;
				}
				else {
					c = null;
				}
				
			}
			else if(c.getId() <= 0 && (!StringUtils.isEmptyOrWhitespaceOnly(c.getName()))) {
				
				c = CompanyDao.I.insert(c);
				
			}
			else {
				
				c = null;
				
			}
			
			o.setCompany(c);
		}
		
		return c;
	}
	
	public boolean deleteComputer(int id) {
		boolean result = false;
		
		boolean commit = true;
		
		try {
			result = ComputerDao.I.delete(new Computer(id, null));
		} catch (SQLException e) {
			logger.warn("Service - delete computer:"+e.getMessage());
			logger.warn("Service - delete "+id);
			commit = false;
		}
		
		if(commit) {
			try {
				Connector.JDBC.getConnection().commit();
			} catch (SQLException e) {
				logger.warn("Service - update computer commit failed");
			}
		}
		else {
			try {
				Connector.JDBC.getConnection().rollback();
			} catch (SQLException e) {
				logger.warn("Service - update computer rollback failed");
			}
		}
		
		Connector.JDBC.closeConnection();
		
		return result;
	}

	public List<Company> getCompanies() {
		List<Company> companies = CompanyDao.I.getAll(Sort.NAME, Order.ASC);
		
		Connector.JDBC.closeConnection();
		
		return companies;
	}

	public int count(String search) {
		int i = -1;
		
		try {
			i =  ComputerDao.I.count(search);
		}
		catch (SQLException e) {
			logger.warn("Service - count computers:"+e.getMessage());
			logger.warn("Service - count with "+search);
		}
		
		Connector.JDBC.closeConnection();
		
		return i;
	}
	
	public Page loadPage(int number, int sort, String search) {
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
			p.setCpus(ComputerDao.I.getFromTo(p.getStart(), p.getEnd(), p.getSort(), p.getOrder(), p.getSearch()));
		} catch (SQLException e) {
			logger.warn("Service - load page:"+e.getMessage());
			logger.warn("Service - load "+p);
		}
	
		Connector.JDBC.closeConnection();
		
		return p;
	}

}
