package com.LsmFiServices.FiLsmService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import com.LsmFiServices.Utility.commonUtility;

public class accountEnquiryService {


	public static SOAPMessage createSOAPRequest(String PINSTID, Map<String, String> GetEXTData, String ACCNO)
			throws IOException, SOAPException, ParseException {

		String OldsoapMessage = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "<Body>" 
				+ "<executeFinacleScriptRequest>" 
				+ "<executeFinacleScript_CustomData>" 
				+ "<Account_Number>"+ ACCNO + "</Account_Number>" 
				+ "</executeFinacleScript_CustomData>" 
				+ "<ExecuteFinacleScriptInputVO>"
				+ "<requestId>FI_HACLI_Inq.scr</requestId>"
				+ "</ExecuteFinacleScriptInputVO>"
				+ "</executeFinacleScriptRequest>" 
				+ "</Body>" 
				+ "<Header>"
				+ "<RequestHeader>" 
				+ "<MessageKey>"
				+ "<ChannelId>CLS</ChannelId>"
				+ "<LanguageId></LanguageId>" 
				+ "<RequestUUID>" + commonUtility.createRequestUUID() + "</RequestUUID>" 
				+ "<ServiceRequestId>executeFinacleScript</ServiceRequestId>"
				+ "<ServiceRequestVersion>10.2</ServiceRequestVersion>"
				+ "</MessageKey>" 
				+ "<RequestMessageInfo>"
				+ "<ArmCorrelationId></ArmCorrelationId>" 
				+ "<BankId>BM3</BankId>" 
				+ "<EntityId></EntityId>"
				+ "<EntityType></EntityType>" 
				+ "<MessageDateTime>" + LocalDateTime.now() + "</MessageDateTime>"
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
	

		InputStream is = new ByteArrayInputStream(OldsoapMessage.getBytes());
		SOAPMessage requestSoap = MessageFactory.newInstance().createMessage(null, is);
		requestSoap.saveChanges();
		return requestSoap;
	}
}
