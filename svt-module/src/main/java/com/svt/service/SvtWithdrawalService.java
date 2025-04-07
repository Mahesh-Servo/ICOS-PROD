package com.svt.service;

import java.io.IOException;
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
import com.svt.dao.FetchStatus;
import com.svt.dao.SvtWithdrawalDao;
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
public class SvtWithdrawalService {

	@Autowired
	SvtWithdrawalDao svtWithdrawalDao;

	@Autowired
	FetchStatus fetchStatus;

	@Autowired
	updateServiceDetails updtServiceDetails;

	private static final Logger logger = LoggerFactory.getLogger(SvtWithdrawalService.class);

	public List<Object> svtWithdrawlImpl(String pinstid, String securityName, String subTypeSecurity, String typeOfSvt,
			String Product, String limitPrefix, String limitSuffix, String processName, String collateralId) {

		List<Object> svtvWithDrawalResults = new ArrayList<>();

		System.out
				.println("SvtWithdrawalService.svtWithdrawlImpl().pinstid" + pinstid + ", securityName" + securityName);

		if (subTypeSecurity.equals("ALL")) {
			ArrayList<MainPojo> subTypeSecurityList = svtWithdrawalDao.fetchwithdrawal(pinstid, processName);

			for (MainPojo withDrawalMainPj : subTypeSecurityList) {

				for (InnerPojo SvtSecurityDetailsInnerPojo : withDrawalMainPj.getInnerPojo()) {

					String requestUuid = commonUtility.createRequestUUID();
					String dateAndTime = commonUtility.dateFormat();

					withDrawalMainPj.setSubTypeSecurity(SvtSecurityDetailsInnerPojo.getSubTypeSecurity());
					withDrawalMainPj.setTypeOfSecurity(SvtSecurityDetailsInnerPojo.getTypeOfSecurity());
					withDrawalMainPj.setProduct(SvtSecurityDetailsInnerPojo.getProduct());
					withDrawalMainPj.setLimitPrefix(SvtSecurityDetailsInnerPojo.getLimitPrefix());
					withDrawalMainPj.setLimitSuffix(SvtSecurityDetailsInnerPojo.getLimitPrefix());
					withDrawalMainPj.setCollateralCode(
							CommonDataUtility.getCollateralCode(SvtSecurityDetailsInnerPojo.getTypeOfSecurity()));
					withDrawalMainPj.setCollateralId("");
					withDrawalMainPj.setRequestId(requestUuid);
					withDrawalMainPj.setDateAndTime(dateAndTime);

					// svtvWithDrawalResults.add(withDrawalCollateralId(pinstid, withDrawalMainPj,
					// processName,collateralId));

				}
			}
		} else {
			// for individual
			MainPojo withDrawalIndiPojo = svtWithdrawalDao.fetchIndividualProductDtls(pinstid, securityName,
					subTypeSecurity, typeOfSvt, Product, limitPrefix, limitSuffix, processName);
			for (InnerPojo SvtSecurityDetailsInnerPojo : withDrawalIndiPojo.getInnerPojo()) {

				String requestUuid = commonUtility.createRequestUUID();
				String dateAndTime = commonUtility.dateFormat();

				withDrawalIndiPojo.setSubTypeSecurity(SvtSecurityDetailsInnerPojo.getSubTypeSecurity());
				withDrawalIndiPojo.setTypeOfSecurity(SvtSecurityDetailsInnerPojo.getTypeOfSecurity());
				withDrawalIndiPojo.setProduct(SvtSecurityDetailsInnerPojo.getProduct());
				withDrawalIndiPojo.setLimitPrefix(SvtSecurityDetailsInnerPojo.getLimitPrefix());
				withDrawalIndiPojo.setLimitSuffix(SvtSecurityDetailsInnerPojo.getLimitPrefix());
				withDrawalIndiPojo.setCollateralCode(
						CommonDataUtility.getCollateralCode(SvtSecurityDetailsInnerPojo.getTypeOfSecurity()));
				withDrawalIndiPojo.setCollateralId("");
				withDrawalIndiPojo.setRequestId(requestUuid);
				withDrawalIndiPojo.setDateAndTime(dateAndTime);

				// svtvWithDrawalResults.add(withDrawalCollateralId(pinstid, withDrawalIndiPojo,
				// processName,collateralId));
			}
		}

		return svtvWithDrawalResults;
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

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// for mainPojo
	public Map<String, String> withDrawalCollateralId(String pinstId, String processName, String facilityname,
			String collateralId, String secType, String secCode) {
		String soapRequestPacket = "";
		String rqstType = "";
		Map<String, String> lodgeCollateralResponsePojoList = new HashMap();
		try {

			rqstType = "SVT WITHDRAWAL : " + facilityname + " : " + secType + " : " + collateralId;

			switch (secType) {
			case "BOOK DEBTS": // BD
				soapRequestPacket = createBookDebtRequestPacket(pinstId, secType, collateralId, secCode);
				lodgeCollateralResponsePojoList.putAll(executeSoapMsgAndCreateRes(soapRequestPacket, pinstId,
						collateralId, processName, rqstType, facilityname));
				break;
			case "STOCK": // inv
				soapRequestPacket = createInventoryStockRequestPacket(pinstId, secType, collateralId, secCode);
				lodgeCollateralResponsePojoList.putAll(executeSoapMsgAndCreateRes(soapRequestPacket, pinstId,
						collateralId, processName, rqstType, facilityname));
				break;
			case "MACHINERIES, ETC": // MAC
				soapRequestPacket = createMachineryRequestPacket(pinstId, secType, collateralId, secCode);
				lodgeCollateralResponsePojoList.putAll(executeSoapMsgAndCreateRes(soapRequestPacket, pinstId,
						collateralId, processName, rqstType, facilityname));
				break;
			case "LIFE INSU. POLICIES/PLI": // LI

				soapRequestPacket = createLICRequestPacket(pinstId, secType, collateralId, secCode);
				lodgeCollateralResponsePojoList.putAll(executeSoapMsgAndCreateRes(soapRequestPacket, pinstId,
						collateralId, processName, rqstType, facilityname));

				break;

			case "IMMOVABLE PROPERTY": // IMM
				soapRequestPacket = createIMMRequestPacket(pinstId, secType, collateralId, secCode);
				lodgeCollateralResponsePojoList.putAll(executeSoapMsgAndCreateRes(soapRequestPacket, pinstId,
						collateralId, processName, rqstType, facilityname));
				break;
			case "MUTUAL FUNDS - UNITS": // MFU

				soapRequestPacket = createMutualFundsUnitRequestPacket(pinstId, secType, collateralId, secCode);
				lodgeCollateralResponsePojoList.putAll(executeSoapMsgAndCreateRes(soapRequestPacket, pinstId,
						collateralId, processName, rqstType, facilityname));
				break;
			case "OTHERS": // Other/
				soapRequestPacket = createOthersRequestPacket(pinstId, secType, collateralId, secCode);
				lodgeCollateralResponsePojoList.putAll(executeSoapMsgAndCreateRes(soapRequestPacket, pinstId,
						collateralId, processName, rqstType, facilityname));
				break;
			case "TRADEABLE SECURITIES": // TS
				soapRequestPacket = createTSRequestPacket(pinstId, secType, collateralId, secCode);
				lodgeCollateralResponsePojoList.putAll(executeSoapMsgAndCreateRes(soapRequestPacket, pinstId,
						collateralId, processName, rqstType, facilityname));
				break;

			case "DEPOSITS": // FD
				soapRequestPacket = createFDRequestPacket(pinstId, secType, collateralId, secCode);
				lodgeCollateralResponsePojoList.putAll(executeSoapMsgAndCreateRes(soapRequestPacket, pinstId,
						collateralId, processName, rqstType, facilityname));
				break;

			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("SvtWithdrawalService.withDrawalCollateralId()" + OperationUtillity.traceException(pinstId, e));
			updtServiceDetails.updateInitialStatusInFiExecutionTable(new ServiceDetails(pinstId, rqstType,
					"SVT WITHDRAWAL", rqstType, "FAILED", soapRequestPacket, "", e.getMessage(), false));
		}
		return lodgeCollateralResponsePojoList;
	}

	public Map<String, String> executeSoapMsgAndCreateRes(String soapRequestPacket, String pinstid, String collateralId,
			String processName, String rqstType, String facilityname) throws IOException, SOAPException {
		Map<String, String> svtWithdrawalResMap = new HashMap<>();
		String Status = "";
		Boolean retriger = false;

		Boolean executeStatus = fetchStatus.getStatusForService(pinstid, rqstType, processName);

		if (executeStatus) {
			String svtWithdrawalResMsg = SOAPRequestUtility.soapResponse(soapRequestPacket);
			try {

				if (svtWithdrawalResMsg.contains("<HostTransaction>")) {
					String HostTransaction = svtWithdrawalResMsg.substring(
							svtWithdrawalResMsg.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
							svtWithdrawalResMsg.indexOf("</HostTransaction>"));
					if (HostTransaction.contains("<Status>")) {
						Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),
								HostTransaction.indexOf("</Status>"));
						svtWithdrawalResMap.put("Status", Status);
					}
				}
				svtWithdrawalResMap.putAll(convertwithDrawalResToPojo(pinstid, svtWithdrawalResMsg, Status));
				svtWithdrawalResMap.put(rqstType, Status);
				svtWithdrawalResMap.put("REQUEST_TYPE", rqstType);

				
				if (!"SUCCESS".equalsIgnoreCase(Status)) {
					retriger = true;
				}

				updtServiceDetails.updateInitialStatusInFiExecutionTable(
						new ServiceDetails(pinstid, rqstType, "SVT WITHDRAWAL", rqstType, Status, soapRequestPacket,
								svtWithdrawalResMsg, svtWithdrawalResMap.get("ErrorDesc"), retriger));

				if (processName.equalsIgnoreCase("Limit_Setup")) {
					OperationUtillity.API_RequestResponse_Insert(soapRequestPacket, svtWithdrawalResMsg, rqstType,
							pinstid, svtWithdrawalResMap, "");
				} else if (processName.equalsIgnoreCase("Monitoring")) {
					OperationUtillity.insertFiReqResMonitoring(soapRequestPacket, svtWithdrawalResMsg, rqstType,
							pinstid, svtWithdrawalResMap, "");
				}

			} catch (Exception e) {
				e.printStackTrace();
				logger.info("SvtWithdrawalService.executeSoapMsgAndCreateRes()"
						+ OperationUtillity.traceException(pinstid, e));
				updtServiceDetails.updateInitialStatusInFiExecutionTable(
						new ServiceDetails(pinstid, rqstType, "SVT WITHDRAWAL", rqstType, Status, soapRequestPacket,
								svtWithdrawalResMsg, svtWithdrawalResMap.get("ErrorDesc"), retriger));
			}
		} else {
			svtWithdrawalResMap.put(rqstType, "SUCCESS");
		}
		svtWithdrawalResMap.put("WITHDRAWAL : " + facilityname + " : " + collateralId, Status);
		return svtWithdrawalResMap;
	}

