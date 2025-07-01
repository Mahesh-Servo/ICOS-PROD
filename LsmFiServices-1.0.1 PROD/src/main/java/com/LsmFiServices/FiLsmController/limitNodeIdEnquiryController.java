package com.LsmFiServices.FiLsmController;

import java.sql.SQLException;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutionException;

import javax.xml.soap.SOAPException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LsmFiServices.Utility.limitNodeIdEnquiryUtility;
import com.LsmFiServices.dao.limitNodeIdEnquiryDao;

@RestController
public class limitNodeIdEnquiryController {

	private static final Logger logger = LoggerFactory.getLogger(limitNodeIdEnquiryController.class);

	@Autowired
	limitNodeIdEnquiryDao limitNodeIdEnquiryDao;

	@Autowired
	limitNodeIdEnquiryUtility limitNodeIdEnquiryUtility;

	@RequestMapping(value = { "/limitNodeIdEnquiry" }, method = RequestMethod.GET)
	public WeakHashMap<String, String> limitNodeIdEnquiry(@RequestParam(value = "PINSTID") String PINSTID,
			@RequestParam(value = "LIMITPREFIX") String LmtPrefix,
			@RequestParam(value = "LIMITSUFFIX") String LmtSuffix)
			throws SOAPException, SQLException, InterruptedException, ExecutionException {

//		logger.info("[limitNodeIdEnquiryController].[limitNodeIdEnquiry()].[Pinstid()] :: " + PINSTID
//				+ " , [LmtPrefix()]:: " + LmtPrefix + " , [LmtSuffix()]:: " + LmtSuffix);

		WeakHashMap<String, String> limitNodeIdEnquiryrRequestMap = new WeakHashMap<String, String>();

		limitNodeIdEnquiryrRequestMap.put("Status", "SUCCESS");

		WeakHashMap<String, String> LimitNodeIdMapResponse = limitNodeIdEnquiryUtility
				.limitNodeIdEnquiryUtilityRecur(PINSTID, LmtPrefix, LmtSuffix, limitNodeIdEnquiryrRequestMap);
//		logger.info("limitNodeIdEnquiryController.limitNodeIdEnquiry()--->returning LimitNodeIdMap--->" + LimitNodeIdMapResponse);

		return LimitNodeIdMapResponse;
	}
}
