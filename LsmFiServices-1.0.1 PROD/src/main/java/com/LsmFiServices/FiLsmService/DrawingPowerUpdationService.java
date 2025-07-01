package com.LsmFiServices.FiLsmService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
public class DrawingPowerUpdationService {
    private static final Logger logger = LoggerFactory.getLogger(DrawingPowerUpdationService.class);
    private RestAPIUtility restUtils;
    private ServiceDetails serviceDetails;

    public DrawingPowerUpdationService(RestAPIUtility restUtils, ServiceDetails serviceDetails) {
	this.restUtils = restUtils;
	this.serviceDetails = serviceDetails;
    }

    public String executeDrawingPowerUpdationService(String pinstId, Map<String,String> inputMap)  throws SQLException, Exception {
	Map<String, String> responseMap = null;
	ServiceDetailsUpdatePojo pojo = new ServiceDetailsUpdatePojo();
	String serviceName = "DRAWING POWER UPDATE";
	String accountNumber = inputMap.get("Account_Number");
	String requestType = "DRAWING POWER UPDATE :: ACCOUNT NUMBER :: " + accountNumber;
	String requestFromICose = prepareRequest(inputMap);
	pojo.setPinstId(pinstId);
	pojo.setServiceName(serviceName);
	pojo.setAccountNumber(accountNumber);
	pojo.setRequestType(requestType);
	pojo.setStatus("Request Sent...!");
	pojo.setReTrigger(true);
	String responseFromFinacle = restUtils.getResponseFromFinacle(requestFromICose);
	logger.info("executeDrawingPowerUpdationService responseFromFinacle:: {} ", responseFromFinacle);
	String status = commonUtility.getStatus(responseFromFinacle);
	logger.info("executeDrawingPowerUpdationService() {} ", status);
	if ("SUCCESS".equalsIgnoreCase(status)) {
	    pojo.setReTrigger(false);
	    pojo.setStatus(status);
	    serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
	    responseMap = xmlToMap.successMapForDrgPwrUpdate(pinstId, responseFromFinacle);
	    logger.info("executeDrawingPowerUpdationService map {} ", responseMap);
	} else {
	    pojo.setReTrigger(true);
	    pojo.setStatus(status);
	    serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
	    responseMap = xmlToMap.packetDataToMap(pinstId, responseFromFinacle);
	    logger.info("successMapForDrgPwrChk map {} :: ", responseMap);
	}
	
	OperationUtillity.API_RequestResponse_Insert(requestFromICose, responseFromFinacle,requestType, pinstId, responseMap, commonUtility.createRequestUUID());
	logger.info("responseMap check :: "+accountNumber,responseMap);
	logger.info("DrawingPowerUpdationService.executeDrawingPowerUpdationService(){}",status);
	return status;
    }
   public String prepareRequest(Map<String,String> inputMap) {
	DateTimeFormatter formater = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//	System.out.println("hsjka   ::m  "+LocalDate.now().format(formater));
	return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\">" + 
		"    <soapenv:Header/>" + 
		"    <soapenv:Body>" + 
		"        <web:executeService>" + 
		"            <arg_0_0>" + 
		"                <![CDATA[" + 
		"					<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + 
		"					<Header>" + 
		"						<RequestHeader>" + 
		"							<MessageKey>" + 
		"								<RequestUUID>"+commonUtility.createRequestUUID()+"</RequestUUID>" + 
		"								<ServiceRequestId>executeFinacleScript</ServiceRequestId>" + 
		"								<ServiceRequestVersion>10.2</ServiceRequestVersion>" + 
		"								<ChannelId>CLS</ChannelId>" + 
		"								<LanguageId></LanguageId>" + 
		"							</MessageKey>" + 
		"							<RequestMessageInfo>" + 
		"								<BankId>BM3</BankId>" + 
		"								<TimeZone></TimeZone>" + 
		"								<EntityId></EntityId>" + 
		"								<EntityType></EntityType>" + 
		"								<ArmCorrelationId></ArmCorrelationId>" + 
		"								<MessageDateTime>"+LocalDateTime.now()+"</MessageDateTime>" + 
		"							</RequestMessageInfo>" + 
		"							<Security>" + 
		"								<Token>" + 
		"									<PasswordToken>" + 
		"										<UserId></UserId>" + 
		"										<Password></Password>" + 
		"									</PasswordToken>" + 
		"								</Token>" + 
		"								<FICertToken></FICertToken>" + 
		"								<RealUserLoginSessionId></RealUserLoginSessionId>" + 
		"								<RealUser></RealUser>" + 
		"								<RealUserPwd></RealUserPwd>" + 
		"								<SSOTransferToken></SSOTransferToken>" + 
		"							</Security>" + 
		"						</RequestHeader>" + 
		"					</Header>" + 
		"					<Body>" + 
		"						<executeFinacleScriptRequest>" + 
		"							<ExecuteFinacleScriptInputVO>" + 
		"								<requestId>FI_CMART_DrawingPower.scr</requestId>" + 
		"							</ExecuteFinacleScriptInputVO>" + 
		"							<executeFinacleScript_CustomData>" + 
		"								<applDate>"+LocalDate.now().format(formater)+"</applDate>" + 
		"								<drwngPower>"+inputMap.get("Drawing_Power")+"</drwngPower>" + 
		"								<notes>drawing power test</notes>" + 
		"								<accId>"+inputMap.get("Account_Number")+"</accId>" + 
		"							</executeFinacleScript_CustomData>" + 
		"						</executeFinacleScriptRequest>" + 
		"					</Body>" + 
		"				</FIXML>]]>" + 
		"            </arg_0_0>" + 
		"        </web:executeService>" + 
		"    </soapenv:Body>" + 
		"</soapenv:Envelope>";
    }
}
