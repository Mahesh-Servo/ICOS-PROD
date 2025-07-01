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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.commonUtility;

public class parentLimitNodeCreationService {
	
	private static final Logger logger = LoggerFactory.getLogger(parentLimitNodeCreationService.class);
	
	public static SOAPMessage createSOAPRequest(String PINSTID, Map<String, String> GetEXTData)
			throws IOException, SOAPException, ParseException {

	String sysTime = commonUtility.sysTimeWithT();
	String isRevocable = "";
	String currency = GetEXTData.get("CURRENCY_TWO");
	String TOTAL_FB_NONFB = "";
	String sanctLvl = OperationUtillity.getSanctLvl(PINSTID);

	if (!"".equals(GetEXTData.get("PARENT_CURRENCY")) && !GetEXTData.get("PARENT_CURRENCY").equals(null)) {
	    currency = GetEXTData.get("PARENT_CURRENCY");
	}
	if ("ESM".equalsIgnoreCase(GetEXTData.get("MEMO_TYPE"))) {
	    TOTAL_FB_NONFB = "1";
	} else {
	    TOTAL_FB_NONFB = GetEXTData.get("TOTAL_FB_NONFB");
	}
	
	if ("No".equalsIgnoreCase(GetEXTData.get("ISREVOCABLE"))) {
		logger.info("parentLimitNodeCreationService.WHEN No() ----------> " + GetEXTData.get("ISREVOCABLE"));
		isRevocable = "N";
	} else 	if ("Yes".equalsIgnoreCase(GetEXTData.get("ISREVOCABLE"))) {
		logger.info("parentLimitNodeCreationService.WHEN Yes() ----------> " + GetEXTData.get("ISREVOCABLE"));
		isRevocable = "Y";
	}
	
	logger.info("parentLimitNodeCreationService.isRevocable----------> " + isRevocable);
	
		String soapMessage = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
				"<FIXML xsi:schemaLocation=\"http://www.finacle.com/fixml executeFinacleScript.xsd\"  xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +			
				"<Header>" + 				
				"<RequestHeader>" + 				
				"<MessageKey>" + 				
				"<RequestUUID>"+commonUtility.createRequestUUID()  +"</RequestUUID>" + 				
				"<ServiceRequestId>addLimitNode</ServiceRequestId>" + 				
				"<ServiceRequestVersion>10.2</ServiceRequestVersion>" + 				
				"<ChannelId>CLS</ChannelId>" + 				
				"<LanguageId></LanguageId>" + 				
				"</MessageKey>" + 				
				"<RequestMessageInfo>" + 				
				"<BankId>BM3</BankId>" + 				
				"<TimeZone></TimeZone>" + 				
				"<EntityId></EntityId>" + 				
				"<EntityType></EntityType>" + 				
				"<ArmCorrelationId></ArmCorrelationId>" + 				
				"<MessageDateTime>"+LocalDateTime.now()+"</MessageDateTime>" + 				
				"</RequestMessageInfo>" + 				
				"<Security>" + 				
				"<Token>" + 				
				"<PasswordToken>" + 				
				"<UserId></UserId>" + 				
				"<Password></Password>" + 				
				"</PasswordToken>" + 				
				"</Token>" + 				
				"<FICertToken></FICertToken>" + 				
				"<RealUserLoginSessionId></RealUserLoginSessionId>" + 				
				"<RealUser></RealUser>" + 				
				"<RealUserPwd></RealUserPwd>" + 				
				"<SSOTransferToken></SSOTransferToken>" + 				
				"</Security>" + 				
				"</RequestHeader>" + 				
				"</Header>" + 				
				"<Body>" + 				
				"<addLimitNodeRequest>" + 				
				"<AddLimitNodeInputVO>" + 				
				"<crncy>"+currency+"</crncy>" + 	
				"<custId>" + 				
				"<cifId>"+OperationUtillity.NullReplace(GetEXTData.get("cifId"))+"</cifId>" + 				
				"</custId>" + 				
				"<drwngPower>" + 	
				"<amountValue>"+ TOTAL_FB_NONFB +"</amountValue>" + 	
				"<currencyCode>"+currency+"</currencyCode>" + 			
				"</drwngPower>" + 				
				"<drwngPowerInd>E</drwngPowerInd>" + 				
				"<freeText>" +  PINSTID+ "</freeText>" + 
				"<limitDesc>" + commonUtility.replaceAnd(commonUtility.removeSpecialCharacters(GetEXTData.get("PARENT_LIMIT_DESC")))+"</limitDesc>" + 	
				"<limitExpiryDate>" + GetEXTData.get("PARENT_LIMIT_EXP_DATE") + sysTime +"</limitExpiryDate>" + 				
				"<limitPrefix>" + GetEXTData.get("PARENT_LIMIT_PREFIX") +"</limitPrefix>" + 
				"<limitSanctDate>" + GetEXTData.get("PARENT_LIMIT_SANCTION_DATE")+ sysTime +"</limitSanctDate>" +  //for prod
				"<limitSuffix>" + GetEXTData.get("PARENT_LIMIT_SUFFIX") +"</limitSuffix>" + 				
				"<limitType>C</limitType>" + 				
				"<parentLimitPrefix></parentLimitPrefix>" + 				
				"<parentLimitSuffix></parentLimitSuffix>" + 				
				"<condPrecedentFlg>N</condPrecedentFlg>" + 				
				"<sanctAuthCode>" + GetEXTData.get("SANCTION_FORUM") +"</sanctAuthCode>" + 				
				"<sanctLevelCode>"+sanctLvl+"</sanctLevelCode>" + 				
				"<sanctLimit>" + 				
				"<amountValue>" + TOTAL_FB_NONFB +"</amountValue>" + 			
				"<currencyCode>"+currency+"</currencyCode>" + 				
				"</sanctLimit>" + 				
				"<singleTranFlg>N</singleTranFlg>" + 				
				"<baselDtls>" + 				
				"<genDtls>" + 				
				"<isRevocable>"+ isRevocable +"</isRevocable>" + 				
				"<limitClassifier>B</limitClassifier>" + 				
				"</genDtls>" + 				
				"</baselDtls>" + 				
				"</AddLimitNodeInputVO>" + 				
				"<addLimitNode_CustomData>" + 				
				"<GlobalLimitFlg>N</GlobalLimitFlg>" + 				
				"<LimitMasterCode>MIGMLM</LimitMasterCode>" + 				
				"<bussModel></bussModel>" + 				
				"<sppiCrit></sppiCrit>" + 				
				"<dirCost>N</dirCost>" + 				
				"<leiNumber></leiNumber>" + 				
				"<leiExpiryDate></leiExpiryDate>" + 				
				"<facAggAmt>" + GetEXTData.get("DOCUMNET_AMOUNT") + "</facAggAmt>" + 				
				"<facExpDate>" +  commonUtility.rotateDate(GetEXTData.get("FACILITY_EXP_DATE")) + "</facExpDate>" + 				
				"<facSanctDate>" +  commonUtility.rotateDate(GetEXTData.get("FA_EXECUTION_DATE")) + "</facSanctDate>" + 				
				"<riskId>NA</riskId>" + 				
				"<productCode>"+ GetEXTData.get("PROGRAM_CODE") + "</productCode>" +				
				"<loanStat>" + GetEXTData.get("LOAN_STATUS") + "</loanStat>" + 				
				"<buyTakeDate>"+commonUtility.customDateFormat()+"</buyTakeDate>" + 
				"<nameBank>" + commonUtility.replaceAnd(commonUtility.removeSpecialCharacters(GetEXTData.get("NAME_OF_FIRST_BANK"))) + "</nameBank>" + 				
				"<sancValDt>" + commonUtility.rotateDate(GetEXTData.get("SANCTION_VAL_DATE")) + "</sancValDt>" + 				
				"</addLimitNode_CustomData>" + 
				"</addLimitNodeRequest>" + 
				"</Body>" + 				
				"</FIXML>"	+			
				"]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
				
		InputStream is = new ByteArrayInputStream(soapMessage.getBytes());
		SOAPMessage requestSoap = MessageFactory.newInstance().createMessage(null, is);
		MimeHeaders headers = requestSoap.getMimeHeaders();
		requestSoap.saveChanges();
		
		return requestSoap;
	}
	
}
