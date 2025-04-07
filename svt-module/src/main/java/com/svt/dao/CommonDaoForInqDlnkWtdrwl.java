package com.svt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.svt.model.commonModel.AddressCodes;
import com.svt.model.commonModel.InnerPojo;
import com.svt.model.commonModel.MainPojo;
import com.svt.utils.common.OperationUtillity;
import com.svt.utils.common.commonUtility;
import com.svt.utils.dataConnectivity.dbConnection;

@Repository
public class CommonDaoForInqDlnkWtdrwl {
	
	@Autowired
	commonUtility common;

	private static final Logger logger = LoggerFactory.getLogger(CommonDaoForInqDlnkWtdrwl.class);

	// Fetch Data for All Security
	public ArrayList<MainPojo> fetchCommonData(String pinstId, String processName) {

		ArrayList<MainPojo> securityDetailsList = new ArrayList<>();

		try (Connection con = dbConnection.getConnection()) {

			Map<String, String[]> limitData = fetchLimitPrefixSuffix(pinstId, con,processName);
			String lsmNumber = getSVTCommonData(pinstId, con).get("LSM_NUMBER");
			String uccBasedCustId = getUCCBasedCustId(pinstId, con);

			String securityDetailsQuery = "";
			String SubTypeSecurityData = "";
//			String queryTofetchPolicyDtls = "";

			switch (processName) {
			case "Monitoring":
				securityDetailsQuery = "SELECT * FROM (SELECT SECURITY.QUESTION_ID AS SECURITY_QUESTION, SECURITY.ANSWER AS SECURITY_ANSWER, SECURITY.SECURITY_PROVIDER_NAME AS SECURITY_NAME,SECURITY.SECURITY_PROVIDER_ID AS SECURITY_PROVIDER_ID FROM LSM_SECU_MONTY_ANS SECURITY WHERE SECURITY.PINSTID = ? AND SECURITY.QUESTION_ID IN ('76','78','79','87','91','92','93','94','186','189','190','191','192','193','194', '195','196','197','84','186','217','218')) PIVOT ( MAX (SECURITY_ANSWER)  FOR SECURITY_QUESTION IN ('76' AS SECURITY,'78' AS OWNERSHIP_STATUS,'79' AS VALUATION_DATE,'87' AS SECURITY_CREATED , '91' AS SECURITY_CREATED_DATE ,'92' AS SECURITY_SUBMISSION_DATE, '93' AS APPORTIONED_VALUE, '94' AS SECURITY_PROVIDER_NAME, '186' AS REVIEW_DATE,'189' AS ADDRESS_LINE,'190' AS AREA,'191' AS ROAD, '192' AS LANDMARK,'193' AS PINCODE,'194' AS DISTRICT , '195' AS CITY,'196' AS STATE,'197' AS COUNTRY , '84' AS SECURITY_DUE_DATE, '186' AS SECURITY_REVIEW_DATE, '217' AS TYPE_SEC_CREATION, '218' AS DERIVED_VALUE )) ORDER BY SECURITY_NAME";
				SubTypeSecurityData = "SELECT * FROM (SELECT SECURITY_PROVIDER_NAME, CASE WHEN INSTR(QUESTION_ID,'_') > 0 THEN TO_NUMBER(SUBSTR(QUESTION_ID,INSTR(QUESTION_ID,'_')+1)) ELSE TO_NUMBER('0') END QUESTION_INDEX,QUESTION, ANSWER from LSM_SECU_MONTY_ANS where PINSTID = ?  AND SECURITY_PROVIDER_NAME= ? and (QUESTION in ('Sub Type Security','Type Of Security (SVT)','Products','Type of charge','Value of Sub Type Security in Million','Security Value in Million','FDR NO','Amount of FDR in Million','Policy Number','Amount of Policy in Million','Mutual Fund Units','Amount of Mutual Fund Units in Million','NAME OF THE HOLDING STOCK','UNIT VALUE','NO. OF UNITS'))) PIVOT (MAX(ANSWER) FOR QUESTION IN ('Sub Type Security' AS \"SUB_TYPE_SEC\",'Type Of Security (SVT)' AS \"TYPE_OF_SEC\",'Products'AS \"PRODUCT\",'Type of charge' AS \"TYPE_OF_CHARGE\",'Value of Sub Type Security in Million' AS \"VAL_OF_SUB_TYPE_SEC_IN_MN\",'Security Value in Million' AS \"SEC_VAL_IN_MN\",'FDR NO' AS \"FDR_NO\",'Amount of FDR in Million' AS \"FDR_AMOUNT\",'Policy Number' AS \"POLICY_NO\", 'Amount of Policy in Million' AS \"POLICY_AMOUNT\",'Mutual Fund Units' as \"MUTUAL_FUND_UNIT\",'Amount of Mutual Fund Units in Million' AS \"MUTUAL_FUND_AMOUNT\",'NAME OF THE HOLDING STOCK'  AS \"NAME_OF_HOLDING_STOCK\",'UNIT VALUE' AS \"UNIT_VALUE\",'NO. OF UNITS' AS \"NO_OF_UNITS\")) ORDER BY QUESTION_INDEX";
//				queryTofetchPolicyDtls = "SELECT listagg(ANSWER,',' ) within group (order by question) AS PREFIX_SUFFIX ,facility_name FROM LSM_SECU_MONTY_ANS where PINSTID = ? and QUESTION in ('Limit Prefix','Limit Suffix' ) group by facility_name ";
				break;
			case "Limit_Setup":
				securityDetailsQuery = "SELECT * FROM (SELECT SECURITY.QUESTION_ID AS SECURITY_QUESTION, SECURITY.ANSWER AS SECURITY_ANSWER, SECURITY.SECURITY_PROVIDER_NAME AS SECURITY_NAME,SECURITY.SECURITY_PROVIDER_ID AS SECURITY_PROVIDER_ID FROM LSM_SECURITY_ANSWERS SECURITY WHERE SECURITY.PINSTID = ? AND SECURITY.QUESTION_ID IN ('76','78','79','87','91','92','93','94','186','189','190','191','192','193','194', '195','196','197','84','186','217','218')) PIVOT ( MAX (SECURITY_ANSWER)  FOR SECURITY_QUESTION IN ('76' AS SECURITY,'78' AS OWNERSHIP_STATUS,'79' AS VALUATION_DATE,'87' AS SECURITY_CREATED , '91' AS SECURITY_CREATED_DATE ,'92' AS SECURITY_SUBMISSION_DATE, '93' AS APPORTIONED_VALUE, '94' AS SECURITY_PROVIDER_NAME, '186' AS REVIEW_DATE,'189' AS ADDRESS_LINE,'190' AS AREA,'191' AS ROAD, '192' AS LANDMARK,'193' AS PINCODE,'194' AS DISTRICT , '195' AS CITY,'196' AS STATE,'197' AS COUNTRY , '84' AS SECURITY_DUE_DATE, '186' AS SECURITY_REVIEW_DATE, '217' AS TYPE_SEC_CREATION, '218' AS DERIVED_VALUE )) ORDER BY SECURITY_NAME";
				SubTypeSecurityData = "SELECT * FROM (SELECT SECURITY_PROVIDER_NAME, CASE WHEN INSTR(QUESTION_ID,'_') > 0 THEN TO_NUMBER(SUBSTR(QUESTION_ID,INSTR(QUESTION_ID,'_')+1)) ELSE TO_NUMBER('0') END QUESTION_INDEX,QUESTION, ANSWER from lsm_security_answers where PINSTID = ?  AND SECURITY_PROVIDER_NAME= ? and (QUESTION in ('Sub Type Security','Type Of Security (SVT)','Products','Type of charge','Value of Sub Type Security in Million','Security Value in Million','FDR NO','Amount of FDR in Million','Policy Number','Amount of Policy in Million','Mutual Fund Units','Amount of Mutual Fund Units in Million','NAME OF THE HOLDING STOCK','UNIT VALUE','NO. OF UNITS'))) PIVOT (MAX(ANSWER) FOR QUESTION IN ('Sub Type Security' AS \"SUB_TYPE_SEC\",'Type Of Security (SVT)' AS \"TYPE_OF_SEC\",'Products'AS \"PRODUCT\",'Type of charge' AS \"TYPE_OF_CHARGE\",'Value of Sub Type Security in Million' AS \"VAL_OF_SUB_TYPE_SEC_IN_MN\",'Security Value in Million' AS \"SEC_VAL_IN_MN\",'FDR NO' AS \"FDR_NO\",'Amount of FDR in Million' AS \"FDR_AMOUNT\",'Policy Number' AS \"POLICY_NO\", 'Amount of Policy in Million' AS \"POLICY_AMOUNT\",'Mutual Fund Units' as \"MUTUAL_FUND_UNIT\",'Amount of Mutual Fund Units in Million' AS \"MUTUAL_FUND_AMOUNT\",'NAME OF THE HOLDING STOCK'  AS \"NAME_OF_HOLDING_STOCK\",'UNIT VALUE' AS \"UNIT_VALUE\",'NO. OF UNITS' AS \"NO_OF_UNITS\")) ORDER BY QUESTION_INDEX";
//				queryTofetchPolicyDtls = "SELECT listagg(ANSWER,',' ) within group (order by question) AS PREFIX_SUFFIX ,facility_name FROM LSM_LIMIT_ANSWERS where PINSTID = ? and QUESTION in ('Limit Prefix','Limit Suffix' ) group by facility_name ";
				break;
			}

			PreparedStatement pst = con.prepareStatement(securityDetailsQuery);
			pst.setString(1, pinstId);

			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {

					MainPojo svtMainReqPj = new MainPojo();
                 
					svtMainReqPj.setSecurityId(rs.getString("SECURITY_PROVIDER_ID"));
					svtMainReqPj.setSecurityName(rs.getString("SECURITY_NAME"));
					svtMainReqPj.setSecurityCreated(rs.getString("SECURITY_CREATED"));
                    String pincode = Optional.ofNullable(rs.getString("PINCODE")).orElse("");
                    String securityName = Optional.ofNullable(rs.getString("SECURITY_NAME")).orElse("");
					// lodge collateral req data
					svtMainReqPj.setCeiling_Limit(OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
					String ownershipStatus = OperationUtillity.NullReplace(rs.getString("OWNERSHIP_STATUS"));
					svtMainReqPj.setCollateral_Class(fetchCollateralClass(ownershipStatus.toUpperCase(), con));
					svtMainReqPj.setGross_Val(OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
					svtMainReqPj.setDue_Dt(OperationUtillity
							.customDateFormat(OperationUtillity.NullReplace(rs.getString("SECURITY_CREATED_DATE"))));
					svtMainReqPj.setSecurity_Created(OperationUtillity.NullReplace(rs.getString("SECURITY_CREATED")));
					svtMainReqPj.setLast_Val_Date(OperationUtillity
							.customDateFormat(OperationUtillity.NullReplace(rs.getString("VALUATION_DATE"))));
//						svtMainReqPj.setNatureOfCharge(OperationUtillity.NullReplace(rs.getString("NatureOfCharge")));
					svtMainReqPj.setNotes(OperationUtillity.NullReplace(lsmNumber));
					svtMainReqPj.setNotes1(OperationUtillity.NullReplace(lsmNumber));
					svtMainReqPj.setNotes2(OperationUtillity.NullReplace(lsmNumber));
					svtMainReqPj.setReview_Dt(OperationUtillity
							.customDateFormat(OperationUtillity.NullReplace(rs.getString("REVIEW_DATE"))));
					svtMainReqPj.setReceive_Dt(OperationUtillity
							.customDateFormat(OperationUtillity.NullReplace(rs.getString("REVIEW_DATE"))));
					svtMainReqPj.setFromDeriveVal(OperationUtillity.NullReplace(rs.getString("DERIVED_VALUE")));
					svtMainReqPj.setValue(OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
					svtMainReqPj.setCollateral_Value(OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
					svtMainReqPj.setSecurity_Id(OperationUtillity.NullReplace(rs.getString("SECURITY_PROVIDER_ID")));
					svtMainReqPj.setSecurity_Name(OperationUtillity.NullReplace(rs.getString("SECURITY_NAME")));
					svtMainReqPj.setSecurity_Type(OperationUtillity.NullReplace(rs.getString("SECURITY")));
					svtMainReqPj.setUcc_Based_CustId(OperationUtillity.NullReplace(uccBasedCustId));
					svtMainReqPj.setFromDeriveVal(getFromDeriveFromMs(
							Optional.ofNullable(rs.getString("DERIVED_VALUE")).orElse(""), con));
					svtMainReqPj.setTypeOfSecurityCreation(Optional.ofNullable(rs.getString("TYPE_SEC_CREATION")).orElse(""));
					svtMainReqPj.setAddressLine1(Optional.ofNullable(rs.getString("ADDRESS_LINE")).orElse(""));
					svtMainReqPj.setRoad(Optional.ofNullable(rs.getString("ROAD")).orElse(""));
					svtMainReqPj.setArea(Optional.ofNullable(rs.getString("AREA")).orElse(""));
					svtMainReqPj.setLandmark(Optional.ofNullable(rs.getString("LANDMARK")).orElse(""));
					svtMainReqPj.setPincode(Optional.ofNullable(rs.getString("PINCODE")).orElse(""));
					svtMainReqPj.setCity(Optional.ofNullable(rs.getString("CITY")).orElse(""));
					svtMainReqPj.setDistrict(Optional.ofNullable(rs.getString("DISTRICT")).orElse(""));
					svtMainReqPj.setState(Optional.ofNullable(rs.getString("STATE")).orElse(""));
					svtMainReqPj.setCountry(Optional.ofNullable(rs.getString("COUNTRY")).orElse(""));
					svtMainReqPj.setAddressCode(getAddressCode(pincode));
					svtMainReqPj.setPropertyowner(getPropertyOwner(pinstId, securityName).get(securityName).toString().replaceAll("\\[|\\]", ""));
					svtMainReqPj.setLsmNumber(getLSMNumber(pinstId));   //LSM_MS_ANSWERS table
					// list of products
					svtMainReqPj.setInnerPojo(
							fectListOfProducts(pinstId, svtMainReqPj, SubTypeSecurityData, limitData, con));


					securityDetailsList.add(svtMainReqPj);
				}
			}
			if (pst != null) {
				pst = null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.info("CommonDaoForInqDlnkWtdrwl.fetchwithdrawal()" + OperationUtillity.traceException(pinstId, ex));
		}
		logger.info("\nCommonDaoForInqDlnkWtdrwl.fetchwithdrawal().pinstid[" + pinstId + "]\n" + securityDetailsList);

		return securityDetailsList;
	}

	public static List<InnerPojo> fectListOfProducts(String pinstId, MainPojo svtMainReqPj, String SubTypeSecurityData,
			Map<String, String[]> limitData, Connection con) {

		List<InnerPojo> svtSecurityDtlsList = new ArrayList<InnerPojo>();

		try (PreparedStatement pstSvt = con.prepareStatement(SubTypeSecurityData);) {
			pstSvt.setString(1, pinstId);
			pstSvt.setString(2, svtMainReqPj.getSecurityName());
			try (ResultSet rsSvt = pstSvt.executeQuery()) {

				while (rsSvt.next()) {
					InnerPojo innerPojo = new InnerPojo();

					innerPojo.setSubTypeSecurity(OperationUtillity.NullReplace(rsSvt.getString("SUB_TYPE_SEC")));
					innerPojo.setTypeOfSecurity(OperationUtillity.NullReplace(rsSvt.getString("TYPE_OF_SEC")));
					String holdingStack = getHolding(OperationUtillity.NullReplace(rsSvt.getString("NAME_OF_HOLDING_STOCK")));
					innerPojo.setNameofHoldingStock(holdingStack);
					innerPojo.setProduct(OperationUtillity.NullReplace(rsSvt.getString("PRODUCT")));
					innerPojo.setValSubTypeSecMn(
							OperationUtillity.NullReplace(rsSvt.getString("VAL_OF_SUB_TYPE_SEC_IN_MN")));
					innerPojo.setTypeOfCharge(OperationUtillity.NullReplace(rsSvt.getString("TYPE_OF_CHARGE")));
					innerPojo.setSecurityValueMn(OperationUtillity.NullReplace(rsSvt.getString("SEC_VAL_IN_MN")));

					if (OperationUtillity.NullReplace(rsSvt.getString("POLICY_NO")) != ""
							|| !OperationUtillity.NullReplace(rsSvt.getString("POLICY_NO")).equals("")) {
						innerPojo.setPolicyNumber(OperationUtillity.NullReplace(rsSvt.getString("POLICY_NO")));
						innerPojo.setPolicyAmount(OperationUtillity.NullReplace(rsSvt.getString("POLICY_AMOUNT")));
					} else if (OperationUtillity.NullReplace(rsSvt.getString("FDR_NO")) != ""
							|| !OperationUtillity.NullReplace(rsSvt.getString("FDR_NO")).equals("")) {
						innerPojo.setPolicyNumber(OperationUtillity.NullReplace(rsSvt.getString("FDR_NO")));
						innerPojo.setPolicyAmount(OperationUtillity.NullReplace(rsSvt.getString("FDR_AMOUNT")));
					} else if (OperationUtillity.NullReplace(rsSvt.getString("MUTUAL_FUND_UNIT")) != ""
							|| !OperationUtillity.NullReplace(rsSvt.getString("MUTUAL_FUND_UNIT")).equals("")) {
						innerPojo.setPolicyNumber(OperationUtillity.NullReplace(rsSvt.getString("MUTUAL_FUND_UNIT")));
						innerPojo.setPolicyAmount(OperationUtillity.NullReplace(rsSvt.getString("MUTUAL_FUND_AMOUNT")));
					}
					
					innerPojo.setUnitValue(OperationUtillity.NullReplace(rsSvt.getString("UNIT_VALUE")));
					innerPojo.setNoOfUnits(OperationUtillity.NullReplace(rsSvt.getString("NO_OF_UNITS")));
					
					logger.info(
							"\nCommonDaoForInqDlnkWtdrwl.fectListOfProducts().Before IF--->");
					logger.info(
							"\nCommonDaoForInqDlnkWtdrwl.fectListOfProducts().limitData--->"+limitData);
					logger.info(
							"\nCommonDaoForInqDlnkWtdrwl.fectListOfProducts().innerPojo.getProduct()--->"+innerPojo.getProduct());
					logger.info(
							"\nCommonDaoForInqDlnkWtdrwl.fectListOfProducts().limitData.get(innerPojo.getProduct())--->"+limitData.get(innerPojo.getProduct()));
					int len = limitData.get(innerPojo.getProduct()) != null ? limitData.get(innerPojo.getProduct()).length : 0;
					
					if (limitData.containsKey(innerPojo.getProduct()) &&  len == 2) {
						logger.info(
								"\nCommonDaoForInqDlnkWtdrwl.fectListOfProducts().Inside  IF--->");
						innerPojo.setLimitPrefix(
								Optional.ofNullable(limitData.get(innerPojo.getProduct())[0]).orElse(""));
						innerPojo.setLimitSuffix(
								Optional.ofNullable(limitData.get(innerPojo.getProduct())[1]).orElse(""));
					}
					svtMainReqPj.setTypeOfCharge(innerPojo.getTypeOfCharge());
					svtSecurityDtlsList.add(innerPojo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(
					"\nCommonDaoForInqDlnkWtdrwl.fectListOfProducts()" + OperationUtillity.traceException(pinstId, e));
		}
		return svtSecurityDtlsList;
	}

	// To fecth Data for Individual Security on the basis of product Starts here
	public MainPojo fetchIndividualProductDtls(String pinstid, String securityName, String subTypeSecurity,
			String typeOfSvt, String product, String processName) {

		MainPojo svtMainReqPj = new MainPojo();

		try (Connection con = dbConnection.getConnection()) {
			Map<String, String[]> limitData = fetchLimitPrefixSuffix(pinstid, con,processName);
			String lsmNumber = getSVTCommonData(pinstid, con).get("LSM_NUMBER");
			String uccBasedCustId = getUCCBasedCustId(pinstid, con);

			String securityDetailsQuery = "";
			String SubTypeSecurityData = "";

			switch (processName) {

			case "Monitoring":
				securityDetailsQuery = "SELECT * FROM (SELECT SECURITY.QUESTION_ID AS SECURITY_QUESTION, SECURITY.ANSWER AS SECURITY_ANSWER, SECURITY.SECURITY_PROVIDER_NAME AS SECURITY_NAME,SECURITY.SECURITY_PROVIDER_ID AS SECURITY_PROVIDER_ID FROM LSM_SECU_MONTY_ANS SECURITY WHERE SECURITY.PINSTID = ? AND SECURITY.SECURITY_PROVIDER_NAME = ?  AND SECURITY.QUESTION_ID IN ('76','78','79','87','91','92','93','94','186','189','190','191','192','193','194','195','196','197','84','186','217','218'))  PIVOT ( MAX (SECURITY_ANSWER)  FOR SECURITY_QUESTION  IN ('76' AS SECURITY,'78' AS OWNERSHIP_STATUS,'79' AS VALUATION_DATE,'87' AS SECURITY_CREATED ,'91' AS SECURITY_CREATED_DATE,'92' AS SECURITY_SUBMISSION_DATE, '93' AS APPORTIONED_VALUE, '94' AS SECURITY_PROVIDER_NAME, '186' AS REVIEW_DATE,'189' AS ADDRESS_LINE,'190' AS AREA,'191' AS ROAD, '192' AS LANDMARK,'193' AS PINCODE,'194' AS DISTRICT ,'195' AS CITY,'196' AS STATE,'197' AS COUNTRY , '84' AS SECURITY_DUE_DATE, '186' AS SECURITY_REVIEW_DATE, '217' AS TYPE_SEC_CREATION, '218' AS DERIVED_VALUE  )) ORDER BY SECURITY_NAME";
				SubTypeSecurityData = "SELECT * FROM (SELECT SECURITY_PROVIDER_NAME, CASE WHEN INSTR(QUESTION_ID,'_') > 0 THEN TO_NUMBER(SUBSTR(QUESTION_ID,INSTR(QUESTION_ID,'_')+1)) ELSE TO_NUMBER('0') END QUESTION_INDEX,QUESTION, ANSWER from LSM_SECU_MONTY_ANS where PINSTID = ?  AND SECURITY_PROVIDER_NAME= ? and (QUESTION in ('Sub Type Security','Type Of Security (SVT)','Products','Type of charge','Value of Sub Type Security in Million','Security Value in Million','FDR NO','Amount of FDR in Million','Policy Number','Amount of Policy in Million','Mutual Fund Units','Amount of Mutual Fund Units in Million','NAME OF THE HOLDING STOCK'))) PIVOT (MAX(ANSWER) FOR QUESTION IN ('Sub Type Security' AS \"SUB_TYPE_SEC\",'Type Of Security (SVT)' AS \"TYPE_OF_SEC\",'Products'AS \"PRODUCT\",'Type of charge' AS \"TYPE_OF_CHARGE\",'Value of Sub Type Security in Million' AS \"VAL_OF_SUB_TYPE_SEC_IN_MN\",'Security Value in Million' AS \"SEC_VAL_IN_MN\",'FDR NO' AS \"FDR_NO\",'Amount of FDR in Million' AS \"FDR_AMOUNT\",'Policy Number' AS \"POLICY_NO\", 'Amount of Policy in Million' AS \"POLICY_AMOUNT\",'Mutual Fund Units' as \"MUTUAL_FUND_UNIT\",'Amount of Mutual Fund Units in Million' AS \"MUTUAL_FUND_AMOUNT\",'NAME OF THE HOLDING STOCK'  AS \"NAME_OF_HOLDING_STOCK\")) ORDER BY QUESTION_INDEX";
				break;

			case "Limit_Setup":
				securityDetailsQuery = "SELECT * FROM (SELECT SECURITY.QUESTION_ID AS SECURITY_QUESTION, SECURITY.ANSWER AS SECURITY_ANSWER, SECURITY.SECURITY_PROVIDER_NAME AS SECURITY_NAME,SECURITY.SECURITY_PROVIDER_ID AS SECURITY_PROVIDER_ID FROM LSM_SECURITY_ANSWERS SECURITY WHERE SECURITY.PINSTID = ? AND SECURITY.SECURITY_PROVIDER_NAME = ?  AND SECURITY.QUESTION_ID IN ('76','78','79','87','91','92','93','94','186','189','190','191','192','193','194','195','196','197','84','186','217','218'))  PIVOT ( MAX (SECURITY_ANSWER)  FOR SECURITY_QUESTION  IN ('76' AS SECURITY,'78' AS OWNERSHIP_STATUS,'79' AS VALUATION_DATE,'87' AS SECURITY_CREATED ,'91' AS SECURITY_CREATED_DATE,'92' AS SECURITY_SUBMISSION_DATE, '93' AS APPORTIONED_VALUE, '94' AS SECURITY_PROVIDER_NAME, '186' AS REVIEW_DATE,'189' AS ADDRESS_LINE,'190' AS AREA,'191' AS ROAD, '192' AS LANDMARK,'193' AS PINCODE,'194' AS DISTRICT ,'195' AS CITY,'196' AS STATE,'197' AS COUNTRY , '84' AS SECURITY_DUE_DATE, '186' AS SECURITY_REVIEW_DATE, '217' AS TYPE_SEC_CREATION, '218' AS DERIVED_VALUE  )) ORDER BY SECURITY_NAME";
				SubTypeSecurityData = "SELECT * FROM (SELECT SECURITY_PROVIDER_NAME, CASE WHEN INSTR(QUESTION_ID,'_') > 0 THEN TO_NUMBER(SUBSTR(QUESTION_ID,INSTR(QUESTION_ID,'_')+1)) ELSE TO_NUMBER('0') END QUESTION_INDEX,QUESTION, ANSWER from lsm_security_answers where PINSTID = ?  AND SECURITY_PROVIDER_NAME= ? and (QUESTION in ('Sub Type Security','Type Of Security (SVT)','Products','Type of charge','Value of Sub Type Security in Million','Security Value in Million','FDR NO','Amount of FDR in Million','Policy Number','Amount of Policy in Million','Mutual Fund Units','Amount of Mutual Fund Units in Million','NAME OF THE HOLDING STOCK'))) PIVOT (MAX(ANSWER) FOR QUESTION IN ('Sub Type Security' AS \"SUB_TYPE_SEC\",'Type Of Security (SVT)' AS \"TYPE_OF_SEC\",'Products'AS \"PRODUCT\",'Type of charge' AS \"TYPE_OF_CHARGE\",'Value of Sub Type Security in Million' AS \"VAL_OF_SUB_TYPE_SEC_IN_MN\",'Security Value in Million' AS \"SEC_VAL_IN_MN\",'FDR NO' AS \"FDR_NO\",'Amount of FDR in Million' AS \"FDR_AMOUNT\",'Policy Number' AS \"POLICY_NO\", 'Amount of Policy in Million' AS \"POLICY_AMOUNT\",'Mutual Fund Units' as \"MUTUAL_FUND_UNIT\",'Amount of Mutual Fund Units in Million' AS \"MUTUAL_FUND_AMOUNT\",'NAME OF THE HOLDING STOCK'  AS \"NAME_OF_HOLDING_STOCK\")) ORDER BY QUESTION_INDEX";
				break;
			}

			PreparedStatement pst = con.prepareStatement(securityDetailsQuery);
			pst.setString(1, pinstid);
			pst.setString(2, securityName);

			try (ResultSet rs = pst.executeQuery()) {

				while (rs.next()) {

					svtMainReqPj.setSecurityId(rs.getString("SECURITY_PROVIDER_ID"));
					svtMainReqPj.setSecurityName(rs.getString("SECURITY_NAME"));
					svtMainReqPj.setSecurityCreated(rs.getString("SECURITY_CREATED"));
					// lodge collateral req data
					svtMainReqPj.setCeiling_Limit(OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
					String ownershipStatus = OperationUtillity.NullReplace(rs.getString("OWNERSHIP_STATUS"));
					svtMainReqPj.setCollateral_Class(fetchCollateralClass(ownershipStatus.toUpperCase(), con));
					svtMainReqPj.setGross_Val(OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
					svtMainReqPj.setDue_Dt(OperationUtillity
							.customDateFormat(OperationUtillity.NullReplace(rs.getString("SECURITY_CREATED_DATE"))));
					svtMainReqPj.setSecurity_Created(OperationUtillity.NullReplace(rs.getString("SECURITY_CREATED")));
					svtMainReqPj.setLast_Val_Date(OperationUtillity
							.customDateFormat(OperationUtillity.NullReplace(rs.getString("VALUATION_DATE"))));
//						svtMainReqPj.setNatureOfCharge(OperationUtillity.NullReplace(rs.getString("NatureOfCharge")));
					svtMainReqPj.setNotes(OperationUtillity.NullReplace(lsmNumber));
					svtMainReqPj.setNotes1(OperationUtillity.NullReplace(lsmNumber));
					svtMainReqPj.setNotes2(OperationUtillity.NullReplace(lsmNumber));
					svtMainReqPj.setReview_Dt(OperationUtillity
							.customDateFormat(OperationUtillity.NullReplace(rs.getString("REVIEW_DATE"))));
					svtMainReqPj.setReceive_Dt(OperationUtillity
							.customDateFormat(OperationUtillity.NullReplace(rs.getString("REVIEW_DATE"))));
//						svtMainReqPj.setFromDeriveVal(OperationUtillity.NullReplace(rs.getString("FromDeriveVal")));
					svtMainReqPj.setValue(OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
					svtMainReqPj.setCollateral_Value(OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
					svtMainReqPj.setSecurity_Id(OperationUtillity.NullReplace(rs.getString("SECURITY_PROVIDER_ID")));
					svtMainReqPj.setSecurity_Name(OperationUtillity.NullReplace(rs.getString("SECURITY_NAME")));
					svtMainReqPj.setSecurity_Type(OperationUtillity.NullReplace(rs.getString("SECURITY")));
					svtMainReqPj.setUcc_Based_CustId(OperationUtillity.NullReplace(uccBasedCustId));
					svtMainReqPj.setFromDeriveVal(Optional.ofNullable(rs.getString("DERIVED_VALUE")).orElse(""));
					svtMainReqPj.setTypeOfSecurityCreation(
							Optional.ofNullable(rs.getString("TYPE_SEC_CREATION")).orElse(""));

					// list of products
					svtMainReqPj.setInnerPojo(fectListOfProducts(pinstid, svtMainReqPj, SubTypeSecurityData, limitData,
							subTypeSecurity, typeOfSvt, product, con));

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("CommonDaoForInqDlnkWtdrwl.fetchIndividualProductDtls()"
					+ OperationUtillity.traceException(pinstid, e));
		}
		return svtMainReqPj;
	}
	// Ends

	// Methods for Data fetching
	public static List<InnerPojo> fectListOfProducts(String pinstId, MainPojo svtMainReqPj, String SubTypeSecurityData,
			Map<String, String[]> limitData, String subTypeSecurity, String typeOfSvt, String product, Connection con) {

		logger.info("inner pojo query-->"+SubTypeSecurityData);
		logger.info(" svtMainReqPj.getSecurityName()"+ svtMainReqPj.getSecurityName());

		List<InnerPojo> svtSecurityDtlsList = new ArrayList<InnerPojo>();

		try (PreparedStatement pstSvt = con.prepareStatement(SubTypeSecurityData);) {
			pstSvt.setString(1, pinstId);
			pstSvt.setString(2, svtMainReqPj.getSecurityName());
			try (ResultSet rsSvt = pstSvt.executeQuery()) {

				while (rsSvt.next()) {
					InnerPojo innerPojo = new InnerPojo();

					if (OperationUtillity.NullReplace(rsSvt.getString("SUB_TYPE_SEC")).equals(subTypeSecurity)
							&& OperationUtillity.NullReplace(rsSvt.getString("TYPE_OF_SEC")).equals(typeOfSvt)
							&& OperationUtillity.NullReplace(rsSvt.getString("PRODUCT")).equals(product)) {
						
						logger.info("inside iff of inner pojo-->");
						String holdingStack = getHolding(OperationUtillity.NullReplace(rsSvt.getString("NAME_OF_HOLDING_STOCK")));
						innerPojo.setNameofHoldingStock(holdingStack);
						innerPojo.setSubTypeSecurity(OperationUtillity.NullReplace(rsSvt.getString("SUB_TYPE_SEC")));
						innerPojo.setTypeOfSecurity(OperationUtillity.NullReplace(rsSvt.getString("TYPE_OF_SEC")));
						innerPojo.setProduct(OperationUtillity.NullReplace(rsSvt.getString("PRODUCT")));
						innerPojo.setValSubTypeSecMn(
								OperationUtillity.NullReplace(rsSvt.getString("VAL_OF_SUB_TYPE_SEC_IN_MN")));
						
						innerPojo.setTypeOfCharge(OperationUtillity.NullReplace(rsSvt.getString("TYPE_OF_CHARGE")));
						innerPojo.setSecurityValueMn(OperationUtillity.NullReplace(rsSvt.getString("SEC_VAL_IN_MN")));

						if (rsSvt.getString("POLICY_NO") != null) {
							innerPojo.setPolicyNumber(OperationUtillity.NullReplace(rsSvt.getString("POLICY_NO")));
							innerPojo.setPolicyAmount(OperationUtillity.NullReplace(rsSvt.getString("POLICY_AMOUNT")));
						} else if (rsSvt.getString("FDR_NO") != null) {
							innerPojo.setPolicyNumber(OperationUtillity.NullReplace(rsSvt.getString("FDR_NO")));
							innerPojo.setPolicyAmount(OperationUtillity.NullReplace(rsSvt.getString("FDR_AMOUNT")));
						} else if (rsSvt.getString("MUTUAL_FUND_UNIT") != null) {
							innerPojo.setPolicyNumber(
									OperationUtillity.NullReplace(rsSvt.getString("MUTUAL_FUND_UNIT")));
							innerPojo.setPolicyAmount(
									OperationUtillity.NullReplace(rsSvt.getString("MUTUAL_FUND_AMOUNT")));
						}
//						if (Optional.ofNullable(limitData.get(innerPojo.getProduct()).length).orElse(0) == 2) {
						if (limitData.containsKey(innerPojo.getProduct())
								&& limitData.get(innerPojo.getProduct()).length == 2) {
							innerPojo.setLimitPrefix(
									Optional.ofNullable(limitData.get(innerPojo.getProduct())[0]).orElse(""));
							innerPojo.setLimitSuffix(
									Optional.ofNullable(limitData.get(innerPojo.getProduct())[1]).orElse(""));
						}

						svtSecurityDtlsList.add(innerPojo);
						logger.info("svtSecurityDtlsList from inner pojo-->"+svtSecurityDtlsList);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(
					"CommonDaoForInqDlnkWtdrwl.fectListOfProducts()" + OperationUtillity.traceException(pinstId, e));
		}
		return svtSecurityDtlsList;
	}

	@SuppressWarnings("unchecked")
	public Boolean checkRenewal(String pinstid, String processName) {
		Boolean flag = false;
		try (Connection con = dbConnection.getConnection()) {
			String checkRenewalQuery = "";
			switch (processName) {
			case "Monitoring":
				checkRenewalQuery = "SELECT PROPOSALTYPE FROM ICOS_MONT_EXT WHERE PINSTID = ? AND PROPOSALTYPE LIKE '%Renewal%'";
				break;

			case "Limit_Setup":
				checkRenewalQuery = "SELECT PROPOSALTYPE FROM LIMIT_SETUP_EXT WHERE PINSTID = ? AND PROPOSALTYPE LIKE '%Renewal%'";
				break;
			}

			PreparedStatement pst = con.prepareStatement(checkRenewalQuery);
			pst.setString(1, pinstid);

			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					flag = true;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				logger.info("CommonDaoForInqDlnkWtdrwl.checkRenewal()" + OperationUtillity.traceException(pinstid, ex));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.info("CommonDaoForInqDlnkWtdrwl.checkRenewal()" + OperationUtillity.traceException(pinstid, ex));
		}
		return flag;
	}

	public static Map<String, String[]> fetchLimitPrefixSuffix(String pinstid, Connection con,String processName) {
		
		String pinstidnew = "";
		if("Monitoring".equalsIgnoreCase(processName)) {
			pinstidnew =pinstid.replace("MON","LSM");
		} else if("Limit_Setup".equalsIgnoreCase(processName)){  
			pinstidnew =pinstid;
		}

		Map<String, String[]> limitData = new HashMap<String, String[]>();
		try (PreparedStatement pstmt = con.prepareStatement(
				"SELECT listagg(ANSWER,',' ) within group (order by question) AS PREFIX_SUFFIX ,FACILITY_NAME"
						+ " FROM LSM_LIMIT_ANSWERS where PINSTID = ? "
						+ "and QUESTION in ('Limit Prefix','Limit Suffix' ) group by facility_name ");) {
			pstmt.setString(1, pinstidnew);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {

					String[] prfxSffx = null;
					if (OperationUtillity.NullReplace(rs.getString("PREFIX_SUFFIX")) != "" && OperationUtillity.NullReplace(rs.getString("PREFIX_SUFFIX")).contains(",")) {
						prfxSffx = OperationUtillity.NullReplace(rs.getString("PREFIX_SUFFIX")).split(",");
						limitData.put(OperationUtillity.NullReplace(rs.getString("FACILITY_NAME")), prfxSffx);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("\nCommonDaoForInqDlnkWtdrwl.fetchLimitPrefixSuffix()"
					+ OperationUtillity.traceException(pinstid, e));
		}
		return limitData;
	}

	public Map<String, String> getSVTCommonData(String pinstid, Connection con) throws SQLException, Exception {
		Map<String, String> svtCommonData = new LinkedHashMap<>();

		if (pinstid.contains("MON")) {
			pinstid = pinstid.replace("MON", "LSM");
		}

		try {
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
		} catch (Exception e) {
			logger.info(OperationUtillity.traceException(pinstid, e));
		}
		return svtCommonData;
	}

	public String getUCCBasedCustId(String pinstid, Connection con) {

		String uccBasedCustId = "";
		if (pinstid.contains("MON")) {
			pinstid = pinstid.replace("MON", "LSM");
		}

		try (PreparedStatement pst = con.prepareStatement(
				"SELECT ANSWER AS UCC_BASED_CUST_ID FROM LSM_LIMIT_ANSWERS  WHERE PINSTID = ? AND QUESTION_ID='54'")) {
			pst.setString(1, pinstid);
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					uccBasedCustId = OperationUtillity.NullReplace(rs.getString("UCC_BASED_CUST_ID"));
				}
			}
		} catch (Exception e) {
			logger.info(OperationUtillity.traceException(pinstid, e));
		}
		return uccBasedCustId;
	}

	public String fetchCollateralClass(String owenershipStatus, Connection con) {

		String collateralClass = "";
		try (PreparedStatement pst1 = con.prepareStatement(
				"SELECT COLLATERAL_CLASS FROM LSM_MS_COLLATERAL_CLASS WHERE UPPER(OWNERSHIP_STATUS) = ?")) {
			pst1.setString(1, owenershipStatus.toUpperCase());
			try (ResultSet rs1 = pst1.executeQuery()) {
				while (rs1.next()) {
					collateralClass = OperationUtillity.NullReplace(rs1.getString("COLLATERAL_CLASS"));
				}
			}
		} catch (Exception e) {
			logger.info(OperationUtillity.traceException(owenershipStatus, e));
		}
		return collateralClass;
	}

	public String getCollateralCode(String temp) {
		return temp;

	}

	public void getSecurityOtherDetails(String pinstid, MainPojo mainpojo) {
		
		try (Connection con = dbConnection.getConnection()) {
		try (PreparedStatement pst = con.prepareStatement(
				"select REF_CODE, REF_DESC from LSM_MS_NATURE_OF_CHARGE where UPPER(REF_DESC) = UPPER(?)")) {
			pst.setString(1, mainpojo.getTypeOfCharge());
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					mainpojo.setReferenceCode(Optional.ofNullable(rs.getString("REF_CODE")).orElse(""));
					mainpojo.setReferenceDescription(Optional.ofNullable(rs.getString("REF_DESC")).orElse(""));
				}
			}
		}
		} catch (Exception e) {
			logger.error(OperationUtillity.traceException(pinstid, e));
		}
	}
	
	public String getFromDeriveFromMs(String screenLevelDropDown, Connection con) {

		String FromDeriveFromMs = "";

		try (PreparedStatement pst = con.prepareStatement(
				"SELECT FROM_DERIVE_VAL_CODE FROM LSM_MS_DERIVED_VALUE WHERE UPPER(SCREEN_LEVEL_DROPDOWN) = ?")) {
			pst.setString(1, screenLevelDropDown.toUpperCase());
			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					FromDeriveFromMs = Optional.ofNullable(rs.getString("FROM_DERIVE_VAL_CODE")).orElse("");
				} else {
					FromDeriveFromMs = "";
				}
			}
		} catch (Exception e) {
			logger.error(OperationUtillity.traceException(screenLevelDropDown, e));
		}
		return FromDeriveFromMs;
	}
	
	public AddressCodes getAddressCode(String pincode) {
		AddressCodes addressCodes = new AddressCodes();
		try (Connection con = dbConnection.getConnection();
			PreparedStatement pst1 = con.prepareStatement("SELECT CITY_CODE,STATE_CODE FROM TSR_MS_PINCODE WHERE PINCODE = ?")) {
		    pst1.setString(1, pincode);
		    try (ResultSet rs1 = pst1.executeQuery()) {
			if (rs1.next()) {
			    addressCodes.setCityCode(Optional.ofNullable((rs1.getString("CITY_CODE"))).orElse(""));
			    addressCodes.setStateCode(Optional.ofNullable((rs1.getString("STATE_CODE"))).orElse(""));
			}
		    }
		} catch (Exception e) {
		    logger.error("CommonDaoForInqDlnkWtdrwl.getAddressCode{}", OperationUtillity.traceException(e));
		}
		logger.info("CommonDaoForInqDlnkWtdrwl.getAddressCodes().outputMap{}", addressCodes);
		return addressCodes;
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
		try (Connection con = dbConnection.getConnection(); PreparedStatement pst = con.prepareStatement(query)) {
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
		logger.info("CommonDaoForInqDlnkWtdrwl.getPropertyOwners() for securityprovider-->" + securityProvider + "<-->"
			+ securitywisePropertyOwnersmap);
		return securitywisePropertyOwnersmap;
	    }
	
	 public String getLSMNumber(String pinstid) {
			String lsmNumber = "";
			try (Connection con = dbConnection.getConnection();
				PreparedStatement pst = con.prepareStatement("SELECT ANSWER AS LSM_NO FROM LSM_MS_ANSWERS  WHERE PINSTID =? AND QUESTION_ID ='2'")) {
			    pst.setString(1, pinstid);
			    try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
				    lsmNumber = Optional.ofNullable(rs.getString("LSM_NO")).orElse("");
				}
			    }
			} catch (Exception e) {
			    logger.info("CommonDaoForInqDlnkWtdrwl.getLSMNumber()", OperationUtillity.traceException(e));
			}
			return lsmNumber;
		    }
	 
	public String getLodgeColl_ID(String pinstid, String reqtype,String processName) {
		logger.info("\n[CommonDaoForInqDlnkWtdrwl.getLodgeColl_ID()].[pinstid: " + pinstid
				+ " ].[reqtypeLC] --> " + reqtype);
		String LC_ID = "" ,LodgeColllateralID = "" , LC_table = "";

		switch (processName) {
		case "Monitoring":
			LC_table = "MONT_SERVICE_REQ_RES";
			break;
		case "Limit_Setup":
			LC_table = "LSM_SERVICE_REQ_RES";
			break;
		}
		
		try (Connection con = dbConnection.getConnection();
				PreparedStatement pst = con.prepareStatement(
						"SELECT MESSAGE FROM "+LC_table+" WHERE PINSTID = ? AND REQUESTTYPE = ? AND STATUS = ? ORDER BY  DATETIME DESC")) {
			pst.setString(1, pinstid);
			pst.setString(2, reqtype);
			pst.setString(3, "SUCCESS");
			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					LC_ID = Optional.ofNullable(rs.getString("MESSAGE")).orElse("");
				}
			}
			logger.info(
					"\n[CommonDaoForInqDlnkWtdrwl.getLodgeColl_ID()].[pinstid: " + pinstid + " ].[LC_ID] --> " + LC_ID);
			if (LC_ID != "") {
				LodgeColllateralID = LC_ID.substring(LC_ID.indexOf(": ") + 2) != ""
						? LC_ID.substring(LC_ID.indexOf(": ") + 2).split(" ")[0]
						: "";
				logger.info("\n[CommonDaoForInqDlnkWtdrwl.getLodgeColl_ID()].[pinstid: " + pinstid
						+ " ].[LodgeColllateralID] --> " + LodgeColllateralID);

			}
		} catch (Exception e) {
			logger.info("CommonDaoForInqDlnkWtdrwl.getLSMNumber()", OperationUtillity.traceException(e));
		}
		return LodgeColllateralID;
	}
	
	public Boolean checkIsExecutedOnce(String pinstid,String reqType) {
		Boolean flag = false;
		int exeCount =0;
		try (Connection con = dbConnection.getConnection()) {
			String IsExecutedOnceQuery = "";
			IsExecutedOnceQuery = "SELECT COUNT(1) AS CNT FROM LSM_FI_EXECUTION_DETAILS WHERE PINSTID = ? AND SERVICE_NAME = 'LODGE COLLATERAL LINKAGE' AND REQUEST_TYPE = ?";
			PreparedStatement pst = con.prepareStatement(IsExecutedOnceQuery);
			pst.setString(1, pinstid);
			pst.setString(2, reqType);
			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					exeCount = rs.getInt("CNT");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				logger.info("CommonDaoForInqDlnkWtdrwl.checkIsExecutedOnce()" + OperationUtillity.traceException(pinstid, ex));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.info("CommonDaoForInqDlnkWtdrwl.checkIsExecutedOnce()" + OperationUtillity.traceException(pinstid, ex));
		}
		logger.info("CommonDaoForInqDlnkWtdrwl.checkIsExecutedOnce().exeCount [" + exeCount + "]");
		if(exeCount > 0) {
			flag = true;
		}
		return flag;
	}
	
	public static  String getHolding(String nameofstock) {
		String collateralcode = "";
		String lsql = "SELECT COLLATERAL_CODE FROM SVT_TRADE_MF_MS WHERE NAME_OF_HOLDING_STACK =  ? ";
		try (Connection con = dbConnection.getConnection();) {
			logger.info("CommonDaoForInqDlnkWtdrwl.getHolding().query ---> " + lsql);
			PreparedStatement statement = con.prepareStatement(lsql);
			statement.setString(1, nameofstock);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				collateralcode =  Optional.ofNullable(rs.getString("COLLATERAL_CODE")).orElse("");
			}
			logger.info("CommonDaoForInqDlnkWtdrwl.getHolding().collateralcode ---> " + collateralcode);
		} catch (Exception ex) {
			logger.info("CommonDaoForInqDlnkWtdrwl.getHolding().Exception\n"
					+ OperationUtillity.traceException(ex));
		}
		return collateralcode;
	}
	
}
