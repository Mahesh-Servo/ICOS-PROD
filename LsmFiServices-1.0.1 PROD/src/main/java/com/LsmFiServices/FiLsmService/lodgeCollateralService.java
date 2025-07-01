package com.LsmFiServices.FiLsmService;

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

import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.SOAPRequestUtility;
import com.LsmFiServices.Utility.ServiceDetails;
import com.LsmFiServices.Utility.commonUtility;
import com.LsmFiServices.Utility.xmlToMap;
import com.LsmFiServices.dao.lodgeCollateralDaoImpl;
import com.LsmFiServices.pojo.lodgeCollateral.lodgeCollateralRequestPojo;
import com.LsmFiServices.pojo.lodgeCollateral.policySecurityDetails;
import com.LsmFiServices.pojo.lodgeCollateral.svtSecurityDetails;
import com.LsmFiServices.pojo.serviceexecutiondetails.ServiceDetailsUpdatePojo;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class lodgeCollateralService {

	private static final Logger logger = LoggerFactory.getLogger(lodgeCollateralService.class);

	@Autowired
	lodgeCollateralDaoImpl daoImpl;

	@Autowired
	private ServiceDetails serviceDetails;

    public Object executeLodgeCollateralService(String pinstid, String securityName, String subTypeSecurity,
	    String typeOfSvt, String processName) throws Exception {

	List<Object> lodgeCollateralResult = new ArrayList<>();
	String caseType = OperationUtillity.getCaseType(pinstid).get("CaseType");
	if ("LOPS".equalsIgnoreCase(caseType)) {
	    if (subTypeSecurity.equals("ALL")) {
		ArrayList<lodgeCollateralRequestPojo> subTypeSecurityList = daoImpl.getSubTypeSecurityList(pinstid,
			processName);

//			logger.info(
//					"\nlodgeCollateralController.lodgeCollateralCtrl().lodgeCollateralService.executeLodgeCollateralService() pinstId : "
//							+ pinstid + ",  subTypeSecurity : " + subTypeSecurity + ",  typeOfSvt : " + typeOfSvt
//							+ ",  processName : " + processName + "\nsubTypeSecurityList ==" + subTypeSecurityList);

		logger.info("subTypeSecurityList- check in main if->" + subTypeSecurityList);
		for (lodgeCollateralRequestPojo lodgeCollateralRequestPojo : subTypeSecurityList) {
//				if (OperationUtillity.NullReplace(lodgeCollateralRequestPojo.getSecurity_Created()).equalsIgnoreCase("Yes")) {
		    logger.info("lodgeCollateralRequestPojo.getSvtDtls() check in if-->" + lodgeCollateralRequestPojo);
		    for (svtSecurityDetails svtSecDtls : lodgeCollateralRequestPojo.getSvtDtls()) {
			lodgeCollateralRequestPojo.setSubTypeSecurity(svtSecDtls.getSub_Type_Security());
			lodgeCollateralRequestPojo.setTypeOfSecurity(svtSecDtls.getType_Of_Security());
			lodgeCollateralRequestPojo.setCollateral_Code(
				daoImpl.getCollateralCode(lodgeCollateralRequestPojo.getTypeOfSecurity()));
			lodgeCollateralResult
				.add(LodgeSubTypeSecCollateral(pinstid, lodgeCollateralRequestPojo, processName));
		    }
//				}
		}
	    } else {
		lodgeCollateralRequestPojo lodgeCollateralSec = daoImpl.getSubTypeSecurityData(pinstid, securityName,
			subTypeSecurity, typeOfSvt, processName);
		logger.info("In else lodgeCollateralSec check->" + lodgeCollateralSec);
//			if (OperationUtillity.NullReplace(lodgeCollateralSec.getSecurity_Created()).equalsIgnoreCase("Yes")) {
		logger.info("lodgeCollateralSec.getSvtDtls() check in else ->" + lodgeCollateralSec.getSvtDtls());
		for (svtSecurityDetails svtSecDtls : lodgeCollateralSec.getSvtDtls()) {
		    if (daoImpl.checkServiceStatus(pinstid, svtSecDtls, lodgeCollateralSec)) {
			lodgeCollateralSec.setSubTypeSecurity(svtSecDtls.getSub_Type_Security());
			lodgeCollateralSec.setTypeOfSecurity(svtSecDtls.getType_Of_Security());
			lodgeCollateralSec
				.setCollateral_Code(daoImpl.getCollateralCode(lodgeCollateralSec.getTypeOfSecurity()));
			lodgeCollateralResult.add(LodgeSubTypeSecCollateral(pinstid, lodgeCollateralSec, processName));
		    }
		}
//			}
	    }
	} else {
	    lodgeCollateralResult.add("Case Type is :" + caseType);
	}
	logger.info("lodgeCollateralService.executeLodgeCollateralService() lodgeCollateralResult check->"
		+ lodgeCollateralResult);
	return lodgeCollateralResult;
    }

	public Object LodgeSubTypeSecCollateral(String pinstid, lodgeCollateralRequestPojo lodgeCollateralRequestPj,
			String processName) throws IOException, SOAPException {

//		logger.info("lodgeCollateralService.LodgeSubTypeSecCollateral() pinstId : " + pinstid
//				+ ",  lodgeCollateralRequestPj : " + lodgeCollateralRequestPj);

		String soapRequestPacket = "";
		List<Object> lodgeCollateralResponsePojoList = new ArrayList<>();
		try {
//			lodgeCollateralRequestPj
//					.setSubTypeSecurity(lodgeCollateralRequestPj.getSubTypeSecurity().equalsIgnoreCase("Other")? "Others"
//							: lodgeCollateralRequestPj.getSubTypeSecurity());

			if (lodgeCollateralRequestPj.getSubTypeSecurity().equalsIgnoreCase("Other")
					|| lodgeCollateralRequestPj.getSubTypeSecurity().equalsIgnoreCase("Others")) {
				lodgeCollateralRequestPj.setSubTypeSecurity("Other");
			}

			String rqstType = "LODGE COLLATERAL : "
					+ OperationUtillity.NullReplace(lodgeCollateralRequestPj.getSecurity_Name()) + " : "+ OperationUtillity.NullReplace(lodgeCollateralRequestPj.getSubTypeSecurity()) + " : "+ OperationUtillity.NullReplace(lodgeCollateralRequestPj.getTypeOfSecurity());

			lodgeCollateralRequestPj.setRequestType(rqstType);
			logger.info("lodgeCollateralService.LodgeSubTypeSecCollateral().rqstType check ->" + rqstType);
			switch (lodgeCollateralRequestPj.getSubTypeSecurity()) {
			case "BOOK_DEBTS": // BD
				soapRequestPacket = createBookDebtRequestPacket(lodgeCollateralRequestPj);
				lodgeCollateralResponsePojoList.add(executeSoapMsgAndCreateRes(soapRequestPacket, pinstid, lodgeCollateralRequestPj, processName));
				break;
			case "STOCK": // inv
				soapRequestPacket = createInventoryStockRequestPacket(lodgeCollateralRequestPj);
				lodgeCollateralResponsePojoList.add(
						executeSoapMsgAndCreateRes(soapRequestPacket, pinstid, lodgeCollateralRequestPj, processName));
				break;
			case "MACHINERIES": // MAC
				soapRequestPacket = createMachineryRequestPacket(lodgeCollateralRequestPj);
				lodgeCollateralResponsePojoList.add(
						executeSoapMsgAndCreateRes(soapRequestPacket, pinstid, lodgeCollateralRequestPj, processName));
				break;
			case "LIFE_INSURANCE": // LI
				for (policySecurityDetails liPolicyDtls : lodgeCollateralRequestPj.getPolicySecurityDtls()) {

					lodgeCollateralRequestPj.setPolicy_No(liPolicyDtls.getPolicy_No());
					lodgeCollateralRequestPj.setPolicy_Amt(liPolicyDtls.getPolicy_Amount());

					rqstType = "LODGE COLLATERAL : "+ OperationUtillity.NullReplace(lodgeCollateralRequestPj.getSecurity_Name()) + " : "+ OperationUtillity.NullReplace(lodgeCollateralRequestPj.getSubTypeSecurity()) + " : "
							+ OperationUtillity.NullReplace(lodgeCollateralRequestPj.getTypeOfSecurity()) + " : "+ OperationUtillity.NullReplace(lodgeCollateralRequestPj.getPolicy_No());

					lodgeCollateralRequestPj.setRequestType(rqstType);

					soapRequestPacket = createLICRequestPacket(lodgeCollateralRequestPj);
					lodgeCollateralResponsePojoList.add(executeSoapMsgAndCreateRes(soapRequestPacket, pinstid,
							lodgeCollateralRequestPj, processName));
				}
				break;
			case "Mutual_funds_Units": // MFU
				for (policySecurityDetails liPolicyDtls : lodgeCollateralRequestPj.getPolicySecurityDtls()) {
					lodgeCollateralRequestPj.setNo_Of_Units(liPolicyDtls.getPolicy_No());
					lodgeCollateralRequestPj.setUnit_Val(liPolicyDtls.getPolicy_Amount());
				}

				soapRequestPacket = createMutualFundsUnitRequestPacket(lodgeCollateralRequestPj);
				lodgeCollateralResponsePojoList.add(
						executeSoapMsgAndCreateRes(soapRequestPacket, pinstid, lodgeCollateralRequestPj, processName));
				break;
			case "Other": // Other/
				soapRequestPacket = createOthersRequestPacket(lodgeCollateralRequestPj);
				lodgeCollateralResponsePojoList.add(
						executeSoapMsgAndCreateRes(soapRequestPacket, pinstid, lodgeCollateralRequestPj, processName));
				break;
			case "Tradable_Securities": // TS
				soapRequestPacket = createTSRequestPacket(lodgeCollateralRequestPj);
				lodgeCollateralResponsePojoList.add(
						executeSoapMsgAndCreateRes(soapRequestPacket, pinstid, lodgeCollateralRequestPj, processName));
				break;
			default:
				break;
			}
		} catch (Exception e) {
			logger.info("Exception.lodgeCollateralService.LodgeSubTypeSecCollateral().pinstid " + pinstid + " ::\n "
					+ OperationUtillity.traceException(pinstid, e));
		}
		logger.info("lodgeCollateralService.LodgeSubTypeSecCollateral().lodgeCollateralResponsePojoList check-->"+ lodgeCollateralResponsePojoList);
		return lodgeCollateralResponsePojoList;
	}

	public Map<String, String> executeSoapMsgAndCreateRes(String soapRequestPacket, String pinstid,
			lodgeCollateralRequestPojo lodgeCollateralRequestPj, String processName) throws IOException, SOAPException {


		logger.info("\n[lodgeCollateralService.LodgeSubTypeSecCollateral()].[pinstid: " + pinstid + " ].[requestType] --> "
						+ lodgeCollateralRequestPj.getRequestType() + "\n" + lodgeCollateralRequestPj);
		ServiceDetailsUpdatePojo pojo = new ServiceDetailsUpdatePojo();
		String lodgeCollateralResponsePacket = SOAPRequestUtility.soapResponse(soapRequestPacket);

		Map<String, String> lodgeCollateralResponseMap = new HashMap<>();
		try {
			pojo.setPinstId(pinstid);
			pojo.setServiceName("Lodge Collateral");
			lodgeCollateralResponseMap.put("requestType", lodgeCollateralRequestPj.getRequestType());
			lodgeCollateralResponseMap.put("requestPacket", soapRequestPacket);
			pojo.setServiceRequest(soapRequestPacket);
			
			logger.info("lodgeCollateralRequestPj check-->"+lodgeCollateralRequestPj);
			
			if ("LIFE_INSURANCE".equalsIgnoreCase(lodgeCollateralRequestPj.getSubTypeSecurity())) {
				pojo.setFacility(lodgeCollateralRequestPj.getSecurity_Name() + " :: "
						+ lodgeCollateralRequestPj.getSecurity_Type() + " :: "
						+ lodgeCollateralRequestPj.getSubTypeSecurity() + " :: "
						+ lodgeCollateralRequestPj.getTypeOfSecurity() + " :: "
						+ lodgeCollateralRequestPj.getPolicy_No());
				logger.info("pojo in if-->" + lodgeCollateralRequestPj);
				logger.info("Pojon check in iiif-->"+pojo);
			} else {
				pojo.setFacility(lodgeCollateralRequestPj.getSecurity_Name() + " :: "+ lodgeCollateralRequestPj.getSecurity_Type() + " :: "+ lodgeCollateralRequestPj.getSubTypeSecurity() + " :: "
						+ lodgeCollateralRequestPj.getTypeOfSecurity());
			}
			pojo.setStatus("Request Sent...!");
			logger.info("Pojo check--->in lodge"+pojo);
			serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
			lodgeCollateralResponseMap.put("responsePacket", lodgeCollateralResponsePacket);
			String Status = "";

			if (lodgeCollateralResponsePacket.contains("<HostTransaction>")) {
				String HostTransaction = lodgeCollateralResponsePacket.substring(lodgeCollateralResponsePacket.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
						lodgeCollateralResponsePacket.indexOf("</HostTransaction>"));
				if (HostTransaction.contains("<Status>")) {
					Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),HostTransaction.indexOf("</Status>"));
					lodgeCollateralResponseMap.put("Status", Status);
				}
			}
			lodgeCollateralResponseMap.putAll(convertLodgeCollateralResToPojo(pinstid, lodgeCollateralResponsePacket, Status));
			
			logger.info("lodgeCollateralRequestPj check-->"+lodgeCollateralRequestPj);
			logger.info("lodgeCollateralResponseMap check-->"+lodgeCollateralResponseMap);
//--
//			pojo.setRequestType(lodgeCollateralRequestPj.getSecurity_Name()+" :: "+lodgeCollateralRequestPj.getSecurity_Type()+" :: "+" :: "+lodgeCollateralRequestPj.getSubTypeSecurity()+" :: "+lodgeCollateralRequestPj.getTypeOfSecurity());
			pojo.setRequestType(lodgeCollateralRequestPj.getRequestType());
			logger.info("RequestType chjeck-->"+lodgeCollateralRequestPj.getSecurity_Name()+" :: "+lodgeCollateralRequestPj.getSubTypeSecurity()+" :: "+lodgeCollateralRequestPj.getTypeOfSecurity());
                	    if ("SUCCESS".equalsIgnoreCase(Status)) {
                		logger.info("lodgeCollateralService.executeSoapMsgAndCreateRes() into success-->" + Status);
                		pojo.setServiceResponse(lodgeCollateralResponsePacket);
                		pojo.setStatus(Status);
                		pojo.setReTrigger(true);
                		serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
                	    } else {
                		logger.info("lodgeCollateralService.executeSoapMsgAndCreateRes() into Failure-->" + Status);
                		pojo.setStatus(Status);
                		pojo.setServiceResponse(lodgeCollateralResponsePacket);
                		pojo.setMessage(lodgeCollateralResponseMap.get("ErrorDesc"));
                		pojo.setReTrigger(false);
                		serviceDetails.updateInitialStatusInFiExecutionTable(pojo);
                	    }
			if (processName.equalsIgnoreCase("Limit_Setup")) {
				OperationUtillity.API_RequestResponse_Insert(soapRequestPacket, lodgeCollateralResponsePacket,lodgeCollateralRequestPj.getRequestType(), pinstid, lodgeCollateralResponseMap, "");
			} else if (processName.equalsIgnoreCase("Monitoring")) {
				OperationUtillity.insertFiReqResMonitoring(soapRequestPacket, lodgeCollateralResponsePacket,
						lodgeCollateralRequestPj.getRequestType(), pinstid, lodgeCollateralResponseMap, "");
			}
		} catch (Exception e) {
			logger.info("\nlodgeCollateralService.LodgeSubTypeSecCollateral().Exception -->"+ OperationUtillity.traceException(pinstid, e));
			pojo.setRequestType(lodgeCollateralRequestPj.getRequestType());
		}
		return lodgeCollateralResponseMap;
	}

	
	public String createBookDebtRequestPacket(lodgeCollateralRequestPojo ldgCltrlRequestPojo) {
		
		String rqstPacket = "";
		try {
			String requestUuid = commonUtility.createRequestUUID();
			String dateAndTime = commonUtility.dateFormat();

			rqstPacket = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<FIXML" + 
				"        xmlns='http://www.finacle.com/fixml'" + 
				"        xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.finacle.com/fixml executeFinacleScript.xsd'>" + 
				"        <Header>" + 
				"            <RequestHeader>" + 
				"                <MessageKey>" + 
				"                    <RequestUUID>"+ requestUuid +"</RequestUUID>" + 
				"                    <ServiceRequestId>executeFinacleScript</ServiceRequestId>" + 
				"                    <ServiceRequestVersion>10.2</ServiceRequestVersion>" + 
				"                    <ChannelId>UAT</ChannelId>" + 
				"                    <LanguageId></LanguageId>" + 
				"                </MessageKey>" + 
				"                <RequestMessageInfo>" + 
				"                    <BankId>BM3</BankId>" + 
				"                    <TimeZone></TimeZone>" + 
				"                    <EntityId></EntityId>" + 
				"                    <EntityType></EntityType>" + 
				"                    <ArmCorrelationId></ArmCorrelationId>" + 
				"                    <MessageDateTime>"+ dateAndTime +"</MessageDateTime>" + 
				"                </RequestMessageInfo>" + 
				"                <Security>" + 
				"                    <Token>" + 
				"                        <PasswordToken>" + 
				"                            <UserId></UserId>" + 
				"                            <Password></Password>" + 
				"                        </PasswordToken>" + 
				"                    </Token>" + 
				"                    <FICertToken></FICertToken>" + 
				"                    <RealUserLoginSessionId></RealUserLoginSessionId>" + 
				"                    <RealUser></RealUser>" + 
				"                    <RealUserPwd></RealUserPwd>" + 
				"                    <SSOTransferToken></SSOTransferToken>" + 
				"                </Security>" + 
				"            </RequestHeader>" + 
				"        </Header>" + 
				"		" + 
				"<Body>" + 
				"<executeFinacleScriptRequest>" + 
				"<ExecuteFinacleScriptInputVO>" + 
				"<requestId>FI_LodgeCollateral_BD.scr</requestId>" + 
				"</ExecuteFinacleScriptInputVO>" + 
				"<executeFinacleScript_CustomData>" + 
				"<Ceiling_Limit>"+ldgCltrlRequestPojo.getCeiling_Limit()+"</Ceiling_Limit>" + 
				"<Collateral_Class>"+ldgCltrlRequestPojo.getCollateral_Class()+"</Collateral_Class>" + 
				"<Collateral_Code>"+ldgCltrlRequestPojo.getCollateral_Code()+"</Collateral_Code>" + 
				"<Gross_Val>"+ldgCltrlRequestPojo.getGross_Val()+"</Gross_Val>" + 
				"<Due_Dt>"+ldgCltrlRequestPojo.getDue_Dt()+"</Due_Dt>" + 
				"<Last_Val_Date>"+ldgCltrlRequestPojo.getLast_Val_Date()+"</Last_Val_Date>" + 
				"<NatureOfCharge>"+ldgCltrlRequestPojo.getNatureOfCharge()+"</NatureOfCharge>" + 
				"<notes>"+ldgCltrlRequestPojo.getNotes()+"</notes>" + 
				"<Receive_Dt>"+ldgCltrlRequestPojo.getReceive_Dt()+"</Receive_Dt>" + 
				"<Review_Dt>"+ldgCltrlRequestPojo.getReview_Dt()+"</Review_Dt>" + 
				"</executeFinacleScript_CustomData>" + 
				"</executeFinacleScriptRequest>" + 
				"</Body>" + 
				"</FIXML>"+
				"]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
			return rqstPacket;
		} catch (Exception e) {
			logger.info("lodgeCollateralService.createBookDebtRequestPacket()" + OperationUtillity.traceException(e));
		}
		return rqstPacket;
	}

	public String createInventoryStockRequestPacket(lodgeCollateralRequestPojo ldgCltrlRequestPojo) {
		String rqstPacket = "";
		try {
			String requestUuid = commonUtility.createRequestUUID();
			String dateAndTime = commonUtility.dateFormat();

		 rqstPacket = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<FIXML" + 
				"        xmlns='http://www.finacle.com/fixml'" + 
				"        xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.finacle.com/fixml executeFinacleScript.xsd'>" + 
				"        <Header>" + 
				"            <RequestHeader>" + 
				"                <MessageKey>" + 
				"                    <RequestUUID>"+requestUuid+"</RequestUUID>" + 
				"                    <ServiceRequestId>executeFinacleScript</ServiceRequestId>" + 
				"                    <ServiceRequestVersion>10.2</ServiceRequestVersion>" + 
				"                    <ChannelId>UAT</ChannelId>" + 
				"                    <LanguageId></LanguageId>" + 
				"                </MessageKey>" + 
				"                <RequestMessageInfo>" + 
				"                    <BankId>BM3</BankId>" + 
				"                    <TimeZone></TimeZone>" + 
				"                    <EntityId></EntityId>" + 
				"                    <EntityType></EntityType>" + 
				"                    <ArmCorrelationId></ArmCorrelationId>" + 
				"                    <MessageDateTime>"+dateAndTime+"</MessageDateTime>" + 
				"                </RequestMessageInfo>" + 
				"                <Security>" + 
				"                    <Token>" + 
				"                        <PasswordToken>" + 
				"                            <UserId></UserId>" + 
				"                            <Password></Password>" + 
				"                        </PasswordToken>" + 
				"                    </Token>" + 
				"                    <FICertToken></FICertToken>" + 
				"                    <RealUserLoginSessionId></RealUserLoginSessionId>" + 
				"                    <RealUser></RealUser>" + 
				"                    <RealUserPwd></RealUserPwd>" + 
				"                    <SSOTransferToken></SSOTransferToken>" + 
				"                </Security>" + 
				"            </RequestHeader>" + 
				"        </Header>" + 
				"		" + 
				"<Body>" + 
				"<executeFinacleScriptRequest>" + 
				"<ExecuteFinacleScriptInputVO>" + 
				"<requestId>FI_LodgeCollateral_Inv.scr</requestId>" + 
				"</ExecuteFinacleScriptInputVO>" + 
				"<executeFinacleScript_CustomData>" + 
				"<Ceiling_Limit>"+ldgCltrlRequestPojo.getCeiling_Limit()+"</Ceiling_Limit>" + 
				"<Collateral_Class>"+ldgCltrlRequestPojo.getCollateral_Class()+"</Collateral_Class>" + 
				"<Collateral_Code>"+ldgCltrlRequestPojo.getCollateral_Code()+"</Collateral_Code>" + 
				"<Gross_Val>"+ldgCltrlRequestPojo.getGross_Val()+"</Gross_Val>" + 
				"<Due_Dt>"+ldgCltrlRequestPojo.getDue_Dt()+"</Due_Dt>" + 
				"<Last_Val_Date>"+ldgCltrlRequestPojo.getLast_Val_Date()+"</Last_Val_Date>" + 
				"<notes>"+ldgCltrlRequestPojo.getNotes()+"</notes>" + 
				"<Receive_Dt>"+ldgCltrlRequestPojo.getReceive_Dt()+"</Receive_Dt>" + 
				"<Review_Dt>"+ldgCltrlRequestPojo.getReview_Dt()+"</Review_Dt>" + 
				"</executeFinacleScript_CustomData>" + 
				"</executeFinacleScriptRequest>" + 
				"</Body>" + 
				"</FIXML>"+
				"]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
			return rqstPacket;
		} catch (Exception e) {
			logger.info("lodgeCollateralService.createInventoryStockRequestPacket()" + OperationUtillity.traceException(e));
		}
		return rqstPacket;
	}

	public String createMachineryRequestPacket(lodgeCollateralRequestPojo ldgCltrlRequestPojo) {
		
		String rqstPacket = "";
		try {
			String requestUuid = commonUtility.createRequestUUID();
			String dateAndTime = commonUtility.dateFormat();

		 rqstPacket = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<FIXML" + 
				"        xmlns='http://www.finacle.com/fixml'" + 
				"        xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.finacle.com/fixml executeFinacleScript.xsd'>" + 
				"        <Header>" + 
				"            <RequestHeader>" + 
				"                <MessageKey>" + 
				"                    <RequestUUID>"+requestUuid+"</RequestUUID>" + 
				"                    <ServiceRequestId>executeFinacleScript</ServiceRequestId>" + 
				"                    <ServiceRequestVersion>10.2</ServiceRequestVersion>" + 
				"                    <ChannelId>UAT</ChannelId>" + 
				"                    <LanguageId></LanguageId>" + 
				"                </MessageKey>" + 
				"                <RequestMessageInfo>" + 
				"                    <BankId>BM3</BankId>" + 
				"                    <TimeZone></TimeZone>" + 
				"                    <EntityId></EntityId>" + 
				"                    <EntityType></EntityType>" + 
				"                    <ArmCorrelationId></ArmCorrelationId>" + 
				"                    <MessageDateTime>"+dateAndTime+"</MessageDateTime>" + 
				"                </RequestMessageInfo>" + 
				"                <Security>" + 
				"                    <Token>" + 
				"                        <PasswordToken>" + 
				"                            <UserId></UserId>" + 
				"                            <Password></Password>" + 
				"                        </PasswordToken>" + 
				"                    </Token>" + 
				"                    <FICertToken></FICertToken>" + 
				"                    <RealUserLoginSessionId></RealUserLoginSessionId>" + 
				"                    <RealUser></RealUser>" + 
				"                    <RealUserPwd></RealUserPwd>" + 
				"                    <SSOTransferToken></SSOTransferToken>" + 
				"                </Security>" + 
				"            </RequestHeader>" + 
				"        </Header>" + 
				"		" + 
				"<Body>" + 
				"<executeFinacleScriptRequest>" + 
				"<ExecuteFinacleScriptInputVO>" + 
				"<requestId>FI_LodgeCollateral_Mac.scr</requestId>" + 
				"</ExecuteFinacleScriptInputVO>" + 
				"<executeFinacleScript_CustomData>" + 
				"<Ceiling_Limit>"+ldgCltrlRequestPojo.getCeiling_Limit()+"</Ceiling_Limit>" + 
				"<Collateral_Class>"+ldgCltrlRequestPojo.getCollateral_Class()+"</Collateral_Class>" + 
				"<Collateral_Code>"+ldgCltrlRequestPojo.getCollateral_Code()+"</Collateral_Code>" + 
				"<Due_Dt>"+ldgCltrlRequestPojo.getDue_Dt()+"</Due_Dt>" + 
				"<Last_Val_Date>"+ldgCltrlRequestPojo.getLast_Val_Date()+"</Last_Val_Date>" + 
				"<NatureOfCharge>"+ldgCltrlRequestPojo.getNatureOfCharge()+"</NatureOfCharge>" + 
				"<notes>"+ldgCltrlRequestPojo.getNotes()+"</notes>" + 
				"<FromDeriveVal>"+ldgCltrlRequestPojo.getFromDeriveVal()+"</FromDeriveVal>" + 
				"<Value>"+ldgCltrlRequestPojo.getValue()+"</Value>" + 
				"<Receive_Dt>"+ldgCltrlRequestPojo.getReceive_Dt()+"</Receive_Dt>" + 
				"<Review_Dt>"+ldgCltrlRequestPojo.getReview_Dt()+"</Review_Dt>" + 
				"</executeFinacleScript_CustomData>" + 
				"</executeFinacleScriptRequest>" + 
				"</Body>" + 
				"</FIXML>" +
				"]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
		return rqstPacket;
		} catch (Exception e) {
			logger.info("lodgeCollateralService.createMachineryRequestPacket()" + OperationUtillity.traceException(e));
		}
		return rqstPacket;
	}

	public String createLICRequestPacket(lodgeCollateralRequestPojo ldgCltrlRequestPojo) {

		String rqstPacket = "";
		try {
			String requestUuid = commonUtility.createRequestUUID();
			String dateAndTime = commonUtility.dateFormat();

		 rqstPacket = 
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"    <FIXML" + 
				"        xmlns='http://www.finacle.com/fixml'" + 
				"        xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.finacle.com/fixml executeFinacleScript.xsd'>" + 
				"        <Header>" + 
				"            <RequestHeader>" + 
				"                <MessageKey>" + 
				"                    <RequestUUID>"+requestUuid+"</RequestUUID>" + 
				"                    <ServiceRequestId>executeFinacleScript</ServiceRequestId>" + 
				"                    <ServiceRequestVersion>10.2</ServiceRequestVersion>" + 
				"                    <ChannelId>UAT</ChannelId>" + 
				"                    <LanguageId></LanguageId>" + 
				"                </MessageKey>" + 
				"                <RequestMessageInfo>" + 
				"                    <BankId>BM3</BankId>" + 
				"                    <TimeZone></TimeZone>" + 
				"                    <EntityId></EntityId>" + 
				"                    <EntityType></EntityType>" + 
				"                    <ArmCorrelationId></ArmCorrelationId>" + 
				"                    <MessageDateTime>"+dateAndTime+"</MessageDateTime>" + 
				"                </RequestMessageInfo>" + 
				"                <Security>" + 
				"                    <Token>" + 
				"                        <PasswordToken>" + 
				"                            <UserId></UserId>" + 
				"                            <Password></Password>" + 
				"                        </PasswordToken>" + 
				"                    </Token>" + 
				"                    <FICertToken></FICertToken>" + 
				"                    <RealUserLoginSessionId></RealUserLoginSessionId>" + 
				"                    <RealUser></RealUser>" + 
				"                    <RealUserPwd></RealUserPwd>" + 
				"                    <SSOTransferToken></SSOTransferToken>" + 
				"                </Security>" + 
				"            </RequestHeader>" + 
				"        </Header>" + 
				"		" + 
				"<Body>" + 
				"<executeFinacleScriptRequest>" + 
				"<ExecuteFinacleScriptInputVO>" + 
				"<requestId>FI_LodgeCollateral_LI.scr</requestId>" + 
				"</ExecuteFinacleScriptInputVO>" + 
				"<executeFinacleScript_CustomData>" + 
				"<Ceiling_Limit>"+ldgCltrlRequestPojo.getCeiling_Limit()+"</Ceiling_Limit>" + 
				"<Collateral_Class>"+ldgCltrlRequestPojo.getCollateral_Class()+"</Collateral_Class>" + 
				"<Collateral_Code>"+ldgCltrlRequestPojo.getCollateral_Code()+"</Collateral_Code>" + 
				"<Policy_Amt>"+ldgCltrlRequestPojo.getPolicy_Amt()+"</Policy_Amt>" + 
				"<Policy_No>"+ldgCltrlRequestPojo.getPolicy_No()+"</Policy_No>" + 
				"<Due_Dt>"+ldgCltrlRequestPojo.getDue_Dt()+"</Due_Dt>" + 
				"<Last_Val_Date>"+ldgCltrlRequestPojo.getLast_Val_Date()+"</Last_Val_Date>" + 
				"<NatureOfCharge>"+ldgCltrlRequestPojo.getNatureOfCharge()+"</NatureOfCharge>" + 
				"<notes>"+ldgCltrlRequestPojo.getNotes()+"</notes>" + 
				"<Review_Dt>"+ldgCltrlRequestPojo.getReview_Dt()+"</Review_Dt>" + 
				"<Receive_Dt>"+ldgCltrlRequestPojo.getReceive_Dt()+"</Receive_Dt>" + 
				"</executeFinacleScript_CustomData>" + 
				"</executeFinacleScriptRequest>" + 
				"</Body>" + 
				"</FIXML>"+
				"]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";

			return rqstPacket;
		} catch (Exception e) {
			logger.info("lodgeCollateralService.createLICRequestPacket()" + OperationUtillity.traceException(e));
		}
		return rqstPacket;
	}

	public String createMutualFundsUnitRequestPacket(lodgeCollateralRequestPojo ldgCltrlRequestPojo) {

		String rqstPacket = "";
		try {
			String requestUuid = commonUtility.createRequestUUID();
			String dateAndTime = commonUtility.dateFormat();

		 rqstPacket = 
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"    <FIXML" + 
				"        xmlns='http://www.finacle.com/fixml'" + 
				"        xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.finacle.com/fixml executeFinacleScript.xsd'>" + 
				"        <Header>" + 
				"            <RequestHeader>" + 
				"                <MessageKey>" + 
				"                    <RequestUUID>"+requestUuid+"</RequestUUID>" + 
				"                    <ServiceRequestId>executeFinacleScript</ServiceRequestId>" + 
				"                    <ServiceRequestVersion>10.2</ServiceRequestVersion>" + 
				"                    <ChannelId>UAT</ChannelId>" + 
				"                    <LanguageId></LanguageId>" + 
				"                </MessageKey>" + 
				"                <RequestMessageInfo>" + 
				"                    <BankId>BM3</BankId>" + 
				"                    <TimeZone></TimeZone>" + 
				"                    <EntityId></EntityId>" + 
				"                    <EntityType></EntityType>" + 
				"                    <ArmCorrelationId></ArmCorrelationId>" + 
				"                    <MessageDateTime>"+dateAndTime+"</MessageDateTime>" + 
				"                </RequestMessageInfo>" + 
				"                <Security>" + 
				"                    <Token>" + 
				"                        <PasswordToken>" + 
				"                            <UserId></UserId>" + 
				"                            <Password></Password>" + 
				"                        </PasswordToken>" + 
				"                    </Token>" + 
				"                    <FICertToken></FICertToken>" + 
				"                    <RealUserLoginSessionId></RealUserLoginSessionId>" + 
				"                    <RealUser></RealUser>" + 
				"                    <RealUserPwd></RealUserPwd>" + 
				"                    <SSOTransferToken></SSOTransferToken>" + 
				"                </Security>" + 
				"            </RequestHeader>" + 
				"        </Header>" + 
				"		" + 
				"<Body>" + 
				"<executeFinacleScriptRequest>" + 
				"<ExecuteFinacleScriptInputVO>" + 
				"<requestId>FI_LodgeCollateral_MFU.scr</requestId>" + 
				"</ExecuteFinacleScriptInputVO>" + 
				"<executeFinacleScript_CustomData>" + 
				"<Ceiling_Limit>"+ldgCltrlRequestPojo.getCeiling_Limit()+"</Ceiling_Limit>" + 
				"<Collateral_Class>"+ldgCltrlRequestPojo.getCollateral_Class()+"</Collateral_Class>" + 
				"<Collateral_Code>"+ldgCltrlRequestPojo.getCollateral_Code()+"</Collateral_Code>" + 
				"<Unit_Val>"+ldgCltrlRequestPojo.getUnit_Val()+"</Unit_Val>" + 
				"<No_Of_Units>"+ldgCltrlRequestPojo.getNo_Of_Units()+"</No_Of_Units>" + 
				"<Due_Dt>"+ldgCltrlRequestPojo.getDue_Dt()+"</Due_Dt>" + 
				"<Last_Val_Date>"+ldgCltrlRequestPojo.getLast_Val_Date()+"</Last_Val_Date>" + 
				"<NatureOfCharge>"+ldgCltrlRequestPojo.getNatureOfCharge()+"</NatureOfCharge>" + 
				"<notes1>"+ldgCltrlRequestPojo.getNotes1()+"</notes1>" + 
				"<notes2>"+ldgCltrlRequestPojo.getNotes1()+"</notes2>" + 
				"<Review_Dt>"+ldgCltrlRequestPojo.getReview_Dt()+"</Review_Dt>" + 
				"</executeFinacleScript_CustomData>" + 
				"</executeFinacleScriptRequest>" + 
				"</Body>" + 
				"</FIXML>" +
				"]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
			return rqstPacket;
		} catch (Exception e) {
			logger.info("lodgeCollateralService.createMutualFundsUnitRequestPacket()" + OperationUtillity.traceException(e));
		}
		return rqstPacket;
	}

	public String createOthersRequestPacket(lodgeCollateralRequestPojo ldgCltrlRequestPojo) {
		
		String rqstPacket = "";
		try {
			String requestUuid = commonUtility.createRequestUUID();
			String dateAndTime = commonUtility.dateFormat();

		 rqstPacket = 
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<FIXML" + 
				"        xmlns='http://www.finacle.com/fixml'" + 
				"        xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.finacle.com/fixml executeFinacleScript.xsd'>" + 
				"        <Header>" + 
				"            <RequestHeader>" + 
				"                <MessageKey>" + 
				"                    <RequestUUID>"+ requestUuid +"</RequestUUID>" + 
				"                    <ServiceRequestId>executeFinacleScript</ServiceRequestId>" + 
				"                    <ServiceRequestVersion>10.2</ServiceRequestVersion>" + 
				"                    <ChannelId>UAT</ChannelId>" + 
				"                    <LanguageId></LanguageId>" + 
				"                </MessageKey>" + 
				"                <RequestMessageInfo>" + 
				"                    <BankId>BM3</BankId>" + 
				"                    <TimeZone></TimeZone>" + 
				"                    <EntityId></EntityId>" + 
				"                    <EntityType></EntityType>" + 
				"                    <ArmCorrelationId></ArmCorrelationId>" + 
				"                    <MessageDateTime>"+dateAndTime+"</MessageDateTime>" + 
				"                </RequestMessageInfo>" + 
				"                <Security>" + 
				"                    <Token>" + 
				"                        <PasswordToken>" + 
				"                            <UserId></UserId>" + 
				"                            <Password></Password>" + 
				"                        </PasswordToken>" + 
				"                    </Token>" + 
				"                    <FICertToken></FICertToken>" + 
				"                    <RealUserLoginSessionId></RealUserLoginSessionId>" + 
				"                    <RealUser></RealUser>" + 
				"                    <RealUserPwd></RealUserPwd>" + 
				"                    <SSOTransferToken></SSOTransferToken>" + 
				"                </Security>" + 
				"            </RequestHeader>" + 
				"        </Header>" + 
				"		" + 
				"<Body>" + 
				"<executeFinacleScriptRequest>" + 
				"<ExecuteFinacleScriptInputVO>" + 
				"<requestId>FI_LodgeCollateral_Oth.scr</requestId>" + 
				"</ExecuteFinacleScriptInputVO>" + 
				"<executeFinacleScript_CustomData>" + 
				"<Ceiling_Limit>"+ldgCltrlRequestPojo.getCeiling_Limit()+"</Ceiling_Limit>" + 
				"<Collateral_Class>"+ldgCltrlRequestPojo.getCollateral_Class()+"</Collateral_Class>" + 
				"<Collateral_Code>"+ldgCltrlRequestPojo.getCollateral_Code()+"</Collateral_Code>" + 
				"<Due_Dt>"+ldgCltrlRequestPojo.getDue_Dt()+"</Due_Dt>" + 
				"<Last_Val_Date>"+ldgCltrlRequestPojo.getLast_Val_Date()+"</Last_Val_Date>" + 
				"<NatureOfCharge>"+ldgCltrlRequestPojo.getNatureOfCharge()+"</NatureOfCharge>" + 
				"<notes>"+ldgCltrlRequestPojo.getNotes()+"</notes>" + 
				"<Collateral_Value>"+ldgCltrlRequestPojo.getCollateral_Value()+"</Collateral_Value>" + 
				"<Receive_Dt>"+ldgCltrlRequestPojo.getReceive_Dt()+"</Receive_Dt>" + 
				"<Review_Dt>"+ldgCltrlRequestPojo.getReview_Dt()+"</Review_Dt>" + 
				"</executeFinacleScript_CustomData>" + 
				"</executeFinacleScriptRequest>" + 
				"</Body>" + 
				"</FIXML>" + 
				"]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
			return rqstPacket;
		} catch (Exception e) {
			logger.info("lodgeCollateralService.createOthersRequestPacket()" + OperationUtillity.traceException(e));
		}
		return rqstPacket;
	}

	public String createTSRequestPacket(lodgeCollateralRequestPojo ldgCltrlRequestPojo) {
		String rqstPacket = "";
		try {
			String requestUuid = commonUtility.createRequestUUID();
			String dateAndTime = commonUtility.dateFormat();
		
		 rqstPacket = 
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<FIXML" + 
				"        xmlns='http://www.finacle.com/fixml'" + 
				"        xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.finacle.com/fixml executeFinacleScript.xsd'>" + 
				"        <Header>" + 
				"            <RequestHeader>" + 
				"                <MessageKey>" + 
				"                    <RequestUUID>"+ requestUuid +"</RequestUUID>" + 
				"                    <ServiceRequestId>executeFinacleScript</ServiceRequestId>" + 
				"                    <ServiceRequestVersion>10.2</ServiceRequestVersion>" + 
				"                    <ChannelId>UAT</ChannelId>" + 
				"                    <LanguageId></LanguageId>" + 
				"                </MessageKey>" + 
				"                <RequestMessageInfo>" + 
				"                    <BankId>BM3</BankId>" + 
				"                    <TimeZone></TimeZone>" + 
				"                    <EntityId></EntityId>" + 
				"                    <EntityType></EntityType>" + 
				"                    <ArmCorrelationId></ArmCorrelationId>" + 
				"                    <MessageDateTime>"+ dateAndTime +"</MessageDateTime>" + 
				"                </RequestMessageInfo>" + 
				"                <Security>" + 
				"                    <Token>" + 
				"                        <PasswordToken>" + 
				"                            <UserId></UserId>" + 
				"                            <Password></Password>" + 
				"                        </PasswordToken>" + 
				"                    </Token>" + 
				"                    <FICertToken></FICertToken>" + 
				"                    <RealUserLoginSessionId></RealUserLoginSessionId>" + 
				"                    <RealUser></RealUser>" + 
				"                    <RealUserPwd></RealUserPwd>" + 
				"                    <SSOTransferToken></SSOTransferToken>" + 
				"                </Security>" + 
				"            </RequestHeader>" + 
				"        </Header>" + 
				"		" + 
				"<Body>" + 
				"<executeFinacleScriptRequest>" + 
				"<ExecuteFinacleScriptInputVO>" + 
				"<requestId>FI_LodgeCollateral_TS.scr</requestId>" + 
				"</ExecuteFinacleScriptInputVO>" + 
				"<executeFinacleScript_CustomData>" + 
				"<Ceiling_Limit>"+ldgCltrlRequestPojo.getCeiling_Limit()+"</Ceiling_Limit>" + 
				"<Collateral_Class>"+ldgCltrlRequestPojo.getCollateral_Class()+"</Collateral_Class>" + 
				"<Collateral_Code>"+ldgCltrlRequestPojo.getCollateral_Code()+"</Collateral_Code>" + 
				"<Unit_Val>"+ldgCltrlRequestPojo.getUnit_Val()+"</Unit_Val>	" + 
				"<No_Of_Units>"+ldgCltrlRequestPojo.getNo_Of_Units()+"</No_Of_Units>" + 
				"<Due_Dt>"+ldgCltrlRequestPojo.getDue_Dt()+"</Due_Dt>" + 
				"<Last_Val_Date>"+ldgCltrlRequestPojo.getLast_Val_Date()+"</Last_Val_Date>" + 
				"<NatureOfCharge>"+ldgCltrlRequestPojo.getNatureOfCharge()+"</NatureOfCharge>" + 
				"<notes1>"+ldgCltrlRequestPojo.getNotes1()+"</notes1>" + 
				"<notes2>"+ldgCltrlRequestPojo.getNotes2()+"</notes2>" + 
				"<Review_Dt>"+ldgCltrlRequestPojo.getReview_Dt()+"</Review_Dt>" + 
				"</executeFinacleScript_CustomData>" + 
				"</executeFinacleScriptRequest>" + 
				"</Body>" + 
				"</FIXML>" +
				"]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
			return rqstPacket;
		} catch (Exception e) {
			logger.info("lodgeCollateralService.createTSRequestPacket()" + OperationUtillity.traceException(e));
		}
		return rqstPacket;
	}

	public Map<String, String> convertLodgeCollateralResToPojo(String pinstId, String lodgeCollateralResponsePacket,
			String Status) throws JsonProcessingException, SQLException {
		Map<String, String> lodgeCollateralRspnsPckt = new WeakHashMap<>();
		
		if (Status.equalsIgnoreCase("Success")) {
		
			String message = lodgeCollateralResponsePacket.substring(lodgeCollateralResponsePacket.indexOf("<message>") + "<message>".length(),
					lodgeCollateralResponsePacket.indexOf("</message>"));

			lodgeCollateralRspnsPckt.put("ErrorDesc", message);

		} else {
			lodgeCollateralRspnsPckt.putAll(xmlToMap.packetDataToMap(pinstId, lodgeCollateralResponsePacket));
		}
		return lodgeCollateralRspnsPckt; // lodgeCollateralResponsePojo
	}

}
