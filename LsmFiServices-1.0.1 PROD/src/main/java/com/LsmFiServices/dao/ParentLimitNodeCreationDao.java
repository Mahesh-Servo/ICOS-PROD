package com.LsmFiServices.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.LsmFiServices.FiLsmService.parentLimitNodeCreationService;
import com.LsmFiServices.Utility.Constants;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.ServiceDetails;
import com.LsmFiServices.Utility.xmlToMap;
import com.LsmFiServices.pojo.serviceexecutiondetails.ServiceDetailsUpdatePojo;

@SuppressWarnings("deprecation")
@Controller
public class ParentLimitNodeCreationDao {

    private static final Logger logger = LoggerFactory.getLogger(ParentLimitNodeCreationDao.class);

    @Autowired
    private ServiceDetails serviceDetails;

    public String parentLimitNodeCreationService(String PINSTID, Map<String, String> GetEXTData)
	    throws SOAPException, SQLException {

	Map<String, String> API_REQ_RES_map = new HashMap<>();
	String parentLimitNodeCreationResp = "";
	SOAPMessage soapRequest = null;
	String RequestUUID = "", HostTransaction = "", Status = "";
	ServiceDetailsUpdatePojo pojo = new ServiceDetailsUpdatePojo();
	String requestType = "PARENT LIMIT NODE CREATION";
	try {
	    pojo.setPinstId(PINSTID);
	    pojo.setServiceName(requestType);
	    pojo.setRequestType(requestType);
	    SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
	    SOAPConnection soapConnection = soapConnectionFactory.createConnection();
	    soapRequest = parentLimitNodeCreationService.createSOAPRequest(PINSTID, GetEXTData);
	    pojo.setStatus("Request Sent...!");
	    pojo.setServiceRequest(OperationUtillity.soapMessageToString(soapRequest));
	    pojo.setReTrigger(true);
	    serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
	    SOAPMessage soapResponse = soapConnection.call(soapRequest, Constants.FIWebService);
	    parentLimitNodeCreationResp = OperationUtillity.soapMessageToString(soapResponse);
	    parentLimitNodeCreationResp = StringEscapeUtils.unescapeXml(parentLimitNodeCreationResp);
	    if (parentLimitNodeCreationResp.contains("<HostTransaction>")) {
		HostTransaction = parentLimitNodeCreationResp.substring(
			parentLimitNodeCreationResp.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
			parentLimitNodeCreationResp.indexOf("</HostTransaction>"));
		if (HostTransaction.contains("<Status>")) {
		    Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),
			    HostTransaction.indexOf("</Status>"));
		}
	    }
	    if (Status.equalsIgnoreCase("SUCCESS")) {
		API_REQ_RES_map = xmlToMap.successPacketDataToMapParentNodeCreation(PINSTID,
			parentLimitNodeCreationResp);
		pojo.setStatus(Status);
		pojo.setServiceResponse(parentLimitNodeCreationResp);
		pojo.setRequestType(requestType);
		pojo.setReTrigger(false);
		serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
	    } else {
		API_REQ_RES_map = xmlToMap.packetDataToMap(PINSTID, parentLimitNodeCreationResp);
		pojo.setStatus(API_REQ_RES_map.get("Status"));
		pojo.setMessage(API_REQ_RES_map.get("ErrorDesc"));
		pojo.setRequestType(requestType);
		pojo.setServiceResponse(parentLimitNodeCreationResp);
		pojo.setReTrigger(true);
		serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
	    }
	    OperationUtillity.API_RequestResponse_Insert(OperationUtillity.soapMessageToString(soapRequest),
		    parentLimitNodeCreationResp, requestType, PINSTID, API_REQ_RES_map, RequestUUID);
	} catch (Exception Ex) {
	    API_REQ_RES_map.put("RequestUUID", "");
	    API_REQ_RES_map.put("MessageDateTime", "");
	    API_REQ_RES_map.put("Error_Code", "500");
	    API_REQ_RES_map.put("message", Ex.getMessage());
	    API_REQ_RES_map.put("Status", "FAILED");
	    API_REQ_RES_map.put("Error_At", "Parent Limit Node Creation");
	    API_REQ_RES_map.put("request Header", soapRequest.getSOAPHeader().toString());
	    API_REQ_RES_map.put("request body", soapRequest.getSOAPBody().toString());
	    API_REQ_RES_map.put("response", parentLimitNodeCreationResp);
	    logger.error("parentLimitNodeCreationDao.parentLimitNodeCreationDaoImpl(){}" + PINSTID + " :: "
		    + OperationUtillity.traceException(Ex));
	}
	return API_REQ_RES_map.get("Status");
    }
}
