package com.LsmFiServices.Utility;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.LsmFiServices.pojo.svtcollateralenquiry.AccountNumber;
import com.LsmFiServices.pojo.svtcollateralenquiry.ExecuteFinacleScriptCustomData;

@Component
public class SVTCollateralEnquiryUtility {

	private static final Logger logger = LoggerFactory.getLogger(SVTCollateralEnquiryUtility.class);

//	private OperationUtillity utility;

	public List<AccountNumber> getAllAccountNumbers(String pinstid) {
		List<AccountNumber> accountNumbers = new LinkedList<>();
		String query = "SELECT  DISTINCT(ANSWER) AS ACCOUNT_NUMBER FROM LSM_LIMIT_ANSWERS  WHERE PINSTID= ? AND QUESTION= ?  ORDER BY ACCOUNT_NUMBER";
		try (Connection con = DBConnect.getConnection(); PreparedStatement psmt = con.prepareStatement(query)) {
			psmt.setString(1, pinstid);
			psmt.setString(2, "Account No");
			try (ResultSet rs = psmt.executeQuery()) {
				while(rs.next()) {
				accountNumbers.add(new AccountNumber(OperationUtillity.NullReplace(rs.getString("ACCOUNT_NUMBER"))));
				}
			}
		} catch (Exception e) {
		}
		logger.info("SVTCollateralEnquiryUtility.getAllAccountNumbers().accountNumbers-->"+accountNumbers);
		return accountNumbers;
	}

	public Map<String, Object> responseDataToMap(String pinstId, String xmlPacket) throws SQLException {
        Map<String, Object> datamap = new HashMap<>();
        datamap.put("RequestUUID", "");
        datamap.put("ServiceRequestId", "");
        datamap.put("ServiceRequestVersion", "");
        datamap.put("ChannelId", "");
        datamap.put("BankId", "");
        datamap.put("TimeZone", "");
        datamap.put("MessageDateTime", "");
        datamap.put("Status", "");
        datamap.put("userName", "");
        datamap.put("ErrorDesc", "");
        datamap.put("executeFinacleScript_CustomData", "");

        logger.info("Xml Response for collateral code enquiry in -->"+xmlPacket);
        try {
            Iterator<Map.Entry<String, Object>> itr = datamap.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry<String, Object> pair = itr.next();
                String openingTag = "<" + pair.getKey() + ">";
                String closingTag = "</" + pair.getKey() + ">";
                if (xmlPacket.contains(openingTag) && xmlPacket.contains(closingTag)) {
                    String value = xmlPacket.substring(xmlPacket.indexOf(openingTag) + openingTag.length(), xmlPacket.indexOf(closingTag));
                    pair.setValue(value);
                }
            }
            logger.info("SVTCollateralEnquiryUtility.responseDataToMap().datamap--->\n" + datamap);
            
            if (datamap.containsKey("executeFinacleScript_CustomData")) {
                String customDataXml = "<executeFinacleScriptCustomData>" +datamap.get("executeFinacleScript_CustomData") +"</executeFinacleScriptCustomData>";
                datamap.put("executeFinacleScriptCustomData", convertXmlToJson(customDataXml, pinstId));
            }
        } catch (Exception e) {
            logger.error("SVTCollateralEnquiryUtility.responseDataToMap().Exception : " + OperationUtillity.traceException(e));
        }
        logger.info("SVTCollateralEnquiryUtility.responseDataToMap().datamap : " + datamap);
        return datamap;
    }

    public ExecuteFinacleScriptCustomData convertXmlToJson(String xmlData, String pinstid) {
        ExecuteFinacleScriptCustomData rootTag = new ExecuteFinacleScriptCustomData();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ExecuteFinacleScriptCustomData.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            rootTag = (ExecuteFinacleScriptCustomData) unmarshaller.unmarshal(new StringReader(xmlData));
        } catch (Exception e) {
            logger.error("Exception while unmarshalling XML:\n" +OperationUtillity.traceException(e));
        }
        logger.info("Root tag data check--->" + rootTag);
        return rootTag;
    }
	
	public Map<String, String> saveSVTCollateralEnquiryResponse(String request, String leiNumEnquiryResp,
			String req_type, String PINSTID, Map<String, Object> API_REQ_RES_map, String requestUUID)
			throws SQLException {

		Map<String, String> inputMap = new WeakHashMap<>();
		Connection con = null;
		PreparedStatement statement = null;
		String lsql = null;

		try {
			con = DBConnect.getConnection();
			if (con == null) {
				inputMap.put("Result", "Fail");
				inputMap.put("Message", "DB connection not established");
				return inputMap;
			} else {
				con.setAutoCommit(false);
				lsql = ReadPropertyFIle.getInstance().getPropConst().getProperty("API_REQ_RESP_INSERT");

				statement = con.prepareStatement(lsql);
				statement.setString(1, OperationUtillity.NullReplace(PINSTID));
				statement.setString(2, OperationUtillity.NullReplace(request));
				statement.setString(3, OperationUtillity.NullReplace(leiNumEnquiryResp));
				statement.setString(4, OperationUtillity.NullReplace(req_type));
				statement.setString(5, OperationUtillity.NullReplace(API_REQ_RES_map.get("Status").toString()));
				statement.setString(6, OperationUtillity.NullReplace(API_REQ_RES_map.get("ErrorDesc").toString()));
				int int_count = statement.executeUpdate();

				if (int_count > 0) {
					con.commit();
				}
				if (statement != null) {
					statement.close();
				}
			}
		} catch (Exception e) {
			con.rollback();
			logger.info("LEINumberEnquiryUtility.API_RequestResponse_Insert() exception "+ OperationUtillity.traceException(e));
		} finally {
			if (statement != null) {
				statement.close();
			}
			if (con != null) {
				con.close();
			}
		}
		return inputMap;
	}
	
	
}
