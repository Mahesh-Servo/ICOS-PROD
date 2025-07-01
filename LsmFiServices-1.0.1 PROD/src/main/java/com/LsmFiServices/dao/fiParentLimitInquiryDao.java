package com.LsmFiServices.dao;

import java.sql.SQLException;
import java.util.Map;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import com.LsmFiServices.FiLsmService.fiParentLimitInquiryService;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.SOAPRequestUtility;
import com.LsmFiServices.Utility.fiParentLimitInquiryUtility;
import com.LsmFiServices.Utility.xmlToMap;

@Service
public class fiParentLimitInquiryDao {

	private static final Logger logger = LoggerFactory.getLogger(fiParentLimitInquiryDao.class);

	@Autowired
	fiParentLimitInquiryService fiParentLimitInquiryService;

	@SuppressWarnings("static-access")
	public @ResponseBody WeakHashMap<String, Object> fiParentLimitInquiryDaoImlp(String PINSTID, String custId)
			throws SQLException {

		logger.info("fiParentLimitInquiryDao.fiParentLimitInquiryDaoImlp().Pinstid() :: " + PINSTID + " , CUSTID():: "
				+ custId);

		WeakHashMap<String, Object> fiParentLimitInquiryMap = new WeakHashMap<>();
		try {

			String soapRequest = fiParentLimitInquiryService.createSOAPRequest(PINSTID, custId);

//			logger.info("[fiParentLimitInquiryDao].[fiParentLimitInquiryDaoImlp()].[soapRequest]-->\n"
//					+ soapRequest);

			String fiParentLimitInquiryResponse = SOAPRequestUtility.soapResponse(soapRequest);

			logger.info("[fiParentLimitInquiryDao].[fiParentLimitInquiryDaoImlp()].[fiParentLimitInquiryResponse]-->\n"
					+ fiParentLimitInquiryResponse);

			String HostTransaction;
			String Status = "";
			if (fiParentLimitInquiryResponse.contains("<HostTransaction>")) {
				HostTransaction = fiParentLimitInquiryResponse.substring(
						fiParentLimitInquiryResponse.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
						fiParentLimitInquiryResponse.indexOf("</HostTransaction>"));
				if (HostTransaction.contains("<Status>")) {
					Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),
							HostTransaction.indexOf("</Status>"));
				}
			}

			if (Status.equalsIgnoreCase("SUCCESS")) {
				fiParentLimitInquiryMap = fiParentLimitInquiryUtility.successPacketDataToMapfiParentLimitInquiry(PINSTID, fiParentLimitInquiryResponse, custId);
			} else {
				fiParentLimitInquiryMap.putAll(xmlToMap.packetDataToMap(PINSTID, fiParentLimitInquiryResponse));
			}

			try {

				WeakHashMap<String, String> convertedMap = new WeakHashMap<>();
				for (Map.Entry<String, Object> obj : fiParentLimitInquiryMap.entrySet()) {
					convertedMap.put(obj.getKey(), obj.getValue().toString());
				}

				OperationUtillity.API_RequestResponse_Insert(soapRequest, fiParentLimitInquiryResponse,
						"FI PARENT LIMIT INQUIRY FOR CUSTID : " + custId, PINSTID, convertedMap, "");
			} catch (Exception e) {
				logger.info(
						"limitFetchFiCmartDao.limitFetchFiCmartDaoImlp() Exception parsing API_REQ_RES_map to string-->\n"
								+ OperationUtillity.traceException(e));
			}
		} catch (Exception e) {
			logger.error("limitFetchFiCmartDao.limitFetchFiCmartDaoImlp().exception()"
					+ OperationUtillity.traceException(e));
		}
		logger.info("[fiParentLimitInquiryDao].[fiParentLimitInquiryDaoImlp()] -->"
				+ String.valueOf(fiParentLimitInquiryMap));

		return fiParentLimitInquiryMap;
	}
}
