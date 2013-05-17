package com.excilys.projet.computerdb.service;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.projet.computerdb.dao.Dao.Order;
import com.excilys.projet.computerdb.dao.Dao.Sort;
import com.excilys.projet.computerdb.daoImpl.CompanyDao;
import com.excilys.projet.computerdb.daoImpl.ComputerDao;
import com.excilys.projet.computerdb.exception.DataAccessException;
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
	public Computer getComputer(int id) throws DataAccessException {
		Computer cpu = null;
		
		try {
			cpu = ComputerDao.I.get(id);
		} catch (SQLException e) {
			logger.warn("Service - get computer:"+e.getMessage());
			logger.warn("Service - get "+ id +" (ERRCODE:"+e.getErrorCode()+")");
			
			throw new DataAccessException("Problème lors du chargement d'un computer : accès aux données impossible.");
		}
		finally {
			Connector.JDBC.closeConnection();
		}
		
		return cpu;
	}

	
	/* 
	 * Méthode utilisée par UpdateComputer pour l'insertion et la mise-à-jour du computer
	 */
	public Computer updateComputer(Computer cpu) {
		boolean commit = true;
		
		checkCompany(cpu);
		
		if(cpu.getId() > 0) {
			try {
				cpu = ComputerDao.I.update(cpu);
			} catch (SQLException e) {
				logger.warn("Service - update computer:"+e.getMessage());
				logger.warn("Service - update "+ cpu +" (ERRCODE:"+e.getErrorCode()+")");
				commit = false;
			}
		}
		else {
			try {
				cpu = ComputerDao.I.insert(cpu);
			} catch (SQLException e) {
				logger.warn("Service - insert computer:"+e.getMessage());
				logger.warn("Service - insert "+ cpu +" (ERRCODE:"+e.getErrorCode()+")");
				commit = false;
			}
		}
		
		if(commit) {
			try {
				Connector.JDBC.getConnection().commit();
			} catch (SQLException e) {
				logger.warn("Service - update computer commit failed (ERRCODE:"+e.getErrorCode()+")");
			}
		}
		else {
			try {
				Connector.JDBC.getConnection().rollback();
			} catch (SQLException e) {
				logger.warn("Service - update computer rollback failed (ERRCODE:"+e.getErrorCode()+")");
			}
		}
		
		Connector.JDBC.closeConnection();
		
		return cpu;
	}
	
	
	/* 
	 * Méthode utilisée lors de l'ajout ou la mise-à-jour de computers 
	 * pour valider la company associée
	 */
	private Company checkCompany(Computer o) {
		if(o.getCompany() != null) {
			Company c = null;
			
			try {
				for(Company e : CompaniesList.getInstance().getList()) {
					if(e.getId() == o.getCompany().getId()) {
						c = e;
						break;
					}
				}
			} catch (DataAccessException e) {
			}
			
			o.setCompany(c);
		}
		
		return o.getCompany();
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
			logger.warn("Service - delete "+ id +" (ERRCODE:"+e.getErrorCode()+")");
			commit = false;
		}
		
		if(commit) {
			try {
				Connector.JDBC.getConnection().commit();
			} catch (SQLException e) {
				logger.warn("Service - delete computer commit failed (ERRCODE:"+e.getErrorCode()+")");
			}
		}
		else {
			try {
				Connector.JDBC.getConnection().rollback();
			} catch (SQLException e) {
				logger.warn("Service - delete computer rollback failed (ERRCODE:"+e.getErrorCode()+")");
			}
		}
		
		Connector.JDBC.closeConnection();
		
		return result;
	}

	
	/*
	 * Méthode utilisée par ListComputers pour obtenir des computers à afficher
	 */
	public Page loadPage(int number, int sort, String search) throws DataAccessException {
		
		Page p = null;
		
		int count = 0;
		
		try {
			try {
				count = ComputerDao.I.count(search);
			} catch (SQLException e) {
				logger.warn("Service - load page:"+e.getMessage());
				logger.warn("Service - count total (ERRCODE:"+e.getErrorCode()+")");
				
				throw new DataAccessException("Problème lors du compte des computers : accès aux données impossible.");
			}
			
			int maxPage = count / Page.SIZE;
			
			if(number > maxPage) {
				number = maxPage;
			}
			
			p = new Page(number, search);
			
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
				logger.warn("Service - load "+ p +" (ERRCODE:"+e.getErrorCode()+")");
				
				throw new DataAccessException("Problème lors du chargement des computers : accès aux données impossible.");
			}
		}
		finally {
			Connector.JDBC.closeConnection();
		}
		
		return p;
	}

}
