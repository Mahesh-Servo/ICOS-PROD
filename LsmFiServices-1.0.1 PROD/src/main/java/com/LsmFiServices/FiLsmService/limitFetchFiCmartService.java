package com.LsmFiServices.FiLsmService;

import java.io.IOException;
import java.text.ParseException;

import javax.xml.soap.SOAPException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.LsmFiServices.Utility.commonUtility;

@Service
public class limitFetchFiCmartService {
	
	private static final Logger logger = LoggerFactory.getLogger(limitFetchFiCmartService.class);
	
	public static String createSOAPRequest(String PINSTID, String LmtPrefix, String LmtSuffix)
			throws IOException, SOAPException, ParseException {
		
//		logger.info("limitFetchFiCmartService.createSOAPRequest().PINSTID ::"+ PINSTID +" , LmtPrefix():: " + LmtPrefix +" , LmtSuffix():: " + LmtSuffix);
//		DBlogs.log_sout("limitFetchFiCmartService.createSOAPRequest().LimitNodeId :: " , "PINSTID ::"+ PINSTID +" LmtPrefix():: " + LmtPrefix +" LmtSuffix():: " + LmtSuffix);
		
		String requestUuid = commonUtility.createRequestUUID();		
		String dateAndTime = commonUtility.dateFormat();
		
		String soapMessage = 
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<FIXML xsi:schemaLocation=\"http://www.finacle.com/fixml executeFinacleScript.xsd\" xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
		+"<Header>"
		+"<RequestHeader>"
		+"<MessageKey>"
		+"<RequestUUID>" + requestUuid + "</RequestUUID>"
		+"<ServiceRequestId>executeFinacleScript</ServiceRequestId>"
		+"<ServiceRequestVersion>10.2</ServiceRequestVersion>"
		+"<ChannelId>CLS</ChannelId>"
		+"<LanguageId>"
		+"</LanguageId>"
		+"</MessageKey>"
		+"<RequestMessageInfo>"
		+"<BankId>BM3</BankId>"
		+"<TimeZone>"
		+"</TimeZone>"
		+"<EntityId>"
		+"</EntityId>"
		+"<EntityType>"
		+"</EntityType>"
		+"<ArmCorrelationId>"
		+"</ArmCorrelationId>"
		+"<MessageDateTime>" + dateAndTime + "</MessageDateTime>"
		+"</RequestMessageInfo>"
		+"<Security>"
		+"<Token>"
		+"<PasswordToken>"
		+"<UserId>"
		+"</UserId>"
		+"<Password>"
		+"</Password>"
		+"</PasswordToken>"
		+"</Token>"
		+"<FICertToken>"
		+"</FICertToken>"
		+"<RealUserLoginSessionId>"
		+"</RealUserLoginSessionId>"
		+"<RealUser>"
		+"</RealUser>"
		+"<RealUserPwd>"
		+"</RealUserPwd>"
		+"<SSOTransferToken>"
		+"</SSOTransferToken>"
		+"</Security>"
		+"</RequestHeader>"
		+"</Header>"
		+"<Body>"
		+"<executeFinacleScriptRequest>"
		+"<ExecuteFinacleScriptInputVO>"
		+"<requestId>FI_CMART_LimitFetch.scr</requestId>"
		+"</ExecuteFinacleScriptInputVO>"
		+"<executeFinacleScript_CustomData>"
		+"<LmtPrefix>" + LmtPrefix + "</LmtPrefix>"
		+"<LmtSuffix>" + LmtSuffix +  "</LmtSuffix>"
		+"</executeFinacleScript_CustomData>"
		+"</executeFinacleScriptRequest>"
		+"</Body>"
		+"</FIXML>" +
		"]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
		
		return soapMessage;
	}

}
