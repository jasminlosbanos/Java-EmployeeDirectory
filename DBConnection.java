package jas;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
	private static final String URL = "jdbc:mysql://localhost:3306/jasmindb";
	private static final String USER = "root";
	private static final String PSW = "";
	
	public static Connection getConnection() throws Exception {
		return DriverManager.getConnection(URL, USER, PSW);
	}
}
