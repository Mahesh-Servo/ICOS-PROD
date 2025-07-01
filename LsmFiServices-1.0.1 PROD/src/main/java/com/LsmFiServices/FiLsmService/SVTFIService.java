package com.LsmFiServices.FiLsmService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
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
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.SVTFIServiceUtility;
import com.LsmFiServices.Utility.SVTLinkageServiceUtility;
import com.LsmFiServices.Utility.ServiceDetails;
import com.LsmFiServices.Utility.commonUtility;
import com.LsmFiServices.Utility.xmlToMap;
import com.LsmFiServices.pojo.serviceexecutiondetails.ServiceDetailsUpdatePojo;

@Service
@SuppressWarnings("deprecation")
public class SVTFIService {
    private static final Logger logger = LoggerFactory.getLogger(SVTFIService.class);

    @Autowired
    private SVTFIServiceUtility svtFIServiceUtility;

    @Autowired
    private SVTLinkageService svtLinkageService;

    @Autowired
    private SVTLinkageServiceUtility utility;

    @Autowired
    private ServiceDetails serviceDetails;

    public Map<String, String> SVTService(String pinstId, String Security) throws SOAPException, SQLException {

	logger.info("Entering into  SVTService() for pinstid ::-> " + pinstId);

	Map<String, String> API_REQ_RES_map = new LinkedHashMap<>();
	String svtResponse = "";
	String RequestUUID = "", HostTransaction = "", Status = "";
	List<Map<String, String>> finalListOfMap = new LinkedList<>();
	Map<String, String> collateralDataForFDMap = new LinkedHashMap<>();
	ServiceDetailsUpdatePojo pojo = new ServiceDetailsUpdatePojo();
	String requestType = "";
	try {
	    if ("LOPS".equalsIgnoreCase(OperationUtillity.getCaseType(pinstId).get("CaseType"))) { 
		finalListOfMap = svtFIServiceUtility.getSVTDataListOfMap(pinstId, Security);
		logger.info("FinalList of map into SVTFIService.SVTService(){}", finalListOfMap);
		for (int i = 0; i < finalListOfMap.size(); i++) {
		    Map<String, String> IndividualSecurityMapData = finalListOfMap.get(i);
		    if (("Immovable Fixed Assets".equalsIgnoreCase(IndividualSecurityMapData.get("SECURITY_TYPE")))
			    || ("Fixed Deposit (Others)".equalsIgnoreCase(IndividualSecurityMapData.get("SECURITY_TYPE")))) {
			if (("Fixed Deposit (Others)".equalsIgnoreCase(IndividualSecurityMapData.get("SECURITY_TYPE")))) { 
			    collateralDataForFDMap.put("SECURITY_TYPE", IndividualSecurityMapData.get("SECURITY_TYPE")); // Immovables,
			    collateralDataForFDMap.putAll(IndividualSecurityMapData);
			    logger.info("Calling SVT For FD Security{}", collateralDataForFDMap);
			    SVTServiceForFixedDeposit(pinstId, IndividualSecurityMapData.get("SECURITY_NAME"),collateralDataForFDMap);
			    continue; // when SVT execute for FD then below logic for this iteration will be skipped
			}
			    pojo.setPinstId(pinstId);
			    pojo.setServiceName("SVT");
			    pojo.setAccountNumber(IndividualSecurityMapData.get("SECURITY_TYPE"));
			    pojo.setFacility(IndividualSecurityMapData.get("SECURITY_NAME"));
			    SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			    SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			    SOAPMessage soapRequest = createSOAPRequest(pinstId, IndividualSecurityMapData);
			    pojo.setServiceRequest(OperationUtillity.soapMessageToString(soapRequest));
			    pojo.setStatus("Request Sent...!");
			    pojo.setReTrigger(true);
			    serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			    SOAPMessage soapResponse = soapConnection.call(soapRequest, Constants.FIWebService);
			    svtResponse = OperationUtillity.soapMessageToString(soapResponse);
			    svtResponse = StringEscapeUtils.unescapeXml(svtResponse);
			    if (svtResponse.contains("<HostTransaction>")) {
				HostTransaction = svtResponse.substring(
					svtResponse.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
					svtResponse.indexOf("</HostTransaction>"));
				if (HostTransaction.contains("<Status>")) {
				    Status = HostTransaction.substring(
					    HostTransaction.indexOf("<Status>") + "<Status>".length(),
					    HostTransaction.indexOf("</Status>"));
				}
			    }
			    String collateralId = "";
			    if (Status.equalsIgnoreCase("SUCCESS")) {
				API_REQ_RES_map = xmlToMap.successPacketDataToMapSVT(pinstId, svtResponse);
				String rawResp = API_REQ_RES_map.get("message");
				if (rawResp.contains(":")) {
				    collateralId = Arrays.stream(rawResp.split(":")).skip(1) // Skip the part before ":"
					    .map(String::trim).findFirst() // Get the first part after ":"
					    .map(s -> s.split(" ")[0]) // Extract the first word
					    .orElse("");
				}
				IndividualSecurityMapData.put("COLLATERAL_ID", collateralId);
				svtFIServiceUtility.saveCollateralID(pinstId, IndividualSecurityMapData);
				pojo.setStatus(API_REQ_RES_map.get("Status"));
				pojo.setServiceResponse(svtResponse);
				pojo.setReTrigger(false);
				serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
				    logger.info("Security check in SVTFIService.SVTService().IndividualSecurityMapData{} ", IndividualSecurityMapData.get("SECURITY_NAME"));
				    List<Map<String, String>> list = utility.productwiseSVTLinkageData(pinstId,IndividualSecurityMapData.get("SECURITY_NAME"));
				    for (int k = 0; k < list.size(); k++) {
					Map<String, String> map1 = list.get(k);
					map1.put("COLLATERAL_ID", collateralId);
					map1.putAll(IndividualSecurityMapData);
					svtLinkageService.svtLinkageService(pinstId, map1);
				    }
			    } else {
				API_REQ_RES_map = xmlToMap.packetDataToMap(pinstId, svtResponse);
				pojo.setStatus(API_REQ_RES_map.get("Status"));
				pojo.setMessage(API_REQ_RES_map.get("ErrorDesc"));
				pojo.setServiceResponse(svtResponse);
				pojo.setReTrigger(true);
				serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			    }
			    requestType = "SVT : "
				    + OperationUtillity.NullReplace(IndividualSecurityMapData.get("SECURITY_NAME"))+ " : "
				    + OperationUtillity.NullReplace(IndividualSecurityMapData.get("SECURITY_TYPE"))+ " : "
				    + OperationUtillity.NullReplace(IndividualSecurityMapData.get("SUB_TYPE_SECURITY_SVT"))
				    + " : " + OperationUtillity.NullReplace(IndividualSecurityMapData.get("PRODUCT"))
				    + ", COLLATERAL_ID :: " + IndividualSecurityMapData.get("COLLATERAL_ID");
			    OperationUtillity.API_RequestResponse_Insert(OperationUtillity.soapMessageToString(soapRequest), svtResponse, requestType,pinstId, API_REQ_RES_map, RequestUUID);
			    pojo.setRequestType(requestType);
			    serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
		    }
		}
	    }
	} catch (Exception Ex) {
	    API_REQ_RES_map.put("RequestUUID", "");
	    API_REQ_RES_map.put("MessageDateTime", API_REQ_RES_map.get("MessageDateTime"));
	    API_REQ_RES_map.put("Error_Code", "500");
	    API_REQ_RES_map.put("message", Ex.getMessage());
	    API_REQ_RES_map.put("Status", "FAILED");
	    logger.error("SVTFIService.SVTService(){}",OperationUtillity.traceException(Ex));
	}
	return API_REQ_RES_map;
    }

