package com.LsmFiServices.Utility;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("deprecation")
@Component
public class RestAPIUtility {

    private static final Logger logger = LoggerFactory.getLogger(RestAPIUtility.class);

    @Autowired
    private RestTemplate restTemplate;

    public String getResponseFromFinacle(String requestBody) {
	try {
	    logger.info("requestBody:: {}", requestBody);
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.TEXT_XML);
	    HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
	    ResponseEntity<String> responseEntity = restTemplate.exchange(Constants.FIWebService, HttpMethod.POST,
		    requestEntity, String.class);
	    return StringEscapeUtils.unescapeHtml4(responseEntity.getBody()); // decodedResponse returning
	} catch (Exception e) {
	    logger.info("RestAPIUtility.getResponseFromFinacle()-->" + OperationUtillity.traceException(e));
	    return "Someting Went Wrong While Exchanging Data...!";
	}
    }
}
