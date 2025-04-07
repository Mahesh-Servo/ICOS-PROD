package com.svt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.svt.model.commonModel.serviceDetails.ServiceDetails;
import com.svt.utils.common.OperationUtillity;
import com.svt.utils.common.updateServiceDetails;
import com.svt.utils.dataConnectivity.dbConnection;

@Repository
public class InquiryDao {

	private static final Logger logger = LoggerFactory.getLogger(InquiryDao.class);
	
	@Autowired
	updateServiceDetails updtServiceDetails;

	public List<Map<String, String>> checkInquiryDetailsInMaster(String pinstid, String securityName,
			String subTypeSecurity, String typeOfSvt, String Product, String limitPrefix, String limitSuffix,
			String processName) {

		List<Map<String, String>> list = new ArrayList<>();
		Boolean flag = false;
		
		String  requestType = "SVT INQUIRY : " + OperationUtillity.NullReplace(securityName) + " : "
					+ subTypeSecurity + " : " + typeOfSvt + " : " + Product;
		
		try (Connection con = dbConnection.getConnection()) {
			try (PreparedStatement pstSvt = con
					.prepareStatement("SELECT SEC_CODE AS COLLATERAL_CODE,SEC_SRL_NUM AS COLLATERAL_ID FROM"
							+ " SVT_DELINK_MS where CHILDLIMITID = ? ");) { // SEC_TYPE = ? and SECDESC = ? and

//				pstSvt.setString(1, subTypeSecurity); // 'BOOK DEBTS'
//				pstSvt.setString(2, typeOfSvt); // 'CURRENT ASSET RECEIVABLES
				pstSvt.setString(1, limitPrefix.trim() + "/" + limitSuffix.trim()); // 'MAHAVIRSPIN/LC

				try (ResultSet rsSvt = pstSvt.executeQuery()) {

					while (rsSvt.next()) {
						Map<String, String> resultMap = new HashMap<>();
						flag = true;
						resultMap.put("COLLATERAL_CODE", rsSvt.getString("COLLATERAL_CODE"));
						resultMap.put("COLLATERAL_ID", rsSvt.getString("COLLATERAL_ID"));

						resultMap.put("SVT INQUIRY : " + securityName + " : " + subTypeSecurity + " : " + typeOfSvt
								+ " : " + Product, "SUCCESS");
						resultMap.put("SVT INQUIRY : " + securityName + " : " + subTypeSecurity + " : " + typeOfSvt
								+ " : " + Product + " : STATUS", "SUCCESS");
						list.add(resultMap);
						
						updtServiceDetails.updateInitialStatusInFiExecutionTable(
								new ServiceDetails(pinstid, requestType, "SVT INQUIRY", requestType, "SUCCESS", "",
										"", "SUCCESS", false));
					}

					if (!flag) {
						Map<String, String> resultMap = new HashMap<>();
						resultMap.put("SVT INQUIRY : " + securityName + " : " + subTypeSecurity + " : " + typeOfSvt
								+ " : " + Product, "FAILED");
						resultMap.put("SVT INQUIRY : " + securityName + " : " + subTypeSecurity + " : " + typeOfSvt
								+ " : " + Product + " : STATUS", "FAILED");
						list.add(resultMap);
						
						updtServiceDetails.updateInitialStatusInFiExecutionTable(
								new ServiceDetails(pinstid, requestType, "SVT INQUIRY", requestType, "FAILED", "",
										"", "Collateral ID Not Found In Master", false));
					}
				}
			}
		} catch (Exception e) {
			logger.info("\nInquiryDao.checkInquiryDetailsInMaster()" + OperationUtillity.traceException(
					subTypeSecurity + ":" + typeOfSvt + ":" + limitPrefix.trim() + "/" + limitSuffix.trim(), e));
			updtServiceDetails.updateInitialStatusInFiExecutionTable(
					new ServiceDetails(pinstid, requestType, "SVT INQUIRY", requestType, "FAILED", "",
							"", "Some error occurred while Inquiry", false));
		}
		return list;
	}
	
	public List<Map<String, String>> checkInquiryDetailsInMasterNew(String pinstid, String processName, String[] pref_suff,String facilityname) {
	    logger.info("SvtCommonDao.checkInquiryDetailsInMaster().pinstid()" + pinstid+ "pref ["+pref_suff[0]+"] suff ["+pref_suff[1]+"]" );

		List<Map<String, String>> list = new ArrayList<>();
		Boolean flag = false;
		try (Connection con = dbConnection.getConnection()) {
			try (PreparedStatement pstSvt = con
					.prepareStatement("SELECT SEC_TYPE AS SEC_TYPE,SEC_CODE AS SEC_CODE,SEC_SRL_NUM AS COLLATERAL_ID FROM"
							+ " SVT_DELINK_MS where CHILDLIMITID = ? ");) { // SEC_TYPE = ? and SECDESC = ? and
				pstSvt.setString(1, pref_suff[0].trim() + "/" + pref_suff[1].trim()); // 'MAHAVIRSPIN/LC

				try (ResultSet rsSvt = pstSvt.executeQuery()) {

					while (rsSvt.next()) {
						Map<String, String> resultMap = new HashMap<>();
						flag = true;
						resultMap.put("SEC_TYPE", rsSvt.getString("SEC_TYPE"));
						resultMap.put("SEC_CODE", rsSvt.getString("SEC_CODE"));
						resultMap.put("COLLATERAL_ID", rsSvt.getString("COLLATERAL_ID"));
						resultMap.put("SVT INQUIRY", "SUCCESS");
						list.add(resultMap);
					}

					if (!flag) {
						Map<String, String> resultMap = new HashMap<>();
						resultMap.put("SVT INQUIRY" , "FAILED");
						list.add(resultMap);
						updtServiceDetails.updateInitialStatusInFiExecutionTable(
								new ServiceDetails(pinstid, "SVT INQUIRY : "+ facilityname +" : " + pref_suff[0].trim() + "/" + pref_suff[1].trim(), "SVT INQUIRY", "SVT INQUIRY : "+facilityname+ " : " + pref_suff[0].trim() + "/" + pref_suff[1].trim(), "FAILED", "",
										"", "Collateral ID Not Found In Master", false));
					} else {
						updtServiceDetails.updateInitialStatusInFiExecutionTable(
								new ServiceDetails(pinstid, "SVT INQUIRY : "+facilityname+ " : "+ pref_suff[0].trim() + "/" + pref_suff[1].trim(), "SVT INQUIRY", "SVT INQUIRY : "+facilityname+ " : " + pref_suff[0].trim() + "/" + pref_suff[1].trim(), "SUCCESS", "",
										"", "SUCCESS", false));
					}
				}
			}
		} catch (Exception e) {
			logger.info("\nSvtCommonDao.checkInquiryDetailsInMaster()" + OperationUtillity.traceException( "Prefix_suff " + pref_suff[0].trim() + "/" + pref_suff[1].trim(), e));
			updtServiceDetails.updateInitialStatusInFiExecutionTable(
					new ServiceDetails(pinstid, "SVT INQUIRY", "SVT INQUIRY", "SVT INQUIRY", "FAILED", "",
							"", "Some error occurred while Inquiry", false));
		}
		return list;
	}

}
