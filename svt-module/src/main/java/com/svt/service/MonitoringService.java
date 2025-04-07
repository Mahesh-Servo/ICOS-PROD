//package com.svt.service;
//
//import java.io.IOException;
//import java.sql.SQLException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import javax.servlet.http.HttpServletRequest;
//import javax.xml.soap.SOAPException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import com.svt.model.commonModel.ServiceExecutionDetails;
//import com.svt.controllers.InquiryController;
//import com.svt.controllers.lodgeCollateralController;
//import com.svt.dao.CommonDaoForInqDlnkWtdrwl;
//import com.svt.model.commonModel.InnerPojo;
//import com.svt.model.commonModel.MainPojo;
//import com.svt.utils.common.CommonDataUtility;
//import com.svt.utils.common.OperationUtillity;
//import com.svt.utils.common.commonUtility;
//import com.svt.utils.common.updateServiceDetails;
//import com.svt.dao.SvtMonitoringDao;
//
//@Service
//public class MonitoringService {
//
//	private static final Logger logger = LoggerFactory.getLogger(MonitoringService.class);
//
//	@Autowired
//	CommonDaoForInqDlnkWtdrwl commonDaoForInqDlnkWtdrwl;
//
//	@Autowired
//	InquiryController inquiryController;
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
//	CommonService commonservice;
//
//	@Autowired
//	lodgeCollateralController lodgeCollateralController;
//
//	@Autowired
//	SvtMonitoringDao SvtMonitoringDao;
//
//	@SuppressWarnings("unchecked")
//	public Map<String, String> executeServices(String pinstid, String processName, String securityName,
//			String subTypeSecurity, String typeOfSvt, String product, String submitFlag, HttpServletRequest req,
//			String limitType, String dwSecType) throws IOException, SOAPException, Exception {
//		logger.info("MonitoringService.executeServices().pinstid(" + pinstid + ")");
//		Map<String, String> result = new HashMap<>();
//
//		logger.info("MonitoringService.executeServices().pinstid(" + pinstid + ") submitFlag [" + submitFlag + "]");
//		if (subTypeSecurity.equals("ALL")) {
//
//			if ("submit".equalsIgnoreCase(submitFlag)) {
//				logger.info("MonitoringService.executeServices().pinstid(" + pinstid
//						+ ") Before prepareExecutionForSVTMON");
//				List<ServiceExecutionDetails> listOfPojo = SvtMonitoringDao.prepareExecutionForSVTMON(pinstid,
//						processName, subTypeSecurity, limitType);
//				SvtMonitoringDao.insertMonFiExecutionData(pinstid, listOfPojo, limitType);
//			}
//
//			logger.info("MonitoringService.executeServices().pinstid(" + pinstid + ") inside  if renewal ");
//			result.putAll(commonservice.executeServices(pinstid, processName, securityName, subTypeSecurity, typeOfSvt,
//					product, dwSecType));
//
//		} else {
//
//			logger.info("MonitoringService.executeServices().pinstid(" + pinstid
//					+ ") inside  if renewal subTypeSecurity [" + subTypeSecurity + "]");
//			result.putAll(commonservice.executeServices(pinstid, processName, securityName, subTypeSecurity, typeOfSvt,
//					product, dwSecType));
//		}
//		logger.info("MonitoringService.executeServices().pinstid[" + pinstid + "] = " + result);
//		return result;
//	}
//
//	public void prepareStructureForFreshMon(String pinstid, String processName, String securityName,
//			String subTypeSecurity, String submitFlag, String limitType) throws SQLException {
//
//		logger.info("MonitoringService.executeServices().prepareStructureForFreshMon.pinstid[" + pinstid
//				+ "] = submitFlag" + submitFlag + "securityName [" + securityName + "]");
//
//		if (subTypeSecurity.equals("ALL")) {
//			if ("submit".equalsIgnoreCase(submitFlag)) {
//				logger.info("MonitoringService.executeServices().pinstid(" + pinstid
//						+ ") Before prepareExecutionForSVTMON");
//				List<ServiceExecutionDetails> listOfPojo = SvtMonitoringDao.prepareExecutionForSVTMON(pinstid,
//						processName, subTypeSecurity, limitType);
//				SvtMonitoringDao.insertMonFiExecutionData(pinstid, listOfPojo, limitType);
//			}
//		}
//	}
//
//}
