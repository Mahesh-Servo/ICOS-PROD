package com.LsmFiServices.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.LsmFiServices.FiLsmController.DrawingPowerUpdationController;

@Component
public class DrawingPowerUtility {
    private static final Logger logger = LoggerFactory.getLogger(DrawingPowerUpdationController.class);

    public Map<String, String> getResponseMap(String pinstId, String accountNumber) {

	Map<String, String> responseMap = new HashMap<>();
	String response = "";
	try (Connection con = DBConnect.getConnection();
		PreparedStatement pst = con.prepareStatement(Queries.GET_DRAWING_POWER_RESPONSE)) {
	    pst.setString(1, pinstId);
	    pst.setString(2, "SUCCESS");
	    pst.setString(3, "DRAWING POWER CHECK :: ACCOUNT NUMBER :: " + accountNumber);
	    try (ResultSet rs = pst.executeQuery()) {
		while (rs.next()) {
		    response = Optional.ofNullable(rs.getString("RESPONSE_MESSSAGE")).orElse("");
		}
		responseMap = xmlToMap.successMapForDrgPwrChk(pinstId, response);
	    }
	} catch (Exception e) {
	    logger.error("DrawingPowerUtility.getInputMap(){}", OperationUtillity.traceException(e));
	}
	logger.info("responseMap ncheck for " + accountNumber, responseMap);
	return responseMap;
    }

    public  boolean getFlagForDrgPwrCheck(String pinstId, String facility) {
    	logger.info("DrawingPower facility  : "+facility);
    	logger.info("DrawingPower   value : "+((facility.contains("Cash Credit") || facility.contains("Drop Line Overdraft")) 	&& (!facility.contains("CROSS CALL"))));
	if (((facility.contains("Cash Credit") || facility.contains("Drop Line Overdraft"))
		&& (!facility.contains("CROSS CALL")))
		&& !"Fresh".equalsIgnoreCase(OperationUtillity.getProposalType(pinstId))) {
		logger.info("inside if : "+facility);
	    return true;
	}
	return false;
    }
    
    public  boolean getFlagForDrgPwrCheck_1(String pinstId, String facility) {
		logger.info("DrawingPower facility  : "+facility);
		facility = facility.contains("-") ? facility.substring(0,facility.indexOf("-")) : facility;
		logger.info("DrawingPower   value : "+((facility.contains("Cash Credit") || facility.contains("Drop Line Overdraft")) 	&& (!facility.contains("CROSS CALL"))));

	if (((facility.contains("Cash Credit") || facility.contains("Drop Line Overdraft")) && (!facility.contains("CROSS CALL")))
		&& !"Fresh".equalsIgnoreCase(OperationUtillity.getProposalType(pinstId))) {
		logger.info("inside if : "+facility);
	    return true;
	}
	return false;
    }
    
 // SN OF MAHESHV ON 10122024
    public Map<String,String> getDPAmountForDPUpdate(String pinstid, String Act_no) {
        
    	Map<String, String> dpCheckAmountMap = new HashMap<>();
    	try (Connection con = DBConnect.getConnection();
    		PreparedStatement pst = con.prepareStatement("SELECT ACC_NO,DP_AMOUNT FROM LSM_DP_CHK_DATA WHERE PINSTID =? AND ACC_NO =? AND UPPER(STATUS) = 'SUCCESS'");) {
    	    pst.setString(1, pinstid);
    	    pst.setString(2, Act_no);
    	    try (ResultSet rs = pst.executeQuery()) {
    		while (rs.next()) {
    			dpCheckAmountMap.put("Account_Number",rs.getString("ACC_NO"));
    			dpCheckAmountMap.put("Drawing_Power",rs.getString("DP_AMOUNT"));
    		}
    	    }
    	} catch (SQLException e) {
    	    logger.info("nOperationUtillity.getDPAmountForDPUpdate  exception : " + OperationUtillity.traceException(e));
    	}
    	logger.info("nOperationUtillity.getDPAmountForDPUpdate.dpCheckAmountMap : " + dpCheckAmountMap.toString());
    	return dpCheckAmountMap;
        }
    
	public int getDPFlagCheckToProceed(String pinstid) {

		int dpCheckflag = 0;
		try (Connection con = DBConnect.getConnection();
				PreparedStatement pst = con.prepareStatement(
						"SELECT COUNT(1) AS CNT FROM LSM_DP_CHK_DATA WHERE PINSTID =? AND UPPER(STATUS) NOT IN ('SUCCESS')");) {
			pst.setString(1, pinstid);
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					dpCheckflag = rs.getInt("CNT");
				}
			}
		} catch (SQLException e) {
			logger.info(
					"DrawingPowerUtility.getDPFlagCheckToProceed  exception : " + OperationUtillity.traceException(e));
		}
		logger.info("DrawingPowerUtility.getDPFlagCheckToProceed.dpCheckflag count : " + String.valueOf(dpCheckflag));
		return dpCheckflag;
	}
    // EN OF MAHESHV ON 10122024
}
