package com.svt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.svt.model.commonModel.InnerPojo;
import com.svt.model.commonModel.MainPojo;
import com.svt.utils.common.OperationUtillity;
import com.svt.utils.common.SVTFIServiceUtility;
import com.svt.utils.dataConnectivity.dbConnection;

@Repository
public class lodgeCollateralDaoImpl {

	@Autowired
	private SVTFIServiceUtility svtFIServiceUtility;

	private static final Logger logger = LoggerFactory.getLogger(lodgeCollateralDaoImpl.class);

	public ArrayList<MainPojo> getSubTypeSecurityList(String pinstId, String processName) throws Exception {

		String uccBasedCustId = svtFIServiceUtility.getUCCBasedCustId(pinstId);
		String lsmNumber = svtFIServiceUtility.getSVTCommonData(pinstId).get("LSM_NUMBER");

		ArrayList<MainPojo> SubTypeSecurityList = new ArrayList<>();

		try (Connection con = dbConnection.getConnection()) {

			Map<String, String[]> limitData = SvtCommonDao.fetchLimitPrefixSuffix(pinstId, con);

			String securityDetailsQuery = "";
			String SubTypeSecurityData = "";
			String queryTofetchPolicyDtls = "";
			switch (processName) {
			case "Monitoring":
				securityDetailsQuery = "SELECT * FROM (SELECT SECURITY.QUESTION_ID AS SECURITY_QUESTION, SECURITY.ANSWER AS SECURITY_ANSWER, SECURITY.SECURITY_PROVIDER_NAME AS SECURITY_NAME,SECURITY.SECURITY_PROVIDER_ID AS SECURITY_PROVIDER_ID FROM LSM_SECU_MONTY_ANS SECURITY WHERE SECURITY.PINSTID = ?  AND SECURITY.QUESTION_ID IN ('76','78','79','87','91','92','93','94','186','189','190','191','192','193','194','195','196','197','84','186'))  PIVOT ( MAX (SECURITY_ANSWER)  FOR SECURITY_QUESTION  IN ('76' AS SECURITY,'78' AS OWNERSHIP_STATUS,'79' AS VALUATION_DATE,'87' AS SECURITY_CREATED ,'91' AS SECURITY_CREATED_DATE ,'92' AS SECURITY_SUBMISSION_DATE, '93' AS APPORTIONED_VALUE, '94' AS SECURITY_PROVIDER_NAME, '186' AS REVIEW_DATE,'189' AS ADDRESS_LINE,'190' AS AREA,'191' AS ROAD, '192' AS LANDMARK,'193' AS PINCODE,'194' AS DISTRICT ,'195' AS CITY,'196' AS STATE,'197' AS COUNTRY , '84' AS SECURITY_DUE_DATE, '186' AS SECURITY_REVIEW_DATE )) ORDER BY SECURITY_NAME";
//				SubTypeSecurityData = "SELECT * FROM (SELECT DISTINCT A.ANSWER AS SUB_TYPE_SECURITY, B.ANSWER AS TYPE_OF_SECURITY_SVT FROM LSM_SECU_MONTY_ANS A JOIN LSM_SECU_MONTY_ANS B ON A.PINSTID = B.PINSTID AND A.SECURITY_PROVIDER_ID=B.SECURITY_PROVIDER_ID AND SUBSTR(A.QUESTION_ID,INSTR(A.QUESTION_ID,'_') + 1) = SUBSTR(B.QUESTION_ID,INSTR(B.QUESTION_ID,'_') + 1) WHERE A.PINSTID = ? AND A.SECURITY_PROVIDER_ID = ? AND A.QUESTION = 'Sub Type Security'   AND B.QUESTION = 'Type Of Security (SVT)')";
				SubTypeSecurityData = "SELECT * FROM (SELECT DISTINCT A.ANSWER AS SUB_TYPE_SECURITY, B.ANSWER AS TYPE_OF_SECURITY_SVT, C.ANSWER AS PRODUCT FROM LSM_SECU_MONTY_ANS A , LSM_SECU_MONTY_ANS B , LSM_SECU_MONTY_ANS C WHERE A.PINSTID = B.PINSTID AND A.PINSTID = C.PINSTID AND A.SECURITY_PROVIDER_ID=B.SECURITY_PROVIDER_ID AND A.SECURITY_PROVIDER_ID=C.SECURITY_PROVIDER_ID AND SUBSTR(A.QUESTION_ID,INSTR(A.QUESTION_ID,'_') + 1) = SUBSTR(B.QUESTION_ID,INSTR(B.QUESTION_ID,'_') + 1) AND SUBSTR(A.QUESTION_ID,INSTR(A.QUESTION_ID,'_') + 1) = SUBSTR(C.QUESTION_ID,INSTR(C.QUESTION_ID,'_') + 1) AND A.PINSTID = ? AND A.SECURITY_PROVIDER_ID = ? AND A.QUESTION = 'Sub Type Security' AND B.QUESTION = 'Type Of Security (SVT)' AND C.QUESTION = 'Products')";
				queryTofetchPolicyDtls = "SELECT * FROM (SELECT DISTINCT  A.ANSWER AS POLICY_NO, B.ANSWER AS POLICY_AMOUNT FROM LSM_SECU_MONTY_ANS A JOIN LSM_SECU_MONTY_ANS B ON A.PINSTID = B.PINSTID AND A.SECURITY_PROVIDER_ID = B.SECURITY_PROVIDER_ID AND SUBSTR(A.QUESTION_ID,INSTR(A.QUESTION_ID,'_') + 1) = SUBSTR(B.QUESTION_ID,INSTR(B.QUESTION_ID,'_') + 1) WHERE A.PINSTID = ? AND A.SECURITY_PROVIDER_ID = ? AND A.QUESTION_ID LIKE '184%' AND B.QUESTION_ID  LIKE '185%')";
				break;
			case "Limit_Setup":
				securityDetailsQuery = "SELECT * FROM (SELECT SECURITY.QUESTION_ID AS SECURITY_QUESTION, SECURITY.ANSWER AS SECURITY_ANSWER, SECURITY.SECURITY_PROVIDER_NAME AS SECURITY_NAME,SECURITY.SECURITY_PROVIDER_ID AS SECURITY_PROVIDER_ID FROM LSM_SECURITY_ANSWERS SECURITY WHERE SECURITY.PINSTID = ?  AND SECURITY.QUESTION_ID IN ('76','78','79','87','91','92','93','94','186','189','190','191','192','193','194','195','196','197','84','186'))  PIVOT ( MAX (SECURITY_ANSWER)  FOR SECURITY_QUESTION  IN ('76' AS SECURITY,'78' AS OWNERSHIP_STATUS,'79' AS VALUATION_DATE,'87' AS SECURITY_CREATED ,'91' AS SECURITY_CREATED_DATE ,'92' AS SECURITY_SUBMISSION_DATE, '93' AS APPORTIONED_VALUE, '94' AS SECURITY_PROVIDER_NAME, '186' AS REVIEW_DATE,'189' AS ADDRESS_LINE,'190' AS AREA,'191' AS ROAD, '192' AS LANDMARK,'193' AS PINCODE,'194' AS DISTRICT ,'195' AS CITY,'196' AS STATE,'197' AS COUNTRY , '84' AS SECURITY_DUE_DATE, '186' AS SECURITY_REVIEW_DATE )) ORDER BY SECURITY_NAME";
//				SubTypeSecurityData = "SELECT * FROM (SELECT DISTINCT  A.ANSWER AS SUB_TYPE_SECURITY, B.ANSWER AS TYPE_OF_SECURITY_SVT FROM LSM_SECURITY_ANSWERS A JOIN LSM_SECURITY_ANSWERS B ON A.PINSTID = B.PINSTID AND A.SECURITY_PROVIDER_ID=B.SECURITY_PROVIDER_ID AND SUBSTR(A.QUESTION_ID,INSTR(A.QUESTION_ID,'_') + 1) = SUBSTR(B.QUESTION_ID,INSTR(B.QUESTION_ID,'_') + 1) WHERE A.PINSTID = ? AND A.SECURITY_PROVIDER_ID = ? AND A.QUESTION = 'Sub Type Security'   AND B.QUESTION = 'Type Of Security (SVT)')";
				SubTypeSecurityData = "SELECT DISTINCT A.ANSWER AS SUB_TYPE_SECURITY, B.ANSWER AS TYPE_OF_SECURITY_SVT, C.ANSWER AS PRODUCT FROM LSM_SECURITY_ANSWERS A , LSM_SECURITY_ANSWERS B , LSM_SECURITY_ANSWERS C WHERE A.PINSTID = B.PINSTID AND A.PINSTID = C.PINSTID AND A.SECURITY_PROVIDER_ID=B.SECURITY_PROVIDER_ID AND A.SECURITY_PROVIDER_ID=C.SECURITY_PROVIDER_ID AND SUBSTR(A.QUESTION_ID,INSTR(A.QUESTION_ID,'_') + 1) = SUBSTR(B.QUESTION_ID,INSTR(B.QUESTION_ID,'_') + 1) AND SUBSTR(A.QUESTION_ID,INSTR(A.QUESTION_ID,'_') + 1) = SUBSTR(C.QUESTION_ID,INSTR(C.QUESTION_ID,'_') + 1) AND A.PINSTID = ? AND A.SECURITY_PROVIDER_ID = ? AND A.QUESTION = 'Sub Type Security' AND B.QUESTION = 'Type Of Security (SVT)' AND C.QUESTION = 'Products'";
				queryTofetchPolicyDtls = "SELECT * FROM (SELECT DISTINCT  A.ANSWER AS POLICY_NO, B.ANSWER AS POLICY_AMOUNT FROM LSM_SECURITY_ANSWERS A JOIN LSM_SECURITY_ANSWERS B ON A.PINSTID = B.PINSTID AND A.SECURITY_PROVIDER_ID = B.SECURITY_PROVIDER_ID AND SUBSTR(A.QUESTION_ID,INSTR(A.QUESTION_ID,'_') + 1) = SUBSTR(B.QUESTION_ID,INSTR(B.QUESTION_ID,'_') + 1) WHERE A.PINSTID = ? AND A.SECURITY_PROVIDER_ID = ? AND A.QUESTION_ID LIKE '184%' AND B.QUESTION_ID  LIKE '185%')";
				break;
			}

			PreparedStatement pst = con.prepareStatement(securityDetailsQuery);
			pst.setString(1, pinstId);

			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					MainPojo lodgeCollateralReqDtls = new MainPojo();

					lodgeCollateralReqDtls
							.setCeiling_Limit(OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
					String ownershipStatus = OperationUtillity.NullReplace(rs.getString("OWNERSHIP_STATUS"));
					lodgeCollateralReqDtls.setCollateral_Class(
							svtFIServiceUtility.fetchCollateralClass(ownershipStatus.toUpperCase()));

					lodgeCollateralReqDtls
							.setGross_Val(OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
					lodgeCollateralReqDtls
							.setDue_Dt(OperationUtillity.NullReplace(rs.getString("SECURITY_CREATED_DATE")));
					lodgeCollateralReqDtls
							.setSecurity_Created(OperationUtillity.NullReplace(rs.getString("SECURITY_CREATED")));
					lodgeCollateralReqDtls
							.setLast_Val_Date(OperationUtillity.NullReplace(rs.getString("VALUATION_DATE")));
//					lodgeCollateralReqDtls.setNatureOfCharge(OperationUtillity.NullReplace(rs.getString("NatureOfCharge")));
					lodgeCollateralReqDtls.setNotes(OperationUtillity.NullReplace(lsmNumber));
					lodgeCollateralReqDtls.setNotes1(OperationUtillity.NullReplace(lsmNumber));
					lodgeCollateralReqDtls.setNotes2(OperationUtillity.NullReplace(lsmNumber));
					lodgeCollateralReqDtls.setReview_Dt(OperationUtillity.NullReplace(rs.getString("REVIEW_DATE")));
					lodgeCollateralReqDtls.setReceive_Dt(OperationUtillity.NullReplace(rs.getString("REVIEW_DATE")));
//					lodgeCollateralReqDtls.setFromDeriveVal(OperationUtillity.NullReplace(rs.getString("FromDeriveVal")));
					lodgeCollateralReqDtls.setValue(OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
					lodgeCollateralReqDtls
							.setCollateral_Value(OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
					lodgeCollateralReqDtls
							.setSecurity_Id(OperationUtillity.NullReplace(rs.getString("SECURITY_PROVIDER_ID")));
					lodgeCollateralReqDtls
							.setSecurity_Name(OperationUtillity.NullReplace(rs.getString("SECURITY_NAME")));
					lodgeCollateralReqDtls.setSecurity_Type(OperationUtillity.NullReplace(rs.getString("SECURITY")));
					lodgeCollateralReqDtls.setUcc_Based_CustId(OperationUtillity.NullReplace(uccBasedCustId));

					// SVT List on the basis of security ID
					List<InnerPojo> svtSecurityDtlsList = new ArrayList<InnerPojo>();
					svtSecurityDtlsList.addAll(fetchSubTypeSecurityList(pinstId, lodgeCollateralReqDtls,
							SubTypeSecurityData, limitData, con));

					SubTypeSecurityList.add(lodgeCollateralReqDtls);
				}
			}
			if (pst != null) {
				pst = null;
			}
		} catch (Exception ex) {
//			System.out.println("lodgeCollateralDaoImpl.getSubTypeSecurityList().Exception :: "+ ex);
			logger.info("\nException.lodgeCollateralDaoImpl.getSubTypeSecurityList().pinstid " + pinstId + " ::\n "
					+ OperationUtillity.traceException(pinstId, ex));
		}
		logger.info(
				"lodgeCollateralDaoImpl.getSubTypeSecurityList()-->pinstid->" + pinstId + "\n" + SubTypeSecurityList);

		return SubTypeSecurityList;

	}

	public List<InnerPojo> fetchSubTypeSecurityList(String pinstId, MainPojo lodgeCollateralReqDtls,
			String SubTypeSecurityData, Map<String, String[]> limitData, Connection con) {
		List<InnerPojo> svtSecurityDtlsList = new ArrayList<InnerPojo>();
//		String SubTypeSecurityData = "SELECT * FROM (SELECT  DISTINCT QUESTION, ANSWER FROM lsm_security_answers WHERE pinstid = ? AND security_provider_id = ? and QUESTION in ('Sub Type Security','Type Of Security (SVT)')) PIVOT(MAX(ANSWER) FOR QUESTION in ('Sub Type Security' as Sub_Type_Security,'Type Of Security (SVT)' AS Type_Of_Security_SVT))";
		try (PreparedStatement pstSvt = con.prepareStatement(SubTypeSecurityData);) {
			pstSvt.setString(1, pinstId);
			pstSvt.setString(2, lodgeCollateralReqDtls.getSecurityId());
			try (ResultSet rsSvt = pstSvt.executeQuery()) {

				while (rsSvt.next()) {
					InnerPojo svtSecurityDtls = new InnerPojo();

					svtSecurityDtls
							.setSubTypeSecurity(OperationUtillity.NullReplace(rsSvt.getString("SUB_TYPE_SECURITY")));
					svtSecurityDtls.setTypeOfSecurity(
							OperationUtillity.NullReplace(rsSvt.getString("TYPE_OF_SECURITY_SVT")));
					svtSecurityDtls.setProduct(OperationUtillity.NullReplace(rsSvt.getString("PRODUCT")));
					svtSecurityDtls.setLimitPrefix(limitData.get(svtSecurityDtls.getProduct())[0]);
					svtSecurityDtls.setLimitSuffix(limitData.get(svtSecurityDtls.getProduct())[1]);

					svtSecurityDtlsList.add(svtSecurityDtls);
				}
				lodgeCollateralReqDtls.setInnerPojo(svtSecurityDtlsList);
			}
		} catch (Exception e) {
			logger.info("lodgeCollateralDaoImpl.fetchSubTypeSecurityList()" + OperationUtillity.traceException(e));
		}
		return svtSecurityDtlsList;
	}

	public MainPojo getSubTypeSecurityData(String pinstid, String securityName, String subTypeSecurity,
			String typeOfSvt, String product, String processName) throws SQLException, Exception {

		MainPojo lodgeCollateralReqDtls = new MainPojo();
		String uccBasedCustId = svtFIServiceUtility.getUCCBasedCustId(pinstid);
		String lsmNumber = svtFIServiceUtility.getSVTCommonData(pinstid).get("LSM_NUMBER");
//		List<Map<String, String>> securityWiseOtherDetailsListofMap = svtFIServiceUtility.getSecurityWiseOtherDetails(pinstId);

		try (Connection con = dbConnection.getConnection()) {

			Map<String, String[]> limitData = SvtCommonDao.fetchLimitPrefixSuffix(pinstid, con);

			String securityDetailsQuery = "";
			String SubTypeSecurityData = "";
			switch (processName) {
			case "Monitoring":
				securityDetailsQuery = "SELECT * FROM (SELECT SECURITY.QUESTION_ID AS SECURITY_QUESTION, SECURITY.ANSWER AS SECURITY_ANSWER, SECURITY.SECURITY_PROVIDER_NAME AS SECURITY_NAME,SECURITY.SECURITY_PROVIDER_ID AS SECURITY_PROVIDER_ID FROM LSM_SECU_MONTY_ANS SECURITY WHERE SECURITY.PINSTID = ? AND SECURITY.SECURITY_PROVIDER_NAME = ?  AND SECURITY.QUESTION_ID IN ('76','78','79','87','91','92','93','94','186','189','190','191','192','193','194','195','196','197','84','186'))  PIVOT ( MAX (SECURITY_ANSWER)  FOR SECURITY_QUESTION  IN ('76' AS SECURITY,'78' AS OWNERSHIP_STATUS,'79' AS VALUATION_DATE,'87' AS SECURITY_CREATED ,'91' AS SECURITY_CREATED_DATE,'92' AS SECURITY_SUBMISSION_DATE, '93' AS APPORTIONED_VALUE, '94' AS SECURITY_PROVIDER_NAME, '186' AS REVIEW_DATE,'189' AS ADDRESS_LINE,'190' AS AREA,'191' AS ROAD, '192' AS LANDMARK,'193' AS PINCODE,'194' AS DISTRICT ,'195' AS CITY,'196' AS STATE,'197' AS COUNTRY , '84' AS SECURITY_DUE_DATE, '186' AS SECURITY_REVIEW_DATE )) ORDER BY SECURITY_NAME";
				SubTypeSecurityData = "SELECT * FROM (SELECT DISTINCT  A.ANSWER AS SUB_TYPE_SECURITY, B.ANSWER AS TYPE_OF_SECURITY_SVT FROM LSM_SECU_MONTY_ANS A JOIN LSM_SECU_MONTY_ANS B ON A.PINSTID = B.PINSTID AND A.SECURITY_PROVIDER_ID=B.SECURITY_PROVIDER_ID AND SUBSTR(A.QUESTION_ID,INSTR(A.QUESTION_ID,'_') + 1) = SUBSTR(B.QUESTION_ID,INSTR(B.QUESTION_ID,'_') + 1) WHERE A.PINSTID = ? AND A.SECURITY_PROVIDER_ID = ? AND A.QUESTION = 'Sub Type Security'   AND B.QUESTION = 'Type Of Security (SVT)') ";
				break;
			case "Limit_Setup":
				securityDetailsQuery = "SELECT * FROM (SELECT SECURITY.QUESTION_ID AS SECURITY_QUESTION, SECURITY.ANSWER AS SECURITY_ANSWER, SECURITY.SECURITY_PROVIDER_NAME AS SECURITY_NAME,SECURITY.SECURITY_PROVIDER_ID AS SECURITY_PROVIDER_ID FROM LSM_SECURITY_ANSWERS SECURITY WHERE SECURITY.PINSTID = ? AND SECURITY.SECURITY_PROVIDER_NAME = ?  AND SECURITY.QUESTION_ID IN ('76','78','79','87','91','92','93','94','186','189','190','191','192','193','194','195','196','197','84','186'))  PIVOT ( MAX (SECURITY_ANSWER)  FOR SECURITY_QUESTION  IN ('76' AS SECURITY,'78' AS OWNERSHIP_STATUS,'79' AS VALUATION_DATE,'87' AS SECURITY_CREATED ,'91' AS SECURITY_CREATED_DATE,'92' AS SECURITY_SUBMISSION_DATE, '93' AS APPORTIONED_VALUE, '94' AS SECURITY_PROVIDER_NAME, '186' AS REVIEW_DATE,'189' AS ADDRESS_LINE,'190' AS AREA,'191' AS ROAD, '192' AS LANDMARK,'193' AS PINCODE,'194' AS DISTRICT ,'195' AS CITY,'196' AS STATE,'197' AS COUNTRY , '84' AS SECURITY_DUE_DATE, '186' AS SECURITY_REVIEW_DATE )) ORDER BY SECURITY_NAME";
				SubTypeSecurityData = "SELECT * FROM (SELECT DISTINCT  A.ANSWER AS SUB_TYPE_SECURITY, B.ANSWER AS TYPE_OF_SECURITY_SVT FROM LSM_SECURITY_ANSWERS A JOIN LSM_SECURITY_ANSWERS B ON A.PINSTID = B.PINSTID AND A.SECURITY_PROVIDER_ID=B.SECURITY_PROVIDER_ID AND SUBSTR(A.QUESTION_ID,INSTR(A.QUESTION_ID,'_') + 1) = SUBSTR(B.QUESTION_ID,INSTR(B.QUESTION_ID,'_') + 1) WHERE A.PINSTID = ? AND A.SECURITY_PROVIDER_ID = ? AND A.QUESTION = 'Sub Type Security'   AND B.QUESTION = 'Type Of Security (SVT)') ";
				break;
			}

			PreparedStatement pst = con.prepareStatement(securityDetailsQuery);
			pst.setString(1, pinstid);
			pst.setString(2, securityName);
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {

					lodgeCollateralReqDtls
							.setCeiling_Limit(OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
					String ownershipStatus = OperationUtillity.NullReplace(rs.getString("OWNERSHIP_STATUS"));
					lodgeCollateralReqDtls.setCollateral_Class(
							svtFIServiceUtility.fetchCollateralClass(ownershipStatus.toUpperCase()));

					lodgeCollateralReqDtls
							.setGross_Val(OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
					lodgeCollateralReqDtls
							.setDue_Dt(OperationUtillity.NullReplace(rs.getString("SECURITY_CREATED_DATE")));
					lodgeCollateralReqDtls
							.setSecurity_Created(OperationUtillity.NullReplace(rs.getString("SECURITY_CREATED")));
					lodgeCollateralReqDtls
							.setLast_Val_Date(OperationUtillity.NullReplace(rs.getString("VALUATION_DATE")));
//					lodgeCollateralReqDtls.setNatureOfCharge(OperationUtillity.NullReplace(rs.getString("NatureOfCharge")));
					lodgeCollateralReqDtls.setNotes(OperationUtillity.NullReplace(lsmNumber));
					lodgeCollateralReqDtls.setNotes1(OperationUtillity.NullReplace(lsmNumber));
					lodgeCollateralReqDtls.setNotes2(OperationUtillity.NullReplace(lsmNumber));
					lodgeCollateralReqDtls.setReview_Dt(OperationUtillity.NullReplace(rs.getString("REVIEW_DATE")));
					lodgeCollateralReqDtls.setReceive_Dt(OperationUtillity.NullReplace(rs.getString("REVIEW_DATE")));
//					lodgeCollateralReqDtls.setFromDeriveVal(OperationUtillity.NullReplace(rs.getString("FromDeriveVal")));
					lodgeCollateralReqDtls.setValue(OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
					lodgeCollateralReqDtls
							.setCollateral_Value(OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
					lodgeCollateralReqDtls
							.setSecurity_Id(OperationUtillity.NullReplace(rs.getString("SECURITY_PROVIDER_ID")));
					lodgeCollateralReqDtls
							.setSecurity_Name(OperationUtillity.NullReplace(rs.getString("SECURITY_NAME")));
					lodgeCollateralReqDtls.setSecurity_Type(OperationUtillity.NullReplace(rs.getString("SECURITY")));
					lodgeCollateralReqDtls.setUcc_Based_CustId(OperationUtillity.NullReplace(uccBasedCustId));

					// SVT List on the basis of security ID
					List<InnerPojo> svtSecurityDtlsList = new ArrayList<InnerPojo>();
					svtSecurityDtlsList.addAll(fetchSubTypeSecurityList(pinstid, lodgeCollateralReqDtls,
							SubTypeSecurityData, limitData, con));

					// List of Policy Details for MFU, LIC
//					List<policySecurityDetails> polcSecDtlsList = new ArrayList<policySecurityDetails>();
//					polcSecDtlsList
//							.addAll(fetchPolicyDetails(pinstid, lodgeCollateralReqDtls, queryTofetchPolicyDtls, con));
				}
			}
			if (pst != null) {
				pst = null;
			}
		} catch (Exception ex) {
			logger.info("\nException.lodgeCollateralDaoImpl.getSubTypeSecurityData().pinstid " + pinstid + " ::\n "
					+ OperationUtillity.traceException(pinstid, ex));
		}
		return lodgeCollateralReqDtls;
	}

	public String getCollateralCode(String subTypeSecuritySvt) {
		String collateralCode = "";

		String queryForCollateralCode = "SELECT DISTINCT ICORE_CODE AS COLLATERAL_CODE FROM UNICORE_MS_SUBTYPE_SVT WHERE SUB_TYPE_SECURITY_SVT = ? ";
		try (Connection con = dbConnection.getConnection()) {
			try (PreparedStatement CltrlCdPs = con.prepareStatement(queryForCollateralCode);) {
				CltrlCdPs.setString(1, subTypeSecuritySvt);
				try (ResultSet CltrlCdRs = CltrlCdPs.executeQuery()) {
					while (CltrlCdRs.next()) {
						collateralCode = OperationUtillity.NullReplace(CltrlCdRs.getString("COLLATERAL_CODE"));
					}
				}
			}
		} catch (Exception ex) {
			logger.info(
					"\nException.lodgeCollateralDaoImpl.getSubTypeSecurityList().getCollateralCode().subTypeSecuritySvt :\n"
							+ OperationUtillity.traceException(subTypeSecuritySvt, ex));
		}
		return collateralCode;
	}

	public void getNatureOfChargeAndDerivedVal(String pinstId, String security, MainPojo lodgeCollateralReqND) {

		String queryForCollateralCode = "SELECT  A.SECURITY_DETAILS, A.TYPE_SEC_CREATION,A.SEELING_LIMIT,B.REF_DESC, B.REF_CODE,  C.ICORE_CODE AS DERIVED_VALUE FROM CAL_SECURITY_DETAILS_SECURED A  JOIN LSM_MS_NATURE_OF_CHARGE B ON UPPER(B.REF_DESC) = UPPER(A.TYPE_SEC_CREATION)  JOIN LSM_MS_DERIVED_VALUE C  ON UPPER(C.SCREEN_LEVEL_DROPDOWN) = UPPER(A.DERIVED_VALUE) WHERE PINSTID =( SELECT DISHUBID FROM LIMIT_SETUP_EXT WHERE PINSTID= ? ) AND A.SECURITY_DETAILS = ? ";
		try (Connection con = dbConnection.getConnection()) {
			try (PreparedStatement CltrlCdPs = con.prepareStatement(queryForCollateralCode);) {
				CltrlCdPs.setString(1, pinstId);
				CltrlCdPs.setString(2, security);
				try (ResultSet CltrlCdRs = CltrlCdPs.executeQuery()) {
					while (CltrlCdRs.next()) {
						lodgeCollateralReqND
								.setNatureOfCharge(OperationUtillity.NullReplace(CltrlCdRs.getString("REF_CODE")));
						lodgeCollateralReqND
								.setFromDeriveVal(OperationUtillity.NullReplace(CltrlCdRs.getString("DERIVED_VALUE")));
					}
				}
			}
		} catch (Exception ex) {
			logger.info(
					"\nException.lodgeCollateralDaoImpl.getSubTypeSecurityList().getNatureOfChargeAndDerivedVal() ::\n"
							+ OperationUtillity.traceException(pinstId, ex));
		}
	}

	public boolean checkServiceStatus(String pinstId, InnerPojo svtSecDtls, MainPojo lodgeCollateralSec) {
		boolean status = true;
		try (Connection con = dbConnection.getConnection()) {
			String fiServiceStatusQuery = "SELECT DISTINCT(STATUS) FROM LSM_SERVICE_LATEST_REQ_RES WHERE PINSTID=? AND REQUESTTYPE=? AND UPPER(STATUS)=? ";
			String requestType = "LODGE COLLATERAL : "
					+ OperationUtillity.NullReplace(lodgeCollateralSec.getSecurityName()) + " : "
					+ OperationUtillity.NullReplace(svtSecDtls.getSubTypeSecurity()) + " : "
					+ OperationUtillity.NullReplace(svtSecDtls.getTypeOfSecurity());

			try (PreparedStatement fiSrvcStsPs = con.prepareStatement(fiServiceStatusQuery);) {
				fiSrvcStsPs.setString(1, pinstId);
				fiSrvcStsPs.setString(2, requestType);
				fiSrvcStsPs.setString(3, "SUCCESS");
				try (ResultSet fiSrvcStsRs = fiSrvcStsPs.executeQuery()) {
					if (fiSrvcStsRs.next()) {
						status = false;
					} else {
						status = true;
					}
				}
			}
		} catch (Exception ex) {
			logger.info("\nException.lodgeCollateralDaoImpl.getSubTypeSecurityList().checkServiceStatus() :\n"
					+ OperationUtillity.traceException(pinstId, ex));
		}
		return status;
	}
	
}