    public Map<String, String> SVTServiceForFixedDeposit(String pinstId, String Security,
	    Map<String, String> SVTMAPforFD) throws SOAPException, SQLException {

	logger.info("Entering into  SVTFIService.SVTServiceForFixedDeposite() for:: " + pinstId + " and Security :: "+ Security);
	logger.info("Map check for  SVTFIService.SVTServiceForFixedDeposite(){} ", SVTMAPforFD);

	Map<String, String> API_REQ_RES_map = new LinkedHashMap<>();
	String svtResponse = "";
	String RequestUUID = "", HostTransaction = "", Status = "", Collateral_Id = ""; // response = "";
	ServiceDetailsUpdatePojo pojo = new ServiceDetailsUpdatePojo();
	List<Map<String, String>> finalListOfMapForFD = null;
	String requestType = "";

	try {
	    finalListOfMapForFD = svtFIServiceUtility.getFixedDepositDetails(pinstId, Security);
	    logger.info("SVTFIService.SVTServiceForFixedDeposit() map check{}", finalListOfMapForFD);
	    for (int i = 0; i < finalListOfMapForFD.size(); i++) {
		Map<String, String> IndividualSecurityMapData = finalListOfMapForFD.get(i);
		IndividualSecurityMapData.putAll(SVTMAPforFD);
		try {
		    pojo.setPinstId(pinstId);
		    pojo.setServiceName("SVT");
		    pojo.setAccountNumber(IndividualSecurityMapData.get("SECURITY_TYPE"));
		    String facility = IndividualSecurityMapData.get("SECURITY_NAME") + " :: "
			    + IndividualSecurityMapData.get("SECURITY_TYPE") + " :: "
			    + IndividualSecurityMapData.get("FD_ACCOUNT_NUMBER");
		    pojo.setFacility(facility);
		    SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
		    SOAPConnection soapConnection = soapConnectionFactory.createConnection();
		    SOAPMessage soapRequest = createSOAPRequest(pinstId, IndividualSecurityMapData);
		    pojo.setServiceRequest(OperationUtillity.soapMessageToString(soapRequest));
		    pojo.setStatus("Request Sent...!");
		    pojo.setReTrigger(true);
		    serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
		    SOAPMessage soapResponse = soapConnection.call(soapRequest, Constants.FIWebService);
		    svtResponse = OperationUtillity.soapMessageToString(soapResponse);
		    svtResponse = StringEscapeUtils.unescapeXml(svtResponse);
		    if (svtResponse.contains("<HostTransaction>")) {
			HostTransaction = svtResponse.substring(
				svtResponse.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
				svtResponse.indexOf("</HostTransaction>"));
			if (HostTransaction.contains("<Status>")) {
			    Status = HostTransaction.substring(
				    HostTransaction.indexOf("<Status>") + "<Status>".length(),
				    HostTransaction.indexOf("</Status>"));
			}
		    }
		    if (Status.equalsIgnoreCase("SUCCESS")) {
			API_REQ_RES_map = xmlToMap.successPacketDataToMapSVT(pinstId, svtResponse);
			String rawResp = API_REQ_RES_map.get("message");
			if (rawResp.contains(":")) {
			    Collateral_Id = Arrays.stream(rawResp.split(":")).skip(1) // Skip the part before ":"
				    .map(String::trim).findFirst() // Get the first part after ":"
				    .map(s -> s.split(" ")[0]) // Extract the first word
				    .orElse("");
			}
			IndividualSecurityMapData.put("COLLATERAL_ID", Collateral_Id);
			svtFIServiceUtility.saveCollateralID(pinstId, IndividualSecurityMapData);
			logger.info("Security check in SVTFIService.SVTService() IndividualSecurityMapData{}",IndividualSecurityMapData.get("SECURITY_NAME"));
			List<Map<String, String>> list = utility.productwiseSVTLinkageData(pinstId,
				IndividualSecurityMapData.get("SECURITY_NAME"));
			for (int k = 0; k < list.size(); k++) {
			    Map<String, String> map1 = list.get(k);
			    map1.put("COLLATERAL_ID", Collateral_Id);
			    map1.putAll(IndividualSecurityMapData);
			    svtLinkageService.svtLinkageService(pinstId, map1);
			}
			pojo.setStatus(API_REQ_RES_map.get("Status"));
			pojo.setServiceResponse(svtResponse);
			pojo.setReTrigger(false);
			serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
		    } else {
			API_REQ_RES_map = xmlToMap.packetDataToMap(pinstId, svtResponse);
			pojo.setStatus(API_REQ_RES_map.get("Status"));
			pojo.setMessage(API_REQ_RES_map.get("ErrorDesc"));
			pojo.setServiceResponse(svtResponse);
			pojo.setReTrigger(true);
			serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
		    }

		    requestType = "SVT : "
			    + OperationUtillity.NullReplace(IndividualSecurityMapData.get("SECURITY_NAME")) + " : "
			    + OperationUtillity.NullReplace(IndividualSecurityMapData.get("SECURITY_TYPE"))
			    + " & ACCOUNT_NUMBER :"
			    + OperationUtillity.NullReplace(IndividualSecurityMapData.get("FD_ACCOUNT_NUMBER"))
			    + "  & SUB_TYPE_SECURITY_SVT ::"
			    + OperationUtillity.NullReplace(IndividualSecurityMapData.get("SUB_TYPE_SECURITY_SVT"))
			    + ", COLLATERAL_ID ::" + Collateral_Id;

		    OperationUtillity.API_RequestResponse_Insert(OperationUtillity.soapMessageToString(soapRequest),svtResponse, requestType, pinstId, API_REQ_RES_map, RequestUUID);
		    pojo.setRequestType(requestType);
		    serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
		} catch (Exception Ex) {
		    API_REQ_RES_map.put("RequestUUID", "");
		    API_REQ_RES_map.put("MessageDateTime", API_REQ_RES_map.get("MessageDateTime"));
		    API_REQ_RES_map.put("Error_Code", "500");
		    API_REQ_RES_map.put("message", Ex.getMessage());
		    API_REQ_RES_map.put("Status", "FAILED");
		    logger.info("SVTFIService.SVTServiceForFixedDeposit(){}",OperationUtillity.traceException(Ex));
		}
	    }
	} catch (Exception e) {
	    logger.info("SVTFIService.SVTServiceForFixedDeposit(){}", OperationUtillity.traceException(e));
	}
	return API_REQ_RES_map;
    }
	
