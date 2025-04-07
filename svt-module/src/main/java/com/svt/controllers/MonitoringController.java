//package com.svt.controllers;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import javax.servlet.http.HttpServletRequest;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import com.svt.utils.common.CaseRouting;
//import com.svt.dao.CommonDaoForInqDlnkWtdrwl;
//import com.svt.dao.SvtMonitoringDao;
//import com.svt.model.commonModel.ServiceExecutionDetails;
//import com.svt.service.CommonService;
//import com.svt.service.MonitoringService;
//import com.svt.utils.common.OperationUtillity;
//
//@RestController
//@RequestMapping("svt")
//public class MonitoringController {
//
//	@Autowired
//	MonitoringService monitoringService;
//
//	@Autowired
//	CommonDaoForInqDlnkWtdrwl commonDao;
//
//	@Autowired
//	private CaseRouting route;
//
//	@Autowired
//	lodgeCollateralController lodgeCollateralController;
//
//	@Autowired
//	SvtMonitoringDao SvtMonitoringDao;
//
//	private static final Logger logger = LoggerFactory.getLogger(MonitoringController.class);
//
//	@GetMapping("monitoring")
//	public ResponseEntity monitoringController(@RequestParam("PINSTID") String pinstid,
//			@RequestParam("processName") String processName, @RequestParam("securityName") String securityName,
//			@RequestParam("subTypeSecurity") String subTypeSecurity, @RequestParam("typeOfSvt") String typeOfSvt,
//			@RequestParam("product") String product, @RequestParam("submitFlag") String submitFlag,
//			@RequestParam("dwSecType") String dwSecType, HttpServletRequest req) {
//
//		logger.info("\n[Start] MonitoringController.monitoringController().pinstid(" + pinstid + ") processName ["
//				+ processName + "] " + "securityName [" + securityName + "] subTypeSecurity [" + subTypeSecurity + "]");
//
//		String status = "";
//		Map<String, String> result = new HashMap<>();
//
//		try {
//
//			String limitType = SvtMonitoringDao.getLimitType(pinstid,processName); // ADDED BY MAHESHV ON 05032025 FOR RENEWAL
//
//			if (!("First Limit setup".toUpperCase().equalsIgnoreCase(limitType))
//					&& !("First Limit set up".toUpperCase().equalsIgnoreCase(limitType))) { // MODIFICATION FOR RENEWAL
//																							// 05032025
//
//				result.putAll(monitoringService.executeServices(pinstid, processName, securityName, subTypeSecurity,
//						typeOfSvt, product, submitFlag, req, limitType, dwSecType));
//				logger.info("MonitoringController.monitoringController().result().pinstid(" + pinstid
//						+ ").isRenewal(true) =" + result);
//			} else {
//				logger.info("MonitoringController.monitoringController().result().pinstid(" + pinstid
//						+ " before prepareStructureForFreshMon)");
//				monitoringService.prepareStructureForFreshMon(pinstid, processName, securityName, subTypeSecurity,
//						submitFlag, limitType);
//				result.putAll(lodgeCollateralController.lodgeCollateralCtrl(pinstid, securityName, subTypeSecurity,
//						typeOfSvt, product, processName, req));
//				logger.info("MonitoringController.monitoringController().result().pinstid(" + pinstid
//						+ ").isRenewal(false) =" + result);
//			}
//
//		} catch (Exception e) {
//			OperationUtillity.traceException(pinstid, e);
//		} finally {
//			status = route.updateFIStatusInEXT(pinstid);
//			logger.info("status check in finally of main controller " + status);
//			logger.info("RunFiService.runFiServicesAsync() :: Rounting done for PINSTID :: " + pinstid);
//		}
//
//		logger.info(
//				"\n[END]MonitoringController.monitoringController().pinstid(" + pinstid + ").result= " + result + "\n");
//		return new ResponseEntity<>(result, HttpStatus.OK);
//
//	}
//
//}
