package com.LsmFiServices.FiLsmService;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.constraints.NotNull;
import javax.xml.soap.SOAPException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import com.LsmFiServices.Utility.LEI_URCCNumberEnquiryUtility;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.SOAPRequestUtility;
import com.LsmFiServices.Utility.ServiceDetails;
import com.LsmFiServices.Utility.commonUtility;
import com.LsmFiServices.Utility.xmlToMap;
import com.LsmFiServices.pojo.serviceexecutiondetails.ServiceDetailsUpdatePojo;


@Service
public class URCCUpdationService {
	
	@Autowired
	private LEI_URCCNumberEnquiryUtility urccUtility;
	
	@Autowired
	private leiUrcVerificationService verifactionDaoLayer;
	
	@NotNull
	private Object objectValue = null;
	
	@Autowired
	private ServiceDetails serviceDetails;
	
	private static final Logger logger =  LoggerFactory.getLogger(URCCUpdationService.class);

	public @ResponseBody Map<String, Object> executeURCC_UpdationService(String pinstId, String operation,
			Map<String, String> urccInputDataMap) throws SQLException {

		logger.info("URCCUpdationService.executeURCC_UpdationService():: " + pinstId + " and urccInputDataMap-->"
				+ urccInputDataMap);
		String HostTransaction;
		String Status = "";
		Map<String, Object> urccUpdationMap = new ConcurrentHashMap<>();
		ServiceDetailsUpdatePojo pojo = new ServiceDetailsUpdatePojo();
		String requestType = "";
		try {
			pojo.setPinstId(pinstId);
			pojo.setServiceName("URCC NUMBER");
			pojo.setAccountNumber(urccInputDataMap.get("URCC_NUMBER"));
			pojo.setFacility(urccInputDataMap.get("CUSTID"));
			String urccUpdationSoapRequest = createSOAPRequest(pinstId, operation, urccInputDataMap);
			pojo.setServiceRequest(urccUpdationSoapRequest);
			pojo.setStatus("Request Sent...!");
			pojo.setReTrigger(true);
			serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			String urccUpdationSoapResponse = SOAPRequestUtility.soapResponse(urccUpdationSoapRequest);
			if (urccUpdationSoapResponse.contains("<HostTransaction>")) {
				HostTransaction = urccUpdationSoapResponse.substring(
						urccUpdationSoapResponse.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
						urccUpdationSoapResponse.indexOf("</HostTransaction>"));
				if (HostTransaction.contains("<Status>")) {
					Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),
							HostTransaction.indexOf("</Status>"));
				}
			}
			if (Status.equalsIgnoreCase("SUCCESS")) {
				urccUpdationMap = urccUtility.successPacketDataLEINumber(pinstId, urccUpdationSoapResponse);
				pojo.setStatus(urccUpdationMap.get("Status").toString());
				pojo.setServiceResponse(urccUpdationSoapResponse);
				pojo.setReTrigger(false);
				serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			} else {
				urccUpdationMap.putAll(xmlToMap.packetDataToMap(pinstId, urccUpdationSoapResponse));
				pojo.setStatus(urccUpdationMap.get("Status").toString());
				pojo.setMessage(urccUpdationMap.get("ErrorDesc").toString());
				pojo.setServiceResponse(urccUpdationSoapResponse);
				pojo.setReTrigger(true);
				serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			}

			WeakHashMap<String, String> convertedMap = new WeakHashMap<>();
			try {
				for (Map.Entry<String, Object> obj : urccUpdationMap.entrySet()) {
					objectValue = obj.getValue().toString();
					if (objectValue != null) {
						convertedMap.put(obj.getKey(), obj.getValue().toString());
						objectValue = null;
					}
				}
			} catch (Exception e) {
				logger.info("executeURCC_UpdationService.excetpion " + OperationUtillity.traceException(e));
			}

			logger.info("Before executing verication and after success of urcc add/update service");
			requestType = getRequestType(operation, urccInputDataMap);
//				requestType = "URCC NUMBER ADD SERVICE, CUST_ID :: "+urccInputDataMap.get("CUSTID")+", REFERENCE_NUMBER :: "+urccInputDataMap.get("URCC_NUMBER")
			urccUtility.API_RequestResponse_Insert(urccUpdationSoapRequest, urccUpdationSoapResponse, requestType,
					pinstId, convertedMap, "");
			if (Status.equalsIgnoreCase("SUCCESS")) {
				verifactionDaoLayer.executeLeiUrcVrfctnService(pinstId, urccInputDataMap.get("CUSTID"),
						urccInputDataMap.get("REFERENCE_NUMBER_URCC"), "URCC VERIFICATION");
			}
			pojo.setRequestType(requestType);
			serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
		} catch (Exception e) {
			logger.error("URCCUpdationService.executeURCC_UpdationService()-> " + OperationUtillity.traceException(e));
			pojo.setRequestType(getRequestType(operation, urccInputDataMap));
			serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
		}
