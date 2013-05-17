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

public enum ComputerDao implements Dao<Computer> {

	I;
	
	private static final Logger logger = LoggerFactory.getLogger(ComputerDao.class);
	
	private static final String INSERT_COMPUTER = "INSERT INTO computer (name, introduced, discontinued, company_id) VALUES(?, ?, ?, ?);";
	private static final String UPDATE_COMPUTER = "UPDATE computer SET name = ? , introduced = ? , discontinued = ? , company_id = ? WHERE id = ?;";
	private static final String DELETE_COMPUTER = "DELETE FROM computer WHERE id = ?;";
	private static final String GET_ONE_COMPUTER = "SELECT cpu.id, cpu.name, cpu.introduced, cpu.discontinued, cpu.company_id, cie.name FROM computer cpu LEFT OUTER JOIN company cie ON cpu.company_id = cie.id WHERE cpu.id = ?;";
	private static final String GET_ALL_COMPUTERS = "SELECT cpu.id, cpu.name, cpu.introduced, cpu.discontinued, cpu.company_id, cie.name FROM computer cpu LEFT OUTER JOIN company cie ON cpu.company_id = cie.id;";
	private static final String COUNT_COMPUTERS = "SELECT count(id) as count FROM computer";
	
	@Override
	public Computer insert(Computer o) throws SQLException {
		if(o != null) {
			
			PreparedStatement pstmt = null;

			try {
				pstmt = Connector.JDBC.getConnection().prepareStatement(INSERT_COMPUTER, Statement.RETURN_GENERATED_KEYS);
				
				pstmt.setString(1, o.getName());
				
				if(o.getIntroduced() != null) {
					pstmt.setDate(2, new Date(o.getIntroduced().getTimeInMillis()));
				}
				else {
					pstmt.setDate(2, null);
				}
				
				if(o.getDiscontinued() != null) {
					pstmt.setDate(3, new Date(o.getDiscontinued().getTimeInMillis()));
				}
				else {
					pstmt.setDate(3, null);
				}
				
				if(o.getCompany() != null) {
					pstmt.setInt(4, o.getCompany().getId());
				}
				else {
					pstmt.setString(4, null);
				}
				
				logger.info(pstmt.toString().split(":\\s")[1]);
				
				if(pstmt.executeUpdate() > 0) {
					ResultSet rs = pstmt.getGeneratedKeys();
					
					if(rs.next()) {
						o.setId(rs.getInt(1));
					}
					
					rs.close();
				}
				
			}
			finally {
				try {
					if(pstmt != null) {
						pstmt.close();
					}
				}
				catch(SQLException e) {
					logger.warn("insert computer - DAO Statement close failed (ERRCODE:"+e.getErrorCode()+")");
				}
			}
		}
		return o;
	}

	@Override
	public Computer update(Computer o) throws SQLException {
		if(o != null) {
			
			PreparedStatement pstmt = null;
			
			try {
				pstmt = Connector.JDBC.getConnection().prepareStatement(UPDATE_COMPUTER);
				
				pstmt.setString(1, o.getName());
				
				if(o.getIntroduced() != null) {
					pstmt.setDate(2, new Date(o.getIntroduced().getTimeInMillis()));
				}
				else {
					pstmt.setDate(2, null);
				}
				
				if(o.getDiscontinued() != null) {
					pstmt.setDate(3, new Date(o.getDiscontinued().getTimeInMillis()));
				}
				else {
					pstmt.setDate(3, null);
				}
				
				if(o.getCompany() != null) {
					pstmt.setInt(4, o.getCompany().getId());
				}
				else {
					pstmt.setString(4, null);
				}
				
				pstmt.setInt(5, o.getId());
				
				logger.info(pstmt.toString().split(":\\s")[1]);
				
				if(pstmt.executeUpdate() <= 0){
					o.setId(0);
				}
				
			}
			finally {
				try {
					if(pstmt != null) {
						pstmt.close();
					}
				}
				catch(SQLException e) {
					logger.warn("update computer - DAO Statement close failed (ERRCODE:"+e.getErrorCode()+")");
				}
			}
		}
		return o;
	}

	@Override
	public boolean delete(Computer o) throws SQLException {
		boolean result = false;
		
		if(o != null && o.getId() > 0) {
			
			PreparedStatement pstmt = null;
			
			try {
				pstmt = Connector.JDBC.getConnection().prepareStatement(DELETE_COMPUTER);
				
				pstmt.setInt(1, o.getId());
				
				logger.info(pstmt.toString().split(":\\s")[1]);
				
				if(pstmt.executeUpdate() > 0) {
					result = true;
				}
					
			}
			finally {
				try {
					if(pstmt != null) {
						pstmt.close();
					}
				}
				catch(SQLException e) {
					logger.warn("delete computer - DAO Statement close failed (ERRCODE:"+e.getErrorCode()+")");
				}
			}
		}
		
		return result;
	}

