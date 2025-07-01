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

import com.LsmFiServices.FiLsmService.DrawingPowerCheckService;
import com.LsmFiServices.FiLsmService.ParentLimitNodeModificationService;
import com.LsmFiServices.Utility.CaseRouting;
import com.LsmFiServices.Utility.CreateFacilityLimitList;
import com.LsmFiServices.Utility.DrawingPowerUtility;
import com.LsmFiServices.Utility.ESMUtils;
//import com.LsmFiServices.Utility.CustomLogger;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.ParentLimitNodeModificationUtility;
import com.LsmFiServices.Utility.RevisedServiceSequence;
import com.LsmFiServices.Utility.ServicesSequenceLogics;
import com.LsmFiServices.dao.ParentLimitNodeCreationDao;

@RestController
public class ParentLimitNodeCreationController {

    private static final Logger logger = LoggerFactory.getLogger(ParentLimitNodeCreationController.class);

    @Autowired
    private ParentLimitNodeCreationDao parentLimitNodeCreationDao;

    @Autowired
    private ParentLimitNodeModificationUtility parentLimitNodeModificationUtility;

    @Autowired
    private OperationUtillity utility;

    @Autowired
    private ParentLimitNodeModificationService service;

    @Autowired
    private RevisedServiceSequence revisedSequence;

    @Autowired
    private LEI_URCC_NumberEnquiryController leiUrccController;

    @Autowired
    private CaseRouting route;

    @Autowired
    private ESMUtils esmUtils;

    @Autowired
    private DrawingPowerUtility drgPwrUtils;
    
    @Autowired
    private CreateFacilityLimitList createFacilityLimitList;
    
    @Autowired
    private DrawingPowerCheckService dpcheckservice;
    
    @GetMapping("/parentLimitNodeCreation")
    public String parentLimitNode(@RequestParam(value = "PINSTID") String pinstId, HttpServletRequest req)
	    throws Exception {

	String status = "Parent Limit Node Creation Is Not Executed Due To Some Problem Occured..!";
	String proposalType = parentLimitNodeModificationUtility.getProposalType(pinstId);
	String limitType = parentLimitNodeModificationUtility.getLimitType(pinstId);  // ADDED BY MAHESHV ON 27122024 FOR RENEWAL
	logger.info("Entered into Parent Limit Node Controller for --->" + pinstId);
	// SN OF MAHESHV ON 10122024
		Map<String, String> FacilityListforDP = createFacilityLimitList.createFacilityListForDPCheck(pinstId);
		for(String key : FacilityListforDP.keySet()) {
		if (!"Fresh Sanction".equalsIgnoreCase(proposalType) && drgPwrUtils.getFlagForDrgPwrCheck_1(pinstId, key) && !parentLimitNodeModificationUtility.isLimitAsPerSanctionIsZero(pinstId, key)) {
			  logger.info("Calling to drawing power check{}", pinstId);
			  String DP_status =  dpcheckservice.executeDrawingPowerCheck(pinstId, FacilityListforDP.get(key));
			}
		
		}
		int DPCheckCount = drgPwrUtils.getDPFlagCheckToProceed(pinstId);
		if(DPCheckCount > 0)  {
			status = "Parent Limit Node Creation Is Not Executed Due To DP Check is not Success for all Accounts..!";
		} else {
		// EN OF MAHESHV ON 10122024
	Map<String, String> GetEXTData = utility.GetParentLimitNodeData(pinstId);

	try {
	    logger.info("if check :: " + esmUtils.caseTypeIsESM(pinstId) + " pinstid :: " + pinstId);
	    logger.info("else if check :: " + esmUtils.getESMFlagForLSM(pinstId) + " pinstid :: " + pinstId);
	    logger.info("else check  pinstid :: " + pinstId);
	    if (esmUtils.caseTypeIsESM(pinstId)) {
			if (ServicesSequenceLogics.checkStatusOfIndividualServiceFlag("PARENT LIMIT NODE CREATION", pinstId)) {
				if(!"FRESH SANCTION".equalsIgnoreCase(proposalType)) {	
				       status = service.parentLimitNodeModificationService(pinstId, GetEXTData);
					    if (status.equalsIgnoreCase("Success")) {
					     logger.info("Parent for ESM is Success [other than fresh] now executing child services for ::" + pinstId);
						revisedSequence.executeAllChildsForESM(pinstId);    //SANCTION LIMIT SERVICE CALLED
					    }
					} else {  //CREATION FOR FRESH CASES 
						  status = parentLimitNodeCreationDao.parentLimitNodeCreationService(pinstId, GetEXTData);   //PARENT LIMIT NODE IS CALLED FOR ESM CASE
						    if (status.equalsIgnoreCase("Success")) {
							logger.info("Parent for ESM is Success now executing child services for ::" + pinstId);
							revisedSequence.executeAllChildsForESM(pinstId);   //CHILD LIMIT NODE IS CALLED FOR ESM CASE
						    }
					}
			}
		    } else if (esmUtils.getESMFlagForLSM(pinstId)) {
		if (ServicesSequenceLogics.checkStatusOfIndividualServiceFlag("PARENT LIMIT NODE MODIFICATION",
			pinstId)) {
		    status = service.parentLimitNodeModificationService(pinstId, GetEXTData);
		    if (status.equalsIgnoreCase("Success")) {
			revisedSequence.executeAllChildsAfterESM(pinstId, req);
			leiUrccController.runLEI_URCCNumberEnquiry(pinstId, "common");
		    }
		}
	    } else {
		logger.info("Into the else of parent " + pinstId);
		logger.info("Into the else of parent limitType : " + limitType);
//		if (("Renewal with Enhancement of limits".toUpperCase().equalsIgnoreCase(proposalType)
//			|| "Pure Enhancement".toUpperCase().equalsIgnoreCase(proposalType))) {
		 //SN BY MAHESHV ON 2712/2024 FOR RENEWAL
		    if (!("First Limit setup".toUpperCase().equalsIgnoreCase(limitType)) && !("First Limit set up".toUpperCase().equalsIgnoreCase(limitType))) {
		    status = service.parentLimitNodeModificationService(pinstId, GetEXTData);

		    if (status.equalsIgnoreCase("Success")) {
			revisedSequence.executeAllChildServices(pinstId, req);
			leiUrccController.runLEI_URCCNumberEnquiry(pinstId, "common");
		    }
		} else {
		    status = parentLimitNodeCreationDao.parentLimitNodeCreationService(pinstId, GetEXTData);
		    if (status.equalsIgnoreCase("Success")) {
			revisedSequence.executeAllChildServices(pinstId, req);
			leiUrccController.runLEI_URCCNumberEnquiry(pinstId, "common");
		    }
		}
	    }
	} catch (Exception e) {
	    logger.info(OperationUtillity.traceException(e, pinstId));
	} finally {
	    route.updateFIStatusInEXT(pinstId);
	}
    }   // maheshv 10122024
	return status;
    }
}