	public String createBookDebtRequestPacket(String pinstId, String secType, String collateralId, String secCode) {

		String rqstPacket = "";
		try {
			String requestUuid = commonUtility.createRequestUUID();
			String dateAndTime = commonUtility.dateFormat();

			rqstPacket = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<FIXML xsi:schemaLocation=\"http://www.finacle.com/fixml executeFinacleScript.xsd\" xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
					+ "<Header>\r\n" + "<RequestHeader>\r\n" + "<MessageKey>\r\n" + "<RequestUUID>" + requestUuid
					+ "</RequestUUID>\r\n" + "<ServiceRequestId>executeFinacleScript</ServiceRequestId>\r\n"
					+ "<ServiceRequestVersion>10.2</ServiceRequestVersion>\r\n" + "<ChannelId>UAT</ChannelId>\r\n"
					+ "</MessageKey>\r\n" + "<RequestMessageInfo>\r\n" + "<BankId>BM3</BankId>\r\n"
					+ "<ArmCorrelationId>\r\n" + "</ArmCorrelationId>\r\n" + "<MessageDateTime>" + dateAndTime
					+ "</MessageDateTime>\r\n" + "</RequestMessageInfo>\r\n" + "</RequestHeader>\r\n" + "</Header>\r\n"
					+ "<Body>\r\n" + "<executeFinacleScriptRequest>\r\n" + "<ExecuteFinacleScriptInputVO>\r\n"
					+ "<requestId>FI_CMN_WithDrawColl.scr</requestId>\r\n" + "</ExecuteFinacleScriptInputVO>\r\n"
					+ "<executeFinacleScript_CustomData>\r\n" + "<CollateralId>" + collateralId + "</CollateralId>\r\n"
					+ "<Coll_Code>" + secCode + "</Coll_Code>\r\n" + "<Remarks>" + pinstId + "</Remarks>\r\n"
					+ "<ServiceCode>BOOKDEBT</ServiceCode>\r\n" + // BOOKDEBT
					"</executeFinacleScript_CustomData>\r\n" + "</executeFinacleScriptRequest>\r\n" + "</Body>\r\n"
					+ "</FIXML>\r\n" + "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("lodgeCollateralService.createBookDebtRequestPacket()" + OperationUtillity.traceException(e));
		}
		return rqstPacket;
	}

