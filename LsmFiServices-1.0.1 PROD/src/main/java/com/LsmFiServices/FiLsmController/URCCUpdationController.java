package com.LsmFiServices.FiLsmController;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.soap.SOAPException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LsmFiServices.FiLsmService.URCCUpdationService;
import com.LsmFiServices.Utility.CaseRouting;
import com.LsmFiServices.Utility.LEI_URCCNumberEnquiryUtility;
import com.LsmFiServices.Utility.OperationUtillity;

@RestController
public class URCCUpdationController {

    private static final Logger logger = LoggerFactory.getLogger(URCCUpdationController.class);

    @Autowired
    private LEI_URCCNumberEnquiryUtility urccutility;

    @Autowired
    private URCCUpdationService service;

    @Autowired
    private CaseRouting route;

    @GetMapping("/urccNum")
    public Map<String, Object> runURCCUpdation(@RequestParam(value = "PINSTID") String pinstId,
	    @RequestParam(value = "FACILITY") String operation, Map<String, String> mapData)
	    throws SOAPException, SQLException {

	Map<String, String> urccInputDataMap = urccutility.getLEI_URCC_InputDate(pinstId);
	urccInputDataMap.putAll(mapData);
	logger.info("Entered into urccUpdation service-->" + pinstId + " and input Map -->" + urccInputDataMap);
	Map<String, Object> urccUpdation = new HashMap<>();
	try {
	    if (!"".equals(urccInputDataMap.get("URCC_NUMBER")) && null != urccInputDataMap.get("URCC_NUMBER")) {
		if (operation.equals("ADD") || operation.equals("UPDATE")) {
		    urccUpdation = service.executeURCC_UpdationService(pinstId, operation, urccInputDataMap);
		} else {
		    // here operation will be like =>URCC NUMBER ADD SERVICE, CUST_ID :: 658268648,
		    // REFERENCE_NUMBER :: 54637289R789
		    urccInputDataMap.putAll(urccutility.getIndividualData(operation));
		    urccUpdation = service.executeURCC_UpdationService(pinstId, urccInputDataMap.get("OPERATION"),
			    urccInputDataMap);
		}
	    } else {
		urccUpdation.put("UNABLE TO RUN", "URCC NO IS NULL");
	    }
	} catch (Exception e) {
	    logger.info("URCCUpdationController.runURCCUpdation() : " + OperationUtillity.traceException(e));
	} finally {
	    route.updateFIStatusInEXT(pinstId);
	}
	return urccUpdation;
    }

}
