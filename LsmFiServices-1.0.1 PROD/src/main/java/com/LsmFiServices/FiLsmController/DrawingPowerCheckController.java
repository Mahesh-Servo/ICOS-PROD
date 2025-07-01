package com.LsmFiServices.FiLsmController;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LsmFiServices.FiLsmService.DrawingPowerCheckService;
import com.LsmFiServices.Utility.OperationUtillity;

@RestController
public class DrawingPowerCheckController {

    private static final Logger logger = LoggerFactory.getLogger(DrawingPowerCheckController.class);

    private DrawingPowerCheckService service;

    public DrawingPowerCheckController(DrawingPowerCheckService service) {
	this.service = service;
    }

    @GetMapping("/drgpwrcheck")
    public String executeDrawingPowerCheckService(@RequestParam("PINSTID") String pinstId,
	    @RequestParam("FACILITY") String accountNumber) {
	String message = "";
	try {
	    message = service.executeDrawingPowerCheck(pinstId, accountNumber);
	} catch (SQLException e) {
	    logger.error("executeDrawingPowerUpdationService() {}", OperationUtillity.traceException(e));
	} catch (Exception e) {
	    logger.error("executeDrawingPowerUpdationService() {}", OperationUtillity.traceException(e));
	}
	logger.info("executeDrawingPowerUpdationService() {}", message);
	return message;
    }
}
