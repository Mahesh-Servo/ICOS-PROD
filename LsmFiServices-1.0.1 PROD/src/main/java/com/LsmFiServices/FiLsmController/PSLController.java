package com.LsmFiServices.FiLsmController;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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
public class PSLController {

    @Autowired
    private RevisedServiceSequence newSeq;

    @Autowired
    private CreateFacilityLimitList createFacLmList;

    @Autowired
    private CaseRouting route;
    private static final Logger logger = LoggerFactory.getLogger(PSLController.class);

    @GetMapping( "/PSL")
    public @ResponseBody Map<String, Map<String, String>> executePSLServiceIndividually(
	    @RequestParam(value = "PINSTID") String pinstId, @RequestParam(value = "FACILITY") String facility)
	    throws SQLException, InterruptedException, ExecutionException {

	Map<String, Map<String, String>> pslResponseMap = new LinkedHashMap<>();
	try {
	    pslResponseMap = newSeq.executePSLIndividually(pinstId,createFacLmList.getfacilityWiseData(pinstId, facility));
	} catch (Exception e) {
	    logger.info(OperationUtillity.traceException(e));
	} finally {
	    route.updateFIStatusInEXT(pinstId);
	}
	return pslResponseMap;
    }
}
