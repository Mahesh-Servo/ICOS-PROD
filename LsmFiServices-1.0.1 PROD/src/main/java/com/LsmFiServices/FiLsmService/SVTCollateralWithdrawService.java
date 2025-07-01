
package com.LsmFiServices.FiLsmService;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.SOAPRequestUtility;
import com.LsmFiServices.Utility.SVTCollateralEnquiryUtility;
import com.LsmFiServices.Utility.commonUtility;
import com.LsmFiServices.Utility.xmlToMap;
import com.LsmFiServices.pojo.svtcollateralenquiry.AccountNumber;
import com.LsmFiServices.pojo.svtcollateralenquiry.ExecuteFinacleScriptCustomData;

	public class SVTCollateralWithdrawService {


			private static final Logger logger = LoggerFactory.getLogger(SVTCollateralEnquiryService.class);
			
			@Autowired
			private RestTemplate template;
			
			@Autowired
			private SVTCollateralEnquiryUtility utility;
			
			@NotNull
			private Object objectValue = null;
			
			public String executeCollateralWithdrawService(String pinstid, List<String> collateralIdsList){		
				String response = "";
				String HostTransaction;
				String Status = "";
				Map<String, Object> svtCollateralEnquiryMap = new LinkedHashMap<>();
				
				try {
					List<AccountNumber> listOfAccountNumbers = utility.getAllAccountNumbers(pinstid);
					logger.info("SVTCollateralEnquiryService.executeCollateralEnquiryService().listOfAccountNumbers-->"+listOfAccountNumbers);
					for(String collateralId: collateralIdsList) {
						
					
					String requestBody = createSOAPRequest(collateralId);
					String responseEntity = SOAPRequestUtility.soapResponse(requestBody);
					
					response = responseEntity.toString();
					
					if (response.contains("<HostTransaction>")) {
						HostTransaction = response.substring(response.indexOf("<HostTransaction>") + "<HostTransaction>".length(),response.indexOf("</HostTransaction>"));
						if (HostTransaction.contains("<Status>")) {
							Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),HostTransaction.indexOf("</Status>"));
						}
					}
					if (Status.equalsIgnoreCase("SUCCESS")) {
						svtCollateralEnquiryMap = utility.responseDataToMap(pinstid, response);			
						logger.info("SVTCollateralWithdrawService.executeCollateralWithdrawService().svtCollateralEnquiryMap ---->\n" + svtCollateralEnquiryMap);
						ExecuteFinacleScriptCustomData rootTag =(ExecuteFinacleScriptCustomData) svtCollateralEnquiryMap.get("ExecuteFinalcleScriptCustomData");
//						logger.info("Getting collateral code list details->"+details);
						
						} else {
							svtCollateralEnquiryMap.putAll(xmlToMap.packetDataToMap(pinstid, response));		
						}
					Map<String, Object> convertedMap = new LinkedHashMap<>();
					for (Map.Entry<String, Object> obj : svtCollateralEnquiryMap.entrySet()) {
						objectValue = obj.getValue().toString();
						if (objectValue != null) {
							convertedMap.put(obj.getKey(), obj.getValue().toString());
							objectValue = null;
						}
					}
					utility.saveSVTCollateralEnquiryResponse(requestBody, response,"SVT_COLLATERAL_WITHDRAW_SERVICE->"+collateralId, pinstid, convertedMap, "");
					logger.info("SVTCollateralWithdrawService.executeCollateralWithdrawService().respone for ->"+pinstid +" and collateralId ->"+collateralId+" is "+response);
					}
				} catch (Exception e) {
					logger.info("SVTCollateralWithdrawService.executeCollateralWithdrawService().Exception ->\n"+OperationUtillity.traceException(e));
				}	
					return response;
			}
			
			public String createSOAPRequest(String collateralId) {
				return "<soapenv:Envelope" + 
						"  xmlns:soapenv =\"http://schemas.xmlsoap.org/soap/envelope/\"" + 
						"  xmlns:web=\"http://webservice.fiusb.ci.infosys.com\">" + 
						"  <soapenv:Header/>" + 
						"  <soapenv:Body>" + 
						"    <web:executeService>" + 
						"      <arg_0_0>" + 
						"        <![CDATA[" + 
						"        <?xml version=\"1.0\" encoding=\"UTF-8\"?><FIXML xsi:schemaLocation=\"http://www.finacle.com/fixml executeFinacleScript.xsd\"" + 
						"        xmlns=\"http://www.finacle.com/fixml\"" + 
						"        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"></FIXML>]]>" + 
						"		<Header>" + 
						"		  <RequestHeader>" + 
						"			<MessageKey>" + 
						"			  <RequestUUID>"+commonUtility.createRequestUUID()+"</RequestUUID>" + 
						"			  <ServiceRequestId>executeFinacleScript</ServiceRequestId>" + 
						"			  <ServiceRequestVersion>10.2</ServiceRequestVersion>" + 
						"			  <ChannelId>CLS</ChannelId>" + 
						"			  <LanguageId></LanguageId>" + 
						"			</MessageKey>" + 
						"			<RequestMessageInfo>" + 
						"			  <BankId>BM3</BankId>" + 
						"			  <TimeZone></TimeZone>" + 
						"			  <EntityId></EntityId>" + 
						"			  <EntityType></EntityType>" + 
						"			  <ArmCorrelationId></ArmCorrelationId>" + 
						"			  <MessageDateTime>"+LocalDateTime.now()+"</MessageDateTime>" + 
						"			</RequestMessageInfo>" + 
						"			<Security>" + 
						"			  <Token>" + 
						"				<PasswordToken>" + 
						"				  <UserId></UserId>" + 
						"				  <Password></Password>" + 
						"				</PasswordToken>" + 
						"			  </Token>" + 
						"			  <FICertToken></FICertToken>" + 
						"			  <RealUserLoginSessionId></RealUserLoginSessionId>" + 
						"			  <RealUser></RealUser>" + 
						"			  <RealUserPwd></RealUserPwd>" + 
						"			  <SSOTransferToken></SSOTransferToken>" + 
						"			</Security>" + 
						"		  </RequestHeader>" + 
						"		</Header>" + 
						"		<Body>" + 
						"		  <executeFinacleScriptRequest>" + 
						"			<ExecuteFinacleScriptInputVO>" + 
						"			  <requestId>FI_OMNI_WithDrawCollateral.scr</requestId>" + 
						"			</ExecuteFinacleScriptInputVO>" + 
						"			<executeFinacleScript_CustomData>" + 
						"			  <CollateralId>"+collateralId+"</CollateralId>" + 	//collateral Id
//						"			  <Withdraw_Date>05-12-2017 00:00:00</Withdraw_Date>" + 	//sysdatetime
						"			  <Withdraw_Date>"+LocalDateTime.now()+"</Withdraw_Date>" + 	//sysdatetime
						"			  <Remarks>CIB Collateral WithDrawl</Remarks>" + 
						"			</executeFinacleScript_CustomData>" + 
						"		  </executeFinacleScriptRequest>" + 
						"		</Body>" + 
						"      </arg_0_0>" + 
						"</FIXML>]]>"+
						"    </web:executeService>" + 
						"  </soapenv:Body>" + 
						"</soapenv:Envelope>";
			}
		}

		

