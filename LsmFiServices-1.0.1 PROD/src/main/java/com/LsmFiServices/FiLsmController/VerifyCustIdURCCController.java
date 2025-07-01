package com.LsmFiServices.FiLsmController;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.LsmFiServices.FiLsmService.VerifyCustIdURCCService;
import com.LsmFiServices.Utility.OperationUtillity;

@Controller
public class VerifyCustIdURCCController {
	
	private static final Logger logger = LoggerFactory.getLogger(VerifyCustIdURCCController.class);

	@Autowired
	private VerifyCustIdURCCService VerifyCustIdURCCService;
	
	@RequestMapping(value = { "/getVerifyCustIdURCC" }, method = RequestMethod.GET)
	public @ResponseBody String callSoapWebService(@RequestParam(value = "PINSTID") String PINSTID,
			@RequestParam(value = "URCNO") String URCNO) throws SQLException {
	
		String result ="";
		 try {
			result = VerifyCustIdURCCService.VerifyCustIdURCCService(PINSTID,URCNO);
		} catch (Exception e) {
			logger.info("there is problem in VerifyCustIdURCCController "+OperationUtillity.traceException(e));
			return "there is problem in VerifyCustIdURCCController "+OperationUtillity.traceException(e);
		}
		return result;
	}
	
}
