package com.LsmFiServices.Utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class OperationUtillity {

	private static final Logger logger = LoggerFactory.getLogger(OperationUtillity.class);

//	public Map<String, String> GetExtData(String pinstid) throws SQLException {
//		Map<String, String> EXTData = new HashMap<>();
//
//		String SL_Limit = null;
//		String SL_docDate = null;
//		String SL_expDate = null;
//		String SL_RefNo = null;
//		String lsql = null;
//		String ACCOUNT_NO = null;
//		try {
//
//			con = DBConnect.getConnection();
//			if (con == null) {
//				EXTData.put("Result", "Fail");
//				EXTData.put("Message", "DB connection not established");
//				return EXTData;
//			} else {
//
//				// start query
//				lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("ACCOUNT_NO_QUERY");
//				statement = con.prepareStatement(lsql);
//				statement.setString(1, pinstid);
//				rs = statement.executeQuery();
//
//				while (rs.next()) {
//					ACCOUNT_NO = NullReplace(rs.getString("ANSWER"));
//					EXTData.put("ACCOUNT_NO", ACCOUNT_NO);
//				}
//
//				if (statement != null) {
//					statement.close();
//				}
//
//				if (rs != null) {
//					rs.close();
//				}
//				// end
//
//				lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("SL_SancLimit");
//				statement = con.prepareStatement(lsql);
//				statement.setString(1, pinstid);
//				rs = statement.executeQuery();
//
//				while (rs.next()) {
//					SL_Limit = rs.getString("ANSWER");
//					EXTData.put("SL_Limit", SL_Limit);
//				}
//
//				if (statement != null) {
//					statement.close();
//				}
//
//				if (rs != null) {
//					rs.close();
//				}
//				lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("SL_docDate");
//				statement = con.prepareStatement(lsql);
//				statement.setString(1, pinstid);
//				rs = statement.executeQuery();
//
//				while (rs.next()) {
//					SL_docDate = rs.getString("ANSWER");
//					EXTData.put("SL_docDate", SL_docDate);
//				}
//
//				if (statement != null) {
//					statement.close();
//				}
//
//				if (rs != null) {
//					rs.close();
//				}
//				lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("SL_expDate");
//				statement = con.prepareStatement(lsql);
//				statement.setString(1, pinstid);
//				rs = statement.executeQuery();
//
//				while (rs.next()) {
//					SL_expDate = rs.getString("ANSWER");
//					EXTData.put("SL_expDate", SL_expDate);
//				}
//
//				// Execute query execution start for sanction reference no from
//				// TestAPI.properties
//				if (statement != null) {
//					statement.close();
//				}
//
//				if (rs != null) {
//					rs.close();
//				}
//				lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("SL_SanctionReference");
//				statement = con.prepareStatement(lsql);
//				statement.setString(1, pinstid);
//				rs = statement.executeQuery();
//
//				while (rs.next()) {
//					SL_RefNo = rs.getString("ANSWER");
//					EXTData.put("SL_RefNo", SL_RefNo);
//				}
//				// Query Execution end
//
//			}
//
//		} catch (Exception e) {
//			System.out.println("OperationUtillity.GetExtData() Error occurred while fetching values for SL limit fom DB"
//					+ pinstid);
//		} finally {
//			if (statement != null) {
//				statement.close();
//			}
//			if (rs != null) {
//				rs.close();
//			}
//			if (con != null) {
//				con.close();
//			}
//		}
//
//		return EXTData;
//
//	}

//	public Map<String, String> GetIRExtData(String pinstid) throws SQLException {
//		Map<String, String> EXTData = new HashMap<>();
//
//		String IR_Spread = null;
//		String IR_StartDate = null;
//
//		String lsql = null;
//		try {
//
//			con = DBConnect.getConnection();
//			if (con == null) {
//				EXTData.put("Result", "Fail");
//				EXTData.put("Message", "DB connection not established");
//				return EXTData;
//			} else {
//
//				lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("IR_Spread");
//				statement = con.prepareStatement(lsql);
//				statement.setString(1, pinstid);
//				rs = statement.executeQuery();
//
//				while (rs.next()) {
//					IR_Spread = rs.getString("ANSWER");
//					EXTData.put("IR_Spread", IR_Spread);
//				}
//
//				if (statement != null) {
//					statement.close();
//				}
//
//				if (rs != null) {
//					rs.close();
//				}
//				lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("IR_StartDate");
//				statement = con.prepareStatement(lsql);
//				statement.setString(1, pinstid);
//				rs = statement.executeQuery();
//
//				while (rs.next()) {
//					IR_StartDate = rs.getString("ANSWER");
//					EXTData.put("IR_StartDate", IR_StartDate);
//				}
//			}
//
//		} catch (Exception e) {
//
//			System.out.println(
//					"OperationUtillity.GetIRExtData() Error occurred while fetching values for SL limit fom DB ::"
//							+ pinstid);
//		} finally {
//			if (statement != null) {
//				statement.close();
//			}
//			if (rs != null) {
//				rs.close();
//			}
//			if (con != null) {
//				con.close();
//			}
//		}
//
//		return EXTData;
//
//	}

//	public Map<String, String> GetDPSExtData(String pinstid) throws SQLException {
//		Map<String, String> EXTData = new HashMap<>();
//
//		String DPS_Security = null;
//
//		String lsql = null;
//		try (Connection con = DBConnect.getConnection()){
//			
//			if (con == null) {
//				EXTData.put("Result", "Fail");
//				EXTData.put("Message", "DB connection not established");
//				return EXTData;
//			} else {
//
//				lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("DPS_Security");
//				statement = con.prepareStatement(lsql);
//				statement.setString(1, pinstid);
//				rs = statement.executeQuery();
//
//				while (rs.next()) {
//					DPS_Security = rs.getString("ANSWER");
//					EXTData.put("DPS_Security", DPS_Security);
//				}
//
//			}
//
//		} catch (Exception e) {
//			System.out.println(
//					"OperationUtillity.GetDPSExtData() Error occurred while fetching values for SL limit fom DB::"
//							+ pinstid);
//		} finally {
//			if (statement != null) {
//				statement.close();
//			}
//			if (rs != null) {
//				rs.close();
//			}
//
//		}
//
//		return EXTData;
//
//	}
	
//	public Map<String, String> GetACCIExtData(String pinstid) throws SQLException {
//	Map<String, String> EXTData = new HashMap<>();
//
//	String ACCI_AccountNo = null;
//
//	String lsql = null;
//	try {
//
//		con = DBConnect.getConnection();
//		if (con == null) {
//			EXTData.put("Result", "Fail");
//			EXTData.put("Message", "DB connection not established");
//			return EXTData;
//		} else {
//
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("ACCI_AccountNo");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				ACCI_AccountNo = rs.getString("ANSWER");
//				EXTData.put("ACCI_AccountNo", ACCI_AccountNo);
//			}
//
//		}
//
//	} catch (Exception e) {
//
//		logger.info("OperationUtillity.GetACCIExtData() Error occurred while fetching values for SL limit fom DB \n"
//				+ OperationUtillity.traceException(e));
//	} finally {
//		if (statement != null) {
//			statement.close();
//		}
//		if (rs != null) {
//			rs.close();
//		}
//		if (con != null) {
//			con.close();
//		}
//	}
//
//	return EXTData;
//
//}

//public Map<String, String> GetBDExtData(String pinstid) throws SQLException {
//	Map<String, String> EXTData = new HashMap<>();
//
//	String BD_Limit = null;
//	String BD_SancDate = null;
//	String BD_expDate = null;
//	String BD_currCode = null;
//	String lsql = null;
//	try {
//
//		con = DBConnect.getConnection();
//		if (con == null) {
//			EXTData.put("Result", "Fail");
//			EXTData.put("Message", "DB connection not established");
//			return EXTData;
//		} else {
//
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("BD_Limit");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				BD_Limit = rs.getString("ANSWER");
//				EXTData.put("BD_Limit", BD_Limit);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("BD_SancDate");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				BD_SancDate = rs.getString("ANSWER");
//				EXTData.put("BD_SancDate", BD_SancDate);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("BD_expDate");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				BD_expDate = rs.getString("ANSWER");
//				EXTData.put("BD_expDate", BD_expDate);
//			}
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("BD_currCode");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				BD_currCode = rs.getString("ANSWER");
//				EXTData.put("BD_currCode", BD_currCode);
//			}
//
//		}
//
//	} catch (Exception e) {
//		logger.info("OperationUtillity.GetBDExtData().Exception" + OperationUtillity.traceException(e));
//	} finally {
//		if (statement != null) {
//			statement.close();
//		}
//		if (rs != null) {
//			rs.close();
//		}
//		if (con != null) {
//			con.close();
//		}
//	}
//
//	return EXTData;
//
//}

//public Map<String, String> GetLCMExtData(String pinstid) throws SQLException {
//	Map<String, String> EXTData = new HashMap<>();
//
//	String LCM_nameBank = null;
//
//	String lsql = null;
//	try {
//
//		con = DBConnect.getConnection();
//		if (con == null) {
//			EXTData.put("Result", "Fail");
//			EXTData.put("Message", "DB connection not established");
//			return EXTData;
//		} else {
//
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("LCM_nameBank");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				LCM_nameBank = rs.getString("ANSWER");
//				EXTData.put("LCM_nameBank", LCM_nameBank);
//			}
//
//		}
//
//	} catch (Exception e) {
//		logger.info(" OperationUtillity.GetLCMExtData().Exception\n" + OperationUtillity.traceException(e));
//	} finally {
//		if (statement != null) {
//			statement.close();
//		}
//		if (rs != null) {
//			rs.close();
//		}
//		if (con != null) {
//			con.close();
//		}
//	}
//
//	return EXTData;
//
//}

//public Map<String, String> GetURCExtData(String pinstid) throws SQLException {
//	Map<String, String> EXTData = new HashMap<>();
//
//	String URCNumber = null;
//	String cifId = null;
//	String lsql = null;
//	try {
//
//		con = DBConnect.getConnection();
//		if (con == null) {
//			EXTData.put("Result", "Fail");
//			EXTData.put("Message", "DB connection not established");
//			return EXTData;
//		} else {
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("cifId");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				cifId = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("cifId", cifId);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("URC_NUMBER");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				URCNumber = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("URCNumber", URCNumber);
//			}
//
//		}
//
//	} catch (Exception e) {
//		logger.info("OperationUtillity.GetURCExtData() Exception->" + OperationUtillity.traceException(e));
//	} finally {
//		if (statement != null) {
//			statement.close();
//		}
//		if (rs != null) {
//			rs.close();
//		}
//		if (con != null) {
//			con.close();
//		}
//	}
//	return EXTData;
//}

//public Map<String, String> GetLEIExtData(String pinstid) throws SQLException {
//	Map<String, String> EXTData = new HashMap<>();
//
//	String LEINumber = null;
//	String cifId = null;
//	String lsql = null;
//	try {
//
//		con = DBConnect.getConnection();
//		if (con == null) {
//			EXTData.put("Result", "Fail");
//			EXTData.put("Message", "DB connection not established");
//			return EXTData;
//		} else {
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("cifId");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				cifId = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("cifId", cifId);
//			}
//			if (statement != null) {
//				statement.close();
//			}
//			if (rs != null) {
//				rs.close();
//			}
//
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("LEINumber");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				LEINumber = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("LEINumber", LEINumber);
//			}
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("LEI_EXP_DATE");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				String LEI_EXP_DATE = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("LEI_EXP_DATE", LEI_EXP_DATE);
//			}
//		}
//	} catch (Exception e) {
//		logger.info("OperationUtillity.GetLEIExtData().Excetion\n" + OperationUtillity.traceException(e));
//		e.printStackTrace();
//	} finally {
//		if (statement != null) {
//			statement.close();
//		}
//		if (rs != null) {
//			rs.close();
//		}
//		if (con != null) {
//			con.close();
//		}
//	}
//
//	return EXTData;
//
//}

//public Map<String, String> GetDPANExtData(String pinstid) throws SQLException {
//	Map<String, String> EXTData = new HashMap<>();
//
//	String DPAN_AccountNo = null;
//
//	String lsql = null;
//	try {
//
//		con = DBConnect.getConnection();
//		if (con == null) {
//			EXTData.put("Result", "Fail");
//			EXTData.put("Message", "DB connection not established");
//			return EXTData;
//		} else {
//
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("DPAN_AccountNo");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				DPAN_AccountNo = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("DPAN_AccountNo", DPAN_AccountNo);
//			}
//
//		}
//
//	} catch (Exception e) {
//		logger.info("OperationUtillity.GetDPANExtData().Exception" + OperationUtillity.traceException(e));
//	} finally {
//		if (statement != null) {
//			statement.close();
//		}
//		if (rs != null) {
//			rs.close();
//		}
//		if (con != null) {
//			con.close();
//		}
//	}
//
//	return EXTData;
//
//}

//public Map<String, String> GetLCExtData(String pinstid) throws SQLException {
//	Map<String, String> EXTData = new HashMap<>();
//
//	String LC_sppiCrit = null;
//	String LC_SancAuthCode = null;
//
//	String lsql = null;
//	try {
//
//		con = DBConnect.getConnection();
//		if (con == null) {
//			EXTData.put("Result", "Fail");
//			EXTData.put("Message", "DB connection not established");
//			return EXTData;
//		} else {
//
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("LC_sppiCrit");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				LC_sppiCrit = NullReplace(rs.getString("LC_sppiCrit"));
//				EXTData.put("LC_sppiCrit", LC_sppiCrit);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("LC_SancAuthCode");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				LC_SancAuthCode = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("LC_SancAuthCode", LC_SancAuthCode);
//			}
//		}
//
//	} catch (Exception e) {
//		logger.info("OperationUtillity.GetLCExtData().Exception\n" + OperationUtillity.traceException(e));
//	} finally {
//		if (statement != null) {
//			statement.close();
//		}
//		if (rs != null) {
//			rs.close();
//		}
//		if (con != null) {
//			con.close();
//		}
//	}
//	logger.info("OperationUtillity.GetLCExtData().EXTData\n" + EXTData);
//	return EXTData;
//
//}

//public Map<String, String> GetExtLimitNodeModifyData(String pinstid) throws SQLException {
//
//	Map<String, String> EXTData = new HashMap<>();
//
//	String cifId = null;
//	String limitDesc = null;
//	String limitExpiryDate = null;
//	String limitPrefix = null;
//	String limitSanctDate = null;
//	String limitSuffix = null;
//	String lsql = null;
//
//	try {
//
//		con = DBConnect.getConnection();
//		if (con == null) {
//			EXTData.put("Result", "Fail");
//			EXTData.put("Message", "DB connection not established");
//			return EXTData;
//		} else {
//
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("cifId");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				cifId = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("cifId", cifId);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("childlimitDesc");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				limitDesc = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("limitDesc", limitDesc);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("limitExpiryDate");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				limitExpiryDate = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("limitExpiryDate", limitExpiryDate);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("limitPrefix");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				limitPrefix = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("limitPrefix", limitPrefix);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("limitSanctDate");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				limitSanctDate = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("limitSanctDate", limitSanctDate);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("limitSuffix");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				limitSuffix = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("limitSuffix", limitSuffix);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//
//		}
//
//	} catch (Exception e) {
//		logger.info("OperationUtillity.GetExtLimitNodeModifyData().Exception----->\n"
//				+ OperationUtillity.traceException(e));
//	} finally {
//		if (statement != null) {
//			statement.close();
//		}
//		if (rs != null) {
//			rs.close();
//		}
//		if (con != null) {
//			con.close();
//		}
//	}
//
//	logger.info("OperationUtillity.GetExtLimitNodeModifyData().EXTDATA---->" + EXTData);
//	return EXTData;
//
//}
	
//	public Map<String, String> GetChildLimitNodeData(String pinstid, String fACILITY) throws SQLException {
//	Map<String, String> EXTData = new HashMap<>();
//
//	String cifId = null;
//	String limitDesc = null;
//	String limitExpiryDate = null;
//	String limitPrefix = null;
//	String parentlimitPrefix = null;
//	String limitSanctDate = null;
//	String limitSuffix = null;
//	String parentlimitSuffix = null;
//	String lsql = null;
//	String childAmount = null;
//
//	try {
//
//		con = DBConnect.getConnection();
//		if (con == null) {
//			EXTData.put("Result", "Fail");
//			EXTData.put("Message", "DB connection not established GetChildLimitNodeData");
//			return EXTData;
//		} else {
//
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("cifId");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				cifId = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("cifId", cifId);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("childlimitDesc");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				limitDesc = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("limitDesc", limitDesc);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("childFacilityExpDate");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				limitExpiryDate = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("limitExpiryDate", limitExpiryDate);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("limitPrefix");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				limitPrefix = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("limitPrefix", limitPrefix);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("limitSanctDate");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				limitSanctDate = NullReplace(rs.getString("ANSWER"));
////				EXTData.put("limitSanctDate", limitSanctDate);
//				EXTData.put("SANCTION_DATE", limitSanctDate);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("limitSuffix");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				limitSuffix = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("limitSuffix", limitSuffix);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("childAmtvalue");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				childAmount = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("childAmount", childAmount);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("parentLimitPrefix");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				parentlimitPrefix = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("parentlimitPrefix", parentlimitPrefix);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("parentLimitSuffix");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				parentlimitSuffix = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("parentlimitSuffix", parentlimitSuffix);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//
//		}
//
//	} catch (Exception e) {
//		logger.info("OperationUtillity.GetChildLimitNodeData()-->" + OperationUtillity.traceException(e));
////				"");
//	} finally {
//		if (statement != null) {
//			statement.close();
//		}
//		if (rs != null) {
//			rs.close();
//		}
//		if (con != null) {
//			con.close();
//		}
//	}
//
//	logger.info("OperationUtillity.GetChildLimitNodeData()--EXTData-->" + EXTData);
//	return EXTData;
//
//}

//	public Map<String, String> GetURCCExtData(String pinstid) throws SQLException {
//	Map<String, String> EXTData = new HashMap<>();
//
//	String URCCNumber = null;
//	String cifId = null;
//	String lsql = null;
//
//	try {
//
//		con = DBConnect.getConnection();
//		if (con == null) {
//			EXTData.put("Result", "Fail");
//			EXTData.put("Message", "DB connection not established");
//			return EXTData;
//		} else {
//
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("cifId");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				cifId = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("cifId", cifId);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("URCNumber");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				URCCNumber = rs.getString("ANSWER");
//				EXTData.put("URCCNumber", URCCNumber);
//			}
//			if (statement != null) {
//				statement.close();
//			}
//			if (rs != null) {
//				rs.close();
//			}
//		}
//	} catch (Exception e) {
//		logger.info(
//				"OperationUtillity.GetURCCExtData() Error occurred while fetching values for GetURCCExtData fom DB"
//						+ traceException(e));
//	} finally {
//		if (statement != null) {
//			statement.close();
//		}
//		if (rs != null) {
//			rs.close();
//		}
//		if (con != null) {
//			con.close();
//		}
//	}
//	logger.info("OperationUtillity.GetURCCExtData() ---EXTData---->" + EXTData);
//	return EXTData;
//}
	
//	public Map<String, String> GetCDOCExtData(String pinstid) throws SQLException {
//	Map<String, String> EXTData = new HashMap<>();
//
//	String CDOC_Dept = null;
//	String CDOC_LoanAmt = null;
//	String CDOC_Remarks = null;
//	String CDOC_MemoFrom = null;
//	String CDOC_Ucc = null;
//
//	String lsql = null;
//	try {
//
//		con = DBConnect.getConnection();
//		if (con == null) {
//			EXTData.put("Result", "Fail");
//			EXTData.put("Message", "DB connection not established");
//			return EXTData;
//		} else {
//
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("CDOC_MemoFrom_Ucc");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				CDOC_MemoFrom = NullReplace(rs.getString("CASE_INITIATOR"));
//				CDOC_Ucc = NullReplace(rs.getString("UCC"));
//				CDOC_Dept = NullReplace(rs.getString("CMOG_GROUP"));
//				CDOC_Remarks = NullReplace(rs.getString("COMPANYNAME"));
//				EXTData.put("CDOC_MemoFrom", CDOC_MemoFrom);
//				EXTData.put("CDOC_Ucc", CDOC_Ucc);
//				EXTData.put("CDOC_Dept", CDOC_Dept);
//				EXTData.put("CDOC_Remarks", CDOC_Remarks);
//			}
//			if (statement != null) {
//				statement.close();
//			}
//			if (rs != null) {
//				rs.close();
//			}
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("CDOC_LoanAmt");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				CDOC_LoanAmt = NullReplace(rs.getString("LOANAMOUNT"));
//				EXTData.put("CDOC_LoanAmt", CDOC_LoanAmt);
//			}
//			if (statement != null) {
//				statement.close();
//			}
//			if (rs != null) {
//				rs.close();
//			}
//		}
//	} catch (Exception e) {
//		logger.info("Error occurred while fetching values for GetCDOCExtData fom DB" + traceException(e));
//	} finally {
//		if (statement != null) {
//			statement.close();
//		}
//		if (rs != null) {
//			rs.close();
//		}
//		if (con != null) {
//			con.close();
//		}
//	}
//	return EXTData;
//}

//public List<Map<String, String>> GetCDOC1ExtData(String pinstid) throws SQLException {
//
//	List<Map<String, String>> list = new ArrayList<Map<String, String>>();
//	Map<String, String> EXTData = new HashMap<>();
//	Connection con = null;
//	PreparedStatement statement = null;
//	ResultSet rs = null;
//	String CDOC_TypeOfDoc = null;
//	String CDOC_ExecDate = null;
//	String lsql = null;
//
//	try {
//		con = DBConnect.getConnection();
//		if (con == null) {
//			EXTData.put("Result", "Fail");
//			EXTData.put("Message", "DB connection not established");
//			list.add(EXTData);
//			return list;
//		} else {
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("CDOC_TypeOfDoc_ExeDate");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				CDOC_TypeOfDoc = NullReplace(rs.getString("TYPE_OF_DOCUMENT"));
//				CDOC_ExecDate = NullReplace(rs.getString("EXECUTION_DATE"));
//				EXTData.put(CDOC_TypeOfDoc, CDOC_ExecDate);
//				list.add(EXTData);
//			}
//			if (statement != null) {
//				statement.close();
//			}
//			if (rs != null) {
//				rs.close();
//			}
//		}
//	} catch (Exception e) {
//		logger.info("Error occurred while fetching values for SL limit fom DB" + traceException(e));
//	} finally {
//		if (statement != null) {
//			statement.close();
//		}
//		if (rs != null) {
//			rs.close();
//		}
//		if (con != null) {
//			con.close();
//		}
//	}
//	return list;
//}

//public Map<String, String> GetSanctLimiAcctLevelExtData(String pinstid) throws SQLException {
//
//	Map<String, String> EXTData = new HashMap<>();
//	Connection con = null;
//	PreparedStatement statement = null;
//	ResultSet rs = null;
//	String SLAL_LimitExpryDate = null;
//	String SLAL_LSMNO = null;
//	String SLAL_SancLimit = null;
//	String lsql = null;
//
//	try {
//		con = DBConnect.getConnection();
//		if (con == null) {
//			EXTData.put("Result", "Fail");
//			EXTData.put("Message", "DB connection not established");
//
//			return EXTData;
//		} else {
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("limitExpiryDate");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				SLAL_LimitExpryDate = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("SLAL_LimitExpryDate", SLAL_LimitExpryDate);
//			}
//			if (statement != null) {
//				statement.close();
//			}
//			if (rs != null) {
//				rs.close();
//			}
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("SLAL_LSMNO");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				SLAL_LSMNO = NullReplace(rs.getString("LSM_NO"));
//				EXTData.put("SLAL_LSMNO", SLAL_LSMNO);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("SLAL_SancLimit");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				SLAL_SancLimit = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("SLAL_SancLimit", SLAL_SancLimit);
//			}
//			if (statement != null) {
//				statement.close();
//			}
//			if (rs != null) {
//				rs.close();
//			}
//		}
//	} catch (Exception e) {
//		logger.info("OperationUtillity.GetSanctLimiAcctLevelExtData().Exception\n" + traceException(e));
//	} finally {
//		if (statement != null) {
//			statement.close();
//		}
//		if (rs != null) {
//			rs.close();
//		}
//		if (con != null) {
//			con.close();
//		}
//	}
//	return EXTData;
//
//}

//public Map<String, String> GetRateOfIntExtData(String pinstid, String DEFAULT_INTEREST, String INTEREST_BASE)
//		throws SQLException {
//	Map<String, String> EXTData = new HashMap<>();
//
//	String cifId = null;
//	String ROI_PeggingFrequency = null;
//	String ROI_NoOfDays = null;
//	String ROI_EndDate_T = null;
//	String ROI_EndDate = null;
//	String ROI_RateCode = null;
//	String ROI_Spread = null;
//	String lsql = null;
//	String ACCOUNT_NO = null;
//	try {
//
//		con = DBConnect.getConnection();
//		if (con == null) {
//			EXTData.put("Result", "Fail");
//			EXTData.put("Message", "DB connection not established");
//			return EXTData;
//		} else {
//
//			// start query
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("ACCOUNT_NO_QUERY");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				ACCOUNT_NO = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("ACCOUNT_NO", ACCOUNT_NO);
//			}
//			if (statement != null) {
//				statement.close();
//			}
//			if (rs != null) {
//				rs.close();
//			}
//			// end
//
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("cifId");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				cifId = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("cifId", cifId);
//			}
//			// Start
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("ROI_PeggingFrequency");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, DEFAULT_INTEREST);
//			statement.setString(2, INTEREST_BASE);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				ROI_PeggingFrequency = NullReplace(rs.getString("pegging_frequency"));
//				EXTData.put("ROI_PeggingFrequency", ROI_PeggingFrequency);
//			}
//			// end
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("ROI_NoOfDays");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, ROI_PeggingFrequency);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				ROI_NoOfDays = NullReplace(rs.getString("no_of_days"));
//				EXTData.put("ROI_NoOfDays", ROI_NoOfDays);
//			}
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("ROI_EndDate");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, ROI_NoOfDays);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				ROI_EndDate_T = NullReplace(rs.getString("End_Date"));
//				if (ROI_EndDate_T != null || !(ROI_EndDate_T == ""))
//					ROI_EndDate_T = ROI_EndDate_T.substring(0, ROI_EndDate_T.indexOf(" "));
//				ROI_EndDate = ROI_EndDate_T.substring(ROI_EndDate_T.lastIndexOf("-") + 1)
//						+ ROI_EndDate_T.substring(ROI_EndDate_T.indexOf("-"), ROI_EndDate_T.lastIndexOf("-") + 1)
//						+ ROI_EndDate_T.substring(0, 4);
//				EXTData.put("ROI_EndDate", ROI_EndDate);
//			}
//			logger.info("ROI_EndDate --> " + ROI_EndDate);
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("ROI_RateCode");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, DEFAULT_INTEREST);
//			statement.setString(2, ROI_PeggingFrequency);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				ROI_RateCode = NullReplace(rs.getString("RATE_CODE"));
//				EXTData.put("ROI_RateCode", ROI_RateCode);
//			}
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("ROI_Spread");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				ROI_Spread = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("ROI_Spread", ROI_Spread);
//			}
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//		}
//
//	} catch (Exception e) {
//
//		logger.info("OperatioUtility.GetRateOfIntExtData fom DB" + OperationUtillity.traceException(e));
//	} finally {
//		if (statement != null) {
//			statement.close();
//		}
//		if (rs != null) {
//			rs.close();
//		}
//		if (con != null) {
//			con.close();
//		}
//	}
//
//	return EXTData;
//
//}

