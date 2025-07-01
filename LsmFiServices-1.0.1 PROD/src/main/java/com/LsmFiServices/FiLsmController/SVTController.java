package com.LsmFiServices.FiLsmController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.LsmFiServices.FiLsmService.SVTFIService;
import com.LsmFiServices.Utility.CaseRouting;
import com.LsmFiServices.Utility.OperationUtillity;

@Controller
public class SVTController {
    private static final Logger logger = LoggerFactory.getLogger(SVTController.class);

    @Autowired
    private SVTFIService SVTService;

    @Autowired
    private CaseRouting route;

    @GetMapping( "/svt" )
    public @ResponseBody Map<String, String> runSVTService(@RequestParam(value = "PINSTID") String pinstId,
	    @RequestParam(value = "FACILITY") String security) throws InterruptedException, ExecutionException {
	Map<String, String> result = new HashMap<>();
	try {
	    logger.info("SVT is executing for -->" + security);
	    result = SVTService.SVTService(pinstId, security);
	} catch (Exception e) {
	    logger.info("SVTController.runSVTService(){}", OperationUtillity.traceException(e));
	} finally {
	    route.updateFIStatusInEXT(pinstId);
	}
	return result;
    }
}
