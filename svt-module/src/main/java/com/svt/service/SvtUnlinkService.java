package com.svt.service;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.xml.soap.SOAPException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.svt.dao.CommonDaoForInqDlnkWtdrwl;
import com.svt.dao.FetchStatus;
import com.svt.model.commonModel.InnerPojo;
import com.svt.model.commonModel.MainPojo;
import com.svt.model.commonModel.serviceDetails.ServiceDetails;
import com.svt.utils.common.CommonDataUtility;
import com.svt.utils.common.OperationUtillity;
import com.svt.utils.common.SOAPRequestUtility;
import com.svt.utils.common.commonUtility;
import com.svt.utils.common.updateServiceDetails;
import com.svt.utils.common.xmlToMap;

@Service
public class SvtUnlinkService {

	@Autowired
	CommonDaoForInqDlnkWtdrwl svtUnlinkDao;

	@Autowired
	SOAPRequestUtility soapRequestUtility;

	@Autowired
	updateServiceDetails updtServiceDetails;

	@Autowired
	FetchStatus fetchStatus;

	private static final Logger logger = LoggerFactory.getLogger(SvtUnlinkService.class);

	PreparedStatement statement = null;
	ResultSet rs = null;

	public List<Object> unlinkCollateralDetailsImpl(String pinstid, String processName, String collateralId) {

		List<Object> resultList = new ArrayList<>();

		System.out.println("SvtUnlinkService.unlinkCollateralDetailsImpl().pinstid(" + pinstid + ")");

		ArrayList<MainPojo> subTypeSecurityList = svtUnlinkDao.fetchCommonData(pinstid, processName);

		for (MainPojo mainPojo : subTypeSecurityList) {
			for (InnerPojo innerPojo : mainPojo.getInnerPojo()) {

				try {
					String requestUuid = commonUtility.createRequestUUID();
					String dateAndTime = commonUtility.dateFormat();

					mainPojo.setSubTypeSecurity(innerPojo.getSubTypeSecurity());
					mainPojo.setTypeOfSecurity(innerPojo.getTypeOfSecurity());
					mainPojo.setProduct(innerPojo.getProduct());
					mainPojo.setLimitPrefix(innerPojo.getLimitPrefix());
					mainPojo.setLimitSuffix(innerPojo.getLimitSuffix());
					mainPojo.setCollateralCode(CommonDataUtility.getCollateralCode(innerPojo.getTypeOfSecurity()));
					mainPojo.setCollateralId(CommonDataUtility.getCollateralCode(innerPojo.getTypeOfSecurity()));
					mainPojo.setRequestId(requestUuid);
					mainPojo.setDateAndTime(dateAndTime);
					mainPojo.setColtrlLinkage("Yes");

				//	resultList.add(executeSvtUnlinkSrvcImpl(pinstid, mainPojo, processName,collateralId));

				} catch (Exception e) {

				}
			}
		}
		return resultList;
	}

	public Map<String, String> convertwithDrawalResToPojo(String pinstid, String svtWithdrawalResMsg, String status)
			throws JsonProcessingException, SQLException {

		Map<String, String> svtWithdrawalResMap = new WeakHashMap<>();

		if (!(status.equalsIgnoreCase("Success"))) {

			String message = svtWithdrawalResMsg.substring(
					svtWithdrawalResMsg.indexOf("<ErrorDesc>") + "<ErrorDesc>".length(),
					svtWithdrawalResMsg.indexOf("</ErrorDesc>"));

			svtWithdrawalResMap.put("ErrorDesc", message);

		} else {
			svtWithdrawalResMap.putAll(xmlToMap.packetDataToMap(pinstid, svtWithdrawalResMsg));
		}
		return svtWithdrawalResMap;
	}

