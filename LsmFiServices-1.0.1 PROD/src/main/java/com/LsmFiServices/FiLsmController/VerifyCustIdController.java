package com.LsmFiServices.FiLsmController;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.LsmFiServices.FiLsmService.VerifyCustIdService;
import com.LsmFiServices.Utility.Constants;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.xmlToMap;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class VerifyCustIdController {

    private static final Logger logger = LoggerFactory.getLogger(VerifyCustIdController.class);

    @SuppressWarnings("deprecation")
    @GetMapping("/verifyCustId")
    public @ResponseBody String verifyCustId(@RequestParam(value = "PINSTID") String PINSTID,
	    @RequestParam(value = "CUSTID") String CUSTID) throws SQLException {

	logger.info("Verifying CustID Check Service called For PINSTIND ----------> " + PINSTID);
	Map<String, String> API_REQ_RES_map = new LinkedHashMap<>();
	String Account_Opening_Response = "";
	SOAPMessage soapRequest = null;
	String RequestUUID = "", HostTransaction = "", Status = "";
	String result = "";

	try {
	    SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
	    SOAPConnection soapConnection = soapConnectionFactory.createConnection();
	    Map<String, String> GetEXTData = null;
	    GetEXTData = OperationUtillity.GetcifIdData(PINSTID);
	    soapRequest = VerifyCustIdService.createSOAPRequest(PINSTID, GetEXTData, CUSTID);
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
		API_REQ_RES_map = xmlToMap.successPacketDataToMapCustIdVerify(PINSTID, Account_Opening_Response);
	    } else {
		API_REQ_RES_map = xmlToMap.packetDataToMap(PINSTID, Account_Opening_Response);

	    }
	    OperationUtillity.API_RequestResponse_Insert(OperationUtillity.soapMessageToString(soapRequest),
		    Account_Opening_Response, "VERIFY CUST_ID", PINSTID, API_REQ_RES_map, RequestUUID);
	    result = new ObjectMapper().writeValueAsString(API_REQ_RES_map);
	} catch (Exception Ex) {
	    API_REQ_RES_map.put("RequestUUID", "");
	    API_REQ_RES_map.put("MessageDateTime", "");
	    API_REQ_RES_map.put("errorCode", "500");
	    API_REQ_RES_map.put("message", Ex.getMessage());
	    API_REQ_RES_map.put("Status", "FAILED");
	    API_REQ_RES_map.put("Error_At", "Verify CustID");
	    API_REQ_RES_map.put("recordId", "");
	    Ex.printStackTrace();
	}
	return result;
    }
}
