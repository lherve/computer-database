package com.excilys.projet.computerdb.daoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.excilys.projet.computerdb.dao.Dao;
import com.excilys.projet.computerdb.db.Connector;
import com.excilys.projet.computerdb.model.Company;
import com.mysql.jdbc.StringUtils;

public enum CompanyDao implements Dao<Company> {
	
	I;

	@Override
	public Company insert(Company o) {
		if(o != null) {
			Connection con = null;
			Statement stmt = null;
			
			try {
				if(o.getId() <= 0) {
					con = Connector.JDBC.getConnection();
					
					stmt = con.createStatement();
					
					StringBuilder query = new StringBuilder("INSERT INTO company (name) VALUES ('");
					query.append(o.getName()).append("');");
					
					Logger.getLogger("queryLogger").log(Level.INFO, query.toString());
					
					if(stmt.execute(query.toString(), Statement.RETURN_GENERATED_KEYS)) {
						ResultSet rs = stmt.getGeneratedKeys();
						
						if(rs.next()) {
							o.setId(rs.getInt(1));
						}
						
						rs.close();
					}
					
					System.out.println("id = "+o.getId());
					
				}
				else {
					o = update(o);
				}
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
			finally {
				try {
					if(stmt != null) {
						stmt.close();
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
	public Company update(Company o) {
		if(o != null) {
			Connection con = null;
			Statement stmt = null;
			
			try {
				if(o.getId() <= 0) {
					o = insert(o);
				}
				else {
					con = Connector.JDBC.getConnection();
					
					stmt = con.createStatement();
					
					StringBuilder query = new StringBuilder("UPDATE company SET name = '");
					query.append(o.getName()).append("' WHERE id = ").append(o.getId());
					
					Logger.getLogger("queryLogger").log(Level.INFO, query.toString());
					
					stmt.execute(query.toString());
				}
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
			finally {
				try {
					if(stmt != null) {
						stmt.close();
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
	public boolean delete(Company o) {
		boolean result = false;
		if(o != null) {
			Connection con = null;
			PreparedStatement pstmt = null;
			
			try {
				if(o.getId() > 0) {
					con = Connector.JDBC.getConnection();
					
					pstmt = con.prepareStatement("DELETE FROM company WHERE id = ?");
					pstmt.setInt(1, o.getId());
					
					Logger.getLogger("queryLogger").log(Level.INFO, pstmt.toString().split(":\\s")[1]);
					
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
	public Company get(int id) {
		Connection con = Connector.JDBC.getConnection();
		PreparedStatement pstmt = null;
		
		Company cie = null;
		
		try {
			pstmt = con.prepareStatement("SELECT id, name FROM company WHERE id = ?");
			pstmt.setInt(1, id);
			
			Logger.getLogger("queryLogger").log(Level.INFO, pstmt.toString().split(":\\s")[1]);
			
			ResultSet rs = pstmt.executeQuery(); 
			
			if(rs.next()) {
				cie = new Company(rs.getInt("id"), rs.getString("name"));
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
		
		return cie;
	}

	@Override
	public List<Company> getFromTo(int start, int end, Sort sortedBy, Order order, String search) {
		Connection con = Connector.JDBC.getConnection();
		PreparedStatement pstmt = null;
		
		List<Company> cies = new ArrayList<Company>();
		
		try {
			pstmt = con.prepareStatement("SELECT id, name FROM company LIMIT ?,?;");
			pstmt.setInt(1, --start);
			
			int i = end - start;
			if(i < 0) {
				i = 10;
			}
				
			pstmt.setInt(2, i);
			
			Logger.getLogger("queryLogger").log(Level.INFO, pstmt.toString().split(":\\s")[1]);
			
			ResultSet rs = pstmt.executeQuery(); 
			
			while(rs.next()) {
				Company cie = new Company(rs.getInt("id"), rs.getString("name"));
				cies.add(cie);
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
		
		return cies;
	}

	@Override
	public List<Company> getAll(Sort sortedBy, Order order) {
		Connection con = Connector.JDBC.getConnection();
		PreparedStatement pstmt = null;
		
		List<Company> cies = new ArrayList<Company>();
		
		try {
			pstmt = con.prepareStatement("SELECT id, name FROM company ORDER BY name ASC;");
			
			ResultSet rs = pstmt.executeQuery(); 
			
			while(rs.next()) {
				Company cie = new Company(rs.getInt("id"), rs.getString("name"));
				cies.add(cie);
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
		
		return cies;
	}

	@Override
	public int count(String search) {
		Connection con = null;
		Statement stmt = null;

		int count = 0;
		
		try {
			con = Connector.JDBC.getConnection();
			
			stmt = con.createStatement();
			
			StringBuilder query = new StringBuilder("SELECT count(id) as count FROM company");
			
			if(!StringUtils.isNullOrEmpty(search)) {
				query.append(" WHERE name LIKE '%").append(search).append("%'");
			}
			
			query.append(";");
			
			Logger.getLogger("queryLogger").log(Level.INFO, query.toString());
			
			ResultSet rs = stmt.executeQuery(query.toString());
			
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
				if(stmt != null) {
					stmt.close();
				}
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
			Connector.JDBC.closeConnection(con);
		}
		
		return count;
	}
	
}
