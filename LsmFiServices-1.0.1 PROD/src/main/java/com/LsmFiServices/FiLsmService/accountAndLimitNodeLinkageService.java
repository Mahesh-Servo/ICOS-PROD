package com.LsmFiServices.FiLsmService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.commonUtility;

public class accountAndLimitNodeLinkageService {


	public static SOAPMessage createSOAPRequest(String PINSTID, Map<String, String> GetEXTData, int i)//added by Bharat, Hemant
			throws IOException, SOAPException, ParseException {
		
		String requestUuid = commonUtility.createRequestUUID();
		String dateAndTime = commonUtility.dateFormat();	
		
		String drawingInd = "E";
		if(GetEXTData.get("FACILITY_NAME").equalsIgnoreCase("Cash Credit")) {
			drawingInd ="M"; 
		} else if (GetEXTData.get("FACILITY_NAME").contains("Drop Line Overdraft")) {  // ICO - 10080
			drawingInd = OperationUtillity.getChildLimitAction(PINSTID,GetEXTData.get("FACILITY_NAME"));
		}
	
		String soapMessage = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " + 
				"xmlns:web=\"http://webservice.fiusb.ci.infosys.com\">" + 
				"<soapenv:Header/>" + 
				"<soapenv:Body>" + 
				"<web:executeService>" + 
				"<arg_0_0>" + 
				"<![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
				"<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"+ 
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
				"                <TimeZone></TimeZone>" + 
				"                <EntityId></EntityId>" + 
				"                <EntityType></EntityType>" + 
				"                <ArmCorrelationId></ArmCorrelationId>" + 
				"                <MessageDateTime>"+dateAndTime+"</MessageDateTime>" + 
				"            </RequestMessageInfo>" + 
				"			 <Security>" + 
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
				"                <SSOTransferToken></SSOTransferToken>" + 
				"            </Security>" + 
				"        </RequestHeader>" + 
				"    </Header>" + 
				"<Body>" + 
				"        <executeFinacleScriptRequest>" + 
				"            <ExecuteFinacleScriptInputVO>" + 
				"                <requestId>FI_CMART_SanctLim_DrawngPower.scr</requestId>" + 
				"            </ExecuteFinacleScriptInputVO>" + 
				"            <executeFinacleScript_CustomData>" + 
				"                <applDate>"+commonUtility.customDateFormat()+"</applDate>  " + 		//sys date in dd-MM-yyyy
				"                <indCat>D</indCat>" + 
				"                <limitPrefix>"+GetEXTData.get("LIMIT_PREFIX")+"</limitPrefix>   " + 
				"                <limitSuffix>"+GetEXTData.get("LIMIT_SUFFIX")+"</limitSuffix> " + 
				"                <drwngPowerpn>0</drwngPowerpn> " + 
				"                <dpInd>"+drawingInd+"</dpInd> " + 
				"                <acctRecalled>N</acctRecalled>  " + 
				"                <notes>" +  PINSTID+ "</notes>   " +  
				"                <accId>"+GetEXTData.get("ACCOUNT_NO_"+i)+"</accId> " + 
				"            </executeFinacleScript_CustomData>" + 
				"        </executeFinacleScriptRequest>" + 
				"    </Body>" + 
				"</FIXML>"
				+ "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope> ";
		

		InputStream is = new ByteArrayInputStream(soapMessage.getBytes());
		SOAPMessage requestSoap = MessageFactory.newInstance().createMessage(null, is);
		requestSoap.saveChanges();
		return requestSoap;
	}

}
