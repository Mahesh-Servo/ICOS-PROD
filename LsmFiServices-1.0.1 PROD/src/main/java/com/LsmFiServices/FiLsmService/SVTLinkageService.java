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
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.ServiceDetails;
import com.LsmFiServices.Utility.commonUtility;
import com.LsmFiServices.Utility.xmlToMap;
import com.LsmFiServices.pojo.serviceexecutiondetails.ServiceDetailsUpdatePojo;

@SuppressWarnings("deprecation")
@Service
public class SVTLinkageService {
	private static final Logger logger = LoggerFactory.getLogger(SVTLinkageService.class);

	@Autowired
	private ServiceDetails serviceDetails;

	public  Map<String, String>  svtLinkageService(String pinstId, Map<String, String> svtLinkageServiceMap) throws SOAPException, SQLException {

		logger.info("Entering into svtLinkageService():: " + pinstId +" and input Map -->"+svtLinkageServiceMap);
		
		Map<String, String> svtLinkageServiceResponseMap = new HashMap<>();
		String svtLinkageServiceResponse = "";
		String RequestUUID = "", HostTransaction = "", Status = "";
		ServiceDetailsUpdatePojo pojo = new ServiceDetailsUpdatePojo();
		String requestType = "SVTLinkageService : "+ svtLinkageServiceMap.get("SECURITY_NAME") + " : "+ svtLinkageServiceMap.get("SECURITY_TYPE") + " : " +svtLinkageServiceMap.get("PRODUCT")+ " ::: SUB_TYPE_SECURITY - "+ svtLinkageServiceMap.get("SUB_TYPE_SECURITY_SVT");
		logger.info("Request Type check Linkage {}",requestType);
		try {
			pojo.setPinstId(pinstId);		
			pojo.setServiceName("SVT LINKAGE");
			pojo.setRequestType(requestType);
			 pojo.setFacility(requestType);
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			SOAPMessage soapRequest = createSOAPRequest(pinstId, svtLinkageServiceMap);
			pojo.setServiceRequest(OperationUtillity.soapMessageToString(soapRequest));
			pojo.setStatus("Request Sent...!");
			pojo.setReTrigger(true);
			serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			SOAPMessage soapResponse = soapConnection.call(soapRequest, Constants.FIWebService);
			svtLinkageServiceResponse = OperationUtillity.soapMessageToString(soapResponse);
			svtLinkageServiceResponse = StringEscapeUtils.unescapeXml(svtLinkageServiceResponse);
				if (svtLinkageServiceResponse.contains("<HostTransaction>")) {
					HostTransaction = svtLinkageServiceResponse.substring(svtLinkageServiceResponse.indexOf("<HostTransaction>") + "<HostTransaction>".length(),svtLinkageServiceResponse.indexOf("</HostTransaction>"));
					if (HostTransaction.contains("<Status>")) {
						Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),HostTransaction.indexOf("</Status>"));
					}
				}
				if (Status.equalsIgnoreCase("SUCCESS")) {
					svtLinkageServiceResponseMap = xmlToMap.svtLinkageServiceSuccessMap(pinstId, svtLinkageServiceResponse);
					pojo.setStatus(svtLinkageServiceResponseMap.get("Status").toString());
					pojo.setServiceResponse(svtLinkageServiceResponse);
					pojo.setReTrigger(false);
					serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
				} else {
					svtLinkageServiceResponseMap = xmlToMap.packetDataToMap(pinstId, svtLinkageServiceResponse);
					pojo.setStatus(svtLinkageServiceResponseMap.get("Status"));
					pojo.setMessage(svtLinkageServiceResponseMap.get("ErrorDesc"));
					pojo.setServiceResponse(svtLinkageServiceResponse);
					pojo.setReTrigger(true);
					serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
				}
				OperationUtillity.API_RequestResponse_Insert(OperationUtillity.soapMessageToString(soapRequest),svtLinkageServiceResponse,requestType,	pinstId, svtLinkageServiceResponseMap, RequestUUID);
			} catch (Exception e) {
				svtLinkageServiceResponseMap.put("RequestUUID", "");
				svtLinkageServiceResponseMap.put("MessageDateTime", svtLinkageServiceResponseMap.get("MessageDateTime"));
				svtLinkageServiceResponseMap.put("Error_Code", "500");
				svtLinkageServiceResponseMap.put("message", e.getMessage());
				svtLinkageServiceResponseMap.put("Status", "FAILED");
				logger.error("SVTLinkageService.svtLinkageService()-> "+ OperationUtillity.traceException(e));
			}
		return svtLinkageServiceResponseMap ;
	}

	public  SOAPMessage createSOAPRequest(String pinstId, Map<String, String> SVTData)
			throws IOException, SOAPException, ParseException {

		logger.info("Entering into svtLinkageService.createSOAPRequest : : " + pinstId+"  and map -->"+SVTData);
/*
		//for executing account number-wise
		String soapMessage = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
				"<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + 
				"   <Body>" + 
				"      <executeFinacleScriptRequest>" + 
				"         <executeFinacleScript_CustomData>" + 
				"            <Account_Number>"+ OperationUtillity.NullReplace(SVTData.get("ACCOUNT_NUMBER")) +"</Account_Number> " +  // Facility Wise Same as Account Linkage
				"            <Apportioned_Value>"+ commonUtility.millionString(OperationUtillity.NullReplace(SVTData.get("PRODUCT_AMOUNT"))) + "</Apportioned_Value>  " + 
				"            <Collateral_Id>"+ OperationUtillity.NullReplace(SVTData.get("COLLATERAL_ID"))+"</Collateral_Id>" + 
				"            <Prim_Secndry_Code>C</Prim_Secndry_Code> " +//Hard code C 
				"         </executeFinacleScript_CustomData>" + 
				"         <ExecuteFinacleScriptInputVO>" + 
				"            <requestId>FI_CLS_LinkCollateral.scr</requestId>" + 
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
*/
		String soapMessage = 		"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
			"<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.finacle.com/fixml ColtrlLinkAdd.xsd\">" + 
			"   <Header>" + 
			"      <RequestHeader>" + 
			"         <MessageKey>" + 
			"            <RequestUUID>"+commonUtility.createRequestUUID()+"</RequestUUID>" + 
			"            <ServiceRequestId>ColtrlLinkAdd</ServiceRequestId>" + 
			"            <ServiceRequestVersion>10.2</ServiceRequestVersion>" + 
			"            <ChannelId>CLS</ChannelId>" + 
			"            <LanguageId />" + 
			"         </MessageKey>" + 
			"         <RequestMessageInfo>" + 
			"            <BankId>BM3</BankId>" + 
			"            <TimeZone />" + 
			"            <EntityId />" + 
			"            <EntityType />" + 
			"            <ArmCorrelationId />" + 
			"            <MessageDateTime>"+LocalDateTime.now()+"</MessageDateTime>" + 
			"         </RequestMessageInfo>" + 
			"         <Security>" + 
			"            <Token>" + 
			"               <PasswordToken>" + 
			"                  <UserId />" + 
			"                  <Password />" + 
			"               </PasswordToken>" + 
			"            </Token>" + 
			"            <FICertToken />" + 
			"            <RealUserLoginSessionId />" + 
			"            <RealUser />" + 
			"            <RealUserPwd />" + 
			"            <SSOTransferToken />" + 
			"         </Security>" + 
			"      </RequestHeader>" + 
			"   </Header>" + 
			"   <Body>" + 
			"      <ColtrlLinkAddRequest>" + 
			"         <ColtrlLinkAddRq>" + 
			"            <ColtrlLinkageType>N</ColtrlLinkageType>	" + 
			"            <LimitNodeId>" + 
			"               <LimitPrefix>"+OperationUtillity.NullReplace(SVTData.get("PRODUCT_PREFIX"))+"</LimitPrefix>	" + 
			"               <LimitSuffix>"+OperationUtillity.NullReplace(SVTData.get("PRODUCT_SUFFIX"))+"</LimitSuffix>		" + 
			"            </LimitNodeId>" + 
			"            <ColtrlId>"+OperationUtillity.NullReplace(SVTData.get("COLLATERAL_ID"))+"</ColtrlId>		" + 
			"            <ApportionedAmt>" + 
			"               <amountValue>"+commonUtility.millionString(SVTData.get("PRODUCT_AMOUNT"))+"</amountValue>		" + 
			"               <currencyCode>INR</currencyCode>		" + 
			"            </ApportionedAmt>" + 
			"            <ColtrlNatureInd>P</ColtrlNatureInd>	" + 
			"            <MarginPcnt>" + 
			"			<value></value>" + 
			"		</MarginPcnt>" + 
			"		<LoanToValuePcnt>" + 
			"			<value></value>" + 
			"		</LoanToValuePcnt>" + 
			"         </ColtrlLinkAddRq>" + 
			"      </ColtrlLinkAddRequest>" + 
			"   </Body>" + 
			"</FIXML>" + 
			"]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
		
		InputStream is = new ByteArrayInputStream(soapMessage.getBytes());
		SOAPMessage requestSoap = MessageFactory.newInstance().createMessage(null, is);
		requestSoap.saveChanges();
		return requestSoap;
	}
}
