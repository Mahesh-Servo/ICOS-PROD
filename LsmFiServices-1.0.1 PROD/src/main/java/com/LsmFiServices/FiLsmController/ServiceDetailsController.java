package com.LsmFiServices.FiLsmController;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.LsmFiServices.FiLsmService.SVTFIService;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.ServiceDetails;
import com.LsmFiServices.pojo.serviceexecutiondetails.ServiceDetailsToUI;

@RestController
@RequestMapping("/fi-excecution-details")
public class ServiceDetailsController {

    private static final Logger logger = LoggerFactory.getLogger(SVTFIService.class);

    @Autowired
    private ServiceDetails service;

//	@GetMapping("/save-count")
//	public boolean fetchExecutionCount(@RequestParam(name = "PINSTID") String pinstId) {
//		return service.saveCountInExt(pinstId);
//	}

    @GetMapping("/fetch-all")
    public List<ServiceDetailsToUI> fetchServiceDetailsToUI(String pinstId) {
	List<ServiceDetailsToUI> listOfPojo = null;
	try {
//	    listOfPojo = service.fetchServiceExecutionDetailsForUI(pinstId);
	} catch (Exception e) {
	    logger.info("ServiceDetailsController.fetchServiceDetailsToUI() :: " + OperationUtillity.traceException(e));
	}
	return listOfPojo;
    }

}
