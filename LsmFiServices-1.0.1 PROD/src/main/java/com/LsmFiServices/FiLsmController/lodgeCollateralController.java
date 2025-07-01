package com.LsmFiServices.FiLsmController;

import java.util.Map;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LsmFiServices.FiLsmService.lodgeCollateralService;
import com.LsmFiServices.Utility.CaseRouting;
import com.LsmFiServices.Utility.OperationUtillity;

@RestController
public class lodgeCollateralController {

    private static final Logger logger = LoggerFactory.getLogger(lodgeCollateralController.class);

    @Autowired
    private lodgeCollateralService service;

    @Autowired
    private CaseRouting route;

    @GetMapping("/lodgeCollateralController")
    public Map<String, String> lodgeCollateralCtrl(@RequestParam("PINSTID") String pinstId,
	    @RequestParam("securityName") String securityName, @RequestParam("subTypeSecurity") String subTypeSecurity,
	    @RequestParam("typeOfSvt") String typeOfSvt, @RequestParam("processName") String processName) {
	Map<String, String> result = new WeakHashMap<>();
	try {
	    result.put("response", service
		    .executeLodgeCollateralService(pinstId, securityName, subTypeSecurity, typeOfSvt, processName)
		    .toString());
	    logger.info("lodgeCollateralController.lodgeCollateralCtrl().result pinstId : " + pinstId + "  " + result);
	} catch (Exception e) {
	    logger.info("lodgeCollateralController.lodgeCollateralCtrl().Exception :"
		    + OperationUtillity.traceException(e));
	} finally {
	    route.updateFIStatusInEXT(pinstId);
	}
	return result;
    }

}
