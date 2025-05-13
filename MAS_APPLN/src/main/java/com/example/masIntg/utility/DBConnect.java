package com.example.masIntg.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DBConnect {
	
	private static final Logger logger = LoggerFactory.getLogger(DBConnect.class);
	
	public static Connection getConnection() throws SQLException {
		Connection con = null;
		
		try {
				//changes done on (12-10-2023) for thread stuck
				Context initContext = new InitialContext();
				DataSource ds = (DataSource) initContext.lookup("oracleJNDI");
				con = ds.getConnection();
				logger.info("MAS -------------------> DB connection Established");
			
		}
		catch(Exception e) {
			logger.info("MAS -------------------> Error occured while getting connection "+e.getMessage());
			return null ;
		}
		return con;
		
	}

}
