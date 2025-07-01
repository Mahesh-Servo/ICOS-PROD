package com.LsmFiServices.FiLsmController;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.SOAPException;

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
public class ChildLimitNodeCreationController {

	private static final Logger logger = LoggerFactory.getLogger(ChildLimitNodeCreationController.class);

	@Autowired
	private RevisedServiceSequence newSequence;

	@Autowired
	private CaseRouting route;

	@Autowired
	private ESMUtils esmUtils;

	@GetMapping("/ChildLimitNodeCreation")
	public String ChildLimitNodeCreation(@RequestParam(value = "PINSTID") String pinstId,
			@RequestParam(value = "FACILITY") String facility, HttpServletRequest req)
			throws SOAPException, SQLException, InterruptedException, ExecutionException {
		Map<String, Map<String, String>> executeChildServiceIndividually = new HashMap<>();
		try {
			if (esmUtils.caseTypeIsESM(pinstId)) {
				return newSequence.executeESMChildIndividually(pinstId, facility).get(facility).get("Status");
			} else if (esmUtils.getESMFlagForLSM(pinstId)) {
				return newSequence.executeChildServiceIndividually(pinstId, facility, req).get(facility).get("Status");
			} else {
				executeChildServiceIndividually = newSequence.executeChildServiceIndividually(pinstId, facility, req);
				logger.info("ChildLimitNodeCreationController.ChildLimitNodeCreation(){}",
						executeChildServiceIndividually);
			}
		} catch (Exception e) {
			logger.info("ChildLimitNodeCreationController.ChildLimitNodeCreation(){}",
					OperationUtillity.traceException(e));
		} finally {
			route.updateFIStatusInEXT(pinstId);
		}
		return String.valueOf(executeChildServiceIndividually);
	}
}
