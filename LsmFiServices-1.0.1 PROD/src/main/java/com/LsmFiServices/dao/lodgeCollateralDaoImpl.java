package com.LsmFiServices.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.LsmFiServices.Utility.DBConnect;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.SVTFIServiceUtility;
import com.LsmFiServices.pojo.lodgeCollateral.lodgeCollateralRequestPojo;
import com.LsmFiServices.pojo.lodgeCollateral.policySecurityDetails;
import com.LsmFiServices.pojo.lodgeCollateral.svtSecurityDetails;

@Repository
public class lodgeCollateralDaoImpl {

	@Autowired
	private SVTFIServiceUtility svtFIServiceUtility;

	private static final Logger logger = LoggerFactory.getLogger(lodgeCollateralDaoImpl.class);

	public ArrayList<lodgeCollateralRequestPojo> getSubTypeSecurityList(String pinstId, String processName) throws Exception {

		String uccBasedCustId = svtFIServiceUtility.getUCCBasedCustId(pinstId);
		String lsmNumber = svtFIServiceUtility.getSVTCommonData(pinstId).get("LSM_NUMBER");
//		List<Map<String, String>> securityWiseOtherDetailsListofMap = svtFIServiceUtility.getSecurityWiseOtherDetails(pinstId);

		ArrayList<lodgeCollateralRequestPojo> SubTypeSecurityList = new ArrayList<>();

		try (Connection con = DBConnect.getConnection()) {
			String securityDetailsQuery = "";
			String SubTypeSecurityData = "";
			String queryTofetchPolicyDtls = "";
			switch (processName) {
			case "Monitoring":
				securityDetailsQuery = "SELECT * FROM (SELECT SECURITY.QUESTION_ID AS SECURITY_QUESTION, SECURITY.ANSWER AS SECURITY_ANSWER, SECURITY.SECURITY_PROVIDER_NAME AS SECURITY_NAME,SECURITY.SECURITY_PROVIDER_ID AS SECURITY_PROVIDER_ID FROM LSM_SECU_MONTY_ANS SECURITY WHERE SECURITY.PINSTID = ?  AND SECURITY.QUESTION_ID IN ('76','78','79','87','91','92','93','94','186','189','190','191','192','193','194','195','196','197','84','186'))  PIVOT ( MAX (SECURITY_ANSWER)  FOR SECURITY_QUESTION  IN ('76' AS SECURITY,'78' AS OWNERSHIP_STATUS,'79' AS VALUATION_DATE,'87' AS SECURITY_CREATED ,'91' AS SECURITY_CREATED_DATE ,'92' AS SECURITY_SUBMISSION_DATE, '93' AS APPORTIONED_VALUE, '94' AS SECURITY_PROVIDER_NAME, '186' AS REVIEW_DATE,'189' AS ADDRESS_LINE,'190' AS AREA,'191' AS ROAD, '192' AS LANDMARK,'193' AS PINCODE,'194' AS DISTRICT ,'195' AS CITY,'196' AS STATE,'197' AS COUNTRY , '84' AS SECURITY_DUE_DATE, '186' AS SECURITY_REVIEW_DATE )) ORDER BY SECURITY_NAME";
				SubTypeSecurityData = "SELECT * FROM (SELECT DISTINCT A.ANSWER AS SUB_TYPE_SECURITY, B.ANSWER AS TYPE_OF_SECURITY_SVT FROM LSM_SECU_MONTY_ANS A JOIN LSM_SECU_MONTY_ANS B ON A.PINSTID = B.PINSTID AND A.SECURITY_PROVIDER_ID=B.SECURITY_PROVIDER_ID AND SUBSTR(A.QUESTION_ID,INSTR(A.QUESTION_ID,'_') + 1) = SUBSTR(B.QUESTION_ID,INSTR(B.QUESTION_ID,'_') + 1) WHERE A.PINSTID = ? AND A.SECURITY_PROVIDER_ID = ? AND A.QUESTION = 'Sub Type Security'   AND B.QUESTION = 'Type Of Security (SVT)')";
				queryTofetchPolicyDtls = "SELECT * FROM (SELECT DISTINCT  A.ANSWER AS POLICY_NO, B.ANSWER AS POLICY_AMOUNT FROM LSM_SECU_MONTY_ANS A JOIN LSM_SECU_MONTY_ANS B ON A.PINSTID = B.PINSTID AND A.SECURITY_PROVIDER_ID = B.SECURITY_PROVIDER_ID AND SUBSTR(A.QUESTION_ID,INSTR(A.QUESTION_ID,'_') + 1) = SUBSTR(B.QUESTION_ID,INSTR(B.QUESTION_ID,'_') + 1) WHERE A.PINSTID = ? AND A.SECURITY_PROVIDER_ID = ? AND A.QUESTION_ID LIKE '184%' AND B.QUESTION_ID  LIKE '185%')";
				break;
			case "Limit_Setup":
				securityDetailsQuery = "SELECT * FROM (SELECT SECURITY.QUESTION_ID AS SECURITY_QUESTION, SECURITY.ANSWER AS SECURITY_ANSWER, SECURITY.SECURITY_PROVIDER_NAME AS SECURITY_NAME,SECURITY.SECURITY_PROVIDER_ID AS SECURITY_PROVIDER_ID FROM LSM_SECURITY_ANSWERS SECURITY WHERE SECURITY.PINSTID = ?  AND SECURITY.QUESTION_ID IN ('76','78','79','87','91','92','93','94','186','189','190','191','192','193','194','195','196','197','84','186'))  PIVOT ( MAX (SECURITY_ANSWER)  FOR SECURITY_QUESTION  IN ('76' AS SECURITY,'78' AS OWNERSHIP_STATUS,'79' AS VALUATION_DATE,'87' AS SECURITY_CREATED ,'91' AS SECURITY_CREATED_DATE ,'92' AS SECURITY_SUBMISSION_DATE, '93' AS APPORTIONED_VALUE, '94' AS SECURITY_PROVIDER_NAME, '186' AS REVIEW_DATE,'189' AS ADDRESS_LINE,'190' AS AREA,'191' AS ROAD, '192' AS LANDMARK,'193' AS PINCODE,'194' AS DISTRICT ,'195' AS CITY,'196' AS STATE,'197' AS COUNTRY , '84' AS SECURITY_DUE_DATE, '186' AS SECURITY_REVIEW_DATE )) ORDER BY SECURITY_NAME";
				SubTypeSecurityData = "SELECT * FROM (SELECT DISTINCT  A.ANSWER AS SUB_TYPE_SECURITY, B.ANSWER AS TYPE_OF_SECURITY_SVT FROM LSM_SECURITY_ANSWERS A JOIN LSM_SECURITY_ANSWERS B ON A.PINSTID = B.PINSTID AND A.SECURITY_PROVIDER_ID=B.SECURITY_PROVIDER_ID AND SUBSTR(A.QUESTION_ID,INSTR(A.QUESTION_ID,'_') + 1) = SUBSTR(B.QUESTION_ID,INSTR(B.QUESTION_ID,'_') + 1) WHERE A.PINSTID = ? AND A.SECURITY_PROVIDER_ID = ? AND A.QUESTION = 'Sub Type Security'   AND B.QUESTION = 'Type Of Security (SVT)')";
				queryTofetchPolicyDtls = "SELECT * FROM (SELECT DISTINCT  A.ANSWER AS POLICY_NO, B.ANSWER AS POLICY_AMOUNT FROM LSM_SECURITY_ANSWERS A JOIN LSM_SECURITY_ANSWERS B ON A.PINSTID = B.PINSTID AND A.SECURITY_PROVIDER_ID = B.SECURITY_PROVIDER_ID AND SUBSTR(A.QUESTION_ID,INSTR(A.QUESTION_ID,'_') + 1) = SUBSTR(B.QUESTION_ID,INSTR(B.QUESTION_ID,'_') + 1) WHERE A.PINSTID = ? AND A.SECURITY_PROVIDER_ID = ? AND A.QUESTION_ID LIKE '184%' AND B.QUESTION_ID  LIKE '185%')";
				break;
			}

			PreparedStatement pst = con.prepareStatement(securityDetailsQuery);
			pst.setString(1, pinstId);

			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					lodgeCollateralRequestPojo lodgeCollateralReqDtls = new lodgeCollateralRequestPojo();

					lodgeCollateralReqDtls.setCeiling_Limit(OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
					String ownershipStatus = OperationUtillity.NullReplace(rs.getString("OWNERSHIP_STATUS"));
					lodgeCollateralReqDtls.setCollateral_Class(svtFIServiceUtility.fetchCollateralClass(ownershipStatus.toUpperCase()));
				
					lodgeCollateralReqDtls.setGross_Val(OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
					lodgeCollateralReqDtls.setDue_Dt(OperationUtillity.NullReplace(rs.getString("SECURITY_CREATED_DATE")));
					lodgeCollateralReqDtls.setSecurity_Created(OperationUtillity.NullReplace(rs.getString("SECURITY_CREATED")));
					lodgeCollateralReqDtls.setLast_Val_Date(OperationUtillity.NullReplace(rs.getString("VALUATION_DATE")));
//					lodgeCollateralReqDtls.setNatureOfCharge(OperationUtillity.NullReplace(rs.getString("NatureOfCharge")));
					lodgeCollateralReqDtls.setNotes(OperationUtillity.NullReplace(lsmNumber));
					lodgeCollateralReqDtls.setNotes1(OperationUtillity.NullReplace(lsmNumber));
					lodgeCollateralReqDtls.setNotes2(OperationUtillity.NullReplace(lsmNumber));
					lodgeCollateralReqDtls.setReview_Dt(OperationUtillity.NullReplace(rs.getString("REVIEW_DATE")));
					lodgeCollateralReqDtls.setReceive_Dt(OperationUtillity.NullReplace(rs.getString("REVIEW_DATE")));
//					lodgeCollateralReqDtls.setFromDeriveVal(OperationUtillity.NullReplace(rs.getString("FromDeriveVal")));
					lodgeCollateralReqDtls.setValue(OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
					lodgeCollateralReqDtls.setCollateral_Value(OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
					lodgeCollateralReqDtls.setSecurity_Id(OperationUtillity.NullReplace(rs.getString("SECURITY_PROVIDER_ID")));
					lodgeCollateralReqDtls.setSecurity_Name(OperationUtillity.NullReplace(rs.getString("SECURITY_NAME")));
					lodgeCollateralReqDtls.setSecurity_Type(OperationUtillity.NullReplace(rs.getString("SECURITY")));
					lodgeCollateralReqDtls.setUcc_Based_CustId(OperationUtillity.NullReplace(uccBasedCustId));

					// SVT List on the basis of security ID
					List<svtSecurityDetails> svtSecurityDtlsList = new ArrayList<svtSecurityDetails>();
					svtSecurityDtlsList.addAll(
							fetchSubTypeSecurityList(pinstId, lodgeCollateralReqDtls, SubTypeSecurityData, con));

					// List of Policy Details for MFU, LIC
					List<policySecurityDetails> polcSecDtlsList = new ArrayList<policySecurityDetails>();
					polcSecDtlsList.addAll(fetchPolicyDetails(pinstId, lodgeCollateralReqDtls, queryTofetchPolicyDtls, con));

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
		logger.info("lodgeCollateralDaoImpl.getSubTypeSecurityList()-->pinstid->"+pinstId+"\n"+SubTypeSecurityList);
		
		return SubTypeSecurityList;

	}
	
	public List<svtSecurityDetails> fetchSubTypeSecurityList(String pinstId,
			lodgeCollateralRequestPojo lodgeCollateralReqDtls, String SubTypeSecurityData, Connection con){
		List<svtSecurityDetails> svtSecurityDtlsList = new ArrayList<svtSecurityDetails>();
//		String SubTypeSecurityData = "SELECT * FROM (SELECT  DISTINCT QUESTION, ANSWER FROM lsm_security_answers WHERE pinstid = ? AND security_provider_id = ? and QUESTION in ('Sub Type Security','Type Of Security (SVT)')) PIVOT(MAX(ANSWER) FOR QUESTION in ('Sub Type Security' as Sub_Type_Security,'Type Of Security (SVT)' AS Type_Of_Security_SVT))";
		try (PreparedStatement pstSvt = con.prepareStatement(SubTypeSecurityData);) {
			pstSvt.setString(1, pinstId);
			pstSvt.setString(2, lodgeCollateralReqDtls.getSecurity_Id());
			try (ResultSet rsSvt = pstSvt.executeQuery()) {

				while (rsSvt.next()) {
					svtSecurityDetails svtSecurityDtls = new svtSecurityDetails();

					svtSecurityDtls.setSub_Type_Security(OperationUtillity.NullReplace(rsSvt.getString("SUB_TYPE_SECURITY")));
					svtSecurityDtls.setType_Of_Security(OperationUtillity.NullReplace(rsSvt.getString("TYPE_OF_SECURITY_SVT")));
					svtSecurityDtlsList.add(svtSecurityDtls);
				}
				lodgeCollateralReqDtls.setSvtDtls(svtSecurityDtlsList);
			}
		}catch(Exception e) {
			logger.info("lodgeCollateralDaoImpl.fetchSubTypeSecurityList()"+OperationUtillity.traceException(e));
		}
		return svtSecurityDtlsList;
	}
	
	public List<policySecurityDetails> fetchPolicyDetails(String pinstId,
			lodgeCollateralRequestPojo lodgeCollateralReqDtls, String queryTofetchPolicyDtls, Connection con){
		List<policySecurityDetails> polcSecDtlsList = new ArrayList<policySecurityDetails>();
		try (PreparedStatement pstPlDtls = con.prepareStatement(queryTofetchPolicyDtls);) {
			pstPlDtls.setString(1, pinstId);
			pstPlDtls.setString(2, lodgeCollateralReqDtls.getSecurity_Id());
			try (ResultSet PlDtls = pstPlDtls.executeQuery()) {

				while (PlDtls.next()) {
					policySecurityDetails polcSecDtls = new policySecurityDetails();

					polcSecDtls.setPolicy_No(OperationUtillity.NullReplace(PlDtls.getString("POLICY_NO")));
					polcSecDtls.setPolicy_Amount(OperationUtillity.NullReplace(PlDtls.getString("POLICY_AMOUNT")));
					polcSecDtlsList.add(polcSecDtls);
				}
				lodgeCollateralReqDtls.setPolicySecurityDtls(polcSecDtlsList);
			}
		}catch(Exception e) {
			logger.info("lodgeCollateralDaoImpl.fetchPolicyDetails()"+OperationUtillity.traceException(e));
		}
		return polcSecDtlsList;
	}

	public lodgeCollateralRequestPojo getSubTypeSecurityData(String pinstid, String securityName, String subTypeSecurity, String typeOfSvt,String processName) throws SQLException, Exception {

		lodgeCollateralRequestPojo lodgeCollateralReqDtls = new lodgeCollateralRequestPojo();
		String uccBasedCustId = svtFIServiceUtility.getUCCBasedCustId(pinstid);
		String lsmNumber = svtFIServiceUtility.getSVTCommonData(pinstid).get("LSM_NUMBER");
//		List<Map<String, String>> securityWiseOtherDetailsListofMap = svtFIServiceUtility.getSecurityWiseOtherDetails(pinstId);

		try (Connection con = DBConnect.getConnection()) {
			String securityDetailsQuery = "";
			String SubTypeSecurityData = "";
			String queryTofetchPolicyDtls = "";
			switch (processName) {
			case "Monitoring":
				securityDetailsQuery = "SELECT * FROM (SELECT SECURITY.QUESTION_ID AS SECURITY_QUESTION, SECURITY.ANSWER AS SECURITY_ANSWER, SECURITY.SECURITY_PROVIDER_NAME AS SECURITY_NAME,SECURITY.SECURITY_PROVIDER_ID AS SECURITY_PROVIDER_ID FROM LSM_SECU_MONTY_ANS SECURITY WHERE SECURITY.PINSTID = ? AND SECURITY.SECURITY_PROVIDER_NAME = ?  AND SECURITY.QUESTION_ID IN ('76','78','79','87','91','92','93','94','186','189','190','191','192','193','194','195','196','197','84','186'))  PIVOT ( MAX (SECURITY_ANSWER)  FOR SECURITY_QUESTION  IN ('76' AS SECURITY,'78' AS OWNERSHIP_STATUS,'79' AS VALUATION_DATE,'87' AS SECURITY_CREATED ,'91' AS SECURITY_CREATED_DATE,'92' AS SECURITY_SUBMISSION_DATE, '93' AS APPORTIONED_VALUE, '94' AS SECURITY_PROVIDER_NAME, '186' AS REVIEW_DATE,'189' AS ADDRESS_LINE,'190' AS AREA,'191' AS ROAD, '192' AS LANDMARK,'193' AS PINCODE,'194' AS DISTRICT ,'195' AS CITY,'196' AS STATE,'197' AS COUNTRY , '84' AS SECURITY_DUE_DATE, '186' AS SECURITY_REVIEW_DATE )) ORDER BY SECURITY_NAME";
				SubTypeSecurityData = "SELECT * FROM (SELECT DISTINCT  A.ANSWER AS SUB_TYPE_SECURITY, B.ANSWER AS TYPE_OF_SECURITY_SVT FROM LSM_SECU_MONTY_ANS A JOIN LSM_SECU_MONTY_ANS B ON A.PINSTID = B.PINSTID AND A.SECURITY_PROVIDER_ID=B.SECURITY_PROVIDER_ID AND SUBSTR(A.QUESTION_ID,INSTR(A.QUESTION_ID,'_') + 1) = SUBSTR(B.QUESTION_ID,INSTR(B.QUESTION_ID,'_') + 1) WHERE A.PINSTID = ? AND A.SECURITY_PROVIDER_ID = ? AND A.QUESTION = 'Sub Type Security'   AND B.QUESTION = 'Type Of Security (SVT)') ";
				queryTofetchPolicyDtls = "SELECT * FROM (SELECT DISTINCT  A.ANSWER AS POLICY_NO, B.ANSWER AS POLICY_AMOUNT FROM LSM_SECU_MONTY_ANS A JOIN LSM_SECU_MONTY_ANS B ON A.PINSTID = B.PINSTID AND A.SECURITY_PROVIDER_ID = B.SECURITY_PROVIDER_ID AND SUBSTR(A.QUESTION_ID,INSTR(A.QUESTION_ID,'_') + 1) = SUBSTR(B.QUESTION_ID,INSTR(B.QUESTION_ID,'_') + 1) WHERE A.PINSTID = ? AND A.SECURITY_PROVIDER_ID = ? AND A.QUESTION_ID LIKE '184%' AND B.QUESTION_ID  LIKE '185%')";
				break;
			case "Limit_Setup":
				securityDetailsQuery = "SELECT * FROM (SELECT SECURITY.QUESTION_ID AS SECURITY_QUESTION, SECURITY.ANSWER AS SECURITY_ANSWER, SECURITY.SECURITY_PROVIDER_NAME AS SECURITY_NAME,SECURITY.SECURITY_PROVIDER_ID AS SECURITY_PROVIDER_ID FROM LSM_SECURITY_ANSWERS SECURITY WHERE SECURITY.PINSTID = ? AND SECURITY.SECURITY_PROVIDER_NAME = ?  AND SECURITY.QUESTION_ID IN ('76','78','79','87','91','92','93','94','186','189','190','191','192','193','194','195','196','197','84','186'))  PIVOT ( MAX (SECURITY_ANSWER)  FOR SECURITY_QUESTION  IN ('76' AS SECURITY,'78' AS OWNERSHIP_STATUS,'79' AS VALUATION_DATE,'87' AS SECURITY_CREATED ,'91' AS SECURITY_CREATED_DATE,'92' AS SECURITY_SUBMISSION_DATE, '93' AS APPORTIONED_VALUE, '94' AS SECURITY_PROVIDER_NAME, '186' AS REVIEW_DATE,'189' AS ADDRESS_LINE,'190' AS AREA,'191' AS ROAD, '192' AS LANDMARK,'193' AS PINCODE,'194' AS DISTRICT ,'195' AS CITY,'196' AS STATE,'197' AS COUNTRY , '84' AS SECURITY_DUE_DATE, '186' AS SECURITY_REVIEW_DATE )) ORDER BY SECURITY_NAME";
				SubTypeSecurityData = "SELECT * FROM (SELECT DISTINCT  A.ANSWER AS SUB_TYPE_SECURITY, B.ANSWER AS TYPE_OF_SECURITY_SVT FROM LSM_SECURITY_ANSWERS A JOIN LSM_SECURITY_ANSWERS B ON A.PINSTID = B.PINSTID AND A.SECURITY_PROVIDER_ID=B.SECURITY_PROVIDER_ID AND SUBSTR(A.QUESTION_ID,INSTR(A.QUESTION_ID,'_') + 1) = SUBSTR(B.QUESTION_ID,INSTR(B.QUESTION_ID,'_') + 1) WHERE A.PINSTID = ? AND A.SECURITY_PROVIDER_ID = ? AND A.QUESTION = 'Sub Type Security'   AND B.QUESTION = 'Type Of Security (SVT)') ";
				queryTofetchPolicyDtls = "SELECT * FROM (SELECT DISTINCT  A.ANSWER AS POLICY_NO, B.ANSWER AS POLICY_AMOUNT FROM LSM_SECURITY_ANSWERS A JOIN LSM_SECURITY_ANSWERS B ON A.PINSTID = B.PINSTID AND A.SECURITY_PROVIDER_ID = B.SECURITY_PROVIDER_ID AND SUBSTR(A.QUESTION_ID,INSTR(A.QUESTION_ID,'_') + 1) = SUBSTR(B.QUESTION_ID,INSTR(B.QUESTION_ID,'_') + 1) WHERE A.PINSTID = ? AND A.SECURITY_PROVIDER_ID = ? AND A.QUESTION_ID LIKE '184%' AND B.QUESTION_ID  LIKE '185%') ";
				break;
			}

			PreparedStatement pst = con.prepareStatement(securityDetailsQuery);
			pst.setString(1, pinstid);
			pst.setString(2, securityName);
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					
					lodgeCollateralReqDtls.setCeiling_Limit(OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
					String ownershipStatus = OperationUtillity.NullReplace(rs.getString("OWNERSHIP_STATUS"));
					lodgeCollateralReqDtls.setCollateral_Class(svtFIServiceUtility.fetchCollateralClass(ownershipStatus.toUpperCase()));
				
					lodgeCollateralReqDtls.setGross_Val(OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
					lodgeCollateralReqDtls.setDue_Dt(OperationUtillity.NullReplace(rs.getString("SECURITY_CREATED_DATE")));
					lodgeCollateralReqDtls.setSecurity_Created(OperationUtillity.NullReplace(rs.getString("SECURITY_CREATED")));
					lodgeCollateralReqDtls.setLast_Val_Date(OperationUtillity.NullReplace(rs.getString("VALUATION_DATE")));
//					lodgeCollateralReqDtls.setNatureOfCharge(OperationUtillity.NullReplace(rs.getString("NatureOfCharge")));
					lodgeCollateralReqDtls.setNotes(OperationUtillity.NullReplace(lsmNumber));
					lodgeCollateralReqDtls.setNotes1(OperationUtillity.NullReplace(lsmNumber));
					lodgeCollateralReqDtls.setNotes2(OperationUtillity.NullReplace(lsmNumber));
					lodgeCollateralReqDtls.setReview_Dt(OperationUtillity.NullReplace(rs.getString("REVIEW_DATE")));
					lodgeCollateralReqDtls.setReceive_Dt(OperationUtillity.NullReplace(rs.getString("REVIEW_DATE")));
//					lodgeCollateralReqDtls.setFromDeriveVal(OperationUtillity.NullReplace(rs.getString("FromDeriveVal")));
					lodgeCollateralReqDtls.setValue(OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
					lodgeCollateralReqDtls.setCollateral_Value(OperationUtillity.NullReplace(rs.getString("APPORTIONED_VALUE")));
					lodgeCollateralReqDtls.setSecurity_Id(OperationUtillity.NullReplace(rs.getString("SECURITY_PROVIDER_ID")));
					lodgeCollateralReqDtls.setSecurity_Name(OperationUtillity.NullReplace(rs.getString("SECURITY_NAME")));
					lodgeCollateralReqDtls.setSecurity_Type(OperationUtillity.NullReplace(rs.getString("SECURITY")));
					lodgeCollateralReqDtls.setUcc_Based_CustId(OperationUtillity.NullReplace(uccBasedCustId));

					// SVT List on the basis of security ID
					List<svtSecurityDetails> svtSecurityDtlsList = new ArrayList<svtSecurityDetails>();
					svtSecurityDtlsList.addAll(
							fetchSubTypeSecurityList(pinstid, lodgeCollateralReqDtls, SubTypeSecurityData, con));

					// List of Policy Details for MFU, LIC
					List<policySecurityDetails> polcSecDtlsList = new ArrayList<policySecurityDetails>();
					polcSecDtlsList
							.addAll(fetchPolicyDetails(pinstid, lodgeCollateralReqDtls, queryTofetchPolicyDtls, con));
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
		try (Connection con = DBConnect.getConnection()) {
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
					"\nException.lodgeCollateralDaoImpl.getSubTypeSecurityList().getCollateralCode().subTypeSecuritySvt ::\n"
							+ OperationUtillity.traceException(subTypeSecuritySvt, ex));
		}
		return collateralCode;
	}
	
	public void getNatureOfChargeAndDerivedVal(String pinstId, String security,
			lodgeCollateralRequestPojo lodgeCollateralReqND) {

		String queryForCollateralCode = "SELECT  A.SECURITY_DETAILS, A.TYPE_SEC_CREATION,A.SEELING_LIMIT,B.REF_DESC, B.REF_CODE,  C.ICORE_CODE AS DERIVED_VALUE FROM CAL_SECURITY_DETAILS_SECURED A  JOIN LSM_MS_NATURE_OF_CHARGE B ON UPPER(B.REF_DESC) = UPPER(A.TYPE_SEC_CREATION)  JOIN LSM_MS_DERIVED_VALUE C  ON UPPER(C.SCREEN_LEVEL_DROPDOWN) = UPPER(A.DERIVED_VALUE) WHERE PINSTID =( SELECT DISHUBID FROM LIMIT_SETUP_EXT WHERE PINSTID= ? ) AND A.SECURITY_DETAILS = ? ";
		try (Connection con = DBConnect.getConnection()) {
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

	public boolean checkServiceStatus(String pinstId, svtSecurityDetails svtSecDtls,
			lodgeCollateralRequestPojo lodgeCollateralSec) {
		boolean status = true;
		try (Connection con = DBConnect.getConnection()) {
			String fiServiceStatusQuery = "SELECT DISTINCT(STATUS) FROM LSM_SERVICE_LATEST_REQ_RES WHERE PINSTID=? AND REQUESTTYPE=? AND UPPER(STATUS)=? ";
			String requestType = "LODGE COLLATERAL : "
					+ OperationUtillity.NullReplace(lodgeCollateralSec.getSecurity_Name()) + " : "
					+ OperationUtillity.NullReplace(svtSecDtls.getSub_Type_Security()) + " : "
					+ OperationUtillity.NullReplace(svtSecDtls.getType_Of_Security());

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
			logger.info("\nException.lodgeCollateralDaoImpl.getSubTypeSecurityList().checkServiceStatus() ::\n"
					+ OperationUtillity.traceException(pinstId, ex));
		}
		return status;
	}
}