//public Map<String, String> GetAcctLinkedlimitNodeExtData(String pinstid, String fACILITY) throws SQLException {
//
//	Map<String, String> EXTData = new HashMap<>();
//	Connection con = null;
//	PreparedStatement statement = null;
//	ResultSet rs = null;
//	String ALLN_DrawingPower = null;
//	String ALLN_LSMNO = null;
//	String lsql = null;
//	String ACCOUNT_NO = null;
//	String limitPrefix = null;
//	String limitSuffix = null;
//
//	try {
//
//		con = DBConnect.getConnection();
//		if (con == null) {
//			EXTData.put("Result", "Fail");
//			EXTData.put("Message", "DB connection not established");
//			return EXTData;
//		} else {
//
//			// start query
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("ACCOUNT_NO_QUERY");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				ACCOUNT_NO = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("ACCOUNT_NO", ACCOUNT_NO);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//			// end
//
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("limitPrefix");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				limitPrefix = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("limitPrefix", limitPrefix);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("limitSuffix");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				limitSuffix = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("limitSuffix", limitSuffix);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("ALLN_DrawingPower");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				ALLN_DrawingPower = NullReplace(rs.getString("ANSWER"));
//				EXTData.put("ALLN_DrawingPower", ALLN_DrawingPower);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("ALLN_LSMNO");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				ALLN_LSMNO = NullReplace(rs.getString("LSM_NO"));
//				EXTData.put("ALLN_LSMNO", ALLN_LSMNO);
//			}
//
//			if (statement != null) {
//				statement.close();
//			}
//
//			if (rs != null) {
//				rs.close();
//			}
//		}
//
//	} catch (Exception e) {
//
////		logger.info(
////				"Error occurred while fetching values for SL limit fom DB" + e.fillInStackTrace(), "");
//	} finally {
//		if (statement != null) {
//			statement.close();
//		}
//		if (rs != null) {
//			rs.close();
//		}
//		if (con != null) {
//			con.close();
//		}
//	}
//
//	return EXTData;
//
//}

