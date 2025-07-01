package com.LsmFiServices.FiLsmController;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.LsmFiServices.FiLsmService.StatusCodeFiService;
import com.LsmFiServices.Utility.OperationUtillity;

@Controller
public class StatusCodeController {
	private static final Logger logger = LoggerFactory.getLogger(StatusCodeController.class);
	
	@Autowired
	private StatusCodeFiService service;

	@RequestMapping(value = { "/status_code_updation" }, method = RequestMethod.GET)
	public @ResponseBody Map<String,String>  runStatusCodeService(@RequestParam(value = "PINSTID") String PINSTID) throws InterruptedException, ExecutionException {
		Map<String,String> result =null;
		try {
			result = service.StatusCodeService(PINSTID);
		} catch (Exception e) {
			logger.info("StatusCodeController.runStatusCodeService() exception :: " + OperationUtillity.traceException(e));
		}
		return result;	
	}
}
