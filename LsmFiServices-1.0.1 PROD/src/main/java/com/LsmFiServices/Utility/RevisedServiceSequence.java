package com.LsmFiServices.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.SOAPException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.LsmFiServices.FiLsmController.lodgeCollateralController;
import com.LsmFiServices.FiLsmService.ChildLimitNodeModificationService;
import com.LsmFiServices.FiLsmService.FeeRecoveryService;
import com.LsmFiServices.FiLsmService.SVTFIService;
import com.LsmFiServices.dao.AccountAndLimitNodeLinkageDao;
import com.LsmFiServices.dao.ChildLimitNodeCreationDao;
import com.LsmFiServices.dao.PSLDao;
import com.LsmFiServices.dao.RateOfInterestDao;
import com.LsmFiServices.dao.SanctionedLimitDao;

@Component
public class RevisedServiceSequence {

	private static final Logger logger = LoggerFactory.getLogger(ServicesSequenceLogics.class);

	@Autowired
	private CreateFacilityLimitList createFacilityLimitList;

	@Autowired
	private ChildLimitNodeModificationService childLimitNodeModificationService;

	@Autowired
	private SanctionedLimitDao sanctionedLimitDao;

	@Autowired
	private ChildLimitNodeCreationDao childLimitNodeCreationDao;

	@Autowired
	private AccountAndLimitNodeLinkageDao accountAndLimitNodeLinkageDao;

	@Autowired
	private PSLDao PSLDao;

	@Autowired
	private RateOfInterestDao RateOfInterestDao;

	@Autowired
	private SVTFIService svtService;

	@Autowired
	private FeeRecoveryService feeService;

	@Autowired
	private lodgeCollateralController lodgeCollateralCntlr;

	@Autowired
	private ParentLimitNodeModificationUtility parentModUtility;

	@Autowired
	private ESMUtils esmUtils;

	public Map<String, Map<String, String>> executeChildServiceIndividually(String pinstId, String facility,
			HttpServletRequest req) {
		Map<String, String> childLimitNodeResponseMap = new LinkedHashMap<>();
		String proposalType = OperationUtillity.getProposalType(pinstId);
		Map<String, Map<String, String>> fiServiceResMap = new HashMap<>();
		Map<String, String> individualFacilityDataMap = new LinkedHashMap<>();
		Map<String, String> feeRecoveryResp = new LinkedHashMap<>();
		try {
			individualFacilityDataMap = createFacilityLimitList.getfacilityWiseData(pinstId, facility);

			if (!"FRESH SANCTION".equalsIgnoreCase(proposalType) && ((!facility.contains("CROSS CALL")
					&& parentModUtility.isLimitAsPerSanctionIsZero(pinstId, facility))
					|| (facility.contains("CROSS CALL")
							&& parentModUtility.isLimitAsPerSanctionIsZeroForCrossCall(pinstId, facility)))) {
				logger.info(
						"RevisedServiceSequence.executeChildServiceIndividually() :: Child Service check for PINSTID "
								+ pinstId + " :: Into 1st condition");
				childLimitNodeResponseMap = childLimitNodeCreationDao.childLimitNodeCreationDaoImlp(pinstId,
						individualFacilityDataMap);
				logger.info("childLimitNodeCreationDaoImlp-->" + childLimitNodeResponseMap);
				fiServiceResMap.put(
						"childLimitNodeResponseMap" + " : "
								+ OperationUtillity.NullReplace(individualFacilityDataMap.get("FACILITY_NAME")),
						childLimitNodeResponseMap);
				if (childLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
					executeAccountLevelServices(pinstId, individualFacilityDataMap);
				}
			}

//	    else if ("Renewal with Enhancement of limits".toUpperCase().equalsIgnoreCase(proposalType)
//		    || "Pure Enhancement".toUpperCase().equalsIgnoreCase(proposalType)) {
			else if (((!facility.contains("CROSS CALL")
					&& !parentModUtility.isLimitAsPerSanctionIsZero(pinstId, facility))
					|| (facility.contains("CROSS CALL")
							&& !parentModUtility.isLimitAsPerSanctionIsZeroForCrossCall(pinstId, facility)))
					&& (proposalType.contains("RENEWAL") || proposalType.contains("ENHANCEMENT"))) { // PARVEJ
				logger.info(
						"RevisedServiceSequence.executeChildServiceIndividually() :: Child Service check for PINSTID "
								+ pinstId + " :: Into 2nd condition");
				String status = childLimitNodeModificationService
						.childLimitNodeModificationService(pinstId, individualFacilityDataMap).get("Status");
				logger.info("childLimitNodeModificationService-->" + childLimitNodeResponseMap);
				fiServiceResMap.put(
						"childLimitNodeModification" + " : "
								+ OperationUtillity.NullReplace(individualFacilityDataMap.get("FACILITY_NAME")),
						childLimitNodeResponseMap);
				if (status.equalsIgnoreCase("success")) {
					executeAccountLevelServices(pinstId, individualFacilityDataMap);
				}
			} else {
				logger.info(
						"RevisedServiceSequence.executeChildServiceIndividually() :: Child Service check for PINSTID "
								+ pinstId + " :: Into 3rd condition");
				childLimitNodeResponseMap = childLimitNodeCreationDao.childLimitNodeCreationDaoImlp(pinstId,
						individualFacilityDataMap);
				logger.info("childLimitNodeCreationDaoImlp-->" + childLimitNodeResponseMap);
				fiServiceResMap.put(
						"childLimitNodeResponseMap" + " : "
								+ OperationUtillity.NullReplace(individualFacilityDataMap.get("FACILITY_NAME")),
						childLimitNodeResponseMap);
				if (childLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
					executeAccountLevelServices(pinstId, individualFacilityDataMap);
				}
			}
			Map<String, String> svtResponseMap = new LinkedHashMap<>();
			if (getFlagOfLevelTwoServices(pinstId)) {
				svtResponseMap = svtService.SVTService(pinstId, "common");
				fiServiceResMap.put("SVT", svtResponseMap);
			}
			// if (getFlagForFeeRecovery(pinstId)) {
			if (0 == OperationUtillity.getFeeServiceSuccessCount(pinstId)) {
				feeRecoveryResp = feeService.feeRecoveryService(pinstId, "common");
				fiServiceResMap.put("Fee Recovery", feeRecoveryResp);
			}
			// }
		} catch (SQLException | SOAPException e) {
			logger.info("RevisedServiceSequence.executeAllChildServices()-->" + OperationUtillity.traceException(e));
		}
		return fiServiceResMap;
	}

	public List<String> executeAllChildsForESM(String pinstId) {
		logger.info("executeAllChildsForESM() calling child for ESM");
		List<String> statusList = new ArrayList<>();
		String proposalType = OperationUtillity.getProposalType(pinstId);
		try {
			List<Map<String, String>> childFacilityList = createFacilityLimitList.createChildList(pinstId);
			for (Map<String, String> individualFacilityDataMap : childFacilityList) {
				String facility = individualFacilityDataMap.get("FACILITY_NAME");
				boolean isLimitAmountZero = false;
				isLimitAmountZero = commonUtility.isZero(individualFacilityDataMap.get("AMOUNT_LIMIT"));

				if (facility.contains("CROSS CALL")) {
					if (isLimitAmountZero && esmUtils.caseTypeIsESM(pinstId)) {
						continue;
					}
				} else {
					if (isLimitAmountZero && parentModUtility.isLimitAsPerSanctionIsZero(pinstId, facility)
							&& esmUtils.caseTypeIsESM(pinstId)) {
						continue; // if limit amount is 0 then child and subsequent services wont execute
					}
				}
				if ((!"FRESH SANCTION".equalsIgnoreCase(proposalType) && parentModUtility
						.isLimitAsPerSanctionIsZero(pinstId, individualFacilityDataMap.get("FACILITY_NAME")))
						|| (parentModUtility.isLimitAsPerSanctionIsZeroForCrossCall(pinstId,
								individualFacilityDataMap.get("FACILITY_NAME")))) {
					logger.info("executeAllChildsForESM() calling child for facility :: "
							+ individualFacilityDataMap.get("FACILITY_NAME"));
					childLimitNodeCreationDao.childLimitNodeCreationDaoImlp(pinstId, individualFacilityDataMap)
							.get("Status");
				} else if ((!parentModUtility.isLimitAsPerSanctionIsZero(pinstId,
						individualFacilityDataMap.get("FACILITY_NAME")))
						&& (proposalType.contains("RENEWAL") || proposalType.contains("ENHANCEMENT"))) {
					childLimitNodeModificationService
							.childLimitNodeModificationService(pinstId, individualFacilityDataMap).get("Status");
				} else {
					logger.info("executeAllChildsForESM() calling child for facility :: "
							+ individualFacilityDataMap.get("FACILITY_NAME"));
					childLimitNodeCreationDao.childLimitNodeCreationDaoImlp(pinstId, individualFacilityDataMap)
							.get("Status");
				}
			}
		} catch (SQLException e) {
			logger.info("RevisedServiceSequence.executeAllChildsForESM()\n" + pinstId + " :: \n"
					+ OperationUtillity.traceException(e));
		}
		return statusList;
	}