	public String createInventoryStockRequestPacket(String pinstId, String secType, String collateralId,
			String secCode) {
		String rqstPacket = "";
		try {
			String requestUuid = commonUtility.createRequestUUID();
			String dateAndTime = commonUtility.dateFormat();

			rqstPacket = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<FIXML xsi:schemaLocation=\"http://www.finacle.com/fixml executeFinacleScript.xsd\" xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
					+ "<Header>\r\n" + "<RequestHeader>\r\n" + "<MessageKey>\r\n" + "<RequestUUID>" + requestUuid
					+ "</RequestUUID>\r\n" + "<ServiceRequestId>executeFinacleScript</ServiceRequestId>\r\n"
					+ "<ServiceRequestVersion>10.2</ServiceRequestVersion>\r\n" + "<ChannelId>UAT</ChannelId>\r\n"
					+ "</MessageKey>\r\n" + "<RequestMessageInfo>\r\n" + "<BankId>BM3</BankId>\r\n"
					+ "<ArmCorrelationId>\r\n" + "</ArmCorrelationId>\r\n" + "<MessageDateTime>" + dateAndTime
					+ "</MessageDateTime>\r\n" + "</RequestMessageInfo>\r\n" + "</RequestHeader>\r\n" + "</Header>\r\n"
					+ "<Body>\r\n" + "<executeFinacleScriptRequest>\r\n" + "<ExecuteFinacleScriptInputVO>\r\n"
					+ "<requestId>FI_CMN_WithDrawColl.scr</requestId>\r\n" + "</ExecuteFinacleScriptInputVO>\r\n"
					+ "<executeFinacleScript_CustomData>\r\n" + "<CollateralId>" + collateralId + "</CollateralId>\r\n"
					+ "<Coll_Code>" + secCode + "</Coll_Code>\r\n" + "<Remarks>" + pinstId + "</Remarks>\r\n"
					+ "<ServiceCode>INVSTOCK</ServiceCode>\r\n" + // INVSTOCK
					"</executeFinacleScript_CustomData>\r\n" + "</executeFinacleScriptRequest>\r\n" + "</Body>\r\n"
					+ "</FIXML>\r\n" + "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";

		} catch (Exception e) {
			e.printStackTrace();
			logger.info(
					"lodgeCollateralService.createInventoryStockRequestPacket()" + OperationUtillity.traceException(e));
		}
		return rqstPacket;
	}