//		}
		logger.info(
				"URCCUpdationService.executeURCC_UpdationService() returning urccUpdationMap -->" + urccUpdationMap);
		return urccUpdationMap;
	}
	
	public String getRequestType(String operation, Map<String,String> inputMap) {
		String requestType = "";
		if(operation.equalsIgnoreCase("ADD")) {
			requestType = "URCC NUMBER ADD SERVICE, CUST_ID :: "+inputMap.get("CUSTID")+", REFERENCE_NUMBER :: "+inputMap.get("URCC_NUMBER");
		}else {					
			requestType = "URCC NUMBER UPDATE SERVICE, CUST_ID :: "+inputMap.get("CUSTID")+", REFERENCE_NUMBER :: "+inputMap.get("REFERENCE_NUMBER_URCC");
		}
		return requestType;
	}
	
	public String createSOAPRequest(String pinstId,String operation,Map<String,String> inputMap) throws IOException, SOAPException, ParseException {
		
		return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.finacle.com/fixml updateCorpCustomer.xsd\">" + 
				"        <Header>" + 
				"            <RequestHeader>" + 
				"                <MessageKey>" + 
				"                    <RequestUUID>"+commonUtility.createRequestUUID()+"</RequestUUID>" + 
				"                    <ServiceRequestId>updateCorpCustomer</ServiceRequestId>" + 
				"                    <ServiceRequestVersion>10.2</ServiceRequestVersion>" + 
				"                    <ChannelId>CLS</ChannelId>" + 
				"                    <LanguageId/>" + 
				"                </MessageKey>" + 
				"                <RequestMessageInfo>" + 
				"                    <BankId>BM3</BankId>" + 
				"                    <TimeZone/>" + 
				"                    <EntityId/>" + 
				"                    <EntityType/>" + 
				"                    <ArmCorrelationId/>" + 
				"                    <MessageDateTime>"+LocalDateTime.now()+"</MessageDateTime>" + 
				"                </RequestMessageInfo>" + 
				"                <Security>" + 
				"                    <Token>" + 
				"                        <PasswordToken>" + 
				"                            <UserId/>" + 
				"                            <Password/>" + 
				"                        </PasswordToken>" + 
				"                    </Token>" + 
				"                    <FICertToken/>" + 
				"                    <RealUserLoginSessionId/>" + 
				"                    <RealUser/>" + 
				"                    <RealUserPwd/>" + 
				"                    <SSOTransferToken/>" + 
				"                </Security>" + 
				"            </RequestHeader>" + 
				"        </Header>" + 
				"        <Body>" + 
				"            <updateCorpCustomerRequest>" + 
				"                <CorpCustomerModData>" + 
				"                    <mainData>" + 
				"                        <corpCustomerModCorporateData>" + 
				"                            <corp_key>"+inputMap.get("CUSTID")+"</corp_key> " + 
				"                        </corpCustomerModCorporateData>" + 
				"                    </mainData>" + 
				"                    <relatedData>" + 
				"                      <corpentitydocumentModData>" + 
				"                            <doccode>" + ("Y".equals(inputMap.get("IS_URCC")) ? inputMap.get("DOC_CODE") : "URCC") + "</doccode>" + 
				"                            <docdescr>" + ("Y".equals(inputMap.get("IS_URCC")) ? inputMap.get("DOC_CODE_DESCR") : "UDYAM REGISTRATION NUMBER") + "</docdescr>" + 
				"                            <docissuedate>"+LocalDateTime.now()+"</docissuedate>" + 
				"                            <docexpirydate></docexpirydate>" + //LEI_EXPIRY_DATE
				"                            <doctypecode>" + ("Y".equals(inputMap.get("IS_URCC")) ? inputMap.get("DOC_TYPE") : "UDYOG AADHAR NUMBER") + "</doctypecode>" + 
				"                            <doctypedescr>" + ("Y".equals(inputMap.get("IS_URCC")) ? inputMap.get("DOC_TYPE_DESCR") : "UDYOG AADHAR NUMBER") + "</doctypedescr>" + 
				"                            <entityType>" + 
				"                                <cif_att_id>"+operation+"</cif_att_id>" + 
				"                                <hidden_cif_fld>CIFCorpCust</hidden_cif_fld>" + 
				"                            </entityType>" + 
				"                            <identificationtype>" + 
				"                                <cif_att_id>"+operation+"</cif_att_id>" + 
				"                                <hidden_cif_fld>URCC</hidden_cif_fld>" + 
				"                            </identificationtype>" + 
				"                            <referencenumber>"+inputMap.get("URCC_NUMBER")+"</referencenumber>" + 
				"                            <docreceiveddate>"+LocalDateTime.now()+"</docreceiveddate>" + 
				"                        </corpentitydocumentModData>" + 
				"                    </relatedData>" + 
				"                </CorpCustomerModData>" + 
				"                <updateCorpCustomer_CustomData>" + 
				"                    <ModifiedBySystemID>CLS</ModifiedBySystemID>" + 
				"                    <EntityDet isMultiRec=\"Y\">" + 
				"                        <DocumentPreferredFlag>Y</DocumentPreferredFlag>" + 
				"                        <IDIssued_Org/>" + 
				"                    </EntityDet>" + 
				"                </updateCorpCustomer_CustomData>" + 
				"            </updateCorpCustomerRequest>" + 
				"        </Body>" + 
				"    </FIXML>]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";			
	}
}