	public String executeLSMChildsAfterESM(String pinstId, String facility) {
		Map<String, String> childLimitNodeResponseMap = new LinkedHashMap<>();
		String status = "";
		try {
			Map<String, String> individualFacData = createFacilityLimitList.getfacilityWiseData(pinstId, facility);
			String facilityName = individualFacData.get("FACILITY_NAME");
			boolean isLimitAmountZero = false;
			isLimitAmountZero = commonUtility.isZero(individualFacData.get("AMOUNT_LIMIT"));
			if (facilityName.contains("CROSS CALL")) {
				if (isLimitAmountZero && !esmUtils.caseTypeIsESM(pinstId)) {
					return status;
				}
			} else {
				if (isLimitAmountZero && parentModUtility.isLimitAsPerSanctionIsZero(pinstId, facilityName)
						&& !esmUtils.caseTypeIsESM(pinstId)) {
					return status; // if limit amount is 0 then child and subsequent services wont execute
				}
			}
			status = childLimitNodeModificationService.childLimitNodeModificationService(pinstId, individualFacData)
					.get("Status");
			logger.info("childLimitNodeModificationDaoImlp-->" + childLimitNodeResponseMap);
			if (status.equalsIgnoreCase("success")) {
				executeAccountLevelServices(pinstId, individualFacData);
			}

			if (getFlagOfLevelTwoServices(pinstId)) { // it will say whether all child services are succeed or not if//
				svtService.SVTService(pinstId, "common");
			}
			if (0 == OperationUtillity.getFeeServiceSuccessCount(pinstId)) {
				feeService.feeRecoveryService(pinstId, "common");
			}
		} catch (Exception e) {
			logger.info("RevisedServiceSequence.executeAllChildServices()-->" + OperationUtillity.traceException(e));
		}
		return status;
	}

	public Map<String, Map<String, String>> executeESMChildIndividually(String pinstId, String facility) {
		Map<String, String> childLimitNodeResponseMap = new LinkedHashMap<>();
		String proposalType = OperationUtillity.getProposalType(pinstId);
		Map<String, Map<String, String>> fiServiceResMap = new HashMap<>();
		try {
			Map<String, String> individualFacData = createFacilityLimitList.getfacilityWiseData(pinstId, facility);

			if (!"FRESH SANCTION".equalsIgnoreCase(proposalType) && ((!facility.contains("CROSS CALL")
					&& parentModUtility.isLimitAsPerSanctionIsZero(pinstId, facility))
					|| (facility.contains("CROSS CALL")
							&& parentModUtility.isLimitAsPerSanctionIsZeroForCrossCall(pinstId, facility)))) {
				logger.info("RevisedServiceSequence.executeESMChildIndividually() :: Child Service check for PINSTID "
						+ pinstId + " :: Into 1st condition");
				childLimitNodeResponseMap = childLimitNodeCreationDao.childLimitNodeCreationDaoImlp(pinstId,
						individualFacData);
				logger.info("childLimitNodeCreationDaoImlp-->" + childLimitNodeResponseMap);
				fiServiceResMap.put(
						"childLimitNodeResponseMap" + " : "
								+ OperationUtillity.NullReplace(individualFacData.get("FACILITY_NAME")),
						childLimitNodeResponseMap);
			}

//	    else if ("Renewal with Enhancement of limits".toUpperCase().equalsIgnoreCase(proposalType)
//		    || "Pure Enhancement".toUpperCase().equalsIgnoreCase(proposalType)) {
			else if (((!facility.contains("CROSS CALL")
					&& !parentModUtility.isLimitAsPerSanctionIsZero(pinstId, facility))
					|| (facility.contains("CROSS CALL")
							&& !parentModUtility.isLimitAsPerSanctionIsZeroForCrossCall(pinstId, facility)))
					&& (proposalType.contains("RENEWAL") || proposalType.contains("ENHANCEMENT"))) { // PARVEJ
				logger.info("RevisedServiceSequence.executeESMChildIndividually() :: Child Service check for PINSTID "
						+ pinstId + " :: Into 2nd condition");
				String status = childLimitNodeModificationService
						.childLimitNodeModificationService(pinstId, individualFacData).get("Status");
				logger.info("childLimitNodeModificationService-->" + childLimitNodeResponseMap);
				fiServiceResMap.put(
						"childLimitNodeModification" + " : "
								+ OperationUtillity.NullReplace(individualFacData.get("FACILITY_NAME")),
						childLimitNodeResponseMap);
			} else {
				logger.info("RevisedServiceSequence.executeESMChildIndividually() :: Child Service check for PINSTID "
						+ pinstId + " :: Into 3rd condition");
				childLimitNodeResponseMap = childLimitNodeCreationDao.childLimitNodeCreationDaoImlp(pinstId,
						individualFacData);
				logger.info("childLimitNodeCreationDaoImlp-->" + childLimitNodeResponseMap);
				fiServiceResMap.put(
						"childLimitNodeResponseMap" + " : "
								+ OperationUtillity.NullReplace(individualFacData.get("FACILITY_NAME")),
						childLimitNodeResponseMap);
			}

		} catch (Exception e) {
			logger.info(
					"RevisedServiceSequence.executeESMChildIndividually()-->" + OperationUtillity.traceException(e));
		}
		return fiServiceResMap;
	}