	public  SOAPMessage createSOAPRequest(String pinstId, Map<String, String> SVTData)
			throws IOException, SOAPException, ParseException {
		
		String security_type = OperationUtillity.NullReplace(SVTData.get("SECURITY_TYPE"));
		logger.info("Entering into SVTFIService.createSOAPRequest : : " + pinstId+"  and Security Type -->"+security_type+" and Map Data-->"+SVTData);
		String addressLine1 = OperationUtillity.NullReplace(SVTData.get("ADDRESS_LINE_1")+", "+SVTData.get("AREA"));
		String addressLine2 = OperationUtillity.NullReplace(SVTData.get("ROAD") + ", " + SVTData.get("LANDMARK"));		
		String cityCode = OperationUtillity.NullReplace(SVTData.get("CITY_CODE"));
		String soapMessage ="";
		String postalCode = OperationUtillity.NullReplace(SVTData.get("PINCODE"));
		String stateCode = OperationUtillity.NullReplace(SVTData.get("STATE_CODE"));
		String fdAmount = OperationUtillity.NullReplace(SVTData.get("FD_AMOUNT"));
		String apportionedValueInString = OperationUtillity.NullReplace(SVTData.get("APPORTIONED_VALUE"));
		
		if (!apportionedValueInString.equalsIgnoreCase("0") || !apportionedValueInString.equalsIgnoreCase("")) {
			apportionedValueInString = commonUtility.millionString(OperationUtillity.NullReplace(SVTData.get("APPORTIONED_VALUE")));
		}
		
		if ((!"".equals(addressLine1)) && (!addressLine1.equals(null)) && ((addressLine1.length() > 45))) {
			addressLine1 = addressLine1.substring(0, 45);
		}
		if ((!"".equals(addressLine2)) && (!addressLine2.equals(null)) && ((addressLine2.length() > 45))) {
			addressLine2 = addressLine2.substring(0, 45);
		}
			if("Fixed Deposit (Others)".equalsIgnoreCase(security_type)) {			
				soapMessage = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
						"<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + 
						"   <Body>" + 
						"      <executeFinacleScriptRequest>" + 
						"         <executeFinacleScript_CustomData>" + 
						"            <Ceiling_Limit>"+commonUtility.millionString(fdAmount)+"</Ceiling_Limit>" + 
						"            <Collateral_Code>"+OperationUtillity.NullReplace(SVTData.get("COLLATERAL_CODE"))+"</Collateral_Code>" +
						"            <DrAcct_No>"+OperationUtillity.NullReplace(SVTData.get("FD_ACCOUNT_NUMBER"))+"</DrAcct_No>" + 
						"         </executeFinacleScript_CustomData>" + 
						"         <ExecuteFinacleScriptInputVO>" + 
						"            <requestId>FI_LodgeCollateral_Deposits.scr</requestId>" + 
						"         </ExecuteFinacleScriptInputVO>" + 
						"      </executeFinacleScriptRequest>" + 
						"   </Body>" + 
						"   <Header>" + 
						"      <RequestHeader>" + 
						"         <MessageKey>" + 
						"            <ChannelId>CLS</ChannelId>" + 
						"            <LanguageId></LanguageId>" + 
						"            <RequestUUID>"+commonUtility.createRequestUUID()+"</RequestUUID>" + 
						"            <ServiceRequestId>executeFinacleScript</ServiceRequestId>" + 
						"            <ServiceRequestVersion>10.2</ServiceRequestVersion>" + 
						"         </MessageKey>" + 
						"         <RequestMessageInfo>" + 
						"            <ArmCorrelationId></ArmCorrelationId>" + 
						"            <BankId>BM3</BankId>" + 
						"            <EntityId></EntityId>" + 
						"            <EntityType></EntityType>" + 
						"            <MessageDateTime>"+LocalDateTime.now()+"</MessageDateTime>" + 
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
			}else{
				soapMessage = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Header>" + 
						"      <RequestHeader>" + 
						"         <MessageKey>" + 
						"            <ChannelId>CLS</ChannelId>" + 
						"            <LanguageId></LanguageId>" + 
						"            <RequestUUID>"+commonUtility.createRequestUUID()+"</RequestUUID>" + 
						"            <ServiceRequestId>executeFinacleScript</ServiceRequestId>" + 
						"            <ServiceRequestVersion>10.2</ServiceRequestVersion>" + 
						"         </MessageKey>" + 
						"         <RequestMessageInfo>" + 
						"            <ArmCorrelationId></ArmCorrelationId>" + 
						"            <BankId>BM3</BankId>" + 
						"            <EntityId></EntityId>" + 
						"            <EntityType></EntityType>" + 
						"            <MessageDateTime>"+LocalDateTime.now()+"</MessageDateTime>" + 
						"            <TimeZone></TimeZone>" + 
						"         </RequestMessageInfo>" + 
						"         <Security>" + 
						"            <FICertToken></FICertToken>" + 
						"            <SSOTransferToken></SSOTransferToken>" + 
						"            <RealUser></RealUser>" + 
						"            <RealUserLoginSessionId></RealUserLoginSessionId>" + 
						"            <RealUserPwd></RealUserPwd>" + 
						"            <Token>" + 
						"               <passwordToken>" + 
						"                  <password></password>" + 
						"                  <userId></userId>" + 
						"               </passwordToken>" + 
						"            </Token>" + 
						"         </Security>" + 
						"      </RequestHeader>" + 
						"   </Header><Body>" + 
						"      <executeFinacleScriptRequest>" + 
						"         <ExecuteFinacleScriptInputVO>" + 
						"            <requestId>FI_LodgeCollateral_Immov.scr</requestId>" + 
						"         </ExecuteFinacleScriptInputVO>" + 
						"         <executeFinacleScript_CustomData>" + 
						"            <Address_Line1>"+commonUtility.replaceAnd(commonUtility.removeSpecialCharacters(addressLine1))+"</Address_Line1>" + 
						"            <Address_Line2>"+commonUtility.replaceAnd(commonUtility.removeSpecialCharacters(addressLine2))+"</Address_Line2>" + 
						"            <Address_Line3>"+OperationUtillity.NullReplace(SVTData.get("CITY"))+"</Address_Line3>" + 
						"            <Assessed_Value>"+apportionedValueInString+"</Assessed_Value>" + 
						"            <Built_Area></Built_Area>" + 
						"            <Ceiling_Limit>"+commonUtility.millionString(OperationUtillity.NullReplace(SVTData.get("VALUE_OF_SUB_TYPE_SEC")))+"</Ceiling_Limit>" +
						"            <Charge_Amount>"+apportionedValueInString+"</Charge_Amount>" + 
						"            <City>"+cityCode+"</City>" + 
						"            <Collateral_Class>"+OperationUtillity.NullReplace(SVTData.get("COLLATERAL_CLASS"))+"</Collateral_Class>" + 
						"            <Collateral_Code>"+OperationUtillity.NullReplace(SVTData.get("COLLATERAL_CODE"))+"</Collateral_Code>" + 
						"            <Collateral_Group>01RES</Collateral_Group>" + 
						"            <Collateral_Value>"+commonUtility.millionString(SVTData.get("VALUE_OF_SUB_TYPE_SEC"))+"</Collateral_Value>" + 
						"            <DueDate_For_Visit>"+ commonUtility.rotateDate(SVTData.get("REVIEW_DATE"))+"</DueDate_For_Visit>" + 
						"            <Due_Date>"+ commonUtility.rotateDate(SVTData.get("REVIEW_DATE"))+"</Due_Date>" + 
						"            <From_Deried_Value>A</From_Deried_Value>" + 
						"            <Inspected_Value>"+apportionedValueInString+"</Inspected_Value>" + 
						"            <Inspection_Type>L&amp;B</Inspection_Type>" + 
						"            <Last_Valuation_Date>"+commonUtility.changedDateFormat(OperationUtillity.NullReplace(SVTData.get("VALUATION_DATE")))+"</Last_Valuation_Date>" + 
						"            <Nature_Of_Charge>"+OperationUtillity.NullReplace(SVTData.get("REF_CODE")) +"</Nature_Of_Charge>" + 
						"            <Notes>"+pinstId+"</Notes>" + 
						"            <Particular_AddressLine1>"+commonUtility.replaceAnd(commonUtility.removeSpecialCharacters(addressLine1))+"</Particular_AddressLine1>" + 
						"            <Particular_AddressLine2>"+commonUtility.replaceAnd(commonUtility.removeSpecialCharacters(addressLine2))+"</Particular_AddressLine2>" + 
						"            <Particular_City>"+cityCode+"</Particular_City>" + 
						"            <Particular_Notes>"+pinstId+"</Particular_Notes>" + 
						"            <Particular_PostalCode>"+postalCode+"</Particular_PostalCode>" + 
						"            <Particular_State>"+stateCode+"</Particular_State>" + 
						"            <Postal_Code>"+postalCode+"</Postal_Code>" + 
						"            <Property_Doc_No>"+OperationUtillity.NullReplace(SVTData.get("UCC_BASED_CUST_ID"))+"</Property_Doc_No>" + 
						"            <Property_Owner>"+commonUtility.replaceAnd(commonUtility.removeSpecialCharacters(SVTData.get("PROPERTY_OWNER")))+"</Property_Owner>" + 
						"            <Receipt_Date>"+commonUtility.customDateFormat()+"</Receipt_Date>" + 
						"            <Received_Date>"+commonUtility.customDateFormat()+"</Received_Date>" + 
						"            <Registration_Auth>SUB REGISTER</Registration_Auth>" + 
						"            <Registration_Date>"+commonUtility.customDateFormat()+"</Registration_Date>" + 
						"            <Review_Date>"+commonUtility.rotateDate(SVTData.get("REVIEW_DATE"))+"</Review_Date>" + 
						"            <State>"+stateCode+"</State>" + 
						"            <Visit_Date>"+commonUtility.rotateDate(SVTData.get("REVIEW_DATE"))+"</Visit_Date>" + 
						"         </executeFinacleScript_CustomData>" + 
						"      </executeFinacleScriptRequest>" + 
						"   </Body></FIXML>]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";			
			}
		InputStream is = new ByteArrayInputStream(soapMessage.getBytes());
		SOAPMessage requestSoap = MessageFactory.newInstance().createMessage(null, is);
		requestSoap.saveChanges();
		return requestSoap;
	}
}
