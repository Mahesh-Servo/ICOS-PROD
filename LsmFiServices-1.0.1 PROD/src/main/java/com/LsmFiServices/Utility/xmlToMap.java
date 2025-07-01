package com.LsmFiServices.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import com.LsmFiServices.FiLsmController.RunFiService;
import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
public class xmlToMap {

    private static final Logger logger = LoggerFactory.getLogger(RunFiService.class);

//	@Autowired
//	OperationUtillity OperationUtility;

    // Failed packet to data map for all packets
    public static HashMap<String, String> packetDataToMap(String pinstid, String xmlPacket)
	    throws SQLException, JsonProcessingException {

	OperationUtillity OperationUtility = new OperationUtillity();

//		System.out.println("Packet \n" + xmlPacket);
	HashMap<String, String> datamap = new HashMap<>();

	try {

	    String userName;
	    userName = OperationUtility.getuserName(pinstid);

	    datamap.put("ErrorCode", "");
	    datamap.put("ErrorDesc", "");
	    datamap.put("ErrorType", "");
	    datamap.put("Status", "");
	    datamap.put("MessageDateTime", "");
	    datamap.put("userName", OperationUtillity.NullReplace(userName));
	    datamap.put("", "");
	    datamap.put("message", "");
	    datamap.put("Response1", "");

	    Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	    while (itr.hasNext()) {
		Map.Entry<String, String> pair = itr.next();
		try {
		    String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">"); // sample
		    String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">"); // sample
		    if (xmlPacket.indexOf(openingtag) != -1) {
			String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
				xmlPacket.indexOf(closingtag)); //
			pair.setValue(value);
		    }

		} catch (Exception e) {
		    logger.info("Exception in packetDataToMap for datamap--> " + OperationUtillity.traceException(e));
		}
	    }

	    if (xmlPacket.contains("<HostTransaction>")) {
		String HostTransaction = xmlPacket.substring(
			xmlPacket.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
			xmlPacket.indexOf("</HostTransaction>"));
		if (HostTransaction.contains("<Status>")) {
		    String Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),
			    HostTransaction.indexOf("</Status>"));
		    datamap.put("Status", Status);
		}
	    }
	} catch (Exception e) {
	    logger.info("Exception in main packetDataToMap \n", e.toString());
	}
	logger.info("datamap check fro pinstid :: ", datamap);
	return datamap;
    }

    // success xml packet to data map for account inquiry
    public HashMap<String, String> successPacketDataToMapAccInq(String pinstid, String xmlPacket) throws SQLException {

	OperationUtillity OperationUtility = new OperationUtillity();

	System.out.println("Success Packet \n" + xmlPacket);
	HashMap<String, String> datamap = new HashMap<>();
	String userName;
	userName = OperationUtility.getuserName(pinstid);

	datamap.put("userName", userName);
	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("BankId", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");
	datamap.put("Customer_Id", "");
	datamap.put("Account_Id", "");
	datamap.put("Account_Opn_Date", "");
	datamap.put("Customer_Status", "");
	datamap.put("Gl_Sub_Head", "");
	datamap.put("Name", "");
	datamap.put("Purge_Date", "");
	datamap.put("Account_Status", "");
	datamap.put("Email", "");
	datamap.put("Currency", "");
	datamap.put("Sol_id", "");
	datamap.put("Cust_Name", "");
	datamap.put("Gender", "");
	datamap.put("Pan", "");
	datamap.put("Nom_Name", "");
	datamap.put("Cust_NRE_Flg", "");
	datamap.put("Cust_Status", "");
	datamap.put("Date_Of_Birth", "");
	datamap.put("STMT_REQ", "");
	datamap.put("Comm_Address1", "");
	datamap.put("Comm_Address2", "");
	datamap.put("Comm_Address3", "");
	datamap.put("Comm_City", "");
	datamap.put("Comm_Pin", "");
	datamap.put("Home_Address1", "");
	datamap.put("Home_Address2", "");
	datamap.put("Home_Address3", "");
	datamap.put("Home_City", "");
	datamap.put("Home_Pin", "");
	datamap.put("Comm_Phone", "");
	datamap.put("Registered_Phone", "");
	datamap.put("Mobile_Number", "");
	datamap.put("Mod_Of_Opr", "");
	datamap.put("Schm_code", "");
	datamap.put("Freeze_Code", "");
	datamap.put("Freeze_Reason1", "");
	datamap.put("Freeze_Reason2", "");
	datamap.put("Freeze_Reason3", "");
	datamap.put("Freeze_Reason4", "");
	datamap.put("Freeze_Reason5", "");
	datamap.put("Freeze_Date", "");
	datamap.put("UnFreeze_Date", "");
	datamap.put("Constitution_Code", "");
	datamap.put("scheme_type", ""); // converted to lowercase as per response tag
	datamap.put("limitPrefix", "");
	datamap.put("limitExpDate", "");
	datamap.put("Cust_Id", "");
	datamap.put("Scheme_Code", "");
	datamap.put("GL_Code", "");
	datamap.put("Limit_Prefix", "");
	datamap.put("Limit_Suffix", "");
	datamap.put("Sanction_Limit", "");
	datamap.put("Status_Code", "");
	datamap.put("PAN_No", "");
	// datamap.put("Scheme_Type", ""); //added on 27.06.2023 by Bharat

	Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, String> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	return datamap;
    }

    // success xml packet to data map for verify cust id
    public static HashMap<String, String> successPacketDataToMapCustIdVerify(String pinstid, String xmlPacket)
	    throws SQLException {

	OperationUtillity OperationUtility = new OperationUtillity();

	System.out.println("Success Packet \n" + xmlPacket);
	HashMap<String, String> datamap = new HashMap<>();
	String userName;
	userName = OperationUtility.getuserName(pinstid);

	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("BankId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");

	datamap.put("cifId", "");
	datamap.put("desc", "");

	datamap.put("status", "");
	datamap.put("Customer_Name", "");
	datamap.put("CustId_IsSuspended", "");

	Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, String> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
//		System.out.println("success datamap \n" + datamap);
	return datamap;
    }

    // success xml packet to data map for Account Limit Li kage--
    public static HashMap<String, String> successPacketDataToMapACCLIMLinkage(String pinstid, String xmlPacket)
	    throws SQLException {

	OperationUtillity OperationUtility = new OperationUtillity();

	System.out.println("Success Packet \n" + xmlPacket);
	HashMap<String, String> datamap = new HashMap<>();
	String userName;
	userName = OperationUtility.getuserName(pinstid);

	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("BankId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");
	datamap.put("ExecuteFinacleScriptOutputVO", "");
	datamap.put("SuccessOrFailure", "");
	datamap.put("RESULT_MSG", "");
	Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, String> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	return datamap;
    }

    // success xml packet to data map for Child Node Creation
    public static HashMap<String, String> successPacketDataToMapChildNodeCreation(String pinstid, String xmlPacket)
	    throws SQLException {

	OperationUtillity OperationUtility = new OperationUtillity();

	HashMap<String, String> datamap = new HashMap<>();
	String userName = OperationUtility.getuserName(pinstid);

	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("BankId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");
	datamap.put("limitPrefix", "");
	datamap.put("limitSuffix", "");

	Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, String> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	return datamap;
    }

    // success xml packet to data map for Child Node Modification
    public HashMap<String, String> successPacketDataToMapChildNodeModify(String pinstid, String xmlPacket)
	    throws SQLException {

	OperationUtillity OperationUtility = new OperationUtillity();

	HashMap<String, String> datamap = new HashMap<>();
	String userName;
	userName = OperationUtility.getuserName(pinstid);

	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("BankId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");
	datamap.put("amountValue", "");
	datamap.put("currencyCode", "");
	datamap.put("committedFlg", "");
	datamap.put("crncy", "");
	datamap.put("cifId", "");
	datamap.put("custId", "");
	datamap.put("custName", "");
	datamap.put("custShortName", "");
	datamap.put("custTitleCode", "");
	datamap.put("amountValue", "");
	datamap.put("currencyCode", "");
	datamap.put("drwngPowerInd", "");
	datamap.put("value", "");
	datamap.put("freeText", "");
	datamap.put("limitDesc", "");
	datamap.put("limitExpiryDate", "");
	datamap.put("limitPrefix", "");
	datamap.put("limitSanctDate", "");
	datamap.put("limitSanctRef", "");
	datamap.put("limitSuffix", "");
	datamap.put("limitType", "");
	datamap.put("value", "");
	datamap.put("parentLimitPrefix", "");
	datamap.put("parentLimitSuffix", "");
	datamap.put("patternOfFund", "");
	datamap.put("reasonCode", "");
	datamap.put("sanctAuthCode", "");
	datamap.put("sanctLevelCode", "");
	datamap.put("amountValue", "");
	datamap.put("currencyCode", "");
	datamap.put("sanctLimitFlg", "");
	datamap.put("cifId", "");
	datamap.put("serial_num", "");
	datamap.put("primaryCustomer", "");
	datamap.put("categoryType", "");
	datamap.put("categoryCode", "");
	datamap.put("categoryDesc", "");
	datamap.put("activeFlg", "");
	datamap.put("cifId", "");
	datamap.put("serial_num", "");
	datamap.put("primaryCustomer", "");
	datamap.put("categoryType", "");
	datamap.put("categoryCode", "");
	datamap.put("categoryDesc", "");
	datamap.put("activeFlg", "");
	datamap.put("singleTranFlg", "");
	datamap.put("statusCode", "");
	datamap.put("amountValue", "");
	datamap.put("currencyCode", "");

	Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, String> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	return datamap;
    }

    // success xml packet to data map for LEI
    public HashMap<String, String> successPacketDataToMapLEI(String pinstid, String xmlPacket) throws SQLException {
	System.out.println("Success Packet \n" + xmlPacket);

	OperationUtillity OperationUtility = new OperationUtillity();

	HashMap<String, String> datamap = new HashMap<>();
	String userName;
	userName = OperationUtility.getuserName(pinstid);

	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("BankId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");
	datamap.put("cifId", "");
	datamap.put("desc", "");
	datamap.put("entity", "");
	datamap.put("service", "");
	datamap.put("status", "");
	datamap.put("Success", "");
	datamap.put("ErrorDesc", "");

	Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, String> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}
	    } catch (Exception e) {
		e.printStackTrace();
//				logger.info(" PSL DOA API_Error---------->", e.toString());
	    }
	}
	return datamap;
    }

    // success xml packet to data map for Lein Marking
    public HashMap<String, String> successPacketDataToMapLienMarking(String pinstid, String xmlPacket)
	    throws SQLException {

	OperationUtillity OperationUtility = new OperationUtillity();

	HashMap<String, String> datamap = new HashMap<>();
	String userName;
	userName = OperationUtility.getuserName(pinstid);

	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("BankId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");
	datamap.put("AcctId", "");
	datamap.put("SchmCode", "");
	datamap.put("SchmType", "");
	datamap.put("AcctCurr", "");
	datamap.put("BankId", "");
	datamap.put("Name", "");
	datamap.put("BranchId", "");
	datamap.put("BranchName", "");
	datamap.put("Addr1", "");
	datamap.put("Addr2", "");
	datamap.put("Addr3", "");
	datamap.put("City", "");
	datamap.put("StateProv", "");
	datamap.put("PostalCode", "");
	datamap.put("Country", "");
	datamap.put("AddrType", "");
	datamap.put("LienId", "");

	Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, String> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	return datamap;
    }

    // success xml packet to data map for Lein Marking modification--pending
    public HashMap<String, String> successPacketDataToMapLienMarkingModification(String pinstid, String xmlPacket)
	    throws SQLException {
	HashMap<String, String> datamap = new HashMap<>();

	OperationUtillity OperationUtility = new OperationUtillity();

	String userName;
	userName = OperationUtility.getuserName(pinstid);

	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("BankId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");

	datamap.put("AcctId", "");
	datamap.put("SchmCode", "");
	datamap.put("SchmType", "");
	datamap.put("AcctCurr", "");
	datamap.put("BankId", "");
	datamap.put("Name", "");
	datamap.put("BranchId", "");
	datamap.put("BranchName", "");
	datamap.put("Addr1", "");
	datamap.put("Addr2", "");
	datamap.put("Addr3", "");
	datamap.put("City", "");
	datamap.put("StateProv", "");
	datamap.put("PostalCode", "");
	datamap.put("Country", "");
	datamap.put("AddrType", "");
	datamap.put("LienId", "");
	datamap.put("LienId", "");

	Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, String> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	return datamap;
    }

    // success xml packet to data map for Parent Node craetion

    public static HashMap<String, String> successPacketDataToMapParentNodeCreation(String pinstid, String xmlPacket)
	    throws SQLException {

	OperationUtillity OperationUtility = new OperationUtillity();

	HashMap<String, String> datamap = new HashMap<>();
	String userName;
	userName = OperationUtility.getuserName(pinstid);

	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("BankId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");

	datamap.put("limitPrefix", "");
	datamap.put("limitSuffix", "");

	Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, String> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}
	    } catch (Exception e) {
		System.out.println("There is problem in xmlToMap.successPacketDataToMapParentNodeCreation()");
		e.printStackTrace();
	    }
	}
	return datamap;
    }

    // Success xml packet to data map for Parent Limit Node Modification
    public static HashMap<String, String> successPacketDataToMapLimitNodeModifiy(String pinstid, String xmlPacket)
	    throws SQLException {

	OperationUtillity OperationUtility = new OperationUtillity();

	HashMap<String, String> datamap = new HashMap<>();
	String userName;
	userName = OperationUtility.getuserName(pinstid);

	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("BankId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");
	datamap.put("amountValue", "");
	datamap.put("currencyCode", "");
	datamap.put("committedFlg", "");
	datamap.put("crncy", "");
	datamap.put("cifId", "");
	datamap.put("custId", "");
	datamap.put("custName", "");
	datamap.put("custShortName", "");
	datamap.put("custTitleCode", "");
	datamap.put("amountValue", "");
	datamap.put("currencyCode", "");
	datamap.put("drwngPowerInd", "");
	datamap.put("value", "");
	datamap.put("freeText", "");
	datamap.put("limitDesc", "");
	datamap.put("limitExpiryDate", "");
	datamap.put("limitPrefix", "");
	datamap.put("limitSanctDate", "");
	datamap.put("limitSanctRef", "");
	datamap.put("limitSuffix", "");
	datamap.put("limitType", "");
	datamap.put("value", "");
	datamap.put("parentLimitPrefix", "");
	datamap.put("parentLimitSuffix", "");
	datamap.put("patternOfFund", "");
	datamap.put("reasonCode", "");
	datamap.put("sanctAuthCode", "");
	datamap.put("sanctLevelCode", "");
	datamap.put("amountValue", "");
	datamap.put("currencyCode", "");
	datamap.put("sanctLimitFlg", "");
	datamap.put("cifId", "");
	datamap.put("serial_num", "");
	datamap.put("primaryCustomer", "");
	datamap.put("categoryType", "");
	datamap.put("categoryCode", "");
	datamap.put("categoryDesc", "");
	datamap.put("cifId", "");
	datamap.put("serial_num", "");
	datamap.put("primaryCustomer", "");
	datamap.put("categoryType", "");
	datamap.put("categoryCode", "");
	datamap.put("categoryDesc", "");
	datamap.put("activeFlg", "");
	datamap.put("singleTranFlg", "");
	datamap.put("statusCode", "");
	datamap.put("amountValue", "");
	datamap.put("currencyCode", "");

	Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, String> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	return datamap;
    }

    //// success xml packet to data map for PSL

    public static HashMap<String, String> successPacketDataToMapPSL(String pinstid, String xmlPacket)
	    throws SQLException {

	OperationUtillity OperationUtility = new OperationUtillity();

	HashMap<String, String> datamap = new HashMap<>();
	String userName;
	userName = OperationUtility.getuserName(pinstid);

	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("BankId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");
	datamap.put("ExecuteFinacleScriptOutputVO", "");
	datamap.put("message", "");

	Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, String> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
//		System.out.println("success datamap \n" + datamap);
//		logger.info("success datamap \n" + datamap);
	return datamap;
    }

    // success xml packet to data map for Rate of Interest
    public static HashMap<String, String> successPacketDataToMapRateofInterest(String pinstid, String xmlPacket)
	    throws SQLException {

	OperationUtillity OperationUtility = new OperationUtillity();

	System.out.println("Success Packet \n" + xmlPacket);
	HashMap<String, String> datamap = new HashMap<>();
	String userName;
	userName = OperationUtility.getuserName(pinstid);

	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("BankId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");

	datamap.put("ResultMSG", "");

	Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, String> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}
	    } catch (Exception e) {
		e.printStackTrace();
		logger.info(" PSL DOA API_Error---------->", e.toString());
	    }
	}
