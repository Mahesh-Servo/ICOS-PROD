package com.LsmFiServices.FiLsmController;

import java.sql.SQLException;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutionException;

import javax.xml.soap.SOAPException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LsmFiServices.dao.fiParentLimitInquiryDao;

@RestController
public class fiParentLimitInquiryController {

	@Autowired
	fiParentLimitInquiryDao fiParentLimitInquiryDao;

	@RequestMapping(value = { "/fiParentLimitInquiry" }, method = RequestMethod.GET)
	public WeakHashMap<String, Object> fiParentLimitInquiry(@RequestParam(value = "PINSTID") String PINSTID,
			@RequestParam(value = "CUSTID") String custId)
			throws SOAPException, SQLException, InterruptedException, ExecutionException {

//		logger.info("[fiParentLimitInquiryController].[fiParentLimitInquiry()].[Pinstid()] :: " + PINSTID
//				+ " , [CUSTID()]:: " + custId);

		WeakHashMap<String, Object> fiParentLimitInquiryResponse = fiParentLimitInquiryDao.fiParentLimitInquiryDaoImlp(PINSTID, custId);

//		logger.info("limitFetchFiCmartController.limitFetchFiCmart()--->returning LimitNodeIdMap--->" + LimitNodeIdMapResponse);

		return fiParentLimitInquiryResponse;
	}
}
