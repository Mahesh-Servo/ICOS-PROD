package com.LsmFiServices.FiLsmService;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

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
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.RevisedServiceSequence;
import com.LsmFiServices.Utility.ServiceDetails;
import com.LsmFiServices.Utility.commonUtility;
import com.LsmFiServices.Utility.xmlToMap;
import com.LsmFiServices.pojo.serviceexecutiondetails.ServiceDetailsUpdatePojo;

@SuppressWarnings("deprecation")
@Service
public class FeeRecoveryService {

    @Autowired
    private ServiceDetails serviceDetails;
    private static final Logger logger = LoggerFactory.getLogger(FeeRecoveryService.class);

    public Map<String, String> feeRecoveryService(String PINSTID, String feeID) throws SOAPException, SQLException {
	logger.info("Entering into feeRecoveryService for -->" + PINSTID + " feeId->" + feeID);

	Map<String, String> API_REQ_RES_map = new LinkedHashMap<String, String>();
	String feeRecoveryResponse = "";
	SOAPMessage soapRequest = null;
	String RequestUUID = "", HostTransaction = "", Status = "";
	String Transaction_Id = "";
	List<Map<String, String>> GetEXTData = new LinkedList<>();
	String requestType = "";
	ServiceDetailsUpdatePojo pojo = new ServiceDetailsUpdatePojo();

//	Map<String , String> caseTypeCheckMap = OperationUtillity.getCaseType(PINSTID);
//	if("LOPS".equalsIgnoreCase(OperationUtillity.NullReplace(caseTypeCheckMap.get("CaseType")))) {

	try {
	    GetEXTData = OperationUtillity.getFeeRecoveryData(PINSTID, feeID);
	    int dataLen = GetEXTData.size();
	    Map<String, String> tempMap = GetEXTData.get(0);
	    String businessGroup = tempMap.get("Business_Group");
	    String OtherAccountFlag = "False";
	    for (int j = 1; j < dataLen; j++) {
		Map<String, String> IndividualMapData = new LinkedHashMap<>();
		IndividualMapData = GetEXTData.get(j);
		logger.info("Individual map data for " + j + "---->>" + IndividualMapData);
		if ("Other Account".equalsIgnoreCase(
			OperationUtillity.NullReplace(IndividualMapData.get("Fee_to_be_recovered_from_" + j)))) {
		    OtherAccountFlag = "True";
		} else {
		    if (RevisedServiceSequence.getFlagForFeeRecovery(PINSTID)) {
			OtherAccountFlag = "True";
		    }
		}
		if (OperationUtillity.NullReplace(IndividualMapData.get("Fee_Type_" + j)) != null
			&& "True".equalsIgnoreCase(OtherAccountFlag)) {
		    String k = "";
		    for (String key : IndividualMapData.keySet()) {
			if (key.contains("Fee_Type_")) {
			    k = key.split("_")[2];
			    break;
			}
		    }
		    if (OperationUtillity.NullReplace(IndividualMapData.get("Fee_Type_" + k))
			    .equalsIgnoreCase("Loan Processing Fee")
			    || OperationUtillity.NullReplace(IndividualMapData.get("Fee_Type_" + k))
				    .equalsIgnoreCase("Valuation Charges")
			    || OperationUtillity.NullReplace(IndividualMapData.get("Fee_Type_" + k))
				    .equalsIgnoreCase("Legal Fees")
			    || OperationUtillity.NullReplace(IndividualMapData.get("Fee_Type_" + k))
				    .equalsIgnoreCase("Processing Fee")
			    || OperationUtillity.NullReplace(IndividualMapData.get("Fee_Type_" + k))
				    .equalsIgnoreCase("Valuation Fee/Legal Fees (if there)")
			    || OperationUtillity.NullReplace(IndividualMapData.get("Fee_Type_" + k))
				    .equalsIgnoreCase("Processing Fee/Valuation Fee")) {

			pojo.setPinstId(PINSTID);
			pojo.setServiceName("FEE RECOVERY");
			pojo.setFacility(IndividualMapData.get("Fee_Type_" + k));
			pojo.setAccountNumber(IndividualMapData.get("Account_Number_" + k));
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			Map<String, String> inputMap = createSOAPRequest(PINSTID, IndividualMapData, businessGroup);
			logger.info("inputMap.get SOAP_MESSAGE-->" + inputMap.get("SOAP_MESSAGE"));
			String request = inputMap.get("SOAP_MESSAGE");
			soapRequest = OperationUtillity.convertStringToSoapMessage(request); // converting string into
											     // soap message
			pojo.setServiceRequest(OperationUtillity.soapMessageToString(soapRequest));
			pojo.setStatus("Request Sent...!");
			pojo.setReTrigger(true);
			requestType = "FEE RECOVERY SERVICE : Fee Type-" + k + "::"
					+ IndividualMapData.get("Fee_Type_" + k) + ":: Event ID :: " + inputMap.get("EVENT_ID");
			 pojo.setRequestType(requestType);
			serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			SOAPMessage soapResponse = soapConnection.call(soapRequest, Constants.FIWebService);
			feeRecoveryResponse = OperationUtillity.soapMessageToString(soapResponse);
			feeRecoveryResponse = StringEscapeUtils.unescapeXml(feeRecoveryResponse);
			if (feeRecoveryResponse.contains("<HostTransaction>")) {
			    HostTransaction = feeRecoveryResponse.substring(
				    feeRecoveryResponse.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
				    feeRecoveryResponse.indexOf("</HostTransaction>"));
			    if (HostTransaction.contains("<Status>")) {
				Status = HostTransaction.substring(
					HostTransaction.indexOf("<Status>") + "<Status>".length(),
					HostTransaction.indexOf("</Status>"));
			    }
			}
			logger.info("Status check -->in fee" + Status);
			requestType = "FEE RECOVERY SERVICE : Fee Type-" + k + "::"
				+ IndividualMapData.get("Fee_Type_" + k) + ":: Event ID :: " + inputMap.get("EVENT_ID");
			if (Status.equalsIgnoreCase("SUCCESS")) {
			    API_REQ_RES_map.put("Status", Status);
			    API_REQ_RES_map = xmlToMap.successPacketDataToMapfeeRecovery(PINSTID, feeRecoveryResponse);
			    String rawResp = API_REQ_RES_map.get("Response");
			    String[] tempdata = rawResp.split(":");
			    if (tempdata.length > 1) {
				Transaction_Id = tempdata[1].trim();
				IndividualMapData.put("Transaction_Id", Transaction_Id);
				logger.info("GetEXTData transaction ID------>" + IndividualMapData);
				OperationUtillity.saveTxnID(PINSTID, IndividualMapData, j);
			    }
			    logger.info("into fee recobvery success -->" + API_REQ_RES_map);
			    pojo.setStatus(API_REQ_RES_map.get("Status"));

			    try {
				pojo.setMessage(API_REQ_RES_map.get("message"));
			    } catch (Exception e) {
				logger.info("Fee Recovery message key test-->" + API_REQ_RES_map);
				e.printStackTrace();
			    }
			    pojo.setRequestType(requestType);
			    pojo.setServiceResponse(feeRecoveryResponse);
			    pojo.setReTrigger(false);
			    if (API_REQ_RES_map.get("Status").equalsIgnoreCase("FAILURE")) {
				pojo.setReTrigger(true);
			    }
			    serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			} else {
			    API_REQ_RES_map = xmlToMap.packetDataToMap(PINSTID, feeRecoveryResponse);
			    logger.info("into Rate Of fee recobvery failure -->" + API_REQ_RES_map);
			    pojo.setStatus(API_REQ_RES_map.get("Status"));
			    if (!"".equals(API_REQ_RES_map.get("ErrorDesc"))) {
				pojo.setMessage(API_REQ_RES_map.get("ErrorDesc"));
			    }
			    pojo.setRequestType(requestType);
			    pojo.setServiceResponse(feeRecoveryResponse);
			    logger.info("Pojo chec in failed fee->" + pojo);
			    pojo.setReTrigger(true);
			    serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			}
			OperationUtillity.FeeRecoveryAPI_RequestResponse_Insert(
				OperationUtillity.soapMessageToString(soapRequest), feeRecoveryResponse, requestType,
				PINSTID, API_REQ_RES_map, RequestUUID);
		    }
		}
		// } // m
	    }
	} catch (Exception Ex) {
	    API_REQ_RES_map.put("RequestUUID", "");
	    API_REQ_RES_map.put("MessageDateTime", "");
	    API_REQ_RES_map.put("Error_Code", "500");
	    API_REQ_RES_map.put("message", Ex.getMessage());
	    API_REQ_RES_map.put("Status", "FAILED");
	    API_REQ_RES_map.put("Error_At", "Fee Recovery Service");
	    API_REQ_RES_map.put("request body", String.valueOf(soapRequest));
	    API_REQ_RES_map.put("response", feeRecoveryResponse);
	    logger.info("Exception in feeRecoveryService()->" + OperationUtillity.traceException(Ex));
	}
	return API_REQ_RES_map;
    }

