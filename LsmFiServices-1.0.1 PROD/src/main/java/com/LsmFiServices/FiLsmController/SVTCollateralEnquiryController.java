package com.LsmFiServices.FiLsmController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LsmFiServices.FiLsmService.SVTCollateralEnquiryService;
import com.LsmFiServices.Utility.OperationUtillity;

@RestController
public class SVTCollateralEnquiryController {

	private static final Logger logger = LoggerFactory.getLogger(SVTCollateralEnquiryController.class);

	@Autowired
	private SVTCollateralEnquiryService service;

	@GetMapping("/svtcollateralenquiry")
	public String callSVTCollateralEnquiryService(@RequestParam("PINSTID") String pinstId) {
		logger.info("Execution begins for SVTCollateralEnquiryController.callSVTCollateralEnquiryService()->"+pinstId);
		String result = "";
		try {
			result = service.executeCollateralEnquiryService(pinstId);
		} catch (Exception e) {
			logger.info("SVTCollateralCodeEnquiryController.callSVTCollateralEnquiryService().Exception-->"+ OperationUtillity.traceException(e));
		}
		return result;
	}
}
