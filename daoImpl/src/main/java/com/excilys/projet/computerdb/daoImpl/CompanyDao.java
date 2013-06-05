package com.excilys.projet.computerdb.daoImpl;

import java.util.List;
import java.util.Observable;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.excilys.projet.computerdb.dao.Dao;
import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.model.Page.Order;
import com.excilys.projet.computerdb.model.Page.Sort;

@Repository
public class CompanyDao implements Dao<Company> {
	
	private static final Logger logger = LoggerFactory.getLogger(CompanyDao.class);
	
//	private static final String INSERT_COMPANY = "INSERT INTO company (name) VALUES (?);";
	private static final String UPDATE_COMPANY = "update Company c set c.name = :name where c.id = :id";
	private static final String DELETE_COMPANY = "delete from Company c where c.id = :id";
	private static final String GET_COMPANY = "from Company c where c.id = :id";
	private static final String GET_SOME_COMPANIES = "from Company limit :start, :nb";
	private static final String GET_ALL_COMPANIES = "from Company c order by c.name asc";
	private static final String COUNT_COMPANIES = "select count(c.id) from Company c";
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Observable dataUpdateNotifier = new Observable() {
            @Override
            public void notifyObservers() {
                super.setChanged();
                super.notifyObservers();
            }
	};
	
	@Override
	public Observable getDataUpdateNotifier() {
		return dataUpdateNotifier;
	}
	
	private void notifyUpdate() {
		logger.info("(observer) - Company change notified");
		dataUpdateNotifier.notifyObservers();
	}
	
	@Override
	public void insert(Company o) throws HibernateException {
		getSession().save(o);
		notifyUpdate();
	}

	@Override
	public void update(Company o) throws HibernateException {
		getSession().update(o);
		notifyUpdate();
	}

	@Override
	public boolean delete(Company o) throws HibernateException {
		int i = getSession().createQuery(DELETE_COMPANY).setInteger("id", o.getId()).executeUpdate();
		notifyUpdate();
		return i == 0 ? false : true;
	}

	@Override
	public Company get(int id) throws HibernateException {
		return (Company) getSession().get(Company.class, id);
	}

	@Override
	public List<Company> getFromTo(int start, int end, Sort sortedBy, Order order, String search) throws HibernateException {
		int i = end - (--start);
		if(i < 0) {
			i = 10;
		}
		return getSession().createQuery(GET_SOME_COMPANIES).setInteger("start", start).setInteger("nb", i).list();
	}

	@Override
	public List<Company> getAll(Sort sortedBy, Order order) throws DataAccessException {
		return getSession().createQuery(GET_ALL_COMPANIES).list();
	}

	@Override
	public int count(String search) throws DataAccessException {
		return ((Long)getSession().createQuery(COUNT_COMPANIES).uniqueResult()).intValue();
	}
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
		}
	
}