    public Map<String, String> createSOAPRequest(String PINSTID, Map<String, String> GetEXTData, String businessGroup)
	    throws IOException, SOAPException, ParseException {

	String requestUuid = "";
	String dateAndTime = commonUtility.dateFormat();
	logger.info("Entered into Fee recovery request pkt checking map data ------->" + GetEXTData);
	String accNo = "", feeAmt = "", feeType = "", acctSolid = "";
	String EventId = "";
	Map<String, String> opMap = new WeakHashMap<>();

	try {
	    for (Map.Entry<String, String> map : GetEXTData.entrySet()) {
		if (map.getKey().contains("Account_Number")) {
		    accNo = map.getValue();
		} else if (map.getKey().contains("Fee_Amount")) {
		    feeAmt = map.getValue();
		} else if (map.getKey().contains("Fee_Type")) {
		    feeType = map.getValue();
		}
	    }
	} catch (Exception e) {
	    logger.info("Exception in setting values to variables --->" + OperationUtillity.traceException(e));
	}
	try {
	    if (("BLG").equalsIgnoreCase(businessGroup)) {
		EventId = "CNPROBLG";
	    } else if (("RIBG").equalsIgnoreCase(businessGroup) || ("RBC").equalsIgnoreCase(businessGroup)) {

		for (Map.Entry<String, String> map : GetEXTData.entrySet()) {

		    if (("Loan Processing Fee").equalsIgnoreCase(map.getValue())) {
			EventId = "CNPFEACL";
		    } else if (("Valuation Charges").equalsIgnoreCase(map.getValue())) {
			EventId = "CNVLCRBC";//
		    } else if (("Legal Fees").equalsIgnoreCase(map.getValue())) {
			EventId = "CNLGCRBC";
		    }
		}
	    } else {
		for (Map.Entry<String, String> map : GetEXTData.entrySet()) {
		    if (("Loan Processing Fee").equalsIgnoreCase(map.getValue())) {
			EventId = "CN0LTFEE"; // zero is that not O
		    } else if (("Valuation Charges").equalsIgnoreCase(map.getValue())
			    || ("Legal Fees").equalsIgnoreCase(map.getValue())) {
			EventId = "CNCOPOTH";
		    }
		}
	    }
	} catch (Exception e) {
	    logger.info("There is problem with setting Event ID and Bussiness Group--->"
		    + OperationUtillity.traceException(e));
	}

	opMap.put("EVENT_ID", EventId);
	acctSolid = OperationUtillity.fetchAcctSolidfromLsmRmSignedApiInfo(PINSTID, accNo);
	requestUuid = OperationUtillity.getRequestUUIDFeeRecoveryFromDB(PINSTID, feeType);
	
	if ("".equals(requestUuid)) {
		requestUuid = commonUtility.createRequestUUID();
	}

		String soapMessage = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
			"<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + 
			"   <Body>" + 
			"      <executeFinacleScriptRequest>" + 
			"         <executeFinacleScript_CustomData>" + 
			"            <Acct_solid>"+ acctSolid +"</Acct_solid>" +
			"            <Amt>"+OperationUtillity.NullReplace(feeAmt)+"</Amt>" + 				
			"            <Chupl_flg>N</Chupl_flg>" + 
			"            <EventId>"+OperationUtillity.NullReplace(EventId)+"</EventId>" + 
			"            <EventType>GCHRG</EventType>" + 
			"            <Exmpt_Flg>N</Exmpt_Flg>" + 
			"            <Oper_acctId>"+ OperationUtillity.NullReplace(accNo)+ "</Oper_acctId>" + 
			"            <Tran_Partclrs>"+OperationUtillity.NullReplace(feeType)+" "+PINSTID+"</Tran_Partclrs>" + 	
			"            <Tran_rmks>"+OperationUtillity.NullReplace(GetEXTData.get("Remark"))+"</Tran_rmks>" + //LSM number 
			"         </executeFinacleScript_CustomData>" + 
			"         <ExecuteFinacleScriptInputVO>" + 
			"            <requestId>FI_Process_upload.scr</requestId>" + 
			"         </ExecuteFinacleScriptInputVO>" + 
			"      </executeFinacleScriptRequest>" + 
			"   </Body>" + 
			"   <Header>" + 
			"      <RequestHeader>" + 
			"         <MessageKey>" + 
			"            <ChannelId>CLS</ChannelId>" + 
			"            <LanguageId></LanguageId>" + 
			"            <RequestUUID>"+requestUuid+"</RequestUUID>" + 
			"            <ServiceRequestId>executeFinacleScript</ServiceRequestId>" + 
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
		opMap.put("SOAP_MESSAGE", soapMessage);

	return opMap;	
	}
}
