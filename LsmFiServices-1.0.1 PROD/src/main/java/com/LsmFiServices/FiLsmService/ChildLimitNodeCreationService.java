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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.LsmFiServices.Utility.ChildLimitNodeCreationUtility;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.commonUtility;

public class ChildLimitNodeCreationService {
	
	private static final Logger logger = LoggerFactory.getLogger(ChildLimitNodeCreationService.class);
	
	public static SOAPMessage createSOAPRequest(String PINSTID, Map<String, String> GetEXTData)
			throws IOException, SOAPException, ParseException {

	String sanctLvl = OperationUtillity.getSanctLvl(PINSTID);
	String sysTime = commonUtility.sysTimeWithT();
	String facExpDate = commonUtility.rotateDate(GetEXTData.get("FACILITY_EXP_DATE"));
	String soapMessage = "";
	String isRevocable = "Y";
	String limitAmount = GetEXTData.get("AMOUNT_LIMIT");
	if ("ESM".equalsIgnoreCase(GetEXTData.get("MEMO_TYPE"))) {
	    limitAmount = "1";
	}
	String singleTranFlg = "N";
	Boolean otlSingleTransFlag = ChildLimitNodeCreationUtility.otlSingleTransFlag(GetEXTData.get("FACILITY_NAME"));
	if (otlSingleTransFlag) {
	    singleTranFlg = "Y";
	}

	String parentLimitSuffix = GetEXTData.get("PARENT_LIMIT_SUFFIX");
	String parentLimitPrefix = GetEXTData.get("PARENT_LIMIT_PREFIX");
	if (GetEXTData.get("FACILITY_NAME").contains("CROSS CALL")) {
	    parentLimitPrefix = GetEXTData.get("MAIN_LIMIT_PREFIX_FOR_CROSS_CALL");
	    parentLimitSuffix = GetEXTData.get("MAIN_LIMIT_SUFFIX_FOR_CROSS_CALL");
	}
	
	if ("No".equalsIgnoreCase(GetEXTData.get("IS_REVOCABLE_CLAUSE"))) {      //10064   
	    logger.info("ChildLimitNodeCreationService.WHEN No ->"+GetEXTData.get("IS_REVOCABLE_CLAUSE"));
		isRevocable = "N";
	} else 	if ("Yes".equalsIgnoreCase(GetEXTData.get("IS_REVOCABLE_CLAUSE"))) {
	    logger.info("ChildLimitNodeCreationService.WHEN Yes ->"+GetEXTData.get("IS_REVOCABLE_CLAUSE"));
		isRevocable = "Y";
	}
	
	logger.info("ChildLimitNodeCreationService.isRevocable ->"+isRevocable);
	
		if(GetEXTData.get("LIEN_APPLICABILITY") != null && GetEXTData.get("LIEN_APPLICABILITY").equalsIgnoreCase("Yes")) {
		
			soapMessage = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "		 <Header>" + 
				"        <RequestHeader>" + 
				"            <MessageKey>" + 
				"                <RequestUUID>" + commonUtility.createRequestUUID() + "</RequestUUID>" + 
				"                <ServiceRequestId>addLimitNode</ServiceRequestId>" + 
				"                <ServiceRequestVersion>10.2</ServiceRequestVersion>" + 
				"                <ChannelId>CLS</ChannelId>" + 	//changed on 26.10.2023
				"                <LanguageId/>" + 
				"            </MessageKey>" + 
				"            <RequestMessageInfo>" + 
				"                <BankId>BM3</BankId>" + 
				"                <TimeZone></TimeZone>" + 
				"                <EntityId></EntityId>" + 
				"                <EntityType></EntityType>" + 
				"                <ArmCorrelationId></ArmCorrelationId>" + 
				"                <MessageDateTime>" + LocalDateTime.now()+ "</MessageDateTime>" + 
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
				"                <SSOTransferToken></SSOTransferToken>" + 
				"            </Security>" + 
				"        </RequestHeader>" + 
				"    </Header>" + 
				"    <Body>" + 
				"        <addLimitNodeRequest>" + 
				"            <AddLimitNodeInputVO>" + 
				"                <crncy>"+OperationUtillity.NullReplace(GetEXTData.get("CURRENCY_TWO"))+"</crncy>" + 	//INR modified to screen currency on 23.06.2023  
				"                <custId>" + 
				"                    <cifId>"+ OperationUtillity.NullReplace(GetEXTData.get("CUST_ID")) + "</cifId>" +
				"                </custId>" + 
				"                <drwngPower>" + 
				"                    <amountValue>"+ limitAmount + "</amountValue>" +   // TOTAL_FB_NONFB
				"                    <currencyCode>"+OperationUtillity.NullReplace(GetEXTData.get("CURRENCY_TWO"))+"</currencyCode>" + 
				"                </drwngPower>" + 
				"                <drwngPowerInd>E</drwngPowerInd>" +
				"                <freeText>" +  PINSTID+ "</freeText>" + 
				"                <limitDesc>" + OperationUtillity.NullReplace(GetEXTData.get("SECURITY_PROVIDER"))+"</limitDesc>" + 
				"                <limitExpiryDate>" + OperationUtillity.NullReplace(GetEXTData.get("LIMIT_EXP_DATE")) + sysTime +"</limitExpiryDate>" + 
				"                <limitPrefix>" + GetEXTData.get("LIMIT_PREFIX")+ "</limitPrefix>" + 
				"                <limitSanctDate>" + GetEXTData.get("SANCTION_DATE")+sysTime+ "</limitSanctDate>" + 
				"                <limitSuffix>" + GetEXTData.get("LIMIT_SUFFIX")+ "</limitSuffix>" + 
				"                <limitType>C</limitType>" + 
				"                <ouserMaintLiabLL>" + 
				"	 			 <umlDate>" + LocalDateTime.now()+ "</umlDate>"  +
				"                <umlDept>CMOG</umlDept>" +
				"                <umlLiabValue>" + 
				"                <amountValue>" + OperationUtillity.NullReplace(GetEXTData.get("LIEN_AMOUNT"))+ "</amountValue>"+ // ------> Lein Amount only level 2 -- Parent Null" + 
				"                <currencyCode>"+OperationUtillity.NullReplace(GetEXTData.get("CURRENCY_TWO"))+"</currencyCode>" + //INR modified to screen currency on 23.06.2023
				"                </umlLiabValue>" + 
				"                <umlReasonCode>UML</umlReasonCode>"+ // -----> Hardcode" + 
				"                <umlRemarks>" + OperationUtillity.NullReplace(GetEXTData.get("LIEN_IN_FAVOUR_OF"))+":"+PINSTID+ "</umlRemarks>" +			// added on 28th june
				"                </ouserMaintLiabLL>" + 
				"				<parentLimitPrefix>"+  parentLimitPrefix+ "</parentLimitPrefix>" + 
				"				<parentLimitSuffix>"+parentLimitSuffix+ "</parentLimitSuffix>" + 
				"                <condPrecedentFlg>N</condPrecedentFlg>" + 
				"                <sanctAuthCode>" + OperationUtillity.NullReplace(GetEXTData.get("SANCTION_FORUM"))+ "</sanctAuthCode>" + 
				"                <sanctLevelCode>"+sanctLvl+"</sanctLevelCode>" + 
				"                <sanctLimit>" + 
				"                <amountValue>" +limitAmount + "</amountValue>" + 
				"                <currencyCode>"+OperationUtillity.NullReplace(GetEXTData.get("CURRENCY_TWO"))+"</currencyCode>" + 		//INR modified to screen currency on 23.06.2023
				"                </sanctLimit>" + 
				"                <singleTranFlg>"+ singleTranFlg +"</singleTranFlg>" + 
				"                <baselDtls>" + 
				"                <genDtls>" + 
				"                <isRevocable>"+isRevocable+"</isRevocable>" +    //dynamic added - 03--2-2025 //10064
				"                <limitClassifier>B</limitClassifier>" + 
				"                </genDtls>" + 
				"                </baselDtls>" + 
				"                </AddLimitNodeInputVO>" + 
				"                <addLimitNode_CustomData>" + 
				"                <GlobalLimitFlg>N</GlobalLimitFlg>" + 
				"                <LimitMasterCode>MIGMLM</LimitMasterCode>" + 
				"                <bussModel>HELD</bussModel>" +
				"                <sppiCrit>Y</sppiCrit>" + 
				"                <dirCost>N</dirCost>" + 
				"                <leiNumber></leiNumber>" + 
				"                <leiExpiryDate></leiExpiryDate>" + 
				"                <facAggAmt>" + OperationUtillity.NullReplace(GetEXTData.get("DOCUMENT_AMOUNT")) + "</facAggAmt>" + 
				"                <facExpDate>" + facExpDate + "</facExpDate>" + 
				"                <facSanctDate>" + commonUtility.rotateDate(GetEXTData.get("FA_EXECUTION_DATE")) + "</facSanctDate>" + 
				"                <riskId></riskId>" + 
				"                <productCode>"+OperationUtillity.NullReplace( GetEXTData.get("PROGRAM_CODE") )+ "</productCode>" + 
				"                <loanStat>" + OperationUtillity.NullReplace(GetEXTData.get("LOAN_STATUS") )+ "</loanStat>" + 
				"                <buyTakeDate></buyTakeDate>" + 
				"                <nameBank>" + commonUtility.replaceAnd(commonUtility.removeSpecialCharacters(GetEXTData.get("NAME_OF_BANK_ONE"))) + "</nameBank>" + 
				"                <sancValDt>" + commonUtility.rotateDate(GetEXTData.get("SANCTION_VAL_DATE")) + "</sancValDt>" + 
				"            </addLimitNode_CustomData>" + 
				"        </addLimitNodeRequest>" + 
				"    </Body>"
				+ "</FIXML>"
				+ "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
		} else {
			soapMessage = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
				"<FIXML xsi:schemaLocation=\"http://www.finacle.com/fixml executeFinacleScript.xsd\"  xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +			
				"<Header>" + 				
				"<RequestHeader>" + 				
				"<MessageKey>" + 				
				"<RequestUUID>"+ commonUtility.createRequestUUID()  +"</RequestUUID>" + 				
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
				"<MessageDateTime>"+ LocalDateTime.now() +"</MessageDateTime>" + 				
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
				" " + 				
				"<Body>" + 				
				"<addLimitNodeRequest>" + 				
				"<AddLimitNodeInputVO>" + 				
				"<crncy>"+OperationUtillity.NullReplace(GetEXTData.get("CURRENCY_TWO"))+"</crncy>" +
				"<custId>" + 				
				"<cifId>"+ OperationUtillity.NullReplace(GetEXTData.get("CUST_ID")) +"</cifId>" + 				
				"</custId>" + 				
				"<drwngPower>" + 				
				"<amountValue>"+ limitAmount +"</amountValue>" +
				"<currencyCode>"+OperationUtillity.NullReplace(GetEXTData.get("CURRENCY_TWO"))+"</currencyCode>" + 				
				"</drwngPower>" + 				
				"<drwngPowerInd>E</drwngPowerInd>" +
				"<freeText>" +  PINSTID+ "</freeText>" + 
				"<limitDesc>" + OperationUtillity.NullReplace(GetEXTData.get("SECURITY_PROVIDER"))+"</limitDesc>" + 			
				"<limitExpiryDate>" + OperationUtillity.NullReplace(GetEXTData.get("LIMIT_EXP_DATE")) + sysTime +"</limitExpiryDate>" + 				
				"<limitPrefix>" + OperationUtillity.NullReplace(GetEXTData.get("LIMIT_PREFIX")) +"</limitPrefix>" + 
				"<limitSanctDate>"+ GetEXTData.get("SANCTION_DATE")+sysTime+"</limitSanctDate>" + 				//to be enable on production
				"<limitSuffix>" + OperationUtillity.NullReplace(GetEXTData.get("LIMIT_SUFFIX")) +"</limitSuffix>" + 				
				"<limitType>C</limitType>" + 				
				"<parentLimitPrefix>"+ parentLimitPrefix+ "</parentLimitPrefix>" + 				
				"<parentLimitSuffix>"+ parentLimitSuffix + "</parentLimitSuffix>" + 				
				"<condPrecedentFlg>N</condPrecedentFlg>" + 				
				"<sanctAuthCode>" +OperationUtillity.NullReplace( GetEXTData.get("SANCTION_FORUM") )+"</sanctAuthCode>" + 				
				"<sanctLevelCode>"+sanctLvl+"</sanctLevelCode>" + 				
				"<sanctLimit>" + 				
				"<amountValue>" + OperationUtillity.NullReplace(GetEXTData.get("AMOUNT_LIMIT")) +"</amountValue>" + 				
				"<currencyCode>"+OperationUtillity.NullReplace(GetEXTData.get("CURRENCY_TWO"))+"</currencyCode>" + 		
				"</sanctLimit>" + 				
				"<singleTranFlg>"+ singleTranFlg +"</singleTranFlg>" + 				
				"<baselDtls>" + 				
				"<genDtls>" + 				
				"<isRevocable>"+ isRevocable +"</isRevocable>" + 	//10064  			
				"<limitClassifier>B</limitClassifier>" + 				
				"</genDtls>" + 				
				"</baselDtls>" + 				
				"</AddLimitNodeInputVO>" + 				
				"<addLimitNode_CustomData>" + 				
				"<GlobalLimitFlg>N</GlobalLimitFlg>" + 				
				"<LimitMasterCode>MIGMLM</LimitMasterCode>" + 				
				"<bussModel>HELD</bussModel>" +
				"<sppiCrit>Y</sppiCrit>" + 
				"<dirCost>N</dirCost>" + 				
				"<leiNumber></leiNumber>" + 				
				"<leiExpiryDate></leiExpiryDate>" + 				
				"<facAggAmt>" + GetEXTData.get("DOCUMENT_AMOUNT") + "</facAggAmt>" + 				
			        "<facExpDate>" + facExpDate + "</facExpDate>" + 
				"<facSanctDate>" +  commonUtility.rotateDate(GetEXTData.get("FA_EXECUTION_DATE")) + "</facSanctDate>" + 				
				"<riskId>NA</riskId>" + 				
				"<productCode>"+ OperationUtillity.NullReplace(GetEXTData.get("PROGRAM_CODE")) + "</productCode>" + 				
				"<loanStat>" + OperationUtillity.NullReplace(GetEXTData.get("LOAN_STATUS")) + "</loanStat>" + 				
				"<buyTakeDate></buyTakeDate>" +
				"<nameBank>" + commonUtility.replaceAnd(commonUtility.removeSpecialCharacters(OperationUtillity.NullReplace(GetEXTData.get("NAME_OF_FIRST_BANK")))) + "</nameBank>" + 				
				"<sancValDt>" + commonUtility.rotateDate(GetEXTData.get("SANCTION_VAL_DATE")) + "</sancValDt>" + 				
				"</addLimitNode_CustomData>" + 
				"</addLimitNodeRequest>" + 
				"</Body>" + 				
				"</FIXML>"	+			
				"]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
		}
		
		InputStream is = new ByteArrayInputStream(soapMessage.getBytes());
		SOAPMessage requestSoap = MessageFactory.newInstance().createMessage(null, is);
		requestSoap.saveChanges();
		return requestSoap;
	}
}
