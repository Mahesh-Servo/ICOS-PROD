package com.LsmFiServices.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.LsmFiServices.LsmFiCommonData.childListCommonMapData;

@Controller
public class CreateFacilityLimitList {

    private static final Logger logger = LoggerFactory.getLogger(CreateFacilityLimitList.class);

    @Autowired
    private CrossCallUtility crossUtils;

    public List<Map<String, String>> createChildList(String pinstid) throws SQLException {

	List<Map<String, String>> childList = new LinkedList<>();
	String CUST_ID = "";
	String COMPANYNAME = "";
	Map<String, String> HomeTabDataMap = new LinkedHashMap<>();
	Map<String, String> parentFacilityDataMap = new LinkedHashMap<>();
	try {
	    CUST_ID = childListCommonMapData.getCustid(pinstid);
	    COMPANYNAME = childListCommonMapData.getCompanyName(pinstid);
	    HomeTabDataMap = childListCommonMapData.getHomeTabData(pinstid);
	    parentFacilityDataMap = childListCommonMapData.getParentFacilityDataMap(pinstid);

	} catch (Exception ex) {
	    logger.info("Exception while fetching input for childList :: " + pinstid + ":: "
		    + OperationUtillity.traceException(ex));
	}
	String lsql2 = "SELECT * FROM (SELECT limit.question_id limit_question, limit.answer limit_answer, limit.facility_name   facility_name FROM lsm_limit_answers limit WHERE limit.pinstid = ? AND upper(replace(FACILITY_NAME, ' ','_')) != 'PARENT_LIMIT' AND limit.question_id IN ('146','127','171','132','133','155','156','128','129','149','49','163','164','137', '134', '130', '131','122', '123', '150','125','126','141','165','212','152','226')) PIVOT (MAX ( limit_answer ) FOR limit_question IN ( 146 AS currency_one,127 AS amount_limit,171 AS currency_two,132 AS security_provider,133 AS limit_exp_date,155 AS limit_prefix,129 AS sanction_date,156 AS limit_suffix,128 AS sanction_amount,146 AS currency_three, 149 AS ACCOUNT_NO, 49 AS DISTRICT, 163 AS LIEN_APPLICABILITY, 164 AS LIEN_AMOUNT, 137 AS DOCUMENT_AMOUNT,134 AS FACILITY_EXP_DATE , 130 AS SANCTION_VAL_DATE, 131 AS SANCTION_FORUM , 122 AS BUSINESS_MODEL , 123 AS SPPI, 150 AS FA_EXECUTION_DATE ,125 AS SEQUENCE_NO, 126 AS MAIN_LIMIT , 141 AS IS_REVOCABLE,165 AS LIEN_IN_FAVOUR_OF,212 EXISTING_LMT_AS_PER_SANCTION,152 AS IS_REVOCABLE_CLAUSE, 226 AS TYPE_OF_SERVICE) ) ORDER BY SEQUENCE_NO";
	try (Connection con = DBConnect.getConnection(); PreparedStatement statement = con.prepareStatement(lsql2);) {
	    statement.setString(1, pinstid);

	    try (ResultSet rs = statement.executeQuery();) {
		while (rs.next()) {
		    Map<String, String> childLimitNodeMap = new LinkedHashMap<>();

		    childLimitNodeMap.put("PINSTID", pinstid); // pinstid
		    childLimitNodeMap.put("FACILITY_NAME",OperationUtillity.NullReplace(rs.getString("FACILITY_NAME")));
		    childLimitNodeMap.put("CURRENCY_ONE", OperationUtillity.NullReplace(rs.getString("CURRENCY_TWO")));
		    childLimitNodeMap.put("AMOUNT_LIMIT",commonUtility.millionString(OperationUtillity.NullReplace(rs.getString("AMOUNT_LIMIT"))));
		    childLimitNodeMap.put("CURRENCY_TWO", OperationUtillity.NullReplace(rs.getString("CURRENCY_TWO")));
		    childLimitNodeMap.put("SECURITY_PROVIDER",OperationUtillity.NullReplace(rs.getString("SECURITY_PROVIDER")));
		    childLimitNodeMap.put("LIMIT_EXP_DATE",OperationUtillity.NullReplace(rs.getString("LIMIT_EXP_DATE")));
		    childLimitNodeMap.put("LIMIT_PREFIX", OperationUtillity.NullReplace(rs.getString("LIMIT_PREFIX")));
		    childLimitNodeMap.put("SANCTION_DATE",OperationUtillity.NullReplace(rs.getString("SANCTION_DATE")));
		    childLimitNodeMap.put("LIMIT_SUFFIX", OperationUtillity.NullReplace(rs.getString("LIMIT_SUFFIX")));
		    childLimitNodeMap.put("SANCTION_AMOUNT", commonUtility.millionString(OperationUtillity.NullReplace(rs.getString("SANCTION_AMOUNT"))));
		    childLimitNodeMap.put("CURRENCY_THREE",OperationUtillity.NullReplace(rs.getString("CURRENCY_TWO")));
		    childLimitNodeMap.put("CUST_ID", OperationUtillity.NullReplace(CUST_ID));
		    childLimitNodeMap.put("PARENT_LIMIT_DESC", OperationUtillity.NullReplace(COMPANYNAME));
		    childLimitNodeMap.put("LIEN_APPLICABILITY",OperationUtillity.NullReplace(rs.getString("LIEN_APPLICABILITY"))); // limit LIEN
		    childLimitNodeMap.put("LIEN_AMOUNT",commonUtility.millionString(OperationUtillity.NullReplace(rs.getString("LIEN_AMOUNT"))));
		    childLimitNodeMap.put("DOCUMENT_AMOUNT", commonUtility.millionString(OperationUtillity.NullReplace(rs.getString("DOCUMENT_AMOUNT"))));
		    childLimitNodeMap.put("FACILITY_EXP_DATE",OperationUtillity.NullReplace(rs.getString("FACILITY_EXP_DATE")));
		    childLimitNodeMap.put("SANCTION_VAL_DATE",OperationUtillity.NullReplace(rs.getString("SANCTION_VAL_DATE")));
		    childLimitNodeMap.put("SANCTION_FORUM",OperationUtillity.NullReplace(rs.getString("SANCTION_FORUM")));
		    childLimitNodeMap.put("BUSINESS_MODEL",OperationUtillity.NullReplace(rs.getString("BUSINESS_MODEL")));
		    childLimitNodeMap.put("FA_EXECUTION_DATE",OperationUtillity.NullReplace(rs.getString("FA_EXECUTION_DATE")));
		    childLimitNodeMap.put("SPPI", OperationUtillity.NullReplace(rs.getString("SPPI")));
		    childLimitNodeMap.put("SEQUENCE_NO", OperationUtillity.NullReplace(rs.getString("SEQUENCE_NO")));
		    childLimitNodeMap.put("MAIN_LIMIT", OperationUtillity.NullReplace(rs.getString("MAIN_LIMIT")));
		    childLimitNodeMap.put("IS_REVOCABLE", OperationUtillity.NullReplace(rs.getString("IS_REVOCABLE")));
		    childLimitNodeMap.put("LIEN_IN_FAVOUR_OF",OperationUtillity.NullReplace(rs.getString("LIEN_IN_FAVOUR_OF"))); // added on 28.09.2023 by
		    childLimitNodeMap.put("EXISTING_LMT_AS_PER_SANCTION",OperationUtillity.NullReplace(rs.getString("EXISTING_LMT_AS_PER_SANCTION"))); // added on 28.09.2023 by
		    childLimitNodeMap.put("IS_REVOCABLE_CLAUSE",OperationUtillity.NullReplace(rs.getString("IS_REVOCABLE_CLAUSE"))); // 10064
		    childLimitNodeMap.put("TYPE_OF_SERVICE", OperationUtillity.NullReplace(rs.getString("TYPE_OF_SERVICE"))); // 10064 
		    childLimitNodeMap.putAll(parentFacilityDataMap);
		    childLimitNodeMap.putAll(HomeTabDataMap);
		    childLimitNodeMap.putAll(getFacilityWiseAccountNumbers(pinstid, childLimitNodeMap.get("FACILITY_NAME")));

		    Map<String, String> roiMapData = new LinkedHashMap<>();
		    try (Connection con4 = DBConnect.getConnection()) {
			roiMapData = ROISoapRequestDataMap.getROIData(pinstid, childLimitNodeMap.get("FACILITY_NAME"),con4);
		    } catch (Exception ex) {
			logger.info("createFacilityLimitList.createChildList().childLimitNodeMap{}"+ pinstid, OperationUtillity.traceException(ex));
		    }
		    childLimitNodeMap.putAll(roiMapData);
		    childList.add(childLimitNodeMap);
		}
	    } catch (Exception ee) {
		logger.info(OperationUtillity.traceException(ee));
	    }

	    try (Connection con3 = DBConnect.getConnection()) {
		Map<String, List<String>> PrefixSuffixMap = updateParentPrefixSuffix(pinstid);
		for (Map<String, String> childLimitNodeMap : childList) {
		    if (!childLimitNodeMap.get("SEQUENCE_NO").equals("") && !childLimitNodeMap.isEmpty()
			    && !childLimitNodeMap.get("SEQUENCE_NO").equals("2")) {
			List<String> li = PrefixSuffixMap.get(childLimitNodeMap.get("MAIN_LIMIT"));
			logger.info("childLimitNodeMap check in createChildList()-->" + childLimitNodeMap);
			logger.info("li check in createChildList()-->" + li);
			try {
			    logger.info(
				    "(!li.isEmpty() && OperationUtillity.NullReplace(li.get(0)).equals(String.valueOf(Integer.valueOf(childLimitNodeMap.get(\"SEQUENCE_NO\")) - 1))) Condition check "
					    + (!li.isEmpty() && OperationUtillity.NullReplace(li.get(0)).equals(
						    String.valueOf(Integer.valueOf(childLimitNodeMap.get("SEQUENCE_NO"))
							    - 1))));
			    if (!li.isEmpty() && OperationUtillity.NullReplace(li.get(0)).equals(
				    String.valueOf(Integer.valueOf(childLimitNodeMap.get("SEQUENCE_NO")) - 1))) {
				childLimitNodeMap.put("PARENT_LIMIT_PREFIX", OperationUtillity.NullReplace(li.get(2)));
				childLimitNodeMap.put("PARENT_LIMIT_SUFFIX", OperationUtillity.NullReplace(li.get(3)));
			    }
			} catch (Exception ex) {
			    logger.info(
				    "createFacilityLimitList.createChildList().childLimitNodeMap EXCEPTION  for  li PrefixSuffixMap  ->"
					    + pinstid + "--->" + OperationUtillity.traceException(ex));
			}
		    }
		}
	    } catch (Exception e) {
		logger.info("" + OperationUtillity.traceException(e));
	    }
	    childList.addAll(crossUtils.getCrossCallfacilityList(pinstid));
	} catch (Exception ex) {
	    logger.info("createFacilityLimitList.createChildList().exception -->" + pinstid + "    exception --->"
		    + OperationUtillity.traceException(ex));
	}
	  logger.info("childList check for pinstid :: " + pinstid + " \n" + childList);
	return childList;
    }

