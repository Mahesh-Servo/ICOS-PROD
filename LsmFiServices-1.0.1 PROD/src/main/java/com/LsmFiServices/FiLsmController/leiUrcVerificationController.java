package com.LsmFiServices.FiLsmController;

import java.sql.SQLException;
import java.util.Map;
import java.util.WeakHashMap;

import javax.xml.soap.SOAPException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.LsmFiServices.FiLsmService.leiUrcVerificationService;
import com.LsmFiServices.Utility.CaseRouting;
import com.LsmFiServices.Utility.OperationUtillity;

@Controller
public class leiUrcVerificationController {

    private static final Logger logger = LoggerFactory.getLogger(leiUrcVerificationController.class);

    @Autowired
    private leiUrcVerificationService srvcClass;

    @Autowired
    private CaseRouting route;

    @RequestMapping(value = { "/leiUrcVrfctn" }, method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> leiUrcVerificationCntlr(@RequestParam(value = "PINSTID") String pinstId,
	    @RequestParam(value = "FACILITY") String FACILITY) throws SOAPException, SQLException {

	Map<String, Object> result = new WeakHashMap<>();
	try {
	    String cifId = FACILITY.split("::")[1].trim();
	    String number = FACILITY.split("::")[3].trim();
	    String type = "";
	    if (FACILITY.contains("LEI")) {
		type = "LEI VERIFICATION";
	    } else {
		type = "URCC VERIFICATION";
	    }
	    result.put("response", srvcClass.executeLeiUrcVrfctnService(pinstId, cifId, number, type).toString());
	    logger.info("lodgeCollateralController.lodgeCollateralCtrl().result pinstId : " + pinstId + "  " + result);
	} catch (Exception e) {
	    logger.info("leiUrcVerificationController.leiUrcVerificationCntlr().Exception :"
		    + OperationUtillity.traceException(pinstId, e));
	} finally {
	    route.updateFIStatusInEXT(pinstId);
	}
	return result;
    }
}
