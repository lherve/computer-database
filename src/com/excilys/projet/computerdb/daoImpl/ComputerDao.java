package com.excilys.projet.computerdb.daoImpl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import com.excilys.projet.computerdb.dao.Dao;
import com.excilys.projet.computerdb.db.Connector;
import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.model.Computer;

public enum ComputerDao implements Dao<Computer> {

	I;
	
	@Override
	public Computer insert(Computer o) {
		if(o != null) {
			Connection con = null;
			Statement pstmt = null;
			
			checkCompany(o);
			
			try {
				
				if(o.getId() <= 0) {
					con = Connector.JDBC.getConnection();
					
					pstmt = con.createStatement();
					
					String query = "INSERT INTO computer (name, introduced, discontinued, company_id) VALUES('"
							+ o.getName() + "',"; 
					
					if(o.getIntroduced() != null) {
						query += "'" + new Date(o.getIntroduced().getTimeInMillis()) + "',";
					}
					else {
						query += "null, ";
					}
					
					if(o.getDiscontinued() != null) {
						query += "'" + new Date(o.getDiscontinued().getTimeInMillis()) + "',";
					}
					else {
						query += "null, ";
					}
					
					if(o.getCompany() != null) {
						query += o.getCompany().getId() + ");";
					}
					else {
						query += "null);";
					}
					
					System.out.println(query);
					
					if(pstmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS) > 0) {
						ResultSet rs = pstmt.getGeneratedKeys();
						
						if(rs.next()) {
							o.setId(rs.getInt(1));
						}
						
						rs.close();
					}
					
					System.out.println("id = "+o.getId());
					
					/*pstmt = con.prepareStatement("INSERT INTO computer (name, introduced, discontinued, company_id) VALUES(?,?,?,?)");
					pstmt.setString(1, o.getName());
					pstmt.setDate(2, new Date(o.getIntroduced().getTimeInMillis()));
					pstmt.setDate(3, new Date(o.getDiscontinued().getTimeInMillis()));
					pstmt.setInt(4, o.getCompany().getId());
	
					if(pstmt.executeUpdate()>0);*/
					
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
			Statement stmt = null;
	
			checkCompany(o);
			
			try {
				
				if(o.getId() <= 0) {
					o = insert(o);
				}
				else {
					con = Connector.JDBC.getConnection();
					
					stmt = con.createStatement();
					
					String query = "UPDATE computer SET name = '" + o.getName() +"'";
					
					if(o.getIntroduced() != null) {
						query += ", introduced = '" + new Date(o.getIntroduced().getTimeInMillis()) + "'";
					}
					
					if(o.getDiscontinued() != null) {
						query += ", discontinued = '" + new Date(o.getDiscontinued().getTimeInMillis()) + "'";
					}
					
					if(o.getCompany() != null) {
						query += ", company_id = " + o.getCompany().getId();
					}
					
					query += " WHERE id = " + o.getId();
					
					System.out.println(query);
					
					stmt.execute(query);
					
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
			pstmt = con.prepareStatement("SELECT cpu.id, cpu.name, cpu.introduced, cpu.discontinued, cpu.company_id, cie.name FROM computer cpu LEFT OUTER JOIN company cie ON cpu.company_id = cie.id WHERE cpu.id = ?");
			pstmt.setInt(1, id);
			
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
	public Collection<Computer> getFromTo(int start, int end) {
		Connection con = Connector.JDBC.getConnection();
		PreparedStatement pstmt = null;
		
		List<Computer> cpus = new ArrayList<Computer>();
		
		try {
			pstmt = con.prepareStatement("SELECT cpu.id, cpu.name, cpu.introduced, cpu.discontinued, cpu.company_id, cie.name FROM computer cpu LEFT OUTER JOIN company cie ON cpu.company_id = cie.id LIMIT ?,?;");
			pstmt.setInt(1, --start);
			
			int i = end - start;
			if(i < 0) {
				i = 10;
			}
				
			pstmt.setInt(2, i);
			
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
	public Collection<Computer> getAll() {
		Connection con = Connector.JDBC.getConnection();
		Statement pstmt = null;
		
		List<Computer> cpus = new ArrayList<Computer>();
		
		try {
			pstmt = con.createStatement();
			
			String query = "SELECT cpu.id, cpu.name, cpu.introduced, cpu.discontinued, cpu.company_id, cie.name FROM computer cpu LEFT OUTER JOIN company cie ON cpu.company_id = cie.id;";
			
			System.out.println(query);
			
			ResultSet rs = pstmt.executeQuery(query); 
			
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
	public int count() {
		Connection con = null;
		Statement stmt = null;

		int count = 0;
		
		try {
			con = Connector.JDBC.getConnection();
			
			stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT count(id) as count FROM computer;");
			
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
	
	private Company checkCompany(Computer o) {
		Company c = o != null ? o.getCompany() : null;
		
		if(c != null) {
			
			if(c.getId() > 0 && (c.getName() == null || c.getName().equals(""))) {
				
				Company search = CompanyDao.I.get(c.getId());
				
				if(search != null) {
					c = search;
				}
				else {
					c = null;
				}
				
			}
			else if(c.getId() <= 0 && (c.getName() != null && !c.getName().equals(""))) {
				
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
