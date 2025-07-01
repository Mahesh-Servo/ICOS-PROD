package com.LsmFiServices.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import com.LsmFiServices.FiLsmService.DrawingPowerCheckService;
import com.LsmFiServices.FiLsmService.DrawingPowerUpdationService;
import com.LsmFiServices.FiLsmService.accountAndLimitNodeLinkageService;
import com.LsmFiServices.Utility.Constants;
import com.LsmFiServices.Utility.DrawingPowerUtility;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.ParentLimitNodeModificationUtility;
import com.LsmFiServices.Utility.ServiceDetails;
import com.LsmFiServices.Utility.xmlToMap;
import com.LsmFiServices.pojo.serviceexecutiondetails.ServiceDetailsUpdatePojo;

@Controller
@SuppressWarnings("deprecation")
public class AccountAndLimitNodeLinkageDao {
	private static final Logger logger = LoggerFactory.getLogger(AccountAndLimitNodeLinkageDao.class);

	@Autowired
	private ServiceDetails serviceDetails;
	@Autowired
	private DrawingPowerCheckService service;
	@Autowired
	private ParentLimitNodeModificationUtility parentModUtility;
	@Autowired
	private DrawingPowerUtility drgPwrUtils;
	@Autowired
	private DrawingPowerUpdationService drawingPowerUpdationService;

	public @ResponseBody Map<String, String> accountAndLimitNodeLinkageDaoImpl(String pinstId,
			Map<String, String> accountAndLimitNodeLinkageMap, int i) throws SOAPException, SQLException {

		Map<String, String> API_REQ_RES_map = new HashMap<>();
		String accountAndLimitNodeResponse = "";
		SOAPMessage soapRequest = null;
		String HostTransaction = "", Status = "";
		ServiceDetailsUpdatePojo pojo = new ServiceDetailsUpdatePojo();
		String requestType = "";
		String facility = accountAndLimitNodeLinkageMap.get("FACILITY_NAME");
		String accountNumber = accountAndLimitNodeLinkageMap.get("ACCOUNT_NO_" + i);
		try {
			pojo.setPinstId(pinstId);
			pojo.setServiceName("ACCOUNT AND LIMIT NODE LINKAGE");
			pojo.setAccountNumber(accountNumber);
			pojo.setFacility(facility);
			requestType = "ACCOUNT AND LIMIT NODE LINKAGE : " + facility + " Account No : " + accountNumber + "";
			pojo.setRequestType(requestType);
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			soapRequest = accountAndLimitNodeLinkageService.createSOAPRequest(pinstId, accountAndLimitNodeLinkageMap,
					i);
			pojo.setServiceRequest(OperationUtillity.soapMessageToString(soapRequest));
			pojo.setStatus("Request Sent...!");
			pojo.setReTrigger(true);
			serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			SOAPMessage soapResponse = soapConnection.call(soapRequest, Constants.FIWebService);
			accountAndLimitNodeResponse = OperationUtillity.soapMessageToString(soapResponse);
			accountAndLimitNodeResponse = StringEscapeUtils.unescapeXml(accountAndLimitNodeResponse);
			if (accountAndLimitNodeResponse.contains("<HostTransaction>")) {
				HostTransaction = accountAndLimitNodeResponse.substring(
						accountAndLimitNodeResponse.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
						accountAndLimitNodeResponse.indexOf("</HostTransaction>"));
				if (HostTransaction.contains("<Status>")) {
					Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),
							HostTransaction.indexOf("</Status>"));
				}
			}
			if (Status.equalsIgnoreCase("SUCCESS")) {
				API_REQ_RES_map = xmlToMap.successPacketDataToMapACCLIMLinkage(pinstId, accountAndLimitNodeResponse);
				pojo.setStatus(API_REQ_RES_map.get("Status"));
				pojo.setServiceResponse(accountAndLimitNodeResponse);
				pojo.setRequestType(requestType);
				pojo.setReTrigger(false);
				serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
				// SN OF CHANGES BY MAHESHV ON 10122024.
//		if (!"Fresh Sanction".equalsIgnoreCase(parentModUtility.getProposalType(pinstId))
//			&& drgPwrUtils.getFlagForDrgPwrCheck(pinstId, facility)) {
//		    logger.info("Calling to drawing power check{}", pinstId);
//		    service.executeDrawingPowerCheck(pinstId, accountNumber);
//		}
				Map<String, String> DPAmountCheckMap = drgPwrUtils.getDPAmountForDPUpdate(pinstId, accountNumber);
				if (DPAmountCheckMap.size() > 0 && !parentModUtility.isLimitAsPerSanctionIsZero(pinstId,facility)) {
					try {   
						Thread.sleep(2000);   // ICO-10080
					} catch (InterruptedException e) {
						logger.info("Exception while waiting :: " + OperationUtillity.traceException(e));
					} 
					drawingPowerUpdationService.executeDrawingPowerUpdationService(pinstId, DPAmountCheckMap);
				}
				// EN OF CHANGES BY MAHESHV ON 10122024.
			} else {
				API_REQ_RES_map = xmlToMap.packetDataToMap(pinstId, accountAndLimitNodeResponse);
				pojo.setStatus(API_REQ_RES_map.get("Status"));
				pojo.setRequestType(requestType);
				pojo.setMessage(API_REQ_RES_map.get("ErrorDesc"));
				pojo.setServiceResponse(accountAndLimitNodeResponse);
				pojo.setReTrigger(true);
				serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			}
			OperationUtillity.API_RequestResponse_Insert(OperationUtillity.soapMessageToString(soapRequest),
					accountAndLimitNodeResponse, requestType, pinstId, API_REQ_RES_map, "");
		} catch (Exception Ex) {
			API_REQ_RES_map.put("RequestUUID", "");
			API_REQ_RES_map.put("MessageDateTime", "");
			API_REQ_RES_map.put("Error_Code", "500");
			API_REQ_RES_map.put("message", Ex.getMessage());
			API_REQ_RES_map.put("Status", "FAILED");
			API_REQ_RES_map.put("Error_At", "Account And Limit Node Linkage");
			API_REQ_RES_map.put("request Header", soapRequest.getSOAPHeader().toString());
			API_REQ_RES_map.put("request body", soapRequest.getSOAPBody().toString());
			API_REQ_RES_map.put("response", accountAndLimitNodeResponse);
			logger.info("AccountAndLimitNodeLinkageDao.accountAndLimitNodeLinkageDaoImpl()->"
					+ OperationUtillity.traceException(pinstId, Ex));
		}
		return API_REQ_RES_map;
	}
}
