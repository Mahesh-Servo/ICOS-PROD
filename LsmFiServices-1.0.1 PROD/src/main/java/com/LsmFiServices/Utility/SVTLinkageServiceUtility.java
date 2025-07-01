
package com.LsmFiServices.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class SVTLinkageServiceUtility {

    private static final Logger logger = LoggerFactory.getLogger(SVTLinkageServiceUtility.class);

    public List<Map<String, String>> getSVTProdWiseList(String pinstid) throws SQLException {
	List<Map<String, String>> securityWiseListOfMap = new ArrayList<>();

	logger.info("Entered into getSVTProdWiseListOfMap():: " + pinstid);
	String securityProvider = "";
	String securityType = "";
	try (Connection con = DBConnect.getConnection();
		PreparedStatement pst = con.prepareStatement(
			"SELECT security_provider_name, question_id, question, answer FROM lsm_security_answers WHERE pinstid =? AND (question_id LIKE '76%' OR question_id LIKE '179%')  ORDER BY security_provider_name, question_id desc")) {
	    ;
	    pst.setString(1, pinstid);

	    try (ResultSet rs = pst.executeQuery()) {
		while (rs.next()) {
		    Map<String, String> securityWiseMap = new LinkedHashMap<>();
		    if (!securityProvider.equals(OperationUtillity.NullReplace(rs.getString("SECURITY_PROVIDER_NAME")))
			    && OperationUtillity.NullReplace(rs.getString("QUESTION")).equalsIgnoreCase("Security")) {
			securityType = OperationUtillity.NullReplace(rs.getString("ANSWER"));
			securityProvider = OperationUtillity.NullReplace(rs.getString("SECURITY_PROVIDER_NAME"));
			logger.info("securityType is-->" + securityType + "  and securityProvider is ---->"
				+ securityProvider);
		    }
		    if (OperationUtillity.NullReplace(rs.getString("QUESTION")).equalsIgnoreCase("Products")) {
			securityWiseMap.put("SECURITY_PROVIDER_NAME",
				OperationUtillity.NullReplace(rs.getString("SECURITY_PROVIDER_NAME")));
			securityWiseMap.put("TYPE_OF_SECURITY", OperationUtillity.NullReplace(securityType));
			securityWiseMap.put("FACILITY_NAME", OperationUtillity.NullReplace(rs.getString("ANSWER")));
			logger.info("Security wise TYPE_OF_SECURITY and FACILITY_NAME------->  " + securityWiseMap);
			securityWiseListOfMap.add(securityWiseMap);
		    }
		}
	    } catch (Exception e) {
		logger.error("Error while getting data in getSVTProdWiseListOfMap(): "
			+ OperationUtillity.traceException(e));
	    }
	}
	logger.info("SVT Security wise final list of map check ====>" + securityWiseListOfMap);
	return securityWiseListOfMap;
    }

    public Map<String, List<String>> securityProductWiseAccountNumber(String pinstid) {

	List<Map<String, String>> svtFacilityWiseListOfMap = null;
	try {
	    svtFacilityWiseListOfMap = getSVTProdWiseList(pinstid);
	} catch (SQLException e1) {
	    logger.info("Exception in getSVTProdWiseList -->\n" + OperationUtillity.traceException(e1));
	}
	Map<String, List<String>> facilityWiseMapOfAccountNumber = new LinkedHashMap<>();

	String queryToGetFacilityWiseAccountNumbers = "SELECT * FROM (SELECT LIMIT.QUESTION_ID, LIMIT.ANSWER, LIMIT.FACILITY_NAME FROM LSM_LIMIT_ANSWERS LIMIT WHERE LIMIT.PINSTID = ?  AND LIMIT.FACILITY_NAME = ? AND LIMIT.QUESTION_ID LIKE '%149%')";

	try (Connection con = DBConnect.getConnection();
		PreparedStatement pst = con.prepareStatement(queryToGetFacilityWiseAccountNumbers)) {

	    for (Map<String, String> securityWiseMap : svtFacilityWiseListOfMap) {
		List<String> accountNumbersList = new ArrayList<>();

		String facilityName = OperationUtillity.NullReplace(securityWiseMap.get("FACILITY_NAME"));
		pst.setString(1, pinstid);
		pst.setString(2, facilityName);
		try (ResultSet rs = pst.executeQuery()) {
		    while (rs.next()) {
			accountNumbersList.add(OperationUtillity.NullReplace(rs.getString("ANSWER")));
		    }
		}
		facilityWiseMapOfAccountNumber.put(facilityName, accountNumbersList);
	    }
	} catch (Exception e) {
	    logger.error("Error while executing the query: " + OperationUtillity.traceException(e));
	}
	logger.info("Facility wise Account NUmbers Check Map --->" + facilityWiseMapOfAccountNumber);
	return facilityWiseMapOfAccountNumber;
    }

    public Map<String, List<String>> securitywiseListOfProducts(String pinstid, String sec) throws Exception {

	Map<String, List<String>> mainMap = new LinkedHashMap<>();
	String queryToGetFacilityWiseAccountNumbers = "";
	logger.info("Sec check in securitywiseListOfProducts() ---> " + sec);

	if (sec.equalsIgnoreCase("Common")) {
	    queryToGetFacilityWiseAccountNumbers = "SELECT  SECURITY_PROVIDER_NAME,  ANSWER AS PRODUCT FROM LSM_SECURITY_ANSWERS SECURITY WHERE SECURITY.PINSTID = ? AND QUESTION ='Products'";
	} else if (!sec.equalsIgnoreCase("Common")) {
	    queryToGetFacilityWiseAccountNumbers = "SELECT  SECURITY_PROVIDER_NAME,  ANSWER AS PRODUCT FROM LSM_SECURITY_ANSWERS SECURITY WHERE SECURITY.PINSTID = ?  AND QUESTION ='Products'  and SECURITY_PROVIDER_NAME='"
		    + sec + "'";
	}
	try (Connection con = DBConnect.getConnection();
		PreparedStatement pst = con.prepareStatement(queryToGetFacilityWiseAccountNumbers)) {

	    pst.setString(1, pinstid);
	    try (ResultSet rs = pst.executeQuery()) {
		while (rs.next()) {
		    // here we are adding security wise all the products
		    String security = rs.getString("SECURITY_PROVIDER_NAME");
		    String product = rs.getString("PRODUCT");
		    if (!mainMap.containsKey(security)) {
			mainMap.put(security, new LinkedList<String>());
		    }
		    mainMap.get(security).add(product);
		}

	    } catch (Exception e) {
		logger.error("Error while executing the securitywiseListOfProducts(): "
			+ OperationUtillity.traceException(e));
	    }
	}
	logger.info("SVTLinkageServiceUtility.securitywiseListOfProducts() Map check -->" + mainMap);
	return mainMap;
    }

    public Map<String, String> productWiseSuffixPrefix(String pinstid, String product) throws Exception {

	Map<String, String> mapOfProductWiseData = new LinkedHashMap<>();
	String queryToGetProductWiseSuffixPrefix = "SELECT * FROM (SELECT LIMIT.QUESTION_ID LIMIT_QUESTION, LIMIT.ANSWER LIMIT_ANSWER, LIMIT.FACILITY_NAME as PRODUCT FROM LSM_LIMIT_ANSWERS LIMIT WHERE LIMIT.PINSTID = ?  and FACILITY_NAME =? AND UPPER(REPLACE(FACILITY_NAME, ' ','_')) != 'PARENT_LIMIT' AND LIMIT.QUESTION_ID IN ('155','156','171')) PIVOT (MAX ( LIMIT_ANSWER ) FOR LIMIT_QUESTION IN ('155' AS LIMIT_PREFIX,'156' AS LIMIT_SUFIX,'171' AS CURRENCY) )";
	try (Connection con = DBConnect.getConnection();
		PreparedStatement pst = con.prepareStatement(queryToGetProductWiseSuffixPrefix)) {
	    pst.setString(1, pinstid);
	    pst.setString(2, product);

	    try (ResultSet rs = pst.executeQuery()) {
		while (rs.next()) {
		    // here we are getting suffix and prefix of each product
		    mapOfProductWiseData.put("PRODUCT", OperationUtillity.NullReplace(rs.getString("PRODUCT")));
		    mapOfProductWiseData.put("LIMIT_PREFIX",
			    OperationUtillity.NullReplace(rs.getString("LIMIT_PREFIX")));
		    mapOfProductWiseData.put("LIMIT_SUFIX", OperationUtillity.NullReplace(rs.getString("LIMIT_SUFIX")));
		    mapOfProductWiseData.put("CURRENCY", OperationUtillity.NullReplace(rs.getString("CURRENCY")));
//					mapOfProductWiseData.put("PRODUCT", OperationUtillity.NullReplace(rs.getString("PRODUCT")));
		    mapOfProductWiseData.put("PRODUCT_PREFIX",
			    OperationUtillity.NullReplace(rs.getString("LIMIT_PREFIX")));
		    mapOfProductWiseData.put("PRODUCT_SUFFIX",
			    OperationUtillity.NullReplace(rs.getString("LIMIT_SUFIX")));
		    mapOfProductWiseData.put("CURRENCY", OperationUtillity.NullReplace(rs.getString("CURRENCY")));

		}
	    } catch (Exception e) {
		logger.error("Error while executing the securitywiseListOfProducts(): "
			+ OperationUtillity.traceException(e));
	    }
	}
	logger.info("SVTLinkageServiceUtility.productWiseSuffixPrefix() check in -->" + mapOfProductWiseData);
	return mapOfProductWiseData;
    }

    public List<Map<String, String>> productwiseSVTLinkageData(String pinstid, String security) throws Exception {

	List<Map<String, String>> list = new LinkedList<>();
	String query = "SELECT A.QUESTION_ID,A.SECURITY_PROVIDER_NAME AS SECURITY, A.ANSWER   AS FACILITY_NAME, B.ANSWER   AS PRODUCT_AMOUNT FROM LSM_SECURITY_ANSWERS  A, LSM_SECURITY_ANSWERS B  WHERE   A.PINSTID = B.PINSTID  AND DECODE (INSTR(A.QUESTION_ID,'_'),0,'0',SUBSTR(A.QUESTION_ID,INSTR(A.QUESTION_ID,'_') ))   = DECODE (INSTR(A.QUESTION_ID,'_'),0,'0',SUBSTR(B.QUESTION_ID,INSTR(B.QUESTION_ID,'_'))) AND  A.QUESTION = 'Products'  AND A.SECURITY_PROVIDER_NAME =B.SECURITY_PROVIDER_NAME AND B.QUESTION = 'Security Value in Million' AND  A.PINSTID = ?  AND A.SECURITY_PROVIDER_NAME =?  ORDER BY SECURITY";
	String query2 = "SELECT * FROM (SELECT LIMIT.QUESTION_ID LIMIT_QUESTION, LIMIT.ANSWER LIMIT_ANSWER FROM LSM_LIMIT_ANSWERS LIMIT WHERE LIMIT.PINSTID = ? AND LIMIT.FACILITY_NAME =? AND LIMIT.QUESTION_ID IN ('155','156')) PIVOT (MAX (LIMIT_ANSWER) FOR LIMIT_QUESTION IN (155 AS PRODUCT_PREFIX, 156 AS PRODUCT_SUFFIX))";
	try (Connection con = DBConnect.getConnection(); PreparedStatement pst = con.prepareStatement(query)) {
	    pst.setString(1, pinstid);
	    pst.setString(2, security);

	    try (ResultSet rs = pst.executeQuery()) {
		while (rs.next()) {
		    Map<String, String> mapOfProductWiseData = new LinkedHashMap<>();

		    mapOfProductWiseData.put("SECURITY_NAME", OperationUtillity.NullReplace(rs.getString("SECURITY")));
		    mapOfProductWiseData.put("PRODUCT", OperationUtillity.NullReplace(rs.getString("FACILITY_NAME")));
		    mapOfProductWiseData.put("PRODUCT_AMOUNT",OperationUtillity.NullReplace(rs.getString("PRODUCT_AMOUNT")));

		    try (Connection con1 = DBConnect.getConnection();
			    PreparedStatement pst1 = con1.prepareStatement(query2)) {
			pst1.setString(1, pinstid);
			pst1.setString(2, rs.getString("FACILITY_NAME"));

			try (ResultSet rs1 = pst1.executeQuery()) {
			    while (rs1.next()) {
				mapOfProductWiseData.put("PRODUCT_SUFFIX",OperationUtillity.NullReplace(rs1.getString("PRODUCT_SUFFIX")));
				mapOfProductWiseData.put("PRODUCT_PREFIX",OperationUtillity.NullReplace(rs1.getString("PRODUCT_PREFIX")));
			    }
			}
		    }
		    list.add(mapOfProductWiseData);
		}
	    } catch (Exception e) {
		logger.error("SVTLinkageServiceUtility.productwiseSVTLinkageData()", OperationUtillity.traceException(e));
	    }
	}
	logger.info("SVTLinkage.ProductWise Data List check in -->" + list);
	return list;
    }

    public Map<String, String> getIndividualMapForSVTLinkage(String pinstId, String security) throws Exception {

//	String security = "SVTLinkageService : Security (3) : Fixed Deposit (Others) : Overdraft, ACCOUNT NUMBER :654738294783 ::: SUB_TYPE_SECURITY - Fixed Deposit/Bank Deposit";
//	String security = SVTLinkageService :Security (5) : Immovable Fixed Assets : Overdraft ::: SUB_TYPE_SECURITY - RESIDENTIAL REAL ESTATE
	Map<String, String> map = new LinkedHashMap<>();
	String securityName = security.split(":")[1].trim();// Security (3)
	String securityType = security.split(":")[2].trim();// Fixed Deposit (Others)
	String faciltyName = security.split(":")[3].trim().split(",")[0].trim();// Overdraft
	String subTypeOfSecurity = security.split(":::")[1].split("-")[1].trim();

	try {
	    map.putAll(productwiseSecurityAmountData(pinstId, securityName, faciltyName.toUpperCase()));
	} catch (Exception e1) {
	    logger.info("productwiseSecurityAmountData()->" + OperationUtillity.traceException(e1));
	}
	map.put("SECURITY_NAME", securityName);
	map.put("SECURITY_TYPE", securityType);
	map.put("FACILITY_NAME", faciltyName);
	map.put("SUB_TYPE_SECURITY_SVT", subTypeOfSecurity);

	String getIndividualApportionedDataForSVTLinkageQuery = "SELECT  ANSWER AS APPORTIONED_VALUE FROM LSM_SECURITY_ANSWERS SECURITY WHERE SECURITY.PINSTID = ? AND SECURITY.QUESTION_ID ='93' and SECURITY.SECURITY_PROVIDER_NAME = ?";
	String getCollateralIDForSVTLinkageQuery = "SELECT COLLATERAL_ID  FROM LSM_SVT_RESP_DATA WHERE PINSTID=? AND SECURITY_NAME =? AND SECURITY_TYPE =? AND SUB_TYPE_SECURITY=? ORDER BY DATE_TIME DESC";

	try (Connection con = DBConnect.getConnection()) {
	    try (PreparedStatement pst = con.prepareStatement(getIndividualApportionedDataForSVTLinkageQuery)) {
		pst.setString(1, pinstId);
		pst.setString(2, securityName);
		try (ResultSet rs = pst.executeQuery()) {
		    if (rs.next()) {
			map.put("APPORTIONED_VALUE", OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
		    }
		}
	    } catch (Exception e) {
		logger.info("Exception while getting apportioned value -->" + OperationUtillity.traceException(e));
	    }

	    try (PreparedStatement pst = con.prepareStatement(getCollateralIDForSVTLinkageQuery)) {
		pst.setString(1, pinstId);
		pst.setString(2, securityName);
		pst.setString(3, securityType);
		pst.setString(4, subTypeOfSecurity);
		try (ResultSet rs = pst.executeQuery()) {
		    if (rs.next()) {
			map.put("COLLATERAL_ID", OperationUtillity.NullReplace(rs.getString("COLLATERAL_ID")));
		    }
		}
	    } catch (Exception e) {
		logger.info("Exception while getting collateral ID -->" + OperationUtillity.traceException(e));
	    }
	    map.putAll(productWiseSuffixPrefix(pinstId, map.get("FACILITY_NAME")));
	} catch (Exception e) {
	    logger.info("Exception main catch -->" + OperationUtillity.traceException(e));
	}

	logger.info("SVTLinkageServiceUtility.getIndividualMapForSVTLinkage() map check -->" + map);
	return map;
    }

    public Map<String, String> productwiseSecurityAmountData(String pinstid, String security, String facility) throws Exception {

	Map<String, String> productwiseSecurityAmountMapData = new LinkedHashMap<>();
//	String query = "SELECT DISTINCT A.ANSWER   AS FACILITY_NAME,A.SECURITY_PROVIDER_NAME AS SECURITY  B.ANSWER   AS PRODUCT_AMOUNT FROM LSM_SECURITY_ANSWERS  A, LSM_SECURITY_ANSWERS B  WHERE   A.PINSTID = B.PINSTID  AND DECODE (INSTR(A.QUESTION_ID,'_'),0,'0',SUBSTR(A.QUESTION_ID,INSTR(A.QUESTION_ID,'_') ))   = DECODE (INSTR(A.QUESTION_ID,'_'),0,'0',SUBSTR(B.QUESTION_ID,INSTR(B.QUESTION_ID,'_'))) AND  A.ANSWER= ? AND A.SECURITY_PROVIDER_NAME =B.SECURITY_PROVIDER_NAME AND B.QUESTION = 'Security Value in Million' AND  A.PINSTID = ? AND A.SECURITY_PROVIDER_NAME =?";

	String query = "SELECT DISTINCT FACILITY_NAME,SECURITY,PRODUCT_AMOUNT from (SELECT A.QUESTION_ID,A.SECURITY_PROVIDER_NAME AS SECURITY, A.ANSWER   AS FACILITY_NAME, B.ANSWER   AS PRODUCT_AMOUNT FROM LSM_SECURITY_ANSWERS  A, LSM_SECURITY_ANSWERS B  WHERE   A.PINSTID = B.PINSTID  AND DECODE (INSTR(A.QUESTION_ID,'_'),0,'0',SUBSTR(A.QUESTION_ID,INSTR(A.QUESTION_ID,'_') ))   = DECODE (INSTR(A.QUESTION_ID,'_'),0,'0',SUBSTR(B.QUESTION_ID,INSTR(B.QUESTION_ID,'_'))) AND  UPPER(A.ANSWER)= ? AND A.SECURITY_PROVIDER_NAME =B.SECURITY_PROVIDER_NAME AND B.QUESTION = 'Security Value in Million' AND  A.PINSTID = ?  AND A.SECURITY_PROVIDER_NAME =?)";

	try (Connection con = DBConnect.getConnection(); PreparedStatement pst = con.prepareStatement(query)) {
	    pst.setString(1, facility.toUpperCase());
	    pst.setString(2, pinstid);
	    pst.setString(3, security);

	    try (ResultSet rs = pst.executeQuery()) {
		while (rs.next()) {
		    productwiseSecurityAmountMapData.put("PRODUCT",OperationUtillity.NullReplace(rs.getString("FACILITY_NAME")));
		    productwiseSecurityAmountMapData.put("PRODUCT_AMOUNT",OperationUtillity.NullReplace(rs.getString("PRODUCT_AMOUNT")));
		}
	    } catch (Exception e) {
		logger.error("SVTLinkageServiceUtility.productwiseSecurityAmountData()", OperationUtillity.traceException(e));
	    }
	}
	logger.info("SVTLinkageServiceUtility.productwiseSecurityAmountData() :: ", productwiseSecurityAmountMapData);
	return productwiseSecurityAmountMapData;
    }

    public Map<String, List<String>> getAccountNumbersForFacility(String pinstId, String securityDetails) {
	Map<String, List<String>> map1 = new LinkedHashMap<>();

	String query1 = "SELECT  DISTINCT ANSWER AS FACILITY  FROM LSM_SECURITY_ANSWERS WHERE PINSTID = ? and SECURITY_PROVIDER_NAME= ?  and  (QUESTION_ID LIKE '%180%') and QUESTION = 'Products'";
	String query2 = "SELECT  ANSWER AS ACCOUNT_NO FROM LSM_LIMIT_ANSWERS WHERE PINSTID = ? and Upper(FACILITY_NAME)=? AND (QUESTION_ID LIKE '149%')";

	try (Connection con1 = DBConnect.getConnection(); PreparedStatement pst1 = con1.prepareStatement(query1)) {
	    pst1.setString(1, pinstId);
	    pst1.setString(2, securityDetails);

	    try (ResultSet rs1 = pst1.executeQuery()) {
		while (rs1.next()) {
		    try (Connection con2 = DBConnect.getConnection();
			    PreparedStatement pst2 = con2.prepareStatement(query2)) {
			pst2.setString(1, pinstId);
			pst2.setString(2, OperationUtillity.NullReplace(rs1.getString("FACILITY").toUpperCase()));
			List<String> listOfAccountNumber = new LinkedList<>();
			try (ResultSet rs2 = pst2.executeQuery()) {
			    while (rs2.next()) {
				listOfAccountNumber.add(OperationUtillity.NullReplace(rs2.getString("ACCOUNT_NO")));
			    }
			    map1.put(rs1.getString("FACILITY").toUpperCase(), listOfAccountNumber);
			}
		    }
		}
	    }
	} catch (Exception e) {
	    logger.error("SVTLinkageServiceUtility.getAccountNumbersForFacility()" , OperationUtillity.traceException(e));
	}
	logger.info("SVTLinkageServiceUtility.getAccountNumbersForFacility() map check :: "+ map1);
	return map1;
    }

    public List<String> getSecurityWiseProducts(String pinstId, String securityProviderName) {
	List<String> products = new ArrayList<>();
	try (Connection con = DBConnect.getConnection();
		PreparedStatement pst = con.prepareStatement(Queries.GET_SECURITYWISE_PRODUCTS)) {
	    pst.setString(1, pinstId);
	    pst.setString(2, securityProviderName);
	    pst.setString(3, "Products");
	    try (ResultSet rs = pst.executeQuery()) {
		while (rs.next()) {
		    products.add(Optional.ofNullable(rs.getString("PRODUCT")).orElse(""));
		}
	    }
	} catch (Exception e) {
	    logger.error("SVTLinkageServiceUtility.getSecuWiseProducts", OperationUtillity.traceException(e));
	}
	return products;
    }
}