	@Override
	public Computer get(int id) throws SQLException {
		PreparedStatement pstmt = null;
		
		Computer cpu = null;
		
		try {
			pstmt = Connector.JDBC.getConnection().prepareStatement(GET_ONE_COMPUTER);
			
			pstmt.setInt(1, id);
			
			logger.info(pstmt.toString().split(":\\s")[1]);
			
			ResultSet rs = pstmt.executeQuery(); 
			
			if(rs.next()) {
				cpu = new Computer(rs.getInt("cpu.id"), rs.getString("cpu.name"));
				
				Date dt;
				
				if((dt = rs.getDate("cpu.introduced")) != null) {
					cpu.setIntroduced(Calendar.getInstance());
					cpu.getIntroduced().setTime(dt);
				}
				
				if((dt = rs.getDate("cpu.discontinued")) != null) {
					cpu.setDiscontinued(Calendar.getInstance());
					cpu.getDiscontinued().setTime(dt);
				}
				
				int company_id = rs.getInt("cpu.company_id");
				
				if(company_id > 0) {
					cpu.setCompany(new Company(company_id, rs.getString("cie.name")));
				}
			}
		}
		finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
			}
			catch(SQLException e) {
				logger.warn("get one computer - DAO Statement close failed (ERRCODE:"+e.getErrorCode()+")");
			}
		}
		
		return cpu;
	}

	@Override
	public List<Computer> getFromTo(int start, int end, Sort sortedBy, Order order, String search) throws SQLException {
		PreparedStatement pstmt = null;
		
		List<Computer> cpus = new ArrayList<Computer>();
		
		try {
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

			pstmt = Connector.JDBC.getConnection().prepareStatement(query.toString());

			int k = 1;
			
			if(!StringUtils.isEmptyOrWhitespaceOnly(search)) {
				pstmt.setString(k++, "%"+search+"%");
			}
			
			pstmt.setInt(k++, --start);
			
			int i = end - start;
			if(i < 0) {
				i = 10;
			}
			
			pstmt.setInt(k++, i);

			logger.info(pstmt.toString().split(":\\s")[1]);
			
			ResultSet rs = pstmt.executeQuery(); 

			cpus = fillComputers(rs);
			
			rs.close();
			
		}
		finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
			}
			catch(SQLException e) {
				logger.warn("get some computers - DAO Statement close failed (ERRCODE:"+e.getErrorCode()+")");
			}
		}
		
		return cpus;
	}

	@Override
	public List<Computer> getAll(Sort sortedBy, Order order) throws SQLException {
		Statement pstmt = null;
		
		List<Computer> cpus = new ArrayList<Computer>();
		
		try {
			pstmt = Connector.JDBC.getConnection().createStatement();

			logger.info(GET_ALL_COMPUTERS);
			
			ResultSet rs = pstmt.executeQuery(GET_ALL_COMPUTERS); 
			
			cpus = fillComputers(rs);
			
			rs.close();
			
		}
		finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
			}
			catch(SQLException e) {
				logger.warn("get all computers - DAO Statement close failed (ERRCODE:"+e.getErrorCode()+")");
			}
		}
		
		return cpus;
	}

	@Override
	public int count(String search) throws SQLException {
		PreparedStatement pstmt = null;

		int count = 0;
		
		try {
			StringBuilder query = new StringBuilder(COUNT_COMPUTERS);
			
			if(!StringUtils.isEmptyOrWhitespaceOnly(search)) {
				query.append(" WHERE name LIKE ?");
			}
			
			query.append(";");
			
			pstmt = Connector.JDBC.getConnection().prepareStatement(query.toString());
			
			if(!StringUtils.isEmptyOrWhitespaceOnly(search)) {
				pstmt.setString(1, "%"+search+"%");
			}
			
			logger.info(pstmt.toString().split(":\\s")[1]);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				count = rs.getInt("count");
			}
			
			rs.close();
			
		}
		finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
			}
			catch(SQLException e) {
				logger.warn("count computers - DAO Statement close failed (ERRCODE:"+e.getErrorCode()+")");
			}
		}
		
		return count;
	}
	
	private List<Computer> fillComputers(ResultSet rs) {
		List<Computer> cpus = new ArrayList<Computer>();
		
		try {
			while(rs.next()) {
				Computer cpu = new Computer(rs.getInt("cpu.id"), rs.getString("cpu.name"));
				
				Date dt;
				
				if((dt = rs.getDate("cpu.introduced")) != null) {
					cpu.setIntroduced(Calendar.getInstance());
					cpu.getIntroduced().setTime(dt);
				}
				
				if((dt = rs.getDate("cpu.discontinued")) != null) {
					cpu.setDiscontinued(Calendar.getInstance());
					cpu.getDiscontinued().setTime(dt);
				}
				
				int company_id = rs.getInt("cpu.company_id");
				
				if(company_id > 0) {
					cpu.setCompany(new Company(company_id, rs.getString("cie.name")));
				}
				
				cpus.add(cpu);
			}
		} catch (SQLException e) {
			logger.warn("fill computers from result set: "+e.getMessage());
		}
		
		return cpus;
	}
	
}
