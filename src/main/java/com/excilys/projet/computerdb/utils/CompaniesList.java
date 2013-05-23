package com.excilys.projet.computerdb.utils;

import java.sql.SQLException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import com.excilys.projet.computerdb.dao.Dao.Order;
import com.excilys.projet.computerdb.dao.Dao.Sort;
import com.excilys.projet.computerdb.daoImpl.CompanyDao;
import com.excilys.projet.computerdb.exception.DataAccessException;
import com.excilys.projet.computerdb.model.Company;

@Component
public class CompaniesList implements Observer {

	private static final Logger logger = LoggerFactory.getLogger(CompaniesList.class);
	
	private List<Company> list;

	public List<Company> getList() throws DataAccessException {
		if(this.list == null) {
			loadCompanies();
		}
		
		if(this.list == null) {
			throw new DataAccessException("Problème lors du chargement de la liste des companies : accès aux données impossible.");
		}
		
		return this.list;
	}
	
	private CompanyDao companyDao;
	
	private void loadCompanies() {
		if(companyDao == null) {
			companyDao = ApplicationContextHolder.getContext().getBean("companyDao", CompanyDao.class);
			companyDao.getDataUpdateNotifier().addObserver(this);
		}
		
		try {
			this.list = companyDao.getAll(Sort.NAME, Order.ASC);
		} catch (SQLException e) {
			logger.warn("Companies list - Impossible de charger la liste des companies");
			this.list = null;
		}
		
		Connector.JDBC.closeConnection();
	}
	
	@Override
	public void update(Observable o, Object arg) {
		loadCompanies();
	}
	
}
