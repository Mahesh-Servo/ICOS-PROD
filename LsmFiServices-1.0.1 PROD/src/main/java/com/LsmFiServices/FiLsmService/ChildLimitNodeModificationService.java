package com.LsmFiServices.FiLsmService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LsmFiServices.Utility.ChildLimitNodeCreationUtility;
import com.LsmFiServices.Utility.Constants;
import com.LsmFiServices.Utility.ESMUtils;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.ParentLimitNodeModificationUtility;
import com.LsmFiServices.Utility.ServiceDetails;
import com.LsmFiServices.Utility.commonUtility;
import com.LsmFiServices.Utility.xmlToMap;
import com.LsmFiServices.pojo.serviceexecutiondetails.ServiceDetailsUpdatePojo;

@SuppressWarnings("deprecation")
@Service
public class ChildLimitNodeModificationService {

    private final static Logger logger = LoggerFactory.getLogger(ChildLimitNodeModificationService.class);

    @Autowired
    private ServiceDetails serviceDetails;
    
    @Autowired
	private ESMUtils esmUtils;
	
	@Autowired
	private ParentLimitNodeModificationUtility parentModUtility;

    public Map<String, String> childLimitNodeModificationService(String PINSTID, Map<String, String> childLimitNodeMap)
	    throws SQLException {

	logger.info("ChildLimitNodeModificationService.childLimitNodeModificationService()->" + PINSTID
		+ " and map data::" + childLimitNodeMap);
	Map<String, String> API_REQ_RES_map = new HashMap<>();
	String childLimitNodeModificationResponse = "";
	SOAPMessage soapRequest = null;
	String RequestUUID = "";
	String HostTransaction = "";
	String Status = "";
	String requestType = "";
	ServiceDetailsUpdatePojo pojo = new ServiceDetailsUpdatePojo();
	try {
			pojo.setPinstId(PINSTID);		
			pojo.setServiceName("CHILD LIMIT NODE MODIFICATION");
			pojo.setFacility(childLimitNodeMap.get("FACILITY_NAME"));
			requestType = "CHILD LIMIT NODE MODIFICATION : " + childLimitNodeMap.get("FACILITY_NAME");
			pojo.setRequestType(requestType);
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			soapRequest = createSOAPRequest(PINSTID, childLimitNodeMap);
			pojo.setServiceRequest(OperationUtillity.soapMessageToString(soapRequest));
			pojo.setStatus("Request Sent...!");
			pojo.setReTrigger(true);
			serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			SOAPMessage soapResponse = soapConnection.call(soapRequest, Constants.FIWebService);
			childLimitNodeModificationResponse = OperationUtillity.soapMessageToString(soapResponse);
			childLimitNodeModificationResponse = StringEscapeUtils.unescapeXml(childLimitNodeModificationResponse);
			if (childLimitNodeModificationResponse.contains("<HostTransaction>")) {
				HostTransaction = childLimitNodeModificationResponse.substring(childLimitNodeModificationResponse.indexOf("<HostTransaction>") + "<HostTransaction>".length(),childLimitNodeModificationResponse.indexOf("</HostTransaction>"));
				if (HostTransaction.contains("<Status>")) {
					Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),HostTransaction.indexOf("</Status>"));
				}
			}
			if (Status.equalsIgnoreCase("SUCCESS")) {
				API_REQ_RES_map = xmlToMap.successPacketDataToMapChildNodeCreation(PINSTID, childLimitNodeModificationResponse);
				pojo.setStatus(API_REQ_RES_map.get("Status"));
				pojo.setServiceResponse(childLimitNodeModificationResponse);
				pojo.setRequestType(requestType);
				pojo.setReTrigger(false);
				serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			} else {
				API_REQ_RES_map = xmlToMap.packetDataToMap(PINSTID, childLimitNodeModificationResponse);
				pojo.setStatus(API_REQ_RES_map.get("Status"));
				pojo.setRequestType(requestType);
				pojo.setMessage(API_REQ_RES_map.get("ErrorDesc"));
				pojo.setServiceResponse(childLimitNodeModificationResponse);
				pojo.setReTrigger(true);
				serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			}
			OperationUtillity.API_RequestResponse_Insert(OperationUtillity.soapMessageToString(soapRequest),childLimitNodeModificationResponse,requestType, 
					PINSTID,	API_REQ_RES_map, RequestUUID);
		} catch (Exception e) {

			API_REQ_RES_map.put("RequestUUID", "");
			API_REQ_RES_map.put("MessageDateTime", "");
			API_REQ_RES_map.put("Error_Code", "500");
			API_REQ_RES_map.put("message", e.getMessage());
			API_REQ_RES_map.put("Status", "FAILED");
			API_REQ_RES_map.put("Error_At", "Child Limit Node Modification");
			API_REQ_RES_map.put("request",OperationUtillity.soapMessageToString(soapRequest));
			API_REQ_RES_map.put("response", childLimitNodeModificationResponse);
			logger.info("ChildLimitNodeModificationService.childLimitNodeModificationService()->'"+PINSTID+"'\n"+OperationUtillity.traceException(e));
		}
		return API_REQ_RES_map;
	}

    public SOAPMessage createSOAPRequest(String pinstid, Map<String, String> childLimitNodeModificationInputMap)
	    throws IOException, SOAPException, ParseException {

	String sysTime = commonUtility.sysTimeWithT();
	String sanctLevelCode = OperationUtillity.getSanctLvl(pinstid);
	String singleTranFlg = "N";
	String isRevocable  = "Y";
	
	if ("No".equalsIgnoreCase(childLimitNodeModificationInputMap.get("IS_REVOCABLE_CLAUSE"))) {    //JIRA-10064
		logger.info("ChildLimitNodeModificationService.WHEN No-->" + childLimitNodeModificationInputMap.get("IS_REVOCABLE_CLAUSE"));
		isRevocable = "N";
	} else 	if ("Yes".equalsIgnoreCase(childLimitNodeModificationInputMap.get("IS_REVOCABLE_CLAUSE"))) {
		logger.info("ChildLimitNodeModificationService.When Yes>" + childLimitNodeModificationInputMap.get("IS_REVOCABLE_CLAUSE"));
		isRevocable = "Y";
	}
	
	logger.info("ChildLimitNodeModificationService.isRevocable>" + isRevocable);
	
	Boolean otlSingleTransFlag = ChildLimitNodeCreationUtility.otlSingleTransFlag(childLimitNodeModificationInputMap.get("FACILITY_NAME"));
	if (otlSingleTransFlag) {
	    singleTranFlg = "Y";
	}
	
	String amountValue = Optional.ofNullable(childLimitNodeModificationInputMap.get("AMOUNT_LIMIT")).orElse("0");
	String lienTags="";
	if(childLimitNodeModificationInputMap.get("LIEN_APPLICABILITY") != null && childLimitNodeModificationInputMap.get("LIEN_APPLICABILITY").equalsIgnoreCase("Yes")) {
	    lienTags ="<userMaintLiabModLL>" + 
	    	"                    <umlDate>"+LocalDateTime.now()+"</umlDate>" + 
	    	"                    <umlDept>CMOG</umlDept>" + 
	    	"                    <umlLiabValue>" + 
	    	"                        <amountValue>"+childLimitNodeModificationInputMap.get("LIEN_AMOUNT")+"</amountValue>" + 
	    	"                        <currencyCode>"+childLimitNodeModificationInputMap.get("CURRENCY_TWO")+"</currencyCode>" + 
	    	"                    </umlLiabValue>" + 
	    	"                    <umlReasonCode>UML</umlReasonCode>" + 
	    	"                    <umlRemarks>"+childLimitNodeModificationInputMap.get("LIEN_IN_FAVOUR_OF")+":"+pinstid+"</umlRemarks>" + 
	    	"                </userMaintLiabModLL>";
	}

		String soapMessage = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
				"<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + 
		"    <Header>" + 
		"        <RequestHeader>" + 
		"            <MessageKey>" + 
		"                <RequestUUID>"+commonUtility.createRequestUUID()+"</RequestUUID>" + 
		"                <ServiceRequestId>modifyLimitNode</ServiceRequestId>" + 
		"                <ServiceRequestVersion>10.2</ServiceRequestVersion>" + 
		"                <ChannelId>CLS</ChannelId>" + 
		"                <LanguageId/>" + 
		"            </MessageKey>" + 
		"            <RequestMessageInfo>" + 
		"                <BankId>BM3</BankId>" + 
		"                <TimeZone/>" + 
		"                <EntityId/>" + 
		"                <EntityType/>" + 
		"                <ArmCorrelationId/>" + 
		"                <MessageDateTime>"+LocalDateTime.now()+"</MessageDateTime>" + 
		"            </RequestMessageInfo>" + 
		"            <Security>" + 
		"                <Token>" + 
		"                    <PasswordToken>" + 
		"                        <UserId/>" + 
		"                        <Password/>" + 
		"                    </PasswordToken>" + 
		"                </Token>" + 
		"                <FICertToken/>" + 
		"                <RealUserLoginSessionId/>" + 
		"                <RealUser/>" + 
		"                <RealUserPwd/>" + 
		"                <SSOTransferToken/>" + 
		"            </Security>" + 
		"        </RequestHeader>" + 
		"    </Header>" + 
		"    <Body>" + 
		"        <modifyLimitNodeRequest>" + 
		"            <ModifyLimitNodeInputVO>" + 
		"                <singleTranFlg>"+singleTranFlg+"</singleTranFlg>" + 
		"                <drwngPower>" + 
		"                    <amountValue>"+childLimitNodeModificationInputMap.get("TOTAL_FB_NONFB")+"</amountValue>" + 
		"                    <currencyCode>"+childLimitNodeModificationInputMap.get("CURRENCY_TWO")+"</currencyCode>" + 
		"                </drwngPower>" + 
		"                <drwngPowerInd>E</drwngPowerInd>" + 
		"                <drwngPowerPcnt>" + 
		"                    <value>100</value>" +
		"                </drwngPowerPcnt>" + 
		"                <freeText>"+pinstid+"</freeText>" + 
		"                <limitDesc>"+childLimitNodeModificationInputMap.get("SECURITY_PROVIDER")+"</limitDesc>" + 
		"                <limitExpiryDate>"+childLimitNodeModificationInputMap.get("LIMIT_EXP_DATE")+sysTime+"</limitExpiryDate>" + 
		"                <limitPrefix>"+childLimitNodeModificationInputMap.get("LIMIT_PREFIX")+"</limitPrefix>" + 
		"                <limitSanctDate>"+ childLimitNodeModificationInputMap.get("SANCTION_DATE")+ sysTime+"</limitSanctDate>" + 
		"                <limitSuffix>"+childLimitNodeModificationInputMap.get("LIMIT_SUFFIX")+"</limitSuffix>" + 
		"                <limitType>C</limitType>" + 
		"                <minReqdCollPcnt>" + 
		"                    <value>0</value>" + 		//to be discussed
		"                </minReqdCollPcnt>" + 
		"                <sanctAuthCode>"+childLimitNodeModificationInputMap.get("SANCTION_FORUM")+"</sanctAuthCode>" + 
		"                <sanctLevelCode>"+sanctLevelCode+"</sanctLevelCode>" + 
		"                <sanctLimit>" + 
		"                    <amountValue>"+amountValue+"</amountValue>" + 
		"                    <currencyCode>"+childLimitNodeModificationInputMap.get("CURRENCY_TWO")+"</currencyCode>" + 
		"                </sanctLimit>" + 
		"                <sanctLimitFlg/>" + lienTags+
		"                <baselDtls>" + 
		"                    <genDtls>" + 
		"                        <isRevocable>"+ isRevocable+"</isRevocable>" + //JIRA-10064  
		"                    </genDtls>" + 
		"                </baselDtls>" + 
		"            </ModifyLimitNodeInputVO>" + 
		"            <modifyLimitNode_CustomData>" + 
		"                <facAggAmt>"+childLimitNodeModificationInputMap.get("DOCUMENT_AMOUNT")+"</facAggAmt>" + 
		"                <facExpDate>"+commonUtility.rotateDate(childLimitNodeModificationInputMap.get("FACILITY_EXP_DATE"))+"</facExpDate>" + 
		"                <facSanctDate>"+commonUtility.rotateDate(childLimitNodeModificationInputMap.get("FA_EXECUTION_DATE"))+"</facSanctDate>" + 
		"                <riskId>testingFI</riskId>" + 	//to be discuss
		"                <productCode>"+childLimitNodeModificationInputMap.get("PROGRAM_CODE")+"</productCode>" + 
		"                <loanStat></loanStat>" + 
		"                <buyTakeDate></buyTakeDate>" + 
		"                <nameBank></nameBank>"+
		"                <sancValDt>"+ commonUtility.rotateDate(childLimitNodeModificationInputMap.get("SANCTION_VAL_DATE")) +"</sancValDt>" +
		"                <limExtnDate>"+ commonUtility.rotateDate(childLimitNodeModificationInputMap.get("LIMIT_EXP_DATE")) +"</limExtnDate>" +   // ADDED BY MAHESHV ON 20-10-2025 FOR ICO-9766
		"            </modifyLimitNode_CustomData>" + 
		"        </modifyLimitNodeRequest>" + 
		"    </Body>" + 
		"</FIXML>"+ 
		"]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
		InputStream is = new ByteArrayInputStream(soapMessage.getBytes());
		SOAPMessage requestSoap = MessageFactory.newInstance().createMessage(null, is);
		requestSoap.saveChanges();
		return requestSoap;
	}
}