	public Map<String, Map<String, String>> executeAllChildServices(String pinstId, HttpServletRequest req) {
		Map<String, String> childLimitNodeResponseMap = new LinkedHashMap<>();
//	List<String> listStatus = new ArrayList<>();
		String proposalType = OperationUtillity.getProposalType(pinstId);
		Map<String, Map<String, String>> fiServiceResMap = new HashMap<>();
		Map<String, String> svtResponseMap = new LinkedHashMap<>();
		List<Map<String, String>> childFacilityList = new LinkedList<>();
		Map<String, String> feeRecoveryResp = new LinkedHashMap<>();
		try {
			childFacilityList = createFacilityLimitList.createChildList(pinstId);
		} catch (SQLException e1) {
			logger.info("RevisedServiceSequence.executeAllChildServices(){}", OperationUtillity.traceException(e1));
		}
		try {
			for (Map<String, String> individualFacilityDataMap : childFacilityList) {
//				String limitAmount = individualFacilityDataMap.get("AMOUNT_LIMIT");
//
//				if (("0".equals(limitAmount) || "".equals(limitAmount)) && esmUtils.caseTypeIsESM(pinstId)) {
//					continue; // if limit amount is 0 then child and subsequent services wont execute
//				}
				String facility = individualFacilityDataMap.get("FACILITY_NAME");
				boolean isLimitAmountZero = false;
				isLimitAmountZero = commonUtility.isZero(individualFacilityDataMap.get("AMOUNT_LIMIT"));

				boolean isTOSClosed = "Limit to be closed".equalsIgnoreCase(
						OperationUtillity.NullReplace(individualFacilityDataMap.get("TYPE_OF_SERVICE")));
				if (facility.contains("CROSS CALL")) {
					if (isLimitAmountZero && !isTOSClosed && !esmUtils.caseTypeIsESM(pinstId)) {
						continue;
					}
				} else {
					if (isLimitAmountZero && !isTOSClosed && !esmUtils.caseTypeIsESM(pinstId)) {
						continue; // if limit amount is 0 then child and subsequent services wont execute
					}
				}

				if (!"FRESH SANCTION".equalsIgnoreCase(proposalType) && ((!facility.contains("CROSS CALL")
						&& parentModUtility.isLimitAsPerSanctionIsZero(pinstId, facility))
						|| (facility.contains("CROSS CALL")
								&& parentModUtility.isLimitAsPerSanctionIsZeroForCrossCall(pinstId, facility)))) {
					logger.info("RevisedServiceSequence.executeAllChildServices() :: Child Service check for PINSTID "
							+ pinstId + " :: Into 1st condition");
					childLimitNodeResponseMap = childLimitNodeCreationDao.childLimitNodeCreationDaoImlp(pinstId,
							individualFacilityDataMap);
					logger.info("childLimitNodeCreationDaoImlp-->" + childLimitNodeResponseMap);
					fiServiceResMap.put("childLimitNodeResponseMap" + " : " + OperationUtillity.NullReplace(facility),
							childLimitNodeResponseMap);
					if (childLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
						executeAccountLevelServices(pinstId, individualFacilityDataMap); // sanction limit for fresh
					}
				}
//		else if ("Renewal with Enhancement of limits".toUpperCase().equalsIgnoreCase(proposalType)
//			|| "Pure Enhancement".toUpperCase().equalsIgnoreCase(proposalType)) {
				else if (((!facility.contains("CROSS CALL")
						&& !parentModUtility.isLimitAsPerSanctionIsZero(pinstId, facility))
						|| (facility.contains("CROSS CALL")
								&& !parentModUtility.isLimitAsPerSanctionIsZeroForCrossCall(pinstId, facility)))
						&& (proposalType.contains("RENEWAL") || proposalType.contains("ENHANCEMENT"))) { // PARVEJ
					logger.info("RevisedServiceSequence.executeAllChildServices() :: Child Service check for PINSTID "
							+ pinstId + " :: Into 2nd condition");
					String status = childLimitNodeModificationService
							.childLimitNodeModificationService(pinstId, individualFacilityDataMap).get("Status");
					logger.info("status check {}", status);
					fiServiceResMap.put("childLimitNodeModification" + " : " + OperationUtillity.NullReplace(facility),
							childLimitNodeResponseMap);
					if (status.equalsIgnoreCase("success")) {
						executeAccountLevelServices(pinstId, individualFacilityDataMap); // sanction limit for renewal
					}
				} else {
					logger.info("RevisedServiceSequence.executeAllChildServices() :: Child Service check for PINSTID "
							+ pinstId + " :: Into 3rd condition");
					childLimitNodeResponseMap = childLimitNodeCreationDao.childLimitNodeCreationDaoImlp(pinstId,
							individualFacilityDataMap);
					logger.info("childLimitNodeCreationDaoImlp-->" + childLimitNodeResponseMap);
					fiServiceResMap.put("childLimitNodeResponseMap" + " : " + OperationUtillity.NullReplace(facility),
							childLimitNodeResponseMap);
					if (childLimitNodeResponseMap.get("Status").equalsIgnoreCase("success")) {
						executeAccountLevelServices(pinstId, individualFacilityDataMap);
					}
				}
			}
			if (getFlagOfLevelTwoServices(pinstId)) {
				try {
					svtResponseMap = svtService.SVTService(pinstId, "common");
					fiServiceResMap.put("SVT", svtResponseMap);
				} catch (Exception ex) {
					logger.info("RevisedServiceSequence.svtService.SVTService(pinstId, 'common')(ALL)-->"
							+ OperationUtillity.traceException(ex));
				}
//		try {
//		    fiServiceResMap.put("lodgeCollateral",
//			    lodgeCollateralCntlr.lodgeCollateralCtrl(pinstId, "ALL", "ALL", "ALL", "Limit_Setup"));
//		} catch (Exception ex) {
//		    logger.info("RevisedServiceSequence.executelodgeCollateral(ALL)-->"
//			    + OperationUtillity.traceException(pinstId, ex));
//		}
			}
			// if (getFlagForFeeRecovery(pinstId)) {
			if (0 == OperationUtillity.getFeeServiceSuccessCount(pinstId)) {
				feeRecoveryResp = feeService.feeRecoveryService(pinstId, "common");
				fiServiceResMap.put("Fee Recovery", feeRecoveryResp);
			}
			// }
		} catch (SQLException | SOAPException e) {
			logger.info("RevisedServiceSequence.executeAllChildServices()-->" + OperationUtillity.traceException(e));
		}
		return fiServiceResMap;
	}

