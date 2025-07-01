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
public class CollateralEnquiryController {

	private static final Logger logger = LoggerFactory.getLogger(CollateralEnquiryController.class);
	
	@Autowired
	private SVTCollateralEnquiryService service;

	@GetMapping("/collateralenquiry")
	public String callThirdPartyApi(@RequestParam("PINSTID") String pinstid) {

		String response = "";
		try {
//			response = service.executeCollateralEnquiryService(pinstid);
		} catch (Exception e) {
			logger.info("CollateralEnquiryController.callThirdPartyApi().Exception-->\n"+ OperationUtillity.traceException(e));
		}
		return response;
	}
}
