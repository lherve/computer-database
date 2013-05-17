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
import com.excilys.projet.computerdb.utils.CompaniesList;
import com.excilys.projet.computerdb.utils.Connector;
import com.mysql.jdbc.StringUtils;

public enum ComputerService {

	I;

	private final static Logger logger = LoggerFactory.getLogger(ComputerService.class);

	
	/* 
	 * Méthode utilisée par UpdateComputer pour récupérer les informations du computer à éditer
	 */
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

	
	/* 
	 * Méthode utilisée par UpdateComputer pour l'insertion et la mise-à-jour du computer
	 */
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
	
	
	/* 
	 * Méthode utilisée lors de l'ajout ou la mise-à-jour de computers 
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
	
	
	/* 
	 * Méthode utilisée par DeleteComputer pour la suppression du computer
	 */
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
				logger.warn("Service - delete computer commit failed");
			}
		}
		else {
			try {
				Connector.JDBC.getConnection().rollback();
			} catch (SQLException e) {
				logger.warn("Service - delete computer rollback failed");
			}
		}
		
		Connector.JDBC.closeConnection();
		
		return result;
	}

	public List<Company> getCompanies() {
		return CompaniesList.getInstance().getList();
	}

	
	/*
	 * Méthode utilisée par ListComputers pour obtenir des computers à afficher
	 */
	public Page loadPage(int number, int sort, String search) {
		
		int count = 0;
		
		try {
			count = ComputerDao.I.count(null);
		} catch (SQLException e) {
			logger.warn("Service - load page:"+e.getMessage());
			logger.warn("Service - count total");
		}
		
		int maxPage = count / Page.SIZE;
		
		if(number > maxPage) {
			number = maxPage;
		}
		
		Page p = new Page(number, search);
		
		p.setTotal(count);
		
		p.setNext(number == maxPage ? new Page(maxPage) : new Page(number + 1));
		p.setPrevious(number == 0 ? new Page(0) : new Page(number -1));
		
		p.setStart(count == 0 ? 0 : number * Page.SIZE + 1);
		
		int last = ((number + 1) * Page.SIZE);
		p.setEnd(last > count ? count : last);
		
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