	public Map<String, Map<String, String>> executeAccountLevelServices(String pinstId,
			Map<String, String> individualFacilityDataMap) {
		Map<String, Map<String, String>> fiServiceResMap = new HashMap<>();
		Map<String, String> RateOfInterest = new LinkedHashMap<>();
		Map<String, String> sanctionedLimit = new LinkedHashMap<>();
		Map<String, String> accountAndLimitNode = new LinkedHashMap<>();
		Map<String, String> PSL = new LinkedHashMap<>();
//		Map<String, String> feeRecoveryResp = new LinkedHashMap<>();

		Set<String> keys = null;
		int counter = -1;
		keys = individualFacilityDataMap.keySet();
		Iterator<String> ids = keys.iterator();
		while (ids.hasNext()) {
			if ((ids.next()).contains("ACCOUNT_NO_")) {
				counter++;
			}
		}
		logger.info("Checking counter -->" + counter);
		for (int i = 0; i <= counter; i++) {
			logger.info("Account No Inside for loop :" + individualFacilityDataMap.get("ACCOUNT_NO_" + i));
			if (OperationUtillity.NullReplace(individualFacilityDataMap.get("ACCOUNT_NO_" + i)) != null
					&& !OperationUtillity.NullReplace(individualFacilityDataMap.get("ACCOUNT_NO_" + i)).equals("")) {
				try {
					String DISTRICT_CODE = OperationUtillity.NullReplace(createFacilityLimitList
							.getDistrictCodeFromMaster(individualFacilityDataMap.get("ACCOUNT_NO_" + i), pinstId));
					individualFacilityDataMap.put("DISTRICT_CODE", DISTRICT_CODE);
					String INDUSTRY_CODE = PSLSoapRequestDataMap.getIndustryCodeFromMaster(pinstId);
					logger.info("[PSLController].[PSL()] [INDUSTRY_CODE] :: " + INDUSTRY_CODE);
					individualFacilityDataMap.put("INDUSTRY_CODE", INDUSTRY_CODE);
					String OCCUPATION_CODE = OperationUtillity
							.NullReplace(createFacilityLimitList.getPSLOccupationCode(pinstId));
					logger.info("[PSLController].[PSL()] [OCCUPATION_CODE] :: " + OCCUPATION_CODE);
					individualFacilityDataMap.put("OCCUPATION_CODE", OCCUPATION_CODE);
					if (checkStatusOfIndividualServiceFlag("SANCTION LIMIT :"
							+ OperationUtillity.NullReplace(individualFacilityDataMap.get("FACILITY_NAME")) + " :  "
							+ OperationUtillity.NullReplace(individualFacilityDataMap.get("ACCOUNT_NO_" + i)) + "",
							pinstId)) {
						sanctionedLimit = sanctionedLimitDao.sanctionedLimitDaoImlp(pinstId, individualFacilityDataMap,
								i);
						logger.info("executeAccountLevelServices().sanctionedLimit->" + sanctionedLimit);
						fiServiceResMap.put(
								"sanctionedLimit : "
										+ OperationUtillity.NullReplace(individualFacilityDataMap.get("FACILITY_NAME"))
										+ " : "
										+ OperationUtillity
												.NullReplace(individualFacilityDataMap.get("ACCOUNT_NO_" + i))
										+ "",
								sanctionedLimit);
						if (sanctionedLimit.get("Status").equalsIgnoreCase("success")) {
							if (checkStatusOfIndividualServiceFlag(
									"ACCOUNT AND LIMIT NODE LINKAGE : " + individualFacilityDataMap.get("FACILITY_NAME")
											+ " Account No : " + individualFacilityDataMap.get("ACCOUNT_NO_" + i) + "",
									pinstId)) {
								logger.info("Before thread sleep ::" + LocalDateTime.now().toString());
								try {
									Thread.sleep(2000);
								} catch (InterruptedException e) {
									logger.info("Exception while waiting :: " + OperationUtillity.traceException(e));
								}
								logger.info("After thread sleep ::" + LocalDateTime.now().toString());
								accountAndLimitNode = accountAndLimitNodeLinkageDao
										.accountAndLimitNodeLinkageDaoImpl(pinstId, individualFacilityDataMap, i);
								logger.info(
										"executeAccountLevelServices.accountAndLimitNodeMap->" + accountAndLimitNode);
								fiServiceResMap.put(
										"accountAndLimitNodeLinkage : "
												+ OperationUtillity
														.NullReplace(individualFacilityDataMap.get("FACILITY_NAME"))
												+ " : "
												+ OperationUtillity
														.NullReplace(individualFacilityDataMap.get("ACCOUNT_NO_" + i)),
										accountAndLimitNode);
								if (accountAndLimitNode.get("Status").equalsIgnoreCase("success")) {
									if (checkStatusOfIndividualServiceFlag(
											"PSL : " + individualFacilityDataMap.get("FACILITY_NAME") + " Account No : "
													+ individualFacilityDataMap.get("ACCOUNT_NO_" + i) + " ",
											pinstId)) {

										Map<String, String> plsMap = new LinkedHashMap<String, String>();
										plsMap = createFacilityLimitList.getPSLOtherData(pinstId, OperationUtillity
												.NullReplace(individualFacilityDataMap.get("FACILITY_NAME")));
										Set<String> keySet = plsMap.keySet();
										Iterator<String> plsI = keySet.iterator();
										while (plsI.hasNext()) {
											String key = plsI.next();
											individualFacilityDataMap.put(key, plsMap.get(key));
										}
										PSL = PSLDao.PSLDaoImlp(pinstId, individualFacilityDataMap, i);
										logger.info("executeAccountLevelServices().PSL->" + PSL);
										fiServiceResMap.put("PSL : "
												+ OperationUtillity
														.NullReplace(individualFacilityDataMap.get("FACILITY_NAME"))
												+ " : " + OperationUtillity.NullReplace(
														individualFacilityDataMap.get("ACCOUNT_NO_" + i))
												+ "", PSL);

									} else {
										PSL.put("Status", "Success");
									}

									String ip = "RATE OF INTEREST : " + individualFacilityDataMap.get("FACILITY_NAME")
											+ " : Data =  ACCOUNT_NUMBER : "
											+ individualFacilityDataMap.get("ACCOUNT_NO_" + i) + ", RATE_CODE : "
											+ individualFacilityDataMap.get("RATE_CODE") + ", ROI_SPREAD : "
											+ individualFacilityDataMap.get("ROI_SPREAD") + ", ROI_PEGGED_FLAG : "
											+ individualFacilityDataMap.get("ROI_PEGGED_FLAG")
											+ ", PEGGING_FREQUENCY_IN_MONTHS : "
											+ individualFacilityDataMap.get("PEGGING_FREQUENCY") + ", NUMBER_OF_DAYS : "
											+ individualFacilityDataMap.get("NUMBER_OF_DAYS") + ", ROI_Spread : "
											+ individualFacilityDataMap.get("ROI_SPREAD") + ", NUMBER_OF_DAYS_DATE : "
											+ individualFacilityDataMap.get("ROI_END_DATE");

									if (checkStatusOfIndividualServiceFlag(ip, pinstId)) {
										RateOfInterest = RateOfInterestDao.RateOfInterestDaoImpl(pinstId,
												individualFacilityDataMap, i);
										logger.info(
												"executeAccountLevelServices().RateOfInterestMap->" + RateOfInterest);
										fiServiceResMap.put(
												"RateOfInterest : " + OperationUtillity
														.NullReplace(individualFacilityDataMap.get("FACILITY_NAME"))
														+ " : "
														+ OperationUtillity.NullReplace(
																individualFacilityDataMap.get("ACCOUNT_NO_" + i)),
												RateOfInterest);
									}
								}
							}
						}
					}
				} catch (Exception e) {
					logger.info("RevisedServiceSequence.executeAccountLevelServices().Exception in main catch -->"
							+ OperationUtillity.traceException(e));
				} // catch
			} // if
		} // for
		return fiServiceResMap;
	}

