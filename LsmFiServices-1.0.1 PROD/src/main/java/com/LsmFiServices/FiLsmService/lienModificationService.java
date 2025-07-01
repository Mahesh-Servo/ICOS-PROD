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

public class lienModificationService {

//	private static final Logger logger = LoggerFactory.getLogger(lienModificationService.class);

	public static SOAPMessage createSOAPRequest(String PINSTID, Map<String, String> GetEXTData)
			throws IOException, SOAPException, ParseException {

		String requestUuid = commonUtility.createRequestUUID();
		String dateAndTime = commonUtility.dateFormat();

		/*
//		 Old LEI UPDATION REQUEST
		String soapMessage = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "   <Body>" 
				+ "      <AcctLienModRequest>" 
				+ "         <AcctLienModRq>" 
				+ "            <AcctId>"
				+ "               <AcctId>" + GetEXTData.get("ACCOUNT_NO") + "</AcctId>" 
				+ "            </AcctId>"
				+ "            <LienDtls>" 
				+ "               <LienDt>"
				+ "                  <EndDt>2099-12-31T00:00:00.000</EndDt>" 
				+ "               </LienDt>"
				+ "               <NewLienAmt>" 
				+ "                  <amountValue>" + GetEXTData.get("LIMIT_AMT")+ "</amountValue>" 
				+ "                  <currencyCode>" + GetEXTData.get("CURRENCY") + "</currencyCode>"
				+ "               </NewLienAmt>" 
				+ "               <ReasonCode>RESER</ReasonCode>"
				+ "               <Rmks>" + PINSTID + "</Rmks>" 
				+ "            </LienDtls>"
				+ "            <ModuleType>ULIEN</ModuleType>" 
				+ "         </AcctLienModRq>"
				+ "      </AcctLienModRequest>" 
				+ "   </Body>" 
				+ "   <Header>" 
				+ "      <RequestHeader>"
				+ "         <MessageKey>" 
//				+ "            <ChannelId>CMT</ChannelId>"
				+ "            <ChannelId>CLS</ChannelId>"  //changed on 26.10.2023
				+ "            <LanguageId></LanguageId>" 
				+ "            <RequestUUID>" + requestUuid + "</RequestUUID>"
				+ "            <ServiceRequestId>AcctLienMod</ServiceRequestId>"
				+ "            <ServiceRequestVersion>10.2</ServiceRequestVersion>" 
				+ "         </MessageKey>"
				+ "         <RequestMessageInfo>" 
				+ "            <ArmCorrelationId></ArmCorrelationId>"
				+ "            <BankId>BM3</BankId>" 
				+ "            <EntityId></EntityId>"
				+ "            <EntityType></EntityType>" 
				+ "            <MessageDateTime>" + dateAndTime+ "</MessageDateTime>" 
				+ "            <TimeZone></TimeZone>" 
				+ "         </RequestMessageInfo>"
				+ "         <Security>" 
				+ "            <FICertToken></FICertToken>"
				+ "            <SSOTransferToken></SSOTransferToken>" 
				+ "            <RealUser></RealUser>"
				+ "            <RealUserLoginSessionId></RealUserLoginSessionId>"
				+ "            <RealUserPwd></RealUserPwd>" 
				+ "            <Token>" 
				+ "               <PasswordToken>"
				+ "                  <Password></Password>" 
				+ "                  <UserId></UserId>"
				+ "               </PasswordToken>" 
				+ "            </Token>" 
				+ "         </Security>"
				+ "      </RequestHeader>" 
				+"   </Header>" 
				+ "</FIXML>"
				+ "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
		
		*/
		String soapMessage = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><FIXML xsi:schemaLocation=\"http://www.finacle.com/fixml " + 
				"executeFinacleScript.xsd\" xmlns=\"http://www.finacle.com/fixml\" " + 
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + 
				"        <Header>" + 
				"            <RequestHeader>" + 
				"                <MessageKey>" + 
				"                    <RequestUUID>"+requestUuid+"</RequestUUID>" + 
				"                    <ServiceRequestId>executeFinacleScript</ServiceRequestId>" + 
				"                    <ServiceRequestVersion>10.2</ServiceRequestVersion>" + 
				"                    <ChannelId>CLS</ChannelId>" + 
				"                    <LanguageId>" + 
				"                    </LanguageId>" + 
				"                </MessageKey>" + 
				"                <RequestMessageInfo>" + 
				"                    <BankId>BM3</BankId>" + 
				"                    <TimeZone>" + 
				"                    </TimeZone>" + 
				"                    <EntityId>" + 
				"                    </EntityId>" + 
				"                    <EntityType>" + 
				"                    </EntityType>" + 
				"                    <ArmCorrelationId>" + 
				"                    </ArmCorrelationId>" + 
				"                    <MessageDateTime>"+dateAndTime+"</MessageDateTime>" + 
				"                </RequestMessageInfo>" + 
				"                <Security>" + 
				"                    <Token>" + 
				"                        <PasswordToken>" + 
				"                            <UserId>" + 
				"                            </UserId>" + 
				"                            <Password>" + 
				"                            </Password>" + 
				"                        </PasswordToken>" + 
				"                    </Token>" + 
				"                    <FICertToken>" + 
				"                    </FICertToken>" + 
				"                    <RealUserLoginSessionId>" + 
				"                    </RealUserLoginSessionId>" + 
				"                    <RealUser>" + 
				"                    </RealUser>" + 
				"                    <RealUserPwd>" + 
				"                    </RealUserPwd>" + 
				"                    <SSOTransferToken>" + 
				"                    </SSOTransferToken>" + 
				"                </Security>" + 
				"            </RequestHeader>" + 
				"        </Header>" + 
				"        <Body>" + 
				"            <executeFinacleScriptRequest>" + 
				"                <ExecuteFinacleScriptInputVO>" + 
				"                    <requestId>FI_CMN_AllDocDetails.scr</requestId>" + 
				"                </ExecuteFinacleScriptInputVO>" + 
				"                <executeFinacleScript_CustomData>" + 
				"                    <Customer_Id>526954039</Customer_Id>" + 
				"                </executeFinacleScript_CustomData>" + 
				"            </executeFinacleScriptRequest>" + 
				"        </Body>" + 
				"    </FIXML>"+ 
				"]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";

		InputStream is = new ByteArrayInputStream(soapMessage.getBytes());
		SOAPMessage requestSoap = MessageFactory.newInstance().createMessage(null, is);
//		MimeHeaders headers = requestSoap.getMimeHeaders();
		requestSoap.saveChanges();
		return requestSoap;
	}

}
