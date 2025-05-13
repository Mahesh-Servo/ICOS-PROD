package com.example.masIntg.utility;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import com.example.masIntg.utility.DBConnect;

@Controller
public class OperationUtillity {
	
	private static final Logger logger = LoggerFactory.getLogger(OperationUtillity.class);
	
    Connection con = null;
	PreparedStatement statement = null;
	ResultSet rs =null;
	
	
	public Map<String, String> GetExtData(String pinstid) throws SQLException {
		Map<String, String> EXTData = new HashMap<>();
		
		String SL_Limit = null;
		String SL_docDate = null;
		String SL_expDate = null;
		String SL_RefNo = null;
		String lsql = null;
		try {
			
		con = DBConnect.getConnection();
		System.out.println("DB connected Successfully");
		if(con==null) {
			EXTData.put("Result", "Fail");
			EXTData.put("Message", "DB connection not established");
			return EXTData;
		}
		else {
			
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("SL_SancLimit");
			logger.info("Query SL_SancLimit --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
		    rs = statement.executeQuery();
			
			while(rs.next()) {
			SL_Limit = rs.getString("ANSWER");
		    EXTData.put("SL_Limit", SL_Limit);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("SL_docDate");
			logger.info("Query SL_docDate --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				SL_docDate	= rs.getString("ANSWER");
		    EXTData.put("SL_docDate", SL_docDate);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("SL_expDate");
			logger.info("Query SL_expDate --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				SL_expDate	= rs.getString("ANSWER");
		    EXTData.put("SL_expDate", SL_expDate);
			}
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("SL_SanctionReference");
			logger.info("Query SL_SanctionReference --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				SL_RefNo	= rs.getString("ANSWER");
		    EXTData.put("SL_RefNo", SL_RefNo);
			}
			
		}
		
		}
		catch(Exception e) {
			
			logger.info("Error occurred while fetching values for SL limit fom DB"+e);
		}
		finally {
			if(statement!=null) {
				statement.close();
			}
			if(rs!=null) {
				rs.close();
			}
			if(con!=null) {
				con.close();
			}
		}
		
		return EXTData;
		
	}
	
	public Map<String, String> GetIRExtData(String pinstid) throws SQLException {
		Map<String, String> EXTData = new HashMap<>();
		
		String IR_Spread = null;
		String IR_StartDate = null;

		String lsql = null;
		try {
			
		con = DBConnect.getConnection();
		System.out.println("DB connected Successfully");
		if(con==null) {
			EXTData.put("Result", "Fail");
			EXTData.put("Message", "DB connection not established");
			return EXTData;
		}
		else {
			
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("IR_Spread");
			logger.info("Query IR_Spread --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
		    rs = statement.executeQuery();
			
			while(rs.next()) {
			IR_Spread = rs.getString("ANSWER");
		    EXTData.put("IR_Spread", IR_Spread);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("IR_StartDate");
			logger.info("Query IR_StartDate --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				IR_StartDate	= rs.getString("ANSWER");
		    EXTData.put("IR_StartDate", IR_StartDate);
			}
		}
		
		}
		catch(Exception e) {
			
			logger.info("Error occurred while fetching values for SL limit fom DB"+e);
		}
		finally {
			if(statement!=null) {
				statement.close();
			}
			if(rs!=null) {
				rs.close();
			}
			if(con!=null) {
				con.close();
			}
		}
		
		return EXTData;
		
	}
	
	public Map<String, String> GetDPSExtData(String pinstid) throws SQLException {
		Map<String, String> EXTData = new HashMap<>();
		
		String DPS_Security = null;

		String lsql = null;
		try {
			
		con = DBConnect.getConnection();
		System.out.println("DB connected Successfully");
		if(con==null) {
			EXTData.put("Result", "Fail");
			EXTData.put("Message", "DB connection not established");
			return EXTData;
		}
		else {
			
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("DPS_Security");
			logger.info("Query DPS_Security --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
		    rs = statement.executeQuery();
			
			while(rs.next()) {
			DPS_Security = rs.getString("ANSWER");
		    EXTData.put("DPS_Security", DPS_Security);
			}
			
			
		}
		
		}
		catch(Exception e) {
			
			logger.info("Error occurred while fetching values for SL limit fom DB"+e);
		}
		finally {
			if(statement!=null) {
				statement.close();
			}
			if(rs!=null) {
				rs.close();
			}
			if(con!=null) {
				con.close();
			}
		}
		
		return EXTData;
		
	}
	
	public Map<String, String> GetCustInqExtData(String pinstid) throws SQLException {
		Map<String, String> EXTData = new HashMap<>();
		
		String CI_CustomerId = null;

		String lsql = null;
		try {
			
		con = DBConnect.getConnection();
		System.out.println("DB connected Successfully");
		if(con==null) {
			EXTData.put("Result", "Fail");
			EXTData.put("Message", "DB connection not established");
			return EXTData;
		}
		else {
			
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("CI_CustomerId");
			logger.info("Query CI_CustomerId --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
		    rs = statement.executeQuery();
			
			while(rs.next()) {
			CI_CustomerId = rs.getString("ANSWER");
		    EXTData.put("CI_CustomerId", CI_CustomerId);
			}
			
			
		}
		
		}
		catch(Exception e) {
			
			logger.info("Error occurred while fetching values for SL limit fom DB"+e);
		}
		finally {
			if(statement!=null) {
				statement.close();
			}
			if(rs!=null) {
				rs.close();
			}
			if(con!=null) {
				con.close();
			}
		}
		
		return EXTData;
		
	}

	public Map<String, String> GetACCIExtData(String pinstid) throws SQLException {
		Map<String, String> EXTData = new HashMap<>();
		
		String ACCI_AccountNo = null;

		String lsql = null;
		try {
			
		con = DBConnect.getConnection();
		System.out.println("DB connected Successfully");
		if(con==null) {
			EXTData.put("Result", "Fail");
			EXTData.put("Message", "DB connection not established");
			return EXTData;
		}
		else {
			
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("ACCI_AccountNo");
			logger.info("Query ACCI_AccountNo --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
		    rs = statement.executeQuery();
			
			while(rs.next()) {
				ACCI_AccountNo = rs.getString("ANSWER");
		    EXTData.put("ACCI_AccountNo", ACCI_AccountNo);
			}
			
			
		}
		
		}
		catch(Exception e) {
			
			logger.info("Error occurred while fetching values for SL limit fom DB"+e);
		}
		finally {
			if(statement!=null) {
				statement.close();
			}
			if(rs!=null) {
				rs.close();
			}
			if(con!=null) {
				con.close();
			}
		}
		
		return EXTData;
		
	}
	
	public Map<String, String> GetBDExtData(String pinstid) throws SQLException {
		Map<String, String> EXTData = new HashMap<>();
		
		String BD_Limit = null;
		String BD_SancDate = null;
		String BD_expDate = null;
		String BD_currCode = null;
		String lsql = null;
		try {
			
		con = DBConnect.getConnection();
		System.out.println("DB connected Successfully");
		if(con==null) {
			EXTData.put("Result", "Fail");
			EXTData.put("Message", "DB connection not established");
			return EXTData;
		}
		else {
			
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("BD_Limit");
			logger.info("Query BD_Limit --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
		    rs = statement.executeQuery();
			
			while(rs.next()) {
				BD_Limit = rs.getString("ANSWER");
		    EXTData.put("BD_Limit", BD_Limit);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("BD_SancDate");
			logger.info("Query BD_SancDate --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				BD_SancDate	= rs.getString("ANSWER");
		    EXTData.put("BD_SancDate", BD_SancDate);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("BD_expDate");
			logger.info("Query BD_expDate --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				BD_expDate	= rs.getString("ANSWER");
		    EXTData.put("BD_expDate", BD_expDate);
			}
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("BD_currCode");
			logger.info("Query BD_currCode --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				BD_currCode	= rs.getString("ANSWER");
		    EXTData.put("BD_currCode", BD_currCode);
			}
			
		}
		
		}
		catch(Exception e) {
			
			logger.info("Error occurred while fetching values for SL limit fom DB"+e);
		}
		finally {
			if(statement!=null) {
				statement.close();
			}
			if(rs!=null) {
				rs.close();
			}
			if(con!=null) {
				con.close();
			}
		}
		
		return EXTData;
		
	}
	
	public Map<String, String> GetLCMExtData(String pinstid) throws SQLException {
		Map<String, String> EXTData = new HashMap<>();
		
		String LCM_nameBank = null;

		String lsql = null;
		try {
			
		con = DBConnect.getConnection();
		System.out.println("DB connected Successfully");
		if(con==null) {
			EXTData.put("Result", "Fail");
			EXTData.put("Message", "DB connection not established");
			return EXTData;
		}
		else {
			
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("LCM_nameBank");
			logger.info("Query LCM_nameBank --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
		    rs = statement.executeQuery();
			
			while(rs.next()) {
				LCM_nameBank = rs.getString("ANSWER");
		    EXTData.put("LCM_nameBank", LCM_nameBank);
			}
			
			
		}
		
		}
		catch(Exception e) {
			
			logger.info("Error occurred while fetching values for SL limit fom DB"+e);
		}
		finally {
			if(statement!=null) {
				statement.close();
			}
			if(rs!=null) {
				rs.close();
			}
			if(con!=null) {
				con.close();
			}
		}
		
		return EXTData;
		
	}
	
	public Map<String, String> GetLEIExtData(String pinstid) throws SQLException {
		Map<String, String> EXTData = new HashMap<>();
		
		String LEINumber = null;

		String lsql = null;
		try {
			
		con = DBConnect.getConnection();
		System.out.println("DB connected Successfully");
		if(con==null) {
			EXTData.put("Result", "Fail");
			EXTData.put("Message", "DB connection not established");
			return EXTData;
		}
		else {
			
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("LEINumber");
			logger.info("Query LEINumber --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
		    rs = statement.executeQuery();
			
			while(rs.next()) {
				LEINumber = NullReplace(rs.getString("ANSWER"));
		    EXTData.put("LEINumber", LEINumber);
			}
			
			
		}
		
		}
		catch(Exception e) {
			
			logger.info("Error occurred while fetching values for LEI data fom DB"+e.fillInStackTrace());
		}
		finally {
			if(statement!=null) {
				statement.close();
			}
			if(rs!=null) {
				rs.close();
			}
			if(con!=null) {
				con.close();
			}
		}
		
		return EXTData;
		
	}
	
	public Map<String, String> GetDPANExtData(String pinstid) throws SQLException {
		Map<String, String> EXTData = new HashMap<>();
		
		String DPAN_AccountNo = null;

		String lsql = null;
		try {
			
		con = DBConnect.getConnection();
		System.out.println("DB connected Successfully");
		if(con==null) {
			EXTData.put("Result", "Fail");
			EXTData.put("Message", "DB connection not established");
			return EXTData;
		}
		else {
			
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("DPAN_AccountNo");
			logger.info("Query DPAN_AccountNo --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
		    rs = statement.executeQuery();
			
			while(rs.next()) {
				DPAN_AccountNo = NullReplace(rs.getString("ANSWER"));
		    EXTData.put("DPAN_AccountNo", DPAN_AccountNo);
			}
			
			
		}
		
		}
		catch(Exception e) {
			
			logger.info("Error occurred while fetching values for DPAN data fom DB"+e.fillInStackTrace());
		}
		finally {
			if(statement!=null) {
				statement.close();
			}
			if(rs!=null) {
				rs.close();
			}
			if(con!=null) {
				con.close();
			}
		}
		
		return EXTData;
		
	}

	public Map<String, String> GetLCExtData(String pinstid) throws SQLException {
		Map<String, String> EXTData = new HashMap<>();
		
		String LC_sppiCrit = null;
		String LC_SancAuthCode = null;

		String lsql = null;
		try {
			
		con = DBConnect.getConnection();
		System.out.println("DB connected Successfully");
		if(con==null) {
			EXTData.put("Result", "Fail");
			EXTData.put("Message", "DB connection not established");
			return EXTData;
		}
		else {
			
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("LC_sppiCrit");
			logger.info("Query LC_sppiCrit --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
		    rs = statement.executeQuery();
			
			while(rs.next()) {
				LC_sppiCrit = NullReplace(rs.getString("LC_sppiCrit"));
		    EXTData.put("LC_sppiCrit", LC_sppiCrit);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("LC_SancAuthCode");
			logger.info("Query LC_SancAuthCode --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				LC_SancAuthCode	= NullReplace(rs.getString("ANSWER"));
		    EXTData.put("LC_SancAuthCode", LC_SancAuthCode);
			}
		}
		
		}
		catch(Exception e) {
			
			logger.info("Error occurred while fetching values for SL limit fom DB"+e);
		}
		finally {
			if(statement!=null) {
				statement.close();
			}
			if(rs!=null) {
				rs.close();
			}
			if(con!=null) {
				con.close();
			}
		}
		
		return EXTData;
		
	}
	
	public Map<String, String> GetExtLimitNodeModifyData(String pinstid) throws SQLException {
		Map<String, String> EXTData = new HashMap<>();
		
		String cifId = null;
		String limitDesc = null;
		String limitExpiryDate = null;
		String limitPrefix = null;
		String limitSanctDate = null;
		String limitSuffix = null;
		String parentLimitPrefix = null;
		String parentLimitSuffix = null;
		String facAggAmt = null;
		String facExpDate = null;
		String facSanctDate = null;

		String lsql = null;
		try {
			
		con = DBConnect.getConnection();
		System.out.println("DB connected Successfully");
		if(con==null) {
			EXTData.put("Result", "Fail");
			EXTData.put("Message", "DB connection not established");
			return EXTData;
		}
		else {
			
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("cifId");
			logger.info("Query cifId --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
		    rs = statement.executeQuery();
			
			while(rs.next()) {
				cifId = NullReplace(rs.getString("ANSWER"));
		    EXTData.put("cifId", cifId);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("childlimitDesc");
			logger.info("Query childlimitDesc --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				limitDesc	= NullReplace(rs.getString("ANSWER"));
		    EXTData.put("limitDesc", limitDesc);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("limitExpiryDate");
			logger.info("Query limitExpiryDate --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				limitExpiryDate	= NullReplace(rs.getString("ANSWER"));
		    EXTData.put("limitExpiryDate", limitExpiryDate);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("limitPrefix");
			logger.info("Query limitPrefix --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				limitPrefix	= NullReplace(rs.getString("ANSWER"));
		    EXTData.put("limitPrefix", limitPrefix);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("limitSanctDate");
			logger.info("Query limitSanctDate --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				limitSanctDate	= NullReplace(rs.getString("ANSWER"));
		    EXTData.put("limitSanctDate", limitSanctDate);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("limitSuffix");
			logger.info("Query limitSuffix --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				limitSuffix	= NullReplace(rs.getString("ANSWER"));
		    EXTData.put("limitSuffix", limitSuffix);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}

			
		}
		
		}
		catch(Exception e) {
			e.printStackTrace();
			logger.info("Error occurred while fetching values for Limit Node Modify fom DB"+e.fillInStackTrace());
		}
		finally {
			if(statement!=null) {
				statement.close();
			}
			if(rs!=null) {
				rs.close();
			}
			if(con!=null) {
				con.close();
			}
		}
		
		return EXTData;
		
	}

	public Map<String, String> GetParentLimitNodeData(String pinstid) throws SQLException {
		Map<String, String> EXTData = new HashMap<>();
		
		String cifId = null;
		String limitDesc = null;
		String limitExpiryDate = null;
		String limitPrefix = null;
		String limitSanctDate = null;
		String limitSuffix = null;
		String parentAmount = null;
		

		String lsql = null;
		try {
			
		con = DBConnect.getConnection();
		System.out.println("DB connected Successfully");
		if(con==null) {
			EXTData.put("Result", "Fail");
			EXTData.put("Message", "DB connection not established");
			return EXTData;
		}
		else {
			
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("cifId");
			logger.info("Query cifId --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
		    rs = statement.executeQuery();
			
			while(rs.next()) {
				cifId = NullReplace(rs.getString("ANSWER"));
		    EXTData.put("cifId", cifId);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("parentlimitDesc");
			logger.info("Query parentlimitDesc --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				limitDesc	= NullReplace(rs.getString("COMPANYNAME"));
		    EXTData.put("limitDesc", limitDesc);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("parentlimitExpiryDate");
			logger.info("Query parentlimitExpiryDate --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				limitExpiryDate	= NullReplace(rs.getString("ANSWER"));
		    EXTData.put("limitExpiryDate", limitExpiryDate);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("parentLimitPrefix");
			logger.info("Query parentLimitPrefix --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				limitPrefix	= NullReplace(rs.getString("ANSWER"));
		    EXTData.put("limitPrefix", limitPrefix);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("parentSanDate");
			logger.info("Query parentSanDate --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				limitSanctDate	= NullReplace(rs.getString("ANSWER"));
		    EXTData.put("limitSanctDate", limitSanctDate);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("parentLimitSuffix");
			logger.info("Query parentLimitSuffix --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				limitSuffix	= NullReplace(rs.getString("ANSWER"));
		    EXTData.put("limitSuffix", limitSuffix);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("parentAmtvalue");
			logger.info("Query parentAmtvalue --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
			parentAmount	= NullReplace(rs.getString("ANSWER"));
		    EXTData.put("parentAmount", parentAmount);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}

			
		}
		
		}
		catch(Exception e) {
			e.printStackTrace();
			logger.info("Error occurred while fetching values for Parent Node Create fom DB"+e.fillInStackTrace());
		}
		finally {
			if(statement!=null) {
				statement.close();
			}
			if(rs!=null) {
				rs.close();
			}
			if(con!=null) {
				con.close();
			}
		}
		
		return EXTData;
		
	}
	
	public Map<String, String> GetChildLimitNodeData(String pinstid) throws SQLException {
		Map<String, String> EXTData = new HashMap<>();
		
		String cifId = null;
		String limitDesc = null;
		String limitExpiryDate = null;
		String limitPrefix = null;
		String parentlimitPrefix = null;
		String limitSanctDate = null;
		String limitSuffix = null;
		String parentlimitSuffix = null;
		String lsql = null;
		String childAmount = null;
		
		try {
			
		con = DBConnect.getConnection();
		System.out.println("DB connected Successfully");
		if(con==null) {
			EXTData.put("Result", "Fail");
			EXTData.put("Message", "DB connection not established");
			return EXTData;
		}
		else {
			
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("cifId");
			logger.info("Query cifId --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
		    rs = statement.executeQuery();
			
			while(rs.next()) {
				cifId = NullReplace(rs.getString("ANSWER"));
		    EXTData.put("cifId", cifId);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("childlimitDesc");
			logger.info("Query childlimitDesc --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				limitDesc	= NullReplace(rs.getString("ANSWER"));
		    EXTData.put("limitDesc", limitDesc);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("childFacilityExpDate");
			logger.info("Query childFacilityExpDate --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				limitExpiryDate	= NullReplace(rs.getString("ANSWER"));
		    EXTData.put("limitExpiryDate", limitExpiryDate);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("limitPrefix");
			logger.info("Query limitPrefix --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				limitPrefix	= NullReplace(rs.getString("ANSWER"));
		    EXTData.put("limitPrefix", limitPrefix);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("limitSanctDate");
			logger.info("Query limitSanctDate --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				limitSanctDate	= NullReplace(rs.getString("ANSWER"));
		    EXTData.put("limitSanctDate", limitSanctDate);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("limitSuffix");
			logger.info("Query limitSuffix --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				limitSuffix	= NullReplace(rs.getString("ANSWER"));
		    EXTData.put("limitSuffix", limitSuffix);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
            
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("childAmtvalue");
			logger.info("Query childAmtvalue --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				childAmount	= NullReplace(rs.getString("ANSWER"));
		    EXTData.put("childAmount", childAmount);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("parentLimitPrefix");
			logger.info("Query parentLimitPrefix --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				parentlimitPrefix	= NullReplace(rs.getString("ANSWER"));
		    EXTData.put("parentlimitPrefix", parentlimitPrefix);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("parentLimitSuffix");
			logger.info("Query parentLimitSuffix --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
			rs = statement.executeQuery();
			
			while(rs.next()) {
				parentlimitSuffix	= NullReplace(rs.getString("ANSWER"));
		    EXTData.put("parentlimitSuffix", parentlimitSuffix);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			
		}
		
		}
		catch(Exception e) {
			e.printStackTrace();
			logger.info("Error occurred while fetching values for Limit Node Modify fom DB"+e.fillInStackTrace());
		}
		finally {
			if(statement!=null) {
				statement.close();
			}
			if(rs!=null) {
				rs.close();
			}
			if(con!=null) {
				con.close();
			}
		}
		
		return EXTData;
		
	}

//	public static String soapMessageToString(SOAPMessage message) {
//		
//		String result = null;
//		
//		if(message != null) {
//			ByteArrayOutputStream baos = null;
//			try {
//				baos = new ByteArrayOutputStream();
//				message.writeTo(baos);
//				result = baos.toString();
//			}
//			catch(Exception e) {
//				System.out.println("soapMessageToString Error "+ e);
//			}
//			finally {
//				if (baos != null) {
//					try {
//						baos.close();
//					}
//					catch (Exception e) {
//						System.out.println("soapMessageToString closing connection Error "+ e);
//					}
//				}
//			}
//		}
//		return result;
//	}
	
	public Map<String, String> GetURCCExtData(String pinstid) throws SQLException {
		Map<String, String> EXTData = new HashMap<>();
		
		String URCCNumber = null;
		String lsql = null;
		
		try {
			
		con = DBConnect.getConnection();
		System.out.println("DB connected Successfully");
		if(con==null) {
			EXTData.put("Result", "Fail");
			EXTData.put("Message", "DB connection not established");
			return EXTData;
		}
		else {
			
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("URCNumber");
			logger.info("Query URCCNumber --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
		    rs = statement.executeQuery();
			
			while(rs.next()) {
				URCCNumber = rs.getString("ANSWER");
		    EXTData.put("URCCNumber", URCCNumber);
			}
			
			if(statement!=null) {
				statement.close();
			}
			
			if(rs!=null) {
				rs.close();
			}
			
		}
		
		}
		catch(Exception e) {
			
			logger.info("Error occurred while fetching values for GetURCCExtData fom DB"+e);
		}
		finally {
			if(statement!=null) {
				statement.close();
			}
			if(rs!=null) {
				rs.close();
			}
			if(con!=null) {
				con.close();
			}
		}
		
		return EXTData;
		
	}
	
	public Map<String, String> GetcifIdData(String pinstid) throws SQLException {
		Map<String, String> EXTData = new HashMap<>();
		
		String  cifId = null;

		String lsql = null;
		try {
			
		con = DBConnect.getConnection();
		System.out.println("DB connected Successfully");
		if(con==null) {
			EXTData.put("Result", "Fail");
			EXTData.put("Message", "DB connection not established");
			return EXTData;
		}
		else {
			
			lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("cifId");
			logger.info("Query cifId --> "+lsql);
			statement = con.prepareStatement(lsql);
			statement.setString(1, pinstid);
		    rs = statement.executeQuery();
			
			while(rs.next()) {
				cifId = rs.getString("ANSWER");
		    EXTData.put("cifId", cifId);
			}
			
			
		}
		
		}
		catch(Exception e) {
			
			logger.info("Error occurred while fetching values for SL limit fom DB"+e);
		}
		finally {
			if(statement!=null) {
				statement.close();
			}
			if(rs!=null) {
				rs.close();
			}
			if(con!=null) {
				con.close();
			}
		}
		
		return EXTData;
		
	}
	
	public static String NullReplace(String value) {
		try {
			if(value == "null" || value == null) {
				value = "";
			}
		} catch(Exception ex) {
			value = "0";
			ex.printStackTrace();
			System.out.println("NullReplace Error "+ ex);
			logger.info("Error occurred NullReplace "+ex.fillInStackTrace());
		}
		return value;
	}

}

