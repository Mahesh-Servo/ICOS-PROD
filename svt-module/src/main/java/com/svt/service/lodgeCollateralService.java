//package com.svt.service;
//
//import java.io.IOException;
//import java.sql.SQLException;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.WeakHashMap;
//import javax.xml.soap.SOAPException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.svt.dao.CommonDaoForInqDlnkWtdrwl;
//import com.svt.dao.FetchStatus;
//import com.svt.dao.SvtMonitoringDao;
//import com.svt.dao.lodgeCollateralDaoImpl;
//import com.svt.model.commonModel.InnerPojo;
//import com.svt.model.commonModel.MainPojo;
//import com.svt.model.commonModel.serviceDetails.ServiceDetails;
//import com.svt.utils.common.OperationUtillity;
//import com.svt.utils.common.SOAPRequestUtility;
//import com.svt.utils.common.commonUtility;
//import com.svt.utils.common.updateServiceDetails;
//import com.svt.utils.common.xmlToMap;
//
//@Service
//public class lodgeCollateralService {
//
//	private static final Logger logger = LoggerFactory.getLogger(lodgeCollateralService.class);
//
//	@Autowired
//	lodgeCollateralDaoImpl daoImpl;
//
//	@Autowired
//	CommonDaoForInqDlnkWtdrwl commonDaoForInqDlnkWtdrwl;
//
//	@Autowired
//	FetchStatus fetchStatus;
//
//	@Autowired
//	updateServiceDetails updtServiceDetails;
//
//	@Autowired
//	LodgeCollateralLinkageService lodgeCollaterallinkageSrvc;
//
//	@Autowired
//	SvtMonitoringDao svtMonDao;
//
//	public Object executeLodgeCollateralService(String pinstid, String securityName, String subTypeSecurity,
//			String typeOfSvt, String product, String processName, String limitType) throws Exception {
//
//		logger.info("lodgeCollateralService.executeLodgeCollateralService().pinstId : " + pinstid + " securityName "
//				+ securityName);
//
//		List<Object> lodgeCollateralResult = new ArrayList<>();
//		if (subTypeSecurity.equals("ALL")) {
//			ArrayList<MainPojo> subTypeSecurityList = commonDaoForInqDlnkWtdrwl.fetchCommonData(pinstid, processName);
//
//			for (MainPojo MainPojo : subTypeSecurityList) {
//
//				// security created to be check for production
////				if (OperationUtillity.NullReplace(lodgeCollateralSec.getSecurity_Created()).equalsIgnoreCase("Yes")) {
//
//				for (InnerPojo svtSecDtls : MainPojo.getInnerPojo()) {
//					try {
//						Map<String, String> resultMap = new HashMap<>();
//						String requestUuid = commonUtility.createRequestUUID();
//						String dateAndTime = commonUtility.dateFormat();
//
//						MainPojo.setRequestId(requestUuid);
//						MainPojo.setDateAndTime(dateAndTime);
//						MainPojo.setSubTypeSecurity(svtSecDtls.getSubTypeSecurity());
//						MainPojo.setTypeOfSecurity(svtSecDtls.getTypeOfSecurity());
//						MainPojo.setProduct(svtSecDtls.getProduct());
//						MainPojo.setLimitPrefix(svtSecDtls.getLimitPrefix());
//						MainPojo.setLimitSuffix(svtSecDtls.getLimitSuffix());
//						MainPojo.setPolicy_No(svtSecDtls.getPolicyNumber());
//						MainPojo.setPolicy_Amt(svtSecDtls.getPolicyAmount());
//						MainPojo.setColtrlLinkage("Yes");
//						MainPojo.setValSubTypeSecInMn(svtSecDtls.getValSubTypeSecMn());
//						MainPojo.setSecurityValueInMn(svtSecDtls.getSecurityValueMn());
//						MainPojo.setNameofHoldingStock(svtSecDtls.getNameofHoldingStock());
//						MainPojo.setUnitValue(svtSecDtls.getUnitValue());
//						MainPojo.setNoOfUnits(svtSecDtls.getNoOfUnits());
//						MainPojo.setCollateralCode(daoImpl.getCollateralCode(MainPojo.getTypeOfSecurity()));
//						MainPojo.setTypeOfCharge(svtSecDtls.getTypeOfCharge());
//						commonDaoForInqDlnkWtdrwl.getSecurityOtherDetails(pinstid, MainPojo);
//
//						String rqstType = OperationUtillity.NullReplace(MainPojo.getSecurityName()) + " : "
//								+ OperationUtillity.NullReplace(MainPojo.getSubTypeSecurity()) + " : "
//								+ OperationUtillity.NullReplace(MainPojo.getTypeOfSecurity()) + " : "
//								+ OperationUtillity.NullReplace(MainPojo.getProduct());
//
//						logger.info(
//								"\n[100]lodgeCollateralService.executeLodgeCollateralService(ALL).pinstid(" + pinstid
//										+ ").rqstType(" + rqstType + ").mainPojo[InnerPojo] = " + MainPojo.toString());
//
//						resultMap.putAll(LodgeSubTypeSecCollateral(pinstid, MainPojo, processName, limitType));
//						lodgeCollateralResult.add(resultMap);
//						if (OperationUtillity.NullReplace(resultMap.get("LODGE COLLATERAL : " + rqstType))
//								.equals("SUCCESS")
//								|| OperationUtillity
//										.NullReplace(resultMap.get("LODGE COLLATERAL : " + rqstType + " : "
//												+ OperationUtillity.NullReplace(MainPojo.getPolicy_No())))
//										.equals("SUCCESS")) {
//							resultMap.putAll(lodgeCollaterallinkageSrvc.LodgeSubTypeSecCollateralLinkage(pinstid,
//									MainPojo, svtSecDtls, processName));
//
//							logger.info("CommonService.executeServices().pinstid(" + pinstid
//									+ ").lodgeCollaterallinkageSrvc = " + resultMap.toString());
//						} else {
//							resultMap.put("LODGE COLLATERAL : " + rqstType + " : "
//									+ OperationUtillity.NullReplace(MainPojo.getPolicy_No()), "Not Executed");
//
////							updtServiceDetails.updateInitialStatusInFiExecutionTable(
////									new ServiceDetails(pinstid, "LODGE COLLATERAL : " + rqstType, "LODGE COLLATERAL",
////											"LODGE COLLATERAL : " + rqstType, "LODGE COLLATERAL FAILS,SVT WITHDRAWAL FAILED Hence not executed", "", "",
////											"SVT WITHDRAWAL fails,SVT WITHDRAWAL executes with Lodge Collateral if requires.", true));
//						}
//
//					} catch (Exception e) {
//						logger.info(OperationUtillity.traceException(pinstid, e));
//					}
//				}
////			}
//			}
//		} else {
//			MainPojo lodgeCollateralSec = commonDaoForInqDlnkWtdrwl.fetchIndividualProductDtls(pinstid, securityName,
//					subTypeSecurity, typeOfSvt, product, processName);
//
//			logger.info("lodgeCollateralService.executeLodgeCollateralService().pinstId : [" + pinstid
//					+ "] securityName [" + securityName + "] subTypeSecurity [" + subTypeSecurity + "]");
//
//			// security created to be check for production
////			if (OperationUtillity.NullReplace(lodgeCollateralSec.getSecurity_Created()).equalsIgnoreCase("Yes")) {
//
//			for (InnerPojo svtSecDtls : lodgeCollateralSec.getInnerPojo()) {
//				try {
//					Map<String, String> resultMap = new HashMap();
//					if (daoImpl.checkServiceStatus(pinstid, svtSecDtls, lodgeCollateralSec)) {
//
//						String requestUuid = commonUtility.createRequestUUID();
//						String dateAndTime = commonUtility.dateFormat();
//
//						lodgeCollateralSec.setRequestId(requestUuid);
//						lodgeCollateralSec.setDateAndTime(dateAndTime);
//
//						lodgeCollateralSec.setSubTypeSecurity(svtSecDtls.getSubTypeSecurity());
//						lodgeCollateralSec.setTypeOfSecurity(svtSecDtls.getTypeOfSecurity());
//						lodgeCollateralSec.setProduct(svtSecDtls.getProduct());
//						lodgeCollateralSec.setPolicy_No(svtSecDtls.getPolicyNumber());
//						lodgeCollateralSec.setPolicy_Amt(svtSecDtls.getPolicyAmount());
//						lodgeCollateralSec.setTypeOfCharge(svtSecDtls.getTypeOfCharge());
//						lodgeCollateralSec.setValSubTypeSecInMn(svtSecDtls.getValSubTypeSecMn());
//						lodgeCollateralSec.setSecurityValueInMn(svtSecDtls.getSecurityValueMn());
//						lodgeCollateralSec.setNameofHoldingStock(svtSecDtls.getNameofHoldingStock());
//						lodgeCollateralSec.setUnitValue(svtSecDtls.getUnitValue());
//						lodgeCollateralSec.setNoOfUnits(svtSecDtls.getNoOfUnits());
//						commonDaoForInqDlnkWtdrwl.getSecurityOtherDetails(pinstid, lodgeCollateralSec);
//
//						String rqstType = OperationUtillity.NullReplace(lodgeCollateralSec.getSecurityName()) + " : "
//								+ OperationUtillity.NullReplace(lodgeCollateralSec.getSubTypeSecurity()) + " : "
//								+ OperationUtillity.NullReplace(lodgeCollateralSec.getTypeOfSecurity()) + " : "
//								+ OperationUtillity.NullReplace(lodgeCollateralSec.getProduct());
//
//						logger.info("\n[400]lodgeCollateralService.executeLodgeCollateralService(else).pinstid("
//								+ pinstid + ").rqstType(" + rqstType + ").mainPojo[InnerPojo] = "
//								+ lodgeCollateralSec.toString());
//
//						lodgeCollateralSec
//								.setCollateralCode(daoImpl.getCollateralCode(lodgeCollateralSec.getTypeOfSecurity()));
//
//						resultMap
//								.putAll(LodgeSubTypeSecCollateral(pinstid, lodgeCollateralSec, processName, limitType));
//						lodgeCollateralResult.add(resultMap);
//
//						if (OperationUtillity.NullReplace(resultMap.get("LODGE COLLATERAL : " + rqstType))
//								.equals("SUCCESS")
//								|| OperationUtillity
//										.NullReplace(resultMap.get("LODGE COLLATERAL : " + rqstType + " : "
//												+ OperationUtillity.NullReplace(lodgeCollateralSec.getPolicy_No())))
//										.equals("SUCCESS")) {
//							resultMap.putAll(lodgeCollaterallinkageSrvc.LodgeSubTypeSecCollateralLinkage(pinstid,
//									lodgeCollateralSec, svtSecDtls, processName));
//
//							logger.info("CommonService.executeServices().pinstid(" + pinstid
//									+ ").lodgeCollaterallinkageSrvc = " + resultMap.toString());
//						} else {
//							resultMap.put(
//									"LODGE COLLATERAL : " + rqstType + " : "
//											+ OperationUtillity.NullReplace(lodgeCollateralSec.getPolicy_No()),
//									"Not Executed");
//
//						}
//					}
//				} catch (Exception e) {
//					logger.info(OperationUtillity.traceException(pinstid, e));
//				}
//			}
////			}
//		}
//		logger.info("\nlodgeCollateralService.executeLodgeCollateralService() lodgeCollateralResult ->"
//				+ lodgeCollateralResult);
//
//		return lodgeCollateralResult;
//	}
//
//	public Map<String, String> LodgeSubTypeSecCollateral(String pinstid, MainPojo lodgeCollateralRequestPj,
//			String processName, String limitType) throws IOException, SOAPException {
//
//		logger.info("lodgeCollateralService.LodgeSubTypeSecCollateral() pinstId : " + pinstid + ",  LCRequestPj : "
//				+ lodgeCollateralRequestPj);
//
//		String soapRequestPacket = "";
//		Boolean isExecute = true;
//		Map<String, String> lodgeCollateralResponsePojoList = new HashMap<>();
//
//		try {
//
//			if (lodgeCollateralRequestPj.getSubTypeSecurity().equalsIgnoreCase("Other")
//					|| lodgeCollateralRequestPj.getSubTypeSecurity().equalsIgnoreCase("Others")) {
//				lodgeCollateralRequestPj.setSubTypeSecurity("Other");
//			}
//
//			String rqstType = "LODGE COLLATERAL : "
//					+ OperationUtillity.NullReplace(lodgeCollateralRequestPj.getSecurityName()) + " : " + ""
//					+ OperationUtillity.NullReplace(lodgeCollateralRequestPj.getSubTypeSecurity()) + " : "
//					+ OperationUtillity.NullReplace(lodgeCollateralRequestPj.getTypeOfSecurity()) + " : "
//					+ OperationUtillity.NullReplace(lodgeCollateralRequestPj.getProduct());
//
//			lodgeCollateralRequestPj.setRequestType(rqstType);
//
//			switch (lodgeCollateralRequestPj.getSubTypeSecurity()) {
//
//			case "BOOK_DEBTS": // BD
//				soapRequestPacket = createBookDebtRequestPacket(lodgeCollateralRequestPj, pinstid);
//				break;
//			case "STOCK": // inv
//				soapRequestPacket = createInventoryStockRequestPacket(lodgeCollateralRequestPj, pinstid);
//				break;
//			case "MACHINERIES": // MAC
//				soapRequestPacket = createMachineryRequestPacket(lodgeCollateralRequestPj, pinstid);
//				break;
//			case "LIFE_INSURANCE": // LI
//
//				rqstType = "LODGE COLLATERAL : "
//						+ OperationUtillity.NullReplace(lodgeCollateralRequestPj.getSecurityName()) + " : "
//						+ OperationUtillity.NullReplace(lodgeCollateralRequestPj.getSubTypeSecurity()) + " : "
//						+ OperationUtillity.NullReplace(lodgeCollateralRequestPj.getTypeOfSecurity()) + " : "
//						+ OperationUtillity.NullReplace(lodgeCollateralRequestPj.getProduct()) + " : "
//						+ OperationUtillity.NullReplace(lodgeCollateralRequestPj.getPolicy_No());
//
//				lodgeCollateralRequestPj.setRequestType(rqstType);
//				soapRequestPacket = createLICRequestPacket(lodgeCollateralRequestPj, pinstid);
//				break;
//			case "Mutual_funds_Units": // MFU
//
//				rqstType = "LODGE COLLATERAL : "
//						+ OperationUtillity.NullReplace(lodgeCollateralRequestPj.getSecurityName()) + " : "
//						+ OperationUtillity.NullReplace(lodgeCollateralRequestPj.getSubTypeSecurity()) + " : "
//						+ OperationUtillity.NullReplace(lodgeCollateralRequestPj.getTypeOfSecurity()) + " : "
//						+ OperationUtillity.NullReplace(lodgeCollateralRequestPj.getProduct()) + " : "
//						+ OperationUtillity.NullReplace(lodgeCollateralRequestPj.getPolicy_No());
//
//				lodgeCollateralRequestPj.setRequestType(rqstType);
//
//				soapRequestPacket = createMutualFundsUnitRequestPacket(lodgeCollateralRequestPj, pinstid);
//				break;
//			case "Other": // Other/
//				soapRequestPacket = createOthersRequestPacket(lodgeCollateralRequestPj, pinstid);
//				break;
//			case "Tradable_Securities": // TS
//				soapRequestPacket = createTSRequestPacket(pinstid, lodgeCollateralRequestPj);
//				break;
//
//			case "IMMOVABLE_PROPERTIES": // IMM
//				soapRequestPacket = createIMMRequestPacket(pinstid, lodgeCollateralRequestPj);
//				break;
//
//			case "DEPOSITS": // FD
//				soapRequestPacket = createFDRequestPacket(pinstid, lodgeCollateralRequestPj);
//				break;
//
//			default:
//				break;
//			}
//
//			if (!("First Limit setup".toUpperCase().equalsIgnoreCase(limitType))
//					&& !("First Limit set up".toUpperCase().equalsIgnoreCase(limitType))) { // MODIFICATION FOR RENEWAL
//																							// 05032025
//
//			} else {
//				if ("Monitoring".equalsIgnoreCase(processName)
//						&& svtMonDao.checkIsExecutedOnceLCInLsm(pinstid, rqstType)) {
//					logger.info("lodgeCollateralService.LodgeSubTypeSecCollateral() --> pinstid [" + pinstid
//							+ "] processName [" + processName + "]");
//					isExecute = false;
//				}
//			}
//			logger.info("lodgeCollateralService.LodgeSubTypeSecCollateral() -->pinstid [" + pinstid + "] processName ["
//					+ processName + "] isExecute [" + isExecute + "]");
//			if (isExecute) {
//				lodgeCollateralResponsePojoList.putAll(
//						executeSoapMsgAndCreateRes(soapRequestPacket, pinstid, lodgeCollateralRequestPj, processName));
//			}
//
//		} catch (Exception e) {
//			logger.info("\nException.lodgeCollateralService.LodgeSubTypeSecCollateral().pinstid " + pinstid + " ::\n "
//					+ OperationUtillity.traceException(pinstid, e));
//
//			updtServiceDetails.updateInitialStatusInFiExecutionTable(new ServiceDetails(pinstid,
//					lodgeCollateralRequestPj.getRequestType(), "LODGE COLLATERAL",
//					lodgeCollateralRequestPj.getRequestType(), "FAILURE", soapRequestPacket, "", e.getMessage(), true));
//		}
//		logger.info("\nlodgeCollateralService.LodgeSubTypeSecCollateral().lodgeCollateralResponsePojoList check-->"
//				+ lodgeCollateralResponsePojoList);
//		return lodgeCollateralResponsePojoList;
//	}
//
//	public Map<String, String> executeSoapMsgAndCreateRes(String soapRequestPacket, String pinstid,
//			MainPojo lodgeCollateralRequestPj, String processName) throws IOException, SOAPException {
//		String Status = "";
//		logger.info(
//				"\n[lodgeCollateralService.LodgeSubTypeSecCollateral()].[pinstid: " + pinstid + " ].[requestType] --> "
//						+ lodgeCollateralRequestPj.getRequestType() + "\n" + lodgeCollateralRequestPj);
//
//		Map<String, String> lodgeCollateralResponseMap = new HashMap<>();
//
//		Boolean executeStatus = fetchStatus.getStatusForService(pinstid, lodgeCollateralRequestPj.getRequestType(),
//				processName);
//
//		logger.info("\n[lodgeCollateralService.LodgeSubTypeSecCollateral()].[pinstid = " + pinstid + " ].[requestType ="
//				+ lodgeCollateralRequestPj.getRequestType() + "[executeStatus =" + executeStatus);
//
//		if (executeStatus) {
//			String lodgeCollateralResponsePacket = SOAPRequestUtility.soapResponse(soapRequestPacket);
//
//			try {
//				lodgeCollateralResponseMap.put("requestType", lodgeCollateralRequestPj.getRequestType());
//				lodgeCollateralResponseMap.put("requestPacket", soapRequestPacket);
//				lodgeCollateralResponseMap.put("responsePacket", lodgeCollateralResponsePacket);
//
//				if (lodgeCollateralResponsePacket.contains("<HostTransaction>")) {
//
//					String HostTransaction = lodgeCollateralResponsePacket.substring(
//							lodgeCollateralResponsePacket.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
//							lodgeCollateralResponsePacket.indexOf("</HostTransaction>"));
//					if (HostTransaction.contains("<Status>")) {
//						Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),
//								HostTransaction.indexOf("</Status>"));
//						lodgeCollateralResponseMap.put("Status", Status);
//					}
//				}
//
//				lodgeCollateralResponseMap
//						.putAll(convertLodgeCollateralResToPojo(pinstid, lodgeCollateralResponsePacket, Status));
//
//				lodgeCollateralResponseMap.put(lodgeCollateralRequestPj.getRequestType(), Status);
//
//				Boolean retriger = false;
//				if (!"SUCCESS".equalsIgnoreCase(Status)) {
//					retriger = true;
//				}
//
//				ServiceDetails sdUpdate = new ServiceDetails(pinstid, lodgeCollateralRequestPj.getRequestType(),
//						"LODGE COLLATERAL", lodgeCollateralRequestPj.getRequestType(), Status, soapRequestPacket,
//						lodgeCollateralResponsePacket, lodgeCollateralResponseMap.get("ErrorDesc"), retriger);
//
//				updtServiceDetails.updateInitialStatusInFiExecutionTable(sdUpdate);
//
//				if (processName.equalsIgnoreCase("Limit_Setup")) {
//					OperationUtillity.API_RequestResponse_Insert(soapRequestPacket, lodgeCollateralResponsePacket,
//							lodgeCollateralRequestPj.getRequestType(), pinstid, lodgeCollateralResponseMap,
//							lodgeCollateralRequestPj.getRequestId());
//				} else if (processName.equalsIgnoreCase("Monitoring")) {
//					OperationUtillity.insertFiReqResMonitoring(soapRequestPacket, lodgeCollateralResponsePacket,
//							lodgeCollateralRequestPj.getRequestType(), pinstid, lodgeCollateralResponseMap,
//							lodgeCollateralRequestPj.getRequestId());
//				}
//			} catch (Exception e) {
//				logger.info("\nlodgeCollateralService.executeSoapMsgAndCreateRes().Exception -->"
//						+ OperationUtillity.traceException(pinstid, e));
//				ServiceDetails sdUpdateex = new ServiceDetails(pinstid, lodgeCollateralRequestPj.getRequestType(),
//						"LODGE COLLATERAL", lodgeCollateralRequestPj.getRequestType(), "FAILURE", soapRequestPacket,
//						lodgeCollateralResponsePacket, e.getMessage(), true);
//				logger.info("\nlodgeCollateralService.executeSoapMsgAndCreateRes().Exception sdUpdateex BEFORE -->"
//						+ sdUpdateex);
//				updtServiceDetails.updateInitialStatusInFiExecutionTable(sdUpdateex);
//				logger.info("\nlodgeCollateralService.executeSoapMsgAndCreateRes().Exception sdUpdateex AFTER -->"
//						+ sdUpdateex);
//			}
//		} else {
//			lodgeCollateralResponseMap.put(lodgeCollateralRequestPj.getRequestType(), "SUCCESS");
//			lodgeCollateralResponseMap.put("Execute Status", "STATUS = SUCCESS in LSM_FI_EXECUTION_DETAILS");
//		}
//		return lodgeCollateralResponseMap;
//	}
//
//	public String createBookDebtRequestPacket(MainPojo ldgCltrlRequestPojo, String pinstid) {
//
//		String rqstPacket = "";
//		try {
//
//			rqstPacket = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
//					+ "<FIXML" + "        xmlns='http://www.finacle.com/fixml'"
//					+ "        xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.finacle.com/fixml executeFinacleScript.xsd'>"
//					+ "<Header>" + "            <RequestHeader>" + " <MessageKey>" + "<RequestUUID>"
//					+ ldgCltrlRequestPojo.getRequestId() + "</RequestUUID>"
//					+ "<ServiceRequestId>executeFinacleScript</ServiceRequestId>"
//					+ " <ServiceRequestVersion>10.2</ServiceRequestVersion>" + " <ChannelId>CLS</ChannelId>"
//					+ " <LanguageId></LanguageId>" + "</MessageKey>" + "<RequestMessageInfo>" + "<BankId>BM3</BankId>"
//					+ "                    <TimeZone></TimeZone>" + "<EntityId></EntityId>"
//					+ "<EntityType></EntityType>" + "<ArmCorrelationId></ArmCorrelationId>" + "<MessageDateTime>"
//					+ ldgCltrlRequestPojo.getDateAndTime() + "</MessageDateTime>" + " </RequestMessageInfo>"
//					+ " <Security>" + "<Token>" + "		<PasswordToken>" + "			<UserId></UserId>"
//					+ " 			<Password></Password>" + " 		</PasswordToken>" + " </Token>"
//					+ "<FICertToken></FICertToken>" + " <RealUserLoginSessionId></RealUserLoginSessionId>"
//					+ " <RealUser></RealUser>" + " <RealUserPwd></RealUserPwd>"
//					+ " <SSOTransferToken></SSOTransferToken>" + "  </Security>" + " </RequestHeader>" + "</Header>"
//					+ "<Body>" + "<executeFinacleScriptRequest>" + "<ExecuteFinacleScriptInputVO>"
//					+ "			<requestId>FI_LodgeCollateral_BD.scr</requestId>" + "</ExecuteFinacleScriptInputVO>"
//					+ "<executeFinacleScript_CustomData>"
////					+ "			<Ceiling_Limit>" + ldgCltrlRequestPojo.getSecurityValueInMn()+ "</Ceiling_Limit>" //commented - 26/12/2024
//					+ "			<Ceiling_Limit>"
//					+ commonUtility.millionString(ldgCltrlRequestPojo.getValSubTypeSecInMn()) + "</Ceiling_Limit>"
//					+ "			<Collateral_Class>" + ldgCltrlRequestPojo.getCollateral_Class() + "</Collateral_Class>"
//					+ "			<Collateral_Code>" + ldgCltrlRequestPojo.getCollateralCode() + "</Collateral_Code>"
//					+ "			<Gross_Val>" + commonUtility.millionString(ldgCltrlRequestPojo.getSecurityValueInMn())
//					+ "</Gross_Val>" + "			<Due_Dt>" + ldgCltrlRequestPojo.getDue_Dt() + "</Due_Dt>"
//					+ "			<Last_Val_Date>" + ldgCltrlRequestPojo.getLast_Val_Date() + "</Last_Val_Date>"
//					+ "			<NatureOfCharge>" + ldgCltrlRequestPojo.getReferenceCode() + "</NatureOfCharge>"
//					// + " <notes>"+ ldgCltrlRequestPojo.getNotes() + "</notes>" //commented -
//					// 26/12/2024
//					+ "			<notes>" + pinstid + "</notes>"
////					+ "			<Receive_Dt>" + OperationUtillity.getSystemDateFormat()+ "</Receive_Dt>"  //merge sysdate on LIVE
//					+ "			<Receive_Dt>02-04-2022</Receive_Dt>" // uat pass hardcoded
//					+ "			<Review_Dt>" + ldgCltrlRequestPojo.getReview_Dt() + "</Review_Dt>"
//					+ "</executeFinacleScript_CustomData>" + "</executeFinacleScriptRequest>" + "</Body>" + "</FIXML>"
//					+ "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
//			return rqstPacket;
//		} catch (Exception e) {
//			logger.info("lodgeCollateralService.createBookDebtRequestPacket()" + OperationUtillity.traceException(e));
//		}
//		return rqstPacket;
//	}
//
//	public String createInventoryStockRequestPacket(MainPojo ldgCltrlRequestPojo, String pinstid) {
//		String rqstPacket = "";
//		try {
//
//			rqstPacket = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
//					+ "<FIXML" + "        xmlns='http://www.finacle.com/fixml'"
//					+ "        xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.finacle.com/fixml executeFinacleScript.xsd'>"
//					+ "        <Header>" + "            <RequestHeader>" + "                <MessageKey>"
//					+ "                    <RequestUUID>" + ldgCltrlRequestPojo.getRequestId() + "</RequestUUID>"
//					+ "                    <ServiceRequestId>executeFinacleScript</ServiceRequestId>"
//					+ "                    <ServiceRequestVersion>10.2</ServiceRequestVersion>"
//					+ "                    <ChannelId>CLS</ChannelId>" + "                    <LanguageId></LanguageId>"
//					+ "                    </MessageKey>" + "				  <RequestMessageInfo>"
//					+ "                    <BankId>BM3</BankId>" + "                   <TimeZone></TimeZone>"
//					+ "                    <EntityId></EntityId>" + "                  <EntityType></EntityType>"
//					+ "                    <ArmCorrelationId></ArmCorrelationId>"
//					+ "                    <MessageDateTime>" + ldgCltrlRequestPojo.getDateAndTime()
//					+ "</MessageDateTime>" + "                    </RequestMessageInfo>"
//					+ " 					<Security>" + "                    <Token>"
//					+ "					<PasswordToken>" + "                            <UserId></UserId>"
//					+ "                            <Password></Password>" + "                   </PasswordToken>"
//					+ "                    </Token>" + "                    <FICertToken></FICertToken>"
//					+ "                    <RealUserLoginSessionId></RealUserLoginSessionId>"
//					+ "                    <RealUser></RealUser>" + "                    <RealUserPwd></RealUserPwd>"
//					+ "                    <SSOTransferToken></SSOTransferToken>" + "                </Security>"
//					+ "            </RequestHeader>" + "        	</Header>" + "<Body>"
//					+ "<executeFinacleScriptRequest>" + "<ExecuteFinacleScriptInputVO>"
//					+ "			<requestId>FI_LodgeCollateral_Inv.scr</requestId>" + "</ExecuteFinacleScriptInputVO>"
//					+ "<executeFinacleScript_CustomData>"
////					+ "			<Ceiling_Limit>" + ldgCltrlRequestPojo.getSecurityValueInMn()+ "</Ceiling_Limit>" //commented - 26/12/2024
//					+ "			<Ceiling_Limit>"
//					+ commonUtility.millionString(ldgCltrlRequestPojo.getValSubTypeSecInMn()) + "</Ceiling_Limit>"
//					+ "			<Collateral_Class>" + ldgCltrlRequestPojo.getCollateral_Class() + "</Collateral_Class>"
//					+ "			<Collateral_Code>" + ldgCltrlRequestPojo.getCollateralCode() + "</Collateral_Code>"
//					+ "			<Gross_Val>" + commonUtility.millionString(ldgCltrlRequestPojo.getSecurityValueInMn())
//					+ "</Gross_Val>" + "			<Due_Dt>" + ldgCltrlRequestPojo.getDue_Dt() + "</Due_Dt>"
//					+ "			<Last_Val_Date>" + ldgCltrlRequestPojo.getLast_Val_Date() + "</Last_Val_Date>"
////					+ "			<notes>"+ ldgCltrlRequestPojo.getNotes() + "</notes>" //commented - 26/12/2024
//					+ "			<notes>" + pinstid + "</notes>"
////					+ "			<Receive_Dt>" + OperationUtillity.getSystemDateFormat()+ "</Receive_Dt>"  //merge sysdate on LIVE
//					+ "			<Receive_Dt>02-04-2022</Receive_Dt>" // uat pass hardcoded
//					+ "			<Review_Dt>" + ldgCltrlRequestPojo.getReview_Dt() + "</Review_Dt>"
//					+ "</executeFinacleScript_CustomData>" + "</executeFinacleScriptRequest>" + "</Body>" + "</FIXML>"
//					+ "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
//			return rqstPacket;
//		} catch (Exception e) {
//			logger.info(
//					"lodgeCollateralService.createInventoryStockRequestPacket()" + OperationUtillity.traceException(e));
//		}
//		return rqstPacket;
//	}
//
//	public String createMachineryRequestPacket(MainPojo ldgCltrlRequestPojo, String pinstid) {
//
//		String rqstPacket = "";
//		try {
//
//			rqstPacket = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
//					+ "<FIXML" + "        xmlns='http://www.finacle.com/fixml'"
//					+ "        xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.finacle.com/fixml executeFinacleScript.xsd'>"
//					+ "        <Header>" + "            <RequestHeader>" + "                <MessageKey>"
//					+ "                    <RequestUUID>" + ldgCltrlRequestPojo.getRequestId() + "</RequestUUID>"
//					+ "                    <ServiceRequestId>executeFinacleScript</ServiceRequestId>"
//					+ "                    <ServiceRequestVersion>10.2</ServiceRequestVersion>"
//					+ "                    <ChannelId>CLS</ChannelId>" + "                    <LanguageId></LanguageId>"
//					+ "                </MessageKey>" + "                <RequestMessageInfo>"
//					+ "                    <BankId>BM3</BankId>" + "                    <TimeZone></TimeZone>"
//					+ "                    <EntityId></EntityId>" + "                    <EntityType></EntityType>"
//					+ "                    <ArmCorrelationId></ArmCorrelationId>"
//					+ "                    <MessageDateTime>" + ldgCltrlRequestPojo.getDateAndTime()
//					+ "</MessageDateTime>" + "                </RequestMessageInfo>" + "                <Security>"
//					+ "                    <Token>" + "<PasswordToken>"
//					+ "                            <UserId></UserId>"
//					+ "                            <Password></Password>" + "                        </PasswordToken>"
//					+ "                    </Token>" + "                    <FICertToken></FICertToken>"
//					+ "                    <RealUserLoginSessionId></RealUserLoginSessionId>"
//					+ "                    <RealUser></RealUser>" + "                    <RealUserPwd></RealUserPwd>"
//					+ "                    <SSOTransferToken></SSOTransferToken>" + "                </Security>"
//					+ "            </RequestHeader>" + "        </Header>" + "<Body>" + "<executeFinacleScriptRequest>"
//					+ "<ExecuteFinacleScriptInputVO>" + "<requestId>FI_LodgeCollateral_Mac.scr</requestId>"
//					+ "</ExecuteFinacleScriptInputVO>" + "<executeFinacleScript_CustomData>"
////					+ "			<Ceiling_Limit>" + ldgCltrlRequestPojo.getSecurityValueInMn()+ "</Ceiling_Limit>"  //commented-26/12/2024
//					+ "			<Ceiling_Limit>"
//					+ commonUtility.millionString(ldgCltrlRequestPojo.getValSubTypeSecInMn()) + "</Ceiling_Limit>"
//					+ "			<Collateral_Class>" + ldgCltrlRequestPojo.getCollateral_Class() + "</Collateral_Class>"
//					+ "			<Collateral_Code>" + ldgCltrlRequestPojo.getCollateralCode() + "</Collateral_Code>"
//					+ "			<Due_Dt>" + ldgCltrlRequestPojo.getDue_Dt() + "</Due_Dt>" + "			<Last_Val_Date>"
//					+ ldgCltrlRequestPojo.getLast_Val_Date() + "</Last_Val_Date>" + "			<NatureOfCharge>"
//					+ ldgCltrlRequestPojo.getReferenceCode() + "</NatureOfCharge>"
////					+ "			<notes>"+ ldgCltrlRequestPojo.getNotes() + "</notes>" 
//					+ "			<notes>" + pinstid + "</notes>" + "			<FromDeriveVal>"
//					+ ldgCltrlRequestPojo.getFromDeriveVal() + "</FromDeriveVal>"
//					// + " <FromDeriveVal>A</FromDeriveVal>"
//					+ "			<Value>" + commonUtility.millionString(ldgCltrlRequestPojo.getValSubTypeSecInMn())
//					+ "</Value>"
////					+ "			<Receive_Dt>" + OperationUtillity.getSystemDateFormat()+ "</Receive_Dt>"  //merge sysdate on LIVE
//					+ "			<Receive_Dt>02-04-2022</Receive_Dt>" // uat pass hardcoded
//					+ "			<Review_Dt>" + ldgCltrlRequestPojo.getReview_Dt() + "</Review_Dt>"
//					+ "</executeFinacleScript_CustomData>" + "</executeFinacleScriptRequest>" + "</Body>" + "</FIXML>"
//					+ "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
//			return rqstPacket;
//		} catch (Exception e) {
//			logger.info("lodgeCollateralService.createMachineryRequestPacket()" + OperationUtillity.traceException(e));
//		}
//		return rqstPacket;
//	}
//
//	public String createLICRequestPacket(MainPojo ldgCltrlRequestPojo, String pinstid) {
//
//		String rqstPacket = "";
//		try {
//
//			rqstPacket = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
//					+ "    <FIXML" + "        xmlns='http://www.finacle.com/fixml'"
//					+ "        xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.finacle.com/fixml executeFinacleScript.xsd'>"
//					+ "        <Header>" + "            <RequestHeader>" + "                <MessageKey>"
//					+ "                    <RequestUUID>" + ldgCltrlRequestPojo.getRequestId() + "</RequestUUID>"
//					+ "                    <ServiceRequestId>executeFinacleScript</ServiceRequestId>"
//					+ "                    <ServiceRequestVersion>10.2</ServiceRequestVersion>"
//					+ "                    <ChannelId>CLS</ChannelId>" + "                    <LanguageId></LanguageId>"
//					+ "                </MessageKey>" + "                <RequestMessageInfo>"
//					+ "                    <BankId>BM3</BankId>" + "                    <TimeZone></TimeZone>"
//					+ "                    <EntityId></EntityId>" + "                    <EntityType></EntityType>"
//					+ "                    <ArmCorrelationId></ArmCorrelationId>"
//					+ "                    <MessageDateTime>" + ldgCltrlRequestPojo.getDateAndTime()
//					+ "</MessageDateTime>" + "                </RequestMessageInfo>" + "                <Security>"
//					+ "                    <Token>" + "                        <PasswordToken>"
//					+ "                            <UserId></UserId>"
//					+ "                            <Password></Password>" + "                        </PasswordToken>"
//					+ "                    </Token>" + "                    <FICertToken></FICertToken>"
//					+ "                    <RealUserLoginSessionId></RealUserLoginSessionId>"
//					+ "                    <RealUser></RealUser>" + "                    <RealUserPwd></RealUserPwd>"
//					+ "                    <SSOTransferToken></SSOTransferToken>" + "                </Security>"
//					+ "            </RequestHeader>" + "        </Header>" + "<Body>" + "<executeFinacleScriptRequest>"
//					+ "<ExecuteFinacleScriptInputVO>" + "			<requestId>FI_LodgeCollateral_LI.scr</requestId>"
//					+ "</ExecuteFinacleScriptInputVO>" + "<executeFinacleScript_CustomData>"
//					+ "				<Ceiling_Limit>"
//					+ commonUtility.millionString(ldgCltrlRequestPojo.getValSubTypeSecInMn()) + "</Ceiling_Limit>"
//					+ "				<Collateral_Class>" + ldgCltrlRequestPojo.getCollateral_Class()
//					+ "</Collateral_Class>" + "				<Collateral_Code>" + ldgCltrlRequestPojo.getCollateralCode()
//					+ "</Collateral_Code>" + "			    <Unit_Val>"
//					+ commonUtility.millionString(ldgCltrlRequestPojo.getValSubTypeSecInMn()) + "</Unit_Val>"
////					+ "			    <No_Of_Units>" + ldgCltrlRequestPojo.getPolicy_No() + "</No_Of_Units>"
//					+ "			    <No_Of_Units>" + commonUtility.millionString(ldgCltrlRequestPojo.getPolicy_No())
//					+ "</No_Of_Units>" + "				<Due_Dt>" + ldgCltrlRequestPojo.getDue_Dt() + "</Due_Dt>"
//					+ "				<Last_Val_Date>" + ldgCltrlRequestPojo.getLast_Val_Date() + "</Last_Val_Date>"
//					+ "				<NatureOfCharge>" + ldgCltrlRequestPojo.getReferenceCode() + "</NatureOfCharge>"
//					+ "				<notes>" + pinstid + "</notes>" + "				<Policy_Amt>"
//					+ commonUtility.millionString(ldgCltrlRequestPojo.getPolicy_Amt()) + "</Policy_Amt>"
//					+ "				<Surrender_Val>"
//					+ commonUtility.millionString(ldgCltrlRequestPojo.getSecurityValueInMn()) + "</Surrender_Val>"
//					+ "				<Policy_No>" + ldgCltrlRequestPojo.getPolicy_No() + "</Policy_No>"
//					+ "				<Review_Dt>" + ldgCltrlRequestPojo.getReview_Dt() + "</Review_Dt>"
////					+ "			<Receive_Dt>" + OperationUtillity.getSystemDateFormat()+ "</Receive_Dt>"  //merge sysdate on LIVE
//					+ "			<Receive_Dt>02-04-2022</Receive_Dt>" // uat pass hardcoded
//					+ "</executeFinacleScript_CustomData>" + "</executeFinacleScriptRequest>" + "</Body>" + "</FIXML>"
//					+ "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
//
//			return rqstPacket;
//		} catch (Exception e) {
//			logger.info("lodgeCollateralService.createLICRequestPacket()" + OperationUtillity.traceException(e));
//		}
//		return rqstPacket;
//	}
//
//	public String createMutualFundsUnitRequestPacket(MainPojo ldgCltrlRequestPojo, String pinstid) {
//
//		String rqstPacket = "";
//		try {
//
//			rqstPacket = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
//					+ "    <FIXML" + "        xmlns='http://www.finacle.com/fixml'"
//					+ "        xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.finacle.com/fixml executeFinacleScript.xsd'>"
//					+ "        <Header>" + "            <RequestHeader>" + "                <MessageKey>"
//					+ "                    <RequestUUID>" + ldgCltrlRequestPojo.getRequestId() + "</RequestUUID>"
//					+ "                    <ServiceRequestId>executeFinacleScript</ServiceRequestId>"
//					+ "                    <ServiceRequestVersion>10.2</ServiceRequestVersion>"
//					+ "                    <ChannelId>CLS</ChannelId>" + "                    <LanguageId></LanguageId>"
//					+ "                </MessageKey>" + "                <RequestMessageInfo>"
//					+ "                    <BankId>BM3</BankId>" + "                    <TimeZone></TimeZone>"
//					+ "                    <EntityId></EntityId>" + "                    <EntityType></EntityType>"
//					+ "                    <ArmCorrelationId></ArmCorrelationId>"
//					+ "                    <MessageDateTime>" + ldgCltrlRequestPojo.getDateAndTime()
//					+ "</MessageDateTime>" + "                </RequestMessageInfo>" + "                <Security>"
//					+ "                    <Token>" + "                        <PasswordToken>"
//					+ "                            <UserId></UserId>"
//					+ "                            <Password></Password>" + "                        </PasswordToken>"
//					+ "                    </Token>" + "                    <FICertToken></FICertToken>"
//					+ "                    <RealUserLoginSessionId></RealUserLoginSessionId>"
//					+ "                    <RealUser></RealUser>" + "                    <RealUserPwd></RealUserPwd>"
//					+ "                    <SSOTransferToken></SSOTransferToken>" + "                </Security>"
//					+ "            </RequestHeader>" + "        </Header>" + "<Body>" + "<executeFinacleScriptRequest>"
//					+ "<ExecuteFinacleScriptInputVO>" + "<requestId>FI_LodgeCollateral_MFU.scr</requestId>"
//					+ "</ExecuteFinacleScriptInputVO>" + "<executeFinacleScript_CustomData>"
//					+ "			<Ceiling_Limit>"
//					+ commonUtility.millionString(ldgCltrlRequestPojo.getValSubTypeSecInMn()) + "</Ceiling_Limit>"
//					+ "			<Collateral_Class>" + ldgCltrlRequestPojo.getCollateral_Class() + "</Collateral_Class>"
//					+ "			<Collateral_Code>" + ldgCltrlRequestPojo.getNameofHoldingStock() + "</Collateral_Code>"
//					+ "			<Unit_Val>" + ldgCltrlRequestPojo.getUnitValue() + "</Unit_Val>" // changed for absolute
////					+ "			<No_Of_Units>" + ldgCltrlRequestPojo.getNoOfUnits() + "</No_Of_Units>"   //commented on 18-2-2025
//					+ "			<No_Of_Units>" + commonUtility.millionString(ldgCltrlRequestPojo.getNoOfUnits())
//					+ "</No_Of_Units>" // passing absolute value
//					+ "			<Due_Dt>" + ldgCltrlRequestPojo.getDue_Dt() + "</Due_Dt>" + "			<Review_Dt>"
//					+ ldgCltrlRequestPojo.getReview_Dt() + "</Review_Dt>" + "			<Last_Val_Date>"
//					+ ldgCltrlRequestPojo.getLast_Val_Date() + "</Last_Val_Date>" + "			<NatureOfCharge>"
//					+ ldgCltrlRequestPojo.getReferenceCode() + "</NatureOfCharge>" + "			<notes1>" + pinstid
//					+ "</notes1>" + "			<notes2>" + pinstid + "</notes2>" + "</executeFinacleScript_CustomData>"
//					+ "</executeFinacleScriptRequest>" + "</Body>" + "</FIXML>"
//					+ "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
//			return rqstPacket;
//		} catch (Exception e) {
//			logger.info("lodgeCollateralService.createMutualFundsUnitRequestPacket()"
//					+ OperationUtillity.traceException(e));
//		}
//		return rqstPacket;
//	}
//
//	public String createOthersRequestPacket(MainPojo ldgCltrlRequestPojo, String pinstid) {
//
//		String rqstPacket = "";
//		try {
//
//			rqstPacket = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
//					+ "<FIXML" + "        xmlns='http://www.finacle.com/fixml'"
//					+ "        xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.finacle.com/fixml executeFinacleScript.xsd'>"
//					+ "        <Header>" + "            <RequestHeader>" + "                <MessageKey>"
//					+ "                    <RequestUUID>" + ldgCltrlRequestPojo.getRequestId() + "</RequestUUID>"
//					+ "                    <ServiceRequestId>executeFinacleScript</ServiceRequestId>"
//					+ "                    <ServiceRequestVersion>10.2</ServiceRequestVersion>"
//					+ "                    <ChannelId>CLS</ChannelId>" + "                    <LanguageId></LanguageId>"
//					+ "                </MessageKey>" + "                <RequestMessageInfo>"
//					+ "                    <BankId>BM3</BankId>" + "                    <TimeZone></TimeZone>"
//					+ "                    <EntityId></EntityId>" + "                    <EntityType></EntityType>"
//					+ "                    <ArmCorrelationId></ArmCorrelationId>"
//					+ "                    <MessageDateTime>" + ldgCltrlRequestPojo.getDateAndTime()
//					+ "</MessageDateTime>" + "                </RequestMessageInfo>" + "                <Security>"
//					+ "                    <Token>" + "                        <PasswordToken>"
//					+ "                            <UserId></UserId>"
//					+ "                            <Password></Password>" + "                        </PasswordToken>"
//					+ "                    </Token>" + "                    <FICertToken></FICertToken>"
//					+ "                    <RealUserLoginSessionId></RealUserLoginSessionId>"
//					+ "                    <RealUser></RealUser>" + "                    <RealUserPwd></RealUserPwd>"
//					+ "                    <SSOTransferToken></SSOTransferToken>" + "                </Security>"
//					+ "            </RequestHeader>" + "        </Header>" + "<Body>" + "<executeFinacleScriptRequest>"
//					+ "<ExecuteFinacleScriptInputVO>" + "          <requestId>FI_LodgeCollateral_Oth.scr</requestId>"
//					+ "</ExecuteFinacleScriptInputVO>" + "<executeFinacleScript_CustomData>"
//					+ "          			<Ceiling_Limit>"
//					+ commonUtility.millionString(ldgCltrlRequestPojo.getValSubTypeSecInMn()) + "</Ceiling_Limit>"
//					+ "					<Collateral_Class>" + ldgCltrlRequestPojo.getCollateral_Class()
//					+ "</Collateral_Class>" + "					<Collateral_Code>"
//					+ ldgCltrlRequestPojo.getCollateralCode() + "</Collateral_Code>" + "					<Due_Dt>"
//					+ ldgCltrlRequestPojo.getDue_Dt() + "</Due_Dt>" + "					<Last_Val_Date>"
//					+ ldgCltrlRequestPojo.getLast_Val_Date() + "</Last_Val_Date>"
//					+ "					<NatureOfCharge>" + ldgCltrlRequestPojo.getReferenceCode() + "</NatureOfCharge>"
//					+ "					<notes>" + pinstid + "</notes>" + "					<Collateral_Value>"
//					+ commonUtility.millionString(ldgCltrlRequestPojo.getValSubTypeSecInMn()) + "</Collateral_Value>"
////					+ "			       <Receive_Dt>" + OperationUtillity.getSystemDateFormat()+ "</Receive_Dt>"  //merge sysdate on LIVE
//					+ "			      <Receive_Dt>02-04-2022</Receive_Dt>" // uat pass hardcoded
//					+ "					<Review_Dt>" + ldgCltrlRequestPojo.getReview_Dt() + "</Review_Dt>"
//					+ "</executeFinacleScript_CustomData>" + "</executeFinacleScriptRequest>" + "</Body>" + "</FIXML>"
//					+ "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
//			return rqstPacket;
//		} catch (Exception e) {
//			logger.info("lodgeCollateralService.createOthersRequestPacket()" + OperationUtillity.traceException(e));
//		}
//		return rqstPacket;
//	}
//
//	public String createTSRequestPacket(String pinstid, MainPojo ldgCltrlRequestPojo) {
//		String rqstPacket = "";
//		try {
//
//			rqstPacket = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
//					+ "<FIXML" + "        xmlns='http://www.finacle.com/fixml'"
//					+ "        xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.finacle.com/fixml executeFinacleScript.xsd'>"
//					+ "        <Header>" + "            <RequestHeader>" + "                <MessageKey>"
//					+ "                    <RequestUUID>" + ldgCltrlRequestPojo.getRequestId() + "</RequestUUID>"
//					+ "                    <ServiceRequestId>executeFinacleScript</ServiceRequestId>"
//					+ "                    <ServiceRequestVersion>10.2</ServiceRequestVersion>"
//					+ "                    <ChannelId>CLS</ChannelId>" + "                    <LanguageId></LanguageId>"
//					+ "                </MessageKey>" + "                <RequestMessageInfo>"
//					+ "                    <BankId>BM3</BankId>" + "                    <TimeZone></TimeZone>"
//					+ "                    <EntityId></EntityId>" + "                    <EntityType></EntityType>"
//					+ "                    <ArmCorrelationId></ArmCorrelationId>"
//					+ "                    <MessageDateTime>" + ldgCltrlRequestPojo.getDateAndTime()
//					+ "</MessageDateTime>" + "                </RequestMessageInfo>" + "                <Security>"
//					+ "                    <Token>" + "                        <PasswordToken>"
//					+ "                            <UserId></UserId>"
//					+ "                            <Password></Password>" + "                        </PasswordToken>"
//					+ "                    </Token>" + "                    <FICertToken></FICertToken>"
//					+ "                    <RealUserLoginSessionId></RealUserLoginSessionId>"
//					+ "                    <RealUser></RealUser>" + "                    <RealUserPwd></RealUserPwd>"
//					+ "                    <SSOTransferToken></SSOTransferToken>" + "                </Security>"
//					+ "            </RequestHeader>" + "        </Header>" + "<Body>" + "<executeFinacleScriptRequest>"
//					+ "<ExecuteFinacleScriptInputVO>" + "			<requestId>FI_LodgeCollateral_TS.scr</requestId>"
//					+ "</ExecuteFinacleScriptInputVO>" + "<executeFinacleScript_CustomData>"
//					+ "				<Ceiling_Limit>"
//					+ commonUtility.millionString(ldgCltrlRequestPojo.getValSubTypeSecInMn()) + "</Ceiling_Limit>"
//					+ "				<Collateral_Class>" + ldgCltrlRequestPojo.getCollateral_Class()
//					+ "</Collateral_Class>" + "				<Collateral_Code>"
//					+ ldgCltrlRequestPojo.getNameofHoldingStock() + "</Collateral_Code>" + "				<Unit_Val>"
//					+ ldgCltrlRequestPojo.getUnitValue() + "</Unit_Val>	"
////					+ "				<No_Of_Units>" + ldgCltrlRequestPojo.getNoOfUnits() + "</No_Of_Units>" 
//					+ "				<No_Of_Units>" + commonUtility.millionString(ldgCltrlRequestPojo.getNoOfUnits())
//					+ "</No_Of_Units>" + "				<Due_Dt>" + ldgCltrlRequestPojo.getDue_Dt() + "</Due_Dt>"
//					+ "				<Last_Val_Date>" + ldgCltrlRequestPojo.getLast_Val_Date() + "</Last_Val_Date>"
//					+ "				<NatureOfCharge>" + ldgCltrlRequestPojo.getReferenceCode() + "</NatureOfCharge>"
//					+ "				<notes1>" + pinstid + "</notes1>" + "				<notes2>" + pinstid
//					+ "</notes2>" + "				<Review_Dt>" + ldgCltrlRequestPojo.getReview_Dt() + "</Review_Dt>"
//					+ "</executeFinacleScript_CustomData>" + "</executeFinacleScriptRequest>" + "</Body>" + "</FIXML>"
//					+ "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
//			return rqstPacket;
//		} catch (Exception e) {
//			logger.info("lodgeCollateralService.createTSRequestPacket()" + OperationUtillity.traceException(e));
//		}
//		return rqstPacket;
//	}
//
//	public String createIMMRequestPacket(String pinstid, MainPojo ldgCltrlRequestPojo) {
//
//		String rqstPacket = "";
//		String Assessed_Value = "";
//		String Market_Value = "";
//		String Invoice_Value = "";
//		String writtenDownValue = "";
//		String addressLine1 = ldgCltrlRequestPojo.getAddressLine1() + ", " + ldgCltrlRequestPojo.getArea();
//		String addressLine2 = ldgCltrlRequestPojo.getRoad() + ", " + ldgCltrlRequestPojo.getLandmark();
//		String stateCode = ldgCltrlRequestPojo.getAddressCode().getStateCode();
//		String cityCode = ldgCltrlRequestPojo.getAddressCode().getCityCode();
//		String apportionedValueInString = ldgCltrlRequestPojo.getValue();
//
//		String fromDerivedVal = "".equalsIgnoreCase(ldgCltrlRequestPojo.getFromDeriveVal())
//				? ldgCltrlRequestPojo.getFromDeriveVal()
//				: ldgCltrlRequestPojo.getFromDeriveVal().substring(0, 1);
//
//		// new changes - JIRA 10199
//		if ("A".equalsIgnoreCase(fromDerivedVal)) {
//			Assessed_Value = commonUtility.millionString(ldgCltrlRequestPojo.getValSubTypeSecInMn());
//			logger.info("Assessed_Value---" + Assessed_Value);
//		} else if ("M".equalsIgnoreCase(fromDerivedVal)) {
//			Market_Value = commonUtility.millionString(ldgCltrlRequestPojo.getValSubTypeSecInMn());
//			logger.info("Market_Value---" + Market_Value);
//		} else if ("I".equalsIgnoreCase(fromDerivedVal)) {
//			Invoice_Value = commonUtility.millionString(ldgCltrlRequestPojo.getValSubTypeSecInMn());
//			logger.info("Invoice_Value---" + Invoice_Value);
//		} else if ("W".equalsIgnoreCase(fromDerivedVal)) {
//			writtenDownValue = commonUtility.millionString(ldgCltrlRequestPojo.getValSubTypeSecInMn());
//			logger.info("writtenDownValue---" + writtenDownValue);
//		}
//
//		if (!apportionedValueInString.equalsIgnoreCase("0") || !apportionedValueInString.equalsIgnoreCase("")) {
//			apportionedValueInString = commonUtility.millionString(apportionedValueInString);
//		}
//
//		if ((!"".equals(addressLine1)) && (!addressLine1.equals(null)) && ((addressLine1.length() > 45))) {
//			addressLine1 = addressLine1.substring(0, 45);
//		}
//		if ((!"".equals(addressLine2)) && (!addressLine2.equals(null)) && ((addressLine2.length() > 45))) {
//			addressLine2 = addressLine2.substring(0, 45);
//		}
//
//		try {
//
//			rqstPacket = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Header>"
//					+ "      <RequestHeader>" + "         <MessageKey>" + "            <ChannelId>CLS</ChannelId>"
//					+ "            <LanguageId></LanguageId>" + "            <RequestUUID>"
//					+ commonUtility.createRequestUUID() + "</RequestUUID>"
//					+ "            <ServiceRequestId>executeFinacleScript</ServiceRequestId>"
//					+ "            <ServiceRequestVersion>10.2</ServiceRequestVersion>" + "         </MessageKey>"
//					+ "         <RequestMessageInfo>" + "            <ArmCorrelationId></ArmCorrelationId>"
//					+ "            <BankId>BM3</BankId>" + "            <EntityId></EntityId>"
//					+ "            <EntityType></EntityType>" + "            <MessageDateTime>" + LocalDateTime.now()
//					+ "</MessageDateTime>" + "            <TimeZone></TimeZone>" + "         </RequestMessageInfo>"
//					+ "         <Security>" + "            <FICertToken></FICertToken>"
//					+ "            <SSOTransferToken></SSOTransferToken>" + "            <RealUser></RealUser>"
//					+ "            <RealUserLoginSessionId></RealUserLoginSessionId>"
//					+ "            <RealUserPwd></RealUserPwd>" + "            <Token>"
//					+ "               <passwordToken>" + "                  <password></password>"
//					+ "                  <userId></userId>" + "               </passwordToken>" + "            </Token>"
//					+ "         </Security>" + "      </RequestHeader>" + "   </Header><Body>"
//					+ "      <executeFinacleScriptRequest>" + "         <ExecuteFinacleScriptInputVO>"
//					+ "            <requestId>FI_LodgeCollateral_Immov.scr</requestId>"
//					+ "         </ExecuteFinacleScriptInputVO>" + "         <executeFinacleScript_CustomData>"
//					+ "            <Address_Line1>" + commonUtility.removeSpecialCharacters(addressLine1)
//					+ "</Address_Line1>" + "            <Address_Line2>"
//					+ commonUtility.removeSpecialCharacters(addressLine2) + "</Address_Line2>"
//					+ "            <Address_Line3>" + ldgCltrlRequestPojo.getCity() + "</Address_Line3>" +
////					"            <Assessed_Value>"+commonUtility.millionString(ldgCltrlRequestPojo.getValSubTypeSecInMn())+"</Assessed_Value>" + 
//					"            <Built_Area></Built_Area>" +
////					"            <Ceiling_Limit>"+ldgCltrlRequestPojo.getSecurityValueInMn()+"</Ceiling_Limit>" +  //commented - 26/12/2024
//					"            <Ceiling_Limit>"
//					+ commonUtility.millionString(ldgCltrlRequestPojo.getValSubTypeSecInMn()) + "</Ceiling_Limit>" +
////					"            <Charge_Amount>"+ldgCltrlRequestPojo.getValue()+"</Charge_Amount>" + //CHANGED
//					"            <Charge_Amount>" + ldgCltrlRequestPojo.getSecurityValueInMn() + "</Charge_Amount>" + // column
//																														// SEC_VAL_IN_MN
//					"            <City>" + cityCode + "</City>" + "            <Collateral_Class>"
//					+ ldgCltrlRequestPojo.getCollateral_Class() + "</Collateral_Class>"
//					+ "            <Collateral_Code>" + ldgCltrlRequestPojo.getCollateralCode() + "</Collateral_Code>"
//					+ "            <Collateral_Group>01RES</Collateral_Group>" + "            <Collateral_Value>"
//					+ commonUtility.millionString(ldgCltrlRequestPojo.getValSubTypeSecInMn()) + "</Collateral_Value>"
//					+ "            <DueDate_For_Visit>05-12-2017</DueDate_For_Visit>" +
////					"            <Due_Date>02-04-2022</Due_Date>" +   //FOU UAT AND SYSDATE FOR LIVE
//					"            <Due_Date>" + ldgCltrlRequestPojo.getDue_Dt() + "</Due_Date>"
//					+ "            <From_Deried_Value>" + fromDerivedVal + "</From_Deried_Value>"
//					+ "            <Assessed_Value>" + Assessed_Value + "</Assessed_Value>" + // new changes - JIRA
//																								// 10199
//					"            <Market_Value>" + Market_Value + "</Market_Value>" + "            <Invoice_Value>"
//					+ Invoice_Value + "</Invoice_Value>" + "            <writtenDownValue>" + writtenDownValue
//					+ "</writtenDownValue>" + "            <Inspected_Value>"
//					+ commonUtility.millionString(ldgCltrlRequestPojo.getValSubTypeSecInMn()) + "</Inspected_Value>" +
////					"            <Inspection_Type>L&amp;B</Inspection_Type>" + 
//					"            <Inspection_Type>&amp;</Inspection_Type>" +
////					"            <Last_Valuation_Date>05-12-2017</Last_Valuation_Date>" + 
//					"            <Last_Valuation_Date>" + ldgCltrlRequestPojo.getLast_Val_Date()
//					+ "</Last_Valuation_Date>" + "            <Nature_Of_Charge>"
//					+ ldgCltrlRequestPojo.getReferenceCode() + "</Nature_Of_Charge>" + "            <Notes>" + pinstid
//					+ "</Notes>" + "            <Particular_AddressLine1>"
//					+ commonUtility.removeSpecialCharacters(addressLine1) + "</Particular_AddressLine1>"
//					+ "            <Particular_AddressLine2>" + commonUtility.removeSpecialCharacters(addressLine2)
//					+ "</Particular_AddressLine2>" + "            <Particular_City>" + cityCode + "</Particular_City>"
//					+ "            <Particular_Notes>" + pinstid + "</Particular_Notes>"
//					+ "            <Particular_PostalCode>" + ldgCltrlRequestPojo.getPincode()
//					+ "</Particular_PostalCode>" + "            <Particular_State>" + stateCode + "</Particular_State>"
//					+ "            <Postal_Code>" + ldgCltrlRequestPojo.getPincode() + "</Postal_Code>"
//					+ "            <Property_Doc_No>" + ldgCltrlRequestPojo.getUcc_Based_CustId() + "</Property_Doc_No>"
//					+ "            <Property_Owner>" + (ldgCltrlRequestPojo.getPropertyowner()).replaceAll("&", "&amp;")
//					+ "</Property_Owner>" + "            <Receipt_Date>05-12-2017</Receipt_Date>" +
//					// " <Received_Date>"+ldgCltrlRequestPojo.getReceive_Dt()+"</Received_Date>" +
//					// // FOR LIVE
//					"            <Received_Date>02-04-2022</Received_Date>" + // FOR UAT
//					"            <Registration_Auth>SUB REGISTER</Registration_Auth>"
//					+ "            <Registration_Date>05-12-2017</Registration_Date>" + "            <Review_Date>"
//					+ ldgCltrlRequestPojo.getReview_Dt() + "</Review_Date>" + "            <State>" + stateCode
//					+ "</State>" + "            <Visit_Date>05-12-2017</Visit_Date>"
//					+ "         </executeFinacleScript_CustomData>" + "      </executeFinacleScriptRequest>"
//					+ "   </Body></FIXML>]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
//
//		} catch (Exception e) {
//			logger.info("lodgeCollateralService.createIMMRequestPacket()" + OperationUtillity.traceException(e));
//		}
//		return rqstPacket;
//	}
//
//	public String createFDRequestPacket(String pinstid, MainPojo ldgCltrlRequestPojo) {
//		String rqstPacket = "";
//		String fdAmount = "0.0";
//
//		try {
//			rqstPacket = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
//					+ "<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
//					+ "   <Body>" + "      <executeFinacleScriptRequest>" + "         <executeFinacleScript_CustomData>"
//					+ "            <Ceiling_Limit>"
//					+ commonUtility.millionString(ldgCltrlRequestPojo.getValSubTypeSecInMn()) + "</Ceiling_Limit>"
//					+ "            <Collateral_Code>" + ldgCltrlRequestPojo.getCollateralCode() + "</Collateral_Code>"
//					+ "            <Collateral_Class>" + ldgCltrlRequestPojo.getCollateral_Class()
//					+ "</Collateral_Class>" + "            <Margin_Pcnt>10</Margin_Pcnt>" + // commented
////					"            <Margin_Pcnt>15</Margin_Pcnt>" + 
//					"            <LoanToValue_Pcnt>90</LoanToValue_Pcnt>" + // commented
////					"            <LoanToValue_Pcnt>85</LoanToValue_Pcnt>" + 
//					"            <DrAcct_No>" + ldgCltrlRequestPojo.getPolicy_No() + "</DrAcct_No>" + // need to map
//					"            <Received_Dt>02-04-2022</Received_Dt>" + // FOR UAT
//					"            <Due_Dt>02-04-2022</Due_Dt>" + // FOU UAT AND SYSDATE FOR LIVE
//					"            <Review_Dt>" + ldgCltrlRequestPojo.getReview_Dt() + "</Review_Dt>"
//					+ "            <ApportionAmt>100</ApportionAmt>" + "            <Notes>" + pinstid + "</Notes>"
//					+ "         </executeFinacleScript_CustomData>" + "         <ExecuteFinacleScriptInputVO>" +
////					"            <requestId>FI_LodgeCollateral_Deposits.scr</requestId>" + 
//					"            <requestId>FI_OMNI_LodgeCollateral.scr</requestId>" + // changed fd script - 31-01-2025
//					"         </ExecuteFinacleScriptInputVO>" + "      </executeFinacleScriptRequest>" + "   </Body>"
//					+ "   <Header>" + "      <RequestHeader>" + "         <MessageKey>"
//					+ "            <ChannelId>CLS</ChannelId>" + "            <LanguageId></LanguageId>"
//					+ "            <RequestUUID>" + commonUtility.createRequestUUID() + "</RequestUUID>"
//					+ "            <ServiceRequestId>executeFinacleScript</ServiceRequestId>"
//					+ "            <ServiceRequestVersion>10.2</ServiceRequestVersion>" + "         </MessageKey>"
//					+ "         <RequestMessageInfo>" + "            <ArmCorrelationId></ArmCorrelationId>"
//					+ "            <BankId>BM3</BankId>" + "            <EntityId></EntityId>"
//					+ "            <EntityType></EntityType>" + "            <MessageDateTime>" + LocalDateTime.now()
//					+ "</MessageDateTime>" + "            <TimeZone></TimeZone>" + "         </RequestMessageInfo>"
//					+ "         <Security>" + "            <FICertToken></FICertToken>"
//					+ "            <SSOTransferToken></SSOTransferToken>" + "            <RealUser></RealUser>"
//					+ "            <RealUserLoginSessionId></RealUserLoginSessionId>"
//					+ "            <RealUserPwd></RealUserPwd>" + "            <Token>"
//					+ "               <PasswordToken>" + "                  <Password></Password>"
//					+ "                  <UserId></UserId>" + "               </PasswordToken>" + "            </Token>"
//					+ "         </Security>" + "      </RequestHeader>" + "   </Header>" + "</FIXML>"
//					+ "]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
//		} catch (Exception e) {
//			logger.info("lodgeCollateralService.createIMMRequestPacket() " + OperationUtillity.traceException(e));
//		}
//		return rqstPacket;
//	}
//
//	public Map<String, String> convertLodgeCollateralResToPojo(String pinstId, String lodgeCollateralResponsePacket,
//			String Status) throws JsonProcessingException, SQLException {
//		Map<String, String> lodgeCollateralRspnsPckt = new WeakHashMap<>();
//		try {
//			if (Status.equalsIgnoreCase("Success")) {
//
//				String message = lodgeCollateralResponsePacket.substring(
//						lodgeCollateralResponsePacket.indexOf("<message>") + "<message>".length(),
//						lodgeCollateralResponsePacket.indexOf("</message>"));
//
//				lodgeCollateralRspnsPckt.put("ErrorDesc", message);
//
//			} else {
//				lodgeCollateralRspnsPckt.putAll(xmlToMap.packetDataToMap(pinstId, lodgeCollateralResponsePacket));
//			}
//		} catch (Exception e) {
//			logger.info("\nlodgeCollateralService.convertLodgeCollateralResToPojo()",
//					OperationUtillity.traceException(pinstId, e));
//		}
//		return lodgeCollateralRspnsPckt;
//	}
//
//}
