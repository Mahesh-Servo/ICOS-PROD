package com.LsmFiServices.FiLsmController;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.xml.soap.SOAPException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.LsmFiServices.Utility.CaseRouting;
import com.LsmFiServices.Utility.CreateFacilityLimitList;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.RevisedServiceSequence;

@Controller
public class AccountAndLimitNodeLinkageController {
    private static final Logger logger = LoggerFactory.getLogger(AccountAndLimitNodeLinkageController.class);

    @Autowired
    private RevisedServiceSequence newSequence;

    @Autowired
    private CreateFacilityLimitList createFacilityLimitList;

    @Autowired
    private CaseRouting route;

    @RequestMapping(value = { "/accountAndLimitNodeLinkage" }, method = RequestMethod.GET)
    public @ResponseBody Map<String, Map<String, String>> accountAndLimitNodeLinkage(
	    @RequestParam(value = "PINSTID") String pinstId, @RequestParam(value = "FACILITY") String facility)
	    throws SOAPException, SQLException, InterruptedException, ExecutionException {

	Map<String, Map<String, String>> accountAndLimitNodeLinkageResponseMap = new LinkedHashMap<>();
	try {
	    accountAndLimitNodeLinkageResponseMap = newSequence.executeAccountAndLimitNodeLinkageOnwards(pinstId,
		    createFacilityLimitList.getfacilityWiseData(pinstId, facility));
	} catch (Exception e) {
	    logger.error("AccountAndLimitNodeLinkageController.accountAndLimitNodeLinkage()\n{}",
		    OperationUtillity.traceException(e));
	} finally {
	    route.updateFIStatusInEXT(pinstId);
	}
	return accountAndLimitNodeLinkageResponseMap;
    }
}
