package com.LsmFiServices.FiLsmController;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.LsmFiServices.FiLsmService.lienMarkingService;
import com.LsmFiServices.Utility.Constants;
//import com.LsmFiServices.Utility.CustomLogger;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.xmlToMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

//@Async
@SuppressWarnings("deprecation")
@Controller
public class lienMarkingController {

//	private static final Logger logger = LoggerFactory.getLogger(lienMarkingController.class);

//	@Autowired
//	OperationUtillity OperationUtillity;
//
//	@Autowired
//	xmlToMap xmlToMap;
//
//	Gson gson;

	@RequestMapping(value = { "/lienMarking" }, method = RequestMethod.GET)
	public @ResponseBody String lienMarking(@RequestParam(value = "PINSTID") String PINSTID) throws SQLException {

		Gson gson;

		OperationUtillity OperationUtillity = new OperationUtillity();
		xmlToMap xmlToMap = new xmlToMap();

//		CustomLogger.writeConsoleLogger("LIEN MARKING  check Service CALLED FOR PINSTID------------------->" + PINSTID, "");
		System.out.println("LIEN MARKING  Check Service called For PINSTIND ----------> " + PINSTID);

		Map<String, String> API_REQ_RES_map = new HashMap();
		String Account_Opening_Response = "";
		SOAPMessage soapRequest = null;

		String RequestUUID = "", MessageDateTime = "", AcctId = "", HostTransaction = "", Status = "";
		try {

			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			Map<String, String> GetEXTData = null;

			GetEXTData = OperationUtillity.getLienMarkingData(PINSTID);
			soapRequest = lienMarkingService.createSOAPRequest(PINSTID, GetEXTData);

			System.out.println("LIEN MARKING Check Service SOAP webservice URL ----------> " + Constants.FIWebService);

//			CustomLogger.writeConsoleLogger("FIWebService------------------->" + Constants.FIWebService, "");

			SOAPMessage soapResponse = soapConnection.call(soapRequest, Constants.FIWebService);
			Account_Opening_Response = OperationUtillity.soapMessageToString(soapResponse);
			Account_Opening_Response = StringEscapeUtils.unescapeXml(Account_Opening_Response);

//			CustomLogger.writeConsoleLogger("LIEN MARKING Response----------> " + Account_Opening_Response, "");

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
				API_REQ_RES_map = xmlToMap.successPacketDataToMapLienMarking(PINSTID, Account_Opening_Response);

			} else {
				API_REQ_RES_map = xmlToMap.packetDataToMap(PINSTID, Account_Opening_Response);
			}

		} catch (Exception Ex) {
//			CustomLogger.writeConsoleLogger("Account error exception ----------->", Ex.toString());
			API_REQ_RES_map.put("RequestUUID", "");
			API_REQ_RES_map.put("MessageDateTime", "");
			API_REQ_RES_map.put("Error_Code", "500");
			API_REQ_RES_map.put("message", Ex.getMessage());
			API_REQ_RES_map.put("Status", "FAILED");
			API_REQ_RES_map.put("Error_At", "Lien Marking Controller");
			Ex.printStackTrace();
		}
		try {

			OperationUtillity.API_RequestResponse_Insert(OperationUtillity.soapMessageToString(soapRequest),
					Account_Opening_Response, "Lien Marking", PINSTID, API_REQ_RES_map, RequestUUID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//			CustomLogger.writeConsoleLogger("api insert exception Lien Marking----------->" + e, "");
		}
		String result = "";

		try {
			result = new ObjectMapper().writeValueAsString(API_REQ_RES_map);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//			CustomLogger.writeConsoleLogger("result exception Lien Marking----------->" + e, "");
		}
		System.out.println("lienMarkingController.lienMarking()");
		return result;
	}
}
