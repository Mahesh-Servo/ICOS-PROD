package com.LsmFiServices.FiLsmService;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.validation.constraints.NotNull;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import com.LsmFiServices.Utility.DBConnect;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.SOAPRequestUtility;
import com.LsmFiServices.Utility.ServiceDetails;
import com.LsmFiServices.Utility.commonUtility;
import com.LsmFiServices.Utility.xmlToMap;
import com.LsmFiServices.pojo.serviceexecutiondetails.ServiceDetailsUpdatePojo;
import com.fasterxml.jackson.databind.ObjectMapper;

import oracle.jdbc.oracore.OracleType;

@Service
public class statusUpdateService {
    
    @NotNull
	private Object objectValue = null;
    
    @Autowired
	private ServiceDetails serviceDetails;
    
    private static final Logger logger =  LoggerFactory.getLogger(statusUpdateService.class);
    
    public @ResponseBody Map<String, Object> executeStatusCode_UpdationService(String pinstId, String Status_code,Map<String,String> mapData) throws SQLException {

	logger.info("statusUpdateService.executeStatusCode_UpdationService():: " + pinstId+" and Status_code-->[" + Status_code + "] and Map -> "+ mapData);

	String Status = "";
	Map<String, Object> statusUpdationMap = new ConcurrentHashMap<>();
	Map<String, String> API_REQ_RES_map = new LinkedHashMap<>();
	ServiceDetailsUpdatePojo pojo = new ServiceDetailsUpdatePojo();
	String requestType = "",HostTransaction= "";
	int count_sc =0;
	String CallProc = "";
	String OutPut_SC = "";
	String Error_MSg = "";
	try {
	         pojo.setPinstId(pinstId);		
	         pojo.setServiceName("STATUS CODE UPDATION");
	         pojo.setAccountNumber("");
	         pojo.setFacility("");
	         
	         
	     //    if(count_sc == 0) {
	             
	             CallProc = " {call SP_GETPROPOSEDTOT_STATUSCODE(?,?,?)}";
	             
	             try(Connection con = DBConnect.getConnection(); 
	        	     CallableStatement cst = con.prepareCall(CallProc);) {
	        	 cst.setString(1, pinstId);
	        	 cst.registerOutParameter(2, Types.NVARCHAR);
	        	 cst.registerOutParameter(3, Types.NVARCHAR);
                         cst.execute();	           
                         OutPut_SC =  cst.getString(2);
                         Error_MSg =  cst.getString(3);
                         logger.info("statusUpdateService.executeStatusCode_UpdationService():: Data From Procedure :: Error_MSg [" + Error_MSg  +"] and OutPut_SC --> [" + OutPut_SC + "]");
	             } catch (Exception e) {
	        	 logger.info("statusUpdateService.executeStatusCode_UpdationService() Error while Executing Procedure  -->"+ OperationUtillity.traceException(e));
	             }
	             
	        if("OK".equalsIgnoreCase(Error_MSg)) {
		String statusCodeUpdationSoapRequest = createSOAPRequest(pinstId,OutPut_SC,mapData);
		logger.info("statusUpdateService.executeStatusCode_UpdationService() statusCodeUpdationSoapRequest  -->"+ statusCodeUpdationSoapRequest);
		
	        pojo.setServiceRequest(statusCodeUpdationSoapRequest);
	        pojo.setStatus("Request Sent...!");
	        pojo.setReTrigger(true);
		serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
		String statusUpdationSoapResponse = SOAPRequestUtility.soapResponse(statusCodeUpdationSoapRequest);
		logger.info("statusUpdateService.executeStatusCode_UpdationService() statusUpdationSoapResponse  ->"+statusUpdationSoapResponse);
		if (statusUpdationSoapResponse.contains("<HostTransaction>")) {
			HostTransaction = statusUpdationSoapResponse.substring(statusUpdationSoapResponse.indexOf("<HostTransaction>") + "<HostTransaction>".length(),statusUpdationSoapResponse.indexOf("</HostTransaction>"));
			if (statusUpdationSoapResponse.contains("<Status>")) {
				Status = statusUpdationSoapResponse.substring(statusUpdationSoapResponse.indexOf("<Status>") + "<Status>".length(),statusUpdationSoapResponse.indexOf("</Status>"));
			}
		}
			
		if (Status.equalsIgnoreCase("SUCCESS")) {
			API_REQ_RES_map = xmlToMap.successPacketDataToMapStatusCodeUpdation(pinstId, statusUpdationSoapResponse);
		} else {
			API_REQ_RES_map = xmlToMap.packetDataToMap(pinstId, statusUpdationSoapResponse);

		}
		String result ="";
		try {
			OperationUtillity.API_RequestResponse_Insert(statusCodeUpdationSoapRequest,statusUpdationSoapResponse, "STATUS CODE UPDATION", pinstId, API_REQ_RES_map, API_REQ_RES_map.get("RequestUUID"));
			result = new ObjectMapper().writeValueAsString(API_REQ_RES_map);
		} catch (Exception e) {
			logger.info("accountEnquiryController.accountEnquiry() Exception ->"+OperationUtillity.traceException(e));
		}
		
	      } // Status Code Service Procedure OK Block
	        
	        

			
	} catch (Exception e) {
		logger.error("URCCUpdationService.executeURCC_UpdationService()-> "+ OperationUtillity.traceException(e));
	}
//	}
	logger.info("statusUpdateService.executeStatusCode_UpdationService() returning statusUpdationMap -->"+ statusUpdationMap);
	return statusUpdationMap;
}

//public String createSOAPRequest(String pinstId,String operation,Map<String,String> inputMap) throws IOException, SOAPException, ParseException {
    public String createSOAPRequest(String pinstId,String status_code,Map<String,String> mapData)   throws IOException, SOAPException, ParseException {
	return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.finacle.com/fixml updateCorpCustomer.xsd\">" +//<?xml version =\"1.0\" encoding = \"UTF-8\"?> 
		"        <Header>" + 
		"            <RequestHeader>" + 
		"                <MessageKey>" + 
		"                    <RequestUUID>"+commonUtility.createRequestUUID()+"</RequestUUID>" +
		"                    <ServiceRequestId>executeFinacleScript</ServiceRequestId>" +
		"                    <ServiceRequestVersion>10.2</ServiceRequestVersion>" +
		"                    <ChannelId>CLS</ChannelId>" + 
		"                    <LanguageId></LanguageId>" +    
		"                </MessageKey>" + 
		"                <RequestMessageInfo>" + 
		"                    <BankId>BM3</BankId>" +
		"                    <TimeZone></TimeZone>" +
		"                    <EntityId></EntityId>" +
		"                    <EntityType></EntityType>" +  
		"                    <ArmCorrelationId></ArmCorrelationId>" + 
	        "                    <MessageDateTime>"+LocalDateTime.now()+"</MessageDateTime>" + 
		"                </RequestMessageInfo>" + 
		"                <Security>" + 
		"                    <Token>" + 
		"                        <PasswordToken>" + 
		"                            <UserId></UserId>" +
		"                            <Password></Password>" +  
		"                        </PasswordToken>" + 
		"                    </Token>" +
		"                    <FICertToken></FICertToken>" + 
		"                    <RealUserLoginSessionId></RealUserLoginSessionId>" +
		"                    <RealUser></RealUser>" + 
		"                    <RealUserPwd></RealUserPwd>" +
		"                    <SSOTransferToken></SSOTransferToken>" + 
		"                   </Security>" + 
		"            </RequestHeader>" + 
		"        </Header>" + 
		"        <Body>" + 
		  "            <executeFinacleScriptRequest>" + 
		  "            <executeFinacleScriptInputVO>" + 
		  "             <requestId>FI_cmn_demogUpdate.scr</requestId>" + 
                    "            </executeFinacleScriptInputVO>" + 	
		  "            <executeFinacleScript_CustomData>" + 
		  "                <Customer_Id>"+mapData.get("Customer_Id")+"</Customer_Id>" + 
		  "                <Status_Code>"+status_code+"</Status_Code>" +
		  "                <constCode>"+mapData.get("Constitution_Code")+"</constCode>" +
		  "                <Channel_Id>CLS<user_id></user_id></Channel_Id>" +
		  "            </executeFinacleScript_CustomData>" + 
                    "            </executeFinacleScriptRequest>" + 
		    "        </Body>" + 
			"    </FIXML>]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";			
}

}
