package com.excilys.projet.computerdb.utils;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.excilys.projet.computerdb.model.Page.Order;
import com.excilys.projet.computerdb.model.Page.Sort;
import com.excilys.projet.computerdb.dao.Dao;
import com.excilys.projet.computerdb.exception.DBException;
import com.excilys.projet.computerdb.model.Company;

@Component
public class CompaniesList implements Observer {

	//private static final Logger logger = LoggerFactory.getLogger(CompaniesList.class);
	
	private List<Company> list;

	public List<Company> getList() throws DBException {
        if(this.list == null) {
            loadCompanies();
        }

        if(this.list == null) {
            throw new DBException("Problème lors du chargement de la liste des companies : accès aux données impossible.");
    	}	

        return this.list;
	}
	
	private Dao<Company> companyDao;
	
	@Autowired
	public void setCompanyDao(Dao<Company> companyDao) {
		this.companyDao = companyDao;
		this.companyDao.getDataUpdateNotifier().addObserver(this);
	}
	
	private void loadCompanies() {
//        if(companyDao == null) {
//            companyDao = ApplicationContextHolder.getContext().getBean("companyDao", Dao.class);
//            companyDao.getDataUpdateNotifier().addObserver(this);
//        }

        this.list = companyDao.getAll(Sort.NAME, Order.ASC);
	}
	
	@Override
	public void update(Observable o, Object arg) {
        loadCompanies();
	}
	
}
