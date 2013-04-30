package com.excilys.projet.computerdb.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum Connector {

	JDBC("com.mysql.jdbc.Driver", "jdbc:mysql://");

	Connector(String driverClass, String urlDriver) {
		try {
			Class.forName(driverClass);
			this.url = urlDriver + "localhost:3306/computer_database";
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private String url;

	public Connection getConnection() {
		Connection c = null;
		try {
			c = DriverManager.getConnection(this.url, "root", "passe");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return c;
	}

	public void closeConnection(Connection c) {
		try {
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
