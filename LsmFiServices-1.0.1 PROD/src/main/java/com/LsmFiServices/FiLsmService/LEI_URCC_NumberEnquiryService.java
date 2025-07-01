package com.LsmFiServices.FiLsmService;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.constraints.NotNull;
import javax.xml.soap.SOAPException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import com.LsmFiServices.FiLsmController.LEIUpdationController;
import com.LsmFiServices.FiLsmController.URCCUpdationController;
import com.LsmFiServices.Utility.LEI_URCCNumberEnquiryUtility;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.SOAPRequestUtility;
import com.LsmFiServices.Utility.ServiceDetails;
import com.LsmFiServices.Utility.commonUtility;
import com.LsmFiServices.Utility.xmlToMap;
import com.LsmFiServices.pojo.leinumenquiry.AllDocDetail;
import com.LsmFiServices.pojo.leinumenquiry.ExecuteFinacleScriptResponse;
import com.LsmFiServices.pojo.serviceexecutiondetails.ServiceDetailsUpdatePojo;

@Service
public class LEI_URCC_NumberEnquiryService {

	private static final Logger logger = LoggerFactory.getLogger(LEI_URCC_NumberEnquiryService.class);

	
	
	@Autowired
	private LEI_URCCNumberEnquiryUtility utility;

	@NotNull
	private Object objectValue = null;
	
	@Autowired
	private URCCUpdationController urccController;
	
	@Autowired
	private LEIUpdationController leiController;
	
	@Autowired
	private ServiceDetails serviceDetails;

