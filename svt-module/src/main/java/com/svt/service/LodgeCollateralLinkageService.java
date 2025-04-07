package com.svt.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.svt.dao.CommonDaoForInqDlnkWtdrwl;
import com.svt.dao.FetchStatus;
import com.svt.dao.lodgeCollateralDaoImpl;
import com.svt.model.commonModel.InnerPojo;
import com.svt.model.commonModel.MainPojo;
import com.svt.model.commonModel.serviceDetails.ServiceDetails;
import com.svt.utils.common.OperationUtillity;
import com.svt.utils.common.SOAPRequestUtility;
import com.svt.utils.common.commonUtility;
import com.svt.utils.common.updateServiceDetails;
import com.svt.utils.common.xmlToMap;

@Service
public class LodgeCollateralLinkageService {
	
	private static final Logger logger = LoggerFactory.getLogger(LodgeCollateralLinkageService.class);
	
	@Autowired
	lodgeCollateralDaoImpl daoImpl;

	@Autowired
	CommonDaoForInqDlnkWtdrwl commonDaoForInqDlnkWtdrwl;

	@Autowired
	FetchStatus fetchStatus;

	@Autowired
	updateServiceDetails updtServiceDetails;
	
	public Object executeLodgeCollateralLinkageService(String pinstid, String securityName, String subTypeSecurity,
			String typeOfSvt, String product, String processName) throws Exception {

		List<Object> lodgeCollateralResult = new ArrayList<>();
		if (subTypeSecurity.equals("ALL")) {
			ArrayList<MainPojo> subTypeSecurityList = commonDaoForInqDlnkWtdrwl.fetchCommonData(pinstid, processName);

			logger.info("LodgeCollateralLinkageService.executeLodgeCollateralService(ALL).subTypeSecurityList = "
					+ subTypeSecurityList);

			for (MainPojo MainPojo : subTypeSecurityList) {
				for (InnerPojo svtSecDtls : MainPojo.getInnerPojo()) {
					try {

						String requestUuid = commonUtility.createRequestUUID();
						String dateAndTime = commonUtility.dateFormat();

						MainPojo.setRequestId(requestUuid);
						MainPojo.setDateAndTime(dateAndTime);
						MainPojo.setSubTypeSecurity(svtSecDtls.getSubTypeSecurity());
						MainPojo.setTypeOfSecurity(svtSecDtls.getTypeOfSecurity());
						MainPojo.setProduct(svtSecDtls.getProduct());
						MainPojo.setLimitPrefix(svtSecDtls.getLimitPrefix());
						MainPojo.setLimitSuffix(svtSecDtls.getLimitSuffix());
						MainPojo.setColtrlLinkage("Yes");
						MainPojo.setPolicy_No(svtSecDtls.getPolicyNumber());
						MainPojo.setPolicy_Amt(svtSecDtls.getPolicyAmount());

						MainPojo.setCollateralCode(daoImpl.getCollateralCode(MainPojo.getTypeOfSecurity()));

						lodgeCollateralResult.add(LodgeSubTypeSecCollateralLinkage(pinstid, MainPojo,svtSecDtls, processName));
					} catch (Exception e) {
						logger.info(OperationUtillity.traceException(pinstid, e));
					}
				}
			}
		} else {
			MainPojo lodgeCollateralSec = commonDaoForInqDlnkWtdrwl.fetchIndividualProductDtls(pinstid, securityName,
					subTypeSecurity, typeOfSvt, product, processName);

			logger.info("LodgeCollateralLinkageService.executeLodgeCollateralService(Else).subTypeSecurityList = "
					+ lodgeCollateralSec.toString());

			for (InnerPojo svtSecDtls : lodgeCollateralSec.getInnerPojo()) {
				try {
					if (daoImpl.checkServiceStatus(pinstid, svtSecDtls, lodgeCollateralSec)) {

						String requestUuid = commonUtility.createRequestUUID();
						String dateAndTime = commonUtility.dateFormat();

						lodgeCollateralSec.setRequestId(requestUuid);
						lodgeCollateralSec.setDateAndTime(dateAndTime);

						lodgeCollateralSec.setSubTypeSecurity(svtSecDtls.getSubTypeSecurity());
						lodgeCollateralSec.setTypeOfSecurity(svtSecDtls.getTypeOfSecurity());
						lodgeCollateralSec.setProduct(svtSecDtls.getProduct());
						lodgeCollateralSec
								.setCollateralCode(daoImpl.getCollateralCode(lodgeCollateralSec.getTypeOfSecurity()));
						lodgeCollateralSec.setTypeOfCharge(svtSecDtls.getTypeOfCharge());
						lodgeCollateralSec.setPolicy_No(svtSecDtls.getPolicyNumber());
						lodgeCollateralSec.setLimitPrefix(svtSecDtls.getLimitPrefix());
						lodgeCollateralSec.setLimitSuffix(svtSecDtls.getLimitSuffix());
						commonDaoForInqDlnkWtdrwl.getSecurityOtherDetails(pinstid, lodgeCollateralSec);
						lodgeCollateralResult.add(LodgeSubTypeSecCollateralLinkage(pinstid, lodgeCollateralSec,svtSecDtls, processName));
					}
				} catch (Exception e) {
					logger.info(OperationUtillity.traceException(pinstid, e));
				}
			}
//			}
		}
		logger.info("\nLodgeCollateralLinkageService.executeLodgeCollateralService() lodgeCollateralResult ->"
				+ lodgeCollateralResult);

		return lodgeCollateralResult;
	}
	
