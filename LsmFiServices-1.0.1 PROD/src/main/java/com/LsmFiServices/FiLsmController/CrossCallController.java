package com.LsmFiServices.FiLsmController;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LsmFiServices.Utility.CaseRouting;
import com.LsmFiServices.Utility.ESMUtils;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.RevisedServiceSequence;

@RestController
public class CrossCallController {
	private static final Logger logger = LoggerFactory.getLogger(CrossCallController.class);

	@Autowired
	private RevisedServiceSequence newSequence;

	@Autowired
	private CaseRouting route;

	@Autowired
	private ESMUtils esmUtils;

	@GetMapping("/crossCall")
	public String runCrossCallService(@RequestParam(value = "PINSTID") String pinstId,
			@RequestParam(value = "FACILITY") String facility, HttpServletRequest req)
			throws InterruptedException, ExecutionException {

		String status = "Cross Call Executed ";
		Map<String, Map<String, String>> executeChildServiceIndividually = null;
		try {
			if (esmUtils.caseTypeIsESM(pinstId)) {
				logger.info("---if---");
				status = newSequence.executeESMChildIndividually(pinstId, facility).get(facility).get("Status");
			} else if (esmUtils.getESMFlagForLSM(pinstId)) {
				logger.info("----else if-----");
				executeChildServiceIndividually = newSequence.executeChildServiceIndividually(pinstId, facility, req);
			} else {
				logger.info("----else----");
				executeChildServiceIndividually = newSequence.executeChildServiceIndividually(pinstId, facility, req);
				logger.info(
						"ChildLimitNodeCreationController.ChildLimitNodeCreation() executeChildServiceIndividually :: {}",
						executeChildServiceIndividually);
			}
		} catch (Exception e) {
			logger.error("CrossCallController.runCrossCallService(){}", OperationUtillity.traceException(e));
		} finally {
			route.updateFIStatusInEXT(pinstId);
		}
		return status;
	}
}