//		System.out.println("success datamap \n" + datamap);
	return datamap;
    }
    // success xml packet to data map for Sanction Limit

    public static HashMap<String, String> successPacketDataToMapSanctionLimit(String pinstid, String xmlPacket)
	    throws SQLException {

	OperationUtillity OperationUtility = new OperationUtillity();

	System.out.println("Success Packet \n" + xmlPacket);
	HashMap<String, String> datamap = new HashMap<>();
	String userName;
	userName = OperationUtility.getuserName(pinstid);

	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("BankId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");

	datamap.put("ExecuteFinacleScriptOutputVO", "");
	datamap.put("SuccessOrFailure", "");
	datamap.put("RESULT_MSG", "");

	Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, String> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	return datamap;
    }

    // success xml packet to data map for URC

    public HashMap<String, String> successPacketDataToMapURC(String pinstid, String xmlPacket) throws SQLException {

	OperationUtillity OperationUtility = new OperationUtillity();

	System.out.println("Success Packet \n" + xmlPacket);
	HashMap<String, String> datamap = new HashMap<>();
	String userName;
	userName = OperationUtility.getuserName(pinstid);

	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("BankId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");
	datamap.put("cifid", "");
	datamap.put("desc", "");
	datamap.put("entity", "");
	datamap.put("service", "");
	datamap.put("status", "");
	datamap.put("Success", "");
	datamap.put("ErrorDesc", "");

	Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, String> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}
	    } catch (Exception e) {
		logger.info("xmlToMap.successPacketDataToMapURC()", " " + e.toString());
//				System.out.println("xmlToMap.successPacketDataToMapURC()");
		e.printStackTrace();
	    }
	}
	return datamap;
    }

    // Success response Fee Recovery Service
    public static HashMap<String, String> successPacketDataToMapfeeRecovery(String pinstid, String xmlPacket)
	    throws SQLException {
//		logger.info("Entered into xmlToMap.successPacketDataToMapfeeRecovery()");
	OperationUtillity OperationUtility = new OperationUtillity();

//		System.out.println("Success Packet \n" + xmlPacket);
	HashMap<String, String> datamap = new HashMap<>();
	String userName;
	userName = OperationUtility.getuserName(pinstid);

	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("BankId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");
	datamap.put("ExecuteFinacleScriptOutputVO", "");
	datamap.put("Response", "");
	datamap.put("Response1", "");
	Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, String> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	datamap.put("message", datamap.get("Response1"));
	if (datamap.get("Response1").toLowerCase().contains("failed")
		|| datamap.get("Response").toLowerCase().contains("failed")) {
	    datamap.put("Status", "FAILURE");
	    datamap.put("message", datamap.get("Response1"));
	}
	logger.info("Response tags added into map " + datamap);
	return datamap;
    }

    // Failed packet to data map for FeeRecovery
    public static HashMap<String, String> failedFeeRecoveryPacketDataToMap(String pinstid, String xmlPacket)
	    throws SQLException, JsonProcessingException {

	OperationUtillity OperationUtility = new OperationUtillity();

//		System.out.println("Packet \n" + xmlPacket);
	HashMap<String, String> datamap = new HashMap<>();

	try {

	    String userName;
	    userName = OperationUtility.getuserName(pinstid);

	    datamap.put("ErrorCode", "");
	    datamap.put("ErrorDesc", "");
	    datamap.put("ErrorType", "");
	    datamap.put("Status", "");
	    datamap.put("MessageDateTime", "");
	    datamap.put("userName", userName);
	    datamap.put("", "");
	    datamap.put("message", "");
	    datamap.put("Response1", "");

	    Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	    while (itr.hasNext()) {
		Map.Entry<String, String> pair = itr.next();
		try {
		    String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">"); // <ErrorCode>
		    String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">"); // </ErrorCode>

		    if (xmlPacket.indexOf(openingtag) != -1) {
			String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
				xmlPacket.indexOf(closingtag)); //
			pair.setValue(value);
		    }

		} catch (Exception e) {
		    e.printStackTrace();
		    logger.info("Exception in packetDataToMap for datamap--> " + OperationUtillity.traceException(e));
		}
	    }

	    if (xmlPacket.contains("<HostTransaction>")) {
		String HostTransaction = xmlPacket.substring(
			xmlPacket.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
			xmlPacket.indexOf("</HostTransaction>"));
		if (HostTransaction.contains("<Status>")) {
		    String Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),
			    HostTransaction.indexOf("</Status>"));
		    datamap.put("Status", Status);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.info("Exception in main packetDataToMap \n", e.toString());
	}

	return datamap;
    }

    // updating columns in lsm_service_req_res for success tag if value is N
    public void updateSuccessStatus(String pinstid, String reqType, String xmlPacket, String successTagvalue) {

	Connection con = null;
	PreparedStatement statement = null;
	String lsql = null;

	try {

	    con = DBConnect.getConnection();
	    if (con == null) {

		return;
	    } else {
		con.setAutoCommit(false);

		try {
		    String ErrorDesc = "";
		    if (xmlPacket.contains("<ErrorDesc>")) {
			ErrorDesc = xmlPacket.substring(xmlPacket.indexOf("<ErrorDesc>") + "<ErrorDesc>".length(),
				xmlPacket.indexOf("</ErrorDesc>"));
		    }

		    String updateLsmReqResQuery = "UPDATE LSM_SERVICE_REQ_RES SET STATUS='" + successTagvalue
			    + "', MESSAGE='" + ErrorDesc + "' WHERE PINSTID='" + pinstid + "' AND REQUESTTYPE ='"
			    + reqType + "'";
		    statement = con.prepareStatement(updateLsmReqResQuery);
		    int int_count = statement.executeUpdate();
		    logger.info("updated status and error message  --> ", String.valueOf(int_count));

		    if (int_count > 0) {
			con.commit();
		    }
		    if (statement != null) {
			statement.close();
		    }

		} catch (Exception e) {
		    con.rollback();
		    e.printStackTrace();
		    logger.info("Exception in main packetDataToMap \n", e.toString());
		} finally {
		    if (statement != null) {
			statement.close();
		    }
		    if (con != null) {
			con.close();
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.info("Exception in main packetDataToMap \n", e.toString());
	}
    }

    // success xml packet to data map for SVT Service
    public static HashMap<String, String> successPacketDataToMapSVT(String pinstid, String xmlPacket)
	    throws SQLException {

	OperationUtillity OperationUtility = new OperationUtillity();
	logger.info("Success Packet \n" + xmlPacket);
	HashMap<String, String> datamap = new HashMap<>();
	String userName;
	userName = OperationUtility.getuserName(pinstid);

//		datamap.put(key, value)
	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");
	datamap.put("message", "");

	Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, String> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}
	    } catch (Exception e) {
		e.printStackTrace();
		logger.info(" Error in xmlToMap.successPacketDataToMapSVT() ---------->", e.toString());
	    }
	}
	return datamap;
    }

    // success xml packet to data map for Limit Node Inquiry Service
    public static HashMap<String, String> successPacketDataToMaplimitNodeEnquiryService(String pinstid,
	    String xmlPacket) throws SQLException {

	OperationUtillity OperationUtility = new OperationUtillity();
	logger.info("Success Packet \n" + xmlPacket);
	HashMap<String, String> datamap = new HashMap<>();
	String userName;
	userName = OperationUtility.getuserName(pinstid);

//		datamap.put(key, value)
	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");
	datamap.put("message", "");
	datamap.put("Id", "");

	Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, String> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}
	    } catch (Exception e) {
		logger.error("xmlToMap.successPacketDataToMaplimitNodeEnquiryService(){}\n",OperationUtillity.traceException(e));
	    }
	}
	return datamap;
    }

    // Success response Status Code Updation Service
    public static HashMap<String, String> StatusCodeSuccessPacketDataToMap(String pinstid, String xmlPacket)
	    throws SQLException {

	OperationUtillity OperationUtility = new OperationUtillity();

	HashMap<String, String> datamap = new HashMap<>();
	String userName;
	userName = OperationUtility.getuserName(pinstid);

	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("BankId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");
	datamap.put("Success", "");
	datamap.put("RESULT_MSG", "");
	Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, String> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	logger.info("successPacketDataToMapStatusCodeUpdation datamap \n" + datamap);
	return datamap;
    }

    // Success response HDCLMLinkageService
    public static HashMap<String, String> HDCLMLinkageServiceSuccessMap(String pinstid, String xmlPacket)
	    throws SQLException {

	OperationUtillity OperationUtility = new OperationUtillity();

	HashMap<String, String> datamap = new HashMap<>();
	String userName;
	userName = OperationUtility.getuserName(pinstid);

	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("BankId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");
	datamap.put("SuccessOrFailure", "");
	datamap.put("ResultMsg", "");

	Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, String> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	logger.info("HDCLMLinkageService Success datamap \n" + datamap);
	return datamap;
    }

    // Success response for
    public static WeakHashMap<String, String> successPacketDataToMaplimitNodeIdEnquiry(String pinstid, String xmlPacket)
	    throws SQLException {
	OperationUtillity OperationUtility = new OperationUtillity();

	WeakHashMap<String, String> datamap = new WeakHashMap<>();
	String userName = OperationUtility.getuserName(pinstid);

	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("BankId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");
	datamap.put("SuccessOrFailure", "");
	datamap.put("ResultMsg", "");
	datamap.put("SerialNo", "");
	datamap.put("LimitPrefix", "");
	datamap.put("LimitSuffix", "");
	datamap.put("Level", "");
	datamap.put("SanctionLimit", "");
	datamap.put("UtilizedAmount", "");

	Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, String> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
//			logger.info("xmlToMap.successPacketDataToMaplimitNodeIdEnquiry() \n" + datamap);
	return datamap;
    }

    // Success map LEI and URCC Service
    public static WeakHashMap<String, String> successPacketDataToMapforLEINumberEnquiry(String pinstid,
	    String xmlPacket) throws SQLException {

	OperationUtillity OperationUtility = new OperationUtillity();

	WeakHashMap<String, String> datamap = new WeakHashMap<>();
	String userName;
	userName = OperationUtility.getuserName(pinstid);

	datamap.put("User_Name", userName);
	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("BankId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");
	datamap.put("Success", "");
	datamap.put("SerialNo", "");
	datamap.put("DocCode", "");
	datamap.put("DocCodeDescr", "");
	datamap.put("DocType", "");
	datamap.put("DocTypeDescr", "");
	datamap.put("RefNumber", "");
	datamap.put("ExpiryDate", "");
	Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, String> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}
	    } catch (Exception e) {
		logger.info("xmlToMap.successPacketDataToMapforLEINumberEnquiry()-->"
			+ OperationUtillity.traceException(e));
		e.printStackTrace();
	    }
	}
	logger.info("successPacketDataToMapStatusCodeUpdation datamap \n" + datamap);
	return datamap;
    }

    // Success response HDCLMLinkageService
    public static HashMap<String, String> svtLinkageServiceSuccessMap(String pinstid, String xmlPacket)
	    throws SQLException {

	OperationUtillity OperationUtility = new OperationUtillity();

	HashMap<String, String> datamap = new HashMap<>();
	String userName;
	userName = OperationUtility.getuserName(pinstid);

	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("BankId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");
	datamap.put("SuccessOrFailure", "");
	datamap.put("ResultMsg", "");

	Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, String> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	logger.info("HDCLMLinkageService Success datamap \n" + datamap);
	return datamap;
    }

    public static HashMap<String, String> successPacketDataToMapStatusCodeUpdation(String pinstid, String xmlPacket)
	    throws SQLException {

	OperationUtillity OperationUtility = new OperationUtillity();

	System.out.println("Success Packet \n" + xmlPacket);
	logger.info("successPacketDataToMapStatusCodeUpdation  Success Packet \\n" + xmlPacket);
	HashMap<String, String> datamap = new HashMap<>();
	String userName;
	userName = OperationUtility.getuserName(pinstid);

	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("BankId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");

	Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, String> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	logger.info("successPacketDataToMapStatusCodeUpdation  datamap  \\n" + datamap);
	return datamap;
    }

    public static Map<String, String> successMapForDrgPwrChk(String pinstid, String xmlPacket) throws SQLException {
	logger.info("successMapForDrgPwrChk  Success Packet {}", xmlPacket);
	HashMap<String, String> datamap = new HashMap<>();

	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("BankId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");
	datamap.put("Account_Number", "");
	datamap.put("Drawing_Power", "");
	datamap.put("Sanction_Limit", "");

	Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, String> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}
	    } catch (Exception e) {
		logger.error("xmlToMap.successMapForDrgPwrChk(){}", OperationUtillity.traceException(e));
	    }
	}
	logger.info("successMapForDrgPwrChk  datamap{} ", datamap);
	return datamap;
    }

    public static Map<String, String> successMapForDrgPwrUpdate(String pinstid, String xmlPacket) throws SQLException {
	Map<String, String> datamap = new HashMap<>();
	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("BankId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");

	Iterator<Map.Entry<String, String>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, String> pair = itr.next();
	    try {
		String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">");
		String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">");
		if (xmlPacket.indexOf(openingtag) != -1) {
		    String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
			    xmlPacket.indexOf(closingtag));
		    pair.setValue(value);
		}
	    } catch (Exception e) {
		logger.error("xmlToMap.successMapForDrgPwrUpdate(){}", OperationUtillity.traceException(e));
	    }
	}
	return datamap;
    }
}