//public Map<String, String> GetFeeRecovery(String pinstid) throws SQLException {
//
//	logger.info("Entered into OperationUtillity.GetFeeRecovery()-->" + pinstid);
//
//	Map<String, String> EXTData = new HashMap<>();
//	Connection con = null;
//	PreparedStatement statement = null;
//	ResultSet rs = null;
//	String lsql = null;
//	String ACCOUNT_NO = null;
//	try {
//		con = DBConnect.getConnection();
//		if (con == null) {
//			EXTData.put("Result", "Fail");
//			EXTData.put("Message", "DB connection not established");
//			return EXTData;
//		} else {
//
//			// start account query
//			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("ACCOUNT_NO_QUERY");
//			statement = con.prepareStatement(lsql);
//			statement.setString(1, pinstid);
//			rs = statement.executeQuery();
//			try {
//				while (rs.next()) {
//					ACCOUNT_NO = NullReplace(rs.getString("ANSWER"));
//					EXTData.put("ACCOUNT_NO", ACCOUNT_NO);
//				}
//			} catch (Exception e) {
//				logger.info("OperationUtillity.GetFeeRecovery().Exception" + OperationUtillity.traceException(e));
//			}
//			if (statement != null) {
//				statement.close();
//			}
//			if (rs != null) {
//				rs.close();
//			}
//			// end
//		}
//
//	} catch (Exception e) {
//	} finally {
//		if (statement != null) {
//			statement.close();
//		}
//		if (rs != null) {
//			rs.close();
//		}
//		if (con != null) {
//			con.close();
//		}
//	}
//	logger.info("OperationUtillity.GetFeeRecovery().EXTData\n" + EXTData);
//	return EXTData;
//}
	
//	public Map<String, String> getPSL(String pinstid, String facility) throws SQLException {
//
//		Map<String, String> EXTData = new HashMap<>();
//		Connection con = null;
//		PreparedStatement statement = null;
//		ResultSet rs = null;
//		String ACCOUNT_NO = null;
//		String ALLN_LSMNO = null;
//		String lsql = null;
//		String CURRENCY = null;
//		String LIMIT_AMT = null;
//
//		try {
//
//			con = DBConnect.getConnection();
//			if (con == null) {
//				EXTData.put("Result", "Fail");
//				EXTData.put("Message", "DB connection not established");
//				return EXTData;
//			} else {
//				// start query
//				lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("ACCOUNT_NO_QUERY");
//				statement = con.prepareStatement(lsql);
//				statement.setString(1, pinstid);
//				rs = statement.executeQuery();
//
//				while (rs.next()) {
//					ACCOUNT_NO = NullReplace(rs.getString("ANSWER"));
//					EXTData.put("ACCOUNT_NO", ACCOUNT_NO);
//				}
//
//				if (statement != null) {
//					statement.close();
//				}
//
//				if (rs != null) {
//					rs.close();
//				}
//				// end
//			}
//
//		} catch (Exception e) {
//			logger.info("OperationUtillity.getPSL().Exception\n" + OperationUtillity.traceException(e));
//		} finally {
//			if (statement != null) {
//				statement.close();
//			}
//			if (rs != null) {
//				rs.close();
//			}
//			if (con != null) {
//				con.close();
//			}
//		}
//
//		return EXTData;
//	}
	
