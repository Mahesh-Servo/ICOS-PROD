package com.LsmFiServices.FiLsmService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
public class ParentLimitNodeModificationService {

	private static final Logger logger = LoggerFactory.getLogger(ParentLimitNodeModificationService.class);
	
	@Autowired
	private ServiceDetails serviceDetails;
	@Autowired
	private ParentLimitNodeModificationUtility parentModUtility;

    public String parentLimitNodeModificationService(String pinstid,
	    Map<String, String> parentLimitModificationInputMap) throws SOAPException, SQLException {

	Map<String, String> API_REQ_RES_map = new HashMap<>();
	String parentLimitNodeModificationServiceResponse = "";
	SOAPMessage soapRequest = null;
	String RequestUUID = "";
	String HostTransaction = "";
	String Status = "";
	ServiceDetailsUpdatePojo pojo = new ServiceDetailsUpdatePojo();
	String requestType = "PARENT LIMIT NODE MODIFICATION";

	try {
	    pojo.setPinstId(pinstid);
	    pojo.setServiceName(requestType);
	    pojo.setRequestType(requestType);
	    SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
	    SOAPConnection soapConnection = soapConnectionFactory.createConnection();

	    soapRequest = createSOAPRequest(pinstid, parentLimitModificationInputMap);
	    pojo.setServiceRequest(OperationUtillity.soapMessageToString(soapRequest));
	    pojo.setStatus("Request Sent...!");
	    pojo.setReTrigger(true);
	    serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
	    SOAPMessage soapResponse = soapConnection.call(soapRequest, Constants.FIWebService);
	    parentLimitNodeModificationServiceResponse = OperationUtillity.soapMessageToString(soapResponse);
	    parentLimitNodeModificationServiceResponse = StringEscapeUtils
		    .unescapeXml(parentLimitNodeModificationServiceResponse);
	    if (parentLimitNodeModificationServiceResponse.contains("<HostTransaction>")) {
		HostTransaction = parentLimitNodeModificationServiceResponse.substring(
			parentLimitNodeModificationServiceResponse.indexOf("<HostTransaction>")
				+ "<HostTransaction>".length(),
			parentLimitNodeModificationServiceResponse.indexOf("</HostTransaction>"));
		if (HostTransaction.contains("<Status>")) {
		    Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),
			    HostTransaction.indexOf("</Status>"));
		}
	    }
	    if (Status.equalsIgnoreCase("SUCCESS")) {
		API_REQ_RES_map = xmlToMap.successPacketDataToMapLimitNodeModifiy(pinstid,
			parentLimitNodeModificationServiceResponse);
		pojo.setServiceResponse(parentLimitNodeModificationServiceResponse);
		pojo.setStatus(API_REQ_RES_map.get("Status"));
		pojo.setReTrigger(false);
		serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
	    } else {
		API_REQ_RES_map = xmlToMap.packetDataToMap(pinstid, parentLimitNodeModificationServiceResponse);
		pojo.setStatus(API_REQ_RES_map.get("Status"));
		pojo.setMessage(API_REQ_RES_map.get("ErrorDesc"));
		pojo.setServiceResponse(parentLimitNodeModificationServiceResponse);
		pojo.setReTrigger(true);
		serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
	    }
	    OperationUtillity.API_RequestResponse_Insert(OperationUtillity.soapMessageToString(soapRequest),
		    parentLimitNodeModificationServiceResponse, requestType, pinstid, API_REQ_RES_map, RequestUUID);
	} catch (Exception Ex) {
	    API_REQ_RES_map.put("RequestUUID", "");
	    API_REQ_RES_map.put("MessageDateTime", "");
	    API_REQ_RES_map.put("Error_Code", "500");
	    API_REQ_RES_map.put("message", Ex.getMessage());
	    API_REQ_RES_map.put("Status", "FAILED");
	    API_REQ_RES_map.put("Error_At", "Parent Limit Node Creation");
	    API_REQ_RES_map.put("request Header", soapRequest.getSOAPHeader().toString());
	    API_REQ_RES_map.put("request body", soapRequest.getSOAPBody().toString());
	    API_REQ_RES_map.put("response", parentLimitNodeModificationServiceResponse);
	    logger.info("ParentLimitNodeModificationService.parentLimitNodeModificationService()::" + pinstid + "\n "
		    + OperationUtillity.traceException(Ex));
	}
	return API_REQ_RES_map.get("Status");
    }

    public  SOAPMessage createSOAPRequest(String pinstid, Map<String, String> parentLimitModificationInputMap)
	    throws IOException, SOAPException, ParseException {

	logger.info("Entered into ParentLimitNodeModificationService.createSOAPRequest() ----------> " + pinstid + " \n"
		+ parentLimitModificationInputMap);

	String soapMessage = "";
	String isRevocable = "";
	
	if ("No".equalsIgnoreCase(parentLimitModificationInputMap.get("ISREVOCABLE"))) {
		logger.info("ParentLimitNodeModificationService.WHEN No() ----------> " + parentLimitModificationInputMap.get("ISREVOCABLE"));
		isRevocable = "N";
	} else 	if ("Yes".equalsIgnoreCase(parentLimitModificationInputMap.get("ISREVOCABLE"))) {
		logger.info("ParentLimitNodeModificationService.WHEN Yes() ----------> " + parentLimitModificationInputMap.get("ISREVOCABLE"));
		isRevocable = "Y";
	}
	
	logger.info("ParentLimitNodeModificationService.isRevocable----------> " + isRevocable);
	
	String sysTime = commonUtility.sysTimeWithT();
	String sanctLevelCode = OperationUtillity.getSanctLvl(pinstid);
	try {
	    soapMessage = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\">" +
		    "<soapenv:Header/>" +
		    "<soapenv:Body>" +
		    "<web:executeService>" +
		    "<arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		    "<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
		    "    <Header>" +
		    "        <RequestHeader>" +
		    "            <MessageKey>" +
		    "                <RequestUUID>" + commonUtility.createRequestUUID() + "</RequestUUID>" +
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
		    "                <MessageDateTime>" + LocalDateTime.now() + "</MessageDateTime>" +
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
		    "                <singleTranFlg>N</singleTranFlg>" +
		    "                <drwngPower>" +
		    "                    <amountValue>" + parentLimitModificationInputMap.get("TOTAL_FB_NONFB") + "</amountValue>" +
		    "                    <currencyCode>" +  parentLimitModificationInputMap.get("PARENT_CURRENCY")  + "</currencyCode>" +
		    "                </drwngPower>" +
		    "                <drwngPowerInd>E</drwngPowerInd>" +
		    "                <drwngPowerPcnt>" +
		    "                    <value>100</value>" +
		    "                </drwngPowerPcnt>" +
		    "                <freeText>" + pinstid + "</freeText>" +
		    "                <limitDesc>" + commonUtility.replaceAnd(commonUtility.removeSpecialCharacters(parentLimitModificationInputMap.get("PARENT_LIMIT_DESC"))) + "</limitDesc>" +
		    "                <limitExpiryDate>" + parentLimitModificationInputMap.get("PARENT_LIMIT_EXP_DATE") +  sysTime+ "</limitExpiryDate>" +
		    "                <limitPrefix>" + parentLimitModificationInputMap.get("PARENT_LIMIT_PREFIX") + "</limitPrefix>" +
		    "                <limitSanctDate>" + parentLimitModificationInputMap.get("PARENT_LIMIT_SANCTION_DATE") + sysTime + "</limitSanctDate>"+ // <!-- to be enabled for production -->" +
		    "                <limitSuffix>" + parentLimitModificationInputMap.get("PARENT_LIMIT_SUFFIX") + "</limitSuffix>" +
		    "                <limitType>C</limitType>" +
		    "                <minReqdCollPcnt>" +
		    "                    <value>0</value>" +
		    "                </minReqdCollPcnt>" +
		    "                <sanctAuthCode>" + parentLimitModificationInputMap.get("SANCTION_FORUM") + "</sanctAuthCode>" +
		    "                <sanctLevelCode>"+sanctLevelCode+"</sanctLevelCode>" + // to be discussed
		    "                <sanctLimit>" +
		    "                    <amountValue>" + parentLimitModificationInputMap.get("TOTAL_FB_NONFB") + "</amountValue>" +
		    "                    <currencyCode>" +  parentLimitModificationInputMap.get("PARENT_CURRENCY")  + "</currencyCode>" +
		    "                </sanctLimit>" +
		    "                <sanctLimitFlg/>" +
		    "                <baselDtls>" +
		    "                    <genDtls>" +
			"                        <isRevocable>"+isRevocable+"</isRevocable>" + 
		    "                    </genDtls>" +
		    "                </baselDtls>" +
		    "            </ModifyLimitNodeInputVO>" +
		    "            <modifyLimitNode_CustomData>" +
		    "                <facAggAmt>" + parentLimitModificationInputMap.get("DOCUMNET_AMOUNT") + "</facAggAmt>" +
		    "                <facExpDate>" + commonUtility.rotateDate(parentLimitModificationInputMap.get("FACILITY_EXP_DATE")) + "</facExpDate>" +
		    "                <facSanctDate>" + commonUtility.rotateDate(parentLimitModificationInputMap.get("FA_EXECUTION_DATE")) + "</facSanctDate>" +
		    "                <riskId>NA</riskId>" +
		    "                <productCode>" + parentLimitModificationInputMap.get("PROGRAM_CODE") + "</productCode>" +
		    "                <loanStat></loanStat>" +
		    "                <buyTakeDate></buyTakeDate>" +
		    "                <nameBank></nameBank>" +
		    "                <sancValDt>" + commonUtility.rotateDate(parentLimitModificationInputMap.get("SANCTION_VAL_DATE")) + "</sancValDt>" +
		    "                <limExtnDate>"+ commonUtility.rotateDate(parentLimitModificationInputMap.get("PARENT_LIMIT_EXP_DATE")) +"</limExtnDate>" +   // ADDED BY MAHESHV ON 20-10-2025 FOR ICO-9766
		    "            </modifyLimitNode_CustomData>" +
		    "        </modifyLimitNodeRequest>" +
		    "    </Body>" +
		    "</FIXML>" +
		    "]]></arg_0_0>" +
		    "</web:executeService>" +
		    "</soapenv:Body>" +
		    "</soapenv:Envelope>";

	} catch (Exception e) {
	    logger.info("ParentLimitNodeModificationService.createSOAPRequest()-->" + OperationUtillity.traceException(e));
	}
	InputStream is = new ByteArrayInputStream(soapMessage.getBytes());
	SOAPMessage requestSoap = MessageFactory.newInstance().createMessage(null, is);
	requestSoap.saveChanges();
	return requestSoap;
    }
}
