package com.LsmFiServices.dao;

import java.sql.SQLException;
import java.util.Optional;
import java.util.WeakHashMap;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

import com.LsmFiServices.FiLsmService.limitNodeIdEnquiryService;
import com.LsmFiServices.Utility.Constants;
import com.LsmFiServices.Utility.OperationUtillity;
import com.LsmFiServices.Utility.SOAPRequestUtility;
import com.LsmFiServices.Utility.xmlToMap;

@Repository
public class limitNodeIdEnquiryDao {

	private static final Logger logger = LoggerFactory.getLogger(limitNodeIdEnquiryDao.class);

	@Autowired
	limitNodeIdEnquiryService limitNodeIdEnquiryService;

	@SuppressWarnings("static-access")
	public @ResponseBody WeakHashMap<String, String> limitNodeIdEnquiryDaoImlp(String PINSTID, String LmtPrefix,
			String LmtSuffix) throws SQLException {

		logger.info("limitNodeIdEnquiryDao.limitNodeIdEnquiryDaoImlp().Pinstid() :: " + PINSTID + " , LmtPrefix():: "
				+ LmtPrefix + " , LmtSuffix():: " + LmtSuffix);

		WeakHashMap<String, String> LimitNodeIdMap = new WeakHashMap<>();
		try {

			String soapRequest = limitNodeIdEnquiryService.createSOAPRequest(PINSTID, LmtPrefix, LmtSuffix);
			LimitNodeIdMap.put("soapRequest", soapRequest);

			String limitNodeIdEnquiryResponse = SOAPRequestUtility.soapResponse(soapRequest);

			limitNodeIdEnquiryResponse = StringEscapeUtils.unescapeXml(limitNodeIdEnquiryResponse);

			String HostTransaction;
			String Status = "";
			if (limitNodeIdEnquiryResponse.contains("<HostTransaction>")) {
				HostTransaction = limitNodeIdEnquiryResponse.substring(
						limitNodeIdEnquiryResponse.indexOf("<HostTransaction>") + "<HostTransaction>".length(),
						limitNodeIdEnquiryResponse.indexOf("</HostTransaction>"));
				if (HostTransaction.contains("<Status>")) {
					Status = HostTransaction.substring(HostTransaction.indexOf("<Status>") + "<Status>".length(),
							HostTransaction.indexOf("</Status>"));
				}
			}
			LimitNodeIdMap.put("limitNodeIdEnquiryResponse", limitNodeIdEnquiryResponse);

			if (Status.equalsIgnoreCase("SUCCESS")) {
				LimitNodeIdMap.putAll(xmlToMap.successPacketDataToMaplimitNodeIdEnquiry(PINSTID, limitNodeIdEnquiryResponse));
			} else {
				LimitNodeIdMap.putAll(xmlToMap.packetDataToMap(PINSTID, limitNodeIdEnquiryResponse));
			}

		} catch (Exception e) {
			logger.error("limitNodeIdEnquiryDao.limitNodeIdEnquiryDaoImlp().exception()"
					+ OperationUtillity.traceException(e));
		}
		logger.info("[limitNodeIdEnquiryDao].[limitNodeIdEnquiryDaoImlp()] -->" + String.valueOf(LimitNodeIdMap));
		return LimitNodeIdMap;
	}

}
