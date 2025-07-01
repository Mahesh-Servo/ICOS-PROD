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
import org.springframework.stereotype.Service;

import com.LsmFiServices.FiLsmService.ChildLimitNodeCreationService;
import com.LsmFiServices.Utility.Constants;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.ServiceDetails;
import com.LsmFiServices.Utility.xmlToMap;
import com.LsmFiServices.pojo.serviceexecutiondetails.ServiceDetailsUpdatePojo;

@SuppressWarnings("deprecation")
@Service
public class ChildLimitNodeCreationDao {

    private static final Logger logger = LoggerFactory.getLogger(ChildLimitNodeCreationDao.class);

    @Autowired
    private ServiceDetails serviceDetails;

    public Map<String, String> childLimitNodeCreationDaoImlp(String PINSTID, Map<String, String> childLimitNodeMap)
	    throws SQLException {

	Map<String, String> API_REQ_RES_map = new HashMap<>();
	String childCreationResponse = "";
	SOAPMessage soapRequest = null;
	String RequestUUID = "", HostTransaction = "", Status = "";
	ServiceDetailsUpdatePojo pojo = new ServiceDetailsUpdatePojo();
	String requestType = "";
	try {
	    pojo.setPinstId(PINSTID);
	    pojo.setServiceName("CHILD LIMIT NODE CREATION");
	    pojo.setFacility(childLimitNodeMap.get("FACILITY_NAME"));
	    requestType = "CHILD LIMIT NODE CREATION : " + childLimitNodeMap.get("FACILITY_NAME");
	    pojo.setRequestType(requestType);
	    SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
	    SOAPConnection soapConnection = soapConnectionFactory.createConnection();
	    soapRequest = ChildLimitNodeCreationService.createSOAPRequest(PINSTID, childLimitNodeMap);
	    pojo.setServiceRequest(OperationUtillity.soapMessageToString(soapRequest));
	    pojo.setStatus("Request Sent...!");
	    pojo.setReTrigger(true);
	    serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
	    SOAPMessage soapResponse = soapConnection.call(soapRequest, Constants.FIWebService);
	    childCreationResponse = OperationUtillity.soapMessageToString(soapResponse);
	    childCreationResponse = StringEscapeUtils.unescapeXml(childCreationResponse);
	    if (childCreationResponse.contains("<HostTransaction>")) {
		HostTransaction = childCreationResponse.substring(
			childCreationResponse.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
			childCreationResponse.indexOf("</HostTransaction>"));
		if (HostTransaction.contains("<Status>")) {
		    Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),
			    HostTransaction.indexOf("</Status>"));
		}
	    }
	    if (Status.equalsIgnoreCase("SUCCESS")) {
		API_REQ_RES_map = xmlToMap.successPacketDataToMapChildNodeCreation(PINSTID, childCreationResponse);
		pojo.setStatus(API_REQ_RES_map.get("Status"));
		pojo.setServiceResponse(childCreationResponse);
		pojo.setRequestType(requestType);
		pojo.setReTrigger(false);
		serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
	    } else {
		API_REQ_RES_map = xmlToMap.packetDataToMap(PINSTID, childCreationResponse);
		logger.info("into Rate Of Interest failure -->" + API_REQ_RES_map);
		pojo.setStatus(API_REQ_RES_map.get("Status"));
		pojo.setMessage(API_REQ_RES_map.get("ErrorDesc"));
		pojo.setRequestType(requestType);
		pojo.setServiceResponse(childCreationResponse);
		pojo.setReTrigger(true);
		serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
	    }
	    OperationUtillity.API_RequestResponse_Insert(OperationUtillity.soapMessageToString(soapRequest),
		    childCreationResponse, requestType, PINSTID, API_REQ_RES_map, RequestUUID);
	} catch (Exception Ex) {
	    API_REQ_RES_map.put("RequestUUID", "");
	    API_REQ_RES_map.put("MessageDateTime", "");
	    API_REQ_RES_map.put("Error_Code", "500");
	    API_REQ_RES_map.put("message", Ex.getMessage());
	    API_REQ_RES_map.put("Status", "FAILED");
	    API_REQ_RES_map.put("Error_At", "Child Limit Node Creation");
	    API_REQ_RES_map.put("request", soapRequest.toString());
	    API_REQ_RES_map.put("response", childCreationResponse);
	    logger.info("childLimitNodeCreationDao.childLimitNodeCreationDaoImlp()->"+ OperationUtillity.traceException(PINSTID, Ex));
	}
	return API_REQ_RES_map;
    }
}