    public Map<String, String> getfacilityWiseData(String pinstid, String facility) throws SQLException {
	Map<String, String> childLimitNodeMap = new ConcurrentHashMap<>();

	String CUST_ID = childListCommonMapData.getCustid(pinstid);
	String COMPANYNAME = childListCommonMapData.getCompanyName(pinstid);
	Map<String, String> HomeTabDataMap = new LinkedHashMap<>();
	Map<String, String> parentFacilityDataMap = new LinkedHashMap<>();
	Map<String, List<String>> PrefixSuffixMap = new LinkedHashMap<>();
	try {
	    HomeTabDataMap = childListCommonMapData.getHomeTabData(pinstid);
	    parentFacilityDataMap = childListCommonMapData.getParentFacilityDataMap(pinstid);
	    PrefixSuffixMap = updateParentPrefixSuffix(pinstid);
	} catch (Exception ex) {
	    logger.error("CreateFacilityLimitList.getfacilityWiseData()",OperationUtillity.traceException(ex));
	}

	try (Connection con = DBConnect.getConnection();
		PreparedStatement statement = con
			.prepareStatement("SELECT MEMO_TYPE FROM LIMIT_SETUP_EXT WHERE PINSTID = ?");) {
	    statement.setString(1, pinstid);
	    try (ResultSet rs = statement.executeQuery();) {
		while (rs.next()) {
		    childLimitNodeMap.put("MEMO_TYPE", OperationUtillity.NullReplace(rs.getString("MEMO_TYPE")));
		}
		if (statement != null) {
		    statement.close();
		}
		if (rs != null) {
		    rs.close();
		}
	    }

	} catch (Exception ex) {
	    logger.info("OperationUtillity.GetParentLimitNodeData().Exception\n " + OperationUtillity.traceException(ex));
	}

	// en maheshv

	String lsql2 = "SELECT * FROM (SELECT limit.question_id limit_question, limit.answer limit_answer, limit.facility_name facility_name FROM lsm_limit_answers limit WHERE limit.pinstid = ? AND FACILITY_NAME=? AND limit.question_id IN ( '146','127','171','132','133','155','156','128','129','149','49','163','164','137', '134' , '130', '131','122', '123' , '150','125','126','141','165','212','152')) PIVOT (MAX ( limit_answer ) FOR limit_question IN ( 146 AS currency_one,127 AS amount_limit,171 AS currency_two,132 AS security_provider,133 AS limit_exp_date,155 AS limit_prefix,129 AS sanction_date,156 AS limit_suffix,128 AS sanction_amount,146 AS currency_three, 149 AS ACCOUNT_NO, 49 AS DISTRICT, 163 AS LIEN_APPLICABILITY, 164 AS LIEN_AMOUNT , 137 AS DOCUMENT_AMOUNT, 134 AS FACILITY_EXP_DATE , 130 AS SANCTION_VAL_DATE , 131 AS SANCTION_FORUM , 122 AS BUSINESS_MODEL, 123 AS SPPI, 150 AS FA_EXECUTION_DATE, 125 AS SEQUENCE_NO, 126 AS MAIN_LIMIT , 141 AS IS_REVOCABLE, 165 AS LIEN_IN_FAVOUR_OF,212 EXISTING_LMT_AS_PER_SANCTION,152 AS IS_REVOCABLE_CLAUSE) ) ORDER BY SEQUENCE_NO";
	try (Connection con = DBConnect.getConnection(); PreparedStatement statement = con.prepareStatement(lsql2);) {
	    statement.setString(1, pinstid);
	    statement.setString(2, facility);
	    try (ResultSet rs = statement.executeQuery();) {

		while (rs.next()) {
		    childLimitNodeMap.put("PINSTID", pinstid);
		    childLimitNodeMap.put("FACILITY_NAME",
			    OperationUtillity.NullReplace(rs.getString("FACILITY_NAME")));
		    childLimitNodeMap.put("CURRENCY_ONE", OperationUtillity.NullReplace(rs.getString("CURRENCY_ONE")));
		    childLimitNodeMap.put("AMOUNT_LIMIT",
			    commonUtility.millionString(OperationUtillity.NullReplace(rs.getString("AMOUNT_LIMIT"))));
		    childLimitNodeMap.put("CURRENCY_TWO", OperationUtillity.NullReplace(rs.getString("CURRENCY_TWO")));
		    childLimitNodeMap.put("SECURITY_PROVIDER",
			    OperationUtillity.NullReplace(rs.getString("SECURITY_PROVIDER")));
		    childLimitNodeMap.put("LIMIT_EXP_DATE",
			    OperationUtillity.NullReplace(rs.getString("LIMIT_EXP_DATE")));
		    childLimitNodeMap.put("LIMIT_PREFIX", OperationUtillity.NullReplace(rs.getString("LIMIT_PREFIX")));
		    childLimitNodeMap.put("SANCTION_DATE",
			    OperationUtillity.NullReplace(rs.getString("SANCTION_DATE")));
		    childLimitNodeMap.put("LIMIT_SUFFIX", OperationUtillity.NullReplace(rs.getString("LIMIT_SUFFIX")));
		    childLimitNodeMap.put("SANCTION_AMOUNT", commonUtility
			    .millionString(OperationUtillity.NullReplace(rs.getString("SANCTION_AMOUNT"))));
		    childLimitNodeMap.put("CURRENCY_THREE",
			    OperationUtillity.NullReplace(rs.getString("CURRENCY_THREE")));
		    childLimitNodeMap.put("CUST_ID", OperationUtillity.NullReplace(CUST_ID));
		    childLimitNodeMap.put("PARENT_LIMIT_DESC", OperationUtillity.NullReplace(COMPANYNAME));
		    childLimitNodeMap.put("LIEN_APPLICABILITY",
			    OperationUtillity.NullReplace(rs.getString("LIEN_APPLICABILITY"))); // limit LIEN
		    childLimitNodeMap.put("SEQUENCE_NO", OperationUtillity.NullReplace(rs.getString("SEQUENCE_NO")));
		    childLimitNodeMap.put("MAIN_LIMIT", OperationUtillity.NullReplace(rs.getString("MAIN_LIMIT")));
		    childLimitNodeMap.put("IS_REVOCABLE", OperationUtillity.NullReplace(rs.getString("IS_REVOCABLE")));
		    childLimitNodeMap.put("LIEN_IN_FAVOUR_OF",
			    OperationUtillity.NullReplace(rs.getString("LIEN_IN_FAVOUR_OF"))); // added on 28.09.2023 by
		    childLimitNodeMap.put("LIEN_AMOUNT",
			    commonUtility.millionString(OperationUtillity.NullReplace(rs.getString("LIEN_AMOUNT"))));
		    childLimitNodeMap.put("DOCUMENT_AMOUNT", commonUtility
			    .millionString(OperationUtillity.NullReplace(rs.getString("DOCUMENT_AMOUNT"))));
		    childLimitNodeMap.put("FACILITY_EXP_DATE",
			    OperationUtillity.NullReplace(rs.getString("FACILITY_EXP_DATE")));
		    childLimitNodeMap.put("SANCTION_VAL_DATE",
			    OperationUtillity.NullReplace(rs.getString("SANCTION_VAL_DATE")));
		    childLimitNodeMap.put("SANCTION_FORUM",
			    OperationUtillity.NullReplace(rs.getString("SANCTION_FORUM")));
		    childLimitNodeMap.put("BUSINESS_MODEL",
			    OperationUtillity.NullReplace(rs.getString("BUSINESS_MODEL")));
		    childLimitNodeMap.put("FA_EXECUTION_DATE",
			    OperationUtillity.NullReplace(rs.getString("FA_EXECUTION_DATE")));
		    childLimitNodeMap.put("IS_REVOCABLE_CLAUSE",
				    OperationUtillity.NullReplace(rs.getString("IS_REVOCABLE_CLAUSE")));  //JIRA-10064
		    childLimitNodeMap.put("SPPI", OperationUtillity.NullReplace(rs.getString("SPPI")));
		    childLimitNodeMap.put("EXISTING_LMT_AS_PER_SANCTION",OperationUtillity.NullReplace(rs.getString("EXISTING_LMT_AS_PER_SANCTION"))); // added on 28.09.2023 by
		    childLimitNodeMap.putAll(parentFacilityDataMap);
		    childLimitNodeMap.putAll(HomeTabDataMap);
		    childLimitNodeMap.putAll(getFacilityWiseAccountNumbers(pinstid, childLimitNodeMap.get("FACILITY_NAME")));
		}
	    }

	    try {
//		commented on 19.11.2024
//		if (!OperationUtillity.NullReplace(childLimitNodeMap.get("SEQUENCE_NO")).equals("2")) {
//
//		    List<String> li = PrefixSuffixMap.get(childLimitNodeMap.get("MAIN_LIMIT"));
//		    if (!OperationUtillity.NullReplace(li.get(0)).isEmpty() && OperationUtillity.NullReplace(li.get(0))
//			    .equals(String.valueOf(Integer.valueOf(childLimitNodeMap.get("SEQUENCE_NO")) - 1))) {
//			childLimitNodeMap.put("PARENT_LIMIT_PREFIX", OperationUtillity.NullReplace(li.get(2)));
//			childLimitNodeMap.put("PARENT_LIMIT_SUFFIX", OperationUtillity.NullReplace(li.get(3)));
//		    }
//		}
	    } catch (Exception e) {
		logger.info("createFacilityLimitList.getfacilityWiseData() updateParentPrefixSuffix exception \n"
			+ OperationUtillity.traceException(e));
	    }

	    Map<String, String> roiMapData = new LinkedHashMap<>();
	    try {
		roiMapData = ROISoapRequestDataMap.getROIData(pinstid, facility, con);
		childLimitNodeMap.putAll(roiMapData);
		childLimitNodeMap.putAll(crossUtils.getCrossCallfacilityMap(pinstid, facility));
	    } catch (Exception ex) {
		logger.info("createFacilityLimitList.getfacilityWiseData().getROIData() exception \n "
			+ OperationUtillity.traceException(ex));
	    }
	} catch (Exception ex) {
	    logger.info("createFacilityLimitList.getfacilityWiseData() exception  --->\n"
		    + OperationUtillity.traceException(ex));
	}
	return childLimitNodeMap;
    }