	public String createMachineryRequestPacket(String pinstId, String secType, String collateralId, String secCode) {

		String rqstPacket = "";
		try {
			String requestUuid = commonUtility.createRequestUUID();
			String dateAndTime = commonUtility.dateFormat();

			rqstPacket = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<FIXML xsi:schemaLocation=\"http://www.finacle.com/fixml executeFinacleScript.xsd\" xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
					+ "<Header>\r\n" + "<RequestHeader>\r\n" + "<MessageKey>\r\n" + "<RequestUUID>" + requestUuid
					+ "</RequestUUID>\r\n" + "<ServiceRequestId>executeFinacleScript</ServiceRequestId>\r\n"
					+ "<ServiceRequestVersion>10.2</ServiceRequestVersion>\r\n" + "<ChannelId>UAT</ChannelId>\r\n"
					+ "</MessageKey>\r\n" + "<RequestMessageInfo>\r\n" + "<BankId>BM3</BankId>\r\n"
					+ "<ArmCorrelationId>\r\n" + "</ArmCorrelationId>\r\n" + "<MessageDateTime>" + dateAndTime
					+ "</MessageDateTime>\r\n" + "</RequestMessageInfo>\r\n" + "</RequestHeader>\r\n" + "</Header>\r\n"
					+ "<Body>\r\n" + "<executeFinacleScriptRequest>\r\n" + "<ExecuteFinacleScriptInputVO>\r\n"
					+ "<requestId>FI_CMN_WithDrawColl.scr</requestId>\r\n" + "</ExecuteFinacleScriptInputVO>\r\n"
					+ "<executeFinacleScript_CustomData>\r\n" + "<CollateralId>" + collateralId + "</CollateralId>\r\n"
					+ "<Coll_Code>" + secCode + "</Coll_Code>\r\n" + "<Remarks>" + pinstId + "</Remarks>\r\n"
					+ "<ServiceCode>MACHINERY</ServiceCode>\r\n" + // MACHINERY
					"</executeFinacleScript_CustomData>\r\n" + "</executeFinacleScriptRequest>\r\n" + "</Body>\r\n"
					+ "</FIXML>\r\n" + "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("lodgeCollateralService.createMachineryRequestPacket()" + OperationUtillity.traceException(e));
		}
		return rqstPacket;
	}

