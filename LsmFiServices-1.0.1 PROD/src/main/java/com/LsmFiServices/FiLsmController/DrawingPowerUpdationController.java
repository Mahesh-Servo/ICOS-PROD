package com.LsmFiServices.FiLsmController;

import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LsmFiServices.FiLsmService.DrawingPowerUpdationService;
import com.LsmFiServices.Utility.CaseRouting;
import com.LsmFiServices.Utility.DrawingPowerUtility;
import com.LsmFiServices.Utility.OperationUtillity;

@RestController
public class DrawingPowerUpdationController {
    private static final Logger logger = LoggerFactory.getLogger(DrawingPowerUpdationController.class);

    private DrawingPowerUpdationService service;
    private DrawingPowerUtility drgPwrUtils;
    private CaseRouting route;

    public DrawingPowerUpdationController(DrawingPowerUpdationService service, DrawingPowerUtility drgPwrUtils,
	    CaseRouting route) {
	this.service = service;
	this.drgPwrUtils = drgPwrUtils;
	this.route = route;
    }

    @GetMapping("/drgpwrupdate")
    public String drgPwrUpdationHandler(@RequestParam("PINSTID") String pinstId,
	    @RequestParam("FACILITY") String accountNumber) {
	String status = "";
	try {
	    Map<String, String> inputMap = drgPwrUtils.getResponseMap(pinstId, accountNumber);
	    status = service.executeDrawingPowerUpdationService(pinstId, inputMap);
	} catch (SQLException e) {
	    logger.error("executeDrawingPowerUpdationService()1 {}", OperationUtillity.traceException(e));
	} catch (Exception e) {
	    logger.error("executeDrawingPowerUpdationService()2 {}", OperationUtillity.traceException(e));
	} finally {
	    route.updateFIStatusInEXT(pinstId);
	}
	logger.info("DrawingPowerUpdationController.executeDrawingPowerUpdationService() {}", status);
	return status;
    }
}
