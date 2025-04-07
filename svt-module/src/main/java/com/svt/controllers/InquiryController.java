//package com.svt.controllers;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.svt.service.InquiryService;
//
//@RestController
//@RequestMapping("svt")
//public class InquiryController {
//
//	@Autowired
//	InquiryService inquiryService;
//
//	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(InquiryController.class);
//
//	@GetMapping("/inquiry")
//	public List<Map<String, String>> svtInquiry(@RequestParam("PINSTID") String pinstId,
//			@RequestParam("securityName") String securityName, @RequestParam("subTypeSecurity") String subTypeSecurity,
//			@RequestParam("typeOfSvt") String typeOfSvt, @RequestParam("product") String product,
//			@RequestParam("limitPrefix") String limitPrefix, @RequestParam("limitSuffix") String limitSuffix,
//			@RequestParam("processName") String processName) { // , HttpServletRequest request
//		List<Map<String, String>> fetchedDetails = new ArrayList<>();
//		try {
//
//			logger.info("\nInquiryController.svtInquiry(" + pinstId + ").BeforeFetch = " + pinstId);
//
//			fetchedDetails = inquiryService.inquiryServiceImpl(pinstId, securityName, subTypeSecurity, typeOfSvt,
//					product, limitPrefix, limitSuffix, processName);
//
//			logger.info("\nInquiryController.svtInquiry(" + pinstId + ").afterFetch = " + fetchedDetails);
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return fetchedDetails;
//	}
//
//}
