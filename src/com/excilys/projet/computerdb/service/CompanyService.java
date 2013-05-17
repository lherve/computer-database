package com.excilys.projet.computerdb.service;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.projet.computerdb.daoImpl.CompanyDao;
import com.excilys.projet.computerdb.daoImpl.ComputerDao;
import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.model.Computer;
import com.excilys.projet.computerdb.utils.CompaniesList;
import com.excilys.projet.computerdb.utils.Connector;

public enum CompanyService {

	I;
	
	private final static Logger logger = LoggerFactory.getLogger(CompanyService.class);
	
	public List<Company> getCompanies() {
		return CompaniesList.getInstance().getList();
	}
	
	public Company updateCompany(Company cie) {
		if(cie != null) {
			
			boolean commit = true;
			
			if(cie.getId() >= 0) {
				
				try {
					cie = CompanyDao.I.update(cie);
				} catch (SQLException e) {
					logger.warn("Service - update company:"+e.getMessage());
					logger.warn("Service - update "+ cie);
					commit = false;
				}
				
			}
			else {
				try {
					cie = CompanyDao.I.insert(cie);
				} catch (SQLException e) {
					logger.warn("Service - insert company:"+e.getMessage());
					logger.warn("Service - insert "+ cie);
					commit = false;
				}
			}
			
			if(commit) {
				try {
					Connector.JDBC.getConnection().commit();
				} catch (SQLException e) {
					logger.warn("Service - update company commit failed");
				}
			}
			else {
				cie = null;
				try {
					Connector.JDBC.getConnection().rollback();
				} catch (SQLException e) {
					logger.warn("Service - update company rollback failed");
				}
			}
			
		}
		
		Connector.JDBC.closeConnection();
		
		return cie;
	}
	
	public boolean deleteCompany(int id) {
		boolean result = false;
		
		boolean commit = true;
		
		try {
			result = CompanyDao.I.delete(new Company(id, null));
		} catch (SQLException e) {
			logger.warn("Service - delete company:"+e.getMessage());
			logger.warn("Service - delete "+id);
			commit = false;
		}
		
		if(commit) {
			try {
				Connector.JDBC.getConnection().commit();
			} catch (SQLException e) {
				logger.warn("Service - delete company commit failed");
			}
		}
		else {
			try {
				Connector.JDBC.getConnection().rollback();
			} catch (SQLException e) {
				logger.warn("Service - delete company rollback failed");
			}
		}
		
		Connector.JDBC.closeConnection();
		
		return result;
	}
	
}
