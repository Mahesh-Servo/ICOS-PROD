package com.LsmFiServices.FiLsmService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LsmFiServices.Utility.Constants;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.xmlToMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class VerifyCustIdURCCService {
	private static final Logger logger = LoggerFactory.getLogger(VerifyCustIdURCCService.class);

	@Autowired
	private OperationUtillity utility;

	
	public String VerifyCustIdURCCService(String PINSTID, String URCNO) throws SQLException {
		logger.info("Verify CustID URCC check Service CALLED FOR PINSTID------------------->" + PINSTID,"URC Number " + URCNO);
		Map<String, String> API_REQ_RES_map = new HashMap();
		String Account_Opening_Response = "";
		SOAPMessage soapRequest = null;
		String RequestUUID = "", MessageDateTime = "", AcctId = "", HostTransaction = "", Status = "";
		try {
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			Map<String, String> GetEXTData = null;

			GetEXTData = utility.GetcifIdData(PINSTID);
			soapRequest = createSOAPRequest(PINSTID, GetEXTData, URCNO);

			System.out.println(
					"Verify CustID URCC Check Service SOAP webservice URL ----------> " + Constants.FIWebService);
			logger.info("FIWebService------------------->" + Constants.FIWebService, "");
			SOAPMessage soapResponse = soapConnection.call(soapRequest, Constants.FIWebService);
			Account_Opening_Response = OperationUtillity.soapMessageToString(soapResponse);
			Account_Opening_Response = StringEscapeUtils.unescapeXml(Account_Opening_Response);
			logger.info("Verify CustID URCC_Check_Response----------> " + Account_Opening_Response, "");

			if (Account_Opening_Response.contains("<RequestUUID>")) {
				RequestUUID = Account_Opening_Response.substring(
						Account_Opening_Response.indexOf("<RequestUUID>") + "<RequestUUID>".length(),
						Account_Opening_Response.indexOf("</RequestUUID>"));
			}
			if (Account_Opening_Response.contains("<MessageDateTime>")) {
				MessageDateTime = Account_Opening_Response.substring(
						Account_Opening_Response.indexOf("<MessageDateTime>") + "<MessageDateTime>".length(),
						Account_Opening_Response.indexOf("</MessageDateTime>"));
			}
			if (Account_Opening_Response.contains("<HostTransaction>")) {
				HostTransaction = Account_Opening_Response.substring(
						Account_Opening_Response.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
						Account_Opening_Response.indexOf("</HostTransaction>"));
				if (HostTransaction.contains("<Status>")) {
					Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),
							HostTransaction.indexOf("</Status>"));
				}
			}
			if (Account_Opening_Response.contains("<AcctId>")) {
				AcctId = Account_Opening_Response.substring(
						Account_Opening_Response.indexOf("<AcctId>") + "<AcctId>".length(),
						Account_Opening_Response.indexOf("</AcctId>"));
			}

			logger.info("Verify CustID URCC API Response Status----------->" + Status, "");
			if (Status.equalsIgnoreCase("SUCCESS")) {
				API_REQ_RES_map.put("request header", soapRequest.getSOAPHeader().toString());
				API_REQ_RES_map.put("request body", soapRequest.getSOAPBody().toString());
				API_REQ_RES_map.put("response", Account_Opening_Response);
			} else {
              String Error_Code = "";
              String Error_Detail = "";
              String error_details = "";
              if (Account_Opening_Response.contains("<ErrorDetail>")) {
                  error_details = Account_Opening_Response.substring(Account_Opening_Response.indexOf("<ErrorDetail>") + "<ErrorDetail>".length(), Account_Opening_Response.indexOf("</ErrorDetail>")); 
              }
              if (error_details.contains("<ErrorCode>")) {
                  Error_Code = Account_Opening_Response.substring(error_details.indexOf("<ErrorCode>") + "<ErrorCode>".length(), error_details.indexOf("</ErrorCode>"));
              }
              if (error_details.contains("<ErrorDesc>")) {
                  Error_Detail = Account_Opening_Response.substring(error_details.indexOf("<ErrorDesc>") + "<ErrorDesc>".length(), error_details.indexOf("</ErrorDesc>"));
              }
              API_REQ_RES_map.put("RequestUUID", RequestUUID);
              API_REQ_RES_map.put("MessageDateTime", MessageDateTime);
              API_REQ_RES_map.put("Error_Code", Error_Code);
              API_REQ_RES_map.put("Error_Detail", Error_Detail);
              API_REQ_RES_map.put("Status", Status);
              API_REQ_RES_map.put("Error_At", "Account Opening error");
              API_REQ_RES_map.put("request header", soapRequest.getSOAPHeader().toString());
              API_REQ_RES_map.put("request body", soapRequest.getSOAPBody().toString());
              API_REQ_RES_map.put("response", Account_Opening_Response);
				API_REQ_RES_map = xmlToMap.packetDataToMap(PINSTID, Account_Opening_Response);
			}
		} catch (Exception Ex) {
			logger.info(" Verify CustID URCC_API_Error---------->", Ex);
			API_REQ_RES_map.put("RequestUUID", "");
			API_REQ_RES_map.put("MessageDateTime", "");
			API_REQ_RES_map.put("Error_Code", "500");
			API_REQ_RES_map.put("message", Ex.getMessage());
			API_REQ_RES_map.put("Status", "FAILED");
			API_REQ_RES_map.put("Error_At", "Account Opening error");
			Ex.printStackTrace();
		}
		OperationUtillity.API_RequestResponse_Insert(OperationUtillity.soapMessageToString(soapRequest),
				Account_Opening_Response, "VERIFY CUST_ID URCC", PINSTID, API_REQ_RES_map, RequestUUID);
		logger.info("Verify CustID URCC_map----------->" + API_REQ_RES_map, "");
		String result = "";

		try {
			result = new ObjectMapper().writeValueAsString(API_REQ_RES_map);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;		
	}
		private static SOAPMessage createSOAPRequest(String PINSTID, Map<String, String> GetEXTData, String URCNO)
				throws IOException, SOAPException, ParseException {
			Calendar now = Calendar.getInstance();
//			String newtrannum = now.get(Calendar.DATE) + "" + (now.get(Calendar.MONTH) + 1) + "" + now.get(Calendar.YEAR)
//					+ "" + now.get(Calendar.HOUR_OF_DAY) + "" + now.get(Calendar.MINUTE) + "" + now.get(Calendar.SECOND)
//					+ "" + now.get(Calendar.MILLISECOND);
			String sysdateandtime = now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-"
					+ now.get(Calendar.DATE) + "T" + now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":"
					+ now.get(Calendar.SECOND) + "." + now.get(Calendar.MILLISECOND);
			String soapMessage = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
					+ "<Body>" 
					+ "<verifyCustomerDetailsRequest>" 
					+ "<CustomerVerifyRq>" 
					+ "<cifId>"+ GetEXTData.get("cifId") + "</cifId>" 
					+ "<decision>Approve</decision>"
					+ "<entityName>CorporateCustomer</entityName>" 
					+ "</CustomerVerifyRq>"
					+ "</verifyCustomerDetailsRequest>"
					+ "</Body>" 
					+ "<Header>"
					+ "<RequestHeader>"
					+ "<MessageKey>"
					+ "<ChannelId>CLS</ChannelId>" 
					+ "<LanguageId></LanguageId>"
					+ "<RequestUUID>CV_28202213404467</RequestUUID>"
					+ "<ServiceRequestId>verifyCustomerDetails</ServiceRequestId>"
					+ "<ServiceRequestVersion>10.2</ServiceRequestVersion>" + "</MessageKey>"
					+ "<RequestMessageInfo>" 
					+ "<ArmCorrelationId></ArmCorrelationId>" 
					+ "<BankId>BM3</BankId>"
					+ "<EntityId></EntityId>" 
					+ "<EntityType></EntityType>" 
					+ "<MessageDateTime>" + sysdateandtime+ "</MessageDateTime>" 
					+ "<TimeZone></TimeZone>" 
					+ "</RequestMessageInfo>"
					+ "<Security>"
					+ "<FICertToken></FICertToken>" 
					+ "<SSOTransferToken></SSOTransferToken>"
					+ "<RealUser></RealUser>"
					+ "<RealUserLoginSessionId></RealUserLoginSessionId>"
					+ "<RealUserPwd></RealUserPwd>" 
					+ "<Token>" 
					+ "<PasswordToken>"
					+ "<Password></Password>" 
					+ "<UserId></UserId>"
					+ "</PasswordToken>" 
					+ "</Token>"
					+ "</Security>" 
					+ "</RequestHeader>"
					+ "</Header>" 
					+ "</FIXML>"
					+ "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";

			logger.info("soapMessage----------> " + soapMessage, "");
			InputStream is = new ByteArrayInputStream(soapMessage.getBytes());
			SOAPMessage requestSoap = MessageFactory.newInstance().createMessage(null, is);
			MimeHeaders headers = requestSoap.getMimeHeaders();
			requestSoap.saveChanges();
			return requestSoap;
		}
		
}
