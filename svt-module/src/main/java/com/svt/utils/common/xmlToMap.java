package com.svt.utils.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.svt.utils.dataConnectivity.dbConnection;

@Service
public class xmlToMap {

	private static final Logger logger = LoggerFactory.getLogger(xmlToMap.class);

	@Autowired
	OperationUtillity OperationUtility;

	// Failed packet to data map for all packets
	public static HashMap<String, String> packetDataToMap(String pinstid, String xmlPacket)
			throws SQLException, JsonProcessingException {

		OperationUtillity OperationUtility = new OperationUtillity();

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
					String openingtag = pair.getKey().replace(pair.getKey(), "<" + pair.getKey() + ">"); // sample openingtag = <ErrorCode>
					String closingtag = pair.getKey().replace(pair.getKey(), "</" + pair.getKey() + ">"); //sample closingtag =  </ErrorCode>

					if (xmlPacket.indexOf(openingtag) != -1) {
						String value = xmlPacket.substring(xmlPacket.indexOf(openingtag) + openingtag.length(),
								xmlPacket.indexOf(closingtag)); //
						pair.setValue(value);
					}

				} catch (Exception e) {
					logger.info("\nException in packetDataToMap for datamap--> " + OperationUtillity.traceException(pinstid,e));
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
//			System.out.println("xmlToMap.packetDataToMap()");
			logger.info("\nxmlToMap.packetDataToMap()", OperationUtillity.traceException(pinstid,e));
		}

		return datamap;
	}

	

	public static HashMap<String, String> successPacketDataToMapSanctionLimit(String pinstid, String xmlPacket)
			throws SQLException {

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
				logger.info("\nxmlToMap.successPacketDataToMapSanctionLimit()", OperationUtillity.traceException(pinstid,e));
			}
		}
		return datamap;
	}

	// updating columns in lsm_service_req_res for success tag if value is N
	public void updateSuccessStatus(String pinstid, String reqType, String xmlPacket, String successTagvalue) {

		Connection con = null;
		PreparedStatement statement = null;
		String lsql = null;

		try {
			con = dbConnection.getConnection();
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
					logger.info("\nupdated status and error message  --> ", String.valueOf(int_count));

					if (int_count > 0) {
						con.commit();
					}
					if (statement != null) {
						statement.close();
					}

				} catch (Exception e) {
					con.rollback();
					e.printStackTrace();
					logger.info("\nException in main packetDataToMap \n", e.toString());
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
//			logger.info("\nException in main packetDataToMap \n", e.toString());
			logger.info("\nxmlToMap.updateSuccessStatus()", OperationUtillity.traceException(pinstid,e));
		}
	}
}
