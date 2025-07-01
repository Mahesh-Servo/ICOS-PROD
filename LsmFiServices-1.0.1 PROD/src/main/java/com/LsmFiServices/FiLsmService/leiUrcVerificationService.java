package com.LsmFiServices.FiLsmService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import javax.xml.soap.SOAPException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.SOAPRequestUtility;
import com.LsmFiServices.Utility.ServiceDetails;
import com.LsmFiServices.Utility.commonUtility;
import com.LsmFiServices.Utility.xmlToMap;
import com.LsmFiServices.pojo.serviceexecutiondetails.ServiceDetailsUpdatePojo;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class leiUrcVerificationService {
    private static final Logger logger = LoggerFactory.getLogger(leiUrcVerificationService.class);

    @Autowired
    private ServiceDetails serviceDetails;

    public Object executeLeiUrcVrfctnService(String pinstid, String cifId, String leiUrccNumber, String type)
	    throws SQLException, IOException, SOAPException {

	logger.info("\n[leiUrcVerificationService.executeLeiUrcVrfctnService()].[pinstid: " + pinstid + " ].[cifId] == "
		+ cifId);
	Map<String, String> LeiUrcVrfctnResponseMap = new HashMap<>();
	ServiceDetailsUpdatePojo pojo = new ServiceDetailsUpdatePojo();
	String name = "";
	String requestType = "";
	try {

	    if (type.contains("LEI")) {
		name = "LEI";
	    } else if (type.contains("URCC")) {
		name = "URCC";
	    }
	    pojo.setPinstId(pinstid);
	    pojo.setServiceName(type);
	    pojo.setFacility(cifId);
	    pojo.setAccountNumber(leiUrccNumber);
	    String soapRequestPacket = createLeiVrfctnRequestPacket(cifId);
	    pojo.setServiceRequest(soapRequestPacket);
	    pojo.setStatus("Request Sent...!");
	    pojo.setReTrigger(true);
	    serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
	    String LeiUrcVrfctnResponsePacket = SOAPRequestUtility.soapResponse(soapRequestPacket);
	    LeiUrcVrfctnResponseMap.put("cifId", cifId);
	    LeiUrcVrfctnResponseMap.put("requestPacket", soapRequestPacket);
	    LeiUrcVrfctnResponseMap.put("responsePacket", LeiUrcVrfctnResponsePacket);

	    String Status = "";

	    if (LeiUrcVrfctnResponsePacket.contains("<HostTransaction>")) {
		String HostTransaction = LeiUrcVrfctnResponsePacket.substring(
			LeiUrcVrfctnResponsePacket.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
			LeiUrcVrfctnResponsePacket.indexOf("</HostTransaction>"));
		if (HostTransaction.contains("<Status>")) {
		    Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),
			    HostTransaction.indexOf("</Status>"));
		    LeiUrcVrfctnResponseMap.put("Status", Status);
		}
	    }

	    requestType = String
		    .valueOf(name + " Verification :: " + cifId + " ::" + name + " Number :: " + leiUrccNumber);
	    logger.info("Verifiucation packect-->" + requestType);
	    LeiUrcVrfctnResponseMap.putAll(convertLeiUrcVrfctnResToMap(pinstid, LeiUrcVrfctnResponsePacket, Status));

	    if ("SUCCESS".equalsIgnoreCase(Status)) {
		pojo.setStatus(Status);
		pojo.setServiceResponse(LeiUrcVrfctnResponsePacket);
		pojo.setRequestType(requestType);
		pojo.setReTrigger(false);
		serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
	    } else {
		pojo.setStatus(Status);
		pojo.setServiceResponse(LeiUrcVrfctnResponsePacket);
		pojo.setRequestType(requestType);
		pojo.setMessage(LeiUrcVrfctnResponseMap.get("ErrorDesc"));
		pojo.setReTrigger(true);
		serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
	    }
	    OperationUtillity.API_RequestResponse_Insert(soapRequestPacket, LeiUrcVrfctnResponsePacket, requestType,
		    pinstid, LeiUrcVrfctnResponseMap, "");
	} catch (Exception e) {
	    logger.info("\nleiUrcVerificationService.executeLeiUrcVrfctnService().[Exception] -->"
		    + OperationUtillity.traceException(pinstid, e));
	}
	return LeiUrcVrfctnResponseMap;
    }

    public String createLeiVrfctnRequestPacket(String cifId) {

	String rqstPacket = "";
	try {

	    rqstPacket = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		    + "<FIXML" + "        xmlns='http://www.finacle.com/fixml'"
		    + "        xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.finacle.com/fixml executeFinacleScript.xsd'>"
		    + "        <Header>" + "               <RequestHeader>" + "                <MessageKey>"
		    + "                    <RequestUUID>" + commonUtility.createRequestUUID() + "</RequestUUID>"
		    + "                    <ServiceRequestId>verifyCustomerDetails</ServiceRequestId>"
		    + "                    <ServiceRequestVersion>10.2</ServiceRequestVersion>"
		    + "                    <ChannelId>CLS</ChannelId>" + "                    <LanguageId/>"
		    + "                </MessageKey>" + "                <RequestMessageInfo>"
		    + "                    <BankId>BM3</BankId>" + "                    <TimeZone/>"
		    + "                    <EntityId/>" + "                    <EntityType/>"
		    + "                    <ArmCorrelationId/>" + "                    <MessageDateTime>"
		    + LocalDateTime.now() + "</MessageDateTime>" + "                </RequestMessageInfo>"
		    + "                <Security>" + "                    <Token>"
		    + "                        <PasswordToken>" + "                            <UserId/>"
		    + "                            <Password/>" + "                        </PasswordToken>"
		    + "                    </Token>" + "                    <FICertToken/>"
		    + "                    <RealUserLoginSessionId/>" + "                    <RealUser/>"
		    + "                    <RealUserPwd/>" + "                    <SSOTransferToken/>"
		    + "                </Security>" + "            </RequestHeader> " + "        </Header>" + "  <Body>"
		    + "      <verifyCustomerDetailsRequest>" + "                <CustomerVerifyRq>"
		    + "                    <cifId>" + cifId + "</cifId>"
		    + "                    <decision>Approve</decision>"
		    + "                    <entityName>CorporateCustomer</entityName>"
		    + "                </CustomerVerifyRq>" + "            </verifyCustomerDetailsRequest> "
		    + "  </Body>" + "</FIXML>" + "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
	    return rqstPacket;
	} catch (Exception e) {
	    logger.info("leiUrcVerificationService.executeLeiUrcVrfctnService.createLeiVrfctnRequestPacket()"
		    + OperationUtillity.traceException(e));
	}
	return rqstPacket;
    }

    public WeakHashMap<String, String> convertLeiUrcVrfctnResToMap(String pinstid, String responsePacket, String Status)
	    throws JsonProcessingException, SQLException {
	WeakHashMap<String, String> LeiUrcVrfctnRspnsPckt = new WeakHashMap<>();

	if (Status.equalsIgnoreCase("Success")) {
	    String HostTransaction = responsePacket.substring(
		    responsePacket.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
		    responsePacket.indexOf("</HostTransaction>"));
	    LeiUrcVrfctnRspnsPckt.put("HostTransaction", HostTransaction);
	} else {
	    LeiUrcVrfctnRspnsPckt.putAll(xmlToMap.packetDataToMap(pinstid, responsePacket));
	}
	return LeiUrcVrfctnRspnsPckt;
    }
}
