//package com.svt.controllers;
//
//import java.util.Map;
//import java.util.WeakHashMap;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//
//import com.svt.dao.SvtMonitoringDao;
//import com.svt.service.lodgeCollateralService;
//import com.svt.utils.common.OperationUtillity;
//
//@RestController
//public class lodgeCollateralController {
//
//	private static final Logger logger = LoggerFactory.getLogger(lodgeCollateralController.class);
//
//	@Autowired
//	private lodgeCollateralService service;
//
//	@Autowired
//	SvtMonitoringDao SvtMonitoringDao;
//
//	@GetMapping("/lodgeCollateralController")
//	public Map<String, String> lodgeCollateralCtrl(@RequestParam("PINSTID") String pinstId,
//			@RequestParam("securityName") String securityName, @RequestParam("subTypeSecurity") String subTypeSecurity,
//			@RequestParam("typeOfSvt") String typeOfSvt, @RequestParam("product") String product,
//			@RequestParam("processName") String processName, HttpServletRequest req) {
//		Map<String, String> result = new WeakHashMap<>();
//		try {
//
////			String requrl[] = req.getRequestURL().toString().split("LsmFiServices");
//			
//			String limitType = SvtMonitoringDao.getLimitType(pinstId,processName); // ADDED BY MAHESHV ON 05032025 FOR RENEWAL
//
//			logger.info("LODGE COLLATERAL CONTROLLER CALLED FOR =" + req.getRequestURL().toString());
//
//			result.put("response", service.executeLodgeCollateralService(pinstId, securityName, subTypeSecurity,
//					typeOfSvt, product, processName,limitType).toString());
//
//			logger.info("lodgeCollateralController.lodgeCollateralCtrl().result pinstId : " + pinstId + "  " + result);
//
//		} catch (Exception e) {
//			logger.info("lodgeCollateralController.lodgeCollateralCtrl().Exception :"
//					+ OperationUtillity.traceException(pinstId,e));
//		}
//		return result;
//	}
//
//}
