package com.svt.utils.dataConnectivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.svt.utils.common.OperationUtillity;
import com.svt.utils.common.constantsUtils;

@Repository
public class dbConnection {
	private static final Logger logger = LoggerFactory.getLogger(dbConnection.class);

	public static Connection getConnection() throws SQLException {
		Connection con = null;
		try {
			if ("true".equalsIgnoreCase(constantsUtils.dsFlag)) {
				Context initContext = new InitialContext();
				DataSource ds = (DataSource) initContext.lookup(constantsUtils.dataSource);
				con = ds.getConnection();
			} else {
				Class.forName(constantsUtils.driver);
				con = DriverManager.getConnection(constantsUtils.driverUrl, constantsUtils.userName,
						constantsUtils.password);
			}
		} catch (Exception e) {
			logger.info("Error occured while getting connection "+OperationUtillity.traceException(e));
			throw new SQLException();
		}
		return con;
	}
}
