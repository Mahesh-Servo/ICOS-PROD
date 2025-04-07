//package com.svt.controllers;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.svt.dao.CommonDaoForInqDlnkWtdrwl;
//import com.svt.dao.SvtMonitoringDao;
//import com.svt.service.CommonService;
//import com.svt.utils.common.OperationUtillity;
//
//@RestController
//@RequestMapping("svt")
//public class CommonController {
//
//	@Autowired
//	CommonService commonservice;
//
//	@Autowired
//	CommonDaoForInqDlnkWtdrwl commonDao;
//
//	@Autowired
//	lodgeCollateralController lodgeCollateralController;
//	
//	@Autowired
//	SvtMonitoringDao svtMonDao;
//
//	private static final Logger logger = LoggerFactory.getLogger(CommonController.class);
//
//	@GetMapping("common")
//	public ResponseEntity commonController(@RequestParam("PINSTID") String pinstid,
//			@RequestParam("processName") String processName, @RequestParam("securityName") String securityName,
//			@RequestParam("subTypeSecurity") String subTypeSecurity, @RequestParam("typeOfSvt") String typeOfSvt,
//			@RequestParam("product") String product, @RequestParam("dwSecType") String dwSecType,HttpServletRequest req) {
//
//		logger.info("\n[Start]CommonController.commonController().pinstid(" + pinstid + ")");
//
//		Map<String, String> result = new HashMap<>();
//
//		try {
//			
//			String limitType = svtMonDao.getLimitType(pinstid,processName); // ADDED BY MAHESHV ON 05032025 FOR RENEWAL
//			
//			if (!("First Limit setup".toUpperCase().equalsIgnoreCase(limitType))
//					&& !("First Limit set up".toUpperCase().equalsIgnoreCase(limitType))) { // MODIFICATION FOR RENEWAL 05032025
//				
//				result.putAll(commonservice.executeServices(pinstid, processName, securityName, subTypeSecurity,
//						typeOfSvt, product,dwSecType));
//				logger.info("\nCommonController.commonController().result().pinstid(" + pinstid + ").isRenewal(true) =="
//						+ result);
//			} else {
//				
//				result.putAll(lodgeCollateralController.lodgeCollateralCtrl(pinstid, securityName, subTypeSecurity,
//						typeOfSvt, product, processName, req));
//				logger.info("\nCommonController.commonController().result().pinstid(" + pinstid
//						+ ").isRenewal(false) ==" + result);
//				
//			}
//
//		} catch (Exception e) {
//			OperationUtillity.traceException(pinstid, e);
//		}
//
//		logger.info("\n[END]CommonController.commonController().pinstid(" + pinstid + ").result= " + result + "\n");
//		return new ResponseEntity<>(result, HttpStatus.OK);
//
//	}
//
//}
