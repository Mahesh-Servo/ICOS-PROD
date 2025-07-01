package com.LsmFiServices.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.LsmFiServices.LsmFiCommonData.childListCommonMapData;

@Component
public class CrossCallUtility {

    private static final Logger logger = LoggerFactory.getLogger(CrossCallUtility.class);

    public List<Map<String, String>> getCrossCallfacilityList(String pinstid) {
	List<Map<String, String>> crossCallList = new LinkedList<>();
	try (Connection con = DBConnect.getConnection();
		PreparedStatement statement = con.prepareStatement(getCrossCallQuery());) {
	    statement.setString(1, pinstid);
	    try (ResultSet rs = statement.executeQuery()) {
		while (rs.next()) {
		    Map<String, String> childLimitNodeMap = new ConcurrentHashMap<>();
		    populateChildLimitNodeMap(rs, childLimitNodeMap, pinstid);
		    crossCallList.add(childLimitNodeMap);
		}
	    }
	} catch (Exception ex) {
	    logger.error("CrossCallUtility.getCrossCallfacilityList(){} " + pinstid,
		    OperationUtillity.traceException(ex));
	}
	return crossCallList;
    }

    private static String getCrossCallQuery() {
//	    return "SELECT * FROM (SELECT CROSS_CALL.question_id CROSS_CALL_question, CROSS_CALL.answer CROSS_CALL_answer, CROSS_CALL.facility_name facility_name FROM LSM_CROSS_CALL_ANSWERS CROSS_CALL WHERE CROSS_CALL.pinstid = ? AND CROSS_CALL.question_id IN ('146','127','171','132','133','155','156','128','129','149','49','163','164','137', '134', '130', '131','122', '123', '150','125','126','141','165')) PIVOT (MAX ( CROSS_CALL_answer ) FOR CROSS_CALL_question IN ( 146 AS currency_one,127 AS amount_limit,171 AS currency_two,132 AS security_provider,133 AS limit_exp_date,155 AS limit_prefix,129 AS sanction_date,156 AS limit_suffix,128 AS sanction_amount,146 AS currency_three, 149 AS ACCOUNT_NO, 49 AS DISTRICT, 163 AS LIEN_APPLICABILITY, 164 AS LIEN_AMOUNT, 137 AS DOCUMENT_AMOUNT,134 AS FACILITY_EXP_DATE , 130 AS SANCTION_VAL_DATE, 131 AS SANCTION_FORUM , 122 AS BUSINESS_MODEL , 123 AS SPPI, 150 AS FA_EXECUTION_DATE ,125 AS SEQUENCE_NO, 126 AS MAIN_LIMIT , 141 AS IS_REVOCABLE, 165 AS LIEN_IN_FAVOUR_OF)) ORDER BY SEQUENCE_NO";
	return Queries.FETCH_CROSS_CALL_DETAILS;
    }

