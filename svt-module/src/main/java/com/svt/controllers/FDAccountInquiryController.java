package com.svt.controllers;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.svt.service.FDAccountInqService;

@RestController
@RequestMapping("svt")
public class FDAccountInquiryController {

	@Autowired
	private FDAccountInqService fdInqservice;

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FDAccountInquiryController.class);

	@PostMapping("/FDAccountInquiry")
	public Map<String, String> FDAccountInquiry(@RequestBody Map<String, String> requestData)
			throws JsonMappingException, JsonProcessingException {

		Map<String, String> finalResult = new HashMap<>();
		String pinstid = "", fdrNo = "", reviewDate = "";
		String finalMessage = "";
		logger.info("FDAccountInquiryController.FDAccountInquiry.requestData [" + requestData + "] ");
		pinstid = requestData.get("pinstId");
		fdrNo = requestData.get("FDAccountNumber");
		reviewDate = requestData.get("reviewDate");
		logger.info("FDAccountInquiryController.FDAccountInquiry [START] pinstid [" + pinstid + "]  [FDRNO] = [" + fdrNo
				+ "] [ReviewDate] = [" + reviewDate + "]");
		if (fdrNo != null) {
			if (fdrNo.length() < 12) {
				finalMessage = "FD Number should be 12 digits";
				finalResult.put("FinalResult", finalMessage);
				return finalResult;
			} else {
				finalResult = fdInqservice.FDAccInquiryService(pinstid, fdrNo, reviewDate);
			}
		}

		logger.info("FDAccountInquiryController.FDAccountInquiry.pinstid [" + pinstid + "] END [FDRNO] = [" + fdrNo
				+ "] FINAL RESULT MAP [" + finalResult.toString() + "]");

		return finalResult;
	}

}
