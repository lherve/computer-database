package com.excilys.projet.computerdb.service;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.excilys.projet.computerdb.daoImpl.CompanyDao;
import com.excilys.projet.computerdb.exception.DBException;
import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.utils.ApplicationContextHolder;
import com.excilys.projet.computerdb.utils.CompaniesList;
import com.excilys.projet.computerdb.utils.Connector;

@Service
public class CompanyService {

	@Autowired
	private CompanyDao companyDao;
	
	@Autowired
	private CompaniesList companiesList;
	
	private final static Logger logger = LoggerFactory.getLogger(CompanyService.class);
	
	public List<Company> getCompanies() throws DBException {
		List<Company> list;
		try {
			list = companiesList.getList();
		} catch (DBException e) {
			throw e;
		}
		return list;
	}
	
	public Company updateCompany(Company cie) {
		if(cie != null) {
			boolean commit = true;
			
			if(cie.getId() >= 0) {
				
				try {
					cie = companyDao.update(cie);
				} catch (DataAccessException e) {
					logger.warn("Service - update company:"+e.getMessage());
					logger.warn("Service - update "+ cie);
					commit = false;
				}
				
			}
			else {
				try {
					cie = companyDao.insert(cie);
				} catch (DataAccessException e) {
					logger.warn("Service - insert company:"+e.getMessage());
					logger.warn("Service - insert "+ cie);
					commit = false;
				}
			}
			
			if(commit) {
				try {
					Connector.JDBC.getConnection().commit();
					companyDao.notifyUpdate();
				} catch (SQLException e) {
					logger.warn("Service - update company commit failed (ERRCODE:"+e.getErrorCode()+")");
				}
			}
			else {
				cie = null;
				try {
					Connector.JDBC.getConnection().rollback();
				} catch (SQLException e) {
					logger.warn("Service - update company rollback failed (ERRCODE:"+e.getErrorCode()+")");
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
			result = companyDao.delete(new Company(id, null));
		} catch (DataAccessException e) {
			logger.warn("Service - delete company:"+e.getMessage());
			logger.warn("Service - delete "+ id);
			commit = false;
		}
		
		if(commit && result) {
			try {
				Connector.JDBC.getConnection().commit();
				companyDao.notifyUpdate();
			} catch (SQLException e) {
				logger.warn("Service - delete company commit failed (ERRCODE:"+e.getErrorCode()+")");
			}
		}
		else {
			try {
				Connector.JDBC.getConnection().rollback();
			} catch (SQLException e) {
				logger.warn("Service - delete company rollback failed (ERRCODE:"+e.getErrorCode()+")");
			}
		}
		
		Connector.JDBC.closeConnection();
		
		return result;
	}
	
}