    public static void populateChildLimitNodeMap(ResultSet rs, Map<String, String> childLimitNodeMap, String pinstid)
	    throws SQLException {
	String CUST_ID = childListCommonMapData.getCustid(pinstid);
	String COMPANYNAME = childListCommonMapData.getCompanyName(pinstid);
	Map<String, String> homeTabDataMap = childListCommonMapData.getHomeTabData(pinstid);
	Map<String, String> parentFacilityDataMap = childListCommonMapData.getParentFacilityDataMap(pinstid);
	try {
	    String mainLimit = rs.getString("CROSS_CALL_MAIN_LIMIT");
	    childLimitNodeMap.put("PINSTID", pinstid); // pinstid
	    childLimitNodeMap.put("FACILITY_NAME", OperationUtillity.NullReplace(rs.getString("FACILITY_NAME")));
	    childLimitNodeMap.put("CURRENCY_ONE", OperationUtillity.NullReplace(rs.getString("CURRENCY_TWO")));
	    childLimitNodeMap.put("AMOUNT_LIMIT",
		    commonUtility.millionString(OperationUtillity.NullReplace(rs.getString("AMOUNT_LIMIT"))));
	    childLimitNodeMap.put("CURRENCY_TWO", OperationUtillity.NullReplace(rs.getString("CURRENCY_TWO")));
	    childLimitNodeMap.put("SECURITY_PROVIDER",
		    OperationUtillity.NullReplace(rs.getString("SECURITY_PROVIDER"))); // limit Desc
	    childLimitNodeMap.put("LIMIT_EXP_DATE", OperationUtillity.NullReplace(rs.getString("LIMIT_EXP_DATE")));
	    childLimitNodeMap.put("LIMIT_PREFIX", OperationUtillity.NullReplace(rs.getString("LIMIT_PREFIX")));
	    childLimitNodeMap.put("SANCTION_DATE", OperationUtillity.NullReplace(rs.getString("SANCTION_DATE")));
	    childLimitNodeMap.put("LIMIT_SUFFIX", OperationUtillity.NullReplace(rs.getString("LIMIT_SUFFIX")));
	    childLimitNodeMap.put("SANCTION_AMOUNT",
		    commonUtility.millionString(OperationUtillity.NullReplace(rs.getString("SANCTION_AMOUNT"))));
	    childLimitNodeMap.put("CURRENCY_THREE", OperationUtillity.NullReplace(rs.getString("CURRENCY_TWO")));
	    childLimitNodeMap.put("CUST_ID", OperationUtillity.NullReplace(CUST_ID));
	    childLimitNodeMap.put("PARENT_LIMIT_DESC", OperationUtillity.NullReplace(COMPANYNAME));
	    childLimitNodeMap.put("LIEN_APPLICABILITY",
		    OperationUtillity.NullReplace(rs.getString("LIEN_APPLICABILITY"))); // limit LIEN
	    childLimitNodeMap.put("LIEN_AMOUNT",
		    commonUtility.millionString(OperationUtillity.NullReplace(rs.getString("LIEN_AMOUNT"))));
	    childLimitNodeMap.put("DOCUMENT_AMOUNT",
		    commonUtility.millionString(OperationUtillity.NullReplace(rs.getString("DOCUMENT_AMOUNT"))));
	    childLimitNodeMap.put("FACILITY_EXP_DATE",
		    OperationUtillity.NullReplace(rs.getString("FACILITY_EXP_DATE")));
	    childLimitNodeMap.put("SANCTION_VAL_DATE",
		    OperationUtillity.NullReplace(rs.getString("SANCTION_VAL_DATE")));
	    childLimitNodeMap.put("SANCTION_FORUM", OperationUtillity.NullReplace(rs.getString("SANCTION_FORUM")));
	    childLimitNodeMap.put("BUSINESS_MODEL", OperationUtillity.NullReplace(rs.getString("BUSINESS_MODEL")));
	    childLimitNodeMap.put("FA_EXECUTION_DATE",
		    OperationUtillity.NullReplace(rs.getString("FA_EXECUTION_DATE")));
	    childLimitNodeMap.put("SPPI", OperationUtillity.NullReplace(rs.getString("SPPI")));
	    childLimitNodeMap.put("SEQUENCE_NO", OperationUtillity.NullReplace(rs.getString("SEQUENCE_NO")));
	    childLimitNodeMap.put("IS_REVOCABLE", OperationUtillity.NullReplace(rs.getString("IS_REVOCABLE")));
	    childLimitNodeMap.put("LIEN_IN_FAVOUR_OF",
		    OperationUtillity.NullReplace(rs.getString("LIEN_IN_FAVOUR_OF")));
	    childLimitNodeMap.put("TYPE_OF_SERVICE", OperationUtillity.NullReplace(rs.getString("TYPE_OF_SERVICE")));
	    childLimitNodeMap.putAll(getCrossCallMainLimit(pinstid, mainLimit));
	    childLimitNodeMap.putAll(parentFacilityDataMap);
	    childLimitNodeMap.putAll(homeTabDataMap);
	} catch (Exception e) {
	    logger.error("CrossCallUtility.populateChildLimitNodeMap(){}", OperationUtillity.traceException(e));
	}
    }

