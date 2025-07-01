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
import com.LsmFiServices.Utility.StatusCodeServiceUtility;
import com.LsmFiServices.Utility.commonUtility;
import com.LsmFiServices.Utility.xmlToMap;

@SuppressWarnings("deprecation")
@Service
public class StatusCodeFiService {
	
	
	@Autowired
	private StatusCodeServiceUtility utility;

	private static final Logger logger = LoggerFactory.getLogger(StatusCodeFiService.class);

	public Map<String,String> StatusCodeService(String PINSTID) throws SOAPException, SQLException {
//		String response = "";
		String StatusCodeServiceResponse = "";
		SOAPMessage soapRequest = null;
		String RequestUUID="", HostTransaction = "", Status = "";
			logger.info("Entering into StatusCodeService():: " + PINSTID);
			Map<String, String> API_REQ_RES_map = new HashMap<>();

			Map<String, String> statusCodeInputMap = utility.getMapForStatusCode(PINSTID);
			logger.info("statusCodeInputMap check in StatusCodeService()--->"+statusCodeInputMap);

			if ("Yes".equalsIgnoreCase(statusCodeInputMap.get("IS_INTERNAL_BALANCE_TRANSFER_OPTION"))) {

				try {
					SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
					SOAPConnection soapConnection = soapConnectionFactory.createConnection();
					try {
						SOAPMessage soapResponse;
						try {
							soapRequest = StatusCodeFiService.createSOAPRequest(PINSTID, statusCodeInputMap);
							soapResponse = soapConnection.call(soapRequest, Constants.FIWebService);
							StatusCodeServiceResponse = OperationUtillity.soapMessageToString(soapResponse);
							StatusCodeServiceResponse = StringEscapeUtils.unescapeXml(StatusCodeServiceResponse);
						} catch (Exception e) {
							logger.info("In info Logger There is problem in calling StatusCodeService request packet-->"
									+ OperationUtillity.traceException(e));
						}
					} catch (Exception e) {
						logger.info("In info Logger There is problem in calling request packet-->"
								+ OperationUtillity.traceException(e));
					}

					if (StatusCodeServiceResponse.contains("<HostTransaction>")) {
						HostTransaction = StatusCodeServiceResponse.substring(
								StatusCodeServiceResponse.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
								StatusCodeServiceResponse.indexOf("</HostTransaction>"));
						if (HostTransaction.contains("<Status>")) {
							Status = HostTransaction.substring(
									HostTransaction.indexOf("<Status>") + "<Status>".length(),
									HostTransaction.indexOf("</Status>"));
						}
					}
					if (Status.equalsIgnoreCase("SUCCESS")) {
						API_REQ_RES_map = xmlToMap.StatusCodeSuccessPacketDataToMap(PINSTID, StatusCodeServiceResponse);
					} else {
						API_REQ_RES_map = xmlToMap.packetDataToMap(PINSTID, StatusCodeServiceResponse);
					}
				} catch (Exception Ex) {
					API_REQ_RES_map.put("RequestUUID", "");
					API_REQ_RES_map.put("MessageDateTime", API_REQ_RES_map.get("MessageDateTime"));
					API_REQ_RES_map.put("Error_Code", "500");
					API_REQ_RES_map.put("message", Ex.getMessage());
					API_REQ_RES_map.put("Status", "FAILED");
					API_REQ_RES_map.put("request header", soapRequest.getSOAPHeader().toString());
					API_REQ_RES_map.put("request body", soapRequest.getSOAPBody().toString());
					logger.info("Exception while processing request in StatusCodeService:: " + PINSTID + " :: Exception ="+ OperationUtillity.traceException(Ex));
				}
				try {
					OperationUtillity.API_RequestResponse_Insert(OperationUtillity.soapMessageToString(soapRequest),
							StatusCodeServiceResponse, "Status Code  ", PINSTID, API_REQ_RES_map, RequestUUID);
//					response = new ObjectMapper().writeValueAsString(API_REQ_RES_map);
				} catch (Exception e) {
					logger.info("StatusCode API Insert res error ----------->" + OperationUtillity.traceException(e));
				}

				logger.info("StatusCodeService Service data for " + PINSTID + ", REQUEST :: \n "+ OperationUtillity.soapMessageToString(soapRequest) + ", RESPONSE :: \n"
						+ StatusCodeServiceResponse+"API_REQ_RES_map-->"+API_REQ_RES_map);

				return API_REQ_RES_map;
			}else {
				API_REQ_RES_map.put("INTERNAL_BALANCE_TRANSFER_OPTION", "NO");
				logger.info("--------"+API_REQ_RES_map);
				return API_REQ_RES_map;
			}
	}