    public String getDistrictCodeFromMaster(String Account_no, String pinstid) throws SQLException {

	String solId = Account_no.substring(0, 4);
	String code = "";

//		String DISTRICT_CODE_QUERY = ReadPropertyFIle.getInstance().getPropConst().getProperty("DISTRICT_CODE_QUERY");
//		try (Connection con = DBConnect.getConnection(); PreparedStatement statement = con.prepareStatement(DISTRICT_CODE_QUERY);) {
	try (Connection con = DBConnect.getConnection();
		PreparedStatement statement = con
			.prepareStatement("SELECT DISTRICT_CODE FROM LSM_MS_DISTRICT_CODE WHERE SOL_ID ="
				+ "(SELECT DISTINCT(SOL_ID) FROM lsm_rm_signed_api_info where pinstid="
				+ "(SELECT DISTINCT(DISHUBID) FROM LIMIT_SETUP_EXT where pinstid=?) and FIELD_VALUE =?)");) {

	    statement.setString(1, pinstid);
	    statement.setString(2, Account_no);

	    try (ResultSet rs = statement.executeQuery();) {
		while (rs.next()) {
		    code = OperationUtillity.NullReplace(rs.getString("DISTRICT_CODE"));
		}
	    }
	} catch (Exception ex) {
	    logger.info("createFacilityLimitList.getDistrictCodeFromMaster() exception::"
		    + OperationUtillity.traceException(ex));
	}
	return code;
    }

