package com.LsmFiServices.FiLsmService;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
public class LEIUpdationService {
	
	private static final Logger logger = LoggerFactory.getLogger(LEIUpdationService.class);
	
	@Autowired
	private LEI_URCCNumberEnquiryUtility leiUtility;
	
	@Autowired
	private leiUrcVerificationService verifactionDaoLayer;
	
	@NotNull
	private Object objectValue = null;
	
	@Autowired
	private ServiceDetails serviceDetails;


	public @ResponseBody Map<String, Object> executeLEI_UpdationService(String pinstId,String operation,Map<String,String> lieInputDataMap) throws SQLException {

		logger.info("LeiNoUpdationService.executeLEI_UpdationService():: " + pinstId+" and lieInputDataMap-->"+lieInputDataMap);
		String HostTransaction;
		String Status = "";
		Map<String, Object> leiUpdationMap = new ConcurrentHashMap<>();
		ServiceDetailsUpdatePojo pojo = new ServiceDetailsUpdatePojo();
		String requestType = "";
		
		try {
			pojo.setPinstId(pinstId);
			pojo.setServiceName("LEI NUMBER");
			pojo.setFacility(lieInputDataMap.get("CUSTID"));
			pojo.setAccountNumber(lieInputDataMap.get("LEI_NUMBER"));
			serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			String leiUpdationSoapRequest = createSOAPRequest(pinstId,operation,lieInputDataMap);
			pojo.setStatus("Request Sent...!");
			pojo.setServiceRequest(leiUpdationSoapRequest);
			pojo.setReTrigger(true);
			serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			String leiUpdationSoapResponse = SOAPRequestUtility.soapResponse(leiUpdationSoapRequest);
			
			if (leiUpdationSoapResponse.contains("<HostTransaction>")) {
				HostTransaction = leiUpdationSoapResponse.substring(leiUpdationSoapResponse.indexOf("<HostTransaction>") + "<HostTransaction>".length(),leiUpdationSoapResponse.indexOf("</HostTransaction>"));
				if (HostTransaction.contains("<Status>")) {
					Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),HostTransaction.indexOf("</Status>"));
				}
			}
			if (Status.equalsIgnoreCase("SUCCESS")) {
				leiUpdationMap = leiUtility.successPacketDataLEINumber(pinstId, leiUpdationSoapResponse);
				pojo.setStatus(leiUpdationMap.get("Status").toString());
				pojo.setServiceResponse(leiUpdationSoapResponse);
				pojo.setReTrigger(false);
				serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			} else {
				leiUpdationMap.putAll(xmlToMap.packetDataToMap(pinstId, leiUpdationSoapResponse));
				pojo.setStatus(leiUpdationMap.get("Status").toString());
				pojo.setMessage(leiUpdationMap.get("ErrorDesc").toString());
				pojo.setServiceResponse(leiUpdationSoapResponse);
				pojo.setReTrigger(true);
				serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			}
				Map<String, String> convertedMap = new WeakHashMap<>();
				for (Map.Entry<String, Object> obj : leiUpdationMap.entrySet()) {
					objectValue = obj.getValue().toString();
					if (objectValue != null) {
						convertedMap.put(obj.getKey(), obj.getValue().toString());
						objectValue = null;
					}
				}
				requestType = getRequestType(operation, lieInputDataMap);
//				requestType = "LEI NUMBER ADD SERVICE, CUST_ID :: "+lieInputDataMap.get("CUSTID")+", REFERENCE_NUMBER :: "+lieInputDataMap.get("LEI_NUMBER");
				leiUtility.API_RequestResponse_Insert(leiUpdationSoapRequest, leiUpdationSoapResponse,requestType, pinstId, convertedMap, "");					
				if (Status.equalsIgnoreCase("SUCCESS")) {
					verifactionDaoLayer.executeLeiUrcVrfctnService(pinstId, lieInputDataMap.get("CUSTID"),lieInputDataMap.get("REFERENCE_NUMBER_LEI"),"LEI VERIFICATION");
				}
				pojo.setRequestType(requestType);
				serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
		} catch (Exception e) {
			logger.error("LeiNoUpdationService.executeLEI_UpdationService()-> "+ OperationUtillity.traceException(e));
		}
		return leiUpdationMap;
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
	
	public  String createSOAPRequest(String pinstId, String operation,Map<String, String> inputMap) throws IOException, SOAPException, ParseException {

		logger.info("LeiNoUpdationService.createSOAPRequest()-->"+pinstId+" and input Map--->"+inputMap);
	
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
		"                            <doccode>LEIC</doccode>" + 
		"                            <docdescr>LEGAL ENTITY IDENTIFIER</docdescr>" + 
		"                            <docissuedate>"+LocalDateTime.now()+"</docissuedate>" + 
		"                            <docexpirydate>"+inputMap.get("LEI_EXPIRY_DATE")+"T"+LocalTime.now()+"</docexpirydate>" + 
		"                            <doctypecode>ENTITY PROOF</doctypecode>" + 
		"                            <doctypedescr>ENTITY PROOF</doctypedescr>" + 
		"                            <entityType>" + 
		"                                <cif_att_id>"+operation+"</cif_att_id>" + 
		"                                <hidden_cif_fld>CIFCorpCust</hidden_cif_fld>" + 
		"                            </entityType>" + 
		"                            <identificationtype>" + 
		"                                <cif_att_id>"+operation+"</cif_att_id>" + 
		"                                <hidden_cif_fld>LEIC</hidden_cif_fld>" + 
		"                            </identificationtype>" + 
		"                            <referencenumber>"+inputMap.get("LEI_NUMBER")+"</referencenumber>" + 
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
