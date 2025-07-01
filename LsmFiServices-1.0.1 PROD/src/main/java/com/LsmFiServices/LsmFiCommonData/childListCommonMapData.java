package com.LsmFiServices.LsmFiCommonData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.LsmFiServices.Utility.DBConnect;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.ReadPropertyFIle;
import com.LsmFiServices.Utility.commonUtility;

@Component
public class childListCommonMapData {
	private static final Logger logger = LoggerFactory.getLogger(childListCommonMapData.class);

	public static String getCustid(String pinstid) {
		String CUST_ID = "";
		String custIdQuery = ReadPropertyFIle.getInstance().getPropConst().getProperty("cifId");
		try (Connection con = DBConnect.getConnection(); PreparedStatement stmt = con.prepareStatement(custIdQuery);) {
			stmt.setString(1, pinstid);
			try (ResultSet rset = stmt.executeQuery();) {
				while (rset.next()) {
					CUST_ID = OperationUtillity.NullReplace(rset.getString("ANSWER"));
				}
			}
		} catch (Exception ex) {
			logger.info("childListCommonMapData.getCustid() exception -------->" + OperationUtillity.traceException(ex));
		}
		return CUST_ID;
	}

	public static String getCompanyName(String pinstid) {
		String COMPANYNAME = "";
		String PARENT_LIMIT_DESC_QUERY = ReadPropertyFIle.getInstance().getPropConst().getProperty("PARENT_LIMIT_DESC");
		try (Connection con = DBConnect.getConnection();
				PreparedStatement stmt = con.prepareStatement(PARENT_LIMIT_DESC_QUERY);) {
			stmt.setString(1, pinstid);
			try (ResultSet rset = stmt.executeQuery();) {
				while (rset.next()) {
					COMPANYNAME = OperationUtillity.NullReplace(rset.getString("COMPANYNAME")).replaceAll("[^a-zA-Z0-9 ]", "");
					if (COMPANYNAME.length() > 25) {
						//COMPANYNAME = COMPANYNAME.substring(0, 24);
						COMPANYNAME = COMPANYNAME.substring(0, 19);
					}
				}
			}
		} catch (Exception ex) {
			logger.info("childListCommonMapData.getCompanyName().Exception :: "+pinstid+" :: "+OperationUtillity.traceException(ex));
		}
		return COMPANYNAME;
	}

	public static Map<String, String> getParentFacilityDataMap(String pinstid) {
		Map<String, String> ParentFacilityDataMap = new LinkedHashMap<>();
			String PARENT_LIMIT_EXP_DATE = null;
			String PARENT_LIMIT_SANCTION_DATE = null;
			String PARENT_LIMIT_SANCTION_AMOUNT = null;
			String TOTAL_FB_NONFB = null;
			String SANCTION_REFERENCE_NUMBER = null;

		String lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("PARENT_LIMIT_DATA_FACILITY");
		try (Connection con = DBConnect.getConnection(); PreparedStatement stmt = con.prepareStatement(lsql);) {
			stmt.setString(1, pinstid);
			stmt.setString(2, "Parent Limit");

			try (ResultSet rset = stmt.executeQuery();) {
					while (rset.next()) {
						PARENT_LIMIT_EXP_DATE = OperationUtillity.NullReplace(rset.getString("PARENT_LIMIT_EXP_DATE"));
						PARENT_LIMIT_SANCTION_DATE = OperationUtillity.NullReplace(rset.getString("PARENT_LIMIT_SANCTION_DATE"));
						PARENT_LIMIT_SANCTION_AMOUNT = commonUtility.millionString(OperationUtillity.NullReplace(rset.getString("PARENT_LIMIT_SANCTION_AMOUNT")));
						TOTAL_FB_NONFB = commonUtility.millionString(OperationUtillity.NullReplace(rset.getString("TOTAL_FB_NONFB")));
						SANCTION_REFERENCE_NUMBER = OperationUtillity.NullReplace(rset.getString("SANCTION_REFERENCE_NUMBER"));
					}
					ParentFacilityDataMap.put("SANCTION_REFERENCE_NUMBER",OperationUtillity.NullReplace(SANCTION_REFERENCE_NUMBER));
					ParentFacilityDataMap.put("PARENT_LIMIT_EXP_DATE",OperationUtillity.NullReplace(PARENT_LIMIT_EXP_DATE));
					ParentFacilityDataMap.put("PARENT_LIMIT_SANCTION_DATE",OperationUtillity.NullReplace(PARENT_LIMIT_SANCTION_DATE));
					ParentFacilityDataMap.put("PARENT_LIMIT_SANCTION_AMOUNT",OperationUtillity.NullReplace(PARENT_LIMIT_SANCTION_AMOUNT));
					ParentFacilityDataMap.put("TOTAL_FB_NONFB", OperationUtillity.NullReplace(TOTAL_FB_NONFB));
				}
		} catch (Exception ex) {
			logger.info("childListCommonMapData.getParentFacilityDataMap() Exception for " + pinstid + "Exception ::"+OperationUtillity.traceException(ex));
		}
		return ParentFacilityDataMap;
	}

