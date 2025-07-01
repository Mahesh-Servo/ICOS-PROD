package com.LsmFiServices.FiLsmController;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apiinfo")
public class MyController {

//    @GetMapping("/getTimeStamp")
//    public String getTimeStamp() {
//	return LsmFiServicesApplication.appStartTimeStamp.toString();
//    }
    @GetMapping("/hostname")
    public String getHostName(HttpServletRequest request) {
	StringBuffer sb = new StringBuffer();
	sb.append(" HostName :: " + request.getServerName());
	sb.append(" Protocol :: " + request.getProtocol());
	sb.append(" Method :: " + request.getMethod());
	sb.append(" Locale :: " + request.getLocale());

	return sb.toString();
    }
}