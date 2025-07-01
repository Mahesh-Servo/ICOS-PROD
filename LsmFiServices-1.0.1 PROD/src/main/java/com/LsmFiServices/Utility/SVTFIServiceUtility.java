
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
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class SVTFIServiceUtility {

    private static final Logger logger = LoggerFactory.getLogger(SVTFIServiceUtility.class);
    String securityId = "";
    
    public List<Map<String, String>> getSVTDataListOfMap(String pinstid, String security) throws SQLException {
	Map<String, String> svtCommonData = null;
	List<Map<String, String>> securityWiseOtherDetailsListofMap = null;
	String uccBasedCustId = getUCCBasedCustId(pinstid);
	Map<String, List<String>> securitywisePropertyOwnersmap = new HashMap<>();
	List<Map<String, String>> finalListofMap = new LinkedList<>();
	List<Map<String, String>> collateralCodeCumFinalList = new LinkedList<>();
	logger.info("Entered into getSVTDataListOfMap():: " + pinstid);
	try {
	    svtCommonData = getSVTCommonData(pinstid);
	    securityWiseOtherDetailsListofMap = getSecurityWiseOtherDetails(pinstid);
	} catch (Exception e1) {
	    logger.info("Exception while getting svtCommonData-->" + OperationUtillity.traceException(e1));
	}
	try (Connection con = DBConnect.getConnection()) {
	    String query = "";

	    if (security.equalsIgnoreCase("common")) {
		query = "SELECT * FROM (SELECT SECURITY.QUESTION_ID AS SECURITY_QUESTION, SECURITY.ANSWER AS SECURITY_ANSWER, SECURITY.SECURITY_PROVIDER_NAME AS SECURITY_NAME  FROM LSM_SECURITY_ANSWERS SECURITY WHERE SECURITY.PINSTID = ?  AND SECURITY.QUESTION_ID IN ('76','78','79','87','92','93','94','186','189','190','191','192','193','194','195','196','197','84','186'))  PIVOT ( MAX (SECURITY_ANSWER)  FOR SECURITY_QUESTION  IN ('76' AS SECURITY,'78' AS OWNERSHIP_STATUS,'79' AS VALUATION_DATE,'87' AS SECURITY_CREATED ,'92' AS SECURITY_SUBMISSION_DATE, '93' AS APPORTIONED_VALUE, '94' AS SECURITY_PROVIDER_NAME, '186' AS REVIEW_DATE,'189' AS ADDRESS_LINE,'190' AS AREA,'191' AS ROAD, '192' AS LANDMARK,'193' AS PINCODE,'194' AS DISTRICT ,'195' AS CITY,'196' AS STATE,'197' AS COUNTRY , '84' AS SECURITY_DUE_DATE, '186' AS SECURITY_REVIEW_DATE )) WHERE SECURITY_CREATED = 'Yes' ORDER BY SECURITY_NAME";
	    } else if (!security.equalsIgnoreCase("common")) {
		query = "SELECT * FROM (SELECT * FROM (SELECT SECURITY.QUESTION_ID AS SECURITY_QUESTION, SECURITY.ANSWER AS SECURITY_ANSWER, SECURITY.SECURITY_PROVIDER_NAME AS SECURITY_NAME  FROM LSM_SECURITY_ANSWERS SECURITY WHERE SECURITY.PINSTID = ?   AND  SECURITY.QUESTION_ID IN ('76','78','79','87','92','93','94','186','189','190','191','192','193','194','195','196','197','84','186')  )  PIVOT ( MAX (SECURITY_ANSWER)  FOR SECURITY_QUESTION  IN ('76' AS SECURITY,'78' AS OWNERSHIP_STATUS,'79' AS VALUATION_DATE,'87' AS SECURITY_CREATED ,'92' AS SECURITY_SUBMISSION_DATE, '93' AS APPORTIONED_VALUE, '94' AS SECURITY_PROVIDER_NAME, '186' AS REVIEW_DATE,'189' AS ADDRESS_LINE,'190' AS AREA,'191' AS ROAD, '192' AS LANDMARK,'193' AS PINCODE,'194' AS DISTRICT ,'195' AS CITY,'196' AS STATE,'197' AS COUNTRY,'84' AS SECURITY_DUE_DATE, '186' AS SECURITY_REVIEW_DATE))   ORDER BY SECURITY_NAME) WHERE SECURITY_CREATED = 'Yes' AND SECURITY_NAME  ='"
			+ security + "'";
	    }
	    PreparedStatement pst = con.prepareStatement(query);
	    pst.setString(1, pinstid);
	    try (ResultSet rs = pst.executeQuery()) {
		while (rs.next()) {
		    logger.info("rs.getString(SECURITY)--->" + rs.getString("SECURITY") + "------------> and count -->");
		    if (("Fixed Deposit (Others)".equals(rs.getString("SECURITY")) || ("Immovable Fixed Assets".equalsIgnoreCase(rs.getString("SECURITY"))))) {

			Map<String, String> map_data = new LinkedHashMap<>();
			String ownershipStatus = OperationUtillity.NullReplace(rs.getString("OWNERSHIP_STATUS"));
			String secName = OperationUtillity.NullReplace(rs.getString("SECURITY_NAME"));// Security (3)
			map_data.put("SECURITY_NAME", secName); 
			map_data.put("SECURITY_TYPE", OperationUtillity.NullReplace(rs.getString("SECURITY"))); // Immovable, etc
			map_data.put("VALUATION_DATE", OperationUtillity.NullReplace(rs.getString("VALUATION_DATE")));
			map_data.put("SECURITY_CREATED",OperationUtillity.NullReplace(rs.getString("SECURITY_CREATED")));
			map_data.put("SECURITY_SUBMISSION_DATE",OperationUtillity.NullReplace(rs.getString("SECURITY_SUBMISSION_DATE")));
			map_data.put("APPORTIONED_VALUE",OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
			map_data.put("REVIEW_DATE", OperationUtillity.NullReplace(rs.getString("REVIEW_DATE")));
			map_data.put("ADDRESS_LINE_1", OperationUtillity.NullReplace(rs.getString("ADDRESS_LINE")));
			map_data.put("AREA", OperationUtillity.NullReplace(rs.getString("AREA")));
			map_data.put("ROAD", OperationUtillity.NullReplace(rs.getString("ROAD")));
			map_data.put("LANDMARK", OperationUtillity.NullReplace(rs.getString("LANDMARK")));
			map_data.put("PINCODE", OperationUtillity.NullReplace(rs.getString("PINCODE")));
			map_data.put("DISTRICT", OperationUtillity.NullReplace(rs.getString("DISTRICT")));
			map_data.put("CITY", OperationUtillity.NullReplace(rs.getString("CITY")));
			map_data.put("STATE", OperationUtillity.NullReplace(rs.getString("STATE")));
			map_data.put("COUNTRY", OperationUtillity.NullReplace(rs.getString("COUNTRY")));
			map_data.put("SECURITY_DUE_DATE",OperationUtillity.NullReplace(rs.getString("SECURITY_DUE_DATE")));
			map_data.put("SECURITY_REVIEW_DATE",OperationUtillity.NullReplace(rs.getString("SECURITY_REVIEW_DATE")));
			map_data.put("COLLATERAL_CLASS", fetchCollateralClass(ownershipStatus.toUpperCase()));
			map_data.put("UCC_BASED_CUST_ID", uccBasedCustId);
			securitywisePropertyOwnersmap = getPropertyOwner(pinstid, secName);
			String propertyOwner = securitywisePropertyOwnersmap.get(secName).toString();
			propertyOwner = propertyOwner.replaceAll("\\[|\\]", ""); // getting multiple prop owner and adding in one string with "," separated
			map_data.put("PROPERTY_OWNER", propertyOwner);
			logger.info("Main Map Data After query --1" + map_data);

			map_data.putAll(svtCommonData);
			for (Map<String, String> securityWiseMap : securityWiseOtherDetailsListofMap) {
			    if (OperationUtillity.NullReplace(rs.getString("SECURITY")).equalsIgnoreCase(OperationUtillity.NullReplace(securityWiseMap.get("SECURITY_DETAILS")))) {// imova,etc
				map_data.putAll(securityWiseMap);
			    }
			}				
			// Address is not needed for FD
			if (!"Fixed Deposit (Others)".equals(rs.getString("SECURITY"))) {
			    map_data.putAll(getAddressCodes(OperationUtillity.NullReplace(rs.getString("PINCODE"))));
			}
			logger.info("Main Map Data After adding City Code etc" + map_data);
			logger.info("common data for security "+ OperationUtillity.NullReplace(rs.getString("SECURITY_NAME")) + " is :: \n"+ map_data);
			collateralCodeCumFinalList = getCollateralCode(pinstid,OperationUtillity.NullReplace(rs.getString("SECURITY_NAME")));
			logger.info("collateralCodeCumFinalList check in service{}" , collateralCodeCumFinalList);

			for (Map<String, String> map1 : collateralCodeCumFinalList) {
			    if ("Fixed Deposit (Others)".equals(rs.getString("SECURITY"))  && map1.get("SUB_TYPE_SECURITY_SVT").equals("Fixed Deposit/Bank Deposit")) {
				map_data.put("SECURITY_PROVIDER_ID",map1.get("SECURITY_PROVIDER_NAME").replaceAll("[()]", "").split(" ")[1]);
				map1.putAll(map_data);
				finalListofMap.add(map1);
			    } else if ("Immovable Fixed Assets".equalsIgnoreCase(rs.getString("SECURITY"))) {
				map_data.put("SECURITY_PROVIDER_ID",map1.get("SECURITY_PROVIDER_NAME").replaceAll("[()]", "").split(" ")[1]);
				map1.putAll(map_data);
				finalListofMap.add(map1);
			    }
			}
			logger.info("SVTFIServiceUtility.getSVTDataListOfMap().collateralCodeCumFinalList After Modifying ::\n",collateralCodeCumFinalList);
			logger.info("SVTFIServiceUtility.getSVTDataListOfMap().finalListofMap {}",finalListofMap);

			
			secName = secName.replaceAll("[()]", "");
			String[] secNameArray = secName.split(" ");
			securityId = secNameArray[1];
			// checking svt is called from main controller or individual
			if (!security.equalsIgnoreCase("common")) {
			    finalListofMap = finalListofMap.stream() // if individual then removing other securities maps from list
				    .filter(map -> securityId.equals(map.get("SECURITY_PROVIDER_ID")))
				    .collect(Collectors.toList());
			    logger.info("You are hitting individuals service for :: "+ OperationUtillity.NullReplace(rs.getString("SECURITY"))+ " and filtered map :: \n" + finalListofMap);
			}
		    }
		}
	    } catch (SQLException e) {
		logger.error("SVTFIServiceUtility.getSVTDataListOfMap{1})" , OperationUtillity.traceException(e));
	    }
	} catch (SQLException e) {
	    logger.error("SVTFIServiceUtility.getSVTDataListOfMap{2})" , OperationUtillity.traceException(e));
	}
	logger.info("SVTFIServiceUtility.getSVTDataListOfMap() Final List of map{}", finalListofMap);
	return finalListofMap;
    }

    public Map<String, String> getSVTCommonData(String pinstid) throws SQLException, Exception {
	Map<String, String> svtCommonData = new LinkedHashMap<>();

	logger.info("SVTFIServiceUtility.getSVTCommonData() called :: " + pinstid);
	if (pinstid.contains("MON")) {
	    pinstid = pinstid.replace("MON", "LSM");
	}

	try (Connection con = DBConnect.getConnection()) {
	    // Query for LSM_Number
	    String lsmNumberQuery = "SELECT ANSWER FROM LSM_MS_ANSWERS WHERE pinstid = ? AND QUESTION_ID = '2'";
	    try (PreparedStatement stmt = con.prepareStatement(lsmNumberQuery)) {
		stmt.setString(1, pinstid);
		try (ResultSet rs = stmt.executeQuery()) {
		    while (rs.next()) {
			svtCommonData.put("LSM_NUMBER", OperationUtillity.NullReplace(rs.getString("ANSWER")));
		    }
		}
	    }
	}
	logger.info("SVT Common Data Check::: " + svtCommonData);
	return svtCommonData;
    }

    public List<Map<String, String>> getSecurityWiseOtherDetails(String pinstid) throws SQLException {
	List<Map<String, String>> securityWiseOtherDataListOfMap = new LinkedList<>();
	logger.info("SVTFIServiceUtility.getSecurityWiseOtherDetails{} ", pinstid);
	try (Connection con = DBConnect.getConnection();PreparedStatement stmt = con.prepareStatement(Queries.GET_SVT_OTHER_DETAILS)) {
		stmt.setString(1, pinstid);
		try (ResultSet rs = stmt.executeQuery()) {
		    while (rs.next()) {
			Map<String, String> securityWiseOtherDetailsMap = new LinkedHashMap<>();
			String secName= OperationUtillity.NullReplace(rs.getString("SECURITY_DETAILS"));
			if(!"Current Assets".equalsIgnoreCase(secName)) {
        			securityWiseOtherDetailsMap.put("SECURITY_TYPE",secName);
        			securityWiseOtherDetailsMap.put("TYPE_SECURITY_CREATION",OperationUtillity.NullReplace(rs.getString("TYPE_SEC_CREATION")));
        			securityWiseOtherDetailsMap.put("SEELING_LIMIT",OperationUtillity.NullReplace(rs.getString("SEELING_LIMIT")));
        			securityWiseOtherDetailsMap.put("REFERANCE_DESCRIPTION",OperationUtillity.NullReplace(rs.getString("REF_DESC")));
        			securityWiseOtherDetailsMap.put("REFERANCE_CODE",OperationUtillity.NullReplace(rs.getString("REF_CODE")));
        			securityWiseOtherDetailsMap.put("DERIVED_VALUE",OperationUtillity.NullReplace(rs.getString("DERIVED_VALUE")));
        			securityWiseOtherDataListOfMap.add(securityWiseOtherDetailsMap);
			}
		    }
	    } catch (Exception e) {
		logger.error("SVTFIServiceUtility.getSecurityWiseOtherDetails()", OperationUtillity.traceException(e));
	    }
	}
	logger.info("securityWiseOtherDataListOfMap check {}",securityWiseOtherDataListOfMap);
	return securityWiseOtherDataListOfMap;
    }


    public List<Map<String, String>> fetchSecurityWiseCityCodeDetails(String pinstid) {

	List<Map<String, String>> securityWiseAddressDetailsListOfMap = new ArrayList<>();
	try (Connection con = DBConnect.getConnection()) {
//			String getParentAndChildStatusQuery = "SELECT DISTINCT ( T1.FINACLE ) AS CITY_CODE, T2.ID, T2.SECURITY_DETAILS   AS SECURITY, T4.STATECODE AS STATE_CODE FROM  LOPS_MS_CITY T1 JOIN CAL_SECURITY_DETAILS_SECURED T2 ON UPPER(T2.CITY) = UPPER(T1.CITY) JOIN LSM_MS_DISTRICT_CODE T3 ON UPPER(T1.CITY) = UPPER(T3.DISTRICT) JOIN LOPS_MS_DISTRICT T4 ON UPPER(T3.DISTRICT) =UPPER(T4.DISTRICT_NAME) WHERE  T2.PINSTID = (SELECT DISHUBID FROM LIMIT_SETUP_EXT WHERE PINSTID = ?) AND UPPER(T2.STATE) = UPPER(T3.STATE) AND UPPER(T2.DISTRICT) = UPPER(T3.DISTRICT)";
	    String getCityAndStateCodeQuery = "SELECT DISTINCT (T1.FINACLE) AS CITY_CODE,T2.ID,T2.SECURITY_DETAILS AS SECURITY,T3.STATECODE AS STATE_CODE FROM LOPS_MS_CITY T1 JOIN CAL_SECURITY_DETAILS_SECURED T2 ON UPPER(T2.CITY) = UPPER(T1.CITY) JOIN LOPS_MS_STATE_CODE T3 ON UPPER(T3.STATE) = UPPER(T2.STATE) WHERE     T2.PINSTID = (SELECT DISHUBID FROM LIMIT_SETUP_EXT WHERE PINSTID = ?)";
	    try (PreparedStatement stmt = con.prepareStatement(getCityAndStateCodeQuery)) {
		stmt.setString(1, pinstid);
		try (ResultSet rs = stmt.executeQuery()) {
		    while (rs.next()) {
			Map<String, String> securityWiseAddressDetailsMap = new HashMap<>();
			securityWiseAddressDetailsMap.put("SECURITY_TYPE",
				OperationUtillity.NullReplace(rs.getString("SECURITY")));
			securityWiseAddressDetailsMap.put("ID", OperationUtillity.NullReplace(rs.getString("ID")));
			securityWiseAddressDetailsMap.put("CITY_CODE",OperationUtillity.NullReplace(rs.getString("CITY_CODE")));
			securityWiseAddressDetailsMap.put("STATE_CODE",
				OperationUtillity.NullReplace(rs.getString("STATE_CODE")));
			securityWiseAddressDetailsListOfMap.add(securityWiseAddressDetailsMap);
		    }
		}
	    } catch (Exception e) {
		logger.error("Exception while getting fetchSecurityWiseAddressDetails--1\n "
			+ OperationUtillity.traceException(e));
	    }
	} catch (Exception e) {
	    logger.error("Exception while executing fetchSecurityWiseAddressDetails--2\n"
		    + OperationUtillity.traceException(e));
	}
	logger.info("City and state code final map-->" + securityWiseAddressDetailsListOfMap);
	return securityWiseAddressDetailsListOfMap;
    }

    // getting collateral class for every security on ownership status
    public String fetchCollateralClass(String owenershipStatus) {

	String collateralClass = "";
	try (Connection con1 = DBConnect.getConnection();
		PreparedStatement pst1 = con1.prepareStatement("SELECT COLLATERAL_CLASS FROM LSM_MS_COLLATERAL_CLASS WHERE UPPER(OWNERSHIP_STATUS) = ?")) {
	    pst1.setString(1, owenershipStatus.toUpperCase());
	    try (ResultSet rs1 = pst1.executeQuery()) {
		while (rs1.next()) {
		    collateralClass = OperationUtillity.NullReplace(rs1.getString("COLLATERAL_CLASS"));
		}
	    }
	} catch (Exception e) {
	    logger.info(OperationUtillity.traceException(e));
	}
	return collateralClass;
    }

    public void saveCollateralID(String pinstid, Map<String, String> svtMap) {
	logger.info("Entering into saveCollateralID()");
	String insertTxnIdQuery = "INSERT INTO LSM_SVT_RESP_DATA (PINSTID,SECURITY_NAME,SECURITY_TYPE,SUB_TYPE_SECURITY,COLLATERAL_ID,DATE_TIME) VALUES (?,?,?,?,?,SYSDATE)";

	try (Connection con = DBConnect.getConnection();
		PreparedStatement pst = con.prepareStatement(insertTxnIdQuery)) {

	    pst.setString(1, pinstid);
	    pst.setString(2, OperationUtillity.NullReplace(svtMap.get("SECURITY_NAME")));
	    pst.setString(3, OperationUtillity.NullReplace(svtMap.get("SECURITY_TYPE")));
	    pst.setString(4, OperationUtillity.NullReplace(svtMap.get("SUB_TYPE_SECURITY_SVT")));
	    pst.setString(5, OperationUtillity.NullReplace(svtMap.get("COLLATERAL_ID")));
	    pst.execute();

	    logger.info(insertTxnIdQuery + " - Query successfully executed");
	} catch (Exception e) {
	    logger.error("Error during insertion of Collateral_Id: " + OperationUtillity.traceException(e));
	}
    }

	public List<Map<String, String>> getFixedDepositDetails(String pinstid, String security) {
		logger.info("Entered into  SVTFIServiceUtility.getFixedDepositDetails()");
		List<Map<String, String>> listOfMapFDAccount = new ArrayList<>();
		String lsmNumber = "";

		String query1 = "";
		String query2 = "";
		try (Connection con = DBConnect.getConnection()) {
			lsmNumber = getLSMNumber(pinstid);

		query1 = "SELECT A.QUESTION_ID,A.SECURITY_PROVIDER_NAME AS SECURITY_NAME, A.ANSWER   AS ACCOUNT_NUMBER, B.ANSWER   AS FD_AMOUNT FROM LSM_SECURITY_ANSWERS  A, LSM_SECURITY_ANSWERS B WHERE   A.PINSTID = B.PINSTID  AND decode (INSTR(A.QUESTION_ID,'_'),0,'0',SUBSTR(A.QUESTION_ID,INSTR(A.QUESTION_ID,'_') ))   = decode (INSTR(A.QUESTION_ID,'_'),0,'0',SUBSTR(B.QUESTION_ID,INSTR(B.QUESTION_ID,'_'))) AND  A.QUESTION = 'FDR NO'  AND A.SECURITY_PROVIDER_NAME =B.SECURITY_PROVIDER_NAME    AND B.QUESTION = 'Amount of FDR in Million'  AND  A.PINSTID = ?  AND A.SECURITY_PROVIDER_NAME=?  AND A.SECURITY_PROVIDER_NAME= B.SECURITY_PROVIDER_NAME order by SECURITY_NAME";
		query2 = "SELECT * FROM (SELECT * FROM (SELECT SECURITY.QUESTION_ID AS SECURITY_QUESTION, SECURITY.ANSWER AS SECURITY_ANSWER, SECURITY.SECURITY_PROVIDER_NAME AS SECURITY_NAME  FROM LSM_SECURITY_ANSWERS SECURITY WHERE SECURITY.PINSTID = ?  AND  SECURITY.QUESTION_ID IN ('79','84','182','186','87')  )  PIVOT ( MAX (SECURITY_ANSWER)  FOR SECURITY_QUESTION  IN ('79' AS SECURITY_VALUATION_DATE,'84' AS SECURITY_DUE_DATE ,'182' AS FINAL_SECURITY_VALUE , '186' AS SECURITY_REVIEW_DATE,'87' AS SECURITY_CREATED ))   ORDER BY SECURITY_NAME) WHERE SECURITY_NAME = ?";
	    PreparedStatement pst = con.prepareStatement(query1);
	    pst.setString(1, pinstid);
	    pst.setString(2, security);
	    try (ResultSet rs = pst.executeQuery()) {
		while (rs.next()) {
		    Map<String, String> map_data = new LinkedHashMap<>();
		    String securityName = OperationUtillity.NullReplace(rs.getString("SECURITY_NAME"));
		    map_data.put("SECURITY_NAME", securityName);
		    map_data.put("FD_ACCOUNT_NUMBER", OperationUtillity.NullReplace(rs.getString("ACCOUNT_NUMBER")));
		    map_data.put("FD_AMOUNT", OperationUtillity.NullReplace(rs.getString("FD_AMOUNT")));

		    try (Connection con1 = DBConnect.getConnection();
			    PreparedStatement pst1 = con1.prepareStatement(query2)) {
			pst1.setString(1, pinstid);
			pst1.setString(2, securityName);
			try (ResultSet rs1 = pst1.executeQuery()) {
			    while (rs1.next()) {
				map_data.put("SECURITY_CREATED", commonUtility.rotateDate(OperationUtillity.NullReplace(rs1.getString("SECURITY_CREATED"))));
				map_data.put("SECURITY_VALUATION_DATE", commonUtility.rotateDate(OperationUtillity.NullReplace(rs1.getString("SECURITY_VALUATION_DATE"))));
				map_data.put("SECURITY_DUE_DATE", commonUtility.rotateDate(OperationUtillity.NullReplace(rs1.getString("SECURITY_DUE_DATE"))));
				map_data.put("FINAL_SECURITY_VALUE",OperationUtillity.NullReplace(rs1.getString("FINAL_SECURITY_VALUE")));
				map_data.put("SECURITY_REVIEW_DATE", commonUtility.rotateDate(OperationUtillity.NullReplace(rs1.getString("SECURITY_REVIEW_DATE"))));
				map_data.put("LSM_NUMBER", lsmNumber);
			    }
			} catch (Exception e) {
			    logger.info(OperationUtillity.traceException(e));
			}
		    } catch (Exception e) {
			logger.info(OperationUtillity.traceException(e));
		    }
		    listOfMapFDAccount.add(map_data);
		}
	    } catch (Exception e) {
		logger.info(OperationUtillity.traceException(e));
	    }
	} catch (Exception e) {
	    logger.info(OperationUtillity.traceException(e));
	}
	logger.info("getFixedDepositDetails -->List of Map check{} " , listOfMapFDAccount);
	return listOfMapFDAccount;
    }

    public String getLSMNumber(String pinstid) {
	String lsmNumber = "";
	try (Connection con = DBConnect.getConnection();PreparedStatement pst = con.prepareStatement(Queries.FETCH_LSM_NUMBER)) {
	    pst.setString(1, pinstid);
	    try (ResultSet rs = pst.executeQuery()) {
		while (rs.next()) {
		    lsmNumber = OperationUtillity.NullReplace(rs.getString("LSM_NO"));
		}
	    } 
	} catch (Exception e) {
	    logger.info("SVTFIServiceUtility.getLSMNumber()",OperationUtillity.traceException(e));
	}
	return lsmNumber;
    }

    public String getUCCBasedCustId(String pinstid) {

	String uccBasedCustId = "";
	try (Connection con = DBConnect.getConnection();
		PreparedStatement pst = con.prepareStatement(
			"SELECT ANSWER AS UCC_BASED_CUST_ID FROM LSM_LIMIT_ANSWERS  WHERE PINSTID = ? AND QUESTION_ID='54'")) {
	    pst.setString(1, pinstid);
	    try (ResultSet rs = pst.executeQuery()) {
		while (rs.next()) {
		    uccBasedCustId = OperationUtillity.NullReplace(rs.getString("UCC_BASED_CUST_ID"));
		}
	    }
	} catch (Exception e) {
	    logger.info(OperationUtillity.traceException(e));
	}
	return uccBasedCustId;
    }

    public Map<String, List<String>> getPropertyOwners(String pinstid) {

	Map<String, List<String>> securitywisePropertyOwnersmap = new LinkedHashMap<>();
	String query = "";
	if (pinstid.contains("MON")) {
	    query = "SELECT SECURITY_PROVIDER_NAME,QUESTION,ANSWER AS NAME_OF_SECURITY_GUARANTOR FROM LSM_SECU_MONTY_ANS WHERE PINSTID =? AND QUESTION = 'Name of Security Provider/Guarantor' ORDER BY SECURITY_PROVIDER_NAME";
	} else {
	    query = "SELECT SECURITY_PROVIDER_NAME , QUESTION , ANSWER AS NAME_OF_SECURITY_GUARANTOR  FROM LSM_SECURITY_ANSWERS   WHERE PINSTID = ? AND QUESTION='Name of Security Provider/Guarantor' ORDER BY SECURITY_PROVIDER_NAME";
	}
	try (Connection con = DBConnect.getConnection(); PreparedStatement pst = con.prepareStatement(query)) {
	    pst.setString(1, pinstid);
	    try (ResultSet rs = pst.executeQuery()) {
		while (rs.next()) {
		    String security = rs.getString("SECURITY_PROVIDER_NAME");
		    String propertyOwner = rs.getString("NAME_OF_SECURITY_GUARANTOR");
		    if (!securitywisePropertyOwnersmap.containsKey(security)) {
			securitywisePropertyOwnersmap.put(security, new ArrayList<String>());
		    }
		    securitywisePropertyOwnersmap.get(security).add(propertyOwner);
		}
	    }
	} catch (Exception e) {
	    logger.info(OperationUtillity.traceException(e));
	}
	logger.info("SVTFIServiceUtility.getPropertyOwners() -->" + securitywisePropertyOwnersmap);
	return securitywisePropertyOwnersmap;
    }

    public boolean getSecurityCreatedOption(String pinstId, String secProvidedName) {
	System.out.println("SVTFIServiceUtility.getSecurityCreatedOption()");
	boolean flag = false;
	try (Connection con = DBConnect.getConnection();
		PreparedStatement pst = con.prepareStatement(
			"SELECT * FROM (SELECT SECURITY.QUESTION_ID AS SECURITY_QUESTION, SECURITY.ANSWER AS SECURITY_ANSWER, SECURITY.SECURITY_PROVIDER_NAME AS SECURITY_NAME  FROM LSM_SECURITY_ANSWERS SECURITY WHERE SECURITY.PINSTID =?  AND SECURITY.SECURITY_PROVIDER_NAME =? AND SECURITY.QUESTION_ID IN ('87','76'))  PIVOT ( MAX (SECURITY_ANSWER)  FOR SECURITY_QUESTION  IN ('87' AS SECURITY_CREATED,'76' AS SECURITY)) ORDER BY SECURITY_NAME")) {
	    pst.setString(1, pinstId);
	    pst.setString(2, secProvidedName);
	    try (ResultSet rs = pst.executeQuery()) {
		while (rs.next()) {
		    if ("Yes".equalsIgnoreCase(OperationUtillity.NullReplace(rs.getString("SECURITY_CREATED")))) {
			flag = true;
		    }
		}
	    }
	} catch (Exception e) {
	    logger.info(OperationUtillity.traceException(e));
	}
	System.out.println("SVTFIServiceUtility.getSecurityCreatedOption()-->" + flag);
	return flag;
    }
		
    public Map<String, List<String>> getPropertyOwner(String pinstid, String securityProvider) {

	Map<String, List<String>> securitywisePropertyOwnersmap = new LinkedHashMap<>();
	List<String> listOfOwners = new ArrayList<>();
	String query = "";
	if (pinstid.contains("MON")) {
	    query = "SELECT SECURITY_PROVIDER_NAME,QUESTION,ANSWER AS NAME_OF_SECURITY_GUARANTOR FROM LSM_SECU_MONTY_ANS WHERE PINSTID =? AND QUESTION = 'Name of Security Provider/Guarantor' AND SECURITY_PROVIDER_NAME = ?";
	} else {
	    query = "SELECT SECURITY_PROVIDER_NAME , QUESTION , ANSWER AS NAME_OF_SECURITY_GUARANTOR  FROM LSM_SECURITY_ANSWERS   WHERE PINSTID = ? AND QUESTION='Name of Security Provider/Guarantor' AND SECURITY_PROVIDER_NAME = ?";
	}
	try (Connection con = DBConnect.getConnection(); PreparedStatement pst = con.prepareStatement(query)) {
	    pst.setString(1, pinstid);
	    pst.setString(2, securityProvider);
	    try (ResultSet rs = pst.executeQuery()) {
		while (rs.next()) {
		    listOfOwners.add(OperationUtillity.NullReplace(rs.getString("NAME_OF_SECURITY_GUARANTOR")));
		}
	    }
	} catch (Exception e) {
	    logger.info(OperationUtillity.traceException(e));
	}
	securitywisePropertyOwnersmap.put(securityProvider, listOfOwners);
	logger.info("SVTFIServiceUtility.getPropertyOwners() for securityprovider-->" + securityProvider + "<-->"
		+ securitywisePropertyOwnersmap);
	return securitywisePropertyOwnersmap;
    }

    public List<Map<String, String>> getCollateralCode(String pinstid, String securityProviderName) {
	List<Map<String, String>> outputList = new LinkedList<>();
	try (Connection con1 = DBConnect.getConnection(); PreparedStatement pst1 = con1.prepareStatement(Queries.GET_SUB_TYPE_SECURITY_SVT)) {
	    pst1.setString(1, pinstid);
	    pst1.setString(2, securityProviderName);
	    pst1.setString(3, "Type Of Security (SVT)");
	    try (ResultSet rs1 = pst1.executeQuery()) {
		while (rs1.next()) {
		    Map<String, String> outputMap = new LinkedHashMap<>();
		    outputMap.put("SECURITY_PROVIDER_NAME", securityProviderName);
		    outputMap.put("SUB_TYPE_SECURITY_SVT",OperationUtillity.NullReplace(rs1.getString("SUB_TYPE_SECURITY_SVT")));
		    try (PreparedStatement pstV = con1.prepareStatement(Queries.GET_COLLATERAL_AMOUNT_SVT)) {
			pstV.setString(1, pinstid);
			pstV.setString(2, securityProviderName);
			pstV.setString(3,"Value of Sub Type Security in Million");
			try (ResultSet rsV = pstV.executeQuery()) {
			    if (rsV.next()) {
				outputMap.put("VALUE_OF_SUB_TYPE_SEC",OperationUtillity.NullReplace(rsV.getString("ANSWER")));
			    }
			}
		    }
		    try (Connection con2 = DBConnect.getConnection();PreparedStatement pst2 = con2.prepareStatement(Queries.GET_COLLATERAL_CODE_SVT)) {
			pst2.setString(1, OperationUtillity.NullReplace(rs1.getString("SUB_TYPE_SECURITY_SVT")));
			try (ResultSet rs2 = pst2.executeQuery()) {
			    while (rs2.next()) {
				outputMap.put("COLLATERAL_CODE",OperationUtillity.NullReplace(rs2.getString("COLLATERAL_CODE")));
				outputList.add(outputMap);
			    }
			}
		    }
		}
	    }
	} catch (Exception e) {
	    logger.info(OperationUtillity.traceException(e));
	}
	logger.info("SVTFIServiceUtility.getCollateralCode().outputMap-->" + outputList);
	return outputList;
    }

    public Map<String, String> getAddressCodes(String pincode) {
	final String query1 = "SELECT CITY_CODE,STATE_CODE FROM TSR_MS_PINCODE WHERE PINCODE = ?";
	Map<String, String> outputMap = new LinkedHashMap<>();
	try (Connection con1 = DBConnect.getConnection(); PreparedStatement pst1 = con1.prepareStatement(query1)) {
	    pst1.setString(1, pincode);
	    try (ResultSet rs1 = pst1.executeQuery()) {
		while (rs1.next()) {
		    outputMap.put("CITY_CODE", OperationUtillity.NullReplace(rs1.getString("CITY_CODE")));
		    outputMap.put("STATE_CODE", OperationUtillity.NullReplace(rs1.getString("STATE_CODE")));
		}
	    }
	} catch (Exception e) {
	    logger.info(OperationUtillity.traceException(e));
	}
	logger.info("SVTFIServiceUtility.getAddressCodes().outputMap{}", outputMap);
	return outputMap;
    }
    public Map<String, String> getSecurityDetails(String security,String pinstId) {
	String query = "";
	Map<String, String> outputMap = new HashMap<>();
	try (Connection con1 = DBConnect.getConnection(); PreparedStatement pst1 = con1.prepareStatement(query)) {
//	    pst1.setString(1, pincode);
	    try (ResultSet rs1 = pst1.executeQuery()) {
		while (rs1.next()) {
		    
		}
	    }
	} catch (Exception e) {
	    logger.info("getSecurityDetails{}",OperationUtillity.traceException(e));
	}
	logger.info("SVTFIServiceUtility.getSecurityDetails(){}", outputMap);
	return outputMap;
    }
}
