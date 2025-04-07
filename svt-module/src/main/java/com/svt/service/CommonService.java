//package com.svt.service;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import javax.xml.soap.SOAPException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import com.svt.dao.CommonDaoForInqDlnkWtdrwl;
//import com.svt.dao.InquiryDao;
//import com.svt.dao.SvtMonitoringDao;
//import com.svt.model.commonModel.InnerPojo;
//import com.svt.model.commonModel.MainPojo;
//import com.svt.utils.common.CommonDataUtility;
//import com.svt.utils.common.OperationUtillity;
//import com.svt.utils.common.commonUtility;
//import com.svt.utils.common.updateServiceDetails;
//
//@Service
//public class CommonService {
//
//	private static final Logger logger = LoggerFactory.getLogger(CommonService.class);
//
//	@Autowired
//	CommonDaoForInqDlnkWtdrwl commonDaoForInqDlnkWtdrwl;
//
//	@Autowired
//	InquiryDao inquiryDao;
//
//	@Autowired
//	SvtUnlinkService svtUnlinkService;
//
//	@Autowired
//	SvtWithdrawalService svtWithdrawalService;
//
//	@Autowired
//	lodgeCollateralService lodgeCollateralSrvc;
//
//	@Autowired
//	LodgeCollateralLinkageService lodgeCollaterallinkageSrvc;
//
//	@Autowired
//	CommonDaoForInqDlnkWtdrwl commonDao;
//
//	@Autowired
//	updateServiceDetails updtServiceDetails;
//
//	@Autowired
//	SvtMonitoringDao svtMonDao;
//
//	@SuppressWarnings("unchecked")
//	public Map<String, String> executeServices(String pinstid, String processName, String securityName,
//			String subTypeSecurity, String typeOfSvt, String product, String dwSecType)
//			throws IOException, SOAPException, Exception {
//
//		logger.info(
//				"[START]CommonService.executeServices().pinstid [" + pinstid + "] securityName [" + securityName + "]");
//
//		Map<String, String> result = new HashMap<>();
//
//		String limitType = svtMonDao.getLimitType(pinstid,processName); // ADDED BY MAHESHV ON 27122024 FOR RENEWAL
//
//		if ("ALL".equals(subTypeSecurity)) {
//
//			ArrayList<MainPojo> mainPojoList = commonDaoForInqDlnkWtdrwl.fetchCommonData(pinstid, processName);
//
//			Map<String, String[]> limitData = new HashMap<String, String[]>();
//
//			if (!("First Limit setup".toUpperCase().equalsIgnoreCase(limitType))
//					&& !("First Limit set up".toUpperCase().equalsIgnoreCase(limitType))) { // MODIFICATION FOR RENEWAL
//																							// CASE
//				logger.info(" if case is renewal--" + limitType);
//
//				limitData = SvtMonitoringDao.fetchLimitPrefixSuffix(pinstid, processName);
//
//				for (Map.Entry<String, String[]> entry : limitData.entrySet()) {
//					String facilityname = entry.getKey();
//					String[] pref_suff = entry.getValue();
//					List<Map<String, String>> listOfCC = inquiryDao.checkInquiryDetailsInMasterNew(pinstid, processName,
//							pref_suff,facilityname);  //check in master 
//
//					logger.info("\n[104]CommonService.executeServices().pinstid(" + pinstid + ").listOfCC = "
//							+ listOfCC.toString());
//
//					for (Map<String, String> map : listOfCC) {
//
//						Map<String, String> resultMap = new HashMap<>();
//
//						if (map.containsValue("FAILED")) {     //it will exit the current loop for fail and execute next loop
//							continue;
//						}
//
//						if (map.containsValue("SUCCESS")) {
//
//							resultMap.putAll(svtUnlinkService.executeSvtUnlinkSrvcImpl(pinstid, processName,
//									map.get("COLLATERAL_ID"), facilityname, pref_suff));
//
//							logger.info("CommonService.executeServices().pinstid(" + pinstid
//									+ ").svtDelinkServiceResult = " + resultMap.toString());
//
//							if (resultMap.get("SVT DELINK : " + facilityname + " : " + map.get("COLLATERAL_ID"))
//									.equals("SUCCESS")) {
//								// executing withdrawal
//								resultMap.putAll(
//										svtWithdrawalService.withDrawalCollateralId(pinstid, processName, facilityname,
//												map.get("COLLATERAL_ID"), map.get("SEC_TYPE"), map.get("SEC_CODE")));
//
//								logger.info("CommonService.executeServices().pinstid(" + pinstid
//										+ ").svtWithdrawalServiceResult = " + resultMap.toString());
//							} else {
//								resultMap.put("SVT DELINK :" + facilityname + " : " + map.get("COLLATERAL_ID"),
//										"SVT DELINK FAILED");
//							}
//						} else {
//							resultMap.put("SVT INQUIRY", "There's no Data found in Inquiry Master");
//						}
//					}
//				}
//			} // limitData
//
//			for (MainPojo mainPojo : mainPojoList) {
//
//				for (InnerPojo innerPojo : mainPojo.getInnerPojo()) {
//
//					try {
//						Map<String, String> resultMap = new HashMap<>();
//
//						String requestUuid = commonUtility.createRequestUUID();
//						String dateAndTime = commonUtility.dateFormat();
//
//						mainPojo.setSubTypeSecurity(innerPojo.getSubTypeSecurity());
//						mainPojo.setTypeOfSecurity(innerPojo.getTypeOfSecurity());
//						mainPojo.setProduct(innerPojo.getProduct());
//						mainPojo.setLimitPrefix(innerPojo.getLimitPrefix());
//						mainPojo.setLimitSuffix(innerPojo.getLimitSuffix());
//						mainPojo.setCollateralCode(CommonDataUtility.getCollateralCode(innerPojo.getTypeOfSecurity()));
//						mainPojo.setCollateralId(CommonDataUtility.getCollateralCode(innerPojo.getTypeOfSecurity()));
//						mainPojo.setPolicy_No(innerPojo.getPolicyNumber());
//						mainPojo.setPolicy_Amt(innerPojo.getPolicyAmount());
//						mainPojo.setRequestId(requestUuid);
//						mainPojo.setDateAndTime(dateAndTime);
//						mainPojo.setColtrlLinkage("Yes");
//						mainPojo.setTypeOfCharge(innerPojo.getTypeOfCharge());
//						mainPojo.setValSubTypeSecInMn(innerPojo.getValSubTypeSecMn());
//						mainPojo.setSecurityValueInMn(innerPojo.getSecurityValueMn());
//						mainPojo.setNameofHoldingStock(innerPojo.getNameofHoldingStock());
//						mainPojo.setUnitValue(innerPojo.getUnitValue());
//						mainPojo.setNoOfUnits(innerPojo.getNoOfUnits());
//						commonDao.getSecurityOtherDetails(pinstid, mainPojo);
//
//						logger.info("\n[100]CommonService.executeServices().pinstid(" + pinstid
//								+ ").mainPojo[InnerPojo] = " + mainPojo.toString());
//
//						String rqstType = OperationUtillity.NullReplace(mainPojo.getSecurityName()) + " : "
//								+ OperationUtillity.NullReplace(mainPojo.getSubTypeSecurity()) + " : "
//								+ OperationUtillity.NullReplace(mainPojo.getTypeOfSecurity()) + " : "
//								+ OperationUtillity.NullReplace(mainPojo.getProduct());
//
//						if (SvtMonitoringDao.checkIsDW(pinstid)) {
//							resultMap.putAll(lodgeCollateralSrvc.LodgeSubTypeSecCollateral(pinstid, mainPojo,
//									processName, limitType));
//
//							logger.info("CommonService.executeServices().pinstid(" + pinstid
//									+ ").lodgeCollateralSrvcResult = " + resultMap.toString());
//
//							if (OperationUtillity.NullReplace(resultMap.get("LODGE COLLATERAL : " + rqstType))
//									.equals("SUCCESS")
//									|| OperationUtillity
//											.NullReplace(resultMap.get("LODGE COLLATERAL : " + rqstType + " : "
//													+ OperationUtillity.NullReplace(mainPojo.getPolicy_No())))
//											.equals("SUCCESS")) {
//								resultMap.putAll(lodgeCollaterallinkageSrvc.LodgeSubTypeSecCollateralLinkage(pinstid,
//										mainPojo, innerPojo, processName));
//
//								logger.info("CommonService.executeServices().pinstid(" + pinstid
//										+ ").lodgeCollaterallinkageSrvc = " + resultMap.toString());
//							} else {
//								resultMap.put(
//										"LODGE COLLATERAL : " + rqstType + " : "
//												+ OperationUtillity.NullReplace(mainPojo.getPolicy_No()),
//										"Not Executed");
//							}
//						}
//						result.putAll(resultMap);
//					} catch (Exception e) {
//						OperationUtillity.traceException(pinstid, e);
//					}
//				}
//			}
//
//		} else { // individual
//
//			logger.info(" if case is renewal--" + limitType + " dwSecType [" + dwSecType + "]");
//
//			String facility = "", sec_type = "", coll_id = "";
//			Map<String, String> resultMapdw = new HashMap<>();
//
//			if (!("First Limit setup".toUpperCase().equalsIgnoreCase(limitType))
//					&& !("First Limit set up".toUpperCase().equalsIgnoreCase(limitType))) { // MODIFICATION FOR RENEWAL
//
//				String[] dwarr = null, prefix_suff = null;
//
//				if (dwSecType != null && dwSecType.contains(":")) {
//					if (dwSecType.contains("SVT DELINK")) {
//						dwarr = dwSecType.split(" : ");
//						facility = dwarr[1];
//						coll_id = dwarr[2];
//					} else {
//						dwarr = dwSecType.split(" : ");
//						facility = dwarr[1];
//						sec_type = dwarr[2];
//						coll_id = dwarr[3];
//					}
//				}
//
//				Map<String, String[]> limitData = new HashMap<String, String[]>();
//
//				limitData = SvtMonitoringDao.fetchLimitPrefixSuffixInd(pinstid, processName, facility);
//
//				for (Map.Entry<String, String[]> entry : limitData.entrySet()) {
//					String facilityname = entry.getKey();
//					prefix_suff = entry.getValue();
//
//					List<Map<String, String>> listOfCC = inquiryDao.checkInquiryDetailsInMasterNew(pinstid, processName,
//							prefix_suff,facilityname);
//
//					logger.info("\n[104]CommonService.executeServices().pinstid(" + pinstid + ").listOfCC = "
//							+ listOfCC.toString());
//
//					for (Map<String, String> map : listOfCC) {
//						if (coll_id.equalsIgnoreCase(map.get("COLLATERAL_ID"))) {
//							sec_type = map.get("SEC_TYPE");
//							logger.info(" if case is renewal--" + limitType + " facility [" + facility + "] sec_type ["
//									+ sec_type + "] coll_id [" + coll_id);
//
//							resultMapdw.putAll(svtUnlinkService.executeSvtUnlinkSrvcImpl(pinstid, processName, coll_id,
//									facility, prefix_suff));
//
//							logger.info("CommonService.executeServices().pinstid(" + pinstid
//									+ ").svtDelinkServiceResult = " + resultMapdw.toString());
//
//							if (resultMapdw.get("SVT DELINK : " + facility + " : " + coll_id).equals("SUCCESS")) {
//								resultMapdw.putAll(svtWithdrawalService.withDrawalCollateralId(pinstid, processName,
//										facility, coll_id, sec_type, map.get("SEC_CODE")));
//
//								logger.info("CommonService.executeServices().pinstid(" + pinstid
//										+ ").svtWithdrawalServiceResult = " + resultMapdw.toString());
//							} else {
//								resultMapdw.put("SVT DELINK :" + facility + " : " + coll_id, "SVT DELINK FAILED");
//							}
//						}
//					}
//				} // limitdata
//			}
//
//			if (SvtMonitoringDao.checkIsDW(pinstid)) {
//				MainPojo mainPojo = null;
//				if (dwSecType.contains("SVT DELINK") || dwSecType.contains("SVT WITHDRAWAL")) {
//					
//					ArrayList<MainPojo> mainPojoList = commonDaoForInqDlnkWtdrwl.fetchCommonData(pinstid, processName);
//					
//					for (MainPojo mainPojo1 : mainPojoList) {
//
//						for (InnerPojo innerPojo : mainPojo1.getInnerPojo()) {
//
//							try {
//								Map<String, String> resultMap = new HashMap<>();
//
//								String requestUuid = commonUtility.createRequestUUID();
//								String dateAndTime = commonUtility.dateFormat();
//
//								mainPojo1.setSubTypeSecurity(innerPojo.getSubTypeSecurity());
//								mainPojo1.setTypeOfSecurity(innerPojo.getTypeOfSecurity());
//								mainPojo1.setProduct(innerPojo.getProduct());
//								mainPojo1.setLimitPrefix(innerPojo.getLimitPrefix());
//								mainPojo1.setLimitSuffix(innerPojo.getLimitSuffix());
//								mainPojo1.setCollateralCode(CommonDataUtility.getCollateralCode(innerPojo.getTypeOfSecurity()));
//								mainPojo1.setCollateralId(CommonDataUtility.getCollateralCode(innerPojo.getTypeOfSecurity()));
//								mainPojo1.setPolicy_No(innerPojo.getPolicyNumber());
//								mainPojo1.setPolicy_Amt(innerPojo.getPolicyAmount());
//								mainPojo1.setRequestId(requestUuid);
//								mainPojo1.setDateAndTime(dateAndTime);
//								mainPojo1.setColtrlLinkage("Yes");
//								mainPojo1.setTypeOfCharge(innerPojo.getTypeOfCharge());
//								mainPojo1.setValSubTypeSecInMn(innerPojo.getValSubTypeSecMn());
//								mainPojo1.setSecurityValueInMn(innerPojo.getSecurityValueMn());
//								mainPojo1.setNameofHoldingStock(innerPojo.getNameofHoldingStock());
//								mainPojo1.setUnitValue(innerPojo.getUnitValue());
//								mainPojo1.setNoOfUnits(innerPojo.getNoOfUnits());
//								commonDao.getSecurityOtherDetails(pinstid, mainPojo1);
//
//								logger.info("\n[100]CommonService.executeServices().pinstid(" + pinstid
//										+ ").mainPojo[InnerPojo] = " + mainPojo1.toString());
//
//								String rqstType = OperationUtillity.NullReplace(mainPojo1.getSecurityName()) + " : "
//										+ OperationUtillity.NullReplace(mainPojo1.getSubTypeSecurity()) + " : "
//										+ OperationUtillity.NullReplace(mainPojo1.getTypeOfSecurity()) + " : "
//										+ OperationUtillity.NullReplace(mainPojo1.getProduct());
//
//								if (SvtMonitoringDao.checkIsDW(pinstid)) {
//									resultMap.putAll(lodgeCollateralSrvc.LodgeSubTypeSecCollateral(pinstid, mainPojo1,
//											processName, limitType));
//
//									logger.info("CommonService.executeServices().pinstid(" + pinstid
//											+ ").lodgeCollateralSrvcResult = " + resultMap.toString());
//
//									if (OperationUtillity.NullReplace(resultMap.get("LODGE COLLATERAL : " + rqstType))
//											.equals("SUCCESS")
//											|| OperationUtillity
//													.NullReplace(resultMap.get("LODGE COLLATERAL : " + rqstType + " : "
//															+ OperationUtillity.NullReplace(mainPojo1.getPolicy_No())))
//													.equals("SUCCESS")) {
//										resultMap.putAll(lodgeCollaterallinkageSrvc.LodgeSubTypeSecCollateralLinkage(pinstid,
//												mainPojo1, innerPojo, processName));
//
//										logger.info("CommonService.executeServices().pinstid(" + pinstid
//												+ ").lodgeCollaterallinkageSrvc = " + resultMap.toString());
//									} else {
//										resultMap.put(
//												"LODGE COLLATERAL : " + rqstType + " : "
//														+ OperationUtillity.NullReplace(mainPojo1.getPolicy_No()),
//												"Not Executed");
//									}
//								}
//								result.putAll(resultMap);
//							} catch (Exception e) {
//								OperationUtillity.traceException(pinstid, e);
//							}
//						}
//					}
//
//					
//				} else  {
//					mainPojo = commonDaoForInqDlnkWtdrwl.fetchIndividualProductDtls(pinstid, securityName,
//						subTypeSecurity, typeOfSvt, product, processName);
//
//				for (InnerPojo innerPojo : mainPojo.getInnerPojo()) {
//					try {
//						Map<String, String> resultMap = new HashMap<>();
//
//						String requestUuid = commonUtility.createRequestUUID();
//						String dateAndTime = commonUtility.dateFormat();
//
//						mainPojo.setSubTypeSecurity(innerPojo.getSubTypeSecurity());
//						mainPojo.setTypeOfSecurity(innerPojo.getTypeOfSecurity());
//						mainPojo.setProduct(innerPojo.getProduct());
//						mainPojo.setLimitPrefix(innerPojo.getLimitPrefix());
//						mainPojo.setLimitSuffix(innerPojo.getLimitSuffix());
//						mainPojo.setCollateralCode(CommonDataUtility.getCollateralCode(innerPojo.getTypeOfSecurity()));
//						mainPojo.setPolicy_No(innerPojo.getPolicyNumber());
//						mainPojo.setPolicy_Amt(innerPojo.getPolicyAmount());
//						mainPojo.setCollateralId("");
//						mainPojo.setRequestId(requestUuid);
//						mainPojo.setDateAndTime(dateAndTime);
//						mainPojo.setTypeOfCharge(innerPojo.getTypeOfCharge());
//						mainPojo.setValSubTypeSecInMn(innerPojo.getValSubTypeSecMn());
//						mainPojo.setSecurityValueInMn(innerPojo.getSecurityValueMn());
//						mainPojo.setNameofHoldingStock(innerPojo.getNameofHoldingStock());
//						mainPojo.setUnitValue(innerPojo.getUnitValue());
//						mainPojo.setNoOfUnits(innerPojo.getNoOfUnits());
//						commonDaoForInqDlnkWtdrwl.getSecurityOtherDetails(pinstid, mainPojo);
//
//						String rqstType = OperationUtillity.NullReplace(mainPojo.getSecurityName()) + " : "
//								+ OperationUtillity.NullReplace(mainPojo.getSubTypeSecurity()) + " : "
//								+ OperationUtillity.NullReplace(mainPojo.getTypeOfSecurity()) + " : "
//								+ OperationUtillity.NullReplace(mainPojo.getProduct());
//
//						resultMap.putAll(lodgeCollateralSrvc.LodgeSubTypeSecCollateral(pinstid, mainPojo, processName,
//								limitType));
//
//						logger.info("CommonService.executeServices().pinstid(" + pinstid
//								+ ").lodgeCollateralSrvcResult = " + resultMap.toString());
//
//						if (OperationUtillity.NullReplace(resultMap.get("LODGE COLLATERAL : " + rqstType))
//								.equals("SUCCESS")
//								|| OperationUtillity
//										.NullReplace(resultMap.get("LODGE COLLATERAL : " + rqstType + " : "
//												+ OperationUtillity.NullReplace(mainPojo.getPolicy_No())))
//										.equals("SUCCESS")) {
//							resultMap.putAll(lodgeCollaterallinkageSrvc.LodgeSubTypeSecCollateralLinkage(pinstid,
//									mainPojo, innerPojo, processName));
//
//							logger.info("CommonService.executeServices().pinstid(" + pinstid
//									+ ").lodgeCollaterallinkageSrvc = " + resultMap.toString());
//						} else {
//							resultMap.put("LODGE COLLATERAL : " + rqstType + " : "
//									+ OperationUtillity.NullReplace(mainPojo.getPolicy_No()), "Not Executed");
//						}
//
//						result.putAll(resultMap);
//					} catch (Exception e) {
//						OperationUtillity.traceException(pinstid, e);
//					}
//				}
//			  }
//			} // DW all success chk
//		}
//		logger.info("CommonService.executeServices().pinstid[" + pinstid + "] = " + result);
//		return result;
//	}
//}
