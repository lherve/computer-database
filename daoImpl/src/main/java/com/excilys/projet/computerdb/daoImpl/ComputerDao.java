package com.excilys.projet.computerdb.daoImpl;

import java.util.List;
import java.util.Observable;

import com.excilys.projet.computerdb.dao.Dao;
import com.excilys.projet.computerdb.model.Computer;
import com.excilys.projet.computerdb.model.Page;
import com.excilys.projet.computerdb.model.Page.Order;
import com.excilys.projet.computerdb.model.Page.Sort;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository
public class ComputerDao implements Dao<Computer> {
	
	private static final String INSERT_COMPUTER = "INSERT INTO computer (name, introduced, discontinued, company_id) VALUES(?, ?, ?, ?);";
	private static final String UPDATE_COMPUTER = "UPDATE computer SET name = ? , introduced = ? , discontinued = ? , company_id = ? WHERE id = ?;";
	private static final String DELETE_COMPUTER = "delete from Computer c where c.id = :id";
	private static final String GET_ONE_COMPUTER = "from Computer c left outer join fetch c.company where c.id = :id";
	private static final String GET_ALL_COMPUTERS = "SELECT cpu.id, cpu.name, cpu.introduced, cpu.discontinued, cpu.company_id, cie.name FROM computer cpu LEFT OUTER JOIN company cie ON cpu.company_id = cie.id;";
	private static final String COUNT_COMPUTERS = "select count(c.id) from Computer c";
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void insert(Computer o) throws HibernateException {
		getSession().save(o);
	}

	@Override
	public void update(Computer o) throws HibernateException {
		getSession().update(o);
	}

	@Override
	public boolean delete(Computer o) throws HibernateException {
		return getSession().createQuery(DELETE_COMPUTER).setInteger("id", o.getId()).executeUpdate() > 0 ? true : false;
	}
	

	/*
	 * Retourne null si aucun computer trouvé avec l'id renseigné
	 */
	@Override
	public Computer get(int id) throws HibernateException {
		return (Computer) getSession().createQuery(GET_ONE_COMPUTER).setInteger("id", id).uniqueResult();
	}

	@Override
	public List<Computer> getFromTo(int start, int end, Sort sortedBy, Order order, String search) throws HibernateException {
		StringBuilder query = new StringBuilder("from Computer c left outer join fetch c.company ");
		
		if(search != null && search.length() > 0) {
			query.append("where c.name like :search ");
		}
		
		query.append("order by ");
		
		switch(sortedBy) {
		case ID:
			query.append("c.id");
			break;
		case NAME:
			query.append("c.name");
			break;
		case INTRODUCED:
			query.append("isnull(c.introduced) asc, c.introduced");
			break;
		case DISCONTINUED:
			query.append("isnull(c.discontinued) asc, c.discontinued");
			break;
		case COMPANY:
			query.append("isnull(c.name) asc, c.name");
			break;
		}
		
		if(order.equals(Order.ASC)) {
			query.append(" asc");
		}
		else
		{
			query.append(" desc");
		}
		
		Query q = getSession().createQuery(query.toString()).setFirstResult(start == 0 ? 0 : --start);
		
		int i = end - start;
		if(i < 0) {
            i = Page.SIZE;
		}
		
		q.setMaxResults(i);
		
		if(search != null && search.length() > 0) {
			q.setString("search", "%"+search+"%");
		}

		return q.list();
	}

	@Override
	public List<Computer> getAll(Sort sortedBy, Order order) throws HibernateException {
		return getSession().createQuery(GET_ALL_COMPUTERS).list();
	}

	@Override
	public int count(String search) throws DataAccessException {
        StringBuilder query = new StringBuilder(COUNT_COMPUTERS);

        if(search != null && !search.trim().isEmpty()) {
			query.append(" WHERE name LIKE :search");
			
		}
		System.out.println(query.toString());
		Query q = getSession().createQuery(query.toString());
		
		if(search != null && !search.trim().isEmpty()) {
			q.setString("search", "%"+search+"%");
		}
		
        return ((Long)q.uniqueResult()).intValue();
	}
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	/**
	 * Fonction non implémentée 
	 * 
	 */
	@Override
	public Observable getDataUpdateNotifier() {
		return null;
	}
	
}
