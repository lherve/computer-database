package com.excilys.projet.computerdb.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.excilys.projet.computerdb.dao.Dao.Order;
import com.excilys.projet.computerdb.dao.Dao.Sort;
import com.excilys.projet.computerdb.daoImpl.CompanyDao;
import com.excilys.projet.computerdb.model.Company;

public class CompaniesList implements Observer {

	private static CompaniesList I;
	
	private CompaniesList() {
		this.list = new ArrayList<Company>();
		loadCompanies();
		CompanyDao.I.getDataUpdateNotifier().addObserver(this);
	}
	
	public static CompaniesList getInstance() {
		if(I == null) {
			I = new CompaniesList();
		}
		return I;
	}
	
	private List<Company> list;

	public List<Company> getList() {
		return this.list;
	}
	
	private void loadCompanies() {
		this.list = CompanyDao.I.getAll(Sort.NAME, Order.ASC);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		loadCompanies();
	}
	
}