	public Map<String, Map<String, String>> executeServicesSanctionOnwards(String pinstId,
			Map<String, String> individualFacilityDataMap) {

		logger.info("Calling executeServicesSanctionOnwards() for--> " + pinstId);
		Map<String, Map<String, String>> fiServiceResMap = new HashMap<>();
		Map<String, String> RateOfInterest = new LinkedHashMap<>();
		Map<String, String> sanctionedLimit = new LinkedHashMap<>();
		Map<String, String> accountAndLimitNode = new LinkedHashMap<>();
		Map<String, String> PSL = new LinkedHashMap<>();
		Map<String, String> feeRecoveryResp = new LinkedHashMap<>();
		try {
			Set<String> keys = null;
			int counter = -1;
			keys = individualFacilityDataMap.keySet();
			Iterator<String> ids = keys.iterator();
			while (ids.hasNext()) {
				if ((ids.next()).contains("ACCOUNT_NO_")) {
					counter++;
				}
			}
			logger.info("Checking counter -->" + counter);
			for (int i = 0; i <= counter; i++) {
				logger.info("Account No Inside for loop :" + individualFacilityDataMap.get("ACCOUNT_NO_" + i));
				if (OperationUtillity.NullReplace(individualFacilityDataMap.get("ACCOUNT_NO_" + i)) != null
						&& !OperationUtillity.NullReplace(individualFacilityDataMap.get("ACCOUNT_NO_" + i))
								.equals("")) {
					String DISTRICT_CODE = OperationUtillity.NullReplace(createFacilityLimitList
							.getDistrictCodeFromMaster(individualFacilityDataMap.get("ACCOUNT_NO_" + i), pinstId));
					individualFacilityDataMap.put("DISTRICT_CODE", DISTRICT_CODE);
					String INDUSTRY_CODE = PSLSoapRequestDataMap.getIndustryCodeFromMaster(pinstId);
					logger.info("[PSLController].[PSL()] [INDUSTRY_CODE] :: " + INDUSTRY_CODE);
					individualFacilityDataMap.put("INDUSTRY_CODE", INDUSTRY_CODE);
					String OCCUPATION_CODE = OperationUtillity
							.NullReplace(createFacilityLimitList.getPSLOccupationCode(pinstId));
					logger.info("[PSLController].[PSL()] [OCCUPATION_CODE] :: " + OCCUPATION_CODE);
					individualFacilityDataMap.put("OCCUPATION_CODE", OCCUPATION_CODE);
					if (checkStatusOfIndividualServiceFlag("SANCTION LIMIT :"
							+ OperationUtillity.NullReplace(individualFacilityDataMap.get("FACILITY_NAME")) + " :  "
							+ OperationUtillity.NullReplace(individualFacilityDataMap.get("ACCOUNT_NO_" + i)),
							pinstId)) {
						sanctionedLimit = sanctionedLimitDao.sanctionedLimitDaoImlp(pinstId, individualFacilityDataMap,
								i);

						logger.info("executeServicesSanctionOnwards().sanctionedLimitMap->" + sanctionedLimit);
						fiServiceResMap.put(
								"sanctionedLimit : "
										+ OperationUtillity.NullReplace(individualFacilityDataMap.get("FACILITY_NAME"))
										+ " : "
										+ OperationUtillity
												.NullReplace(individualFacilityDataMap.get("ACCOUNT_NO_" + i))
										+ "",
								sanctionedLimit);
						if (sanctionedLimit.get("Status").equalsIgnoreCase("success")) {
							if (checkStatusOfIndividualServiceFlag(
									"ACCOUNT AND LIMIT NODE LINKAGE : " + individualFacilityDataMap.get("FACILITY_NAME")
											+ " Account No : " + individualFacilityDataMap.get("ACCOUNT_NO_" + i) + "",
									pinstId)) {
								try {
									Thread.sleep(2000);
								} catch (InterruptedException e) {
									logger.info("Exception while waiting :: " + OperationUtillity.traceException(e));
								}
								accountAndLimitNode = accountAndLimitNodeLinkageDao
										.accountAndLimitNodeLinkageDaoImpl(pinstId, individualFacilityDataMap, i);
								logger.info(
										"executeServicesSanctionOnwards().accountAndLimitNode->" + accountAndLimitNode);
								fiServiceResMap.put(
										"accountAndLimitNodeLinkage : "
												+ OperationUtillity
														.NullReplace(individualFacilityDataMap.get("FACILITY_NAME"))
												+ " : "
												+ OperationUtillity
														.NullReplace(individualFacilityDataMap.get("ACCOUNT_NO_" + i)),
										accountAndLimitNode);
								if (accountAndLimitNode.get("Status").equalsIgnoreCase("success")) {
									if (checkStatusOfIndividualServiceFlag(
											"PSL : " + individualFacilityDataMap.get("FACILITY_NAME") + " Account No : "
													+ individualFacilityDataMap.get("ACCOUNT_NO_" + i) + " ",
											pinstId)) {

										if (accountAndLimitNode.get("Status").equalsIgnoreCase("success")) {
											Map<String, String> plsMap = new LinkedHashMap<String, String>();
											plsMap = createFacilityLimitList.getPSLOtherData(pinstId, OperationUtillity
													.NullReplace(individualFacilityDataMap.get("FACILITY_NAME")));
											Set<String> keySet = plsMap.keySet();
											Iterator<String> plsI = keySet.iterator();
											while (plsI.hasNext()) {
												String key = plsI.next();
												individualFacilityDataMap.put(key, plsMap.get(key));
											}
											PSL = PSLDao.PSLDaoImlp(pinstId, individualFacilityDataMap, i);
											logger.info("executeServicesSanctionOnwards().PSL->" + PSL);
											fiServiceResMap.put(
													"PSL : " + OperationUtillity
															.NullReplace(individualFacilityDataMap.get("FACILITY_NAME"))
															+ " : "
															+ OperationUtillity.NullReplace(
																	individualFacilityDataMap.get("ACCOUNT_NO_" + i))
															+ "",
													PSL);
										}
									}

									String ip = "RATE OF INTEREST : " + individualFacilityDataMap.get("FACILITY_NAME")
											+ " : Data =  ACCOUNT_NUMBER : "
											+ individualFacilityDataMap.get("ACCOUNT_NO_" + i) + ", RATE_CODE : "
											+ individualFacilityDataMap.get("RATE_CODE") + ", ROI_SPREAD : "
											+ individualFacilityDataMap.get("ROI_SPREAD") + ", ROI_PEGGED_FLAG : "
											+ individualFacilityDataMap.get("ROI_PEGGED_FLAG")
											+ ", PEGGING_FREQUENCY_IN_MONTHS : "
											+ individualFacilityDataMap.get("PEGGING_FREQUENCY") + ", NUMBER_OF_DAYS : "
											+ individualFacilityDataMap.get("NUMBER_OF_DAYS") + ", ROI_Spread : "
											+ individualFacilityDataMap.get("ROI_SPREAD") + ", NUMBER_OF_DAYS_DATE : "
											+ individualFacilityDataMap.get("ROI_END_DATE");

									if (checkStatusOfIndividualServiceFlag(ip, pinstId)) {
										RateOfInterest = RateOfInterestDao.RateOfInterestDaoImpl(pinstId,
												individualFacilityDataMap, i);
										logger.info(
												"executeServicesSanctionOnwards().RateOfInterest->" + RateOfInterest);
										fiServiceResMap.put(
												"RateOfInterest : " + OperationUtillity
														.NullReplace(individualFacilityDataMap.get("FACILITY_NAME"))
														+ " : "
														+ OperationUtillity.NullReplace(
																individualFacilityDataMap.get("ACCOUNT_NO_" + i)),
												RateOfInterest);
									}
								}
							}
						}
					}
				} // if
			} // inner for

			// if (getFlagForFeeRecovery(pinstId)) {
			if (0 == OperationUtillity.getFeeServiceSuccessCount(pinstId)) {
				feeRecoveryResp = feeService.feeRecoveryService(pinstId, "common");
				fiServiceResMap.put("Fee Recovery", feeRecoveryResp);
			}
			// }
		} catch (Exception e) {
			logger.info("RevisedServiceSequence.executeServicesSanctionOnwards().Exception-->"
					+ OperationUtillity.traceException(e));
		}
		return fiServiceResMap;
	}

