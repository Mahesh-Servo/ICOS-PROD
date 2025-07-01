package com.LsmFiServices.FiLsmController;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.LsmFiServices.FiLsmService.lienModificationService;
import com.LsmFiServices.Utility.Constants;
//import com.LsmFiServices.Utility.// CustomLogger;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.xmlToMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@SuppressWarnings("deprecation")
@Controller
public class lienModificationController {
	// account link node
	@RequestMapping(value = { "/lienModification" }, method = RequestMethod.GET)
	public @ResponseBody String lienModification(@RequestParam(value = "PINSTID") String PINSTID)
			throws SOAPException, SQLException {

		Gson gson;

		OperationUtillity OperationUtillity = new OperationUtillity();
		xmlToMap xmlToMap = new xmlToMap();

		// CustomLogger.writeConsoleLogger("LIEN MODIFICATION Check CALLED FOR PINSTID------------------->" + PINSTID, "");
		System.out.println("LIEN MODIFICATION  Check Service called For PINSTIND ----------> " + PINSTID);

		Map<String, String> API_REQ_RES_map = new HashMap<String, String>();
		String Account_Opening_Response = "";
		SOAPMessage soapRequest = null;
		String RequestUUID = "", MessageDateTime = "", AcctId = "", HostTransaction = "", Status = "";

		try {

			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			Map<String, String> GetEXTData = null;

			GetEXTData = OperationUtillity.getlienmodification(PINSTID);
			soapRequest = lienModificationService.createSOAPRequest(PINSTID, GetEXTData);

			System.out.println("LIEN MODIFICATION   Data from Query  :: \n" + GetEXTData.toString());
			// CustomLogger.writeConsoleLogger("LIEN MODIFICATION Data from Query  :: " + GetEXTData.toString(), "");

			System.out.println(
					"LIEN MODIFICATION Check Service SOAP webservice URL ----------> " + Constants.FIWebService);
			// CustomLogger.writeConsoleLogger("FIWebService------------------->" + Constants.FIWebService, "");
			SOAPMessage soapResponse = soapConnection.call(soapRequest, Constants.FIWebService);
			Account_Opening_Response = OperationUtillity.soapMessageToString(soapResponse);
			Account_Opening_Response = StringEscapeUtils.unescapeXml(Account_Opening_Response);
			// CustomLogger.writeConsoleLogger("LIEN MODIFICATION API_Response----------> " + Account_Opening_Response, "");

			if (Account_Opening_Response.contains("<HostTransaction>")) {
				HostTransaction = Account_Opening_Response.substring(
						Account_Opening_Response.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
						Account_Opening_Response.indexOf("</HostTransaction>"));
				if (HostTransaction.contains("<Status>")) {
					Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),
							HostTransaction.indexOf("</Status>"));
				}
			}

			// CustomLogger.writeConsoleLogger("LIEN MODIFICATION API Response Status----------->" + Status, "");
			if (Status.equalsIgnoreCase("SUCCESS")) {

				API_REQ_RES_map = xmlToMap.successPacketDataToMapLienMarkingModification(PINSTID,
						Account_Opening_Response);
			} else {

				API_REQ_RES_map = xmlToMap.packetDataToMap(PINSTID, Account_Opening_Response);
			}
		} catch (Exception Ex) {
			// CustomLogger.writeConsoleLogger(" Account Link with Limit Node _API_Error---------->", Ex.toString());
			API_REQ_RES_map.put("RequestUUID", "");
			API_REQ_RES_map.put("MessageDateTime", "");
			API_REQ_RES_map.put("Error_Code", "500");
			API_REQ_RES_map.put("message", Ex.getMessage());
			API_REQ_RES_map.put("Status", "FAILED");
			API_REQ_RES_map.put("Error_At", "LIEN MODIFICATION");
			API_REQ_RES_map.put("request Header", soapRequest.getSOAPHeader().toString());
			API_REQ_RES_map.put("request body", soapRequest.getSOAPBody().toString());
			API_REQ_RES_map.put("response", Account_Opening_Response);
			Ex.printStackTrace();
		}
		try {
			OperationUtillity.API_RequestResponse_Insert(OperationUtillity.soapMessageToString(soapRequest),
					Account_Opening_Response, "LIEN MODIFICATION", PINSTID, API_REQ_RES_map, RequestUUID);
		} catch (Exception e) {
			e.printStackTrace();
			// CustomLogger.writeConsoleLogger("LIEN MODIFICATION API Insert exception map----------->/n" + e, "");
		}

		String result = "";

		try {
			result = new ObjectMapper().writeValueAsString(API_REQ_RES_map);
			// CustomLogger.writeConsoleLogger("LIEN MODIFICATION result map----------->/n" + result, "");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return result;

	}

}