    // when cross call is calling individual
    public void populateCrossCallNodeMapIndividual(ResultSet rs, Map<String, String> childLimitNodeMap, String pinstid)
	    throws SQLException {
	String CUST_ID = childListCommonMapData.getCustid(pinstid);
	String COMPANYNAME = childListCommonMapData.getCompanyName(pinstid);
	Map<String, String> homeTabDataMap = childListCommonMapData.getHomeTabData(pinstid);
	Map<String, String> parentFacilityDataMap = childListCommonMapData.getParentFacilityDataMap(pinstid);

	String mainLimit = Optional.ofNullable(rs.getString("CROSS_CALL_MAIN_LIMIT")).orElse("");
	childLimitNodeMap.put("PINSTID", pinstid);
	childLimitNodeMap.put("FACILITY_NAME", OperationUtillity.NullReplace(rs.getString("FACILITY_NAME")));
	childLimitNodeMap.put("CURRENCY_ONE", OperationUtillity.NullReplace(rs.getString("CURRENCY_TWO")));
	childLimitNodeMap.put("AMOUNT_LIMIT",
		commonUtility.millionString(OperationUtillity.NullReplace(rs.getString("AMOUNT_LIMIT"))));
	childLimitNodeMap.put("CURRENCY_TWO", OperationUtillity.NullReplace(rs.getString("CURRENCY_TWO")));
	childLimitNodeMap.put("SECURITY_PROVIDER", OperationUtillity.NullReplace(rs.getString("SECURITY_PROVIDER"))); // limit
	childLimitNodeMap.put("LIMIT_EXP_DATE", OperationUtillity.NullReplace(rs.getString("LIMIT_EXP_DATE")));
	childLimitNodeMap.put("LIMIT_PREFIX", OperationUtillity.NullReplace(rs.getString("LIMIT_PREFIX")));
	childLimitNodeMap.put("SANCTION_DATE", OperationUtillity.NullReplace(rs.getString("SANCTION_DATE")));
	childLimitNodeMap.put("LIMIT_SUFFIX", OperationUtillity.NullReplace(rs.getString("LIMIT_SUFFIX")));
	childLimitNodeMap.put("SANCTION_AMOUNT",
		commonUtility.millionString(OperationUtillity.NullReplace(rs.getString("SANCTION_AMOUNT"))));
	childLimitNodeMap.put("CURRENCY_THREE", OperationUtillity.NullReplace(rs.getString("CURRENCY_TWO")));
	childLimitNodeMap.put("CUST_ID", OperationUtillity.NullReplace(CUST_ID));
	childLimitNodeMap.put("PARENT_LIMIT_DESC", OperationUtillity.NullReplace(COMPANYNAME));
	childLimitNodeMap.put("LIEN_APPLICABILITY", OperationUtillity.NullReplace(rs.getString("LIEN_APPLICABILITY"))); // limit
	childLimitNodeMap.put("LIEN_AMOUNT",
		commonUtility.millionString(OperationUtillity.NullReplace(rs.getString("LIEN_AMOUNT"))));
	childLimitNodeMap.put("DOCUMENT_AMOUNT",
		commonUtility.millionString(OperationUtillity.NullReplace(rs.getString("DOCUMENT_AMOUNT"))));
	childLimitNodeMap.put("FACILITY_EXP_DATE", OperationUtillity.NullReplace(rs.getString("FACILITY_EXP_DATE")));
	childLimitNodeMap.put("SANCTION_VAL_DATE", OperationUtillity.NullReplace(rs.getString("SANCTION_VAL_DATE")));
	childLimitNodeMap.put("SANCTION_FORUM", OperationUtillity.NullReplace(rs.getString("SANCTION_FORUM")));
	childLimitNodeMap.put("BUSINESS_MODEL", OperationUtillity.NullReplace(rs.getString("BUSINESS_MODEL")));
	childLimitNodeMap.put("FA_EXECUTION_DATE", OperationUtillity.NullReplace(rs.getString("FA_EXECUTION_DATE")));
	childLimitNodeMap.put("SPPI", OperationUtillity.NullReplace(rs.getString("SPPI")));
	childLimitNodeMap.put("SEQUENCE_NO", OperationUtillity.NullReplace(rs.getString("SEQUENCE_NO")));
	childLimitNodeMap.put("MAIN_LIMIT", mainLimit);
	childLimitNodeMap.put("IS_REVOCABLE", OperationUtillity.NullReplace(rs.getString("IS_REVOCABLE")));
	childLimitNodeMap.put("LIEN_IN_FAVOUR_OF", OperationUtillity.NullReplace(rs.getString("LIEN_IN_FAVOUR_OF")));
	childLimitNodeMap.put("CROSS_CALL_EXISTING_LIMIT",OperationUtillity.NullReplace(rs.getString("CROSS_CALL_EXISTING_LIMIT")));
	childLimitNodeMap.putAll(getCrossCallMainLimit(pinstid, mainLimit));

	childLimitNodeMap.putAll(parentFacilityDataMap);
	childLimitNodeMap.putAll(homeTabDataMap);
    }

    public Map<String, String> getCrossCallfacilityMap(String pinstid, String facility) {
	Map<String, String> childLimitNodeMap = new ConcurrentHashMap<>();
	try (Connection con = DBConnect.getConnection();
		PreparedStatement statement = con.prepareStatement(Queries.FETCH_CROSS_CALL_INDIVIDUAL_DETAILS);) {
	    statement.setString(1, pinstid);
	    statement.setString(2, facility);
	    try (ResultSet rs = statement.executeQuery()) {
		while (rs.next()) {
		    populateCrossCallNodeMapIndividual(rs, childLimitNodeMap, pinstid);
		}
	    }
	} catch (Exception ex) {
	    logger.error("CrossCallUtility.getCrossCallfacilityMap(){} " + pinstid,
		    OperationUtillity.traceException(ex));
	}
	return childLimitNodeMap;
    }

    public static Map<String, String> getCrossCallMainLimit(String pinstid, String facility) {
	Map<String, String> mainLimitDataForCrossCall = new HashMap<>();
	try (Connection con = DBConnect.getConnection();
		PreparedStatement statement = con.prepareStatement(Queries.GET_CROSS_CALL_MAIN_LIMIT_DETAILS)) {
	    statement.setString(1, pinstid);
	    statement.setString(2, facility);
	    try (ResultSet rs = statement.executeQuery()) {
		while (rs.next()) {
		    mainLimitDataForCrossCall.put("MAIN_LIMIT_PREFIX_FOR_CROSS_CALL",
			    Optional.ofNullable(rs.getString("MAIN_LIMIT_PREFIX_FOR_CROSS_CALL")).orElse(""));
		    mainLimitDataForCrossCall.put("MAIN_LIMIT_SUFFIX_FOR_CROSS_CALL",
			    Optional.ofNullable(rs.getString("MAIN_LIMIT_SUFFIX_FOR_CROSS_CALL")).orElse(""));
		}
	    }
	} catch (Exception ex) {
	    logger.error("CrossCallUtility.getCrossCallMainLimit(){} " + pinstid, OperationUtillity.traceException(ex));
	}
	return mainLimitDataForCrossCall;
    }
}
