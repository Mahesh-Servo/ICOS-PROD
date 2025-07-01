package com.LsmFiServices.FiLsmController;

import java.sql.SQLException;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LsmFiServices.Utility.ESMUtils;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.ServiceDetails;
import com.LsmFiServices.Utility.fiServiceRunCount;

@RestController
public class RunFiService {

	private static final Logger logger = LoggerFactory.getLogger(RunFiService.class);

	@Autowired
	private ParentLimitNodeCreationController parentLimitNodeController;

	@Autowired
	private ServiceDetails serviceDetails;

	@Autowired
	private ESMUtils esmUtils;

	@GetMapping("/runFiService")
	public String runFiServices(@RequestParam(value = "PINSTID") String pinstId, HttpServletRequest req)
			throws SQLException {
		Pair<Boolean, String> decisionPair = serviceDetails.saveCountInExt(pinstId);
		boolean isAccountNumSaved = decisionPair.getLeft();
		String messageToFrontEnd = decisionPair.getRight();
		logger.info("RunFiService.runFiServices().saved-->" + isAccountNumSaved);

		if (!esmUtils.caseTypeIsESM(pinstId)) {
			if (!isAccountNumSaved) {
				return messageToFrontEnd;
			}
		}
		int runCount = 0;
		runCount = fiServiceRunCount.checkRunCount(pinstId);
		logger.info("RunFiService.runFiServices().runCount-->" + runCount);
		if (runCount <= 0) {
//			String insertSchedulerPinstId = OperationUtillity.insertSchedulerPinstId(pinstId);
//			logger.info("RunFiService.runFiServices().insertSchedulerPinstId --> " + insertSchedulerPinstId);
			messageToFrontEnd = runFiServicesAsync(pinstId, req);
		} else {
			messageToFrontEnd = "Services already executed....!";
		}
		return messageToFrontEnd;
	}

	public String runFiServicesAsync(String pinstId, HttpServletRequest req) {
		String status = "";
		logger.info("Fi Services Execution Started for pinstId :: " + pinstId + " at @ " + LocalDateTime.now());
		try {
			status = parentLimitNodeController.parentLimitNode(pinstId, req);
		} catch (Exception ex) {
			logger.info("RunFiService.runFiServicesAsync(){}", OperationUtillity.traceException(pinstId, ex));
		}
		return status;
	}
}