    public Map<String, String> getPSLOtherData(String PINSTID, String Facility) throws SQLException {

	Map<String, String> pslOtherDataMap = new LinkedHashMap<>();
	try (Connection con = DBConnect.getConnection();
		PreparedStatement statement = con.prepareStatement(Queries.GET_PSL_OTHERS_DETAILS);) {
	    statement.setString(1, PINSTID);
	    statement.setString(2, Facility);
	    statement.setString(3, PINSTID);
	    statement.setString(4, Facility);
	    try (ResultSet rs = statement.executeQuery();) {
		while (rs.next()) {
		    pslOtherDataMap.put("MODE_OF_ADVANCE",Optional.ofNullable(rs.getString("MODE_OF_ADVANCE")).orElse(""));
		    pslOtherDataMap.put("SECTOR_CODE", Optional.ofNullable(rs.getString("SECTOR_CODE")).orElse(""));
		    pslOtherDataMap.put("SUB_SECTOR_CODE",Optional.ofNullable(rs.getString("SUB_SECTOR_CODE")).orElse(""));
		}
	    }
	} catch (Exception ex) {
	    logger.info(
		    "createFacilityLimitList.getPSLOtherData() exception --->" + OperationUtillity.traceException(ex));
	}
	return pslOtherDataMap;
    }

