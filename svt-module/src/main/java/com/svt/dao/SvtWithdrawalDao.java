package com.svt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.svt.model.commonModel.InnerPojo;
import com.svt.model.commonModel.MainPojo;
import com.svt.utils.common.OperationUtillity;
import com.svt.utils.dataConnectivity.dbConnection;

@Repository
public class SvtWithdrawalDao {
	
	@Autowired
	CommonDaoForInqDlnkWtdrwl commonDaoForInqDlnkWtdrwl;

	private static final Logger logger = LoggerFactory.getLogger(SvtWithdrawalDao.class);

	public ArrayList<MainPojo> fetchwithdrawal(String pinstId, String processName) {

		
		return null;
	}

	public static List<InnerPojo> fectListOfProducts(String pinstId,
			MainPojo svtWithdrawalReqPojo, String SubTypeSecurityData, Map<String, String[]> limitData,
			Connection con) {

		
		return null;
	}

	public String getCollateralCode(String temp) {
		return temp;

	}
	
	// Individual Starts here
	public MainPojo fetchIndividualProductDtls(String pinstid, String securityName, String subTypeSecurity, String typeOfSvt,
			String Product, String limitPrefix, String limitSuffix, String processName) {
		MainPojo svtMainReqPj = new MainPojo();

		try (Connection con = dbConnection.getConnection()) {
			Map<String, String[]> limitData = CommonDaoForInqDlnkWtdrwl.fetchLimitPrefixSuffix(pinstid, con,processName);
			String lsmNumber = commonDaoForInqDlnkWtdrwl.getSVTCommonData(pinstid, con).get("LSM_NUMBER");
			String uccBasedCustId = commonDaoForInqDlnkWtdrwl.getUCCBasedCustId(pinstid, con);

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
					svtMainReqPj.setCollateral_Class(commonDaoForInqDlnkWtdrwl.fetchCollateralClass(ownershipStatus.toUpperCase(), con));
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
					svtMainReqPj.setInnerPojo(CommonDaoForInqDlnkWtdrwl.fectListOfProducts(pinstid, svtMainReqPj, SubTypeSecurityData, limitData,
							subTypeSecurity, typeOfSvt, Product, con));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("SvtWithdrawalDao.fetchIndividualProductDtls()"
					+ OperationUtillity.traceException(pinstid, e));
		}
		return svtMainReqPj;

	}
	
}
