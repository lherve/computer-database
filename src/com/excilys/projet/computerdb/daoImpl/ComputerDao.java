package com.excilys.projet.computerdb.daoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import com.excilys.projet.computerdb.dao.Dao;
import com.excilys.projet.computerdb.db.Connector;
import com.excilys.projet.computerdb.model.Computer;

public class ComputerDao implements Dao {

	@Override
	public boolean insert(Object o) {
		if(o instanceof Computer) {
			Computer cpu = (Computer) o;
			
			Connection cnx = Connector.JDBC.getConnection();
			return false;
		}
		else return false;
	}

	@Override
	public boolean update(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object get(int id) {
		Connection con = Connector.JDBC.getConnection();
		PreparedStatement pstmt;
		
		Computer cpu = null;
		
		try {
			pstmt = con.prepareStatement("SELECT id, name FROM computer WHERE id = ?");
			pstmt.setInt(1, id);
			
			ResultSet rs = pstmt.executeQuery(); 
			
			if(rs.next()) {
				cpu = new Computer(rs.getInt("id"), rs.getString("name"));
			}
			
			pstmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			Connector.JDBC.closeConnection(con);
		}
		
		return cpu;
	}

	@Override
	public Collection<Object> getFromTo(int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Object> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

}