	public static Map<String, String> getHomeTabData(String pinstid) {
		Map<String, String> HomeTabDataMap = new LinkedHashMap<>();

			String PARENT_LIMIT_PREFIX = null;
			String PARENT_LIMIT_SUFFIX = null;
			String LSM_NO = null;
			String LOAN_STATUS = null;
			String NAME_OF_BANK_ONE = null;
			String PROGRAM_CODE = null;

			String lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("FACILITY_COMMON_DATA");
			try (Connection con = DBConnect.getConnection();
					PreparedStatement tmts = con.prepareStatement(lsql);){
			tmts.setString(1, pinstid);
			try(ResultSet sr = tmts.executeQuery()){;
			while (sr.next()) {
				PARENT_LIMIT_PREFIX = OperationUtillity.NullReplace(sr.getString("PARENT_LIMIT_PREFIX"));
				PARENT_LIMIT_SUFFIX = OperationUtillity.NullReplace(sr.getString("PARENT_LIMIT_SUFFIX"));
				LSM_NO = OperationUtillity.NullReplace(sr.getString("LSM_NO"));
				LOAN_STATUS = OperationUtillity.NullReplace(sr.getString("LOAN_STATUS"));
				NAME_OF_BANK_ONE = OperationUtillity.NullReplace(sr.getString("NAME_OF_BANK_ONE"));
				PROGRAM_CODE = OperationUtillity.NullReplace(sr.getString("PROGRAM_CODE"));

				if (NAME_OF_BANK_ONE != "") {
					NAME_OF_BANK_ONE = NAME_OF_BANK_ONE.substring(1);
					if (NAME_OF_BANK_ONE.contains(",")) {
						NAME_OF_BANK_ONE = NAME_OF_BANK_ONE.substring(0, NAME_OF_BANK_ONE.indexOf(","));
					}
				}

				if (LOAN_STATUS.equalsIgnoreCase("Sanctioned by the bank originally")) {
					LOAN_STATUS = "SAN";
				} else if (LOAN_STATUS.equalsIgnoreCase("Buyout from NBFC/HFC")) {
					LOAN_STATUS = "BUY";
				} else if (LOAN_STATUS.equalsIgnoreCase("Takeover from another bank")) {
					LOAN_STATUS = "BNK";
				} else if (LOAN_STATUS.equalsIgnoreCase("Takeover from NBFC/HFC")) {
					LOAN_STATUS = "NBH";
				}
			}
		}
			HomeTabDataMap.put("PARENT_LIMIT_PREFIX", OperationUtillity.NullReplace(PARENT_LIMIT_PREFIX));
			HomeTabDataMap.put("PARENT_LIMIT_SUFFIX", OperationUtillity.NullReplace(PARENT_LIMIT_SUFFIX));
			HomeTabDataMap.put("LSM_NO", OperationUtillity.NullReplace(LSM_NO));
			HomeTabDataMap.put("NAME_OF_BANK_ONE", OperationUtillity.NullReplace(NAME_OF_BANK_ONE));
			HomeTabDataMap.put("LOAN_STATUS", OperationUtillity.NullReplace(LOAN_STATUS));
			HomeTabDataMap.put("PROGRAM_CODE", OperationUtillity.NullReplace(PROGRAM_CODE));
		} catch (Exception ex) {
			logger.info("childListCommonMapData.getHomeTabData().Exception :: "+pinstid+" :: "+OperationUtillity.traceException(ex));
		}
		return HomeTabDataMap;
	}	
}
