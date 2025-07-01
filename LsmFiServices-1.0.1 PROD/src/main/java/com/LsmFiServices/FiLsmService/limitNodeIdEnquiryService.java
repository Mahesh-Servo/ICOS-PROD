package com.LsmFiServices.FiLsmService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.LsmFiServices.Utility.commonUtility;

@Service
public class limitNodeIdEnquiryService {

	private static final Logger logger = LoggerFactory.getLogger(limitNodeIdEnquiryService.class);
	
	public static String createSOAPRequest(String PINSTID, String LmtPrefix, String LmtSuffix)
			throws IOException, SOAPException, ParseException {
		
//		logger.info("limitNodeIdEnquiryService.createSOAPRequest().PINSTID ::"+ PINSTID +" , LmtPrefix():: " + LmtPrefix +" , LmtSuffix():: " + LmtSuffix);
//		DBlogs.log_sout("limitNodeIdEnquiryService.createSOAPRequest().LimitNodeId :: " , "PINSTID ::"+ PINSTID +" LmtPrefix():: " + LmtPrefix +" LmtSuffix():: " + LmtSuffix);
		
		String requestUuid = commonUtility.createRequestUUID();		
		String dateAndTime = commonUtility.dateFormat();
		 		
		String soapMessage = 
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\">"+
				"<soapenv:Header/>"+
				"<soapenv:Body>"+
				"<web:executeService>"+
				"<arg_0_0>"+
				"<![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
				"<FIXML" + 
				"    xmlns=\"http://www.finacle.com/fixml\"" + 
				"    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + 
				"    <Header>" + 
				"        <RequestHeader>" + 
				"            <MessageKey>" + 
				"                <RequestUUID>"+requestUuid+"</RequestUUID>" + 
				"                <ServiceRequestId>executeFinacleScript</ServiceRequestId>" + 
				"                <ServiceRequestVersion>10.2</ServiceRequestVersion>" + 
				"                <ChannelId>CLS</ChannelId>" + 
				"                <LanguageId></LanguageId>" + 
				"            </MessageKey>" + 
				"            <RequestMessageInfo>" + 
				"                <BankId>BM3</BankId>" +
				"  				 <TimeZone></TimeZone>" + 
				"                <EntityId></EntityId>" + 
				"                <EntityType></EntityType>" + 
				"                <ArmCorrelationId></ArmCorrelationId>" + 
				"                <MessageDateTime>"+dateAndTime+"</MessageDateTime>" + 
				"            </RequestMessageInfo>" + 
				"            <Security>" + 
				"                <Token>" + 
				"                    <PasswordToken>" + 
				"                        <UserId></UserId>" + 
				"                        <Password></Password>" + 
				"                    </PasswordToken>" + 
				"                </Token>" + 
				"                <FICertToken></FICertToken>" + 
				"                <RealUserLoginSessionId></RealUserLoginSessionId>" + 
				"                <RealUser></RealUser>" + 
				"                <RealUserPwd></RealUserPwd>" +
				"				 <SSOTransferToken></SSOTransferToken>" + 
				"            </Security>" + 
				"        </RequestHeader>" + 
				"    </Header>" + 
				"    <Body>" + 
				"        <executeFinacleScriptRequest>" + 
				"            <ExecuteFinacleScriptInputVO>" + 
				"                <requestId>FI_CMART_LimitFetch.scr</requestId>" + 
				"            </ExecuteFinacleScriptInputVO>" + 
				"            <executeFinacleScript_CustomData>" +
				"                <LmtPrefix>"+ LmtPrefix +"</LmtPrefix>" +
//				"                <LmtPrefix>000105021587</LmtPrefix>" + // LIMIT PREFIX TO BE MAPPED
//				"                <LmtSuffix>CC</LmtSuffix>" +  // LIMIT SUFFIX TO BE MAPPED
				"                <LmtSuffix>"+ LmtSuffix +"</LmtSuffix>" +
				"            </executeFinacleScript_CustomData>" + 
				"        </executeFinacleScriptRequest>" + 
				"    </Body>" + 
				"	</FIXML>"+
				"]]></arg_0_0>"+ 
				"</web:executeService>"+ 
				 "</soapenv:Body>"+ 
				"</soapenv:Envelope>";
		
		logger.info("[limitNodeIdEnquiryService.createSOAPRequest().createSOAPPacket()] \nPINSTID ::"+ PINSTID +" ,\n soapMessage():: " + soapMessage);
		
		return soapMessage;
	}
	
}
