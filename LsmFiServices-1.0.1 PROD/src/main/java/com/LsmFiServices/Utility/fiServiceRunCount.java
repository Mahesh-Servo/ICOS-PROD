package com.LsmFiServices.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class fiServiceRunCount {

	private static final Logger logger = LoggerFactory.getLogger(fiServiceRunCount.class);

	public synchronized static int checkRunCount(String pinstid) throws SQLException {

		logger.info("fiServiceRunCount.checkRunCount()-->" + pinstid);
		int count = 0;

		try (Connection con = DBConnect.getConnection();) {
			logger.info("DB connected Successfully");
			if (con == null) {
				return 1;
			} else {
				String serviceCountQuery = "SELECT COUNT(STATUS) AS STATUS_COUNT FROM LSM_SERVICE_REQ_RES WHERE PINSTID = ? AND REQUESTTYPE NOT IN ('SVT FD ACCOUNT INQUIRY')";
				try (PreparedStatement statement = con.prepareStatement(serviceCountQuery)) {
					statement.setString(1, pinstid);
					ResultSet rs = statement.executeQuery();
					while (rs.next()) {
						count = rs.getInt("STATUS_COUNT");
					}
				} catch (Exception e) {
					logger.info("fiServiceRunCount.checkRunCount()-->" + OperationUtillity.traceException(e));
				}
			}
			logger.info("fiServiceRunCount.checkRunCount() count for pinstid :: " + pinstid + " COUNTER : " + count);

			if (count > 0) {
				return 1;
			} else {
				return 0;
			}
		}

	}

}
