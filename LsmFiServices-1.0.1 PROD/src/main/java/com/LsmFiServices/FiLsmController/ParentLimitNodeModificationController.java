package com.LsmFiServices.FiLsmController;

import java.sql.SQLException;
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

import com.LsmFiServices.FiLsmService.ParentLimitNodeModificationService;
import com.LsmFiServices.Utility.ParentLimitNodeModificationUtility;
import com.LsmFiServices.Utility.CaseRouting;
import com.LsmFiServices.Utility.ESMUtils;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.RevisedServiceSequence;
import com.LsmFiServices.Utility.ServicesSequenceLogics;

@RestController
public class ParentLimitNodeModificationController {

	@Autowired
	private ParentLimitNodeModificationService service;

	@Autowired
	private ParentLimitNodeModificationUtility parentLimitNodeModificationUtility;

	@Autowired
	private OperationUtillity utility;

	@Autowired
	private CaseRouting route;

	@Autowired
	private ESMUtils esmUtils;

	@Autowired
	private RevisedServiceSequence revisedSequence;

	@Autowired
	private LEI_URCC_NumberEnquiryController leiUrccController;

	private static final Logger logger = LoggerFactory.getLogger(ParentLimitNodeModificationController.class);

	@GetMapping("/parentLimitNodeModifiaction")
	public String parentLimitNodeModification(@RequestParam(value = "PINSTID") String pinstid, HttpServletRequest req)
			throws SQLException, InterruptedException, ExecutionException, SOAPException {
		logger.info(
				"Entered into ParentLimitNodeModificationController.parentLimitNodeModification() for-->" + pinstid);
		String status = "Parent Limit Node Creation Is Not Executed Due To Some Problem Occured..!";
		try {
			Map<String, String> parentModInputMap = utility.GetParentLimitNodeData(pinstid);
			String proposalType = parentLimitNodeModificationUtility.getProposalType(pinstid);

			logger.info("if check :: " + esmUtils.caseTypeIsESM(pinstid) + " pinstid :: " + pinstid);
			logger.info("else if check :: " + esmUtils.getESMFlagForLSM(pinstid) + " pinstid :: " + pinstid);
			logger.info("else check  pinstid :: " + pinstid);

			if (ServicesSequenceLogics.checkStatusOfIndividualServiceFlag("PARENT LIMIT NODE MODIFICATION", pinstid)) {
				if (!"FRESH SANCTION".equalsIgnoreCase(proposalType)) {
					status = service.parentLimitNodeModificationService(pinstid, parentModInputMap);
					if (status.equalsIgnoreCase("Success")) {
						logger.info("Parent for ESM is Success [other than fresh] now executing child services for ::"
								+ pinstid);
						revisedSequence.executeAllChildsForESM(pinstid); // SANCTION LIMIT SERVICE CALLED
					}
				}
			}

			if (esmUtils.getESMFlagForLSM(pinstid)) {
				if (ServicesSequenceLogics.checkStatusOfIndividualServiceFlag("PARENT LIMIT NODE MODIFICATION",
						pinstid)) {
					status = service.parentLimitNodeModificationService(pinstid, parentModInputMap);
					if (status.equalsIgnoreCase("Success")) {
						revisedSequence.executeAllChildsAfterESM(pinstid, req);
						leiUrccController.runLEI_URCCNumberEnquiry(pinstid, "common");
					}
				}
			} else {
				logger.info("ParentLimitNodeModificationController.parentLimitNodeModification() else " + pinstid);
				status = service.parentLimitNodeModificationService(pinstid, parentModInputMap);
				if (status.equalsIgnoreCase("Success")) {
					revisedSequence.executeAllChildServices(pinstid, req);
					leiUrccController.runLEI_URCCNumberEnquiry(pinstid, "common");
				}
			}
		} catch (Exception e) {
			logger.info(OperationUtillity.traceException(e, pinstid));
		} finally {
			route.updateFIStatusInEXT(pinstid);
		}
		return status;
	}
}
