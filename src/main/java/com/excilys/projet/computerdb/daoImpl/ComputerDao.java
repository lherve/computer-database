package com.excilys.projet.computerdb.daoImpl;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.excilys.projet.computerdb.dao.Dao;
import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.model.Computer;
import com.excilys.projet.computerdb.utils.Connector;

import com.mysql.jdbc.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ComputerDao implements Dao<Computer> {
	
	private static final Logger logger = LoggerFactory.getLogger(ComputerDao.class);
	
	private static final String INSERT_COMPUTER = "INSERT INTO computer (name, introduced, discontinued, company_id) VALUES(?, ?, ?, ?);";
	private static final String UPDATE_COMPUTER = "UPDATE computer SET name = ? , introduced = ? , discontinued = ? , company_id = ? WHERE id = ?;";
	private static final String DELETE_COMPUTER = "DELETE FROM computer WHERE id = ?;";
	private static final String GET_ONE_COMPUTER = "SELECT cpu.id, cpu.name, cpu.introduced, cpu.discontinued, cpu.company_id, cie.name FROM computer cpu LEFT OUTER JOIN company cie ON cpu.company_id = cie.id WHERE cpu.id = ?;";
	private static final String GET_ALL_COMPUTERS = "SELECT cpu.id, cpu.name, cpu.introduced, cpu.discontinued, cpu.company_id, cie.name FROM computer cpu LEFT OUTER JOIN company cie ON cpu.company_id = cie.id;";
	private static final String COUNT_COMPUTERS = "SELECT count(id) as count FROM computer";
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public Computer insert(Computer o) throws DataAccessException {
		if(o != null) {
			
			List<Object> list = new ArrayList<Object>();
			
			list.add(o.getName());
			
			if(o.getIntroduced() != null) {
				list.add(new Date(o.getIntroduced().getTimeInMillis()));
			}
			else {
				list.add(null);
			}
			
			if(o.getDiscontinued() != null) {
				list.add(new Date(o.getDiscontinued().getTimeInMillis()));
			}
			else {
				list.add(null);
			}
			
			if(o.getCompany() != null) {
				list.add(o.getCompany().getId());
			}
			else {
				list.add(null);
			}
			
			if(jdbcTemplate.update(INSERT_COMPUTER, list.toArray()) <= 0) {
				o.setId(-1);
			}
				
		}
		return o;
	}

	@Override
	public Computer update(Computer o) throws DataAccessException {
		if(o != null) {
			
			List<Object> list = new ArrayList<Object>();
			
			list.add(o.getName());
			
			if(o.getIntroduced() != null) {
				list.add(new Date(o.getIntroduced().getTimeInMillis()));
			}
			else {
				list.add(null);
			}
			
			if(o.getDiscontinued() != null) {
				list.add(new Date(o.getDiscontinued().getTimeInMillis()));
			}
			else {
				list.add(null);
			}
			
			if(o.getCompany() != null) {
				list.add(o.getCompany().getId());
			}
			else {
				list.add(null);
			}
			
			list.add(o.getId());
			
			if(jdbcTemplate.update(UPDATE_COMPUTER, list.toArray()) <= 0){
				o.setId(-1);
			}
			
		}
		return o;
	}

	@Override
	public boolean delete(Computer o) throws DataAccessException {
		boolean result = false;
		
		if(o != null && o.getId() > 0) {
				
			if(jdbcTemplate.update(DELETE_COMPUTER, new Object[] {o.getId()}) > 0) {
				result = true;
			}
					
		}
		return result;
	}

	/*
	 * Retourne null si aucun computer trouvé avec l'id renseigné
	 */
	@Override
	public Computer get(int id) throws DataAccessException {
		Computer cpu = null;
		
		List<Computer> cpus = jdbcTemplate.query(GET_ONE_COMPUTER, new Object[] {id}, new ComputerRowMapper());
			
		if(cpus.size() > 0) {
			cpu = cpus.get(0);
		}
		
		return cpu;
	}

	@Override
	public List<Computer> getFromTo(int start, int end, Sort sortedBy, Order order, String search) throws DataAccessException {
		StringBuilder query = new StringBuilder("SELECT cpu.id, cpu.name, cpu.introduced, cpu.discontinued, cpu.company_id, cie.name FROM computer cpu LEFT OUTER JOIN company cie ON cpu.company_id = cie.id ");
		
		if(!StringUtils.isEmptyOrWhitespaceOnly(search)) {
			query.append("WHERE cpu.name LIKE ? ");
		}
		
		query.append("ORDER BY ");
		
		switch(sortedBy) {
		case ID:
			query.append("cpu.id");
			break;
		case NAME:
			query.append("cpu.name");
			break;
		case INTRODUCED:
			query.append("isnull(cpu.introduced) asc, cpu.introduced");
			break;
		case DISCONTINUED:
			query.append("isnull(cpu.discontinued) asc, cpu.discontinued");
			break;
		case COMPANY:
			query.append("isnull(cie.name) asc, cie.name");
			break;
		}
		
		if(order.equals(Dao.Order.ASC)) {
			query.append(" ASC");
		}
		else
		{
			query.append(" DESC");
		}
		
		query.append(" LIMIT ?,?;");

		List<Object> list = new ArrayList<Object>();
		
		if(!StringUtils.isEmptyOrWhitespaceOnly(search)) {
			list.add("%"+search+"%");
		}
		
		list.add(--start);
		
		int i = end - start;
		if(i < 0) {
			i = 10;
		}
		
		list.add(i);

		return jdbcTemplate.query(query.toString(), list.toArray(), new ComputerRowMapper());
	}

	@Override
	public List<Computer> getAll(Sort sortedBy, Order order) throws DataAccessException {
		return jdbcTemplate.query(GET_ALL_COMPUTERS, new Object[] {}, new ComputerRowMapper());
	}

	@Override
	public int count(String search) throws DataAccessException {
		int count = -1;
		
		StringBuilder query = new StringBuilder(COUNT_COMPUTERS);

		Object[] o = new Object[] {};
		
		if(!StringUtils.isEmptyOrWhitespaceOnly(search)) {
			query.append(" WHERE name LIKE ?");
			o = new Object[] {search};
		}
		
		query.append(";");
		
		count = jdbcTemplate.queryForObject(query.toString(), o, Integer.class);
		
		return count;
	}
	
	private class ComputerRowMapper implements RowMapper<Computer> {

		@Override
		public Computer mapRow(ResultSet rs, int line) throws SQLException {
			Computer c = new Computer(rs.getInt("cpu.id"), rs.getString("cpu.name"));
			
			Date dt;
			
			if((dt = rs.getDate("cpu.introduced")) != null) {
				c.setIntroduced(Calendar.getInstance());
				c.getIntroduced().setTime(dt);
			}
			
			if((dt = rs.getDate("cpu.discontinued")) != null) {
				c.setDiscontinued(Calendar.getInstance());
				c.getDiscontinued().setTime(dt);
			}
			
			int company_id = rs.getInt("cpu.company_id");
			
			if(company_id > 0) {
				c.setCompany(new Company(company_id, rs.getString("cie.name")));
			}
			
			return c;
		}
		
	}
	
}
