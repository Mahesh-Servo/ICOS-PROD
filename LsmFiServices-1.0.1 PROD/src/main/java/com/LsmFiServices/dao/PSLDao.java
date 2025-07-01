package com.LsmFiServices.dao;

import java.sql.SQLException;
import java.util.LinkedHashMap;
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

import com.LsmFiServices.FiLsmService.PSLService;
import com.LsmFiServices.Utility.Constants;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.ServiceDetails;
import com.LsmFiServices.Utility.xmlToMap;
import com.LsmFiServices.pojo.serviceexecutiondetails.ServiceDetailsUpdatePojo;

@Controller
public class PSLDao {
	private static final Logger logger = LoggerFactory.getLogger(PSLDao.class);

	@Autowired
	private ServiceDetails serviceDetails;

	public @ResponseBody Map<String, String> PSLDaoImlp(String PINSTID, Map<String, String> PSLMap, int i)
			throws SQLException {

		Map<String, String> API_REQ_RES_map = new LinkedHashMap<>();
		String pslResponse = "";
		SOAPMessage soapRequest = null;
		String HostTransaction = "", Status = "";
		ServiceDetailsUpdatePojo pojo = new ServiceDetailsUpdatePojo();
		String requestType = "";
		try {
			pojo.setPinstId(PINSTID);
			pojo.setServiceName("PSL");
			pojo.setAccountNumber(PSLMap.get("ACCOUNT_NO_" + i));
			pojo.setFacility(PSLMap.get("FACILITY_NAME"));
			requestType = "PSL : " + PSLMap.get("FACILITY_NAME") + " Account No : " + PSLMap.get("ACCOUNT_NO_" + i)
					+ " ";
			pojo.setRequestType(requestType);
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			soapRequest = PSLService.createSOAPRequest(PINSTID, PSLMap, i);
			pojo.setServiceRequest(OperationUtillity.soapMessageToString(soapRequest));
			pojo.setStatus("Request Sent...!");
			pojo.setReTrigger(true);
			serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			SOAPMessage soapResponse = soapConnection.call(soapRequest, Constants.FIWebService);
			pslResponse = OperationUtillity.soapMessageToString(soapResponse);
			pslResponse = StringEscapeUtils.unescapeXml(pslResponse);
			if (pslResponse.contains("<HostTransaction>")) {
				HostTransaction = pslResponse.substring(
						pslResponse.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
						pslResponse.indexOf("</HostTransaction>"));
				if (HostTransaction.contains("<Status>")) {
					Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),
							HostTransaction.indexOf("</Status>"));
				}
			}
			if (Status.equalsIgnoreCase("SUCCESS")) {
				API_REQ_RES_map = xmlToMap.successPacketDataToMapPSL(PINSTID, pslResponse);
				pojo.setStatus(API_REQ_RES_map.get("Status"));
				pojo.setServiceResponse(pslResponse);
				pojo.setRequestType(requestType);
				pojo.setReTrigger(false);
				serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			} else {
				API_REQ_RES_map = xmlToMap.packetDataToMap(PINSTID, pslResponse);
				pojo.setStatus(API_REQ_RES_map.get("Status"));
				pojo.setMessage(API_REQ_RES_map.get("ErrorDesc"));
				pojo.setRequestType(requestType);
				pojo.setServiceResponse(pslResponse);
				pojo.setReTrigger(true);
				serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			}
			OperationUtillity.API_RequestResponse_Insert(OperationUtillity.soapMessageToString(soapRequest),
					pslResponse, requestType, PINSTID, API_REQ_RES_map, "");
		} catch (Exception Ex) {
			API_REQ_RES_map.put("RequestUUID", "");
			API_REQ_RES_map.put("MessageDateTime", "");
			API_REQ_RES_map.put("Error_Code", "500");
			API_REQ_RES_map.put("message", Ex.getMessage());
			API_REQ_RES_map.put("Status", "FAILED");
			API_REQ_RES_map.put("Error_At", "PSL");
			logger.info("PSLDao.PSLDaoImlp()->" + OperationUtillity.traceException(PINSTID, Ex));
		}
		return API_REQ_RES_map;
	}
}
