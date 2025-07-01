package com.LsmFiServices.FiLsmController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.LsmFiServices.FiLsmService.SVTLinkageService;
import com.LsmFiServices.Utility.CaseRouting;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.SVTLinkageServiceUtility;

@Controller
public class SVTLinkageController {

    private static final Logger logger = LoggerFactory.getLogger(SVTLinkageController.class);

    @Autowired
    private SVTLinkageServiceUtility utility;

    @Autowired
    private SVTLinkageService service;

    @Autowired
    private CaseRouting route;

    @GetMapping("/svtLinkage")
    public @ResponseBody Map<String, String> runSVTLinkageService(@RequestParam(value = "PINSTID") String pinstId,
	    @RequestParam(value = "FACILITY") String security) throws InterruptedException, ExecutionException {
	Map<String, String> result = new LinkedHashMap<>();
	logger.info("You are hitting SVTLinkageService individually for " + security);
	try {
	    Map<String, String> inputMap = utility.getIndividualMapForSVTLinkage(pinstId, security);
	    result = service.svtLinkageService(pinstId, inputMap);
	} catch (Exception e) {
	    logger.info("SVTLinkageController.runSVTLinkageService(){}", OperationUtillity.traceException(e));
	} finally {
	    route.updateFIStatusInEXT(pinstId);
	}
	return result;
    }
}
