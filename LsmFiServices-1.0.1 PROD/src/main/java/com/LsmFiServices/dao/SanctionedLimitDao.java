package com.LsmFiServices.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import com.LsmFiServices.FiLsmService.SanctionedLimitService;
import com.LsmFiServices.Utility.Constants;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.ServiceDetails;
import com.LsmFiServices.Utility.xmlToMap;
import com.LsmFiServices.pojo.serviceexecutiondetails.ServiceDetailsUpdatePojo;

@SuppressWarnings("deprecation")
@Controller
public class SanctionedLimitDao {

	private static final Logger logger = LoggerFactory.getLogger(SanctionedLimitDao.class);

	@Autowired
	private ServiceDetails serviceDetails;

	public @ResponseBody Map<String, String> sanctionedLimitDaoImlp(String PINSTID,
			Map<String, String> sanctionedLimitMap, int i) throws SQLException {

		Map<String, String> API_REQ_RES_map = new HashMap<>();
		String sanctionLimitResponse = "";
		String HostTransaction = "", Status = "";
		ServiceDetailsUpdatePojo pojo = new ServiceDetailsUpdatePojo();
		String requestType = "";
		try {
			pojo.setPinstId(PINSTID);
			pojo.setServiceName("SANCTION LIMIT NODE");
			pojo.setAccountNumber(sanctionedLimitMap.get("ACCOUNT_NO_" + i));
			pojo.setFacility(sanctionedLimitMap.get("FACILITY_NAME"));
			requestType = "SANCTION LIMIT :" + sanctionedLimitMap.get("FACILITY_NAME") + " :  "
					+ sanctionedLimitMap.get("ACCOUNT_NO_" + i) + "";
			pojo.setRequestType(requestType);
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			SOAPMessage soapRequest = SanctionedLimitService.createSOAPRequest(PINSTID, sanctionedLimitMap, i);
			pojo.setServiceRequest(OperationUtillity.soapMessageToString(soapRequest));
			pojo.setStatus("Request Sent...!");
			pojo.setReTrigger(true);
			serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			SOAPMessage soapResponse = soapConnection.call(soapRequest, Constants.FIWebService);
			sanctionLimitResponse = OperationUtillity.soapMessageToString(soapResponse);
			sanctionLimitResponse = StringEscapeUtils.unescapeXml(sanctionLimitResponse);
			if (sanctionLimitResponse.contains("<HostTransaction>")) {
				HostTransaction = sanctionLimitResponse.substring(
						sanctionLimitResponse.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
						sanctionLimitResponse.indexOf("</HostTransaction>"));
				if (HostTransaction.contains("<Status>")) {
					Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),
							HostTransaction.indexOf("</Status>"));
				}
			}
			if (Status.equalsIgnoreCase("SUCCESS")) {
				API_REQ_RES_map = xmlToMap.successPacketDataToMapSanctionLimit(PINSTID, sanctionLimitResponse);
				pojo.setStatus(API_REQ_RES_map.get("Status"));
				pojo.setServiceResponse(sanctionLimitResponse);
				pojo.setRequestType(requestType);
				pojo.setReTrigger(false);
				serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			} else {
				API_REQ_RES_map = xmlToMap.packetDataToMap(PINSTID, sanctionLimitResponse);
				pojo.setStatus(API_REQ_RES_map.get("Status"));
				pojo.setMessage(API_REQ_RES_map.get("ErrorDesc"));
				pojo.setRequestType(requestType);
				pojo.setServiceResponse(sanctionLimitResponse);
				pojo.setReTrigger(true);
				serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			}
			OperationUtillity.API_RequestResponse_Insert(OperationUtillity.soapMessageToString(soapRequest),
					sanctionLimitResponse, requestType, PINSTID, API_REQ_RES_map, "");
		} catch (Exception Ex) {
			API_REQ_RES_map.put("RequestUUID", "");
			API_REQ_RES_map.put("MessageDateTime", "");
			API_REQ_RES_map.put("Error_Code", "500");
			API_REQ_RES_map.put("message", Ex.getMessage());
			API_REQ_RES_map.put("Status", "FAILED");
			API_REQ_RES_map.put("Error_At", "Sanction Limit");
			API_REQ_RES_map.put("response", sanctionLimitResponse);
			logger.error("SanctionedLimitDao.sanctionedLimitDaoImlp()\n{}", OperationUtillity.traceException(Ex));
		}
		return API_REQ_RES_map;
	}
}
