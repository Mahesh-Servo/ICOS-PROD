package com.LsmFiServices.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PSLSoapRequestDataMap {

    private static final Logger logger = LoggerFactory.getLogger(PSLSoapRequestDataMap.class);

    public static String getIndustryCodeFromMaster(String pinstid) throws SQLException {

	String code = "";

	try (Connection con = DBConnect.getConnection();) {
	    String INDUSTRY_CODE_QUERY = ReadPropertyFIle.getInstance().getPropConst()
		    .getProperty("INDUSTRY_CODE_QUERY");
	    PreparedStatement statement = con.prepareStatement(INDUSTRY_CODE_QUERY);
	    statement.setString(1, pinstid);
	    ResultSet rs = statement.executeQuery();
	    while (rs.next()) {
		code = OperationUtillity.NullReplace(rs.getString("CODE"));
	    }

	    if (statement != null) {
		statement.close();
	    }
	    if (rs != null) {
		rs.close();
	    }
	} catch (Exception e) {
	    logger.info("pslSoapRequestDataMap.getIndustryCodeFromMaster()-->\n" + OperationUtillity.traceException(e));
	}
	return code;
    }
}