	public Map<String, Map<String, String>> executeAccountAndLimitNodeLinkageOnwards(String pinstId,
			Map<String, String> individualFacilityDataMap) {

		logger.info("Calling executeAccountAndLimitNodeLinkageOnwards() for--> " + pinstId);
		Map<String, Map<String, String>> fiServiceResMap = new HashMap<>();
		Map<String, String> RateOfInterest = new LinkedHashMap<>();
		Map<String, String> accountAndLimitNode = new LinkedHashMap<>();
		Map<String, String> PSL = new LinkedHashMap<>();
		Map<String, String> feeRecoveryResp = new LinkedHashMap<>();
		try {
			Set<String> keys = null;
			int counter = -1;
			keys = individualFacilityDataMap.keySet();
			Iterator<String> ids = keys.iterator();
			while (ids.hasNext()) {
				if ((ids.next()).contains("ACCOUNT_NO_")) {
					counter++;
				}
			}
			logger.info("Checking counter -->" + counter);
			for (int i = 0; i <= counter; i++) {
				logger.info("Account No Inside for loop :" + individualFacilityDataMap.get("ACCOUNT_NO_" + i));
				if (OperationUtillity.NullReplace(individualFacilityDataMap.get("ACCOUNT_NO_" + i)) != null
						&& !OperationUtillity.NullReplace(individualFacilityDataMap.get("ACCOUNT_NO_" + i))
								.equals("")) {
					String DISTRICT_CODE = OperationUtillity.NullReplace(createFacilityLimitList
							.getDistrictCodeFromMaster(individualFacilityDataMap.get("ACCOUNT_NO_" + i), pinstId));
					individualFacilityDataMap.put("DISTRICT_CODE", DISTRICT_CODE);
					String INDUSTRY_CODE = PSLSoapRequestDataMap.getIndustryCodeFromMaster(pinstId);
					logger.info("[PSLController].[PSL()] [INDUSTRY_CODE] :: " + INDUSTRY_CODE);
					individualFacilityDataMap.put("INDUSTRY_CODE", INDUSTRY_CODE);
					String OCCUPATION_CODE = OperationUtillity
							.NullReplace(createFacilityLimitList.getPSLOccupationCode(pinstId));
					logger.info("[PSLController].[PSL()] [OCCUPATION_CODE] :: " + OCCUPATION_CODE);
					individualFacilityDataMap.put("OCCUPATION_CODE", OCCUPATION_CODE);

					if (checkStatusOfIndividualServiceFlag(
							"ACCOUNT AND LIMIT NODE LINKAGE : " + individualFacilityDataMap.get("FACILITY_NAME")
									+ " Account No : " + individualFacilityDataMap.get("ACCOUNT_NO_" + i) + "",
							pinstId)) {
						accountAndLimitNode = accountAndLimitNodeLinkageDao.accountAndLimitNodeLinkageDaoImpl(pinstId,
								individualFacilityDataMap, i);
						logger.info(
								"ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards().accountAndLimitNode->"
										+ accountAndLimitNode);
						fiServiceResMap.put(
								"accountAndLimitNodeLinkage : "
										+ OperationUtillity.NullReplace(individualFacilityDataMap.get("FACILITY_NAME"))
										+ " : "
										+ OperationUtillity
												.NullReplace(individualFacilityDataMap.get("ACCOUNT_NO_" + i)),
								accountAndLimitNode);
						if (accountAndLimitNode.get("Status").equalsIgnoreCase("success")) {
							if (checkStatusOfIndividualServiceFlag(
									"PSL : " + individualFacilityDataMap.get("FACILITY_NAME") + " Account No : "
											+ individualFacilityDataMap.get("ACCOUNT_NO_" + i) + " ",
									pinstId)) {

								if (accountAndLimitNode.get("Status").equalsIgnoreCase("success")) {
									Map<String, String> plsMap = new LinkedHashMap<String, String>();
									plsMap = createFacilityLimitList.getPSLOtherData(pinstId, OperationUtillity
											.NullReplace(individualFacilityDataMap.get("FACILITY_NAME")));
									Set<String> keySet = plsMap.keySet();
									logger.info("keySet--->" + keySet.toString());
									Iterator<String> plsI = keySet.iterator();
									while (plsI.hasNext()) {
										String key = plsI.next();
										individualFacilityDataMap.put(key, plsMap.get(key));
									}
									PSL = PSLDao.PSLDaoImlp(pinstId, individualFacilityDataMap, i);
									logger.info("ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards().PSL->"
											+ PSL);
									fiServiceResMap
											.put("PSL : "
													+ OperationUtillity
															.NullReplace(individualFacilityDataMap.get("FACILITY_NAME"))
													+ " : "
													+ OperationUtillity.NullReplace(
															individualFacilityDataMap.get("ACCOUNT_NO_" + i))
													+ "", PSL);
								}
							}

							String ip = "RATE OF INTEREST : " + individualFacilityDataMap.get("FACILITY_NAME")
									+ " : Data =  ACCOUNT_NUMBER : " + individualFacilityDataMap.get("ACCOUNT_NO_" + i)
									+ ", RATE_CODE : " + individualFacilityDataMap.get("RATE_CODE") + ", ROI_SPREAD : "
									+ individualFacilityDataMap.get("ROI_SPREAD") + ", ROI_PEGGED_FLAG : "
									+ individualFacilityDataMap.get("ROI_PEGGED_FLAG")
									+ ", PEGGING_FREQUENCY_IN_MONTHS : "
									+ individualFacilityDataMap.get("PEGGING_FREQUENCY") + ", NUMBER_OF_DAYS : "
									+ individualFacilityDataMap.get("NUMBER_OF_DAYS") + ", ROI_Spread : "
									+ individualFacilityDataMap.get("ROI_SPREAD") + ", NUMBER_OF_DAYS_DATE : "
									+ individualFacilityDataMap.get("ROI_END_DATE");

							if (checkStatusOfIndividualServiceFlag(ip, pinstId)) {
								RateOfInterest = RateOfInterestDao.RateOfInterestDaoImpl(pinstId,
										individualFacilityDataMap, i);
								logger.info(
										"ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards().RateOfInterest->"
												+ RateOfInterest);
								fiServiceResMap.put(
										"RateOfInterest : "
												+ OperationUtillity
														.NullReplace(individualFacilityDataMap.get("FACILITY_NAME"))
												+ " : "
												+ OperationUtillity
														.NullReplace(individualFacilityDataMap.get("ACCOUNT_NO_" + i)),
										RateOfInterest);
							}
						}
					}
				} // if
			} // inner for
				// if (getFlagForFeeRecovery(pinstId)) {
			feeRecoveryResp = feeService.feeRecoveryService(pinstId, "common");
			fiServiceResMap.put("Fee Recovery", feeRecoveryResp);
			// }
		} catch (Exception e) {
			logger.info(OperationUtillity.traceException(e));
		}
		return fiServiceResMap;
	}

	public Map<String, Map<String, String>> executePSLIndividually(String pinstId,
			Map<String, String> individualFacilityDataMap) {

		logger.info("Calling ServicesSequenceLogics.executeServicesAccountLinkageOnwards() for--> " + pinstId);
		Map<String, Map<String, String>> fiServiceResMap = new HashMap<>();
		Map<String, String> PSL = new LinkedHashMap<>();
		try {
			Set<String> keys = null;
			int counter = -1;
			keys = individualFacilityDataMap.keySet();
			Iterator<String> ids = keys.iterator();
			while (ids.hasNext()) {
				if ((ids.next()).contains("ACCOUNT_NO_")) {
					counter++;
				}
			}
			logger.info("Checking counter -->" + counter);
			for (int i = 0; i <= counter; i++) {
				logger.info("Account No Inside for loop :" + individualFacilityDataMap.get("ACCOUNT_NO_" + i));
				if (OperationUtillity.NullReplace(individualFacilityDataMap.get("ACCOUNT_NO_" + i)) != null
						&& !OperationUtillity.NullReplace(individualFacilityDataMap.get("ACCOUNT_NO_" + i))
								.equals("")) {
					String DISTRICT_CODE = OperationUtillity.NullReplace(createFacilityLimitList
							.getDistrictCodeFromMaster(individualFacilityDataMap.get("ACCOUNT_NO_" + i), pinstId));
					individualFacilityDataMap.put("DISTRICT_CODE", DISTRICT_CODE);
					String INDUSTRY_CODE = PSLSoapRequestDataMap.getIndustryCodeFromMaster(pinstId);
					individualFacilityDataMap.put("INDUSTRY_CODE", INDUSTRY_CODE);
					String OCCUPATION_CODE = OperationUtillity
							.NullReplace(createFacilityLimitList.getPSLOccupationCode(pinstId));
					individualFacilityDataMap.put("OCCUPATION_CODE", OCCUPATION_CODE);
					if (checkStatusOfIndividualServiceFlag("PSL : " + individualFacilityDataMap.get("FACILITY_NAME")
							+ " Account No : " + individualFacilityDataMap.get("ACCOUNT_NO_" + i) + " ", pinstId)) {
						Map<String, String> plsMap = new LinkedHashMap<String, String>();
						plsMap = createFacilityLimitList.getPSLOtherData(pinstId,
								OperationUtillity.NullReplace(individualFacilityDataMap.get("FACILITY_NAME")));
						Set<String> keySet = plsMap.keySet();
						Iterator<String> plsI = keySet.iterator();
						while (plsI.hasNext()) {
							String key = plsI.next();
							individualFacilityDataMap.put(key, plsMap.get(key));
						}
						PSL = PSLDao.PSLDaoImlp(pinstId, individualFacilityDataMap, i);
						logger.info("ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards().PSL->" + PSL);
						fiServiceResMap.put(
								"PSL : " + OperationUtillity.NullReplace(individualFacilityDataMap.get("FACILITY_NAME"))
										+ " : "
										+ OperationUtillity
												.NullReplace(individualFacilityDataMap.get("ACCOUNT_NO_" + i))
										+ "",
								PSL);
					}
				} // if
			} // inner for
		} catch (Exception e) {
			logger.info(OperationUtillity.traceException(e));
		}
		return fiServiceResMap;
	}

