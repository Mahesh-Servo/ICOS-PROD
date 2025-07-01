
package com.LsmFiServices.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StatusCodeServiceUtility {

    private static final Logger logger = LoggerFactory.getLogger(StatusCodeServiceUtility.class);

    public List<Map<String, String>> getStatusCodeDataMap(String pinstid) throws SQLException {
	List<Map<String, String>> uniqueCusIdListOfMap = new LinkedList<>();
	List<Map<String, String>> accountNumberWiseConstCodeListOfMap = new LinkedList<>();
	List<String> acountNumbersList = new LinkedList<>();
	List<String> constCodeList = new LinkedList<>();
	String statusCode = "";

	try (Connection con = DBConnect.getConnection();
		PreparedStatement pst = con
			.prepareStatement("SELECT DISTINCT FACILITY_NAME FROM LSM_LIMIT_ANSWERS WHERE PINSTID = ?")) {
	    pst.setString(1, pinstid);
	    try (ResultSet rs = pst.executeQuery()) {
		while (rs.next()) {
		    String facilityName = OperationUtillity.NullReplace(rs.getString("FACILITY_NAME"));

		    try (PreparedStatement pst1 = con.prepareStatement(
			    "SELECT QUESTION_ID, ANSWER FROM LSM_LIMIT_ANSWERS WHERE PINSTID = ? AND FACILITY_NAME = ? AND (QUESTION_ID LIKE '%149%' OR QUESTION_ID LIKE '%172%') ORDER BY SUBSTR(QUESTION_ID, 4, LENGTH(QUESTION_ID))")) {
			pst1.setString(1, pinstid);
			pst1.setString(2, facilityName);
			try (ResultSet rs1 = pst1.executeQuery()) {
			    while (rs1.next()) {
				if (rs1.getString("QUESTION_ID").contains("149")) {
				    String account_number = rs1.getString("ANSWER");
				    if (account_number != null && !account_number.isEmpty()) {
					acountNumbersList.add(account_number);
				    }
				} else if (rs1.getString("QUESTION_ID").contains("172")) {
				    String constCode = rs1.getString("ANSWER");
				    if (constCode != null && !constCode.isEmpty()) {
					constCodeList.add(constCode);
				    }
				}
			    }
			}
		    }
		}

		for (int i = 0; i < acountNumbersList.size(); i++) {
		    Map<String, String> accNo_ConstCodeMap = new LinkedHashMap<>();
		    accNo_ConstCodeMap.put(acountNumbersList.get(i), constCodeList.get(i));
		    accountNumberWiseConstCodeListOfMap.add(accNo_ConstCodeMap);
		}
	    }
	} catch (Exception e) {
	    logger.error("Exception in getStatusCodeDataMap: " + OperationUtillity.traceException(e));
	    throw new SQLException("Failed to retrieve data", e);
	}

	try (Connection con = DBConnect.getConnection();
		PreparedStatement pst = con.prepareStatement(
			"SELECT DISTINCT ANSWER AS STATUS_CODE FROM LSM_LIMIT_ANSWERS WHERE PINSTID = ? AND QUESTION_ID='177'")) {
	    pst.setString(1, pinstid);
	    try (ResultSet rs = pst.executeQuery()) {
		while (rs.next()) {
		    statusCode = rs.getString("STATUS_CODE");
		}
	    }
	}

	List<Map<String, String>> listOfMap = new LinkedList<>();
	try (Connection con = DBConnect.getConnection();
		PreparedStatement pst = con.prepareStatement(
			"SELECT QUESTION_ID, FACILIY AS FACILITY_NAME ,CUSTID_ACCOUNT_SPECIFIC AS CUSTID,FIELD_VALUE AS ACCOUNT_NUMBER FROM LSM_RM_SIGNED_API_INFO  WHERE PINSTID =(SELECT  DISHUBID  FROM LIMIT_SETUP_EXT WHERE PINSTID=?) AND  QUESTION_ID LIKE '%149%' ORDER BY QUESTION_ID ")) {
	    pst.setString(1, pinstid);
	    try (ResultSet rs = pst.executeQuery()) {
		while (rs.next()) {

		    Map<String, String> facilityWiseAccountNumbersMap = new LinkedHashMap<>();
		    String accountNumber = OperationUtillity.NullReplace(rs.getString("ACCOUNT_NUMBER"));
		    facilityWiseAccountNumbersMap.put("FACILITY_NAME",
			    OperationUtillity.NullReplace(rs.getString("FACILITY_NAME")));
		    facilityWiseAccountNumbersMap.put("CUSTID", OperationUtillity.NullReplace(rs.getString("CUSTID")));
		    facilityWiseAccountNumbersMap.put("ACCOUNT_NUMBER", accountNumber);
		    facilityWiseAccountNumbersMap.put("STATUS_CODE", statusCode);

		    for (Map<String, String> mapFromList : accountNumberWiseConstCodeListOfMap) {
			for (Map.Entry<String, String> entry : mapFromList.entrySet()) {
			    String accountNumberFromMap = entry.getKey();
			    String constitutionCode = entry.getValue();
			    if (accountNumberFromMap.equals(accountNumber)) {
				facilityWiseAccountNumbersMap.put("CONSTITUTION_CODE", constitutionCode);
				listOfMap.add(facilityWiseAccountNumbersMap);
			    }
			}
		    }
		}
	    } catch (Exception e) {
		logger.error("Exception while getting facilityWiseAccountNumbersMap in getStatusCodeDataMap: "
			+ OperationUtillity.traceException(e));
	    }
	}

	List<String> uniqueData = new ArrayList<>();
	for (Map<String, String> custIDMap : listOfMap) {
	    if (!uniqueData.contains(custIDMap.get("CUSTID"))) {
		uniqueData.add(custIDMap.get("CUSTID"));
		uniqueCusIdListOfMap.add(custIDMap);
	    }
	}
	logger.info("Unique CustId Final List check ====> " + uniqueCusIdListOfMap);
	return uniqueCusIdListOfMap;
    }

    public Map<String, String> getMapForStatusCode(String pinstid) {

	String is_Internal_Balance_Transfer_Option = "";
	Map<String, String> statusCodeDataMap = new HashMap<>();
	try (Connection con = DBConnect.getConnection();
		PreparedStatement pst = con.prepareStatement(
			"SELECT ANSWER FROM LSM_LIMIT_ANSWERS WHERE PINSTID = ? AND QUESTION_ID = '67'");) {
	    pst.setString(1, pinstid);
	    try (ResultSet rs = pst.executeQuery()) {
		if (rs.next()) {
		    is_Internal_Balance_Transfer_Option = OperationUtillity.NullReplace(rs.getString("ANSWER"));
		}
	    }
	} catch (SQLException e) {
	    logger.error("Exception in StatusCodeServiceUtility.getMapForStatusCode(): "
		    + OperationUtillity.traceException(e));
	}
	if ("Yes".equalsIgnoreCase(is_Internal_Balance_Transfer_Option)) {
	    try (Connection con2 = DBConnect.getConnection();
		    PreparedStatement pst2 = con2.prepareStatement(
			    "SELECT * FROM ( SELECT LIMIT.QUESTION_ID   LIMIT_QUESTION, LIMIT.ANSWER LIMIT_ANSWER FROM LSM_LIMIT_ANSWERS LIMIT WHERE LIMIT.PINSTID = ?  AND LIMIT.QUESTION_ID IN ('177','54') ) PIVOT (MAX ( LIMIT_ANSWER ) FOR LIMIT_QUESTION IN ( 177 AS STATUS_CODE,54 AS UCC_BASED_CUST_ID))");) {
		pst2.setString(1, pinstid);
		try (ResultSet rs2 = pst2.executeQuery()) {
		    while (rs2.next()) {
			statusCodeDataMap.put("STATUS_CODE",
				OperationUtillity.NullReplace(rs2.getString("STATUS_CODE")));
			statusCodeDataMap.put("UCC_BASED_CUST_ID",
				OperationUtillity.NullReplace(rs2.getString("UCC_BASED_CUST_ID")));
			statusCodeDataMap.put("IS_INTERNAL_BALANCE_TRANSFER_OPTION",
				is_Internal_Balance_Transfer_Option);
		    }
		}
	    } catch (Exception e) {
		logger.info("Exception while getting status code -->" + OperationUtillity.traceException(e));
	    }
	} else {
	    statusCodeDataMap.put("IS_INTERNAL_BALANCE_TRANSFER_OPTION", is_Internal_Balance_Transfer_Option);
	}
	logger.info("StatusCodeServiceUtility.getMapForStatusCode() Status Code Map Check -->" + statusCodeDataMap);
	return statusCodeDataMap;
    }

}