	public String createLICRequestPacket(String pinstId, String secType, String collateralId, String secCode) {

		String rqstPacket = "";
		try {
			String requestUuid = commonUtility.createRequestUUID();
			String dateAndTime = commonUtility.dateFormat();

			rqstPacket = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<FIXML xsi:schemaLocation=\"http://www.finacle.com/fixml executeFinacleScript.xsd\" xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
					+ "<Header>\r\n" + "<RequestHeader>\r\n" + "<MessageKey>\r\n" + "<RequestUUID>" + requestUuid
					+ "</RequestUUID>\r\n" + "<ServiceRequestId>executeFinacleScript</ServiceRequestId>\r\n"
					+ "<ServiceRequestVersion>10.2</ServiceRequestVersion>\r\n" + "<ChannelId>UAT</ChannelId>\r\n"
					+ "</MessageKey>\r\n" + "<RequestMessageInfo>\r\n" + "<BankId>BM3</BankId>\r\n"
					+ "<ArmCorrelationId>\r\n" + "</ArmCorrelationId>\r\n" + "<MessageDateTime>" + dateAndTime
					+ "</MessageDateTime>\r\n" + "</RequestMessageInfo>\r\n" + "</RequestHeader>\r\n" + "</Header>\r\n"
					+ "<Body>\r\n" + "<executeFinacleScriptRequest>\r\n" + "<ExecuteFinacleScriptInputVO>\r\n"
					+ "<requestId>FI_CMN_WithDrawColl.scr</requestId>\r\n" + "</ExecuteFinacleScriptInputVO>\r\n"
					+ "<executeFinacleScript_CustomData>\r\n" + "<CollateralId>" + collateralId + "</CollateralId>\r\n"
					+ "<Coll_Code>" + secCode + "</Coll_Code>\r\n" + "<Remarks>" + pinstId + "</Remarks>\r\n"
					+ "<ServiceCode>LIFEINSU</ServiceCode>\r\n" + // LIFEINSU
					"</executeFinacleScript_CustomData>\r\n" + "</executeFinacleScriptRequest>\r\n" + "</Body>\r\n"
					+ "</FIXML>\r\n" + "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("lodgeCollateralService.createLICRequestPacket()" + OperationUtillity.traceException(e));
		}
		return rqstPacket;
	}

