package com.excilys.projet.computerdb.daoImpl;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.excilys.projet.computerdb.dao.Dao;
import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.model.Page.Order;
import com.excilys.projet.computerdb.model.Page.Sort;

import com.mysql.jdbc.StringUtils;

@Repository
public class CompanyDao implements Dao<Company> {
	
	private static final Logger logger = LoggerFactory.getLogger(CompanyDao.class);
	
	private static final String INSERT_COMPANY = "INSERT INTO company (name) VALUES (?);";
	private static final String UPDATE_COMPANY = "UPDATE company SET name = ? WHERE id = ?;";
	private static final String DELETE_COMPANY = "DELETE FROM company WHERE id = ?;";
	private static final String GET_COMPANY = "SELECT id, name FROM company WHERE id = ?";
	private static final String GET_SOME_COMPANIES = "SELECT id, name FROM company LIMIT ?,?;";
	private static final String GET_ALL_COMPANIES = "SELECT id, name FROM company ORDER BY name ASC;";
	private static final String COUNT_COMPANIES = "SELECT count(id) as count FROM company";
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
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
	public Company insert(Company o) throws DataAccessException {
		if(o != null) {
			if(jdbcTemplate.update(INSERT_COMPANY, o.getName()) <= 0) {
				o.setId(-1);
			}
			else {
				o.setId(0);
				notifyUpdate();
			}
		}
		return o;
	}

	@Override
	public Company update(Company o) throws DataAccessException {
		if(o != null) {
			if(jdbcTemplate.update(UPDATE_COMPANY, o.getName(), o.getId()) <= 0){
				o.setId(-1);
			}
			else {
				notifyUpdate();
			}
		}
		return o;
	}

	@Override
	public boolean delete(Company o) throws DataAccessException {
		boolean result = false;
		
		if(o != null && o.getId() > 0) {
			if(jdbcTemplate.update(DELETE_COMPANY, o.getId()) > 0) {
				result = true;
				notifyUpdate();
			}
		}
		return result;
	}

	@Override
	public Company get(int id) throws DataAccessException {
		Company cie = null;
		
		List<Company> cies = jdbcTemplate.query(GET_COMPANY, new Object[] {id}, new CompanyRowMapper());
		
		if(cies.size() > 0) {
			cie = cies.get(0);
		}
		
		return cie;
	}

	@Override
	public List<Company> getFromTo(int start, int end, Sort sortedBy, Order order, String search) throws DataAccessException {
		List<Company> cies = new ArrayList<Company>();
		
		int i = end - (--start);
		if(i < 0) {
			i = 10;
		}
		
		cies = jdbcTemplate.query(GET_SOME_COMPANIES, new Object[] {start, i},new CompanyRowMapper());
		
		return cies;
	}

	@Override
	public List<Company> getAll(Sort sortedBy, Order order) {
		List<Company> cies = new ArrayList<Company>();
			
		cies = jdbcTemplate.query(GET_ALL_COMPANIES, new Object[] {}, new CompanyRowMapper());
		
		return cies;
	}

	@Override
	public int count(String search) throws DataAccessException {
		int count = -1;
		
		StringBuilder query = new StringBuilder(COUNT_COMPANIES);
		
		Object[] o = new Object[] {};
		
		if(!StringUtils.isEmptyOrWhitespaceOnly(search)) {
			query.append(" WHERE name LIKE ?");
			o = new Object[] {"%"+search+"%"};
		}
		
		query.append(";");
		
		count = jdbcTemplate.queryForObject(query.toString(), o, Integer.class);
		
		return count;
	}
	
	private class CompanyRowMapper implements RowMapper<Company> {

		@Override
		public Company mapRow(ResultSet rs, int line) throws SQLException {
			return new Company(rs.getInt("id"), rs.getString("name"));
		}
		
	}
	
}