//	public String getRecordId(String pinstid) throws SQLException {
//
//		String recordid = null;
//		String lsql = null;
//		try {
//			con = DBConnect.getConnection();
//			if (con == null) {
//				recordid = "RecordId not fetched due to error in establishing connection";
//				return recordid;
//			} else {
//				lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("RECORD_ID");
//				statement = con.prepareStatement(lsql);
//				statement.setString(1, pinstid);
//				rs = statement.executeQuery();
//				while (rs.next()) {
//					recordid = NullReplace(rs.getString("RECORDID"));
//				}
//			}
//		} catch (Exception e) {
//			logger.info("OperationUtillity.getRecordId().Exception\n" + OperationUtillity.traceException(e));
//		} finally {
//			if (statement != null) {
//				statement.close();
//			}
//			if (rs != null) {
//				rs.close();
//			}
//			if (con != null) {
//				con.close();
//			}
//		}
//		return recordid;
//	}
	
//	public static String insertSchedulerPinstId(String pinstid) {
//	String message = "PINSTID :: " + pinstid + " NOT INSERTED INTO SCHEDULER TABLE";
//	deleteSchedulerPinstId(pinstid);
//	try (Connection con = DBConnect.getConnection();
//			PreparedStatement pst = con.prepareStatement(Queries.INSERT_LSM_SCHEDULER_PINSTIDS)) {
//		pst.setString(1, NullReplace(pinstid));
//		con.setAutoCommit(false);
//		int int_count = pst.executeUpdate();
//		if (int_count > 0) {
//			con.commit();
//			message = "PINSTID :: " + pinstid + " INSERTED INTO SCHEDULER TABLE";
//		}
//	} catch (Exception e) {
//		logger.error("OperationUtillity.insertSchedulerPinstIds():: {}", OperationUtillity.traceException(e));
//	}
//	return message;
//}

//public static String deleteSchedulerPinstId(String pinstid) {
//	String message = "PINSTID :: " + pinstid + " NOT DELETED FROM SCHEDULER TABLE";
//	try (Connection con = DBConnect.getConnection();
//			PreparedStatement pst = con.prepareStatement(Queries.DELETE_LSM_SCHEDULER_PINSTIDS)) {
//		pst.setString(1, NullReplace(pinstid));
//		con.setAutoCommit(false);
//		int int_count = pst.executeUpdate();
//		if (int_count > 0) {
//			con.commit();
//			message = "PINSTID :: " + pinstid + " DELETED FROM SCHEDULER TABLE";
//		}
//	} catch (Exception e) {
//		logger.error("OperationUtillity.deleteSchedulerPinstId():: {}", OperationUtillity.traceException(e));
//	}
//	return message;
//}
	
