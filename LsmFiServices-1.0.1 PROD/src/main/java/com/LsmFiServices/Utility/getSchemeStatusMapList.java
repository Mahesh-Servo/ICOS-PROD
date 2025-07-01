package com.LsmFiServices.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class getSchemeStatusMapList {

    private static final Logger logger = LoggerFactory.getLogger(getSchemeStatusMapList.class);

    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rs = null;

    public List<Map<String, String>> createListForSchemeAndStatus(String pinstid) throws SQLException {

	List<Map<String, String>> ListForSchemeAndStatus = new ArrayList<>();

	try {
	    con = DBConnect.getConnection();

	    String CUST_ID = commonUtility.getCustid(pinstid, con);

//			String lsql2 = ReadPropertyFIle.getInstance().getPropConst().getProperty("CHILD_NODE_CREATE_LIST");

	    String lsql2 = "SELECT * FROM (SELECT limit.question_id limit_question, limit.answer limit_answer, limit.facility_name   facility_name FROM lsm_limit_answers limit WHERE limit.pinstid = ? AND upper(replace(FACILITY_NAME, ' ','_')) != 'PARENT_LIMIT' AND limit.question_id IN ('176', '177')) PIVOT (MAX ( LIMIT_ANSWER ) FOR limit_question IN ( 176 AS SCHEME_CODE , 177 AS STATUS_CODE))";
	    statement = con.prepareStatement(lsql2);
	    statement.setString(1, pinstid);
	    rs = statement.executeQuery();
	    while (rs.next()) {
		Map<String, String> childLimitNodeMap = new ConcurrentHashMap<>();

		childLimitNodeMap.put("PINSTID", pinstid); // pinstid
		// facility name
		childLimitNodeMap.put("FACILITY_NAME", OperationUtillity.NullReplace(rs.getString("FACILITY_NAME")));
		// facility currency CURRENCY_ONE
		childLimitNodeMap.put("SCHEME_CODE", OperationUtillity.NullReplace(rs.getString("SCHEME_CODE")));
		childLimitNodeMap.put("STATUS_CODE", OperationUtillity.NullReplace(rs.getString("STATUS_CODE")));

		// Start by Hemant, Bharat on 29.05.2023 // account no for DLOD, commented by
		// Bharat,hemant
		try {
		    String facName = childLimitNodeMap.get("FACILITY_NAME");
//					String query = ReadPropertyFIle.getInstance().getPropConst().getProperty("GET_FACILITYWISE_ACCOUNT_NUM_QUERY");
		    final String query = "((SELECT QUESTION_ID, ANSWER FROM LSM_LIMIT_ANSWERS WHERE QUESTION_ID LIKE '157%' OR QUESTION_ID LIKE '158%' OR QUESTION_ID LIKE '162%' OR QUESTION_ID LIKE '149%') INTERSECT (SELECT QUESTION_ID,ANSWER FROM LSM_LIMIT_ANSWERS WHERE PINSTID=? AND FACILITY_NAME=?)) ORDER BY QUESTION_ID";

		    PreparedStatement ptmst = con.prepareStatement(query);
		    ptmst.setString(1, pinstid);
		    ptmst.setString(2, facName);
		    ResultSet rset = ptmst.executeQuery();

		    int counter1 = 0, counter2 = 0, counter3 = 0, counter4 = 0;
		    while (rset.next()) {
			if (rset.getString(1).contains("149")) {
			    childLimitNodeMap.put("ACCOUNT_NO_" + counter1,
				    OperationUtillity.NullReplace(rset.getString(2)));
			    counter1++;
			}
		    }
		    counter1 = 0;

		} catch (Exception ex) {
		    ex.printStackTrace();
		    logger.info("createFacilityLimitList.createChildList() error for Account No ------------------->"
			    + pinstid, ex.toString());
		}
	    }

	} catch (Exception ex) {
	    ex.printStackTrace();
	    logger.info("createChildList error called for ------------------->" + pinstid, ex.toString());
	} finally {
	    if (statement != null) {
		statement.close();
	    }
	    if (rs != null) {
		rs.close();
	    }
	    if (con != null) {
		con.close();
	    }
	}
	logger.info("childList data for --> " + pinstid + "  ::>> ", ListForSchemeAndStatus.toString());

	return ListForSchemeAndStatus;
    }

}
