package com.LsmFiServices.FiLsmService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import com.LsmFiServices.Utility.commonUtility;

public class lienMarkingService {
	
//	private static final Logger logger = LoggerFactory.getLogger(lienMarkingService.class);
	
	public static SOAPMessage createSOAPRequest(String PINSTID, Map<String, String> GetEXTData)
			throws IOException, SOAPException, ParseException {

		String requestUuid = commonUtility.createRequestUUID();
		String dateAndTime = commonUtility.dateFormat();

		String soapMessage = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
				"<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + 
				"   <Body>" + 
				"      <AcctLienAddRequest>" + 
				"         <AcctLienAddRq>" + 
				"            <AcctId>" + 
				"               <AcctId>" + GetEXTData.get("ACCOUNT_NO") + "</AcctId>      " +  //-----> LSM RM Que---> Acc. No.
				"            </AcctId>" + 
				"            <LienDtls>" + 
				"               <LienDt/>" + 
				"               <NewLienAmt>" + 
				"                  <amountValue>"+GetEXTData.get("LIMIT_AMT")+"</amountValue>    " + //   -------> Limit TAB ----> Limit Amount
				"                  <currencyCode>"+GetEXTData.get("CURRENCY")+"</currencyCode> " +  //  -------> Limit TAB ----> Currency
				"               </NewLienAmt> " + 
				"               <ReasonCode>RESER</ReasonCode>     " + //---Hardcode
				"               <Rmks>"+PINSTID+"</Rmks> " + //  -----> Pinstid
				"            </LienDtls>" + 
				"            <ModuleType>ULIEN</ModuleType>  " +  // ---Hardcode
				"         </AcctLienAddRq>" + 
				"      </AcctLienAddRequest>" + 
				"   </Body>" + 
				"   <Header>" + 
				"      <RequestHeader>" + 
				"         <MessageKey>" + 
//				"            <ChannelId>CMT</ChannelId>" + 
				"            <ChannelId>CLS</ChannelId>" + 	//changed on 26.10.2023
				"            <LanguageId></LanguageId>" + 
				"            <RequestUUID>"+requestUuid+"</RequestUUID>" + 
				"            <ServiceRequestId>AcctLienAdd</ServiceRequestId>" + 
				"            <ServiceRequestVersion>10.2</ServiceRequestVersion>" + 
				"         </MessageKey>" + 
				"         <RequestMessageInfo>" + 
				"            <ArmCorrelationId></ArmCorrelationId>" + 
				"            <BankId>BM3</BankId>" + 
				"            <EntityId></EntityId>" + 
				"            <EntityType></EntityType>" + 
				"            <MessageDateTime>"+dateAndTime+"</MessageDateTime>" + 
				"            <TimeZone></TimeZone>" + 
				"         </RequestMessageInfo>" + 
				"         <Security>" + 
				"            <FICertToken></FICertToken>" + 
				"            <SSOTransferToken></SSOTransferToken>" + 
				"            <RealUser></RealUser>" + 
				"            <RealUserLoginSessionId></RealUserLoginSessionId>" + 
				"            <RealUserPwd></RealUserPwd>" + 
				"            <Token>" + 
				"               <PasswordToken>" + 
				"                  <Password></Password>" + 
				"                  <UserId></UserId>" + 
				"               </PasswordToken>" + 
				"            </Token>" + 
				"         </Security>" + 
				"      </RequestHeader>" + 
				"   </Header>" + 
				"</FIXML>" + 
				"]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";

//		logger.info("soapMessage----------> " + soapMessage, "");

		InputStream is = new ByteArrayInputStream(soapMessage.getBytes());
		SOAPMessage requestSoap = MessageFactory.newInstance().createMessage(null, is);
//		MimeHeaders headers = requestSoap.getMimeHeaders();
		requestSoap.saveChanges();
		return requestSoap;
	}

}
