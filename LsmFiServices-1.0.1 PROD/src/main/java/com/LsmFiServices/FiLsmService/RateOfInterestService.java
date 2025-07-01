package com.LsmFiServices.FiLsmService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import com.LsmFiServices.Utility.commonUtility;

public class RateOfInterestService {

	public static SOAPMessage createSOAPRequest(String PINSTID, Map<String, String> rOIData, int i)
			throws IOException, SOAPException, ParseException {


		String soapMessage = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
				+ "xmlns:web=\"http://webservice.fiusb.ci.infosys.com\">" 
				+ "<soapenv:Header/>" 
				+ "<soapenv:Body>"
				+ "<web:executeService>" 
				+ "<arg_0_0>" 
				+ "<![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "    <Header>"
				+ "        <RequestHeader>" 
				+ "            <MessageKey>"
				+ "                <RequestUUID>" + commonUtility.createRequestUUID() + "</RequestUUID>"
				+ "                <ServiceRequestId>executeFinacleScript</ServiceRequestId>"
				+ "                <ServiceRequestVersion>10.2</ServiceRequestVersion>"
				+ "                <ChannelId>CLS</ChannelId>" 
				+ "                <LanguageId></LanguageId>"
				+ "            </MessageKey>" 
				+ "            <RequestMessageInfo>"
				+ "                <BankId>BM3</BankId>" 
				+ "                <TimeZone></TimeZone>"
				+ "                <EntityId></EntityId>" 
				+ "                <EntityType></EntityType>"
				+ "                <ArmCorrelationId></ArmCorrelationId>" 
				+ "                <MessageDateTime>"+ LocalDateTime.now() + "</MessageDateTime>" 
				+ "            </RequestMessageInfo>" 
				+ "            <Security>"
				+ "                <Token>" 
				+ "                    <PasswordToken>"
				+ "                        <UserId></UserId>" 
				+ "                        <Password></Password>"
				+ "                    </PasswordToken>" 
				+ "                </Token>"
				+ "                <FICertToken></FICertToken>"
				+ "                <RealUserLoginSessionId></RealUserLoginSessionId>"
				+ "                <RealUser></RealUser>" 
				+ "                <RealUserPwd></RealUserPwd>"
				+ "                <SSOTransferToken></SSOTransferToken>" 
				+ "            </Security>"
				+ "        </RequestHeader>" 
				+ "    </Header>" 
				+ "    <Body>" 
				+ "        <executeFinacleScriptRequest>"
				+ "            <ExecuteFinacleScriptInputVO>"
				+ "                <requestId>FI_CMART_IntTblCode_Upd.scr</requestId>"
				+ "            </ExecuteFinacleScriptInputVO>" 
				+ "            <executeFinacleScript_CustomData>"
				+ "                <Account_Number>" + rOIData.get("ACCOUNT_NO_" + i) + "</Account_Number> " 
				+"                <Channel_Id>CMART Int Version Change</Channel_Id>" 
				+ "                <IntTblCode>"+ rOIData.get("RATE_CODE") + "</IntTblCode>" 
				+ "                <Spread>" + rOIData.get("ROI_SPREAD")+ "</Spread>" 
				+ "                <PeggedFlag>" + rOIData.get("ROI_PEGGED_FLAG") + "</PeggedFlag>" 			
				+"                <PeggedFreqInMnths>" + rOIData.get("PEGGING_FREQUENCY") + " </PeggedFreqInMnths>"
				+ "                <NumOfDays>" + rOIData.get("NUMBER_OF_DAYS") + "</NumOfDays> "
				+ "                <StartDate>" + LocalDateTime.now() + "</StartDate>  " 
				+ "                <EndDate>"+ rOIData.get("ROI_END_DATE") + "</EndDate>"
				+ "                <PeggedFreqInDays>0</PeggedFreqInDays>  "  // "+ rOIData.get("PEGGING_FREQUENCY") +"// modified to zero on 16.06.2023										
				+"                <Minimum_IntPctDr></Minimum_IntPctDr>"
				+ "                <Maximum_IntPctDr></Maximum_IntPctDr>"
				+ "            </executeFinacleScript_CustomData>" 
				+ "        </executeFinacleScriptRequest>"
				+ "    </Body>" 
				+ "</FIXML>" 
				+ "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";

		InputStream is = new ByteArrayInputStream(soapMessage.getBytes());
		SOAPMessage requestSoap = MessageFactory.newInstance().createMessage(null, is);
		MimeHeaders headers = requestSoap.getMimeHeaders();
		requestSoap.saveChanges();
		return requestSoap;
	}

}
