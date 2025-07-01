package com.LsmFiServices.Utility;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LsmFiServices.pojo.limitFetchFiCmart.Limit_Details;
import com.LsmFiServices.pojo.limitFetchFiCmart.executeFinacleScriptCustomData;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class limitFetchFiCmartUtility {

    private static final Logger logger = LoggerFactory.getLogger(limitFetchFiCmartUtility.class);

    // Success response for
    public static WeakHashMap<String, Object> successPacketDataToMaplimitFetchFiCmart(String pinstid, String xmlPacket,
	    String LmtPrefix, String LmtSuffix) throws SQLException, JsonProcessingException {
	OperationUtillity OperationUtility = new OperationUtillity();

	WeakHashMap<String, Object> datamap = new WeakHashMap<>();
	String userName = OperationUtility.getuserName(pinstid);

	datamap.put("RequestUUID", "");
	datamap.put("ServiceRequestId", "");
	datamap.put("ServiceRequestVersion", "");
	datamap.put("ChannelId", "");
	datamap.put("BankId", "");
	datamap.put("TimeZone", "");
	datamap.put("MessageDateTime", "");
	datamap.put("Status", "");
	datamap.put("parentLmtSuffix", LmtSuffix);
	datamap.put("parentLmtPrefix", LmtPrefix);

	if (userName != null) {
	    datamap.put("userName", userName);
	}

	datamap.put("pinstid", pinstid);
	datamap.put("executeFinacleScript_CustomData", "");

	Iterator<Map.Entry<String, Object>> itr = datamap.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry<String, Object> pair = itr.next();
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
	if (datamap.containsKey("executeFinacleScript_CustomData")) {

	    StringBuilder sd = new StringBuilder();
	    sd.append("<executeFinacleScriptCustomData>");
	    sd.append(datamap.get("executeFinacleScript_CustomData"));
	    sd.append("</executeFinacleScriptCustomData>");

	    datamap.put("executeFinacleScriptCustomData", sd.toString());
	    datamap.remove("executeFinacleScript_CustomData");
	}

	datamap.put("limitDetails", convertXmlToJsonNodeLimitFetchFi(datamap));
	saveLimitDetailsConvertedXmlToJsonData(datamap);

//		logger.info("limitFetchFiCmartUtility.successPacketDataToMaplimitFetchFiCmart() \n" + datamap);
	return datamap;
    }

    public static List<Limit_Details> convertXmlToJsonNodeLimitFetchFi(WeakHashMap<String, Object> datamap)
	    throws JsonProcessingException {
//		logger.info(
//				"[limitFetchFiCmartUtility].[successPacketDataToMaplimitFetchFiCmart].[convertXmlToJsonNodeLimitFetchFi()] \n"
//						+ datamap);
	String xmlData = datamap.get("executeFinacleScriptCustomData").toString();
	List<Limit_Details> limitDetails = null;
	try {
	    JAXBContext jaxbContext = JAXBContext.newInstance(executeFinacleScriptCustomData.class);
	    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
	    StringReader reader = reader = new StringReader(xmlData);

	    executeFinacleScriptCustomData root = (executeFinacleScriptCustomData) unmarshaller.unmarshal(reader);
	    limitDetails = root.getLimit_Details();

	} catch (JAXBException e) {
	    logger.info(
		    "[EXCEPTION].[limitFetchFiCmartUtility].[successPacketDataToMaplimitFetchFiCmart].[convertXmlToJsonNodeLimitFetchFi()] .[executeFinacleScriptCustomData]\n"
			    + OperationUtillity.traceException(e));
//			e.printStackTrace();
	}
	return limitDetails;
    }

    @SuppressWarnings("unchecked")
    public static void saveLimitDetailsConvertedXmlToJsonData(WeakHashMap<String, Object> dataMap) {

//		logger.info("limitFetchFiCmartUtility.successPacketDataToMaplimitFetchFiCmart().saveLimitDetailsConvertedXmlToJsonData() \n" + dataMap);

	try (Connection con = DBConnect.getConnection()) {
	    con.setAutoCommit(false);
	    List<Limit_Details> limitDetails = (List<Limit_Details>) dataMap.get("limitDetails");
	    try (PreparedStatement prepDelete = con
		    .prepareStatement("DELETE FROM FI_CHILD_LIMIT_DETAILS WHERE PINSTID = ?");) {

		prepDelete.setString(1, dataMap.get("pinstid").toString());
		prepDelete.executeUpdate();
	    } catch (SQLException se) {
		logger.info(
			"[Exception].[fiParentLimitInquiryUtility].[convertXmlToJsonNodeAndSaveData()].[deletePrefixSuffixForLimitNode()].[Pinstid()] :: "
				+ dataMap.get("pinstid").toString() + " :: " + se);
	    }

	    PreparedStatement prepStmt = con.prepareStatement("INSERT INTO FI_CHILD_LIMIT_DETAILS (" + "SERIAL_NO, "
		    + "PINSTID, " + "PARENT_PREFIX, " + "PARENT_SUFFIX, " + "LIMIT_SUFFIX, " + "LIMIT_PREFIX, "
		    + "LIMIT_NODE_ID, " + "LIMIT_LEVEL, " + "SANCTIONLIMIT, "
		    + "UTILIZEDAMOUNT, EXTENSIONDATE, EXPIRYDATE, FETCHDATETIME) "
		    + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?, SYSDATE)");
//			logger.info(
//					".limitFetchFiCmartUtility].[successPacketDataToMaplimitFetchFiCmart()].[saveLimitDetailsConvertedXmlToJsonData()].[Pinstid()] ::"
//							+ dataMap.get("pinstid").toString() + " [DeletedData] :: ");
	    for (Limit_Details limitDetail : limitDetails) {

		prepStmt.setInt(1, limitDetail.getSerialNo());
		prepStmt.setString(2, dataMap.get("pinstid").toString());
		prepStmt.setString(3, dataMap.get("parentLmtPrefix").toString());
		prepStmt.setString(4, dataMap.get("parentLmtSuffix").toString());
		prepStmt.setString(5, limitDetail.getLimitSuffix());
		prepStmt.setString(6, limitDetail.getLimitPrefix());
//		prepStmt.setString(7, limitDetail.getLimitSuffix() + "/" + limitDetail.getLimitPrefix());
		prepStmt.setString(7, limitDetail.getLimitPrefix() + "/" +  limitDetail.getLimitSuffix());
		prepStmt.setLong(8, limitDetail.getLevel());
		prepStmt.setLong(9, limitDetail.getSanctionLimit());
//		prepStmt.setLong(10, limitDetail.getUtilizedAmount());
		prepStmt.setString(10, String.format("%.2f",limitDetail.getUtilizedAmount()));
		prepStmt.setString(11, limitDetail.getExtensionDate());
		prepStmt.setString(12, limitDetail.getExpiryDate());
		prepStmt.addBatch();
	    }
	    int[] i = prepStmt.executeBatch();

	    if (prepStmt != null) {
		prepStmt = null;
		if (i.length > 0) {
		    con.commit();
		    con.close();
		}
	    }
//			logger.info(
//					".limitFetchFiCmartUtility].[successPacketDataToMaplimitFetchFiCmart()].[saveLimitDetailsConvertedXmlToJsonData()].[Pinstid()] ::"
//							+ dataMap.get("pinstid").toString() + " [BatchSize] :: " + i);
	} catch (SQLException ex) {
	    logger.info(
		    "[Exception].limitFetchFiCmartUtility].[successPacketDataToMaplimitFetchFiCmart()].[saveLimitDetailsConvertedXmlToJsonData()].[Pinstid()] ::"
			    + dataMap.get("pinstid").toString() + " :: " + ex);
	} finally {

	}
    }
}
