package com.LsmFiServices.FiLsmController;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LsmFiServices.FiLsmService.LEI_URCC_NumberEnquiryService;
import com.LsmFiServices.Utility.CaseRouting;
import com.LsmFiServices.Utility.LEI_URCCNumberEnquiryUtility;
import com.LsmFiServices.Utility.OperationUtillity;

@RestController
public class LEI_URCC_NumberEnquiryController {

    private static final Logger logger = LoggerFactory.getLogger(LEI_URCC_NumberEnquiryController.class);

    @Autowired
    private LEI_URCC_NumberEnquiryService service;
    @Autowired
    private LEI_URCCNumberEnquiryUtility utility;

    @Autowired
    private CaseRouting route;

    @GetMapping( "/leinumberenquiry" )
    public Map<String, Object> runLEI_URCCNumberEnquiry(@RequestParam(value = "PINSTID") String pinstId,
	    @RequestParam(value = "FACILITY") String input) {

	Map<String, Object> result = new LinkedHashMap<>();
	try {
	    if (input.equalsIgnoreCase("common")) {
		result = service.executeLEIURCCEnquiryService(pinstId, input);
	    } else {
		result = service.executeLEIURCCEnquiryService(pinstId, utility.getCustIdForLEIURCCEnquiry(input));
	    }
	} catch (Exception e) {
	    logger.error("LEINumberEnquiryController.runLEI_URCCNumberEnquiry() {}",
		    OperationUtillity.traceException(e));
	} finally {
	    route.updateFIStatusInEXT(pinstId);
	}
	return result;
    }
}
