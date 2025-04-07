//package com.svt.controllers;
//
//import java.util.List;
//
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.svt.service.SvtUnlinkService;
//
//@RestController
//@RequestMapping("svt")
//public class SvtUnlinkController {
//
//	@Autowired
//	SvtUnlinkService svtUnlinkService;
//
//	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SvtUnlinkController.class);
//
//	@GetMapping("/unlink")
//	public ResponseEntity unlinkCollateralDetails(@RequestParam("pinstid") String pinstId,
//			@RequestParam("securityName") String securityName, @RequestParam("subTypeSecurity") String subTypeSecurity,
//			@RequestParam("typeOfSvt") String typeOfSvt, @RequestParam("product") String Product,
//			@RequestParam("limitPrefix") String limitPrefix, @RequestParam("limitSuffix") String limitSuffix,
//			@RequestParam("processName") String processName,@RequestParam("collateralID") String collateralId) {
//		
//		List<Object> resultFetched = null;
//		
//		try {
//			resultFetched = svtUnlinkService.unlinkCollateralDetailsImpl(pinstId, processName,collateralId);
//			logger.info("unlinkCollateralDetails after fetch = " + resultFetched);
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return new ResponseEntity<>(resultFetched, HttpStatus.OK);
//
//	}
//}
