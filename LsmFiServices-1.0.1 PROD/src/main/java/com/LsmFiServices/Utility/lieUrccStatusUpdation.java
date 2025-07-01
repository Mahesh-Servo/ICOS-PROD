package com.LsmFiServices.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class lieUrccStatusUpdation {

//	private static final Logger logger = LoggerFactory.getLogger(lieUrccStatusUpdation.class);

    public static String UpdateLieUrccStatus(String pinstid, String requestType, String status, String errorMeassage,
	    String xmlResponsePacket) throws SQLException {

	Connection con = null;
	PreparedStatement statement = null;
	if (status != null && status.equalsIgnoreCase("N")) {
	    try {
		String UPDATE_QUERY_LEI_URCC = ReadPropertyFIle.getInstance().getPropConst()
			.getProperty("LEI_URCC_STATUS_UPDATE");
		con = DBConnect.getConnection();
		statement = con.prepareStatement(UPDATE_QUERY_LEI_URCC);
		statement.setString(1, status);
		statement.setString(2, errorMeassage);
		statement.setString(3, pinstid);
		statement.setString(4, requestType);
		int queryCount = statement.executeUpdate();

		if (statement != null) {
		    statement.close();
		}
		if (con != null) {
		    con.close();
		}
	    } catch (Exception e) {
		System.out.println(
			"lieUrccStatusUpdation.UpdateLieUrccStatus()UpdateLieUrccStatus catch block ::" + pinstid);
		e.printStackTrace();
		;
	    } finally {
		if (statement != null) {
		    con.close();
		}
	    }
	}

	return null;

    }

}
