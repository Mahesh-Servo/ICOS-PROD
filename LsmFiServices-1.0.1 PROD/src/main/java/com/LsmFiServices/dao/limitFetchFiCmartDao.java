package com.LsmFiServices.dao;

import java.sql.SQLException;
import java.util.Map;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import com.LsmFiServices.FiLsmService.limitFetchFiCmartService;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.SOAPRequestUtility;
import com.LsmFiServices.Utility.limitFetchFiCmartUtility;
import com.LsmFiServices.Utility.xmlToMap;

@Service
public class limitFetchFiCmartDao {

	private static final Logger logger = LoggerFactory.getLogger(limitFetchFiCmartDao.class);

	@Autowired
	limitFetchFiCmartService limitFetchFiCmartService;

	@SuppressWarnings("static-access")
	public @ResponseBody WeakHashMap<String, Object> limitFetchFiCmartDaoImlp(String PINSTID, String LmtPrefix,
			String LmtSuffix) throws SQLException {

		logger.info("[limitFetchFiCmartDao].[limitFetchFiCmartDaoImlp()].[Pinstid()] :: " + PINSTID
				+ " , LmtPrefix():: " + LmtPrefix + " , LmtSuffix():: " + LmtSuffix);

		WeakHashMap<String, Object> limitFetchFiCmartMap = new WeakHashMap<>();
		try {

			String soapRequest = limitFetchFiCmartService.createSOAPRequest(PINSTID, LmtPrefix, LmtSuffix);

			String limitFetchFiCmartResponse = SOAPRequestUtility.soapResponse(soapRequest);
			logger.info("[limitFetchFiCmartDao].[limitFetchFiCmartDaoImlp()].[limitFetchFiCmartResponse] -->"
					+ limitFetchFiCmartResponse);

			String HostTransaction;
			String Status = "";
			if (limitFetchFiCmartResponse.contains("<HostTransaction>")) {
				HostTransaction = limitFetchFiCmartResponse.substring(
						limitFetchFiCmartResponse.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
						limitFetchFiCmartResponse.indexOf("</HostTransaction>"));
				if (HostTransaction.contains("<Status>")) {
					Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),
							HostTransaction.indexOf("</Status>"));
				}
			}

			if (Status.equalsIgnoreCase("SUCCESS")) {
				limitFetchFiCmartMap = limitFetchFiCmartUtility.successPacketDataToMaplimitFetchFiCmart(PINSTID,
						limitFetchFiCmartResponse, LmtPrefix, LmtSuffix);
			} else {
				limitFetchFiCmartMap.putAll(xmlToMap.packetDataToMap(PINSTID, limitFetchFiCmartResponse));
			}

			try {
				WeakHashMap<String, String> convertedMap = new WeakHashMap<>();
				for (Map.Entry<String, Object> obj : limitFetchFiCmartMap.entrySet()) {
					convertedMap.put(obj.getKey(), obj.getValue().toString());
				}

				OperationUtillity.API_RequestResponse_Insert(soapRequest, limitFetchFiCmartResponse,
						"LIMIT NODE ENQUIRY: " + LmtPrefix + "-" + LmtSuffix, PINSTID, convertedMap, "");
			} catch (Exception e) {
				logger.info(
						"limitFetchFiCmartDao.limitFetchFiCmartDaoImlp() Exception parsing API_REQ_RES_map to string-->\n"
								+ OperationUtillity.traceException(e));
			}
		} catch (Exception e) {
			logger.error("limitFetchFiCmartDao.limitFetchFiCmartDaoImlp().exception()"
					+ OperationUtillity.traceException(e));
		}
		logger.info("[limitFetchFiCmartDao].[limitFetchFiCmartDaoImlp()] -->" + String.valueOf(limitFetchFiCmartMap));
		return limitFetchFiCmartMap;
	}

}
