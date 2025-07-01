package com.LsmFiServices.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ROISoapRequestDataMap {

    private static final Logger logger = LoggerFactory.getLogger(ROISoapRequestDataMap.class);

    public static Map<String, String> getROIData(String pinstid, String facility, Connection con) throws SQLException {

	Map<String, String> rOIData = new LinkedHashMap<>();
	String ROI_PeggingFrequency_Query = null;
	String ROI_NoOfDays_Query = null;
	String ROI_EndDate_Query = null;
	String ROI_RateCode_Query = null;
	String ROI_Spread_Query = null;
	String ROI_PAGEFLAG_Query = null;

	PreparedStatement stmt = null;
	ResultSet rs = null;
	try {
	    try {
		String peggFreq = null;
		ROI_PeggingFrequency_Query = ReadPropertyFIle.getInstance().getPropConst()
			.getProperty("ROI_PEGGING_FREQUENCY_QUERY");
		stmt = con.prepareStatement(ROI_PeggingFrequency_Query);
		stmt.setString(1, pinstid);
		stmt.setString(2, facility);
		stmt.setString(3, pinstid);
		rs = stmt.executeQuery();

		if (rs.next()) {
		    peggFreq = OperationUtillity.NullReplace(rs.getString("PEGGING_FREQUENCY"));
		    rOIData.put("PEGGING_FREQUENCY", peggFreq);
		}
	    } catch (Exception e) {
		logger.info("ROISoapRequestDataMap.getROIData().PaggFreq -> " + OperationUtillity.traceException(e));
	    }

	    String NumOfDays = "";
	    try {
		if (stmt != null) {
		    stmt.close();
		}
		if (rs != null) {
		    rs.close();
		}

		ROI_NoOfDays_Query = ReadPropertyFIle.getInstance().getPropConst().getProperty("ROI_NO_OF_DAYS_QUERY");
		stmt = con.prepareStatement(ROI_NoOfDays_Query);
		stmt.setString(1, pinstid);
		stmt.setString(2, facility);
		stmt.setString(3, pinstid);
		rs = stmt.executeQuery();

		if (rs.next()) {
		    NumOfDays = OperationUtillity.NullReplace(rs.getString("NUMOFDAYS"));
		    rOIData.put("NUMBER_OF_DAYS", NumOfDays);
		}
	    } catch (Exception e) {
		logger.info("ROISoapRequestDataMap.getROIData().NumOfDays -> " + OperationUtillity.traceException(e));
	    }
	    try {
		if (stmt != null) {
		    stmt.close();
		}

		if (rs != null) {
		    rs.close();
		}

		String NumOfDaysDate = null;

		ROI_EndDate_Query = ReadPropertyFIle.getInstance().getPropConst().getProperty("ROI_END_DATE_QUERY");
		stmt = con.prepareStatement(ROI_EndDate_Query);
		stmt.setString(1, NumOfDays);
		stmt.setString(2, pinstid);
		stmt.setString(3, facility);
		stmt.setString(4, pinstid);
		rs = stmt.executeQuery();
		if (rs.next()) {
		    NumOfDaysDate = OperationUtillity.NullReplace(rs.getString("ROI_END_DATE"));
		    rOIData.put("ROI_END_DATE", NumOfDaysDate);
		}
	    } catch (Exception e) {
		logger.info("ROISoapRequestDataMap.getROIData().NumOfDaysDate -> " + pinstid + " :: "
			+ OperationUtillity.traceException(e));
	    }
	    try {
//				Query4
		if (stmt != null) {
		    stmt.close();
		}
		if (rs != null) {
		    rs.close();
		}
		String RATE_CODE = null;
		ROI_RateCode_Query = ReadPropertyFIle.getInstance().getPropConst().getProperty("ROI_RATE_CODE_QUERY");
		stmt = con.prepareStatement(ROI_RateCode_Query);
		stmt.setString(1, pinstid);
		stmt.setString(2, facility);
		stmt.setString(3, pinstid);
		rs = stmt.executeQuery();
		if (rs.next()) {
		    RATE_CODE = OperationUtillity.NullReplace(rs.getString("RATE_CODE"));
		    rOIData.put("RATE_CODE", RATE_CODE);
		}
	    } catch (Exception e) {
		logger.error("ROISoapRequestDataMap.getROIData().RATE_CODE -> ", OperationUtillity.traceException(e));
	    }
	    try {
		if (stmt != null) {
		    stmt.close();
		}

		if (rs != null) {
		    rs.close();
		}
		String ROI_Spread = null;
		ROI_Spread_Query = ReadPropertyFIle.getInstance().getPropConst().getProperty("ROI_SPREAD_QUERY");
		stmt = con.prepareStatement(ROI_Spread_Query);
		stmt.setString(1, pinstid);
		stmt.setString(2, facility);
		rs = stmt.executeQuery();
		while (rs.next()) {
		    ROI_Spread = OperationUtillity.NullReplace(rs.getString("ANSWER"));
		    rOIData.put("ROI_SPREAD", ROI_Spread);
		}
	    } catch (Exception e) {
		logger.info("ROISoapRequestDataMap.getROIData().ROI_Spread -> " + OperationUtillity.traceException(e));
	    }
	    try {
		if (stmt != null) {
		    stmt.close();
		}

		if (rs != null) {
		    rs.close();
		}
		String ROI_PEGGED_FLAG = null;
		ROI_PAGEFLAG_Query = ReadPropertyFIle.getInstance().getPropConst().getProperty("ROI_PAGE_FLAG_QUERY");
		stmt = con.prepareStatement(ROI_PAGEFLAG_Query);
		stmt.setString(1, pinstid);
		stmt.setString(2, facility);
		rs = stmt.executeQuery();
		if (rs.next()) {
		    ROI_PEGGED_FLAG = OperationUtillity.NullReplace(rs.getString("ROI_PEGGED_FLAG"));
		    rOIData.put("ROI_PEGGED_FLAG", ROI_PEGGED_FLAG);
		}
	    } catch (Exception e) {
		logger.info(
			"ROISoapRequestDataMap.getROIData().ROI_PEGGED_FLAG -> " + OperationUtillity.traceException(e));
	    }
	    if (stmt != null) {
		stmt.close();
	    }
	    if (rs != null) {
		rs.close();
	    }
	} catch (Exception e) {
	    logger.info(
		    "ROISoapRequestDataMap.getROIData().rOIData  exception ->" + OperationUtillity.traceException(e));
	}
	return rOIData;
    }
}