	public Map<String, Map<String, String>> executeROIIndividually(String pinstId,
			Map<String, String> individualFacilityDataMap) {

		logger.info("Calling RevisedServiceSequence.executeROIIndividually() for--> " + pinstId);
		Map<String, Map<String, String>> fiServiceResMap = new HashMap<>();
		Map<String, String> RateOfInterest = new LinkedHashMap<>();
//		Map<String, String> feeRecoveryResp = new LinkedHashMap<>();
		try {
			Set<String> keys = null;
			int counter = -1;
			keys = individualFacilityDataMap.keySet();
			Iterator<String> ids = keys.iterator();
			while (ids.hasNext()) {
				if ((ids.next()).contains("ACCOUNT_NO_")) {
					counter++;
				}
			}
			logger.info("Checking counter -->" + counter);
			for (int i = 0; i <= counter; i++) {
				logger.info("Account No Inside for loop :" + individualFacilityDataMap.get("ACCOUNT_NO_" + i));
				if (OperationUtillity.NullReplace(individualFacilityDataMap.get("ACCOUNT_NO_" + i)) != null
						&& !OperationUtillity.NullReplace(individualFacilityDataMap.get("ACCOUNT_NO_" + i))
								.equals("")) {
					String DISTRICT_CODE = OperationUtillity.NullReplace(createFacilityLimitList
							.getDistrictCodeFromMaster(individualFacilityDataMap.get("ACCOUNT_NO_" + i), pinstId));
					individualFacilityDataMap.put("DISTRICT_CODE", DISTRICT_CODE);
					String INDUSTRY_CODE = PSLSoapRequestDataMap.getIndustryCodeFromMaster(pinstId);
					logger.info("[PSLController].[PSL()] [INDUSTRY_CODE] :: " + INDUSTRY_CODE);
					individualFacilityDataMap.put("INDUSTRY_CODE", INDUSTRY_CODE);

					String ip = "RATE OF INTEREST : " + individualFacilityDataMap.get("FACILITY_NAME")
							+ " : Data =  ACCOUNT_NUMBER : " + individualFacilityDataMap.get("ACCOUNT_NO_" + i)
							+ ", RATE_CODE : " + individualFacilityDataMap.get("RATE_CODE") + ", ROI_SPREAD : "
							+ individualFacilityDataMap.get("ROI_SPREAD") + ", ROI_PEGGED_FLAG : "
							+ individualFacilityDataMap.get("ROI_PEGGED_FLAG") + ", PEGGING_FREQUENCY_IN_MONTHS : "
							+ individualFacilityDataMap.get("PEGGING_FREQUENCY") + ", NUMBER_OF_DAYS : "
							+ individualFacilityDataMap.get("NUMBER_OF_DAYS") + ", ROI_Spread : "
							+ individualFacilityDataMap.get("ROI_SPREAD") + ", NUMBER_OF_DAYS_DATE : "
							+ individualFacilityDataMap.get("ROI_END_DATE");

					if (checkStatusOfIndividualServiceFlag(ip, pinstId)) {
						RateOfInterest = RateOfInterestDao.RateOfInterestDaoImpl(pinstId, individualFacilityDataMap, i);
						logger.info("ServicesSequenceLogics.executeFromRateIntrestLinkageOnwards().RateOfInterest->"
								+ RateOfInterest);
						fiServiceResMap.put(
								"RateOfInterest : "
										+ OperationUtillity.NullReplace(individualFacilityDataMap.get("FACILITY_NAME"))
										+ " : "
										+ OperationUtillity
												.NullReplace(individualFacilityDataMap.get("ACCOUNT_NO_" + i)),
								RateOfInterest);
					}
				} // if
			} // inner for
		} catch (Exception e) {
			logger.info(OperationUtillity.traceException(e));
		}
		return fiServiceResMap;
	}