	public Map<String, String> LodgeSubTypeSecCollateralLinkage(String pinstid, MainPojo mp,InnerPojo ip,
			String processName) throws IOException, SOAPException {

		logger.info("LodgeCollateralLinkageService.LodgeSubTypeSecCollateralLinkage() pinstId : " + pinstid + ",  lodgeCollateralRequestPj : " + mp);
		String soapRequestPacket = "";
		Map<String, String> lodgeCollateralLinkageResponseMap = new HashMap<>();
		try {

			String rqstType = "LODGE COLLATERAL LINKAGE : "
					+ OperationUtillity.NullReplace(mp.getSecurityName()) + " : " + ""
					+ OperationUtillity.NullReplace(mp.getSubTypeSecurity()) + " : "
					+ OperationUtillity.NullReplace(mp.getTypeOfSecurity()) + " : "
					+ OperationUtillity.NullReplace(mp.getProduct());
			
			String rqstTypeLC = "LODGE COLLATERAL : "
					+ OperationUtillity.NullReplace(mp.getSecurityName()) + " : " + ""
					+ OperationUtillity.NullReplace(mp.getSubTypeSecurity()) + " : "
					+ OperationUtillity.NullReplace(mp.getTypeOfSecurity()) + " : "
					+ OperationUtillity.NullReplace(mp.getProduct()) ;
			
			logger.info("before switch : " + pinstid + ",  lodgeCollateralRequestPj : "+mp.getSubTypeSecurity());

			switch (mp.getSubTypeSecurity()) {

			case "LIFE_INSURANCE": // LI
				 rqstType = "LODGE COLLATERAL LINKAGE : "
						+ OperationUtillity.NullReplace(mp.getSecurityName()) + " : " + ""
						+ OperationUtillity.NullReplace(mp.getSubTypeSecurity()) + " : "
						+ OperationUtillity.NullReplace(mp.getTypeOfSecurity()) + " : "
						+ OperationUtillity.NullReplace(mp.getProduct()) + " : "
						+ OperationUtillity.NullReplace(mp.getPolicy_No());
				 
				 rqstTypeLC = "LODGE COLLATERAL : "
							+ OperationUtillity.NullReplace(mp.getSecurityName()) + " : " + ""
							+ OperationUtillity.NullReplace(mp.getSubTypeSecurity()) + " : "
							+ OperationUtillity.NullReplace(mp.getTypeOfSecurity()) + " : "
							+ OperationUtillity.NullReplace(mp.getProduct()) + " : "
							+ OperationUtillity.NullReplace(mp.getPolicy_No());
				 
			case "Mutual_funds_Units": //  MFU
				 rqstType = "LODGE COLLATERAL LINKAGE : "
						+ OperationUtillity.NullReplace(mp.getSecurityName()) + " : " + ""
						+ OperationUtillity.NullReplace(mp.getSubTypeSecurity()) + " : "
						+ OperationUtillity.NullReplace(mp.getTypeOfSecurity()) + " : "
						+ OperationUtillity.NullReplace(mp.getProduct()) + " : "
						+ OperationUtillity.NullReplace(mp.getPolicy_No());
				 
			    rqstTypeLC = "LODGE COLLATERAL : "
							+ OperationUtillity.NullReplace(mp.getSecurityName()) + " : " + ""
							+ OperationUtillity.NullReplace(mp.getSubTypeSecurity()) + " : "
							+ OperationUtillity.NullReplace(mp.getTypeOfSecurity()) + " : "
							+ OperationUtillity.NullReplace(mp.getProduct()) + " : "
							+ OperationUtillity.NullReplace(mp.getPolicy_No());
				
				 break;
			default:
				break;
			}
			
			mp.setRequestType(rqstType);
			logger.info("before switch : " + pinstid + ",  lodgeCollateralRequestPj : "+mp.getRequestType());
				
			String coll_ID = commonDaoForInqDlnkWtdrwl.getLodgeColl_ID(pinstid,rqstTypeLC,processName);
			mp.setLodgeColllateralID(coll_ID);
			Boolean executeStatus = fetchStatus.getStatusForService(pinstid, mp.getRequestType(),processName);
			logger.info("LodgeCollateralLinkageService.LodgeSubTypeSecCollateralLinkage()   executeStatus: " + executeStatus );

			if (executeStatus) {
				logger.info("LodgeCollateralLinkageService.LodgeSubTypeSecCollateralLinkage() inside  executeStatus: " + executeStatus );
			    soapRequestPacket = createLodgeCollLinkageRequestPacket(pinstid,mp,ip);
			    logger.info("LodgeCollateralLinkageService.LodgeSubTypeSecCollateralLinkage() soapRequestPacket: " + soapRequestPacket );
			    String lodgeCollLinkageResponse = SOAPRequestUtility.soapResponse(soapRequestPacket);
			    logger.info("LodgeCollateralLinkageService.LodgeSubTypeSecCollateralLinkage() lodgeCollLinkageResponse: " + lodgeCollLinkageResponse );
			    try {
			    	lodgeCollateralLinkageResponseMap.put("requestType", mp.getRequestType());
			    	lodgeCollateralLinkageResponseMap.put("requestPacket", soapRequestPacket);
			    	lodgeCollateralLinkageResponseMap.put("responsePacket", lodgeCollLinkageResponse);
					String Status = "";

					if (lodgeCollLinkageResponse.contains("<HostTransaction>")) {

						String HostTransaction = lodgeCollLinkageResponse.substring(
								lodgeCollLinkageResponse.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
								lodgeCollLinkageResponse.indexOf("</HostTransaction>"));
						if (HostTransaction.contains("<Status>")) {
							Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),
									HostTransaction.indexOf("</Status>"));
							lodgeCollateralLinkageResponseMap.put("Status", Status);
						}
					}
					lodgeCollateralLinkageResponseMap
							.putAll(convertLodgeCollateralResToPojo(pinstid, lodgeCollLinkageResponse, Status));
					
					lodgeCollateralLinkageResponseMap.put(mp.getRequestType(), Status);

					Boolean retriger = false;
					if (!"SUCCESS".equalsIgnoreCase(Status)) {
						retriger = true;
					}
					
					ServiceDetails sdUpdate = new ServiceDetails(pinstid, mp.getRequestType(),
							"LODGE COLLATERAL LINKAGE", mp.getRequestType(), Status, soapRequestPacket,
							lodgeCollLinkageResponse, lodgeCollateralLinkageResponseMap.get("ErrorDesc"), retriger);

					updtServiceDetails.updateInitialStatusInFiExecutionTable(sdUpdate);

					if (processName.equalsIgnoreCase("Limit_Setup")) {
						OperationUtillity.API_RequestResponse_Insert(soapRequestPacket, lodgeCollLinkageResponse,
								mp.getRequestType(), pinstid, lodgeCollateralLinkageResponseMap, 
								mp.getRequestId());
					} else if (processName.equalsIgnoreCase("Monitoring")) {
						OperationUtillity.insertFiReqResMonitoring(soapRequestPacket, lodgeCollLinkageResponse,
								mp.getRequestType(), pinstid, lodgeCollateralLinkageResponseMap, 
								mp.getRequestId());
					}
				} catch (Exception e) {
					logger.info("\nLodgeCollateralLinkageService.LodgeSubTypeSecCollateralLinkage().Exception -->"
							+ OperationUtillity.traceException(pinstid, e));
				}

			}
			
			
					} catch (Exception e) {
			logger.info("\nException.LodgeCollateralLinkageService.LodgeSubTypeSecCollateralLinkage().pinstid " + pinstid + " ::\n "
					+ OperationUtillity.traceException(pinstid, e));
			ServiceDetails sdUpdateex = new ServiceDetails(pinstid, mp.getRequestType(),
					"LODGE COLLATERAL LINKAGE", mp.getRequestType(), "FAILURE", soapRequestPacket,
					"", e.getMessage(), true);
		//	logger.info("\nException.lodgeCollateralService.LodgeSubTypeSecCollateral() sdUpdateex BEFORE -->"+ sdUpdateex);
			updtServiceDetails.updateInitialStatusInFiExecutionTable(sdUpdateex);
	//		logger.info("\nException.lodgeCollateralService.LodgeSubTypeSecCollateral() sdUpdateex AFTER -->"+ sdUpdateex);
		}
		logger.info("\nLodgeCollateralLinkageService.LodgeSubTypeSecCollateralLinkage().lodgeCollateralResponsePojoList check-->"
				+ mp);
		return lodgeCollateralLinkageResponseMap;
	}

	
	public String createLodgeCollLinkageRequestPacket(String pinstId,MainPojo mp,InnerPojo ip) {
		String rqstPacket = "";
		try {

			logger.info("Entering into svtLinkageService.createSOAPRequest : : " + pinstId+"  and MainPojo{} -->"+mp);
			
			rqstPacket  = 		"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.fiusb.ci.infosys.com\"><soapenv:Header/><soapenv:Body><web:executeService><arg_0_0><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
				"<FIXML xmlns=\"http://www.finacle.com/fixml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.finacle.com/fixml ColtrlLinkAdd.xsd\">" + 
				"   <Header>" + 
				"      <RequestHeader>" + 
				"         <MessageKey>" + 
				"            <RequestUUID>"+commonUtility.createRequestUUID()+"</RequestUUID>" + 
				"            <ServiceRequestId>ColtrlLinkAdd</ServiceRequestId>" + 
				"            <ServiceRequestVersion>10.2</ServiceRequestVersion>" + 
				"            <ChannelId>CLS</ChannelId>" + 
				"            <LanguageId />" + 
				"         </MessageKey>" + 
				"         <RequestMessageInfo>" + 
				"            <BankId>BM3</BankId>" + 
				"            <TimeZone />" + 
				"            <EntityId />" + 
				"            <EntityType />" + 
				"            <ArmCorrelationId />" + 
				"            <MessageDateTime>"+LocalDateTime.now()+"</MessageDateTime>" + 
				"         </RequestMessageInfo>" + 
				"         <Security>" + 
				"            <Token>" + 
				"               <PasswordToken>" + 
				"                  <UserId />" + 
				"                  <Password />" + 
				"               </PasswordToken>" + 
				"            </Token>" + 
				"            <FICertToken />" + 
				"            <RealUserLoginSessionId />" + 
				"            <RealUser />" + 
				"            <RealUserPwd />" + 
				"            <SSOTransferToken />" + 
				"         </Security>" + 
				"      </RequestHeader>" + 
				"   </Header>" + 
				"   <Body>" + 
				"      <ColtrlLinkAddRequest>" + 
				"         <ColtrlLinkAddRq>" + 
				"            <ColtrlLinkageType>N</ColtrlLinkageType>	" + 
				"            <LimitNodeId>" + 
				"               <LimitPrefix>"+mp.getLimitPrefix()+"</LimitPrefix>" + 
				"               <LimitSuffix>"+mp.getLimitSuffix()+"</LimitSuffix>" + 
				"            </LimitNodeId>" + 
				"            <ColtrlId>"+mp.getLodgeColllateralID()+"</ColtrlId>" + 
				"            <ApportionedAmt>" + 
				"               <amountValue>"+commonUtility.millionString(ip.getSecurityValueMn())+"</amountValue>" + 
				"               <currencyCode>INR</currencyCode>" + 
				"            </ApportionedAmt>" + 
				"            <ColtrlNatureInd>C</ColtrlNatureInd>	" + 
				"            <MarginPcnt>" + 
				"			<value></value>" + 
				"		</MarginPcnt>" + 
				"		<LoanToValuePcnt>" + 
				"			<value></value>" + 
				"		</LoanToValuePcnt>" + 
				"         </ColtrlLinkAddRq>" + 
				"      </ColtrlLinkAddRequest>" + 
				"   </Body>" + 
				"</FIXML>" + 
				"]]></arg_0_0></web:executeService></soapenv:Body></soapenv:Envelope>";
			
			InputStream is = new ByteArrayInputStream(rqstPacket.getBytes());
			SOAPMessage requestSoap = MessageFactory.newInstance().createMessage(null, is);
			requestSoap.saveChanges();
			
		} catch(Exception e) {
			logger.info("\nlodgeCollateralLinkageService.createLodgeCollLinkageRequestPacket()",
					OperationUtillity.traceException(pinstId, e));
		}
		return rqstPacket;
	}
	
	public Map<String, String> convertLodgeCollateralResToPojo(String pinstId, String lodgeCollateralResponsePacket,
			String Status) throws JsonProcessingException, SQLException {
		Map<String, String> lodgeCollateralRspnsPckt = new WeakHashMap<>();
		try {
			if (Status.equalsIgnoreCase("Success")) {

				String message = lodgeCollateralResponsePacket.substring(
						lodgeCollateralResponsePacket.indexOf("<message>") + "<message>".length(),
						lodgeCollateralResponsePacket.indexOf("</message>"));

				lodgeCollateralRspnsPckt.put("ErrorDesc", message);

			} else {
				lodgeCollateralRspnsPckt.putAll(xmlToMap.packetDataToMap(pinstId, lodgeCollateralResponsePacket));
			}
		} catch (Exception e) {
			logger.info("\nlodgeCollateralService.convertLodgeCollateralResToPojo()",
					OperationUtillity.traceException(pinstId, e));
		}
		return lodgeCollateralRspnsPckt; // lodgeCollateralResponsePojo
	}

}