	// pending - not given
	public String createMutualFundsUnitRequestPacket(String pinstId, String secType, String collateralId,
			String secCode) {

		String rqstPacket = "";
		try {
			String requestUuid = commonUtility.createRequestUUID();
			String dateAndTime = commonUtility.dateFormat();

			rqstPacket = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<FIXML xsi:schemaLocation=\"http://www.finacle.com/fixml executeFinacleScript.xsd\" xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
					+ "<Header>\r\n" + "<RequestHeader>\r\n" + "<MessageKey>\r\n" + "<RequestUUID>" + requestUuid
					+ "</RequestUUID>\r\n" + "<ServiceRequestId>executeFinacleScript</ServiceRequestId>\r\n"
					+ "<ServiceRequestVersion>10.2</ServiceRequestVersion>\r\n" + "<ChannelId>UAT</ChannelId>\r\n"
					+ "</MessageKey>\r\n" + "<RequestMessageInfo>\r\n" + "<BankId>BM3</BankId>\r\n"
					+ "<ArmCorrelationId>\r\n" + "</ArmCorrelationId>\r\n" + "<MessageDateTime>" + dateAndTime
					+ "</MessageDateTime>\r\n" + "</RequestMessageInfo>\r\n" + "</RequestHeader>\r\n" + "</Header>\r\n"
					+ "<Body>\r\n" + "<executeFinacleScriptRequest>\r\n" + "<ExecuteFinacleScriptInputVO>\r\n"
					+ "<requestId>FI_CMN_WithDrawColl.scr</requestId>\r\n" + "</ExecuteFinacleScriptInputVO>\r\n"
					+ "<executeFinacleScript_CustomData>\r\n" + "<CollateralId>" + collateralId + "</CollateralId>\r\n"
					+ "<Coll_Code>" + secCode + "</Coll_Code>\r\n" + "<Remarks>" + pinstId + "</Remarks>\r\n"
					+ "<ServiceCode>MUTUALFUND</ServiceCode>\r\n" + // MUTUALFUND
					"</executeFinacleScript_CustomData>\r\n" + "</executeFinacleScriptRequest>\r\n" + "</Body>\r\n"
					+ "</FIXML>\r\n" + "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("lodgeCollateralService.createMutualFundsUnitRequestPacket()"
					+ OperationUtillity.traceException(e));
		}
		return rqstPacket;
	}

	public String createOthersRequestPacket(String pinstId, String secType, String collateralId, String secCode) {

		String rqstPacket = "";
		try {
			String requestUuid = commonUtility.createRequestUUID();
			String dateAndTime = commonUtility.dateFormat();

			rqstPacket = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<FIXML xsi:schemaLocation=\"http://www.finacle.com/fixml executeFinacleScript.xsd\" xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
					+ "<Header>\r\n" + "<RequestHeader>\r\n" + "<MessageKey>\r\n" + "<RequestUUID>" + requestUuid
					+ "</RequestUUID>\r\n" + "<ServiceRequestId>executeFinacleScript</ServiceRequestId>\r\n"
					+ "<ServiceRequestVersion>10.2</ServiceRequestVersion>\r\n" + "<ChannelId>UAT</ChannelId>\r\n"
					+ "</MessageKey>\r\n" + "<RequestMessageInfo>\r\n" + "<BankId>BM3</BankId>\r\n"
					+ "<ArmCorrelationId>\r\n" + "</ArmCorrelationId>\r\n" + "<MessageDateTime>" + dateAndTime
					+ "</MessageDateTime>\r\n" + "</RequestMessageInfo>\r\n" + "</RequestHeader>\r\n" + "</Header>\r\n"
					+ "<Body>\r\n" + "<executeFinacleScriptRequest>\r\n" + "<ExecuteFinacleScriptInputVO>\r\n"
					+ "<requestId>FI_CMN_WithDrawColl.scr</requestId>\r\n" + "</ExecuteFinacleScriptInputVO>\r\n"
					+ "<executeFinacleScript_CustomData>\r\n" + "<CollateralId>" + collateralId + "</CollateralId>\r\n"
					+ "<Coll_Code>" + secCode + "</Coll_Code>\r\n" + "<Remarks>" + pinstId + "</Remarks>\r\n"
					+ "<ServiceCode>OTHER</ServiceCode>\r\n" + // OTHER
					"</executeFinacleScript_CustomData>\r\n" + "</executeFinacleScriptRequest>\r\n" + "</Body>\r\n"
					+ "</FIXML>" + "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("lodgeCollateralService.createOthersRequestPacket()" + OperationUtillity.traceException(e));
		}
		return rqstPacket;
	}

	public String createTSRequestPacket(String pinstId, String secType, String collateralId, String secCode) {
		String rqstPacket = "";
		try {

			String requestUuid = commonUtility.createRequestUUID();
			String dateAndTime = commonUtility.dateFormat();

			rqstPacket = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<FIXML xsi:schemaLocation=\"http://www.finacle.com/fixml executeFinacleScript.xsd\" xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
					+ "<Header>\r\n" + "<RequestHeader>\r\n" + "<MessageKey>\r\n" + "<RequestUUID>" + requestUuid
					+ "</RequestUUID>\r\n" + "<ServiceRequestId>executeFinacleScript</ServiceRequestId>\r\n"
					+ "<ServiceRequestVersion>10.2</ServiceRequestVersion>\r\n" + "<ChannelId>UAT</ChannelId>\r\n"
					+ "</MessageKey>\r\n" + "<RequestMessageInfo>\r\n" + "<BankId>BM3</BankId>\r\n"
					+ "<ArmCorrelationId>\r\n" + "</ArmCorrelationId>\r\n" + "<MessageDateTime>" + dateAndTime
					+ "</MessageDateTime>\r\n" + "</RequestMessageInfo>\r\n" + "</RequestHeader>\r\n" + "</Header>\r\n"
					+ "<Body>\r\n" + "<executeFinacleScriptRequest>\r\n" + "<ExecuteFinacleScriptInputVO>\r\n"
					+ "<requestId>FI_CMN_WithDrawColl.scr</requestId>\r\n" + "</ExecuteFinacleScriptInputVO>\r\n"
					+ "<executeFinacleScript_CustomData>\r\n" + "<CollateralId>" + collateralId + "</CollateralId>\r\n"
					+ "<Coll_Code>" + secCode + "</Coll_Code>\r\n" + "<Remarks>" + pinstId + "</Remarks>\r\n"
					+ "<ServiceCode>TRADSEC</ServiceCode>\r\n" + // TRADSEC
					"</executeFinacleScript_CustomData>\r\n" + "</executeFinacleScriptRequest>\r\n" + "</Body>\r\n"
					+ "</FIXML>\r\n" + "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("lodgeCollateralService.createTSRequestPacket()" + OperationUtillity.traceException(e));
		}
		return rqstPacket;
	}

	public String createFDRequestPacket(String pinstId, String secType, String collateralId, String secCode) {

		String rqstPacket = "";
		try {
			String requestUuid = commonUtility.createRequestUUID();
			String dateAndTime = commonUtility.dateFormat();

			rqstPacket = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<FIXML xsi:schemaLocation=\"http://www.finacle.com/fixml executeFinacleScript.xsd\" xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
					+ "<Header>\r\n" + "<RequestHeader>\r\n" + "<MessageKey>\r\n" + "<RequestUUID>" + requestUuid
					+ "</RequestUUID>\r\n" + "<ServiceRequestId>executeFinacleScript</ServiceRequestId>\r\n"
					+ "<ServiceRequestVersion>10.2</ServiceRequestVersion>\r\n" + "<ChannelId>UAT</ChannelId>\r\n"
					+ "</MessageKey>\r\n" + "<RequestMessageInfo>\r\n" + "<BankId>BM3</BankId>\r\n"
					+ "<ArmCorrelationId>\r\n" + "</ArmCorrelationId>\r\n" + "<MessageDateTime>" + dateAndTime
					+ "</MessageDateTime>\r\n" + "</RequestMessageInfo>\r\n" + "</RequestHeader>\r\n" + "</Header>\r\n"
					+ "<Body>\r\n" + "<executeFinacleScriptRequest>\r\n" + "<ExecuteFinacleScriptInputVO>\r\n"
					+ "<requestId>FI_CMN_WithDrawColl.scr</requestId>\r\n" + "</ExecuteFinacleScriptInputVO>\r\n"
					+ "<executeFinacleScript_CustomData>\r\n" + "<CollateralId>" + collateralId + "</CollateralId>\r\n"
					+ "<Coll_Code>" + secCode + "</Coll_Code>\r\n" + "<Remarks>" + pinstId + "</Remarks>\r\n"
					+ "<ServiceCode>DEPOSIT</ServiceCode>\r\n" + // DEPOSIT
					"</executeFinacleScript_CustomData>\r\n" + "</executeFinacleScriptRequest>\r\n" + "</Body>\r\n"
					+ "</FIXML>" + "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("lodgeCollateralService.createFDRequestPacket()" + OperationUtillity.traceException(e));
		}
		return rqstPacket;
	}

	public String createIMMRequestPacket(String pinstId, String secType, String collateralId, String secCode) {

		String rqstPacket = "";
		try {
			String requestUuid = commonUtility.createRequestUUID();
			String dateAndTime = commonUtility.dateFormat();

			rqstPacket = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<FIXML xsi:schemaLocation=\"http://www.finacle.com/fixml executeFinacleScript.xsd\" xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
					+ "<Header>\r\n" + "<RequestHeader>\r\n" + "<MessageKey>\r\n" + "<RequestUUID>" + requestUuid
					+ "</RequestUUID>\r\n" + "<ServiceRequestId>executeFinacleScript</ServiceRequestId>\r\n"
					+ "<ServiceRequestVersion>10.2</ServiceRequestVersion>\r\n" + "<ChannelId>UAT</ChannelId>\r\n"
					+ "</MessageKey>\r\n" + "<RequestMessageInfo>\r\n" + "<BankId>BM3</BankId>\r\n"
					+ "<ArmCorrelationId>\r\n" + "</ArmCorrelationId>\r\n" + "<MessageDateTime>" + dateAndTime
					+ "</MessageDateTime>\r\n" + "</RequestMessageInfo>\r\n" + "</RequestHeader>\r\n" + "</Header>\r\n"
					+ "<Body>\r\n" + "<executeFinacleScriptRequest>\r\n" + "<ExecuteFinacleScriptInputVO>\r\n"
					+ "<requestId>FI_CMN_WithDrawColl.scr</requestId>\r\n" + "</ExecuteFinacleScriptInputVO>\r\n"
					+ "<executeFinacleScript_CustomData>\r\n" + "<CollateralId>" + collateralId + "</CollateralId>\r\n"
					+ "<Coll_Code>" + secCode + "</Coll_Code>\r\n" + "<Remarks>" + pinstId + "</Remarks>\r\n"
					+ "<ServiceCode>IMMOVPROP</ServiceCode>\r\n" + // IMMOVPROP
					"</executeFinacleScript_CustomData>\r\n" + "</executeFinacleScriptRequest>\r\n" + "</Body>\r\n"
					+ "</FIXML>" + "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("lodgeCollateralService.createFDRequestPacket()" + OperationUtillity.traceException(e));
		}
		return rqstPacket;
	}

}
