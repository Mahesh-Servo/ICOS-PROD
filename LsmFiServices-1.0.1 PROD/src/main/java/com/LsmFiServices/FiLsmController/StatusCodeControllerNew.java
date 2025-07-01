package com.LsmFiServices.FiLsmController;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.soap.SOAPException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.LsmFiServices.FiLsmService.statusUpdateService;
import com.LsmFiServices.Utility.CaseRouting;
import com.LsmFiServices.Utility.OperationUtillity;

@Controller
public class StatusCodeControllerNew {
    
    private static final Logger logger = LoggerFactory.getLogger(StatusCodeControllerNew.class);
    
    @Autowired
	private statusUpdateService service;
    
    @Autowired
	private CaseRouting route;
    
    @RequestMapping(value = { "/statusCodeUpdate" }, method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> statusCodeUpdate(@RequestParam(value = "PINSTID") String pinstId,
			@RequestParam(value = "STATUS_CODE") String status_code, Map<String, String> mapData)
			throws SOAPException, SQLException {
	        logger.info("statusCodeUpdate Service Execution Starts PINSTID [" + pinstId + "] STATUS_CODE [" +status_code+ "]" );
		//Map<String, String> urccInputDataMap = urccutility.getLEI_URCC_InputDate(pinstId);
		//urccInputDataMap.putAll(mapData);
		Map<String, Object> statusCodeUpdation = new HashMap<>();
		try {
		    logger.info("Before Calling executeStatusCode_UpdationService ");	
		    statusCodeUpdation = service.executeStatusCode_UpdationService(pinstId,status_code,mapData);
		    logger.info("After Calling executeStatusCode_UpdationService ");	
		} catch (Exception e) {
			logger.info("There is  problem in Status Code Updation Service " + OperationUtillity.traceException(e));
		}

		logger.info("statusCodeUpdate Service Execution Ends");
		return statusCodeUpdation;
	}


}
