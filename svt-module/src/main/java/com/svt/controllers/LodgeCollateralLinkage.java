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
//
//import com.svt.service.LodgeCollateralLinkageService;
//import com.svt.service.lodgeCollateralService;
//import com.svt.utils.common.OperationUtillity;
//
//@RestController
//public class LodgeCollateralLinkage {
//
//	private static final Logger logger = LoggerFactory.getLogger(LodgeCollateralLinkage.class);
//
//	@Autowired
//	private LodgeCollateralLinkageService service;
//
//	@GetMapping("/lodgeCollateralLinkage")
//	public Map<String, String> lodgeCollateralLinkageCtrl(@RequestParam("PINSTID") String pinstId,
//			@RequestParam("securityName") String securityName, @RequestParam("subTypeSecurity") String subTypeSecurity,
//			@RequestParam("typeOfSvt") String typeOfSvt, @RequestParam("product") String product,
//			@RequestParam("processName") String processName, HttpServletRequest req) {
//		Map<String, String> result = new WeakHashMap<>();
//		try {
//
//			logger.info("LODGE COLLATERAL LINKAGE CONTROLLER CALLED FOR =" + req.getRequestURL().toString());
//
//			result.put("response", service.executeLodgeCollateralLinkageService(pinstId, securityName, subTypeSecurity,
//					typeOfSvt, product, processName).toString());
//
//			logger.info("LodgeCollateralLinkage.lodgeCollateralLinkageCtrl().result pinstId : " + pinstId + "  " + result);
//
//		} catch (Exception e) {
//			logger.info("LodgeCollateralLinkage.lodgeCollateralLinkageCtrl().Exception :"
//					+ OperationUtillity.traceException(pinstId,e));
//		}
//		return result;
//	}
//
//}
