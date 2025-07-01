package com.LsmFiServices.FiLsmController;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.LsmFiServices.FiLsmService.accountEnquiryService;
import com.LsmFiServices.Utility.Constants;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.xmlToMap;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class accountEnquiryController {

    private static final Logger logger = LoggerFactory.getLogger(accountEnquiryController.class);

    @Autowired
    private OperationUtillity opUtils;

    @Autowired
    private xmlToMap xmlToMapData;

    @Autowired
    private StatusCodeControllerNew StatusCodeControllerNew;

    @RequestMapping(value = { "/accountEnquiry" }, method = RequestMethod.GET)
    public @ResponseBody String accountEnquiry(@RequestParam(value = "PINSTID") String PINSTID,
	    @RequestParam(value = "ACCNO") String ACCNO) throws SQLException {

	Map<String, String> API_REQ_RES_map = new HashMap<>();
	String Account_Opening_Response = "";
	SOAPMessage soapRequest = null;
	int count_sc = 0;

	String RequestUUID = "", HostTransaction = "", Status = "";
	try {

	    SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
	    SOAPConnection soapConnection = soapConnectionFactory.createConnection();
	    Map<String, String> GetEXTData = null;

	    GetEXTData = opUtils.GetCustInqExtData(PINSTID);
	    soapRequest = accountEnquiryService.createSOAPRequest(PINSTID, GetEXTData, ACCNO);

	    SOAPMessage soapResponse = soapConnection.call(soapRequest, Constants.FIWebService);
	    Account_Opening_Response = OperationUtillity.soapMessageToString(soapResponse);
	    Account_Opening_Response = StringEscapeUtils.unescapeXml(Account_Opening_Response);
	    if (Account_Opening_Response.contains("<HostTransaction>")) {
		HostTransaction = Account_Opening_Response.substring(
			Account_Opening_Response.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
			Account_Opening_Response.indexOf("</HostTransaction>"));
		if (HostTransaction.contains("<Status>")) {
		    Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),
			    HostTransaction.indexOf("</Status>"));
		}
	    }
	    if (Status.equalsIgnoreCase("SUCCESS")) {
		API_REQ_RES_map = xmlToMapData.successPacketDataToMapAccInq(PINSTID, Account_Opening_Response);

		logger.info("accountEnquiryController.accountEnquiry():: Cust_Status "
			+ API_REQ_RES_map.get("Cust_Status"));
//		count_sc = OperationUtillity.getStatusCodeForUpdate(PINSTID, API_REQ_RES_map.get("Cust_Status"));
//		logger.info("accountEnquiryController.accountEnquiry():: Count " + count_sc);
//		if (count_sc == 0) {
//		    StatusCodeControllerNew.statusCodeUpdate(PINSTID, API_REQ_RES_map.get("Cust_Status"),API_REQ_RES_map);
//		}

	    } else {
		API_REQ_RES_map = xmlToMapData.packetDataToMap(PINSTID, Account_Opening_Response);
	    }
	} catch (Exception Ex) {
	    API_REQ_RES_map.put("RequestUUID", "");
	    API_REQ_RES_map.put("MessageDateTime", "");
	    API_REQ_RES_map.put("Error_Code", API_REQ_RES_map.get("ErrorCode"));
	    API_REQ_RES_map.put("message", API_REQ_RES_map.get("ErrorDesc"));
	    API_REQ_RES_map.put("Status", API_REQ_RES_map.get("Status"));
	    API_REQ_RES_map.put("Error_At", "Account Enquiry");
	    API_REQ_RES_map.put("Request", soapRequest.toString());
	    API_REQ_RES_map.put("Response", Account_Opening_Response);
	    logger.info(
		    "accountEnquiryController.accountEnquiry() Exception ->" + OperationUtillity.traceException(PINSTID,Ex));
	}
	String result = "";
	try {
	    OperationUtillity.API_RequestResponse_Insert(OperationUtillity.soapMessageToString(soapRequest),
		    Account_Opening_Response, "ACCOUNT INQUIRY", PINSTID, API_REQ_RES_map, RequestUUID);
	    result = new ObjectMapper().writeValueAsString(API_REQ_RES_map);
	} catch (Exception e) {
	    logger.info("accountEnquiryController.accountEnquiry() Exception ->" + OperationUtillity.traceException(PINSTID,e));
	}
	return result;
    }
}