	public static SOAPMessage createSOAPRequest(String PINSTID,Map<String, String> statusCodeInputMap)
			throws IOException, SOAPException, ParseException {

		logger.info("Entering into StatusCodeServiceResponse.createSOAPRequest : : " + PINSTID+" and input map is-->"+statusCodeInputMap);

		String soapMessage ="";
		String requestUuid = commonUtility.createRequestUUID();
		
		try {

			soapMessage =  "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\">" + 
			 "    <soapenv:Header/>" + 
			 "    <soapenv:Body>" + 
			 "        <web:executeService>" + 
			 "            <arg_0_0>" + 
			 "                <![CDATA[<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + 
			 "                    <Header>" + 
			 "                        <RequestHeader>" + 
			 "                            <MessageKey>" + 
			 "                                <RequestUUID>"+requestUuid+"</RequestUUID>" + 
			 "                                <ServiceRequestId>executeFinacleScript</ServiceRequestId>" + 
			 "                                <ServiceRequestVersion>10.2</ServiceRequestVersion>" + 
			 "                                <ChannelId>CLS</ChannelId>" + 
			 "                                <LanguageId/>" + 
			 "                            </MessageKey>" + 
			 "                            <RequestMessageInfo>" + 
			 "                                <BankId>BM3</BankId>" + 
			 "                                <TimeZone/>" + 
			 "                                <EntityId/>" + 
			 "                                <EntityType/>" + 
			 "                                <ArmCorrelationId/>" + 
			 "                                <MessageDateTime>"+LocalDateTime.now()+"</MessageDateTime>" + 
			 "                            </RequestMessageInfo>" + 
			 "                            <Security>" + 
			 "                                <Token>" + 
			 "                                    <PasswordToken>" + 
			 "                                        <UserId/>" + 
			 "                                        <Password/>" + 
			 "										</PasswordToken>" + 
			 "                                </Token>" + 
			 "                                <FICertToken/>" + 
			 "                                <RealUserLoginSessionId/>" + 
			 "                                <RealUser/>" + 
			 "                                <RealUserPwd/>" + 
			 "                                <SSOTransferToken/>" + 
			 "                            </Security>" + 
			 "                        </RequestHeader>" + 
			 "                    </Header>" + 
			 "                    <Body>" + 
			 "                        <executeFinacleScriptRequest>" + 
			 "                            <ExecuteFinacleScriptInputVO>" + 
			 "                                <requestId>FI_cmn_demogUpdate.scr</requestId>" + 
			 "                            </ExecuteFinacleScriptInputVO>" + 
			 "                            <executeFinacleScript_CustomData>" + 
			 "                                <Customer_Id>"+statusCodeInputMap.get("UCC_BASED_CUST_ID")+"</Customer_Id>" + 
			 "                                <Status_Code>"+statusCodeInputMap.get("STATUS_CODE")+"</Status_Code>" + 
			 "                                <constCode>" + 
			 "                                </constCode>" + 
			 "                                <Channel_Id>CLS</Channel_Id>" + 
			 "                            </executeFinacleScript_CustomData>" + 
			 "                        </executeFinacleScriptRequest>" + 
			 "                    </Body>" + 
			 "                </FIXML>]] >" + 
			 "            </arg_0_0>" + 
			 "        </web:executeService>" + 
			 "    </soapenv:Body>" + 
			 "</soapenv:Envelope>";
			 
		}catch(Exception e) {
			logger.info("Exception while pasting data in request pkt-->"+OperationUtillity.traceException(e));
		}
		
		InputStream is = new ByteArrayInputStream(soapMessage.getBytes());
		SOAPMessage requestSoap = MessageFactory.newInstance().createMessage(null, is);
		requestSoap.saveChanges();
		return requestSoap;
	}	
}