    public Map<String, List<String>> updateParentPrefixSuffix(String pinstId) throws SQLException {
	Map<String, List<String>> MapList = new LinkedHashMap<>();

	String queryToUpdatePreSuf = ReadPropertyFIle.getInstance().getPropConst()
		.getProperty("QUERY_TO_UPDATE_PRE_SUF");
	try (Connection con = DBConnect.getConnection();
		PreparedStatement statement = con.prepareStatement(queryToUpdatePreSuf);) {
	    statement.setString(1, pinstId);
	    try (ResultSet rs = statement.executeQuery();) {
		while (rs.next()) {
		    List<String> li = new LinkedList<>();
		    li.add(OperationUtillity.NullReplace(rs.getString("SEQUENCE_NO")));
		    li.add(OperationUtillity.NullReplace(rs.getString("MAIN_LIMIT")));
		    li.add(OperationUtillity.NullReplace(rs.getString("PREFIX")));
		    li.add(OperationUtillity.NullReplace(rs.getString("SUFFIX")));
		    MapList.put(OperationUtillity.NullReplace(rs.getString("FACILITY_NAME")), li);
		}
	    }
	} catch (Exception ex) {
	    logger.info("createFacilityLimitList.updateParentPrefixSuffix().Exception ------------------->" + pinstId
		    + " :: " + OperationUtillity.traceException(ex));
	}
	return MapList;
    }

