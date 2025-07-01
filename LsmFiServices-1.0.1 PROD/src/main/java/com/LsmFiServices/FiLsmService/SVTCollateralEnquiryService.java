package com.LsmFiServices.FiLsmService;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.RestAPIUtility;
import com.LsmFiServices.Utility.SVTCollateralEnquiryUtility;
import com.LsmFiServices.Utility.commonUtility;
import com.LsmFiServices.Utility.xmlToMap;
import com.LsmFiServices.pojo.svtcollateralenquiry.AccountNumber;
import com.LsmFiServices.pojo.svtcollateralenquiry.ExecuteFinacleScriptCustomData;
import com.LsmFiServices.pojo.svtcollateralenquiry.FDDetails;

@Service
public class SVTCollateralEnquiryService {
	private static final Logger logger = LoggerFactory.getLogger(SVTCollateralEnquiryService.class);

	@Autowired
	private RestAPIUtility restUtils;

	@Autowired
	private SVTCollateralEnquiryUtility utility;

//	@Autowired
//	private SVTCollateralWithdrawService withdrawService;

	@NotNull
	private Object objectValue = null;

	public String executeCollateralEnquiryService(String pinstid) {
		String decodedResponse = "";
		String HostTransaction;
		String Status = "";
		String requestEntity="";
		Map<String, Object> svtCollateralEnquiryMap = new LinkedHashMap<>();
		List<String> collateralIdList = new LinkedList<>();

		try {
			List<AccountNumber> listOfAccountNumbers = utility.getAllAccountNumbers(pinstid);
			logger.info("SVTCollateralEnquiryService.executeCollateralEnquiryService().listOfAccountNumbers-->"
					+ listOfAccountNumbers);
			for (AccountNumber accountNumber : listOfAccountNumbers) {

				/*
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.TEXT_XML);
				String requestBody = createSOAPRequest(accountNumber);
				HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
				ResponseEntity<String> responseEntity = template.exchange(Constants.FIWebService, HttpMethod.POST,requestEntity, String.class);
				*/
				
				requestEntity = createSOAPRequest(accountNumber);
				decodedResponse = restUtils.getResponseFromFinacle(requestEntity);
				logger.info("Collaterol Enquiry decodedResponse --> " + decodedResponse);
				
				if (decodedResponse.contains("<HostTransaction>")) {
					HostTransaction = decodedResponse.substring(
							decodedResponse.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
							decodedResponse.indexOf("</HostTransaction>"));
					if (HostTransaction.contains("<Status>")) {
						Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),
								HostTransaction.indexOf("</Status>"));
					}
				}
				if (Status.equalsIgnoreCase("SUCCESS")) {
					svtCollateralEnquiryMap = utility.responseDataToMap(pinstid, decodedResponse);
					logger.info("SVTCollateralEnquiryService.executeCollateralEnquiryService().svtCollateralEnquiryMap ---->\n"+ svtCollateralEnquiryMap);
					ExecuteFinacleScriptCustomData rootTag = (ExecuteFinacleScriptCustomData) svtCollateralEnquiryMap.get("executeFinacleScriptCustomData");
					logger.info("rootTag in service->" + rootTag);

					List<FDDetails> details = new LinkedList<>();
					details = rootTag.getListOfFDDeatils();
					logger.info("Getting collateral code list details->" + details);

					if (details == null || details.isEmpty()) {
						logger.info("There are no Collateral code(s) for account number 2nd -->" + accountNumber);
						logger.info("Collateral Id List >====>  in  if");
					} else {
						collateralIdList = details.stream().map(p -> p.getCollateralId()).collect(Collectors.toList());
						logger.info("Collateral Id List >====> " + collateralIdList);
						logger.info("Collateral Id List >====>  in else");
					}
				} else {
					svtCollateralEnquiryMap.putAll(xmlToMap.packetDataToMap(pinstid, decodedResponse));
				}
				Map<String, Object> convertedMap = new LinkedHashMap<>();
				for (Map.Entry<String, Object> obj : svtCollateralEnquiryMap.entrySet()) {
					objectValue = obj.getValue().toString();
					if (objectValue != null) {
						convertedMap.put(obj.getKey(), obj.getValue().toString());
						objectValue = null;
					}
				}
				utility.saveSVTCollateralEnquiryResponse(requestEntity, decodedResponse,
						"SVT_COLLATERAL_ENQUIRY : " + accountNumber.getAccountNumber(), pinstid, convertedMap, "");
				logger.info("SVTCollateralEnquiryService.executeCollateralEnquiryService().respone for ->" + pinstid
						+ " and account number ->" + accountNumber.getAccountNumber() + " is " + decodedResponse);
			}
		} catch (Exception e) {
			logger.info("SVTCollateralEnquiryService.executeCollateralEnquiryService().Exception ->\n"
					+ OperationUtillity.traceException(e));
		}
//		withdrawService.executeCollateralWithdrawService(pinstid, collateralIdList);
		return decodedResponse;
	}

	public String createSOAPRequest(AccountNumber accountNumber) {
		return "<soapenv:Envelope xmlns:soapenv =\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?><FIXML xsi:schemaLocation=\"http://www.finacle.com/fixml executeFinacleScript.xsd\""
				+ "	xmlns=\"http://www.finacle.com/fixml\""
				+ "	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + "	<Header>" + "		<RequestHeader>"
				+ "			<MessageKey>" + "				<RequestUUID>" + commonUtility.createRequestUUID()
				+ "</RequestUUID>" + "				<ServiceRequestId>executeFinacleScript</ServiceRequestId>"
				+ "				<ServiceRequestVersion>10.2</ServiceRequestVersion>"
				+ "				<ChannelId>CLS</ChannelId>" + "				<LanguageId></LanguageId>"
				+ "			</MessageKey>" + "			<RequestMessageInfo>" + "				<BankId>BM3</BankId>"
				+ "				<TimeZone></TimeZone>" + "				<EntityId></EntityId>"
				+ "				<EntityType></EntityType>" + "				<ArmCorrelationId></ArmCorrelationId>"
				+ "				<MessageDateTime>" + LocalDateTime.now() + "</MessageDateTime>"
				+ "			</RequestMessageInfo>" + "			<Security>" + "				<Token>"
				+ "					<PasswordToken>" + "						<UserId></UserId>"
				+ "						<Password></Password>" + "					</PasswordToken>"
				+ "				</Token>" + "				<FICertToken></FICertToken>"
				+ "				<RealUserLoginSessionId></RealUserLoginSessionId>"
				+ "				<RealUser></RealUser>" + "				<RealUserPwd></RealUserPwd>"
				+ "				<SSOTransferToken></SSOTransferToken>" + "			</Security>"
				+ "		</RequestHeader>" + "	</Header>" + "	<Body>" + "		<executeFinacleScriptRequest>"
				+ "			<ExecuteFinacleScriptInputVO>"
				+ "				<requestId>FI_OMNI_ODFD_AccountInquiry.scr</requestId>"
				+ "			</ExecuteFinacleScriptInputVO>" + "			<executeFinacleScript_CustomData>"
				+ "				<Account_No>" + accountNumber.getAccountNumber() + "</Account_No>"
				+ "			</executeFinacleScript_CustomData>" + "		</executeFinacleScriptRequest>" + "	</Body>"
				+ "</FIXML>]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
	}
}
