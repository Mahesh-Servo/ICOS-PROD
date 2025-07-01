package com.LsmFiServices.Utility;

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
			if ("true".equalsIgnoreCase(Constants.DSFLAG)) {
				Context initContext = new InitialContext();
				DataSource ds = (DataSource) initContext.lookup(Constants.DATASOURCE);
				con = ds.getConnection();
			} else {
				Class.forName(Constants.DRIVER);
				con = DriverManager.getConnection(Constants.DRIVER_URL, Constants.USERNAME, Constants.PASSWORD);
			}
		} catch (Exception e) {
			logger.info("Error occured while getting connection "+OperationUtillity.traceException(e));
			throw new SQLException();
		}
		return con;
	}
}
