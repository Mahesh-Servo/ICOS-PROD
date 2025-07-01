package com.LsmFiServices.FiLsmService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.RestAPIUtility;
import com.LsmFiServices.Utility.ServiceDetails;
import com.LsmFiServices.Utility.commonUtility;
import com.LsmFiServices.Utility.xmlToMap;
import com.LsmFiServices.pojo.serviceexecutiondetails.ServiceDetailsUpdatePojo;

@Service
public class DrawingPowerCheckService {

    private static final Logger logger = LoggerFactory.getLogger(DrawingPowerCheckService.class);
    private RestAPIUtility restUtils;
    private DrawingPowerUpdationService drawingPowerUpdationService;
    private ServiceDetails serviceDetails;

    public DrawingPowerCheckService(RestAPIUtility restUtils, DrawingPowerUpdationService drawingPowerUpdationService,ServiceDetails serviceDetails) {
	this.restUtils = restUtils;
	this.drawingPowerUpdationService = drawingPowerUpdationService;
	this.serviceDetails = serviceDetails;
    }

    public String executeDrawingPowerCheck(String pinstId, String accountNumber) throws SQLException, Exception {
	Map<String, String> responseMap = null;
	ServiceDetailsUpdatePojo pojo = new ServiceDetailsUpdatePojo();
	String status ="";
	String serviceName = "DRAWING POWER CHECK";
	String requestType = "DRAWING POWER CHECK :: ACCOUNT NUMBER :: " + accountNumber;
	try {
	    String requestFromICose = prepareRequest(accountNumber);
	    pojo.setPinstId(pinstId);
	    pojo.setServiceName(serviceName);
	    pojo.setAccountNumber(accountNumber);
	    pojo.setRequestType(requestType);
	    pojo.setStatus("Request Sent...!");
	    pojo.setReTrigger(true);
	    serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
	    logger.info("Request from ICose: {}", requestFromICose);
	    String responseFromFinacle = restUtils.getResponseFromFinacle(requestFromICose);
	    logger.info("Response from Finacle: {}", responseFromFinacle);
	    status = commonUtility.getStatus(responseFromFinacle);
	    logger.info("Status: {}", status);
	    if ("SUCCESS".equalsIgnoreCase(status)) {
		pojo.setReTrigger(false);
		pojo.setStatus(status);
		serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
		responseMap = xmlToMap.successMapForDrgPwrChk(pinstId, responseFromFinacle);
		logger.info("successMapForDrgPwrChk map {} :: ", responseMap);
		// SN OF MAHESHV ON 10122024
		//drawingPowerUpdationService.executeDrawingPowerUpdationService(pinstId, responseMap);
		OperationUtillity.insertForDPCheckService(pinstId,accountNumber,responseMap.get("Drawing_Power"),status);
		// EN OF MAHESHV ON 10122024
	    } else {
		pojo.setReTrigger(true);
		pojo.setStatus(status);
		serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
		responseMap = xmlToMap.packetDataToMap(pinstId, responseFromFinacle);
		logger.info("FailureMapForDrgPwrChk map --> :: ", responseMap);
		OperationUtillity.insertForDPCheckService(pinstId,accountNumber,"",status);
	    }
	    OperationUtillity.API_RequestResponse_Insert(requestFromICose, responseFromFinacle, requestType, pinstId,responseMap, commonUtility.createRequestUUID());
	} catch (Exception e) {
	    logger.error("DrawingPowerCheckService.executeDrawingPowerCheck(){}",OperationUtillity.traceException(e));
	    OperationUtillity.insertForDPCheckService(pinstId,accountNumber,"","FAILED");
	}
	return status;
    }

    public String prepareRequest(String accountNumber) {
	return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\">" + 
		"    <soapenv:Header/>" + 
		"    <soapenv:Body>" + 
		"        <web:executeService>" + 
		"            <arg_0_0>" + 
		"                <![CDATA[" + 
		"					<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + 
		"						<Header>" + 
		"							<RequestHeader>" + 
		"								<MessageKey>" + 
		"									<RequestUUID>"+commonUtility.createRequestUUID()+"</RequestUUID>" + 
		"									<ServiceRequestId>executeFinacleScript</ServiceRequestId>" + 
		"									<ServiceRequestVersion>10.2</ServiceRequestVersion>" + 
		"									<ChannelId>CLS</ChannelId>" + 
		"									<LanguageId></LanguageId>" + 
		"								</MessageKey>" + 
		"								<RequestMessageInfo>" + 
		"									<BankId>BM3</BankId>" + 
		"									<TimeZone></TimeZone>" + 
		"									<EntityId></EntityId>" + 
		"									<EntityType></EntityType>" + 
		"									<ArmCorrelationId></ArmCorrelationId>" + 
		"									<MessageDateTime>"+LocalDateTime.now()+"</MessageDateTime>" + 
		"								</RequestMessageInfo>" + 
		"								<Security>" + 
		"									<Token>" + 
		"										<PasswordToken>" + 
		"											<UserId></UserId>" + 
		"											<Password></Password>" + 
		"										</PasswordToken>" + 
		"									</Token>" + 
		"									<FICertToken></FICertToken>" + 
		"									<RealUserLoginSessionId></RealUserLoginSessionId>" + 
		"									<RealUser></RealUser>" + 
		"									<RealUserPwd></RealUserPwd>" + 
		"									<SSOTransferToken></SSOTransferToken>" + 
		"								</Security>" + 
		"							</RequestHeader>" + 
		"						</Header>" + 
		"						<Body>" + 
		"							<executeFinacleScriptRequest>" + 
		"								<ExecuteFinacleScriptInputVO>" + 
		"									<requestId>FI_CMART_SanctionDrawingInq.scr</requestId>" + 
		"								</ExecuteFinacleScriptInputVO>" + 
		"								<executeFinacleScript_CustomData>" + 
		"									<Account_No>"+accountNumber+"</Account_No>" + 
		"								</executeFinacleScript_CustomData>" + 
		"							</executeFinacleScriptRequest>" + 
		"						</Body>" + 
		"					</FIXML>]]>" + 
		"            </arg_0_0>" + 
		"        </web:executeService>" + 
		"    </soapenv:Body>" + 
		"</soapenv:Envelope>";
    }    
}