    public Map<String, String> getFacilityWiseAccountNumbers(String pinstId, String facilityName) {

	Map<String, String> childLimitNodeMap = new LinkedHashMap<>();
	String query = "((SELECT QUESTION_ID, ANSWER FROM LSM_LIMIT_ANSWERS WHERE QUESTION_ID LIKE '157%' OR QUESTION_ID LIKE '158%' OR QUESTION_ID LIKE '162%' OR QUESTION_ID LIKE '149%') INTERSECT (SELECT QUESTION_ID,ANSWER FROM LSM_LIMIT_ANSWERS WHERE PINSTID=? AND FACILITY_NAME=?)) ORDER BY QUESTION_ID";
	try (Connection con = DBConnect.getConnection(); PreparedStatement ptmst = con.prepareStatement(query);) {
	    ptmst.setString(1, pinstId);
	    ptmst.setString(2, facilityName);
	    try (ResultSet rset = ptmst.executeQuery();) {
		int counter1 = 0, counter2 = 0, counter3 = 0, counter4 = 0;
		while (rset.next()) {
		    if (rset.getString(1) != null && OperationUtillity.NullReplace(rset.getString(1)) != "") {

			if (OperationUtillity.NullReplace(rset.getString(1)).contains("149")) {
			    childLimitNodeMap.put("ACCOUNT_NO_" + counter1,
				    OperationUtillity.NullReplace(rset.getString(2)));
			    counter1++;
			}
			if (OperationUtillity.NullReplace(rset.getString(1)).contains("157")) {
			    childLimitNodeMap.put("PROPOSED_TOTAL_" + counter2,
				    OperationUtillity.NullReplace(rset.getString(2)));
			    counter2++;
			}
			if (OperationUtillity.NullReplace(rset.getString(1)).contains("158")) {
			    childLimitNodeMap.put("DP_AMOUNT_" + counter3,
				    commonUtility.millionString(OperationUtillity.NullReplace(rset.getString(2))));
			    counter3++;
			}
			if (OperationUtillity.NullReplace(rset.getString(1)).contains("162")) {
			    childLimitNodeMap.put("PROPOSED_TOTAL_LIMIT_AMOUNT_" + counter4,
				    commonUtility.millionString(OperationUtillity.NullReplace(rset.getString(2))));
			    counter4++;
			}
		    }
		}
		counter1 = 0;
		counter2 = 0;
		counter3 = 0;
		counter4 = 0;
	    }
	} catch (Exception ex) {
	    logger.info("createFacilityLimitList.getfacilityWiseData()  Exception --->"
		    + OperationUtillity.traceException(ex));
	}

	return childLimitNodeMap;

    }

