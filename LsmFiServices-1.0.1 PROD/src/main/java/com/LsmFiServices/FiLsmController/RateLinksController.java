package com.LsmFiServices.FiLsmController;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.xml.soap.SOAPException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.LsmFiServices.Utility.CaseRouting;
import com.LsmFiServices.Utility.CreateFacilityLimitList;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.RevisedServiceSequence;

@RestController
public class RateLinksController {

    private static final Logger logger = LoggerFactory.getLogger(RateLinksController.class);

    @Autowired
    private RevisedServiceSequence newSequence;

    @Autowired
    private CreateFacilityLimitList createFacilityLimitList;

    @Autowired
    private CaseRouting route;

    @GetMapping("/rateOfInterest")
    public @ResponseBody Map<String, Map<String, String>> rateOfInterest(
	    @RequestParam(value = "PINSTID") String pinstId, @RequestParam(value = "FACILITY") String facility)
	    throws SOAPException, SQLException, InterruptedException, ExecutionException {

	Map<String, Map<String, String>> rateLinkageMap = new HashMap<>();
	try {
	    rateLinkageMap = newSequence.executeROIIndividually(pinstId,createFacilityLimitList.getfacilityWiseData(pinstId, facility));
	} catch (Exception e) {
	    logger.info(OperationUtillity.traceException(e));
	} finally {
	    route.updateFIStatusInEXT(pinstId);
	}
	return rateLinkageMap;
    }
}