    public @ResponseBody Map<String, Object> executeLEIURCCEnquiryService(String pinstId, String input)
			throws SQLException {

		logger.info("LEINumberEnquiryService.executeLEIEnquiryService():: " + pinstId + " input->" + input);
		String HostTransaction;
		String Status = "";
		Map<String, Object> leiNumberEnquiryMap = new HashMap<>();
		List<Map<String, String>> uniqueCustIdsListofMap = new LinkedList<>();
		List<Map<String, String>> statusCodeDataMap = utility.getStatusCodeDataMap(pinstId);
		if (statusCodeDataMap.size() == 0) {
			logger.info("getting custid from home tab", pinstId);
			uniqueCustIdsListofMap = utility.getLeiUrccNums(pinstId);
		} else {
			uniqueCustIdsListofMap = utility.getStatusCodeDataMap(pinstId);
		}
		ServiceDetailsUpdatePojo pojo = new ServiceDetailsUpdatePojo();
		String requestType = "";
		if (!input.equalsIgnoreCase("common")) {
			uniqueCustIdsListofMap.removeAll(uniqueCustIdsListofMap);
		}
		try {
			for (int i = 0; i < uniqueCustIdsListofMap.size(); i++) {
				Map<String, String> individualMap = uniqueCustIdsListofMap.get(i);
				pojo.setPinstId(pinstId);
				pojo.setServiceName("LEI_URCC_NUMBER_ENQUIRY");
				pojo.setFacility(individualMap.get("CUSTID"));
				String leiNumberEnquirySoapRequest = createSOAPRequest(pinstId, individualMap);
				pojo.setServiceRequest(leiNumberEnquirySoapRequest);
				pojo.setStatus("Request Sent...!");
				pojo.setReTrigger(true);
				serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
				String leiNumberEnquirySoapResponse = SOAPRequestUtility.soapResponse(leiNumberEnquirySoapRequest);
				if (leiNumberEnquirySoapResponse.contains("<HostTransaction>")) {
					HostTransaction = leiNumberEnquirySoapResponse.substring(
							leiNumberEnquirySoapResponse.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
							leiNumberEnquirySoapResponse.indexOf("</HostTransaction>"));
					if (HostTransaction.contains("<Status>")) {
						Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),
								HostTransaction.indexOf("</Status>"));
					}
				}
				requestType = "LEI_URCC_NUMBER_ENQUIRY, CUSTID :: " + individualMap.get("CUSTID");
				if (Status.equalsIgnoreCase("SUCCESS")) {
					leiNumberEnquiryMap = utility.successPacketDataLEINumber(pinstId, leiNumberEnquirySoapResponse);
					pojo.setStatus(leiNumberEnquiryMap.get("Status").toString());
					pojo.setRequestType(requestType);
					pojo.setServiceResponse(leiNumberEnquirySoapResponse);
					pojo.setReTrigger(false);
					serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
					ExecuteFinacleScriptResponse rootTag = (ExecuteFinacleScriptResponse) leiNumberEnquiryMap
							.get("ExecuteFinacleScriptResponse");
					logger.info("ExecuteFinacleScriptResponse rootTag for pinstid " + pinstId + " -----> " + rootTag);
					List<AllDocDetail> listOfAllDocDetails = rootTag.getAllDocDetails();
					logger.info("listOfAllDocDetails for pinstid " + pinstId + " -----> " + listOfAllDocDetails);
					boolean hasURCC = listOfAllDocDetails.stream()
							.anyMatch(allDocDetail -> allDocDetail.getDocCode().equalsIgnoreCase("URCC"));
					boolean hasLEIC = listOfAllDocDetails.stream()
							.anyMatch(allDocDetail -> allDocDetail.getDocCode().equalsIgnoreCase("LEIC"));
					Optional<String> refCodeForURCC = listOfAllDocDetails.stream()
							.filter(allDocDetail -> "URCC".equalsIgnoreCase(allDocDetail.getDocCode()))
							.map(AllDocDetail::getRefNumber).findFirst();
					Optional<String> refCodeForLEIC = listOfAllDocDetails.stream()
							.filter(allDocDetail -> "LEIC".equalsIgnoreCase(allDocDetail.getDocCode()))
							.map(AllDocDetail::getRefNumber).findFirst();

					if (hasURCC) {
						if (refCodeForURCC.isPresent()) {
							individualMap.put("REFERENCE_NUMBER_URCC", refCodeForURCC.get());
						}
						individualMap.put("IS_URCC", "Y");
						individualMap.put("DOC_TYPE",
								listOfAllDocDetails.stream()
										.filter(detail -> "URCC".equalsIgnoreCase(detail.getDocCode()))
										.map(AllDocDetail::getDocType).findFirst().orElse(""));
						individualMap.put("DOC_CODE",
								listOfAllDocDetails.stream()
										.filter(detail -> "URCC".equalsIgnoreCase(detail.getDocCode()))
										.map(AllDocDetail::getDocCode).findFirst().orElse(""));
						individualMap.put("DOC_TYPE_DESCR",
								listOfAllDocDetails.stream()
										.filter(detail -> "URCC".equalsIgnoreCase(detail.getDocCode()))
										.map(AllDocDetail::getDocTypeDescr).findFirst().orElse(""));
						individualMap.put("DOC_CODE_DESCR",
								listOfAllDocDetails.stream()
										.filter(detail -> "URCC".equalsIgnoreCase(detail.getDocCode()))
										.map(AllDocDetail::getDocCodeDescr).findFirst().orElse(""));
						logger.info("individualMap for pinstid " + pinstId + " -----> " + individualMap);
						leiNumberEnquiryMap.put("URCC SERVICE",
								urccController.runURCCUpdation(pinstId, "UPDATE", individualMap));
					} else {
						leiNumberEnquiryMap.put("URCC SERVICE",
								urccController.runURCCUpdation(pinstId, "ADD", individualMap));
					}
					if (hasLEIC) {
						if (refCodeForLEIC.isPresent()) {
							individualMap.put("REFERENCE_NUMBER_LEI", refCodeForLEIC.get());
						}
						leiNumberEnquiryMap.put("LEI SERVICE",
								leiController.runLEIUpdation(pinstId, "UPDATE", individualMap));
					} else {
						leiNumberEnquiryMap.put("LEI SERVICE",
								leiController.runLEIUpdation(pinstId, "ADD", individualMap));
					}
				} else {
					leiNumberEnquiryMap.putAll(xmlToMap.packetDataToMap(pinstId, leiNumberEnquirySoapResponse));
					pojo.setStatus(leiNumberEnquiryMap.get("Status").toString());
					pojo.setRequestType(requestType);
					pojo.setMessage(leiNumberEnquiryMap.get("ErrorDesc").toString());
					pojo.setServiceResponse(leiNumberEnquirySoapResponse);
					pojo.setReTrigger(true);
					serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
				}
				Map<String, String> convertedMap = new LinkedHashMap<>();
				for (Map.Entry<String, Object> obj : leiNumberEnquiryMap.entrySet()) {
					objectValue = obj.getValue().toString();
					if (objectValue != null) {
						convertedMap.put(obj.getKey(), obj.getValue().toString());
						objectValue = null;
					}
				}
				utility.API_RequestResponse_Insert(leiNumberEnquirySoapRequest, leiNumberEnquirySoapResponse,
						requestType, pinstId, convertedMap, "");
			} // for
		} catch (Exception e) {
			logger.error("LEINumberEnquiryService.executeLEIEnquiryService()-> " + OperationUtillity.traceException(e));
		}
		return leiNumberEnquiryMap;
	}

	public String createSOAPRequest(String pinstId,Map<String,String> inputMap) throws IOException, SOAPException, ParseException {
		return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\">"
				+ "	<soapenv:Header/>" 
				+ "	<soapenv:Body>" 
				+ "		<web:executeService>" 
				+ "			<arg_0_0>"
				+ "				<![CDATA[<FIXML xsi:schemaLocation=\"http://www.finacle.com/fixml executeFinacleScript.xsd\" xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "		<Header>" 
				+ "			<RequestHeader>" 
				+ "				<MessageKey>"
				+ "					<RequestUUID>" + commonUtility.createRequestUUID() + "</RequestUUID>"
				+ "					<ServiceRequestId>executeFinacleScript</ServiceRequestId>"
				+ "					<ServiceRequestVersion>10.2</ServiceRequestVersion>"
				+ "					<ChannelId>CLS</ChannelId>" 
				+ "					<LanguageId>"
				+ "					</LanguageId>" 
				+ "				</MessageKey>"
				+ "				<RequestMessageInfo>" 
				+ "					<BankId>BM3</BankId>"
				+ "					<TimeZone>" 
				+ "					</TimeZone>" 
				+"					<EntityId>"
				+ "					</EntityId>" 
				+ "					<EntityType>"
				+ "					</EntityType>" 
				+ "					<ArmCorrelationId>"
				+ "					</ArmCorrelationId>" 
				+ "					<MessageDateTime>"+ LocalDateTime.now()+ "</MessageDateTime>" 
				+ "				</RequestMessageInfo>"
				+ "				<Security>" 
				+ "					<Token>" 
				+ "						<PasswordToken>"
				+ "							<UserId>" 
				+ "							</UserId>"
				+ "							<Password>" 
				+ "							</Password>"
				+ "						</PasswordToken>" 
				+ "					</Token>"
				+ "					<FICertToken>" 
				+ "					</FICertToken>"
				+ "					<RealUserLoginSessionId>" 
				+ "					</RealUserLoginSessionId>"
				+ "					<RealUser>" 
				+ "					</RealUser>" 
				+ "					<RealUserPwd>"
				+ "					</RealUserPwd>" 
				+ "					<SSOTransferToken>"
				+ "					</SSOTransferToken>" 
				+ "				</Security>"
				+ "			</RequestHeader>" 
				+ "		</Header>" 
				+ "		<Body>"
				+ "			<executeFinacleScriptRequest>" 
				+ "				<ExecuteFinacleScriptInputVO>"
				+ "					<requestId>FI_CMN_AllDocDetails.scr</requestId>"
				+ "				</ExecuteFinacleScriptInputVO>" 
				+ "				<executeFinacleScript_CustomData>"
				+ "					<Customer_Id>" + inputMap.get("CUSTID")+ "</Customer_Id>"
				+ "				</executeFinacleScript_CustomData>" 
				+ "			</executeFinacleScriptRequest>"
				+ "		</Body>" 
				+ "	</FIXML>]]>" 
				+ "		</arg_0_0>" 
				+ "	</web:executeService>"
				+ "</soapenv:Body>" 
				+ "</soapenv:Envelope>";
	}
}