//	public static int getStatusCodeForUpdate(String pinstid, String StatusCode) {
//
//		int count_sc = 0;
//		try (Connection con = DBConnect.getConnection();
//				PreparedStatement pst = con.prepareStatement(
//						"SELECT COUNT(1)  AS COUNT FROM LSM_MS_STATUS_CODE WHERE LMS_STATUS_CODE =?");) {
//			pst.setString(1, StatusCode);
//			try (ResultSet rs = pst.executeQuery()) {
//				while (rs.next()) {
//					count_sc = rs.getInt("COUNT");
//				}
//			}
//		} catch (SQLException e) {
//			logger.info("info logger  get message() Exception occurred while fetching Case Type: "
//					+ OperationUtillity.traceException(e));
//		}
//		return count_sc;
//	}
	
	public Map<String, String> GetCustInqExtData(String pinstid) throws SQLException {
		Map<String, String> EXTData = new HashMap<>();
        String CI_CustomerId = null;

		try (Connection con = DBConnect.getConnection();
				PreparedStatement statement = con.prepareStatement(ReadPropertyFIle.getInstance().getPropConst().getProperty("CI_CustomerId"))) {
				statement.setString(1, pinstid);
				try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					CI_CustomerId = rs.getString("ANSWER");
					EXTData.put("CI_CustomerId", CI_CustomerId);
				}
			}
		} catch (Exception e) {
			logger.info("OperationUtillity.GetCustInqExtData() Error occurred while fetching values for SL limit fom DB:: "+ pinstid);
		}
		return EXTData;
	}


	public Map<String, String> GetParentLimitNodeData(String pinstid) throws SQLException {
		Map<String, String> EXTData = new LinkedHashMap<>();

		String cifId = null;

		try (Connection con = DBConnect.getConnection();
				PreparedStatement statement = con.prepareStatement(ReadPropertyFIle.getInstance().getPropConst().getProperty("cifId"))) {
					statement.setString(1, pinstid);
					try (ResultSet rs = statement.executeQuery()) {
					while (rs.next()) {
						cifId = NullReplace(rs.getString("ANSWER"));
						EXTData.put("cifId", cifId);
					}
				} catch (Exception ex) {
					logger.info("OperationUtillity.GetParentLimitNodeData().Exception\n"
							+ OperationUtillity.traceException(ex));
				}

			try (PreparedStatement statement1 = con.prepareStatement(ReadPropertyFIle.getInstance().getPropConst().getProperty("FACILITY_COMMON_DATA"))) {
					statement1.setString(1, pinstid);
					try (ResultSet rs = statement1.executeQuery()) {
					while (rs.next()) {
						EXTData.put("LSM_NO", rs.getString("LSM_NO"));
						EXTData.put("PARENT_LIMIT_PREFIX", NullReplace(rs.getString("PARENT_LIMIT_PREFIX")));
						EXTData.put("PARENT_LIMIT_SUFFIX", NullReplace(rs.getString("PARENT_LIMIT_SUFFIX")));
						EXTData.put("PROGRAM_CODE", NullReplace(rs.getString("PROGRAM_CODE")));

						String LOAN_STATUS = NullReplace(rs.getString("LOAN_STATUS"));
						String NAME_OF_FIRST_BANK = NullReplace(rs.getString("NAME_OF_BANK_ONE"));

						if (NAME_OF_FIRST_BANK != "") {
							if (NAME_OF_FIRST_BANK.contains(",")) {
								NAME_OF_FIRST_BANK = NAME_OF_FIRST_BANK.substring(1);
								NAME_OF_FIRST_BANK = NAME_OF_FIRST_BANK.substring(0, NAME_OF_FIRST_BANK.indexOf(","));
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

						EXTData.put("LOAN_STATUS", LOAN_STATUS);
						EXTData.put("NAME_OF_FIRST_BANK", NAME_OF_FIRST_BANK);

					}
				  }
				} catch (Exception ex) {
					logger.info("OperationUtillity.GetParentLimitNodeData().Exception -"+ OperationUtillity.traceException(ex));
				}
			
			  try (PreparedStatement statement1 = con.prepareStatement(ReadPropertyFIle.getInstance().getPropConst().getProperty("PARENT_LIMIT_DESC"))) {
					statement1.setString(1, pinstid);
				try (ResultSet rs = statement1.executeQuery()) {
					while (rs.next()) {
						String companyName = NullReplace(rs.getString("COMPANYNAME"));
						if (companyName.length() > 25) {
							//companyName = companyName.substring(0, 24);
							companyName = companyName.substring(0, 19);
						}
						EXTData.put("PARENT_LIMIT_DESC", companyName);
					}
				  }
				} catch (Exception ex) {
					logger.info("OperationUtillity.GetParentLimitNodeData().Exception\n"
							+ OperationUtillity.traceException(ex));
				}

			  try (PreparedStatement statement1 = con.prepareStatement("SELECT * FROM ( SELECT LIMIT.QUESTION_ID   LIMIT_QUESTION, LIMIT.ANSWER LIMIT_ANSWER FROM LSM_LIMIT_ANSWERS LIMIT WHERE LIMIT.PINSTID =? AND LIMIT.QUESTION_ID IN ( '50','60','58', '61', '57','52', '64','62','59','63','75','171','70') ) PIVOT (MAX ( LIMIT_ANSWER ) FOR LIMIT_QUESTION IN ( 60 AS PARENT_LIMIT_EXP_DATE,58 AS PARENT_LIMIT_SANCTION_DATE ,61 AS PARENT_LIMIT_SANCTION_AMOUNT, 50 AS TOTAL_FB_NONFB, 57 AS SANCTION_FORUM, 52 AS DOCUMENT_AMOUNT , 64 AS FA_EXECUTION_DATE, 62 AS FACILITY_EXP_DATE , 59 AS SANCTION_VAL_DATE,63 AS SANCTION_REFERENCE_NUMBER , 75 AS PARENT_CURRENCY,171 AS CURRENCY_TWO,70 AS ISREVOCABLE))")) {
					statement1.setString(1, pinstid);
					try (ResultSet rs = statement1.executeQuery()) {
					while (rs.next()) {
						EXTData.put("PARENT_LIMIT_EXP_DATE", NullReplace(rs.getString("PARENT_LIMIT_EXP_DATE")));
						EXTData.put("PARENT_LIMIT_SANCTION_DATE",
								NullReplace(rs.getString("PARENT_LIMIT_SANCTION_DATE")));
						EXTData.put("PARENT_SANCTION_AMOUNT",
								commonUtility.millionString(NullReplace(rs.getString("PARENT_LIMIT_SANCTION_AMOUNT"))));
						EXTData.put("FACILITY_EXP_DATE", NullReplace(rs.getString("FACILITY_EXP_DATE")));
						EXTData.put("SANCTION_VAL_DATE", NullReplace(rs.getString("SANCTION_VAL_DATE")));
						EXTData.put("TOTAL_FB_NONFB",
								commonUtility.millionString(NullReplace(rs.getString("TOTAL_FB_NONFB"))));
						EXTData.put("SANCTION_FORUM", NullReplace(rs.getString("SANCTION_FORUM")));
						EXTData.put("FA_EXECUTION_DATE", NullReplace(rs.getString("FA_EXECUTION_DATE")));
						EXTData.put("DOCUMNET_AMOUNT",
								commonUtility.millionString(NullReplace(rs.getString("DOCUMENT_AMOUNT"))));
						EXTData.put("PARENT_CURRENCY", NullReplace(rs.getString("PARENT_CURRENCY")));
						EXTData.put("CURRENCY_TWO", NullReplace(rs.getString("CURRENCY_TWO")));
						EXTData.put("ISREVOCABLE", NullReplace(rs.getString("ISREVOCABLE"))); //  10064
					}
				  }
				} catch (Exception ex) {
					logger.info("OperationUtillity.GetParentLimitNodeData().Exception\n "
							+ OperationUtillity.traceException(ex) + "");
				}

				// maheshv
			  try (PreparedStatement statement1 = con.prepareStatement("SELECT MEMO_TYPE FROM LIMIT_SETUP_EXT WHERE PINSTID = ? ")) {
					//String lsql5 = "SELECT MEMO_TYPE FROM LIMIT_SETUP_EXT WHERE PINSTID = ? ";
					statement1.setString(1, pinstid);
					try (ResultSet rs = statement1.executeQuery()) {
					while (rs.next()) {
						EXTData.put("MEMO_TYPE", NullReplace(rs.getString("MEMO_TYPE")));
					}
				  }
				 logger.info("OperationUtillity.GetParentLimitNodeData().MEMO_TYPE\n " + EXTData.get("MEMO_TYPE"));
				} catch (Exception ex) {
					logger.info("OperationUtillity.GetParentLimitNodeData().Exception\n "
							+ OperationUtillity.traceException(ex) + "");
				}
		} catch (Exception e) {
			logger.info("OperationUtillity.GetParentLimitNodeData().Exception\n" + OperationUtillity.traceException(e));
		}
		logger.info("OperationUtillity.GetParentLimitNodeData().EXTData finalmap -->" + EXTData);
		return EXTData;
	}


	public static String soapMessageToString(SOAPMessage message) {

		String result = null;
		if (message != null) {
			ByteArrayOutputStream baos = null;
			try {
				baos = new ByteArrayOutputStream();
				message.writeTo(baos);
				result = baos.toString();
			} catch (Exception e) {
				logger.info("soapMessageToString closing connection Error " + e.fillInStackTrace(), "");
			} finally {
				if (baos != null) {
					try {
						baos.close();
					} catch (Exception e) {
						logger.info("soapMessageToString closing connection Error " + e.fillInStackTrace(), "");
						System.out.println("soapMessageToString closing connection Error ");
					}
				}
			}
		}
		System.out.println("OperationUtillity.soapMessageToString()");
		return result;
	}

	public static Map<String, String> GetcifIdData(String pinstid) throws SQLException {
		Map<String, String> EXTData = new LinkedHashMap<>();

		String cifId = null;
		
		try (Connection con = DBConnect.getConnection();
				PreparedStatement statement = con.prepareStatement(ReadPropertyFIle.getInstance().getPropConst().getProperty("cifId"))) {
				statement.setString(1, pinstid);
				try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					cifId = OperationUtillity.NullReplace(rs.getString("ANSWER"));
					EXTData.put("cifId", cifId);
				}
			}
		} catch (Exception e) {
			logger.info("Exception.OperationUtillity.GetcifIdData() " + traceException(e));
		}
		return EXTData;
	}

	// common for All
	public static Map<String, String> API_RequestResponse_Insert(String request, String account_Opening_Response,
			String req_type, String PINSTID, Map<String, String> API_REQ_RES_map, String requestUUID)
			throws SQLException {

		Map<String, String> EXTData = new HashMap<>();
		
		try (Connection con = DBConnect.getConnection();
				PreparedStatement statement = con.prepareStatement(Queries.INSERT_FEE_REQ_RES)) {
				//statement = con.prepareStatement(Queries.INSERT_FEE_REQ_RES);
				statement.setString(1, NullReplace(PINSTID));
				statement.setString(2, NullReplace(request));
				statement.setString(3, NullReplace(account_Opening_Response));
				statement.setString(4, NullReplace(req_type));
				statement.setString(5, NullReplace(API_REQ_RES_map.get("Status")));
				statement.setString(6, NullReplace(API_REQ_RES_map.get("ErrorDesc")));
				statement.setString(7, InetAddress.getLocalHost().getHostAddress());
				int int_count = statement.executeUpdate();
			logger.info("OperationUtillity.API_RequestResponse_Insert().int_count"+ int_count);			
		} catch (Exception e) {
			logger.info("OperationUtillity.API_RequestResponse_Insert().exception " + OperationUtillity.traceException(e));
		} 
		return EXTData;
	}

	// updating limitTab Field from Rate of Interest
	public static void updateLimitTabDateForRoI(String pinstid) throws SQLException {
		
				//String updatesql = "UPDATE LSM_LIMIT_ANSWERS SET ANSWER= ? WHERE PINSTID= ? AND QUESTION_ID = ?";
				try (Connection con = DBConnect.getConnection();
						PreparedStatement statement = con.prepareStatement("UPDATE LSM_LIMIT_ANSWERS SET ANSWER= ? WHERE PINSTID= ? AND QUESTION_ID = ?")) {
					statement.setString(2, NullReplace(pinstid));
					statement.setString(1, NullReplace(LocalDate.now().toString()));
					statement.setString(3, "65");
					int int_count = statement.executeUpdate();
			logger.info("OperationUtillity.updateLimitTabDateForRoI().int_count"+ int_count);		
		} catch (Exception e) {
			logger.info("Exception.OperationUtillity.updateLimitTabDateForRoI[PINSTID :'" + pinstid + "']"+ OperationUtillity.traceException(e));
		} 
	}

	// for fee rcovery only
	public static Map<String, String> FeeRecoveryAPI_RequestResponse_Insert(String request,
			String account_Opening_Response, String req_type, String PINSTID, Map<String, String> API_REQ_RES_map,
			String requestUUID) throws SQLException {

		Map<String, String> EXTData = new HashMap<>();
		try (Connection con = DBConnect.getConnection();
				PreparedStatement statement = con.prepareStatement(Queries.INSERT_FEE_REQ_RES)) {
				statement.setString(1, NullReplace(PINSTID));
				statement.setString(2, NullReplace(request));
				statement.setString(3, NullReplace(account_Opening_Response));
				statement.setString(4, NullReplace(req_type));
				statement.setString(5, NullReplace(API_REQ_RES_map.get("Status")));
				statement.setString(6, NullReplace(API_REQ_RES_map.get("Response1")));
				statement.setString(7, InetAddress.getLocalHost().getHostAddress());
				int int_count = statement.executeUpdate();
				
			logger.info("OperationUtillity.FeeRecoveryAPI_RequestResponse_Insert().int_count"+ int_count);
		} catch (Exception e) {
			logger.info("OperationUtillity.FeeRecoveryAPI_RequestResponse_Insert().Exception"+ OperationUtillity.traceException(e));
		} 
		return EXTData;
	}

	public Map<String, String> getLienMarkingData(String pinstid) throws SQLException {

		Map<String, String> EXTData = new HashMap<>();
		String ACCOUNT_NO = null,CURRENCY = null,LIMIT_AMT = null;

//		lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("ACCOUNT_NO_QUERY");
		try (Connection con = DBConnect.getConnection();
				PreparedStatement statement = con.prepareStatement(
						ReadPropertyFIle.getInstance().getPropConst().getProperty("ACCOUNT_NO_QUERY"))) {
			statement.setString(1, pinstid);
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					ACCOUNT_NO = NullReplace(rs.getString("ANSWER"));
					EXTData.put("ACCOUNT_NO", ACCOUNT_NO);
				}
			}

			// lsql =
			// ReadPropertyFIle.getInstance().getPropConst().getProperty("LIMIT_CURRENCY");
			try (PreparedStatement statement1 = con
					.prepareStatement(ReadPropertyFIle.getInstance().getPropConst().getProperty("LIMIT_CURRENCY"))) {
				statement1.setString(1, pinstid);
				try (ResultSet rs = statement1.executeQuery()) {
					while (rs.next()) {
						CURRENCY = NullReplace(rs.getString("ANSWER"));
						EXTData.put("CURRENCY", CURRENCY);
					}
				}
			}

			// lsql =
			// ReadPropertyFIle.getInstance().getPropConst().getProperty("LIMIT_AMOUNT");
			try (PreparedStatement statement1 = con
					.prepareStatement(ReadPropertyFIle.getInstance().getPropConst().getProperty("LIMIT_CURRENCY"))) {
				statement1.setString(1, pinstid);
				try (ResultSet rs = statement1.executeQuery()) {
					while (rs.next()) {
						LIMIT_AMT = NullReplace(rs.getString("ANSWER"));
						EXTData.put("LIMIT_AMT", LIMIT_AMT);
					}
				}
			}
		} catch (Exception e) {
			logger.info("OperationUtillity.getLienMarkingData().Exception\n" + OperationUtillity.traceException(e));
		}
		return EXTData;
	}

	public Map<String, String> getlienmodification(String pinstid) throws SQLException {

		Map<String, String> EXTData = new HashMap<>();

		String ACCOUNT_NO = null;
		String CURRENCY = null;
		String LIMIT_AMT = null;

		// lsql =
		// ReadPropertyFIle.getInstance().getPropConst().getProperty("ACCOUNT_NO_QUERY");
		try (Connection con = DBConnect.getConnection();
				PreparedStatement statement = con.prepareStatement(
						ReadPropertyFIle.getInstance().getPropConst().getProperty("ACCOUNT_NO_QUERY"))) {
			statement.setString(1, pinstid);
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					ACCOUNT_NO = NullReplace(rs.getString("ANSWER"));
					EXTData.put("ACCOUNT_NO", ACCOUNT_NO);
				}
			}

			// lsql =
			// ReadPropertyFIle.getInstance().getPropConst().getProperty("LIMIT_CURRENCY");
			try (PreparedStatement statement1 = con
					.prepareStatement(ReadPropertyFIle.getInstance().getPropConst().getProperty("LIMIT_CURRENCY"))) {
				statement1.setString(1, pinstid);
				try (ResultSet rs = statement1.executeQuery()) {
					while (rs.next()) {
						CURRENCY = NullReplace(rs.getString("ANSWER"));
						EXTData.put("CURRENCY", CURRENCY);
					}
				}
			}

			// lsql =
			// ReadPropertyFIle.getInstance().getPropConst().getProperty("LIMIT_AMOUNT");
			try (PreparedStatement statement1 = con
					.prepareStatement(ReadPropertyFIle.getInstance().getPropConst().getProperty("LIMIT_AMOUNT"))) {
				statement1.setString(1, pinstid);
				try (ResultSet rs = statement1.executeQuery()) {
					while (rs.next()) {
						LIMIT_AMT = NullReplace(rs.getString("ANSWER"));
						EXTData.put("LIMIT_AMT", LIMIT_AMT);
					}
				}
			}

		} catch (Exception e) {
			logger.info("OperationUtillity.getlienmodification().Exception\n" + OperationUtillity.traceException(e));
		}
		return EXTData;
	}

	public String getuserName(String pinstid) throws SQLException {

		String username = null;
		//lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("USER_NAME");
		
		try (Connection con = DBConnect.getConnection();
				PreparedStatement statement = con.prepareStatement(
						ReadPropertyFIle.getInstance().getPropConst().getProperty("USER_NAME"))) {
				statement.setString(1, pinstid);
				try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					username = rs.getString("LOCKEDBYNAME");
				}
			}

		} catch (Exception e) {
			logger.info("OperationUtillity.getuserName().Exception\n" + OperationUtillity.traceException(e));
		} 
		return username;
	}

	public static String NullReplace(String value) {
		try {
			if (value == "null" || value == null) {
				value = "";
			}
		} catch (Exception ex) {
			value = "0";
			ex.printStackTrace();
			System.out.println("NullReplace Error " + ex);
		}
		return value;
	}

	public static String traceException(Exception exception) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		exception.printStackTrace(printWriter);
		String stackTrace = stringWriter.toString();
		return " Exeption :: " + stackTrace;
	}

	public static String traceException(Exception exception, String pinstid) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		exception.printStackTrace(printWriter);
		String stackTrace = stringWriter.toString();
		return "Pinstid :: " + pinstid + " \nExeption :: " + stackTrace;
	}

	public static String fetchAcctSolidfromLsmRmSignedApiInfo(String pinstid, String accountNo) {
		String solId = "";
		try (Connection con = DBConnect.getConnection();) {
			try (PreparedStatement pst = con
					.prepareStatement("SELECT DISTINCT(SOL_ID) FROM lsm_rm_signed_api_info where pinstid="
							+ "(SELECT DISTINCT(DISHUBID) FROM LIMIT_SETUP_EXT where pinstid=?) and FIELD_VALUE =? AND SOL_ID IS NOT NULL")) {
				pst.setString(1, pinstid);
				pst.setString(2, accountNo);
				ResultSet rs = pst.executeQuery();
				if (rs.next()) {
					solId = NullReplace(rs.getString("SOL_ID"));
				}
			}
		} catch (Exception exe) {
			logger.info("Exception.OperationUtillity.fetchAcctSolidfromLsmRmSignedApiInfo():\n"
					+ OperationUtillity.traceException(exe));
		}
		return solId;
	}

	public static List<Map<String, String>> getFeeRecoveryData(String pinstid, String feeID) {

		logger.info("Entered into getFeeRecoveryData");
		List<Map<String, String>> finalMapData = new ArrayList<>();
		Map<String, String> map_data = new ConcurrentHashMap<>();
		int feeCount = getFeeCount(pinstid);

		String fetchDataQuery = "";

		if (!"".equals(feeID)) {
			feeID = "_" + feeID;
		}
		try (Connection con = DBConnect.getConnection();) {
			Map<String, String> remarkMap = getBusinessGroup(pinstid);
			finalMapData.add(remarkMap); // adding business group at 1st index
			String remark = fetchLimitNodeID(pinstid) + remarkMap.get("Business_Group");

			if (feeID.equals("_common")) {
				fetchDataQuery = "SELECT QUESTION_ID,QUESTION, ANSWER,TAB_NAME FROM LSM_MS_ANSWERS WHERE PINSTID = ? INTERSECT  SELECT QUESTION_ID,QUESTION, ANSWER,TAB_NAME FROM LSM_MS_ANSWERS WHERE (QUESTION_ID LIKE '36%' OR QUESTION_ID LIKE '37%' OR QUESTION_ID LIKE '38%'  OR QUESTION_ID LIKE '42%'  OR QUESTION_ID LIKE '44%' OR QUESTION_ID = '2')  and TAB_NAME='Fee Details'";
			} else {
				if (feeCount > 1) {
					fetchDataQuery = "SELECT QUESTION_ID,QUESTION, ANSWER FROM LSM_MS_ANSWERS WHERE PINSTID = ? and  QUESTION_ID in ( '37"
							+ feeID + "','38" + feeID + "','42" + feeID + "','44" + feeID + "','36" + feeID
							+ "','2') and TAB_NAME='Fee Details'";
				} else {
					fetchDataQuery = "SELECT QUESTION_ID,QUESTION, ANSWER FROM LSM_MS_ANSWERS WHERE PINSTID = ? and  QUESTION_ID in ( '36','37','38','42','44','2') and TAB_NAME='Fee Details'";
				}
			}

			PreparedStatement pst = con.prepareStatement(fetchDataQuery);
			pst.setString(1, pinstid);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				String qId = NullReplace(rs.getString("question_id"));
				String ans = NullReplace(rs.getString("answer"));
				map_data.put(qId, ans);
			}
			logger.info("Map Data for Fee Recovery before set--->" + map_data);
			Set<String> keySet = map_data.keySet();
			int count = 0;
			Iterator<String> itr = keySet.iterator();
			while (itr.hasNext()) {
				if (itr.next().contains("37")) {
					count++;
				}
			}

			if (feeID.equals("_common")) {

				for (int i = 1; i <= count; i++) {
					if (OperationUtillity.NullReplace(map_data.get("42")).equalsIgnoreCase("Yes")) {
						Map<String, String> feeTypeFilteredMap = new LinkedHashMap<>();
						feeTypeFilteredMap.put("Fee_to_be_recovered_from_" + i,
								OperationUtillity.NullReplace(map_data.get("36")));
						feeTypeFilteredMap.put("Fee_Type_" + i, OperationUtillity.NullReplace(map_data.get("37")));
						feeTypeFilteredMap.put("Fee_Amount_" + i, OperationUtillity.NullReplace(map_data.get("38")));
						feeTypeFilteredMap.put("Is_Amount_to_be_recovered_" + i,
								OperationUtillity.NullReplace(map_data.get("42")));
						feeTypeFilteredMap.put("Account_Number_" + i,
								OperationUtillity.NullReplace(map_data.get("44")));
						feeTypeFilteredMap.put("LSM_Number", OperationUtillity.NullReplace(map_data.get("2")));
						feeTypeFilteredMap.put("Remark", remark);
						finalMapData.add(feeTypeFilteredMap);
					} else if (OperationUtillity.NullReplace(map_data.get("42_" + i)).equalsIgnoreCase("Yes")) {
						Map<String, String> feeTypeFilteredMap = new LinkedHashMap<>();

						feeTypeFilteredMap.put("Fee_to_be_recovered_from_" + i,
								OperationUtillity.NullReplace(map_data.get("36_" + i)));
						feeTypeFilteredMap.put("Fee_Type_" + i, OperationUtillity.NullReplace(map_data.get("37_" + i)));
						feeTypeFilteredMap.put("Fee_Amount_" + i,
								OperationUtillity.NullReplace(map_data.get("38_" + i)));
						feeTypeFilteredMap.put("Is_Amount_to_be_recovered_" + i,
								OperationUtillity.NullReplace(map_data.get("42_" + i)));
						feeTypeFilteredMap.put("Account_Number_" + i,
								OperationUtillity.NullReplace(map_data.get("44_" + i)));
						feeTypeFilteredMap.put("LSM_Number", OperationUtillity.NullReplace(map_data.get("2")));
						feeTypeFilteredMap.put("Remark", remark);
						finalMapData.add(feeTypeFilteredMap);
					} // if
				} // for
			} else {
				if (OperationUtillity.NullReplace(map_data.get("42")).equalsIgnoreCase("Yes")) {
					Map<String, String> feeTypeFilteredMap = new LinkedHashMap<>();
					feeTypeFilteredMap.put("Fee_to_be_recovered_from" + feeID,
							OperationUtillity.NullReplace(map_data.get("36")));
					feeTypeFilteredMap.put("Fee_Type" + feeID, OperationUtillity.NullReplace(map_data.get("37")));
					feeTypeFilteredMap.put("Fee_Amount" + feeID, OperationUtillity.NullReplace(map_data.get("38")));
					feeTypeFilteredMap.put("Is_Amount_to_be_recovered" + feeID,
							OperationUtillity.NullReplace(map_data.get("42")));
					feeTypeFilteredMap.put("Account_Number" + feeID, OperationUtillity.NullReplace(map_data.get("44")));
					feeTypeFilteredMap.put("LSM_Number", OperationUtillity.NullReplace(map_data.get("2")));
					feeTypeFilteredMap.put("Remark", remark);
					finalMapData.add(feeTypeFilteredMap);
				} else if (OperationUtillity.NullReplace(map_data.get("42" + feeID)).equalsIgnoreCase("Yes")) {
					Map<String, String> feeTypeFilteredMap = new LinkedHashMap<>();

					feeTypeFilteredMap.put("Fee_to_be_recovered_from" + feeID,
							OperationUtillity.NullReplace(map_data.get("36" + feeID)));
					feeTypeFilteredMap.put("Fee_Type" + feeID,
							OperationUtillity.NullReplace(map_data.get("37" + feeID)));
					feeTypeFilteredMap.put("Fee_Amount" + feeID,
							OperationUtillity.NullReplace(map_data.get("38" + feeID)));
					feeTypeFilteredMap.put("Is_Amount_to_be_recovered" + feeID,
							OperationUtillity.NullReplace(map_data.get("42" + feeID)));
					feeTypeFilteredMap.put("Account_Number" + feeID,
							OperationUtillity.NullReplace(map_data.get("44" + feeID)));
					feeTypeFilteredMap.put("LSM_Number", OperationUtillity.NullReplace(map_data.get("2")));
					feeTypeFilteredMap.put("Remark", remark);
					finalMapData.add(feeTypeFilteredMap);
				} // if
			}
		} catch (Exception e) {
			logger.info("Exception in Getting fee Type Data 3 " + OperationUtillity.traceException(e));
		}

		logger.info("final map ::" + finalMapData);
		if (finalMapData.isEmpty()) {
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put("Error", "You have not enterd fee types");
			finalMapData.add(errorMap);
		}
		logger.info("getFeeRecoveryData()------>" + finalMapData);
		return finalMapData;
	}

	public static String getFeeID(String input) {
//"FEE RECOVERY SERVICE : Fee Type-2::Valuation Charges:: Event ID :: CNPROBLG";		
		return input.split(":")[1].split("-")[1].split("::")[0];
	}

	public static String getRequestUUIDFeeRecoveryFromDB(String pinstid, String facility) {
		String requestUUID = "";
		try (Connection con = DBConnect.getConnection();
				PreparedStatement pst = con.prepareStatement(
						"SELECT REQUEST FROM LSM_FI_EXECUTION_DETAILS WHERE PINSTID = ? AND  SERVICE_NAME = ? AND FACILITY = ? AND STATUS != 'SUCCESS'");) {
			pst.setString(1, pinstid);
			pst.setString(2, "FEE RECOVERY");
			pst.setString(3, facility);
			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					requestUUID = getRequestUUIDFeeRecovery(OperationUtillity.NullReplace(rs.getString("REQUEST")));
				}
			}
		} catch (SQLException e) {
			logger.error("OperationUtillity.getRequestUUIDFeeRecoveryFromTable() :: {}"
					+ OperationUtillity.traceException(e));
		}
		return requestUUID;
	}

	public static String getRequestUUIDFeeRecovery(String request) {
		String requestUUID = "";
		try {
			if (request != "" && request.contains("<RequestUUID>")) {
				requestUUID = request.substring(request.indexOf("<RequestUUID>") + "<RequestUUID>".length(),
						request.indexOf("</RequestUUID>"));
				logger.info("OperationUtillity.getRequestUUIDFeeRecovery() :: requestUUID --- >" + requestUUID);
			}
		} catch (Exception e) {
			System.out.println("OperationUtillity.getRequestUUIDFeeRecovery()");
			logger.error("OperationUtillity.getRequestUUIDFeeRecovery() :: {}", traceException(e));
		}
		return requestUUID;
	}

	public static int getFeeCount(String pinstid) {
		int feesCount = 0;
		try (Connection con = DBConnect.getConnection();
				PreparedStatement pst = con.prepareStatement(
						"SELECT COUNT (*) AS FEE_COUNT FROM LSM_MS_ANSWERS WHERE PINSTID =? AND TAB_NAME =? AND QUESTION =?");) {
			pst.setString(1, pinstid);
			pst.setString(2, "Fee Details");
			pst.setString(3, "Fee Type");
			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					feesCount = Integer.parseInt(rs.getString("FEE_COUNT"));
				}
			}
		} catch (SQLException e) {
			logger.info("info logger  get message() Exception occurred while fetching Case Type: "
					+ OperationUtillity.traceException(e));
		}
		return feesCount;
	}

	public static int getFeeServiceSuccessCount(String pinstid) {

		int feeSuccessCount = 0;
		try (Connection con = DBConnect.getConnection();
				PreparedStatement pst = con.prepareStatement(
						"SELECT COUNT (STATUS) AS FEE_SUCCESS_COUNT FROM LSM_SERVICE_REQ_RES WHERE PINSTID = ?  AND STATUS = ? AND REQUESTTYPE LIKE 'FEE RECOVERY SERVICE : Fee Type%' ");) {
			pst.setString(1, pinstid);
			pst.setString(2, "SUCCESS");
			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					feeSuccessCount = rs.getInt("FEE_SUCCESS_COUNT");
				}
			}
		} catch (Exception e) {
			logger.info("info logger  get message() Exception occurred while fetching Case Type: "
					+ OperationUtillity.traceException(e));
		}
		return feeSuccessCount;
	}

	public static void saveTxnID(String pinstid, Map<String, String> feeMap, int i) {

		try (Connection con = DBConnect.getConnection();) {
			final String insertTxnIdQuery = "INSERT INTO LSM_FEE_REC_RESP_DATA  (PINSTID,FEE_TYPE,ACCOUNT_NUMBER,TRANSACTION_ID,DATE_TIME) VALUES (?,?,?,?,SYSDATE)";
			PreparedStatement pst = con.prepareStatement(insertTxnIdQuery);
			pst.setString(1, pinstid);
			pst.setString(2, NullReplace(feeMap.get("Fee_Type_" + i)));
			pst.setString(3, NullReplace(feeMap.get("Account_Number_" + i)));
			pst.setString(4, NullReplace(feeMap.get("Transaction_Id")));
			pst.execute();
			logger.info("Fee_Type, Account_Number and Transaction_Id inserted Succesfully");
			if (pst != null) {
				pst.close();
			}
			if (con != null) {
				con.close();
			}
		} catch (Exception e) {
			logger.info("OperationUtillity.saveTxnID().Exception\n" + OperationUtillity.traceException(e));
		}
	}

	public static Map<String, String> getBusinessGroup(String pinstid) {
		Map<String, String> commonDataForFeeRecoveryMap = new HashMap<>();

		try (Connection con = DBConnect.getConnection();
				PreparedStatement pst = con.prepareStatement("SELECT DISHUBID FROM LIMIT_SETUP_EXT WHERE PINSTID = ?");
				PreparedStatement pst2 = con
						.prepareStatement("SELECT PROGRAM_NAME FROM DISBURSEMENT_EXT WHERE PINSTID = ?")) {
			pst.setString(1, pinstid);
			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					String dishubId = NullReplace(rs.getString("DISHUBID"));

					pst2.setString(1, dishubId);
					try (ResultSet rs2 = pst2.executeQuery()) {
						if (rs2.next()) {
							String businessGroup = NullReplace(rs2.getString("PROGRAM_NAME"));
							commonDataForFeeRecoveryMap.put("Business_Group", businessGroup);
						}
					}
				}
			}
		} catch (SQLException e) {
			logger.info("OperationUtillity.getBusinessGroup().Exception\n" + OperationUtillity.traceException(e));
		}
		return commonDataForFeeRecoveryMap;
	}

	public static String fetchLimitNodeID(String pinstid) {
		String limitNodeId = "";
		try (Connection con = DBConnect.getConnection();
				PreparedStatement pst = con.prepareStatement(
						"SELECT * FROM (SELECT LIMIT.QUESTION_ID LIMIT_QUESTION, LIMIT.ANSWER LIMIT_ANSWER FROM LSM_MS_ANSWERS LIMIT WHERE LIMIT.PINSTID = ? AND LIMIT.QUESTION_ID IN ( '5','6')) PIVOT (MAX ( LIMIT_ANSWER ) FOR LIMIT_QUESTION IN ( 5 AS LIMIT_PREFIX,6 AS LIMIT_SUFFIX) )")) {
			pst.setString(1, pinstid);
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					limitNodeId = ":" + NullReplace(rs.getString("LIMIT_PREFIX")) + ":" + NullReplace(rs.getString("LIMIT_SUFFIX")) + ":";
				}
			}
		} catch (Exception e) {
			logger.info(traceException(e));
		}
		return limitNodeId;
	}

	public static Map<String, String> getCaseType(String pinstid) {
		Map<String, String> caseTypeCheckMap = new HashMap<>();

		try (Connection con = DBConnect.getConnection();
				PreparedStatement pst = con
						.prepareStatement("SELECT CASETYPE FROM LIMIT_SETUP_EXT WHERE PINSTID = ?");) {
			pst.setString(1, pinstid);
			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					String CaseType = NullReplace(rs.getString("CASETYPE"));
					caseTypeCheckMap.put("CaseType", CaseType);
				}
			}
		} catch (SQLException e) {
			logger.info("OperationUtillity.getCaseType().Exception\n " + OperationUtillity.traceException(e));
		}
		return caseTypeCheckMap;
	}

	public static String getProposalType(String pinstid) {

		String proposalType = "";
		try (Connection con = DBConnect.getConnection();
				PreparedStatement pst = con.prepareStatement(
						"SELECT UPPER(PROPOSALTYPE) AS PROPOSAL_TYPE FROM LIMIT_SETUP_EXT WHERE PINSTID =?");) {
			pst.setString(1, pinstid);
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					proposalType = OperationUtillity.NullReplace(rs.getString("PROPOSAL_TYPE"));
				}
			}
		} catch (SQLException e) {
			logger.info("OperationUtillity.getProposalType(): ", OperationUtillity.traceException(e));
		}
		return proposalType;
	}

	public static String getSanctLvl(String pinstid) {
		String reasonForRenewal = "";
		String sanctLvl = "";
		String proposalType = OperationUtillity.getProposalType(pinstid);

		List<String> listProposalTypes = Arrays.asList("RENEWAL", 
				"RENEWAL WITH ENHANCEMENT OF LIMITS",
				"RENEWAL OF EXISTING LIMITS WITH BETTER TERMS TO BORROWER",
				"RENEWAL OF EXISTING LIMITS WITH NO CHANGE IN TERMS",
				"RENEWAL WITH REDUCTION IN LIMITS WITH BETTER TERMS",
				"RENEWAL WITH REDUCTION IN LIMITS WITH NO CHANGE IN TERMS",
				"RENEWAL AT EXISTING",
				"RENEWAL CUM AQR",
				"RENEWAL CUM ENHANCEMENT",
				"RENEWAL CUM REDUCTION",
				"RENEWAL NOTE",
				"RENEWAL OF EXISTING LIMITS WITH ADVERSE TERMS TO BORROWER",
				"RENEWAL WITH ENHANCEMENT",
				"RENEWAL WITH ENHANCEMENT OF LIMITS ON ADVERSE TERMS",
				"RENEWAL WITH ENHANCEMENT OF LIMITS ON SAME OR BETTER TERMS",
				"RENEWAL WITH MANUAL DATA",
				"RENEWAL WITH NO CHANGE IN OVERALL LIMITS AND ON SAME TERMS BUT WITH CHANGE IN INTER CHANGEABILITY AMONGST THE LIMITS",
				"RENEWAL WITH NO CHANGE IN OVERALL LIMITS AND ON SAME TERMS BUT WITH CHANGE IN INTERCHANGEABILITY AMONGST THE LIMITS",
				"RENEWAL WITH NO CHANGE IN OVERALL LIMITS BUT ON ADVERSE TERMS WITH CHANGE IN INTER CHANGEABILITY AMONGST THE LIMITS",
				"RENEWAL WITH NO CHANGE IN OVERALL LIMITS BUT ON ADVERSE TERMS WITH CHANGE IN INTERCHANGEABILITY AMONGST THE LIMITS",
				"RENEWAL WITH REDUCTION",
				"RENEWAL WITH REDUCTION IN LIMITS WITH ADVERSE TERMS TO BORROWER",
				"RENEWAL WITH REDUCTION IN LIMITS WITH BETTER TERMS OR NO CHANGE IN TERMS");
		
		try (Connection con = DBConnect.getConnection();
				PreparedStatement pst = con.prepareStatement(Queries.FETCH_REASON_FOR_RENEWAL);) {
			pst.setString(1, pinstid);
			pst.setString(2, "55");
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					reasonForRenewal = Optional.ofNullable(rs.getString("REASON_FOR_RENEWAL")).orElse("");
				}
				if ("Fresh Sanction".equalsIgnoreCase(proposalType)) {
					sanctLvl = "FRESH";
				} else if ("Pure Enhancement".equalsIgnoreCase(proposalType)) {
					sanctLvl = "ENHAN";
				} else if ((listProposalTypes.contains(proposalType.toUpperCase()))
						&& ("Restriction upto LAD".equalsIgnoreCase(reasonForRenewal))) {
					sanctLvl = "LAREN";
				} else if ((listProposalTypes.contains(proposalType.toUpperCase()))
						&& ("Simplified renewal".equalsIgnoreCase(reasonForRenewal))) {
					sanctLvl = "MIREN";
//				} 
//				else if ((listProposalTypes.contains(proposalType.toUpperCase()))
//						&& (("Select".equalsIgnoreCase(reasonForRenewal) || "".equalsIgnoreCase(reasonForRenewal)))) {
//					sanctLvl = "RENEW";
//				}
				}else if ((listProposalTypes.contains(proposalType.toUpperCase()))
						&& (("Select".equalsIgnoreCase(reasonForRenewal) || "".equalsIgnoreCase(reasonForRenewal) || "For the Compliance of sanction condition".equalsIgnoreCase(reasonForRenewal)))) {
					sanctLvl = "RENEW";
				}
			}
		} catch (SQLException e) {
			logger.error("OperationUtillity.getReasonForRenewal(){}", OperationUtillity.traceException(e));
		}
		return sanctLvl;
	}

	public static String traceException(String pinstid, Exception exception) {
		StringBuilder stackTrace = new StringBuilder();
		stackTrace.append(System.lineSeparator() + "EXCEPTION (PINSTID =" + pinstid + ")");
		stackTrace.append(System.lineSeparator() + "Message : " + exception);
		StackTraceElement[] elementArr = Thread.currentThread().getStackTrace();
		for (int i = 0; i < elementArr.length; i++) {
			if (elementArr[i].toString().startsWith("com.LsmFiServices")
					|| elementArr[i].toString().contains("com.LsmFiServices")) {
				stackTrace.append(System.lineSeparator() + elementArr[i + 1].toString());
			}
		}
		return stackTrace.toString();
	}

	public static Object insrtLtstRcrdForLsmSrvic(String request, String accOpeningRes, String reqType, String pinstId,
			Map<String, String> apiReqRes, String requestUuid, Connection con) throws SQLException {

		String insrtQuery = "INSERT INTO LSM_SERVICE_LATEST_REQ_RES "
				+ "(PINSTID, REQUEST_MESSAGE, RESPONSE_MESSSAGE, REQUESTTYPE, STATUS, DATETIME, RECORDID, MESSAGE) "
				+ "VALUES (?,?,?,?,?,SYSDATE, AUTO_INCREMENT_RECORDID.nextval,?)";
		String deleteQuery = "DELETE FROM LSM_SERVICE_LATEST_REQ_RES WHERE PINSTID=? AND REQUESTTYPE=?";

			try (PreparedStatement pstmt = con.prepareStatement(deleteQuery)) {
				pstmt.setString(1, pinstId);
				pstmt.setString(2, reqType);
				pstmt.executeUpdate();
			} catch (Exception e) {
				logger.info("\nOperationUtillity.insrtLtstRcrdForLsmSrvic().deleteQuery : "
						+ OperationUtillity.traceException(pinstId, e));
			}

			try (PreparedStatement statement = con.prepareStatement(insrtQuery)) {
				statement.setString(1, NullReplace(pinstId));
				statement.setString(2, NullReplace(request));
				statement.setString(3, NullReplace(accOpeningRes));
				statement.setString(4, NullReplace(reqType));
				statement.setString(5, NullReplace(apiReqRes.get("Status")));

				if (NullReplace(reqType).contains("FEE RECOVERY")
						&& NullReplace(apiReqRes.get("Status")).equals("SUCCESS")) {
					statement.setString(6, NullReplace(apiReqRes.get("Response1")));
				} else if (NullReplace(reqType).contains("FEE RECOVERY")
						&& NullReplace(apiReqRes.get("Status")).equals("FAILED")) {
					statement.setString(6, NullReplace(apiReqRes.get("Response1")));
				} else {
					statement.setString(6, NullReplace(apiReqRes.get("ErrorDesc")));
				}
				int intCount = statement.executeUpdate();

				if (intCount > 0) {
//					con.commit();
					return "" + intCount + " row inserted for " + request + "";
				}

			} catch (Exception e) {
				logger.info("\nOperationUtillity.insrtLtstRcrdForLsmSrvic().insrtQuery : "+ OperationUtillity.traceException(pinstId, e));
			}
		return "";
	}

	public static Map<String, String> insertFiReqResMonitoring(String request, String account_Opening_Response,
			String req_type, String PINSTID, Map<String, String> API_REQ_RES_map, String requestUUID)
			throws SQLException {

		Map<String, String> EXTData = new HashMap<>();

		//lsql = "INSERT INTO MONT_SERVICE_REQ_RES (PINSTID, REQUEST_MESSAGE, RESPONSE_MESSSAGE, REQUESTTYPE, STATUS, DATETIME, RECORDID, MESSAGE) VALUES (?,?,?,?,?,SYSDATE, AUTO_INCREMENT_RECORDID.nextval,?)";

		try (Connection con = DBConnect.getConnection();
				PreparedStatement statement = con.prepareStatement("INSERT INTO MONT_SERVICE_REQ_RES (PINSTID, REQUEST_MESSAGE, RESPONSE_MESSSAGE, REQUESTTYPE, STATUS, DATETIME, RECORDID, MESSAGE) VALUES (?,?,?,?,?,SYSDATE, AUTO_INCREMENT_RECORDID.nextval,?)");) {
				statement.setString(1, NullReplace(PINSTID));
				statement.setString(2, NullReplace(request));
				statement.setString(3, NullReplace(account_Opening_Response));
				statement.setString(4, NullReplace(req_type));
				statement.setString(5, NullReplace(API_REQ_RES_map.get("Status")));
				statement.setString(6, NullReplace(API_REQ_RES_map.get("ErrorDesc")));
				int int_count = statement.executeUpdate();
		    logger.info("OperationUtillity.insertFiReqResMonitoring().int_count"+ int_count);
		} catch (Exception e) {
		    logger.info("OperationUtillity.insertFiReqResMonitoring().exception " + OperationUtillity.traceException(e));
		} 
		return EXTData;
	}

	public static Object insrtLtstRcrdForMonitoringSrvic(String request, String accOpeningRes, String reqType,
			String pinstId, Map<String, String> apiReqRes, String requestUuid, Connection con) throws SQLException {

		String insrtQuery = "INSERT INTO MONT_SERVICE_LTST_REQ_RES "
				+ "(PINSTID, REQUEST_MESSAGE, RESPONSE_MESSSAGE, REQUESTTYPE, STATUS, DATETIME, RECORDID, MESSAGE) "
				+ "VALUES (?,?,?,?,?,SYSDATE, AUTO_INCREMENT_RECORDID.nextval,?)";
		String deleteQuery = "DELETE FROM MONT_SERVICE_LTST_REQ_RES WHERE PINSTID=? AND REQUESTTYPE=?";

			try (PreparedStatement pstmt = con.prepareStatement(deleteQuery)) {
				pstmt.setString(1, pinstId);
				pstmt.setString(2, reqType);
				pstmt.executeUpdate();
			} catch (Exception e) {
				logger.info("\nOperationUtillity.insrtLtstRcrdForLsmSrvic().deleteQuery : "+ OperationUtillity.traceException(pinstId, e));
			}

			try (PreparedStatement statement = con.prepareStatement(insrtQuery)) {
				statement.setString(1, NullReplace(pinstId));
				statement.setString(2, NullReplace(request));
				statement.setString(3, NullReplace(accOpeningRes));
				statement.setString(4, NullReplace(reqType));
				statement.setString(5, NullReplace(apiReqRes.get("Status")));
				statement.setString(6, NullReplace(apiReqRes.get("ErrorDesc")));
				int intCount = statement.executeUpdate();

				if (intCount > 0) {
					return "" + intCount + " row inserted for " + request + "";
				}
			} catch (Exception e) {
				logger.info("\nOperationUtillity.insrtLtstRcrdForLsmSrvic().insrtQuery : "+ OperationUtillity.traceException(pinstId, e));
			}
		return "";
	}


	public static SOAPMessage convertStringToSoapMessage(String stringMessage) {
		MessageFactory factory;
		SOAPMessage message = null;
		try {
			factory = MessageFactory.newInstance();
			message = factory.createMessage(null, new ByteArrayInputStream(stringMessage.getBytes()));
		} catch (SOAPException | IOException e) {
			logger.info("OperationUtillity.convertStringToSoapMessage()->" + OperationUtillity.traceException(e));
		}
		return message;
	}

	// SN OF MAHESHV 10122024
	public static Object insertForDPCheckService(String pinstid, String acc_no, String DPamount, String status)
			throws SQLException {

		String insrtQuery = "INSERT INTO LSM_DP_CHK_DATA (PINSTID,ACC_NO,DP_AMOUNT,EXE_DATE,STATUS)"
				+ "VALUES (?,?,?,SYSDATE,?)";
		String deleteQuery = "DELETE FROM LSM_DP_CHK_DATA WHERE PINSTID=? AND ACC_NO=? ";

		try (Connection con = DBConnect.getConnection(); PreparedStatement pstmt = con.prepareStatement(deleteQuery)) {
			pstmt.setString(1, pinstid);
			pstmt.setString(2, acc_no);
			int i = pstmt.executeUpdate();
			logger.info("\nOperationUtillity.insertForDPCheckService().deleted rows : " + i);
		} catch (Exception e) {
			logger.info("\nException.OperationUtillity.insertForDPCheckService().deleteQuery : "
					+ OperationUtillity.traceException(pinstid, e));
		}

		try (Connection con = DBConnect.getConnection();
				PreparedStatement statement = con.prepareStatement(insrtQuery)) {
			statement.setString(1, NullReplace(pinstid));
			statement.setString(2, NullReplace(acc_no));
			statement.setString(3, NullReplace(DPamount));
			statement.setString(4, NullReplace(status));
			int intCount = statement.executeUpdate();

			if (intCount > 0) {
				return "" + intCount + " row inserted for " + acc_no + "";
			}
		} catch (Exception e) {
			logger.info("Exception.OperationUtillity.insertForDPCheckService().insrtQuery : "+ OperationUtillity.traceException(pinstid, e));
		}
		return "";
	}

	// EN OF MAHESHV 10122024

	public static Map<String, String> fetchLimitPrefixSuffix(String pinstid, String facility) {
		Map<String, String> prefixSuffixMap = new HashMap<>();
		prefixSuffixMap.put("limitPrefix", "");
		prefixSuffixMap.put("limitSuffix", "");
		String statement = "SELECT * FROM (SELECT LIMIT.QUESTION_ID LIMIT_QUESTION, LIMIT.ANSWER LIMIT_ANSWER, LIMIT.FACILITY_NAME FACILITY_NAME FROM LSM_LIMIT_ANSWERS LIMIT WHERE LIMIT.PINSTID = ? AND FACILITY_NAME= ? AND LIMIT.QUESTION_ID IN ('155','156')) PIVOT (MAX ( LIMIT_ANSWER ) FOR LIMIT_QUESTION IN (155 AS LIMIT_PREFIX, 156 AS LIMIT_SUFFIX ))";
		try (Connection con = DBConnect.getConnection(); PreparedStatement pst = con.prepareStatement(statement)) {
			pst.setString(1, NullReplace(pinstid));
			pst.setString(2, NullReplace(facility));
			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					prefixSuffixMap.put("limitPrefix", NullReplace(rs.getString("LIMIT_PREFIX")));
					prefixSuffixMap.put("limitSuffix", NullReplace(rs.getString("LIMIT_SUFFIX")));
				}
			}
		} catch (Exception e) {
			logger.error("OperationUtillity.fetchLimitPrefixSuffix():: {}", OperationUtillity.traceException(e));
		}
		return prefixSuffixMap;
	}
	
	public static String getChildLimitAction(String pinstid, String facility) { // ICO - 10080
		String ChildAction = "";
		try (Connection con = DBConnect.getConnection();
				PreparedStatement pst = con.prepareStatement(
						"SELECT CASE WHEN REQUESTTYPE LIKE '%CREATION%' THEN 'E' WHEN REQUESTTYPE LIKE '%MODIFICATION%' THEN 'M' ELSE '' END AS REQUESTTYPE FROM LSM_SERVICE_REQ_RES WHERE PINSTID =  ? AND REQUESTTYPE LIKE ? ");) {
			pst.setString(1, pinstid);
			pst.setString(2, "CHILD LIMIT NODE%"+ facility +"%");
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					ChildAction = rs.getString("REQUESTTYPE");
				}
			}
			logger.info("OperationUtillity.getChildLimitAction :: ChildAction :: "+ChildAction);
		} catch (SQLException e) {
			logger.info("OperationUtillity.getChildLimitAction :: Exception :: "+ OperationUtillity.traceException(e));
		}
		return ChildAction;
	}
}