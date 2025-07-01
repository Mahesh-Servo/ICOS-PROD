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

import com.LsmFiServices.dao.limitFetchFiCmartDao;

@RestController
public class limitFetchFiCmartController {

	@Autowired
	limitFetchFiCmartDao limitFetchFiCmartDao;
	
	private static final Logger logger = LoggerFactory.getLogger(limitFetchFiCmartController.class);

	@RequestMapping(value = { "/limitFetchFiCmart" }, method = RequestMethod.GET)
	public WeakHashMap<String, Object> limitFetchFiCmart(@RequestParam(value = "PINSTID") String PINSTID,
			@RequestParam(value = "LIMITPREFIX") String LmtPrefix,
			@RequestParam(value = "LIMITSUFFIX") String LmtSuffix)
			throws SOAPException, SQLException, InterruptedException, ExecutionException {

		logger.info("[limitFetchFiCmartController].[limitFetchFiCmart()].[insertRequest].[Pinstid()] :: " + PINSTID
				+ " , [LmtPrefix()]:: " + LmtPrefix + " , [LmtSuffix()]:: " + LmtSuffix);

		WeakHashMap<String, Object> limitFetchFiCmartResponse = limitFetchFiCmartDao.limitFetchFiCmartDaoImlp(PINSTID,
				LmtPrefix, LmtSuffix);

		logger.info("[limitFetchFiCmartController].[limitFetchFiCmart()].[exiting response]--->" + limitFetchFiCmartResponse);

		return limitFetchFiCmartResponse;
	}

}
