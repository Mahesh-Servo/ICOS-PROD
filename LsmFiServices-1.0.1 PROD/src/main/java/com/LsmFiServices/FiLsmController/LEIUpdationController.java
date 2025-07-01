package com.LsmFiServices.FiLsmController;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.soap.SOAPException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.LsmFiServices.FiLsmService.LEIUpdationService;
import com.LsmFiServices.Utility.CaseRouting;
import com.LsmFiServices.Utility.LEI_URCCNumberEnquiryUtility;
import com.LsmFiServices.Utility.OperationUtillity;

@RestController
public class LEIUpdationController {

    private static final Logger logger = LoggerFactory.getLogger(LEIUpdationController.class);

    @Autowired
    private LEI_URCCNumberEnquiryUtility leiUtility;

    @Autowired
    private LEIUpdationService service;

    @Autowired
    private CaseRouting route;

    @RequestMapping(value = { "/leiNum" }, method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> runLEIUpdation(@RequestParam(value = "PINSTID") String pinstId,
	    @RequestParam(value = "FACILITY") String operation, Map<String, String> mapdata)
	    throws SOAPException, SQLException {

	Map<String, String> leiInputDataMap = leiUtility.getLEI_URCC_InputDate(pinstId);
	leiInputDataMap.putAll(mapdata);
	logger.info("Entered into leiUpdation service-->" + pinstId + " and input Map -->" + leiInputDataMap);
	Map<String, Object> leiResultMap = new HashMap<>();
	try {
	    if (!"".equals(leiInputDataMap.get("LEI_NUMBER")) && leiInputDataMap.get("LEI_NUMBER") != null) {
		if (operation.equals("ADD") || operation.equals("UPDATE")) {
		    leiResultMap = service.executeLEI_UpdationService(pinstId, operation, leiInputDataMap);
		} else {
		    // here operation will be like =>URCC NUMBER ADD SERVICE, CUST_ID :: 658268648,
		    // REFERENCE_NUMBER :: 54637289R789
		    leiInputDataMap.putAll(leiUtility.getIndividualData(operation));
		    logger.info("getIndividualData(operation) for LEI-->" + leiUtility.getIndividualData(operation));
		    leiResultMap = service.executeLEI_UpdationService(pinstId, leiInputDataMap.get("OPERATION"),
			    leiInputDataMap);
		}
	    } else {
		leiResultMap.put("UNABLE TO RUN", "LEI NUMBER NO IS NULL");
	    }
	} catch (Exception e) {
	    logger.error("LEIUpdationController.runLEIUpdation()->\n" + OperationUtillity.traceException(e));
	} finally {
	    route.updateFIStatusInEXT(pinstId);
	}
	return leiResultMap;
    }
}
