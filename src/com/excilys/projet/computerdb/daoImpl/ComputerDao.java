package com.excilys.projet.computerdb.daoImpl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import com.excilys.projet.computerdb.dao.Dao;
import com.excilys.projet.computerdb.db.Connector;
import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.model.Computer;
import com.mysql.jdbc.StringUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

public enum ComputerDao implements Dao<Computer> {

	I;
	
	@Override
	public Computer insert(Computer o) {
		if(o != null) {
			Connection con = null;
			PreparedStatement pstmt = null;
			
			checkCompany(o);

			try {
				con = Connector.JDBC.getConnection();
				
				String query = "INSERT INTO computer (name, introduced, discontinued, company_id) VALUES(?, ?, ?, ?);";
				
				pstmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

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
				
				Logger.getLogger("queryLogger").log(Level.INFO, pstmt.toString().split(":\\s")[1]);
				
				if(pstmt.executeUpdate() > 0) {
					ResultSet rs = pstmt.getGeneratedKeys();
					
					if(rs.next()) {
						o.setId(rs.getInt(1));
					}
					
					rs.close();
				}
				
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
			finally {
				try {
					if(pstmt != null) {
						pstmt.close();
					}
				}
				catch(SQLException e) {
					e.printStackTrace();
				}
				Connector.JDBC.closeConnection(con);
			}
		}
		return o;
	}

	@Override
	public Computer update(Computer o) {
		if(o != null) {
			Connection con = null;
			PreparedStatement pstmt = null;
	
			checkCompany(o);
			
			try {
				con = Connector.JDBC.getConnection();
				
				String query = "UPDATE computer SET name = ? , introduced = ? , discontinued = ? , company_id = ? WHERE id = ?;";
				
				pstmt = con.prepareStatement(query);
				
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
				
				Logger.getLogger("queryLogger").log(Level.INFO, pstmt.toString().split(":\\s")[1]);

				if(pstmt.executeUpdate() <= 0){
					o.setId(0);
				}
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
			finally {
				try {
					if(pstmt != null) {
						pstmt.close();
					}
				}
				catch(SQLException e) {
					e.printStackTrace();
				}
				Connector.JDBC.closeConnection(con);
			}
		}
		return o;
	}

	@Override
	public boolean delete(Computer o) {
		boolean result = false;
		if(o != null) {
			Connection con = null;
			PreparedStatement pstmt = null;
			
			try {
				if(o.getId() > 0) {
					con = Connector.JDBC.getConnection();
					
					pstmt = con.prepareStatement("DELETE FROM computer WHERE id = ?");
					pstmt.setInt(1, o.getId());
					
					if(pstmt.executeUpdate()>0)
						result = true;
					
				}
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
			finally {
				try {
					if(pstmt != null) {
						pstmt.close();
					}
				}
				catch(SQLException e) {
					e.printStackTrace();
				}
				Connector.JDBC.closeConnection(con);
			}
		}
		return result;
	}

	@Override
	public Computer get(int id) {
		Connection con = Connector.JDBC.getConnection();
		PreparedStatement pstmt = null;
		
		Computer cpu = null;
		
		try {
			String query = "SELECT cpu.id, cpu.name, cpu.introduced, cpu.discontinued, cpu.company_id, cie.name FROM computer cpu LEFT OUTER JOIN company cie ON cpu.company_id = cie.id WHERE cpu.id = ?";
			
			pstmt = con.prepareStatement(query);
			pstmt.setInt(1, id);
			
			Logger.getLogger("queryLogger").log(Level.INFO, pstmt.toString().split(":\\s")[1]);
			
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
			
			pstmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
			Connector.JDBC.closeConnection(con);
		}
		
		return cpu;
	}

	@Override
	public List<Computer> getFromTo(int start, int end, Sort sortedBy, Order order, String search) {
		Connection con = Connector.JDBC.getConnection();
		PreparedStatement pstmt = null;
		
		List<Computer> cpus = new ArrayList<Computer>();
		
		try {
			
			StringBuilder query = new StringBuilder("SELECT cpu.id, cpu.name, cpu.introduced, cpu.discontinued, cpu.company_id, cie.name FROM computer cpu LEFT OUTER JOIN company cie ON cpu.company_id = cie.id ");
			
			if(search != null && search.trim().length() > 0) {
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

			pstmt = con.prepareStatement(query.toString());

			int k = 1;
			
			if(search != null && search.trim().length() > 0) {
				pstmt.setString(k++, "%"+search+"%");
			}
			
			pstmt.setInt(k++, --start);
			
			int i = end - start;
			if(i < 0) {
				i = 10;
			}
				
			pstmt.setInt(k++, i);

			Logger.getLogger("queryLogger").log(Level.INFO, pstmt.toString().split(":\\s")[1]);
			
			ResultSet rs = pstmt.executeQuery(); 

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
			e.printStackTrace();
		}
		finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
			Connector.JDBC.closeConnection(con);
		}
		
		return cpus;
	}

	@Override
	public List<Computer> getAll(Sort sortedBy, Order order) {
		Connection con = Connector.JDBC.getConnection();
		Statement pstmt = null;
		
		List<Computer> cpus = new ArrayList<Computer>();
		
		try {
			pstmt = con.createStatement();

			StringBuilder query = new StringBuilder("SELECT cpu.id, cpu.name, cpu.introduced, cpu.discontinued, cpu.company_id, cie.name FROM computer cpu LEFT OUTER JOIN company cie ON cpu.company_id = cie.id;");

			Logger.getLogger("queryLogger").log(Level.INFO, query.toString());
			
			ResultSet rs = pstmt.executeQuery(query.toString()); 
			
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
			e.printStackTrace();
		}
		finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
			Connector.JDBC.closeConnection(con);
		}
		
		return cpus;
	}

	@Override
	public int count(String search) {
		Connection con = null;
		PreparedStatement pstmt = null;

		int count = 0;
		
		try {
			con = Connector.JDBC.getConnection();
			
			StringBuilder query = new StringBuilder("SELECT count(id) as count FROM computer");
			
			if(search != null && search.trim().length() > 0) {
				query.append(" WHERE name LIKE ?");
			}
			
			query.append(";");
			
			pstmt = con.prepareStatement(query.toString());
			
			if(search != null && search.trim().length() > 0) {
				pstmt.setString(1, "%"+search+"%");
			}
			
			Logger.getLogger("queryLogger").log(Level.INFO, pstmt.toString().split(":\\s")[1]);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				count = rs.getInt("count");
			}
			
			rs.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
			Connector.JDBC.closeConnection(con);
		}
		
		return count;
	}
	
	private Company checkCompany(Computer o) {
		Company c = o != null ? o.getCompany() : null;
		
		if(c != null) {
			
			if(c.getId() > 0 && (StringUtils.isNullOrEmpty(c.getName()))) {
				
				Company search = CompanyDao.I.get(c.getId());
				
				if(search != null) {
					c = search;
				}
				else {
					c = null;
				}
				
			}
			else if(c.getId() <= 0 && (!StringUtils.isNullOrEmpty(c.getName()))) {
				
				c = CompanyDao.I.insert(c);
				
			}
			else {
				c = null;
			}
			
			o.setCompany(c);
		}
		
		return c;
	}
	
}
