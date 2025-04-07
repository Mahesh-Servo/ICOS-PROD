//package com.svt.controllers;
//
//import java.util.List;
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
//import com.svt.service.SvtWithdrawalService;
//
//@RestController
//@RequestMapping("svt")
//public class SvtWithdrawalController {
//
//	@Autowired
//	SvtWithdrawalService svtWithdrawalService;
//
//	private static final Logger logger = LoggerFactory.getLogger(SvtWithdrawalController.class);
//
//	@GetMapping("/withdrawal")
//	public ResponseEntity withdrawalDetails(@RequestParam("PINSTID") String pinstId,
//			@RequestParam("securityName") String securityName, @RequestParam("subTypeSecurity") String subTypeSecurity,
//			@RequestParam("typeOfSvt") String typeOfSvt, @RequestParam("product") String product,
//			@RequestParam("limitPrefix") String limitPrefix, @RequestParam("limitSuffix") String limitSuffix,
//			@RequestParam("processName") String processName,@RequestParam("collateralId") String collateralId) {
//
//		List<Object> executedData = null;
//
//		try {
//			System.out.println("SvtWithdrawalController.withdrawalDetails().pinstid" + pinstId);
//			logger.info("SvtWithdrawalController.withdrawalDetails().pinstid=" + pinstId);
//
//			executedData = svtWithdrawalService.svtWithdrawlImpl(pinstId, securityName, subTypeSecurity, typeOfSvt,
//					product, limitPrefix, limitSuffix, processName,collateralId);
//
//			logger.info("SvtWithdrawalController.withdrawalDetails().fetchedid= " + executedData);
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return new ResponseEntity<>(executedData, HttpStatus.OK);
//
//	}
//}
