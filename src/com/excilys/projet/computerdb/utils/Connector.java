package com.excilys.projet.computerdb.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Formatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Connector {

	JDBC("com.mysql.jdbc.Driver", "jdbc:mysql:");
	
	private static final Logger logger = LoggerFactory.getLogger(Connector.class);
	
	private static final String HOST = "localhost";
	private static final String PORT = "3306";
	private static final String DATABASE = "computer_database";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "passe";
	
	private String url;
	
	public ThreadLocal<Connection> session;
	
	Connector(String driverClass, String urlDriver) {
		try {
			Class.forName(driverClass);
			this.url = new Formatter().format("%1$s//%2$s:%3$s/%4$s", urlDriver, HOST, PORT, DATABASE).toString();
		} catch (ClassNotFoundException e) {
			LoggerFactory.getLogger(Connector.class).error("Connector " + this + " - Impossible de charger le driver");
			System.exit(-1);
		}
		
		this.session = new ThreadLocal<Connection>();
	}
	
	public Connection getConnection() {
		Connection con;
		
		if((con = this.session.get()) == null) {
			try {
				con = DriverManager.getConnection(url, USERNAME, PASSWORD);
				con.setAutoCommit(false);
				this.session.set(con);
			} catch (SQLException e) {
				logger.error("Connector " + this + " - Impossible d'établir la connexion");
				con = null;
			}
		}
		
		return con;
	}
	
	public void closeConnection() {
		try {
			if(session.get() != null) {
				session.get().close();
			}
		} catch (SQLException e) {
			logger.error("Connector " + this + " - Erreur lors de la fermeture de la connexion");
		}
		this.session.remove();
	}
	
}
