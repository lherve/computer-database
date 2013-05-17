package com.excilys.projet.computerdb.daoImpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.projet.computerdb.dao.Dao;
import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.utils.Connector;

import com.mysql.jdbc.StringUtils;

public enum CompanyDao implements Dao<Company> {
	
	I;
	
	private static final Logger logger = LoggerFactory.getLogger(CompanyDao.class);
	
	private static final String INSERT_COMPANY = "INSERT INTO company (name) VALUES (?);";
	private static final String UPDATE_COMPANY = "UPDATE company SET name = ? WHERE id = ?;";
	private static final String DELETE_COMPANY = "DELETE FROM company WHERE id = ?;";
	private static final String GET_COMPANY = "SELECT id, name FROM company WHERE id = ?";
	private static final String GET_SOME_COMPANIES = "SELECT id, name FROM company LIMIT ?,?;";
	private static final String GET_ALL_COMPANIES = "SELECT id, name FROM company ORDER BY name ASC;";
	private static final String COUNT_COMPANIES = "SELECT count(id) as count FROM company";
	
	private Observable dataUpdateNotifier = new Observable() {
		@Override
		public void notifyObservers() {
			super.setChanged();
			super.notifyObservers();
			super.clearChanged();
		}
	};
	
	public Observable getDataUpdateNotifier() {
		return dataUpdateNotifier;
	}
	
	public void notifyUpdate() {
		logger.info("(observer) - Company change notified");
		dataUpdateNotifier.notifyObservers();
	}
	
	@Override
	public Company insert(Company o) throws SQLException {
		if(o != null) {
			
			PreparedStatement pstmt = null;
			
			try {
				pstmt = Connector.JDBC.getConnection().prepareStatement(INSERT_COMPANY, Statement.RETURN_GENERATED_KEYS);
				
				pstmt.setString(1, o.getName());
				
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
					logger.warn("insert company - DAO Statement close failed - (ERRCODE:"+e.getErrorCode()+")");
				}
			}
		}
		return o;
	}

	@Override
	public Company update(Company o) throws SQLException {
		if(o != null) {
			
			PreparedStatement pstmt = null;
			
			try {
				pstmt = Connector.JDBC.getConnection().prepareStatement(UPDATE_COMPANY);
				
				pstmt.setString(1, o.getName());
				pstmt.setInt(2, o.getId());
				
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
					logger.warn("update company - DAO Statement close failed (ERRCODE:"+e.getErrorCode()+")");
				}
			}
		}
		return o;
	}

	@Override
	public boolean delete(Company o) throws SQLException {
		boolean result = false;
		
		if(o != null && o.getId() > 0) {
			
			PreparedStatement pstmt = null;
			
			try {
					pstmt = Connector.JDBC.getConnection().prepareStatement(DELETE_COMPANY);
					
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
					logger.warn("delete company - DAO Statement close failed (ERRCODE:"+e.getErrorCode()+")");
				}
			}
		}
		return result;
	}

	@Override
	public Company get(int id) throws SQLException {
		PreparedStatement pstmt = null;
		
		Company cie = null;
		
		try {
			pstmt = Connector.JDBC.getConnection().prepareStatement(GET_COMPANY);
			pstmt.setInt(1, id);
			
			logger.info(pstmt.toString().split(":\\s")[1]);
			
			ResultSet rs = pstmt.executeQuery(); 
			
			if(rs.next()) {
				cie = new Company(rs.getInt("id"), rs.getString("name"));
			}
			
			pstmt.close();
			
		}
		finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
			}
			catch(SQLException e) {
				logger.warn("get company - DAO Statement close failed (ERRCODE:"+e.getErrorCode()+")");
			}
		}
		
		return cie;
	}

	@Override
	public List<Company> getFromTo(int start, int end, Sort sortedBy, Order order, String search) throws SQLException {
		PreparedStatement pstmt = null;
		
		List<Company> cies = new ArrayList<Company>();
		
		try {
			pstmt = Connector.JDBC.getConnection().prepareStatement(GET_SOME_COMPANIES);
			pstmt.setInt(1, --start);
			
			int i = end - start;
			if(i < 0) {
				i = 10;
			}
				
			pstmt.setInt(2, i);
			
			logger.info(pstmt.toString().split(":\\s")[1]);
			
			ResultSet rs = pstmt.executeQuery(); 
			
			while(rs.next()) {
				Company cie = new Company(rs.getInt("id"), rs.getString("name"));
				cies.add(cie);
			}
			
		}
		finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
			}
			catch(SQLException e) {
				logger.warn("get some companies - DAO Statement close failed (ERRCODE:"+e.getErrorCode()+")");
			}
		}
		
		return cies;
	}

	@Override
	public List<Company> getAll(Sort sortedBy, Order order) throws SQLException {
		PreparedStatement pstmt = null;
		
		List<Company> cies = new ArrayList<Company>();
		
		try {
			pstmt = Connector.JDBC.getConnection().prepareStatement(GET_ALL_COMPANIES);
			
			logger.info(pstmt.toString().split(":\\s")[1]);
			
			ResultSet rs = pstmt.executeQuery(); 
			
			while(rs.next()) {
				Company cie = new Company(rs.getInt("id"), rs.getString("name"));
				cies.add(cie);
			}
			
		}
		finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
			}
			catch(SQLException e) {
				logger.warn("get all companies - DAO Statement close failed (ERRCODE:"+e.getErrorCode()+")");
			}
		}
		
		return cies;
	}

	@Override
	public int count(String search) throws SQLException {
		PreparedStatement pstmt = null;

		int count = 0;
		
		try {
			StringBuilder query = new StringBuilder(COUNT_COMPANIES);
			
			if(!StringUtils.isEmptyOrWhitespaceOnly(search)) {
				query.append(" WHERE name LIKE ?");
			}
			
			query.append(";");

			pstmt = Connector.JDBC.getConnection().prepareStatement(query.toString());
			
			if(!StringUtils.isEmptyOrWhitespaceOnly(search)) {
				pstmt.setString(1, search);
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
				logger.warn("count companies - DAO Statement close failed (ERRCODE:"+e.getErrorCode()+")");
			}
		}
		
		return count;
	}
	
}