	@SuppressWarnings({ "unused", "static-access" })
	public Map<String, String> executeSvtUnlinkSrvcImpl(String pinstid, String processName,String collateralId,String facilityname,String[] pref_suff) {
		Map<String, String> unlinkResMap = new HashMap<>();
		String requestPacket = "" , responePacket  = "", requestType  = "", Status = "";
		Boolean retriger = false;
		
		logger.info("SvtUnlinkService.executeSvtUnlinkSrvcImpl().pinstid ["+pinstid+"] IN ");
		
		 requestType = "SVT DELINK : "+facilityname +" : "+ collateralId;

		try {

			Boolean executeStatus = fetchStatus.getStatusForService(pinstid, requestType,processName);
			logger.info("SvtUnlinkService.executeSvtUnlinkSrvcImpl().pinstid [" + pinstid + "] executeStatus [" + executeStatus + "]");
			if (executeStatus) { // FI EXECUTION is not equall to SUCCESS

				 requestPacket = createunlinkCollateraldataPacket(collateralId,pref_suff);
//				unlinkResMap.put("limitPrefixSuffix",
//						unlinkReqPojo.getLimitPrefix() + "/" + unlinkReqPojo.getLimitSuffix());
				
				 responePacket = soapRequestUtility.soapResponse(requestPacket);
				 
				try {

					if (responePacket.contains("<HostTransaction>")) {
						String HostTransaction = responePacket.substring(
								responePacket.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
								responePacket.indexOf("</HostTransaction>"));
						if (HostTransaction.contains("<Status>")) {
							Status = HostTransaction.substring(
									HostTransaction.indexOf("<Status>") + "<Status>".length(),
									HostTransaction.indexOf("</Status>"));
							unlinkResMap.put("Status", Status);
						}
					}
					
					if (!"SUCCESS".equalsIgnoreCase(Status)) {
						retriger = true;
					}

					unlinkResMap.put(requestType, Status);
					
					updtServiceDetails.updateInitialStatusInFiExecutionTable(
							new ServiceDetails(pinstid, requestType, "SVT DELINK", requestType, Status, requestPacket,
									responePacket, unlinkResMap.get("ErrorDesc"), retriger));
					
					unlinkResMap.putAll(convertwithDrawalResToPojo(pinstid, responePacket, Status));

					if (processName.equalsIgnoreCase("Limit_Setup")) {
						OperationUtillity.API_RequestResponse_Insert(requestPacket, responePacket, requestType, pinstid,
								unlinkResMap, "");
					} else if (processName.equalsIgnoreCase("Monitoring")) {
						OperationUtillity.insertFiReqResMonitoring(requestPacket, responePacket, requestType, pinstid,
								unlinkResMap, "");
					}

				} catch (Exception e) {
					e.printStackTrace();
					logger.info("SvtUnlinkService.executeSvtUnlinkSrvcImpl()"
							+ OperationUtillity.traceException(pinstid, e));
					updtServiceDetails.updateInitialStatusInFiExecutionTable(
							new ServiceDetails(pinstid, requestType, "SVT DELINK", requestType, Status, requestPacket,
									responePacket, unlinkResMap.get("ErrorDesc"), retriger));
				}
			} else {
				unlinkResMap.put(requestType, "SUCCESS");
				unlinkResMap.put("DELINK : " + facilityname + " : " + collateralId, "SUCCESS");
			}

		} catch (IOException | SOAPException e) {
			e.printStackTrace();
			logger.info("Exception while getting Response = " + e.getMessage());
			
			updtServiceDetails.updateInitialStatusInFiExecutionTable(
					new ServiceDetails(pinstid, requestType, "SVT DELINK", requestType, "FAILURE", requestPacket,
							responePacket, e.getMessage(), false));
		}
		return unlinkResMap;
	}

	public String createunlinkCollateraldataPacket(String collateralId,String[] pref_suff) {
		logger.info("SvtUnlinkService.createunlinkCollateraldataPacket().collateralId = " + collateralId + " prefix ["+pref_suff[0]+ "] suffix [ "+pref_suff[1]+ "]");
		String rqstPacket = "";
		String requestUuid = commonUtility.createRequestUUID();
		String dateAndTime = commonUtility.dateFormat();
		
		try {
			rqstPacket = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.finacle.com/fixml unlinkCollateralDetails.xsd\">\r\n"
					+ "<Header>\r\n"
					+ "<RequestHeader>\r\n" 
					+ "<MessageKey>\r\n"
					+ "<RequestUUID>"+ requestUuid + "</RequestUUID>\r\n"
					+ "<ServiceRequestId>unlinkCollateralDetails</ServiceRequestId>\r\n"
					+ "<ServiceRequestVersion>10.2</ServiceRequestVersion>\r\n" 
					+ "<ChannelId>CLS</ChannelId>\r\n"
					+ "<LanguageId>\r\n" 
					+ "</LanguageId>\r\n" 
					+ "</MessageKey>\r\n" 
					+ "<RequestMessageInfo>\r\n"
					+ "<BankId>BM3</BankId>\r\n" 
					+ "<TimeZone>\r\n" 
					+ "</TimeZone>\r\n" 
					+ "<EntityId>\r\n"
					+ "</EntityId>\r\n" 
					+ "<EntityType> </EntityType>\r\n"
					+ "<ArmCorrelationId>\r\n"
					+ "</ArmCorrelationId>\r\n"
					+ "<MessageDateTime>" + dateAndTime+ "</MessageDateTime>\r\n" 
					+ "</RequestMessageInfo>\r\n"
					+ "<Security>\r\n"
					+ "<Token>\r\n"
					+ "<PasswordToken>\r\n" 
					+ "<UserId>\r\n" + "</UserId>\r\n" 
					+ "<Password>\r\n" + "</Password>\r\n"
					+ "</PasswordToken>\r\n" 
					+ "</Token>\r\n" 
					+ "<FICertToken>\r\n" + "</FICertToken>\r\n"
					+ "<RealUserLoginSessionId> </RealUserLoginSessionId>\r\n"
					+ "<RealUser>\r\n" + "</RealUser>\r\n"
					+ "<RealUserPwd>\r\n" + "</RealUserPwd>\r\n"
					+ "<SSOTransferToken>\r\n" + "</SSOTransferToken>\r\n"
					+ "</Security> </RequestHeader>\r\n"
					+ "</Header>\r\n" 
					+ "<Body>\r\n"
					+ "<CIColtrlUnLinkDtlsMsg>\r\n" 
					+ "<coltrlCrit>\r\n" 
					+ "<nodeId>\r\n"
					+ "<limitPrefix>"+ pref_suff[0] + "</limitPrefix>\r\n"
					+ "<limitSuffix>"+ pref_suff[1] + "</limitSuffix>\r\n"
					+ "</nodeId>\r\n"
					+ "<coltrlLinkage>N</coltrlLinkage>\r\n" // 
					+ "<coltrlSrlNum>" + collateralId + "</coltrlSrlNum>\r\n"
					+ "</coltrlCrit>\r\n" 
					+ "<withdrawReasonCode>WDRW</withdrawReasonCode>\r\n"
					+ "</CIColtrlUnLinkDtlsMsg>\r\n" 
					+ "</Body>\r\n" 
					+ "</FIXML>\r\n"
					+ "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception in InqueryscreateunlinkCollateraldataPacket" + OperationUtillity.traceException(e));
		}
		return rqstPacket;
	}

}
