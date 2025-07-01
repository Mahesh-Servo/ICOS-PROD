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
import org.springframework.stereotype.Repository;

import com.LsmFiServices.pojo.fiParentLimitInquiry.Limit_Details;
import com.LsmFiServices.pojo.fiParentLimitInquiry.executeFinacleScriptCustomData;
import com.fasterxml.jackson.core.JsonProcessingException;

@Repository
public class fiParentLimitInquiryUtility {

	private static final Logger logger = LoggerFactory.getLogger(fiParentLimitInquiryUtility.class);

	// Success response for
	public static WeakHashMap<String, Object> successPacketDataToMapfiParentLimitInquiry(String pinstid,
			String xmlPacket, String custId) throws SQLException, JsonProcessingException {
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
		datamap.put("userName", userName);
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
				} else {
//					datamap.remove(pair.getKey());
				}
			} catch (Exception e) {
//				e.printStackTrace();
				logger.info("[fiParentLimitInquiryUtility].[successPacketDataToMapfiParentLimitInquiry()] \n"
						+ OperationUtillity.traceException(e));
			}
		}
//		logger.info(
//				"[fiParentLimitInquiryUtility].[successPacketDataToMapfiParentLimitInquiry()].[datamap] \n" + datamap);

		if (datamap.containsKey("executeFinacleScript_CustomData")) {

			StringBuilder sd = new StringBuilder();
			sd.append("<executeFinacleScriptCustomData>");
			sd.append(datamap.get("executeFinacleScript_CustomData"));
			sd.append("</executeFinacleScriptCustomData>");

			datamap.put("executeFinacleScriptCustomData", sd.toString());
			datamap.remove("executeFinacleScript_CustomData");
		}

		datamap.put("limitDetails", convertXmlToJsonNodeAndSaveData(
				datamap.get("executeFinacleScriptCustomData").toString(), pinstid, custId));
//				logger.info("limitFetchFiCmartUtility.successPacketDataToMaplimitFetchFiCmart() \n" + datamap);
		return datamap;
	}

	public static List<Limit_Details> convertXmlToJsonNodeAndSaveData(String xmlData, String pinstid, String custId)
			throws JsonProcessingException {

		List<Limit_Details> limitDetails = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(executeFinacleScriptCustomData.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			StringReader reader = reader = new StringReader(xmlData);

			executeFinacleScriptCustomData root = (executeFinacleScriptCustomData) unmarshaller.unmarshal(reader);
			limitDetails = root.getLimit_Details();

			saveConvertedXmlToJsonData(limitDetails, pinstid, custId);

//			logger.info(
//					"[fiParentLimitInquiryUtility].[successPacketDataToMapfiParentLimitInquiry].[convertXmlToJsonNode()] \n"
//							+ efcdResponse);
		} catch (JAXBException e) {
			logger.info(
					"[fiParentLimitInquiryUtility].[successPacketDataToMapfiParentLimitInquiry].[convertXmlToJsonNode()].[executeFinacleScriptCustomData]\n"
							+ OperationUtillity.traceException(e));
//			e.printStackTrace();
		}
		return limitDetails;
	}

	public static void saveConvertedXmlToJsonData(List<Limit_Details> limitDetails, String pinstid, String custId) {
		try (Connection con = DBConnect.getConnection()) {
			con.setAutoCommit(false);

			try (PreparedStatement prepDelete = con
					.prepareStatement("DELETE FROM FI_PARENT_LIMIT_DETAILS WHERE PINSTID = ?"); ) {
				prepDelete.setString(1, pinstid);
				prepDelete.executeUpdate();
			} catch (SQLException se) {
				logger.info(
						"[Exception].[fiParentLimitInquiryUtility].[convertXmlToJsonNodeAndSaveData()].[deletePrefixSuffixForLimitNode()].[Pinstid()] :: "
								+ pinstid + " :: " + se.getMessage());
			}

			PreparedStatement prepStmt = con.prepareStatement(
					"INSERT INTO FI_PARENT_LIMIT_DETAILS (SERIAL_NO, PINSTID, CUST_ID, PARENT_NODE, PARENT_PREFIX, PARENT_SUFFIX) VALUES(?,?,?,?,?,?)");

			for (Limit_Details limitDetail : limitDetails) {

				prepStmt.setInt(1, limitDetail.getSerialNo());
				prepStmt.setString(2, pinstid);
				prepStmt.setString(3, custId);
				prepStmt.setString(4, limitDetail.getParentNode());
				prepStmt.setString(5, limitDetail.getParentPrefix());
				prepStmt.setString(6, limitDetail.getParentSuffix());
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

		} catch (SQLException ex) {
			logger.info(
					"[Exception].fiParentLimitInquiryUtility].[convertXmlToJsonNodeAndSaveData()].[insertPrefixSuffixForLimitNode()].[Pinstid()] ::"
							+ pinstid + " :: " + ex.getMessage());
		} finally {

		}
	}
}
