package com.LsmFiServices.Utility;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.LsmFiServices.pojo.leinumenquiry.ExecuteFinacleScriptResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class LEI_URCCNumberEnquiryUtility {

    private static final Logger logger = LoggerFactory.getLogger(LEI_URCCNumberEnquiryUtility.class);

    @Autowired
    private SVTFIServiceUtility svtUtility;

    public String getUCCBasedCustId(String pinstId) {
	return svtUtility.getUCCBasedCustId(pinstId);
    }

    public Map<String, String> getLEI_URCC_InputDate(String pinstId) {
	String query = "SELECT * FROM (SELECT HOME .QUESTION_ID LIMIT_QUESTION, HOME .ANSWER LIMIT_ANSWER  FROM LSM_MS_ANSWERS HOME WHERE HOME .PINSTID = ? AND HOME .QUESTION_ID IN ( '18','19','28')) PIVOT (MAX ( LIMIT_ANSWER ) FOR LIMIT_QUESTION IN ( '18' AS LEI_NUMBER ,'19' AS LEI_EXPIRY_DATE  ,'28' AS URCC_NUMBER))";
	Map<String, String> map = new HashMap<>();
	try (Connection con = DBConnect.getConnection()) {
	    try (PreparedStatement psmt = con.prepareStatement(query)) {
		psmt.setString(1, pinstId);
		try (ResultSet rs = psmt.executeQuery()) {
		    while (rs.next()) {
			map.put("LEI_NUMBER", OperationUtillity.NullReplace(rs.getString("LEI_NUMBER")));
			map.put("LEI_EXPIRY_DATE", OperationUtillity.NullReplace(rs.getString("LEI_EXPIRY_DATE")));
			map.put("URCC_NUMBER", OperationUtillity.NullReplace(rs.getString("URCC_NUMBER")));
		    }
		}
	    }
	} catch (Exception e) {
	    logger.info("Exception while getting LEIExpireyDate::" + OperationUtillity.traceException(e));
	}
//		logger.info("LEI_URCCNumberEnquiryUtility.getLEI_URCC_InputDate()-->"+map);
	return map;
    }

    public ExecuteFinacleScriptResponse convertXmlToJson(String xmlData, String pinstid)
	    throws JsonProcessingException {
	ExecuteFinacleScriptResponse rootTag = null;
	try {
	    JAXBContext jaxbContext = JAXBContext.newInstance(ExecuteFinacleScriptResponse.class);
	    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
	    rootTag = (ExecuteFinacleScriptResponse) unmarshaller.unmarshal(new StringReader(xmlData));

	} catch (JAXBException e) {
	    logger.info("Exception while unmarshlling XML::\n" + OperationUtillity.traceException(e));
	}
	// logger.info("Root tag data check--->" + rootTag);
	return rootTag;
    }

    public Map<String, Object> successPacketDataLEINumber(String pinstId, String xmlPacket)
	    throws SQLException, JsonProcessingException {
	OperationUtillity OperationUtility = new OperationUtillity();

	Map<String, Object> datamap = new ConcurrentHashMap<>();
	String userName = OperationUtility.getuserName(pinstId);

	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("BankId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");
	datamap.put("userName", OperationUtillity.NullReplace(userName));
	datamap.put("ErrorDesc", "");
	datamap.put("executeFinacleScript_CustomData", "");

	Iterator<Map.Entry<String, Object>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, Object> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}

	    } catch (Exception e) {
		logger.info(" LEINumberEnquiryUtility.successPacketDataLEINumber() \n"
			+ OperationUtillity.traceException(e));
	    }
	}
	if (datamap.containsKey("executeFinacleScript_CustomData")) {
	    StringBuilder sd = new StringBuilder();
	    sd.append("<executeFinacleScriptCustomData>");
	    sd.append(datamap.get("executeFinacleScript_CustomData"));
	    sd.append("</executeFinacleScriptCustomData>");
	    datamap.put("executeFinacleScriptCustomData", sd.toString());
	    datamap.remove("executeFinacleScript_CustomData");
	}
	try {
	    datamap.put("ExecuteFinacleScriptResponse",
		    convertXmlToJson(datamap.get("executeFinacleScriptCustomData").toString(), pinstId));
	} catch (Exception e) {
	    logger.error("Exception while putting data into datamap");
	}
	return datamap;
    }

    public Map<String, String> API_RequestResponse_Insert(String request, String leiNumEnquiryResp, String req_type,
	    String PINSTID, Map<String, String> API_REQ_RES_map, String requestUUID) throws SQLException {

	Map<String, String> inputMap = new WeakHashMap<>();
	Connection con = null;
	PreparedStatement statement = null;
	String lsql = null;

	try {
	    con = DBConnect.getConnection();
	    if (con == null) {
		inputMap.put("Result", "Fail");
		inputMap.put("Message", "DB connection not established");
		return inputMap;
	    } else {
		con.setAutoCommit(false);
		lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("API_REQ_RESP_INSERT");

		statement = con.prepareStatement(lsql);
		statement.setString(1, OperationUtillity.NullReplace(PINSTID));
		statement.setString(2, OperationUtillity.NullReplace(request));
		statement.setString(3, OperationUtillity.NullReplace(leiNumEnquiryResp));
		statement.setString(4, OperationUtillity.NullReplace(req_type));
		statement.setString(5, OperationUtillity.NullReplace(API_REQ_RES_map.get("Status").toString()));
		statement.setString(6, OperationUtillity.NullReplace(API_REQ_RES_map.get("ErrorDesc").toString()));
		int int_count = statement.executeUpdate();

		if (int_count > 0) {
		    logger.info("LEI URCC Enquiry Response Map check-->" + leiNumEnquiryResp);
		    con.commit();
		}
		if (statement != null) {
		    statement.close();
		}
	    }
	} catch (Exception e) {
	    con.rollback();
	    logger.info("LEINumberEnquiryUtility.API_RequestResponse_Insert() exception "
		    + OperationUtillity.traceException(e));
	} finally {
	    if (statement != null) {
		statement.close();
	    }
	    if (con != null) {
		con.close();
	    }
	}
	return inputMap;
    }

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
	logger.info("Unique CustId Final List check ====> " + uniqueCusIdListOfMap.size());
	return uniqueCusIdListOfMap;
    }

    public Map<String, String> getIndividualData(String inputString) {
	Map<String, String> outputMap = new HashMap<>();
	// here operation will be like =>URCC NUMBER ADD SERVICE, CUST_ID :: 658268648,
	// REFERENCE_NUMBER :: 54637289R789
	if (inputString.contains("URCC NUMBER")) {
	    outputMap.put("OPERATION", inputString.split(",")[0].split(" ")[2].trim());
	    outputMap.put("CUSTID", inputString.split("::")[1].split(",")[0].trim());
	    outputMap.put("REFERENCE_NUMBER_URCC", inputString.split("::")[2].trim());
	} else if (inputString.contains("LEI NUMBER")) {
	    outputMap.put("OPERATION", inputString.split(",")[0].split(" ")[2].trim());
	    outputMap.put("CUSTID", inputString.split("::")[1].split(",")[0].trim());
	    outputMap.put("REFERENCE_NUMBER_LEI", inputString.split("::")[2].trim());
	}
	return outputMap;
    }

    public String getCustIdForLEIURCCEnquiry(String inputString) {
	// here operation will be like =>LEI_URCC_NUMBER_ENQUIRY, CUSTID :: 658268648
	return inputString.split("::")[1].trim();
    }

    public boolean getLeiUrccFlag(String pinstid) {
	boolean flag = false;
	try (Connection con = DBConnect.getConnection();
		PreparedStatement pst = con.prepareStatement(Queries.GET_FLAG_FOR_LEI_URCC)) {
	    pst.setString(1, pinstid);
	    pst.setString(2, pinstid);
	    try (ResultSet rs = pst.executeQuery()) {
		if (rs.next()) {
		    flag = true;
		}
	    }
	} catch (Exception e) {
	    logger.error("LEI_URCCNumberEnquiryUtility.getLeiUrccFlag(){}", OperationUtillity.traceException(e));
	}
	return flag;
    }

    public List<Map<String, String>> getLeiUrccNums(String pinstid) {
	List<Map<String, String>> outputList = new ArrayList<>();
	try (Connection con = DBConnect.getConnection();
		PreparedStatement pst = con.prepareStatement(Queries.FETCH_LEI_URCC_NUMS)) {
	    pst.setString(1, pinstid);
	    try (ResultSet rs = pst.executeQuery()) {
		while (rs.next()) {
		    Map<String, String> dataMap = new HashMap<>();
		    dataMap.put("URCC_NUMBER", Optional.ofNullable(rs.getString("URCC")).orElse(""));
		    dataMap.put("LEI_NUMBER", Optional.ofNullable(rs.getString("LEI")).orElse(""));
		    dataMap.put("LEI_EXPIRY_DATE", Optional.ofNullable(rs.getString("LEI_EXPIRY_DATE")).orElse(""));
		    dataMap.put("CUSTID", getCustId(pinstid));
		    outputList.add(dataMap);
		}
	    }
	} catch (Exception e) {
	    logger.error("LEI_URCCNumberEnquiryUtility.getLeiUrccNums(){}", OperationUtillity.traceException(e));
	}
	return outputList;
    }

    public String getCustId(String pinstid) {
	String custid = "";
	try (Connection con = DBConnect.getConnection();
		PreparedStatement pst = con.prepareStatement(Queries.FETCH_CUSTID)) {
	    pst.setString(1, pinstid);
	    pst.setString(2, "54");
	    try (ResultSet rs = pst.executeQuery()) {
		if (rs.next()) {
		    custid = Optional.ofNullable(rs.getString("CUSTID")).orElse("");
		}
	    }
	} catch (Exception e) {
	    logger.error("LEI_URCCNumberEnquiryUtility.getCustId(){}", OperationUtillity.traceException(e));
	}
	return custid;
    }
}
