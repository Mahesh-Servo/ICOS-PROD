package com.LsmFiServices.FiLsmController;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.soap.SOAPException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LsmFiServices.FiLsmService.FeeRecoveryService;
import com.LsmFiServices.Utility.CaseRouting;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.ServicesSequenceLogics;

@RestController
public class FeeRecoveryController {

    private static final Logger logger = LoggerFactory.getLogger(FeeRecoveryController.class);

    @Autowired
    private FeeRecoveryService feeRecoveryService;

    @Autowired
    private CaseRouting route;

    // ../LsmFiServices/feeRecovery?PINSTID=LSM-000000001489-PRO&FACILITY=FEE
    // RECOVERY SERVICE : Fee Type-2::Valuation Charges:: Event ID :: CNPROBLG
    @GetMapping("/feeRecovery")
    public Map<String, String> feeRecovery(@RequestParam(value = "PINSTID") String pinstId,
	    @RequestParam(value = "FACILITY") String feeID) throws SOAPException, SQLException {
	Map<String, String> feeRecoveryServiceresult = new LinkedHashMap<>();
	logger.info("Individually Fee Recovery Check CALLED FOR PINSTID->" + pinstId + "and Fee Type -->" + feeID);
	try {
	    if (ServicesSequenceLogics.checkStatusOfIndividualServiceFlag(feeID, pinstId)) {
		feeRecoveryServiceresult = feeRecoveryService.feeRecoveryService(pinstId,OperationUtillity.getFeeID(feeID));
	    } else {
		feeRecoveryServiceresult.put("Status", "Service Already Executed");
	    }
	} catch (Exception e) {
	    logger.error("FeeRecoveryController.feeRecovery().Exception-->\n", OperationUtillity.traceException(e));
	} finally {
	    route.updateFIStatusInEXT(pinstId);
	}
	return feeRecoveryServiceresult;
    }
}
