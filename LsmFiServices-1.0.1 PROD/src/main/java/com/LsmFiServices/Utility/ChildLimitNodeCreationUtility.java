package com.LsmFiServices.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChildLimitNodeCreationUtility {

	private static final Logger logger = LoggerFactory.getLogger(ChildLimitNodeCreationUtility.class);

	public static Boolean otlSingleTransFlag(String facility) {


		Boolean singleTransFlag = false;
		try (Connection con = DBConnect.getConnection();) {
			PreparedStatement stmt = null;
			ResultSet rs = null;
			String query = "SELECT DISTINCT(NAME) FACILITY_NAME FROM SMEAG_MS_FACILITY_TEMPLATE WHERE LIMIT_TYPE = 'OTL' AND NAME LIKE '%"
					+ facility + "%'";
			stmt = con.prepareStatement(query);
			rs = stmt.executeQuery();

			if (rs.next()) {
				singleTransFlag = true;
			}
			if (stmt != null) {
				stmt.close();
			}
			if (rs != null) {
				rs.close();
			}
		} catch (Exception e) {
			logger.info("Error in ChildLimitNodeCreationUtility.otlSingleTransFlag()-->"+OperationUtillity.traceException(e));
		}
		return singleTransFlag;
	}
}