	public static boolean checkStatusOfIndividualServiceFlag(String requestType, String pinstid) {

		boolean flag = true;
		try (Connection con = DBConnect.getConnection()) {
			String lsmNumberQuery = "SELECT STATUS FROM LSM_SERVICE_REQ_RES WHERE PINSTID = ? AND REQUESTTYPE = ? ORDER BY DATETIME DESC FETCH FIRST ROW ONLY";
			try (PreparedStatement stmt = con.prepareStatement(lsmNumberQuery)) {
				stmt.setString(1, pinstid);
				stmt.setString(2, requestType);
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						if ("SUCCESS".equalsIgnoreCase(OperationUtillity.NullReplace(rs.getString("STATUS")))) {
							flag = false;
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error(OperationUtillity.traceException(ex));
		}
		return flag;
	}

	public static List<String> getAllFacilities(String pinstId) {
		List<String> listOfAllFacilities = new LinkedList<>();
		try (Connection con = DBConnect.getConnection()) {
			String query1 = "SELECT DISTINCT FACILITY_NAME AS FACILITY FROM LSM_LIMIT_ANSWERS WHERE PINSTID = ?";
			try (PreparedStatement stmt = con.prepareStatement(query1)) {
				stmt.setString(1, pinstId);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						if (!(OperationUtillity.NullReplace(rs.getString("FACILITY")).equals("Default Interest Rate"))
								&& !(OperationUtillity.NullReplace(rs.getString("FACILITY")).equals("Parent Limit"))) {
							listOfAllFacilities.add(OperationUtillity.NullReplace(rs.getString("FACILITY")));
						}
					}
				}
			}
			String query2 = "SELECT DISTINCT FACILITY_NAME AS FACILITY FROM LSM_CROSS_CALL_ANSWERS WHERE PINSTID = ?";
			try (PreparedStatement stmt = con.prepareStatement(query2)) {
				stmt.setString(1, pinstId);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						if (!(OperationUtillity.NullReplace(rs.getString("FACILITY")).equals("Default Interest Rate"))
								&& !(OperationUtillity.NullReplace(rs.getString("FACILITY")).equals("Parent Limit"))) {
							listOfAllFacilities.add(OperationUtillity.NullReplace(rs.getString("FACILITY")));
						}
					}
				}
			}
		} catch (Exception e) {
			logger.info(OperationUtillity.traceException(e));
		}
		return listOfAllFacilities;
	}

	public static boolean getFlagOfLevelTwoServices(String pinstId) {
		boolean flag = false;
		try (Connection con = DBConnect.getConnection()) {
			try (PreparedStatement stmt = con.prepareStatement(Queries.GET_CHILD_SERVICE_FLAG)) {
				stmt.setString(1, pinstId);
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next() && "TRUE".equalsIgnoreCase(rs.getString("STATUS"))) {
						flag = true;
					}
				}
			}
		} catch (Exception e) {
			logger.info(OperationUtillity.traceException(e));
		}
		return flag;
	}

	public boolean getFlagOfAccountLinkageServices(String pinstId) {
		boolean flag = false;

		logger.info("Entered into RevisedServiceSequence.getFlagOfAccountLinkageServices()");
		try {
			Map<String, List<String>> facilitiwiseAccountNumberListOfMap = facilityWiseAccountNumbersListOfMap(pinstId);// here
			logger.info(
					"RevisedServiceSequence.getFlagOfAccountLinkageServices().facilitiwiseAccountNumberListOfMap check -->"
							+ facilitiwiseAccountNumberListOfMap);
			List<String> listOfRequestTypes = new LinkedList<>();
			for (Map.Entry<String, List<String>> entry : facilitiwiseAccountNumberListOfMap.entrySet()) {
				String facility = entry.getKey();
				List<String> accounts = entry.getValue();
				for (String acno : accounts) {
					String requestType = "ACCOUNT AND LIMIT NODE LINKAGE : " + facility + " Account No : " + acno;
					listOfRequestTypes.add(requestType);
				}
			} // size 4
			logger.info("RevisedServiceSequence.getFlagOfAccountLinkageServices(.listOfRequestTypes check)-->"
					+ listOfRequestTypes);
			int successCount = 0;
			StringBuilder reuestTypeCondition = new StringBuilder("AND REQUESTTYPE IN (");
			for (String requestType : listOfRequestTypes) {
				reuestTypeCondition.append("'" + requestType + "',");
			}
			reuestTypeCondition.replace(reuestTypeCondition.length() - 1, reuestTypeCondition.length(), ")");
			logger.info("RequestType Ccheck-->" + pinstId + ": " + reuestTypeCondition.toString());

			try (Connection con = DBConnect.getConnection()) {
				String query2 = "SELECT COUNT(STATUS) SUCCESSCOUNT FROM LSM_SERVICE_REQ_RES WHERE PINSTID = ?  and  STATUS = ? "
						+ reuestTypeCondition.toString();
				try (PreparedStatement stmt = con.prepareStatement(query2)) {
					stmt.setString(1, pinstId);
					stmt.setString(2, "SUCCESS");
					try (ResultSet rs = stmt.executeQuery()) {
						while (rs.next()) {
							successCount = rs.getInt("SUCCESSCOUNT");
						}
					}
				}
			} catch (Exception e) {
				logger.info(OperationUtillity.traceException(e));
			}

			logger.info("listOfRequestTypes.size-->" + listOfRequestTypes.size());
			logger.info("successCount check-->" + successCount);
			if (listOfRequestTypes.size() == successCount) {
				flag = true;
			}
		} catch (Exception e) {
			logger.info(OperationUtillity.traceException(e));
		}
		return flag;
	}

	public static Map<String, List<String>> facilityWiseAccountNumbersListOfMap(String pinstid) {

		Map<String, List<String>> facwiseAcNumListOfMap = new LinkedHashMap<>();
		List<String> facilitiesList = getAllFacilities(pinstid);
		logger.info(
				"RevisedServiceSequence.facilityWiseAccountNumbersListOfMap.facilitiesList check-->" + facilitiesList);
		for (String facility : facilitiesList) {
			List<String> accountNumbersList = new ArrayList<>();
			String query1 = "SELECT * FROM (SELECT LIMIT.QUESTION_ID, LIMIT.ANSWER, LIMIT.FACILITY_NAME FROM LSM_LIMIT_ANSWERS LIMIT WHERE LIMIT.PINSTID = ?  AND LIMIT.FACILITY_NAME = ? AND LIMIT.QUESTION_ID LIKE '%149%')";
			try (Connection con = DBConnect.getConnection(); PreparedStatement pst = con.prepareStatement(query1)) {
				pst.setString(1, pinstid);
				pst.setString(2, facility);
				try (ResultSet rs = pst.executeQuery()) {
					while (rs.next()) {
						accountNumbersList.add(OperationUtillity.NullReplace(rs.getString("ANSWER")));
					}
				}
			} catch (Exception e) {
				logger.info(OperationUtillity.traceException(e));
			}
			facwiseAcNumListOfMap.put(facility, accountNumbersList);
		}
		logger.info("RevisedServiceSequence.facilityWiseAccountNumbersListOfMap().facwiseAcNumListOfMap check-->"
				+ facwiseAcNumListOfMap);
		return facwiseAcNumListOfMap;
	}

	public static boolean getFlagForFeeRecovery(String pinstId) {
		List<String> listOfFeeRecoveryAccounts = new LinkedList<>();
		try (Connection con = DBConnect.getConnection()) {
			String query1 = "SELECT DISTINCT ANSWER AS ACCOUNT_NUMBER FROM LSM_MS_ANSWERS WHERE PINSTID = ? AND QUESTION=?";
			try (PreparedStatement stmt = con.prepareStatement(query1)) {
				stmt.setString(1, pinstId);
				stmt.setString(2, "Account No");
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						listOfFeeRecoveryAccounts.add(OperationUtillity.NullReplace(rs.getString("ACCOUNT_NUMBER")));
					}
				}
			}
			Map<String, List<String>> facilitiwiseAccountNumberListOfMap = facilityWiseAccountNumbersListOfMap(pinstId);//
			logger.info(
					"RevisedServiceSequence.getFlagOfAccountLinkageServices().facilitiwiseAccountNumberListOfMap check -->"
							+ facilitiwiseAccountNumberListOfMap);
			List<String> listOfRequestTypes = new LinkedList<>();
			for (Map.Entry<String, List<String>> entry : facilitiwiseAccountNumberListOfMap.entrySet()) {
				String facility = entry.getKey();
				List<String> accounts = entry.getValue(); // []
				if (accounts.size() > 0) {
					for (String acno : accounts) {
						if (listOfFeeRecoveryAccounts.contains(acno)) {
							String requestType = "ACCOUNT AND LIMIT NODE LINKAGE : " + facility + " Account No : "
									+ acno;
							listOfRequestTypes.add(requestType);
						}
					}
				}
			}

			int successCount = 0;
			StringBuilder reuestTypeCondition = new StringBuilder("AND REQUESTTYPE IN (");
			for (String requestType : listOfRequestTypes) {
				reuestTypeCondition.append("'" + requestType + "',");
			}
			reuestTypeCondition.replace(reuestTypeCondition.length() - 1, reuestTypeCondition.length(), ")");
			logger.info("RequestType Ccheck-->" + pinstId + ": " + reuestTypeCondition.toString());

			String query2 = "SELECT COUNT(STATUS) AS SUCCESSCOUNT FROM LSM_SERVICE_REQ_RES WHERE PINSTID = ?  and  STATUS = ? "
					+ reuestTypeCondition.toString();
			try (PreparedStatement stmt = con.prepareStatement(query2)) {
				stmt.setString(1, pinstId);
				stmt.setString(2, "SUCCESS");
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						successCount = rs.getInt("SUCCESSCOUNT");
					}
				}
			}

			logger.info("listOfRequestTypes.size-->" + pinstId + "::" + listOfRequestTypes.size());
			logger.info("successCount check-->" + pinstId + "::" + successCount);
			if (listOfFeeRecoveryAccounts.size() == successCount) {
				return true;
			}
		} catch (Exception e) {
			logger.info(OperationUtillity.traceException(e));
		}
		return false;
	}

	public Map<String, Map<String, String>> executeAllChildsAfterESM(String pinstId, HttpServletRequest req) {
		Map<String, String> childLimitNodeResponseMap = new LinkedHashMap<>();
		Map<String, Map<String, String>> fiServiceResMap = new HashMap<>();
		Map<String, String> svtResponseMap = new LinkedHashMap<>();
		List<Map<String, String>> childFacilityList = new LinkedList<>();
		Map<String, String> feeRecoveryResp = new LinkedHashMap<>();
		try {
			childFacilityList = createFacilityLimitList.createChildList(pinstId);
		} catch (SQLException e1) {
			logger.info("RevisedServiceSequence.executeAllChildServices().Exception while calling createChildList()-->"
					+ OperationUtillity.traceException(e1));
		}
		try {
			for (Map<String, String> individualFacilityDataMap : childFacilityList) {
				String limitAmount = individualFacilityDataMap.get("AMOUNT_LIMIT");

				if ("0".equals(limitAmount) || ("".equals(limitAmount)) && esmUtils.caseTypeIsESM(pinstId)) {
					continue; // if limit amount is 0 then child and subsequent services wont execute
				}
				String status = childLimitNodeModificationService
						.childLimitNodeModificationService(pinstId, individualFacilityDataMap).get("Status");
				logger.info("childLimitNodeModificationService-->" + childLimitNodeResponseMap);
				fiServiceResMap.put(
						"childLimitNodeModification" + " : "
								+ OperationUtillity.NullReplace(individualFacilityDataMap.get("FACILITY_NAME")),
						childLimitNodeResponseMap);
				if (status.equalsIgnoreCase("success")) {
					executeAccountLevelServices(pinstId, individualFacilityDataMap);
				}
			}
			if (getFlagOfLevelTwoServices(pinstId)) {
				try {
					svtResponseMap = svtService.SVTService(pinstId, "common");
					fiServiceResMap.put("SVT", svtResponseMap);
				} catch (Exception ex) {
					logger.info("RevisedServiceSequence. svtService.SVTService(pinstId, 'common');-->"
							+ OperationUtillity.traceException(pinstId, ex));
				}
//		try {
//		    fiServiceResMap.put("lodgeCollateral",
//			    lodgeCollateralCntlr.lodgeCollateralCtrl(pinstId, "ALL", "ALL", "ALL", "Limit_Setup"));
//		} catch (Exception ex) {
//		    logger.info("RevisedServiceSequence.executelodgeCollateral(ALL)-->"
//			    + OperationUtillity.traceException(pinstId, ex));
//		}
			}
			if (0 == OperationUtillity.getFeeServiceSuccessCount(pinstId)) {
				feeRecoveryResp = feeService.feeRecoveryService(pinstId, "common");
				fiServiceResMap.put("Fee Recovery", feeRecoveryResp);
			}
		} catch (SQLException | SOAPException e) {
			logger.info("RevisedServiceSequence.executeAllChildServices()-->" + OperationUtillity.traceException(e));
		}
		return fiServiceResMap;
	}
}