    public String getPSLOccupationCode(String PINSTID) throws SQLException {

	String PslOccuCode = "";
//			String PSL_OTHER_DATA = ReadPropertyFIle.getInstance().getPropConst().getProperty("PSL_OTHER_DATA");
	String PSL_OCCU_CODE = "SELECT SUBSTR(OCCUPATION_CODE,0,INSTR(OCCUPATION_CODE,'-')-1) AS OCCUPATION_CODE FROM SMEAG_CUSTOMERDETAIL WHERE PINSTID IN (SELECT APPRAISALID FROM LIMIT_SETUP_EXT WHERE PINSTID = ? )";
	try (Connection con = DBConnect.getConnection();
		PreparedStatement statement = con.prepareStatement(PSL_OCCU_CODE);) {
	    statement.setString(1, PINSTID);
	    try (ResultSet rs = statement.executeQuery();) {
		while (rs.next()) {
		    PslOccuCode = OperationUtillity.NullReplace(rs.getString("OCCUPATION_CODE"));

		}
	    }
	    logger.info("createFacilityLimitList.getPSLOccupationCode()  PslOccuCode --->" + PslOccuCode);
	} catch (Exception ex) {
	    logger.info("createFacilityLimitList.getPSLOccupationCode() exception --->"
		    + OperationUtillity.traceException(ex));
	}
	return PslOccuCode;
    }
 // SN OF MAHESHV ON 10122024
    public Map<String, String> createFacilityListForDPCheck(String pinstid) throws SQLException {
    	Map<String, String> facilitListMap = new LinkedHashMap<>();
    	//String query = "SELECT * FROM (SELECT limit.question_id limit_question, limit.answer limit_answer, limit.facility_name facility_name FROM lsm_limit_answers limit WHERE limit.pinstid = ? AND upper(replace(FACILITY_NAME, ' ','_')) != 'PARENT_LIMIT' AND limit.question_id IN ('149')) PIVOT (MAX ( limit_answer ) FOR limit_question IN (  149 AS ACCOUNT_NO, 125 AS SEQUENCE_NO ) ) ORDER BY SEQUENCE_NO";
    	String query = "SELECT FACILITY_NAME,QUESTION,ANSWER AS ACCOUNT_NO FROM LSM_LIMIT_ANSWERS WHERE PINSTID = ?  AND QUESTION_ID LIKE '149%'";
    	try (Connection con = DBConnect.getConnection(); PreparedStatement ptmst = con.prepareStatement(query);) {
    	    ptmst.setString(1, pinstid);
    	    try (ResultSet rset = ptmst.executeQuery();) {
    		while (rset.next()) {
    		   facilitListMap.put(OperationUtillity.NullReplace(rset.getString("FACILITY_NAME"))+"-"+OperationUtillity.NullReplace(rset.getString("ACCOUNT_NO")),OperationUtillity.NullReplace(rset.getString("ACCOUNT_NO")));
    	    }
    	} catch (Exception ex) {
    	    logger.info("createFacilityLimitList.createFacilityListForDPCheck()  Exception --->"
    		    + OperationUtillity.traceException(ex));
    	}
    	    logger.info("createFacilityLimitList.createFacilityListForDPCheck().facilitListMap --->"
        		    + facilitListMap.toString());
    	return facilitListMap;
    }
  }
 // EN OF MAHESHV ON 10122024
}
