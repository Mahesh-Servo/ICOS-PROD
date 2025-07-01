package com.LsmFiServices.FiLsmController;

import java.sql.SQLException;
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
public class ChildLimitNodeModificationController {

	@Autowired
	private RevisedServiceSequence newSequence;

	@Autowired
	private CaseRouting route;

	@Autowired
	private ESMUtils esmUtils;

	private static final Logger logger = LoggerFactory.getLogger(ChildLimitNodeModificationController.class);

	@GetMapping("/ChildLimitNodeModification")
	public String childLimitNodeModification(@RequestParam(value = "PINSTID") String pinstId,
			@RequestParam(value = "FACILITY") String facility, HttpServletRequest req)
			throws SOAPException, SQLException, InterruptedException, ExecutionException {

		logger.info("Child Node Limit Modification executing for : " + pinstId + " :: " + facility);
		String status = "";
		try {
			if (esmUtils.caseTypeIsESM(pinstId)) {
				status = newSequence.executeESMChildIndividually(pinstId, facility).get(facility).get("Status");
			} else if (esmUtils.getESMFlagForLSM(pinstId)) {
				newSequence.executeLSMChildsAfterESM(pinstId, facility);
			} else {
				logger.info("Modification for plain LSM::" + pinstId + " :: " + facility);
				newSequence.executeChildServiceIndividually(pinstId, facility, req);
			}
		} catch (Exception e) {
			logger.info(OperationUtillity.traceException(e, pinstId));
		} finally {
			route.updateFIStatusInEXT(pinstId);
		}
		return status;
	}
}
