package com.example.masIntg.DBFetch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.example.masIntg.entity.Memo_Entity;
import com.example.masIntg.service.Mas_Service;
import com.example.masIntg.utility.DBConnect;

@Repository
public class ProgramName {

	private static final Logger logger = LoggerFactory.getLogger(ProgramName.class);

	PreparedStatement statement = null;
	ResultSet rs = null;

	public String GetProgramName(String pinstid) {
		String programname = "";
		String lsql = "SELECT PROGRAM FROM UNICORE_EXT WHERE PINSTID =  ? ";
		try (Connection con = DBConnect.getConnection();) {
			logger.info("query for GetProgramName ---> " + lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			while (rs.next()) {
				programname = rs.getString("PROGRAM");
				logger.info("GetProgramName ---> " + programname);

			}
		} catch (Exception ex) {
			logger.info(
					"Exception in MAS_APPLN inside Class ProgramName ---method GetProgramName ---> " + ex.getMessage());
		}
		return programname;
	}


	// onboarding get pinstid
	public Map<String, String> GetActiveTab(String empID) {
		String pinstid = "";
		String gettab = "";
		List<String> list = new ArrayList<>();
		Map<String, String> map = new HashMap<String, String>();
		try (Connection con = DBConnect.getConnection();) {

			String sqlque = "SELECT a.PINSTID,a.ACTIVE_TAB FROM ICOS_CM_ONBOARD_DETAILS a,ICOS_BASIC_DETAILS b WHERE a.PINSTID = b.PINSTID AND b.RHS_ID=?";
			logger.info("SELECT query for ONBOARDING API pinstid ---> " + sqlque);
			statement = con.prepareStatement(sqlque);
			statement.setString(1, empID);
			rs = statement.executeQuery();
			while (rs.next()) {
				map.put(NullReplace(rs.getString("PINSTID")),NullReplace(rs.getString("ACTIVE_TAB")));
			}
			logger.info("pinstid FOR  ONBOARDING inside programName--> map" + map.toString());
		} catch (Exception ex) {
			logger.info(
					"Exception in MAS_APPLN inside Class ProgramName ---method GetActiveTab ---> " + ex.getMessage());
		}
		return map;
	}
	
	public String GetActiveTabForUpdate(String empID,String pinstid) {
		String gettab = "";
		List<String> list = new ArrayList<>();
		Map<String, String> map = new HashMap<String, String>();
		try (Connection con = DBConnect.getConnection();) {

			String sqlque = "SELECT ACTIVE_TAB FROM ICOS_CM_ONBOARD_DETAILS  WHERE PINSTID = ?";
			logger.info("SELECT query for GetActiveTabForUpdate  pinstid ---> " + sqlque);
			statement = con.prepareStatement(sqlque);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			while (rs.next()) {
				gettab = NullReplace(rs.getString("ACTIVE_TAB"));
			}
			logger.info("pinstid FOR  ONBOARDING inside programName--> map" + gettab);
		} catch (Exception ex) {
			logger.info(
					"Exception in MAS_APPLN inside Class ProgramName ---method GetActiveTab ---> " + ex.getMessage());
		}
		return gettab;
	}

	public String insertToRHSAudit(String pinstid, String ID, String status, String tab) {
		String rmname = "";
		String rmid = "";
		String tyop = "";
		String lsql = "";
		try (Connection con = DBConnect.getConnection();) {
			lsql = "SELECT CASEINITIATOR,CASEINITIATOR_NTUSERNAME,TYPE_OF_PROGRAM FROM ICOS_CM_ONBOARD_DETAILS WHERE PINSTID = ?";
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			while (rs.next()) {
				rmid = rs.getString("CASEINITIATOR");
				rmname = rs.getString("CASEINITIATOR_NTUSERNAME");
				tyop = rs.getString("TYPE_OF_PROGRAM");
				logger.info("pinstid FOR  ONBOARDING inside programName-->" + pinstid);
			}

		} catch (Exception ex) {
			logger.info("ProgramName.insertToRHSAudit().Exception\n" + ex.getMessage());

		}

		try (Connection con = DBConnect.getConnection();) {
			final String insertTxnIdQuery = "INSERT INTO ICOS_ON_CUST_RHS_AUDIT (PINSTID,RM_ID,RM_NAME,PROGRAM_NAME,ACTIVE_TAB,USERNAME,RHS_ID,APPROVAL_TO_BE_TAKEN,APPROVAL_GIVEN_BY,STATUS_OF_CONSENT,OPERATIONDATETIME) VALUES (?,?,?,?,?,?,?,?,?,?,SYSDATE)";
			PreparedStatement pst = con.prepareStatement(insertTxnIdQuery);
			pst.setString(1, pinstid);
			pst.setString(2, rmid);
			pst.setString(3, rmname);
			pst.setString(4, tyop);
			pst.setString(5, tab);
			pst.setString(6, ID);
			pst.setString(7, ID);
			pst.setString(8, "Offline");
			pst.setString(9, ID);
			pst.setString(10, status);
			pst.execute();
			logger.info("RHS DETAILS  inserted Succesfully");
			if (pst != null) {
				pst.close();
			}
			if (con != null) {
				con.close();
			}
		} catch (Exception e) {
			logger.info("ProgramName.insertToRHSAudit().Exception\n" + e.getMessage());
		}

		return "RHS DETAILS  inserted Succesfully";
	}
	
	public static String NullReplace(String value) {
		try {
			if (value == "null" || value == null) {
				value = "";
			}
		} catch (Exception ex) {
			value = "0";
			ex.printStackTrace();
			logger.info("Error occurred NullReplace " + ex.getMessage());
		}
		return value;
	}
}
